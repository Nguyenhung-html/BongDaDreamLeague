<template>
  <div class="page">

    <!-- ===== NỀN AMBIENT: đèn pha + hạt sáng + hạt rơi + đạo cụ bay bay ===== -->
    <div class="page__ambient" aria-hidden="true">
      <div class="floodlight floodlight--l"></div>
      <div class="floodlight floodlight--r"></div>
      <div class="motes"><span v-for="n in 12" :key="'m'+n" class="mote" :style="{ '--i': n }"></span></div>
      <div class="falling"><span v-for="n in 16" :key="'f'+n" class="falling__item" :style="{ '--i': n }"></span></div>
      <div class="field-props">
        <div class="prop prop--ball"><svg viewBox="0 0 40 40"><circle cx="20" cy="20" r="18" fill="var(--chalk-050)" stroke="var(--night-950)" stroke-width="1.4"/><polygon points="20,10 26,14.5 24,21.5 16,21.5 14,14.5" fill="var(--night-950)"/></svg></div>
        <div class="prop prop--whistle"><svg viewBox="0 0 48 32" fill="none"><path d="M14 16a10 10 0 1 0 20 0 10 10 0 0 0-20 0Z" stroke="var(--lime-300)" stroke-width="1.6"/><circle cx="24" cy="16" r="3.2" fill="var(--lime-300)"/></svg></div>
        <div class="prop prop--flag"><svg viewBox="0 0 30 46" fill="none"><path d="M4 44V4" stroke="var(--lime-300)" stroke-width="1.8" stroke-linecap="round"/><path d="M4 5l20 6-20 6V5Z" fill="var(--lime-300)" opacity=".85"/></svg></div>
      </div>
    </div>

    <!-- Header -->
    <header class="page__header reveal">
      <h1 class="page__title">Quản lý khách hàng</h1>
      <p class="page__desc">Quản lý toàn bộ khách hàng trong hệ thống <span class="brand">Dream League</span></p>
    </header>

    <!-- Stat strip -->
    <section class="stats">
      <div class="stat-card reveal" style="--d:0s">
        <div class="stat-card__icon stat-card__icon--total">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H7a4 4 0 0 0-4 4v2"/><circle cx="10" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
        </div>
        <div>
          <p class="stat-card__value">{{ stats.total }}</p>
          <p class="stat-card__label">Tổng khách hàng</p>
        </div>
      </div>

      <div class="stat-card reveal" style="--d:.06s">
        <div class="stat-card__icon stat-card__icon--active">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/><path d="M8.5 12.5l2.5 2.5 5-5"/></svg>
        </div>
        <div>
          <p class="stat-card__value">{{ stats.active }}</p>
          <p class="stat-card__label">Đang hoạt động</p>
        </div>
      </div>

      <div class="stat-card reveal" style="--d:.12s">
        <div class="stat-card__icon stat-card__icon--locked">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="4" y="10" width="16" height="10" rx="2"/><path d="M8 10V7a4 4 0 0 1 8 0v3"/></svg>
        </div>
        <div>
          <p class="stat-card__value">{{ stats.locked }}</p>
          <p class="stat-card__label">Đã khóa</p>
        </div>
      </div>

      <div class="stat-card reveal" style="--d:.18s">
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
    <div class="toolbar reveal" style="--d:.22s">
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
    <div class="table-card reveal" style="--d:.28s">

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
// Toàn bộ dữ liệu vẫn lấy thật từ API /api/admin/khach-hang, không đổi logic gốc,
// chỉ thay giao diện sang tông "sân đêm" + hiệu ứng.
import axios from "axios"
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from "vue"

const API_BASE = "/api/admin/khach-hang"

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

    revealObserver = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          entry.target.classList.add('is-visible')
          revealObserver.unobserve(entry.target)
        }
      })
    }, { threshold: 0.1 })
    nextTick(() => document.querySelectorAll('.reveal').forEach(el => revealObserver.observe(el)))
})

