import { defineStore } from 'pinia'
import { ref } from 'vue'
import { commentApi } from '@/api/comment.api'
import type { Comment, CreateCommentForm } from '@/types/comment'

export const useCommentStore = defineStore('comment', () => {
  const comments = ref<Comment[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  /**
   * 获取问题的评论列表
   */
  async function fetchComments(issueId: number) {
    loading.value = true
    error.value = null
    try {
      comments.value = await commentApi.list(issueId)
    } catch (err) {
      error.value = (err as Error).message
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * 创建评论
   */
  async function createComment(data: CreateCommentForm) {
    loading.value = true
    error.value = null
    try {
      const comment = await commentApi.create(data)
      comments.value.push(comment)
      return comment
    } catch (err) {
      error.value = (err as Error).message
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * 删除评论
   */
  async function deleteComment(id: number) {
    loading.value = true
    error.value = null
    try {
      await commentApi.delete(id)
      comments.value = comments.value.filter(c => c.id !== id)
    } catch (err) {
      error.value = (err as Error).message
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * 清空评论（用于切换问题时）
   */
  function clearComments() {
    comments.value = []
  }

  return {
    comments,
    loading,
    error,
    fetchComments,
    createComment,
    deleteComment,
    clearComments
  }
})
