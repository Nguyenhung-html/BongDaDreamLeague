<template>
  <div class="page">

    <!-- ===== NỀN AMBIENT: đèn pha + hạt sáng + hạt rơi + đạo cụ bay bay ===== -->
    <div class="page__ambient" aria-hidden="true">
      <div class="floodlight floodlight--l"></div>
      <div class="floodlight floodlight--r"></div>
      <div class="motes"><span v-for="n in 12" :key="'m'+n" class="mote" :style="{ '--i': n }"></span></div>
      <div class="falling"><span v-for="n in 16" :key="'f'+n" class="falling__item" :style="{ '--i': n }"></span></div>
      <div class="field-props">
        <div class="prop prop--ball1"><svg viewBox="0 0 40 40"><circle cx="20" cy="20" r="18" fill="var(--chalk-050)" stroke="var(--night-950)" stroke-width="1.4"/><polygon points="20,10 26,14.5 24,21.5 16,21.5 14,14.5" fill="var(--night-950)"/></svg></div>
        <div class="prop prop--ball2"><svg viewBox="0 0 40 40"><circle cx="20" cy="20" r="18" fill="var(--chalk-050)" stroke="var(--night-950)" stroke-width="1.4"/><polygon points="20,10 26,14.5 24,21.5 16,21.5 14,14.5" fill="var(--night-950)"/></svg></div>
        <div class="prop prop--flag"><svg viewBox="0 0 30 46" fill="none"><path d="M4 44V4" stroke="var(--lime-300)" stroke-width="1.8" stroke-linecap="round"/><path d="M4 5l20 6-20 6V5Z" fill="var(--lime-300)" opacity=".85"/></svg></div>
        <div class="prop prop--trophy"><svg viewBox="0 0 40 46" fill="none"><path d="M11 6h18v10a9 9 0 0 1-18 0V6Z" stroke="var(--amber-400)" stroke-width="1.6"/><path d="M20 25v6M13 37h14l-1.6-4.5a2 2 0 0 0-1.9-1.5h-6.9a2 2 0 0 0-1.9 1.5L13 37Z" stroke="var(--amber-400)" stroke-width="1.6" stroke-linejoin="round"/></svg></div>
      </div>
    </div>

    <!-- Header -->
    <header class="page__header reveal">
      <h1 class="page__title">Quản lý bài viết &amp; Tin tức</h1>
      <p class="page__desc">Quản lý toàn bộ bài viết, tin tức giải đấu và sự kiện trên hệ thống <span class="brand">Dream League</span></p>
    </header>

    <!-- Stat strip -->
    <section class="stats">
      <div class="stat-card reveal" style="--d:0s">
        <div class="stat-card__icon stat-card__icon--total">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M4 7V4h16v3"/><path d="M9 20h6"/><path d="M12 4v16"/></svg>
        </div>
        <div>
          <p class="stat-card__value">{{ posts.length }}</p>
          <p class="stat-card__label">Tổng số bài viết</p>
        </div>
      </div>

      <div class="stat-card reveal" style="--d:.06s">
        <div class="stat-card__icon stat-card__icon--active">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/><path d="M8.5 12.5l2.5 2.5 5-5"/></svg>
        </div>
        <div>
          <p class="stat-card__value">{{ posts.filter(p => p.status === 'published').length }}</p>
          <p class="stat-card__label">Đã xuất bản</p>
        </div>
      </div>

      <div class="stat-card reveal" style="--d:.12s">
        <div class="stat-card__icon stat-card__icon--warn">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/></svg>
        </div>
        <div>
          <p class="stat-card__value">{{ posts.filter(p => p.status === 'draft').length }}</p>
          <p class="stat-card__label">Bản nháp / Chờ duyệt</p>
        </div>
      </div>

      <div class="stat-card reveal" style="--d:.18s">
        <div class="stat-card__icon stat-card__icon--views">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
        </div>
        <div>
          <p class="stat-card__value">{{ totalViews.toLocaleString('vi-VN') }}</p>
          <p class="stat-card__label">Tổng lượt xem</p>
        </div>
      </div>
    </section>

    <!-- Toolbar -->
    <div class="toolbar reveal" style="--d:.22s">
      <div class="toolbar__filters">
        <div class="search">
          <svg class="search__icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="7"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
          <input v-model="searchQuery" placeholder="Tìm kiếm theo tiêu đề bài viết..." />
        </div>

        <select v-model="selectedStatus" @change="fetchPosts" class="filter-select">
          <option value="">Tất cả trạng thái</option>
          <option value="published">Đã xuất bản</option>
          <option value="draft">Bản nháp</option>
          <option value="archived">Đã ẩn</option>
        </select>
      </div>

      <button class="btn btn--primary" @click="openCreateModal">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        Tạo bài viết mới
      </button>
    </div>

    <!-- Table -->
    <div class="table-card reveal" style="--d:.28s">

      <div v-if="loading" class="state">
        <div class="spinner"></div>
        <p>Đang tải danh sách bài viết...</p>
      </div>

      <div v-else-if="filteredPosts.length === 0" class="state">
        <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><rect x="4" y="4" width="16" height="16" rx="2"/><path d="M4 9h16M9 4v16"/><line x1="14" y1="14" x2="18" y2="18"/><line x1="18" y1="14" x2="14" y2="18"/></svg>
        <p>Không tìm thấy bài viết nào phù hợp.</p>
      </div>

      <table v-else>
        <thead>
          <tr>
            <th>Ảnh</th>
            <th>Tiêu đề &amp; Tóm tắt</th>
            <th>Lượt xem</th>
            <th>Trạng thái</th>
            <th>Ngày tạo</th>
            <th class="col-actions">Thao tác</th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="post in filteredPosts" :key="post.id">

            <td><img :src="post.thumbnailUrl || 'https://via.placeholder.com/150'" class="thumb-img" alt="" /></td>

            <td>
              <p class="post-title">{{ post.title }}</p>
              <p class="post-summary">{{ post.summary || 'Chưa có tóm tắt...' }}</p>
            </td>

            <td>
              <span class="metric">
                <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                {{ post.viewsCount || 0 }}
              </span>
            </td>

            <td>
              <span class="badge" :class="statusMeta(post.status).class">
                <span class="badge__dot"></span>
                {{ statusMeta(post.status).label }}
              </span>
            </td>

            <td class="cell-muted">{{ formatDate(post.createdAt) }}</td>

            <td class="col-actions">
              <button class="icon-btn icon-btn--edit" title="Chỉnh sửa" @click="openEditModal(post)">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 20h9"/><path d="M16.5 3.5a2.12 2.12 0 0 1 3 3L7 19l-4 1 1-4 12.5-12.5z"/></svg>
              </button>
              <button class="icon-btn icon-btn--delete" title="Xóa bài" @click="openDeleteModal(post)">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/><path d="M10 11v6"/><path d="M14 11v6"/><path d="M9 6V4a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2v2"/></svg>
              </button>
            </td>

          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal Thêm/Sửa -->
    <Transition name="fade">
      <div class="overlay" v-if="showModal" @click.self="closeModal">
        <div class="modal modal--wide">

          <div class="modal__header">
            <div class="modal__icon">
              <svg v-if="!isEditing" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M4 7V4h16v3"/><path d="M9 20h6"/><path d="M12 4v16"/></svg>
              <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 20h9"/><path d="M16.5 3.5a2.12 2.12 0 0 1 3 3L7 19l-4 1 1-4 12.5-12.5z"/></svg>
            </div>
            <div>
              <h2>{{ isEditing ? "Cập nhật bài viết" : "Thêm bài viết mới" }}</h2>
              <p>{{ isEditing ? "Chỉnh sửa nội dung và trạng thái bài viết" : "Soạn tin tức hoặc bài viết mới cho hệ thống" }}</p>
            </div>
            <button class="modal__close" @click="closeModal">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </div>

          <form @submit.prevent="savePost">
            <div class="modal__body">

              <div class="field">
                <label>Tiêu đề bài viết <span class="required">*</span></label>
                <input v-model="form.title" @input="onTitleInput" required placeholder="Nhập tiêu đề..." />
              </div>

              <div class="field">
                <label>Slug (đường dẫn tĩnh)</label>
                <input v-model="form.slug" placeholder="tudong-tao-slug" />
              </div>

              <div class="field">
                <label>Ảnh đại diện (Thumbnail URL)</label>
                <div class="image-picker">
                  <img :src="form.thumbnailUrl || 'https://via.placeholder.com/150'" class="image-picker__preview" alt="" />
                  <input v-model="form.thumbnailUrl" placeholder="https://example.com/image.jpg" />
                </div>
              </div>

              <div class="field">
                <label>Tóm tắt bài viết</label>
                <textarea v-model="form.summary" rows="2" placeholder="Tóm tắt ngắn hiển thị ở trang danh sách..."></textarea>
              </div>

              <div class="field">
                <label>Nội dung chi tiết <span class="required">*</span></label>
                <textarea v-model="form.content" rows="6" required placeholder="Nhập nội dung bài viết..."></textarea>
              </div>

              <div class="field">
                <label>Trạng thái bài viết</label>
                <select v-model="form.status">
                  <option value="published">Đã xuất bản (Công khai)</option>
                  <option value="draft">Bản nháp (Lưu nội bộ)</option>
                  <option value="archived">Đã ẩn</option>
                </select>
              </div>

            </div>

            <div class="modal__footer">
              <button type="button" class="btn btn--ghost" @click="closeModal">Hủy</button>
              <button type="submit" class="btn btn--primary" :disabled="submitting">
                {{ submitting ? "Đang lưu..." : (isEditing ? "Cập nhật" : "Thêm mới") }}
              </button>
            </div>
          </form>

        </div>
      </div>
    </Transition>

    <!-- Modal Xóa -->
    <Transition name="fade">
      <div class="overlay" v-if="showDelete" @click.self="showDelete = false">
        <div class="modal modal--sm">

          <div class="modal__icon modal__icon--danger">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/><path d="M10 11v6"/><path d="M14 11v6"/><path d="M9 6V4a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2v2"/></svg>
          </div>

          <h2>Xóa bài viết?</h2>
          <p class="modal__text">
            Bạn có chắc muốn xóa <strong>{{ postDangXoa?.title }}</strong>?
            Hành động này không thể hoàn tác.
          </p>

          <div class="modal__footer modal__footer--center">
            <button class="btn btn--ghost" @click="showDelete = false">Hủy</button>
            <button class="btn btn--danger" :disabled="deleting" @click="deletePost">
              {{ deleting ? "Đang xóa..." : "Xóa bài viết" }}
            </button>
          </div>

        </div>
      </div>
    </Transition>

  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, nextTick } from 'vue'

