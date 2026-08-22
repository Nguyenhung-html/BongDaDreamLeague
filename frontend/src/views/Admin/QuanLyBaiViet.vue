<template>
  <div class="quan-ly-bai-viet">
    <!-- Header Page -->
    <div class="page-header">
      <div>
        <h2 class="page-title">Quản lý bài viết & Tin tức</h2>
        <p class="page-subtitle">Quản lý toàn bộ bài viết, tin tức giải đấu và sự kiện trên hệ thống</p>
      </div>
      <button class="btn-primary" @click="openCreateModal">
        <svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M12 5v14M5 12h14" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
        Tạo bài viết mới
      </button>
    </div>

    <!-- Quick Stats Cards -->
    <div class="stats-grid">
      <div class="stat-card">
        <span class="stat-label">Tổng số bài viết</span>
        <span class="stat-value">{{ posts.length }}</span>
      </div>
      <div class="stat-card success">
        <span class="stat-label">Đã xuất bản</span>
        <span class="stat-value">{{ posts.filter(p => p.status === 'published').length }}</span>
      </div>
      <div class="stat-card warning">
        <span class="stat-label">Bản nháp / Chờ duyệt</span>
        <span class="stat-value">{{ posts.filter(p => p.status === 'draft').length }}</span>
      </div>
      <div class="stat-card info">
        <span class="stat-label">Tổng lượt xem</span>
        <span class="stat-value">{{ totalViews.toLocaleString('vi-VN') }}</span>
      </div>
    </div>

    <!-- Filter & Search Bar -->
    <div class="filter-card">
      <div class="search-box">
        <svg viewBox="0 0 24 24" fill="none" width="18" height="18"><circle cx="11" cy="11" r="8" stroke="currentColor" stroke-width="1.7"/><path d="M21 21l-4.35-4.35" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/></svg>
        <input type="text" v-model="searchQuery" placeholder="Tìm kiếm theo tiêu đề bài viết..." />
      </div>

      <div class="filter-actions">
        <select v-model="selectedStatus" @change="fetchPosts" class="select-filter">
          <option value="">Tất cả trạng thái</option>
          <option value="published">Đã xuất bản</option>
          <option value="draft">Bản nháp</option>
          <option value="archived">Đã ẩn</option>
        </select>
      </div>
    </div>

    <!-- Table Posts -->
    <div class="table-card">
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>Đang tải danh sách bài viết...</p>
      </div>

      <table v-else-if="filteredPosts.length > 0" class="custom-table">
        <thead>
          <tr>
            <th width="80">Ảnh</th>
            <th>Tiêu đề & Tóm tắt</th>
            <th width="120">Lượt xem</th>
            <th width="130">Trạng thái</th>
            <th width="120">Ngày tạo</th>
            <th width="140" class="text-right">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="post in filteredPosts" :key="post.id">
            <td>
              <img :src="post.thumbnailUrl || 'https://via.placeholder.com/150'" class="thumb-img" alt="thumb" />
            </td>
            <td>
              <div class="post-title">{{ post.title }}</div>
              <div class="post-summary">{{ post.summary || 'Chưa có tóm tắt...' }}</div>
            </td>
            <td>
              <span class="view-count">👁️ {{ post.viewsCount || 0 }}</span>
            </td>
            <td>
              <span :class="['status-badge', getStatusClass(post.status)]">
                {{ getStatusLabel(post.status) }}
              </span>
            </td>
            <td class="date-text">{{ formatDate(post.createdAt) }}</td>
            <td class="text-right">
              <div class="action-buttons">
                <button class="btn-icon edit" @click="openEditModal(post)" title="Chỉnh sửa">
                  <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7" stroke="currentColor" stroke-width="1.7"/><path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z" stroke="currentColor" stroke-width="1.7"/></svg>
                </button>
                <button class="btn-icon delete" @click="confirmDelete(post)" title="Xóa bài">
                  <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M3 6h18M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2" stroke="currentColor" stroke-width="1.7"/></svg>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-else class="empty-state">
        <p>Không tìm thấy bài viết nào phù hợp.</p>
      </div>
    </div>

    <!-- Modal Form (Create / Edit) -->
    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-box">
        <div class="modal-header">
          <h3>{{ isEditing ? 'Cập nhật bài viết' : 'Thêm bài viết mới' }}</h3>
          <button class="btn-close" @click="closeModal">&times;</button>
        </div>

        <form @submit.prevent="savePost" class="modal-body">
          <div class="form-group">
            <label>Tiêu đề bài viết <span class="required">*</span></label>
            <input type="text" v-model="form.title" @input="onTitleInput" required placeholder="Nhập tiêu đề..." />
          </div>

          <div class="form-group">
            <label>Slug (Đường dẫn tĩnh)</label>
            <input type="text" v-model="form.slug" placeholder="tudong-tao-slug" />
          </div>

          <div class="form-group">
            <label>Đường dẫn ảnh đại diện (Thumbnail URL)</label>
            <input type="text" v-model="form.thumbnailUrl" placeholder="https://example.com/image.jpg" />
          </div>

          <div class="form-group">
            <label>Tóm tắt bài viết</label>
            <textarea v-model="form.summary" rows="2" placeholder="Tóm tắt ngắn hiển thị ở trang danh sách..."></textarea>
          </div>

          <div class="form-group">
            <label>Nội dung chi tiết <span class="required">*</span></label>
            <textarea v-model="form.content" rows="6" required placeholder="Nhập nội dung bài viết..."></textarea>
          </div>

          <div class="form-group">
            <label>Trạng thái bài viết</label>
            <select v-model="form.status">
              <option value="published">Đã xuất bản (Công khai)</option>
              <option value="draft">Bản nháp (Lưu nội bộ)</option>
              <option value="archived">Đã ẩn</option>
            </select>
          </div>

          <div class="modal-footer">
            <button type="button" class="btn-cancel" @click="closeModal">Hủy</button>
            <button type="submit" class="btn-primary" :disabled="submitting">
              {{ submitting ? 'Đang lưu...' : (isEditing ? 'Cập nhật' : 'Thêm mới') }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'

const API_BASE_URL = '/api/posts'

const posts = ref([])
const loading = ref(false)
const submitting = ref(false)
const showModal = ref(false)
const isEditing = ref(false)
const editingId = ref(null)

const searchQuery = ref('')
const selectedStatus = ref('')

const form = reactive({
  title: '',
  slug: '',
  summary: '',
  content: '',
  thumbnailUrl: '',
  status: 'draft'
})

// Tính tổng lượt xem
const totalViews = computed(() => {
  return posts.value.reduce((acc, cur) => acc + (cur.viewsCount || 0), 0)
})

// Filter dữ liệu client
const filteredPosts = computed(() => {
  return posts.value.filter(post => {
    const matchSearch = post.title.toLowerCase().includes(searchQuery.value.toLowerCase())
    return matchSearch
  })
})

onMounted(() => {
  fetchPosts()
})

async function fetchPosts() {
  loading.value = true
  try {
    let url = API_BASE_URL
    if (selectedStatus.value) {
      url += `?status=${selectedStatus.value}`
    }

    const token = localStorage.getItem('token') || localStorage.getItem('accessToken') || localStorage.getItem('jwt')
    const res = await fetch(url, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })

    if (res.ok) {
      posts.value = await res.json()
    }
  } catch (err) {
    console.error('Lỗi tải danh sách bài viết:', err)
  } finally {
    loading.value = false
  }
}

