<template>
  <div class="tkdt">

    <!-- ===== HEADER ===== -->
    <div class="page-header">
      <div>
        <span class="eyebrow">⚽ Bảng điều khiển · Admin</span>
        <h1 class="page-title">Thống kê doanh thu</h1>
        <p class="page-desc">Xem báo cáo doanh thu, số lượng đơn đặt và hiệu suất khai thác sân lưới.</p>
      </div>
    </div>

    <!-- ===== BỘ LỌC ===== -->
    <div class="filter-card">
      <div class="filter-top">
        <div class="pill-select">
          <label>Tháng</label>
          <select v-model="selectedMonth" class="form-control" @change="loadData">
            <option :value="null">Tất cả các tháng</option>
            <option v-for="m in 12" :key="m" :value="m">Tháng {{ m }}</option>
          </select>
        </div>

        <div class="pill-select">
          <label>Năm</label>
          <select v-model="selectedYear" class="form-control" @change="loadData">
            <option v-for="y in yearOptions" :key="y" :value="y">{{ y }}</option>
          </select>
        </div>

        <button class="btn-primary btn-export" @click="xuatExcel" :disabled="loading || !data.chiTietTheoSan">
          <span>📥</span> Xuất Excel
        </button>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="state-box"><span class="spinner"></span> Đang tải dữ liệu thống kê...</div>

    <!-- Error -->
    <div v-else-if="error" class="state-box error">
      <p>⚠ {{ error }}</p>
      <button class="btn-ghost" @click="loadData">Thử lại</button>
    </div>

    <template v-else>
      <!-- ===== 4 STAT CARDS ===== -->
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-card__icon icon-success">💰</div>
          <div class="stat-card__body">
            <span class="stat-card__title">Tổng doanh thu</span>
            <span class="stat-card__value text-success">{{ formatPrice(data.tongDoanhThu) }}</span>
            <span class="stat-card__desc">Từ các đơn đã hoàn thành</span>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-card__icon icon-done">✅</div>
          <div class="stat-card__body">
            <span class="stat-card__title">Đơn hoàn thành</span>
            <span class="stat-card__value text-done">{{ data.donHoanThanh }}</span>
            <span class="stat-card__desc">Đã thanh toán &amp; sử dụng sân</span>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-card__icon icon-cancel">✕</div>
          <div class="stat-card__body">
            <span class="stat-card__title">Đơn bị hủy</span>
            <span class="stat-card__value text-cancel">{{ data.donDaHuy }}</span>
            <span class="stat-card__desc">Tỷ lệ hủy: {{ data.tyLeHuy }}%</span>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-card__icon icon-waiting">📋</div>
          <div class="stat-card__body">
            <span class="stat-card__title">Tổng đơn</span>
            <span class="stat-card__value text-waiting">{{ data.donHoanThanh + data.donDaHuy }}</span>
            <span class="stat-card__desc">Hoàn thành + Đã hủy</span>
          </div>
        </div>
      </div>

      <!-- ===== BIỂU ĐỒ DOANH THU ===== -->
      <div class="chart-section">
        <div class="chart-header">
          <div>
            <span class="eyebrow">Biểu đồ</span>
            <h2 class="chart-title">
              {{ selectedMonth ? `Doanh thu theo ngày — Tháng ${selectedMonth}/${selectedYear}` : `Doanh thu theo tháng — Năm ${selectedYear}` }}
            </h2>
            <p class="chart-subtitle">
              {{ selectedMonth ? 'Biểu đồ doanh thu từng ngày trong tháng' : 'Biểu đồ doanh thu từng tháng trong năm' }}
            </p>
          </div>
        </div>
        <div class="chart-container">
          <Line :data="revenueChartData" :options="revenueChartOptions" :key="chartKey" />
        </div>
      </div>

      <!-- ===== BẢNG CHI TIẾT THEO SÂN ===== -->
      <div class="section-head">
        <span class="eyebrow">Chi tiết</span>
        <h2 class="section-title">Doanh thu chi tiết theo sân</h2>
      </div>
      <div class="table-card">
        <div class="table-responsive">
          <table class="data-table">
            <thead>
              <tr>
                <th>Tên sân</th>
                <th>Số lượt đặt</th>
                <th>Số giờ đá</th>
                <th>Doanh thu</th>
                <th>Tỷ lệ đóng góp</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="field in data.chiTietTheoSan" :key="field.tenSan">
                <td><strong class="bold">{{ field.tenSan }}</strong></td>
                <td><span class="sub">{{ field.soLuotDat }} lượt</span></td>
                <td><span class="sub">{{ field.soGio.toFixed(1) }} giờ</span></td>
                <td><strong class="price">{{ formatPrice(field.doanhThu) }}</strong></td>
                <td>
                  <div class="progress-wrap">
                    <div class="progress-bar"><div class="progress-fill" :style="{ width: getContributionRate(field.doanhThu) + '%' }"></div></div>
                    <span class="progress-text">{{ getContributionRate(field.doanhThu) }}%</span>
                  </div>
                </td>
              </tr>
              <tr v-if="!data.chiTietTheoSan || data.chiTietTheoSan.length === 0">
                <td colspan="5" class="no-data">
                  <div class="no-data__icon">📊</div>
                  Không có dữ liệu thống kê cho thời gian này.
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue"
import { Line } from 'vue-chartjs'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler
} from 'chart.js'
import ExcelJS from 'exceljs'
import thongKeService from '../../services/thongKeService'

