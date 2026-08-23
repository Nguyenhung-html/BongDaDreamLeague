<template>
  <div class="phan-hoi">
    <!-- Nền ambient đồng bộ Dashboard -->
    <div class="phan-hoi__ambient" aria-hidden="true">
      <div class="floodlight floodlight--l"></div>
      <div class="floodlight floodlight--r"></div>
      <div class="motes"><span v-for="n in 10" :key="'m' + n" class="mote" :style="{ '--i': n }"></span></div>
      <!-- Hạt rơi nhẹ xuống, giống hiệu ứng bên Dashboard -->
      <div class="falling"><span v-for="n in 18" :key="'f' + n" class="falling__item" :style="{ '--i': n }"></span></div>
    </div>

    <!-- Header -->
    <div class="phan-hoi__header reveal">
      <span class="eyebrow">Truyền thông · Phản hồi</span>
      <h1 class="phan-hoi__title">Quản lý Phản hồi Đánh giá</h1>
      <p class="phan-hoi__desc">Xem đánh giá từ khách hàng và gửi phản hồi đại diện cho sân bóng.</p>
    </div>

    <!-- Quick Stats — tick vào ô nào sẽ lọc bảng theo đúng trạng thái đó -->
    <div class="stat-grid">
      <button
        type="button"
        class="stat-card stat-card--all reveal"
        style="--d: 0s"
        :class="{ 'stat-card--active': filterStatus === 'all' }"
        @click="setFilterStatus('all')"
      >
        <span class="stat-check">
          <svg v-if="filterStatus === 'all'" viewBox="0 0 24 24" width="12" height="12" fill="none"><path d="M5 13L9.5 17.5L19 7" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/></svg>
        </span>
        <div class="stat-card__icon">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none"><path d="M21 11.5a8.4 8.4 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.4 8.4 0 0 1-3.8-.9L3 21l1.9-5.7a8.4 8.4 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.4 8.4 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5Z" stroke="currentColor" stroke-width="1.6" stroke-linejoin="round"/></svg>
        </div>
        <p class="stat-card__label">Tổng số đánh giá</p>
        <p class="stat-card__num">{{ tongSoDanhGia }}</p>
      </button>

      <button
        type="button"
        class="stat-card stat-card--replied reveal"
        style="--d: .08s"
        :class="{ 'stat-card--active': filterStatus === 'replied' }"
        @click="setFilterStatus('replied')"
      >
        <span class="stat-check">
          <svg v-if="filterStatus === 'replied'" viewBox="0 0 24 24" width="12" height="12" fill="none"><path d="M5 13L9.5 17.5L19 7" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/></svg>
        </span>
        <div class="stat-card__icon">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none"><path d="M5 13l4 4L19 7" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/></svg>
        </div>
        <p class="stat-card__label">Đã phản hồi</p>
        <p class="stat-card__num">{{ daPhanHoiCount }}</p>
      </button>

      <button
        type="button"
        class="stat-card stat-card--unreplied reveal"
        style="--d: .16s"
        :class="{ 'stat-card--active': filterStatus === 'unreplied' }"
        @click="setFilterStatus('unreplied')"
      >
        <span class="stat-check">
          <svg v-if="filterStatus === 'unreplied'" viewBox="0 0 24 24" width="12" height="12" fill="none"><path d="M5 13L9.5 17.5L19 7" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/></svg>
        </span>
        <div class="stat-card__icon">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="9" stroke="currentColor" stroke-width="1.6"/><path d="M12 7.5v5.5l3.5 2" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"/></svg>
        </div>
        <p class="stat-card__label">Chưa phản hồi</p>
        <p class="stat-card__num">{{ chuaPhanHoiCount }}</p>
        <p v-if="chuaPhanHoiCount > 0" class="stat-card__note is-warning">● Cần phản hồi</p>
      </button>
    </div>

    <!-- Filter & Search Bar -->
    <div class="filter-card reveal" style="--d: .22s">
      <div class="search-box">
        <svg viewBox="0 0 24 24" fill="none" width="17" height="17"><circle cx="11" cy="11" r="8" stroke="currentColor" stroke-width="1.7"/><path d="M21 21l-4.35-4.35" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/></svg>
        <input type="text" v-model="searchQuery" placeholder="Tìm theo tên khách hàng hoặc nội dung..." />
      </div>

      <div class="date-range">
        <span class="date-range__label">Khung giờ phản hồi</span>
        <input type="datetime-local" v-model="tuNgayPhanHoi" class="date-input" />
        <span class="date-range__sep">→</span>
        <input type="datetime-local" v-model="denNgayPhanHoi" class="date-input" />
        <button
          v-if="tuNgayPhanHoi || denNgayPhanHoi"
          type="button"
          class="btn-clear-date"
          title="Xoá bộ lọc khung giờ"
          @click="clearDateFilter"
        >
          &times;
        </button>
      </div>
    </div>

    <!-- Table Reviews -->
    <div class="table-card reveal" style="--d: .3s">
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>Đang tải danh sách đánh giá...</p>
      </div>

      <table v-else-if="filteredReviews.length > 0" class="custom-table">
        <thead>
          <tr>
            <th width="170">Khách hàng</th>
            <th width="100">Đánh giá</th>
            <th>Nội dung nhận xét</th>
            <th width="260">Phản hồi từ sân</th>
            <th width="110">Ngày tạo</th>
            <th width="110" class="text-right">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="review in filteredReviews" :key="review.id">
            <td>
              <div class="user-info">
                <div class="user-avatar">{{ getInitials(review.userName || review.name) }}</div>
                <div>
                  <div class="user-name">{{ review.userName || review.name || 'Khách hàng' }}</div>
                  <div class="user-email">{{ review.userEmail }}</div>
                </div>
              </div>
            </td>
            <td>
              <div class="star-rating">
                <span v-for="star in 5" :key="star" :class="{ active: star <= review.rating }">★</span>
              </div>
            </td>
            <td>
              <p class="review-comment">{{ review.quote || review.comment }}</p>
            </td>
            <td>
              <div v-if="review.replyContent || review.phanHoi" class="reply-box">
                <span class="reply-tag">Staff:</span> {{ review.replyContent || review.phanHoi }}
                <div class="reply-time">
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="9" stroke="currentColor" stroke-width="1.8"/><path d="M12 7v5l3 2" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/></svg>
                  {{ formatDateTime(getNgayPhanHoi(review)) }}
                </div>
              </div>
              <span v-else class="text-muted">Chưa có phản hồi</span>
            </td>
            <td class="date-text">
              {{ formatDate(review.ngayDanhGia || review.createdAt) }}
            </td>
            <td class="text-right">
              <button class="btn-reply" @click="openReplyModal(review)">
                {{ (review.replyContent || review.phanHoi) ? 'Sửa phản hồi' : 'Phản hồi' }}
              </button>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-else class="empty-state">
        <div class="empty-state__icon">
          <svg width="26" height="26" viewBox="0 0 24 24" fill="none"><circle cx="11" cy="11" r="7" stroke="currentColor" stroke-width="1.6"/><path d="M21 21l-4.3-4.3" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"/></svg>
        </div>
        <p class="empty-state__title">Không tìm thấy đánh giá nào phù hợp</p>
        <p class="empty-state__desc">Thử đổi bộ lọc trạng thái, khung giờ phản hồi hoặc từ khoá tìm kiếm.</p>
      </div>
    </div>

    <!-- Modal Reply -->
    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-box">
        <div class="modal-header">
          <h3>Phản hồi đánh giá của {{ selectedReview?.userName || selectedReview?.name || 'Khách hàng' }}</h3>
          <button class="btn-close" @click="closeModal">&times;</button>
        </div>
        <div class="modal-body">
          <div class="customer-quote">
            <div class="star-rating">
              <span v-for="star in 5" :key="star" :class="{ active: star <= selectedReview?.rating }">★</span>
            </div>
            <p class="quote-text">"{{ selectedReview?.quote || selectedReview?.comment }}"</p>
          </div>

          <div v-if="selectedReview && (selectedReview.replyContent || selectedReview.phanHoi)" class="existing-reply-note">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="9" stroke="currentColor" stroke-width="1.8"/><path d="M12 7v5l3 2" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/></svg>
            Đã phản hồi lúc {{ formatDateTime(getNgayPhanHoi(selectedReview)) }}
          </div>

          <div class="form-group">
            <label>Nội dung phản hồi <span class="required">*</span></label>
            <textarea v-model="replyInput" rows="4" placeholder="Nhập câu trả lời đại diện cho sân bóng..."></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn-cancel" @click="closeModal">Hủy</button>
          <button type="button" class="btn-primary" :disabled="submitting || !replyInput.trim()" @click="submitReply">
            {{ submitting ? 'Đang gửi...' : 'Gửi phản hồi' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'

const API_BASE_URL = '/api/danh-gia-he-thong'
const reviews = ref([])
const loading = ref(false)
const submitting = ref(false)
const showModal = ref(false)
const selectedReview = ref(null)
const replyInput = ref('')
const searchQuery = ref('')
const filterStatus = ref('all')

// Bộ lọc khung giờ phản hồi
const tuNgayPhanHoi = ref('')
const denNgayPhanHoi = ref('')

function setFilterStatus(status) {
  // Tick lại vào ô đang chọn thì quay về "Tất cả"; ngược lại chuyển sang trạng thái vừa tick
  filterStatus.value = filterStatus.value === status ? 'all' : status
}

function clearDateFilter() {
  tuNgayPhanHoi.value = ''
  denNgayPhanHoi.value = ''
}

function getNgayPhanHoi(review) {
  if (!review) return null
  return review.ngayPhanHoi || review.replyDate || review.ngayTraLoi || review.updatedAt || null
}

const filteredReviews = computed(() => {
  return reviews.value.filter(item => {
    const name = item.userName || item.name || ''
    const comment = item.quote || item.comment || ''
    const matchSearch =
      name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      comment.toLowerCase().includes(searchQuery.value.toLowerCase())

    let matchStatus = true
    const hasReply = !!(item.replyContent || item.phanHoi)
    if (filterStatus.value === 'unreplied') {
      matchStatus = !hasReply
    } else if (filterStatus.value === 'replied') {
      matchStatus = hasReply
    }

    // Lọc theo khung giờ phản hồi (chỉ áp dụng cho các đánh giá đã có phản hồi)
    let matchDate = true
    if (tuNgayPhanHoi.value || denNgayPhanHoi.value) {
      const ngayPH = getNgayPhanHoi(item)
      if (!hasReply || !ngayPH) {
        matchDate = false
      } else {
        const t = new Date(ngayPH).getTime()
        if (tuNgayPhanHoi.value && t < new Date(tuNgayPhanHoi.value).getTime()) matchDate = false
        if (denNgayPhanHoi.value && t > new Date(denNgayPhanHoi.value).getTime()) matchDate = false
      }
    }

    return matchSearch && matchStatus && matchDate
  })
})

let revealObserver = null

onMounted(() => {
  revealObserver = new IntersectionObserver(entries => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('is-visible')
        revealObserver.unobserve(entry.target)
      }
    })
  }, { threshold: 0.12 })
  nextTick(() => document.querySelectorAll('.reveal').forEach(el => revealObserver.observe(el)))

  fetchReviews()
})

