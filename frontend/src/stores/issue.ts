import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { issueApi } from '@/api/issue.api'
import type { Issue, CreateIssueForm, UpdateIssueForm, IssueBoardData } from '@/types/issue'

export const useIssueStore = defineStore('issue', () => {
  const issues = ref<Issue[]>([])
  const currentIssue = ref<Issue | null>(null)
  const boardData = ref<IssueBoardData | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  const issueCount = computed(() => issues.value.length)

  async function fetchIssues(projectId: number, filters?: { status?: string; priority?: string }) {
    loading.value = true
    error.value = null
    try {
      const response = await issueApi.list(projectId, { ...filters })
      issues.value = response.items
    } catch (err) {
      error.value = (err as Error).message
      throw err
    } finally {
      loading.value = false
    }
  }

  async function createIssue(data: CreateIssueForm & { projectId: number }) {
    loading.value = true
    error.value = null
    try {
      const newIssue = await issueApi.create(data)
      issues.value.push(newIssue)
      return newIssue
    } catch (err) {
      error.value = (err as Error).message
      throw err
    } finally {
      loading.value = false
    }
  }

  async function getIssue(id: number) {
    loading.value = true
    error.value = null
    try {
      const issue = await issueApi.get(id)
      currentIssue.value = issue
      return issue
    } catch (err) {
      error.value = (err as Error).message
      throw err
    } finally {
      loading.value = false
    }
  }

  async function updateIssue(id: number, data: UpdateIssueForm) {
    loading.value = true
    error.value = null
    try {
      const updated = await issueApi.update(id, data)
      const index = issues.value.findIndex((i) => i.id === id)
      if (index > -1) {
        issues.value[index] = updated
      }
      if (currentIssue.value?.id === id) {
        currentIssue.value = updated
      }
      return updated
    } catch (err) {
      error.value = (err as Error).message
      throw err
    } finally {
      loading.value = false
    }
  }

  async function deleteIssue(id: number) {
    loading.value = true
    error.value = null
    try {
      await issueApi.delete(id)
      issues.value = issues.value.filter((i) => i.id !== id)
      if (currentIssue.value?.id === id) {
        currentIssue.value = null
      }
    } catch (err) {
      error.value = (err as Error).message
      throw err
    } finally {
      loading.value = false
    }
  }

  async function fetchBoard(projectId: number) {
    loading.value = true
    error.value = null
    try {
      boardData.value = await issueApi.board(projectId)
    } catch (err) {
      error.value = (err as Error).message
      throw err
    } finally {
      loading.value = false
    }
  }

  return {
    issues,
    currentIssue,
    boardData,
    loading,
    error,
    issueCount,
    fetchIssues,
    createIssue,
    getIssue,
    updateIssue,
    deleteIssue,
    fetchBoard,
  }
})