// Đăng ký Chart.js components
ChartJS.register(
  CategoryScale, LinearScale, PointElement, LineElement,
  Title, Tooltip, Legend, Filler
)

// ── State ──
const loading = ref(true)
const error = ref(null)
const chartKey = ref(0) // Force re-render chart khi data thay đổi

const currentYear = new Date().getFullYear()
const selectedMonth = ref(new Date().getMonth() + 1) // Tháng hiện tại
const selectedYear = ref(currentYear)
const yearOptions = [currentYear, currentYear - 1, currentYear - 2]

const data = ref({
  tongDoanhThu: 0,
  donHoanThanh: 0,
  donDaHuy: 0,
  tyLeHuy: 0,
  doanhThuTheoNgay: [],
  chiTietTheoSan: []
})

// ── Load data từ API ──
async function loadData() {
  loading.value = true
  error.value = null
  try {
    data.value = await thongKeService.layDoanhThu(
      selectedYear.value,
      selectedMonth.value
    )
    chartKey.value++ // Force chart re-render
  } catch (e) {
    error.value = e.message || 'Không thể tải dữ liệu thống kê'
  } finally {
    loading.value = false
  }
}

onMounted(loadData)

// ── Chart: Doanh thu (đổi bảng màu sang tông sân đêm: lime/turf) ──
const revenueChartData = computed(() => ({
  labels: (data.value.doanhThuTheoNgay || []).map(item => item.nhan),
  datasets: [{
    label: 'Doanh thu (VNĐ)',
    data: (data.value.doanhThuTheoNgay || []).map(item => Number(item.giaTri)),
    borderColor: '#b6ff3c',
    backgroundColor: (ctx) => {
      if (!ctx.chart.chartArea) return 'rgba(182, 255, 60, 0.12)'
      const gradient = ctx.chart.ctx.createLinearGradient(0, ctx.chart.chartArea.top, 0, ctx.chart.chartArea.bottom)
      gradient.addColorStop(0, 'rgba(182, 255, 60, 0.28)')
      gradient.addColorStop(1, 'rgba(182, 255, 60, 0.01)')
      return gradient
    },
    borderWidth: 2.5,
    pointBackgroundColor: '#b6ff3c',
    pointBorderColor: '#050b08',
    pointBorderWidth: 2,
    pointRadius: 4,
    pointHoverRadius: 6,
    tension: 0.4,
    fill: true
  }]
}))

