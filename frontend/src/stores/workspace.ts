import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { workspaceApi } from '@/api/workspace.api'
import type { Workspace } from '@/api/workspace.api'

export const useWorkspaceStore = defineStore('workspace', () => {
  const workspaces = ref<Workspace[]>([])
  const currentWorkspaceId = ref<number | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  const currentWorkspace = computed(() => {
    if (!currentWorkspaceId.value) return null
    return workspaces.value.find((w) => w.id === currentWorkspaceId.value) || null
  })

  async function fetchWorkspaces() {
    loading.value = true
    error.value = null
    try {
      workspaces.value = await workspaceApi.list()
      if (workspaces.value.length > 0 && !currentWorkspaceId.value) {
        currentWorkspaceId.value = workspaces.value[0].id
      }
    } catch (err) {
      error.value = (err as Error).message
      throw err
    } finally {
      loading.value = false
    }
  }

  function setCurrentWorkspace(workspaceId: number) {
    currentWorkspaceId.value = workspaceId
  }

  return {
    workspaces,
    currentWorkspaceId,
    currentWorkspace,
    loading,
    error,
    fetchWorkspaces,
    setCurrentWorkspace,
  }
})
