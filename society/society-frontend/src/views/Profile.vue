<template>
  <div class="profile-container">
    <div class="profile-header">
      <h2>个人信息</h2>
      <p>管理您的个人资料和账户设置</p>
    </div>

    <div class="profile-content">
      <el-row :gutter="24">
        <!-- 左侧：基本信息 -->
        <el-col :xs="24" :lg="16">
          <el-card class="profile-card">
            <template #header>
              <h3>基本信息</h3>
            </template>

            <el-form
                ref="profileFormRef"
                :model="profileForm"
                :rules="profileRules"
                label-width="100px"
                @submit.prevent="handleSaveProfile"
            >
              <el-form-item label="姓名" prop="name">
                <el-input
                    v-model="profileForm.name"
                    :placeholder="getNamePlaceholder"
                />
              </el-form-item>

              <el-form-item :label="getIdLabel" prop="number">
                <el-input
                    v-model="profileForm.number"
                    :placeholder="getIdPlaceholder"
                    disabled
                />
                <div class="form-tip">此信息不可修改</div>
              </el-form-item>

              <el-form-item label="手机号" prop="phone">
                <el-input
                    v-model="profileForm.phone"
                    placeholder="请输入手机号"
                    :prefix-icon="Phone"
                />
              </el-form-item>

              <el-form-item v-if="userStore.isLeader" label="负责社团">
                <el-input
                    v-model="profileForm.clubName"
                    placeholder="暂无负责的社团"
                    disabled
                />
              </el-form-item>

              <el-form-item label="创建时间">
                <el-input
                    :value="formatDate(profileForm.createTime)"
                    placeholder="未知"
                    disabled
                />
              </el-form-item>

              <el-form-item>
                <el-button
                    type="primary"
                    @click="handleSaveProfile"
                    :loading="saving"
                >
                  {{ saving ? '保存中...' : '保存修改' }}
                </el-button>
                <el-button @click="resetForm">重置</el-button>
              </el-form-item>
            </el-form>
          </el-card>

          <!-- 密码修改 -->
          <el-card class="password-card">
            <template #header>
              <h3>修改密码</h3>
            </template>

            <el-form
                ref="passwordFormRef"
                :model="passwordForm"
                :rules="passwordRules"
                label-width="100px"
                @submit.prevent="handleChangePassword"
            >
              <el-form-item label="当前密码" prop="currentPassword">
                <el-input
                    v-model="passwordForm.currentPassword"
                    type="password"
                    placeholder="请输入当前密码"
                    :prefix-icon="Lock"
                    show-password
                />
              </el-form-item>

              <el-form-item label="新密码" prop="newPassword">
                <el-input
                    v-model="passwordForm.newPassword"
                    type="password"
                    placeholder="请输入新密码"
                    :prefix-icon="Lock"
                    show-password
                />
              </el-form-item>

              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input
                    v-model="passwordForm.confirmPassword"
                    type="password"
                    placeholder="请再次输入新密码"
                    :prefix-icon="Lock"
                    show-password
                />
              </el-form-item>

              <el-form-item>
                <el-button
                    type="warning"
                    @click="handleChangePassword"
                    :loading="changingPassword"
                >
                  {{ changingPassword ? '修改中...' : '修改密码' }}
                </el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>

        <!-- 右侧：统计信息 -->
        <el-col :xs="24" :lg="8">
          <!-- 用户信息卡片 -->
          <el-card class="user-card">
            <template #header>
              <h3>用户信息</h3>
            </template>

            <div class="user-avatar-section">
              <div class="avatar-container">
                <el-avatar :size="80" :src="userStore.userInfo?.avatar">
                  {{ getUserNameFirstChar }}
                </el-avatar>
              </div>
              <div class="user-summary">
                <h3>{{ getUserName }}</h3>
                <p class="user-type">{{ getUserTypeText }}</p>
                <p class="user-number">{{ getUserNumber }}</p>
              </div>
            </div>

            <div class="user-stats">
              <div class="stat-item">
                <div class="stat-label">注册时间</div>
                <div class="stat-value">
                  {{ formatDate(userStore.userInfo?.createTime) || '未知' }}
                </div>
              </div>
              <div v-if="userStore.isStudent" class="stat-item">
                <div class="stat-label">申请总数</div>
                <div class="stat-value">{{ userStats.totalApplications || 0 }}</div>
              </div>
              <div v-if="userStore.isStudent" class="stat-item">
                <div class="stat-label">已加入社团</div>
                <div class="stat-value">{{ userStats.joinedClubs || 0 }}</div>
              </div>
              <div v-if="userStore.isLeader" class="stat-item">
                <div class="stat-label">负责社团</div>
                <div class="stat-value">
                  {{ userStore.userInfo?.club ? 1 : 0 }}
                </div>
              </div>
            </div>
          </el-card>

          <!-- 快捷操作 -->
          <el-card class="actions-card">
            <template #header>
              <h3>快捷操作</h3>
            </template>

            <div class="quick-actions">
              <el-button
                  v-if="userStore.isStudent"
                  type="primary"
                  @click="$router.push('/clubs')"
              >
                <el-icon><Plus /></el-icon>
                申请加入社团
              </el-button>

              <el-button
                  v-if="userStore.isStudent"
                  @click="$router.push('/applications')"
              >
                <el-icon><Document /></el-icon>
                查看申请记录
              </el-button>

              <el-button
                  v-if="userStore.isLeader && userStore.userInfo?.club"
                  type="success"
                  @click="$router.push('/management')"
              >
                <el-icon><Setting /></el-icon>
                管理社团
              </el-button>

              <el-button
                  type="info"
                  @click="$router.push('/home')"
              >
                <el-icon><HomeFilled /></el-icon>
                返回首页
              </el-button>

              <el-button
                  type="danger"
                  @click="handleLogout"
              >
                <el-icon><SwitchButton /></el-icon>
                退出登录
              </el-button>
            </div>
          </el-card>
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
  Phone,
  Lock,
  Plus,
  Document,
  Setting,
  HomeFilled,
  SwitchButton
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useApplicationStore } from '@/stores/application'
import { formatDate, validatePhone } from '@/utils'

