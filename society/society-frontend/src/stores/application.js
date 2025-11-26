import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { applicationApi } from '@/api'

export const useApplicationStore = defineStore('application', () => {
    const applications = ref([])
    const currentApplication = ref(null)
    const loading = ref(false)
    const auditStatus = ref(['待审核', '已通过', '已拒绝'])

    // 计算属性
    const pendingApplications = computed(() =>
        applications.value.filter(app => app.auditStatus === '待审核')
    )
    const approvedApplications = computed(() =>
        applications.value.filter(app => app.auditStatus === '已通过')
    )
    const rejectedApplications = computed(() =>
        applications.value.filter(app => app.auditStatus === '已拒绝')
    )

    // 获取所有申请
    const fetchAllApplications = async () => {
        loading.value = true
        try {
            const response = await applicationApi.getAllApplications()
            if (response.code === 200) {
                applications.value = response.data
            }
            return response
        } catch (error) {
            console.error('获取申请列表失败:', error)
            throw error
        } finally {
            loading.value = false
        }
    }

    // 根据学生ID获取申请
    const fetchApplicationsByStudent = async (studentId) => {
        loading.value = true
        try {
            console.log('调用API获取学生申请，学生ID:', studentId)
            const response = await applicationApi.getApplicationsByStudentId(studentId)
            console.log('API返回数据:', response)

            if (response.code === 200) {
                applications.value = response.data
                console.log('Store中的申请数据已更新:', applications.value)
            } else {
                console.warn('API返回非200状态:', response)
            }
            return response
        } catch (error) {
            console.error('获取学生申请失败:', error)
            throw error
        } finally {
            loading.value = false
        }
    }

    // 根据社团ID获取申请
    const fetchApplicationsByClub = async (clubId) => {
        loading.value = true
        try {
            const response = await applicationApi.getApplicationsByClubId(clubId)
            if (response.code === 200) {
                applications.value = response.data
            }
            return response
        } catch (error) {
            console.error('获取社团申请失败:', error)
            throw error
        } finally {
            loading.value = false
        }
    }

    // 提交申请
    const submitApplication = async (applicationData) => {
        loading.value = true
        try {
            console.log('提交申请数据到API:', applicationData)

            const response = await applicationApi.submitApplication(applicationData)
            console.log('API响应:', response)

            if (response.code === 200) {
                // 确保数据格式正确
                const newApplication = {
                    ...response.data,
                    student: userStore.userInfo, // 添加学生信息
                    club: applicationData.club // 添加社团信息
                }
                applications.value.push(newApplication)

                // 触发事件通知其他组件
                emitApplicationUpdate()
            }
            return response
        } catch (error) {
            console.error('提交申请失败:', error)
            // 提供更详细的错误信息
            if (error.response?.data) {
                throw new Error(error.response.data.message || error.response.data)
            }
            throw error
        } finally {
            loading.value = false
        }
    }

// 添加应用更新事件
    const emitApplicationUpdate = () => {
        // 可以在这里添加事件总线或状态更新逻辑
        console.log('申请数据已更新')
    }

    // 审核申请
    const auditApplication = async (recordId, auditStatus) => {
        loading.value = true
        try {
            const response = await applicationApi.auditApplication(recordId, auditStatus)
            if (response.code === 200) {
                const index = applications.value.findIndex(app => app.recordId === recordId)
                if (index !== -1) {
                    applications.value[index].auditStatus = auditStatus
                }
            }
            return response
        } catch (error) {
            console.error('审核申请失败:', error)
            throw error
        } finally {
            loading.value = false
        }
    }

    // 删除申请
    const deleteApplication = async (recordId) => {
        loading.value = true
        try {
            const response = await applicationApi.deleteApplication(recordId)
            if (response.code === 200) {
                const index = applications.value.findIndex(app => app.recordId === recordId)
                if (index !== -1) {
                    applications.value.splice(index, 1)
                }
            }
            return response
        } catch (error) {
            console.error('删除申请失败:', error)
            throw error
        } finally {
            loading.value = false
        }
    }

    // 检查是否已申请
    const checkApplicationExists = async (studentId, clubId) => {
        try {
            const response = await applicationApi.checkApplicationExists(studentId, clubId)
            return response.data
        } catch (error) {
            console.error('检查申请状态失败:', error)
            throw error
        }
    }

    // 获取申请统计
    const getApplicationStats = async (type, id) => {
        try {
            let response
            if (type === 'student') {
                response = await applicationApi.countApplicationsByStudent(id)
            } else {
                response = await applicationApi.countApplicationsByClub(id)
            }
            return response.data
        } catch (error) {
            console.error('获取申请统计失败:', error)
            throw error
        }
    }

    return {
        applications,
        currentApplication,
        loading,
        auditStatus,
        pendingApplications,
        approvedApplications,
        rejectedApplications,
        fetchAllApplications,
        fetchApplicationsByStudent,
        fetchApplicationsByClub,
        submitApplication,
        auditApplication,
        deleteApplication,
        checkApplicationExists,
        getApplicationStats
    }
})