import request from '../request'

export interface Guide {
  id: number
  titleCn: string
  titleEn: string
  category: string
  classRestriction: string
  contentCn: string
  contentEn: string
  tags: string
  status: string
  authorId: number
  viewCount: number
  likeCount: number
  favoriteCount: number
  createdAt: string
}

export interface Comment {
  id: number
  guideId: number
  userId: number
  parentId: number
  content: string
  createdAt: string
}

export interface Favorite {
  id: number
  userId: number
  guideId: number
  createdAt: string
}

export const communityApi = {
  // Guides
  getGuides(params: { page?: number; size?: number; category?: string; keyword?: string }) {
    return request.get<{ records: Guide[]; total: number }>('/guides', { params })
  },
  getGuide(id: number) {
    return request.get<Guide>(`/guides/${id}`)
  },

  // Comments
  getComments(guideId: number) {
    return request.get<Comment[]>('/community/comments', { params: { guideId } })
  },
  addComment(data: { guideId: number; userId: number; content: string; parentId?: number }) {
    return request.post<Comment>('/community/comments', data)
  },
  deleteComment(id: number) {
    return request.delete(`/community/comments/${id}`)
  },

  // Favorites
  addFavorite(guideId: number, userId: number) {
    return request.post(`/community/favorites/${guideId}`, null, { params: { userId } })
  },
  removeFavorite(guideId: number, userId: number) {
    return request.delete(`/community/favorites/${guideId}`, { params: { userId } })
  },
  getMyFavorites(userId: number) {
    return request.get<Favorite[]>('/community/my-favorites', { params: { userId } })
  },

  // Recommendations
  getRecommendations(params: { page?: number; size?: number; className?: string; stage?: string }) {
    return request.get<{ records: any[]; total: number }>('/recommendations', { params })
  },
}
