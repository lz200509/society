<template>
  <div class="management-container">
    <div class="management-header">
      <h2>社团管理</h2>
      <p>管理您的社团信息和申请记录</p>
    </div>

    <div v-if="!userStore.isLeader" class="access-denied">
      <el-result
          icon="warning"
          title="访问受限"
          sub-title="此功能仅对社团负责人开放"
      >
        <template #extra>
          <el-button type="primary" @click="$router.push('/home')">
            返回首页
          </el-button>
        </template>
      </el-result>
    </div>

    <div v-else-if="!club" class="no-club">
      <el-result
          icon="info"
          title="暂无管理的社团"
          sub-title="您当前没有负责的社团"
      >
        <template #extra>
          <el-button type="primary" @click="$router.push('/clubs')">
            查看社团列表
          </el-button>
        </template>
      </el-result>
    </div>

    <div v-else class="management-content">
      <!-- 社团基本信息 -->
      <div class="club-info-section">
        <el-card>
          <template #header>
            <div class="card-header">
              <h3>社团基本信息</h3>
              <el-button type="primary" @click="showEditDialog = true">
                <el-icon><Edit /></el-icon>
                编辑信息
              </el-button>
            </div>
          </template>

          <div class="club-basic-info">
            <div class="info-row">
              <div class="info-item">
                <label>社团名称：</label>
                <span>{{ club.clubName }}</span>
              </div>
              <div class="info-item">
                <label>社团类型：</label>
                <el-tag :type="getClubTypeTag(club.clubType)">
                  {{ club.clubType }}
                </el-tag>
              </div>
            </div>
            <div class="info-row">
              <div class="info-item">
                <label>总名额：</label>
                <span>{{ club.totalQuota }}</span>
              </div>
              <div class="info-item">
                <label>剩余名额：</label>
                <span :class="{ 'text-danger': club.remainingQuota === 0 }">
                  {{ club.remainingQuota }}
                </span>
              </div>
              <div class="info-item">
                <label>满员率：</label>
                <span>{{ getFullPercentage(club) }}%</span>
              </div>
            </div>
            <div class="info-row">
              <div class="info-item full-width">
                <label>社团介绍：</label>
                <p class="club-intro">{{ club.clubIntro || '暂无介绍' }}</p>
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 统计信息 -->
      <div class="stats-section">
        <el-row :gutter="20">
          <el-col :xs="12" :sm="6">
            <div class="stat-card">
              <div class="stat-icon total-applications">
                <el-icon><Document /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.totalApplications || 0 }}</div>
                <div class="stat-label">总申请数</div>
              </div>
            </div>
          </el-col>
          <el-col :xs="12" :sm="6">
            <div class="stat-card">
              <div class="stat-icon pending-applications">
                <el-icon><Clock /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.pendingApplications || 0 }}</div>
                <div class="stat-label">待审核</div>
              </div>
            </div>
          </el-col>
          <el-col :xs="12" :sm="6">
            <div class="stat-card">
              <div class="stat-icon approved-applications">
                <el-icon><Check /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.approvedApplications || 0 }}</div>
                <div class="stat-label">已通过</div>
              </div>
            </div>
          </el-col>
          <el-col :xs="12" :sm="6">
            <div class="stat-card">
              <div class="stat-icon rejected-applications">
                <el-icon><Close /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.rejectedApplications || 0 }}</div>
                <div class="stat-label">已拒绝</div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 申请管理 -->
      <div class="applications-section">
        <el-card>
          <template #header>
            <div class="card-header">
              <h3>申请管理</h3>
              <div class="header-actions">
                <el-button
                    type="primary"
                    :icon="Refresh"
                    @click="loadApplications"
                    :loading="loading"
                >
                  刷新
                </el-button>
                <el-button
                    type="success"
                    :icon="Download"
                    @click="exportApplications"
                >
                  导出
                </el-button>
              </div>
            </div>
          </template>

          <div class="applications-filter">
            <el-radio-group v-model="applicationFilter" @change="handleFilterChange">
              <el-radio-button label="all">全部</el-radio-button>
              <el-radio-button label="pending">待审核</el-radio-button>
              <el-radio-button label="approved">已通过</el-radio-button>
              <el-radio-button label="rejected">已拒绝</el-radio-button>
            </el-radio-group>
          </div>

          <div class="applications-table">
            <el-table
                :data="filteredApplications"
                style="width: 100%"
                empty-text="暂无申请记录"
                v-loading="loading"
            >
              <el-table-column prop="recordId" label="申请ID" width="80" />

              <el-table-column label="学生信息" min-width="150">
                <template #default="{ row }">
                  <div class="student-info">
                    <div class="student-name">{{ row.student.studentName }}</div>
                    <div class="student-num">{{ row.student.studentNum }}</div>
                    <div class="student-phone" v-if="row.student.phone">
                      {{ row.student.phone }}
                    </div>
                  </div>
                </template>
              </el-table-column>

              <el-table-column prop="applyTime" label="申请时间" width="180">
                <template #default="{ row }">
                  {{ formatDate(row.applyTime) }}
                </template>
              </el-table-column>

              <el-table-column prop="auditStatus" label="状态" width="120">
                <template #default="{ row }">
                  <el-tag :type="getStatusTagType(row.auditStatus)" effect="light">
                    {{ row.auditStatus }}
                  </el-tag>
                </template>
              </el-table-column>

              <el-table-column label="操作" width="200" fixed="right">
                <template #default="{ row }">
                  <div class="action-buttons">
                    <el-button
                        size="small"
                        @click="viewApplicationDetail(row)"
                    >
                      详情
                    </el-button>

                    <el-button
                        v-if="row.auditStatus === '待审核'"
                        type="success"
                        size="small"
                        :loading="auditingRecordId === row.recordId"
                        @click="handleAudit(row.recordId, '已通过')"
                    >
                      通过
                    </el-button>

                    <el-button
                        v-if="row.auditStatus === '待审核'"
                        type="danger"
                        size="small"
                        :loading="auditingRecordId === row.recordId"
                        @click="handleAudit(row.recordId, '已拒绝')"
                    >
                      拒绝
                    </el-button>

                    <el-button
                        v-if="row.auditStatus !== '待审核'"
                        type="warning"
                        size="small"
                        @click="handleResetAudit(row.recordId)"
                    >
                      重置
                    </el-button>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <!-- 分页 -->
          <div v-if="filteredApplications.length > 0" class="pagination-container">
            <el-pagination
                v-model:current-page="currentPage"
                v-model:page-size="pageSize"
                :total="filteredApplications.length"
                :page-sizes="[10, 20, 50, 100]"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
            />
          </div>
        </el-card>
      </div>

      <!-- 危险操作 -->
      <div class="danger-section">
        <el-card>
          <template #header>
            <h3>危险操作</h3>
          </template>
          <div class="danger-actions">
            <el-button
                type="danger"
                :icon="Delete"
                @click="handleDeleteClub"
                :loading="deleting"
            >
              删除社团
            </el-button>
            <p class="danger-tip">
              注意：删除社团将同时删除所有相关的申请记录，此操作不可恢复！
            </p>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 编辑社团信息对话框 -->
    <el-dialog
        v-model="showEditDialog"
        title="编辑社团信息"
        width="500px"
        :before-close="handleEditDialogClose"
    >
      <ClubForm
          v-if="showEditDialog"
          :club="editClubData"
          @submit="handleUpdateClub"
          @cancel="showEditDialog = false"
      />
    </el-dialog>

    <!-- 申请详情对话框 -->
    <el-dialog
        v-model="showDetailDialog"
        :title="`申请详情 - ${selectedApplication ? `ID: ${selectedApplication.recordId}` : ''}`"
        width="600px"
    >
      <ApplicationDetail
          v-if="selectedApplication"
          :application="selectedApplication"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Edit,
  Document,
  Clock,
  Check,
  Close,
  Refresh,
  Download,
  Delete
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useClubStore } from '@/stores/club'
import { useApplicationStore } from '@/stores/application'
import ClubForm from '@/components/ClubForm.vue'
import ApplicationDetail from '@/components/ApplicationDetail.vue'
import { formatDate } from '@/utils'

