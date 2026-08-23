<template>
  <div class="lich-san">
    <!-- Nền ambient đồng bộ các trang khác -->
    <div class="lich-san__ambient" aria-hidden="true">
      <div class="floodlight floodlight--l"></div>
      <div class="floodlight floodlight--r"></div>
      <div class="motes"><span v-for="n in 12" :key="n" class="mote" :style="{ '--i': n }"></span></div>
    </div>

    <!-- HEADER -->
    <div class="page-header reveal">
      <div>
        <span class="eyebrow">Quản lý</span>
        <h1 class="page-title">Lịch sân theo ngày</h1>
        <p class="page-desc">Chọn một sân để xem toàn bộ khung giờ và trạng thái trong ngày.</p>
      </div>
      <div class="header-controls">
        <button class="btn btn-outline" @click="prevDay">← Hôm trước</button>
        <div class="date-display">
          <input type="date" v-model="ngayChon" class="date-input" @change="fetchLich" />
          <span class="date-text">{{ ngayFormatted }}</span>
        </div>
        <button class="btn btn-outline" @click="nextDay">Hôm sau →</button>
        <button class="btn btn-primary" @click="openKhoaModal">🔒 Khóa sân</button>
      </div>
    </div>

    <!-- TOAST -->
    <Transition name="toast-pop">
      <div v-if="toast.show" :class="['toast', toast.type]">{{ toast.msg }}</div>
    </Transition>

    <!-- LEGEND -->
    <div class="legend-bar reveal">
      <span class="legend-item"><span class="dot dot-empty"></span> Trống</span>
      <span class="legend-item"><span class="dot dot-dat"></span> Đã cọc / CHO XN</span>
      <span class="legend-item"><span class="dot dot-dang"></span> Đang đá</span>
      <span class="legend-item"><span class="dot dot-done"></span> Hoàn thành</span>
      <span class="legend-item"><span class="dot dot-bao"></span> Bảo trì</span>
    </div>

    <!-- FILTER LOẠI SÂN -->
    <div class="filter-bar reveal">
      <button :class="['tab-btn', loaiFilter === '' ? 'active' : '']" @click="loaiFilter = ''">Tất cả</button>
      <button :class="['tab-btn', loaiFilter === '5' ? 'active' : '']" @click="loaiFilter = '5'">Sân 5 người</button>
      <button :class="['tab-btn', loaiFilter === '7' ? 'active' : '']" @click="loaiFilter = '7'">Sân 7 người</button>
    </div>

    <!-- LOADING / ERROR -->
    <div v-if="loading" class="state-box">⏳ Đang tải lịch sân...</div>
    <div v-else-if="error" class="state-box error">{{ error }}</div>

    <template v-else>
      <!-- DANH SÁCH SÂN — 1 HÀNG, TICK ĐỂ CHỌN -->
      <div class="court-row reveal">
        <button
          v-for="san in filteredSan" :key="san.sanBongId"
          class="court-chip"
          :class="{ 'court-chip--active': selectedSanId === san.sanBongId, 'court-chip--bao': san.trangThaiSan === 'BAO_TRI' }"
          @click="selectedSanId = san.sanBongId"
        >
          <span class="court-chip__check">
            <svg v-if="selectedSanId === san.sanBongId" width="12" height="12" viewBox="0 0 24 24" fill="none"><path d="M5 13L9.5 17.5L19 7" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/></svg>
          </span>
          <span class="court-chip__dot" :class="san.trangThaiSan === 'BAO_TRI' ? 'is-bao' : 'is-hoat-dong'"></span>
          <span class="court-chip__name">{{ san.tenSan }}</span>
          <span class="court-chip__loai">Sân {{ san.loaiSan }}</span>
        </button>
        <p v-if="!filteredSan.length" class="empty-note">Không có sân nào phù hợp bộ lọc.</p>
      </div>

      <!-- LỊCH CỦA SÂN ĐANG CHỌN -->
      <div v-if="selectedSan" class="schedule-panel reveal" ref="schedulePanelRef">
        <div class="schedule-panel__head">
          <div>
            <h3>{{ selectedSan.tenSan }} <span class="muted">· Sân {{ selectedSan.loaiSan }} người</span></h3>
            <span class="schedule-panel__status" :class="selectedSan.trangThaiSan === 'BAO_TRI' ? 'bao-tri' : 'hoat-dong'">
              {{ selectedSan.trangThaiSan === 'BAO_TRI' ? '🔒 Đang bảo trì' : '✅ Đang hoạt động' }}
            </span>
          </div>
          <span class="schedule-panel__date">{{ ngayFormatted }}</span>
        </div>

        <div class="slot-grid">
          <button
            v-for="gio in gioHienThi" :key="gio"
            class="slot-card" :class="getSlotClass(selectedSan, gio)"
            @click="onSlotClick(selectedSan, gio)"
          >
            <span class="slot-card__time">{{ gio }}</span>
            <span v-if="getSlotInfo(selectedSan, gio)" class="slot-card__name">{{ getSlotInfo(selectedSan, gio).hoTenDat }}</span>
            <span v-else-if="selectedSan.trangThaiSan === 'BAO_TRI'" class="slot-card__empty">Bảo trì</span>
            <span v-else class="slot-card__empty">Trống</span>
          </button>
        </div>
      </div>
      <p v-else class="empty-note reveal">Chưa có sân nào để hiển thị lịch.</p>

      <!-- ===== BẢNG LỊCH ĐÃ ĐẶT (KHOẢNG NGÀY) ===== -->
      <div class="booking-panel reveal">
        <div class="booking-panel__head">
          <div>
            <h3 class="booking-panel__title">📋 Lịch đã đặt</h3>
            <p class="booking-panel__desc">Tick vào một dòng để nhảy thẳng tới đúng sân &amp; đúng ngày ở phần lịch phía trên.</p>
          </div>
          <div class="range-controls">
            <div class="range-field">
              <label>Từ ngày</label>
              <input type="date" v-model="khoangNgay.tu" class="date-input" />
            </div>
            <div class="range-field">
              <label>Đến ngày</label>
              <input type="date" v-model="khoangNgay.den" class="date-input" />
            </div>
            <button class="btn btn-primary" @click="fetchDsDatKhoang" :disabled="loadingKhoang">
              {{ loadingKhoang ? 'Đang tra cứu...' : '🔍 Tra cứu' }}
            </button>
          </div>
        </div>

        <div v-if="errorKhoang" class="state-box error small">{{ errorKhoang }}</div>
        <div v-else-if="loadingKhoang" class="state-box small">⏳ Đang tải danh sách đặt sân...</div>
        <template v-else>
          <div v-if="!dsDatKhoangSorted.length" class="empty-note">Không có lượt đặt nào trong khoảng ngày đã chọn.</div>
          <div v-else class="booking-table-wrap">
            <table class="booking-table">
              <thead>
                <tr>
                  <th></th>
                  <th>Ngày</th>
                  <th>Sân</th>
                  <th>Khung giờ</th>
                  <th>Khách hàng</th>
                  <th>SĐT</th>
                  <th>Trạng thái</th>
                </tr>
              </thead>
              <tbody>
                <tr
                  v-for="item in dsDatKhoangSorted" :key="item.datSanId ?? (item.ngay + item.sanBongId + item.gioBatDau)"
                  class="booking-row"
                  :class="{ 'booking-row--active': isRowActive(item) }"
                  @click="chonDongDaDat(item)"
                >
                  <td class="booking-row__check">
                    <span class="row-tick" :class="{ 'row-tick--on': isRowActive(item) }">
                      <svg v-if="isRowActive(item)" width="11" height="11" viewBox="0 0 24 24" fill="none"><path d="M5 13L9.5 17.5L19 7" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/></svg>
                    </span>
                  </td>
                  <td>{{ formatNgay(item.ngay) }}</td>
                  <td>{{ item.tenSan }} <span class="muted">· Sân {{ item.loaiSan }}</span></td>
                  <td class="mono">{{ item.gioBatDau?.slice(0,5) }} – {{ item.gioKetThuc?.slice(0,5) }}</td>
                  <td>{{ item.hoTenDat }}</td>
                  <td class="mono">{{ item.soDienThoai }}</td>
                  <td><span :class="['badge', badgeClass(item.trangThai)]">{{ labelTT(item.trangThai) }}</span></td>
                </tr>
              </tbody>
            </table>
          </div>
        </template>
      </div>
    </template>

    <!-- MODAL KHÓA SÂN -->
    <Transition name="modal-pop">
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
    </Transition>

    <!-- MODAL XEM CHI TIẾT SLOT -->
    <Transition name="modal-pop">
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
    </Transition>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, nextTick } from 'vue'
