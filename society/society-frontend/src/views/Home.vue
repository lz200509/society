<template>
  <div class="home-container">
    <!-- 顶部统计卡片 -->
    <div class="stats-cards">
      <el-row :gutter="20">
        <el-col :xs="12" :sm="8" :lg="6">
          <div class="stat-card">
            <div class="stat-icon" style="background: #409eff;">
              <el-icon><School /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalClubs || 0 }}</div>
              <div class="stat-label">总社团数</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="8" :lg="6">
          <div class="stat-card">
            <div class="stat-icon" style="background: #67c23a;">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalStudents || 0 }}</div>
              <div class="stat-label">总学生数</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="8" :lg="6">
          <div class="stat-card">
            <div class="stat-icon" style="background: #e6a23c;">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalApplications || 0 }}</div>
              <div class="stat-label">总申请数</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="8" :lg="6">
          <div class="stat-card">
            <div class="stat-icon" style="background: #f56c6c;">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingApplications || 0 }}</div>
              <div class="stat-label">待审核申请</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <el-row :gutter="20">
        <!-- 左侧：热门社团 -->
        <el-col :xs="24" :lg="16">
          <div class="card">
            <div class="card-header">
              <h3>热门社团</h3>
              <el-button type="primary" text @click="$router.push('/clubs')">
                查看全部
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
            <div class="card-body">
              <div v-if="loading" class="loading-container">
                <el-skeleton :rows="3" animated />
              </div>
              <div v-else class="clubs-grid">
                <div
                    v-for="club in popularClubs"
                    :key="club.clubId"
                    class="club-card"
                    @click="goToClubDetail(club.clubId)"
                >
                  <div class="club-logo">
                    <img v-if="club.logoUrl" :src="club.logoUrl" :alt="club.clubName" />
                    <div v-else class="club-logo-placeholder">
                      {{ club.clubName.charAt(0) }}
                    </div>
                  </div>
                  <div class="club-info">
                    <h4 class="club-name">{{ club.clubName }}</h4>
                    <el-tag :type="getClubTypeTag(club.clubType)" size="small">
                      {{ club.clubType }}
                    </el-tag>
                    <div class="club-quota">
                      <span>剩余名额: {{ club.remainingQuota }}/{{ club.totalQuota }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-col>

        <!-- 右侧：个人信息和快捷操作 -->
        <el-col :xs="24" :lg="8">
          <!-- 个人信息卡片 -->
          <div class="card">
            <div class="card-header">
              <h3>个人信息</h3>
            </div>
            <div class="card-body">
              <div class="user-info">
                <div class="user-avatar">
                  <el-avatar :size="60" :src="userStore.userInfo?.avatar">
                    {{ getUserNameFirstChar }}
                  </el-avatar>
                </div>
                <div class="user-details">
                  <h4>{{ getUserName }}</h4>
                  <p>{{ getUserTypeText }}</p>
                  <p class="user-id">{{ getUserNumber }}</p>
                </div>
              </div>
              <div class="user-actions">
                <el-button type="primary" @click="$router.push('/profile')">
                  编辑资料
                </el-button>
                <el-button @click="handleLogout">退出登录</el-button>
              </div>
            </div>
          </div>

          <!-- 快捷操作 -->
          <div class="card quick-actions">
            <div class="card-header">
              <h3>快捷操作</h3>
            </div>
            <div class="card-body">
              <div class="action-buttons">
                <el-button
                    v-if="userStore.isStudent"
                    type="primary"
                    @click="$router.push('/clubs')"
                >
                  <el-icon><Plus /></el-icon>
                  申请加入社团
                </el-button>
                <el-button
                    v-if="userStore.isLeader"
                    type="success"
                    @click="$router.push('/management')"
                >
                  <el-icon><Setting /></el-icon>
                  管理我的社团
                </el-button>
                <el-button @click="$router.push('/applications')">
                  <el-icon><Document /></el-icon>
                  查看申请记录
                </el-button>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  School,
  User,
  Document,
  Clock,
  ArrowRight,
  Plus,
  Setting
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useClubStore } from '@/stores/club'
import { useApplicationStore } from '@/stores/application'

const router = useRouter()
const userStore = useUserStore()
const clubStore = useClubStore()
const applicationStore = useApplicationStore()

const loading = ref(false)
const stats = reactive({
  totalClubs: 0,
  totalStudents: 0,
  totalApplications: 0,
  pendingApplications: 0
})

// 计算属性
const popularClubs = computed(() => {
  return clubStore.clubs.slice(0, 6) // 取前6个社团作为热门社团
})

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

const getUserTypeText = computed(() => {
  return userStore.isStudent ? '学生' : '社团负责人'
})

const getUserNumber = computed(() => {
  if (userStore.isStudent) {
    return userStore.userInfo?.studentNum || ''
  } else {
    return userStore.userInfo?.leaderNum || ''
  }
})

// 方法
const getClubTypeTag = (type) => {
  const typeMap = {
    '体育': 'success',
    '文艺': 'warning',
    '学术': 'info'
  }
  return typeMap[type] || 'info'
}

const goToClubDetail = (clubId) => {
  router.push(`/club/${clubId}`)
}

const handleLogout = async () => {
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
}

const loadData = async () => {
  loading.value = true
  try {
    // 加载社团数据
    await clubStore.fetchAllClubs()
    stats.totalClubs = clubStore.clubs.length

    // 如果是学生，加载申请数据
    if (userStore.isStudent) {
      await applicationStore.fetchApplicationsByStudent(userStore.userId)
      stats.totalApplications = applicationStore.applications.length
      stats.pendingApplications = applicationStore.pendingApplications.length
    }

    // 如果是负责人，加载负责的社团申请数据
    if (userStore.isLeader && userStore.userInfo?.club) {
      await applicationStore.fetchApplicationsByClub(userStore.userInfo.club.clubId)
      stats.totalApplications = applicationStore.applications.length
      stats.pendingApplications = applicationStore.pendingApplications.length
    }

  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.home-container {
  padding: 20px;
}

.stats-cards {
  margin-bottom: 24px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 60px;
  height: 60px;
  border-radius: 8px;
  color: white;
  font-size: 24px;
  margin-right: 16px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #ebeef5;
}

.card-header h3 {
  margin: 0;
  font-size: 16px;
  color: #303133;
}

.card-body {
  padding: 20px;
}

.clubs-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.club-card {
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.club-card:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
  transform: translateY(-2px);
}

.club-logo {
  width: 60px;
  height: 60px;
  border-radius: 6px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 12px;
  overflow: hidden;
}

.club-logo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.club-logo-placeholder {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
}

.club-info {
  text-align: center;
}

.club-name {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #303133;
}

.club-quota {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}

.user-info {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.user-details {
  margin-left: 16px;
  flex: 1;
}

.user-details h4 {
  margin: 0 0 4px 0;
  font-size: 16px;
  color: #303133;
}

.user-details p {
  margin: 0 0 4px 0;
  font-size: 14px;
  color: #606266;
}

.user-id {
  font-size: 12px;
  color: #909399;
}

.user-actions {
  display: flex;
  gap: 8px;
}

.quick-actions .action-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.quick-actions .el-button {
  width: 100%;
  justify-content: flex-start;
}

.loading-container {
  padding: 20px 0;
}

@media (max-width: 768px) {
  .home-container {
    padding: 10px;
  }

  .clubs-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .user-info {
    flex-direction: column;
    text-align: center;
  }

  .user-details {
    margin-left: 0;
    margin-top: 12px;
  }
}
</style>