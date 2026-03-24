<template>
  <div class="project-list">
    <h1>项目列表</h1>
    <p>此页面将在 Day 6 实现</p>
    <el-button @click="handleLogout">退出登录</el-button>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

function handleLogout() {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.project-list {
  padding: 20px;
}
</style>
