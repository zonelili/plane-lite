<template>
  <div class="comment-form">
    <div class="form-header">
      <h4>Add a comment</h4>
    </div>

    <div class="form-body">
      <textarea
        v-model="content"
        placeholder="Write your comment..."
        :maxlength="maxLength"
        rows="4"
        class="comment-textarea"
        @keydown.ctrl.enter="handleSubmit"
      ></textarea>

      <div class="form-footer">
        <span class="char-count" :class="{ 'near-limit': content.length > maxLength * 0.8 }">
          {{ content.length }} / {{ maxLength }}
        </span>

        <button
          class="btn-submit"
          :disabled="!isValid || loading"
          @click="handleSubmit"
        >
          <span v-if="loading" class="spinner">Posting...</span>
          <span v-else>Post Comment</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps<{
  loading: boolean
}>()

const emit = defineEmits<{
  submit: [content: string]
}>()

const content = ref('')
const maxLength = 2000

const isValid = computed(() => {
  return content.value.trim().length > 0 && content.value.length <= maxLength
})

async function handleSubmit() {
  if (!isValid.value || props.loading) return

  const trimmedContent = content.value.trim()
  emit('submit', trimmedContent)
  content.value = ''
}
</script>

<style scoped lang="scss">
.comment-form {
  margin-top: 24px;
  padding: 20px;
  background-color: white;
  border: 1px solid var(--border-light, #e4e7ed);
  border-radius: 8px;

  .form-header {
    margin-bottom: 16px;

    h4 {
      margin: 0;
      font-family: 'DM Serif Display', serif;
      font-size: 16px;
      font-weight: 400;
      color: var(--ink, #1C1410);
    }
  }

  .form-body {
    .comment-textarea {
      width: 100%;
      padding: 14px 16px;
      border: 1.5px solid transparent;
      border-radius: 3px;
      background-color: var(--field-bg, #EDE8E0);
      font-family: 'JetBrains Mono', monospace;
      font-size: 14px;
      line-height: 1.6;
      color: var(--ink, #1C1410);
      resize: vertical;
      min-height: 100px;
      transition: background-color 0.2s, border-color 0.2s;

      &::placeholder {
        color: #B8A898;
      }

      &:focus {
        outline: none;
        background-color: var(--field-focus, #E3D9CC);
        border-color: var(--amber, #D4870A);
      }
    }

    .form-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-top: 12px;

      .char-count {
        font-family: 'JetBrains Mono', monospace;
        font-size: 12px;
        color: var(--text-secondary, #999);

        &.near-limit {
          color: var(--amber, #D4870A);
        }
      }

      .btn-submit {
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

        &:hover:not(:disabled) {
          background-color: #C07809;
          border-color: #C07809;
          color: white;
          box-shadow: 0 4px 20px rgba(212, 135, 10, 0.35);
          transform: translateY(-1px);
        }

        &:active:not(:disabled) {
          background-color: #A86A08;
          border-color: #A86A08;
          color: white;
        }

        &:disabled {
          opacity: 0.5;
          cursor: not-allowed;
        }

        .spinner {
          display: inline-block;
        }
      }
    }
  }
}
</style>
