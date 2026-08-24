<template>
  <div class="dash">
    <!-- ===== NỀN AMBIENT: đèn pha + hạt sáng bay + hạt rơi + đạo cụ bay bay ===== -->
    <div class="dash__ambient" aria-hidden="true">
      <svg class="pitch-silhouette" viewBox="0 0 800 500" preserveAspectRatio="xMidYMid slice">
        <rect x="24" y="24" width="752" height="452" rx="16" class="ps-line"/>
        <line x1="400" y1="24" x2="400" y2="476" class="ps-line"/>
        <circle cx="400" cy="250" r="92" class="ps-line"/>
        <circle cx="400" cy="250" r="3.5" class="ps-dot"/>
        <rect x="24" y="130" width="130" height="240" class="ps-line"/>
        <rect x="646" y="130" width="130" height="240" class="ps-line"/>
        <rect x="24" y="190" width="46" height="120" class="ps-line"/>
        <rect x="730" y="190" width="46" height="120" class="ps-line"/>
      </svg>

      <div class="floodlight floodlight--l"></div>
      <div class="floodlight floodlight--r"></div>

      <div class="motes"><span v-for="n in 14" :key="'m'+n" class="mote" :style="{ '--i': n }"></span></div>
      <div class="falling"><span v-for="n in 18" :key="'f'+n" class="falling__item" :style="{ '--i': n }"></span></div>

      <div class="field-props">
        <div class="prop prop--ball1"><svg viewBox="0 0 40 40"><circle cx="20" cy="20" r="18" fill="var(--chalk-050)" stroke="var(--night-950)" stroke-width="1.4"/><polygon points="20,10 26,14.5 24,21.5 16,21.5 14,14.5" fill="var(--night-950)"/></svg></div>
        <div class="prop prop--ball2"><svg viewBox="0 0 40 40"><circle cx="20" cy="20" r="18" fill="var(--chalk-050)" stroke="var(--night-950)" stroke-width="1.4"/><polygon points="20,10 26,14.5 24,21.5 16,21.5 14,14.5" fill="var(--night-950)"/></svg></div>
        <div class="prop prop--whistle"><svg viewBox="0 0 48 32" fill="none"><path d="M14 16a10 10 0 1 0 20 0 10 10 0 0 0-20 0Z" stroke="var(--lime-300)" stroke-width="1.6"/><circle cx="24" cy="16" r="3.2" fill="var(--lime-300)"/></svg></div>
        <div class="prop prop--flag"><svg viewBox="0 0 30 46" fill="none"><path d="M4 44V4" stroke="var(--lime-300)" stroke-width="1.8" stroke-linecap="round"/><path d="M4 5l20 6-20 6V5Z" fill="var(--lime-300)" opacity=".85"/></svg></div>
        <div class="prop prop--trophy"><svg viewBox="0 0 40 46" fill="none"><path d="M11 6h18v10a9 9 0 0 1-18 0V6Z" stroke="var(--amber-400)" stroke-width="1.6"/><path d="M20 25v6M13 37h14l-1.6-4.5a2 2 0 0 0-1.9-1.5h-6.9a2 2 0 0 0-1.9 1.5L13 37Z" stroke="var(--amber-400)" stroke-width="1.6" stroke-linejoin="round"/></svg></div>
        <div class="prop prop--boot"><svg viewBox="0 0 48 34" fill="none"><path d="M8 6v11.5c0 1.5.8 2.4 2 3.2l9 5.8c1 .7 2.2 1 3.4 1h16.4c1.8 0 2.9-2 1.9-3.5l-3-4.5a6 6 0 0 0-3.8-2.5l-8.4-1.7a3 3 0 0 1-2-1.4L19.5 6.6A3 3 0 0 0 17 5h-6a3 3 0 0 0-3 1Z" stroke="var(--lime-300)" stroke-width="1.6" stroke-linejoin="round"/><path d="M8 22.5h30" stroke="var(--lime-300)" stroke-width="1.6"/></svg></div>
      </div>
    </div>

    <!-- ===== HEADER ===== -->
    <div class="dash__header reveal">
      <span class="eyebrow">{{ loiChao }} · {{ ngayHomNay }}</span>
      <h1 class="dash__title">Dashboard</h1>
      <p class="dash__desc">Tổng quan về các hoạt động quản lý của hệ thống DreamLeague.</p>
    </div>

    <!-- Loading -->
    <div v-if="dangTaiTongQuan" class="loading-state reveal">
      <div class="spinner"></div>
      <p>Đang tải dữ liệu thống kê...</p>
    </div>

    <!-- Error -->
    <div v-else-if="loiTongQuan" class="error-state reveal">
      <p>⚠️ {{ loiTongQuan }}</p>
      <button class="btn btn-primary" @click="taiTongQuan">Thử lại</button>
    </div>

    <template v-else>
      <!-- ===== 3 THẺ TÓM TẮT — BẤM ĐỂ ĐI THẲNG ĐẾN TRANG QUẢN LÝ ===== -->
      <div class="stat-grid">
        <router-link to="/admin/khach-hang" class="stat-card reveal" style="--d:0s">
          <div class="stat-card__icon">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
              <circle cx="8" cy="8" r="3.5" stroke="currentColor" stroke-width="1.6" />
              <path d="M2 18c0-2.21 2.69-4 6-4s6 1.79 6 4" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
              <path d="M16 8c0 2.21 1.34 4 3 4s3-1.79 3-4" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
              <path d="M16 14c-1.8 0-3.48.4-4.66 1.12" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
            </svg>
          </div>
          <div class="stat-card__body">
            <p class="stat-card__label">Tổng khách hàng</p>
            <p class="stat-card__num">{{ formatNumber(tongQuan.tongKhachHang) }}</p>
            <p class="stat-card__trend">Xem quản lý khách hàng</p>
          </div>
          <svg class="stat-card__arrow" width="16" height="16" viewBox="0 0 24 24" fill="none"><path d="M5 12H19M19 12L13 6M19 12L13 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
        </router-link>

        <router-link to="/admin/san-bong" class="stat-card reveal" style="--d:.08s">
          <div class="stat-card__icon stat-card__icon--sky">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none"><rect x="2" y="3" width="20" height="18" rx="2" stroke="currentColor" stroke-width="1.6"/><line x1="12" y1="3" x2="12" y2="21" stroke="currentColor" stroke-width="1.6"/><circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="1.6"/></svg>
          </div>
          <div class="stat-card__body">
            <p class="stat-card__label">Sân bóng hiện tại</p>
            <p class="stat-card__num">{{ dangTaiSoSan ? '···' : tongQuan.tongSoSan }}</p>
            <p class="stat-card__trend stat-card__trend--sky">Xem quản lý sân bóng</p>
          </div>
          <svg class="stat-card__arrow" width="16" height="16" viewBox="0 0 24 24" fill="none"><path d="M5 12H19M19 12L13 6M19 12L13 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
        </router-link>

        <router-link to="/admin/doanh-thu" class="stat-card reveal" style="--d:.16s">
          <div class="stat-card__icon stat-card__icon--amber">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none"><rect x="2" y="5" width="20" height="14" rx="1.5" stroke="currentColor" stroke-width="1.6" /><path d="M2 9.5h20" stroke="currentColor" stroke-width="1.6" /></svg>
          </div>
          <div class="stat-card__body">
            <p class="stat-card__label">Doanh thu hôm nay</p>
            <p class="stat-card__num">{{ formatPrice(tongQuan.doanhThuHomNay) }}</p>
            <p class="stat-card__trend stat-card__trend--amber">Xem thống kê doanh thu</p>
          </div>
          <svg class="stat-card__arrow" width="16" height="16" viewBox="0 0 24 24" fill="none"><path d="M5 12H19M19 12L13 6M19 12L13 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
        </router-link>
      </div>

      <!-- ===== BIỂU ĐỒ DOANH THU (cột) — lọc theo ngày / tháng / năm ===== -->
      <div class="chart-section reveal" style="--d:.22s">
        <div class="chart-header">
          <div>
            <h2 class="chart-title">Tổng doanh thu</h2>
            <p class="chart-subtitle">{{ moTaBoLoc(revenueFilter) }}</p>
          </div>
          <div class="chart-controls">
            <div class="mode-switch">
              <button v-for="m in cheDoLoc" :key="'r'+m.value" class="mode-btn" :class="{ active: revenueFilter.mode === m.value }" @click="doiCheDo('revenue', m.value)">{{ m.label }}</button>
            </div>
            <select v-if="revenueFilter.mode !== 'nam'" v-model.number="revenueFilter.nam" @change="taiBieuDoDoanhThu" class="select-input">
              <option v-for="y in yearOptions" :key="y" :value="y">{{ y }}</option>
            </select>
            <select v-if="revenueFilter.mode === 'ngay'" v-model.number="revenueFilter.thang" @change="taiBieuDoDoanhThu" class="select-input">
              <option v-for="m in 12" :key="m" :value="m">Tháng {{ m }}</option>
            </select>
          </div>
        </div>
        <div class="chart-container">
          <p v-if="loiDoanhThu" class="chart-empty">⚠️ {{ loiDoanhThu }}</p>
          <p v-else-if="dangTaiDoanhThu" class="chart-empty">Đang tải biểu đồ...</p>
          <Bar v-else :data="revenueChartData" :options="revenueChartOptions" />
        </div>
      </div>

      <!-- ===== BIỂU ĐỒ LƯỢT ĐẶT SÂN (cột) — lọc theo ngày / tháng / năm ===== -->
      <div class="chart-section reveal" style="--d:.3s">
        <div class="chart-header">
          <div>
            <h2 class="chart-title">Tổng lượt đặt sân</h2>
            <p class="chart-subtitle">{{ moTaBoLoc(bookingFilter) }}</p>
          </div>
          <div class="chart-controls">
            <div class="mode-switch">
              <button v-for="m in cheDoLoc" :key="'b'+m.value" class="mode-btn" :class="{ active: bookingFilter.mode === m.value }" @click="doiCheDo('booking', m.value)">{{ m.label }}</button>
            </div>
            <select v-if="bookingFilter.mode !== 'nam'" v-model.number="bookingFilter.nam" @change="taiBieuDoDatSan" class="select-input">
              <option v-for="y in yearOptions" :key="y" :value="y">{{ y }}</option>
            </select>
            <select v-if="bookingFilter.mode === 'ngay'" v-model.number="bookingFilter.thang" @change="taiBieuDoDatSan" class="select-input">
              <option v-for="m in 12" :key="m" :value="m">Tháng {{ m }}</option>
            </select>
          </div>
        </div>
        <div class="chart-container">
          <p v-if="loiDatSan" class="chart-empty">⚠️ {{ loiDatSan }}</p>
          <p v-else-if="dangTaiDatSan" class="chart-empty">Đang tải biểu đồ...</p>
          <Bar v-else :data="bookingChartData" :options="bookingChartOptions" />
        </div>
      </div>

      <!-- ===== ĐIỀU HƯỚNG NHANH — đầy đủ chức năng như sidebar admin ===== -->
      <div class="quicknav">
        <div v-for="(nhom, ni) in duongDanQuanLy" :key="nhom.nhom" class="quicknav__group reveal" :style="{ '--d': (ni * 0.08 + .38) + 's' }">
          <p class="quicknav__group-title">{{ nhom.nhom }}</p>
          <div class="quicknav__grid">
            <router-link v-for="item in nhom.items" :key="item.to" :to="item.to" class="quicknav__card" :class="{ 'quicknav__card--active': item.active }">
              <div class="quicknav__icon" v-html="item.icon"></div>
              <span class="quicknav__label">{{ item.label }}</span>
              <svg class="quicknav__arrow" width="15" height="15" viewBox="0 0 24 24" fill="none"><path d="M5 12H19M19 12L13 6M19 12L13 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
            </router-link>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { Bar } from 'vue-chartjs'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
} from 'chart.js'
import thongKeService from '../../services/thongKeService'

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend)

