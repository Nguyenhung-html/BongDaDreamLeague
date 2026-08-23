<template>
  <div class="qldsan">

    <!-- ===== HEADER ===== -->
    <div class="page-header">
      <div>
        <span class="eyebrow">⚽ Bảng điều khiển · Staff</span>
        <h1 class="page-title">Quản lý đặt sân</h1>
        <p class="page-desc">Xem, xác nhận và xử lý tất cả đơn đặt sân tại DreamLeague.</p>
      </div>
      <button class="btn-primary" @click="openWalkInModal">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 5v14M5 12h14"/></svg>
        Đặt sân tại quầy
      </button>
    </div>

    <!-- ===== TOAST ===== -->
    <Transition name="toast-fade">
      <div v-if="toast.show" :class="['toast', toast.type]">
        <span class="toast__dot"></span>{{ toast.msg }}
      </div>
    </Transition>

    <!-- ===== BỘ LỌC ===== -->
    <div class="filter-card">
      <div class="filter-top">
        <div class="search-wrap">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="7"/><path d="M21 21l-4.3-4.3"/></svg>
          <input type="text" v-model="filters.search" placeholder="Tìm tên khách, số điện thoại..." class="search-input"/>
        </div>

        <div class="pill-select">
          <label>Loại sân</label>
          <select v-model="filters.loaiSan" class="form-control">
            <option value="">Tất cả</option>
            <option value="5">Sân 5 người</option>
            <option value="7">Sân 7 người</option>
          </select>
        </div>

        <div class="pill-select">
          <label>Trạng thái</label>
          <select v-model="filters.trangThai" class="form-control">
            <option value="">Tất cả trạng thái</option>
            <option value="CHO_XAC_NHAN">Chờ xác nhận</option>
            <option value="DA_COC">Đã cọc</option>
            <option value="HOAN_THANH">Hoàn thành</option>
            <option value="DA_HUY">Đã hủy</option>
          </select>
        </div>

        <button class="btn-ghost btn-ghost--sm" @click="fetchDanhSach">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 12a9 9 0 1 1-2.64-6.36"/><path d="M21 4v6h-6"/></svg>
          Làm mới
        </button>
      </div>

      <!-- Bộ lọc theo ngày: dạng dải ngày rõ ràng thay vì ô chọn ngày ẩn -->
      <div class="day-strip">
        <span class="day-strip__label">Chọn ngày</span>

        <button class="day-nav" @click="dayOffset -= 7" aria-label="Tuần trước">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4"><path d="M15 18l-6-6 6-6"/></svg>
        </button>

        <button
          class="day-chip day-chip--all"
          :class="{ 'day-chip--active': !filters.ngay }"
          @click="filters.ngay = ''"
        >
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
    </div>

    <!-- ===== LOADING / ERROR ===== -->
    <div v-if="loading" class="state-box">
      <span class="spinner"></span> Đang tải dữ liệu...
    </div>
    <div v-else-if="error" class="state-box error">⚠ {{ error }}</div>

    <!-- ===== BẢNG DỮ LIỆU ===== -->
    <div v-else class="table-card">
      <table class="data-table">
        <thead>
          <tr>
            <th>Mã đơn</th>
            <th>Khách hàng</th>
            <th>Thông tin sân</th>
            <th>Thời gian</th>
            <th>Tổng tiền / Cọc</th>
            <th>Trạng thái</th>
            <th style="text-align:center">Hành động</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="don in filteredList" :key="don.id">
            <td><strong class="ma-don">#{{ don.maGanDo }}</strong></td>
            <td>
              <div class="cell-stack">
                <span class="bold">{{ don.hoTenDat }}</span>
                <span class="sub">{{ don.soDienThoai }}</span>
              </div>
            </td>
            <td>
              <div class="cell-stack">
                <span class="bold">{{ don.tenSan }}</span>
                <span class="badge-san">Sân {{ don.loaiSan }} người</span>
              </div>
            </td>
            <td>
              <div class="cell-stack">
                <span class="bold">{{ don.gioBatDau }} – {{ don.gioKetThuc }}</span>
                <span class="sub">{{ formatDate(don.ngayDa) }}</span>
              </div>
            </td>
            <td>
              <div class="cell-stack">
                <span class="price">{{ formatMoney(don.tongTien) }}</span>
                <span class="sub">Cọc: {{ formatMoney(don.tienCoc) }}</span>
              </div>
            </td>
            <td>
              <span :class="['badge', badgeClass(don.trangThai)]">{{ labelTrangThai(don.trangThai) }}</span>
            </td>
            <td>
              <div class="actions">
                <button v-if="don.trangThai === 'CHO_XAC_NHAN'" class="btn-act confirm" title="Xác nhận cọc" @click="xacNhan(don)">✓ Xác nhận</button>
                <button
                  v-if="don.trangThai !== 'DA_HUY'"
                  :class="['btn-act', 'dichvu', { 'dichvu--noi-bat': coDichVu(don.id) }]"
                  :title="coDichVu(don.id) ? 'Khách có gọi dịch vụ!' : 'Xem dịch vụ đã gọi'"
                  @click="xemDichVu(don)"
                >
                  🛒 Dịch vụ<span v-if="coDichVu(don.id)" class="dichvu-dot"></span>
                </button>
                <button
                  v-if="don.trangThai !== 'DA_HUY' && don.trangThai !== 'HOAN_THANH'"
                  class="btn-act giahan"
                  title="Gia hạn thêm 30 phút"
                  @click="giaHanGio(don)"
                  :disabled="dangGiaHan === don.id"
                >
                  {{ dangGiaHan === don.id ? '⏳...' : '⏱️ +30p' }}
                </button>
                <button v-if="don.trangThai !== 'DA_HUY' && don.trangThai !== 'HOAN_THANH'" class="btn-act cancel" title="Hủy đơn" @click="openHuyModal(don)">✕ Hủy</button>
                <span v-if="don.trangThai === 'DA_HUY'" class="no-action">—</span>
              </div>
            </td>
          </tr>
          <tr v-if="filteredList.length === 0">
            <td colspan="7" class="no-data">
              <div class="no-data__icon">🔍</div>
              Không tìm thấy đơn nào khớp với bộ lọc hiện tại.
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- ===== MODAL HỦY ĐƠN ===== -->
    <div class="overlay" v-if="huyModal.show" @click.self="huyModal.show = false">
      <div class="modal-box">
        <div class="modal-header">
          <h2>Hủy đơn đặt sân</h2>
          <button class="close-btn" @click="huyModal.show = false">&times;</button>
        </div>
        <div class="modal-body">
          <p class="modal-sub">Hủy đơn <strong>#{{ huyModal.don?.maGanDo }}</strong> – {{ huyModal.don?.hoTenDat }}</p>
          <label class="form-label">Lý do hủy *</label>
          <textarea v-model="huyModal.lyDo" class="form-control" rows="3" placeholder="Nhập lý do hủy..."></textarea>
        </div>
        <div class="modal-footer">
          <button class="btn-ghost" @click="huyModal.show = false">Đóng</button>
          <button class="btn-danger" @click="xacNhanHuy">Xác nhận hủy</button>
        </div>
      </div>
    </div>

    <!-- ===== MODAL XEM DỊCH VỤ ĐÃ GỌI ===== -->
    <div class="overlay" v-if="dichVuModal.show" @click.self="dichVuModal.show = false">
      <div class="modal-box">
        <div class="modal-header">
          <h2>🛒 Dịch vụ đã gọi</h2>
          <button class="close-btn" @click="dichVuModal.show = false">&times;</button>
        </div>
        <div class="modal-body">
          <p class="modal-sub">
            #{{ dichVuModal.don?.maGanDo }} — {{ dichVuModal.don?.hoTenDat }} —
            {{ dichVuModal.don?.tenSan }} ({{ dichVuModal.don?.gioBatDau }}-{{ dichVuModal.don?.gioKetThuc }})
          </p>

          <div v-if="dichVuModal.dangTai" class="state-box" style="padding:20px 0"><span class="spinner"></span> Đang tải...</div>

          <template v-else-if="dichVuModal.gioHang">
            <div v-if="dichVuModal.gioHang.danhSach.length === 0" class="dich-vu-trong">
              Khách chưa gọi dịch vụ nào cho đơn này.
            </div>
            <div v-else class="dich-vu-list">
              <div v-for="ct in dichVuModal.gioHang.danhSach" :key="ct.id" class="dich-vu-row">
                <span>{{ ct.tenSanPham }} × {{ ct.soLuong }}</span>
                <strong>{{ formatMoney(ct.thanhTien) }}</strong>
              </div>
            </div>

            <div class="tong-tien-block">
              <div class="info-row">
                <span class="info-label">Còn lại tiền sân:</span>
                <span>{{ formatMoney(dichVuModal.gioHang.tienConLaiSan) }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">Tiền dịch vụ:</span>
                <span>{{ formatMoney(dichVuModal.gioHang.tongTienDichVu) }}</span>
              </div>
              <div class="info-row highlight">
                <span class="info-label">Tổng cần thu tại sân:</span>
                <span class="price-big">{{ formatMoney(dichVuModal.gioHang.tongCanThanhToan) }}</span>
              </div>
            </div>
          </template>

          <div v-else class="dich-vu-trong">Không tải được dữ liệu dịch vụ, vui lòng thử lại.</div>
        </div>
        <div class="modal-footer">
          <button class="btn-ghost" @click="dichVuModal.show = false">Đóng</button>
        </div>
      </div>
    </div>

    <!-- ===== MODAL WALK-IN ===== -->
    <div class="overlay" v-if="walkInModal.show" @click.self="walkInModal.show = false">
      <div class="modal-box wide">
        <div class="modal-header">
          <h2>Đặt sân tại quầy (Walk-in)</h2>
          <button class="close-btn" @click="walkInModal.show = false">&times;</button>
        </div>
        <div class="modal-body">
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">Tên khách hàng *</label>
              <input v-model="walkIn.hoTenDat" class="form-control" placeholder="Nguyễn Văn A"/>
            </div>
            <div class="form-group">
              <label class="form-label">Số điện thoại *</label>
              <input v-model="walkIn.soDienThoai" class="form-control" placeholder="0901234567"/>
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">Chọn sân *</label>
              <select v-model="walkIn.sanBongId" class="form-control">
                <option value="">-- Chọn sân --</option>
                <option v-for="s in danhSachSan" :key="s.id" :value="s.id">{{ s.tenSan }} ({{ s.loaiSan }} người)</option>
              </select>
            </div>
            <div class="form-group">
              <label class="form-label">Ngày đá *</label>
              <input type="date" v-model="walkIn.ngayDa" class="form-control"/>
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">Giờ bắt đầu *</label>
              <select v-model="walkIn.gioBatDau" class="form-control">
                <option v-for="h in gioOptions" :key="h" :value="h">{{ h }}</option>
              </select>
            </div>
            <div class="form-group">
              <label class="form-label">Giờ kết thúc *</label>
              <select v-model="walkIn.gioKetThuc" class="form-control">
                <option v-for="h in gioOptions" :key="h" :value="h">{{ h }}</option>
              </select>
            </div>
          </div>
          <div class="form-group">
            <label class="form-label">Phương thức thanh toán cọc</label>
            <select v-model="walkIn.phuongThuc" class="form-control">
              <option value="TIEN_MAT">Tiền mặt</option>
              <option value="QR">QR / Chuyển khoản</option>
            </select>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-ghost" @click="walkInModal.show = false">Đóng</button>
          <button class="btn-primary" @click="submitWalkIn" :disabled="walkInModal.loading">
            {{ walkInModal.loading ? 'Đang xử lý...' : 'Xác nhận đặt sân' }}
          </button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import staffService from '@/services/staffService'
import sanService from '@/services/sanService'

const API = '/api'

const danhSach = ref([])
const danhSachSan = ref([])
const loading = ref(false)
const error = ref('')
const toast = ref({ show: false, msg: '', type: 'success' })

const filters = ref({ search: '', loaiSan: '', ngay: '', trangThai: '' })

const huyModal = ref({ show: false, don: null, lyDo: '' })
const walkInModal = ref({ show: false, loading: false })
const walkIn = ref({ hoTenDat: '', soDienThoai: '', sanBongId: '', ngayDa: '', gioBatDau: '06:00', gioKetThuc: '07:00', phuongThuc: 'TIEN_MAT' })

const dichVuModal = ref({ show: false, don: null, dangTai: false, gioHang: null })
const dsCoDichVu = ref(new Set())
const dangGiaHan = ref(null)

const gioOptions = ['06:00','07:00','08:00','09:00','10:00','11:00','12:00','13:00','14:00','15:00','16:00','17:00','18:00','19:00','20:00','21:00','22:00']

/* ================= DẢI CHỌN NGÀY (thay thế ô date ẩn) ================= */
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

/* ================= TẢI DANH SÁCH ================= */
onMounted(async () => {
  await fetchDanhSach()
  try {
    const res = await fetch('/api/san-bong', { headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` } })
    danhSachSan.value = await res.json()
  } catch {}
})

// Khi đổi ngày hoặc trạng thái, tự động tải lại dữ liệu từ server (không cần bấm Làm mới)
watch(() => [filters.value.ngay, filters.value.trangThai], () => {
  fetchDanhSach()
})

async function fetchDanhSach() {
  loading.value = true; error.value = ''
  try {
    const params = {}
    if (filters.value.ngay) params.ngay = filters.value.ngay
    if (filters.value.trangThai) params.trangThai = filters.value.trangThai
    danhSach.value = await staffService.layDanhSachDatSan(params)
    kiemTraDichVuChoDanhSach() // chạy ngầm, không chặn hiện bảng
  } catch (e) {
    error.value = e.message
  } finally {
    loading.value = false
  }
}

async function kiemTraDichVuChoDanhSach() {
  const token = localStorage.getItem('token')
  const dsCanKiemTra = danhSach.value.filter(d => d.trangThai !== 'DA_HUY')

  const ketQua = await Promise.all(
    dsCanKiemTra.map(async (don) => {
      try {
        const res = await fetch(`${API}/dat-san/${don.id}/dich-vu`, {
          headers: { 'Authorization': `Bearer ${token}` }
        })
        if (!res.ok) return null
        const data = await res.json()
        return data.danhSach && data.danhSach.length > 0 ? don.id : null
      } catch {
        return null
      }
    })
  )

  dsCoDichVu.value = new Set(ketQua.filter(id => id !== null))
}

function coDichVu(donId) {
  return dsCoDichVu.value.has(donId)
}

// Lọc lại ở phía client để đảm bảo bộ lọc trạng thái/ngày luôn đúng
// dù backend có hỗ trợ lọc theo params hay không.
const filteredList = computed(() => {
  return danhSach.value.filter(d => {
    const s = filters.value.search.toLowerCase()
    const matchSearch = !s || d.hoTenDat?.toLowerCase().includes(s) || d.soDienThoai?.includes(s)
    const matchLoai = !filters.value.loaiSan || String(d.loaiSan) === filters.value.loaiSan
    const matchNgay = !filters.value.ngay || d.ngayDa === filters.value.ngay
    const matchTrangThai = !filters.value.trangThai || d.trangThai === filters.value.trangThai
    return matchSearch && matchLoai && matchNgay && matchTrangThai
  })
})

async function xacNhan(don) {
  if (!confirm(`Xác nhận đơn #${don.maGanDo} của ${don.hoTenDat}?`)) return
  try {
    await staffService.xacNhanDon(don.id)
    don.trangThai = 'DA_COC'
    showToast('Xác nhận thành công! Đơn đã chuyển sang Đã cọc.')
  } catch (e) { showToast(e.message, 'error') }
}

function openHuyModal(don) {
  huyModal.value = { show: true, don, lyDo: '' }
}

async function xacNhanHuy() {
  if (!huyModal.value.lyDo.trim()) { showToast('Vui lòng nhập lý do hủy!', 'error'); return }
  try {
    await staffService.staffHuyDon(huyModal.value.don.id, huyModal.value.lyDo)
    huyModal.value.don.trangThai = 'DA_HUY'
    huyModal.value.show = false
    showToast('Đã hủy đơn thành công!')
  } catch (e) { showToast(e.message, 'error') }
}

async function xemDichVu(don) {
  dichVuModal.value = { show: true, don, dangTai: true, gioHang: null }
  try {
    const token = localStorage.getItem('token')
    const res = await fetch(`${API}/dat-san/${don.id}/dich-vu`, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    if (res.ok) {
      dichVuModal.value.gioHang = await res.json()
    }
  } catch (e) {
    // giữ nguyên gioHang = null, template sẽ tự hiện thông báo lỗi tải
  } finally {
    dichVuModal.value.dangTai = false
  }
}

async function giaHanGio(don) {
  if (!confirm(`Gia hạn thêm 30 phút cho đơn #${don.maGanDo} (${don.hoTenDat})? Số tiền phát sinh sẽ cộng vào phần thanh toán tại sân.`)) return

  dangGiaHan.value = don.id
  try {
    const token = localStorage.getItem('token')
    const res = await fetch(`${API}/dat-san/${don.id}/gia-han`, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${token}` }
    })
    const data = await res.json()
    if (!res.ok) throw new Error(data.message || 'Gia hạn thất bại!')

    don.gioKetThuc = data.gioKetThucMoi
    don.tongTien = data.tongTienMoi
    showToast(data.thongBao)
  } catch (e) {
    showToast(e.message, 'error')
  } finally {
    dangGiaHan.value = null
  }
}

function openWalkInModal() {
  walkIn.value = { hoTenDat: '', soDienThoai: '', sanBongId: '', ngayDa: new Date().toISOString().slice(0,10), gioBatDau: '06:00', gioKetThuc: '07:00', phuongThuc: 'TIEN_MAT' }
  walkInModal.value = { show: true, loading: false }
}

async function submitWalkIn() {
  const { hoTenDat, soDienThoai, sanBongId, ngayDa, gioBatDau, gioKetThuc } = walkIn.value
  if (!hoTenDat || !soDienThoai || !sanBongId || !ngayDa || !gioBatDau || !gioKetThuc) {
    showToast('Vui lòng điền đầy đủ thông tin!', 'error'); return
  }
  walkInModal.value.loading = true
  try {
    await staffService.datSanWalkIn(walkIn.value)
    walkInModal.value.show = false
    showToast('Đặt sân walk-in thành công!')
    await fetchDanhSach()
  } catch (e) { showToast(e.message, 'error') }
  finally { walkInModal.value.loading = false }
}

function showToast(msg, type = 'success') {
  toast.value = { show: true, msg, type }
  setTimeout(() => { toast.value.show = false }, 3500)
}

function formatMoney(v) { return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(v || 0) }
function formatDate(s) { if (!s) return ''; const [y,m,d] = s.split('-'); return `${d}/${m}/${y}` }
function labelTrangThai(t) {
  return { CHO_XAC_NHAN: 'Chờ xác nhận', DA_COC: 'Đã cọc', HOAN_THANH: 'Hoàn thành', DA_HUY: 'Đã hủy' }[t] || t
}
function badgeClass(t) {
  return { CHO_XAC_NHAN: 'badge-warning', DA_COC: 'badge-info', HOAN_THANH: 'badge-success', DA_HUY: 'badge-danger' }[t] || ''
}
</script>

<style scoped>
/* ============================================================
   TOKENS — đồng bộ với trang chủ (night-pitch theme)
   ============================================================ */
.qldsan {
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
  border-radius: 999px; font-weight: 600; font-size: 13.5px; cursor: pointer; transition: all .15s ease;
}
.btn-ghost:hover { border-color: var(--lime-400); color: var(--lime-300); background: rgba(182,255,60,.08); }
.btn-ghost--sm { padding: 9px 16px; font-size: 13px; flex-shrink: 0; }

.btn-danger {
  padding: 10px 20px; background: var(--crimson-500); color: #fff; border: none; border-radius: 999px;
  font-weight: 700; font-size: 13.5px; cursor: pointer; transition: background .15s;
}
.btn-danger:hover { background: #ff2e40; }

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

.pill-select { display: flex; flex-direction: column; gap: 5px; min-width: 160px; }
.pill-select label { font-family: var(--font-mono); font-size: 10.5px; text-transform: uppercase; letter-spacing: .06em; color: var(--lime-300); padding-left: 2px; }

.form-control {
  padding: 10px 13px; border: 1px solid rgba(247,251,244,.15); border-radius: 10px; font-size: 13.5px;
  outline: none; background: rgba(5,11,8,.4); color: var(--chalk-050); font-family: var(--font-body);
  transition: border-color .15s;
}
.form-control:focus { border-color: var(--lime-400); box-shadow: 0 0 0 3px rgba(182,255,60,.12); }
select.form-control { cursor: pointer; }
select.form-control option { background: var(--night-800); color: var(--chalk-050); }

/* ---- Dải chọn ngày: rõ ràng theo từng ngày ---- */
.day-strip {
  display: flex; align-items: center; gap: 10px; padding-top: 14px; border-top: 1px solid rgba(247,251,244,.08);
  flex-wrap: wrap;
}
.day-strip__label {
  font-family: var(--font-mono); font-size: 10.5px; text-transform: uppercase; letter-spacing: .06em;
  color: var(--lime-300); flex-shrink: 0; margin-right: 2px;
}
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
.day-chip--all {
  width: auto; padding: 7px 14px; flex-direction: row; font-size: 12.5px; font-weight: 600;
}

.day-jump { margin-left: auto; flex-shrink: 0; }
.day-jump__input {
  padding: 8px 12px; border-radius: 10px; border: 1px dashed rgba(247,251,244,.25); background: rgba(5,11,8,.4);
  color: var(--chalk-200); font-size: 12.5px; font-family: var(--font-mono); outline: none; cursor: pointer;
}
.day-jump__input:focus { border-color: var(--lime-400); border-style: solid; }

/* ============================================================
   STATE BOX
   ============================================================ */
.state-box { text-align: center; padding: 60px 20px; color: var(--chalk-200); opacity: .75; font-size: 14.5px; display: flex; align-items: center; justify-content: center; gap: 10px; }
.state-box.error { color: #ff9686; opacity: 1; }
.spinner { width: 16px; height: 16px; border-radius: 50%; border: 2px solid rgba(182,255,60,.25); border-top-color: var(--lime-400); animation: spin .7s linear infinite; }
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
.data-table td { padding: 14px 16px; border-bottom: 1px solid rgba(247,251,244,.06); vertical-align: middle; }
.data-table tbody tr:hover { background: rgba(182,255,60,.04); }
.cell-stack { display: flex; flex-direction: column; gap: 3px; }
.bold { font-weight: 600; color: var(--chalk-050); }
.sub { font-size: 12px; color: var(--chalk-200); opacity: .65; }
.ma-don { color: var(--lime-300); font-size: 12.5px; font-family: var(--font-mono); }
.price { font-weight: 700; color: var(--chalk-050); }
.badge-san { font-size: 11px; background: rgba(247,251,244,.08); color: var(--chalk-200); padding: 2px 8px; border-radius: 5px; width: max-content; }

/* BADGES */
.badge { display: inline-flex; padding: 4px 11px; border-radius: 20px; font-size: 11.5px; font-weight: 700; }
.badge-warning { background: rgba(255,176,32,.15); color: var(--amber-400); }
.badge-info { background: rgba(35,147,90,.2); color: #6fe0a6; }
.badge-success { background: rgba(182,255,60,.15); color: var(--lime-300); }
.badge-danger { background: rgba(255,71,87,.15); color: #ff9686; }

/* ACTION BUTTONS */
.actions { display: flex; gap: 6px; justify-content: center; flex-wrap: wrap; }
.btn-act { padding: 6px 11px; border-radius: 7px; border: 1px solid transparent; font-size: 11.5px; font-weight: 700; cursor: pointer; transition: .15s; }
.btn-act.confirm { background: rgba(35,147,90,.18); color: #6fe0a6; }
.btn-act.confirm:hover { background: rgba(35,147,90,.3); }
.btn-act.cancel { background: rgba(255,71,87,.15); color: #ff9686; }
.btn-act.cancel:hover { background: rgba(255,71,87,.26); }
.btn-act.dichvu { background: rgba(182,255,60,.1); color: var(--lime-300); position: relative; }
.btn-act.dichvu:hover { background: rgba(182,255,60,.18); }

.btn-act.dichvu--noi-bat {
  background: rgba(255,176,32,.2); color: var(--amber-400); font-weight: 800;
  animation: dichvu-pulse 1.4s ease-in-out infinite;
}
.btn-act.dichvu--noi-bat:hover { background: rgba(255,176,32,.3); }
.dichvu-dot { display: inline-block; width: 7px; height: 7px; border-radius: 50%; background: var(--amber-400); margin-left: 5px; vertical-align: middle; }
@keyframes dichvu-pulse {
  0%, 100% { transform: scale(1); box-shadow: 0 0 0 0 rgba(255,176,32,.45); }
  50% { transform: scale(1.06); box-shadow: 0 0 0 5px rgba(255,176,32,0); }
}

.btn-act.giahan { background: rgba(255,176,32,.12); color: var(--amber-400); }
.btn-act.giahan:hover:not(:disabled) { background: rgba(255,176,32,.22); }
.btn-act.giahan:disabled { opacity: .55; cursor: not-allowed; }
.no-action { color: rgba(247,251,244,.3); font-size: 15px; }

.no-data { text-align: center; padding: 50px 20px; color: var(--chalk-200); opacity: .6; }
.no-data__icon { font-size: 26px; margin-bottom: 8px; }

/* ============================================================
   MODAL
   ============================================================ */
.overlay { position: fixed; inset: 0; background: rgba(5,11,8,.75); backdrop-filter: blur(3px); display: flex; align-items: center; justify-content: center; z-index: 200; padding: 20px; }
.modal-box { background: var(--night-800); border: 1px solid rgba(182,255,60,.2); border-radius: 18px; width: 100%; max-width: 520px; box-shadow: 0 24px 50px rgba(0,0,0,.5); overflow: hidden; max-height: 88vh; overflow-y: auto; }
.modal-box.wide { max-width: 680px; }
.modal-header { padding: 20px 24px; border-bottom: 1px solid rgba(247,251,244,.1); display: flex; justify-content: space-between; align-items: center; }
.modal-header h2 { font-family: var(--font-display); font-size: 17px; font-weight: 600; color: var(--chalk-050); margin: 0; }
.close-btn { background: none; border: none; font-size: 22px; color: var(--chalk-200); opacity: .6; cursor: pointer; }
.close-btn:hover { opacity: 1; color: var(--lime-300); }
.modal-body { padding: 20px 24px; }
.modal-sub { font-size: 13px; color: var(--chalk-200); opacity: .75; margin: -6px 0 16px; }
.modal-footer { padding: 16px 24px; border-top: 1px solid rgba(247,251,244,.1); display: flex; justify-content: flex-end; gap: 10px; }
.form-row { display: flex; gap: 14px; margin-bottom: 14px; }
.form-group { flex: 1; display: flex; flex-direction: column; gap: 6px; margin-bottom: 14px; }
.form-label { font-size: 12.5px; font-weight: 600; color: var(--lime-300); font-family: var(--font-mono); text-transform: uppercase; letter-spacing: .04em; }
textarea.form-control { resize: vertical; width: 100%; box-sizing: border-box; font-family: var(--font-body); }

/* dịch vụ modal */
.dich-vu-trong { font-size: 13.5px; color: var(--chalk-200); opacity: .6; padding: 20px 0; text-align: center; }
.dich-vu-list { display: flex; flex-direction: column; gap: 6px; margin-bottom: 14px; }
.dich-vu-row { display: flex; justify-content: space-between; font-size: 13.5px; color: var(--chalk-200); padding: 6px 0; border-bottom: 1px dashed rgba(247,251,244,.12); }
.tong-tien-block { border-top: 1px solid rgba(247,251,244,.1); padding-top: 12px; display: flex; flex-direction: column; gap: 6px; }
.info-row { display: flex; justify-content: space-between; align-items: center; font-size: 13.5px; color: var(--chalk-200); padding: 3px 0; }
.info-row.highlight { background: rgba(182,255,60,.08); border-radius: 8px; padding: 10px; margin-top: 4px; }
.info-label { font-weight: 600; color: var(--chalk-200); opacity: .8; }
.price-big { font-size: 18px; font-weight: 800; color: var(--lime-300); }

/* ============================================================
   RESPONSIVE
   ============================================================ */
@media (max-width: 720px) {
  .filter-top { flex-direction: column; align-items: stretch; }
  .pill-select { min-width: 0; }
  .day-jump { margin-left: 0; }
  .data-table { font-size: 12.5px; }
}
</style>