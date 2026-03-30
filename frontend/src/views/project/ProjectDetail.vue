<template>
  <div class="project-detail-page">
    <div v-if="projectStore.loading" class="loading-state">
      <div class="spinner">Loading...</div>
    </div>

    <div v-else-if="projectStore.currentProject" class="project-content">
      <div class="project-header">
        <RouterLink to="/projects" class="back-link">← Back to Projects</RouterLink>
        <h1>{{ projectStore.currentProject.name }}</h1>
        <p class="identifier">{{ projectStore.currentProject.identifier }}</p>
      </div>

      <div v-if="projectStore.currentProject.description" class="project-description">
        <h3>About</h3>
        <p>{{ projectStore.currentProject.description }}</p>
      </div>

      <div class="project-issues">
        <h3>Issues</h3>
        <RouterLink
          :to="`/projects/${projectStore.currentProject.id}/issues`"
          class="view-issues-btn"
        >
          View all issues →
        </RouterLink>
      </div>
    </div>

    <div v-else class="empty-state">
      <p>Project not found</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useProjectStore } from '@/stores/project'

const route = useRoute()
const projectStore = useProjectStore()

const projectId = computed(() => Number(route.params.id))

onMounted(async () => {
  if (projectId.value) {
    try {
      await projectStore.getProject(projectId.value)
    } catch (err) {
      ElMessage.error('Failed to load project')
    }
  }
})
</script>

<style scoped lang="scss">
.project-detail-page {
  max-width: 800px;

  .loading-state,
  .empty-state {
    text-align: center;
    padding: 60px 20px;
    color: var(--text-secondary);
  }

  .project-content {
    .project-header {
      margin-bottom: 30px;

      .back-link {
        display: inline-block;
        margin-bottom: 12px;
        color: var(--amber, #D4870A);
        text-decoration: none;
        font-family: 'JetBrains Mono', monospace;
        font-size: 14px;

        &:hover {
          text-decoration: underline;
        }
      }

      h1 {
        margin: 0 0 8px 0;
        font-family: 'DM Serif Display', serif;
        font-size: 32px;
        font-weight: 700;
        color: var(--ink, #1C1410);
      }

      .identifier {
        margin: 0;
        font-family: 'JetBrains Mono', monospace;
        font-size: 14px;
        color: var(--amber, #D4870A);
        text-transform: uppercase;
      }
    }

    .project-description {
      background-color: white;
      border: 1px solid var(--border-light, #e4e7ed);
      border-radius: 8px;
      padding: 20px;
      margin-bottom: 20px;

      h3 {
        margin: 0 0 12px 0;
        font-family: 'DM Serif Display', serif;
        font-size: 16px;
        color: var(--ink, #1C1410);
      }

      p {
        margin: 0;
        color: var(--text-regular);
        line-height: 1.6;
      }
    }

    .project-issues {
      background-color: white;
      border: 1px solid var(--border-light, #e4e7ed);
      border-radius: 8px;
      padding: 20px;

      h3 {
        margin: 0 0 12px 0;
        font-family: 'DM Serif Display', serif;
        font-size: 16px;
        color: var(--ink, #1C1410);
      }

      .view-issues-btn {
        display: inline-block;
        color: var(--amber, #D4870A);
        text-decoration: none;
        font-family: 'JetBrains Mono', monospace;
        font-size: 14px;
        transition: all 0.2s;

        &:hover {
          color: var(--ink, #1C1410);
        }
      }
    }
  }
}
</style>
