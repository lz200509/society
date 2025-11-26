import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { studentApi, leaderApi } from '@/api'
export const useUserStore = defineStore('user', () => {
    const token = ref(localStorage.getItem('token') || '')
    const userInfo = ref(null)
    const userType = ref(localStorage.getItem('userType') || '')
    try {
        const storedUserInfo = localStorage.getItem('userInfo')
        if (storedUserInfo) {
            userInfo.value = JSON.parse(storedUserInfo)
        }
    } catch (error) {
        console.error('Failed to parse user info from localStorage:', error)
        localStorage.removeItem('userInfo')
    }
    // 计算属性
    const isLoggedIn = computed(() => !!token.value)
    const isStudent = computed(() => userType.value === 'student')
    const isLeader = computed(() => userType.value === 'leader')
    const userId = computed(() => userInfo.value?.studentId || userInfo.value?.leaderId)
    // 学生登录
    const studentLogin = async (loginData) => {
        try {
            console.log('调用学生登录API:', loginData)
            const response = await studentApi.login(loginData)
            console.log('学生登录API响应:', response)

            // 修复：检查响应结构
            if (response && response.code === 200) {
                token.value = 'student-token-' + Date.now()
                userInfo.value = response.data
                userType.value = 'student'

                // 存储到localStorage
                localStorage.setItem('token', token.value)
                localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
                localStorage.setItem('userType', userType.value)

                return response
            } else {
                throw new Error(response?.message || '登录失败')
            }
        } catch (error) {
            console.error('学生登录失败:', error)
            throw error
        }
    }

    // 负责人登录
    const leaderLogin = async (loginData) => {
        try {
            console.log('调用负责人登录API，原始参数:', loginData)

            // 确保参数格式正确
            const requestData = {
                leaderNum: loginData.leaderNum || loginData.username,
                password: loginData.password
            }

            console.log('处理后的请求数据:', requestData)
            console.log('JSON字符串:', JSON.stringify(requestData))

            const response = await leaderApi.login(requestData)
            console.log('负责人登录API响应:', response)

            if (response && response.code === 200) {
                token.value = 'leader-token-' + Date.now()
                userInfo.value = response.data
                userType.value = 'leader'

                localStorage.setItem('token', token.value)
                localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
                localStorage.setItem('userType', userType.value)

                return response
            } else {
                throw new Error(response?.message || '登录失败')
            }
        } catch (error) {
            console.error('负责人登录失败:', error)
            console.error('错误响应:', error.response?.data)
            console.error('错误状态:', error.response?.status)
            throw error
        }
    }


    // 学生注册
    const studentRegister = async (registerData) => {
        try {
            const response = await studentApi.register(registerData)
            return Promise.resolve(response)
        } catch (error) {
            return Promise.reject(error)
        }
    }

    // 退出登录
    const logout = () => {
        token.value = ''
        userInfo.value = null
        userType.value = ''

        // 清除localStorage
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        localStorage.removeItem('userType')
    }

    // 更新用户信息
    const updateUserInfo = async (data) => {
        try {
            let response
            if (userType.value === 'student') {
                response = await studentApi.updateStudent(data)
            } else {
                response = await leaderApi.updateLeader(data)
            }

            if (response.code === 200) {
                userInfo.value = response.data
                localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
            }
            return Promise.resolve(response)
        } catch (error) {
            return Promise.reject(error)
        }
    }

    return {
        token,
        userInfo,
        userType,
        isLoggedIn,
        isStudent,
        isLeader,
        userId,
        studentLogin,
        leaderLogin,
        studentRegister,
        logout,
        updateUserInfo
    }
})