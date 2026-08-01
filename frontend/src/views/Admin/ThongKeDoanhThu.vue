<template>
  <div>
    <h1 class="dashboard__page-title">
        Thống kê doanh thu
    </h1>

    <p class="dashboard__page-desc">
        Xem báo cáo doanh thu, số lượng đơn đặt và hiệu suất khai thác sân lưới.
    </p>

    <!-- ===== BỘ LỌC ===== -->
    <div class="toolbar">
        <div class="filter-group">
            <label>Tháng: </label>
            <select v-model="selectedMonth" class="filter-box" @change="loadData">
                <option :value="null">Tất cả các tháng</option>
                <option v-for="m in 12" :key="m" :value="m">Tháng {{ m }}</option>
            </select>
        </div>

        <div class="filter-group">
            <label>Năm: </label>
            <select v-model="selectedYear" class="filter-box" @change="loadData">
                <option v-for="y in yearOptions" :key="y" :value="y">{{ y }}</option>
            </select>
        </div>

        <button
            class="btn-export"
            @click="xuatExcel"
            :disabled="loading || !data.chiTietTheoSan"
            title="Xuất báo cáo doanh thu ra file Excel"
        >
            <span class="btn-export__icon">📥</span>
            Xuất Excel
        </button>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>Đang tải dữ liệu thống kê...</p>
    </div>

    <!-- Error -->
    <div v-else-if="error" class="error-state">
      <p>⚠️ {{ error }}</p>
      <button class="btn-reload" @click="loadData">Thử lại</button>
    </div>

    <template v-else>
      <!-- ===== 4 STAT CARDS ===== -->
      <div class="stats-grid">
          <div class="stat-card">
              <div class="stat-card__title">Tổng doanh thu</div>
              <div class="stat-card__value text-success">{{ formatPrice(data.tongDoanhThu) }}</div>
              <div class="stat-card__desc">Từ các đơn đã hoàn thành</div>
          </div>

          <div class="stat-card">
              <div class="stat-card__title">Đơn hoàn thành</div>
              <div class="stat-card__value text-done">{{ data.donHoanThanh }}</div>
              <div class="stat-card__desc">Đã thanh toán & sử dụng sân</div>
          </div>

          <div class="stat-card">
              <div class="stat-card__title">Đơn bị hủy</div>
              <div class="stat-card__value text-cancel">{{ data.donDaHuy }}</div>
              <div class="stat-card__desc">Tỷ lệ hủy: {{ data.tyLeHuy }}%</div>
          </div>

          <div class="stat-card">
              <div class="stat-card__title">Tổng đơn</div>
              <div class="stat-card__value text-waiting">{{ data.donHoanThanh + data.donDaHuy }}</div>
              <div class="stat-card__desc">Hoàn thành + Đã hủy</div>
          </div>
      </div>

      <!-- ===== BIỂU ĐỒ DOANH THU ===== -->
      <div class="chart-section">
        <div class="chart-header">
          <div>
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
      <h2 class="section-title">Doanh thu chi tiết theo sân</h2>
      <div class="table-wrapper">
          <table>
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
                      <td><b>{{ field.tenSan }}</b></td>
                      <td>{{ field.soLuotDat }} lượt</td>
                      <td>{{ field.soGio.toFixed(1) }} giờ</td>
                      <td class="text-success"><b>{{ formatPrice(field.doanhThu) }}</b></td>
                      <td>
                          <div class="progress-bar-container">
                              <div
                                  class="progress-bar"
                                  :style="{ width: getContributionRate(field.doanhThu) + '%' }"
                              ></div>
                              <span class="progress-text">{{ getContributionRate(field.doanhThu) }}%</span>
                          </div>
                      </td>
                  </tr>
                  <tr v-if="!data.chiTietTheoSan || data.chiTietTheoSan.length === 0">
                      <td colspan="5" style="text-align: center; color: #64748b;">
                          Không có dữ liệu thống kê cho thời gian này.
                      </td>
                  </tr>
              </tbody>
          </table>
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

