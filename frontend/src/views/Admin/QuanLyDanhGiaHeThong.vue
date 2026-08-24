<template>
  <div class="qldght">

    <!-- ===== HEADER ===== -->
    <div class="page-header">
      <div>
        <span class="eyebrow">⚽ Bảng điều khiển · Admin</span>
        <h1 class="page-title">Quản lý đánh giá hệ thống</h1>
        <p class="page-desc">Quản lý nhận xét, ẩn/hiện đánh giá và phản hồi khách hàng.</p>
      </div>
    </div>

    <!-- ===== STAT CARDS ===== -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-card__icon icon-total">💬</div>
        <div class="stat-card__body">
          <span class="stat-card__title">Tổng đánh giá</span>
          <span class="stat-card__value">{{ stats.total }}</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-card__icon icon-rating">⭐</div>
        <div class="stat-card__body">
          <span class="stat-card__title">Đánh giá trung bình</span>
          <span class="stat-card__value">{{ stats.avgRating }} / 5</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-card__icon icon-replied">✅</div>
        <div class="stat-card__body">
          <span class="stat-card__title">Đã phản hồi</span>
          <span class="stat-card__value">{{ stats.replied }}</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-card__icon icon-hidden">🙈</div>
        <div class="stat-card__body">
          <span class="stat-card__title">Đang ẩn</span>
          <span class="stat-card__value">{{ stats.hidden }}</span>
        </div>
      </div>
    </div>

    <!-- ===== BỘ LỌC ===== -->
    <div class="filter-card">
      <div class="filter-top">
        <div class="search-wrap">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="7"/><path d="M21 21l-4.3-4.3"/></svg>
          <input v-model="filters.keyword" type="text" class="search-input" placeholder="Tìm theo tên người dùng, nội dung..."/>
        </div>

        <div class="pill-select">
          <label>Số sao</label>
          <select v-model="filters.rating" class="form-control">
            <option value="">Tất cả số sao</option>
            <option v-for="star in 5" :key="star" :value="star">{{ star }} Sao</option>
          </select>
        </div>

        <div class="pill-select">
          <label>Trạng thái</label>
          <select v-model="filters.status" class="form-control">
            <option value="">Tất cả trạng thái</option>
            <option value="visible">Đang hiển thị</option>
            <option value="hidden">Đã ẩn</option>
            <option value="replied">Đã phản hồi</option>
            <option value="unreplied">Chưa phản hồi</option>
          </select>
        </div>
      </div>
    </div>

    <!-- ===== BẢNG ===== -->
    <div class="table-card">
      <div class="table-responsive">
        <table class="data-table">
          <thead>
            <tr>
              <th width="150">Người đánh giá</th>
              <th width="100">Mức độ</th>
              <th>Nội dung đánh giá</th>
              <th width="260">Phản hồi</th>
              <th width="100">Ngày tạo</th>
              <th width="100">Trạng thái</th>
              <th style="text-align:center" width="110">Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="filteredList.length === 0">
              <td colspan="7" class="no-data">
                <div class="no-data__icon">💬</div>
                Không tìm thấy đánh giá nào.
              </td>
            </tr>
            <tr v-for="item in filteredList" :key="item.id">
              <!-- Người đánh giá -->
              <td>
                <div class="cell-stack">
                  <span class="bold">{{ item.name }}</span>
                  <span class="role-text">{{ item.role || 'Khách hàng' }}</span>
                </div>
              </td>

              <!-- Số sao -->
              <td>
                <div class="star-cell">
                  <span v-for="s in 5" :key="s" :class="['star', { 'star--active': s <= item.rating }]">★</span>
                </div>
              </td>

              <!-- Nội dung -->
              <td class="quote-cell">
                <p class="quote-text" :title="item.quote">"{{ item.quote }}"</p>
              </td>

              <!-- Phản hồi -->
              <td class="reply-cell">
                <div v-if="item.phanHoi" class="reply-box">
                  <div class="reply-box__top">
                    <span class="reply-box__icon">💬</span>
                    <span class="reply-box__name" :title="item.tenNguoiPhanHoi">{{ item.tenNguoiPhanHoi || 'Đông Quân' }}</span>
                    <span class="role-badge" :class="(item.vaiTroNguoiPhanHoi || 'staff').toLowerCase()">
                      {{ item.vaiTroNguoiPhanHoi || 'STAFF' }}
                    </span>
                  </div>
                  <div class="reply-box__time">{{ formatDate(item.ngayPhanHoi || item.ngayDanhGia) }}</div>
                  <p class="reply-box__content">{{ item.phanHoi }}</p>
                </div>
                <span v-else class="badge badge-pending"><span class="badge__dot"></span>Chờ trả lời</span>
              </td>

              <!-- Ngày tạo -->
              <td><span class="sub">{{ formatDate(item.ngayDanhGia) }}</span></td>

              <!-- Trạng thái -->
              <td>
                <span :class="['badge', item.hienThi ? 'badge-success' : 'badge-hidden']">
                  <span class="badge__dot"></span>{{ item.hienThi ? 'Hiển thị' : 'Đã ẩn' }}
                </span>
              </td>

              <!-- Thao tác -->
              <td>
                <div class="actions">
                  <button
                    @click="toggleVisibility(item)"
                    :class="['btn-icon', item.hienThi ? 'btn-hide' : 'btn-show']"
                    :title="item.hienThi ? 'Ẩn đánh giá' : 'Hiện đánh giá'"
                  >
                    {{ item.hienThi ? '🙈' : '👁️' }}
                  </button>
                  <button @click="openReplyModal(item)" class="btn-icon btn-reply" title="Phản hồi">💬</button>
                  <button @click="deleteReview(item.id)" class="btn-icon btn-delete" title="Xóa">🗑️</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- ===== MODAL PHẢN HỒI ===== -->
    <div v-if="showModal" class="overlay" @click.self="closeModal">
      <div class="modal-box">
        <div class="modal-header">
          <h2>💬 Phản hồi đánh giá</h2>
          <button @click="closeModal" class="close-btn">&times;</button>
        </div>
        <div class="modal-body" v-if="selectedItem">
          <div class="review-summary">
            <p><strong>Người đăng:</strong> {{ selectedItem.name }} ({{ selectedItem.rating }} ⭐)</p>
            <p class="review-summary__quote">"{{ selectedItem.quote }}"</p>
          </div>

          <div class="form-group">
            <label class="form-label">Nội dung phản hồi từ Admin/Staff</label>
            <textarea v-model="replyText" class="form-control" rows="4" placeholder="Nhập nội dung phản hồi..."></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="closeModal" class="btn-ghost">Hủy</button>
          <button @click="submitReply" class="btn-primary" :disabled="!replyText.trim()">Lưu phản hồi</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const BASE_URL = '/api/danh-gia-he-thong'
