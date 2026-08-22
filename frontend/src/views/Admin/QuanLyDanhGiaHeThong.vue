<template>
  <div class="admin-review-container">
    <!-- Header Page -->
    <div class="page-header">
      <div>
        <h2>Quản Lý Đánh Giá Hệ Thống</h2>
        <p>Quản lý nhận xét, ẩn/hiện đánh giá và phản hồi khách hàng</p>
      </div>
    </div>

    <!-- 1. Thẻ Thống Kê Overview -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon total">💬</div>
        <div class="stat-info">
          <span>Tổng đánh giá</span>
          <h3>{{ stats.total }}</h3>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon rating">⭐</div>
        <div class="stat-info">
          <span>Đánh giá trung bình</span>
          <h3>{{ stats.avgRating }} / 5</h3>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon replied">✅</div>
        <div class="stat-info">
          <span>Đã phản hồi</span>
          <h3>{{ stats.replied }}</h3>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon hidden">👁️‍🗨️</div>
        <div class="stat-info">
          <span>Đang ẩn</span>
          <h3>{{ stats.hidden }}</h3>
        </div>
      </div>
    </div>

    <!-- 2. Thanh Bộ Lọc & Tìm Kiếm -->
    <div class="filter-card">
      <div class="search-box">
        <input 
          v-model="filters.keyword" 
          type="text" 
          placeholder="Tìm theo tên người dùng, nội dung..." 
        />
      </div>
      <div class="filter-group">
        <select v-model="filters.rating">
          <option value="">Tất cả số sao</option>
          <option v-for="star in 5" :key="star" :value="star">{{ star }} Sao</option>
        </select>

        <select v-model="filters.status">
          <option value="">Tất cả trạng thái</option>
          <option value="visible">Đang hiển thị</option>
          <option value="hidden">Đã ẩn</option>
          <option value="replied">Đã phản hồi</option>
          <option value="unreplied">Chưa phản hồi</option>
        </select>
      </div>
    </div>

    <!-- 3. Bảng Danh Sách Đánh Giá -->
    <div class="table-card">
      <table class="custom-table">
        <thead>
          <tr>
            <th style="width: 160px;">Người đánh giá</th>
            <th style="width: 100px;">Mức độ</th>
            <th>Nội dung đánh giá</th> <!-- Cột 3 -->
            <th style="width: 260px;">Phản hồi</th>      <!-- Cột 4: Đặt sát bên Nội dung -->
            <th style="width: 110px;">Ngày tạo</th>
            <th style="width: 100px;">Trạng thái</th>
            <th style="width: 110px;" class="text-center">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="filteredList.length === 0">
            <td colspan="7" class="text-center empty-cell">Không tìm thấy đánh giá nào</td>
          </tr>
          <tr v-for="item in filteredList" :key="item.id">
            <!-- 1. Tên người đánh giá -->
            <td>
              <div class="user-cell">
                <strong>{{ item.name }}</strong>
                <span class="role-text">{{ item.role || 'Khách hàng' }}</span>
              </div>
            </td>

            <!-- 2. Số sao -->
            <td>
              <div class="star-cell">
                <span v-for="s in 5" :key="s" :class="['star', { active: s <= item.rating }]">★</span>
              </div>
            </td>

            <!-- 3. Nội dung đánh giá của khách -->
            <td class="quote-cell">
              <p class="quote-text" :title="item.quote">"{{ item.quote }}"</p>
            </td>

            <!-- 4. NỘI DUNG PHẢN HỒI (ĐẶT Ở ĐÂY LÀ ĐẸP NHẤT) -->
            <td class="reply-cell">
              <div v-if="item.phanHoi" class="reply-box">
              <!-- Dòng 1: Icon + Tên + Badge -->
              <div class="reply-user-info">
                <span class="reply-icon">💬</span>
                <span class="replier-name" :title="item.tenNguoiPhanHoi">{{ item.tenNguoiPhanHoi || 'Đông Quân' }}</span>
                <span class="role-badge" :class="item.vaiTroNguoiPhanHoi?.toLowerCase() || 'staff'">
                  {{ item.vaiTroNguoiPhanHoi || 'STAFF' }}
                </span>
              </div>

              <!-- Dòng 2: Ngày phản hồi -->
              <div class="reply-time">
                {{ formatDate(item.ngayPhanHoi || item.ngayDanhGia) }}
              </div>

              <!-- Dòng 3: Nội dung phản hồi -->
              <p class="reply-content">
                {{ item.phanHoi }}
              </p>
            </div>

              <span v-else class="badge-reply pending">Chờ trả lời</span>
            </td>

            <!-- 5. Ngày tạo -->
            <td class="date-cell">{{ formatDate(item.ngayDanhGia) }}</td>

            <!-- 6. Trạng thái Ẩn/Hiện -->
            <td>
              <span :class="['badge-status', item.hienThi ? 'badge-success' : 'badge-danger']">
                {{ item.hienThi ? 'Hiển thị' : 'Đã ẩn' }}
              </span>
            </td>

            <!-- 7. Thao tác -->
            <td class="action-cell">
              <button 
                @click="toggleVisibility(item)" 
                :class="['btn-icon', item.hienThi ? 'btn-hide' : 'btn-show']"
                :title="item.hienThi ? 'Ẩn đánh giá' : 'Hiện đánh giá'"
              >
                {{ item.hienThi ? '👁️' : '🔒' }}
              </button>
              <button @click="openReplyModal(item)" class="btn-icon btn-reply" title="Phản hồi">💬</button>
              <button @click="deleteReview(item.id)" class="btn-icon btn-delete" title="Xóa">🗑️</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 4. Modal Xử Lý Phản Hồi -->
    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-box">
        <div class="modal-header">
          <h3>Phản Hồi Đánh Giá</h3>
          <button @click="closeModal" class="close-btn">&times;</button>
        </div>
        <div class="modal-body" v-if="selectedItem">
          <div class="review-summary">
            <p><strong>Người đăng:</strong> {{ selectedItem.name }} ({{ selectedItem.rating }} ⭐)</p>
            <p class="summary-quote">"{{ selectedItem.quote }}"</p>
          </div>

          <div class="form-group">
            <label>Nội dung phản hồi từ Admin/Staff:</label>
            <textarea 
              v-model="replyText" 
              rows="4" 
              placeholder="Nhập nội dung phản hồi..."
            ></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="closeModal" class="btn-cancel">Hủy</button>
          <button @click="submitReply" class="btn-primary" :disabled="!replyText.trim()">
            Lưu phản hồi
          </button>
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