onUnmounted(() => {
  if (revealObserver) revealObserver.disconnect()
})

async function fetchReviews() {
  loading.value = true
  try {
    const token = localStorage.getItem('token') || localStorage.getItem('accessToken') || localStorage.getItem('jwt')
    const res = await fetch(`${API_BASE_URL}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    if (res.ok) {
      reviews.value = await res.json()
    }
  } catch (err) {
    console.error('Lỗi khi tải danh sách đánh giá:', err)
  } finally {
    loading.value = false
  }
}

function openReplyModal(review) {
  selectedReview.value = review
  replyInput.value = review.replyContent || review.phanHoi || ''
  showModal.value = true
}

function closeModal() {
  showModal.value = false
  selectedReview.value = null
  replyInput.value = ''
}

async function submitReply() {
  if (!replyInput.value.trim() || !selectedReview.value) return

  const token = localStorage.getItem('token') || localStorage.getItem('accessToken')

  try {
    const res = await fetch(`/api/danh-gia-he-thong/${selectedReview.value.id}/phan-hoi`, {
      method: 'POST', // Khớp với @PostMapping ở Controller của bạn
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`
      },
      body: JSON.stringify({
        noiDungPhanHoi: replyInput.value.trim()
      })
    })

    if (res.ok) {
      alert('Đã gửi phản hồi thành công!')
      closeModal()
      fetchReviews() // Reload lại danh sách (kèm ngày giờ phản hồi mới từ server)
    } else {
      const errText = await res.text()
      alert(`Gửi thất bại: ${errText}`)
    }
  } catch (err) {
    console.error('Lỗi khi gửi phản hồi:', err)
  }
}