const router = useRouter()
const userStore = useUserStore()
const clubStore = useClubStore()
const applicationStore = useApplicationStore()

const loading = ref(false)
const deleting = ref(false)
const showEditDialog = ref(false)
const showDetailDialog = ref(false)
const auditingRecordId = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const applicationFilter = ref('all')

const editClubData = reactive({})
const selectedApplication = ref(null)
const stats = reactive({
  totalApplications: 0,
  pendingApplications: 0,
  approvedApplications: 0,
  rejectedApplications: 0
})

// 计算属性
const club = computed(() => userStore.userInfo?.club)

const filteredApplications = computed(() => {
  let result = applicationStore.applications

  switch (applicationFilter.value) {
    case 'pending':
      result = result.filter(app => app.auditStatus === '待审核')
      break
    case 'approved':
      result = result.filter(app => app.auditStatus === '已通过')
      break
    case 'rejected':
      result = result.filter(app => app.auditStatus === '已拒绝')
      break
    default:
      // 全部，不筛选
      break
  }

  return result
})

const paginatedApplications = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredApplications.value.slice(start, end)
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

const getStatusTagType = (status) => {
  const statusMap = {
    '待审核': 'warning',
    '已通过': 'success',
    '已拒绝': 'danger'
  }
  return statusMap[status] || 'info'
}