/* ============ LỜI CHÀO / NGÀY ============ */
const ngayHomNay = computed(() =>
  new Date().toLocaleDateString('vi-VN', { weekday: 'long', day: '2-digit', month: '2-digit', year: 'numeric' })
)
const loiChao = computed(() => {
  const gio = new Date().getHours()
  if (gio < 11) return 'Chào buổi sáng'
  if (gio < 14) return 'Chào buổi trưa'
  if (gio < 18) return 'Chào buổi chiều'
  return 'Chào buổi tối'
})

/* ============ 3 THẺ TÓM TẮT (dữ liệu thật) ============ */
const currentYear = new Date().getFullYear()
const yearOptions = [currentYear, currentYear - 1, currentYear - 2, currentYear - 3, currentYear - 4]

const tongQuan = reactive({ tongKhachHang: 0, doanhThuHomNay: 0, tongSoSan: 0 })
const dangTaiTongQuan = ref(true)
const loiTongQuan = ref('')
const dangTaiSoSan = ref(true)

async function taiTongQuan() {
  dangTaiTongQuan.value = true
  loiTongQuan.value = ''
  try {
    const payload = await thongKeService.layDashboard(currentYear)
    tongQuan.tongKhachHang = payload.tongKhachHang ?? 0
    tongQuan.doanhThuHomNay = payload.doanhThuHomNay ?? 0
    // Lưu luôn vào cache năm hiện tại để 2 biểu đồ phía dưới dùng lại, khỏi gọi API 2 lần
    yearCache[currentYear] = payload
  } catch (e) {
    loiTongQuan.value = e.message || 'Không thể tải dữ liệu thống kê'
  } finally {
    dangTaiTongQuan.value = false
  }
}

