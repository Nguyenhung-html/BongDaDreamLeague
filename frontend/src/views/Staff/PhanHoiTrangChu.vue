<template>
  <div class="phan-hoi-container">
    <div class="page-header">
      <div>
        <h2 class="page-title">Quản lý Phản hồi Đánh giá</h2>
        <p class="page-subtitle">Xem đánh giá từ khách hàng và gửi phản hồi đại diện cho sân bóng</p>
      </div>
    </div>
    <!-- Quick Stats Bar -->
    <div class="stats-grid">
      <div class="stat-card">
        <p>Tổng số đánh giá</p>
        <h3>{{ tongSoDanhGia }}</h3>
      </div>
      <div class="stat-card">
        <p>Đã phản hồi</p>
        <h3>{{ daPhanHoiCount }}</h3>
      </div>
      <div class="stat-card">
        <p>Chưa phản hồi</p>
        <h3>{{ chuaPhanHoiCount }}</h3>
      </div>
    </div>
    <!-- Filter & Search Bar -->
    <div class="filter-card">
      <div class="search-box">
        <svg viewBox="0 0 24 24" fill="none" width="18" height="18"><circle cx="11" cy="11" r="8" stroke="currentColor" stroke-width="1.7"/><path d="M21 21l-4.35-4.35" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/></svg>
        <input type="text" v-model="searchQuery" placeholder="Tìm theo tên khách hàng hoặc nội dung..." />
      </div>
      <select v-model="filterStatus" class="select-filter">
        <option value="all">Tất cả trạng thái</option>
        <option value="unreplied">Chưa phản hồi</option>
        <option value="replied">Đã phản hồi</option>
      </select>
    </div>
    <!-- Table Reviews -->
    <div class="table-card">
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>Đang tải danh sách đánh giá...</p>
      </div>
      <table v-else-if="filteredReviews.length > 0" class="custom-table">
        <thead>
          <tr>
            <th width="180">Khách hàng</th>
            <th width="110">Đánh giá</th>
            <th>Nội dung nhận xét</th>
            <th>Phản hồi từ sân</th>
            <th width="120">Ngày tạo</th>
            <th width="120" class="text-right">Thao tác</th>
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
        <p>Không tìm thấy đánh giá nào phù hợp.</p>
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
import { ref, computed, onMounted } from 'vue'

const API_BASE_URL = '/api/danh-gia-he-thong'
const reviews = ref([])
const loading = ref(false)
const submitting = ref(false)
const showModal = ref(false)
const selectedReview = ref(null)
const replyInput = ref('')
const searchQuery = ref('')
const filterStatus = ref('all')

const filteredReviews = computed(() => {
  return reviews.value.filter(item => {
    const name = item.userName || item.name || ''
    const comment = item.quote || item.comment || ''
    const matchSearch = name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
                        comment.toLowerCase().includes(searchQuery.value.toLowerCase())
    
    let matchStatus = true
    const hasReply = !!(item.replyContent || item.phanHoi)
    if (filterStatus.value === 'unreplied') {
      matchStatus = !hasReply
    } else if (filterStatus.value === 'replied') {
      matchStatus = hasReply
    }
    return matchSearch && matchStatus
  })
})

onMounted(() => {
  fetchReviews()
})

async function fetchReviews() {
  loading.value = true
  try {
    const token = localStorage.getItem('token') || localStorage.getItem('accessToken') || localStorage.getItem('jwt')
    const res = await fetch(`${API_BASE_URL}`, {
      headers: { 'Authorization': `Bearer ${token}` }
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
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({ 
        noiDungPhanHoi: replyInput.value.trim() 
      })
    })

    if (res.ok) {
      alert('Đã gửi phản hồi thành công!')
      closeModal()
      fetchReviews() // Reload lại danh sách
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

// 1. Tổng số đánh giá
const tongSoDanhGia = computed(() => {
  return reviews.value ? reviews.value.length : 0
})

// 2. Đã phản hồi
const daPhanHoiCount = computed(() => {
  if (!reviews.value) return 0
  return reviews.value.filter(item => item.phanHoi && item.phanHoi.trim() !== '').length
})

// 3. Chưa phản hồi
const chuaPhanHoiCount = computed(() => {
  if (!reviews.value) return 0
  return reviews.value.filter(item => !item.phanHoi || item.phanHoi.trim() === '').length
})

</script>

<style scoped>
.phan-hoi-container { display: flex; flex-direction: column; gap: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; }
.page-title { font-size: 20px; font-weight: 700; color: #0f172a; margin: 0; }
.page-subtitle { font-size: 13px; color: #64748b; margin-top: 4px; }
/* Stats Grid */
.stats-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; }
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
.custom-table td { padding: 12px 16px; border-bottom: 1px solid #f1f5f9; vertical-align: top; }
.user-info { display: flex; align-items: center; gap: 10px; }
.user-avatar { width: 32px; height: 32px; border-radius: 50%; background: #16a34a; color: white; display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 13px; }
.user-name { font-weight: 600; color: #0f172a; }
.user-email { font-size: 11px; color: #64748b; }
.star-rating { color: #cbd5e1; font-size: 14px; }
.star-rating .active { color: #f59e0b; }
.review-comment { color: #334155; margin: 0; line-height: 1.4; }
.reply-box { background: #f0fdf4; border: 1px solid #bbf7d0; padding: 8px 12px; border-radius: 6px; font-size: 12.5px; color: #166534; }
.reply-tag { font-weight: 700; }
.text-muted { color: #94a3b8; font-style: italic; font-size: 12.5px; }
.btn-reply { background: #16a34a; color: white; border: none; padding: 6px 12px; border-radius: 6px; font-size: 12.5px; font-weight: 600; cursor: pointer; }
.btn-reply:hover { background: #15803d; }
/* Modal */
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal-box { background: white; width: 100%; max-width: 500px; border-radius: 12px; overflow: hidden; }
.modal-header { padding: 16px 20px; border-bottom: 1px solid #e2e8f0; display: flex; justify-content: space-between; align-items: center; }
.modal-header h3 { margin: 0; font-size: 15px; }
.btn-close { border: none; background: transparent; font-size: 20px; cursor: pointer; }
.modal-body { padding: 20px; display: flex; flex-direction: column; gap: 14px; }
.customer-quote { background: #f8fafc; border-left: 3px solid #16a34a; padding: 10px 14px; border-radius: 4px; }
.quote-text { margin: 4px 0 0; font-size: 13px; color: #334155; font-style: italic; }
.form-group { display: flex; flex-direction: column; gap: 6px; }
.form-group label { font-size: 12.5px; font-weight: 600; color: #334155; }
.form-group textarea { padding: 10px; border-radius: 6px; border: 1px solid #cbd5e1; font-size: 13px; outline: none; resize: vertical; }
.modal-footer { padding: 14px 20px; background: #f8fafc; border-top: 1px solid #e2e8f0; display: flex; justify-content: flex-end; gap: 10px; }
.btn-primary { background: #16a34a; color: white; border: none; padding: 8px 16px; border-radius: 6px; font-weight: 600; cursor: pointer; }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-cancel { padding: 8px 16px; border-radius: 6px; border: 1px solid #cbd5e1; background: white; cursor: pointer; }
.loading-state, .empty-state { padding: 40px; text-align: center; color: #64748b; }
</style>