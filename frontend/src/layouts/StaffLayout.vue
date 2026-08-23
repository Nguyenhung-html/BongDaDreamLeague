<template>
  <div class="dashboard">
    <aside class="dashboard__sidebar" :class="{ open: sidebarOpen }">
      <div class="dashboard__sidebar-top">
        <router-link to="/" class="dashboard__sidebar-brand">
          <img :src="logo" alt="DreamLeague" class="dashboard__sidebar-brand-logo" />
          <span><strong>Dream</strong>League</span>
        </router-link>
      </div>
      <nav class="dashboard__sidebar-nav">
        <div class="sidebar-section">
          <p class="sidebar-section__label">Quản lý</p>
          <router-link to="/staff" class="sidebar-nav-item" :class="{ active: isActive('/staff') }">
            <span class="sidebar-nav-item__icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                <rect x="3" y="3" width="8" height="8" rx="1" stroke="currentColor" stroke-width="1.6" />
                <rect x="13" y="3" width="8" height="8" rx="1" stroke="currentColor" stroke-width="1.6" />
                <rect x="3" y="13" width="8" height="8" rx="1" stroke="currentColor" stroke-width="1.6" />
                <rect x="13" y="13" width="8" height="8" rx="1" stroke="currentColor" stroke-width="1.6" />
              </svg>
            </span>
            <span class="sidebar-nav-item__label">Dashboard</span>
          </router-link>
          <router-link to="/staff/dat-san" class="sidebar-nav-item" :class="{ active: isActive('/staff/dat-san') }">
            <span class="sidebar-nav-item__icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                <rect x="4" y="3" width="16" height="16" rx="1" stroke="currentColor" stroke-width="1.6" />
                <path d="M4 8.5h16M9 14h2M15 14h2" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
              </svg>
            </span>
            <span class="sidebar-nav-item__label">Quản lý đặt sân</span>
          </router-link>
          <router-link to="/staff/lich-san" class="sidebar-nav-item" :class="{ active: isActive('/staff/lich-san') }">
            <span class="sidebar-nav-item__icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                <rect x="4" y="3" width="16" height="16" rx="1" stroke="currentColor" stroke-width="1.6" />
                <path d="M4 8.5h16M8 12h3M8 16h3M13 12h3M13 16h3" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
              </svg>
            </span>
            <span class="sidebar-nav-item__label">Quản lý lịch sân</span>
          </router-link>
          <router-link to="/staff/thanh-toan" class="sidebar-nav-item" :class="{ active: isActive('/staff/thanh-toan') }">
            <span class="sidebar-nav-item__icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                <rect x="2" y="5" width="20" height="14" rx="1.5" stroke="currentColor" stroke-width="1.6" />
                <path d="M2 9.5h20" stroke="currentColor" stroke-width="1.6" />
              </svg>
            </span>
            <span class="sidebar-nav-item__label">Quản lý thanh toán</span>
          </router-link>
          <router-link to="/staff/hoa-don" class="sidebar-nav-item" :class="{ active: isActive('/staff/hoa-don') }">
            <span class="sidebar-nav-item__icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                <path d="M4 4h14c1.1 0 2 .9 2 2v10c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2Z" stroke="currentColor" stroke-width="1.6" />
                <path d="M8 10h8M8 14h5" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
              </svg>
            </span>
            <span class="sidebar-nav-item__label">Xem hoá đơn</span>
          </router-link>
        </div>

        <div class="sidebar-section">
          <p class="sidebar-section__label">Truyền thông</p>
          <router-link to="/staff/bai-viet" class="sidebar-nav-item" :class="{ active: isActive('/staff/bai-viet') }">
            <span class="sidebar-nav-item__icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2Z" stroke="currentColor" stroke-width="1.6" />
                <path d="M7 7h10M7 11h10M7 15h6" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
              </svg>
            </span>
            <span class="sidebar-nav-item__label">Bài viết & Tin tức</span>
          </router-link>
          <router-link to="/staff/phan-hoi-trang-chu" class="sidebar-nav-item" :class="{ active: isActive('/staff/phan-hoi-trang-chu') }">
            <span class="sidebar-nav-item__icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" />
              </svg>
            </span>
            <span class="sidebar-nav-item__label">Phản hồi Trang chủ</span>
          </router-link>
        </div>

        <div class="sidebar-section">
          <p class="sidebar-section__label">Khách hàng</p>
          <router-link to="/staff/quan-ly-support" class="sidebar-nav-item" :class="{ active: isActive('/staff/quan-ly-support') }">
            <span class="sidebar-nav-item__icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                <path d="M3 10a7 7 0 1 1 14 0 7 7 0 0 1-14 0Zm3 7l-3 3c-.3.3 0 .7.3.7h3.7l4 4c.3.3.7 0 .7-.3v-4.4" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" />
              </svg>
            </span>
            <span class="sidebar-nav-item__label">Hỗ trợ khách hàng</span>
          </router-link>
        </div>

        <div class="sidebar-section">
          <p class="sidebar-section__label">Tài khoản</p>
          <router-link to="/staff/ca-nhan" class="sidebar-nav-item" :class="{ active: isActive('/staff/ca-nhan') }">
            <span class="sidebar-nav-item__icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                <circle cx="12" cy="8" r="4" stroke="currentColor" stroke-width="1.6" />
                <path d="M4 20c0-3.3 3.6-6 8-6s8 2.7 8 6" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
              </svg>
            </span>
            <span class="sidebar-nav-item__label">Thông tin cá nhân</span>
          </router-link>
          <router-link to="/" class="sidebar-nav-item">
            <span class="sidebar-nav-item__icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                <path d="M3 12h16M3 12L9 6M3 12l6 6" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" />
              </svg>
            </span>
            <span class="sidebar-nav-item__label">Quay lại trang chủ</span>
          </router-link>
        </div>
      </nav>
      <div class="dashboard__sidebar-bottom">
        <div class="sidebar-user">
          <div class="sidebar-user__avatar">
            <img v-if="avatarUrl" :src="avatarUrl" alt="Avatar" class="avatar-img-round" />
            <span v-else>{{ userInitial }}</span>
          </div>
          <div class="sidebar-user__info">
            <p class="sidebar-user__name">{{ tenNguoiDung }}</p>
            <p class="sidebar-user__role">Staff</p>
          </div>
        </div>
      </div>
    </aside>

    <div v-if="isMobile && sidebarOpen" class="dashboard__overlay" @click="sidebarOpen = false"></div>

    <div class="dashboard__content">
      <header class="dashboard__header">
        <div class="dashboard__header-left">
          <button
            class="dashboard__btn-menu"
            @click="sidebarOpen = !sidebarOpen"
            v-if="isMobile"
          >
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
              <line x1="3" y1="6" x2="21" y2="6" stroke="currentColor" stroke-width="2" stroke-linecap="round" />
              <line x1="3" y1="12" x2="21" y2="12" stroke="currentColor" stroke-width="2" stroke-linecap="round" />
              <line x1="3" y1="18" x2="21" y2="18" stroke="currentColor" stroke-width="2" stroke-linecap="round" />
            </svg>
          </button>
          <div class="dashboard__breadcrumb">
            <a href="/">DreamLeague</a>
            <span> / {{ breadcrumbLabel }}</span>
          </div>
        </div>
        <div class="dashboard__header-right">
          <button class="dashboard__btn-logout" @click="logout">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
              <path d="M17 12H7M17 12L13 8M17 12L13 8M17 12l-4 4" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" />
              <path d="M15 18H5c-1.1 0-2-.9-2-2V8c0-1.1.9-2 2-2h10" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
            </svg>
            Đăng xuất
          </button>
        </div>
      </header>
      <main class="dashboard__main">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import logo from '../Image/logo DreamLeague.webp'

