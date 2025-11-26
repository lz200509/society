import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'

const app = createApp(App)
const pinia = createPinia()

// 注册Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

// 配置 Element Plus 以抑制弃用警告
app.use(ElementPlus, {
    // 禁用所有弃用警告
    deprecated: {
        disableDeprecationWarning: true
    }
})

app.use(pinia)
app.use(router)

app.mount('#app')

// 全局错误处理
app.config.errorHandler = (err, vm, info) => {
    console.error('Vue 错误:', err)
}

// 抑制 Element Plus 控制台警告
const originalWarn = console.warn
console.warn = function(...args) {
    if (typeof args[0] === 'string' &&
        (args[0].includes('ElementPlusError') ||
            args[0].includes('deprecated') ||
            args[0].includes('deprecation'))) {
        return // 抑制 Element Plus 弃用警告
    }
    originalWarn.apply(console, args)
}