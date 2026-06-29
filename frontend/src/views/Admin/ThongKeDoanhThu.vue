<template>
  <div>
    <h1 class="dashboard__page-title">
        Thống kê doanh thu
    </h1>

    <p class="dashboard__page-desc">
        Xem báo cáo doanh thu, số lượng đơn đặt và hiệu suất khai thác sân lưới.
    </p>

    <div class="toolbar">
        <div class="filter-group">
            <label>Tháng: </label>
            <select v-model="selectedMonth" class="filter-box">
                <option value="all">Tất cả các tháng</option>
                <option v-for="m in 12" :key="m" :value="m">Tháng {{ m }}</option>
            </select>
        </div>

        <div class="filter-group">
            <label>Năm: </label>
            <select v-model="selectedYear" class="filter-box">
                <option value="2026">2026</option>
                <option value="2025">2025</option>
            </select>
        </div>
    </div>

    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-card__title">Tổng doanh thu</div>
            <div class="stat-card__value text-success">{{ formatPrice(totalRevenue) }}</div>
            <div class="stat-card__desc">Từ các đơn đã hoàn thành</div>
        </div>

        <div class="stat-card">
            <div class="stat-card__title">Đơn hoàn thành</div>
            <div class="stat-card__value text-done">{{ totalCompletedBookings }}</div>
            <div class="stat-card__desc">Đã thanh toán & sử dụng sân</div>
        </div>

        <div class="stat-card">
            <div class="stat-card__title">Đơn bị hủy</div>
            <div class="stat-card__value text-cancel">{{ totalCanceledBookings }}</div>
            <div class="stat-card__desc">Tỷ lệ hủy: {{ cancelRate }}%</div>
        </div>

        <div class="stat-card">
            <div class="stat-card__title">Hiệu suất lấp đầy</div>
            <div class="stat-card__value text-waiting">{{ occupancyRate }}%</div>
            <div class="stat-card__desc">Trung bình các khung giờ</div>
        </div>
    </div>

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
                <tr v-for="field in fieldStats" :key="field.name">
                    <td><b>{{ field.name }}</b></td>
                    <td>{{ field.count }} lượt</td>
                    <td>{{ field.hours }} giờ</td>
                    <td class="text-success"><b>{{ formatPrice(field.revenue) }}</b></td>
                    <td>
                        <div class="progress-bar-container">
                            <div 
                                class="progress-bar" 
                                :style="{ width: getContributionRate(field.revenue) + '%' }"
                            ></div>
                            <span class="progress-text">{{ getContributionRate(field.revenue) }}%</span>
                        </div>
                    </td>
                </tr>
                <tr v-if="fieldStats.length === 0">
                    <td colspan="5" style="text-align: center; color: #64748b;">
                        Không có dữ liệu thống kê cho thời gian này.
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue"

// Bộ lọc mặc định
const selectedMonth = ref("6") // Mặc định tháng 6 năm 2026 theo dữ liệu mẫu
const selectedYear = ref("2026")

// Dữ liệu thô giả lập từ hệ thống đặt sân
const rawBookings = ref([
    { id: 1, field: "Sân 5", date: "2026-06-28", hours: 2, total: 400000, status: "Hoàn thành" },
    { id: 2, field: "Sân 7", date: "2026-06-29", hours: 2, total: 500000, status: "Hoàn thành" },
    { id: 3, field: "Sân 3", date: "2026-06-30", hours: 2, total: 300000, status: "Hoàn thành" },
    { id: 4, field: "Sân 5", date: "2026-06-15", hours: 1.5, total: 300000, status: "Hoàn thành" },
    { id: 5, field: "Sân 7", date: "2026-06-20", hours: 2, total: 500000, status: "Đã hủy" },
    { id: 6, field: "Sân 3", date: "2026-05-12", hours: 2, total: 300000, status: "Hoàn thành" },
])

// Lọc dữ liệu theo tháng và năm được chọn
const filteredData = computed(() => {
    return rawBookings.value.filter(item => {
        const dateObj = new Date(item.date)
        const month = (dateObj.getMonth() + 1).toString()
        const year = dateObj.getFullYear().toString()

        const matchMonth = selectedMonth.value === "all" || month === selectedMonth.value
        const matchYear = year === selectedYear.value

        return matchMonth && matchYear
    })
})

// Tính toán các chỉ số tổng quan dựa trên dữ liệu đã lọc
const totalRevenue = computed(() => {
    return filteredData.value
        .filter(x => x.status === "Hoàn thành")
        .reduce((sum, item) => sum + item.total, 0)
})

const totalCompletedBookings = computed(() => {
    return filteredData.value.filter(x => x.status === "Hoàn thành").length
})

const totalCanceledBookings = computed(() => {
    return filteredData.value.filter(x => x.status === "Đã hủy").length
})

const cancelRate = computed(() => {
    const total = filteredData.value.length
    if (total === 0) return 0
    return ((totalCanceledBookings.value / total) * 100).toFixed(1)
})

const occupancyRate = computed(() => {
    // Giả lập tỷ lệ lấp đầy theo tháng
    if (filteredData.value.length === 0) return 0
    return selectedMonth.value === "6" ? 78.5 : 45.2
})

// Gom nhóm và thống kê chi tiết theo từng sân
const fieldStats = computed(() => {
    const stats = {}
    
    filteredData.value.forEach(item => {
        if (!stats[item.field]) {
            stats[item.field] = { name: item.field, count: 0, hours: 0, revenue: 0 }
        }
        
        if (item.status === "Hoàn thành") {
            stats[item.field].count++
            stats[item.field].hours += item.hours
            stats[item.field].revenue += item.total
        }
    })

    return Object.values(stats).sort((a, b) => b.revenue - a.revenue)
})

// Tính phần trăm đóng góp của từng sân vào tổng doanh thu
function getContributionRate(fieldRevenue) {
    if (totalRevenue.value === 0) return 0
    return ((fieldRevenue / totalRevenue.value) * 100).toFixed(1)
}

// Hàm định dạng tiền tệ (Ví dụ: 400000 -> 400.000đ)
function formatPrice(value) {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' })
        .format(value)
        .replace('₫', 'đ')
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
    transition: width 0.3s ease;
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
    .table-wrapper {
        overflow: auto;
    }
}
</style>