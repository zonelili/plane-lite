<template>
  <div class="issue-list-page">
    <div class="page-header">
      <h1>Issues</h1>
      <button class="btn-primary" @click="showCreateForm = true">+ New Issue</button>
    </div>

    <div class="filters">
      <select v-model="filterStatus" class="filter-select">
        <option value="">All Status</option>
        <option value="todo">To Do</option>
        <option value="in_progress">In Progress</option>
        <option value="done">Done</option>
        <option value="closed">Closed</option>
      </select>
      <select v-model="filterPriority" class="filter-select">
        <option value="">All Priority</option>
        <option value="none">None</option>
        <option value="low">Low</option>
        <option value="medium">Medium</option>
        <option value="high">High</option>
        <option value="urgent">Urgent</option>
      </select>
    </div>

    <div v-if="issueStore.loading" class="loading-state">
      <div class="spinner">Loading...</div>
    </div>

    <div v-else-if="filteredIssues.length === 0" class="empty-state">
      <p>No issues found. Create one to get started!</p>
    </div>

    <div v-else class="issues-table">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Title</th>
            <th>Status</th>
            <th>Priority</th>
            <th>Created</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="issue in filteredIssues" :key="issue.id" class="issue-row">
            <td class="issue-id">{{ issue.number }}</td>
            <td class="issue-title">
              <RouterLink :to="`/projects/${currentProjectId}/issues/${issue.id}`">
                {{ issue.title }}
              </RouterLink>
            </td>
            <td>
              <span class="status-badge" :class="`status-${issue.status}`">
                {{ formatStatus(issue.status) }}
              </span>
            </td>
            <td>
              <span class="priority-badge" :class="`priority-${issue.priority}`">
                {{ formatPriority(issue.priority) }}
              </span>
            </td>
            <td class="date">{{ new Date(issue.createdAt).toLocaleDateString() }}</td>
            <td class="actions">
              <button class="btn-icon" @click="editIssue(issue)" title="Edit">✎</button>
              <button class="btn-icon" @click="deleteIssue(issue.id)" title="Delete">✕</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Create/Edit Issue Form -->
    <div v-if="showCreateForm" class="modal-overlay" @click="closeForm">
      <div class="modal" @click.stop>
        <h2>{{ editingIssue ? 'Edit Issue' : 'Create New Issue' }}</h2>
        <IssueForm :initial-data="formData" @submit="handleSubmit" @cancel="closeForm" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useIssueStore } from '@/stores/issue'
import { IssueStatus, IssuePriority } from '@/types/issue'
import type { Issue, CreateIssueForm } from '@/types/issue'
import IssueForm from '@/components/issue/IssueForm.vue'

const route = useRoute()
const issueStore = useIssueStore()

const currentProjectId = computed(() => Number(route.params.id))
const showCreateForm = ref(false)
const editingIssue = ref<Issue | null>(null)
const filterStatus = ref('')
const filterPriority = ref('')
const formData = reactive<Partial<CreateIssueForm>>({})

const filteredIssues = computed(() => {
  let issues = issueStore.issues
  if (filterStatus.value) {
    issues = issues.filter((i) => i.status === filterStatus.value)
  }
  if (filterPriority.value) {
    issues = issues.filter((i) => i.priority === filterPriority.value)
  }
  return issues
})

onMounted(async () => {
  if (currentProjectId.value) {
    try {
      await issueStore.fetchIssues(currentProjectId.value)
    } catch (err) {
      ElMessage.error('Failed to load issues')
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

function editIssue(issue: Issue) {
  editingIssue.value = issue
  Object.assign(formData, {
    title: issue.title,
    description: issue.description,
    status: issue.status,
    priority: issue.priority,
  })
  showCreateForm.value = true
}

async function handleSubmit(data: CreateIssueForm) {
  try {
    if (editingIssue.value) {
      await issueStore.updateIssue(editingIssue.value.id, data)
      ElMessage.success('Issue updated')
    } else {
      await issueStore.createIssue({
        ...data,
        projectId: currentProjectId.value,
      })
      ElMessage.success('Issue created')
    }
    closeForm()
  } catch (err) {
    ElMessage.error((err as Error).message || 'Failed to save issue')
  }
}

function closeForm() {
  showCreateForm.value = false
  editingIssue.value = null
  // 清空表单数据而不使用 any
  Object.assign(formData, {
    title: '',
    description: '',
    status: undefined,
    priority: undefined,
    assigneeId: undefined,
  } as Partial<CreateIssueForm>)
}

async function deleteIssue(id: number) {
  try {
    await ElMessageBox.confirm('Delete this issue?', 'Warning', {
      confirmButtonText: 'Delete',
      cancelButtonText: 'Cancel',
      type: 'warning',
    })
    await issueStore.deleteIssue(id)
    ElMessage.success('Issue deleted')
  } catch (err) {
    // User cancelled
  }
}
</script>

<style scoped lang="scss">
.issue-list-page {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    h1 {
      margin: 0;
      font-family: 'DM Serif Display', serif;
      font-size: 28px;
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

      &:hover {
        opacity: 0.9;
      }
    }
  }

  .filters {
    display: flex;
    gap: 12px;
    margin-bottom: 20px;

    .filter-select {
      padding: 8px 12px;
      border: 1px solid var(--border-light, #e4e7ed);
      background-color: white;
      border-radius: 4px;
      font-family: 'JetBrains Mono', monospace;
      font-size: 14px;
      cursor: pointer;

      &:focus {
        outline: none;
        border-color: var(--amber, #D4870A);
      }
    }
  }

  .loading-state,
  .empty-state {
    text-align: center;
    padding: 40px 20px;
    color: var(--text-secondary);
  }

  .issues-table {
    background-color: white;
    border: 1px solid var(--border-light, #e4e7ed);
    border-radius: 8px;
    overflow: hidden;

    table {
      width: 100%;
      border-collapse: collapse;

      thead {
        background-color: var(--border-lighter, #ebeef5);
      }

      th {
        padding: 12px;
        text-align: left;
        font-weight: 600;
        color: var(--ink, #1C1410);
        font-family: 'JetBrains Mono', monospace;
        font-size: 12px;
        text-transform: uppercase;
        border-bottom: 1px solid var(--border-light, #e4e7ed);
      }

      tbody {
        tr {
          border-bottom: 1px solid var(--border-lighter, #ebeef5);

          &:hover {
            background-color: var(--border-extra-light, #f2f6fc);
          }

          td {
            padding: 12px;
            color: var(--text-regular);
            font-size: 14px;

            &.issue-id {
              font-family: 'JetBrains Mono', monospace;
              font-weight: 600;
              color: var(--amber, #D4870A);
            }

            &.issue-title a {
              color: var(--ink, #1C1410);
              text-decoration: none;

              &:hover {
                color: var(--amber, #D4870A);
              }
            }

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

            &.date {
              color: var(--text-secondary);
              font-size: 12px;
            }

            &.actions {
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
