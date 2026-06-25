<template>
  <div class="dashboard">
    <aside class="dashboard__sidebar" :class="{ open: sidebarOpen }">
      <div class="dashboard__sidebar-top">
        <router-link to="/" class="dashboard__sidebar-brand">
          <svg width="28" height="28" viewBox="0 0 36 36" fill="none">
            <circle cx="18" cy="18" r="17" stroke="var(--green-600)" stroke-width="2" />
            <path d="M10 13L18 8L26 13L24 22L18 26L12 22L10 13Z" fill="var(--navy-900)" />
            <circle cx="18" cy="17" r="4.2" fill="var(--white)" />
          </svg>
          <span><strong>Dream</strong>League</span>
        </router-link>
      </div>
      <nav class="dashboard__sidebar-nav">
        <div class="sidebar-section">
          <p class="sidebar-section__label">QUẢN LÝ</p>
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
          <p class="sidebar-section__label">KHÁCH HÀNG</p>
          <router-link to="/staff/ho-tro" class="sidebar-nav-item" :class="{ active: isActive('/staff/ho-tro') }">
            <span class="sidebar-nav-item__icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                <path d="M3 10a7 7 0 1 1 14 0 7 7 0 0 1-14 0Zm3 7l-3 3c-.3.3 0 .7.3.7h3.7l4 4c.3.3.7 0 .7-.3v-4.4" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" />
              </svg>
            </span>
            <span class="sidebar-nav-item__label">Hỗ trợ khách hàng</span>
          </router-link>
        </div>
        <div class="sidebar-section">
          <p class="sidebar-section__label">TÀI KHOẢN</p>
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
          <div class="sidebar-user__avatar">{{ userInitial }}</div>
          <div class="sidebar-user__info">
            <p class="sidebar-user__name">Nhân viên</p>
            <p class="sidebar-user__role">Staff</p>
          </div>
        </div>
      </div>
    </aside>
    <div class="dashboard__content">
      <header class="dashboard__header">
        <div class="dashboard__header-left">
          <button class="dashboard__btn-logout" @click="sidebarOpen = !sidebarOpen" v-if="isMobile" style="border: none; background: none; padding: 0;">
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
              <path d="M17 12H7M17 12L13 8M17 12l-4 4" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" />
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

const router = useRouter()
const route = useRoute()

const sidebarOpen = ref(false)
const isMobile = ref(false)
const userInitial = computed(() => 'N')

// ===== AUTH STATE =====
const dangNhap = ref(false)
const tenNguoiDung = ref('')
const vaiTro = ref('')
const showDropdown = ref(false)

const breadcrumbLabel = computed(() => {
  const labels = {
    '/staff': 'Dashboard',
    '/staff/dat-san': 'Quản lý đặt sân',
    '/staff/lich-san': 'Quản lý lịch sân',
    '/staff/thanh-toan': 'Quản lý thanh toán',
    '/staff/hoa-don': 'Xem hoá đơn',
    '/staff/ho-tro': 'Hỗ trợ khách hàng',
    '/staff/ca-nhan': 'Thông tin cá nhân'
  }
  return labels[route.path] || 'Quản lý'
})

function isActive(path) {
  // Nếu path cần kiểm tra là trang Dashboard gốc '/staff'
  if (path === '/staff') {
    return route.path === '/staff' // Chỉ active khi khớp hoàn toàn 100%
  }
  // Các trang con khác thì giữ nguyên logic cũ
  return route.path.startsWith(path)
}

function logout() {
  localStorage.removeItem('token')
  localStorage.removeItem('hoTen')
  localStorage.removeItem('isLoggedIn')
  localStorage.removeItem('userRole')
  localStorage.removeItem('vaiTro') // Xóa nốt cả key cũ phòng hờ

  // Đưa các biến cục bộ về trạng thái ban đầu để giao diện biến đổi ngay lập tức
  dangNhap.value = false
  tenNguoiDung.value = ''
  vaiTro.value = ''
  showDropdown.value = false

  alert('Đã đăng xuất tài khoản thành công!')
  router.push('/dang-nhap')
}

function handleResize() {
  isMobile.value = window.innerWidth < 768
}

onMounted(() => {
  handleResize()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})
</script>