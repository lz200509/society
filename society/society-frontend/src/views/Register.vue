<template>
  <div class="register-container">
    <div class="register-form">
      <h2>学生注册</h2>

      <el-form
          ref="registerFormRef"
          :model="registerForm"
          :rules="registerRules"
          label-width="80px"
          class="demo-registerForm"
      >
        <el-form-item label="学号" prop="studentNum">
          <el-input
              v-model="registerForm.studentNum"
              placeholder="请输入学号"
              @blur="checkStudentNum"
          />
        </el-form-item>

        <el-form-item label="姓名" prop="studentName">
          <el-input
              v-model="registerForm.studentName"
              placeholder="请输入真实姓名"
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="请输入密码"
              show-password
          />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="请再次输入密码"
              show-password
          />
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input
              v-model="registerForm.phone"
              placeholder="请输入手机号"
          />
        </el-form-item>

        <el-form-item>
          <el-button
              type="primary"
              :loading="loading"
              @click="handleRegister"
              style="width: 100%"
          >
            注册
          </el-button>
        </el-form-item>

        <div class="login-link">
          <span>已有账号？</span>
          <el-link type="primary" @click="$router.push('/login')">立即登录</el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { studentApi } from '@/api' // 确保正确导入 API
import { validatePhone } from '@/utils' // 导入验证工具

const router = useRouter()

const loading = ref(false)
const registerFormRef = ref()

// 注册表单数据
const registerForm = reactive({
  studentNum: '',
  studentName: '',
  password: '',
  confirmPassword: '',
  phone: ''
})

// 验证规则
const registerRules = {
  studentNum: [
    { required: true, message: '请输入学号', trigger: 'blur' },
    { min: 3, max: 20, message: '学号长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  studentName: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 10, message: '姓名长度在 2 到 10 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value && !validatePhone(value)) {
          callback(new Error('请输入正确的手机号格式'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 检查学号是否存在
const checkStudentNum = async () => {
  if (!registerForm.studentNum) return

  try {
    console.log('检查学号:', registerForm.studentNum)

    // 直接调用 API，不通过 store
    const response = await studentApi.checkStudentNumExists(registerForm.studentNum)
    console.log('学号检查响应:', response)

    if (response.data) {
      ElMessage.warning('该学号已被注册')
    }
  } catch (error) {
    console.error('检查学号失败:', error)
    // 不显示错误消息，避免干扰用户
  }
}

// 处理注册
const handleRegister = async () => {
  if (!registerFormRef.value) return

  try {
    // 验证表单
    const valid = await registerFormRef.value.validate()
    if (!valid) return

    loading.value = true
    console.log('提交注册数据:', registerForm)

    // 准备注册数据（移除确认密码字段）
    const registerData = {
      studentNum: registerForm.studentNum,
      studentName: registerForm.studentName,
      password: registerForm.password,
      phone: registerForm.phone
    }

    // 直接调用 API 注册
    const response = await studentApi.register(registerData)
    console.log('注册响应:', response)

    if (response.code === 200) {
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } else {
      ElMessage.error(response.message || '注册失败')
    }
  } catch (error) {
    console.error('注册失败:', error)
    console.error('错误详情:', error.response?.data)

    // 显示具体的错误信息
    if (error.response?.data) {
      ElMessage.error(typeof error.response.data === 'string'
          ? error.response.data
          : '注册失败，请检查输入信息')
    } else {
      ElMessage.error(error.message || '注册失败')
    }
  } finally {
    loading.value = false
  }
  const handleRegister = async () => {
    console.log('开始注册流程...')

    try {
      // 验证表单
      const valid = await registerFormRef.value.validate()
      console.log('表单验证结果:', valid)

      if (!valid) {
        console.log('表单验证失败')
        return
      }

      loading.value = true

      const registerData = {
        studentNum: registerForm.studentNum,
        studentName: registerForm.studentName,
        password: registerForm.password,
        phone: registerForm.phone
      }

      console.log('提交的注册数据:', registerData)

      // 直接调用 API
      const response = await studentApi.register(registerData)
      console.log('注册API响应:', response)

      if (response.code === 200) {
        ElMessage.success('注册成功，请登录')
        router.push('/login')
      } else {
        console.log('注册业务失败:', response.message)
        ElMessage.error(response.message || '注册失败')
      }
    } catch (error) {
      console.error('注册过程异常:', error)
      console.error('错误响应:', error.response)
      console.error('错误数据:', error.response?.data)

      // 显示具体错误信息
      if (error.response?.data) {
        const errorData = error.response.data
        if (typeof errorData === 'string') {
          ElMessage.error(errorData)
        } else if (errorData.message) {
          ElMessage.error(errorData.message)
        } else {
          ElMessage.error('注册失败，请检查网络连接')
        }
      } else {
        ElMessage.error(error.message || '注册失败')
      }
    } finally {
      loading.value = false
    }
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.register-form {
  background: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

.register-form h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
}

.login-link {
  text-align: center;
  margin-top: 20px;
  color: #606266;
}

.login-link span {
  margin-right: 8px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}
</style>