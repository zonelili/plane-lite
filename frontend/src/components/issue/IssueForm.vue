<template>
  <div class="issue-form">
    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label>Title</label>
        <input
          v-model="form.title"
          type="text"
          placeholder="Issue title"
          required
        />
        <span v-if="errors.title" class="error">{{ errors.title }}</span>
      </div>

      <div class="form-group">
        <label>Description</label>
        <textarea
          v-model="form.description"
          placeholder="Describe the issue..."
        />
      </div>

      <div class="form-row">
        <div class="form-group">
          <label>Status</label>
          <select v-model="form.status">
            <option value="todo">To Do</option>
            <option value="in_progress">In Progress</option>
            <option value="done">Done</option>
            <option value="closed">Closed</option>
          </select>
        </div>

        <div class="form-group">
          <label>Priority</label>
          <select v-model="form.priority">
            <option value="none">None</option>
            <option value="low">Low</option>
            <option value="medium">Medium</option>
            <option value="high">High</option>
            <option value="urgent">Urgent</option>
          </select>
        </div>
      </div>

      <div class="form-actions">
        <button type="button" class="btn-secondary" @click="$emit('cancel')">Cancel</button>
        <button type="submit" class="btn-primary">{{ isEditing ? 'Update' : 'Create' }}</button>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { reactive, computed } from 'vue'
import type { CreateIssueForm } from '@/types/issue'

const props = withDefaults(
  defineProps<{
    initialData?: Partial<CreateIssueForm>
  }>(),
  { initialData: () => ({}) }
)

const emit = defineEmits<{
  submit: [data: CreateIssueForm]
  cancel: []
}>()

const isEditing = computed(() => {
  // 更准确：检查是否有多个属性被填充（不仅仅是 title）
  return !!(props.initialData && Object.keys(props.initialData).length > 1)
})

const form = reactive<CreateIssueForm>({
  title: props.initialData?.title || '',
  description: props.initialData?.description || '',
  status: props.initialData?.status || 'todo',
  priority: props.initialData?.priority || 'none',
  assigneeId: props.initialData?.assigneeId,
})

const errors = reactive({ title: '' })

function validateForm() {
  errors.title = ''
  if (!form.title.trim()) {
    errors.title = 'Title is required'
  }
  return !errors.title
}

function handleSubmit() {
  if (!validateForm()) return
  emit('submit', form)
}
</script>

<style scoped lang="scss">
.issue-form {
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
      select,
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

      .error {
        color: var(--danger-color, #f56c6c);
        font-size: 12px;
      }
    }

    .form-row {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 16px;
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

          &:hover {
            opacity: 0.9;
          }
        }
      }
    }
  }
}
</style>
