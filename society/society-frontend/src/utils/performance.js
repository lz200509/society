// 性能监控工具
class PerformanceMonitor {
    constructor() {
        this.metrics = new Map()
        this.startTime = performance.now()
    }

    // 开始测量
    start(name) {
        this.metrics.set(name, {
            start: performance.now(),
            end: null,
            duration: null
        })
    }

    // 结束测量
    end(name) {
        const metric = this.metrics.get(name)
        if (metric) {
            metric.end = performance.now()
            metric.duration = metric.end - metric.start
        }
    }

    // 获取测量结果
    getMetrics() {
        return Array.from(this.metrics.entries()).reduce((acc, [name, metric]) => {
            acc[name] = metric
            return acc
        }, {})
    }

    // 打印测量结果
    logMetrics() {
        const metrics = this.getMetrics()
        console.group('📊 Performance Metrics')
        Object.entries(metrics).forEach(([name, metric]) => {
            if (metric.duration) {
                console.log(`${name}: ${metric.duration.toFixed(2)}ms`)
            }
        })
        console.groupEnd()
    }
}

// 创建全局性能监控实例
export const performanceMonitor = new PerformanceMonitor()

// 防抖函数
export const debounce = (func, wait, immediate = false) => {
    let timeout
    return function executedFunction(...args) {
        const later = () => {
            timeout = null
            if (!immediate) func(...args)
        }
        const callNow = immediate && !timeout
        clearTimeout(timeout)
        timeout = setTimeout(later, wait)
        if (callNow) func(...args)
    }
}

// 节流函数
export const throttle = (func, limit) => {
    let inThrottle
    return function(...args) {
        if (!inThrottle) {
            func.apply(this, args)
            inThrottle = true
            setTimeout(() => inThrottle = false, limit)
        }
    }
}

// 内存优化：对象属性清理
export const cleanObject = (obj) => {
    const cleaned = { ...obj }
    Object.keys(cleaned).forEach(key => {
        if (cleaned[key] === null || cleaned[key] === undefined || cleaned[key] === '') {
            delete cleaned[key]
        }
    })
    return cleaned
}

// 数据压缩（用于大量数据传输）
export const compressData = (data) => {
    try {
        return JSON.stringify(data)
    } catch (error) {
        console.error('Data compression error:', error)
        return data
    }
}

// 数据解压缩
export const decompressData = (data) => {
    try {
        return JSON.parse(data)
    } catch (error) {
        console.error('Data decompression error:', error)
        return data
    }
}