function getInitials(name) {
  if (!name) return 'K'
  const parts = name.trim().split(' ')
  return parts[parts.length - 1].charAt(0).toUpperCase()
}

function formatDate(dateStr) {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('vi-VN')
}

function formatDateTime(dateStr) {
  if (!dateStr) return '—'
  const d = new Date(dateStr)
  if (isNaN(d.getTime())) return '—'
  return d.toLocaleString('vi-VN', {
    hour: '2-digit',
    minute: '2-digit',
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  })
}

// 1. Tổng số đánh giá
const tongSoDanhGia = computed(() => (reviews.value ? reviews.value.length : 0))

// 2. Đã phản hồi
const daPhanHoiCount = computed(() => {
  if (!reviews.value) return 0
  return reviews.value.filter(item => (item.replyContent || item.phanHoi) && (item.replyContent || item.phanHoi).trim() !== '').length
})

// 3. Chưa phản hồi
const chuaPhanHoiCount = computed(() => {
  if (!reviews.value) return 0
  return reviews.value.filter(item => !(item.replyContent || item.phanHoi) || (item.replyContent || item.phanHoi).trim() === '').length
})
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Oswald:wght@500;600;700&family=Manrope:wght@400;500;600;700;800&family=Space+Mono:wght@400;700&display=swap');
</style>

