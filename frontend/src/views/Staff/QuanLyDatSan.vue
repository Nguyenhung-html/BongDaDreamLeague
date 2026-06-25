<template>
  <div class="booking-manager">
    <div class="page-header">
      <div>
        <h1 class="page-header__title">Quản lý đặt sân</h1>
        <p class="page-header__desc">Tiếp nhận lịch đặt sân, check-in và xử lý thanh toán tại quầy.</p>
      </div>
      <button class="btn btn-primary" @click="openAddModal">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" style="margin-right: 6px;">
          <path d="M12 5v14M5 12h14" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
        </svg>
        Đặt sân tại quầy
      </button>
    </div>

    <div class="filter-card">
      <div class="filter-group">
        <div class="filter-item search-box">
          <input 
            type="text" 
            v-model="filters.search" 
            placeholder="Tìm tên khách, số điện thoại..." 
            class="form-control"
          />
        </div>
        <div class="filter-item">
          <select v-model="filters.type" class="form-control">
            <option value="">Tất cả loại sân</option>
            <option value="Sân 5">Sân 5 người</option>
            <option value="Sân 7">Sân 7 người</option>
            <option value="Sân 11">Sân 11 người</option>
          </select>
        </div>
        <div class="filter-item">
          <input type="date" v-model="filters.date" class="form-control" />
        </div>
        <div class="filter-item">
          <select v-model="filters.status" class="form-control">
            <option value="">Tất cả trạng thái</option>
            <option value="Đã thanh toán">Đã thanh toán</option>
            <option value="Chờ xác nhận">Chờ xác nhận</option>
            <option value="Đã hủy">Đã hủy</option>
          </select>
        </div>
      </div>
    </div>

    <div class="table-card">
      <table class="data-table">
        <thead>
          <tr>
            <th>Mã đơn</th>
            <th>Khách hàng</th>
            <th>Thông tin sân</th>
            <th>Thời gian</th>
            <th>Tổng tiền</th>
            <th>Trạng thái</th>
            <th style="text-align: center;">Hành động</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="booking in filteredBookings" :key="booking.id">
            <td><strong>#{{ booking.id }}</strong></td>
            <td>
              <div class="customer-info">
                <span class="customer-name">{{ booking.customerName }}</span>
                <span class="customer-phone">{{ booking.customerPhone }}</span>
              </div>
            </td>
            <td>
              <div class="field-info">
                <span class="field-name">{{ booking.fieldName }}</span>
                <span class="field-type-badge">{{ booking.fieldType }}</span>
              </div>
            </td>
            <td>
              <div class="time-info">
                <span class="time-slot">{{ booking.timeSlot }}</span>
                <span class="booking-date">{{ formatDate(booking.date) }}</span>
              </div>
            </td>
            <td><span class="price-text">{{ formatPrice(booking.totalPrice) }}</span></td>
            <td>
              <span :class="['badge', getStatusClass(booking.status)]">
                {{ booking.status }}
              </span>
            </td>
            <td>
              <div class="table-actions">
                <button class="btn-action btn-edit" title="Sửa thông tin" @click="openEditModal(booking)">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7M18.5 2.5a2.121 2.121 0 1 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                  </svg>
                </button>
                <button class="btn-action btn-delete" title="Hủy lịch đặt" @click="deleteBooking(booking.id)">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="3 6 5 6 21 6"></polyline>
                    <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                  </svg>
                </button>
              </div>
            </td>
          </tr>
          <tr v-if="filteredBookings.length === 0">
            <td colspan="7" class="text-center no-data">Không tìm thấy dữ liệu lịch đặt sân phù hợp.</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="modal-overlay" v-if="modal.show">
      <div class="modal-box">
        <div class="modal-header">
          <h2 class="modal-title">{{ modal.isEdit ? 'Cập nhật lịch đặt sân' : 'Đặt sân mới tại quầy' }}</h2>
          <button class="modal-close" @click="closeModal">&times;</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="saveBooking">
            <div class="form-row">
              <div class="form-group-item">
                <label class="form-label">Tên khách hàng *</label>
                <input type="text" v-model="form.customerName" required class="form-control" placeholder="Nhập tên khách" />
              </div>
              <div class="form-group-item">
                <label class="form-label">Số điện thoại *</label>
                <input type="text" v-model="form.customerPhone" required class="form-control" placeholder="Nhập số điện thoại" />
              </div>
            </div>

            <div class="form-row">
              <div class="form-group-item">
                <label class="form-label">Chọn Sân *</label>
                <select v-model="form.fieldName" @change="onFieldChange" required class="form-control">
                  <option value="">-- Chọn sân trống --</option>
                  <option v-for="f in availableFields" :key="f.name" :value="f.name">
                    {{ f.name }} ({{ f.type }})
                  </option>
                </select>
              </div>
              <div class="form-group-item">
                <label class="form-label">Ngày đá *</label>
                <input type="date" v-model="form.date" required class="form-control" />
              </div>
            </div>

            <div class="form-row">
              <div class="form-group-item">
                <label class="form-label">Khung giờ đá *</label>
                <select v-model="form.timeSlot" required class="form-control">
                  <option value="">-- Chọn ca trống --</option>
                  <option value="16:00 - 17:30">16:00 - 17:30 (Khung thường)</option>
                  <option value="17:30 - 19:00">17:30 - 19:00 (Khung giờ Vàng)</option>
                  <option value="19:00 - 20:30">19:00 - 20:30 (Khung giờ Vàng)</option>
                  <option value="20:30 - 22:00">20:30 - 22:00 (Khung thường)</option>
                </select>
              </div>
              <div class="form-group-item">
                <label class="form-label">Tổng tiền thanh toán (VNĐ) *</label>
                <input type="number" v-model="form.totalPrice" required class="form-control" placeholder="Ví dụ: 300000" />
              </div>
            </div>

            <div class="form-group-item" style="width: 100%;">
              <label class="form-label">Trạng thái đặt đơn</label>
              <select v-model="form.status" class="form-control">
                <option value="Chờ xác nhận">Chờ xác nhận (Đặt giữ chỗ trước)</option>
                <option value="Đã thanh toán">Đã thanh toán (Nhận tiền mặt/Chuyển khoản tại quầy)</option>
                <option value="Đã hủy">Hủy lịch đặt</option>
              </select>
            </div>

            <div class="modal-footer">
              <button type="button" class="btn btn-ghost" @click="closeModal">Đóng</button>
              <button type="submit" class="btn btn-primary">Xác nhận lưu</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

