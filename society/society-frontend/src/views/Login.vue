<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h2>社团管理系统</h2>
        <p>欢迎登录</p>
      </div>

      <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          @submit.prevent="handleLogin"
      >
        <el-form-item prop="userType">
          <el-radio-group v-model="loginForm.userType" @change="handleUserTypeChange">
            <el-radio-button value="student">学生登录</el-radio-button>
            <el-radio-button value="leader">负责人登录</el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-form-item prop="username">
          <el-input
              v-model="loginForm.username"
              :prefix-icon="User"
              :placeholder="loginForm.userType === 'student' ? '请输入学号' : '请输入负责人编号'"
              size="large"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
              v-model="loginForm.password"
              :prefix-icon="Lock"
              type="password"
              placeholder="请输入密码"
              size="large"
              show-password
              @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item>
          <el-button
              type="primary"
              size="large"
              class="login-btn"
              :loading="loading"
              @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>

        <div class="login-footer">
          <span>还没有账号？</span>
          <el-link type="primary" @click="$router.push('/register')">立即注册</el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { validateNumber } from '@/utils'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref()
const loading = ref(false)

const loginForm = reactive({
  userType: 'student',
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { validator: validateNumber, trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符', trigger: 'blur' }
  ]
}

const handleUserTypeChange = () => {
  loginForm.username = ''
  loginForm.password = ''
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    const valid = await loginFormRef.value.validate()
    if (!valid) return

    loading.value = true

    const loginData = {
      [loginForm.userType === 'student' ? 'studentNum' : 'leaderNum']: loginForm.username,
      password: loginForm.password
    }

    console.log('发送登录请求:', loginData)

    let response
    if (loginForm.userType === 'student') {
      response = await userStore.studentLogin(loginData)
    } else {
      response = await userStore.leaderLogin(loginData)
    }

    console.log('登录响应:', response)

    // 修复：检查响应是否存在且有 code 属性
    if (response && response.code === 200) {
      ElMessage.success('登录成功')
      router.push('/home')
    } else {
      // 处理其他状态码
      const errorMessage = response?.message || '登录失败，请检查账号密码'
      ElMessage.error(errorMessage)
    }
  } catch (error) {
    console.error('登录失败:', error)
    // 修复：提供更详细的错误信息
    const errorMessage = error.response?.data?.message || error.message || '登录失败，请稍后重试'
    ElMessage.error(errorMessage)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (userStore.isLoggedIn) {
    router.push('/home')
  }
})
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h2 {
  color: #333;
  margin-bottom: 10px;
  font-size: 24px;
}

.login-header p {
  color: #666;
  font-size: 14px;
}

.login-form {
  margin-top: 20px;
}

.login-btn {
  width: 100%;
  margin-top: 10px;
}

.login-footer {
  text-align: center;
  margin-top: 20px;
  color: #666;
  font-size: 14px;
}

.login-footer .el-link {
  margin-left: 5px;
}
</style>