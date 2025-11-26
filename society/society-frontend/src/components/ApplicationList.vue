<template>
  <div class="application-list">
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="3" animated />
    </div>

    <div v-else-if="applications.length === 0" class="empty-state">
      <el-empty :description="emptyText" :image-size="60" />
    </div>

    <div v-else class="applications">
      <div
          v-for="application in displayedApplications"
          :key="application.recordId"
          class="application-item"
      >
        <div class="application-header">
          <div class="application-info">
            <div class="applicant-name">
              {{ getApplicantName(application) }}
            </div>
            <div class="application-time">
              {{ formatDate(application.applyTime) }}
            </div>
          </div>
          <div class="application-status">
            <el-tag :type="getStatusTagType(application.auditStatus)" size="small">
              {{ application.auditStatus }}
            </el-tag>
          </div>
        </div>

        <div class="application-details">
          <div class="detail-item">
            <span class="label">申请ID：</span>
            <span class="value">{{ application.recordId }}</span>
          </div>
          <div class="detail-item">
            <span class="label">{{ userStore.isStudent ? '社团' : '学生' }}：</span>
            <span class="value">{{ getTargetName(application) }}</span>
          </div>
        </div>

        <div class="application-actions" v-if="showActions">
          <el-button
              size="small"
              @click="$emit('view', application)"
          >
            查看详情
          </el-button>

          <el-button
              v-if="userStore.isLeader && application.auditStatus === '待审核'"
              type="success"
              size="small"
              :loading="auditingRecordId === application.recordId"
              @click="$emit('approve', application.recordId)"
          >
            通过
          </el-button>

          <el-button
              v-if="userStore.isLeader && application.auditStatus === '待审核'"
              type="danger"
              size="small"
              :loading="auditingRecordId === application.recordId"
              @click="$emit('reject', application.recordId)"
          >
            拒绝
          </el-button>

          <el-button
              v-if="userStore.isStudent && application.auditStatus === '待审核'"
              type="danger"
              size="small"
              :loading="deletingRecordId === application.recordId"
              @click="$emit('delete', application.recordId)"
          >
            撤销
          </el-button>
        </div>
      </div>
    </div>

    <!-- 查看更多 -->
    <div v-if="applications.length > limit && !showAll" class="view-more">
      <el-button type="text" @click="showAll = true">
        查看更多申请 ({{ applications.length - limit }})
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { formatDate } from '@/utils'

const props = defineProps({
  applications: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  limit: {
    type: Number,
    default: 5
  },
  showActions: {
    type: Boolean,
    default: true
  },
  emptyText: {
    type: String,
    default: '暂无申请记录'
  },
  auditingRecordId: {
    type: Number,
    default: null
  },
  deletingRecordId: {
    type: Number,
    default: null
  }
})

const emit = defineEmits(['view', 'approve', 'reject', 'delete'])

const userStore = useUserStore()
const showAll = ref(false)

const displayedApplications = computed(() => {
  if (showAll.value) {
    return props.applications
  }
  return props.applications.slice(0, props.limit)
})

const getApplicantName = (application) => {
  if (userStore.isStudent) {
    return application.student?.studentName || '未知学生'
  } else {
    return application.student?.studentName || '未知学生'
  }
}

const getTargetName = (application) => {
  if (userStore.isStudent) {
    return application.club?.clubName || '未知社团'
  } else {
    return application.student?.studentName || '未知学生'
  }
}

const getStatusTagType = (status) => {
  const statusMap = {
    '待审核': 'warning',
    '已通过': 'success',
    '已拒绝': 'danger'
  }
  return statusMap[status] || 'info'
}
</script>

<style scoped>
.application-list {
  width: 100%;
}

.loading-container {
  padding: 20px 0;
}

.empty-state {
  padding: 40px 0;
}

.application-item {
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  padding: 16px;
  margin-bottom: 12px;
  transition: all 0.3s ease;
}

.application-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.application-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.application-info {
  flex: 1;
}

.applicant-name {
  font-weight: 500;
  font-size: 16px;
  color: #303133;
  margin-bottom: 4px;
}

.application-time {
  font-size: 12px;
  color: #909399;
}

.application-details {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.detail-item .label {
  font-size: 12px;
  color: #909399;
}

.detail-item .value {
  font-size: 14px;
  color: #606266;
}

.application-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.view-more {
  text-align: center;
  padding: 12px 0;
  border-top: 1px solid #f0f2f5;
}

@media (max-width: 768px) {
  .application-header {
    flex-direction: column;
    gap: 8px;
  }

  .application-details {
    flex-direction: column;
    gap: 8px;
  }

  .application-actions {
    flex-direction: column;
  }

  .application-actions .el-button {
    width: 100%;
  }
}
</style>