// Danh sách sân cứng mẫu (Khớp với dữ liệu xem sân của người dùng)
const availableFields = [
  { name: 'Sân A1', type: 'Sân 5' },
  { name: 'Sân A2', type: 'Sân 5' },
  { name: 'Sân B1', type: 'Sân 7' },
  { name: 'Sân B2', type: 'Sân 7' },
  { name: 'Sân Đại Thế Giới', type: 'Sân 11' },
]

// Mock Data Danh sách lịch đặt sân hiển thị cho nhân viên xử lý
const bookings = ref([
  { id: 1024, customerName: 'Nguyễn Trần Minh An', customerPhone: '0905123456', fieldName: 'Sân A1', fieldType: 'Sân 5', date: '2026-06-25', timeSlot: '17:30 - 19:00', totalPrice: 350000, status: 'Đã thanh toán' },
  { id: 1025, customerName: 'Lê Văn Thắng', customerPhone: '0978555666', fieldName: 'Sân B1', fieldType: 'Sân 7', date: '2026-06-25', timeSlot: '19:00 - 20:30', totalPrice: 500000, status: 'Chờ xác nhận' },
  { id: 1026, customerName: 'Trần Bình Trọng', customerPhone: '0914888999', fieldName: 'Sân A2', fieldType: 'Sân 5', date: '2026-06-26', timeSlot: '16:00 - 17:30', totalPrice: 250000, status: 'Đã thanh toán' },
  { id: 1027, customerName: 'Phạm Minh Hải', customerPhone: '0332111444', fieldName: 'Sân Đại Thế Giới', fieldType: 'Sân 11', date: '2026-06-27', timeSlot: '17:30 - 19:00', totalPrice: 1000000, status: 'Đã hủy' }
])