function openCreateModal() {
  isEditing.value = false
  editingId.value = null
  resetForm()
  showModal.value = true
}

function openEditModal(post) {
  isEditing.value = true
  editingId.value = post.id
  form.title = post.title
  form.slug = post.slug
  form.summary = post.summary
  form.content = post.content
  form.thumbnailUrl = post.thumbnailUrl
  form.status = post.status
  showModal.value = true
}

function closeModal() {
  showModal.value = false
  resetForm()
}

function resetForm() {
  form.title = ''
  form.slug = ''
  form.summary = ''
  form.content = ''
  form.thumbnailUrl = ''
  form.status = 'draft'
}

function onTitleInput() {
  if (!isEditing.value) {
    form.slug = makeSlug(form.title)
  }
}

function makeSlug(str) {
  if (!str) return ''
  return str
    .toLowerCase()
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .replace(/[đĐ]/g, 'd')
    .replace(/[^a-z0-9\s-]/g, '')
    .trim()
    .replace(/\s+/g, '-')
    .replace(/-+/g, '-')
}

async function savePost() {
  submitting.value = true
  const token = localStorage.getItem('token') || localStorage.getItem('accessToken') || localStorage.getItem('jwt')
  
  if (!token) {
    alert('Vui lòng đăng nhập lại!')
    submitting.value = false
    return
  }

  const method = isEditing.value ? 'PUT' : 'POST'
  const url = isEditing.value ? `${API_BASE_URL}/${editingId.value}` : API_BASE_URL

  try {
    const res = await fetch(url, {
      method: method,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(form)
    })
    
    if (res.ok) {
      alert(isEditing.value ? 'Cập nhật bài viết thành công!' : 'Tạo bài viết thành công!')
      closeModal()
      fetchPosts()
    } else {
      const errorText = await res.text()
      alert(`Lỗi (${res.status}): ${errorText}`)
    }
  } catch (err) {
    console.error('Lỗi khi lưu bài viết:', err)
  } finally {
    submitting.value = false
  }
}

