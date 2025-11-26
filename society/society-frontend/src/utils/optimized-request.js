import axios from 'axios'
import { ElMessage, ElLoading } from 'element-plus'
import { errorHandler, ErrorType } from './errorHandler'
import { performanceMonitor } from './performance'
import {useAppStore} from "@/stores/app.js";

// 请求缓存
const requestCache = new Map()
const CACHE_TIMEOUT = 5 * 60 * 1000 // 5分钟缓存

// 请求队列管理
class RequestQueue {
    constructor() {
        this.pendingRequests = new Map()
        this.concurrentLimit = 6 // 最大并发数
        this.currentConcurrent = 0
        this.waitingQueue = []
    }

    generateKey(config) {
        const { method, url, params, data } = config
        return [method, url, JSON.stringify(params), JSON.stringify(data)].join('|')
    }

    add(config) {
        const key = this.generateKey(config)

        // 检查缓存
        const cached = requestCache.get(key)
        if (cached && Date.now() - cached.timestamp < CACHE_TIMEOUT) {
            return Promise.resolve(cached.response)
        }

        // 检查是否已有相同请求在进行中
        if (this.pendingRequests.has(key)) {
            return this.pendingRequests.get(key)
        }

        const requestPromise = this.executeRequest(config, key)
        this.pendingRequests.set(key, requestPromise)

        // 请求完成后清理
        requestPromise.finally(() => {
            this.pendingRequests.delete(key)
        })

        return requestPromise
    }

    async executeRequest(config, key) {
        // 控制并发数
        if (this.currentConcurrent >= this.concurrentLimit) {
            await new Promise(resolve => {
                this.waitingQueue.push(resolve)
            })
        }

        this.currentConcurrent++

        try {
            const response = await axios(config)

            // 缓存成功的GET请求
            if (config.method?.toLowerCase() === 'get' && response.status === 200) {
                requestCache.set(key, {
                    response,
                    timestamp: Date.now()
                })
            }

            return response
        } finally {
            this.currentConcurrent--

            // 释放等待队列中的请求
            if (this.waitingQueue.length > 0) {
                const nextRequest = this.waitingQueue.shift()
                nextRequest()
            }
        }
    }

    clearCache() {
        requestCache.clear()
    }

    removeFromCache(key) {
        requestCache.delete(key)
    }
}

const requestQueue = new RequestQueue()

// 创建axios实例
const request = axios.create({
    baseURL: 'http://localhost:8080/api',
    timeout: 15000,
    headers: {
        'Content-Type': 'application/json;charset=UTF-8'
    }
})

// 请求拦截器
request.interceptors.request.use(
    async (config) => {
        const requestId = `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`
        config.requestId = requestId

        performanceMonitor.start(`request_${requestId}`)

        // 添加缓存控制
        if (config.method?.toLowerCase() === 'get') {
            config.params = {
                ...config.params,
                _t: config.forceUpdate ? Date.now() : undefined
            }
            delete config.forceUpdate
        }

        // 添加取消令牌
        if (!config.cancelToken) {
            config.cancelToken = new axios.CancelToken(cancel => {
                config.cancel = cancel
            })
        }

        // 显示加载状态
        if (config.showLoading !== false) {
            const appStore = useAppStore?.()
            if (appStore) {
                appStore.setLoading(true)
            }
        }

        // 修复：检查store是否可用，避免undefined错误
        try {
            const userStore = useUserStore?.()
            if (userStore?.token) {
                config.headers.Authorization = `Bearer ${userStore.token}`
            }
        } catch (error) {
            console.warn('User store not available:', error)
        }

        return requestQueue.add(config)
    },
    (error) => {
        const appStore = useAppStore?.()
        if (appStore) {
            appStore.setLoading(false)
        }
        return Promise.reject(error)
    }
)

// 响应拦截器
request.interceptors.response.use(
    (response) => {
        const requestId = response.config.requestId
        performanceMonitor.end(`request_${requestId}`)

        const appStore = useAppStore?.()
        if (appStore) {
            appStore.setLoading(false)
        }

        const { data, config } = response

        // 处理Result格式的响应
        if (data && typeof data === 'object' && 'code' in data) {
            const { code, message } = data

            if (code === 200) {
                if (config.showSuccess && message) {
                    ElMessage.success({
                        message,
                        duration: 3000,
                        grouping: true
                    })
                }
                return data
            }

            // 业务错误处理
            const error = new Error(message || '操作失败')
            error.code = code
            throw error
        }

        return data
    },
    (error) => {
        const appStore = useAppStore?.()
        if (appStore) {
            appStore.setLoading(false)
        }

        // 取消的请求不报错
        if (axios.isCancel(error)) {
            return Promise.reject(new Error('请求已取消'))
        }

        let errorType = ErrorType.API_ERROR
        let userMessage = '请求失败'

        if (!error.response) {
            if (error.message.includes('timeout')) {
                errorType = ErrorType.NETWORK_ERROR
                userMessage = '请求超时，请检查网络连接'
            } else if (error.message.includes('Network')) {
                errorType = ErrorType.NETWORK_ERROR
                userMessage = '网络连接失败，请检查网络设置'
            }
        } else {
            const { status, data } = error.response

            switch (status) {
                case 400:
                    errorType = ErrorType.VALIDATION_ERROR
                    userMessage = data?.message || '请求参数错误'
                    break
                case 401:
                    errorType = ErrorType.AUTH_ERROR
                    userMessage = '登录状态已过期，请重新登录'
                    // 触发登出
                    const userStore = useUserStore?.()
                    if (userStore) {
                        userStore.logout()
                        window.location.href = '/login'
                    }
                    break
                case 403:
                    userMessage = '没有权限执行此操作'
                    break
                case 404:
                    userMessage = '请求的资源不存在'
                    break
                case 500:
                    userMessage = '服务器内部错误，请稍后重试'
                    break
                case 502:
                case 503:
                case 504:
                    userMessage = '服务暂时不可用，请稍后重试'
                    break
                default:
                    userMessage = data?.message || `请求失败 (${status})`
            }
        }

        const errorInfo = errorHandler.handle(error, errorType, {
            url: error.config?.url,
            method: error.config?.method,
            status: error.response?.status
        })

        return Promise.reject(errorInfo)
    }
)

// 增强的请求方法
export const enhancedHttp = {
    get: (url, params = {}, config = {}) =>
        request.get(url, { params, ...config }),

    post: (url, data = {}, config = {}) =>
        request.post(url, data, config),

    put: (url, data = {}, config = {}) =>
        request.put(url, data, config),

    delete: (url, config = {}) =>
        request.delete(url, config),

    patch: (url, data = {}, config = {}) =>
        request.patch(url, data, config)
}

// 带重试的请求
export const retryableRequest = async (requestFn, maxRetries = 3, delay = 1000) => {
    for (let attempt = 1; attempt <= maxRetries; attempt++) {
        try {
            return await requestFn()
        } catch (error) {
            if (attempt === maxRetries) {
                throw error
            }

            // 指数退避
            const waitTime = delay * Math.pow(2, attempt - 1)
            console.warn(`请求失败，${waitTime}ms后重试 (${attempt}/${maxRetries})`)

            await new Promise(resolve => setTimeout(resolve, waitTime))
        }
    }
}

// 清除缓存
export const clearRequestCache = (key) => {
    if (key) {
        requestQueue.removeFromCache(key)
    } else {
        requestQueue.clearCache()
    }
}

export default request