const router = useRouter()
const route = useRoute()
const sidebarOpen = ref(false)
const isMobile = ref(false)

function getActiveAvatar() {
  const uid = localStorage.getItem('userId')
  if (!uid) return ''
  return localStorage.getItem(`avatar_${uid}`) || ''
}

// ===== AUTH STATE =====
const dangNhap = ref(false)
const tenNguoiDung = ref(localStorage.getItem('hoTen') || 'Nhân viên')
const avatarUrl = ref(getActiveAvatar())
const vaiTro = ref('')
const showDropdown = ref(false)

const userInitial = computed(() => {
  if (!tenNguoiDung.value) return 'S'
  const parts = tenNguoiDung.value.trim().split(' ')
  return parts[parts.length - 1].charAt(0).toUpperCase()
})

const breadcrumbLabel = computed(() => {
  const labels = {
    '/staff': 'Dashboard',
    '/staff/dat-san': 'Quản lý đặt sân',
    '/staff/lich-san': 'Quản lý lịch sân',
    '/staff/thanh-toan': 'Quản lý thanh toán',
    '/staff/hoa-don': 'Xem hoá đơn',
    '/staff/bai-viet': 'Bài viết & Tin tức',
    '/staff/phan-hoi-trang-chu': 'Phản hồi Trang chủ',
    '/staff/quan-ly-support': 'Hỗ trợ khách hàng',
    '/staff/ca-nhan': 'Thông tin cá nhân'
  }
  return labels[route.path] || 'Quản lý'
})