import staffService from '@/services/staffService'

const ngayChon = ref(new Date().toISOString().slice(0, 10))
const allSan = ref([])
const loading = ref(false)
const error = ref('')
const loaiFilter = ref('')
const toast = ref({ show: false, msg: '', type: 'success' })
const selectedSanId = ref('')
const schedulePanelRef = ref(null)

const khoaModal = ref({ show: false, isLock: true, sanBongId: '', lyDo: '', loading: false })
const slotModal = ref({ show: false, slot: null, gio: '' })

// ===== Khoảng ngày cho bảng "Lịch đã đặt" =====
const khoangNgay = ref({ tu: ngayChon.value, den: ngayChon.value })
const dsDatKhoang = ref([]) // danh sách các lượt đặt đã gom từ nhiều ngày
const loadingKhoang = ref(false)
const errorKhoang = ref('')
const MAX_NGAY_TRA_CUU = 31 // giới hạn số ngày / lần tra cứu, tránh gọi API quá nhiều

const gioHienThi = ['06:00','07:00','08:00','09:00','10:00','11:00','12:00','13:00','14:00','15:00','16:00','17:00','18:00','19:00','20:00','21:00']

const ngayFormatted = computed(() => {
  if (!ngayChon.value) return ''
  const d = new Date(ngayChon.value + 'T00:00:00')
  if (isNaN(d.getTime())) return ''
  const s = d.toLocaleDateString('vi-VN', { weekday: 'long', day: '2-digit', month: '2-digit', year: 'numeric' })
  return s.charAt(0).toUpperCase() + s.slice(1)
})

