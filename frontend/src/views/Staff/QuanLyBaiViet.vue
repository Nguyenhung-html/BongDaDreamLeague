<template>
  <div class="qlbv">

    <!-- ===== NỀN AMBIENT — cùng kỹ thuật với trang Dashboard ===== -->
    <div class="qlbv__ambient" aria-hidden="true">
      <div class="floodlight floodlight--l"></div>
      <div class="floodlight floodlight--r"></div>

      <!-- Hạt sáng bay lên -->
      <div class="motes"><span v-for="n in 14" :key="'m'+n" class="mote" :style="{ '--i': n }"></span></div>

      <!-- Hạt rơi nhẹ xuống, tạo chiều sâu -->
      <div class="falling"><span v-for="n in 18" :key="'f'+n" class="falling__item" :style="{ '--i': n }"></span></div>

      <!-- Đạo cụ bóng đá trôi nổi -->
      <div class="field-props">
        <div class="prop prop--ball1"><svg viewBox="0 0 40 40"><circle cx="20" cy="20" r="18" fill="var(--chalk-050)" stroke="var(--night-950)" stroke-width="1.4"/><polygon points="20,10 26,14.5 24,21.5 16,21.5 14,14.5" fill="var(--night-950)"/></svg></div>
        <div class="prop prop--ball2"><svg viewBox="0 0 40 40"><circle cx="20" cy="20" r="18" fill="var(--chalk-050)" stroke="var(--night-950)" stroke-width="1.4"/><polygon points="20,10 26,14.5 24,21.5 16,21.5 14,14.5" fill="var(--night-950)"/></svg></div>
        <div class="prop prop--flag"><svg viewBox="0 0 30 46" fill="none"><path d="M4 44V4" stroke="var(--lime-300)" stroke-width="1.8" stroke-linecap="round"/><path d="M4 5l20 6-20 6V5Z" fill="var(--lime-300)" opacity=".85"/></svg></div>
        <div class="prop prop--trophy"><svg viewBox="0 0 40 46" fill="none"><path d="M11 6h18v10a9 9 0 0 1-18 0V6Z" stroke="var(--amber-400)" stroke-width="1.6"/><path d="M20 25v6M13 37h14l-1.6-4.5a2 2 0 0 0-1.9-1.5h-6.9a2 2 0 0 0-1.9 1.5L13 37Z" stroke="var(--amber-400)" stroke-width="1.6" stroke-linejoin="round"/></svg></div>
      </div>
    </div>

    <!-- ===== HEADER ===== -->
    <div class="page-header">
      <div>
        <span class="eyebrow">⚽ Bảng điều khiển · STAFF</span>
        <h1 class="page-title">Quản lý bài viết &amp; Tin tức</h1>
        <p class="page-desc">Quản lý nội dung truyền thông và tin tức hiển thị trên hệ thống.</p>
      </div>
      <button class="btn-primary" @click="openCreateModal">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        Thêm bài viết mới
      </button>
    </div>

    <!-- ===== BỘ LỌC ===== -->
    <div class="filter-card">
      <div class="filter-top">
        <div class="search-wrap">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="7"/><path d="M21 21l-4.3-4.3"/></svg>
          <input type="text" v-model="searchQuery" class="search-input" placeholder="Tìm kiếm theo tiêu đề..." @input="handleFilterChange"/>
        </div>

        <div class="pill-select">
          <label class="th-ico">
            <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="22 3 2 3 10 12.46 10 19 14 21 14 12.46 22 3"/></svg>
            Trạng thái
          </label>
          <select v-model="selectedStatus" class="form-control" @change="fetchPosts">
            <option value="">Tất cả trạng thái</option>
            <option value="published">Đã xuất bản</option>
            <option value="draft">Bản nháp</option>
            <option value="archived">Đã ẩn</option>
          </select>
        </div>
      </div>
    </div>

    <!-- ===== BẢNG DANH SÁCH ===== -->
    <div class="table-card">
      <div v-if="loading" class="state-box"><span class="spinner"></span> Đang tải dữ liệu...</div>

      <div v-else-if="posts.length === 0" class="no-data">
        <svg class="no-data__icon" width="34" height="34" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"><path d="M4 4h16v16H4z" opacity=".25"/><path d="M4 4h16v16H4V4z"/><path d="M4 9h16M9 4v16"/><line x1="14" y1="14" x2="18" y2="18"/><line x1="18" y1="14" x2="14" y2="18"/></svg>
        Không tìm thấy bài viết nào.
      </div>

      <div v-else class="table-responsive">
        <table class="data-table">
          <thead>
            <tr>
              <th width="90">
                <span class="th-ico">
                  <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="8.5" cy="8.5" r="1.5"/><path d="M21 15l-5-5L5 21"/></svg>
                  Ảnh
                </span>
              </th>
              <th>
                <span class="th-ico">
                  <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 7V4h16v3"/><path d="M9 20h6"/><path d="M12 4v16"/></svg>
                  Tiêu đề &amp; Slug
                </span>
              </th>
              <th>
                <span class="th-ico">
                  <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
                  Tóm tắt
                </span>
              </th>
              <th>
                <span class="th-ico">
                  <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 12h-4l-3 9L9 3l-3 9H2"/></svg>
                  Trạng thái
                </span>
              </th>
              <th>
                <span class="th-ico">
                  <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                  Lượt xem
                </span>
              </th>
              <th>
                <span class="th-ico">
                  <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
                  Ngày tạo
                </span>
              </th>
              <th style="text-align:center" width="120">
                <span class="th-ico th-ico--center">
                  <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 1 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 1 1-2.83-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 1 1 2.83-2.83l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 1 1 2.83 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z"/></svg>
                  Thao tác
                </span>
              </th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="post in posts" :key="post.id">
              <td>
                <img
                  :src="post.thumbnailUrl || fallbackThumb"
                  @error="e => e.target.src = fallbackThumb"
                  alt="Thumbnail"
                  class="post-thumbnail"
                />
              </td>
              <td>
                <div class="cell-stack">
                  <span class="bold">{{ post.title }}</span>
                  <span class="sub">/tin-tuc/{{ post.slug }}</span>
                </div>
              </td>
              <td>
                <div class="post-summary">{{ post.summary || 'Không có tóm tắt' }}</div>
              </td>
              <td>
                <span :class="['badge', getStatusClass(post.status)]">
                  <span class="badge__dot"></span>{{ getStatusLabel(post.status) }}
                </span>
              </td>
              <td>
                <span class="sub th-ico">
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                  {{ post.viewsCount || 0 }}
                </span>
              </td>
              <td>
                <span class="sub th-ico">
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
                  {{ formatDate(post.createdAt) }}
                </span>
              </td>
              <td>
                <div class="actions">
                  <button class="btn-icon btn-edit" title="Chỉnh sửa" @click="openEditModal(post)">
                    <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                  </button>
                  <button class="btn-icon btn-delete" title="Xóa" @click="confirmDelete(post)">
                    <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- PHÂN TRANG -->
      <div class="pagination" v-if="totalPages > 1">
        <button class="btn-page" :disabled="currentPage === 0" @click="changePage(currentPage - 1)">
          <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4"><polyline points="15 18 9 12 15 6"/></svg>
          Trước
        </button>
        <span class="page-info">Trang {{ currentPage + 1 }} / {{ totalPages }}</span>
        <button class="btn-page" :disabled="currentPage >= totalPages - 1" @click="changePage(currentPage + 1)">
          Sau
          <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4"><polyline points="9 18 15 12 9 6"/></svg>
        </button>
      </div>
    </div>

    <!-- ===== MODAL THÊM / SỬA ===== -->
    <div class="overlay" v-if="showModal" @click.self="closeModal">
      <div class="modal-box wide">
        <div class="modal-header">
          <h2>{{ isEditing ? '✏️ Chỉnh sửa bài viết' : '📝 Thêm bài viết mới' }}</h2>
          <button class="close-btn" @click="closeModal">&times;</button>
        </div>
        <form @submit.prevent="savePost">
          <div class="modal-body">
            <div class="form-group">
              <label class="form-label required">
                <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 7V4h16v3"/><path d="M9 20h6"/><path d="M12 4v16"/></svg>
                Tiêu đề bài viết
              </label>
              <input type="text" v-model="form.title" class="form-control" placeholder="Nhập tiêu đề..." required @input="onTitleInput"/>
            </div>

            <div class="form-group">
              <label class="form-label">
                <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"/><path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"/></svg>
                Đường dẫn thân thiện (Slug)
              </label>
              <input type="text" v-model="form.slug" class="form-control form-control--readonly" readonly placeholder="Tự động tạo từ tiêu đề..."/>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label class="form-label">
                  <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 12h-4l-3 9L9 3l-3 9H2"/></svg>
                  Trạng thái
                </label>
                <select v-model="form.status" class="form-control">
                  <option value="draft">Bản nháp (Draft)</option>
                  <option value="published">Xuất bản (Published)</option>
                  <option value="archived">Ẩn bài (Archived)</option>
                </select>
              </div>
              <div class="form-group">
                <label class="form-label">
                  <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="8.5" cy="8.5" r="1.5"/><path d="M21 15l-5-5L5 21"/></svg>
                  URL Ảnh Thumbnail
                </label>
                <input type="text" v-model="form.thumbnailUrl" class="form-control" placeholder="https://example.com/image.jpg"/>
              </div>
            </div>

            <div class="form-group">
              <label class="form-label">
                <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="21" y1="10" x2="3" y2="10"/><line x1="21" y1="6" x2="3" y2="6"/><line x1="21" y1="14" x2="3" y2="14"/><line x1="17" y1="18" x2="3" y2="18"/></svg>
                Tóm tắt ngắn
              </label>
              <textarea v-model="form.summary" class="form-control" rows="2" placeholder="Nhập tóm tắt bài viết..."></textarea>
            </div>

            <div class="form-group">
              <label class="form-label required">
                <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>
                Nội dung bài viết
              </label>
              <textarea v-model="form.content" class="form-control" rows="8" placeholder="Nhập nội dung bài viết..." required></textarea>
            </div>
          </div>

          <div class="modal-footer">
            <button type="button" class="btn-ghost" @click="closeModal">Hủy bỏ</button>
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
import { ref, reactive, onMounted } from 'vue'