// Bộ lọc tìm kiếm
const filters = ref({
  search: '',
  type: '',
  date: '',
  status: ''
})

// Quản lý trạng thái Modal
const modal = ref({
  show: false,
  isEdit: false,
  editId: null
})

// Khởi tạo Form dữ liệu trống
const form = ref({
  customerName: '',
  customerPhone: '',
  fieldName: '',
  fieldType: '',
  date: '',
  timeSlot: '',
  totalPrice: 0,
  status: 'Chờ xác nhận'
})

// Logic xử lý tìm kiếm lọc dữ liệu thời gian thực
const filteredBookings = computed(() => {
  return bookings.value.filter(item => {
    const matchSearch = item.customerName.toLowerCase().includes(filters.value.search.toLowerCase()) || 
                        item.customerPhone.includes(filters.value.search)
    const matchType = filters.value.type ? item.fieldType === filters.value.type : true
    const matchDate = filters.value.date ? item.date === filters.value.date : true
    const matchStatus = filters.value.status ? item.status === filters.value.status : true
    return matchSearch && matchType && matchDate && matchStatus
  })
})

// Khi chọn tên sân trong Modal, tự động map loại sân tương ứng
function onFieldChange() {
  const selected = availableFields.find(f => f.name === form.value.fieldName)
  if (selected) form.value.fieldType = selected.type
}

// Bật đóng mở modal
function openAddModal() {
  modal.value.show = true
  modal.value.isEdit = false
  form.value = { customerName: '', customerPhone: '', fieldName: '', fieldType: '', date: new Date().toISOString().substr(0, 10), timeSlot: '', totalPrice: 300000, status: 'Chờ xác nhận' }
}

function openEditModal(booking) {
  modal.value.show = true
  modal.value.isEdit = true
  modal.value.editId = booking.id
  form.value = { ...booking }
}

function closeModal() {
  modal.value.show = false
}

// Chức năng: THÊM & SỬA LỊCH ĐẶT SÂN
function saveBooking() {
  if (modal.value.isEdit) {
    // Logic cập nhật phần tử cũ
    const idx = bookings.value.findIndex(b => b.id === modal.value.editId)
    if (idx !== -1) bookings.value[idx] = { ...form.value }
  } else {
    // Logic đẩy thêm phần tử mới vào mảng
    const newId = bookings.value.length > 0 ? Math.max(...bookings.value.map(b => b.id)) + 1 : 1001
    bookings.value.unshift({
      id: newId,
      ...form.value
    })
  }
  closeModal()
}

// Chức năng: XÓA (HỦY ĐƠN ĐẶT SÂN)
function deleteBooking(id) {
  if (confirm(`Bạn chắc chắn muốn hủy bỏ vĩnh viễn đơn đặt sân #${id}?`)) {
    bookings.value = bookings.value.filter(b => b.id !== id)
  }
}

// Định dạng hiển thị tiền và ngày
function formatPrice(val) {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val)
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const [year, month, day] = dateStr.split('-')
  return `${day}/${month}/${year}`
}

function getStatusClass(status) {
  if (status === 'Đã thanh toán') return 'badge-success'
  if (status === 'Chờ xác nhận') return 'badge-warning'
  return 'badge-danger'
}
</script>

<style scoped>
.booking-manager {
  width: 100%;
}

/* ===== HEADER ===== */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.page-header__title {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 4px 0;
}
.page-header__desc {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

/* ===== FILTER CARD ===== */
.filter-card {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 20px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.02);
}
.filter-group {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}
.filter-item {
  flex: 1;
  min-width: 180px;
}
.search-box {
  flex: 2;
  min-width: 260px;
}