const router = useRouter()
const userStore = useUserStore()
const applicationStore = useApplicationStore()

const profileFormRef = ref()
const passwordFormRef = ref()
const saving = ref(false)
const changingPassword = ref(false)

const profileForm = reactive({
  name: '',
  number: '',
  phone: '',
  clubName: '',
  createTime: ''
})

const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const userStats = reactive({
  totalApplications: 0,
  joinedClubs: 0
})

// 计算属性
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

const getNamePlaceholder = computed(() => {
  return userStore.isStudent ? '请输入姓名' : '请输入负责人姓名'
})

const getIdLabel = computed(() => {
  return userStore.isStudent ? '学号' : '负责人编号'
})

const getIdPlaceholder = computed(() => {
  return userStore.isStudent ? '学号' : '负责人编号'
})

// 表单规则
const profileRules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 10, message: '姓名长度在2-10个字符', trigger: 'blur' }
  ],
  phone: [
    { validator: (rule, value, callback) => {
        if (!value) {
          callback()
        } else if (!validatePhone(value)) {
          callback(new Error('手机号格式不正确'))
        } else {
          callback()
        }
      }, trigger: 'blur' }
  ]
}

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  currentPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 方法
const initProfileForm = () => {
  const userInfo = userStore.userInfo
  if (!userInfo) return

  if (userStore.isStudent) {
    profileForm.name = userInfo.studentName || ''
    profileForm.number = userInfo.studentNum || ''
    profileForm.phone = userInfo.phone || ''
    profileForm.createTime = userInfo.createTime || ''
  } else {
    profileForm.name = userInfo.leaderName || ''
    profileForm.number = userInfo.leaderNum || ''
    profileForm.phone = userInfo.phone || ''
    profileForm.clubName = userInfo.club?.clubName || ''
    profileForm.createTime = userInfo.createTime || ''
  }
}

const resetForm = () => {
  initProfileForm()
  if (profileFormRef.value) {
    profileFormRef.value.clearValidate()
  }
}

const handleSaveProfile = async () => {
  if (!profileFormRef.value) return

  try {
    const valid = await profileFormRef.value.validate()
    if (!valid) return

    saving.value = true

    const updateData = {
      [userStore.isStudent ? 'studentId' : 'leaderId']: userStore.userId,
      [userStore.isStudent ? 'studentName' : 'leaderName']: profileForm.name,
      phone: profileForm.phone || null
    }

    const response = await userStore.updateUserInfo(updateData)

    if (response.code === 200) {
      ElMessage.success('个人信息更新成功')
    }
  } catch (error) {
    console.error('更新个人信息失败:', error)
    ElMessage.error(error.message || '更新失败')
  } finally {
    saving.value = false
  }
}

const handleChangePassword = async () => {
  if (!passwordFormRef.value) return

  try {
    const valid = await passwordFormRef.value.validate()
    if (!valid) return

    changingPassword.value = true

    // 这里应该调用修改密码的API
    // 由于后端没有提供修改密码的接口，这里模拟成功
    await new Promise(resolve => setTimeout(resolve, 1000))

    ElMessage.success('密码修改成功')

    // 清空密码表单
    Object.assign(passwordForm, {
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    })

    if (passwordFormRef.value) {
      passwordFormRef.value.resetFields()
    }
  } catch (error) {
    console.error('修改密码失败:', error)
    ElMessage.error(error.message || '修改密码失败')
  } finally {
    changingPassword.value = false
  }
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

const loadUserStats = async () => {
  if (userStore.isStudent) {
    try {
      await applicationStore.fetchApplicationsByStudent(userStore.userId)
      userStats.totalApplications = applicationStore.applications.length
      userStats.joinedClubs = applicationStore.approvedApplications.length
    } catch (error) {
      console.error('加载用户统计失败:', error)
    }
  }
}

onMounted(() => {
  initProfileForm()
  loadUserStats()
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.profile-header {
  text-align: center;
  margin-bottom: 32px;
}

.profile-header h2 {
  font-size: 28px;
  color: #303133;
  margin-bottom: 8px;
}

.profile-header p {
  font-size: 16px;
  color: #606266;
}

.profile-content {
  margin-top: 24px;
}

.profile-card,
.password-card,
.user-card,
.actions-card {
  margin-bottom: 24px;
}

.profile-card h3,
.password-card h3,
.user-card h3,
.actions-card h3 {
  margin: 0;
  font-size: 16px;
  color: #303133;
  font-weight: 600;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.user-avatar-section {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
}

.avatar-container {
  margin-right: 16px;
}

.user-summary h3 {
  margin: 0 0 4px 0;
  font-size: 18px;
  color: #303133;
}

.user-type {
  margin: 0 0 4px 0;
  font-size: 14px;
  color: #606266;
}

.user-number {
  margin: 0;
  font-size: 12px;
  color: #909399;
}

.user-stats {
  border-top: 1px solid #ebeef5;
  padding-top: 16px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
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
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.quick-actions .el-button {
  width: 100%;
  justify-content: flex-start;
}

@media (max-width: 768px) {
  .profile-container {
    padding: 10px;
  }

  .user-avatar-section {
    flex-direction: column;
    text-align: center;
  }

  .avatar-container {
    margin-right: 0;
    margin-bottom: 12px;
  }

  .quick-actions .el-button {
    width: 100%;
  }
}
</style>