const danhGiaList = ref([])
const showModal = ref(false)
const selectedItem = ref(null)
const replyText = ref('')

const filters = ref({
  keyword: '',
  rating: '',
  status: ''
})

// Fetch danh sách đánh giá (dùng /quan-ly để Admin thấy được cả bài đang ẩn)
const fetchReviews = async () => {
  try {
    const token = localStorage.getItem('token')
    const res = await fetch(`${BASE_URL}/quan-ly`, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    if (res.ok) {
      danhGiaList.value = await res.json()
    } else {
      console.error('Không thể tải danh sách đánh giá:', res.status)
    }
  } catch (err) {
    console.error('Lỗi khi tải danh sách đánh giá:', err)
  }
}

// Thống kê tổng quan
const stats = computed(() => {
  const total = danhGiaList.value.length
  if (total === 0) return { total: 0, avgRating: 0, replied: 0, hidden: 0 }

  const sumRating = danhGiaList.value.reduce((acc, cur) => acc + (cur.rating || 0), 0)
  const avg = (sumRating / total).toFixed(1)
  const replied = danhGiaList.value.filter(i => i.phanHoi && i.phanHoi.trim() !== '').length
  const hidden = danhGiaList.value.filter(i => !i.hienThi).length

  return { total, avgRating: avg, replied, hidden }
})

// Danh sách lọc
const filteredList = computed(() => {
  return danhGiaList.value.filter(item => {
    const matchKey = !filters.value.keyword ||
      item.name?.toLowerCase().includes(filters.value.keyword.toLowerCase()) ||
      item.quote?.toLowerCase().includes(filters.value.keyword.toLowerCase())

    const matchStar = !filters.value.rating || item.rating === Number(filters.value.rating)

    let matchStatus = true
    if (filters.value.status === 'visible') matchStatus = item.hienThi === true
    if (filters.value.status === 'hidden') matchStatus = item.hienThi === false
    if (filters.value.status === 'replied') matchStatus = !!item.phanHoi
    if (filters.value.status === 'unreplied') matchStatus = !item.phanHoi

    return matchKey && matchStar && matchStatus
  })
})

// Ẩn / Hiện đánh giá
const toggleVisibility = async (item) => {
  try {
    const updatedStatus = !item.hienThi
    const token = localStorage.getItem('token')

    if (!token) {
      alert('Bạn chưa đăng nhập hoặc phiên làm việc đã hết hạn!')
      return
    }

    const res = await fetch(`${BASE_URL}/${item.id}/trang-thai?hienThi=${updatedStatus}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    })

    if (res.ok) {
      item.hienThi = updatedStatus
    } else if (res.status === 403) {
      alert('Lỗi 403: Tài khoản không có quyền thực hiện thao tác này!')
    } else {
      alert(`Thay đổi trạng thái thất bại! (Mã lỗi: ${res.status})`)
    }
  } catch (err) {
    console.error('Lỗi toggle hien thi:', err)
    alert('Không thể kết nối đến máy chủ!')
  }
}

const openReplyModal = (item) => {
  selectedItem.value = item
  replyText.value = item.phanHoi || ''
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
  selectedItem.value = null
  replyText.value = ''
}

const submitReply = async () => {
  if (!selectedItem.value || !replyText.value.trim()) return

  const currentUser = JSON.parse(localStorage.getItem('user') || '{}')
  const bodyData = {
    noiDungPhanHoi: replyText.value.trim(),
    nguoiPhanHoiId: currentUser.id || currentUser.userId
  }

  try {
    const res = await fetch(`${BASE_URL}/${selectedItem.value.id}/phan-hoi`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: JSON.stringify(bodyData)
    })

    if (res.ok) {
      alert('Đã cập nhật phản hồi!')
      closeModal()
      fetchReviews()
    } else {
      alert('Gửi phản hồi thất bại!')
    }
  } catch (err) {
    console.error('Lỗi submit phan hoi:', err)
  }
}

const deleteReview = async (id) => {
  if (!confirm('Bạn có chắc chắn muốn xóa đánh giá này không? Hành động này không thể hoàn tác!')) return

  try {
    const token = localStorage.getItem('token')
    if (!token) {
      alert('Bạn chưa đăng nhập hoặc phiên làm việc đã hết hạn!')
      return
    }

    const res = await fetch(`${BASE_URL}/${id}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    })

    if (res.ok) {
      danhGiaList.value = danhGiaList.value.filter(i => i.id !== id)
      alert('Đã xóa đánh giá thành công!')
    } else if (res.status === 403) {
      alert('Lỗi 403: Tài khoản không có quyền xóa đánh giá!')
    } else {
      alert(`Xóa đánh giá thất bại! (Mã lỗi: ${res.status})`)
    }
  } catch (err) {
    console.error('Lỗi delete review:', err)
    alert('Không thể kết nối đến máy chủ!')
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return d.toLocaleDateString('vi-VN')
}

onMounted(() => {
  fetchReviews()
})
</script>

<style scoped>
/* ============================================================
   TOKENS — đồng bộ với trang chủ & các trang quản lý khác
   ============================================================ */
.qldght {
  --night-950: #050b08;
  --night-800: #0a1f13;
  --night-700: #123321;
  --turf-500: #23935a;
  --turf-700: #146239;
  --lime-400: #b6ff3c;
  --lime-300: #d3ff8f;
  --amber-400: #ffb020;
  --crimson-500: #ff4757;
  --info-400: #6fa8ff;
  --chalk-050: #f7fbf4;
  --chalk-200: #e3ecdf;
  --font-display: 'Oswald', 'Manrope', sans-serif;
  --font-body: 'Manrope', sans-serif;
  --font-mono: 'Space Mono', monospace;

  width: 100%;
  min-height: 100%;
  background: var(--night-950);
  color: var(--chalk-050);
  font-family: var(--font-body);
  padding: 32px clamp(16px, 3vw, 40px) 60px;
  box-sizing: border-box;
}

/* ============================================================
   HEADER
   ============================================================ */
.page-header { margin-bottom: 22px; }
.eyebrow { display: inline-block; font-family: var(--font-mono); font-size: 11.5px; letter-spacing: .14em; text-transform: uppercase; color: var(--lime-300); }
.page-title { font-family: var(--font-display); font-weight: 600; font-size: clamp(24px, 2.6vw, 30px); margin: 8px 0 4px; color: var(--chalk-050); }
.page-desc { font-size: 13.5px; color: var(--chalk-200); opacity: .72; margin: 0; }

/* ============================================================
   BUTTONS
   ============================================================ */
.btn-primary {
  display: inline-flex; align-items: center; gap: 8px; padding: 10px 20px;
  background: linear-gradient(135deg, var(--turf-500), var(--turf-700)); color: var(--chalk-050);
  border: none; border-radius: 999px; font-weight: 700; font-size: 13.5px; cursor: pointer;
  box-shadow: 0 10px 24px -10px rgba(20,98,57,.75); transition: transform .15s ease, box-shadow .15s ease;
}
.btn-primary:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 14px 28px -8px rgba(182,255,60,.35); }
.btn-primary:disabled { opacity: .45; cursor: not-allowed; transform: none; box-shadow: none; }