async function confirmDelete(post) {
  if (!confirm(`Bạn có chắc chắn muốn xóa bài viết "${post.title}"?`)) return
  
  const token = localStorage.getItem('token') || localStorage.getItem('accessToken') || localStorage.getItem('jwt')

  try {
    const res = await fetch(`${API_BASE_URL}/${post.id}`, {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    
    if (res.ok) {
      alert('Đã xóa bài viết thành công!')
      fetchPosts()
    } else {
      const errorText = await res.text()
      alert(`Xóa thất bại (${res.status}): ${errorText}`)
    }
  } catch (err) {
    console.error('Lỗi khi xóa bài viết:', err)
  }
}

function getStatusClass(status) {
  switch (status) {
    case 'published': return 'badge-success'
    case 'draft': return 'badge-warning'
    case 'archived': return 'badge-secondary'
    default: return 'badge-secondary'
  }
}

function getStatusLabel(status) {
  switch (status) {
    case 'published': return 'Đã xuất bản'
    case 'draft': return 'Bản nháp'
    case 'archived': return 'Đã ẩn'
    default: return status
  }
}

function formatDate(dateStr) {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('vi-VN')
}
</script>

<style scoped>
.quan-ly-bai-viet { display: flex; flex-direction: column; gap: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; }
.page-title { font-size: 20px; font-weight: 700; color: #0f172a; margin: 0; }
.page-subtitle { font-size: 13px; color: #64748b; margin-top: 4px; }

/* Stats Grid */
.stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.stat-card { background: white; padding: 16px; border-radius: 10px; border: 1px solid #e2e8f0; display: flex; flex-direction: column; }
.stat-label { font-size: 12px; color: #64748b; font-weight: 600; }
.stat-value { font-size: 22px; font-weight: 800; color: #0f172a; margin-top: 4px; }

/* Filter */
.filter-card { background: white; padding: 14px 18px; border-radius: 10px; border: 1px solid #e2e8f0; display: flex; justify-content: space-between; align-items: center; gap: 16px; }
.search-box { display: flex; align-items: center; gap: 8px; background: #f8fafc; border: 1px solid #cbd5e1; padding: 8px 12px; border-radius: 8px; flex: 1; max-width: 400px; }
.search-box input { border: none; background: transparent; outline: none; width: 100%; font-size: 13.5px; }
.select-filter { padding: 8px 12px; border-radius: 8px; border: 1px solid #cbd5e1; outline: none; font-size: 13.5px; background: white; }

/* Table */
.table-card { background: white; border-radius: 10px; border: 1px solid #e2e8f0; overflow: hidden; }
.custom-table { width: 100%; border-collapse: collapse; text-align: left; font-size: 13.5px; }
.custom-table th { background: #f8fafc; padding: 12px 16px; font-weight: 600; color: #475569; border-bottom: 1px solid #e2e8f0; }
.custom-table td { padding: 12px 16px; border-bottom: 1px solid #f1f5f9; vertical-align: middle; }
.thumb-img { width: 60px; height: 42px; border-radius: 6px; object-fit: cover; }
.post-title { font-weight: 700; color: #0f172a; line-height: 1.3; }
.post-summary { font-size: 12px; color: #64748b; margin-top: 2px; display: -webkit-box; -webkit-line-clamp: 1; -webkit-box-orient: vertical; overflow: hidden; }
.view-count { font-size: 12px; font-weight: 600; color: #475569; }

/* Badges */
.status-badge { display: inline-block; padding: 4px 10px; border-radius: 20px; font-size: 11.5px; font-weight: 700; }
.badge-success { background: #dcfce7; color: #15803d; }
.badge-warning { background: #fef9c3; color: #a16207; }
.badge-secondary { background: #f1f5f9; color: #475569; }

/* Buttons & Actions */
.btn-primary { background: #16a34a; color: white; border: none; padding: 9px 16px; border-radius: 8px; font-weight: 600; cursor: pointer; display: flex; align-items: center; gap: 6px; }
.btn-primary:hover { background: #15803d; }
.action-buttons { display: flex; justify-content: flex-end; gap: 6px; }
.btn-icon { width: 32px; height: 32px; border-radius: 6px; border: 1px solid #cbd5e1; background: white; cursor: pointer; display: flex; align-items: center; justify-content: center; }
.btn-icon.edit:hover { background: #eff6ff; color: #2563eb; }
.btn-icon.delete:hover { background: #fef2f2; color: #dc2626; }

/* Modal */
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal-box { background: white; width: 100%; max-width: 650px; border-radius: 12px; overflow: hidden; max-height: 90vh; display: flex; flex-direction: column; }
.modal-header { padding: 16px 20px; border-bottom: 1px solid #e2e8f0; display: flex; justify-content: space-between; align-items: center; }
.modal-header h3 { margin: 0; font-size: 16px; }
.btn-close { border: none; background: transparent; font-size: 22px; cursor: pointer; }
.modal-body { padding: 20px; overflow-y: auto; display: flex; flex-direction: column; gap: 14px; }
.form-group { display: flex; flex-direction: column; gap: 6px; }
.form-group label { font-size: 12.5px; font-weight: 600; color: #334155; }
.form-group input, .form-group textarea, .form-group select { padding: 8px 12px; border-radius: 6px; border: 1px solid #cbd5e1; font-size: 13.5px; outline: none; }
.required { color: #dc2626; }
.modal-footer { display: flex; justify-content: flex-end; gap: 10px; margin-top: 10px; }
.btn-cancel { padding: 8px 16px; border-radius: 6px; border: 1px solid #cbd5e1; background: white; cursor: pointer; }
.loading-state, .empty-state { padding: 40px; text-align: center; color: #64748b; }
</style>