// Fetch danh sách đánh giá
// 1. Fetch danh sách (Thêm /quan-ly để Admin thấy được cả bài đang ẩn)
const fetchReviews = async () => {
  try {
    const token = localStorage.getItem('token')
    const res = await fetch(`${BASE_URL}/quan-ly`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
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
    // Lọc theo keyword
    const matchKey = !filters.value.keyword || 
      item.name?.toLowerCase().includes(filters.value.keyword.toLowerCase()) ||
      item.quote?.toLowerCase().includes(filters.value.keyword.toLowerCase())

    // Lọc theo số sao
    const matchStar = !filters.value.rating || item.rating === Number(filters.value.rating)

    // Lọc theo trạng thái
    let matchStatus = true
    if (filters.value.status === 'visible') matchStatus = item.hienThi === true
    if (filters.value.status === 'hidden') matchStatus = item.hienThi === false
    if (filters.value.status === 'replied') matchStatus = !!item.phanHoi
    if (filters.value.status === 'unreplied') matchStatus = !item.phanHoi

    return matchKey && matchStar && matchStatus
  })
})

// Ẩn / Hiện Đánh giá
const toggleVisibility = async (item) => {
  try {
    const updatedStatus = !item.hienThi
    const token = localStorage.getItem('token')

    if (!token) {
      alert('Bạn chưa đăng nhập hoặc phiên làm việc đã hết hạn!')
      return
    }

    // ĐÃ SỬA: Đổi /hien-thi thành /trang-thai cho đúng với Backend Controller
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

// Mở Modal Phản Hồi
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

// Gửi Phản Hồi
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

// 2. Xóa Đánh giá (Đã chuẩn hóa Token & bẫy lỗi)
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
      // Cập nhật lại UI sau khi xóa thành công
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

// Format ngày tháng
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
.admin-review-container {
  padding: 24px;
  background-color: #f8fafc;
  min-height: 100vh;
}

.page-header h2 {
  font-size: 24px;
  font-weight: 800;
  color: #0f172a;
  margin: 0 0 4px 0;
}

.page-header p {
  color: #64748b;
  font-size: 14px;
  margin-bottom: 24px;
}

/* Stats */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: #ffffff;
  padding: 16px 20px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  font-size: 24px;
  padding: 12px;
  border-radius: 10px;
  background: #f1f5f9;
}

.stat-info span {
  font-size: 13px;
  color: #64748b;
}

.stat-info h3 {
  font-size: 20px;
  font-weight: 700;
  color: #0f172a;
  margin: 2px 0 0 0;
}

/* Filter Bar */
.filter-card {
  background: #ffffff;
  padding: 16px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  display: flex;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.search-box input {
  width: 280px;
  padding: 8px 14px;
  border: 1px solid #cbd5e1;
  border-radius: 8px;
  outline: none;
  font-size: 14px;
}

.filter-group {
  display: flex;
  gap: 12px;
}

.filter-group select {
  padding: 8px 12px;
  border: 1px solid #cbd5e1;
  border-radius: 8px;
  outline: none;
  font-size: 14px;
  background: #fff;
}

/* Table */
.table-card {
  background: #ffffff;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
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
  color: #334155;
}

.user-cell strong {
  display: block;
  color: #0f172a;
}

.role-text {
  font-size: 12px;
  color: #10b981;
}

.star-cell .star {
  color: #cbd5e1;
}

.star-cell .star.active {
  color: #f59e0b;
}

.quote-cell {
  max-width: 240px;
}

.quote-text {
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* Badges */
.badge-status {
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
}

.badge-success { background: #d1fae5; color: #047857; }
.badge-danger { background: #fee2e2; color: #b91c1c; }

.badge-reply {
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
}

.badge-reply.replied { background: #e0f2fe; color: #0369a1; }
.badge-reply.pending { background: #fef3c7; color: #b45309; }

/* Actions */
.action-cell {
  display: flex;
  gap: 6px;
  justify-content: center;
}

.btn-icon {
  border: none;
  background: #f1f5f9;
  padding: 6px 10px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-icon:hover { transform: scale(1.1); }
.btn-delete:hover { background: #fee2e2; }

/* Modal */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
}

.modal-box {
  background: #fff;
  width: 100%;
  max-width: 500px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 10px 25px rgba(0,0,0,0.1);
}

.modal-header {
  padding: 16px 20px;
  background: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 { margin: 0; font-size: 16px; color: #0f172a; }
.close-btn { border: none; background: none; font-size: 20px; cursor: pointer; }

.modal-body { padding: 20px; }

.review-summary {
  background: #f1f5f9;
  padding: 12px;
  border-radius: 8px;
  margin-bottom: 16px;
  font-size: 13px;
}

.summary-quote { margin: 4px 0 0 0; color: #475569; font-style: italic; }

.form-group label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 6px;
  color: #334155;
}

.form-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #cbd5e1;
  border-radius: 8px;
  outline: none;
  box-sizing: border-box;
}

.modal-footer {
  padding: 12px 20px;
  border-top: 1px solid #e2e8f0;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.btn-cancel {
  padding: 8px 16px;
  border: 1px solid #cbd5e1;
  background: #fff;
  border-radius: 6px;
  cursor: pointer;
}

.btn-primary {
  padding: 8px 16px;
  background: #10b981;
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
}

.btn-primary:disabled { background: #94a3b8; cursor: not-allowed; }

/* Tăng độ rộng cột phản hồi để thoáng hơn */
.reply-cell {
  min-width: 260px;
  max-width: 300px;
}

.reply-box {
  background-color: #f0fdf4;
  border-left: 3px solid #10b981;
  padding: 8px 12px;
  border-radius: 0 8px 8px 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

/* Dòng 1: Tên người trả lời & Badge */
.reply-user-info {
  display: flex;
  align-items: center;
  gap: 6px;
  width: 100%;
}

.replier-name {
  font-size: 13px;
  font-weight: 700;
  color: #0f172a;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis; /* Tên cực dài sẽ tự cắt gọn bằng ... */
  max-width: 170px;
}

.role-badge {
  font-size: 10px;
  font-weight: 800;
  padding: 1px 5px;
  border-radius: 4px;
  letter-spacing: 0.5px;
  flex-shrink: 0; /* Giữ cố định kích thước Badge không bị co */
}

.role-badge.staff {
  background-color: #d1fae5;
  color: #047857;
}

.role-badge.admin {
  background-color: #fee2e2;
  color: #b91c1c;
}

/* Dòng 2: Ngày phản hồi mờ nhẹ bên dưới */
.reply-time {
  font-size: 11px;
  color: #94a3b8;
  margin-bottom: 4px;
}

/* Dòng 3: Nội dung nhận xét */
.reply-content {
  font-size: 13px;
  color: #334155;
  line-height: 1.4;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

</style>