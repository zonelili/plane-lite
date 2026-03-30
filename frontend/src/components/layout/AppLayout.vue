<template>
  <div class="app-layout">
    <AppSidebar />
    <div class="main-area">
      <AppHeader />
      <div class="content-area">
        <RouterView />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import AppSidebar from '@/components/layout/AppSidebar.vue'
import AppHeader from '@/components/layout/AppHeader.vue'
</script>

<style scoped lang="scss">
.app-layout {
  display: flex;
  height: 100vh;
  background-color: var(--cream, #FAF7F2);

  .main-area {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;

    .content-area {
      flex: 1;
      overflow-y: auto;
      padding: 20px;
    }
  }
}
</style>
