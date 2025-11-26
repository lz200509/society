import { defineConfig } from 'vitest/config'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
    plugins: [vue()],
    test: {
        globals: true,
        environment: 'jsdom',
        coverage: {
            reporter: ['text', 'json', 'html'],
            exclude: [
                'node_modules/',
                'src/main.js',
                '**/*.config.js',
                '**/types/**'
            ]
        },
        setupFiles: ['./src/utils/test-setup.js']
    },
    resolve: {
        alias: {
            '@': resolve(__dirname, './src')
        }
    }
})