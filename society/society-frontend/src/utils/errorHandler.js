import { ElMessage } from 'element-plus'

// 错误类型枚举
export const ErrorType = {
    NETWORK_ERROR: 'NETWORK_ERROR',
    API_ERROR: 'API_ERROR',
    VALIDATION_ERROR: 'VALIDATION_ERROR',
    AUTH_ERROR: 'AUTH_ERROR',
    UNKNOWN_ERROR: 'UNKNOWN_ERROR'
}

// 错误处理器
class ErrorHandler {
    constructor() {
        this.reportUrl = null
        this.enableReporting = process.env.NODE_ENV === 'production'
    }

    // 处理错误
    handle(error, type = ErrorType.UNKNOWN_ERROR, context = {}) {
        const errorInfo = this.normalizeError(error, type, context)

        // 开发环境打印详细错误
        if (process.env.NODE_ENV === 'development') {
            console.error('🚨 Error Details:', errorInfo)
        }

        // 用户友好的错误提示
        this.showUserMessage(errorInfo)

        // 生产环境上报错误
        if (this.enableReporting) {
            this.reportError(errorInfo)
        }

        return errorInfo
    }

    // 规范化错误信息
    normalizeError(error, type, context) {
        const errorInfo = {
            type,
            timestamp: new Date().toISOString(),
            context,
            userAgent: navigator.userAgent,
            url: window.location.href
        }

        if (error instanceof Error) {
            errorInfo.message = error.message
            errorInfo.stack = error.stack
            errorInfo.name = error.name
        } else if (typeof error === 'string') {
            errorInfo.message = error
        } else {
            errorInfo.message = '未知错误'
            errorInfo.originalError = error
        }

        return errorInfo
    }

    // 显示用户友好的错误消息
    showUserMessage(errorInfo) {
        const { type, message } = errorInfo

        const messageMap = {
            [ErrorType.NETWORK_ERROR]: '网络连接失败，请检查网络设置',
            [ErrorType.API_ERROR]: message || '服务器繁忙，请稍后重试',
            [ErrorType.VALIDATION_ERROR]: message || '输入数据格式不正确',
            [ErrorType.AUTH_ERROR]: '登录状态已过期，请重新登录',
            [ErrorType.UNKNOWN_ERROR]: '系统发生未知错误，请联系管理员'
        }

        const userMessage = messageMap[type] || messageMap[ErrorType.UNKNOWN_ERROR]

        ElMessage.error({
            message: userMessage,
            duration: 5000,
            showClose: true
        })
    }

    // 上报错误到服务器
    reportError(errorInfo) {
        if (!this.reportUrl) return

        // 使用navigator.sendBeacon进行无阻塞上报
        const data = new Blob([JSON.stringify(errorInfo)], {
            type: 'application/json'
        })

        if (navigator.sendBeacon) {
            navigator.sendBeacon(this.reportUrl, data)
        } else {
            // 降级方案使用fetch
            fetch(this.reportUrl, {
                method: 'POST',
                body: data,
                keepalive: true
            }).catch(() => {
                // 静默失败
            })
        }
    }

    // 设置错误上报URL
    setReportUrl(url) {
        this.reportUrl = url
    }
}

// 创建全局错误处理器实例
export const errorHandler = new ErrorHandler()

// Vue错误处理
export const setupVueErrorHandler = (app) => {
    app.config.errorHandler = (err, vm, info) => {
        errorHandler.handle(err, ErrorType.UNKNOWN_ERROR, {
            component: vm?.$options?.name,
            lifeCycleHook: info
        })
    }
}

// 全局未捕获错误处理
window.addEventListener('error', (event) => {
    errorHandler.handle(event.error, ErrorType.UNKNOWN_ERROR, {
        filename: event.filename,
        lineno: event.lineno,
        colno: event.colno
    })
})

// 全局未处理的Promise拒绝处理
window.addEventListener('unhandledrejection', (event) => {
    errorHandler.handle(event.reason, ErrorType.UNKNOWN_ERROR, {
        type: 'unhandledrejection'
    })
})