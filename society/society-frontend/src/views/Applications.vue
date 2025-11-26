<template>
  <div class="applications-container">
    <div class="applications-header">
      <h2>申请记录</h2>
      <p>查看和管理您的社团申请</p>
    </div>

    <!-- 筛选条件 -->
    <div class="filter-section">
      <el-card>
        <el-form :model="filterForm" inline>
          <el-form-item label="申请状态">
            <el-select
                v-model="filterForm.auditStatus"
                placeholder="全部状态"
                clearable
                @change="handleFilter"
            >
              <el-option
                  v-for="status in auditStatusOptions"
                  :key="status.value"
                  :label="status.label"
                  :value="status.value"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="时间范围">
            <el-date-picker
                v-model="filterForm.dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="YYYY-MM-DD"
                @change="handleFilter"
            />
          </el-form-item>

          <el-form-item v-if="userStore.isLeader">
            <el-select
                v-model="filterForm.studentId"
                placeholder="选择学生"
                clearable
                filterable
                remote
                :remote-method="searchStudents"
                :loading="studentSearchLoading"
                @change="handleFilter"
            >
              <el-option
                  v-for="student in studentOptions"
                  :key="student.studentId"
                  :label="`${student.studentName} (${student.studentNum})`"
                  :value="student.studentId"
              />
            </el-select>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="resetFilter">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <!-- 申请列表 -->
    <div class="applications-list">
      <el-card>
        <template #header>
          <div class="table-header">
            <span>申请记录</span>
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
                  v-if="userStore.isLeader"
                  type="success"
                  :icon="Download"
                  @click="exportApplications"
              >
                导出
              </el-button>
            </div>
          </div>
        </template>

        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="5" animated />
        </div>

        <el-table
            v-else
            :data="filteredApplications"
            style="width: 100%"
            empty-text="暂无申请记录"
        >
          <el-table-column prop="recordId" label="申请ID" width="80" />

          <el-table-column v-if="userStore.isLeader" label="学生信息" min-width="150">
            <template #default="{ row }">
              <div class="student-info">
                <div class="student-name">{{ row.student.studentName }}</div>
                <div class="student-num">{{ row.student.studentNum }}</div>
              </div>
            </template>
          </el-table-column>

          <el-table-column v-if="userStore.isStudent" label="社团信息" min-width="150">
            <template #default="{ row }">
              <div class="club-info">
                <div class="club-name">{{ row.club.clubName }}</div>
                <el-tag :type="getClubTypeTag(row.club.clubType)" size="small">
                  {{ row.club.clubType }}
                </el-tag>
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
                  查看
                </el-button>

                <el-button
                    v-if="userStore.isLeader && row.auditStatus === '待审核'"
                    type="success"
                    size="small"
                    :loading="auditingRecordId === row.recordId"
                    @click="handleAudit(row.recordId, '已通过')"
                >
                  通过
                </el-button>

                <el-button
                    v-if="userStore.isLeader && row.auditStatus === '待审核'"
                    type="danger"
                    size="small"
                    :loading="auditingRecordId === row.recordId"
                    @click="handleAudit(row.recordId, '已拒绝')"
                >
                  拒绝
                </el-button>

                <el-button
                    v-if="userStore.isStudent && row.auditStatus === '待审核'"
                    type="danger"
                    size="small"
                    :loading="deletingRecordId === row.recordId"
                    @click="handleDelete(row.recordId)"
                >
                  撤销
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>

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
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Download } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useApplicationStore } from '@/stores/application'
import { studentApi } from '@/api'
import ApplicationDetail from '@/components/ApplicationDetail.vue'
import { formatDate } from '@/utils'

const userStore = useUserStore()
const applicationStore = useApplicationStore()

const loading = ref(false)
const studentSearchLoading = ref(false)
const showDetailDialog = ref(false)
const auditingRecordId = ref(null)
const deletingRecordId = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)

const filterForm = reactive({
  auditStatus: '',
  dateRange: [],
  studentId: ''
})