const getFullPercentage = (club) => {
  return Math.round(((club.totalQuota - club.remainingQuota) / club.totalQuota) * 100)
}

const handleFilterChange = () => {
  currentPage.value = 1
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
}

const handleCurrentChange = (page) => {
  currentPage.value = page
}

const viewApplicationDetail = (application) => {
  selectedApplication.value = application
  showDetailDialog.value = true
}

const handleAudit = async (recordId, auditStatus) => {
  try {
    auditingRecordId.value = recordId

    const actionText = auditStatus === '已通过' ? '通过' : '拒绝'
    await ElMessageBox.confirm(
        `确定要${actionText}这条申请吗？`,
        `确认${actionText}`,
        {
          type: auditStatus === '已通过' ? 'success' : 'warning'
        }
    )

    const response = await applicationStore.auditApplication(recordId, auditStatus)

    if (response.code === 200) {
      ElMessage.success(`申请已${actionText}`)
      updateStats()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('审核操作失败:', error)
      ElMessage.error(error.message || '操作失败')
    }
  } finally {
    auditingRecordId.value = null
  }
}

const handleResetAudit = async (recordId) => {
  try {
    await ElMessageBox.confirm(
        '确定要将此申请重置为待审核状态吗？',
        '确认重置',
        {
          type: 'warning'
        }
    )

    const response = await applicationStore.auditApplication(recordId, '待审核')

    if (response.code === 200) {
      ElMessage.success('申请已重置为待审核')
      updateStats()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('重置申请失败:', error)
      ElMessage.error(error.message || '重置失败')
    }
  }
}

const handleEditDialogClose = (done) => {
  ElMessageBox.confirm('确定要取消编辑吗？', '提示', {
    type: 'warning'
  })
      .then(() => {
        Object.assign(editClubData, {})
        done()
      })
      .catch(() => {
        // 取消关闭
      })
}

const handleUpdateClub = async (clubData) => {
  try {
    const response = await clubStore.updateClub(clubData)
    if (response.code === 200) {
      ElMessage.success('社团信息更新成功')
      showEditDialog.value = false
      Object.assign(editClubData, {})

      // 更新用户信息中的社团信息
      if (userStore.userInfo?.club) {
        userStore.userInfo.club = response.data
      }
    }
  } catch (error) {
    console.error('更新社团信息失败:', error)
    ElMessage.error(error.message || '更新失败')
  }
}

