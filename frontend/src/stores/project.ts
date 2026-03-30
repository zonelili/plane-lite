import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { projectApi } from '@/api/project.api'
import type { Project, CreateProjectForm, UpdateProjectForm } from '@/types/project'

export const useProjectStore = defineStore('project', () => {
  const projects = ref<Project[]>([])
  const currentProject = ref<Project | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  const projectCount = computed(() => projects.value.length)

  async function fetchProjects(workspaceId: number) {
    loading.value = true
    error.value = null
    try {
      const response = await projectApi.list(workspaceId)
      projects.value = response.items
    } catch (err) {
      error.value = (err as Error).message
      throw err
    } finally {
      loading.value = false
    }
  }

  async function createProject(data: CreateProjectForm & { workspaceId: number }) {
    loading.value = true
    error.value = null
    try {
      const newProject = await projectApi.create(data)
      projects.value.push(newProject)
      return newProject
    } catch (err) {
      error.value = (err as Error).message
      throw err
    } finally {
      loading.value = false
    }
  }

  async function getProject(id: number) {
    loading.value = true
    error.value = null
    try {
      const project = await projectApi.get(id)
      currentProject.value = project
      return project
    } catch (err) {
      error.value = (err as Error).message
      throw err
    } finally {
      loading.value = false
    }
  }

  async function updateProject(id: number, data: UpdateProjectForm) {
    loading.value = true
    error.value = null
    try {
      const updated = await projectApi.update(id, data)
      const index = projects.value.findIndex((p) => p.id === id)
      if (index > -1) {
        projects.value[index] = updated
      }
      if (currentProject.value?.id === id) {
        currentProject.value = updated
      }
      return updated
    } catch (err) {
      error.value = (err as Error).message
      throw err
    } finally {
      loading.value = false
    }
  }

  async function deleteProject(id: number) {
    loading.value = true
    error.value = null
    try {
      await projectApi.delete(id)
      projects.value = projects.value.filter((p) => p.id !== id)
      if (currentProject.value?.id === id) {
        currentProject.value = null
      }
    } catch (err) {
      error.value = (err as Error).message
      throw err
    } finally {
      loading.value = false
    }
  }

  return {
    projects,
    currentProject,
    loading,
    error,
    projectCount,
    fetchProjects,
    createProject,
    getProject,
    updateProject,
    deleteProject,
  }
})