const revenueChartOptions = computed(() => ({
  responsive: true,
  maintainAspectRatio: false,
  interaction: { intersect: false, mode: 'index' },
  plugins: {
    legend: { display: false },
    tooltip: {
      backgroundColor: '#0a1f13',
      titleColor: '#d3ff8f',
      bodyColor: '#f7fbf4',
      borderColor: 'rgba(182,255,60,.3)',
      borderWidth: 1,
      padding: 12,
      cornerRadius: 8,
      displayColors: false,
      callbacks: {
        title: (items) => {
          if (!items.length) return ''
          return selectedMonth.value
            ? `Ngày ${items[0].label}/${selectedMonth.value}/${selectedYear.value}`
            : items[0].label + ' — ' + selectedYear.value
        },
        label: ctx => formatPrice(ctx.parsed.y)
      }
    }
  },
  scales: {
    x: {
      grid: { display: false },
      ticks: {
        color: 'rgba(227,236,223,.65)',
        font: { size: 11, family: "'Manrope', sans-serif" },
        maxRotation: 0
      }
    },
    y: {
      beginAtZero: true,
      grid: { color: 'rgba(247,251,244,.08)' },
      ticks: {
        color: 'rgba(227,236,223,.65)',
        font: { size: 12, family: "'Manrope', sans-serif" },
        callback: val => {
          if (val >= 1000000) return (val / 1000000).toFixed(1) + 'M'
          if (val >= 1000) return (val / 1000).toFixed(0) + 'K'
          return val
        }
      }
    }
  }
}))

// ── Helpers ──
function getContributionRate(fieldRevenue) {
    const total = data.value.tongDoanhThu
    if (!total || total === 0) return 0
    return ((fieldRevenue / total) * 100).toFixed(1)
}

function formatPrice(value) {
    if (!value) return '0đ'
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' })
        .format(value)
        .replace('₫', 'đ')
}