function isActive(path) {
  if (path === '/staff') {
    return route.path === '/staff'
  }
  return route.path.startsWith(path)
}

function handleProfileUpdated(event) {
  if (event.detail && event.detail.hoTen) {
    tenNguoiDung.value = event.detail.hoTen
  } else {
    tenNguoiDung.value = localStorage.getItem('hoTen') || 'Nhân viên'
  }
  avatarUrl.value = getActiveAvatar()
}

function logout() {
  localStorage.removeItem('token')
  localStorage.removeItem('hoTen')
  localStorage.removeItem('isLoggedIn')
  localStorage.removeItem('userRole')
  localStorage.removeItem('vaiTro')

  dangNhap.value = false
  tenNguoiDung.value = ''
  vaiTro.value = ''
  avatarUrl.value = ''
  showDropdown.value = false

  alert('Đã đăng xuất tài khoản thành công!')
  router.push('/dang-nhap')
}

function handleResize() {
  isMobile.value = window.innerWidth < 768
  if (!isMobile.value) sidebarOpen.value = false
}

onMounted(() => {
  // Dọn dẹp key cũ
  localStorage.removeItem('user_avatar')
  localStorage.removeItem('admin_avatar')
  localStorage.removeItem('staff_avatar')

  tenNguoiDung.value = localStorage.getItem('hoTen') || 'Nhân viên'
  avatarUrl.value = getActiveAvatar()
  handleResize()
  window.addEventListener('resize', handleResize)
  window.addEventListener('user-profile-updated', handleProfileUpdated)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  window.removeEventListener('user-profile-updated', handleProfileUpdated)
})
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Oswald:wght@500;600;700&family=Manrope:wght@400;500;600;700;800&family=Space+Mono:wght@400;700&display=swap');
</style>

<style scoped>
.dashboard {
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

  display: flex;
  min-height: 100vh;
  background: var(--night-950);
  color: var(--chalk-050);
  font-family: var(--font-body);
}

/* ===== SIDEBAR ===== */
.dashboard__sidebar {
  width: 264px;
  flex-shrink: 0;
  background: var(--night-800);
  border-right: 1px solid rgba(247, 251, 244, 0.08);
  display: flex;
  flex-direction: column;
  height: 100vh;
  position: sticky;
  top: 0;
  overflow-y: auto;
  z-index: 40;
}

.dashboard__sidebar-top { padding: 22px 20px 18px; border-bottom: 1px solid rgba(247, 251, 244, 0.08); }

.dashboard__sidebar-brand {
  display: flex; align-items: center; gap: 10px; text-decoration: none;
  color: var(--chalk-050); font-family: var(--font-display); font-size: 17px; font-weight: 600;
}
.dashboard__sidebar-brand-logo { width: 34px; height: 34px; object-fit: contain; border-radius: 8px; flex-shrink: 0; }
.dashboard__sidebar-brand strong { color: var(--lime-300); font-weight: 700; }

.dashboard__sidebar-nav { flex: 1; padding: 18px 14px; display: flex; flex-direction: column; gap: 22px; }

.sidebar-section { display: flex; flex-direction: column; gap: 3px; }
.sidebar-section__label {
  font-family: var(--font-mono); font-size: 10.5px; font-weight: 700; letter-spacing: .1em;
  text-transform: uppercase; color: var(--lime-300); opacity: .6; margin: 0 10px 6px;
}

