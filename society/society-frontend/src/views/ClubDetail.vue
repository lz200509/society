<template>
  <div class="club-detail-container" v-loading="loading">
    <div v-if="club" class="club-detail">
      <!-- 头部信息 -->
      <div class="club-header">
        <div class="club-basic-info">
          <div class="club-logo">
            <img v-if="club.logoUrl" :src="club.logoUrl" :alt="club.clubName" />
            <div v-else class="club-logo-placeholder">
              {{ club.clubName.charAt(0) }}
            </div>
          </div>
          <div class="club-info">
            <h1 class="club-name">{{ club.clubName }}</h1>
            <div class="club-meta">
              <el-tag :type="getClubTypeTag(club.clubType)" size="large">
                {{ club.clubType }}
              </el-tag>
              <div class="quota-info">
                <span class="quota-text">
                  名额: {{ club.remainingQuota }}/{{ club.totalQuota }}
                </span>
                <el-progress
                    :percentage="getQuotaPercentage(club)"
                    :color="getQuotaColor(club)"
                    :show-text="false"
                    style="width: 120px; margin-left: 12px;"
                />
              </div>
            </div>
            <div class="club-actions">
              <el-button
                  v-if="userStore.isStudent && club.remainingQuota > 0"
                  type="primary"
                  size="large"
                  :loading="applying"
                  @click="handleApply"
              >
                <el-icon><Plus /></el-icon>
                申请加入
              </el-button>
              <el-button
                  v-else-if="userStore.isStudent"
                  type="info"
                  size="large"
                  disabled
              >
                已满员
              </el-button>
              <el-button
                  v-if="userStore.isLeader && userStore.userInfo?.club?.clubId === club.clubId"
                  type="warning"
                  size="large"
                  @click="$router.push('/management')"
              >
                <el-icon><Setting /></el-icon>
                管理社团
              </el-button>
              <el-button
                  size="large"
                  @click="$router.back()"
              >
                <el-icon><ArrowLeft /></el-icon>
                返回
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 内容区域 -->
      <div class="club-content">
        <el-row :gutter="20">
          <!-- 左侧：社团介绍 -->
          <el-col :xs="24" :lg="16">
            <div class="content-card">
              <div class="card-header">
                <h3>社团介绍</h3>
              </div>
              <div class="card-body">
                <div v-if="club.clubIntro" class="club-intro">
                  {{ club.clubIntro }}
                </div>
                <div v-else class="empty-intro">
                  <el-empty description="暂无社团介绍" :image-size="80" />
                </div>
              </div>
            </div>

            <!-- 申请记录（负责人可见） -->
            <div
                v-if="userStore.isLeader && userStore.userInfo?.club?.clubId === club.clubId"
                class="content-card"
            >
              <div class="card-header">
                <h3>最近申请</h3>
                <el-button type="primary" text @click="$router.push('/applications')">
                  查看全部
                  <el-icon><ArrowRight /></el-icon>
                </el-button>
              </div>
              <div class="card-body">
                <ApplicationList :club-id="club.clubId" :limit="5" />
              </div>
            </div>
          </el-col>

          <!-- 右侧：统计信息 -->
          <el-col :xs="24" :lg="8">
            <div class="content-card">
              <div class="card-header">
                <h3>社团统计</h3>
              </div>
              <div class="card-body">
                <div class="stats-list">
                  <div class="stat-item">
                    <div class="stat-label">总成员数</div>
                    <div class="stat-value">{{ club.totalQuota - club.remainingQuota }}</div>
                  </div>
                  <div class="stat-item">
                    <div class="stat-label">剩余名额</div>
                    <div class="stat-value">{{ club.remainingQuota }}</div>
                  </div>
                  <div class="stat-item">
                    <div class="stat-label">满员率</div>
                    <div class="stat-value">{{ getFullPercentage(club) }}%</div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 负责人信息 -->
            <div class="content-card">
              <div class="card-header">
                <h3>负责人信息</h3>
              </div>
              <div class="card-body">
                <div v-if="leader" class="leader-info">
                  <div class="leader-avatar">
                    <el-avatar :size="50" :src="leader.avatar">
                      {{ leader.leaderName.charAt(0) }}
                    </el-avatar>
                  </div>
                  <div class="leader-details">
                    <h4>{{ leader.leaderName }}</h4>
                    <p class="leader-num">{{ leader.leaderNum }}</p>
                    <p class="leader-phone" v-if="leader.phone">
                      <el-icon><Phone /></el-icon>
                      {{ leader.phone }}
                    </p>
                  </div>
                </div>
                <div v-else class="empty-leader">
                  <el-empty description="暂无负责人信息" :image-size="60" />
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else-if="!loading" class="empty-state">
      <el-empty description="社团不存在或已被删除">
        <el-button type="primary" @click="$router.push('/clubs')">
          返回社团列表
        </el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Setting, ArrowLeft, ArrowRight, Phone } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useClubStore } from '@/stores/club'