// ── Xuất Excel (ExcelJS — định dạng chuyên nghiệp, giữ nguyên không đổi) ──
async function xuatExcel() {
  const d = data.value
  const wb = new ExcelJS.Workbook()
  wb.creator = 'DreamLeague Admin'
  wb.created = new Date()

  const tieuDeThoiGian = selectedMonth.value
    ? `Tháng ${selectedMonth.value}/${selectedYear.value}`
    : `Năm ${selectedYear.value}`

 // ── Các style dùng chung (Đã thêm FF vào trước mã màu) ──
  const PRIMARY = 'FF16A34A'       
  const PRIMARY_DARK = 'FF15803D'
  const HEADER_BG = 'FF0F172A'     
  const LIGHT_GREEN = 'FFF0FDF4'   
  const LIGHT_GRAY = 'FFF8FAFC'    
  const WHITE = 'FFFFFFFF'
  const BORDER_COLOR = 'FFD1D5DB'

  const thinBorder = {
    top: { style: 'thin', color: { argb: BORDER_COLOR } },
    left: { style: 'thin', color: { argb: BORDER_COLOR } },
    bottom: { style: 'thin', color: { argb: BORDER_COLOR } },
    right: { style: 'thin', color: { argb: BORDER_COLOR } }
  }

  const fontTitle = { name: 'Arial', size: 14, bold: true, color: { argb: WHITE } }
  const fontSubTitle = { name: 'Arial', size: 10, italic: true, color: { argb: 'FF64748B' } }
  const fontHeader = { name: 'Arial', size: 11, bold: true, color: { argb: WHITE } }
  const fontNormal = { name: 'Arial', size: 11, color: { argb: 'FF1E293B' } }
  const fontBold = { name: 'Arial', size: 11, bold: true, color: { argb: 'FF1E293B' } }
  const fontTotal = { name: 'Arial', size: 11, bold: true, color: { argb: WHITE } }

  const currencyFormat = '#,##0 "VNĐ"'
  const percentFormat = '0.0"%"'

  // ===================================================================
  // SHEET 1: Tổng quan
  // ===================================================================
  const ws1 = wb.addWorksheet('Tổng quan', {
    properties: { tabColor: { argb: PRIMARY } }
  })

  // Cột
  ws1.columns = [
    { width: 36 },
    { width: 26 }
  ]

  // Dòng 1 — Tiêu đề chính (merge A1:B1)
  ws1.mergeCells('A1:B1')
  const titleRow1 = ws1.getRow(1)
  titleRow1.height = 40
  const titleCell1 = ws1.getCell('A1')
  titleCell1.value = `📊  BÁO CÁO DOANH THU — ${tieuDeThoiGian}`
  titleCell1.font = fontTitle
  titleCell1.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: HEADER_BG } }
  titleCell1.alignment = { horizontal: 'center', vertical: 'middle' }

  // Dòng 2 — Phụ đề
  ws1.mergeCells('A2:B2')
  const subRow1 = ws1.getRow(2)
  subRow1.height = 22
  const subCell1 = ws1.getCell('A2')
  subCell1.value = `Xuất lúc: ${new Date().toLocaleString('vi-VN')} — Hệ thống DreamLeague`
  subCell1.font = fontSubTitle
  subCell1.alignment = { horizontal: 'center', vertical: 'middle' }
  subCell1.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: LIGHT_GREEN } }

  // Dòng 3 — trống
  ws1.getRow(3).height = 8

  // Dòng 4 — Header bảng
  const hdr1 = ws1.getRow(4)
  hdr1.values = ['Chỉ số', 'Giá trị']
  hdr1.height = 30
  hdr1.eachCell(cell => {
    cell.font = fontHeader
    cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: PRIMARY } }
    cell.alignment = { horizontal: 'center', vertical: 'middle' }
    cell.border = thinBorder
  })

  // Dòng 5–9 — Dữ liệu
  const tongQuanRows = [
    ['💰 Tổng doanh thu', Number(d.tongDoanhThu) || 0, currencyFormat],
    ['✅ Đơn hoàn thành', d.donHoanThanh || 0, '#,##0'],
    ['❌ Đơn bị hủy', d.donDaHuy || 0, '#,##0'],
    ['📉 Tỷ lệ hủy', d.tyLeHuy || 0, percentFormat],
    ['📋 Tổng đơn (Hoàn thành + Hủy)', (d.donHoanThanh || 0) + (d.donDaHuy || 0), '#,##0']
  ]
  tongQuanRows.forEach((item, idx) => {
    const row = ws1.getRow(5 + idx)
    row.values = [item[0], item[1]]
    row.height = 28
    const bgColor = idx % 2 === 0 ? LIGHT_GREEN : WHITE
    row.eachCell((cell, colNumber) => {
      cell.font = colNumber === 2 ? fontBold : fontNormal
      cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: bgColor } }
      cell.border = thinBorder
      cell.alignment = { vertical: 'middle', horizontal: colNumber === 2 ? 'right' : 'left', indent: 1 }
      if (colNumber === 2) cell.numFmt = item[2]
    })
  })

  // Freeze header
  ws1.views = [{ state: 'frozen', ySplit: 4 }]

  // ===================================================================
  // SHEET 2: Doanh thu theo thời gian
  // ===================================================================
  const nhanThoiGian = selectedMonth.value ? 'Ngày' : 'Tháng'
  const ws2 = wb.addWorksheet('Doanh thu theo thời gian', {
    properties: { tabColor: { argb: 'FF2563EB' } }
  })

  ws2.columns = [
    { width: 20 },
    { width: 28 }
  ]

  // Tiêu đề
  ws2.mergeCells('A1:B1')
  const titleRow2 = ws2.getRow(1)
  titleRow2.height = 40
  const titleCell2 = ws2.getCell('A1')
  titleCell2.value = `📈  DOANH THU THEO ${nhanThoiGian.toUpperCase()} — ${tieuDeThoiGian}`
  titleCell2.font = fontTitle
  titleCell2.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: HEADER_BG } }
  titleCell2.alignment = { horizontal: 'center', vertical: 'middle' }

  // Phụ đề
  ws2.mergeCells('A2:B2')
  const subCell2 = ws2.getCell('A2')
  subCell2.value = `Dữ liệu từ các đơn đã hoàn thành`
  subCell2.font = fontSubTitle
  subCell2.alignment = { horizontal: 'center', vertical: 'middle' }
  subCell2.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: LIGHT_GREEN } }
  ws2.getRow(2).height = 22

  ws2.getRow(3).height = 8

  // Header bảng
  const hdr2 = ws2.getRow(4)
  hdr2.values = [nhanThoiGian, 'Doanh thu (VNĐ)']
  hdr2.height = 30
  hdr2.eachCell(cell => {
    cell.font = fontHeader
    cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FF2563EB' } }
    cell.alignment = { horizontal: 'center', vertical: 'middle' }
    cell.border = thinBorder
  })

  // Data rows
  const doanhThuItems = d.doanhThuTheoNgay || []
  let tongDoanhThuSheet2 = 0
  doanhThuItems.forEach((item, idx) => {
    const row = ws2.getRow(5 + idx)
    const val = Number(item.giaTri) || 0
    tongDoanhThuSheet2 += val
    row.values = [item.nhan, val]
    row.height = 26
    const bgColor = idx % 2 === 0 ? WHITE : LIGHT_GRAY
    row.eachCell((cell, colNumber) => {
      cell.font = fontNormal
      cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: bgColor } }
      cell.border = thinBorder
      cell.alignment = { vertical: 'middle', horizontal: colNumber === 2 ? 'right' : 'center' }
      if (colNumber === 2) cell.numFmt = currencyFormat
    })
  })

  // Dòng tổng cộng
  if (doanhThuItems.length > 0) {
    const totalRow2 = ws2.getRow(5 + doanhThuItems.length)
    totalRow2.values = ['TỔNG CỘNG', tongDoanhThuSheet2]
    totalRow2.height = 32
    totalRow2.eachCell(cell => {
      cell.font = fontTotal
      cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: PRIMARY_DARK } }
      cell.alignment = { horizontal: 'center', vertical: 'middle' }
      cell.border = thinBorder
    })
    totalRow2.getCell(2).numFmt = currencyFormat
    totalRow2.getCell(2).alignment = { horizontal: 'right', vertical: 'middle' }
  }

  ws2.views = [{ state: 'frozen', ySplit: 4 }]

  // ===================================================================
  // SHEET 3: Chi tiết theo sân
  // ===================================================================
  const ws3 = wb.addWorksheet('Chi tiết theo sân', {
    properties: { tabColor: { argb: 'FFF59E0B' } }
  })

  ws3.columns = [
    { width: 26 },
    { width: 16 },
    { width: 16 },
    { width: 26 },
    { width: 22 }
  ]

  // Tiêu đề
  ws3.mergeCells('A1:E1')
  const titleRow3 = ws3.getRow(1)
  titleRow3.height = 40
  const titleCell3 = ws3.getCell('A1')
  titleCell3.value = `⚽  CHI TIẾT DOANH THU THEO SÂN — ${tieuDeThoiGian}`
  titleCell3.font = fontTitle
  titleCell3.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: HEADER_BG } }
  titleCell3.alignment = { horizontal: 'center', vertical: 'middle' }

  // Phụ đề
  ws3.mergeCells('A2:E2')
  const subCell3 = ws3.getCell('A2')
  subCell3.value = `Thống kê số lượt đặt, số giờ và doanh thu từng sân`
  subCell3.font = fontSubTitle
  subCell3.alignment = { horizontal: 'center', vertical: 'middle' }
  subCell3.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: LIGHT_GREEN } }
  ws3.getRow(2).height = 22

  ws3.getRow(3).height = 8

  // Header bảng
  const hdr3 = ws3.getRow(4)
  hdr3.values = ['Tên sân', 'Số lượt đặt', 'Số giờ đá', 'Doanh thu (VNĐ)', 'Tỷ lệ đóng góp']
  hdr3.height = 30
  hdr3.eachCell(cell => {
    cell.font = fontHeader
    cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFF59E0B' } }
    cell.alignment = { horizontal: 'center', vertical: 'middle' }
    cell.border = thinBorder
  })
  // Đổi màu chữ header sang tối vì nền vàng
  hdr3.eachCell(cell => {
    cell.font = { name: 'Arial', size: 11, bold: true, color: { argb: 'FF1E293B' } }
  })

  // Data rows
  const sanItems = d.chiTietTheoSan || []
  sanItems.forEach((san, idx) => {
    const row = ws3.getRow(5 + idx)
    row.values = [
      san.tenSan,
      san.soLuotDat || 0,
      san.soGio ? Number(san.soGio.toFixed(1)) : 0,
      Number(san.doanhThu) || 0,
      Number(getContributionRate(san.doanhThu)) / 100 || 0
    ]
    row.height = 28
    const bgColor = idx % 2 === 0 ? WHITE : LIGHT_GRAY
    row.eachCell((cell, colNumber) => {
      cell.font = colNumber === 1 ? fontBold : fontNormal
      cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: bgColor } }
      cell.border = thinBorder
      cell.alignment = { vertical: 'middle' }
      if (colNumber === 1) cell.alignment = { vertical: 'middle', indent: 1 }
      if (colNumber === 2 || colNumber === 3) {
        cell.alignment = { horizontal: 'center', vertical: 'middle' }
        cell.numFmt = colNumber === 3 ? '0.0' : '#,##0'
      }
      if (colNumber === 4) {
        cell.numFmt = currencyFormat
        cell.alignment = { horizontal: 'right', vertical: 'middle' }
        cell.font = { name: 'Arial', size: 11, bold: true, color: { argb: 'FF16A34A' } }
      }
      if (colNumber === 5) {
        cell.numFmt = '0.0%'
        cell.alignment = { horizontal: 'center', vertical: 'middle' }
      }
    })
  })

  // Dòng tổng cộng
  if (sanItems.length > 0) {
    const totalRow3 = ws3.getRow(5 + sanItems.length)
    totalRow3.values = [
      'TỔNG CỘNG',
      sanItems.reduce((s, r) => s + (r.soLuotDat || 0), 0),
      Number(sanItems.reduce((s, r) => s + (r.soGio || 0), 0).toFixed(1)),
      Number(d.tongDoanhThu) || 0,
      1
    ]
    totalRow3.height = 32
    totalRow3.eachCell((cell, colNumber) => {
      cell.font = fontTotal
      cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: PRIMARY_DARK } }
      cell.alignment = { horizontal: 'center', vertical: 'middle' }
      cell.border = thinBorder
      if (colNumber === 3) cell.numFmt = '0.0'
      if (colNumber === 4) {
        cell.numFmt = currencyFormat
        cell.alignment = { horizontal: 'right', vertical: 'middle' }
      }
      if (colNumber === 5) cell.numFmt = '0.0%'
    })
  }

  ws3.views = [{ state: 'frozen', ySplit: 4 }]

  // ===================================================================
  // TẢI FILE
  // ===================================================================
  const tenFile = selectedMonth.value
    ? `DoanhThu_Thang${selectedMonth.value}_${selectedYear.value}.xlsx`
    : `DoanhThu_Nam${selectedYear.value}.xlsx`

  const buffer = await wb.xlsx.writeBuffer()
  const blob = new Blob([buffer], {
    type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
  })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = tenFile
  a.click()
  URL.revokeObjectURL(url)
}
</script>