async function taiSoSan() {
  dangTaiSoSan.value = true
  try {
    // Dùng chung endpoint REST /api/san-bong như trang chủ đang dùng để lấy danh sách sân,
    // tránh phụ thuộc vào một file service riêng có thể chưa tồn tại trong dự án.
    const res = await fetch('/api/san-bong')
    if (res.ok) {
      const ds = await res.json()
      tongQuan.tongSoSan = Array.isArray(ds) ? ds.length : 0
    } else {
      tongQuan.tongSoSan = 0
    }
  } catch (e) {
    tongQuan.tongSoSan = 0
  } finally {
    dangTaiSoSan.value = false
  }
}

/* ============ CACHE DỮ LIỆU THEO NĂM (tránh gọi API trùng lặp) ============ */
const yearCache = reactive({})
async function layDuLieuNam(nam) {
  if (yearCache[nam]) return yearCache[nam]
  const payload = await thongKeService.layDashboard(nam)
  yearCache[nam] = payload
  return payload
}

/* ============ BỘ LỌC CHO 2 BIỂU ĐỒ: NGÀY / THÁNG / NĂM ============ */
const cheDoLoc = [
  { value: 'ngay', label: 'Theo ngày' },
  { value: 'thang', label: 'Theo tháng' },
  { value: 'nam', label: 'Theo năm' }
]
const currentMonth = new Date().getMonth() + 1

const revenueFilter = reactive({ mode: 'thang', nam: currentYear, thang: currentMonth })
const bookingFilter = reactive({ mode: 'thang', nam: currentYear, thang: currentMonth })

function moTaBoLoc(filter) {
  if (filter.mode === 'ngay') return `Theo từng ngày trong tháng ${filter.thang}/${filter.nam}`
  if (filter.mode === 'nam') return `Theo từng năm, ${yearOptions.length} năm gần nhất`
  return `Theo từng tháng trong năm ${filter.nam}`
}