const API_BASE_URL = '/api/posts'
const fallbackThumb = 'https://placehold.co/80x50/0a1f13/b6ff3c?text=DreamLeague'

const posts = ref([])
const loading = ref(false)
const submitting = ref(false)
const showModal = ref(false)
const isEditing = ref(false)
const editingId = ref(null)

const searchQuery = ref('')
const selectedStatus = ref('')
const currentPage = ref(0)
const totalPages = ref(1)
const pageSize = ref(10)

const form = reactive({
  title: '',
  slug: '',
  summary: '',
  content: '',
  thumbnailUrl: '',
  status: 'draft'
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
      headers: { 'Authorization': `Bearer ${token}` }
    })

    if (res.ok) {
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
      headers: { 'Authorization': `Bearer ${token}` }
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

// Mỗi trạng thái một màu riêng biệt
function getStatusClass(status) {
  switch (status) {
    case 'published': return 'badge-published'
    case 'draft': return 'badge-draft'
    case 'archived': return 'badge-archived'
    default: return 'badge-archived'
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
/* ============================================================
   TOKENS — đồng bộ với trang chủ & các trang quản lý khác
   ============================================================ */
.qlbv {
  --night-950: #050b08;
  --night-800: #0a1f13;
  --night-700: #123321;
  --turf-500: #23935a;
  --turf-700: #146239;
  --lime-400: #b6ff3c;
  --lime-300: #d3ff8f;
  --amber-400: #ffb020;
  --crimson-500: #ff4757;
  --violet-400: #a78bfa;
  --chalk-050: #f7fbf4;
  --chalk-200: #e3ecdf;
  --font-display: 'Oswald', 'Manrope', sans-serif;
  --font-body: 'Manrope', sans-serif;
  --font-mono: 'Space Mono', monospace;

  position: relative;
  width: 100%;
  min-height: 100%;
  background: var(--night-950);
  color: var(--chalk-050);
  font-family: var(--font-body);
  padding: 32px clamp(16px, 3vw, 40px) 60px;
  box-sizing: border-box;
  overflow: hidden;
}

/* ============================================================
   AMBIENT — LÁ RƠI
   ============================================================ */
.qlbv__ambient { position: absolute; inset: 0; z-index: 0; pointer-events: none; overflow: hidden; }
.floodlight { position: absolute; top: -20%; width: 40vh; height: 100vh; background: conic-gradient(from 90deg at 50% 0%, transparent 42%, rgba(255,244,214,.07) 50%, transparent 58%); mix-blend-mode: screen; animation: sweep 11s ease-in-out infinite alternate; }
.floodlight--l { left: -6%; }
.floodlight--r { right: -6%; animation-direction: alternate-reverse; }
@keyframes sweep { 0% { transform: rotate(-8deg); } 100% { transform: rotate(8deg); } }

.motes { position: absolute; inset: 0; }
.mote { position: absolute; bottom: -10px; left: calc((var(--i) * 8.4%) + 1%); width: 3px; height: 3px; border-radius: 50%; background: var(--lime-300); opacity: 0; animation: rise 9s linear infinite; animation-delay: calc(var(--i) * -0.7s); }
@keyframes rise { 0% { transform: translateY(0) scale(.6); opacity: 0; } 10% { opacity: .5; } 90% { opacity: .1; } 100% { transform: translateY(-100%) scale(1.1); opacity: 0; } }

/* Hạt rơi nhẹ xuống — dùng px cố định (KHÔNG dùng % hay vh) nên luôn chuyển động rõ,
   bất kể .qlbv được tính chiều cao thế nào. Đây là kỹ thuật giống hệt trang Dashboard. */
.falling { position: absolute; inset: 0; overflow: hidden; }
.falling__item {
  position: absolute; top: -6%; left: calc((var(--i) * 5.6%) + 1%);
  width: 3px; height: 3px; border-radius: 50%; background: var(--chalk-050);
  opacity: 0; animation: fall linear infinite;
  animation-duration: calc(7s + (var(--i) * 0.45s));
  animation-delay: calc(var(--i) * -0.6s);
}
@keyframes fall {
  0% { transform: translateY(0) translateX(0); opacity: 0; }
  8% { opacity: .55; }
  50% { transform: translateY(160px) translateX(14px); }
  92% { opacity: .12; }
  100% { transform: translateY(340px) translateX(-10px); opacity: 0; }
}

/* Đạo cụ bóng đá trôi nổi (bay bay) */
.field-props { position: absolute; inset: 0; }
.prop { position: absolute; opacity: .1; animation: prop-float 7s ease-in-out infinite; }
.prop svg { width: 100%; height: 100%; }
.prop--ball1 { width: 46px; height: 46px; top: 12%; right: 8%; }
.prop--ball2 { width: 26px; height: 26px; bottom: 18%; left: 6%; opacity: .08; animation-delay: .9s; animation-duration: 8.5s; }
.prop--flag { width: 32px; height: 48px; top: 30%; left: 3%; animation-delay: 1.1s; }
.prop--trophy { width: 40px; height: 46px; bottom: 10%; right: 22%; animation-delay: 1.6s; }
@keyframes prop-float { 0%, 100% { transform: translateY(0) rotate(-3deg); } 50% { transform: translateY(-16px) rotate(3deg); } }

/* Nội dung chính nổi trên lớp ambient */
.page-header, .filter-card, .table-card { position: relative; z-index: 1; }

/* ============================================================
   HEADER
   ============================================================ */
.page-header { display: flex; justify-content: space-between; align-items: flex-end; gap: 16px; margin-bottom: 22px; flex-wrap: wrap; }
.eyebrow { display: inline-block; font-family: var(--font-mono); font-size: 11.5px; letter-spacing: .14em; text-transform: uppercase; color: var(--lime-300); }
.page-title { font-family: var(--font-display); font-weight: 600; font-size: clamp(24px, 2.6vw, 30px); margin: 8px 0 4px; color: var(--chalk-050); }
.page-desc { font-size: 13.5px; color: var(--chalk-200); opacity: .72; margin: 0; }

/* ============================================================
   BUTTONS
   ============================================================ */
.btn-primary {
  display: inline-flex; align-items: center; gap: 8px; padding: 12px 22px;
  background: linear-gradient(135deg, var(--turf-500), var(--turf-700)); color: var(--chalk-050);
  border: none; border-radius: 999px; font-weight: 700; font-size: 14px; cursor: pointer;
  box-shadow: 0 10px 24px -10px rgba(20,98,57,.75); transition: transform .15s ease, box-shadow .15s ease;
  white-space: nowrap;
}
.btn-primary:hover { transform: translateY(-2px); box-shadow: 0 14px 28px -8px rgba(182,255,60,.35); }
.btn-primary:disabled { opacity: .55; cursor: not-allowed; transform: none; box-shadow: none; }

.btn-ghost {
  display: inline-flex; align-items: center; gap: 6px; padding: 10px 20px;
  background: rgba(247,251,244,.06); color: var(--chalk-050); border: 1.5px solid rgba(247,251,244,.2);
  border-radius: 999px; font-weight: 600; font-size: 13.5px; cursor: pointer; transition: all .15s ease;
}
.btn-ghost:hover { border-color: var(--lime-400); color: var(--lime-300); background: rgba(182,255,60,.08); }

/* ============================================================
   FILTER CARD
   ============================================================ */
.filter-card {
  background: rgba(247,251,244,.03); border: 1px solid rgba(247,251,244,.1); border-radius: 18px;
  padding: 18px 20px; margin-bottom: 22px; backdrop-filter: blur(6px);
}
.filter-top { display: flex; flex-wrap: wrap; gap: 12px; align-items: flex-end; }

.search-wrap {
  flex: 1.6; min-width: 220px; display: flex; align-items: center; gap: 8px;
  background: rgba(5,11,8,.4); border: 1px solid rgba(247,251,244,.15); border-radius: 10px; padding: 0 12px;
}
.search-wrap svg { color: var(--chalk-200); opacity: .6; flex-shrink: 0; }
.search-input { flex: 1; border: none; background: transparent; outline: none; padding: 10px 0; font-size: 13.5px; color: var(--chalk-050); font-family: var(--font-body); }
.search-input::placeholder { color: var(--chalk-200); opacity: .45; }

.pill-select { display: flex; flex-direction: column; gap: 5px; min-width: 180px; }
.pill-select label { font-family: var(--font-mono); font-size: 10.5px; text-transform: uppercase; letter-spacing: .06em; color: var(--lime-300); padding-left: 2px; }

.form-control {
  padding: 10px 13px; border: 1px solid rgba(247,251,244,.15); border-radius: 10px; font-size: 13.5px;
  outline: none; background: rgba(5,11,8,.4); color: var(--chalk-050); font-family: var(--font-body);
  transition: border-color .15s; width: 100%; box-sizing: border-box;
}
.form-control:focus { border-color: var(--lime-400); box-shadow: 0 0 0 3px rgba(182,255,60,.12); }
select.form-control { cursor: pointer; }
select.form-control option { background: var(--night-800); color: var(--chalk-050); }
.form-control--readonly { background: rgba(247,251,244,.02); color: var(--chalk-200); opacity: .7; cursor: not-allowed; }
textarea.form-control { resize: vertical; font-family: var(--font-body); }

/* ============================================================
   ICON HELPERS
   ============================================================ */
.th-ico { display: inline-flex; align-items: center; gap: 6px; }
.th-ico svg { flex-shrink: 0; opacity: .8; }
.th-ico--center { justify-content: center; }

/* ============================================================
   STATE BOX
   ============================================================ */
.state-box { text-align: center; padding: 60px 20px; color: var(--chalk-200); opacity: .75; font-size: 14.5px; display: flex; align-items: center; justify-content: center; gap: 10px; }
.spinner { width: 16px; height: 16px; border-radius: 50%; border: 2px solid rgba(182,255,60,.25); border-top-color: var(--lime-400); animation: spin .7s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }

/* ============================================================
   TABLE
   ============================================================ */
.table-card { background: rgba(247,251,244,.03); border: 1px solid rgba(247,251,244,.1); border-radius: 18px; overflow: hidden; }
.table-responsive { overflow-x: auto; }
.data-table { width: 100%; border-collapse: collapse; font-size: 13.5px; }
.data-table th {
  background: rgba(247,251,244,.04); color: var(--lime-300); font-family: var(--font-mono); font-weight: 600;
  font-size: 11px; text-transform: uppercase; letter-spacing: .05em; padding: 14px 16px; text-align: left;
  border-bottom: 1px solid rgba(247,251,244,.1);
}
.data-table td { padding: 13px 16px; border-bottom: 1px solid rgba(247,251,244,.06); vertical-align: middle; }
.data-table tbody tr:hover { background: rgba(182,255,60,.04); }
.cell-stack { display: flex; flex-direction: column; gap: 3px; }
.bold { font-weight: 600; color: var(--chalk-050); }
.sub { font-size: 12px; color: var(--chalk-200); opacity: .65; }

.post-thumbnail { width: 64px; height: 42px; object-fit: cover; border-radius: 6px; border: 1px solid rgba(247,251,244,.12); }
.post-summary { max-width: 260px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; color: var(--chalk-200); opacity: .8; font-size: 13px; }

/* BADGES — mỗi trạng thái một màu riêng */
.badge { display: inline-flex; align-items: center; gap: 6px; padding: 5px 12px; border-radius: 20px; font-size: 11.5px; font-weight: 700; }
.badge__dot { width: 6px; height: 6px; border-radius: 50%; flex-shrink: 0; }

.badge-published { background: rgba(182,255,60,.15); color: var(--lime-300); }
.badge-published .badge__dot { background: var(--lime-400); }

.badge-draft { background: rgba(255,176,32,.15); color: var(--amber-400); }
.badge-draft .badge__dot { background: var(--amber-400); }

.badge-archived { background: rgba(167,139,250,.16); color: var(--violet-400); }
.badge-archived .badge__dot { background: var(--violet-400); }

/* ACTIONS */
.actions { display: flex; gap: 6px; justify-content: center; }
.btn-icon {
  width: 30px; height: 30px; border-radius: 8px; border: 1px solid rgba(247,251,244,.14);
  background: rgba(247,251,244,.03); display: inline-flex; align-items: center; justify-content: center;
  cursor: pointer; color: var(--chalk-200); transition: all .15s;
}
.btn-edit:hover { color: var(--lime-300); border-color: var(--lime-400); background: rgba(182,255,60,.08); }
.btn-delete:hover { color: #ff9686; border-color: var(--crimson-500); background: rgba(255,71,87,.1); }

.no-data { text-align: center; padding: 50px 20px; color: var(--chalk-200); opacity: .6; }
.no-data__icon { margin-bottom: 8px; color: var(--chalk-200); opacity: .7; }

/* PAGINATION */
.pagination { display: flex; align-items: center; justify-content: flex-end; gap: 14px; padding: 14px 18px; background: rgba(247,251,244,.03); border-top: 1px solid rgba(247,251,244,.08); }
.btn-page {
  display: inline-flex; align-items: center; gap: 5px;
  padding: 7px 14px; border: 1px solid rgba(247,251,244,.16); background: rgba(247,251,244,.04);
  color: var(--chalk-200); border-radius: 999px; cursor: pointer; font-size: 12.5px; font-weight: 600; transition: all .15s;
}
.btn-page:hover:not(:disabled) { border-color: var(--lime-400); color: var(--lime-300); }
.btn-page:disabled { opacity: .4; cursor: not-allowed; }
.page-info { font-size: 12.5px; color: var(--chalk-200); opacity: .8; font-family: var(--font-mono); }

/* ============================================================
   MODAL
   ============================================================ */
.overlay { position: fixed; inset: 0; background: rgba(5,11,8,.75); backdrop-filter: blur(3px); display: flex; align-items: center; justify-content: center; z-index: 200; padding: 20px; }
.modal-box { background: var(--night-800); border: 1px solid rgba(182,255,60,.2); border-radius: 18px; width: 100%; max-width: 520px; box-shadow: 0 24px 50px rgba(0,0,0,.5); overflow: hidden; max-height: 90vh; display: flex; flex-direction: column; }
.modal-box.wide { max-width: 680px; }
.modal-header { padding: 18px 24px; border-bottom: 1px solid rgba(247,251,244,.1); display: flex; justify-content: space-between; align-items: center; flex-shrink: 0; }
.modal-header h2 { font-family: var(--font-display); font-size: 17px; font-weight: 600; color: var(--chalk-050); margin: 0; }
.close-btn { background: none; border: none; font-size: 22px; color: var(--chalk-200); opacity: .6; cursor: pointer; }
.close-btn:hover { opacity: 1; color: var(--lime-300); }
.modal-body { padding: 20px 24px; overflow-y: auto; }
.modal-footer { padding: 16px 24px; border-top: 1px solid rgba(247,251,244,.1); display: flex; justify-content: flex-end; gap: 10px; flex-shrink: 0; }

.form-group { margin-bottom: 16px; display: flex; flex-direction: column; gap: 6px; }
.form-row { display: flex; gap: 14px; }
.form-row .form-group { flex: 1; }
.form-label { display: flex; align-items: center; gap: 6px; font-size: 12.5px; font-weight: 600; color: var(--lime-300); font-family: var(--font-mono); text-transform: uppercase; letter-spacing: .04em; }
.form-label svg { flex-shrink: 0; opacity: .85; }
.form-label.required::after { content: ' *'; color: var(--crimson-500); }

/* ============================================================
   RESPONSIVE
   ============================================================ */
@media (max-width: 720px) {
  .filter-top { flex-direction: column; align-items: stretch; }
  .pill-select { min-width: 0; }
  .form-row { flex-direction: column; gap: 0; }
  .data-table { font-size: 12.5px; }
}
</style>