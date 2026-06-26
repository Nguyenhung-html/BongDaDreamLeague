<template>
  <div class="booking-manager">
    <!-- HEADER -->
    <div class="page-header">
      <div>
        <h1 class="page-title">Quản lý đặt sân</h1>
        <p class="page-desc">Xem, xác nhận và xử lý tất cả đơn đặt sân.</p>
      </div>
      <button class="btn btn-primary" @click="openWalkInModal">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 5v14M5 12h14"/></svg>
        Đặt sân tại quầy
      </button>
    </div>

    <!-- TOAST -->
    <div v-if="toast.show" :class="['toast', toast.type]">{{ toast.msg }}</div>

    <!-- FILTER -->
    <div class="filter-card">
      <div class="filter-group">
        <input type="text" v-model="filters.search" placeholder="Tìm tên khách, SĐT..." class="form-control search-box"/>
        <select v-model="filters.loaiSan" class="form-control">
          <option value="">Tất cả loại sân</option>
          <option value="5">Sân 5 người</option>
          <option value="7">Sân 7 người</option>
        </select>
        <input type="date" v-model="filters.ngay" class="form-control"/>
        <select v-model="filters.trangThai" class="form-control">
          <option value="">Tất cả trạng thái</option>
          <option value="CHO_XAC_NHAN">Chờ xác nhận</option>
          <option value="DA_COC">Đã cọc</option>
          <option value="HOAN_THANH">Hoàn thành</option>
          <option value="DA_HUY">Đã hủy</option>
        </select>
        <button class="btn btn-outline" @click="fetchDanhSach">🔄 Làm mới</button>
      </div>
    </div>

    <!-- LOADING / ERROR -->
    <div v-if="loading" class="state-box">⏳ Đang tải dữ liệu...</div>
    <div v-else-if="error" class="state-box error">{{ error }}</div>

    <!-- TABLE -->
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
                <button v-if="don.trangThai === 'DA_COC'" class="btn-act complete" title="Hoàn thành" @click="hoanThanh(don)">✔ Hoàn thành</button>
                <button v-if="don.trangThai !== 'DA_HUY' && don.trangThai !== 'HOAN_THANH'" class="btn-act cancel" title="Hủy đơn" @click="openHuyModal(don)">✕ Hủy</button>
                <span v-if="don.trangThai === 'DA_HUY' || don.trangThai === 'HOAN_THANH'" class="no-action">—</span>
              </div>
            </td>
          </tr>
          <tr v-if="filteredList.length === 0">
            <td colspan="7" class="no-data">Không tìm thấy đơn nào.</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- MODAL HỦY ĐƠN -->
    <div class="overlay" v-if="huyModal.show">
      <div class="modal-box">
        <div class="modal-header">
          <h2>Hủy đơn đặt sân</h2>
          <button class="close-btn" @click="huyModal.show = false">&times;</button>
        </div>
        <div class="modal-body">
          <p>Hủy đơn <strong>#{{ huyModal.don?.maGanDo }}</strong> – {{ huyModal.don?.hoTenDat }}</p>
          <label class="form-label">Lý do hủy *</label>
          <textarea v-model="huyModal.lyDo" class="form-control" rows="3" placeholder="Nhập lý do hủy..."></textarea>
        </div>
        <div class="modal-footer">
          <button class="btn btn-ghost" @click="huyModal.show = false">Đóng</button>
          <button class="btn btn-danger" @click="xacNhanHuy">Xác nhận hủy</button>
        </div>
      </div>
    </div>

    <!-- MODAL WALK-IN -->
    <div class="overlay" v-if="walkInModal.show">
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
          <button class="btn btn-ghost" @click="walkInModal.show = false">Đóng</button>
          <button class="btn btn-primary" @click="submitWalkIn" :disabled="walkInModal.loading">
            {{ walkInModal.loading ? 'Đang xử lý...' : 'Xác nhận đặt sân' }}
          </button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import staffService from '@/services/staffService'