.btn-ghost {
  display: inline-flex; align-items: center; gap: 6px; padding: 10px 20px;
  background: rgba(247,251,244,.06); color: var(--chalk-050); border: 1.5px solid rgba(247,251,244,.2);
  border-radius: 999px; font-weight: 600; font-size: 13.5px; cursor: pointer; transition: all .15s ease;
}
.btn-ghost:hover { border-color: var(--lime-400); color: var(--lime-300); background: rgba(182,255,60,.08); }

/* ============================================================
   STAT CARDS
   ============================================================ */
.stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(210px, 1fr)); gap: 14px; margin-bottom: 22px; }
.stat-card { display: flex; align-items: center; gap: 14px; background: rgba(247,251,244,.03); border: 1px solid rgba(247,251,244,.1); border-radius: 16px; padding: 16px 18px; transition: transform .2s, border-color .2s; }
.stat-card:hover { transform: translateY(-2px); border-color: rgba(182,255,60,.3); }
.stat-card__icon { font-size: 21px; width: 44px; height: 44px; border-radius: 12px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.icon-total { background: rgba(182,255,60,.12); }
.icon-rating { background: rgba(255,176,32,.15); }
.icon-replied { background: rgba(111,168,255,.15); }
.icon-hidden { background: rgba(255,71,87,.14); }
.stat-card__body { display: flex; flex-direction: column; gap: 3px; }
.stat-card__title { font-family: var(--font-mono); font-size: 10.5px; text-transform: uppercase; letter-spacing: .05em; color: var(--chalk-200); opacity: .7; }
.stat-card__value { font-family: var(--font-display); font-size: 20px; font-weight: 600; color: var(--chalk-050); }

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
textarea.form-control { resize: vertical; font-family: var(--font-body); }

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
.data-table td { padding: 13px 16px; border-bottom: 1px solid rgba(247,251,244,.06); vertical-align: top; }
.data-table tbody tr:hover { background: rgba(182,255,60,.04); }
.cell-stack { display: flex; flex-direction: column; gap: 2px; }
.bold { font-weight: 600; color: var(--chalk-050); }
.sub { font-size: 12.5px; color: var(--chalk-200); opacity: .7; }
.role-text { font-size: 11.5px; color: var(--lime-300); }

.star-cell { display: flex; }
.star { color: rgba(247,251,244,.2); font-size: 15px; }
.star--active { color: var(--amber-400); }

.quote-cell { max-width: 260px; }
.quote-text { margin: 0; color: var(--chalk-200); opacity: .85; font-size: 13px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; font-style: italic; }

/* BADGES */
.badge { display: inline-flex; align-items: center; gap: 6px; padding: 5px 11px; border-radius: 20px; font-size: 11px; font-weight: 700; white-space: nowrap; }
.badge__dot { width: 6px; height: 6px; border-radius: 50%; flex-shrink: 0; }
.badge-success { background: rgba(182,255,60,.15); color: var(--lime-300); }
.badge-success .badge__dot { background: var(--lime-400); }
.badge-hidden { background: rgba(255,71,87,.14); color: #ff9686; }
.badge-hidden .badge__dot { background: var(--crimson-500); }
.badge-pending { background: rgba(255,176,32,.15); color: var(--amber-400); }
.badge-pending .badge__dot { background: var(--amber-400); }

/* REPLY BOX */
.reply-cell { min-width: 240px; max-width: 280px; }
.reply-box {
  background: rgba(182,255,60,.06); border-left: 3px solid var(--lime-400); border-radius: 0 10px 10px 0;
  padding: 9px 12px; display: flex; flex-direction: column; gap: 3px;
}
.reply-box__top { display: flex; align-items: center; gap: 6px; }
.reply-box__icon { font-size: 12px; }
.reply-box__name { font-size: 12.5px; font-weight: 700; color: var(--chalk-050); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: 140px; }
.role-badge { font-size: 9.5px; font-weight: 800; padding: 1px 6px; border-radius: 4px; letter-spacing: .04em; flex-shrink: 0; }
.role-badge.staff { background: rgba(182,255,60,.15); color: var(--lime-300); }
.role-badge.admin { background: rgba(255,71,87,.18); color: #ff9686; }
.reply-box__time { font-size: 10.5px; color: var(--chalk-200); opacity: .55; }
.reply-box__content { font-size: 12.5px; color: var(--chalk-200); opacity: .85; line-height: 1.45; margin: 0; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

/* ACTIONS */
.actions { display: flex; gap: 6px; justify-content: center; flex-wrap: wrap; }
.btn-icon {
  width: 30px; height: 30px; border-radius: 8px; border: 1px solid rgba(247,251,244,.14);
  background: rgba(247,251,244,.03); display: inline-flex; align-items: center; justify-content: center;
  cursor: pointer; font-size: 13px; transition: all .15s;
}
.btn-icon:hover { transform: scale(1.08); }
.btn-hide:hover { background: rgba(255,176,32,.15); border-color: var(--amber-400); }
.btn-show:hover { background: rgba(182,255,60,.12); border-color: var(--lime-400); }
.btn-reply:hover { background: rgba(111,168,255,.15); border-color: var(--info-400); }
.btn-delete:hover { background: rgba(255,71,87,.15); border-color: var(--crimson-500); }

.no-data { text-align: center; padding: 50px 20px; color: var(--chalk-200); opacity: .6; }
.no-data__icon { font-size: 26px; margin-bottom: 8px; }

/* ============================================================
   MODAL
   ============================================================ */
.overlay { position: fixed; inset: 0; background: rgba(5,11,8,.75); backdrop-filter: blur(3px); display: flex; align-items: center; justify-content: center; z-index: 200; padding: 20px; }
.modal-box { background: var(--night-800); border: 1px solid rgba(182,255,60,.2); border-radius: 18px; width: 100%; max-width: 500px; box-shadow: 0 24px 50px rgba(0,0,0,.5); overflow: hidden; max-height: 90vh; display: flex; flex-direction: column; }
.modal-header { padding: 18px 24px; border-bottom: 1px solid rgba(247,251,244,.1); display: flex; justify-content: space-between; align-items: center; flex-shrink: 0; }
.modal-header h2 { font-family: var(--font-display); font-size: 17px; font-weight: 600; color: var(--chalk-050); margin: 0; }
.close-btn { background: none; border: none; font-size: 22px; color: var(--chalk-200); opacity: .6; cursor: pointer; }
.close-btn:hover { opacity: 1; color: var(--lime-300); }
.modal-body { padding: 20px 24px; overflow-y: auto; }
.modal-footer { padding: 16px 24px; border-top: 1px solid rgba(247,251,244,.1); display: flex; justify-content: flex-end; gap: 10px; flex-shrink: 0; }

.review-summary { background: rgba(247,251,244,.04); border: 1px solid rgba(247,251,244,.08); border-radius: 10px; padding: 12px 14px; margin-bottom: 16px; font-size: 13px; color: var(--chalk-200); }
.review-summary strong { color: var(--chalk-050); }
.review-summary__quote { margin: 6px 0 0; color: var(--chalk-200); opacity: .8; font-style: italic; }

.form-group { display: flex; flex-direction: column; gap: 6px; }
.form-label { font-size: 12.5px; font-weight: 600; color: var(--lime-300); font-family: var(--font-mono); text-transform: uppercase; letter-spacing: .04em; }

/* ============================================================
   RESPONSIVE
   ============================================================ */
@media (max-width: 720px) {
  .filter-top { flex-direction: column; align-items: stretch; }
  .pill-select { min-width: 0; }
  .data-table { font-size: 12.5px; }
}
</style>