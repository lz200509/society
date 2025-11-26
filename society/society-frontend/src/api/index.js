import request from '@/utils/request'

// 学生相关API
export const studentApi = {
    // 学生登录
    login: (data) => request.post('/students/login', null, {
        params: data,
        showSuccess: false,
        showLoading: true
    }),

    // 学生注册
    register: (data) => request.post('/students/register', data, {
        showSuccess: true,
        showLoading: true
    }),

    // 获取所有学生
    getAllStudents: () => request.get('/students'),

    // 根据ID获取学生
    getStudentById: (id) => request.get(`/students/${id}`),

    // 根据学号获取学生
    getStudentByStudentNum: (studentNum) => request.get(`/students/student-num/${studentNum}`),

    // 根据姓名获取学生列表
    getStudentsByName: (studentName) => request.get(`/students/name/${studentName}`),

    // 搜索学生（姓名模糊匹配）
    searchStudents: (studentName) => request.get('/students/search', { params: { studentName } }),

    // 根据手机号获取学生
    getStudentByPhone: (phone) => request.get(`/students/phone/${phone}`),

    // 更新学生信息
    updateStudent: (data) => request.put(`/students/${data.studentId}`, data),

    // 删除学生
    deleteStudent: (id) => request.delete(`/students/${id}`),

    // 检查学号是否存在
    checkStudentNumExists: (studentNum) => request.get('/students/check-student-num', {
        params: { studentNum },
        showSuccess: false,
        showLoading: false
    })
}

// 社团负责人相关API
export const leaderApi = {
    // 负责人登录
    login: (data) => request.post('/leaders/login', data, {
        headers: {
            'Content-Type': 'application/json'
        }
    }),

    // 获取所有负责人
    getAllLeaders: () => request.get('/leaders'),

    // 根据ID获取负责人
    getLeaderById: (id) => request.get(`/leaders/${id}`),

    // 根据负责人编号获取
    getLeaderByLeaderNum: (leaderNum) => request.get(`/leaders/leader-num/${leaderNum}`),

    // 根据社团ID获取负责人
    getLeaderByClubId: (clubId) => request.get(`/leaders/club/${clubId}`),

    // 根据负责人姓名获取列表
    getLeadersByName: (leaderName) => request.get(`/leaders/name/${leaderName}`),

    // 根据手机号获取负责人
    getLeaderByPhone: (phone) => request.get(`/leaders/phone/${phone}`),

    // 创建负责人
    createLeader: (data) => request.post('/leaders', data),

    // 更新负责人信息
    updateLeader: (data) => http.put(`/leaders/${data.leaderId}`, data),

    // 删除负责人
    deleteLeader: (id) => request.delete(`/leaders/${id}`),

    // 检查负责人编号是否存在
    checkLeaderNumExists: (leaderNum) => request.get('/leaders/check-leader-num', { params: { leaderNum } })
}

// 社团相关API
export const clubApi = {
    // 获取所有社团
    getAllClubs: () => request.get('/clubs', {
        showLoading: true,
        showSuccess: false
    }),
    // 根据ID获取社团
    getClubById: (id) => request.get(`/clubs/${id}`),

    // 根据名称获取社团
    getClubByName: (clubName) => request.get(`/clubs/name/${clubName}`),

    // 根据类型获取社团列表
    getClubsByType: (clubType) => request.get(`/clubs/type/${clubType}`),

    // 搜索社团（名称模糊匹配）
    searchClubs: (clubName) => request.get('/clubs/search', { params: { clubName } }),

    // 获取有剩余名额的社团
    getAvailableClubs: (minQuota = 0) => request.get('/clubs/available', { params: { minQuota } }),

    // 创建新社团
    createClub: (data) => request.post('/clubs', data),

    // 更新社团信息
    updateClub: (data) => {
        const { clubId, ...updateData } = data
        return request.put(`/clubs/${clubId}`, updateData)
    },

    // 更新社团剩余名额
    updateClubQuota: (clubId, remainingQuota) =>
        request.put(`/clubs/${clubId}/quota`, null, { params: { remainingQuota } }),

    // 删除社团
    deleteClub: (id) => request.delete(`/clubs/${id}`),

    // 检查社团名称是否存在
    checkClubNameExists: (clubName) => request.get('/clubs/check-name', { params: { clubName } })
}

// 社团申请相关API
export const applicationApi = {
    // 获取所有申请记录
    getAllApplications: () => request.get('/applications'),

    // 根据ID获取申请记录
    getApplicationById: (id) => request.get(`/applications/${id}`),

    // 根据学生ID获取申请记录
    getApplicationsByStudentId: (studentId) => request.get(`/applications/student/${studentId}`),

    // 根据社团ID获取申请记录
    getApplicationsByClubId: (clubId) => request.get(`/applications/club/${clubId}`),

    // 根据审核状态获取申请记录
    getApplicationsByStatus: (auditStatus) => request.get(`/applications/status/${auditStatus}`),

    // 根据学生ID和社团ID获取申请记录
    getApplicationByStudentAndClub: (studentId, clubId) =>
        request.get(`/applications/student/${studentId}/club/${clubId}`),

    // 根据学生ID和状态获取申请记录
    getApplicationsByStudentAndStatus: (studentId, auditStatus) =>
        request.get(`/applications/student/${studentId}/status/${auditStatus}`),

    // 根据社团ID和状态获取申请记录
    getApplicationsByClubAndStatus: (clubId, auditStatus) =>
        request.get(`/applications/club/${clubId}/status/${auditStatus}`),

    // 提交社团申请
    submitApplication: (data) => request.post('/applications', data, {
        showSuccess: true,
        showLoading: true
    }),

    // 审核申请
    auditApplication: (recordId, auditStatus) =>
        request.put(`/applications/${recordId}/audit`, null, { params: { auditStatus } }),

    // 删除申请记录
    deleteApplication: (id) => request.delete(`/applications/${id}`),

    // 检查学生是否已经申请过该社团
    checkApplicationExists: (studentId, clubId) =>
        request.get('/applications/check-application', { params: { studentId, clubId } }),

    // 统计学生的申请数量
    countApplicationsByStudent: (studentId) => request.get(`/applications/student/${studentId}/count`),

    // 统计社团的申请数量
    countApplicationsByClub: (clubId) => request.get(`/applications/club/${clubId}/count`),

    // 根据时间范围查找申请记录
    getApplicationsByTimeRange: (startTime, endTime) =>
        request.get('/applications/time-range', { params: { startTime, endTime } })
}

// 数据统计相关API
export const statisticsApi = {
    // 获取系统统计信息
    getSystemStats: () => request.get('/statistics/system'),

    // 获取社团统计信息
    getClubStats: (clubId) => request.get(`/statistics/clubs/${clubId}`),

    // 获取学生申请统计
    getStudentApplicationStats: (studentId) => request.get(`/statistics/students/${studentId}/applications`)
}

// 文件上传相关API
export const uploadApi = {
    // 上传社团Logo
    uploadClubLogo: (formData) => request.post('/upload/club-logo', formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    }),

    // 上传学生头像
    uploadStudentAvatar: (formData) => request.post('/upload/student-avatar', formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}

export default {
    student: studentApi,
    leader: leaderApi,
    club: clubApi,
    application: applicationApi,
    statistics: statisticsApi,
    upload: uploadApi
}