import { config } from '@vue/test-utils'
import { createPinia } from 'pinia'
import { createI18n } from 'vue-i18n'
import ElementPlus from 'element-plus'

// 测试配置
export const setupTestConfig = () => {
    const pinia = createPinia()

    const i18n = createI18n({
        legacy: false,
        locale: 'zh-cn',
        messages: {
            'zh-cn': {}
        }
    })

    config.global.plugins = [pinia, i18n, ElementPlus]

    // 模拟用户存储
    config.global.mocks = {
        $userStore: {
            isLoggedIn: true,
            isStudent: true,
            isLeader: false,
            userInfo: {
                studentId: 1,
                studentName: '测试用户',
                studentNum: '2024001'
            },
            token: 'test-token'
        }
    }

    // 模拟路由
    config.global.mocks.$router = {
        push: jest.fn(),
        back: jest.fn(),
        currentRoute: {
            value: {
                path: '/'
            }
        }
    }

    config.global.mocks.$route = {
        path: '/',
        params: {},
        query: {}
    }
}

// 测试数据工厂
export const createTestData = {
    student: (overrides = {}) => ({
        studentId: 1,
        studentNum: '2024001',
        studentName: '测试学生',
        password: '123456',
        phone: '13800138000',
        createTime: '2024-01-01 10:00:00',
        ...overrides
    }),

    club: (overrides = {}) => ({
        clubId: 1,
        clubName: '测试社团',
        clubType: '体育',
        clubIntro: '这是一个测试社团',
        totalQuota: 50,
        remainingQuota: 45,
        logoUrl: '/test-logo.png',
        ...overrides
    }),

    application: (overrides = {}) => ({
        recordId: 1,
        student: createTestData.student(),
        club: createTestData.club(),
        applyTime: '2024-01-01 10:00:00',
        auditStatus: '待审核',
        ...overrides
    }),

    leader: (overrides = {}) => ({
        leaderId: 1,
        leaderNum: 'L2024001',
        leaderName: '测试负责人',
        password: '123456',
        phone: '13900139000',
        club: createTestData.club(),
        ...overrides
    })
}

// 模拟API响应
export const mockApiResponse = {
    success: (data = null, message = '操作成功') => ({
        code: 200,
        message,
        data,
        timestamp: Date.now()
    }),

    error: (message = '操作失败', code = 500) => ({
        code,
        message,
        data: null,
        timestamp: Date.now()
    }),

    paginated: (list, total = list.length, pageNum = 1, pageSize = 10) => ({
        code: 200,
        message: '操作成功',
        data: {
            total,
            list,
            pageNum,
            pageSize,
            totalPages: Math.ceil(total / pageSize)
        },
        timestamp: Date.now()
    })
}

// 异步测试工具
export const waitFor = (ms = 0) => new Promise(resolve => setTimeout(resolve, ms))

export const flushPromises = () => new Promise(resolve => setImmediate(resolve))

// 模拟Element Plus组件
export const mockElementComponents = {
    ElMessage: {
        success: jest.fn(),
        error: jest.fn(),
        warning: jest.fn(),
        info: jest.fn()
    },
    ElMessageBox: {
        confirm: jest.fn().mockResolvedValue('confirm'),
        prompt: jest.fn().mockResolvedValue('prompt'),
        alert: jest.fn().mockResolvedValue('alert')
    },
    ElLoading: {
        service: jest.fn().mockReturnValue({
            close: jest.fn()
        })
    }
}