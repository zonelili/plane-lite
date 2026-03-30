<template>
  <div id="app">
    <template v-if="isAuthPage">
      <RouterView />
    </template>
    <template v-else>
      <AppLayout />
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import AppLayout from '@/components/layout/AppLayout.vue'

const route = useRoute()

const isAuthPage = computed(() => {
  return route.path === '/login' || route.path === '/register'
})
</script>

<style>
#app {
  font-family: 'JetBrains Mono', 'Avenir', 'Helvetica', 'Arial', sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  background-color: var(--cream, #FAF7F2);
  color: var(--ink, #1C1410);
}

:root {
  --cream: #FAF7F2;
  --ink: #1C1410;
  --amber: #D4870A;
  --field-bg: #EDE8E0;
  --field-focus: #E3D9CC;
  --text-secondary: #999;
  --text-regular: #666;
  --border-light: #e4e7ed;
  --border-lighter: #ebeef5;
  --border-extra-light: #f2f6fc;
  --danger-color: #f56c6c;
}

body {
  margin: 0;
  padding: 0;
}

* {
  box-sizing: border-box;
}
</style>

