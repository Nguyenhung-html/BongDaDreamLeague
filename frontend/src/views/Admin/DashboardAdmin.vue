<template>
  <div>
    <h1 class="dashboard__page-title">Dashboard</h1>
    <p class="dashboard__page-desc">Tổng quan về các hoạt động quản lý của hệ thống DreamLeague.</p>

    <!-- Loading state -->
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>Đang tải dữ liệu thống kê...</p>
    </div>

    <!-- Error state -->
    <div v-else-if="error" class="error-state">
      <p>⚠️ {{ error }}</p>
      <button class="btn btn-primary" @click="loadData">Thử lại</button>
    </div>

    <template v-else>
      <!-- ===== 3 SUMMARY CARDS ===== -->
      <div class="dashboard-grid">
        <div class="dashboard-card">
          <div class="dashboard-card__icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
              <circle cx="8" cy="8" r="3.5" stroke="currentColor" stroke-width="1.6" />
              <path d="M2 18c0-2.21 2.69-4 6-4s6 1.79 6 4" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
              <path d="M16 8c0 2.21 1.34 4 3 4s3-1.79 3-4" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
              <path d="M16 14c-1.8 0-3.48.4-4.66 1.12" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
            </svg>
          </div>
          <div>
            <p class="dashboard-card__label">Tổng khách hàng</p>
            <p class="dashboard-card__num">{{ formatNumber(data.tongKhachHang) }}</p>
            <p class="dashboard-card__trend">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" style="vertical-align: middle;">
                <path d="M7 17l5-5 5 5" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              Tổng tài khoản
            </p>
          </div>
        </div>

        <div class="dashboard-card">
          <div class="dashboard-card__icon icon-blue">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
              <rect x="4" y="3" width="16" height="16" rx="1" stroke="currentColor" stroke-width="1.6" />
              <path d="M4 8.5h16M9 14h2M15 14h2" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
            </svg>
          </div>
          <div>
            <p class="dashboard-card__label">Đặt sân hôm nay</p>
            <p class="dashboard-card__num">{{ data.datSanHomNay }}</p>
            <p class="dashboard-card__trend trend-blue">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" style="vertical-align: middle;">
                <rect x="3" y="3" width="18" height="18" rx="2" stroke="currentColor" stroke-width="2"/>
              </svg>
              Đơn đang hoạt động
            </p>
          </div>
        </div>

        <div class="dashboard-card">
          <div class="dashboard-card__icon icon-amber">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
              <rect x="2" y="5" width="20" height="14" rx="1.5" stroke="currentColor" stroke-width="1.6" />
              <path d="M2 9.5h20" stroke="currentColor" stroke-width="1.6" />
            </svg>
          </div>
          <div>
            <p class="dashboard-card__label">Doanh thu hôm nay</p>
            <p class="dashboard-card__num">{{ formatPrice(data.doanhThuHomNay) }}</p>
            <p class="dashboard-card__trend trend-amber">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" style="vertical-align: middle;">
                <path d="M12 2v20M17 7H9.5a3.5 3.5 0 100 7h5a3.5 3.5 0 010 7H6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              Đơn hoàn thành
            </p>
          </div>
        </div>
      </div>

      <!-- ===== BIỂU ĐỒ DOANH THU THEO THÁNG ===== -->
      <div class="chart-section">
        <div class="chart-header">
          <div>
            <h2 class="chart-title">Doanh thu theo tháng</h2>
            <p class="chart-subtitle">Biểu đồ doanh thu từ các đơn hoàn thành trong năm {{ selectedYear }}</p>
          </div>
          <select v-model="selectedYear" @change="loadData" class="year-select">
            <option v-for="y in yearOptions" :key="y" :value="y">{{ y }}</option>
          </select>
        </div>
        <div class="chart-container">
          <Line :data="revenueChartData" :options="revenueChartOptions" />
        </div>
      </div>

      <!-- ===== BIỂU ĐỒ LƯỢT ĐẶT SÂN THEO THÁNG ===== -->
      <div class="chart-section">
        <div class="chart-header">
          <div>
            <h2 class="chart-title">Lượt đặt sân theo tháng</h2>
            <p class="chart-subtitle">Số lượng đơn đặt sân (không tính đơn hủy) trong năm {{ selectedYear }}</p>
          </div>
        </div>
        <div class="chart-container">
          <Line :data="bookingChartData" :options="bookingChartOptions" />
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
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
import thongKeService from '../../services/thongKeService'

// Đăng ký Chart.js components
ChartJS.register(
  CategoryScale, LinearScale, PointElement, LineElement,
  Title, Tooltip, Legend, Filler
)

// ── State ──
const loading = ref(true)
const error = ref(null)
const currentYear = new Date().getFullYear()
const selectedYear = ref(currentYear)
const yearOptions = [currentYear, currentYear - 1, currentYear - 2]

const data = ref({
  tongKhachHang: 0,
  datSanHomNay: 0,
  doanhThuHomNay: 0,
  doanhThuTheoThang: [],
  datSanTheoThang: []
})