onMounted(() => {
  fetchLich()
  fetchDsDatKhoang()

  const revealObserver = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('is-visible')
        revealObserver.unobserve(entry.target)
      }
    })
  }, { threshold: 0.12 })
  nextTick(() => document.querySelectorAll('.reveal').forEach(el => revealObserver.observe(el)))
})

async function fetchLich() {
  loading.value = true; error.value = ''
  try {
    allSan.value = await staffService.layLichSanTheoNgay(ngayChon.value)
    ensureSelectedSan()
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

const selectedSan = computed(() => allSan.value.find(s => s.sanBongId === selectedSanId.value) || null)

function ensureSelectedSan() {
  const list = filteredSan.value
  if (!list.length) { selectedSanId.value = ''; return }
  if (!list.find(s => s.sanBongId === selectedSanId.value)) {
    selectedSanId.value = list[0].sanBongId
  }
}

watch(loaiFilter, ensureSelectedSan)

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
    await fetchDsDatKhoang()
  } catch (e) { showToast(e.message, 'error') }
  finally { khoaModal.value.loading = false }
}

function showToast(msg, type = 'success') {
  toast.value = { show: true, msg, type }
  setTimeout(() => { toast.value.show = false }, 3500)
}
function formatMoney(v) { return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(v || 0) }
function formatNgay(ngayStr) {
  if (!ngayStr) return ''
  const d = new Date(ngayStr + 'T00:00:00')
  if (isNaN(d.getTime())) return ngayStr
  return d.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' })
}
function labelTT(t) { return { CHO_XAC_NHAN:'Chờ XN', DA_COC:'Đã cọc', HOAN_THANH:'Hoàn thành', DA_HUY:'Đã hủy', DANG_DA:'Đang đá' }[t] || t }
function badgeClass(t) { return { CHO_XAC_NHAN:'badge-warning', DA_COC:'badge-info', HOAN_THANH:'badge-success', DA_HUY:'badge-danger', DANG_DA:'badge-playing' }[t] || '' }

