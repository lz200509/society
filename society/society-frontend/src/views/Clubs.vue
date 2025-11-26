<template>
  <div class="clubs-container">
    <div class="clubs-header">
      <h2>社团列表</h2>
      <p>发现你感兴趣的社团，开启精彩的校园生活</p>
    </div>

    <!-- 搜索和筛选 -->
    <div class="search-filter">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8">
          <el-input
              v-model="searchKeyword"
              placeholder="搜索社团名称..."
              :prefix-icon="Search"
              clearable
              @input="handleSearch"
          />
        </el-col>
        <el-col :xs="12" :sm="6" :md="4">
          <el-select
              v-model="filterType"
              placeholder="社团类型"
              clearable
              @change="handleFilter"
          >
            <el-option
                v-for="type in clubTypes"
                :key="type"
                :label="type"
                :value="type"
            />
          </el-select>
        </el-col>
        <el-col :xs="12" :sm="6" :md="4">
          <el-select
              v-model="filterQuota"
              placeholder="名额筛选"
              clearable
              @change="handleFilter"
          >
            <el-option label="有名额" value="available" />
            <el-option label="已满员" value="full" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8" class="filter-actions">
          <el-button type="primary" @click="resetFilters">重置筛选</el-button>
          <el-button
              v-if="userStore.isLeader"
              type="success"
              @click="showCreateDialog = true"
          >
            <el-icon><Plus /></el-icon>
            创建社团
          </el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 社团列表 -->
    <div class="clubs-list">
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="6" animated />
      </div>

      <div v-else-if="filteredClubs.length === 0" class="empty-state">
        <el-empty description="暂无社团数据" />
      </div>

      <div v-else class="clubs-grid">
        <div
            v-for="club in filteredClubs"
            :key="club.clubId"
            class="club-card"
        >
          <div class="club-header">
            <div class="club-logo">
              <img v-if="club.logoUrl" :src="club.logoUrl" :alt="club.clubName" />
              <div v-else class="club-logo-placeholder">
                {{ club.clubName.charAt(0) }}
              </div>
            </div>
            <div class="club-basic-info">
              <h3 class="club-name">{{ club.clubName }}</h3>
              <el-tag :type="getClubTypeTag(club.clubType)" size="small">
                {{ club.clubType }}
              </el-tag>
              <div class="club-quota">
                <el-progress
                    :percentage="getQuotaPercentage(club)"
                    :color="getQuotaColor(club)"
                    :show-text="false"
                />
                <span class="quota-text">
                  {{ club.remainingQuota }}/{{ club.totalQuota }}
                </span>
              </div>
            </div>
          </div>

          <div class="club-intro">
            <p>{{ club.clubIntro || '暂无介绍' }}</p>
          </div>

          <div class="club-actions">
            <el-button
                type="primary"
                size="small"
                @click="goToClubDetail(club.clubId)"
            >
              查看详情
            </el-button>
            <el-button
                v-if="userStore.isStudent && club.remainingQuota > 0"
                type="success"
                size="small"
                :loading="applyingClubId === club.clubId"
                @click="handleApply(club)"
            >
              申请加入
            </el-button>
            <el-button
                v-else-if="userStore.isStudent"
                type="info"
                size="small"
                disabled
            >
              已满员
            </el-button>
            <el-button
                v-if="userStore.isLeader && userStore.userInfo?.club?.clubId === club.clubId"
                type="warning"
                size="small"
                @click="goToManagement(club.clubId)"
            >
              管理社团
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="filteredClubs.length > 0" class="pagination-container">
      <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="filteredClubs.length"
          :page-sizes="[12, 24, 36, 48]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
      />
    </div>

    <!-- 创建社团对话框 -->
    <el-dialog
        v-model="showCreateDialog"
        title="创建新社团"
        width="500px"
        :before-close="handleCreateDialogClose"
    >
      <ClubForm
          v-if="showCreateDialog"
          :club="createClubData"
          @submit="handleCreateClub"
          @cancel="showCreateDialog = false"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useClubStore } from '@/stores/club'
import { useApplicationStore } from '@/stores/application'
import ClubForm from '@/components/ClubForm.vue'

const router = useRouter()
const userStore = useUserStore()
const clubStore = useClubStore()
const applicationStore = useApplicationStore()

const loading = ref(false)
const searchKeyword = ref('')
const filterType = ref('')
const filterQuota = ref('')
const currentPage = ref(1)
const pageSize = ref(12)
const showCreateDialog = ref(false)
const applyingClubId = ref(null)

const createClubData = reactive({
  clubName: '',
  clubType: '',
  clubIntro: '',
  totalQuota: 50,
  logoUrl: ''
})

const clubTypes = ['体育', '文艺', '学术']

