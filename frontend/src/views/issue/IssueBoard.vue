<template>
  <div class="board-page">
    <div class="board-header">
      <div class="header-left">
        <RouterLink :to="`/projects/${projectId}/issues`" class="back-link">← List View</RouterLink>
        <h1>Board</h1>
      </div>
      <button class="btn-create" @click="showCreateModal = true">+ New Issue</button>
    </div>

    <div v-if="issueStore.loading" class="loading-state">
      <div class="spinner">Loading board...</div>
    </div>

    <div v-else class="board-columns">
      <div
        v-for="column in columns"
        :key="column.status"
        class="board-column"
      >
        <div class="column-header">
          <span class="column-title">{{ column.title }}</span>
          <span class="column-count">{{ getColumnCount(column.status) }}</span>
        </div>

        <div class="column-body">
          <div
            v-for="issue in getColumnIssues(column.status)"
            :key="issue.id"
            class="issue-card"
            @click="goToIssue(issue.id)"
          >
            <div class="card-header">
              <span class="issue-number">{{ issue.number }}</span>
              <span class="priority-badge" :class="`priority-${issue.priority}`">
                {{ formatPriority(issue.priority) }}
              </span>
            </div>
            <h3 class="card-title">{{ issue.title }}</h3>
            <div class="card-footer">
              <select
                class="status-select"
                :value="issue.status"
                @click.stop
                @change="handleStatusChange(issue.id, $event)"
              >
                <option value="todo">To Do</option>
                <option value="in_progress">In Progress</option>
                <option value="done">Done</option>
                <option value="closed">Closed</option>
              </select>
            </div>
          </div>

          <div v-if="getColumnIssues(column.status).length === 0" class="empty-column">
            <p>No issues</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Create Issue Modal -->
    <div v-if="showCreateModal" class="modal-overlay" @click="showCreateModal = false">
      <div class="modal" @click.stop>
        <h2>Create Issue</h2>
        <IssueForm
          @submit="handleCreate"
          @cancel="showCreateModal = false"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter, RouterLink } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useIssueStore } from '@/stores/issue'
import { IssueStatus, IssuePriority, type Issue } from '@/types/issue'
import type { CreateIssueForm } from '@/types/issue'
import IssueForm from '@/components/issue/IssueForm.vue'

const route = useRoute()
const router = useRouter()
const issueStore = useIssueStore()

const projectId = computed(() => Number(route.params.id))
const showCreateModal = ref(false)

const columns = [
  { status: IssueStatus.TODO, title: 'To Do' },
  { status: IssueStatus.IN_PROGRESS, title: 'In Progress' },
  { status: IssueStatus.DONE, title: 'Done' },
  { status: IssueStatus.CLOSED, title: 'Closed' },
]

onMounted(async () => {
  try {
    await issueStore.fetchBoard(projectId.value)
  } catch (err) {
    ElMessage.error('Failed to load board')
  }
})

function getColumnIssues(status: IssueStatus): Issue[] {
  if (!issueStore.boardData) return []

  const statusMap: Record<IssueStatus, keyof typeof issueStore.boardData> = {
    [IssueStatus.TODO]: 'todo',
    [IssueStatus.IN_PROGRESS]: 'inProgress',
    [IssueStatus.DONE]: 'done',
    [IssueStatus.CLOSED]: 'closed',
  }

  return issueStore.boardData[statusMap[status]] || []
}

function getColumnCount(status: IssueStatus): number {
  return getColumnIssues(status).length
}

function formatPriority(priority: string): string {
  const map: Record<string, string> = {
    'none': 'None',
    'low': 'Low',
    'medium': 'Med',
    'high': 'High',
    'urgent': 'Urgent',
  }
  return map[priority] || priority
}

function goToIssue(issueId: number) {
  router.push(`/projects/${projectId.value}/issues/${issueId}`)
}

async function handleStatusChange(issueId: number, event: Event) {
  const newStatus = (event.target as HTMLSelectElement).value as IssueStatus
  try {
    await issueStore.updateIssue(issueId, { status: newStatus })
    // Refresh board data
    await issueStore.fetchBoard(projectId.value)
    ElMessage.success('Status updated')
  } catch (err) {
    ElMessage.error((err as Error).message || 'Failed to update status')
  }
}

