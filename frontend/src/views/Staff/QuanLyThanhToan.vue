<template>
  <div class="qltt">

    <!-- ===== HEADER ===== -->
    <div class="page-header">
      <div>
        <span class="eyebrow">⚽ Bảng điều khiển · Staff</span>
        <h1 class="page-title">Quản lý thanh toán</h1>
        <p class="page-desc">Xác nhận cọc và thu phần còn lại tại quầy.</p>
      </div>
      <button class="btn-ghost" @click="fetchData">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 12a9 9 0 1 1-2.64-6.36"/><path d="M21 4v6h-6"/></svg>
        Làm mới
      </button>
    </div>

    <!-- ===== TOAST ===== -->
    <Transition name="toast-fade">
      <div v-if="toast.show" :class="['toast', toast.type]">
        <span class="toast__dot"></span>{{ toast.msg }}
      </div>
    </Transition>

    <!-- ===== SUMMARY CARDS ===== -->
    <div class="summary-grid">

      <!-- Tổng thu — chọn theo từng ngày -->
      <div class="summary-card summary-card--stat">
        <div class="card-icon icon-total">💰</div>
        <div class="card-body">
          <span class="card-label">Tổng thu</span>
          <span class="card-value">{{ formatMoney(tongTheoNgay) }}</span>
        </div>
        <div class="card-day-nav">
          <button class="day-nav day-nav--sm" @click="shiftNgayThongKe(-1)" aria-label="Ngày trước">
            <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.6"><path d="M15 18l-6-6 6-6"/></svg>
          </button>
          <span class="card-day-label" :class="{ 'card-day-label--today': isThongKeToday }">
            {{ isThongKeToday ? 'Hôm nay' : formatDate(ngayThongKe) }}
          </span>
          <button class="day-nav day-nav--sm" @click="shiftNgayThongKe(1)" aria-label="Ngày sau">
            <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.6"><path d="M9 18l6-6-6-6"/></svg>
          </button>
          <input type="date" v-model="ngayThongKe" class="card-day-input" title="Chọn ngày bất kỳ"/>
        </div>
      </div>

      <!-- Cọc chờ xác nhận — bấm để lọc ngay danh sách bên dưới -->
      <button
        class="summary-card summary-card--click"
        :class="{ 'summary-card--active': activeQuickFilter === 'PENDING' }"
        @click="toggleQuick('PENDING')"
      >
        <div class="card-icon icon-pending">⏳</div>
        <div class="card-body">
          <span class="card-label">Cọc chờ xác nhận</span>
          <span class="card-value">{{ coCho }}</span>
        </div>
        <span class="card-hint">{{ activeQuickFilter === 'PENDING' ? 'Đang lọc — bấm để bỏ' : 'Bấm để xem danh sách' }}</span>
      </button>

      <!-- Giao dịch thành công — bấm để lọc -->
      <button
        class="summary-card summary-card--click"
        :class="{ 'summary-card--active': activeQuickFilter === 'SUCCESS' }"
        @click="toggleQuick('SUCCESS')"
      >
        <div class="card-icon icon-done">✅</div>
        <div class="card-body">
          <span class="card-label">Giao dịch thành công</span>
          <span class="card-value">{{ thanhCong }}</span>
        </div>
        <span class="card-hint">{{ activeQuickFilter === 'SUCCESS' ? 'Đang lọc — bấm để bỏ' : 'Bấm để xem danh sách' }}</span>
      </button>
    </div>

    <!-- ===== BỘ LỌC ===== -->
    <div class="filter-card">
      <div class="filter-top">
        <div class="search-wrap">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="7"/><path d="M21 21l-4.3-4.3"/></svg>
          <input type="text" v-model="filters.search" placeholder="Tìm khách hàng, sân..." class="search-input"/>
        </div>

        <div class="pill-select">
          <label>Loại giao dịch</label>
          <select v-model="filters.loai" class="form-control">
            <option value="">Tất cả loại</option>
            <option value="COC">Tiền cọc</option>
            <option value="THANH_TOAN_CON_LAI">Thanh toán nốt</option>
          </select>
        </div>

        <div class="pill-select">
          <label>Trạng thái</label>
          <select v-model="filters.trangThai" class="form-control">
            <option value="">Tất cả trạng thái</option>
            <option value="DANG_CHO">Chờ xác nhận</option>
            <option value="THANH_CONG">Thành công</option>
            <option value="THAT_BAI">Thất bại</option>
          </select>
        </div>

        <button class="btn-ghost btn-ghost--sm" @click="resetFilters" v-if="hasActiveFilters">
          <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4"><path d="M18 6L6 18M6 6l12 12"/></svg>
          Xóa lọc
        </button>
      </div>

      <!-- Bộ lọc theo ngày: dải ngày rõ ràng thay vì ô nhập khoảng ngày -->
      <div class="day-strip">
        <span class="day-strip__label">Chọn ngày</span>

        <button class="day-nav" @click="dayOffset -= 7" aria-label="Tuần trước">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4"><path d="M15 18l-6-6 6-6"/></svg>
        </button>

        <button class="day-chip day-chip--all" :class="{ 'day-chip--active': !filters.ngay }" @click="filters.ngay = ''">
          Tất cả
        </button>

        <div class="day-chip-track">
          <button
            v-for="d in ngayOptions"
            :key="d.iso"
            class="day-chip"
            :class="{ 'day-chip--active': filters.ngay === d.iso, 'day-chip--today': d.isToday }"
            @click="filters.ngay = filters.ngay === d.iso ? '' : d.iso"
          >
            <span class="day-chip__dow">{{ d.dow }}</span>
            <span class="day-chip__num">{{ d.dayNum }}</span>
            <span class="day-chip__mon">Th{{ d.month }}</span>
            <span v-if="d.isToday" class="day-chip__tag">Hôm nay</span>
          </button>
        </div>

        <button class="day-nav" @click="dayOffset += 7" aria-label="Tuần sau">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4"><path d="M9 18l6-6-6-6"/></svg>
        </button>

        <div class="day-jump">
          <input type="date" v-model="filters.ngay" class="day-jump__input" title="Chọn ngày bất kỳ"/>
        </div>
      </div>

      <!-- Bộ lọc theo khung giờ -->
      <div class="gio-strip">
        <span class="day-strip__label">Khung giờ</span>
        <select v-model="filters.tuGio" class="form-control gio-select">
          <option value="">Từ giờ</option>
          <option v-for="h in gioOptions" :key="'tu'+h" :value="h">{{ h }}</option>
        </select>
        <span class="gio-dash">—</span>
        <select v-model="filters.denGio" class="form-control gio-select">
          <option value="">Đến giờ</option>
          <option v-for="h in gioOptions" :key="'den'+h" :value="h">{{ h }}</option>
        </select>

        <div class="gio-quick">
          <button
            v-for="q in khungGioNhanh"
            :key="q.label"
            class="gio-chip"
            :class="{ 'gio-chip--active': filters.tuGio === q.tu && filters.denGio === q.den }"
            @click="chonKhungGioNhanh(q)"
          >
            {{ q.label }}
          </button>
        </div>

        <button v-if="filters.tuGio || filters.denGio" class="btn-ghost btn-ghost--sm" @click="filters.tuGio = ''; filters.denGio = ''">
          Tất cả khung giờ
        </button>
      </div>
    </div>

    <!-- ===== TABLE ===== -->
    <div v-if="loading" class="state-box"><span class="spinner"></span> Đang tải...</div>
    <div v-else-if="error" class="state-box error">⚠ {{ error }}</div>
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
            <td colspan="8" class="no-data">
              <div class="no-data__icon">🔍</div>
              Không có giao dịch nào khớp với bộ lọc hiện tại.
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- ===== MODAL THU NỐT ===== -->
    <div class="overlay" v-if="thuNotModal.show" @click.self="thuNotModal.show = false">
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

          <div v-if="dangTaiGioHang" class="dich-vu-loading"><span class="spinner"></span> Đang tải dịch vụ đã gọi...</div>

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
          <button class="btn-ghost" @click="thuNotModal.show = false">Đóng</button>
          <button class="btn-primary" @click="submitThuNot" :disabled="thuNotModal.loading">
            {{ thuNotModal.loading ? 'Đang xử lý...' : 'Xác nhận thu tiền' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import staffService from '@/services/staffService'

const API = '/api'

const danhSach = ref([])
const loading = ref(false)
const error = ref('')
const toast = ref({ show: false, msg: '', type: 'success' })
const filters = ref({ search: '', loai: '', trangThai: '', ngay: '', tuGio: '', denGio: '' })
const thuNotModal = ref({ show: false, tt: null, phuongThuc: 'TIEN_MAT', loading: false })

const gioHangThuNot = ref(null)
const dangTaiGioHang = ref(false)

const gioOptions = ['06:00','07:00','08:00','09:00','10:00','11:00','12:00','13:00','14:00','15:00','16:00','17:00','18:00','19:00','20:00','21:00','22:00']
const khungGioNhanh = [
  { label: 'Sáng', tu: '06:00', den: '11:00' },
  { label: 'Trưa', tu: '11:00', den: '14:00' },
  { label: 'Chiều', tu: '14:00', den: '18:00' },
  { label: 'Tối', tu: '18:00', den: '22:00' }
]
function chonKhungGioNhanh(q) {
  if (filters.value.tuGio === q.tu && filters.value.denGio === q.den) {
    filters.value.tuGio = ''; filters.value.denGio = ''
  } else {
    filters.value.tuGio = q.tu; filters.value.denGio = q.den
  }
}

onMounted(fetchData)

// Tải toàn bộ giao dịch một lần; mọi lọc theo ngày / khung giờ / trạng thái
// được xử lý ở phía client để phản hồi ngay lập tức khi bấm chip ngày hoặc thẻ tổng hợp.
async function fetchData() {
  loading.value = true; error.value = ''
  try {
    danhSach.value = await staffService.layDanhSachThanhToan({ tuNgay: '', denNgay: '' })
  } catch (e) { error.value = e.message }
  finally { loading.value = false }
}

/* ================= DẢI CHỌN NGÀY ================= */
const dayOffset = ref(0)
const DOW = ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7']

function toIso(d) {
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

const ngayOptions = computed(() => {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  const arr = []
  for (let i = 0; i < 7; i++) {
    const d = new Date(today)
    d.setDate(d.getDate() + dayOffset.value + i)
    const iso = toIso(d)
    arr.push({
      iso,
      dow: DOW[d.getDay()],
      dayNum: d.getDate(),
      month: d.getMonth() + 1,
      isToday: iso === toIso(today)
    })
  }
  return arr
})

/* ================= LỌC ================= */
const filteredList = computed(() => {
  return danhSach.value.filter(tt => {
    const s = filters.value.search.toLowerCase()
    const matchSearch = !s || tt.tenKhach?.toLowerCase().includes(s) || tt.tenSan?.toLowerCase().includes(s)
    const matchLoai = !filters.value.loai || tt.loai === filters.value.loai
    const matchTT = !filters.value.trangThai || tt.trangThai === filters.value.trangThai
    const matchNgay = !filters.value.ngay || tt.ngayDa === filters.value.ngay
    const matchTuGio = !filters.value.tuGio || tt.gioBatDau >= filters.value.tuGio
    const matchDenGio = !filters.value.denGio || tt.gioBatDau <= filters.value.denGio
    return matchSearch && matchLoai && matchTT && matchNgay && matchTuGio && matchDenGio
  })
})

const hasActiveFilters = computed(() =>
  !!(filters.value.search || filters.value.loai || filters.value.trangThai || filters.value.ngay || filters.value.tuGio || filters.value.denGio)
)
function resetFilters() {
  filters.value = { search: '', loai: '', trangThai: '', ngay: '', tuGio: '', denGio: '' }
}

/* ================= THẺ TỔNG HỢP ================= */
// Cọc chờ xác nhận / Giao dịch thành công — bấm để lọc thẳng danh sách bên dưới
const activeQuickFilter = computed(() => {
  if (filters.value.trangThai === 'DANG_CHO' && filters.value.loai === 'COC') return 'PENDING'
  if (filters.value.trangThai === 'THANH_CONG' && !filters.value.loai) return 'SUCCESS'
  return ''
})
function toggleQuick(type) {
  if (activeQuickFilter.value === type) {
    filters.value.trangThai = ''
    filters.value.loai = ''
    return
  }
  if (type === 'PENDING') {
    filters.value.trangThai = 'DANG_CHO'
    filters.value.loai = 'COC'
  } else if (type === 'SUCCESS') {
    filters.value.trangThai = 'THANH_CONG'
    filters.value.loai = ''
  }
}

const coCho = computed(() => danhSach.value.filter(t => t.trangThai === 'DANG_CHO').length)
const thanhCong = computed(() => danhSach.value.filter(t => t.trangThai === 'THANH_CONG').length)

// Tổng thu — chọn được từng ngày thay vì cố định "hôm nay"
const ngayThongKe = ref(toIso(new Date()))
const isThongKeToday = computed(() => ngayThongKe.value === toIso(new Date()))
function shiftNgayThongKe(delta) {
  const d = new Date(ngayThongKe.value)
  d.setDate(d.getDate() + delta)
  ngayThongKe.value = toIso(d)
}
const tongTheoNgay = computed(() => {
  return danhSach.value
    .filter(t => t.trangThai === 'THANH_CONG' && (t.ngayThanhToan || '').slice(0, 10) === ngayThongKe.value)
    .reduce((s, t) => s + (parseFloat(t.soTien) || 0), 0)
})

/* ================= HÀNH ĐỘNG ================= */
async function xacNhanCoc(tt) {
  if (!confirm(`Xác nhận đã nhận tiền cọc ${formatMoney(tt.soTien)} từ ${tt.tenKhach}?`)) return
  try {
    await staffService.xacNhanCoc(tt.id)
    tt.trangThai = 'THANH_CONG'
    showToast('Xác nhận cọc thành công!')
  } catch (e) { showToast(e.message, 'error') }
}

async function openThuNot(tt) {
  thuNotModal.value = { show: true, tt, phuongThuc: 'TIEN_MAT', loading: false }
  await taiGioHangChoDon(tt)
}

async function taiGioHangChoDon(tt) {
  gioHangThuNot.value = null
  if (!tt.datSanId) return
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
    // im lặng bỏ qua, modal vẫn hiện được số tiền sân cơ bản nếu lỗi
  } finally {
    dangTaiGioHang.value = false
  }
}

function tinhConLai() {
  if (!thuNotModal.value.tt) return 0
  if (gioHangThuNot.value) {
    return gioHangThuNot.value.tongCanThanhToan
  }
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
function formatDate(s) { if (!s) return ''; const [y,m,d] = s.slice(0,10).split('-'); return `${d}/${m}/${y}` }
function labelTT(t) { return { DANG_CHO: 'Chờ XN', THANH_CONG: 'Thành công', THAT_BAI: 'Thất bại' }[t] || t }
function badgeTT(t) { return { DANG_CHO: 'badge-warning', THANH_CONG: 'badge-success', THAT_BAI: 'badge-danger' }[t] || '' }
</script>

<style scoped>
/* ============================================================
   TOKENS — đồng bộ với trang chủ & Quản lý đặt sân (night-pitch)
   ============================================================ */
.qltt {
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
  border-radius: 999px; font-weight: 600; font-size: 13.5px; cursor: pointer; transition: all .15s ease; white-space: nowrap;
}
.btn-ghost:hover { border-color: var(--lime-400); color: var(--lime-300); background: rgba(182,255,60,.08); }
.btn-ghost--sm { padding: 8px 15px; font-size: 12.5px; flex-shrink: 0; }

/* ============================================================
   TOAST
   ============================================================ */
.toast {
  position: fixed; top: 22px; right: 24px; z-index: 999; display: flex; align-items: center; gap: 8px;
  padding: 13px 20px; border-radius: 12px; font-weight: 600; font-size: 13.5px;
  background: var(--night-800); border: 1px solid rgba(182,255,60,.3); color: var(--chalk-050);
  box-shadow: 0 14px 30px -10px rgba(0,0,0,.6);
}
.toast.error { border-color: rgba(255,71,87,.4); }
.toast__dot { width: 7px; height: 7px; border-radius: 50%; background: var(--lime-400); flex-shrink: 0; }
.toast.error .toast__dot { background: var(--crimson-500); }
.toast-fade-enter-active, .toast-fade-leave-active { transition: opacity .25s ease, transform .25s ease; }
.toast-fade-enter-from, .toast-fade-leave-to { opacity: 0; transform: translateY(-10px); }

/* ============================================================
   SUMMARY CARDS
   ============================================================ */
.summary-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 14px; margin-bottom: 22px; }
.summary-card {
  position: relative; display: flex; align-items: center; gap: 14px;
  background: rgba(247,251,244,.03); border: 1px solid rgba(247,251,244,.1); border-radius: 16px;
  padding: 16px 18px; text-align: left; font-family: var(--font-body); color: inherit;
}
button.summary-card { cursor: pointer; transition: border-color .15s, transform .15s, background .15s; }
button.summary-card:hover { border-color: rgba(182,255,60,.35); transform: translateY(-2px); }
.summary-card--active { border-color: var(--lime-400); background: rgba(182,255,60,.07); box-shadow: 0 0 0 1px rgba(182,255,60,.25) inset; }
.summary-card--stat { flex-wrap: wrap; }

.card-icon { font-size: 24px; width: 46px; height: 46px; border-radius: 12px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.icon-total { background: rgba(255,176,32,.15); }
.icon-pending { background: rgba(255,176,32,.15); }
.icon-done { background: rgba(182,255,60,.12); }
.card-body { display: flex; flex-direction: column; gap: 2px; }
.card-label { font-family: var(--font-mono); font-size: 10.5px; text-transform: uppercase; letter-spacing: .05em; color: var(--lime-300); }
.card-value { font-family: var(--font-display); font-size: 19px; font-weight: 600; color: var(--chalk-050); }
.card-hint { position: absolute; bottom: 8px; right: 14px; font-size: 10.5px; color: var(--chalk-200); opacity: .5; }

.card-day-nav { display: flex; align-items: center; gap: 6px; margin-left: auto; position: relative; }
.day-nav--sm { width: 22px; height: 22px; }
.card-day-label { font-family: var(--font-mono); font-size: 11.5px; color: var(--chalk-200); opacity: .8; min-width: 52px; text-align: center; }
.card-day-label--today { color: var(--lime-300); opacity: 1; font-weight: 700; }
.card-day-input { position: absolute; inset: 0; opacity: 0; cursor: pointer; width: 100%; height: 100%; }

/* ============================================================
   FILTER CARD
   ============================================================ */
.filter-card {
  background: rgba(247,251,244,.03); border: 1px solid rgba(247,251,244,.1); border-radius: 18px;
  padding: 18px 20px; margin-bottom: 22px; backdrop-filter: blur(6px);
}
.filter-top { display: flex; flex-wrap: wrap; gap: 12px; align-items: flex-end; margin-bottom: 16px; }

.search-wrap {
  flex: 1.6; min-width: 220px; display: flex; align-items: center; gap: 8px;
  background: rgba(5,11,8,.4); border: 1px solid rgba(247,251,244,.15); border-radius: 10px; padding: 0 12px;
}
.search-wrap svg { color: var(--chalk-200); opacity: .6; flex-shrink: 0; }
.search-input { flex: 1; border: none; background: transparent; outline: none; padding: 10px 0; font-size: 13.5px; color: var(--chalk-050); font-family: var(--font-body); }
.search-input::placeholder { color: var(--chalk-200); opacity: .45; }

.pill-select { display: flex; flex-direction: column; gap: 5px; min-width: 170px; }
.pill-select label { font-family: var(--font-mono); font-size: 10.5px; text-transform: uppercase; letter-spacing: .06em; color: var(--lime-300); padding-left: 2px; }

.form-control {
  padding: 10px 13px; border: 1px solid rgba(247,251,244,.15); border-radius: 10px; font-size: 13.5px;
  outline: none; background: rgba(5,11,8,.4); color: var(--chalk-050); font-family: var(--font-body);
  transition: border-color .15s;
}
.form-control:focus { border-color: var(--lime-400); box-shadow: 0 0 0 3px rgba(182,255,60,.12); }
select.form-control { cursor: pointer; }
select.form-control option { background: var(--night-800); color: var(--chalk-050); }

/* ---- Dải chọn ngày ---- */
.day-strip, .gio-strip { display: flex; align-items: center; gap: 10px; padding-top: 14px; border-top: 1px solid rgba(247,251,244,.08); flex-wrap: wrap; }
.day-strip__label { font-family: var(--font-mono); font-size: 10.5px; text-transform: uppercase; letter-spacing: .06em; color: var(--lime-300); flex-shrink: 0; margin-right: 2px; }

.day-nav {
  width: 30px; height: 30px; border-radius: 50%; border: 1px solid rgba(247,251,244,.18);
  background: rgba(247,251,244,.04); color: var(--chalk-200); display: flex; align-items: center; justify-content: center;
  cursor: pointer; flex-shrink: 0; transition: all .15s;
}
.day-nav:hover { border-color: var(--lime-400); color: var(--lime-300); }

.day-chip-track { display: flex; gap: 8px; overflow-x: auto; padding: 2px; scrollbar-width: thin; }
.day-chip-track::-webkit-scrollbar { height: 5px; }
.day-chip-track::-webkit-scrollbar-thumb { background: rgba(182,255,60,.25); border-radius: 10px; }

.day-chip {
  flex-shrink: 0; display: flex; flex-direction: column; align-items: center; justify-content: center;
  gap: 1px; width: 58px; padding: 7px 4px; border-radius: 12px; border: 1px solid rgba(247,251,244,.14);
  background: rgba(247,251,244,.03); color: var(--chalk-200); cursor: pointer; transition: all .15s; position: relative;
}
.day-chip:hover { border-color: rgba(182,255,60,.4); background: rgba(182,255,60,.06); }
.day-chip--today { border-color: rgba(182,255,60,.35); }
.day-chip--active {
  background: linear-gradient(135deg, var(--turf-500), var(--turf-700)); border-color: var(--turf-500); color: var(--chalk-050);
  box-shadow: 0 8px 18px -8px rgba(182,255,60,.4);
}
.day-chip__dow { font-family: var(--font-mono); font-size: 10px; text-transform: uppercase; opacity: .75; }
.day-chip__num { font-family: var(--font-display); font-size: 18px; font-weight: 600; line-height: 1.1; }
.day-chip__mon { font-size: 9.5px; opacity: .6; }
.day-chip__tag {
  position: absolute; top: -8px; left: 50%; transform: translateX(-50%); background: var(--lime-400); color: var(--night-950);
  font-size: 8.5px; font-weight: 800; padding: 1px 6px; border-radius: 999px; white-space: nowrap;
}
.day-chip--all { width: auto; padding: 7px 14px; flex-direction: row; font-size: 12.5px; font-weight: 600; }

.day-jump { margin-left: auto; flex-shrink: 0; }
.day-jump__input {
  padding: 8px 12px; border-radius: 10px; border: 1px dashed rgba(247,251,244,.25); background: rgba(5,11,8,.4);
  color: var(--chalk-200); font-size: 12.5px; font-family: var(--font-mono); outline: none; cursor: pointer;
}
.day-jump__input:focus { border-color: var(--lime-400); border-style: solid; }

/* ---- Dải chọn khung giờ ---- */
.gio-select { min-width: 108px; }
.gio-dash { color: var(--chalk-200); opacity: .5; }
.gio-quick { display: flex; gap: 6px; margin-left: 6px; flex-wrap: wrap; }
.gio-chip {
  padding: 8px 14px; border-radius: 999px; border: 1px solid rgba(247,251,244,.16);
  background: rgba(247,251,244,.03); color: var(--chalk-200); font-size: 12.5px; font-weight: 600; cursor: pointer; transition: all .15s;
}
.gio-chip:hover { border-color: rgba(182,255,60,.4); }
.gio-chip--active { background: rgba(182,255,60,.14); border-color: var(--lime-400); color: var(--lime-300); }

/* ============================================================
   STATE BOX
   ============================================================ */
.state-box { text-align: center; padding: 60px 20px; color: var(--chalk-200); opacity: .75; font-size: 14.5px; display: flex; align-items: center; justify-content: center; gap: 10px; }
.state-box.error { color: #ff9686; opacity: 1; }
.spinner { width: 16px; height: 16px; border-radius: 50%; border: 2px solid rgba(182,255,60,.25); border-top-color: var(--lime-400); animation: spin .7s linear infinite; flex-shrink: 0; }
@keyframes spin { to { transform: rotate(360deg); } }

/* ============================================================
   TABLE
   ============================================================ */
.table-card { background: rgba(247,251,244,.03); border: 1px solid rgba(247,251,244,.1); border-radius: 18px; overflow: hidden; }
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
.ma-don { color: var(--lime-300); font-size: 12.5px; font-family: var(--font-mono); }
.price { font-weight: 700; color: var(--chalk-050); }
.phuong-thuc { font-size: 12.5px; color: var(--chalk-200); }

.badge-loai { display: inline-flex; padding: 4px 10px; border-radius: 6px; font-size: 11.5px; font-weight: 700; }
.badge-coc { background: rgba(182,255,60,.12); color: var(--lime-300); }
.badge-con-lai { background: rgba(255,176,32,.15); color: var(--amber-400); }

.badge { display: inline-flex; padding: 4px 11px; border-radius: 20px; font-size: 11.5px; font-weight: 700; }
.badge-warning { background: rgba(255,176,32,.15); color: var(--amber-400); }
.badge-success { background: rgba(182,255,60,.15); color: var(--lime-300); }
.badge-danger { background: rgba(255,71,87,.15); color: #ff9686; }

.actions { display: flex; gap: 6px; justify-content: center; flex-wrap: wrap; }
.btn-act { padding: 6px 11px; border-radius: 7px; border: none; font-size: 11.5px; font-weight: 700; cursor: pointer; transition: .15s; }
.btn-act.confirm { background: rgba(35,147,90,.18); color: #6fe0a6; }
.btn-act.confirm:hover { background: rgba(35,147,90,.3); }
.btn-act.thu { background: rgba(255,176,32,.15); color: var(--amber-400); }
.btn-act.thu:hover { background: rgba(255,176,32,.26); }
.no-action { color: rgba(247,251,244,.4); font-size: 12px; }

.no-data { text-align: center; padding: 50px 20px; color: var(--chalk-200); opacity: .6; }
.no-data__icon { font-size: 26px; margin-bottom: 8px; }

/* ============================================================
   MODAL
   ============================================================ */
.overlay { position: fixed; inset: 0; background: rgba(5,11,8,.75); backdrop-filter: blur(3px); display: flex; align-items: center; justify-content: center; z-index: 200; padding: 20px; }
.modal-box { background: var(--night-800); border: 1px solid rgba(182,255,60,.2); border-radius: 18px; width: 100%; max-width: 480px; box-shadow: 0 24px 50px rgba(0,0,0,.5); overflow: hidden; max-height: 88vh; overflow-y: auto; }
.modal-header { padding: 18px 22px; border-bottom: 1px solid rgba(247,251,244,.1); display: flex; justify-content: space-between; align-items: center; }
.modal-header h2 { font-family: var(--font-display); font-size: 17px; font-weight: 600; color: var(--chalk-050); margin: 0; }
.close-btn { background: none; border: none; font-size: 22px; color: var(--chalk-200); opacity: .6; cursor: pointer; }
.close-btn:hover { opacity: 1; color: var(--lime-300); }
.modal-body { padding: 20px 22px; }
.modal-footer { padding: 14px 22px; border-top: 1px solid rgba(247,251,244,.1); display: flex; justify-content: flex-end; gap: 10px; }

.info-block { background: rgba(247,251,244,.04); border: 1px solid rgba(247,251,244,.08); border-radius: 10px; padding: 14px; }
.info-row { display: flex; gap: 12px; align-items: center; justify-content: space-between; padding: 7px 0; border-bottom: 1px solid rgba(247,251,244,.06); }
.info-row:last-child { border-bottom: none; }
.info-row.highlight { background: rgba(182,255,60,.08); border-radius: 8px; padding: 10px; margin-top: 4px; border-bottom: none; }
.info-label { font-weight: 600; color: var(--chalk-200); opacity: .8; min-width: 100px; font-size: 12.5px; }
.price-big { font-size: 19px; font-weight: 800; color: var(--lime-300); }

.form-group { display: flex; flex-direction: column; gap: 6px; }
.form-label { font-size: 12.5px; font-weight: 600; color: var(--lime-300); font-family: var(--font-mono); text-transform: uppercase; letter-spacing: .04em; }
.toggle-row { display: flex; gap: 8px; }
.toggle-btn {
  flex: 1; padding: 10px; border: 1px solid rgba(247,251,244,.16); border-radius: 10px;
  background: rgba(247,251,244,.03); color: var(--chalk-200); cursor: pointer; font-size: 13px; font-weight: 600; transition: .15s;
}
.toggle-btn.active-green { background: rgba(182,255,60,.14); border-color: var(--lime-400); color: var(--lime-300); }

.dich-vu-loading { text-align: center; padding: 16px 0; color: var(--chalk-200); opacity: .6; font-size: 13px; margin-top: 14px; display: flex; align-items: center; justify-content: center; gap: 8px; }
.dich-vu-block { margin-top: 14px; background: rgba(247,251,244,.04); border: 1px solid rgba(247,251,244,.08); border-radius: 10px; padding: 14px; }
.dich-vu-title { font-size: 13px; font-weight: 700; color: var(--chalk-050); margin: 0 0 8px; }
.dich-vu-trong { font-size: 13px; color: var(--chalk-200); opacity: .6; padding: 4px 0; }
.dich-vu-list { display: flex; flex-direction: column; gap: 6px; margin-bottom: 10px; }
.dich-vu-row { display: flex; justify-content: space-between; font-size: 12.5px; color: var(--chalk-200); padding: 4px 0; border-bottom: 1px dashed rgba(247,251,244,.12); }
.tong-tien-block { border-top: 1px solid rgba(247,251,244,.1); padding-top: 10px; display: flex; flex-direction: column; gap: 4px; }

/* ============================================================
   RESPONSIVE
   ============================================================ */
@media (max-width: 900px) { .summary-grid { grid-template-columns: 1fr; } }
@media (max-width: 720px) {
  .filter-top { flex-direction: column; align-items: stretch; }
  .pill-select { min-width: 0; }
  .day-jump { margin-left: 0; }
  .data-table { font-size: 12.5px; }
}
</style>