/* ===== INPUT STYLES ===== */
.form-control {
  width: 100%;
  padding: 10px 14px;
  font-size: 14px;
  border: 1px solid #cbd5e1;
  border-radius: 8px;
  background-color: #ffffff;
  color: #1e293b;
  outline: none;
  box-sizing: border-box;
  transition: border-color 0.2s;
}
.form-control:focus {
  border-color: #28a745;
}

/* ===== DATA TABLE CARD ===== */
.table-card {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0,0,0,0.02);
}
.data-table {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
  font-size: 14px;
}
.data-table th {
  background-color: #f8fafc;
  color: #64748b;
  font-weight: 600;
  padding: 16px;
  border-bottom: 1px solid #e2e8f0;
}
.data-table td {
  padding: 16px;
  border-bottom: 1px solid #e2e8f0;
  color: #334155;
  vertical-align: middle;
}
.data-table tbody tr:hover {
  background-color: #f8fafc;
}

/* ===== CELLS TYPOGRAPHY ===== */
.customer-info, .field-info, .time-info {
  display: flex;
  flex-direction: column;
  gap: 3px;
}
.customer-name { font-weight: 600; color: #1e293b; }
.customer-phone { font-size: 12px; color: #64748b; }
.field-name { font-weight: 500; }
.field-type-badge {
  font-size: 11px;
  background: #f1f5f9;
  color: #475569;
  padding: 2px 6px;
  border-radius: 4px;
  width: max-content;
}
.time-slot { font-weight: 600; color: #0f172a; }
.booking-date { font-size: 12px; color: #64748b; }
.price-text { font-weight: 700; color: #1e293b; }

/* ===== BADGES STATUS ===== */
.badge {
  display: inline-flex;
  padding: 4px 10px;
  font-size: 12px;
  font-weight: 600;
  border-radius: 12px;
}
.badge-success { background: #dcfce7; color: #15803d; }
.badge-warning { background: #fef3c7; color: #b45309; }
.badge-danger { background: #fee2e2; color: #b91c1c; }

/* ===== ACTIONS BUTTONS ===== */
.table-actions {
  display: flex;
  justify-content: center;
  gap: 8px;
}
.btn-action {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  border: 1px solid #cbd5e1;
  background: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}
.btn-edit:hover { color: #28a745; border-color: #28a745; background: rgba(40,167,69,0.05); }
.btn-delete:hover { color: #ef4444; border-color: #ef4444; background: rgba(239,68,68,0.05); }

/* ===== COMMON BUTTONS ===== */
.btn {
  display: inline-flex;
  align-items: center;
  padding: 10px 20px;
  font-size: 14px;
  font-weight: 600;
  border-radius: 8px;
  cursor: pointer;
  border: none;
  transition: all 0.2s;
  text-decoration: none;
}
.btn-primary { background: #28a745; color: #ffffff; }
.btn-primary:hover { background: #218838; }
.btn-ghost { background: #f1f5f9; color: #475569; }
.btn-ghost:hover { background: #e2e8f0; }

/* ===== MODAL OVERLAY ===== */
.modal-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(15, 23, 42, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
}
.modal-box {
  background: #ffffff;
  border-radius: 16px;
  width: 100%;
  max-width: 600px;
  box-shadow: 0 20px 25px -5px rgba(0,0,0,0.1);
  overflow: hidden;
}
.modal-header {
  padding: 20px 24px;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.modal-title { font-size: 18px; font-weight: 700; color: #1e293b; margin: 0; }
.modal-close { background: none; border: none; font-size: 24px; color: #94a3b8; cursor: pointer; }
.modal-body { padding: 24px; }

.form-row {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}
.form-group-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 16px;
}
.form-label { font-size: 13px; font-weight: 600; color: #475569; }

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}

.text-center { text-align: center; }
.no-data { padding: 32px !important; color: #94a3b8; }
</style>

