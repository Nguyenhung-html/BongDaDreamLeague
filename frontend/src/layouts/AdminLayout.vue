<template>
  <div class="admin-layout">
    <aside class="sidebar">
      <router-link to="/admin" class="sidebar__brand">
        <svg width="30" height="30" viewBox="0 0 36 36" fill="none">
          <circle cx="18" cy="18" r="17" stroke="var(--green-600)" stroke-width="2" />
          <path d="M10 13L18 8L26 13L24 22L18 26L12 22L10 13Z" fill="var(--navy-900)" />
          <circle cx="18" cy="17" r="4.2" fill="var(--white)" />
        </svg>
        <span>DreamLeague</span>
      </router-link>

      <nav class="sidebar__nav">
        <div class="sidebar__group">
          <p class="sidebar__group-title">Quản lý</p>
          <router-link to="/admin" class="sidebar__item" :class="{ active: isActive('/admin', true) }">
            <svg viewBox="0 0 24 24" fill="none"><rect x="3" y="3" width="7" height="7" rx="1" stroke="currentColor" stroke-width="1.7"/><rect x="14" y="3" width="7" height="7" rx="1" stroke="currentColor" stroke-width="1.7"/><rect x="3" y="14" width="7" height="7" rx="1" stroke="currentColor" stroke-width="1.7"/><rect x="14" y="14" width="7" height="7" rx="1" stroke="currentColor" stroke-width="1.7"/></svg>
            Dashboard
          </router-link>
          <router-link to="/admin/khach-hang" class="sidebar__item" :class="{ active: isActive('/admin/khach-hang') }">
            <svg viewBox="0 0 24 24" fill="none"><circle cx="9" cy="8" r="3.2" stroke="currentColor" stroke-width="1.7"/><path d="M3 20c0-3.3 2.7-5.5 6-5.5s6 2.2 6 5.5" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/><path d="M16 8.5c1.4 0 2.5 1.1 2.5 2.5S17.4 13.5 16 13.5" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/><path d="M17 14.8c2 .4 3.5 1.9 3.5 4" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/></svg>
            Quản lý khách hàng
          </router-link>
          <router-link to="/admin/nhan-vien" class="sidebar__item" :class="{ active: isActive('/admin/nhan-vien') }">
            <svg viewBox="0 0 24 24" fill="none"><circle cx="9" cy="8" r="3.2" stroke="currentColor" stroke-width="1.7"/><path d="M3 20c0-3.3 2.7-5.5 6-5.5s6 2.2 6 5.5" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/><path d="M15 9l1.6 1.6L20 7" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round"/></svg>
            Quản lý nhân viên
          </router-link>
          <router-link to="/admin/san-bong" class="sidebar__item" :class="{ active: isActive('/admin/san-bong') }">
            <svg viewBox="0 0 24 24" fill="none"><rect x="2" y="3" width="20" height="18" rx="2" stroke="currentColor" stroke-width="1.7"/><line x1="12" y1="3" x2="12" y2="21" stroke="currentColor" stroke-width="1.7"/><circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="1.7"/></svg>
            Quản lý sân bóng
          </router-link>
          <router-link to="/admin/dat-san" class="sidebar__item" :class="{ active: isActive('/admin/dat-san') }">
            <svg viewBox="0 0 24 24" fill="none"><rect x="4" y="3" width="16" height="18" rx="2" stroke="currentColor" stroke-width="1.7"/><path d="M4 8.5h16M9 14h2M15 14h2" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/></svg>
            Quản lý đặt sân
          </router-link>
        </div>

        <div class="sidebar__group">
          <p class="sidebar__group-title">Kinh doanh</p>
          <router-link to="/admin/thanh-toan" class="sidebar__item" :class="{ active: isActive('/admin/thanh-toan') }">
            <svg viewBox="0 0 24 24" fill="none"><rect x="2" y="5" width="20" height="14" rx="2" stroke="currentColor" stroke-width="1.7"/><path d="M2 9.5h20" stroke="currentColor" stroke-width="1.7"/></svg>
            Quản lý thanh toán
          </router-link>
          <router-link to="/admin/hoa-don" class="sidebar__item" :class="{ active: isActive('/admin/hoa-don') }">
            <svg viewBox="0 0 24 24" fill="none"><path d="M6 2h12v20l-3-2-3 2-3-2-3 2V2Z" stroke="currentColor" stroke-width="1.7" stroke-linejoin="round"/><path d="M9 7h6M9 11h6" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/></svg>
            Quản lý hóa đơn
          </router-link>
          <router-link to="/admin/san-pham" class="sidebar__item" :class="{ active: isActive('/admin/san-pham') }">
            <svg viewBox="0 0 24 24" fill="none"><path d="M5 8h14l-1.4 10.3a2 2 0 0 1-2 1.7H8.4a2 2 0 0 1-2-1.7L5 8Z" stroke="currentColor" stroke-width="1.7" stroke-linejoin="round"/><path d="M9 8V6a3 3 0 0 1 6 0v2" stroke="currentColor" stroke-width="1.7"/></svg>
            Quản lý dịch vụ
          </router-link>
          <router-link to="/admin/voucher" class="sidebar__item" :class="{ active: isActive('/admin/voucher') }">
            <svg viewBox="0 0 24 24" fill="none"><path d="M20 12a2 2 0 0 0 0-4V5a2 2 0 0 0-2-2H6a2 2 0 0 0 0 4 2 2 0 0 0 0 4 2 2 0 0 0 0 4 2 2 0 0 0 0 4h12a2 2 0 0 0 2-2v-3a2 2 0 0 0 0-4Z" stroke="currentColor" stroke-width="1.7"/></svg>
            Quản lý Voucher
          </router-link>
          <router-link to="/admin/doanh-thu" class="sidebar__item" :class="{ active: isActive('/admin/doanh-thu') }">
            <svg viewBox="0 0 24 24" fill="none"><path d="M4 19V10M10 19V5M16 19v-7M22 19H2" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/></svg>
            Thống kê & doanh thu
          </router-link>
        </div>

        <div class="sidebar__group">
          <p class="sidebar__group-title">Nội dung</p>
          <router-link to="/admin/thong-bao" class="sidebar__item" :class="{ active: isActive('/admin/thong-bao') }">
            <svg viewBox="0 0 24 24" fill="none"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" stroke="currentColor" stroke-width="1.7"/><path d="M13.73 21a2 2 0 0 1-3.46 0" stroke="currentColor" stroke-width="1.7"/></svg>
            Quản lý thông báo
          </router-link>
          <router-link to="/admin/danh-gia" class="sidebar__item" :class="{ active: isActive('/admin/danh-gia') }">
            <svg viewBox="0 0 24 24" fill="none"><path d="M12 2.5l3 6.4 6.7.7-5 4.6 1.4 6.8L12 17.7 5.9 21l1.4-6.8-5-4.6 6.7-.7 3-6.4Z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/></svg>
            Quản lý đánh giá
          </router-link>
          <router-link 
            to="/admin/danh-gia-he-thong" 
            class="sidebar__item" 
            :class="{ active: isActive('/admin/danh-gia-he-thong') }"
          >
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            Q.Lý đánh giá hệ thống
          </router-link>
          <!-- Thêm vào bên trong <div class="sidebar__group"> của nhóm Nội dung -->
          <router-link to="/admin/bai-viet" class="sidebar__item" :class="{ active: isActive('/admin/bai-viet') }">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z" fill="currentColor"/>
            </svg>
            Quản lý bài viết
          </router-link>
        </div>

        <div class="sidebar-section">
          <p class="sidebar-section__label">KHÁCH HÀNG</p>
          <router-link to="/admin/quan-ly-support" class="sidebar-nav-item" :class="{ active: isActive('/admin/quan-ly-support') }">
            <span class="sidebar-nav-item__icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                <path d="M3 10a7 7 0 1 1 14 0 7 7 0 0 1-14 0Zm3 7l-3 3c-.3.3 0 .7.3.7h3.7l4 4c.3.3.7 0 .7-.3v-4.4" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" />
              </svg>
            </span>
            <span class="sidebar-nav-item__label">Hỗ trợ khách hàng</span>
          </router-link>
        </div>

        <div class="sidebar__group">
          <p class="sidebar__group-title">Tài khoản</p>
          <router-link to="/admin/ca-nhan" class="sidebar__item" :class="{ active: isActive('/admin/ca-nhan') }">
            <svg viewBox="0 0 24 24" fill="none"><circle cx="12" cy="8" r="3.5" stroke="currentColor" stroke-width="1.7"/><path d="M4 20c0-4 3.6-7 8-7s8 3 8 7" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/></svg>
            Thông tin cá nhân
          </router-link>
          <router-link to="/" class="sidebar__item">
            <svg viewBox="0 0 24 24" fill="none"><path d="M19 12H5M5 12l7 7M5 12l7-7" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round"/></svg>
            Quay lại trang chủ
          </router-link>
        </div>

        
      </nav>

      <div class="sidebar__user">
        <div class="sidebar__avatar">
          <img v-if="avatarUrl" :src="avatarUrl" alt="Avatar" class="avatar-img-round" />
          <span v-else>{{ tenVietTat }}</span>
        </div>
        <div>
          <p class="sidebar__user-name">{{ tenNguoiDung }}</p>
          <p class="sidebar__user-role">Admin</p>
        </div>
      </div>
    </aside>

    <div class="admin-main">
      <header class="admin-topbar">
        <p class="admin-breadcrumb">DreamLeague / <span>{{ tieuDeTrang }}</span></p>
        <button class="btn-dang-xuat" @click="dangXuat">Đăng xuất</button>
      </header>

      <main class="admin-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

