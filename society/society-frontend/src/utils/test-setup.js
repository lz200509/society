import { config } from '@vue/test-utils'
import { vi } from 'vitest'
import { setupTestConfig, mockElementComponents } from './test-utils'

// 设置测试配置
setupTestConfig()

// 模拟Element Plus
config.global.mocks = {
    ...config.global.mocks,
    ...mockElementComponents
}

// 模拟IntersectionObserver
global.IntersectionObserver = vi.fn().mockImplementation(() => ({
    observe: vi.fn(),
    unobserve: vi.fn(),
    disconnect: vi.fn()
}))

// 模拟ResizeObserver
global.ResizeObserver = vi.fn().mockImplementation(() => ({
    observe: vi.fn(),
    unobserve: vi.fn(),
    disconnect: vi.fn()
}))

// 模拟matchMedia
Object.defineProperty(window, 'matchMedia', {
    writable: true,
    value: vi.fn().mockImplementation(query => ({
        matches: false,
        media: query,
        onchange: null,
        addListener: vi.fn(),
        removeListener: vi.fn(),
        addEventListener: vi.fn(),
        removeEventListener: vi.fn(),
        dispatchEvent: vi.fn()
    }))
})