// ===== BẢNG "LỊCH ĐÃ ĐẶT" theo khoảng ngày =====

function danhSachNgayTrongKhoang(tu, den) {
  const dsNgay = []
  const start = new Date(tu + 'T00:00:00')
  const end = new Date(den + 'T00:00:00')
  if (isNaN(start.getTime()) || isNaN(end.getTime()) || start > end) return dsNgay
  const cur = new Date(start)
  let guard = 0
  while (cur <= end && guard < MAX_NGAY_TRA_CUU) {
    dsNgay.push(cur.toISOString().slice(0, 10))
    cur.setDate(cur.getDate() + 1)
    guard++
  }
  return dsNgay
}

async function fetchDsDatKhoang() {
  errorKhoang.value = ''
  const { tu, den } = khoangNgay.value
  if (!tu || !den) { errorKhoang.value = 'Vui lòng chọn đủ Từ ngày và Đến ngày.'; return }
  if (new Date(tu) > new Date(den)) { errorKhoang.value = '"Từ ngày" phải nhỏ hơn hoặc bằng "Đến ngày".'; return }

  const dsNgay = danhSachNgayTrongKhoang(tu, den)
  if (!dsNgay.length) { errorKhoang.value = 'Khoảng ngày không hợp lệ.'; return }

  loadingKhoang.value = true
  try {
    const ketQua = await Promise.all(
      dsNgay.map(ngay => staffService.layLichSanTheoNgay(ngay).then(dsSan => ({ ngay, dsSan })).catch(() => ({ ngay, dsSan: [] })))
    )

    const gomLai = []
    for (const { ngay, dsSan } of ketQua) {
      for (const san of dsSan) {
        for (const dat of (san.danhSachDat || [])) {
          if (dat.trangThai === 'DA_HUY') continue // bỏ qua các lượt đã hủy
          gomLai.push({
            ...dat,
            ngay,
            sanBongId: san.sanBongId,
            tenSan: san.tenSan,
            loaiSan: san.loaiSan
          })
        }
      }
    }
    dsDatKhoang.value = gomLai
  } catch (e) {
    errorKhoang.value = e.message || 'Không thể tải danh sách đặt sân.'
  } finally {
    loadingKhoang.value = false
  }
}

const dsDatKhoangSorted = computed(() => {
  return [...dsDatKhoang.value].sort((a, b) => {
    if (a.ngay !== b.ngay) return a.ngay < b.ngay ? -1 : 1
    return (a.gioBatDau || '').localeCompare(b.gioBatDau || '')
  })
})

function isRowActive(item) {
  return item.ngay === ngayChon.value && item.sanBongId === selectedSanId.value
}

