import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { createTestingPinia } from '@pinia/testing'
import ApplicationList from '../ApplicationList.vue'
import { createTestData, mockApiResponse, setupTestConfig } from '@/utils/test-utils'

describe('ApplicationList', () => {
    beforeEach(() => {
        setupTestConfig()
    })

    it('渲染申请列表', () => {
        const applications = [
            createTestData.application({ recordId: 1, auditStatus: '待审核' }),
            createTestData.application({ recordId: 2, auditStatus: '已通过' })
        ]

        const wrapper = mount(ApplicationList, {
            global: {
                plugins: [createTestingPinia({
                    createSpy: vi.fn,
                    initialState: {
                        user: {
                            isLoggedIn: true,
                            isStudent: true,
                            userInfo: createTestData.student()
                        }
                    }
                })]
            },
            props: {
                applications,
                loading: false
            }
        })

        expect(wrapper.findAll('.application-item')).toHaveLength(2)
        expect(wrapper.text()).toContain('待审核')
        expect(wrapper.text()).toContain('已通过')
    })

    it('显示加载状态', () => {
        const wrapper = mount(ApplicationList, {
            props: {
                applications: [],
                loading: true
            }
        })

        expect(wrapper.find('.loading-container').exists()).toBe(true)
    })

    it('显示空状态', () => {
        const wrapper = mount(ApplicationList, {
            props: {
                applications: [],
                loading: false,
                emptyText: '暂无数据'
            }
        })

        expect(wrapper.text()).toContain('暂无数据')
    })

    it('触发查看详情事件', async () => {
        const applications = [createTestData.application()]
        const wrapper = mount(ApplicationList, {
            props: {
                applications,
                loading: false
            }
        })

        await wrapper.find('.application-item .el-button').trigger('click')
        expect(wrapper.emitted('view')).toBeTruthy()
    })

    it('限制显示数量', () => {
        const applications = Array.from({ length: 10 }, (_, i) =>
            createTestData.application({ recordId: i + 1 })
        )

        const wrapper = mount(ApplicationList, {
            props: {
                applications,
                limit: 5,
                loading: false
            }
        })

        expect(wrapper.findAll('.application-item')).toHaveLength(5)
        expect(wrapper.text()).toContain('查看更多申请')
    })
})