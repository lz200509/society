import { ref } from 'vue'
import { errorHandler, ErrorType } from '@/utils/errorHandler'
import { performanceMonitor } from '@/utils/performance'
import { debounce } from '@/utils/performance'

// API调用状态管理
export const useApi = (apiFunction, options = {}) => {
    const {
        immediate = false,
        debounce: debounceMs = 0,
        onSuccess,
        onError,
        showLoading = true
    } = options

    const data = ref(null)
    const loading = ref(false)
    const error = ref(null)
    const executed = ref(false)

    let debouncedExecute

    // 执行API调用
    const execute = async (...args) => {
        if (debounceMs > 0 && !debouncedExecute) {
            debouncedExecute = debounce(execute, debounceMs)
            return debouncedExecute(...args)
        }

        const operationName = apiFunction.name || 'API_Call'

        try {
            performanceMonitor.start(operationName)
            loading.value = true
            error.value = null

            if (showLoading) {
                // 显示全局加载状态
                const appStore = useAppStore()
                appStore.setLoading(true)
            }

            const response = await apiFunction(...args)

            performanceMonitor.end(operationName)

            if (response && response.code === 200) {
                data.value = response.data
                onSuccess?.(response.data, ...args)
                return response
            } else {
                throw new Error(response?.message || 'API调用失败')
            }
        } catch (err) {
            performanceMonitor.end(operationName)
            error.value = err

            const errorInfo = errorHandler.handle(err, ErrorType.API_ERROR, {
                api: apiFunction.name,
                args
            })

            onError?.(errorInfo, ...args)
            throw err
        } finally {
            loading.value = false
            executed.value = true

            if (showLoading) {
                const appStore = useAppStore()
                appStore.setLoading(false)
            }
        }
    }

    // 立即执行（可选）
    if (immediate) {
        execute()
    }

    // 重置状态
    const reset = () => {
        data.value = null
        loading.value = false
        error.value = null
        executed.value = false
    }

    return {
        data,
        loading,
        error,
        executed,
        execute,
        reset
    }
}

// 批量API调用
export const useBatchApi = (apiFunctions, options = {}) => {
    const {
        onAllSuccess,
        onAnyError,
        showLoading = true
    } = options

    const results = ref({})
    const loading = ref(false)
    const errors = ref({})
    const allExecuted = ref(false)

    const execute = async (paramsMap = {}) => {
        loading.value = true
        errors.value = {}
        allExecuted.value = false

        if (showLoading) {
            const appStore = useAppStore()
            appStore.setLoading(true)
        }

        try {
            const promises = Object.entries(apiFunctions).map(([key, apiFunc]) => {
                const params = paramsMap[key] || []
                return apiFunc(...params)
                    .then(response => ({ key, response, error: null }))
                    .catch(error => ({ key, response: null, error }))
            })

            const settledResults = await Promise.allSettled(promises)

            settledResults.forEach(({ value }) => {
                const { key, response, error } = value

                if (error) {
                    errors.value[key] = error
                    errorHandler.handle(error, ErrorType.API_ERROR, { api: key })
                } else {
                    results.value[key] = response.data
                }
            })

            // 检查是否有错误
            const hasErrors = Object.keys(errors.value).length > 0

            if (hasErrors) {
                onAnyError?.(errors.value)
            } else {
                onAllSuccess?.(results.value)
            }

            allExecuted.value = true
            return results.value
        } finally {
            loading.value = false
            allExecuted.value = true

            if (showLoading) {
                const appStore = useAppStore()
                appStore.setLoading(false)
            }
        }
    }

    const reset = () => {
        results.value = {}
        loading.value = false
        errors.value = {}
        allExecuted.value = false
    }

    return {
        results,
        loading,
        errors,
        allExecuted,
        execute,
        reset
    }
}