import { useApplicationStore } from '@/stores/application'
import ApplicationList from '@/components/ApplicationList.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const clubStore = useClubStore()
const applicationStore = useApplicationStore()

const loading = ref(false)
const applying = ref(false)
const leader = ref(null)

const clubId = computed(() => parseInt(route.params.id))
const club = computed(() => clubStore.currentClub)

const getClubTypeTag = (type) => {
  const typeMap = {
    '体育': 'success',
    '文艺': 'warning',
    '学术': 'info'
  }
  return typeMap[type] || 'info'
}

const getQuotaPercentage = (club) => {
  return (club.remainingQuota / club.totalQuota) * 100
}

const getQuotaColor = (club) => {
  const percentage = getQuotaPercentage(club)
  if (percentage > 50) return '#67c23a'
  if (percentage > 20) return '#e6a23c'
  return '#f56c6c'
}

const getFullPercentage = (club) => {
  return Math.round(((club.totalQuota - club.remainingQuota) / club.totalQuota) * 100)
}

const handleApply = async () => {
  if (!userStore.isStudent) {
    ElMessage.warning('请以学生身份登录')
    return
  }

  try {
    applying.value = true

    // 检查是否已经申请过
    const exists = await applicationStore.checkApplicationExists(
        userStore.userId,
        club.value.clubId
    )

    if (exists) {
      ElMessage.warning('您已经申请过该社团')
      return
    }

    await ElMessageBox.confirm(
        `确定要申请加入 ${club.value.clubName} 吗？`,
        '确认申请',
        {
          type: 'info'
        }
    )

    // 修改为新的 DTO 格式
    const applicationData = {
      studentId: userStore.userId,
      clubId: club.value.clubId,
      auditStatus: '待审核'
    }

    console.log('提交申请数据:', applicationData)

    const response = await applicationStore.submitApplication(applicationData)

    if (response.code === 200) {
      ElMessage.success('申请提交成功，等待审核')
      hasApplied.value = true
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('申请失败:', error)
      console.error('错误详情:', error.response?.data)
      ElMessage.error(error.message || '申请失败')
    }
  } finally {
    applying.value = false
  }
}

const loadClubData = async () => {
  loading.value = true
  try {
    await clubStore.fetchClubById(clubId.value)

    // 如果社团存在，加载负责人信息
    if (club.value) {
      // 这里可以添加加载负责人信息的逻辑
      // 暂时使用模拟数据
      leader.value = {
        leaderName: '负责人',
        leaderNum: 'L0001',
        phone: '13800138000'
      }
    }
  } catch (error) {
    console.error('加载社团详情失败:', error)
    ElMessage.error('加载社团详情失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadClubData()
})
</script>

<style scoped>
.club-detail-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  min-height: calc(100vh - 120px);
}

.club-header {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  padding: 30px;
  margin-bottom: 24px;
}

.club-basic-info {
  display: flex;
  align-items: flex-start;
}

.club-logo {
  width: 100px;
  height: 100px;
  border-radius: 12px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 24px;
  overflow: hidden;
  flex-shrink: 0;
}

.club-logo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.club-logo-placeholder {
  font-size: 36px;
  font-weight: bold;
  color: #409eff;
}

.club-info {
  flex: 1;
}

.club-name {
  font-size: 28px;
  color: #303133;
  margin: 0 0 16px 0;
  font-weight: 600;
}

.club-meta {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.quota-info {
  display: flex;
  align-items: center;
}

.quota-text {
  font-size: 14px;
  color: #606266;
}

.club-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.content-card {
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
  font-weight: 600;
}

.card-body {
  padding: 20px;
}

.club-intro {
  font-size: 16px;
  line-height: 1.7;
  color: #606266;
  white-space: pre-wrap;
}

.empty-intro {
  padding: 40px 0;
}

.stats-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f2f5;
}

.stat-item:last-child {
  border-bottom: none;
}

.stat-label {
  font-size: 14px;
  color: #606266;
}

.stat-value {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.leader-info {
  display: flex;
  align-items: center;
}

.leader-avatar {
  margin-right: 16px;
}

.leader-details h4 {
  margin: 0 0 4px 0;
  font-size: 16px;
  color: #303133;
}

.leader-num {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #606266;
}

.leader-phone {
  margin: 0;
  font-size: 14px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 4px;
}

.empty-leader {
  padding: 20px 0;
}

.empty-state {
  padding: 100px 0;
}

@media (max-width: 768px) {
  .club-detail-container {
    padding: 10px;
  }

  .club-header {
    padding: 20px;
  }

  .club-basic-info {
    flex-direction: column;
    text-align: center;
  }

  .club-logo {
    margin-right: 0;
    margin-bottom: 16px;
    align-self: center;
  }

  .club-meta {
    justify-content: center;
  }

  .club-actions {
    justify-content: center;
  }

  .club-actions .el-button {
    flex: 1;
    min-width: 120px;
  }
}
</style>