<style scoped>
.phan-hoi {
  --night-950: #050b08;
  --night-800: #0a1f13;
  --night-700: #123321;
  --turf-500: #23935a;
  --turf-700: #146239;
  --lime-400: #b6ff3c;
  --lime-300: #d3ff8f;
  --amber-400: #ffb020;
  --crimson-500: #ff4757;
  --chalk-050: #f7fbf4;
  --chalk-200: #e3ecdf;
  --font-display: 'Oswald', 'Manrope', sans-serif;
  --font-body: 'Manrope', sans-serif;
  --font-mono: 'Space Mono', monospace;

  position: relative;
  display: flex;
  flex-direction: column;
  gap: 20px;
  background: var(--night-950);
  color: var(--chalk-050);
  font-family: var(--font-body);
  border-radius: 20px;
  padding: 32px;
  overflow: hidden;
  min-height: calc(100vh - 40px);
}

/* ===== REVEAL ===== */
.reveal { opacity: 0; transform: translateY(16px); transition: opacity .55s ease, transform .55s ease; transition-delay: var(--d, 0s); }
.reveal.is-visible { opacity: 1; transform: translateY(0); }
@media (prefers-reduced-motion: reduce) { .reveal { opacity: 1; transform: none; transition: none; } }

/* ===== AMBIENT ===== */
.phan-hoi__ambient { position: absolute; inset: 0; z-index: 0; pointer-events: none; border-radius: 20px; overflow: hidden; }
.floodlight { position: absolute; top: -20%; width: 40vh; height: 100vh; background: conic-gradient(from 90deg at 50% 0%, transparent 42%, rgba(255,244,214,.06) 50%, transparent 58%); mix-blend-mode: screen; animation: sweep 11s ease-in-out infinite alternate; }
.floodlight--l { left: -6%; }
.floodlight--r { right: -6%; animation-direction: alternate-reverse; }
@keyframes sweep { 0% { transform: rotate(-8deg); } 100% { transform: rotate(8deg); } }
.motes { position: absolute; inset: 0; }
.mote { position: absolute; bottom: -10px; left: calc((var(--i) * 10%) + 1%); width: 3px; height: 3px; border-radius: 50%; background: var(--lime-300); opacity: 0; animation: rise 9s linear infinite; animation-delay: calc(var(--i) * -0.8s); }
@keyframes rise { 0% { transform: translateY(0) scale(.6); opacity: 0; } 10% { opacity: .45; } 90% { opacity: .1; } 100% { transform: translateY(-100%) scale(1.1); opacity: 0; } }

