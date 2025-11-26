<template>
  <div class="app-layout">
    <!-- 顶部导航栏 -->
    <el-header class="app-header">
      <div class="header-content">
        <div class="header-left">
          <div class="logo" @click="$router.push('/home')">
            <span class="logo-text">社团管理系统</span>
          </div>
          <el-menu
              :default-active="activeMenu"
              mode="horizontal"
              :router="true"
              class="header-menu"
          >
            <el-menu-item index="/home">
              <el-icon><HomeFilled /></el-icon>
              首页
            </el-menu-item>
            <el-menu-item index="/clubs">
              <el-icon><School /></el-icon>
              社团列表
            </el-menu-item>
            <el-menu-item index="/applications">
              <el-icon><Document /></el-icon>
              申请记录
            </el-menu-item>
            <el-menu-item
                v-if="userStore.isLeader && userStore.userInfo?.club"
                index="/management"
            >
              <el-icon><Setting /></el-icon>
              社团管理
            </el-menu-item>
          </el-menu>
        </div>

        <div class="header-right">
          <el-dropdown @command="handleUserCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="userStore.userInfo?.avatar">
                {{ getUserNameFirstChar }}
              </el-avatar>
              <span class="user-name">{{ getUserName }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  个人信息
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>

    <!-- 主要内容区域 -->
    <el-main class="app-main">
      <router-view />
    </el-main>


  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  HomeFilled,
  School,
  Document,
  Setting,
  User,
  SwitchButton,
  ArrowDown
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useAppStore } from '@/stores/app'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const appStore = useAppStore()

const activeMenu = computed(() => route.path)

const getUserName = computed(() => {
  if (userStore.isStudent) {
    return userStore.userInfo?.studentName || '学生用户'
  } else {
    return userStore.userInfo?.leaderName || '负责人'
  }
})

const getUserNameFirstChar = computed(() => {
  return getUserName.value.charAt(0)
})

const handleUserCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          type: 'warning'
        })
        userStore.logout()
        ElMessage.success('已退出登录')
        router.push('/login')
      } catch (error) {
        // 用户取消退出
      }
      break
  }
}
</script>

<style scoped>
.app-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-header {
  background: white;
  border-bottom: 1px solid #e6e6e6;
  padding: 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 40px;
}

.logo {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.logo-text {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
  background: linear-gradient(135deg, #409eff, #67c23a);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.header-menu {
  border-bottom: none;
}

.header-menu .el-menu-item {
  font-size: 16px;
  height: 60px;
  line-height: 60px;
  border-bottom: 2px solid transparent;
}

.header-menu .el-menu-item.is-active {
  border-bottom-color: #409eff;
  color: #409eff;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.user-info:hover {
  background: #f5f7fa;
}

.user-name {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.app-main {
  flex: 1;
  padding: 0;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.global-loading {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 2000;
  display: flex;
  align-items: center;
  justify-content: center;
}

@media (max-width: 768px) {
  .header-content {
    padding: 0 10px;
  }

  .header-left {
    gap: 10px;
  }

  .logo-text {
    font-size: 16px;
  }

  .header-menu .el-menu-item {
    font-size: 14px;
    padding: 0 8px;
  }

  .user-name {
    display: none;
  }
}
</style>