function doiCheDo(loai, mode) {
  const filter = loai === 'revenue' ? revenueFilter : bookingFilter
  filter.mode = mode
  if (loai === 'revenue') taiBieuDoDoanhThu()
  else taiBieuDoDatSan()
}

/* ============ BIỂU ĐỒ DOANH THU ============ */
const dangTaiDoanhThu = ref(true)
const loiDoanhThu = ref('')
const nhanDoanhThu = ref([])
const giaTriDoanhThu = ref([])

async function taiBieuDoDoanhThu() {
  dangTaiDoanhThu.value = true
  loiDoanhThu.value = ''
  try {
    if (revenueFilter.mode === 'thang') {
      const payload = await layDuLieuNam(revenueFilter.nam)
      nhanDoanhThu.value = (payload.doanhThuTheoThang || []).map(i => i.nhan)
      giaTriDoanhThu.value = (payload.doanhThuTheoThang || []).map(i => Number(i.giaTri))
    } else if (revenueFilter.mode === 'nam') {
      const payloads = await Promise.all(yearOptions.map(y => layDuLieuNam(y)))
      nhanDoanhThu.value = yearOptions.map(y => String(y)).reverse()
      giaTriDoanhThu.value = payloads
        .map(p => (p.doanhThuTheoThang || []).reduce((s, i) => s + Number(i.giaTri || 0), 0))
        .reverse()
    } else {
      // Theo ngày: cần API trả doanh thu theo từng ngày trong 1 tháng.
      // Nếu backend đã có sẵn hàm này, đổi tên cho khớp là chạy được ngay.
      if (typeof thongKeService.layDoanhThuTheoNgay !== 'function') {
        throw new Error('Backend chưa có API thống kê doanh thu theo ngày (thongKeService.layDoanhThuTheoNgay).')
      }
      const ds = await thongKeService.layDoanhThuTheoNgay(revenueFilter.nam, revenueFilter.thang)
      nhanDoanhThu.value = (ds || []).map(i => i.nhan)
      giaTriDoanhThu.value = (ds || []).map(i => Number(i.giaTri))
    }
  } catch (e) {
    loiDoanhThu.value = e.message || 'Không tải được biểu đồ doanh thu'
  } finally {
    dangTaiDoanhThu.value = false
  }
}

const revenueChartData = computed(() => ({
  labels: nhanDoanhThu.value,
  datasets: [{
    label: 'Doanh thu (VNĐ)',
    data: giaTriDoanhThu.value,
    backgroundColor: 'rgba(182,255,60,.55)',
    hoverBackgroundColor: 'rgba(182,255,60,.85)',
    borderRadius: 6,
    borderSkipped: false,
    maxBarThickness: 42
  }]
}))

const revenueChartOptions = computed(() => ({
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: { display: false },
    tooltip: {
      backgroundColor: 'rgba(5,11,8,.92)',
      titleColor: '#d3ff8f',
      bodyColor: '#f7fbf4',
      padding: 12,
      cornerRadius: 8,
      displayColors: false,
      callbacks: { label: ctx => formatPrice(ctx.parsed.y) }
    }
  },
  scales: {
    x: { grid: { display: false }, ticks: { color: 'rgba(247,251,244,.6)', font: { size: 11.5 } } },
    y: {
      beginAtZero: true,
      grid: { color: 'rgba(247,251,244,.08)' },
      ticks: {
        color: 'rgba(247,251,244,.6)',
        font: { size: 11.5 },
        callback: val => {
          if (val >= 1000000) return (val / 1000000).toFixed(1) + 'M'
          if (val >= 1000) return (val / 1000).toFixed(0) + 'K'
          return val
        }
      }
    }
  }
}))

/* ============ BIỂU ĐỒ LƯỢT ĐẶT SÂN ============ */
const dangTaiDatSan = ref(true)
const loiDatSan = ref('')
const nhanDatSan = ref([])
const giaTriDatSan = ref([])

async function taiBieuDoDatSan() {
  dangTaiDatSan.value = true
  loiDatSan.value = ''
  try {
    if (bookingFilter.mode === 'thang') {
      const payload = await layDuLieuNam(bookingFilter.nam)
      nhanDatSan.value = (payload.datSanTheoThang || []).map(i => i.nhan)
      giaTriDatSan.value = (payload.datSanTheoThang || []).map(i => Number(i.giaTri))
    } else if (bookingFilter.mode === 'nam') {
      const payloads = await Promise.all(yearOptions.map(y => layDuLieuNam(y)))
      nhanDatSan.value = yearOptions.map(y => String(y)).reverse()
      giaTriDatSan.value = payloads
        .map(p => (p.datSanTheoThang || []).reduce((s, i) => s + Number(i.giaTri || 0), 0))
        .reverse()
    } else {
      // Theo ngày: cần API trả lượt đặt sân theo từng ngày trong 1 tháng.
      if (typeof thongKeService.layDatSanTheoNgay !== 'function') {
        throw new Error('Backend chưa có API thống kê lượt đặt sân theo ngày (thongKeService.layDatSanTheoNgay).')
      }
      const ds = await thongKeService.layDatSanTheoNgay(bookingFilter.nam, bookingFilter.thang)
      nhanDatSan.value = (ds || []).map(i => i.nhan)
      giaTriDatSan.value = (ds || []).map(i => Number(i.giaTri))
    }
  } catch (e) {
    loiDatSan.value = e.message || 'Không tải được biểu đồ lượt đặt sân'
  } finally {
    dangTaiDatSan.value = false
  }
}