/* Hạt rơi nhẹ xuống (rơi rơi), giống Dashboard */
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

/* ===== HEADER ===== */
.phan-hoi__header { position: relative; z-index: 1; }
.eyebrow { display: inline-block; font-family: var(--font-mono); font-size: 12px; letter-spacing: .14em; text-transform: uppercase; color: var(--lime-300); }
.phan-hoi__title { font-family: var(--font-display); font-size: 26px; font-weight: 600; color: var(--chalk-050); margin: 8px 0 4px; }
.phan-hoi__desc { font-size: 13.5px; color: var(--chalk-200); opacity: .75; margin: 0; }

/* ===== STAT GRID ===== */
.stat-grid { position: relative; z-index: 1; display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; }
@media (max-width: 720px) { .stat-grid { grid-template-columns: 1fr; } }

.stat-card {
  position: relative;
  background: rgba(247,251,244,.04);
  border: 1.5px solid rgba(247,251,244,.1);
  border-radius: 16px;
  padding: 18px;
  display: flex;
  flex-direction: column;
  text-align: left;
  cursor: pointer;
  font-family: inherit;
  color: var(--chalk-050);
  transition: transform .18s ease, border-color .18s ease, box-shadow .18s ease, background .18s ease;
}
.stat-card:hover { transform: translateY(-3px); box-shadow: 0 14px 30px -16px rgba(0,0,0,.5); }

.stat-card__icon { width: 34px; height: 34px; border-radius: 9px; background: rgba(247,251,244,.07); color: var(--chalk-200); display: flex; align-items: center; justify-content: center; margin-bottom: 12px; transition: background .18s ease, color .18s ease; }
.stat-card__label { font-size: 11.5px; font-weight: 700; text-transform: uppercase; letter-spacing: .05em; color: var(--chalk-200); opacity: .65; margin: 0 0 4px; }
.stat-card__num { font-family: var(--font-display); font-size: 24px; font-weight: 600; margin: 0; }
.stat-card__note { font-size: 12px; font-weight: 600; margin: 6px 0 0; }
.stat-card__note.is-warning { color: var(--amber-400); }

.stat-check {
  position: absolute; top: 14px; right: 14px; width: 18px; height: 18px; border-radius: 50%;
  border: 1.5px solid rgba(247,251,244,.25); display: flex; align-items: center; justify-content: center;
  color: transparent; transition: all .15s ease;
}

/* Mỗi trạng thái một màu riêng để phân biệt rõ khi tick */
.stat-card--all.stat-card--active { border-color: var(--lime-400); background: rgba(182,255,60,.08); }
.stat-card--all.stat-card--active .stat-card__icon { background: var(--lime-400); color: var(--night-950); }
.stat-card--all.stat-card--active .stat-check { background: var(--lime-400); border-color: var(--lime-400); color: var(--night-950); }
.stat-card--all.stat-card--active .stat-card__num { color: var(--lime-300); }

.stat-card--replied.stat-card--active { border-color: var(--turf-500); background: rgba(35,147,90,.14); }
.stat-card--replied.stat-card--active .stat-card__icon { background: var(--turf-500); color: var(--chalk-050); }
.stat-card--replied.stat-card--active .stat-check { background: var(--turf-500); border-color: var(--turf-500); color: var(--chalk-050); }
.stat-card--replied.stat-card--active .stat-card__num { color: var(--lime-300); }

