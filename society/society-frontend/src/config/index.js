// 环境配置
const config = {
    development: {
        baseURL: 'http://localhost:8080/api',
        timeout: 15000,
        enableMock: true,
        logLevel: 'debug'
    },
    test: {
        baseURL: 'http://test-api.example.com/api',
        timeout: 10000,
        enableMock: false,
        logLevel: 'info'
    },
    production: {
        baseURL: 'https://api.example.com/api',
        timeout: 10000,
        enableMock: false,
        logLevel: 'warn'
    }
}

// 获取当前环境配置
export const getConfig = () => {
    const env = process.env.NODE_ENV || 'development'
    return config[env]
}

// 特性开关
export const featureFlags = {
    // 性能监控
    PERFORMANCE_MONITOR: true,

    // 错误上报
    ERROR_REPORTING: process.env.NODE_ENV === 'production',

    // 请求缓存
    REQUEST_CACHE: true,

    // 请求重试
    REQUEST_RETRY: true,

    // 数据压缩
    DATA_COMPRESSION: false
}

// API配置
export const apiConfig = {
    // 重试配置
    retry: {
        maxRetries: 3,
        baseDelay: 1000
    },

    // 缓存配置
    cache: {
        defaultTimeout: 5 * 60 * 1000, // 5分钟
        maxSize: 100
    },

    // 超时配置
    timeout: {
        default: 15000,
        upload: 30000,
        download: 60000
    }
}

export default getConfig()