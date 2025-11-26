import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
    const isLoading = ref(false)
    const pageTitle = ref('社团管理系统')

    // 社团类型选项
    const clubTypes = ref([
        { label: '体育', value: '体育' },
        { label: '文艺', value: '文艺' },
        { label: '学术', value: '学术' }
    ])

    // 审核状态选项
    const auditStatus = ref([
        { label: '待审核', value: '待审核' },
        { label: '已通过', value: '已通过' },
        { label: '已拒绝', value: '已拒绝' }
    ])

    const setLoading = (loading) => {
        isLoading.value = loading
    }

    const setPageTitle = (title) => {
        pageTitle.value = title
        document.title = title
    }

    return {
        isLoading,
        pageTitle,
        clubTypes,
        auditStatus,
        setLoading,
        setPageTitle
    }
})