.stat-card--unreplied.stat-card--active { border-color: var(--amber-400); background: rgba(255,176,32,.12); }
.stat-card--unreplied.stat-card--active .stat-card__icon { background: var(--amber-400); color: var(--night-950); }
.stat-card--unreplied.stat-card--active .stat-check { background: var(--amber-400); border-color: var(--amber-400); color: var(--night-950); }
.stat-card--unreplied.stat-card--active .stat-card__num { color: var(--amber-400); }

/* ===== FILTER BAR ===== */
.filter-card {
  position: relative; z-index: 1;
  background: rgba(247,251,244,.04); border: 1px solid rgba(247,251,244,.1); border-radius: 14px;
  padding: 14px 18px; display: flex; flex-wrap: wrap; justify-content: space-between; align-items: center; gap: 14px;
}
.search-box { display: flex; align-items: center; gap: 8px; background: rgba(5,11,8,.5); border: 1px solid rgba(247,251,244,.15); padding: 8px 12px; border-radius: 9px; flex: 1; min-width: 220px; max-width: 380px; color: var(--chalk-200); }
.search-box input { border: none; background: transparent; outline: none; width: 100%; font-size: 13.5px; color: var(--chalk-050); }
.search-box input::placeholder { color: rgba(227,236,223,.45); }

.date-range { display: flex; align-items: center; gap: 8px; flex-wrap: wrap; }
.date-range__label { font-family: var(--font-mono); font-size: 11px; letter-spacing: .06em; text-transform: uppercase; color: var(--lime-300); opacity: .85; margin-right: 2px; }
.date-range__sep { color: var(--chalk-200); opacity: .5; font-size: 12px; }
.date-input {
  background: rgba(5,11,8,.5); border: 1px solid rgba(247,251,244,.15); border-radius: 8px;
  padding: 7px 10px; font-size: 12.5px; color: var(--chalk-050); outline: none; font-family: var(--font-body);
  color-scheme: dark;
}
.date-input:focus { border-color: var(--lime-400); }
.btn-clear-date {
  width: 26px; height: 26px; border-radius: 50%; border: 1px solid rgba(255,71,87,.4); background: rgba(255,71,87,.1);
  color: #ff9686; font-size: 15px; line-height: 1; cursor: pointer; display: flex; align-items: center; justify-content: center;
}
.btn-clear-date:hover { background: rgba(255,71,87,.2); }

/* ===== TABLE ===== */
.table-card { position: relative; z-index: 1; background: var(--night-800); border-radius: 16px; border: 1px solid rgba(247,251,244,.1); overflow: hidden; }
.custom-table { width: 100%; border-collapse: collapse; text-align: left; font-size: 13.5px; }
.custom-table th { background: rgba(247,251,244,.05); padding: 12px 16px; font-weight: 700; font-size: 11.5px; text-transform: uppercase; letter-spacing: .04em; color: var(--chalk-200); opacity: .75; border-bottom: 1px solid rgba(247,251,244,.1); }
.custom-table td { padding: 13px 16px; border-bottom: 1px solid rgba(247,251,244,.06); vertical-align: top; color: var(--chalk-050); }
.custom-table tbody tr { transition: background .15s ease; }
.custom-table tbody tr:hover { background: rgba(182,255,60,.04); }

.user-info { display: flex; align-items: center; gap: 10px; }
.user-avatar { width: 32px; height: 32px; border-radius: 50%; background: var(--turf-500); color: var(--chalk-050); display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 13px; flex-shrink: 0; }
.user-name { font-weight: 600; color: var(--chalk-050); }
.user-email { font-size: 11px; color: var(--chalk-200); opacity: .6; }

.star-rating { color: rgba(247,251,244,.2); font-size: 14px; }
.star-rating .active { color: var(--amber-400); }

.review-comment { color: var(--chalk-200); margin: 0; line-height: 1.45; }

.reply-box { background: rgba(35,147,90,.12); border: 1px solid rgba(35,147,90,.35); padding: 8px 12px; border-radius: 8px; font-size: 12.5px; color: var(--lime-300); }
.reply-tag { font-weight: 700; color: var(--lime-300); }
.reply-time { display: flex; align-items: center; gap: 5px; margin-top: 6px; font-family: var(--font-mono); font-size: 11px; color: var(--chalk-200); opacity: .7; }

