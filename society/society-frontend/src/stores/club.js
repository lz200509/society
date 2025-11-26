import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { clubApi } from '@/api'

export const useClubStore = defineStore('club', () => {
    const clubs = ref([])
    const currentClub = ref(null)
    const clubTypes = ref(['体育', '文艺', '学术'])
    const loading = ref(false)

    // 计算属性
    const sportsClubs = computed(() => clubs.value.filter(club => club.clubType === '体育'))
    const artsClubs = computed(() => clubs.value.filter(club => club.clubType === '文艺'))
    const academicClubs = computed(() => clubs.value.filter(club => club.clubType === '学术'))
    const availableClubs = computed(() => clubs.value.filter(club => club.remainingQuota > 0))

    // 获取所有社团
    const fetchAllClubs = async () => {
        loading.value = true
        try {
            const response = await clubApi.getAllClubs()
            if (response.code === 200) {
                clubs.value = response.data
            }
            return response
        } catch (error) {
            console.error('获取社团列表失败:', error)
            throw error
        } finally {
            loading.value = false
        }
    }

    // 根据ID获取社团
    const fetchClubById = async (id) => {
        loading.value = true
        try {
            const response = await clubApi.getClubById(id)
            if (response.code === 200) {
                currentClub.value = response.data
            }
            return response
        } catch (error) {
            console.error('获取社团详情失败:', error)
            throw error
        } finally {
            loading.value = false
        }
    }

    // 搜索社团
    const searchClubs = async (clubName) => {
        loading.value = true
        try {
            const response = await clubApi.searchClubs(clubName)
            return response
        } catch (error) {
            console.error('搜索社团失败:', error)
            throw error
        } finally {
            loading.value = false
        }
    }

    // 创建社团
    const createClub = async (clubData) => {
        loading.value = true
        try {
            const response = await clubApi.createClub(clubData)
            if (response.code === 200) {
                clubs.value.push(response.data)
            }
            return response
        } catch (error) {
            console.error('创建社团失败:', error)
            throw error
        } finally {
            loading.value = false
        }
    }

    // 更新社团
    const updateClub = async (clubData) => {
        loading.value = true
        try {
            const response = await clubApi.updateClub(clubData)
            if (response.code === 200) {
                const index = clubs.value.findIndex(club => club.clubId === clubData.clubId)
                if (index !== -1) {
                    clubs.value[index] = response.data
                }
                if (currentClub.value && currentClub.value.clubId === clubData.clubId) {
                    currentClub.value = response.data
                }
            }
            return response
        } catch (error) {
            console.error('更新社团失败:', error)
            throw error
        } finally {
            loading.value = false
        }
    }

    // 删除社团
    const deleteClub = async (clubId) => {
        loading.value = true
        try {
            const response = await clubApi.deleteClub(clubId)
            if (response.code === 200) {
                const index = clubs.value.findIndex(club => club.clubId === clubId)
                if (index !== -1) {
                    clubs.value.splice(index, 1)
                }
            }
            return response
        } catch (error) {
            console.error('删除社团失败:', error)
            throw error
        } finally {
            loading.value = false
        }
    }

    // 根据类型筛选社团
    const getClubsByType = (type) => {
        return clubs.value.filter(club => club.clubType === type)
    }

    // 重置当前社团
    const resetCurrentClub = () => {
        currentClub.value = null
    }

    return {
        clubs,
        currentClub,
        clubTypes,
        loading,
        sportsClubs,
        artsClubs,
        academicClubs,
        availableClubs,
        fetchAllClubs,
        fetchClubById,
        searchClubs,
        createClub,
        updateClub,
        deleteClub,
        getClubsByType,
        resetCurrentClub
    }
})