async function chonDongDaDat(item) {
  loaiFilter.value = '' // đảm bảo sân đó không bị bộ lọc loại sân ẩn đi
  ngayChon.value = item.ngay
  await fetchLich()
  selectedSanId.value = item.sanBongId
  await nextTick()
  schedulePanelRef.value?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Bebas+Neue&family=Oswald:wght@500;600;700&family=Manrope:wght@400;500;600;700;800&family=Space+Mono:wght@400;700&display=swap');
</style>

<style scoped>
.lich-san {
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
  width: 100%;
  background: var(--night-950);
  color: var(--chalk-050);
  font-family: var(--font-body);
  border-radius: 20px;
  padding: 32px;
  overflow: hidden;
  min-height: calc(100vh - 40px);
}

.reveal { opacity: 0; transform: translateY(16px); transition: opacity .55s ease, transform .55s ease; }
.reveal.is-visible { opacity: 1; transform: translateY(0); }
@media (prefers-reduced-motion: reduce) { .reveal { opacity: 1; transform: none; transition: none; } }

/* ===== AMBIENT ===== */
.lich-san__ambient { position: absolute; inset: 0; z-index: 0; pointer-events: none; border-radius: 20px; overflow: hidden; }
.floodlight { position: absolute; top: -20%; width: 40vh; height: 100vh; background: conic-gradient(from 90deg at 50% 0%, transparent 42%, rgba(255,244,214,.06) 50%, transparent 58%); mix-blend-mode: screen; animation: sweep 11s ease-in-out infinite alternate; }
.floodlight--l { left: -6%; }
.floodlight--r { right: -6%; animation-direction: alternate-reverse; }
@keyframes sweep { 0% { transform: rotate(-8deg); } 100% { transform: rotate(8deg); } }
.motes { position: absolute; inset: 0; }
.mote { position: absolute; bottom: -10px; left: calc((var(--i) * 8.4%) + 1%); width: 3px; height: 3px; border-radius: 50%; background: var(--lime-300); opacity: 0; animation: rise 9s linear infinite; animation-delay: calc(var(--i) * -0.7s); }
@keyframes rise { 0% { transform: translateY(0) scale(.6); opacity: 0; } 10% { opacity: .45; } 90% { opacity: .1; } 100% { transform: translateY(-100%) scale(1.1); opacity: 0; } }

/* ===== HEADER ===== */
.page-header { position: relative; z-index: 1; display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 22px; flex-wrap: wrap; gap: 16px; }
.eyebrow { display: inline-block; font-family: var(--font-mono); font-size: 12px; letter-spacing: .14em; text-transform: uppercase; color: var(--lime-300); }
.page-title { font-family: var(--font-display); font-size: 26px; font-weight: 600; color: var(--chalk-050); margin: 6px 0 4px; }
.page-desc { font-size: 13.5px; color: var(--chalk-200); opacity: .75; margin: 0; }

.header-controls { display: flex; gap: 8px; align-items: center; flex-wrap: wrap; }
.date-display { display: flex; flex-direction: column; align-items: flex-start; gap: 2px; }
.date-input { padding: 8px 12px; border: 1.5px solid rgba(247,251,244,.16); border-radius: 10px; font-size: 13.5px; background: rgba(5,11,8,.4); color: var(--chalk-050); outline: none; }
.date-input:focus { border-color: var(--lime-400); }
.date-text { font-size: 11px; color: var(--lime-300); font-family: var(--font-mono); padding-left: 2px; }

.btn { display: inline-flex; align-items: center; gap: 6px; padding: 9px 16px; font-size: 13px; font-weight: 700; border-radius: 999px; border: none; cursor: pointer; transition: .18s; white-space: nowrap; }
.btn-primary { background: linear-gradient(135deg, var(--turf-500), var(--turf-700)); color: var(--chalk-050); }
.btn-primary:hover:not(:disabled) { filter: brightness(1.12); transform: translateY(-1px); }
.btn-primary:disabled { opacity: .6; cursor: not-allowed; }
.btn-ghost { background: rgba(247,251,244,.08); color: var(--chalk-200); }
.btn-ghost:hover { background: rgba(247,251,244,.14); }
.btn-outline { background: transparent; border: 1.5px solid rgba(247,251,244,.18); color: var(--chalk-200); }
.btn-outline:hover { border-color: var(--lime-400); color: var(--lime-300); }

/* ===== TOAST ===== */
.toast { position: fixed; top: 20px; right: 24px; z-index: 9999; padding: 12px 20px; border-radius: 12px; font-weight: 700; font-size: 14px; box-shadow: 0 10px 30px rgba(0,0,0,.4); }
.toast.success { background: rgba(182,255,60,.14); color: var(--lime-300); border: 1px solid rgba(182,255,60,.35); }
.toast.error { background: rgba(255,71,87,.14); color: #ff9686; border: 1px solid rgba(255,71,87,.35); }
.toast-pop-enter-active, .toast-pop-leave-active { transition: opacity .25s ease, transform .25s ease; }
.toast-pop-enter-from, .toast-pop-leave-to { opacity: 0; transform: translateY(-10px); }

/* ===== LEGEND ===== */
.legend-bar { position: relative; z-index: 1; display: flex; gap: 20px; flex-wrap: wrap; padding: 12px 16px; background: rgba(247,251,244,.03); border: 1px solid rgba(247,251,244,.09); border-radius: 12px; margin-bottom: 14px; }
.legend-item { display: flex; align-items: center; gap: 6px; font-size: 12.5px; color: var(--chalk-200); opacity: .85; }
.dot { width: 12px; height: 12px; border-radius: 4px; display: inline-block; }
.dot-empty { background: rgba(247,251,244,.08); border: 1px solid rgba(247,251,244,.2); }
.dot-dat { background: #60a5fa; }
.dot-dang { background: var(--amber-400); }
.dot-done { background: var(--lime-400); }
.dot-bao { background: var(--crimson-500); }

/* ===== FILTER ===== */
.filter-bar { position: relative; z-index: 1; display: flex; gap: 8px; margin-bottom: 18px; }
.tab-btn { padding: 8px 18px; border: 1px solid rgba(247,251,244,.16); border-radius: 999px; background: rgba(247,251,244,.03); font-size: 13px; cursor: pointer; color: var(--chalk-200); transition: .18s; }
.tab-btn:hover { border-color: rgba(182,255,60,.35); }
.tab-btn.active { background: var(--turf-500); color: var(--chalk-050); border-color: var(--turf-500); }

/* ===== COURT ROW (1 hàng) ===== */
.court-row { position: relative; z-index: 1; display: flex; gap: 10px; flex-wrap: wrap; margin-bottom: 24px; }
.court-chip {
  display: flex; align-items: center; gap: 8px; padding: 10px 16px;
  background: rgba(247,251,244,.04); border: 1.5px solid rgba(247,251,244,.12); border-radius: 999px;
  color: var(--chalk-200); font-size: 13.5px; font-weight: 600; cursor: pointer;
  transition: transform .15s ease, border-color .15s ease, background .15s ease;
}
.court-chip:hover { transform: translateY(-2px); border-color: rgba(182,255,60,.35); }
.court-chip--active { background: rgba(182,255,60,.12); border-color: var(--lime-400); color: var(--chalk-050); }
.court-chip--bao { opacity: .7; }
.court-chip__check { width: 14px; height: 14px; display: flex; align-items: center; justify-content: center; color: var(--lime-400); }
.court-chip__dot { width: 8px; height: 8px; border-radius: 50%; flex-shrink: 0; }
.court-chip__dot.is-hoat-dong { background: var(--lime-400); }
.court-chip__dot.is-bao { background: var(--crimson-500); }
.court-chip__loai { font-size: 11px; opacity: .6; font-family: var(--font-mono); }

/* ===== SCHEDULE PANEL ===== */
.schedule-panel { position: relative; z-index: 1; background: rgba(247,251,244,.03); border: 1px solid rgba(247,251,244,.09); border-radius: 18px; padding: 22px; scroll-margin-top: 20px; }
.schedule-panel__head { display: flex; align-items: flex-start; justify-content: space-between; gap: 12px; margin-bottom: 20px; flex-wrap: wrap; }
.schedule-panel__head h3 { font-family: var(--font-display); font-size: 19px; font-weight: 600; color: var(--chalk-050); margin: 0 0 4px; }
.muted { font-size: 13px; color: var(--chalk-200); opacity: .6; font-weight: 400; }
.schedule-panel__status { font-size: 12.5px; font-weight: 700; }
.schedule-panel__status.hoat-dong { color: var(--lime-300); }
.schedule-panel__status.bao-tri { color: #ff9686; }
.schedule-panel__date { font-family: var(--font-mono); font-size: 12px; color: var(--lime-300); opacity: .85; }

.slot-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(110px, 1fr)); gap: 10px; }
.slot-card {
  display: flex; flex-direction: column; align-items: flex-start; gap: 4px;
  padding: 10px 12px; border-radius: 12px; border: 1px solid transparent; cursor: pointer;
  transition: transform .15s ease, filter .15s ease; text-align: left; min-height: 58px;
}
.slot-card:hover { transform: translateY(-2px); filter: brightness(1.08); }
.slot-card__time { font-family: var(--font-mono); font-size: 12px; font-weight: 700; opacity: .85; }
.slot-card__name { font-size: 12px; font-weight: 700; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: 100%; }
.slot-card__empty { font-size: 11.5px; opacity: .55; }

.slot-empty { background: rgba(247,251,244,.04); color: var(--chalk-200); border-color: rgba(247,251,244,.1); }
.slot-dat { background: rgba(96,165,250,.16); color: #bfdbfe; border-color: rgba(96,165,250,.35); }
.slot-dang { background: rgba(255,176,32,.16); color: var(--amber-400); border-color: rgba(255,176,32,.4); }
.slot-done { background: rgba(182,255,60,.14); color: var(--lime-300); border-color: rgba(182,255,60,.35); }
.slot-bao { background: repeating-linear-gradient(45deg, rgba(255,71,87,.14), rgba(255,71,87,.14) 4px, rgba(255,71,87,.04) 4px, rgba(255,71,87,.04) 10px); color: #ff9686; border-color: rgba(255,71,87,.3); cursor: default; }

.empty-note { position: relative; z-index: 1; text-align: center; color: var(--chalk-200); opacity: .6; font-size: 14px; padding: 20px 0; }

/* ===== BOOKING PANEL (bảng lịch đã đặt) ===== */
.booking-panel { position: relative; z-index: 1; margin-top: 26px; background: rgba(247,251,244,.03); border: 1px solid rgba(247,251,244,.09); border-radius: 18px; padding: 22px; }
.booking-panel__head { display: flex; align-items: flex-start; justify-content: space-between; gap: 16px; flex-wrap: wrap; margin-bottom: 18px; }
.booking-panel__title { font-family: var(--font-display); font-size: 18px; font-weight: 600; color: var(--chalk-050); margin: 0 0 4px; }
.booking-panel__desc { font-size: 12.5px; color: var(--chalk-200); opacity: .65; margin: 0; max-width: 420px; }

.range-controls { display: flex; align-items: flex-end; gap: 10px; flex-wrap: wrap; }
.range-field { display: flex; flex-direction: column; gap: 4px; }
.range-field label { font-size: 11.5px; color: var(--chalk-200); opacity: .7; font-family: var(--font-mono); }

.state-box.small { padding: 22px; font-size: 13px; }

.booking-table-wrap { overflow-x: auto; border-radius: 12px; border: 1px solid rgba(247,251,244,.08); }
.booking-table { width: 100%; border-collapse: collapse; font-size: 13px; min-width: 640px; }
.booking-table thead th { text-align: left; padding: 10px 14px; font-size: 11.5px; text-transform: uppercase; letter-spacing: .05em; color: var(--lime-300); opacity: .85; background: rgba(182,255,60,.06); font-family: var(--font-mono); font-weight: 700; }
.booking-table tbody td { padding: 10px 14px; border-top: 1px solid rgba(247,251,244,.06); color: var(--chalk-050); vertical-align: middle; }
.booking-row { cursor: pointer; transition: background .15s ease; }
.booking-row:hover { background: rgba(247,251,244,.04); }
.booking-row--active { background: rgba(182,255,60,.1); }
.booking-row__check { width: 30px; }
.row-tick { width: 16px; height: 16px; border-radius: 5px; border: 1.5px solid rgba(247,251,244,.25); display: flex; align-items: center; justify-content: center; color: var(--lime-400); }
.row-tick--on { border-color: var(--lime-400); background: rgba(182,255,60,.14); }
.mono { font-family: var(--font-mono); font-size: 12.5px; }

/* ===== MODAL ===== */
.overlay { position: fixed; inset: 0; background: rgba(5,11,8,.75); backdrop-filter: blur(3px); display: flex; align-items: center; justify-content: center; z-index: 300; }
.modal-pop-enter-active, .modal-pop-leave-active { transition: opacity .2s ease; }
.modal-pop-enter-from, .modal-pop-leave-to { opacity: 0; }
.modal-box { background: var(--night-800); border: 1px solid rgba(182,255,60,.18); border-radius: 18px; width: 100%; max-width: 480px; box-shadow: 0 30px 60px rgba(0,0,0,.5); overflow: hidden; }
.modal-header { padding: 18px 22px; border-bottom: 1px solid rgba(247,251,244,.08); display: flex; justify-content: space-between; align-items: center; }
.modal-header h2 { font-family: var(--font-display); font-size: 17px; font-weight: 600; color: var(--chalk-050); margin: 0; }
.close-btn { background: none; border: none; font-size: 22px; color: var(--chalk-200); opacity: .6; cursor: pointer; }
.close-btn:hover { opacity: 1; }
.modal-body { padding: 20px 22px; }
.modal-footer { padding: 14px 22px; border-top: 1px solid rgba(247,251,244,.08); display: flex; justify-content: flex-end; gap: 10px; }
.form-group { display: flex; flex-direction: column; gap: 6px; margin-bottom: 14px; }
.form-label { font-size: 13px; font-weight: 600; color: var(--chalk-200); }
.form-control { padding: 9px 13px; border: 1.5px solid rgba(247,251,244,.16); border-radius: 10px; font-size: 14px; outline: none; background: rgba(5,11,8,.4); color: var(--chalk-050); }
.form-control:focus { border-color: var(--lime-400); }
textarea.form-control { resize: vertical; font-family: inherit; }
.toggle-row { display: flex; gap: 8px; }
.toggle-btn { flex: 1; padding: 9px; border: 1.5px solid rgba(247,251,244,.16); border-radius: 10px; background: rgba(247,251,244,.04); cursor: pointer; font-size: 13px; font-weight: 700; color: var(--chalk-200); transition: .18s; }
.toggle-btn.active-lock { background: rgba(255,71,87,.14); border-color: rgba(255,71,87,.4); color: #ff9686; }
.toggle-btn.active-open { background: rgba(182,255,60,.14); border-color: rgba(182,255,60,.4); color: var(--lime-300); }
.info-row { display: flex; gap: 12px; align-items: center; padding: 9px 0; border-bottom: 1px solid rgba(247,251,244,.08); font-size: 13.5px; color: var(--chalk-050); }
.info-label { font-weight: 700; color: var(--chalk-200); opacity: .7; min-width: 100px; font-size: 13px; }
.badge { display: inline-flex; padding: 3px 10px; border-radius: 999px; font-size: 12px; font-weight: 700; }
.badge-warning { background: rgba(255,176,32,.16); color: var(--amber-400); }
.badge-info { background: rgba(96,165,250,.16); color: #93c5fd; }
.badge-success { background: rgba(182,255,60,.14); color: var(--lime-300); }
.badge-danger { background: rgba(255,71,87,.16); color: #ff9686; }
.badge-playing { background: rgba(255,176,32,.16); color: var(--amber-400); }

.state-box { position: relative; z-index: 1; text-align: center; padding: 44px; color: var(--chalk-200); opacity: .8; }
.state-box.error { color: #ff9686; }

@media (max-width: 720px) {
  .page-header { flex-direction: column; align-items: stretch; }
  .header-controls { justify-content: space-between; }
  .booking-panel__head { flex-direction: column; }
}
</style>