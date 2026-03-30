<template>
  <div class="project-list-page">
    <div class="page-header">
      <h1>Projects</h1>
      <button class="btn-primary" @click="showCreateForm = true">+ New Project</button>
    </div>

    <div v-if="projectStore.loading" class="loading-state">
      <div class="spinner">Loading...</div>
    </div>

    <div v-else-if="projectStore.projects.length === 0" class="empty-state">
      <p>No projects yet. Create one to get started!</p>
    </div>

    <div v-else class="projects-grid">
      <div v-for="project in projectStore.projects" :key="project.id" class="project-card">
        <div class="card-header">
          <RouterLink :to="`/projects/${project.id}`" class="project-link">
            <h2>{{ project.name }}</h2>
            <span class="identifier">{{ project.identifier }}</span>
          </RouterLink>
          <div class="card-actions">
            <button class="btn-icon" @click="editProject(project)" title="Edit">✎</button>
            <button class="btn-icon" @click="deleteProject(project.id)" title="Delete">✕</button>
          </div>
        </div>
        <p v-if="project.description" class="description">{{ project.description }}</p>
        <div class="card-footer">
          <small>{{ new Date(project.createdAt).toLocaleDateString() }}</small>
        </div>
      </div>
    </div>

    <!-- Create/Edit Project Form Modal -->
    <div v-if="showCreateForm" class="modal-overlay" @click="closeForm">
      <div class="modal" @click.stop>
        <h2>{{ editingProject ? 'Edit Project' : 'Create New Project' }}</h2>
        <form @submit.prevent="handleSubmit">
          <div class="form-group">
            <label>Project Name</label>
            <input
              v-model="formData.name"
              type="text"
              placeholder="e.g., Product Roadmap"
              required
            />
            <span v-if="errors.name" class="error">{{ errors.name }}</span>
          </div>

          <div v-if="!editingProject" class="form-group">
            <label>Project Identifier</label>
            <input
              v-model="formData.identifier"
              type="text"
              placeholder="e.g., PROD"
              maxlength="10"
              required
            />
            <span v-if="errors.identifier" class="error">{{ errors.identifier }}</span>
            <small>Used for issue numbering (e.g., PROD-1)</small>
          </div>

          <div class="form-group">
            <label>Description (Optional)</label>
            <textarea v-model="formData.description" placeholder="Describe your project..." />
          </div>

          <div class="form-actions">
            <button type="button" class="btn-secondary" @click="closeForm">Cancel</button>
            <button type="submit" class="btn-primary" :disabled="loading">
              {{ loading ? 'Saving...' : editingProject ? 'Update' : 'Create' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useProjectStore } from '@/stores/project'
import { useWorkspaceStore } from '@/stores/workspace'
import type { CreateProjectForm, UpdateProjectForm, Project } from '@/types/project'

const projectStore = useProjectStore()
const workspaceStore = useWorkspaceStore()

const showCreateForm = ref(false)
const editingProject = ref<Project | null>(null)
const loading = ref(false)
const formData = reactive<CreateProjectForm & { workspaceId?: number }>({
  name: '',
  identifier: '',
  description: '',
})
const errors = reactive({ name: '', identifier: '' })

onMounted(async () => {
  try {
    if (!workspaceStore.currentWorkspaceId) {
      await workspaceStore.fetchWorkspaces()
    }
    if (workspaceStore.currentWorkspaceId) {
      await projectStore.fetchProjects(workspaceStore.currentWorkspaceId)
    }
  } catch (err) {
    ElMessage.error('Failed to load projects')
  }
})

function editProject(project: Project) {
  editingProject.value = project
  formData.name = project.name
  formData.description = project.description || ''
  showCreateForm.value = true
}

function validateForm() {
  errors.name = ''
  errors.identifier = ''

  if (!formData.name.trim()) {
    errors.name = 'Project name is required'
  }
  if (!editingProject.value && !formData.identifier.trim()) {
    errors.identifier = 'Project identifier is required'
  }

  return !errors.name && !errors.identifier
}

async function handleSubmit() {
  if (!validateForm()) return

  loading.value = true
  try {
    if (editingProject.value) {
      await projectStore.updateProject(editingProject.value.id, {
        name: formData.name,
        description: formData.description || null,
      })
      ElMessage.success('Project updated')
    } else {
      if (!workspaceStore.currentWorkspaceId) {
        throw new Error('No workspace selected')
      }
      await projectStore.createProject({
        name: formData.name,
        identifier: formData.identifier,
        description: formData.description,
        workspaceId: workspaceStore.currentWorkspaceId,
      })
      ElMessage.success('Project created')
    }
    closeForm()
  } catch (err) {
    ElMessage.error((err as Error).message || 'Failed to save project')
  } finally {
    loading.value = false
  }
}

function closeForm() {
  showCreateForm.value = false
  editingProject.value = null
  formData.name = ''
  formData.identifier = ''
  formData.description = ''
  errors.name = ''
  errors.identifier = ''
}

async function deleteProject(id: number) {
  try {
    await ElMessageBox.confirm(
      'This action will delete the project and all its issues. Continue?',
      'Warning',
      { confirmButtonText: 'Delete', cancelButtonText: 'Cancel', type: 'warning' }
    )
    await projectStore.deleteProject(id)
    ElMessage.success('Project deleted')
  } catch (err) {
    // User cancelled
  }
}
</script>

<style scoped lang="scss">
.project-list-page {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30px;

    h1 {
      margin: 0;
      font-family: 'DM Serif Display', serif;
      font-size: 28px;
      font-weight: 700;
      color: var(--ink, #1C1410);
    }

    .btn-primary {
      padding: 10px 20px;
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

  .loading-state {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 300px;

    .spinner {
      font-size: 16px;
      color: var(--text-secondary);
    }
  }

  .empty-state {
    text-align: center;
    padding: 60px 20px;
    color: var(--text-secondary);
    font-size: 16px;
  }

  .projects-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 20px;

    .project-card {
      background-color: white;
      border: 1px solid var(--border-light, #e4e7ed);
      border-radius: 8px;
      padding: 20px;
      transition: all 0.2s;

      &:hover {
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        border-color: var(--amber, #D4870A);
      }

      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 12px;

        .project-link {
          flex: 1;
          text-decoration: none;
          color: inherit;

          h2 {
            margin: 0 0 4px 0;
            font-family: 'DM Serif Display', serif;
            font-size: 18px;
            font-weight: 700;
            color: var(--ink, #1C1410);
          }

          .identifier {
            font-family: 'JetBrains Mono', monospace;
            font-size: 12px;
            color: var(--text-secondary);
            text-transform: uppercase;
          }

          &:hover h2 {
            color: var(--amber, #D4870A);
          }
        }

        .card-actions {
          display: flex;
          gap: 8px;

          .btn-icon {
            padding: 6px 10px;
            background-color: transparent;
            border: 1px solid var(--border-light, #e4e7ed);
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
            transition: all 0.2s;

            &:hover {
              background-color: var(--border-light, #e4e7ed);
              border-color: var(--amber, #D4870A);
            }
          }
        }
      }

      .description {
        margin: 0 0 12px 0;
        color: var(--text-regular);
        font-size: 14px;
        line-height: 1.5;
      }

      .card-footer {
        border-top: 1px solid var(--border-lighter, #ebeef5);
        padding-top: 12px;

        small {
          color: var(--text-secondary);
          font-size: 12px;
        }
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
    max-width: 500px;
    width: 90%;

    h2 {
      margin: 0 0 20px 0;
      color: var(--ink, #1C1410);
      font-family: 'DM Serif Display', serif;
      font-size: 20px;
    }

    form {
      display: flex;
      flex-direction: column;
      gap: 16px;

      .form-group {
        display: flex;
        flex-direction: column;
        gap: 6px;

        label {
          font-weight: 600;
          color: var(--ink, #1C1410);
          font-size: 14px;
        }

        input,
        textarea {
          padding: 10px 12px;
          border: 1px solid var(--border-light, #e4e7ed);
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

        textarea {
          min-height: 100px;
          resize: vertical;
        }

        small {
          color: var(--text-secondary);
          font-size: 12px;
        }

        .error {
          color: var(--danger-color, #f56c6c);
          font-size: 12px;
        }
      }

      .form-actions {
        display: flex;
        gap: 12px;
        margin-top: 20px;

        button {
          flex: 1;
          padding: 10px;
          border: none;
          border-radius: 4px;
          font-family: 'JetBrains Mono', monospace;
          font-size: 14px;
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

            &:hover:not(:disabled) {
              opacity: 0.9;
            }

            &:disabled {
              opacity: 0.6;
              cursor: not-allowed;
            }
          }
        }
      }
    }
  }
}
</style>
