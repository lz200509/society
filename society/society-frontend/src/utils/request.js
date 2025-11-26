import axios from 'axios'
import { ElMessage, ElLoading } from 'element-plus'
import router from '@/router'
import { useUserStore } from '@/stores/user'
import { useAppStore } from '@/stores/app'


// 创建axios实例
const request = axios.create({
    baseURL: 'http://localhost:8080/api',
    timeout: 15000,
    headers: {
        'Content-Type': 'application/json;charset=UTF-8'
    },
    withCredentials: true,
})

// 请求队列，用于取消重复请求
const pendingRequest = new Map()

// 生成请求key
function generateReqKey(config) {
    const { method, url, params, data } = config
    return [method, url, JSON.stringify(params), JSON.stringify(data)].join('&')
}

// 添加请求到队列
function addPendingRequest(config) {
    const requestKey = generateReqKey(config)
    config.cancelToken = config.cancelToken || new axios.CancelToken(cancel => {
        if (!pendingRequest.has(requestKey)) {
            pendingRequest.set(requestKey, cancel)
        }
    })
}

// 移除请求从队列
function removePendingRequest(config) {
    const requestKey = generateReqKey(config)
    if (pendingRequest.has(requestKey)) {
        const cancel = pendingRequest.get(requestKey)
        cancel(requestKey)
        pendingRequest.delete(requestKey)
    }
}

// 请求拦截器
request.interceptors.request.use(
    config => {
        removePendingRequest(config) // 检查是否存在重复请求，若存在则取消
        addPendingRequest(config) // 把当前请求添加到pendingRequest中

        const userStore = useUserStore()
        const appStore = useAppStore()

        // 显示加载状态
        if (config.showLoading !== false) {
            appStore.setLoading(true)
        }

        // 添加token到请求头
        if (userStore.token) {
            config.headers.Authorization = `Bearer ${userStore.token}`
        }

        // 处理GET请求缓存
        if (config.method === 'get') {
            config.params = {
                ...config.params,
                _t: Date.now() // 添加时间戳防止缓存
            }
        }

        return config
    },
    error => {
        const appStore = useAppStore()
        appStore.setLoading(false)
        return Promise.reject(error)
    }
)

// 响应拦截器
request.interceptors.response.use(
    response => {
        removePendingRequest(response.config) // 从pendingRequest中移除请求

        const appStore = useAppStore()
        appStore.setLoading(false)

        const { data } = response
        const { config } = response

        // 如果返回的是Result格式
        if (data && typeof data === 'object' && 'code' in data) {
            const { code, message } = data

            // 成功请求
            if (code === 200) {
                // 显示成功消息（如果配置了showSuccess）
                if (config.showSuccess && message) {
                    ElMessage.success(message)
                }
                return data
            }

            // 业务异常处理
            let errorMessage = message || '操作失败'

            switch (code) {
                case 401:
                    errorMessage = message || '未授权访问'
                    ElMessage.error(errorMessage)
                    const userStore = useUserStore()
                    userStore.logout()
                    router.push('/login')
                    break
                case 403:
                    errorMessage = message || '禁止访问'
                    ElMessage.error(errorMessage)
                    break
                case 404:
                    errorMessage = message || '资源不存在'
                    ElMessage.error(errorMessage)
                    break
                case 400:
                    errorMessage = message || '请求参数错误'
                    ElMessage.error(errorMessage)
                    break
                case 409:
                    errorMessage = message || '资源冲突'
                    ElMessage.error(errorMessage)
                    break
                case 500:
                    errorMessage = message || '服务器内部错误'
                    ElMessage.error(errorMessage)
                    break
                default:
                    ElMessage.error(errorMessage)
            }

            return Promise.reject(new Error(errorMessage))
        }

        // 如果不是Result格式，直接返回数据
        return data
    },
    error => {
        const appStore = useAppStore()
        appStore.setLoading(false)

        if (axios.isCancel(error)) {
            console.log('请求被取消:', error.message)
            return Promise.reject(new Error('请求被取消'))
        }

        let message = '请求失败'
        if (error.response) {
            const { status, data } = error.response

            switch (status) {
                case 400:
                    message = data?.message || '请求错误'
                    break
                case 401:
                    message = data?.message || '未授权，请重新登录'
                    const userStore = useUserStore()
                    userStore.logout()
                    router.push('/login')
                    break
                case 403:
                    message = data?.message || '拒绝访问'
                    break
                case 404:
                    message = data?.message || '请求地址出错'
                    break
                case 408:
                    message = '请求超时'
                    break
                case 500:
                    message = data?.message || '服务器内部错误'
                    break
                case 501:
                    message = '服务未实现'
                    break
                case 502:
                    message = '网关错误'
                    break
                case 503:
                    message = '服务不可用'
                    break
                case 504:
                    message = '网关超时'
                    break
                case 505:
                    message = 'HTTP版本不受支持'
                    break
                default:
                    message = data?.message || '请求失败'
            }
        } else if (error.message.includes('timeout')) {
            message = '请求超时'
        } else if (error.message.includes('Network')) {
            message = '网络连接失败'
        }

        ElMessage.error(message)
        return Promise.reject(error)
    }
)

// 封装常用的请求方法
export const http = {
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

// 文件上传方法
export const uploadFile = (url, file, onProgress = null, config = {}) => {
    const formData = new FormData()
    formData.append('file', file)

    return request.post(url, formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        },
        onUploadProgress: onProgress,
        ...config
    })
}

// 下载文件方法
export const downloadFile = (url, params = {}, filename = 'download') => {
    return request.get(url, {
        params,
        responseType: 'blob'
    }).then(response => {
        const blob = new Blob([response])
        const downloadUrl = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = downloadUrl
        link.download = filename
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(downloadUrl)
    })
}

export default request