const handleDeleteClub = async () => {
  if (!club.value) return

  try {
    await ElMessageBox.confirm(
        `确定要删除社团 "${club.value.clubName}" 吗？此操作不可恢复！`,
        '确认删除',
        {
          type: 'error',
          confirmButtonText: '确认删除',
          cancelButtonText: '取消'
        }
    )

    deleting.value = true
    const response = await clubStore.deleteClub(club.value.clubId)

    if (response.code === 200) {
      ElMessage.success('社团删除成功')
      // 清除用户信息中的社团关联
      if (userStore.userInfo) {
        userStore.userInfo.club = null
      }
      router.push('/home')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除社团失败:', error)
      ElMessage.error(error.message || '删除失败')
    }
  } finally {
    deleting.value = false
  }
}

const exportApplications = () => {
  ElMessage.info('导出功能开发中...')
}

const updateStats = () => {
  stats.totalApplications = applicationStore.applications.length
  stats.pendingApplications = applicationStore.pendingApplications.length
  stats.approvedApplications = applicationStore.approvedApplications.length
  stats.rejectedApplications = applicationStore.rejectedApplications.length
}

const loadApplications = async () => {
  if (!club.value) return

  loading.value = true
  try {
    await applicationStore.fetchApplicationsByClub(club.value.clubId)
    updateStats()
  } catch (error) {
    console.error('加载申请记录失败:', error)
    ElMessage.error('加载申请记录失败')
  } finally {
    loading.value = false
  }
}

const initEditData = () => {
  if (club.value) {
    Object.assign(editClubData, { ...club.value })
  }
}

onMounted(() => {
  if (club.value) {
    loadApplications()
    initEditData()
  }
})
</script>

<style scoped>
.management-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.management-header {
  text-align: center;
  margin-bottom: 32px;
}

.management-header h2 {
  font-size: 28px;
  color: #303133;
  margin-bottom: 8px;
}

.management-header p {
  font-size: 16px;
  color: #606266;
}

.access-denied,
.no-club {
  padding: 100px 0;
}

.management-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.club-basic-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-row {
  display: flex;
  gap: 40px;
  flex-wrap: wrap;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-item label {
  font-weight: 500;
  color: #606266;
  min-width: 80px;
}

.info-item span {
  color: #303133;
}

.full-width {
  width: 100%;
}

.club-intro {
  margin: 0;
  line-height: 1.6;
  color: #606266;
}

.text-danger {
  color: #f56c6c;
  font-weight: 500;
}

.stats-section {
  margin: 16px 0;
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

.total-applications {
  background: #409eff;
}

.pending-applications {
  background: #e6a23c;
}

.approved-applications {
  background: #67c23a;
}

.rejected-applications {
  background: #f56c6c;
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

.applications-filter {
  margin-bottom: 16px;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.student-info .student-name {
  font-weight: 500;
  margin-bottom: 4px;
}

.student-info .student-num {
  font-size: 12px;
  color: #909399;
  margin-bottom: 2px;
}

.student-info .student-phone {
  font-size: 12px;
  color: #909399;
}

.action-buttons {
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

.danger-actions {
  text-align: center;
}

.danger-tip {
  margin-top: 12px;
  font-size: 14px;
  color: #f56c6c;
  text-align: center;
}

@media (max-width: 768px) {
  .management-container {
    padding: 10px;
  }

  .info-row {
    flex-direction: column;
    gap: 12px;
  }

  .info-item {
    width: 100%;
  }

  .header-actions {
    flex-direction: column;
    width: 100%;
    gap: 8px;
  }

  .action-buttons {
    flex-direction: column;
  }

  .action-buttons .el-button {
    width: 100%;
  }
}
</style>