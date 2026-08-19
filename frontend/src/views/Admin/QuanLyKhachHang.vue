<template>
  <div class="page">

    <!-- Header -->
    <header class="page__header">
      <h1 class="page__title">Quản lý khách hàng</h1>
      <p class="page__desc">Quản lý toàn bộ khách hàng trong hệ thống <span class="brand">Dream League</span></p>
    </header>

    <!-- Stat strip -->
    <section class="stats">
      <div class="stat-card">
        <div class="stat-card__icon stat-card__icon--total">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H7a4 4 0 0 0-4 4v2"/><circle cx="10" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
        </div>
        <div>
          <p class="stat-card__value">{{ stats.total }}</p>
          <p class="stat-card__label">Tổng khách hàng</p>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-card__icon stat-card__icon--active">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/><path d="M8.5 12.5l2.5 2.5 5-5"/></svg>
        </div>
        <div>
          <p class="stat-card__value">{{ stats.active }}</p>
          <p class="stat-card__label">Đang hoạt động</p>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-card__icon stat-card__icon--locked">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="4" y="10" width="16" height="10" rx="2"/><path d="M8 10V7a4 4 0 0 1 8 0v3"/></svg>
        </div>
        <div>
          <p class="stat-card__value">{{ stats.locked }}</p>
          <p class="stat-card__label">Đã khóa</p>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-card__icon stat-card__icon--matches">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="3" y1="10" x2="21" y2="10"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="16" y1="2" x2="16" y2="6"/></svg>
        </div>
        <div>
          <p class="stat-card__value">{{ stats.totalMatches }}</p>
          <p class="stat-card__label">Lượt đặt sân</p>
        </div>
      </div>
    </section>

    <!-- Toolbar -->
    <div class="toolbar">
      <div class="search">
        <svg class="search__icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="7"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
        <input
          v-model="keyword"
          type="text"
          placeholder="Tìm tên hoặc số điện thoại..."
        />
      </div>

      <select v-model="filterTrangThai" class="filter-select">
        <option value="">Tất cả trạng thái</option>
        <option value="HOAT_DONG">Đang hoạt động</option>
        <option value="DA_KHOA">Đã khóa</option>
      </select>
    </div>

    <!-- Table -->
    <div class="table-card">

      <div v-if="loading" class="state">
        <div class="spinner"></div>
        <p>Đang tải dữ liệu...</p>
      </div>

      <div v-else-if="customers.length === 0" class="state">
        <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M22 12h-6l-2 3h-4l-2-3H2"/><path d="M5.45 5.11L2 12v6a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-6l-3.45-6.89A2 2 0 0 0 16.76 4H7.24a2 2 0 0 0-1.79 1.11z"/></svg>
        <p>Không tìm thấy khách hàng nào.</p>
      </div>

      <table v-else>
        <thead>
          <tr>
            <th>Khách hàng</th>
            <th>Liên hệ</th>
            <th>Trạng thái</th>
            <th>Số trận</th>
            <th class="col-actions">Thao tác</th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="item in customers" :key="item.id">

            <td>
              <div class="employee">
                <div class="avatar">{{ initials(item.hoTen) }}</div>
                <div>
                  <p class="employee__name">{{ item.hoTen }}</p>
                  <p class="employee__id">ID #{{ item.id }}</p>
                </div>
              </div>
            </td>

            <td>
              <p class="contact__phone">{{ item.soDienThoai }}</p>
              <p class="contact__email">{{ item.email }}</p>
            </td>

            <td>
              <span class="badge badge--status" :class="item.trangThai ? 'badge--active' : 'badge--locked'">
                <span class="badge__dot"></span>
                {{ item.trangThai ? 'Hoạt động' : 'Đã khóa' }}
              </span>
            </td>

            <td>
              <span class="matches-chip">
                <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
                {{ item.soTranDaDa ?? 0 }}
              </span>
            </td>

            <td class="col-actions">
              <button class="icon-btn icon-btn--edit" title="Sửa" @click="openEditModal(item)">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 20h9"/><path d="M16.5 3.5a2.12 2.12 0 0 1 3 3L7 19l-4 1 1-4 12.5-12.5z"/></svg>
              </button>
              <button class="icon-btn icon-btn--history" title="Lịch sử đặt sân" @click="openHistoryModal(item)">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/><polyline points="12 7 12 12 15.5 14"/></svg>
              </button>
              <button
                v-if="item.trangThai"
                class="icon-btn icon-btn--lock"
                title="Khóa tài khoản"
                @click="toggleLock(item)"
              >
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="4" y="10" width="16" height="10" rx="2"/><path d="M8 10V7a4 4 0 0 1 8 0v3"/></svg>
              </button>
              <button
                v-else
                class="icon-btn icon-btn--unlock"
                title="Mở khóa tài khoản"
                @click="toggleLock(item)"
              >
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="4" y="10" width="16" height="10" rx="2"/><path d="M8 10V7a4 4 0 0 1 7.75-1"/></svg>
              </button>
            </td>

          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal sửa khách hàng -->
    <Transition name="fade">
      <div v-if="showModal" class="overlay" @click.self="showModal = false">
        <div class="modal">

          <div class="modal__header">
            <div class="modal__icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 20h9"/><path d="M16.5 3.5a2.12 2.12 0 0 1 3 3L7 19l-4 1 1-4 12.5-12.5z"/></svg>
            </div>
            <div>
              <h2>{{ isEdit ? 'Cập nhật khách hàng' : 'Thêm khách hàng' }}</h2>
              <p>Chỉnh sửa thông tin liên hệ và trạng thái tài khoản</p>
            </div>
            <button class="modal__close" @click="showModal = false">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </div>

          <div class="modal__body">
            <div class="field-row">
              <div class="field">
                <label>Họ tên</label>
                <input :value="form.hoTen" disabled />
              </div>
              <div class="field">
                <label>Số điện thoại</label>
                <input :value="form.soDienThoai" disabled />
              </div>
            </div>

            <div class="field">
              <label>Email</label>
              <input v-model="form.email" placeholder="email@domain.com" />
            </div>

            <div class="field">
              <label>Địa chỉ</label>
              <input v-model="form.diaChi" placeholder="Địa chỉ liên hệ" />
            </div>

            <div class="field">
              <label>Trạng thái</label>
              <select v-model="form.trangThai">
                <option :value="true">Hoạt động</option>
                <option :value="false">Khóa</option>
              </select>
            </div>
          </div>

          <div class="modal__footer">
            <button class="btn btn--ghost" @click="showModal = false">Hủy</button>
            <button class="btn btn--primary" @click="saveCustomer">
              {{ isEdit ? 'Cập nhật' : 'Thêm mới' }}
            </button>
          </div>

        </div>
      </div>
    </Transition>

    <!-- Modal lịch sử sân -->
    <Transition name="fade">
      <div v-if="showHistory" class="overlay" @click.self="closeHistoryModal">
        <div class="modal modal--history">

          <div class="modal__header">
            <div class="modal__icon modal__icon--history">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="3" y1="10" x2="21" y2="10"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="16" y1="2" x2="16" y2="6"/></svg>
            </div>
            <div>
              <h2>Lịch sử đặt sân</h2>
              <p>{{ historyCustomer?.hoTen }} · Tổng {{ matchHistory.length }} trận</p>
            </div>
            <button class="modal__close" @click="closeHistoryModal">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </div>

          <div class="modal__body modal__body--flush">

            <div v-if="historyLoading" class="state state--sm">
              <div class="spinner"></div>
              <p>Đang tải lịch sử...</p>
            </div>

            <div v-else-if="matchHistory.length" class="history-table-wrap">
              <table class="history-table">
                <thead>
                  <tr>
                    <th>Sân</th>
                    <th>Ngày</th>
                    <th>Giờ</th>
                    <th>Trạng thái</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="h in matchHistory" :key="h.id">
                    <td>{{ h.tenSan }}</td>
                    <td>{{ formatDate(h.ngayDat) }}</td>
                    <td>{{ h.gioBatDau }} - {{ h.gioKetThuc }}</td>
                    <td><span class="badge badge--neutral">{{ h.trangThai }}</span></td>
                  </tr>
                </tbody>
              </table>
            </div>

            <div v-else class="state state--sm">
              <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="3" y1="10" x2="21" y2="10"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="16" y1="2" x2="16" y2="6"/></svg>
              <p>Khách hàng chưa có lịch sử đặt sân nào.</p>
            </div>

          </div>

          <div class="modal__footer">
            <button class="btn btn--ghost" @click="closeHistoryModal">Đóng</button>
          </div>

        </div>
      </div>
    </Transition>

  </div>