const bookingChartData = computed(() => ({
  labels: nhanDatSan.value,
  datasets: [{
    label: 'Lượt đặt sân',
    data: giaTriDatSan.value,
    backgroundColor: 'rgba(56,189,248,.55)',
    hoverBackgroundColor: 'rgba(56,189,248,.85)',
    borderRadius: 6,
    borderSkipped: false,
    maxBarThickness: 42
  }]
}))

const bookingChartOptions = computed(() => ({
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: { display: false },
    tooltip: {
      backgroundColor: 'rgba(5,11,8,.92)',
      titleColor: '#7dd3fc',
      bodyColor: '#f7fbf4',
      padding: 12,
      cornerRadius: 8,
      displayColors: false,
      callbacks: { label: ctx => ctx.parsed.y + ' lượt đặt' }
    }
  },
  scales: {
    x: { grid: { display: false }, ticks: { color: 'rgba(247,251,244,.6)', font: { size: 11.5 } } },
    y: { beginAtZero: true, grid: { color: 'rgba(247,251,244,.08)' }, ticks: { color: 'rgba(247,251,244,.6)', font: { size: 11.5 }, stepSize: 1 } }
  }
}))

/* ============ ĐIỀU HƯỚNG NHANH — toàn bộ chức năng như sidebar admin ============ */
const duongDanQuanLy = [
  {
    nhom: 'Quản lý',
    items: [
      { to: '/admin', label: 'Dashboard', active: true,
        icon: `<svg width="19" height="19" viewBox="0 0 24 24" fill="none"><rect x="3" y="3" width="8" height="8" rx="1" stroke="currentColor" stroke-width="1.7"/><rect x="13" y="3" width="8" height="8" rx="1" stroke="currentColor" stroke-width="1.7"/><rect x="3" y="13" width="8" height="8" rx="1" stroke="currentColor" stroke-width="1.7"/><rect x="13" y="13" width="8" height="8" rx="1" stroke="currentColor" stroke-width="1.7"/></svg>` },
      { to: '/admin/khach-hang', label: 'Quản lý khách hàng',
        icon: `<svg width="19" height="19" viewBox="0 0 24 24" fill="none"><circle cx="9" cy="8" r="3.2" stroke="currentColor" stroke-width="1.7"/><path d="M3 20c0-3.3 2.7-5.5 6-5.5s6 2.2 6 5.5" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/><path d="M16 8.5c1.4 0 2.5 1.1 2.5 2.5S17.4 13.5 16 13.5" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/><path d="M17 14.8c2 .4 3.5 1.9 3.5 4" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/></svg>` },
      { to: '/admin/nhan-vien', label: 'Quản lý nhân viên',
        icon: `<svg width="19" height="19" viewBox="0 0 24 24" fill="none"><circle cx="9" cy="8" r="3.2" stroke="currentColor" stroke-width="1.7"/><path d="M3 20c0-3.3 2.7-5.5 6-5.5s6 2.2 6 5.5" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/><path d="M15 9l1.6 1.6L20 7" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round"/></svg>` },
      { to: '/admin/san-bong', label: 'Quản lý sân bóng',
        icon: `<svg width="19" height="19" viewBox="0 0 24 24" fill="none"><rect x="2" y="3" width="20" height="18" rx="2" stroke="currentColor" stroke-width="1.7"/><line x1="12" y1="3" x2="12" y2="21" stroke="currentColor" stroke-width="1.7"/><circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="1.7"/></svg>` }
    ]
  },
  {
    nhom: 'Kinh doanh',
    items: [
      { to: '/admin/san-pham', label: 'Quản lý dịch vụ',
        icon: `<svg width="19" height="19" viewBox="0 0 24 24" fill="none"><path d="M5 8h14l-1.4 10.3a2 2 0 0 1-2 1.7H8.4a2 2 0 0 1-2-1.7L5 8Z" stroke="currentColor" stroke-width="1.7" stroke-linejoin="round"/><path d="M9 8V6a3 3 0 0 1 6 0v2" stroke="currentColor" stroke-width="1.7"/></svg>` },
      { to: '/admin/voucher', label: 'Quản lý Voucher',
        icon: `<svg width="19" height="19" viewBox="0 0 24 24" fill="none"><path d="M20 12a2 2 0 0 0 0-4V5a2 2 0 0 0-2-2H6a2 2 0 0 0 0 4 2 2 0 0 0 0 4 2 2 0 0 0 0 4 2 2 0 0 0 0 4h12a2 2 0 0 0 2-2v-3a2 2 0 0 0 0-4Z" stroke="currentColor" stroke-width="1.7"/></svg>` },
      { to: '/admin/doanh-thu', label: 'Thống kê & doanh thu',
        icon: `<svg width="19" height="19" viewBox="0 0 24 24" fill="none"><path d="M4 19V10M10 19V5M16 19v-7M22 19H2" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/></svg>` }
    ]
  },
  {
    nhom: 'Nội dung',
    items: [
      { to: '/admin/thong-bao', label: 'Quản lý thông báo',
        icon: `<svg width="19" height="19" viewBox="0 0 24 24" fill="none"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" stroke="currentColor" stroke-width="1.7"/><path d="M13.73 21a2 2 0 0 1-3.46 0" stroke="currentColor" stroke-width="1.7"/></svg>` },
      { to: '/admin/danh-gia', label: 'Quản lý đánh giá',
        icon: `<svg width="19" height="19" viewBox="0 0 24 24" fill="none"><path d="M12 2.5l3 6.4 6.7.7-5 4.6 1.4 6.8L12 17.7 5.9 21l1.4-6.8-5-4.6 6.7-.7 3-6.4Z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/></svg>` },
      { to: '/admin/danh-gia-he-thong', label: 'Q.Lý đánh giá hệ thống',
        icon: `<svg width="19" height="19" viewBox="0 0 24 24" fill="none"><path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round"/></svg>` },
      { to: '/admin/bai-viet', label: 'Quản lý bài viết',
        icon: `<svg width="19" height="19" viewBox="0 0 24 24" fill="none"><path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z" fill="currentColor"/></svg>` }
    ]
  },
  {
    nhom: 'Khách hàng',
    items: [
      { to: '/admin/quan-ly-support', label: 'Hỗ trợ khách hàng',
        icon: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none"><path d="M3 10a7 7 0 1 1 14 0 7 7 0 0 1-14 0Zm3 7l-3 3c-.3.3 0 .7.3.7h3.7l4 4c.3.3.7 0 .7-.3v-4.4" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"/></svg>` }
    ]
  },
  {
    nhom: 'Tài khoản',
    items: [
      { to: '/admin/ca-nhan', label: 'Thông tin cá nhân',
        icon: `<svg width="19" height="19" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="8" r="3.5" stroke="currentColor" stroke-width="1.7"/><path d="M4 20c0-4 3.6-7 8-7s8 3 8 7" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/></svg>` },
      { to: '/', label: 'Quay lại trang chủ',
        icon: `<svg width="19" height="19" viewBox="0 0 24 24" fill="none"><path d="M19 12H5M5 12l7 7M5 12l7-7" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round"/></svg>` }
    ]
  }
]

/* ============ HELPERS ============ */
function formatPrice(value) {
  if (!value) return '0đ'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value).replace('₫', 'đ')
}
function formatNumber(value) {
  if (!value) return '0'
  return new Intl.NumberFormat('vi-VN').format(value)
}