let revealObserver = null
onUnmounted(() => {
    if (revealObserver) revealObserver.disconnect()
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
@import url('https://fonts.googleapis.com/css2?family=Oswald:wght@500;600;700&family=Manrope:wght@400;500;600;700;800&family=Space+Mono:wght@400;700&display=swap');

.page{
    --night-950:#050b08;
    --night-800:#0a1f13;
    --night-700:#123321;
    --turf-500:#23935a;
    --turf-700:#146239;
    --lime-400:#b6ff3c;
    --lime-300:#d3ff8f;
    --sky-400:#38bdf8;
    --sky-300:#7dd3fc;
    --purple-400:#a78bfa;
    --amber-400:#ffb020;
    --crimson-500:#ff4757;
    --chalk-050:#f7fbf4;
    --chalk-200:#e3ecdf;

    --font-display:'Oswald','Manrope',sans-serif;
    --font-body:'Manrope',sans-serif;
    --font-mono:'Space Mono',monospace;

    position:relative;
    background:var(--night-950);
    color:var(--chalk-050);
    font-family:var(--font-body);
    border-radius:20px;
    padding:32px;
    overflow:hidden;
    min-height:calc(100vh - 40px);
}

/* ===== REVEAL ===== */
.reveal{ opacity:0; transform:translateY(18px); transition:opacity .6s ease, transform .6s ease; transition-delay:var(--d,0s); }
.reveal.is-visible{ opacity:1; transform:translateY(0); }
@media (prefers-reduced-motion: reduce){ .reveal{ opacity:1; transform:none; transition:none; } }

/* ===== AMBIENT: đèn pha + hạt sáng + hạt rơi + đạo cụ ===== */
.page__ambient{ position:absolute; inset:0; z-index:0; pointer-events:none; border-radius:20px; overflow:hidden; }
.floodlight{ position:absolute; top:-20%; width:40vh; height:100vh; background:conic-gradient(from 90deg at 50% 0%, transparent 42%, rgba(255,244,214,.07) 50%, transparent 58%); mix-blend-mode:screen; animation:sweep 11s ease-in-out infinite alternate; }
.floodlight--l{ left:-6%; }
.floodlight--r{ right:-6%; animation-direction:alternate-reverse; }
@keyframes sweep{ 0%{ transform:rotate(-8deg); } 100%{ transform:rotate(8deg); } }

.motes{ position:absolute; inset:0; }
.mote{ position:absolute; bottom:-10px; left:calc((var(--i) * 8.6%) + 1%); width:3px; height:3px; border-radius:50%; background:var(--lime-300); opacity:0; animation:rise 9s linear infinite; animation-delay:calc(var(--i) * -0.7s); }
@keyframes rise{ 0%{ transform:translateY(0) scale(.6); opacity:0; } 10%{ opacity:.5; } 90%{ opacity:.1; } 100%{ transform:translateY(-100%) scale(1.1); opacity:0; } }

/* Hạt rơi (giống Dashboard / Staff) */
.falling{ position:absolute; inset:0; overflow:hidden; }
.falling__item{
  position:absolute; top:-6%; left:calc((var(--i) * 6.2%) + 1%);
  width:3px; height:3px; border-radius:50%; background:var(--chalk-050);
  opacity:0; animation:fall linear infinite;
  animation-duration:calc(7s + (var(--i) * 0.45s));
  animation-delay:calc(var(--i) * -0.6s);
}
@keyframes fall{
  0%{ transform:translateY(0) translateX(0); opacity:0; }
  8%{ opacity:.5; }
  50%{ transform:translateY(48vh) translateX(14px); }
  92%{ opacity:.12; }
  100%{ transform:translateY(100vh) translateX(-10px); opacity:0; }
}

.field-props{ position:absolute; inset:0; }
.prop{ position:absolute; opacity:.1; animation:prop-float 7s ease-in-out infinite; }
.prop svg{ width:100%; height:100%; }
.prop--ball{ width:44px; height:44px; top:8%; right:8%; }
.prop--whistle{ width:52px; height:36px; bottom:12%; right:5%; animation-delay:.6s; }
.prop--flag{ width:30px; height:44px; top:20%; left:3%; animation-delay:1.1s; }
@keyframes prop-float{ 0%,100%{ transform:translateY(0) rotate(-3deg); } 50%{ transform:translateY(-14px) rotate(3deg); } }

@media (prefers-reduced-motion: reduce){
  .floodlight, .mote, .falling__item, .prop{ animation:none; }
}

/* Header */
.page__header{ position:relative; z-index:1; }
.page__title{
    font-family:var(--font-display);
    font-size:28px;
    font-weight:600;
    letter-spacing:-.01em;
    margin-bottom:6px;
    color:var(--chalk-050);
}

.page__desc{
    color:var(--chalk-200);
    opacity:.75;
    font-size:14.5px;
    margin-bottom:26px;
}

.brand{
    color:var(--lime-300);
    font-weight:700;
    text-shadow:0 0 10px rgba(182,255,60,.35);
}

/* Stats */
.stats{
    position:relative; z-index:1;
    display:grid;
    grid-template-columns:repeat(4,1fr);
    gap:16px;
    margin-bottom:22px;
}

.stat-card{
    background:rgba(247,251,244,.04);
    border:1px solid rgba(247,251,244,.1);
    border-radius:16px;
    padding:18px 20px;
    display:flex;
    align-items:center;
    gap:14px;
    transition:transform .2s ease, box-shadow .2s ease, border-color .2s ease;
}
.stat-card:hover{ transform:translateY(-3px); border-color:rgba(182,255,60,.3); box-shadow:0 14px 28px -16px rgba(182,255,60,.25); }

.stat-card__icon{
    width:42px;
    height:42px;
    min-width:42px;
    border-radius:11px;
    display:flex;
    align-items:center;
    justify-content:center;
}

.stat-card__icon--total{ background:rgba(56,189,248,.14); color:var(--sky-300); }
.stat-card__icon--active{ background:rgba(182,255,60,.14); color:var(--lime-300); }
.stat-card__icon--locked{ background:rgba(255,176,32,.14); color:var(--amber-400); }
.stat-card__icon--matches{ background:rgba(167,139,250,.16); color:var(--purple-400); }

.stat-card__value{
    font-family:var(--font-display);
    font-size:22px;
    font-weight:600;
    line-height:1.1;
    color:var(--chalk-050);
}

.stat-card__label{
    color:var(--chalk-200);
    opacity:.7;
    font-size:12.5px;
    margin-top:2px;
}

/* Toolbar */
.toolbar{
    position:relative; z-index:1;
    display:flex;
    justify-content:space-between;
    align-items:center;
    gap:16px;
    margin-bottom:18px;
}

.search{ position:relative; width:340px; }

.search__icon{
    position:absolute;
    left:14px;
    top:50%;
    transform:translateY(-50%);
    color:rgba(247,251,244,.4);
}

.search input{
    width:100%;
    padding:11px 14px 11px 42px;
    border:1px solid rgba(247,251,244,.14);
    border-radius:10px;
    background:rgba(5,11,8,.5);
    color:var(--chalk-050);
    font-family:inherit;
    font-size:14px;
    outline:none;
    transition:border-color .15s, box-shadow .15s;
}
.search input::placeholder{ color:rgba(247,251,244,.4); }

.search input:focus{
    border-color:var(--lime-400);
    box-shadow:0 0 0 3px rgba(182,255,60,.15);
}

.filter-select{
    padding:11px 14px;
    border:1px solid rgba(247,251,244,.14);
    border-radius:10px;
    background:rgba(5,11,8,.5);
    font-family:inherit;
    font-size:14px;
    color:var(--chalk-050);
    outline:none;
    cursor:pointer;
    transition:border-color .15s, box-shadow .15s;
}

.filter-select:focus{
    border-color:var(--lime-400);
    box-shadow:0 0 0 3px rgba(182,255,60,.15);
}

.btn{
    display:inline-flex;
    align-items:center;
    gap:8px;
    border:none;
    border-radius:10px;
    padding:11px 20px;
    font-family:inherit;
    font-size:14px;
    font-weight:600;
    cursor:pointer;
    transition:background .15s, transform .05s, opacity .15s, box-shadow .15s;
}

.btn:active{ transform:scale(.98); }
.btn:disabled{ opacity:.6; cursor:not-allowed; }

.btn--primary{ background:var(--turf-500); color:var(--chalk-050); }
.btn--primary:hover:not(:disabled){ background:var(--lime-400); color:var(--night-950); box-shadow:0 8px 20px -8px rgba(182,255,60,.5); }

.btn--ghost{ background:rgba(247,251,244,.06); color:var(--chalk-050); border:1px solid rgba(247,251,244,.14); }
.btn--ghost:hover{ background:rgba(247,251,244,.12); }

/* Table */
.table-card{
    position:relative; z-index:1;
    background:rgba(247,251,244,.03);
    border:1px solid rgba(247,251,244,.09);
    border-radius:16px;
    overflow:hidden;
}

table{ width:100%; border-collapse:collapse; }

th{
    background:rgba(247,251,244,.04);
    padding:14px 18px;
    text-align:left;
    font-size:11.5px;
    font-weight:700;
    text-transform:uppercase;
    letter-spacing:.05em;
    color:var(--chalk-200);
    opacity:.7;
    border-bottom:1px solid rgba(247,251,244,.1);
}

td{
    padding:14px 18px;
    border-bottom:1px solid rgba(247,251,244,.07);
    vertical-align:middle;
    color:var(--chalk-050);
}

tbody tr:last-child td{ border-bottom:none; }
tbody tr:hover{ background:rgba(182,255,60,.045); }

.col-actions{ width:130px; }

/* Employee/customer cell */
.employee{ display:flex; align-items:center; gap:12px; }

.avatar{
    width:38px;
    height:38px;
    min-width:38px;
    border-radius:50%;
    display:flex;
    align-items:center;
    justify-content:center;
    font-family:var(--font-display);
    font-weight:700;
    font-size:13px;
    background:rgba(56,189,248,.16);
    color:var(--sky-300);
}

.employee__name{ font-weight:700; font-size:14px; color:var(--chalk-050); }
.employee__id{ color:var(--chalk-200); opacity:.55; font-size:12.5px; margin-top:1px; }

.contact__phone{ font-size:14px; font-weight:600; color:var(--chalk-050); }
.contact__email{ color:var(--chalk-200); opacity:.7; font-size:13px; margin-top:2px; }

/* Badges */
.badge{
    display:inline-flex;
    align-items:center;
    gap:6px;
    padding:5px 12px;
    border-radius:20px;
    font-size:12.5px;
    font-weight:700;
}

.badge__dot{ width:6px; height:6px; border-radius:50%; background:currentColor; }

.badge--active{ background:rgba(182,255,60,.14); color:var(--lime-300); }
.badge--locked{ background:rgba(255,176,32,.14); color:var(--amber-400); }
.badge--neutral{ background:rgba(247,251,244,.06); color:var(--chalk-200); border:1px solid rgba(247,251,244,.12); }

.matches-chip{
    display:inline-flex;
    align-items:center;
    gap:6px;
    padding:5px 12px;
    border-radius:20px;
    font-size:12.5px;
    font-weight:700;
    background:rgba(167,139,250,.16);
    color:var(--purple-400);
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
    transition:background .15s, color .15s, box-shadow .15s;
}

.icon-btn--edit{ background:rgba(56,189,248,.14); color:var(--sky-300); }
.icon-btn--edit:hover{ background:var(--sky-400); color:var(--night-950); box-shadow:0 0 12px -2px rgba(56,189,248,.6); }

.icon-btn--history{ background:rgba(167,139,250,.16); color:var(--purple-400); }
.icon-btn--history:hover{ background:var(--purple-400); color:var(--night-950); box-shadow:0 0 12px -2px rgba(167,139,250,.6); }

.icon-btn--lock{ background:rgba(255,71,87,.14); color:#ff9686; }
.icon-btn--lock:hover{ background:var(--crimson-500); color:#fff; box-shadow:0 0 12px -2px rgba(255,71,87,.6); }

.icon-btn--unlock{ background:rgba(182,255,60,.14); color:var(--lime-300); }
.icon-btn--unlock:hover{ background:var(--lime-400); color:var(--night-950); box-shadow:0 0 12px -2px rgba(182,255,60,.6); }

/* Loading / empty state */
.state{
    display:flex;
    flex-direction:column;
    align-items:center;
    justify-content:center;
    gap:12px;
    padding:64px 20px;
    color:var(--chalk-200);
    opacity:.75;
    font-size:14px;
}

.state--sm{ padding:40px 20px; }

.spinner{
    width:30px;
    height:30px;
    border:3px solid rgba(247,251,244,.15);
    border-top-color:var(--lime-400);
    border-radius:50%;
    animation:spin .7s linear infinite;
}

@keyframes spin{ to{ transform:rotate(360deg); } }

/* Modal */
.overlay{
    position:fixed;
    inset:0;
    background:rgba(5,11,8,.72);
    backdrop-filter:blur(3px);
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
    background:var(--night-800);
    border:1px solid rgba(182,255,60,.18);
    border-radius:16px;
    box-shadow:0 24px 60px rgba(0,0,0,.5);
    color:var(--chalk-050);
}

.modal--history{ width:560px; }

.modal__header{
    display:flex;
    align-items:flex-start;
    gap:14px;
    padding:24px 24px 18px;
    border-bottom:1px solid rgba(247,251,244,.1);
}

.modal__header h2{
    font-family:var(--font-display);
    font-size:18px;
    font-weight:700;
    color:var(--chalk-050);
}

.modal__header p{
    color:var(--chalk-200);
    opacity:.7;
    font-size:13px;
    margin-top:2px;
}

.modal__icon{
    width:40px;
    height:40px;
    min-width:40px;
    border-radius:10px;
    background:rgba(182,255,60,.14);
    color:var(--lime-300);
    display:flex;
    align-items:center;
    justify-content:center;
}

.modal__icon--history{ background:rgba(167,139,250,.16); color:var(--purple-400); }

.modal__close{
    margin-left:auto;
    width:30px;
    height:30px;
    border:none;
    background:rgba(247,251,244,.06);
    border-radius:8px;
    color:var(--chalk-200);
    display:flex;
    align-items:center;
    justify-content:center;
    cursor:pointer;
}

.modal__close:hover{ background:rgba(247,251,244,.14); color:var(--chalk-050); }

.modal__body{
    padding:20px 24px;
    display:flex;
    flex-direction:column;
    gap:15px;
}

.modal__body--flush{ padding:0; }

.field-row{ display:grid; grid-template-columns:1fr 1fr; gap:15px; }

.field{ display:flex; flex-direction:column; gap:6px; }

.field label{ font-size:12.5px; font-weight:700; color:var(--chalk-200); opacity:.8; }

.field input,
.field select{
    padding:10px 12px;
    border:1px solid rgba(247,251,244,.14);
    border-radius:9px;
    font-family:inherit;
    font-size:14px;
    outline:none;
    background:rgba(5,11,8,.5);
    color:var(--chalk-050);
    transition:border-color .15s, box-shadow .15s;
}

.field input:focus,
.field select:focus{
    border-color:var(--lime-400);
    box-shadow:0 0 0 3px rgba(182,255,60,.15);
}

.field input:disabled{
    background:rgba(247,251,244,.03);
    color:var(--chalk-200);
    opacity:.6;
    cursor:not-allowed;
}

.modal__footer{
    display:flex;
    justify-content:flex-end;
    gap:10px;
    padding:18px 24px 24px;
}

/* History table */
.history-table-wrap{ max-height:340px; overflow-y:auto; }

.history-table{ width:100%; border-collapse:collapse; }

.history-table th{
    position:sticky;
    top:0;
    background:var(--night-800);
    padding:11px 20px;
    font-size:11.5px;
    font-weight:700;
    text-transform:uppercase;
    letter-spacing:.05em;
    color:var(--chalk-200);
    opacity:.75;
    border-bottom:1px solid rgba(247,251,244,.1);
    text-align:left;
}

.history-table td{
    padding:11px 20px;
    font-size:13.5px;
    border-bottom:1px solid rgba(247,251,244,.07);
    color:var(--chalk-050);
}

.history-table tbody tr:last-child td{ border-bottom:none; }
.history-table tbody tr:hover{ background:rgba(182,255,60,.045); }

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