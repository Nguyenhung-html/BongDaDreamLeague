<template>
  <div class="posts-management">
    <!-- HEADER TRANG & NÚT THÊM MỚI -->
    <div class="page-header">
      <div>
        <h1 class="page-title">Quản lý bài viết & Tin tức</h1>
        <p class="page-subtitle">Quản lý nội dung truyền thông và tin tức hiển thị trên hệ thống</p>
      </div>
      <button class="btn btn-primary" @click="openCreateModal">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <line x1="12" y1="5" x2="12" y2="19"></line>
          <line x1="5" y1="12" x2="19" y2="12"></line>
        </svg>
        Thêm bài viết mới
      </button>
    </div>

    <!-- THANH LỌC & TÌM KIẾM -->
    <div class="filter-card">
      <div class="filter-group">
        <div class="search-box">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="11" cy="11" r="8"></circle>
            <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
          </svg>
          <input 
            type="text" 
            v-model="searchQuery" 
            placeholder="Tìm kiếm theo tiêu đề..." 
            @input="handleFilterChange"
          />
        </div>

        <select v-model="selectedStatus" @change="fetchPosts">
          <option value="">Tất cả trạng thái</option>
          <option value="published">Đã xuất bản</option>
          <option value="draft">Bản nháp</option>
          <option value="archived">Đã ẩn</option>
        </select>
      </div>
    </div>

    <!-- BẢNG DANH SÁCH BÀI VIẾT -->
    <div class="table-card">
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>Đang tải dữ liệu...</p>
      </div>

      <div v-else-if="posts.length === 0" class="empty-state">
        <p>Không tìm thấy bài viết nào.</p>
      </div>

      <div v-else class="table-responsive">
        <table class="custom-table">
          <thead>
            <tr>
              <th width="80">Ảnh</th>
              <th>Tiêu đề & Slug</th>
              <th>Tóm tắt</th>
              <th>Trạng thái</th>
              <th>Lượt xem</th>
              <th>Ngày tạo</th>
              <th width="120" class="text-right">Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="post in posts" :key="post.id">
              <td>
                <img 
                  :src="post.thumbnailUrl || 'https://placehold.co/80x50?text=No+Image'" 
                  alt="Thumbnail" 
                  class="post-thumbnail" 
                />
              </td>
              <td>
                <div class="post-title">{{ post.title }}</div>
                <div class="post-slug">/tin-tuc/{{ post.slug }}</div>
              </td>
              <td>
                <div class="post-summary">{{ post.summary || 'Không có tóm tắt' }}</div>
              </td>
              <td>
                <span class="badge" :class="getStatusClass(post.status)">
                  {{ getStatusLabel(post.status) }}
                </span>
              </td>
              <td>{{ post.viewsCount || 0 }}</td>
              <td>{{ formatDate(post.createdAt) }}</td>
              <td class="text-right">
                <div class="action-buttons">
                  <button class="btn-icon btn-edit" title="Chỉnh sửa" @click="openEditModal(post)">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                      <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                    </svg>
                  </button>
                  <button class="btn-icon btn-delete" title="Xóa" @click="confirmDelete(post)">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <polyline points="3 6 5 6 21 6"></polyline>
                      <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                    </svg>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- PHÂN TRANG -->
      <div class="pagination" v-if="totalPages > 1">
        <button 
          class="btn-page" 
          :disabled="currentPage === 0" 
          @click="changePage(currentPage - 1)"
        >
          Trước
        </button>
        <span class="page-info">Trang {{ currentPage + 1 }} / {{ totalPages }}</span>
        <button 
          class="btn-page" 
          :disabled="currentPage >= totalPages - 1" 
          @click="changePage(currentPage + 1)"
        >
          Sau
        </button>
      </div>
    </div>

    <!-- MODAL THÊM / SỬA BÀI VIẾT -->
    <div class="modal-overlay" v-if="showModal" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ isEditing ? 'Chỉnh sửa bài viết' : 'Thêm bài viết mới' }}</h3>
          <button class="btn-close" @click="closeModal">&times;</button>
        </div>
        <form @submit.prevent="savePost" class="modal-body">
          <div class="form-group">
            <label class="form-label required">Tiêu đề bài viết</label>
            <input 
              type="text" 
              v-model="form.title" 
              class="form-control" 
              placeholder="Nhập tiêu đề..." 
              required
              @input="onTitleInput"
            />
          </div>

          <div class="form-group">
            <label class="form-label">Đường dẫn thân thiện (Slug)</label>
            <input 
              type="text" 
              v-model="form.slug" 
              class="form-control readonly" 
              readonly 
              placeholder="Tự động tạo từ tiêu đề..."
            />
          </div>

          <div class="form-row">
            <div class="form-group col-6">
              <label class="form-label">Trạng thái</label>
              <select v-model="form.status" class="form-control">
                <option value="draft">Bản nháp (Draft)</option>
                <option value="published">Xuất bản (Published)</option>
                <option value="archived">Ẩn bài (Archived)</option>
              </select>
            </div>
            <div class="form-group col-6">
              <label class="form-label">URL Ảnh Thumbnail</label>
              <input 
                type="text" 
                v-model="form.thumbnailUrl" 
                class="form-control" 
                placeholder="https://example.com/image.jpg" 
              />
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">Tóm tắt ngắn</label>
            <textarea 
              v-model="form.summary" 
              class="form-control" 
              rows="2" 
              placeholder="Nhập tóm tắt bài viết..."
            ></textarea>
          </div>

          <div class="form-group">
            <label class="form-label required">Nội dung bài viết</label>
            <textarea 
              v-model="form.content" 
              class="form-control" 
              rows="8" 
              placeholder="Nhập nội dung bài viết..." 
              required
            ></textarea>
          </div>

          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeModal">Hủy bỏ</button>
            <button type="submit" class="btn btn-primary" :disabled="submitting">
              {{ submitting ? 'Đang lưu...' : (isEditing ? 'Cập nhật' : 'Thêm mới') }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'

// URL gốc của API bài viết
const API_BASE_URL = 'http://localhost:8080/api/posts'

const posts = ref([])
const loading = ref(false)
const submitting = ref(false)
const showModal = ref(false)
const isEditing = ref(false)
const editingId = ref(null)

// Filters & Pagination
const searchQuery = ref('')
const selectedStatus = ref('')
const currentPage = ref(0)
const totalPages = ref(1)
const pageSize = ref(10)

// Form State
const form = reactive({
  title: '',
  slug: '',
  summary: '',
  content: '',
  thumbnailUrl: '',
  status: 'draft'
})

// Lifecycle
onMounted(() => {
  fetchPosts()
})

// Call API Fetch danh sách
async function fetchPosts() {
  loading.value = true
  try {
    // Tạo URL động có tham số status để lọc
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
      // Đã sửa: Gán đúng vào biến posts.value
      posts.value = await res.json()
    } else {
      console.error('Lỗi khi tải danh sách bài viết từ server')
    }
  } catch (err) {
    console.error('Lỗi tải danh sách bài viết:', err)
  } finally {
    loading.value = false
  }
}

