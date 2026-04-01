<template>
  <div class="comment-list">
    <div class="comment-header">
      <h3>Comments</h3>
      <span class="comment-count">{{ comments.length }}</span>
    </div>

    <div v-if="loading" class="loading-state">
      <div class="spinner">Loading...</div>
    </div>

    <div v-else-if="comments.length === 0" class="empty-state">
      <p>No comments yet. Be the first to comment!</p>
    </div>

    <div v-else class="comments">
      <div
        v-for="comment in comments"
        :key="comment.id"
        class="comment-item"
      >
        <div class="comment-avatar">
          {{ getInitial(comment.userId) }}
        </div>
        <div class="comment-content">
          <div class="comment-meta">
            <span class="comment-author">User #{{ comment.userId }}</span>
            <span class="comment-time">{{ formatTime(comment.createdAt) }}</span>
          </div>
          <p class="comment-text">{{ comment.content }}</p>
        </div>
        <button
          v-if="canDelete(comment)"
          class="btn-delete"
          @click="handleDelete(comment.id)"
          title="Delete comment"
        >
          ×
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ElMessageBox } from 'element-plus'
import type { Comment } from '@/types/comment'
import { useUserStore } from '@/stores/user'

const props = defineProps<{
  comments: Comment[]
  loading: boolean
}>()

const emit = defineEmits<{
  delete: [id: number]
}>()

const userStore = useUserStore()

function getInitial(userId: number): string {
  return `U${userId}`
}

function formatTime(dateString: string): string {
  const date = new Date(dateString)
  if (isNaN(date.getTime())) return 'Unknown'

  const now = new Date()
  const diff = now.getTime() - date.getTime()

  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return 'Just now'
  if (minutes < 60) return `${minutes}m ago`
  if (hours < 24) return `${hours}h ago`
  if (days < 7) return `${days}d ago`
  return date.toLocaleDateString()
}

function canDelete(comment: Comment): boolean {
  // Only the comment author can delete
  return userStore.user?.id === comment.userId
}

async function handleDelete(id: number) {
  try {
    await ElMessageBox.confirm('Delete this comment?', 'Confirm', {
      confirmButtonText: 'Delete',
      cancelButtonText: 'Cancel',
      type: 'warning',
    })
    emit('delete', id)
  } catch {
    // User cancelled
  }
}
</script>

<style scoped lang="scss">
.comment-list {
  margin-top: 24px;

  .comment-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid var(--border-light, #e4e7ed);

    h3 {
      margin: 0;
      font-family: 'DM Serif Display', serif;
      font-size: 18px;
      font-weight: 400;
      color: var(--ink, #1C1410);
    }

    .comment-count {
      display: inline-block;
      padding: 2px 8px;
      background-color: var(--field-bg, #EDE8E0);
      border-radius: 12px;
      font-family: 'JetBrains Mono', monospace;
      font-size: 12px;
      color: var(--text-secondary, #999);
    }
  }

  .loading-state,
  .empty-state {
    text-align: center;
    padding: 40px 20px;
    color: var(--text-secondary, #999);
    font-family: 'JetBrains Mono', monospace;
    font-size: 14px;
  }

  .comments {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  .comment-item {
    display: flex;
    gap: 12px;
    padding: 16px;
    background-color: white;
    border: 1px solid var(--border-light, #e4e7ed);
    border-radius: 8px;
    transition: border-color 0.2s;

    &:hover {
      border-color: var(--amber, #D4870A);

      .btn-delete {
        opacity: 1;
      }
    }

    .comment-avatar {
      width: 36px;
      height: 36px;
      border-radius: 50%;
      background-color: var(--amber, #D4870A);
      color: white;
      display: flex;
      align-items: center;
      justify-content: center;
      font-family: 'JetBrains Mono', monospace;
      font-size: 12px;
      font-weight: 600;
      flex-shrink: 0;
    }

    .comment-content {
      flex: 1;
      min-width: 0;

      .comment-meta {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 8px;

        .comment-author {
          font-family: 'JetBrains Mono', monospace;
          font-size: 13px;
          font-weight: 600;
          color: var(--ink, #1C1410);
        }

        .comment-time {
          font-family: 'JetBrains Mono', monospace;
          font-size: 11px;
          color: var(--text-secondary, #999);
        }
      }

      .comment-text {
        margin: 0;
        font-family: 'JetBrains Mono', monospace;
        font-size: 14px;
        line-height: 1.6;
        color: var(--text-regular, #666);
        word-wrap: break-word;
        white-space: pre-wrap;
      }
    }

    .btn-delete {
      opacity: 0;
      width: 24px;
      height: 24px;
      border: none;
      background-color: transparent;
      color: var(--text-secondary, #999);
      font-size: 18px;
      cursor: pointer;
      border-radius: 4px;
      transition: all 0.2s;
      display: flex;
      align-items: center;
      justify-content: center;

      &:hover {
        background-color: var(--danger-color, #f56c6c);
        color: white;
      }
    }
  }
}
</style>