const API_BASE_URL = '/api/posts'

const posts = ref([])
const loading = ref(false)
const submitting = ref(false)
const showModal = ref(false)
const isEditing = ref(false)
const editingId = ref(null)

const showDelete = ref(false)
const deleting = ref(false)
const postDangXoa = ref(null)

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

// Filter dữ liệu client theo tiêu đề
const filteredPosts = computed(() => {
  return posts.value.filter(post => {
    return post.title.toLowerCase().includes(searchQuery.value.toLowerCase())
  })
})

function statusMeta(status) {
  if (status === 'published') return { label: 'Đã xuất bản', class: 'badge--published' }
  if (status === 'draft') return { label: 'Bản nháp', class: 'badge--draft' }
  return { label: 'Đã ẩn', class: 'badge--archived' }
}

// ===== Hiệu ứng xuất hiện khi cuộn tới =====
let revealObserver = null

onMounted(() => {
  fetchPosts()

  revealObserver = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('is-visible')
        revealObserver.unobserve(entry.target)
      }
    })
  }, { threshold: 0.1 })

  nextTick(() => document.querySelectorAll('.reveal').forEach(el => revealObserver.observe(el)))
})

onUnmounted(() => {
  if (revealObserver) revealObserver.disconnect()
})

function getToken() {
  return localStorage.getItem('token') || localStorage.getItem('accessToken') || localStorage.getItem('jwt')
}

