<template>
  <div class="optimized-application-list">
    <!-- 虚拟滚动容器 -->
    <div
        ref="scrollContainer"
        class="virtual-scroll-container"
        @scroll="handleScroll"
    >
      <div
          class="virtual-scroll-content"
          :style="{ height: totalHeight + 'px' }"
      >
        <div
            v-for="visibleItem in visibleItems"
            :key="visibleItem.data.recordId"
            class="virtual-item"
            :style="{
            transform: `translateY(${visibleItem.offset}px)`,
            height: itemHeight + 'px'
          }"
        >
          <ApplicationItem
              :application="visibleItem.data"
              :show-actions="showActions"
              :auditing-record-id="auditingRecordId"
              :deleting-record-id="deletingRecordId"
              @view="$emit('view', visibleItem.data)"
              @approve="$emit('approve', visibleItem.data.recordId)"
              @reject="$emit('reject', visibleItem.data.recordId)"
              @delete="$emit('delete', visibleItem.data.recordId)"
          />
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-indicator">
      <el-skeleton :rows="3" animated />
    </div>

    <!-- 空状态 -->
    <div v-else-if="applications.length === 0" class="empty-state">
      <el-empty :description="emptyText" :image-size="80" />
    </div>

    <!-- 分页 -->
    <div v-if="showPagination && applications.length > 0" class="pagination">
      <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="applications.length"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handlePageSizeChange"
          @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { debounce, throttle } from '@/utils/performance'
import ApplicationItem from './ApplicationItem.vue'

const props = defineProps({
  applications: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
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
  },
  showPagination: {
    type: Boolean,
    default: true
  },
  pageSize: {
    type: Number,
    default: 10
  },
  enableVirtualScroll: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits([
  'view',
  'approve',
  'reject',
  'delete',
  'page-change',
  'size-change'
])

// 分页状态
const currentPage = ref(1)
const scrollContainer = ref(null)

// 虚拟滚动配置
const itemHeight = 120
const buffer = 5
const scrollTop = ref(0)
const containerHeight = ref(0)

// 计算可见项
const visibleItems = computed(() => {
  if (!props.enableVirtualScroll || props.applications.length === 0) {
    return props.paginatedApplications.map((app, index) => ({
      data: app,
      offset: index * itemHeight,
      index
    }))
  }

  const startIndex = Math.max(0, Math.floor(scrollTop.value / itemHeight) - buffer)
  const endIndex = Math.min(
      props.applications.length - 1,
      startIndex + Math.ceil(containerHeight.value / itemHeight) + buffer * 2
  )

  return props.applications
      .slice(startIndex, endIndex + 1)
      .map((app, index) => ({
        data: app,
        offset: (startIndex + index) * itemHeight,
        index: startIndex + index
      }))
})

// 计算总高度
const totalHeight = computed(() => {
  return props.applications.length * itemHeight
})

// 分页后的数据
const paginatedApplications = computed(() => {
  if (!props.showPagination) {
    return props.applications
  }

  const start = (currentPage.value - 1) * props.pageSize
  const end = start + props.pageSize
  return props.applications.slice(start, end)
})

// 处理滚动
const handleScroll = throttle((event) => {
  if (!props.enableVirtualScroll) return
  scrollTop.value = event.target.scrollTop
}, 16) // 60fps

// 处理分页变化
const handlePageChange = (page) => {
  currentPage.value = page
  emit('page-change', page)
}

const handlePageSizeChange = (size) => {
  props.pageSize = size
  currentPage.value = 1
  emit('size-change', size)
}

// 更新容器高度
const updateContainerHeight = () => {
  if (scrollContainer.value) {
    containerHeight.value = scrollContainer.value.clientHeight
  }
}

// 监听窗口大小变化
const handleResize = debounce(() => {
  updateContainerHeight()
}, 250)

onMounted(() => {
  updateContainerHeight()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})

// 监听应用数据变化
watch(() => props.applications, () => {
  currentPage.value = 1
}, { deep: true })
</script>

<style scoped>
.optimized-application-list {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.virtual-scroll-container {
  flex: 1;
  overflow-y: auto;
  position: relative;
}

.virtual-scroll-content {
  position: relative;
}

.virtual-item {
  position: absolute;
  left: 0;
  right: 0;
  box-sizing: border-box;
}

.loading-indicator {
  padding: 20px;
}

.empty-state {
  padding: 40px 0;
}

.pagination {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .virtual-item {
    padding: 8px;
  }

  .pagination {
    overflow-x: auto;
  }
}
</style>