.sidebar-nav-item {
  display: flex; align-items: center; gap: 11px; padding: 10px 12px; border-radius: 10px;
  color: var(--chalk-200); text-decoration: none; font-size: 13.5px; font-weight: 500;
  transition: background .15s ease, color .15s ease;
  position: relative;
}
.sidebar-nav-item__icon { display: flex; align-items: center; justify-content: center; flex-shrink: 0; opacity: .85; }
.sidebar-nav-item__label { white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

.sidebar-nav-item:hover { background: rgba(247, 251, 244, 0.05); color: var(--chalk-050); }

.sidebar-nav-item.active {
  background: rgba(35, 147, 90, 0.16);
  color: var(--lime-300);
  font-weight: 700;
}
.sidebar-nav-item.active .sidebar-nav-item__icon { opacity: 1; }
.sidebar-nav-item.active::before {
  content: ''; position: absolute; left: -14px; top: 50%; transform: translateY(-50%);
  width: 3px; height: 60%; background: var(--lime-400); border-radius: 0 3px 3px 0;
}

/* ===== SIDEBAR BOTTOM / USER ===== */
.dashboard__sidebar-bottom { padding: 14px; border-top: 1px solid rgba(247, 251, 244, 0.08); }
.sidebar-user { display: flex; align-items: center; gap: 10px; padding: 8px; border-radius: 10px; background: rgba(247, 251, 244, 0.03); }
.sidebar-user__avatar {
  width: 38px; height: 38px; border-radius: 50%; flex-shrink: 0; overflow: hidden;
  background: linear-gradient(135deg, var(--turf-500), var(--turf-700));
  color: var(--chalk-050); display: flex; align-items: center; justify-content: center;
  font-weight: 700; font-size: 14px; font-family: var(--font-display);
}
.avatar-img-round { width: 100%; height: 100%; object-fit: cover; }
.sidebar-user__info { min-width: 0; }
.sidebar-user__name { margin: 0; font-size: 13px; font-weight: 700; color: var(--chalk-050); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.sidebar-user__role { margin: 1px 0 0; font-size: 11px; color: var(--lime-300); opacity: .75; }

/* ===== MOBILE OVERLAY ===== */
.dashboard__overlay { position: fixed; inset: 0; background: rgba(0, 0, 0, .55); z-index: 35; }

/* ===== CONTENT ===== */
.dashboard__content { flex: 1; display: flex; flex-direction: column; min-width: 0; }

.dashboard__header {
  height: 62px; flex-shrink: 0; padding: 0 26px; background: var(--night-800);
  border-bottom: 1px solid rgba(247, 251, 244, 0.08);
  display: flex; align-items: center; justify-content: space-between;
  position: sticky; top: 0; z-index: 30;
}
.dashboard__header-left { display: flex; align-items: center; gap: 14px; }
.dashboard__btn-menu { border: none; background: none; color: var(--chalk-050); padding: 4px; cursor: pointer; display: flex; }

.dashboard__breadcrumb { font-size: 12.5px; color: var(--chalk-200); opacity: .7; }
.dashboard__breadcrumb a { color: var(--lime-300); text-decoration: none; opacity: 1; }
.dashboard__breadcrumb a:hover { text-decoration: underline; }

.dashboard__header-right { display: flex; align-items: center; gap: 12px; }
.dashboard__btn-logout {
  display: flex; align-items: center; gap: 7px;
  background: rgba(255, 71, 87, 0.1); color: #ff9686; border: 1px solid rgba(255, 71, 87, 0.3);
  padding: 8px 14px; border-radius: 8px; font-size: 12.5px; font-weight: 600; cursor: pointer;
  transition: background .15s ease; font-family: var(--font-body);
}
.dashboard__btn-logout:hover { background: rgba(255, 71, 87, 0.2); }

.dashboard__main { flex: 1; padding: 22px; overflow-y: auto; }

/* ===== RESPONSIVE ===== */
@media (max-width: 768px) {
  .dashboard__sidebar {
    position: fixed; left: 0; top: 0; height: 100vh;
    transform: translateX(-100%); transition: transform .22s ease; z-index: 45;
  }
  .dashboard__sidebar.open { transform: translateX(0); }
  .dashboard__main { padding: 14px; }
  .dashboard__header { padding: 0 16px; }
}
</style>