<template>
  <div class="lich-san-manager">
    <!-- HEADER -->
    <div class="page-header">
      <div>
        <h1 class="page-title">Lịch sân theo ngày</h1>
        <p class="page-desc">Xem toàn bộ lịch đặt sân và trạng thái từng sân.</p>
      </div>
      <div class="header-controls">
        <button class="btn btn-outline" @click="prevDay">← Hôm trước</button>
        <input type="date" v-model="ngayChon" class="form-control date-pick" @change="fetchLich"/>
        <button class="btn btn-outline" @click="nextDay">Hôm sau →</button>
        <button class="btn btn-primary" @click="openKhoaModal">🔒 Khóa sân</button>
      </div>
    </div>

    <!-- TOAST -->
    <div v-if="toast.show" :class="['toast', toast.type]">{{ toast.msg }}</div>

    <!-- LEGEND -->
    <div class="legend-bar">
      <span class="legend-item"><span class="dot dot-empty"></span> Trống</span>
      <span class="legend-item"><span class="dot dot-dat"></span> Đã cọc / CHO XN</span>
      <span class="legend-item"><span class="dot dot-dang"></span> Đang đá</span>
      <span class="legend-item"><span class="dot dot-done"></span> Hoàn thành</span>
      <span class="legend-item"><span class="dot dot-bao"></span> Bảo trì</span>
    </div>

    <!-- FILTER LOẠI SÂN -->
    <div class="filter-bar">
      <button :class="['tab-btn', loaiFilter === '' ? 'active' : '']" @click="loaiFilter = ''">Tất cả</button>
      <button :class="['tab-btn', loaiFilter === '5' ? 'active' : '']" @click="loaiFilter = '5'">Sân 5 người</button>
      <button :class="['tab-btn', loaiFilter === '7' ? 'active' : '']" @click="loaiFilter = '7'">Sân 7 người</button>
    </div>

    <!-- LOADING -->
    <div v-if="loading" class="state-box">⏳ Đang tải lịch sân...</div>
    <div v-else-if="error" class="state-box error">{{ error }}</div>

    <!-- CALENDAR GRID -->
    <div v-else class="calendar-wrapper">
      <div class="calendar-scroll">
        <table class="calendar-table">
          <thead>
            <tr>
              <th class="san-col">Sân</th>
              <th v-for="h in gioHienThi" :key="h" class="gio-col">{{ h }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="san in filteredSan" :key="san.sanBongId">
              <td class="san-name-cell">
                <div class="san-info">
                  <span class="san-ten">{{ san.tenSan }}</span>
                  <span :class="['san-status', san.trangThaiSan === 'BAO_TRI' ? 'bao-tri' : 'hoat-dong']">
                    {{ san.trangThaiSan === 'BAO_TRI' ? '🔒 Bảo trì' : '✅ Hoạt động' }}
                  </span>
                </div>
              </td>
              <td v-for="gio in gioHienThi" :key="gio" :class="['slot-cell', getSlotClass(san, gio)]" @click="onSlotClick(san, gio)">
                <div class="slot-content" v-if="getSlotInfo(san, gio)">
                  <span class="slot-name">{{ getSlotInfo(san, gio).hoTenDat }}</span>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- MODAL KHÓA SÂN -->
    <div class="overlay" v-if="khoaModal.show">
      <div class="modal-box">
        <div class="modal-header">
          <h2>{{ khoaModal.isLock ? '🔒 Khóa sân bảo trì' : '🔓 Mở lại sân' }}</h2>
          <button class="close-btn" @click="khoaModal.show = false">&times;</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">Chọn sân *</label>
            <select v-model="khoaModal.sanBongId" class="form-control">
              <option value="">-- Chọn sân --</option>
              <option v-for="san in allSan" :key="san.sanBongId" :value="san.sanBongId">
                {{ san.tenSan }} ({{ san.trangThaiSan === 'BAO_TRI' ? 'Đang bảo trì' : 'Hoạt động' }})
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-label">Thao tác</label>
            <div class="toggle-row">
              <button :class="['toggle-btn', khoaModal.isLock ? 'active-lock' : '']" @click="khoaModal.isLock = true">🔒 Khóa sân</button>
              <button :class="['toggle-btn', !khoaModal.isLock ? 'active-open' : '']" @click="khoaModal.isLock = false">🔓 Mở sân</button>
            </div>
          </div>
          <div class="form-group" v-if="khoaModal.isLock">
            <label class="form-label">Lý do bảo trì</label>
            <textarea v-model="khoaModal.lyDo" class="form-control" rows="3" placeholder="VD: Sửa chữa lưới, vệ sinh sân..."></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-ghost" @click="khoaModal.show = false">Đóng</button>
          <button class="btn btn-primary" @click="submitKhoa" :disabled="khoaModal.loading">
            {{ khoaModal.loading ? 'Đang xử lý...' : 'Xác nhận' }}
          </button>
        </div>
      </div>
    </div>

    <!-- MODAL XEM CHI TIẾT SLOT -->
    <div class="overlay" v-if="slotModal.show">
      <div class="modal-box">
        <div class="modal-header">
          <h2>Chi tiết khung giờ {{ slotModal.gio }}</h2>
          <button class="close-btn" @click="slotModal.show = false">&times;</button>
        </div>
        <div class="modal-body" v-if="slotModal.slot">
          <div class="info-row"><span class="info-label">Khách hàng:</span><span>{{ slotModal.slot.hoTenDat }}</span></div>
          <div class="info-row"><span class="info-label">SĐT:</span><span>{{ slotModal.slot.soDienThoai }}</span></div>
          <div class="info-row"><span class="info-label">Khung giờ:</span><span>{{ slotModal.slot.gioBatDau }} – {{ slotModal.slot.gioKetThuc }}</span></div>
          <div class="info-row"><span class="info-label">Tổng tiền:</span><span>{{ formatMoney(slotModal.slot.tongTien) }}</span></div>
          <div class="info-row"><span class="info-label">Trạng thái:</span><span :class="['badge', badgeClass(slotModal.slot.trangThai)]">{{ labelTT(slotModal.slot.trangThai) }}</span></div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-ghost" @click="slotModal.show = false">Đóng</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import staffService from '@/services/staffService'

const ngayChon = ref(new Date().toISOString().slice(0, 10))
const allSan = ref([])
const loading = ref(false)
const error = ref('')
const loaiFilter = ref('')
const toast = ref({ show: false, msg: '', type: 'success' })

const khoaModal = ref({ show: false, isLock: true, sanBongId: '', lyDo: '', loading: false })
const slotModal = ref({ show: false, slot: null, gio: '' })

const gioHienThi = ['06:00','07:00','08:00','09:00','10:00','11:00','12:00','13:00','14:00','15:00','16:00','17:00','18:00','19:00','20:00','21:00']

onMounted(fetchLich)

async function fetchLich() {
  loading.value = true; error.value = ''
  try {
    allSan.value = await staffService.layLichSanTheoNgay(ngayChon.value)
  } catch (e) {
    error.value = e.message
  } finally {
    loading.value = false
  }
}

const filteredSan = computed(() => {
  if (!loaiFilter.value) return allSan.value
  return allSan.value.filter(s => String(s.loaiSan) === loaiFilter.value)
})

function getSlotInfo(san, gio) {
  if (san.trangThaiSan === 'BAO_TRI') return null
  return san.danhSachDat?.find(d => {
    const start = d.gioBatDau?.slice(0, 5)
    const end = d.gioKetThuc?.slice(0, 5)
    return gio >= start && gio < end
  }) || null
}

function getSlotClass(san, gio) {
  if (san.trangThaiSan === 'BAO_TRI') return 'slot-bao'
  const slot = getSlotInfo(san, gio)
  if (!slot) return 'slot-empty'
  if (slot.trangThai === 'DANG_DA') return 'slot-dang'
  if (slot.trangThai === 'HOAN_THANH') return 'slot-done'
  return 'slot-dat'
}

function onSlotClick(san, gio) {
  const slot = getSlotInfo(san, gio)
  if (slot) { slotModal.value = { show: true, slot, gio } }
}

function prevDay() {
  const d = new Date(ngayChon.value); d.setDate(d.getDate() - 1)
  ngayChon.value = d.toISOString().slice(0, 10); fetchLich()
}
function nextDay() {
  const d = new Date(ngayChon.value); d.setDate(d.getDate() + 1)
  ngayChon.value = d.toISOString().slice(0, 10); fetchLich()
}

function openKhoaModal() {
  khoaModal.value = { show: true, isLock: true, sanBongId: '', lyDo: '', loading: false }
}

async function submitKhoa() {
  if (!khoaModal.value.sanBongId) { showToast('Vui lòng chọn sân!', 'error'); return }
  khoaModal.value.loading = true
  try {
    const tt = khoaModal.value.isLock ? 'BAO_TRI' : 'HOAT_DONG'
    const res = await staffService.capNhatTrangThaiSan(khoaModal.value.sanBongId, tt, khoaModal.value.lyDo)
    showToast(res.message || 'Cập nhật thành công!')
    khoaModal.value.show = false
    await fetchLich()
  } catch (e) { showToast(e.message, 'error') }
  finally { khoaModal.value.loading = false }
}

function showToast(msg, type = 'success') {
  toast.value = { show: true, msg, type }
  setTimeout(() => { toast.value.show = false }, 3500)
}
function formatMoney(v) { return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(v || 0) }
function labelTT(t) { return { CHO_XAC_NHAN:'Chờ XN', DA_COC:'Đã cọc', HOAN_THANH:'Hoàn thành', DA_HUY:'Đã hủy', DANG_DA:'Đang đá' }[t] || t }
function badgeClass(t) { return { CHO_XAC_NHAN:'badge-warning', DA_COC:'badge-info', HOAN_THANH:'badge-success', DA_HUY:'badge-danger', DANG_DA:'badge-playing' }[t] || '' }
</script>

<style scoped>
.lich-san-manager { width: 100%; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; flex-wrap: wrap; gap: 12px; }
.page-title { font-size: 24px; font-weight: 700; color: #1e293b; margin: 0 0 4px; }
.page-desc { font-size: 14px; color: #64748b; margin: 0; }
.header-controls { display: flex; gap: 8px; align-items: center; flex-wrap: wrap; }
.date-pick { font-size: 14px; }

.toast { position: fixed; top: 20px; right: 24px; z-index: 9999; padding: 12px 20px; border-radius: 10px; font-weight: 600; font-size: 14px; box-shadow: 0 4px 12px rgba(0,0,0,.15); }
.toast.success { background: #dcfce7; color: #15803d; border: 1px solid #bbf7d0; }
.toast.error { background: #fee2e2; color: #b91c1c; border: 1px solid #fecaca; }

.legend-bar { display: flex; gap: 20px; flex-wrap: wrap; padding: 10px 16px; background: #fff; border: 1px solid #e2e8f0; border-radius: 10px; margin-bottom: 14px; }
.legend-item { display: flex; align-items: center; gap: 6px; font-size: 13px; color: #475569; }
.dot { width: 12px; height: 12px; border-radius: 3px; display: inline-block; }
.dot-empty { background: #f1f5f9; border: 1px solid #cbd5e1; }
.dot-dat { background: #bfdbfe; }
.dot-dang { background: #fef08a; }
.dot-done { background: #bbf7d0; }
.dot-bao { background: #fecaca; }

.filter-bar { display: flex; gap: 8px; margin-bottom: 16px; }
.tab-btn { padding: 7px 16px; border: 1px solid #cbd5e1; border-radius: 20px; background: #fff; font-size: 13px; cursor: pointer; color: #64748b; transition: .2s; }
.tab-btn.active { background: #1E3932; color: #fff; border-color: #1E3932; }

.calendar-wrapper { background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; overflow: hidden; }
.calendar-scroll { overflow-x: auto; }
.calendar-table { width: 100%; border-collapse: collapse; min-width: 1200px; }
.calendar-table th { background: #f8fafc; padding: 10px 6px; text-align: center; font-size: 12px; font-weight: 600; color: #64748b; border-bottom: 1px solid #e2e8f0; white-space: nowrap; }
.san-col { width: 140px; text-align: left; padding-left: 14px !important; }
.gio-col { width: 70px; }
.calendar-table td { border-right: 1px solid #f1f5f9; border-bottom: 1px solid #f1f5f9; vertical-align: middle; }
.san-name-cell { padding: 10px 14px; background: #f8fafc; border-right: 2px solid #e2e8f0 !important; }
.san-info { display: flex; flex-direction: column; gap: 3px; }
.san-ten { font-weight: 700; font-size: 13px; color: #1e293b; }
.san-status { font-size: 11px; }
.hoat-dong { color: #15803d; }
.bao-tri { color: #b91c1c; }

.slot-cell { height: 48px; cursor: pointer; transition: .15s; }
.slot-cell:hover { filter: brightness(.93); }
.slot-empty { background: #f8fafc; }
.slot-dat { background: #bfdbfe; }
.slot-dang { background: #fef08a; }
.slot-done { background: #bbf7d0; }
.slot-bao { background: repeating-linear-gradient(45deg, #fecaca, #fecaca 4px, #fff 4px, #fff 12px); }
.slot-content { padding: 4px 6px; }
.slot-name { font-size: 11px; font-weight: 600; color: #1e293b; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; display: block; max-width: 62px; }

.btn { display: inline-flex; align-items: center; gap: 6px; padding: 9px 16px; font-size: 13px; font-weight: 600; border-radius: 8px; border: none; cursor: pointer; transition: .2s; white-space: nowrap; }
.btn-primary { background: #1E3932; color: #fff; }
.btn-primary:hover { background: #14261f; }
.btn-ghost { background: #f1f5f9; color: #475569; }
.btn-ghost:hover { background: #e2e8f0; }
.btn-outline { background: #fff; border: 1px solid #cbd5e1; color: #475569; }
.form-control { padding: 9px 13px; border: 1px solid #cbd5e1; border-radius: 8px; font-size: 14px; outline: none; background: #fff; color: #1e293b; }
.form-control:focus { border-color: #1E3932; }

.overlay { position: fixed; inset: 0; background: rgba(15,23,42,.45); display: flex; align-items: center; justify-content: center; z-index: 200; }
.modal-box { background: #fff; border-radius: 16px; width: 100%; max-width: 480px; box-shadow: 0 20px 40px rgba(0,0,0,.15); overflow: hidden; }
.modal-header { padding: 18px 22px; border-bottom: 1px solid #e2e8f0; display: flex; justify-content: space-between; align-items: center; }
.modal-header h2 { font-size: 17px; font-weight: 700; color: #1e293b; margin: 0; }
.close-btn { background: none; border: none; font-size: 22px; color: #94a3b8; cursor: pointer; }
.modal-body { padding: 20px 22px; }
.modal-footer { padding: 14px 22px; border-top: 1px solid #e2e8f0; display: flex; justify-content: flex-end; gap: 10px; }
.form-group { display: flex; flex-direction: column; gap: 6px; margin-bottom: 14px; }
.form-label { font-size: 13px; font-weight: 600; color: #475569; }
textarea.form-control { resize: vertical; }
.toggle-row { display: flex; gap: 8px; }
.toggle-btn { flex: 1; padding: 9px; border: 1px solid #cbd5e1; border-radius: 8px; background: #f8fafc; cursor: pointer; font-size: 13px; font-weight: 600; transition: .2s; }
.toggle-btn.active-lock { background: #fee2e2; border-color: #fca5a5; color: #b91c1c; }
.toggle-btn.active-open { background: #dcfce7; border-color: #86efac; color: #15803d; }
.info-row { display: flex; gap: 12px; align-items: center; padding: 8px 0; border-bottom: 1px solid #f1f5f9; }
.info-label { font-weight: 600; color: #64748b; min-width: 100px; font-size: 13px; }
.badge { display: inline-flex; padding: 3px 9px; border-radius: 20px; font-size: 12px; font-weight: 600; }
.badge-warning { background: #fef3c7; color: #b45309; }
.badge-info { background: #dbeafe; color: #1d4ed8; }
.badge-success { background: #dcfce7; color: #15803d; }
.badge-danger { background: #fee2e2; color: #b91c1c; }
.badge-playing { background: #fef08a; color: #92400e; }
.state-box { text-align: center; padding: 40px; color: #64748b; }
.state-box.error { color: #b91c1c; }
</style>