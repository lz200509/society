// 格式化日期
export const formatDate = (date) => {
    if (!date) return ''
    const d = new Date(date)
    const year = d.getFullYear()
    const month = String(d.getMonth() + 1).padStart(2, '0')
    const day = String(d.getDate()).padStart(2, '0')
    const hours = String(d.getHours()).padStart(2, '0')
    const minutes = String(d.getMinutes()).padStart(2, '0')
    return `${year}-${month}-${day} ${hours}:${minutes}`
}

// 防抖函数
export const debounce = (func, wait) => {
    let timeout
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout)
            func(...args)
        }
        clearTimeout(timeout)
        timeout = setTimeout(later, wait)
    }
}

// 深拷贝
export const deepClone = (obj) => {
    if (obj === null || typeof obj !== 'object') return obj
    if (obj instanceof Date) return new Date(obj.getTime())
    if (obj instanceof Array) return obj.map(item => deepClone(item))
    if (obj instanceof Object) {
        const clonedObj = {}
        Object.keys(obj).forEach(key => {
            clonedObj[key] = deepClone(obj[key])
        })
        return clonedObj
    }
}

// 验证手机号
export const validatePhone = (phone) => {
    const reg = /^1[3-9]\d{9}$/
    return reg.test(phone)
}

// 验证学号/负责人编号
export const validateNumber = (rule, value, callback) => {
    const reg = /^[A-Za-z0-9]+$/
    if (!reg.test(value)) {
        callback(new Error('账号格式不正确'))
    } else {
        callback()
    }
}