const selectedApplication = ref(null)
const studentOptions = ref([])

const auditStatusOptions = [
  { label: '待审核', value: '待审核' },
  { label: '已通过', value: '已通过' },
  { label: '已拒绝', value: '已拒绝' }
]

// 计算属性
const filteredApplications = computed(() => {
  let result = applicationStore.applications

  // 状态筛选
  if (filterForm.auditStatus) {
    result = result.filter(app => app.auditStatus === filterForm.auditStatus)
  }

  // 时间范围筛选
  if (filterForm.dateRange && filterForm.dateRange.length === 2) {
    const [start, end] = filterForm.dateRange
    result = result.filter(app => {
      const applyDate = app.applyTime.split(' ')[0]
      return applyDate >= start && applyDate <= end
    })
  }

  // 学生筛选（负责人可见）
  if (userStore.isLeader && filterForm.studentId) {
    result = result.filter(app => app.student.studentId === parseInt(filterForm.studentId))
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

const handleFilter = () => {
  currentPage.value = 1
}

const resetFilter = () => {
  Object.assign(filterForm, {
    auditStatus: '',
    dateRange: [],
    studentId: ''
  })
  currentPage.value = 1
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
}

const handleCurrentChange = (page) => {
  currentPage.value = page
}

const searchStudents = async (query) => {
  if (!query) {
    studentOptions.value = []
    return
  }

  studentSearchLoading.value = true
  try {
    const response = await studentApi.searchStudents(query)
    if (response.code === 200) {
      studentOptions.value = response.data
    }
  } catch (error) {
    console.error('搜索学生失败:', error)
  } finally {
    studentSearchLoading.value = false
  }
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

const handleDelete = async (recordId) => {
  try {
    deletingRecordId.value = recordId

    await ElMessageBox.confirm(
        '确定要撤销这条申请吗？',
        '确认撤销',
        {
          type: 'warning'
        }
    )

    const response = await applicationStore.deleteApplication(recordId)

    if (response.code === 200) {
      ElMessage.success('申请已撤销')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('撤销申请失败:', error)
      ElMessage.error(error.message || '撤销失败')
    }
  } finally {
    deletingRecordId.value = null
  }
}

const exportApplications = () => {
  ElMessage.info('导出功能开发中...')
}

const loadApplications = async () => {
  loading.value = true
  try {
    if (userStore.isStudent) {
      console.log('加载学生申请记录，学生ID:', userStore.userId)
      await applicationStore.fetchApplicationsByStudent(userStore.userId)
      console.log('加载到的申请记录:', applicationStore.applications)
    } else if (userStore.isLeader && userStore.userInfo?.club) {
      console.log('加载社团申请记录，社团ID:', userStore.userInfo.club.clubId)
      await applicationStore.fetchApplicationsByClub(userStore.userInfo.club.clubId)
    } else {
      console.log('加载所有申请记录')
      await applicationStore.fetchAllApplications()
    }
  } catch (error) {
    console.error('加载申请记录失败:', error)
    ElMessage.error('加载申请记录失败: ' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadApplications()
})
</script>

<style scoped>
.applications-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.applications-header {
  text-align: center;
  margin-bottom: 32px;
}

.applications-header h2 {
  font-size: 28px;
  color: #303133;
  margin-bottom: 8px;
}

.applications-header p {
  font-size: 16px;
  color: #606266;
}

.filter-section {
  margin-bottom: 24px;
}

.filter-section .el-form {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.filter-section .el-form-item {
  margin-bottom: 0;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.table-header span {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
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
}

.club-info .club-name {
  font-weight: 500;
  margin-bottom: 4px;
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

.loading-container {
  padding: 40px 0;
}

@media (max-width: 768px) {
  .applications-container {
    padding: 10px;
  }

  .filter-section .el-form {
    flex-direction: column;
  }

  .filter-section .el-form-item {
    width: 100%;
  }

  .table-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }

  .action-buttons {
    flex-direction: column;
  }

  .action-buttons .el-button {
    width: 100%;
  }
}
</style>