<style scoped>
/* ============================================================
   TOKENS — đồng bộ với trang chủ & các trang quản lý khác
   ============================================================ */
.tkdt {
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
  display: inline-flex; align-items: center; gap: 8px; padding: 12px 22px;
  background: linear-gradient(135deg, var(--turf-500), var(--turf-700)); color: var(--chalk-050);
  border: none; border-radius: 999px; font-weight: 700; font-size: 14px; cursor: pointer;
  box-shadow: 0 10px 24px -10px rgba(20,98,57,.75); transition: transform .15s ease, box-shadow .15s ease;
  white-space: nowrap;
}
.btn-primary:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 14px 28px -8px rgba(182,255,60,.35); }
.btn-primary:disabled { opacity: .5; cursor: not-allowed; transform: none; box-shadow: none; }
.btn-export { margin-left: auto; }

.btn-ghost {
  display: inline-flex; align-items: center; gap: 6px; padding: 10px 20px;
  background: rgba(247,251,244,.06); color: var(--chalk-050); border: 1.5px solid rgba(247,251,244,.2);
  border-radius: 999px; font-weight: 600; font-size: 13.5px; cursor: pointer; transition: all .15s ease;
}
.btn-ghost:hover { border-color: var(--lime-400); color: var(--lime-300); background: rgba(182,255,60,.08); }