import sanService from '@/services/sanService'

const danhSach = ref([])
const danhSachSan = ref([])
const loading = ref(false)
const error = ref('')
const toast = ref({ show: false, msg: '', type: 'success' })

const filters = ref({ search: '', loaiSan: '', ngay: '', trangThai: '' })

const huyModal = ref({ show: false, don: null, lyDo: '' })
const walkInModal = ref({ show: false, loading: false })
const walkIn = ref({ hoTenDat: '', soDienThoai: '', sanBongId: '', ngayDa: '', gioBatDau: '06:00', gioKetThuc: '07:00', phuongThuc: 'TIEN_MAT' })

const gioOptions = ['06:00','07:00','08:00','09:00','10:00','11:00','12:00','13:00','14:00','15:00','16:00','17:00','18:00','19:00','20:00','21:00','22:00']

onMounted(async () => {
  await fetchDanhSach()
  try {
    const res = await fetch('http://localhost:8080/api/san-bong', { headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` } })
    danhSachSan.value = await res.json()
  } catch {}
})

async function fetchDanhSach() {
  loading.value = true; error.value = ''
  try {
    const params = {}
    if (filters.value.ngay) params.ngay = filters.value.ngay
    if (filters.value.trangThai) params.trangThai = filters.value.trangThai
    danhSach.value = await staffService.layDanhSachDatSan(params)
  } catch (e) {
    error.value = e.message
  } finally {
    loading.value = false
  }
}

const filteredList = computed(() => {
  return danhSach.value.filter(d => {
    const s = filters.value.search.toLowerCase()
    const matchSearch = !s || d.hoTenDat?.toLowerCase().includes(s) || d.soDienThoai?.includes(s)
    const matchLoai = !filters.value.loaiSan || String(d.loaiSan) === filters.value.loaiSan
    return matchSearch && matchLoai
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

async function hoanThanh(don) {
  if (!confirm(`Đánh dấu hoàn thành đơn #${don.maGanDo}?`)) return
  try {
    await staffService.hoanThanhDon(don.id)
    don.trangThai = 'HOAN_THANH'
    showToast('Đã đánh dấu hoàn thành!')
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
.booking-manager { width: 100%; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.page-title { font-size: 24px; font-weight: 700; color: #1e293b; margin: 0 0 4px; }
.page-desc { font-size: 14px; color: #64748b; margin: 0; }

/* TOAST */
.toast { position: fixed; top: 20px; right: 24px; z-index: 9999; padding: 12px 20px; border-radius: 10px; font-weight: 600; font-size: 14px; box-shadow: 0 4px 12px rgba(0,0,0,0.15); animation: slideIn .3s ease; }
.toast.success { background: #dcfce7; color: #15803d; border: 1px solid #bbf7d0; }
.toast.error { background: #fee2e2; color: #b91c1c; border: 1px solid #fecaca; }
@keyframes slideIn { from { transform: translateX(60px); opacity: 0; } to { transform: translateX(0); opacity: 1; } }

/* FILTER */
.filter-card { background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 16px; margin-bottom: 20px; }
.filter-group { display: flex; flex-wrap: wrap; gap: 10px; align-items: center; }
.search-box { flex: 2; min-width: 220px; }
.form-control { padding: 9px 13px; border: 1px solid #cbd5e1; border-radius: 8px; font-size: 14px; outline: none; background: #fff; color: #1e293b; }
.form-control:focus { border-color: #1E3932; }

/* TABLE */
.table-card { background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; overflow: hidden; }
.data-table { width: 100%; border-collapse: collapse; font-size: 14px; }
.data-table th { background: #f8fafc; color: #64748b; font-weight: 600; padding: 14px 16px; text-align: left; border-bottom: 1px solid #e2e8f0; }
.data-table td { padding: 14px 16px; border-bottom: 1px solid #f1f5f9; vertical-align: middle; }
.data-table tbody tr:hover { background: #f8fafc; }
.cell-stack { display: flex; flex-direction: column; gap: 3px; }
.bold { font-weight: 600; color: #1e293b; }
.sub { font-size: 12px; color: #64748b; }
.ma-don { color: #1E3932; font-size: 13px; }
.price { font-weight: 700; color: #1e293b; }
.badge-san { font-size: 11px; background: #f1f5f9; color: #475569; padding: 2px 7px; border-radius: 4px; width: max-content; }

/* BADGES */
.badge { display: inline-flex; padding: 4px 10px; border-radius: 20px; font-size: 12px; font-weight: 600; }
.badge-warning { background: #fef3c7; color: #b45309; }
.badge-info { background: #dbeafe; color: #1d4ed8; }
.badge-success { background: #dcfce7; color: #15803d; }
.badge-danger { background: #fee2e2; color: #b91c1c; }

/* ACTION BUTTONS */
.actions { display: flex; gap: 6px; justify-content: center; flex-wrap: wrap; }
.btn-act { padding: 5px 10px; border-radius: 6px; border: none; font-size: 12px; font-weight: 600; cursor: pointer; transition: .2s; }
.btn-act.confirm { background: #dbeafe; color: #1d4ed8; }
.btn-act.confirm:hover { background: #bfdbfe; }
.btn-act.complete { background: #dcfce7; color: #15803d; }
.btn-act.complete:hover { background: #bbf7d0; }
.btn-act.cancel { background: #fee2e2; color: #b91c1c; }
.btn-act.cancel:hover { background: #fecaca; }
.no-action { color: #cbd5e1; font-size: 16px; }

/* BUTTONS */
.btn { display: inline-flex; align-items: center; gap: 6px; padding: 10px 18px; font-size: 14px; font-weight: 600; border-radius: 8px; border: none; cursor: pointer; transition: .2s; }
.btn-primary { background: #1E3932; color: #fff; }
.btn-primary:hover { background: #14261f; }
.btn-ghost { background: #f1f5f9; color: #475569; }
.btn-ghost:hover { background: #e2e8f0; }
.btn-outline { background: #fff; border: 1px solid #cbd5e1; color: #475569; padding: 9px 14px; border-radius: 8px; font-size: 13px; cursor: pointer; }
.btn-danger { background: #ef4444; color: #fff; }
.btn-danger:hover { background: #dc2626; }

/* MODAL */
.overlay { position: fixed; inset: 0; background: rgba(15,23,42,.45); display: flex; align-items: center; justify-content: center; z-index: 200; }
.modal-box { background: #fff; border-radius: 16px; width: 100%; max-width: 520px; box-shadow: 0 20px 40px rgba(0,0,0,.15); overflow: hidden; }
.modal-box.wide { max-width: 680px; }
.modal-header { padding: 20px 24px; border-bottom: 1px solid #e2e8f0; display: flex; justify-content: space-between; align-items: center; }
.modal-header h2 { font-size: 17px; font-weight: 700; color: #1e293b; margin: 0; }
.close-btn { background: none; border: none; font-size: 22px; color: #94a3b8; cursor: pointer; }
.modal-body { padding: 20px 24px; }
.modal-footer { padding: 16px 24px; border-top: 1px solid #e2e8f0; display: flex; justify-content: flex-end; gap: 10px; }
.form-row { display: flex; gap: 14px; margin-bottom: 14px; }
.form-group { flex: 1; display: flex; flex-direction: column; gap: 6px; margin-bottom: 14px; }
.form-label { font-size: 13px; font-weight: 600; color: #475569; }
textarea.form-control { resize: vertical; }

/* STATES */
.state-box { text-align: center; padding: 40px; color: #64748b; font-size: 15px; }
.state-box.error { color: #b91c1c; }
.no-data { text-align: center; padding: 32px; color: #94a3b8; }
</style>
