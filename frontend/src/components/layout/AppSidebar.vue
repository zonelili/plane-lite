<template>
  <aside class="app-sidebar">
    <div class="sidebar-header">
      <h1 class="logo-text">Plane</h1>
    </div>

    <div class="sidebar-content">
      <div class="section">
        <h3 class="section-title">Projects</h3>
        <nav class="nav-list">
          <RouterLink
            v-for="project in projectStore.projects"
            :key="project.id"
            :to="`/projects/${project.id}`"
            class="nav-item"
            :class="{ active: currentProjectId === project.id }"
          >
            {{ project.name }}
          </RouterLink>
        </nav>
      </div>

      <button class="new-project-btn" @click="showNewProjectForm = true">
        + New Project
      </button>
    </div>

    <div class="sidebar-footer">
      <RouterLink to="/settings" class="settings-link">⚙️ Settings</RouterLink>
    </div>

    <!-- New Project Form Modal -->
    <div v-if="showNewProjectForm" class="modal-overlay" @click="showNewProjectForm = false">
      <div class="modal" @click.stop>
        <h2>Create New Project</h2>
        <form @submit.prevent="handleCreateProject">
          <input
            v-model="newProjectForm.name"
            type="text"
            placeholder="Project name"
            class="modal-input"
            required
          />
          <input
            v-model="newProjectForm.identifier"
            type="text"
            placeholder="Project identifier (e.g., PL)"
            class="modal-input"
            required
          />
          <textarea
            v-model="newProjectForm.description"
            placeholder="Description (optional)"
            class="modal-textarea"
          />
          <div class="modal-actions">
            <button type="button" class="btn btn-secondary" @click="showNewProjectForm = false">
              Cancel
            </button>
            <button type="submit" class="btn btn-primary">Create</button>
          </div>
        </form>
      </div>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useProjectStore } from '@/stores/project'
import { useWorkspaceStore } from '@/stores/workspace'
import type { CreateProjectForm } from '@/types/project'

const route = useRoute()
const projectStore = useProjectStore()
const workspaceStore = useWorkspaceStore()

const showNewProjectForm = ref(false)
const newProjectForm = ref<CreateProjectForm>({
  name: '',
  identifier: '',
  description: '',
})

const currentProjectId = computed(() => {
  const id = route.params.id
  return id ? Number(id) : null
})

async function handleCreateProject() {
  if (!workspaceStore.currentWorkspaceId) return

  try {
    await projectStore.createProject({
      ...newProjectForm.value,
      workspaceId: workspaceStore.currentWorkspaceId,
    })
    showNewProjectForm.value = false
    newProjectForm.value = { name: '', identifier: '', description: '' }
  } catch (err) {
    const message = (err as Error).message || 'Failed to create project'
    console.error('Failed to create project:', err)
  }
}
</script>

<style scoped lang="scss">
.app-sidebar {
  width: 260px;
  background-color: var(--ink, #1C1410);
  color: var(--cream, #FAF7F2);
  display: flex;
  flex-direction: column;
  border-right: 1px solid rgba(212, 135, 10, 0.2);

  .sidebar-header {
    padding: 20px;
    border-bottom: 1px solid rgba(212, 135, 10, 0.2);

    .logo-text {
      font-family: 'DM Serif Display', serif;
      font-size: 24px;
      font-weight: 700;
      margin: 0;
      color: var(--amber, #D4870A);
    }
  }

  .sidebar-content {
    flex: 1;
    overflow-y: auto;
    padding: 20px 0;

    .section {
      margin-bottom: 20px;

      .section-title {
        padding: 0 20px;
        font-size: 12px;
        font-weight: 600;
        text-transform: uppercase;
        color: var(--text-secondary);
        margin: 0 0 10px 0;
        letter-spacing: 1px;
      }

      .nav-list {
        list-style: none;
        padding: 0;
        margin: 0;

        .nav-item {
          display: block;
          padding: 10px 20px;
          color: var(--cream, #FAF7F2);
          text-decoration: none;
          font-size: 14px;
          transition: all 0.2s;
          border-left: 3px solid transparent;

          &:hover {
            background-color: rgba(212, 135, 10, 0.1);
          }

          &.active {
            background-color: rgba(212, 135, 10, 0.2);
            border-left-color: var(--amber, #D4870A);
            color: var(--amber, #D4870A);
          }
        }
      }
    }

    .new-project-btn {
      width: calc(100% - 40px);
      margin: 0 20px;
      padding: 10px;
      background-color: rgba(212, 135, 10, 0.2);
      border: 1px solid var(--amber, #D4870A);
      color: var(--amber, #D4870A);
      border-radius: 4px;
      cursor: pointer;
      font-size: 14px;
      transition: all 0.2s;

      &:hover {
        background-color: rgba(212, 135, 10, 0.3);
      }
    }
  }

  .sidebar-footer {
    padding: 20px;
    border-top: 1px solid rgba(212, 135, 10, 0.2);

    .settings-link {
      display: block;
      color: var(--cream, #FAF7F2);
      text-decoration: none;
      font-size: 14px;
      transition: all 0.2s;

      &:hover {
        color: var(--amber, #D4870A);
      }
    }
  }
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;

  .modal {
    background-color: var(--cream, #FAF7F2);
    border-radius: 8px;
    padding: 24px;
    max-width: 400px;
    width: 90%;

    h2 {
      margin: 0 0 20px 0;
      color: var(--ink, #1C1410);
      font-size: 18px;
    }

    form {
      display: flex;
      flex-direction: column;
      gap: 12px;

      .modal-input,
      .modal-textarea {
        padding: 10px 12px;
        border: 1px solid var(--field-bg, #EDE8E0);
        background-color: var(--field-bg, #EDE8E0);
        border-radius: 4px;
        font-family: 'JetBrains Mono', monospace;
        color: var(--ink, #1C1410);
        font-size: 14px;

        &:focus {
          outline: none;
          background-color: var(--field-focus, #E3D9CC);
          border-color: var(--amber, #D4870A);
        }

        &::placeholder {
          color: var(--text-secondary);
        }
      }

      .modal-textarea {
        min-height: 80px;
        resize: vertical;
      }
    }

    .modal-actions {
      display: flex;
      gap: 12px;
      margin-top: 20px;

      .btn {
        flex: 1;
        padding: 10px;
        border: none;
        border-radius: 4px;
        font-family: 'JetBrains Mono', monospace;
        cursor: pointer;
        transition: all 0.2s;

        &.btn-secondary {
          background-color: var(--field-bg, #EDE8E0);
          color: var(--ink, #1C1410);

          &:hover {
            background-color: var(--field-focus, #E3D9CC);
          }
        }

        &.btn-primary {
          background-color: var(--amber, #D4870A);
          color: var(--cream, #FAF7F2);

          &:hover {
            opacity: 0.9;
          }
        }
      }
    }
  }
}
</style>