</template>

<script setup>
import axios from "axios"
import { ref, computed, onMounted, watch } from "vue"

const API_BASE = "http://localhost:8080/api/admin/khach-hang"

const keyword = ref('')
const filterTrangThai = ref("")

const loading = ref(false)
const customers = ref([])

const showModal = ref(false)
const isEdit = ref(false)

const form = ref({
    id: null,
    hoTen: '',
    soDienThoai: '',
    email: '',
    diaChi: '',
    trangThai: true
})

const showHistory = ref(false)
const historyLoading = ref(false)
const historyCustomer = ref(null)
const matchHistory = ref([])

const stats = computed(() => {
    const total = customers.value.length
    const active = customers.value.filter(c => c.trangThai).length
    const totalMatches = customers.value.reduce((sum, c) => sum + (c.soTranDaDa ?? 0), 0)
    return { total, active, locked: total - active, totalMatches }
})

function initials(name) {
    if (!name) return '?'
    const parts = name.trim().split(/\s+/).filter(Boolean)
    if (parts.length === 1) return parts[0].charAt(0).toUpperCase()
    return (parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase()
}

function authHeader() {
    return {
        Authorization: `Bearer ${localStorage.getItem("token")}`
    }
}

onMounted(() => {
    loadCustomers()
})

async function loadCustomers() {
    loading.value = true
    try {
        const res = await axios.get(API_BASE, {
            params: {
                tuKhoa: keyword.value,
                trangThai: filterTrangThai.value
            },
            headers: authHeader()
        })
        customers.value = res.data
    } catch (e) {
        console.log(e)
        alert("Không tải được danh sách khách hàng")
    } finally {
        loading.value = false
    }
}

// debounce ô tìm kiếm để tránh gọi API liên tục khi gõ
let searchTimer = null
watch(keyword, () => {
    clearTimeout(searchTimer)
    searchTimer = setTimeout(loadCustomers, 350)
})

watch(filterTrangThai, () => {
    loadCustomers()
})

function openEditModal(item) {
    isEdit.value = true
    form.value = { ...item }
    showModal.value = true
}

async function saveCustomer() {
    try {
        await axios.put(
            `${API_BASE}/${form.value.id}`,
            {
                email: form.value.email,
                diaChi: form.value.diaChi
            },
            { headers: authHeader() }
        )

        if (form.value.trangThai) {
            await axios.put(`${API_BASE}/${form.value.id}/mo-khoa`, {}, { headers: authHeader() })
        } else {
            await axios.put(`${API_BASE}/${form.value.id}/khoa`, {}, { headers: authHeader() })
        }

        await loadCustomers()
        showModal.value = false
        alert("Cập nhật thành công")
    } catch (e) {
        console.log(e)
        alert("Cập nhật thất bại")
    }
}

// khóa / mở khóa nhanh ngay trên bảng, không cần mở modal sửa
async function toggleLock(item) {
    const confirmMsg = item.trangThai
        ? `Khóa tài khoản "${item.hoTen}"?`
        : `Mở khóa tài khoản "${item.hoTen}"?`

    if (!confirm(confirmMsg)) return

    try {
        const action = item.trangThai ? "khoa" : "mo-khoa"
        await axios.put(`${API_BASE}/${item.id}/${action}`, {}, { headers: authHeader() })
        await loadCustomers()
    } catch (e) {
        console.log(e)
        alert("Thao tác thất bại")
    }
}

// lấy lịch sử đặt sân thật của khách hàng từ database
async function openHistoryModal(item) {
    historyCustomer.value = item
    showHistory.value = true
    historyLoading.value = true
    matchHistory.value = []

    try {
        const res = await axios.get(
            `${API_BASE}/${item.id}/lich-su-dat-san`,
            { headers: authHeader() }
        )
        matchHistory.value = res.data
    } catch (e) {
        console.log(e)
        alert("Không tải được lịch sử đặt sân")
    } finally {
        historyLoading.value = false
    }
}

function closeHistoryModal() {
    showHistory.value = false
    historyCustomer.value = null
    matchHistory.value = []
}

function formatDate(dateStr) {
    if (!dateStr) return ""
    const d = new Date(dateStr)
    if (isNaN(d)) return dateStr
    return d.toLocaleDateString("vi-VN")
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@500;600;700&family=Inter:wght@400;500;600;700&display=swap');

.page{
    --bg:#f5f7fa;
    --surface:#ffffff;
    --surface-alt:#f8fafc;
    --border:#e6eaf0;
    --ink:#0f172a;
    --ink-soft:#64748b;
    --ink-faint:#94a3b8;

    --primary:#16a34a;
    --primary-dark:#15803d;
    --primary-soft:#e8f8ee;

    --staff:#1d4ed8;
    --staff-soft:#e0e9ff;

    --purple:#7c3aed;
    --purple-soft:#ede9fe;

    --active:#15803d;
    --active-soft:#dcfce7;

    --locked:#c2410c;
    --locked-soft:#ffedd5;

    --danger:#dc2626;
    --danger-dark:#b91c1c;
    --danger-soft:#fee2e2;

    --radius:14px;
    --radius-sm:9px;
    --shadow:0 1px 2px rgba(15,23,42,.04), 0 8px 24px rgba(15,23,42,.06);

    font-family:'Inter',sans-serif;
    color:var(--ink);
}

/* Header */

.page__title{
    font-family:'Space Grotesk',sans-serif;
    font-size:30px;
    font-weight:700;
    letter-spacing:-.01em;
    margin-bottom:6px;
}

.page__desc{
    color:var(--ink-soft);
    font-size:15px;
    margin-bottom:26px;
}

.brand{
    color:var(--primary-dark);
    font-weight:600;
}

/* Stats */

.stats{
    display:grid;
    grid-template-columns:repeat(4,1fr);
    gap:16px;
    margin-bottom:22px;
}

.stat-card{
    background:var(--surface);
    border:1px solid var(--border);
    border-radius:var(--radius);
    padding:18px 20px;
    display:flex;
    align-items:center;
    gap:14px;
    box-shadow:var(--shadow);
}

.stat-card__icon{
    width:42px;
    height:42px;
    min-width:42px;
    border-radius:11px;
    display:flex;
    align-items:center;
    justify-content:center;
}

.stat-card__icon--total{ background:var(--staff-soft); color:var(--staff); }
.stat-card__icon--active{ background:var(--active-soft); color:var(--active); }
.stat-card__icon--locked{ background:var(--locked-soft); color:var(--locked); }
.stat-card__icon--matches{ background:var(--purple-soft); color:var(--purple); }

.stat-card__value{
    font-family:'Space Grotesk',sans-serif;
    font-size:22px;
    font-weight:700;
    line-height:1.1;
}

.stat-card__label{
    color:var(--ink-soft);
    font-size:13px;
    margin-top:2px;
}

/* Toolbar */

.toolbar{
    display:flex;
    justify-content:space-between;
    align-items:center;
    gap:16px;
    margin-bottom:18px;
}

.search{
    position:relative;
    width:340px;
}

.search__icon{
    position:absolute;
    left:14px;
    top:50%;
    transform:translateY(-50%);
    color:var(--ink-faint);
}

.search input{
    width:100%;
    padding:11px 14px 11px 42px;
    border:1px solid var(--border);
    border-radius:var(--radius-sm);
    background:var(--surface);
    font-family:inherit;
    font-size:14px;
    outline:none;
    transition:border-color .15s, box-shadow .15s;
}

.search input:focus{
    border-color:var(--primary);
    box-shadow:0 0 0 3px var(--primary-soft);
}

.filter-select{
    padding:11px 14px;
    border:1px solid var(--border);
    border-radius:var(--radius-sm);
    background:var(--surface);
    font-family:inherit;
    font-size:14px;
    color:var(--ink);
    outline:none;
    cursor:pointer;
    transition:border-color .15s, box-shadow .15s;
}

.filter-select:focus{
    border-color:var(--primary);
    box-shadow:0 0 0 3px var(--primary-soft);
}

.btn{
    display:inline-flex;
    align-items:center;
    gap:8px;
    border:none;
    border-radius:var(--radius-sm);
    padding:11px 20px;
    font-family:inherit;
    font-size:14px;
    font-weight:600;
    cursor:pointer;
    transition:background .15s, transform .05s, opacity .15s;
}

.btn:active{ transform:scale(.98); }
.btn:disabled{ opacity:.6; cursor:not-allowed; }

.btn--primary{ background:var(--primary); color:#fff; }
.btn--primary:hover:not(:disabled){ background:var(--primary-dark); }

.btn--ghost{ background:var(--surface-alt); color:var(--ink); }
.btn--ghost:hover{ background:var(--border); }

/* Table */

.table-card{
    background:var(--surface);
    border:1px solid var(--border);
    border-radius:var(--radius);
    box-shadow:var(--shadow);
    overflow:hidden;
}

table{
    width:100%;
    border-collapse:collapse;
}

th{
    background:var(--surface-alt);
    padding:14px 18px;
    text-align:left;
    font-size:12.5px;
    font-weight:600;
    text-transform:uppercase;
    letter-spacing:.04em;
    color:var(--ink-soft);
    border-bottom:1px solid var(--border);
}

td{
    padding:14px 18px;
    border-bottom:1px solid var(--border);
    vertical-align:middle;
}

tbody tr:last-child td{ border-bottom:none; }
tbody tr:hover{ background:var(--surface-alt); }

.col-actions{ width:130px; }

/* Employee/customer cell */

.employee{
    display:flex;
    align-items:center;
    gap:12px;
}

.avatar{
    width:38px;
    height:38px;
    min-width:38px;
    border-radius:50%;
    display:flex;
    align-items:center;
    justify-content:center;
    font-family:'Space Grotesk',sans-serif;
    font-weight:700;
    font-size:13px;
    background:var(--staff-soft);
    color:var(--staff);
}

.employee__name{ font-weight:600; font-size:14px; }
.employee__id{ color:var(--ink-faint); font-size:12.5px; margin-top:1px; }

.contact__phone{ font-size:14px; font-weight:500; }
.contact__email{ color:var(--ink-soft); font-size:13px; margin-top:2px; }

/* Badges */

.badge{
    display:inline-flex;
    align-items:center;
    gap:6px;
    padding:5px 12px;
    border-radius:20px;
    font-size:12.5px;
    font-weight:600;
}

.badge__dot{
    width:6px;
    height:6px;
    border-radius:50%;
    background:currentColor;
}

.badge--active{ background:var(--active-soft); color:var(--active); }
.badge--locked{ background:var(--locked-soft); color:var(--locked); }
.badge--neutral{ background:var(--surface-alt); color:var(--ink-soft); border:1px solid var(--border); }

.matches-chip{
    display:inline-flex;
    align-items:center;
    gap:6px;
    padding:5px 12px;
    border-radius:20px;
    font-size:12.5px;
    font-weight:600;
    background:var(--purple-soft);
    color:var(--purple);
}

/* Icon buttons */

.icon-btn{
    width:32px;
    height:32px;
    border:none;
    border-radius:8px;
    display:inline-flex;
    align-items:center;
    justify-content:center;
    cursor:pointer;
    margin-right:6px;
    transition:background .15s, color .15s;
}

.icon-btn--edit{ background:var(--staff-soft); color:var(--staff); }
.icon-btn--edit:hover{ background:var(--staff); color:#fff; }

.icon-btn--history{ background:var(--purple-soft); color:var(--purple); }
.icon-btn--history:hover{ background:var(--purple); color:#fff; }

.icon-btn--lock{ background:var(--danger-soft); color:var(--danger); }
.icon-btn--lock:hover{ background:var(--danger); color:#fff; }

.icon-btn--unlock{ background:var(--active-soft); color:var(--active); }
.icon-btn--unlock:hover{ background:var(--active); color:#fff; }

/* Loading / empty state */

.state{
    display:flex;
    flex-direction:column;
    align-items:center;
    justify-content:center;
    gap:12px;
    padding:64px 20px;
    color:var(--ink-faint);
    font-size:14px;
}

.state--sm{ padding:40px 20px; }

.spinner{
    width:30px;
    height:30px;
    border:3px solid var(--border);
    border-top-color:var(--primary);
    border-radius:50%;
    animation:spin .7s linear infinite;
}

@keyframes spin{ to{ transform:rotate(360deg); } }

/* Modal */

.overlay{
    position:fixed;
    inset:0;
    background:rgba(15,23,42,.5);
    backdrop-filter:blur(2px);
    display:flex;
    justify-content:center;
    align-items:center;
    z-index:999;
    padding:20px;
}

.modal{
    width:460px;
    max-width:100%;
    max-height:90vh;
    overflow-y:auto;
    background:var(--surface);
    border-radius:16px;
    box-shadow:0 20px 60px rgba(15,23,42,.25);
}

.modal--history{ width:560px; }

.modal__header{
    display:flex;
    align-items:flex-start;
    gap:14px;
    padding:24px 24px 18px;
    border-bottom:1px solid var(--border);
}

.modal__header h2{
    font-family:'Space Grotesk',sans-serif;
    font-size:18px;
    font-weight:700;
}

.modal__header p{
    color:var(--ink-soft);
    font-size:13px;
    margin-top:2px;
}

.modal__icon{
    width:40px;
    height:40px;
    min-width:40px;
    border-radius:10px;
    background:var(--primary-soft);
    color:var(--primary-dark);
    display:flex;
    align-items:center;
    justify-content:center;
}

.modal__icon--history{ background:var(--purple-soft); color:var(--purple); }

.modal__close{
    margin-left:auto;
    width:30px;
    height:30px;
    border:none;
    background:var(--surface-alt);
    border-radius:8px;
    color:var(--ink-soft);
    display:flex;
    align-items:center;
    justify-content:center;
    cursor:pointer;
}

.modal__close:hover{ background:var(--border); color:var(--ink); }

.modal__body{
    padding:20px 24px;
    display:flex;
    flex-direction:column;
    gap:15px;
}

.modal__body--flush{ padding:0; }

.field-row{
    display:grid;
    grid-template-columns:1fr 1fr;
    gap:15px;
}

.field{
    display:flex;
    flex-direction:column;
    gap:6px;
}

.field label{
    font-size:12.5px;
    font-weight:600;
    color:var(--ink-soft);
}

.field input,
.field select{
    padding:10px 12px;
    border:1px solid var(--border);
    border-radius:var(--radius-sm);
    font-family:inherit;
    font-size:14px;
    outline:none;
    background:var(--surface);
    transition:border-color .15s, box-shadow .15s;
}

.field input:focus,
.field select:focus{
    border-color:var(--primary);
    box-shadow:0 0 0 3px var(--primary-soft);
}

.field input:disabled{
    background:var(--surface-alt);
    color:var(--ink-soft);
    cursor:not-allowed;
}

.modal__footer{
    display:flex;
    justify-content:flex-end;
    gap:10px;
    padding:18px 24px 24px;
}

/* History table */

.history-table-wrap{
    max-height:340px;
    overflow-y:auto;
}

.history-table{
    width:100%;
    border-collapse:collapse;
}

.history-table th{
    position:sticky;
    top:0;
    background:var(--surface-alt);
    padding:11px 20px;
    font-size:11.5px;
    font-weight:600;
    text-transform:uppercase;
    letter-spacing:.04em;
    color:var(--ink-soft);
    border-bottom:1px solid var(--border);
    text-align:left;
}

.history-table td{
    padding:11px 20px;
    font-size:13.5px;
    border-bottom:1px solid var(--border);
}

.history-table tbody tr:last-child td{ border-bottom:none; }
.history-table tbody tr:hover{ background:var(--surface-alt); }

/* Transition */

.fade-enter-active,
.fade-leave-active{ transition:opacity .18s ease; }

.fade-enter-from,
.fade-leave-to{ opacity:0; }

.fade-enter-active .modal,
.fade-leave-active .modal{ transition:transform .18s ease, opacity .18s ease; }

.fade-enter-from .modal,
.fade-leave-to .modal{ transform:scale(.96); opacity:0; }

/* Responsive */

@media(max-width:900px){
    .stats{ grid-template-columns:repeat(2,1fr); }
}

@media(max-width:768px){

    .toolbar{ flex-direction:column; align-items:stretch; }
    .search{ width:100%; }
    .filter-select{ width:100%; }
    .field-row{ grid-template-columns:1fr; }
    .table-card{ overflow-x:auto; }
    table{ font-size:13px; min-width:640px; }

}

@media(max-width:520px){
    .stats{ grid-template-columns:1fr 1fr; }
}
</style>