/* ============================================================
   FILTER CARD
   ============================================================ */
.filter-card {
  background: rgba(247,251,244,.03); border: 1px solid rgba(247,251,244,.1); border-radius: 18px;
  padding: 18px 20px; margin-bottom: 22px; backdrop-filter: blur(6px);
}
.filter-top { display: flex; flex-wrap: wrap; gap: 14px; align-items: flex-end; }

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

/* ============================================================
   STATE BOX
   ============================================================ */
.state-box { text-align: center; padding: 60px 20px; color: var(--chalk-200); opacity: .85; font-size: 14.5px; display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 10px; }
.state-box.error { color: #ff9686; opacity: 1; }
.spinner { width: 28px; height: 28px; border-radius: 50%; border: 3px solid rgba(182,255,60,.2); border-top-color: var(--lime-400); animation: spin .8s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }

/* ============================================================
   STAT CARDS
   ============================================================ */
.stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 16px; margin-bottom: 24px; }
.stat-card {
  display: flex; gap: 14px; align-items: flex-start; background: rgba(247,251,244,.03);
  border: 1px solid rgba(247,251,244,.1); border-radius: 16px; padding: 18px; transition: transform .2s, border-color .2s;
}
.stat-card:hover { transform: translateY(-3px); border-color: rgba(182,255,60,.3); }
.stat-card__icon { width: 42px; height: 42px; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 19px; flex-shrink: 0; }
.icon-success { background: rgba(182,255,60,.12); }
.icon-done { background: rgba(111,168,255,.15); }
.icon-cancel { background: rgba(255,71,87,.14); }
.icon-waiting { background: rgba(255,176,32,.15); }
.stat-card__body { display: flex; flex-direction: column; gap: 4px; }
.stat-card__title { font-family: var(--font-mono); font-size: 10.5px; text-transform: uppercase; letter-spacing: .05em; color: var(--chalk-200); opacity: .7; }
.stat-card__value { font-family: var(--font-display); font-size: 22px; font-weight: 600; }
.stat-card__desc { font-size: 11.5px; color: var(--chalk-200); opacity: .55; }