function getActiveAvatar() {
  const uid = localStorage.getItem('userId')
  if (!uid) return ''
  return localStorage.getItem(`avatar_${uid}`) || ''
}

const tenNguoiDung = ref(localStorage.getItem('hoTen') || 'Quản trị viên')
const avatarUrl = ref(getActiveAvatar())

const tenVietTat = computed(() => {
  if (!tenNguoiDung.value) return 'A'
  const ten = tenNguoiDung.value.trim().split(' ')
  return ten[ten.length - 1].charAt(0).toUpperCase()
})

const tieuDeTrang = computed(() => route.meta?.title || route.name || '')

function handleProfileUpdated(event) {
  if (event.detail && event.detail.hoTen) {
    tenNguoiDung.value = event.detail.hoTen
  } else {
    tenNguoiDung.value = localStorage.getItem('hoTen') || 'Quản trị viên'
  }
  avatarUrl.value = getActiveAvatar()
}

onMounted(() => {
  // Dọn dẹp key cũ
  localStorage.removeItem('user_avatar')
  localStorage.removeItem('admin_avatar')
  localStorage.removeItem('staff_avatar')

  tenNguoiDung.value = localStorage.getItem('hoTen') || 'Quản trị viên'
  avatarUrl.value = getActiveAvatar()
  window.addEventListener('user-profile-updated', handleProfileUpdated)
})