// 计算属性
const filteredClubs = computed(() => {
  let result = clubStore.clubs

  // 搜索筛选
  if (searchKeyword.value) {
    result = result.filter(club =>
        club.clubName.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
  }

  // 类型筛选
  if (filterType.value) {
    result = result.filter(club => club.clubType === filterType.value)
  }

  // 名额筛选
  if (filterQuota.value === 'available') {
    result = result.filter(club => club.remainingQuota > 0)
  } else if (filterQuota.value === 'full') {
    result = result.filter(club => club.remainingQuota === 0)
  }

  return result
})

const paginatedClubs = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredClubs.value.slice(start, end)
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

const getQuotaPercentage = (club) => {
  return (club.remainingQuota / club.totalQuota) * 100
}

const getQuotaColor = (club) => {
  const percentage = getQuotaPercentage(club)
  if (percentage > 50) return '#67c23a'
  if (percentage > 20) return '#e6a23c'
  return '#f56c6c'
}

const goToClubDetail = (clubId) => {
  router.push(`/club/${clubId}`)
}

const goToManagement = (clubId) => {
  router.push('/management')
}

const handleSearch = () => {
  currentPage.value = 1
}

const handleFilter = () => {
  currentPage.value = 1
}

const resetFilters = () => {
  searchKeyword.value = ''
  filterType.value = ''
  filterQuota.value = ''
  currentPage.value = 1
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
}

const handleCurrentChange = (page) => {
  currentPage.value = page
}

const handleApply = async (club) => {
  if (!userStore.isStudent) {
    ElMessage.warning('请以学生身份登录')
    return
  }

  try {
    applyingClubId.value = club.clubId

    // 检查是否已经申请过
    const exists = await applicationStore.checkApplicationExists(
        userStore.userId,
        club.clubId
    )

    if (exists) {
      ElMessage.warning('您已经申请过该社团')
      return
    }

    await ElMessageBox.confirm(
        `确定要申请加入 ${club.clubName} 吗？`,
        '确认申请',
        {
          type: 'info'
        }
    )

    // 使用正确的申请数据格式
    const applicationData = {
      studentId: userStore.userId,
      clubId: club.clubId,
      applyTime: new Date().toISOString(),
      auditStatus: '待审核'
    }

    console.log('提交申请数据:', applicationData)

    const response = await applicationStore.submitApplication(applicationData)

    if (response.code === 200) {
      ElMessage.success('申请提交成功，等待审核')
      // 立即刷新申请列表
      await applicationStore.fetchApplicationsByStudent(userStore.userId)
    } else {
      ElMessage.error(response.message || '申请失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('申请失败:', error)
      console.error('错误详情:', error.response?.data)
      ElMessage.error(error.message || '申请失败')
    }
  } finally {
    applyingClubId.value = null
  }
}

const handleCreateDialogClose = (done) => {
  ElMessageBox.confirm('确定要取消创建社团吗？', '提示', {
    type: 'warning'
  })
      .then(() => {
        Object.assign(createClubData, {
          clubName: '',
          clubType: '',
          clubIntro: '',
          totalQuota: 50,
          logoUrl: ''
        })
        done()
      })
      .catch(() => {
        // 取消关闭
      })
}

const handleCreateClub = async (clubData) => {
  try {
    const response = await clubStore.createClub(clubData)
    if (response.code === 200) {
      ElMessage.success('社团创建成功')
      showCreateDialog.value = false
      Object.assign(createClubData, {
        clubName: '',
        clubType: '',
        clubIntro: '',
        totalQuota: 50,
        logoUrl: ''
      })
    }
  } catch (error) {
    console.error('创建社团失败:', error)
    ElMessage.error(error.message || '创建社团失败')
  }
}

const loadData = async () => {
  loading.value = true
  try {
    await clubStore.fetchAllClubs()
  } catch (error) {
    console.error('加载社团数据失败:', error)
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
.clubs-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.clubs-header {
  text-align: center;
  margin-bottom: 32px;
}

.clubs-header h2 {
  font-size: 28px;
  color: #303133;
  margin-bottom: 8px;
}

.clubs-header p {
  font-size: 16px;
  color: #606266;
}

.search-filter {
  margin-bottom: 24px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.filter-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.clubs-list {
  min-height: 400px;
}

.clubs-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.club-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 20px;
  transition: all 0.3s ease;
}

.club-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.club-header {
  display: flex;
  align-items: flex-start;
  margin-bottom: 16px;
}

.club-logo {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  overflow: hidden;
  flex-shrink: 0;
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

.club-basic-info {
  flex: 1;
}

.club-name {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #303133;
  line-height: 1.4;
}

.club-quota {
  margin-top: 8px;
}

.quota-text {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  display: block;
}

.club-intro {
  margin-bottom: 16px;
}

.club-intro p {
  margin: 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.club-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

.loading-container {
  padding: 40px 0;
}

.empty-state {
  padding: 60px 0;
}

@media (max-width: 768px) {
  .clubs-container {
    padding: 10px;
  }

  .clubs-grid {
    grid-template-columns: 1fr;
  }

  .filter-actions {
    justify-content: stretch;
  }

  .filter-actions .el-button {
    flex: 1;
  }
}
</style>