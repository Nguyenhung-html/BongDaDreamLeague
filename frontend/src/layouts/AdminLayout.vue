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
          <router-link to="/admin" class="sidebar-nav-item" :class="{ active: isActive('/admin') }">
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
          <router-link to="/admin/khach-hang" class="sidebar-nav-item" :class="{ active: isActive('/admin/khach-hang') }">
            <span class="sidebar-nav-item__icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                <circle cx="8" cy="8" r="3.5" stroke="currentColor" stroke-width="1.6" />
                <path d="M2 18c0-2.21 2.69-4 6-4s6 1.79 6 4" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
                <path d="M16 8c0 2.21 1.34 4 3 4s3-1.79 3-4" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
                <path d="M16 14c-1.8 0-3.48.4-4.66 1.12" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
              </svg>
            </span>
            <span class="sidebar-nav-item__label">Quản lý khách hàng</span>
          </router-link>
          <router-link to="/admin/nhan-vien" class="sidebar-nav-item" :class="{ active: isActive('/admin/nhan-vien') }">
            <span class="sidebar-nav-item__icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                <circle cx="12" cy="7" r="3" stroke="currentColor" stroke-width="1.6" />
                <path d="M3 18c0-2.21 3.13-4 9-4s9 1.79 9 4" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
              </svg>
            </span>
            <span class="sidebar-nav-item__label">Quản lý nhân viên</span>
          </router-link>
          <router-link to="/admin/san-bong" class="sidebar-nav-item" :class="{ active: isActive('/admin/san-bong') }">
            <span class="sidebar-nav-item__icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                <rect x="3" y="6" width="18" height="12" rx="1.5" stroke="currentColor" stroke-width="1.6" />
                <line x1="12" y1="6" x2="12" y2="18" stroke="currentColor" stroke-width="1.6" />
                <circle cx="7" cy="12" r="1.5" fill="currentColor" />
                <circle cx="17" cy="12" r="1.5" fill="currentColor" />
              </svg>
            </span>
            <span class="sidebar-nav-item__label">Quản lý sân bóng</span>
          </router-link>
        </div>
        <div class="sidebar-section">
          <p class="sidebar-section__label">KINH DOANH</p>
          <router-link to="/admin/dat-san" class="sidebar-nav-item" :class="{ active: isActive('/admin/dat-san') }">
            <span class="sidebar-nav-item__icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                <rect x="4" y="3" width="16" height="16" rx="1" stroke="currentColor" stroke-width="1.6" />
                <path d="M4 8.5h16M9 14h2M15 14h2" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
              </svg>
            </span>
            <span class="sidebar-nav-item__label">Quản lý đặt sân</span>
          </router-link>
          <router-link to="/admin/thanh-toan" class="sidebar-nav-item" :class="{ active: isActive('/admin/thanh-toan') }">
            <span class="sidebar-nav-item__icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                <rect x="2" y="5" width="20" height="14" rx="1.5" stroke="currentColor" stroke-width="1.6" />
                <path d="M2 9.5h20" stroke="currentColor" stroke-width="1.6" />
              </svg>
            </span>
            <span class="sidebar-nav-item__label">Quản lý thanh toán</span>
          </router-link>
          <router-link to="/admin/hoa-don" class="sidebar-nav-item" :class="{ active: isActive('/admin/hoa-don') }">
            <span class="sidebar-nav-item__icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                <path d="M4 4h14c1.1 0 2 .9 2 2v10c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2Z" stroke="currentColor" stroke-width="1.6" />
                <path d="M8 10h8M8 14h5" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
              </svg>
            </span>
            <span class="sidebar-nav-item__label">Quản lý hoá đơn</span>
          </router-link>
          <router-link to="/admin/doanh-thu" class="sidebar-nav-item" :class="{ active: isActive('/admin/doanh-thu') }">
            <span class="sidebar-nav-item__icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                <path d="M3 18h18M3 14h5M3 10h8M14 6h7" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
              </svg>
            </span>
            <span class="sidebar-nav-item__label">Thống kê & doanh thu</span>
          </router-link>
        </div>
        <div class="sidebar-section">
          <p class="sidebar-section__label">NỘI DUNG</p>
          <router-link to="/admin/thong-bao" class="sidebar-nav-item" :class="{ active: isActive('/admin/thong-bao') }">
            <span class="sidebar-nav-item__icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                <path d="M5.5 11c0-2.5 1.1-4.8 2.9-6.3M18.5 11c0 2.5-1.1 4.8-2.9 6.3M3 11a9 9 0 0 1 18 0c0 3.3-2.4 6-5.5 6.5" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
              </svg>
            </span>
            <span class="sidebar-nav-item__label">Quản lý thông báo</span>
          </router-link>
          <router-link to="/admin/danh-gia" class="sidebar-nav-item" :class="{ active: isActive('/admin/danh-gia') }">
            <span class="sidebar-nav-item__icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                <path d="M12 2.5L15.6 9L23 9.9L17.3 15.1L18.6 23L12 19.3L5.4 23L6.7 15.1L1 9.9L8.4 9L12 2.5Z" stroke="currentColor" stroke-width="1.6" stroke-linejoin="round" />
              </svg>
            </span>
            <span class="sidebar-nav-item__label">Quản lý đánh giá</span>
          </router-link>
          <router-link to="/admin/binh-luan" class="sidebar-nav-item" :class="{ active: isActive('/admin/binh-luan') }">
            <span class="sidebar-nav-item__icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                <path d="M3 10a7 7 0 1 1 14 0 7 7 0 0 1-14 0Zm3 7l-3 3c-.3.3 0 .7.3.7h3.7l4 4c.3.3.7 0 .7-.3v-4.4" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" />
              </svg>
            </span>
            <span class="sidebar-nav-item__label">Quản lý bình luận</span>
          </router-link>
        </div>
        <div class="sidebar-section">
          <p class="sidebar-section__label">TÀI KHOẢN</p>
          <router-link to="/admin/ca-nhan" class="sidebar-nav-item" :class="{ active: isActive('/admin/ca-nhan') }">
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
            <p class="sidebar-user__name">Quản trị viên</p>
            <p class="sidebar-user__role">Admin</p>
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
const userInitial = computed(() => 'Q')

const breadcrumbLabel = computed(() => {
  const labels = {
    '/admin': 'Dashboard',
    '/admin/khach-hang': 'Quản lý khách hàng',
    '/admin/nhan-vien': 'Quản lý nhân viên',
    '/admin/san-bong': 'Quản lý sân bóng',
    '/admin/dat-san': 'Quản lý đặt sân',
    '/admin/thanh-toan': 'Quản lý thanh toán',
    '/admin/hoa-don': 'Quản lý hoá đơn',
    '/admin/doanh-thu': 'Thống kê & doanh thu',
    '/admin/thong-bao': 'Quản lý thông báo',
    '/admin/danh-gia': 'Quản lý đánh giá',
    '/admin/binh-luan': 'Quản lý bình luận',
    '/admin/ca-nhan': 'Thông tin cá nhân'
  }
  return labels[route.path] || 'Quản trị'
})

function isActive(path) {
  // Nếu path kiểm tra là '/admin', chỉ active khi trùng khớp hoàn toàn (không ăn theo các trang con)
  if (path === '/admin') {
    return route.path === '/admin'
  }
  // Các trang con khác thì vẫn dùng startsWith bình thường
  return route.path.startsWith(path)
}

function logout() {
  localStorage.removeItem('dreamleague_user')
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