.text-success { color: var(--lime-300); }
.text-done { color: var(--info-400); }
.text-cancel { color: #ff9686; }
.text-waiting { color: var(--amber-400); }

/* ============================================================
   CHART
   ============================================================ */
.chart-section { margin-top: 6px; margin-bottom: 30px; background: rgba(247,251,244,.03); border: 1px solid rgba(247,251,244,.1); border-radius: 18px; padding: 22px; }
.chart-header { margin-bottom: 16px; }
.chart-title { font-family: var(--font-display); font-size: 18px; font-weight: 600; color: var(--chalk-050); margin: 6px 0 4px; }
.chart-subtitle { font-size: 12.5px; color: var(--chalk-200); opacity: .65; margin: 0; }
.chart-container { height: 320px; position: relative; }

/* ============================================================
   TABLE
   ============================================================ */
.section-head { margin: 8px 0 14px; }
.section-title { font-family: var(--font-display); font-size: 19px; font-weight: 600; margin: 6px 0 0; color: var(--chalk-050); }

.table-card { background: rgba(247,251,244,.03); border: 1px solid rgba(247,251,244,.1); border-radius: 18px; overflow: hidden; }
.table-responsive { overflow-x: auto; }
.data-table { width: 100%; border-collapse: collapse; font-size: 13.5px; }
.data-table th {
  background: rgba(247,251,244,.04); color: var(--lime-300); font-family: var(--font-mono); font-weight: 600;
  font-size: 11px; text-transform: uppercase; letter-spacing: .05em; padding: 14px 16px; text-align: left;
  border-bottom: 1px solid rgba(247,251,244,.1);
}
.data-table td { padding: 13px 16px; border-bottom: 1px solid rgba(247,251,244,.06); vertical-align: middle; }
.data-table tbody tr:hover { background: rgba(182,255,60,.04); }
.bold { font-weight: 600; color: var(--chalk-050); }
.sub { font-size: 12.5px; color: var(--chalk-200); opacity: .75; }
.price { color: var(--lime-300); }

.progress-wrap { display: flex; align-items: center; gap: 10px; width: 100%; max-width: 200px; }
.progress-bar { flex: 1; height: 7px; border-radius: 999px; background: rgba(247,251,244,.1); overflow: hidden; }
.progress-fill { height: 100%; background: linear-gradient(90deg, var(--turf-500), var(--lime-400)); border-radius: 999px; transition: width .5s ease; }
.progress-text { font-size: 12px; color: var(--chalk-200); font-family: var(--font-mono); flex-shrink: 0; }

.no-data { text-align: center; padding: 50px 20px; color: var(--chalk-200); opacity: .6; }
.no-data__icon { font-size: 26px; margin-bottom: 8px; }

/* ============================================================
   RESPONSIVE
   ============================================================ */
@media (max-width: 720px) {
  .filter-top { flex-direction: column; align-items: stretch; }
  .pill-select { min-width: 0; }
  .btn-export { margin-left: 0; width: 100%; justify-content: center; }
  .chart-container { height: 240px; }
  .data-table { font-size: 12.5px; }
}
</style>