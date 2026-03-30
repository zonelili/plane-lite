<template>
  <div class="issue-detail-page">
    <div v-if="issueStore.loading" class="loading-state">
      <div class="spinner">Loading...</div>
    </div>

    <div v-else-if="issueStore.currentIssue" class="issue-content">
      <div class="issue-header">
        <RouterLink :to="`/projects/${currentProjectId}/issues`" class="back-link">← Back</RouterLink>
        <h1>{{ issueStore.currentIssue.number }}: {{ issueStore.currentIssue.title }}</h1>
      </div>

      <div class="issue-meta">
        <span class="status-badge" :class="`status-${issueStore.currentIssue.status}`">
          {{ formatStatus(issueStore.currentIssue.status) }}
        </span>
        <span class="priority-badge" :class="`priority-${issueStore.currentIssue.priority}`">
          {{ formatPriority(issueStore.currentIssue.priority) }}
        </span>
        <small>Created: {{ new Date(issueStore.currentIssue.createdAt).toLocaleString() }}</small>
      </div>

      <div class="issue-body">
        <div class="description">
          <h3>Description</h3>
          <p v-if="issueStore.currentIssue.description">{{ issueStore.currentIssue.description }}</p>
          <p v-else class="no-description">No description provided</p>
        </div>

        <div class="actions">
          <button class="btn-secondary" @click="showEditForm = true">Edit</button>
          <button class="btn-danger" @click="deleteIssue">Delete</button>
        </div>
      </div>

      <!-- Edit Form Modal -->
      <div v-if="showEditForm" class="modal-overlay" @click="showEditForm = false">
        <div class="modal" @click.stop>
          <h2>Edit Issue</h2>
          <IssueForm
            :initial-data="editFormData"
            @submit="handleUpdate"
            @cancel="showEditForm = false"
          />
        </div>
      </div>
    </div>

    <div v-else class="empty-state">
      <p>Issue not found</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter, RouterLink } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useIssueStore } from '@/stores/issue'
import { IssueStatus, IssuePriority } from '@/types/issue'
import type { CreateIssueForm } from '@/types/issue'
import IssueForm from '@/components/issue/IssueForm.vue'

const route = useRoute()
const router = useRouter()
const issueStore = useIssueStore()

const currentProjectId = computed(() => Number(route.params.id))
const issueId = computed(() => Number(route.params.issueId))
const showEditForm = ref(false)
const editFormData = reactive<Partial<CreateIssueForm>>({})

onMounted(async () => {
  if (issueId.value) {
    try {
      await issueStore.getIssue(issueId.value)
    } catch (err) {
      ElMessage.error('Failed to load issue')
    }
  }
})

function formatStatus(status: IssueStatus) {
  const map = {
    [IssueStatus.TODO]: 'To Do',
    [IssueStatus.IN_PROGRESS]: 'In Progress',
    [IssueStatus.DONE]: 'Done',
    [IssueStatus.CLOSED]: 'Closed',
  }
  return map[status] || status
}

function formatPriority(priority: IssuePriority) {
  const map = {
    [IssuePriority.NONE]: 'None',
    [IssuePriority.LOW]: 'Low',
    [IssuePriority.MEDIUM]: 'Medium',
    [IssuePriority.HIGH]: 'High',
    [IssuePriority.URGENT]: 'Urgent',
  }
  return map[priority] || priority
}

async function handleUpdate(data: CreateIssueForm) {
  try {
    if (issueStore.currentIssue) {
      await issueStore.updateIssue(issueStore.currentIssue.id, data)
      ElMessage.success('Issue updated')
      showEditForm.value = false
    }
  } catch (err) {
    ElMessage.error((err as Error).message || 'Failed to update issue')
  }
}

async function deleteIssue() {
  try {
    await ElMessageBox.confirm('Delete this issue?', 'Warning', {
      confirmButtonText: 'Delete',
      cancelButtonText: 'Cancel',
      type: 'warning',
    })
    if (issueStore.currentIssue) {
      await issueStore.deleteIssue(issueStore.currentIssue.id)
      ElMessage.success('Issue deleted')
      router.push(`/projects/${currentProjectId.value}/issues`)
    }
  } catch (err) {
    // User cancelled
  }
}
</script>

<style scoped lang="scss">
.issue-detail-page {
  max-width: 800px;

  .loading-state,
  .empty-state {
    text-align: center;
    padding: 60px 20px;
    color: var(--text-secondary);
  }

  .issue-content {
    .issue-header {
      margin-bottom: 20px;

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
        margin: 0;
        font-family: 'DM Serif Display', serif;
        font-size: 28px;
        font-weight: 700;
        color: var(--ink, #1C1410);
      }
    }

    .issue-meta {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 24px;
      padding-bottom: 12px;
      border-bottom: 1px solid var(--border-light, #e4e7ed);

      .status-badge,
      .priority-badge {
        display: inline-block;
        padding: 4px 8px;
        border-radius: 3px;
        font-size: 12px;
        font-weight: 600;

        &.status-todo {
          background-color: var(--border-lighter, #ebeef5);
          color: var(--text-regular);
        }

        &.status-in_progress {
          background-color: #e6f7ff;
          color: #0050b3;
        }

        &.status-done {
          background-color: #f6ffed;
          color: #274e1b;
        }

        &.status-closed {
          background-color: #fff1f0;
          color: #5c0a0a;
        }

        &.priority-urgent {
          background-color: #ffebe6;
          color: #ad2102;
        }

        &.priority-high {
          background-color: #fff7e6;
          color: #ad6800;
        }

        &.priority-medium {
          background-color: #fcf0f6;
          color: #780650;
        }

        &.priority-low {
          background-color: #f0f5ff;
          color: #003eb3;
        }

        &.priority-none {
          background-color: var(--border-lighter, #ebeef5);
          color: var(--text-secondary);
        }
      }

      small {
        margin-left: auto;
        color: var(--text-secondary);
        font-size: 12px;
      }
    }

    .issue-body {
      background-color: white;
      border: 1px solid var(--border-light, #e4e7ed);
      border-radius: 8px;
      padding: 20px;

      .description {
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

          &.no-description {
            color: var(--text-secondary);
            font-style: italic;
          }
        }
      }

      .actions {
        display: flex;
        gap: 12px;
        padding-top: 12px;
        border-top: 1px solid var(--border-lighter, #ebeef5);

        button {
          padding: 8px 16px;
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

          &.btn-danger {
            background-color: var(--danger-color, #f56c6c);
            color: white;

            &:hover {
              opacity: 0.9;
            }
          }
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
  }
}
</style>