// ── Chart: Doanh thu ──
const revenueChartData = computed(() => ({
  labels: (data.value.doanhThuTheoNgay || []).map(item => item.nhan),
  datasets: [{
    label: 'Doanh thu (VNĐ)',
    data: (data.value.doanhThuTheoNgay || []).map(item => Number(item.giaTri)),
    borderColor: '#16a34a',
    backgroundColor: (ctx) => {
      if (!ctx.chart.chartArea) return 'rgba(22, 163, 74, 0.1)'
      const gradient = ctx.chart.ctx.createLinearGradient(0, ctx.chart.chartArea.top, 0, ctx.chart.chartArea.bottom)
      gradient.addColorStop(0, 'rgba(22, 163, 74, 0.2)')
      gradient.addColorStop(1, 'rgba(22, 163, 74, 0.02)')
      return gradient
    },
    borderWidth: 2.5,
    pointBackgroundColor: '#16a34a',
    pointBorderColor: '#fff',
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
      backgroundColor: '#0f172a',
      titleColor: '#e2e8f0',
      bodyColor: '#fff',
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
        color: '#94a3b8',
        font: { size: 11 },
        maxRotation: 0
      }
    },
    y: {
      beginAtZero: true,
      grid: { color: '#f1f5f9' },
      ticks: {
        color: '#94a3b8',
        font: { size: 12 },
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

// ── Xuất Excel (ExcelJS — định dạng chuyên nghiệp) ──
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
.dashboard__page-title {
    font-size: 28px;
    font-weight: 700;
    margin-bottom: 6px;
}

.dashboard__page-desc {
    color: #64748b;
    margin-bottom: 25px;
}

.section-title {
    font-size: 20px;
    font-weight: 600;
    margin: 30px 0 15px 0;
}

.toolbar {
    display: flex;
    gap: 20px;
    margin-bottom: 25px;
    flex-wrap: wrap;
    background: #f8fafc;
    padding: 15px;
    border-radius: 10px;
    align-items: center;
}

.btn-export {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 9px 20px;
    background: linear-gradient(135deg, #16a34a, #15803d);
    color: #fff;
    border: none;
    border-radius: 8px;
    font-size: 14px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.25s ease;
    box-shadow: 0 2px 6px rgba(22, 163, 74, 0.25);
    margin-left: auto;
}

.btn-export:hover:not(:disabled) {
    background: linear-gradient(135deg, #15803d, #166534);
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(22, 163, 74, 0.35);
}

.btn-export:active:not(:disabled) {
    transform: translateY(0);
}

.btn-export:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}

.btn-export__icon {
    font-size: 16px;
}

.filter-group {
    display: flex;
    align-items: center;
    gap: 8px;
}

.filter-group label {
    font-weight: 500;
    color: #475569;
}

.filter-box {
    padding: 8px 14px;
    border: 1px solid #ddd;
    border-radius: 8px;
    min-width: 160px;
    background: #fff;
    box-sizing: border-box;
    outline: none;
    transition: border-color 0.2s;
}

.filter-box:focus {
    border-color: #16a34a;
}

/* ===== LOADING & ERROR ===== */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 24px;
  color: #64748b;
}

.spinner {
  width: 36px;
  height: 36px;
  border: 3px solid #e2e8f0;
  border-top-color: #16a34a;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.error-state {
  text-align: center;
  padding: 48px 24px;
  background: #fff;
  border: 1px dashed #fca5a5;
  border-radius: 12px;
  color: #dc2626;
}

.btn-reload {
  margin-top: 16px;
  padding: 10px 22px;
  background: #16a34a;
  color: #fff;
  border: none;
  border-radius: 20px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-reload:hover {
  background: #15803d;
}

/* Layout lưới cho các thẻ báo cáo nhanh */
.stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
    gap: 20px;
    margin-bottom: 10px;
}

.stat-card {
    background: #fff;
    padding: 20px;
    border-radius: 12px;
    box-shadow: 0 2px 10px rgba(0,0,0,.06);
    border: 1px solid #f1f5f9;
    transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.stat-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0,0,0,.08);
}

.stat-card__title {
    font-size: 14px;
    color: #64748b;
    font-weight: 500;
    margin-bottom: 8px;
}

.stat-card__value {
    font-size: 24px;
    font-weight: 700;
    margin-bottom: 6px;
}

.stat-card__desc {
    font-size: 12px;
    color: #94a3b8;
}

/* Màu chữ bổ trợ */
.text-success { color: #16a34a; }
.text-done { color: #2563eb; }
.text-cancel { color: #dc2626; }
.text-waiting { color: #f59e0b; }

/* ===== BIỂU ĐỒ ===== */
.chart-section {
  margin-top: 24px;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.chart-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 20px;
}

.chart-title {
  font-size: 18px;
  font-weight: 600;
  color: #0f172a;
  margin: 0 0 4px 0;
}

.chart-subtitle {
  font-size: 13px;
  color: #94a3b8;
  margin: 0;
}

.chart-container {
  height: 320px;
  position: relative;
}

/* Bảng dữ liệu */
.table-wrapper {
    background: #fff;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 2px 10px rgba(0,0,0,.08);
}

table {
    width: 100%;
    border-collapse: collapse;
}

th, td {
    padding: 15px;
    border-bottom: 1px solid #eee;
    text-align: left;
}

th {
    background: #f8fafc;
    color: #475569;
    font-weight: 600;
}

/* Thanh hiển thị tiến độ đóng góp doanh thu */
.progress-bar-container {
    display: flex;
    align-items: center;
    gap: 10px;
    width: 100%;
    max-width: 200px;
}

.progress-bar {
    height: 8px;
    background: #16a34a;
    border-radius: 4px;
    transition: width 0.5s ease;
}

.progress-text {
    font-size: 13px;
    color: #475569;
    font-weight: 500;
}

@media(max-width: 768px) {
    .toolbar {
        flex-direction: column;
        gap: 12px;
    }
    .filter-box {
        width: 100%;
    }
    .filter-group {
        width: 100%;
        justify-content: space-between;
    }
    .btn-export {
        width: 100%;
        justify-content: center;
        margin-left: 0;
    }
    .table-wrapper {
        overflow: auto;
    }
    .chart-container {
        height: 250px;
    }
}
</style>