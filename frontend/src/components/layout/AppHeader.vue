<template>
  <header class="app-header">
    <div class="header-left">
      <h1 v-if="projectStore.currentProject" class="project-title">
        {{ projectStore.currentProject.name }}
      </h1>
    </div>

    <div class="header-right">
      <div class="user-info">
        <span class="username">{{ userStore.displayName }}</span>
      </div>
      <button class="logout-btn" @click="handleLogout">Logout</button>
    </div>
  </header>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { useProjectStore } from '@/stores/project'

const router = useRouter()
const userStore = useUserStore()
const projectStore = useProjectStore()

function handleLogout() {
  userStore.logout()
  ElMessage.success('Logged out successfully')
  router.push('/login')
}
</script>

<style scoped lang="scss">
.app-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background-color: var(--cream, #FAF7F2);
  border-bottom: 1px solid var(--border-light, #e4e7ed);
  height: 64px;

  .header-left {
    .project-title {
      margin: 0;
      font-family: 'DM Serif Display', serif;
      font-size: 20px;
      font-weight: 700;
      color: var(--ink, #1C1410);
    }
  }

  .header-right {
    display: flex;
    align-items: center;
    gap: 16px;

    .user-info {
      display: flex;
      align-items: center;

      .username {
        font-size: 14px;
        color: var(--text-regular);
        font-family: 'JetBrains Mono', monospace;
      }
    }

    .logout-btn {
      padding: 8px 16px;
      background-color: var(--amber, #D4870A);
      color: var(--cream, #FAF7F2);
      border: none;
      border-radius: 4px;
      font-family: 'JetBrains Mono', monospace;
      font-size: 14px;
      cursor: pointer;
      transition: all 0.2s;

      &:hover {
        opacity: 0.9;
      }
    }
  }
}
</style>