/* ============ SCROLL REVEAL ============ */
let revealObserver = null

onMounted(async () => {
  revealObserver = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('is-visible')
        revealObserver.unobserve(entry.target)
      }
    })
  }, { threshold: 0.12 })
  nextTick(() => document.querySelectorAll('.reveal').forEach(el => revealObserver.observe(el)))

  await taiTongQuan()
  taiSoSan()
  taiBieuDoDoanhThu()
  taiBieuDoDatSan()
})

onUnmounted(() => {
  if (revealObserver) revealObserver.disconnect()
})
</script>

<style scoped>
.dash {
  --night-950: #050b08;
  --night-800: #0a1f13;
  --night-700: #123321;
  --turf-500: #23935a;
  --turf-700: #146239;
  --lime-400: #b6ff3c;
  --lime-300: #d3ff8f;
  --sky-400: #38bdf8;
  --sky-300: #7dd3fc;
  --amber-400: #ffb020;
  --crimson-500: #ff4757;
  --chalk-050: #f7fbf4;
  --chalk-200: #e3ecdf;
  --font-display: 'Oswald', 'Manrope', sans-serif;
  --font-body: 'Manrope', sans-serif;
  --font-mono: 'Space Mono', monospace;

  position: relative;
  background: var(--night-950);
  color: var(--chalk-050);
  font-family: var(--font-body);
  border-radius: 20px;
  padding: 32px;
  overflow: hidden;
  min-height: calc(100vh - 40px);
}

/* ===== REVEAL ===== */
.reveal { opacity: 0; transform: translateY(18px); transition: opacity .6s ease, transform .6s ease; transition-delay: var(--d, 0s); }
.reveal.is-visible { opacity: 1; transform: translateY(0); }
@media (prefers-reduced-motion: reduce) { .reveal { opacity: 1; transform: none; transition: none; } }

/* ===== AMBIENT ===== */
.dash__ambient { position: absolute; inset: 0; z-index: 0; pointer-events: none; border-radius: 20px; overflow: hidden; }
.floodlight { position: absolute; top: -20%; width: 40vh; height: 100vh; background: conic-gradient(from 90deg at 50% 0%, transparent 42%, rgba(255,244,214,.07) 50%, transparent 58%); mix-blend-mode: screen; animation: sweep 11s ease-in-out infinite alternate; }
.floodlight--l { left: -6%; }
.floodlight--r { right: -6%; animation-direction: alternate-reverse; }
@keyframes sweep { 0% { transform: rotate(-8deg); } 100% { transform: rotate(8deg); } }
.motes { position: absolute; inset: 0; }
.mote { position: absolute; bottom: -10px; left: calc((var(--i) * 8.4%) + 1%); width: 3px; height: 3px; border-radius: 50%; background: var(--lime-300); opacity: 0; animation: rise 9s linear infinite; animation-delay: calc(var(--i) * -0.7s); }
@keyframes rise { 0% { transform: translateY(0) scale(.6); opacity: 0; } 10% { opacity: .5; } 90% { opacity: .1; } 100% { transform: translateY(-100%) scale(1.1); opacity: 0; } }