async function handleCreate(data: CreateIssueForm) {
  try {
    await issueStore.createIssue({
      ...data,
      projectId: projectId.value
    })
    showCreateModal.value = false
    // Refresh board data
    await issueStore.fetchBoard(projectId.value)
    ElMessage.success('Issue created')
  } catch (err) {
    ElMessage.error((err as Error).message || 'Failed to create issue')
  }
}
</script>

<style scoped lang="scss">
.board-page {
  height: 100%;
  display: flex;
  flex-direction: column;

  .board-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;

    .header-left {
      display: flex;
      align-items: center;
      gap: 16px;

      .back-link {
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
        font-weight: 400;
        color: var(--ink, #1C1410);
      }
    }

    .btn-create {
      padding: 10px 20px;
      background-color: rgba(212, 135, 10, 0.06);
      border: 2.5px solid var(--amber, #D4870A);
      border-radius: 3px;
      color: var(--amber, #D4870A);
      font-family: 'JetBrains Mono', monospace;
      font-size: 13px;
      font-weight: 600;
      letter-spacing: 0.08em;
      cursor: pointer;
      transition: all 0.2s;

      &:hover {
        background-color: #C07809;
        border-color: #C07809;
        color: white;
        box-shadow: 0 4px 20px rgba(212, 135, 10, 0.35);
        transform: translateY(-1px);
      }
    }
  }

  .loading-state {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    color: var(--text-secondary, #999);
    font-family: 'JetBrains Mono', monospace;
    font-size: 14px;
  }

  .board-columns {
    display: flex;
    gap: 20px;
    flex: 1;
    overflow-x: auto;
    padding-bottom: 20px;

    .board-column {
      flex: 1;
      min-width: 280px;
      max-width: 320px;
      background-color: var(--field-bg, #EDE8E0);
      border-radius: 8px;
      display: flex;
      flex-direction: column;

      .column-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 16px;
        border-bottom: 1px solid var(--border-light, #e4e7ed);

        .column-title {
          font-family: 'DM Serif Display', serif;
          font-size: 16px;
          font-weight: 400;
          color: var(--ink, #1C1410);
        }

        .column-count {
          display: inline-block;
          padding: 2px 8px;
          background-color: var(--cream, #FAF7F2);
          border-radius: 12px;
          font-family: 'JetBrains Mono', monospace;
          font-size: 12px;
          color: var(--text-secondary, #999);
        }
      }

      .column-body {
        flex: 1;
        padding: 12px;
        overflow-y: auto;
        display: flex;
        flex-direction: column;
        gap: 12px;

        .empty-column {
          text-align: center;
          padding: 40px 20px;
          color: var(--text-secondary, #999);
          font-family: 'JetBrains Mono', monospace;
          font-size: 13px;
        }
      }
    }
  }

  .issue-card {
    background-color: white;
    border: 1px solid var(--border-light, #e4e7ed);
    border-radius: 6px;
    padding: 14px;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      border-color: var(--amber, #D4870A);
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    }

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 8px;

      .issue-number {
        font-family: 'JetBrains Mono', monospace;
        font-size: 12px;
        font-weight: 600;
        color: var(--amber, #D4870A);
      }

      .priority-badge {
        padding: 2px 6px;
        border-radius: 3px;
        font-size: 10px;
        font-weight: 600;
        text-transform: uppercase;

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
          color: var(--text-secondary, #999);
        }
      }
    }

    .card-title {
      margin: 0 0 12px 0;
      font-family: 'JetBrains Mono', monospace;
      font-size: 14px;
      font-weight: 500;
      color: var(--ink, #1C1410);
      line-height: 1.4;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .card-footer {
      .status-select {
        width: 100%;
        padding: 6px 10px;
        border: 1px solid var(--border-light, #e4e7ed);
        border-radius: 4px;
        background-color: var(--cream, #FAF7F2);
        font-family: 'JetBrains Mono', monospace;
        font-size: 12px;
        color: var(--text-regular, #666);
        cursor: pointer;
        transition: border-color 0.2s;

        &:hover {
          border-color: var(--amber, #D4870A);
        }

        &:focus {
          outline: none;
          border-color: var(--amber, #D4870A);
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
