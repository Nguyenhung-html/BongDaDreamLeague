<template>
  <div class="payment-manager">
    <!-- HEADER -->
    <div class="page-header">
      <div>
        <h1 class="page-title">Quản lý thanh toán</h1>
        <p class="page-desc">Xác nhận cọc và thu phần còn lại tại quầy.</p>
      </div>
      <div class="header-controls">
        <input type="date" v-model="filters.tuNgay" class="form-control"/>
        <span style="color:#94a3b8">đến</span>
        <input type="date" v-model="filters.denNgay" class="form-control"/>
        <button class="btn btn-primary" @click="fetchData">🔄 Lọc</button>
      </div>
    </div>

    <!-- TOAST -->
    <div v-if="toast.show" :class="['toast', toast.type]">{{ toast.msg }}</div>

    <!-- SUMMARY CARDS -->
    <div class="summary-grid">
      <div class="summary-card">
        <div class="card-icon icon-total">💰</div>
        <div class="card-body">
          <span class="card-label">Tổng thu hôm nay</span>
          <span class="card-value">{{ formatMoney(tongHomNay) }}</span>
        </div>
      </div>
      <div class="summary-card">
        <div class="card-icon icon-pending">⏳</div>
        <div class="card-body">
          <span class="card-label">Cọc chờ xác nhận</span>
          <span class="card-value">{{ coCho }}</span>
        </div>
      </div>
      <div class="summary-card">
        <div class="card-icon icon-done">✅</div>
        <div class="card-body">
          <span class="card-label">Giao dịch thành công</span>
          <span class="card-value">{{ thanhCong }}</span>
        </div>
      </div>
    </div>

    <!-- FILTER BAR -->
    <div class="filter-bar">
      <input type="text" v-model="filters.search" placeholder="Tìm khách hàng, sân..." class="form-control search-box"/>
      <select v-model="filters.loai" class="form-control">
        <option value="">Tất cả loại</option>
        <option value="COC">Tiền cọc</option>
        <option value="THANH_TOAN_CON_LAI">Thanh toán nốt</option>
      </select>
      <select v-model="filters.trangThai" class="form-control">
        <option value="">Tất cả trạng thái</option>
        <option value="DANG_CHO">Chờ xác nhận</option>
        <option value="THANH_CONG">Thành công</option>
        <option value="THAT_BAI">Thất bại</option>
      </select>
    </div>

    <!-- TABLE -->
    <div v-if="loading" class="state-box">⏳ Đang tải...</div>
    <div v-else-if="error" class="state-box error">{{ error }}</div>
    <div v-else class="table-card">
      <table class="data-table">
        <thead>
          <tr>
            <th>Mã đơn</th>
            <th>Khách hàng</th>
            <th>Sân / Thời gian</th>
            <th>Loại</th>
            <th>Số tiền</th>
            <th>Phương thức</th>
            <th>Trạng thái</th>
            <th style="text-align:center">Hành động</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="tt in filteredList" :key="tt.id">
            <td><strong class="ma-don">#{{ tt.maGanDo }}</strong></td>
            <td>
              <div class="cell-stack">
                <span class="bold">{{ tt.tenKhach }}</span>
                <span class="sub">{{ tt.soDienThoai }}</span>
              </div>
            </td>
            <td>
              <div class="cell-stack">
                <span class="bold">{{ tt.tenSan }}</span>
                <span class="sub">{{ tt.gioBatDau }} - {{ tt.gioKetThuc }} | {{ formatDate(tt.ngayDa) }}</span>
              </div>
            </td>
            <td>
              <span :class="['badge-loai', tt.loai === 'COC' ? 'badge-coc' : 'badge-con-lai']">
                {{ tt.loai === 'COC' ? '💳 Cọc' : '💵 Thanh toán nốt' }}
              </span>
            </td>
            <td><span class="price">{{ formatMoney(tt.soTien) }}</span></td>
            <td>
              <span class="phuong-thuc">{{ tt.phuongThuc === 'QR' ? '📱 QR' : '💵 Tiền mặt' }}</span>
            </td>
            <td>
              <span :class="['badge', badgeTT(tt.trangThai)]">{{ labelTT(tt.trangThai) }}</span>
            </td>
            <td>
              <div class="actions">
                <button v-if="tt.trangThai === 'DANG_CHO' && tt.loai === 'COC'" class="btn-act confirm" @click="xacNhanCoc(tt)">✓ XN Cọc</button>
                <button v-if="tt.loai === 'COC' && tt.trangThai === 'THANH_CONG'" class="btn-act thu" @click="openThuNot(tt)">💵 Thu nốt</button>
                <span v-if="tt.trangThai === 'THANH_CONG' && tt.loai === 'THANH_TOAN_CON_LAI'" class="no-action">Hoàn tất</span>
              </div>
            </td>
          </tr>
          <tr v-if="filteredList.length === 0">
            <td colspan="8" class="no-data">Không có giao dịch nào.</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- MODAL THU NỐT -->
    <div class="overlay" v-if="thuNotModal.show">
      <div class="modal-box">
        <div class="modal-header">
          <h2>💵 Thu phần còn lại</h2>
          <button class="close-btn" @click="thuNotModal.show = false">&times;</button>
        </div>
        <div class="modal-body">
          <div class="info-block">
            <div class="info-row"><span class="info-label">Khách hàng:</span><span>{{ thuNotModal.tt?.tenKhach }}</span></div>
            <div class="info-row"><span class="info-label">Sân:</span><span>{{ thuNotModal.tt?.tenSan }}</span></div>
            <div class="info-row"><span class="info-label">Thời gian:</span><span>{{ thuNotModal.tt?.gioBatDau }} - {{ thuNotModal.tt?.gioKetThuc }}</span></div>
          </div>

          <!-- Đang tải dịch vụ -->
          <div v-if="dangTaiGioHang" class="dich-vu-loading">Đang tải dịch vụ đã gọi...</div>

          <!-- Danh sách dịch vụ đã gọi (Staff xem để chuẩn bị) -->
          <div class="dich-vu-block" v-else-if="gioHangThuNot">
            <p class="dich-vu-title">🛒 Dịch vụ đã gọi</p>
            <div v-if="gioHangThuNot.danhSach.length === 0" class="dich-vu-trong">Khách chưa gọi dịch vụ nào.</div>
            <div v-else class="dich-vu-list">
              <div v-for="ct in gioHangThuNot.danhSach" :key="ct.id" class="dich-vu-row">
                <span>{{ ct.tenSanPham }} × {{ ct.soLuong }}</span>
                <strong>{{ formatMoney(ct.thanhTien) }}</strong>
              </div>
            </div>

            <div class="tong-tien-block">
              <div class="info-row">
                <span class="info-label">Còn lại tiền sân:</span>
                <span>{{ formatMoney(gioHangThuNot.tienConLaiSan) }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">Tiền dịch vụ:</span>
                <span>{{ formatMoney(gioHangThuNot.tongTienDichVu) }}</span>
              </div>
              <div class="info-row highlight">
                <span class="info-label">Cần thu nốt:</span>
                <span class="price-big">{{ formatMoney(tinhConLai()) }}</span>
              </div>
            </div>
          </div>

          <!-- Trường hợp API dịch vụ lỗi, vẫn cho thu tiền sân bình thường -->
          <div class="info-block" v-else>
            <div class="info-row highlight">
              <span class="info-label">Cần thu nốt:</span>
              <span class="price-big">{{ formatMoney(tinhConLai()) }}</span>
            </div>
          </div>

          <div class="form-group" style="margin-top:16px">
            <label class="form-label">Phương thức thanh toán</label>
            <div class="toggle-row">
              <button :class="['toggle-btn', thuNotModal.phuongThuc === 'TIEN_MAT' ? 'active-green' : '']" @click="thuNotModal.phuongThuc = 'TIEN_MAT'">💵 Tiền mặt</button>
              <button :class="['toggle-btn', thuNotModal.phuongThuc === 'QR' ? 'active-green' : '']" @click="thuNotModal.phuongThuc = 'QR'">📱 QR Code</button>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-ghost" @click="thuNotModal.show = false">Đóng</button>
          <button class="btn btn-primary" @click="submitThuNot" :disabled="thuNotModal.loading">
            {{ thuNotModal.loading ? 'Đang xử lý...' : 'Xác nhận thu tiền' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import staffService from '@/services/staffService'

const API = 'http://localhost:8080/api'

const danhSach = ref([])
const loading = ref(false)
const error = ref('')
const toast = ref({ show: false, msg: '', type: 'success' })
const filters = ref({ search: '', loai: '', trangThai: '', tuNgay: '', denNgay: '' })
const thuNotModal = ref({ show: false, tt: null, phuongThuc: 'TIEN_MAT', loading: false })

// ===== MỚI THÊM: dữ liệu dịch vụ đã gọi cho đơn đang thu nốt =====
const gioHangThuNot = ref(null)
const dangTaiGioHang = ref(false)

onMounted(fetchData)

async function fetchData() {
  loading.value = true; error.value = ''
  try {
    danhSach.value = await staffService.layDanhSachThanhToan({ tuNgay: filters.value.tuNgay, denNgay: filters.value.denNgay })
  } catch (e) { error.value = e.message }
  finally { loading.value = false }
}

const filteredList = computed(() => {
  return danhSach.value.filter(tt => {
    const s = filters.value.search.toLowerCase()
    const matchSearch = !s || tt.tenKhach?.toLowerCase().includes(s) || tt.tenSan?.toLowerCase().includes(s)
    const matchLoai = !filters.value.loai || tt.loai === filters.value.loai
    const matchTT = !filters.value.trangThai || tt.trangThai === filters.value.trangThai
    return matchSearch && matchLoai && matchTT
  })
})

const tongHomNay = computed(() => {
  const today = new Date().toISOString().slice(0, 10)
  return danhSach.value.filter(t => t.trangThai === 'THANH_CONG' && t.ngayThanhToan?.includes(new Date().toLocaleDateString('vi-VN').split('/').reverse().join('-').slice(0,10))).reduce((s, t) => s + (parseFloat(t.soTien) || 0), 0)
})

const coCho = computed(() => danhSach.value.filter(t => t.trangThai === 'DANG_CHO').length)
const thanhCong = computed(() => danhSach.value.filter(t => t.trangThai === 'THANH_CONG').length)

async function xacNhanCoc(tt) {
  if (!confirm(`Xác nhận đã nhận tiền cọc ${formatMoney(tt.soTien)} từ ${tt.tenKhach}?`)) return
  try {
    await staffService.xacNhanCoc(tt.id)
    tt.trangThai = 'THANH_CONG'
    showToast('Xác nhận cọc thành công!')
  } catch (e) { showToast(e.message, 'error') }
}

// ===== SỬA: mở modal thu nốt, đồng thời tải dịch vụ đã gọi của đúng đơn đặt sân này =====
async function openThuNot(tt) {
  thuNotModal.value = { show: true, tt, phuongThuc: 'TIEN_MAT', loading: false }
  await taiGioHangChoDon(tt)
}

async function taiGioHangChoDon(tt) {
  gioHangThuNot.value = null
  if (!tt.datSanId) return // an toàn nếu backend chưa trả kèm datSanId
  dangTaiGioHang.value = true
  try {
    const token = localStorage.getItem('token')
    const res = await fetch(`${API}/dat-san/${tt.datSanId}/dich-vu`, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    if (res.ok) {
      gioHangThuNot.value = await res.json()
    }
  } catch (e) {
    // im lặng bỏ qua, modal vẫn hiện được số tiền sân cơ bản như cũ nếu lỗi
  } finally {
    dangTaiGioHang.value = false
  }
}

// ===== SỬA: tính đúng tổng cần thu = tiền sân còn lại + tiền dịch vụ (nếu có) =====
function tinhConLai() {
  if (!thuNotModal.value.tt) return 0
  // Nếu tải được giỏ hàng dịch vụ thật từ API, dùng đúng số đã tính sẵn ở đó
  if (gioHangThuNot.value) {
    return gioHangThuNot.value.tongCanThanhToan
  }
  // Dự phòng: nếu API dịch vụ lỗi/không có datSanId, tạm hiện đúng tiền cọc như cũ
  return thuNotModal.value.tt.soTien || 0
}

async function submitThuNot() {
  thuNotModal.value.loading = true
  try {
    await staffService.thanhToanConLai(thuNotModal.value.tt.datSanId, thuNotModal.value.phuongThuc)
    showToast('Thu tiền thành công! Đơn đã hoàn thành.')
    thuNotModal.value.show = false
    gioHangThuNot.value = null
    await fetchData()
  } catch (e) { showToast(e.message, 'error') }
  finally { thuNotModal.value.loading = false }
}

function showToast(msg, type = 'success') {
  toast.value = { show: true, msg, type }
  setTimeout(() => { toast.value.show = false }, 3500)
}

function formatMoney(v) { return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(v || 0) }
function formatDate(s) { if (!s) return ''; const [y,m,d] = s.split('-'); return `${d}/${m}/${y}` }
function labelTT(t) { return { DANG_CHO: 'Chờ XN', THANH_CONG: 'Thành công', THAT_BAI: 'Thất bại' }[t] || t }
function badgeTT(t) { return { DANG_CHO: 'badge-warning', THANH_CONG: 'badge-success', THAT_BAI: 'badge-danger' }[t] || '' }
</script>

<style scoped>
.payment-manager { width: 100%; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; flex-wrap: wrap; gap: 12px; }
.page-title { font-size: 24px; font-weight: 700; color: #1e293b; margin: 0 0 4px; }
.page-desc { font-size: 14px; color: #64748b; margin: 0; }
.header-controls { display: flex; gap: 8px; align-items: center; flex-wrap: wrap; }
.toast { position: fixed; top: 20px; right: 24px; z-index: 9999; padding: 12px 20px; border-radius: 10px; font-weight: 600; font-size: 14px; box-shadow: 0 4px 12px rgba(0,0,0,.15); }
.toast.success { background: #dcfce7; color: #15803d; border: 1px solid #bbf7d0; }
.toast.error { background: #fee2e2; color: #b91c1c; border: 1px solid #fecaca; }
.summary-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 14px; margin-bottom: 20px; }
.summary-card { display: flex; align-items: center; gap: 14px; background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 16px 20px; }
.card-icon { font-size: 28px; width: 48px; height: 48px; border-radius: 12px; display: flex; align-items: center; justify-content: center; }
.icon-total { background: #fef9c3; }
.icon-pending { background: #fef3c7; }
.icon-done { background: #dcfce7; }
.card-body { display: flex; flex-direction: column; }
.card-label { font-size: 12px; color: #64748b; font-weight: 500; }
.card-value { font-size: 20px; font-weight: 700; color: #1e293b; }
.filter-bar { display: flex; flex-wrap: wrap; gap: 10px; margin-bottom: 16px; align-items: center; }
.search-box { flex: 2; min-width: 200px; }
.form-control { padding: 9px 13px; border: 1px solid #cbd5e1; border-radius: 8px; font-size: 14px; outline: none; background: #fff; color: #1e293b; }
.form-control:focus { border-color: #1E3932; }
.table-card { background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; overflow: hidden; }
.data-table { width: 100%; border-collapse: collapse; font-size: 14px; }
.data-table th { background: #f8fafc; color: #64748b; font-weight: 600; padding: 14px 16px; text-align: left; border-bottom: 1px solid #e2e8f0; }
.data-table td { padding: 12px 16px; border-bottom: 1px solid #f1f5f9; vertical-align: middle; }
.data-table tbody tr:hover { background: #f8fafc; }
.cell-stack { display: flex; flex-direction: column; gap: 3px; }
.bold { font-weight: 600; color: #1e293b; }
.sub { font-size: 12px; color: #64748b; }
.ma-don { color: #1E3932; font-size: 13px; }
.price { font-weight: 700; color: #1e293b; }
.phuong-thuc { font-size: 13px; color: #475569; }
.badge-loai { display: inline-flex; padding: 4px 10px; border-radius: 6px; font-size: 12px; font-weight: 600; }
.badge-coc { background: #ede9fe; color: #6d28d9; }
.badge-con-lai { background: #fef3c7; color: #92400e; }
.badge { display: inline-flex; padding: 4px 10px; border-radius: 20px; font-size: 12px; font-weight: 600; }
.badge-warning { background: #fef3c7; color: #b45309; }
.badge-success { background: #dcfce7; color: #15803d; }
.badge-danger { background: #fee2e2; color: #b91c1c; }
.actions { display: flex; gap: 6px; justify-content: center; }
.btn-act { padding: 5px 10px; border-radius: 6px; border: none; font-size: 12px; font-weight: 600; cursor: pointer; transition: .2s; }
.btn-act.confirm { background: #dbeafe; color: #1d4ed8; }
.btn-act.confirm:hover { background: #bfdbfe; }
.btn-act.thu { background: #fef3c7; color: #92400e; }
.btn-act.thu:hover { background: #fde68a; }
.no-action { color: #94a3b8; font-size: 12px; }
.btn { display: inline-flex; align-items: center; gap: 6px; padding: 9px 16px; font-size: 13px; font-weight: 600; border-radius: 8px; border: none; cursor: pointer; transition: .2s; white-space: nowrap; }
.btn-primary { background: #1E3932; color: #fff; }
.btn-primary:hover { background: #14261f; }
.btn-ghost { background: #f1f5f9; color: #475569; }
.btn-ghost:hover { background: #e2e8f0; }
.overlay { position: fixed; inset: 0; background: rgba(15,23,42,.45); display: flex; align-items: center; justify-content: center; z-index: 200; }
.modal-box { background: #fff; border-radius: 16px; width: 100%; max-width: 480px; box-shadow: 0 20px 40px rgba(0,0,0,.15); overflow: hidden; max-height: 88vh; overflow-y: auto; }
.modal-header { padding: 18px 22px; border-bottom: 1px solid #e2e8f0; display: flex; justify-content: space-between; align-items: center; }
.modal-header h2 { font-size: 17px; font-weight: 700; color: #1e293b; margin: 0; }
.close-btn { background: none; border: none; font-size: 22px; color: #94a3b8; cursor: pointer; }
.modal-body { padding: 20px 22px; }
.modal-footer { padding: 14px 22px; border-top: 1px solid #e2e8f0; display: flex; justify-content: flex-end; gap: 10px; }
.info-block { background: #f8fafc; border-radius: 10px; padding: 14px; }
.info-row { display: flex; gap: 12px; align-items: center; justify-content: space-between; padding: 7px 0; border-bottom: 1px solid #f1f5f9; }
.info-row.highlight { background: #fef9c3; border-radius: 6px; padding: 8px; margin-top: 4px; border-bottom: none; }
.info-label { font-weight: 600; color: #64748b; min-width: 100px; font-size: 13px; }
.price-big { font-size: 20px; font-weight: 800; color: #1E3932; }
.form-group { display: flex; flex-direction: column; gap: 6px; }
.form-label { font-size: 13px; font-weight: 600; color: #475569; }
.toggle-row { display: flex; gap: 8px; }
.toggle-btn { flex: 1; padding: 10px; border: 1px solid #cbd5e1; border-radius: 8px; background: #f8fafc; cursor: pointer; font-size: 13px; font-weight: 600; transition: .2s; }
.toggle-btn.active-green { background: #dcfce7; border-color: #86efac; color: #15803d; }
.state-box { text-align: center; padding: 40px; color: #64748b; }
.state-box.error { color: #b91c1c; }
.no-data { text-align: center; padding: 32px; color: #94a3b8; }

/* ===== MỚI THÊM: khối hiển thị dịch vụ đã gọi trong modal thu nốt ===== */
.dich-vu-loading { text-align: center; padding: 16px 0; color: #94a3b8; font-size: 13px; margin-top: 14px; }
.dich-vu-block { margin-top: 14px; background: #f8fafc; border-radius: 10px; padding: 14px; }
.dich-vu-title { font-size: 13.5px; font-weight: 700; color: #1e293b; margin: 0 0 8px; }
.dich-vu-trong { font-size: 13px; color: #94a3b8; padding: 4px 0; }
.dich-vu-list { display: flex; flex-direction: column; gap: 6px; margin-bottom: 10px; }
.dich-vu-row { display: flex; justify-content: space-between; font-size: 13px; color: #475569; padding: 3px 0; border-bottom: 1px dashed #e2e8f0; }
.tong-tien-block { border-top: 1px solid #e2e8f0; padding-top: 8px; display: flex; flex-direction: column; gap: 4px; }
</style>