async function fetchPosts() {
  loading.value = true
  try {
    let url = API_BASE_URL
    if (selectedStatus.value) {
      url += `?status=${selectedStatus.value}`
    }

    const res = await fetch(url, {
      headers: { 'Authorization': `Bearer ${getToken()}` }
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
  const token = getToken()

  if (!token) {
    alert('Vui lòng đăng nhập lại!')
    submitting.value = false
    return
  }

  const method = isEditing.value ? 'PUT' : 'POST'
  const url = isEditing.value ? `${API_BASE_URL}/${editingId.value}` : API_BASE_URL

  try {
    const res = await fetch(url, {
      method,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(form)
    })

    if (res.ok) {
      closeModal()
      await fetchPosts()
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

function openDeleteModal(post) {
  postDangXoa.value = post
  showDelete.value = true
}

async function deletePost() {
  deleting.value = true
  try {
    const res = await fetch(`${API_BASE_URL}/${postDangXoa.value.id}`, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })

    if (res.ok) {
      await fetchPosts()
      showDelete.value = false
    } else {
      const errorText = await res.text()
      alert(`Xóa thất bại (${res.status}): ${errorText}`)
    }
  } catch (err) {
    console.error('Lỗi khi xóa bài viết:', err)
  } finally {
    deleting.value = false
  }
}

function formatDate(dateStr) {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('vi-VN')
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Oswald:wght@500;600;700&family=Manrope:wght@400;500;600;700;800&family=Space+Mono:wght@400;700&display=swap');

.page{
    --night-950:#050b08;
    --night-800:#0a1f13;
    --night-700:#123321;
    --turf-500:#23935a;
    --turf-700:#146239;
    --lime-400:#b6ff3c;
    --lime-300:#d3ff8f;
    --sky-400:#38bdf8;
    --sky-300:#7dd3fc;
    --purple-400:#a78bfa;
    --amber-400:#ffb020;
    --crimson-500:#ff4757;
    --chalk-050:#f7fbf4;
    --chalk-200:#e3ecdf;

    --font-display:'Oswald','Manrope',sans-serif;
    --font-body:'Manrope',sans-serif;
    --font-mono:'Space Mono',monospace;

    position:relative;
    background:var(--night-950);
    color:var(--chalk-050);
    font-family:var(--font-body);
    border-radius:20px;
    padding:32px;
    overflow:hidden;
    min-height:calc(100vh - 40px);
}

/* ===== REVEAL ===== */
.reveal{ opacity:0; transform:translateY(18px); transition:opacity .6s ease, transform .6s ease; transition-delay:var(--d,0s); }
.reveal.is-visible{ opacity:1; transform:translateY(0); }
@media (prefers-reduced-motion: reduce){ .reveal{ opacity:1; transform:none; transition:none; } }

/* ===== AMBIENT: đèn pha + hạt sáng + hạt rơi + đạo cụ ===== */
.page__ambient{ position:absolute; inset:0; z-index:0; pointer-events:none; border-radius:20px; overflow:hidden; }
.floodlight{ position:absolute; top:-20%; width:40vh; height:100vh; background:conic-gradient(from 90deg at 50% 0%, transparent 42%, rgba(255,244,214,.07) 50%, transparent 58%); mix-blend-mode:screen; animation:sweep 11s ease-in-out infinite alternate; }
.floodlight--l{ left:-6%; }
.floodlight--r{ right:-6%; animation-direction:alternate-reverse; }
@keyframes sweep{ 0%{ transform:rotate(-8deg); } 100%{ transform:rotate(8deg); } }

.motes{ position:absolute; inset:0; }
.mote{ position:absolute; bottom:-10px; left:calc((var(--i) * 8.6%) + 1%); width:3px; height:3px; border-radius:50%; background:var(--lime-300); opacity:0; animation:rise 9s linear infinite; animation-delay:calc(var(--i) * -0.7s); }
@keyframes rise{ 0%{ transform:translateY(0) scale(.6); opacity:0; } 10%{ opacity:.5; } 90%{ opacity:.1; } 100%{ transform:translateY(-100%) scale(1.1); opacity:0; } }

/* Hạt rơi */
.falling{ position:absolute; inset:0; overflow:hidden; }
.falling__item{
  position:absolute; top:-6%; left:calc((var(--i) * 6.2%) + 1%);
  width:3px; height:3px; border-radius:50%; background:var(--chalk-050);
  opacity:0; animation:fall linear infinite;
  animation-duration:calc(7s + (var(--i) * 0.45s));
  animation-delay:calc(var(--i) * -0.6s);
}
@keyframes fall{
  0%{ transform:translateY(0) translateX(0); opacity:0; }
  8%{ opacity:.5; }
  50%{ transform:translateY(48vh) translateX(14px); }
  92%{ opacity:.12; }
  100%{ transform:translateY(100vh) translateX(-10px); opacity:0; }
}

.field-props{ position:absolute; inset:0; }
.prop{ position:absolute; opacity:.1; animation:prop-float 7s ease-in-out infinite; }
.prop svg{ width:100%; height:100%; }
.prop--ball1{ width:44px; height:44px; top:8%; right:8%; }
.prop--ball2{ width:26px; height:26px; bottom:16%; left:5%; opacity:.08; animation-delay:.9s; animation-duration:8.5s; }
.prop--flag{ width:30px; height:44px; top:22%; left:3%; animation-delay:1.1s; }
.prop--trophy{ width:38px; height:44px; bottom:10%; right:22%; animation-delay:1.6s; }
@keyframes prop-float{ 0%,100%{ transform:translateY(0) rotate(-3deg); } 50%{ transform:translateY(-14px) rotate(3deg); } }

@media (prefers-reduced-motion: reduce){
  .floodlight, .mote, .falling__item, .prop{ animation:none; }
}

/* Header */
.page__header{ position:relative; z-index:1; }
.page__title{
    font-family:var(--font-display);
    font-size:28px;
    font-weight:600;
    letter-spacing:-.01em;
    margin-bottom:6px;
    color:var(--chalk-050);
}

.page__desc{
    color:var(--chalk-200);
    opacity:.75;
    font-size:14.5px;
    margin-bottom:26px;
}

.brand{
    color:var(--lime-300);
    font-weight:700;
    text-shadow:0 0 10px rgba(182,255,60,.35);
}

/* Stats */
.stats{
    position:relative; z-index:1;
    display:grid;
    grid-template-columns:repeat(4,1fr);
    gap:16px;
    margin-bottom:22px;
}

.stat-card{
    background:rgba(247,251,244,.04);
    border:1px solid rgba(247,251,244,.1);
    border-radius:16px;
    padding:18px 20px;
    display:flex;
    align-items:center;
    gap:14px;
    transition:transform .2s ease, box-shadow .2s ease, border-color .2s ease;
}
.stat-card:hover{ transform:translateY(-3px); border-color:rgba(182,255,60,.3); box-shadow:0 14px 28px -16px rgba(182,255,60,.25); }

.stat-card__icon{
    width:42px;
    height:42px;
    min-width:42px;
    border-radius:11px;
    display:flex;
    align-items:center;
    justify-content:center;
}

.stat-card__icon--total{ background:rgba(56,189,248,.14); color:var(--sky-300); }
.stat-card__icon--active{ background:rgba(182,255,60,.14); color:var(--lime-300); }
.stat-card__icon--warn{ background:rgba(255,176,32,.14); color:var(--amber-400); }
.stat-card__icon--views{ background:rgba(167,139,250,.16); color:var(--purple-400); }

.stat-card__value{
    font-family:var(--font-display);
    font-size:22px;
    font-weight:600;
    line-height:1.1;
    color:var(--chalk-050);
}

.stat-card__label{
    color:var(--chalk-200);
    opacity:.7;
    font-size:12.5px;
    margin-top:2px;
}

/* Toolbar */
.toolbar{
    position:relative; z-index:1;
    display:flex;
    justify-content:space-between;
    align-items:center;
    gap:16px;
    margin-bottom:18px;
    flex-wrap:wrap;
}

.toolbar__filters{
    display:flex;
    gap:10px;
    flex-wrap:wrap;
}

.search{ position:relative; width:300px; }

.search__icon{
    position:absolute;
    left:14px;
    top:50%;
    transform:translateY(-50%);
    color:rgba(247,251,244,.4);
}

.search input{
    width:100%;
    padding:11px 14px 11px 42px;
    border:1px solid rgba(247,251,244,.14);
    border-radius:10px;
    background:rgba(5,11,8,.5);
    color:var(--chalk-050);
    font-family:inherit;
    font-size:14px;
    outline:none;
    transition:border-color .15s, box-shadow .15s;
}
.search input::placeholder{ color:rgba(247,251,244,.4); }

.search input:focus{
    border-color:var(--lime-400);
    box-shadow:0 0 0 3px rgba(182,255,60,.15);
}

.filter-select{
    padding:11px 14px;
    border:1px solid rgba(247,251,244,.14);
    border-radius:10px;
    background:rgba(5,11,8,.5);
    font-family:inherit;
    font-size:14px;
    color:var(--chalk-050);
    outline:none;
    cursor:pointer;
    transition:border-color .15s, box-shadow .15s;
}

.filter-select:focus{
    border-color:var(--lime-400);
    box-shadow:0 0 0 3px rgba(182,255,60,.15);
}

.btn{
    display:inline-flex;
    align-items:center;
    gap:8px;
    border:none;
    border-radius:10px;
    padding:11px 20px;
    font-family:inherit;
    font-size:14px;
    font-weight:600;
    cursor:pointer;
    transition:background .15s, transform .05s, opacity .15s, box-shadow .15s;
}

.btn:active{ transform:scale(.98); }
.btn:disabled{ opacity:.6; cursor:not-allowed; }

.btn--primary{ background:var(--turf-500); color:var(--chalk-050); }
.btn--primary:hover:not(:disabled){ background:var(--lime-400); color:var(--night-950); box-shadow:0 8px 20px -8px rgba(182,255,60,.5); }

.btn--ghost{ background:rgba(247,251,244,.06); color:var(--chalk-050); border:1px solid rgba(247,251,244,.14); }
.btn--ghost:hover{ background:rgba(247,251,244,.12); }

.btn--danger{ background:var(--crimson-500); color:#fff; }
.btn--danger:hover:not(:disabled){ background:#e8384a; box-shadow:0 8px 20px -8px rgba(255,71,87,.5); }

/* Table */
.table-card{
    position:relative; z-index:1;
    background:rgba(247,251,244,.03);
    border:1px solid rgba(247,251,244,.09);
    border-radius:16px;
    overflow:hidden;
}

table{ width:100%; border-collapse:collapse; }

th{
    background:rgba(247,251,244,.04);
    padding:14px 18px;
    text-align:left;
    font-size:11.5px;
    font-weight:700;
    text-transform:uppercase;
    letter-spacing:.05em;
    color:var(--chalk-200);
    opacity:.7;
    border-bottom:1px solid rgba(247,251,244,.1);
}

td{
    padding:13px 18px;
    border-bottom:1px solid rgba(247,251,244,.07);
    vertical-align:middle;
    color:var(--chalk-050);
}

tbody tr:last-child td{ border-bottom:none; }
tbody tr:hover{ background:rgba(182,255,60,.045); }

.col-actions{ width:90px; }

.cell-muted{ color:var(--chalk-200); opacity:.7; font-size:13px; }

.thumb-img{
    width:60px;
    height:42px;
    border-radius:8px;
    object-fit:cover;
    border:1px solid rgba(247,251,244,.12);
}

.post-title{ font-weight:700; font-size:14px; color:var(--chalk-050); line-height:1.3; }
.post-summary{
    font-size:12px;
    color:var(--chalk-200);
    opacity:.65;
    margin-top:2px;
    display:-webkit-box;
    -webkit-line-clamp:1;
    -webkit-box-orient:vertical;
    overflow:hidden;
}

.metric{
    display:inline-flex;
    align-items:center;
    gap:5px;
    font-family:var(--font-mono);
    font-weight:700;
    font-size:12.5px;
    color:var(--lime-300);
}

/* Badges */
.badge{
    display:inline-flex;
    align-items:center;
    gap:6px;
    padding:5px 12px;
    border-radius:20px;
    font-size:12.5px;
    font-weight:700;
}

.badge__dot{ width:6px; height:6px; border-radius:50%; background:currentColor; }

.badge--published{ background:rgba(182,255,60,.14); color:var(--lime-300); }
.badge--draft{ background:rgba(255,176,32,.14); color:var(--amber-400); }
.badge--archived{ background:rgba(167,139,250,.16); color:var(--purple-400); }

/* Icon buttons */
.icon-btn{
    width:32px;
    height:32px;
    border:none;
    border-radius:8px;
    display:inline-flex;
    align-items:center;
    justify-content:center;
    cursor:pointer;
    margin-right:6px;
    transition:background .15s, color .15s, box-shadow .15s;
}

.icon-btn--edit{ background:rgba(56,189,248,.14); color:var(--sky-300); }
.icon-btn--edit:hover{ background:var(--sky-400); color:var(--night-950); box-shadow:0 0 12px -2px rgba(56,189,248,.6); }

.icon-btn--delete{ background:rgba(247,251,244,.07); color:var(--chalk-200); }
.icon-btn--delete:hover{ background:var(--crimson-500); color:#fff; box-shadow:0 0 12px -2px rgba(255,71,87,.6); }

/* Loading / empty state */
.state{
    display:flex;
    flex-direction:column;
    align-items:center;
    justify-content:center;
    gap:12px;
    padding:64px 20px;
    color:var(--chalk-200);
    opacity:.75;
    font-size:14px;
}

.spinner{
    width:30px;
    height:30px;
    border:3px solid rgba(247,251,244,.15);
    border-top-color:var(--lime-400);
    border-radius:50%;
    animation:spin .7s linear infinite;
}

@keyframes spin{ to{ transform:rotate(360deg); } }

/* Modal */
.overlay{
    position:fixed;
    inset:0;
    background:rgba(5,11,8,.72);
    backdrop-filter:blur(3px);
    display:flex;
    justify-content:center;
    align-items:center;
    z-index:999;
    padding:20px;
}

.modal{
    width:460px;
    max-width:100%;
    max-height:90vh;
    overflow-y:auto;
    background:var(--night-800);
    border:1px solid rgba(182,255,60,.18);
    border-radius:16px;
    box-shadow:0 24px 60px rgba(0,0,0,.5);
    color:var(--chalk-050);
}

.modal--wide{ width:560px; }

.modal--sm{
    width:400px;
    padding:32px;
    text-align:center;
}

.modal__header{
    display:flex;
    align-items:flex-start;
    gap:14px;
    padding:24px 24px 18px;
    border-bottom:1px solid rgba(247,251,244,.1);
}

.modal__header h2{
    font-family:var(--font-display);
    font-size:18px;
    font-weight:700;
    color:var(--chalk-050);
}

.modal__header p{
    color:var(--chalk-200);
    opacity:.7;
    font-size:13px;
    margin-top:2px;
}

.modal__icon{
    width:40px;
    height:40px;
    min-width:40px;
    border-radius:10px;
    background:rgba(182,255,60,.14);
    color:var(--lime-300);
    display:flex;
    align-items:center;
    justify-content:center;
}

.modal--sm .modal__icon{ margin:0 auto; }

.modal__icon--danger{
    width:52px;
    height:52px;
    border-radius:50%;
    background:rgba(255,71,87,.14);
    color:#ff9686;
}

.modal--sm h2{
    font-family:var(--font-display);
    font-size:18px;
    font-weight:700;
    margin:18px 0 8px;
    color:var(--chalk-050);
}

.modal__text{
    color:var(--chalk-200);
    opacity:.75;
    font-size:14px;
    line-height:1.5;
}

.modal__text strong{ color:var(--chalk-050); opacity:1; }

.modal__close{
    margin-left:auto;
    width:30px;
    height:30px;
    border:none;
    background:rgba(247,251,244,.06);
    border-radius:8px;
    color:var(--chalk-200);
    display:flex;
    align-items:center;
    justify-content:center;
    cursor:pointer;
}

.modal__close:hover{ background:rgba(247,251,244,.14); color:var(--chalk-050); }

.modal__body{
    padding:20px 24px;
    display:flex;
    flex-direction:column;
    gap:15px;
}

.field{ display:flex; flex-direction:column; gap:6px; }

.field label{ font-size:12.5px; font-weight:700; color:var(--chalk-200); opacity:.8; }

.field .required{ color:var(--crimson-500); opacity:1; }

.field input,
.field select,
.field textarea{
    padding:10px 12px;
    border:1px solid rgba(247,251,244,.14);
    border-radius:9px;
    font-family:inherit;
    font-size:14px;
    outline:none;
    background:rgba(5,11,8,.5);
    color:var(--chalk-050);
    transition:border-color .15s, box-shadow .15s;
    resize:vertical;
}

.field input:focus,
.field select:focus,
.field textarea:focus{
    border-color:var(--lime-400);
    box-shadow:0 0 0 3px rgba(182,255,60,.15);
}

.field input::placeholder,
.field textarea::placeholder{ color:rgba(247,251,244,.35); }

.field select option{ background:var(--night-800); color:var(--chalk-050); }

/* Ảnh xem trước cho thumbnail */
.image-picker{ display:flex; align-items:center; gap:10px; }

.image-picker__preview{
    width:52px;
    height:38px;
    min-width:52px;
    border-radius:8px;
    object-fit:cover;
    border:1px solid rgba(247,251,244,.14);
}

.image-picker input{ flex:1; }

.modal__footer{
    display:flex;
    justify-content:flex-end;
    gap:10px;
    padding:18px 24px 24px;
}

.modal__footer--center{ justify-content:center; margin-top:22px; padding:0; }

/* Transition */
.fade-enter-active,
.fade-leave-active{ transition:opacity .18s ease; }

.fade-enter-from,
.fade-leave-to{ opacity:0; }

.fade-enter-active .modal,
.fade-leave-active .modal{ transition:transform .18s ease, opacity .18s ease; }

.fade-enter-from .modal,
.fade-leave-to .modal{ transform:scale(.96); opacity:0; }

/* Responsive */
@media(max-width:900px){
    .stats{ grid-template-columns:repeat(2,1fr); }
}

@media(max-width:768px){
    .toolbar{ flex-direction:column; align-items:stretch; }
    .toolbar__filters{ flex-direction:column; }
    .search{ width:100%; }
    .filter-select{ width:100%; }
    .table-card{ overflow-x:auto; }
    table{ font-size:13px; min-width:680px; }
}

@media(max-width:520px){
    .stats{ grid-template-columns:1fr 1fr; }
}
</style>