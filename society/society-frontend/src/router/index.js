import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/Login.vue'),
        meta: { requiresAuth: false, title: '登录 - 社团管理系统' }
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('@/views/Register.vue'),
        meta: { requiresAuth: false, title: '注册 - 社团管理系统' }
    },
    {
        path: '/',
        name: 'Layout',
        component: () => import('@/layout/AppLayout.vue'),
        meta: { requiresAuth: true },
        redirect: '/home',
        children: [
            {
                path: 'home',
                name: 'Home',
                component: () => import('@/views/Home.vue'),
                meta: { title: '首页 - 社团管理系统', requiresAuth: true }
            },
            {
                path: 'clubs',
                name: 'Clubs',
                component: () => import('@/views/Clubs.vue'),
                meta: { title: '社团列表 - 社团管理系统', requiresAuth: true }
            },
            {
                path: 'club/:id',
                name: 'ClubDetail',
                component: () => import('@/views/ClubDetail.vue'),
                meta: { title: '社团详情 - 社团管理系统', requiresAuth: true }
            },
            {
                path: 'applications',
                name: 'Applications',
                component: () => import('@/views/Applications.vue'),
                meta: { title: '申请记录 - 社团管理系统', requiresAuth: true }
            },
            {
                path: 'management',
                name: 'Management',
                component: () => import('@/views/Management.vue'),
                meta: { title: '社团管理 - 社团管理系统', requiresAuth: true, requiresLeader: true }
            },
            {
                path: 'profile',
                name: 'Profile',
                component: () => import('@/views/Profile.vue'),
                meta: { title: '个人信息 - 社团管理系统', requiresAuth: true }
            }
        ]
    },
    {
        path: '/:pathMatch(.*)*',
        name: 'NotFound',
        component: () => import('@/views/NotFound.vue'),
        meta: { title: '页面不存在 - 社团管理系统' }
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
    const userStore = useUserStore()

    // 设置页面标题
    if (to.meta.title) {
        document.title = to.meta.title
    }

    // 检查是否需要认证
    if (to.meta.requiresAuth) {
        if (!userStore.isLoggedIn) {
            next('/login')
            return
        }

        // 检查是否需要负责人权限
        if (to.meta.requiresLeader && !userStore.isLeader) {
            ElMessage.warning('此功能仅对社团负责人开放')
            next('/home')
            return
        }
    }

    // 如果已登录，访问登录页则跳转到首页
    if ((to.name === 'Login' || to.name === 'Register') && userStore.isLoggedIn) {
        next('/home')
        return
    }

    next()
})

export default router