<template>
  <div class="application-detail">
    <div class="detail-section">
      <h4>基本信息</h4>
      <div class="detail-grid">
        <div class="detail-item">
          <span class="label">申请ID：</span>
          <span class="value">{{ application.recordId }}</span>
        </div>
        <div class="detail-item">
          <span class="label">申请时间：</span>
          <span class="value">{{ formatDate(application.applyTime) }}</span>
        </div>
        <div class="detail-item">
          <span class="label">审核状态：</span>
          <el-tag :type="getStatusTagType(application.auditStatus)" effect="light">
            {{ application.auditStatus }}
          </el-tag>
        </div>
      </div>
    </div>

    <div class="detail-section">
      <h4>学生信息</h4>
      <div class="info-card">
        <div class="info-header">
          <div class="avatar">
            <el-avatar :size="50" :src="application.student?.avatar">
              {{ application.student?.studentName?.charAt(0) || 'S' }}
            </el-avatar>
          </div>
          <div class="info-content">
            <h5>{{ application.student?.studentName || '未知学生' }}</h5>
            <p class="student-num">{{ application.student?.studentNum || '未知学号' }}</p>
            <p class="student-phone" v-if="application.student?.phone">
              <el-icon><Phone /></el-icon>
              {{ application.student.phone }}
            </p>
          </div>
        </div>
        <div class="info-stats" v-if="userStore.isLeader">
          <div class="stat">
            <span class="stat-label">总申请数</span>
            <span class="stat-value">{{ studentStats.totalApplications || 0 }}</span>
          </div>
          <div class="stat">
            <span class="stat-label">已加入</span>
            <span class="stat-value">{{ studentStats.joinedClubs || 0 }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="detail-section">
      <h4>社团信息</h4>
      <div class="info-card">
        <div class="info-header">
          <div class="avatar">
            <div
                v-if="application.club?.logoUrl"
                class="club-logo"
                :style="{ backgroundImage: `url(${application.club.logoUrl})` }"
            ></div>
            <div v-else class="club-logo-placeholder">
              {{ application.club?.clubName?.charAt(0) || 'C' }}
            </div>
          </div>
          <div class="info-content">
            <h5>{{ application.club?.clubName || '未知社团' }}</h5>
            <p class="club-type">
              <el-tag :type="getClubTypeTag(application.club?.clubType)" size="small">
                {{ application.club?.clubType || '未知类型' }}
              </el-tag>
            </p>
            <p class="club-quota">
              名额：{{ application.club?.remainingQuota || 0 }}/{{ application.club?.totalQuota || 0 }}
            </p>
          </div>
        </div>
        <div class="club-intro" v-if="application.club?.clubIntro">
          {{ application.club.clubIntro }}
        </div>
      </div>
    </div>

    <div class="detail-section" v-if="userStore.isLeader && application.auditStatus === '待审核'">
      <h4>审核操作</h4>
      <div class="action-buttons">
        <el-button
            type="success"
            size="large"
            :loading="auditing"
            @click="$emit('approve', application.recordId)"
        >
          <el-icon><Check /></el-icon>
          通过申请
        </el-button>
        <el-button
            type="danger"
            size="large"
            :loading="auditing"
            @click="$emit('reject', application.recordId)"
        >
          <el-icon><Close /></el-icon>
          拒绝申请
        </el-button>
      </div>
    </div>

    <div class="detail-section" v-if="userStore.isStudent && application.auditStatus === '待审核'">
      <h4>操作</h4>
      <div class="action-buttons">
        <el-button
            type="danger"
            size="large"
            :loading="deleting"
            @click="$emit('delete', application.recordId)"
        >
          <el-icon><Delete /></el-icon>
          撤销申请
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Phone, Check, Close, Delete } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { applicationApi } from '@/api'
import { formatDate } from '@/utils'

const props = defineProps({
  application: {
    type: Object,
    required: true
  },
  auditing: {
    type: Boolean,
    default: false
  },
  deleting: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['approve', 'reject', 'delete'])

const userStore = useUserStore()
const studentStats = ref({
  totalApplications: 0,
  joinedClubs: 0
})

const getStatusTagType = (status) => {
  const statusMap = {
    '待审核': 'warning',
    '已通过': 'success',
    '已拒绝': 'danger'
  }
  return statusMap[status] || 'info'
}

const getClubTypeTag = (type) => {
  const typeMap = {
    '体育': 'success',
    '文艺': 'warning',
    '学术': 'info'
  }
  return typeMap[type] || 'info'
}

const loadStudentStats = async () => {
  if (!userStore.isLeader || !props.application.student?.studentId) return

  try {
    const response = await applicationApi.countApplicationsByStudent(
        props.application.student.studentId
    )
    if (response.code === 200) {
      studentStats.value.totalApplications = response.data
    }

    // 这里应该调用获取学生已加入社团数量的API
    // 暂时使用模拟数据
    studentStats.value.joinedClubs = Math.floor(Math.random() * 3)
  } catch (error) {
    console.error('加载学生统计失败:', error)
  }
}

onMounted(() => {
  if (userStore.isLeader) {
    loadStudentStats()
  }
})
</script>

<style scoped>
.application-detail {
  padding: 0;
}

.detail-section {
  margin-bottom: 24px;
}

.detail-section h4 {
  margin: 0 0 12px 0;
  font-size: 16px;
  color: #303133;
  font-weight: 600;
  padding-bottom: 8px;
  border-bottom: 1px solid #ebeef5;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.detail-item .label {
  font-size: 14px;
  color: #606266;
  min-width: 80px;
}

.detail-item .value {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.info-card {
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  padding: 16px;
  background: #fafafa;
}

.info-header {
  display: flex;
  align-items: flex-start;
  margin-bottom: 12px;
}

.avatar {
  margin-right: 16px;
}

.club-logo {
  width: 50px;
  height: 50px;
  border-radius: 6px;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.club-logo-placeholder {
  width: 50px;
  height: 50px;
  border-radius: 6px;
  background: #409eff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: bold;
}

.info-content h5 {
  margin: 0 0 4px 0;
  font-size: 16px;
  color: #303133;
}

.student-num,
.club-type {
  margin: 0 0 4px 0;
  font-size: 14px;
  color: #606266;
}

.student-phone {
  margin: 0;
  font-size: 14px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 4px;
}

.club-quota {
  margin: 0;
  font-size: 14px;
  color: #606266;
}

.info-stats {
  display: flex;
  gap: 16px;
  border-top: 1px solid #e4e7ed;
  padding-top: 12px;
}

.stat {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.stat-label {
  font-size: 12px;
  color: #909399;
}

.stat-value {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.club-intro {
  margin: 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
  padding-top: 12px;
  border-top: 1px solid #e4e7ed;
}

.action-buttons {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

@media (max-width: 768px) {
  .detail-grid {
    grid-template-columns: 1fr;
  }

  .info-header {
    flex-direction: column;
    text-align: center;
  }

  .avatar {
    margin-right: 0;
    margin-bottom: 12px;
    align-self: center;
  }

  .action-buttons {
    flex-direction: column;
  }

  .action-buttons .el-button {
    width: 100%;
  }
}
</style>