onUnmounted(() => {
  window.removeEventListener('user-profile-updated', handleProfileUpdated)
})

// exact: chỉ khớp đúng path (dùng cho Dashboard vì "/admin" là tiền tố của mọi route con)
// mặc định: khớp cả path con, phòng khi trang gộp (thanh toán & hoá đơn, đánh giá & bình luận)
// có thêm tab/sub-route sau này, ví dụ /admin/thanh-toan-hoa-don/hoa-don
function isActive(path, exact = false) {
  if (exact) return route.path === path
  return route.path === path || route.path.startsWith(path + '/')
}

function dangXuat() {
  localStorage.removeItem('token')
  localStorage.removeItem('hoTen')
  localStorage.removeItem('isLoggedIn')
  localStorage.removeItem('userRole')
  router.push('/dang-nhap')
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: #f0f4f8;
  font-family: var(--font-display, system-ui);
}

/* ===== SIDEBAR ===== */
.sidebar {
  width: 250px;
  flex-shrink: 0;
  background: white;
  border-right: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
  padding: 20px 14px;
  position: sticky;
  top: 0;
  height: 100vh;
  overflow-y: auto;
}
.sidebar__brand {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 10px 20px;
  text-decoration: none;
}
.sidebar__brand span {
  font-size: 17px;
  font-weight: 800;
  color: var(--navy-900, #0d1f3c);
}
.sidebar__nav { flex: 1; display: flex; flex-direction: column; gap: 20px; }
.sidebar__group-title {
  font-size: 11px;
  font-weight: 700;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: .5px;
  padding: 0 10px 6px;
}
.sidebar__item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 9px 10px;
  border-radius: 8px;
  font-size: 13.5px;
  font-weight: 500;
  color: #475569;
  text-decoration: none;
  transition: background .15s, color .15s;
}
.sidebar__item:hover { background: #f0fdf4; color: var(--green-700, #15803d); }
.sidebar__item.active { background: var(--green-50, #f0fdf4); color: var(--green-700, #15803d); font-weight: 700; }
.sidebar__item svg { width: 17px; height: 17px; flex-shrink: 0; }

.sidebar__user {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 10px;
  border-top: 1px solid #f1f5f9;
  margin-top: 10px;
}
.sidebar__avatar {
  width: 34px; height: 34px; border-radius: 50%;
  background: var(--green-600, #16a34a); color: white;
  display: flex; align-items: center; justify-content: center;
  font-size: 14px; font-weight: 700; flex-shrink: 0;
  overflow: hidden;
}
.sidebar__avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.sidebar__user-name { font-size: 13.5px; font-weight: 700; color: #0f172a; }
.sidebar__user-role { font-size: 11.5px; color: #94a3b8; }

/* ===== MAIN CONTENT ===== */
.admin-main { flex: 1; display: flex; flex-direction: column; min-width: 0; }
.admin-topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 28px;
  background: white;
  border-bottom: 1px solid #e2e8f0;
}
.admin-breadcrumb { font-size: 13.5px; color: #94a3b8; }
.admin-breadcrumb span { color: #0f172a; font-weight: 600; }
.btn-dang-xuat {
  padding: 8px 18px; border-radius: 8px; border: 1px solid #e2e8f0;
  background: white; color: #475569; font-size: 13.5px; font-weight: 600;
  cursor: pointer; transition: .15s;
}
.btn-dang-xuat:hover { background: #f8fafc; border-color: #cbd5e1; }
.admin-content { flex: 1; padding: 28px; }

@media (max-width: 960px) {
  .sidebar { display: none; }
}

</style>