function handleFilterChange() {
  currentPage.value = 0
  fetchPosts()
}

function changePage(page) {
  currentPage.value = page
  fetchPosts()
}

// Modal Helpers
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

// Slug Generator
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

// Save & Delete
async function savePost() {
  submitting.value = true
  
  const token = localStorage.getItem('token') || localStorage.getItem('accessToken') || localStorage.getItem('jwt')
  
  if (!token) {
    alert('Lỗi: Bạn chưa đăng nhập hoặc Token đã hết hạn!')
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
      alert(isEditing.value ? 'Cập nhật thành công!' : 'Thêm bài viết thành công!')
      closeModal()
      fetchPosts()
    } else {
      const errorText = await res.text()
      console.error('Lỗi từ Server (Status ' + res.status + '):', errorText)
      alert(`Lỗi (${res.status}): ${errorText || 'Không có quyền thực hiện thao tác này!'}`)
    }
  } catch (err) {
    console.error('Lỗi khi lưu bài viết:', err)
    alert('Không thể kết nối đến máy chủ!')
  } finally {
    submitting.value = false
  }
}

async function confirmDelete(post) {
  if (!confirm(`Bạn có chắc chắn muốn xóa bài viết "${post.title}"?`)) return
  
  const token = localStorage.getItem('token') || localStorage.getItem('accessToken') || localStorage.getItem('jwt')
  
  if (!token) {
    alert('Vui lòng đăng nhập lại!')
    return
  }

  try {
    const res = await fetch(`${API_BASE_URL}/${post.id}`, {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    
    if (res.ok) {
      alert('Đã xóa bài viết!')
      fetchPosts()
    } else {
      const errorText = await res.text()
      alert(`Xóa thất bại (${res.status}): ${errorText}`)
    }
  } catch (err) {
    console.error('Lỗi khi xóa bài viết:', err)
  }
}

// Helpers
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
  const d = new Date(dateStr)
  return d.toLocaleDateString('vi-VN')
}
</script>

<style scoped>
.posts-management {
  padding: 8px;
}

/* Header */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.page-title {
  font-size: 22px;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 4px 0;
}
.page-subtitle {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

/* Filter Card */
.filter-card {
  background: #ffffff;
  padding: 16px;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
  margin-bottom: 20px;
}
.filter-group {
  display: flex;
  gap: 12px;
}
.search-box {
  position: relative;
  flex: 1;
  display: flex;
  align-items: center;
}
.search-box svg {
  position: absolute;
  left: 12px;
  color: #94a3b8;
}
.search-box input {
  width: 100%;
  padding: 8px 12px 8px 38px;
  border: 1px solid #cbd5e1;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
}
.select-filter {
  padding: 8px 12px;
  border: 1px solid #cbd5e1;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
  background: white;
}

/* Table Card */
.table-card {
  background: #ffffff;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
  overflow: hidden;
}
.table-responsive {
  overflow-x: auto;
}
.custom-table {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
  font-size: 14px;
}
.custom-table th {
  background: #f8fafc;
  padding: 12px 16px;
  color: #475569;
  font-weight: 600;
  border-bottom: 1px solid #e2e8f0;
}
.custom-table td {
  padding: 14px 16px;
  border-bottom: 1px solid #f1f5f9;
  vertical-align: middle;
}
.custom-table tr:hover {
  background: #f8fafc;
}

.post-thumbnail {
  width: 60px;
  height: 40px;
  object-fit: cover;
  border-radius: 4px;
}
.post-title {
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 2px;
}
.post-slug {
  font-size: 12px;
  color: #64748b;
}
.post-summary {
  max-width: 250px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: #475569;
}

/* Badges */
.badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}
.badge-success { background: #dcfce7; color: #15803d; }
.badge-warning { background: #fef9c3; color: #a16207; }
.badge-secondary { background: #f1f5f9; color: #64748b; }

/* Buttons */
.btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  border: none;
  transition: all 0.2s;
}
.btn-primary { background: #16a34a; color: white; }
.btn-primary:hover { background: #15803d; }
.btn-secondary { background: #e2e8f0; color: #475569; }
.btn-secondary:hover { background: #cbd5e1; }

.action-buttons {
  display: flex;
  gap: 6px;
  justify-content: flex-end;
}
.btn-icon {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  border: 1px solid #e2e8f0;
  background: white;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #64748b;
}
.btn-edit:hover { color: #2563eb; border-color: #2563eb; }
.btn-delete:hover { color: #dc2626; border-color: #dc2626; }

/* Modal */
.modal-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
.modal-content {
  background: white;
  width: 100%;
  max-width: 650px;
  border-radius: 12px;
  overflow: hidden;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
}
.modal-header {
  padding: 16px 20px;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.modal-header h3 { margin: 0; font-size: 18px; }
.btn-close {
  background: none; border: none; font-size: 24px; cursor: pointer; color: #94a3b8;
}
.modal-body {
  padding: 20px;
  overflow-y: auto;
}
.form-group { margin-bottom: 16px; }
.form-row { display: flex; gap: 12px; }
.col-6 { flex: 1; }
.form-label { display: block; font-size: 13px; font-weight: 600; color: #334155; margin-bottom: 6px; }
.form-label.required::after { content: ' *'; color: #dc2626; }
.form-control {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #cbd5e1;
  border-radius: 6px;
  font-size: 14px;
  box-sizing: border-box;
}
.form-control.readonly { background: #f8fafc; color: #64748b; }
.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #e2e8f0;
}

/* Helpers */
.loading-state, .empty-state { padding: 40px; text-align: center; color: #64748b; }
.spinner {
  width: 24px; height: 24px; border: 3px solid #e2e8f0;
  border-top-color: #16a34a; border-radius: 50%;
  animation: spin 0.8s linear infinite; margin: 0 auto 12px;
}
@keyframes spin { to { transform: rotate(360deg); } }

/* Pagination */
.pagination {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12px;
  padding: 12px 16px;
  background: #f8fafc;
  border-top: 1px solid #e2e8f0;
}
.btn-page {
  padding: 6px 12px;
  border: 1px solid #cbd5e1;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
}
.btn-page:disabled { opacity: 0.5; cursor: not-allowed; }
.page-info { font-size: 13px; color: #64748b; }
.text-right { text-align: right; }
</style>