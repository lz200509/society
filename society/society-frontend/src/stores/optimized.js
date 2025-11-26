import { defineStore } from 'pinia'
import { ref, computed, watch } from 'vue'
import { debounce, throttle } from '@/utils/performance'

// 带缓存的状态管理
export const createCachedStore = (name, options) => {
    const {
        initialState = {},
        persist = false,
        cacheTimeout = 5 * 60 * 1000, // 5分钟
        ...storeOptions
    } = options

    return defineStore(name, () => {
        // 状态
        const state = ref({ ...initialState })
        const cache = ref(new Map())
        const lastUpdated = ref({})
        const loadingStates = ref({})

        // 计算属性
        const isDataStale = computed(() => (key) => {
            const lastUpdate = lastUpdated.value[key]
            if (!lastUpdate) return true
            return Date.now() - lastUpdate > cacheTimeout
        })

        const isLoading = computed(() => (key) => {
            return loadingStates.value[key] || false
        })

        // 方法
        const setState = (updates) => {
            Object.assign(state.value, updates)

            if (persist) {
                debouncedPersist()
            }
        }

        const setLoading = (key, loading) => {
            if (loading) {
                loadingStates.value[key] = true
            } else {
                delete loadingStates.value[key]
            }
        }

        const getCachedData = (key) => {
            const cached = cache.value.get(key)
            if (cached && !isDataStale.value(key)) {
                return cached
            }
            return null
        }

        const setCachedData = (key, data) => {
            cache.value.set(key, data)
            lastUpdated.value[key] = Date.now()

            if (persist) {
                debouncedPersist()
            }
        }

        const clearCache = (key) => {
            if (key) {
                cache.value.delete(key)
                delete lastUpdated.value[key]
            } else {
                cache.value.clear()
                lastUpdated.value = {}
            }
        }

        const clearStaleData = () => {
            const now = Date.now()
            cache.value.forEach((value, key) => {
                if (now - lastUpdated.value[key] > cacheTimeout) {
                    cache.value.delete(key)
                    delete lastUpdated.value[key]
                }
            })
        }

        // 持久化
        const persistState = () => {
            if (!persist) return

            try {
                const dataToPersist = {
                    state: state.value,
                    cache: Object.fromEntries(cache.value),
                    lastUpdated: lastUpdated.value
                }
                localStorage.setItem(`store_${name}`, JSON.stringify(dataToPersist))
            } catch (error) {
                console.error('持久化状态失败:', error)
            }
        }

        const loadPersistedState = () => {
            if (!persist) return

            try {
                const persisted = localStorage.getItem(`store_${name}`)
                if (persisted) {
                    const { state: savedState, cache: savedCache, lastUpdated: savedLastUpdated } =
                        JSON.parse(persisted)

                    state.value = { ...initialState, ...savedState }
                    cache.value = new Map(Object.entries(savedCache || {}))
                    lastUpdated.value = savedLastUpdated || {}
                }
            } catch (error) {
                console.error('加载持久化状态失败:', error)
            }
        }

        // 防抖的持久化
        const debouncedPersist = debounce(persistState, 1000)

        // 自动清理过期数据（每小时一次）
        setInterval(clearStaleData, 60 * 60 * 1000)

        // 初始化时加载持久化状态
        if (persist) {
            loadPersistedState()
        }

        return {
            // 状态
            state,
            cache,
            lastUpdated,
            loadingStates,

            // 计算属性
            isDataStale,
            isLoading,

            // 方法
            setState,
            setLoading,
            getCachedData,
            setCachedData,
            clearCache,
            clearStaleData,
            persistState,
            loadPersistedState
        }
    }, storeOptions)
}