// ── Load data từ API ──
async function loadData() {
  loading.value = true
  error.value = null
  try {
    data.value = await thongKeService.layDashboard(selectedYear.value)
  } catch (e) {
    error.value = e.message || 'Không thể tải dữ liệu thống kê'
  } finally {
    loading.value = false
  }
}

onMounted(loadData)

// ── Chart: Doanh thu theo tháng ──
const revenueChartData = computed(() => ({
  labels: data.value.doanhThuTheoThang.map(item => item.nhan),
  datasets: [{
    label: 'Doanh thu (VNĐ)',
    data: data.value.doanhThuTheoThang.map(item => Number(item.giaTri)),
    borderColor: '#16a34a',
    backgroundColor: 'rgba(22, 163, 74, 0.08)',
    borderWidth: 2.5,
    pointBackgroundColor: '#16a34a',
    pointBorderColor: '#fff',
    pointBorderWidth: 2,
    pointRadius: 5,
    pointHoverRadius: 7,
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
        label: ctx => formatPrice(ctx.parsed.y)
      }
    }
  },
  scales: {
    x: {
      grid: { display: false },
      ticks: { color: '#94a3b8', font: { size: 12 } }
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

// ── Chart: Lượt đặt sân theo tháng ──
const bookingChartData = computed(() => ({
  labels: data.value.datSanTheoThang.map(item => item.nhan),
  datasets: [{
    label: 'Lượt đặt sân',
    data: data.value.datSanTheoThang.map(item => Number(item.giaTri)),
    borderColor: '#2563eb',
    backgroundColor: 'rgba(37, 99, 235, 0.08)',
    borderWidth: 2.5,
    pointBackgroundColor: '#2563eb',
    pointBorderColor: '#fff',
    pointBorderWidth: 2,
    pointRadius: 5,
    pointHoverRadius: 7,
    tension: 0.4,
    fill: true
  }]
}))

const bookingChartOptions = computed(() => ({
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
        label: ctx => ctx.parsed.y + ' lượt đặt'
      }
    }
  },
  scales: {
    x: {
      grid: { display: false },
      ticks: { color: '#94a3b8', font: { size: 12 } }
    },
    y: {
      beginAtZero: true,
      grid: { color: '#f1f5f9' },
      ticks: {
        color: '#94a3b8',
        font: { size: 12 },
        stepSize: 1
      }
    }
  }
}))

// ── Helpers ──
function formatPrice(value) {
  if (!value) return '0đ'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' })
    .format(value).replace('₫', 'đ')
}

function formatNumber(value) {
  if (!value) return '0'
  return new Intl.NumberFormat('vi-VN').format(value)
}
</script>

<style scoped>
/* ===== TIÊU ĐỀ TRANG ===== */
.dashboard__page-title {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 6px 0;
}

.dashboard__page-desc {
  font-size: 14px;
  color: #64748b;
  margin: 0 0 28px 0;
}

/* ===== LOADING & ERROR STATES ===== */
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

.error-state .btn {
  margin-top: 16px;
}

/* ===== GRID THỐNG KÊ (3 CỘT) ===== */
.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(1, 1fr);
  gap: 20px;
}

@media (min-width: 768px) {
  .dashboard-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

/* ===== CARD THỐNG KÊ ===== */
.dashboard-card {
  background-color: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: flex-start;
  gap: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.dashboard-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.dashboard-card__icon {
  width: 44px;
  height: 44px;
  border-radius: 8px;
  background-color: rgba(22, 163, 74, 0.1);
  color: #16a34a;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.dashboard-card__icon.icon-blue {
  background-color: rgba(37, 99, 235, 0.1);
  color: #2563eb;
}

.dashboard-card__icon.icon-amber {
  background-color: rgba(245, 158, 11, 0.1);
  color: #f59e0b;
}

.dashboard-card__label {
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
  margin: 0 0 4px 0;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.dashboard-card__num {
  font-size: 26px;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 4px 0;
  letter-spacing: -0.5px;
}

.dashboard-card__trend {
  font-size: 13px;
  color: #16a34a;
  font-weight: 500;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 4px;
}

.trend-blue { color: #2563eb; }
.trend-amber { color: #f59e0b; }

/* ===== BIỂU ĐỒ SECTION ===== */
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

.year-select {
  padding: 8px 14px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  color: #475569;
  background: #f8fafc;
  cursor: pointer;
  outline: none;
  transition: border-color 0.2s;
}

.year-select:focus {
  border-color: #16a34a;
}

.chart-container {
  height: 320px;
  position: relative;
}

/* ===== BUTTONS ===== */
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 10px 22px;
  font-size: 14px;
  font-weight: 600;
  border-radius: 20px;
  text-decoration: none;
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;
}

.btn-primary {
  background-color: #16a34a;
  color: #ffffff;
}

.btn-primary:hover {
  background-color: #15803d;
  box-shadow: 0 4px 12px rgba(22, 163, 74, 0.2);
}
</style>