.text-muted { color: var(--chalk-200); opacity: .45; font-style: italic; font-size: 12.5px; }
.date-text { color: var(--chalk-200); opacity: .8; font-size: 12.5px; }

.btn-reply { background: var(--turf-500); color: var(--chalk-050); border: none; padding: 7px 13px; border-radius: 7px; font-size: 12.5px; font-weight: 600; cursor: pointer; transition: background .15s ease; }
.btn-reply:hover { background: var(--turf-700); }

.loading-state, .empty-state {
  background: var(--night-800);
  padding: 52px 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: var(--chalk-200);
}
.spinner { width: 26px; height: 26px; margin: 0 auto 12px; border: 3px solid rgba(247,251,244,.15); border-top-color: var(--lime-400); border-radius: 50%; animation: spin .8s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }

.empty-state__icon {
  width: 48px; height: 48px; border-radius: 50%;
  background: rgba(247,251,244,.06); border: 1px solid rgba(247,251,244,.12);
  color: var(--chalk-200); opacity: .8;
  display: flex; align-items: center; justify-content: center;
  margin-bottom: 14px;
}
.empty-state__title { font-weight: 700; color: var(--chalk-050); font-size: 14px; margin: 0 0 4px; }
.empty-state__desc { font-size: 12.5px; color: var(--chalk-200); opacity: .6; margin: 0; }

/* ===== MODAL ===== */
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,.6); display: flex; align-items: center; justify-content: center; z-index: 1000; padding: 16px; }
.modal-box { background: var(--night-800); border: 1px solid rgba(247,251,244,.12); width: 100%; max-width: 500px; border-radius: 16px; overflow: hidden; color: var(--chalk-050); }
.modal-header { padding: 16px 20px; border-bottom: 1px solid rgba(247,251,244,.1); display: flex; justify-content: space-between; align-items: center; }
.modal-header h3 { margin: 0; font-size: 15px; font-family: var(--font-display); font-weight: 600; }
.btn-close { border: none; background: transparent; font-size: 20px; cursor: pointer; color: var(--chalk-200); line-height: 1; }
.modal-body { padding: 20px; display: flex; flex-direction: column; gap: 14px; }
.customer-quote { background: rgba(247,251,244,.05); border-left: 3px solid var(--turf-500); padding: 10px 14px; border-radius: 6px; }
.quote-text { margin: 4px 0 0; font-size: 13px; color: var(--chalk-200); font-style: italic; }
.existing-reply-note { display: flex; align-items: center; gap: 6px; font-family: var(--font-mono); font-size: 11.5px; color: var(--lime-300); opacity: .85; }
.form-group { display: flex; flex-direction: column; gap: 6px; }
.form-group label { font-size: 12.5px; font-weight: 600; color: var(--chalk-200); }
.required { color: var(--crimson-500); }
.form-group textarea { padding: 10px; border-radius: 8px; border: 1px solid rgba(247,251,244,.18); background: rgba(5,11,8,.5); color: var(--chalk-050); font-size: 13px; outline: none; resize: vertical; font-family: var(--font-body); }
.form-group textarea:focus { border-color: var(--lime-400); }
.modal-footer { padding: 14px 20px; background: rgba(247,251,244,.03); border-top: 1px solid rgba(247,251,244,.1); display: flex; justify-content: flex-end; gap: 10px; }
.btn-primary { background: var(--turf-500); color: var(--chalk-050); border: none; padding: 9px 18px; border-radius: 8px; font-weight: 600; cursor: pointer; transition: background .15s ease; }
.btn-primary:hover:not(:disabled) { background: var(--turf-700); }
.btn-primary:disabled { opacity: .5; cursor: not-allowed; }
.btn-cancel { padding: 9px 16px; border-radius: 8px; border: 1px solid rgba(247,251,244,.18); background: transparent; color: var(--chalk-200); cursor: pointer; }
.btn-cancel:hover { background: rgba(247,251,244,.06); }
</style>