.pitch-silhouette { position: absolute; inset: 0; width: 100%; height: 100%; opacity: .07; }
.ps-line { fill: none; stroke: var(--lime-300); stroke-width: 2; }
.ps-dot { fill: var(--lime-300); }

.falling { position: absolute; inset: 0; overflow: hidden; }
.falling__item {
  position: absolute; top: -6%; left: calc((var(--i) * 5.6%) + 1%);
  width: 3px; height: 3px; border-radius: 50%; background: var(--chalk-050);
  opacity: 0; animation: fall linear infinite;
  animation-duration: calc(7s + (var(--i) * 0.45s));
  animation-delay: calc(var(--i) * -0.6s);
}
@keyframes fall {
  0% { transform: translateY(0) translateX(0); opacity: 0; }
  8% { opacity: .55; }
  50% { transform: translateY(160px) translateX(14px); }
  92% { opacity: .12; }
  100% { transform: translateY(340px) translateX(-10px); opacity: 0; }
}

.field-props { position: absolute; inset: 0; }
.prop { position: absolute; opacity: .13; animation: prop-float 7s ease-in-out infinite; }
.prop svg { width: 100%; height: 100%; }
.prop--ball1 { width: 46px; height: 46px; top: 10%; right: 8%; }
.prop--ball2 { width: 26px; height: 26px; bottom: 18%; left: 6%; opacity: .1; animation-delay: .9s; animation-duration: 8.5s; }
.prop--whistle { width: 56px; height: 38px; top: 62%; right: 4%; animation-delay: .5s; }
.prop--flag { width: 32px; height: 48px; top: 30%; left: 3%; animation-delay: 1.1s; }
.prop--trophy { width: 40px; height: 46px; bottom: 8%; right: 22%; animation-delay: 1.6s; }
.prop--boot { width: 60px; height: 42px; top: 6%; left: 22%; opacity: .1; animation-delay: 2s; animation-duration: 9s; }
@keyframes prop-float { 0%, 100% { transform: translateY(0) rotate(-3deg); } 50% { transform: translateY(-16px) rotate(3deg); } }

@media (prefers-reduced-motion: reduce) {
  .floodlight, .mote, .falling__item, .prop { animation: none; }
}

/* ===== HEADER ===== */
.dash__header { position: relative; z-index: 1; margin-bottom: 28px; }
.eyebrow { display: inline-block; font-family: var(--font-mono); font-size: 12px; letter-spacing: .14em; text-transform: uppercase; color: var(--lime-300); }
.dash__title { font-family: var(--font-display); font-size: 28px; font-weight: 600; color: var(--chalk-050); margin: 8px 0 4px; }
.dash__desc { font-size: 14px; color: var(--chalk-200); opacity: .8; margin: 0; }

