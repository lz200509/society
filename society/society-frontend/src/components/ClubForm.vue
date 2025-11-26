<template>
  <el-form
      ref="formRef"
      :model="club"
      :rules="rules"
      label-width="100px"
      @submit.prevent="handleSubmit"
  >
    <el-form-item label="社团名称" prop="clubName">
      <el-input
          v-model="club.clubName"
          placeholder="请输入社团名称"
          @blur="checkClubName"
      />
    </el-form-item>

    <el-form-item label="社团类型" prop="clubType">
      <el-select v-model="club.clubType" placeholder="请选择社团类型">
        <el-option
            v-for="type in clubTypes"
            :key="type"
            :label="type"
            :value="type"
        />
      </el-select>
    </el-form-item>

    <el-form-item label="总名额" prop="totalQuota">
      <el-input-number
          v-model="club.totalQuota"
          :min="1"
          :max="200"
          placeholder="请输入总名额"
      />
    </el-form-item>

    <el-form-item label="社团介绍" prop="clubIntro">
      <el-input
          v-model="club.clubIntro"
          type="textarea"
          :rows="4"
          placeholder="请输入社团介绍"
          maxlength="500"
          show-word-limit
      />
    </el-form-item>

    <el-form-item label="Logo链接" prop="logoUrl">
      <el-input
          v-model="club.logoUrl"
          placeholder="请输入社团Logo图片链接（可选）"
      />
    </el-form-item>

    <el-form-item>
      <div class="form-actions">
        <el-button type="primary" @click="handleSubmit" :loading="loading">
          {{ loading ? '提交中...' : '提交' }}
        </el-button>
        <el-button @click="handleCancel">取消</el-button>
      </div>
    </el-form-item>
  </el-form>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { clubApi } from '@/api'

const props = defineProps({
  club: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['submit', 'cancel'])

const formRef = ref()
const loading = ref(false)

const club = reactive({ ...props.club })
const clubTypes = ['体育', '文艺', '学术']

const rules = {
  clubName: [
    { required: true, message: '请输入社团名称', trigger: 'blur' },
    { min: 2, max: 50, message: '社团名称长度在2-50个字符', trigger: 'blur' }
  ],
  clubType: [
    { required: true, message: '请选择社团类型', trigger: 'change' }
  ],
  totalQuota: [
    { required: true, message: '请输入总名额', trigger: 'blur' },
    { type: 'number', min: 1, max: 200, message: '名额范围1-200', trigger: 'blur' }
  ],
  clubIntro: [
    { max: 500, message: '介绍不能超过500个字符', trigger: 'blur' }
  ]
}

const checkClubName = async () => {
  if (!club.clubName) return

  try {
    const response = await clubApi.checkClubNameExists(club.clubName)
    if (response.data) {
      ElMessage.warning('该社团名称已存在')
    }
  } catch (error) {
    console.error('检查社团名称失败:', error)
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    const valid = await formRef.value.validate()
    if (!valid) return

    loading.value = true
    emit('submit', { ...club })
  } catch (error) {
    console.error('表单验证失败:', error)
  } finally {
    loading.value = false
  }
}

const handleCancel = () => {
  emit('cancel')
}
</script>

<style scoped>
.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}
</style>