/* ===== LOADING / ERROR ===== */
.loading-state, .error-state {
  position: relative; z-index: 1; display: flex; flex-direction: column; align-items: center; justify-content: center;
  padding: 60px 24px; color: var(--chalk-200); text-align: center;
}
.spinner { width: 36px; height: 36px; border: 3px solid rgba(247,251,244,.15); border-top-color: var(--lime-400); border-radius: 50%; animation: spin .8s linear infinite; margin-bottom: 16px; }
@keyframes spin { to { transform: rotate(360deg); } }
.error-state { background: rgba(255,71,87,.08); border: 1px dashed rgba(255,71,87,.4); border-radius: 12px; color: #ff9686; }
.btn { display: inline-flex; align-items: center; justify-content: center; padding: 10px 22px; font-size: 14px; font-weight: 600; border-radius: 999px; cursor: pointer; border: none; margin-top: 14px; transition: all .2s ease; }
.btn-primary { background: var(--turf-500); color: var(--chalk-050); }
.btn-primary:hover { background: var(--lime-400); color: var(--night-950); }

/* ===== STAT CARDS (liên kết) ===== */
.stat-grid { position: relative; z-index: 1; display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; }
@media (max-width: 900px) { .stat-grid { grid-template-columns: 1fr; } }

.stat-card {
  position: relative;
  display: flex; align-items: flex-start; gap: 14px;
  background: rgba(247,251,244,.04); border: 1px solid rgba(247,251,244,.1); border-radius: 16px;
  padding: 20px; text-decoration: none; color: inherit;
  transition: transform .2s ease, box-shadow .2s ease, border-color .2s ease;
  overflow: hidden;
}
.stat-card::before {
  content: ''; position: absolute; left: 0; top: 0; bottom: 0; width: 3px;
  background: var(--lime-400); transform: scaleY(0); transition: transform .2s ease;
}
.stat-card:hover { transform: translateY(-4px); border-color: rgba(182,255,60,.35); box-shadow: 0 14px 30px -16px rgba(182,255,60,.25); }
.stat-card:hover::before { transform: scaleY(1); }
.stat-card:hover .stat-card__arrow { opacity: 1; transform: translateX(0); }

.stat-card__icon { width: 42px; height: 42px; border-radius: 10px; background: rgba(182,255,60,.12); color: var(--lime-300); display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.stat-card__icon--sky { background: rgba(56,189,248,.14); color: var(--sky-300); }
.stat-card__icon--amber { background: rgba(255,176,32,.12); color: var(--amber-400); }
.stat-card__body { flex: 1; min-width: 0; }
.stat-card__label { font-size: 11.5px; font-weight: 700; text-transform: uppercase; letter-spacing: .05em; color: var(--chalk-200); opacity: .65; margin: 0 0 6px; }
.stat-card__num { font-family: var(--font-display); font-size: 24px; font-weight: 600; color: var(--chalk-050); letter-spacing: -.01em; margin: 0 0 4px; }
.stat-card__trend { font-size: 12.5px; font-weight: 600; color: var(--lime-300); margin: 0; }
.stat-card__trend--sky { color: var(--sky-300); }
.stat-card__trend--amber { color: var(--amber-400); }
.stat-card__arrow { align-self: center; color: var(--lime-300); opacity: 0; transform: translateX(-4px); transition: opacity .18s ease, transform .18s ease; flex-shrink: 0; }

/* ===== CHART SECTIONS ===== */
.chart-section { position: relative; z-index: 1; margin-top: 22px; background: rgba(247,251,244,.03); border: 1px solid rgba(247,251,244,.09); border-radius: 16px; padding: 22px; }
.chart-header { display: flex; align-items: flex-start; justify-content: space-between; gap: 16px; margin-bottom: 18px; flex-wrap: wrap; }
.chart-title { font-family: var(--font-display); font-size: 18px; font-weight: 600; color: var(--chalk-050); margin: 0 0 4px; }
.chart-subtitle { font-size: 12.5px; color: var(--chalk-200); opacity: .7; margin: 0; }

.chart-controls { display: flex; align-items: center; gap: 10px; flex-wrap: wrap; }
.mode-switch { display: flex; background: rgba(247,251,244,.05); border: 1px solid rgba(247,251,244,.12); border-radius: 999px; padding: 3px; }
.mode-btn { border: none; background: none; color: var(--chalk-200); font-size: 12.5px; font-weight: 600; padding: 6px 14px; border-radius: 999px; cursor: pointer; transition: background .18s ease, color .18s ease; }
.mode-btn.active { background: var(--turf-500); color: var(--chalk-050); }
.select-input { padding: 7px 12px; border-radius: 8px; border: 1px solid rgba(247,251,244,.15); background: rgba(5,11,8,.5); color: var(--chalk-050); font-size: 13px; cursor: pointer; outline: none; }
.select-input:focus { border-color: var(--lime-400); }

.chart-container { position: relative; height: 300px; }
.chart-empty { display: flex; align-items: center; justify-content: center; height: 100%; color: var(--chalk-200); opacity: .7; font-size: 13.5px; text-align: center; padding: 0 20px; }

/* ===== QUICK NAV ===== */
.quicknav { position: relative; z-index: 1; margin-top: 34px; display: flex; flex-direction: column; gap: 24px; }
.quicknav__group-title { font-family: var(--font-mono); font-size: 11.5px; font-weight: 700; letter-spacing: .1em; text-transform: uppercase; color: var(--lime-300); opacity: .9; margin: 0 0 12px; }
.quicknav__grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 12px; }

.quicknav__card {
  display: flex; align-items: center; gap: 12px;
  background: rgba(247,251,244,.03); border: 1px solid rgba(247,251,244,.09); border-radius: 14px;
  padding: 14px 16px; text-decoration: none; color: var(--chalk-050);
  transition: transform .18s ease, box-shadow .18s ease, border-color .18s ease, background .18s ease;
  position: relative; overflow: hidden;
}
.quicknav__card::before { content: ''; position: absolute; left: 0; top: 0; bottom: 0; width: 3px; background: var(--lime-400); transform: scaleY(0); transition: transform .18s ease; }
.quicknav__card:hover { transform: translateY(-3px); border-color: rgba(182,255,60,.35); background: rgba(182,255,60,.05); box-shadow: 0 14px 28px -16px rgba(0,0,0,.5); }
.quicknav__card:hover::before { transform: scaleY(1); }
.quicknav__card:hover .quicknav__arrow { opacity: 1; transform: translateX(0); }
.quicknav__card:hover .quicknav__icon { background: var(--turf-500); color: var(--chalk-050); }
.quicknav__card--active { border-color: rgba(182,255,60,.45); background: rgba(182,255,60,.08); }
.quicknav__card--active::before { transform: scaleY(1); }
.quicknav__card--active .quicknav__icon { background: var(--lime-400); color: var(--night-950); }
.quicknav__card--active .quicknav__label { color: var(--lime-300); font-weight: 700; }

.quicknav__icon { width: 36px; height: 36px; border-radius: 10px; flex-shrink: 0; background: rgba(247,251,244,.07); color: var(--chalk-200); display: flex; align-items: center; justify-content: center; transition: background .18s ease, color .18s ease; }
.quicknav__label { flex: 1; font-size: 13.5px; font-weight: 600; }
.quicknav__arrow { color: var(--lime-300); opacity: 0; transform: translateX(-4px); transition: opacity .18s ease, transform .18s ease; flex-shrink: 0; }
</style>