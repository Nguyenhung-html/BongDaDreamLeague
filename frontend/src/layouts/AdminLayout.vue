<template>
  <div class="admin-layout">
    <!-- ===== LỚP NỀN HIỆU ỨNG: đèn pha + hạt sáng + đạo cụ bay bay, đồng bộ trang chủ ===== -->
    <div class="admin-ambient" aria-hidden="true">
      <div class="admin-floodlight admin-floodlight--l"></div>
      <div class="admin-floodlight admin-floodlight--r"></div>
      <!-- Hạt sáng bay lên -->
      <div class="admin-motes">
        <span v-for="n in 10" :key="n" class="admin-mote" :style="{ '--i': n }"></span>
      </div>

      <!-- Hạt rơi nhẹ xuống, tạo chiều sâu — đồng bộ Staff Dashboard -->
      <div class="admin-falling">
        <span v-for="n in 16" :key="'f'+n" class="admin-falling__item" :style="{ '--i': n }"></span>
      </div>

      <!-- Đạo cụ bóng đá trôi nổi (bay bay) -->
      <div class="admin-props">
        <div class="admin-prop admin-prop--ball1"><svg viewBox="0 0 40 40"><circle cx="20" cy="20" r="18" fill="var(--chalk-050)" stroke="var(--night-950)" stroke-width="1.4"/><polygon points="20,10 26,14.5 24,21.5 16,21.5 14,14.5" fill="var(--night-950)"/></svg></div>
        <div class="admin-prop admin-prop--ball2"><svg viewBox="0 0 40 40"><circle cx="20" cy="20" r="18" fill="var(--chalk-050)" stroke="var(--night-950)" stroke-width="1.4"/><polygon points="20,10 26,14.5 24,21.5 16,21.5 14,14.5" fill="var(--night-950)"/></svg></div>
        <div class="admin-prop admin-prop--whistle"><svg viewBox="0 0 48 32" fill="none"><path d="M14 16a10 10 0 1 0 20 0 10 10 0 0 0-20 0Z" stroke="var(--lime-300)" stroke-width="1.6"/><circle cx="24" cy="16" r="3.2" fill="var(--lime-300)"/></svg></div>
        <div class="admin-prop admin-prop--flag"><svg viewBox="0 0 30 46" fill="none"><path d="M4 44V4" stroke="var(--lime-300)" stroke-width="1.8" stroke-linecap="round"/><path d="M4 5l20 6-20 6V5Z" fill="var(--lime-300)" opacity=".85"/></svg></div>
        <div class="admin-prop admin-prop--trophy"><svg viewBox="0 0 40 46" fill="none"><path d="M11 6h18v10a9 9 0 0 1-18 0V6Z" stroke="var(--amber-400)" stroke-width="1.6"/><path d="M20 25v6M13 37h14l-1.6-4.5a2 2 0 0 0-1.9-1.5h-6.9a2 2 0 0 0-1.9 1.5L13 37Z" stroke="var(--amber-400)" stroke-width="1.6" stroke-linejoin="round"/></svg></div>
        <div class="admin-prop admin-prop--boot"><svg viewBox="0 0 48 34" fill="none"><path d="M8 6v11.5c0 1.5.8 2.4 2 3.2l9 5.8c1 .7 2.2 1 3.4 1h16.4c1.8 0 2.9-2 1.9-3.5l-3-4.5a6 6 0 0 0-3.8-2.5l-8.4-1.7a3 3 0 0 1-2-1.4L19.5 6.6A3 3 0 0 0 17 5h-6a3 3 0 0 0-3 1Z" stroke="var(--lime-300)" stroke-width="1.6" stroke-linejoin="round"/><path d="M8 22.5h30" stroke="var(--lime-300)" stroke-width="1.6"/></svg></div>
      </div>
    </div>

    <aside class="sidebar">
      <router-link to="/admin" class="sidebar__brand">
        <span class="sidebar__brand-glow" aria-hidden="true"></span>
        <img :src="logo" alt="DreamLeague" class="sidebar__brand-icon" />
        <span>DreamLeague</span>
      </router-link>

      <nav class="sidebar__nav">
        <div class="sidebar__group">
          <p class="sidebar__group-title">Quản lý</p>
          <router-link to="/admin" class="sidebar__item" :class="{ active: isActive('/admin', true) }">
            <span class="sidebar__item-icon"><svg viewBox="0 0 24 24" fill="none"><rect x="3" y="3" width="7" height="7" rx="1" stroke="currentColor" stroke-width="1.7"/><rect x="14" y="3" width="7" height="7" rx="1" stroke="currentColor" stroke-width="1.7"/><rect x="3" y="14" width="7" height="7" rx="1" stroke="currentColor" stroke-width="1.7"/><rect x="14" y="14" width="7" height="7" rx="1" stroke="currentColor" stroke-width="1.7"/></svg></span>
            Dashboard
          </router-link>
          <router-link to="/admin/khach-hang" class="sidebar__item" :class="{ active: isActive('/admin/khach-hang') }">
            <span class="sidebar__item-icon"><svg viewBox="0 0 24 24" fill="none"><circle cx="9" cy="8" r="3.2" stroke="currentColor" stroke-width="1.7"/><path d="M3 20c0-3.3 2.7-5.5 6-5.5s6 2.2 6 5.5" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/><path d="M16 8.5c1.4 0 2.5 1.1 2.5 2.5S17.4 13.5 16 13.5" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/><path d="M17 14.8c2 .4 3.5 1.9 3.5 4" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/></svg></span>
            Quản lý khách hàng
          </router-link>
          <router-link to="/admin/nhan-vien" class="sidebar__item" :class="{ active: isActive('/admin/nhan-vien') }">
            <span class="sidebar__item-icon"><svg viewBox="0 0 24 24" fill="none"><circle cx="9" cy="8" r="3.2" stroke="currentColor" stroke-width="1.7"/><path d="M3 20c0-3.3 2.7-5.5 6-5.5s6 2.2 6 5.5" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/><path d="M15 9l1.6 1.6L20 7" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round"/></svg></span>
            Quản lý nhân viên
          </router-link>
          <router-link to="/admin/san-bong" class="sidebar__item" :class="{ active: isActive('/admin/san-bong') }">
            <span class="sidebar__item-icon"><svg viewBox="0 0 24 24" fill="none"><rect x="2" y="3" width="20" height="18" rx="2" stroke="currentColor" stroke-width="1.7"/><line x1="12" y1="3" x2="12" y2="21" stroke="currentColor" stroke-width="1.7"/><circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="1.7"/></svg></span>
            Quản lý sân bóng
          </router-link>
        </div>

        <div class="sidebar__group">
          <p class="sidebar__group-title">Kinh doanh</p>
          <router-link to="/admin/san-pham" class="sidebar__item" :class="{ active: isActive('/admin/san-pham') }">
            <span class="sidebar__item-icon"><svg viewBox="0 0 24 24" fill="none"><path d="M5 8h14l-1.4 10.3a2 2 0 0 1-2 1.7H8.4a2 2 0 0 1-2-1.7L5 8Z" stroke="currentColor" stroke-width="1.7" stroke-linejoin="round"/><path d="M9 8V6a3 3 0 0 1 6 0v2" stroke="currentColor" stroke-width="1.7"/></svg></span>
            Quản lý dịch vụ
          </router-link>
          <router-link to="/admin/voucher" class="sidebar__item" :class="{ active: isActive('/admin/voucher') }">
            <span class="sidebar__item-icon"><svg viewBox="0 0 24 24" fill="none"><path d="M20 12a2 2 0 0 0 0-4V5a2 2 0 0 0-2-2H6a2 2 0 0 0 0 4 2 2 0 0 0 0 4 2 2 0 0 0 0 4 2 2 0 0 0 0 4h12a2 2 0 0 0 2-2v-3a2 2 0 0 0 0-4Z" stroke="currentColor" stroke-width="1.7"/></svg></span>
            Quản lý Voucher
          </router-link>
          <router-link to="/admin/doanh-thu" class="sidebar__item" :class="{ active: isActive('/admin/doanh-thu') }">
            <span class="sidebar__item-icon"><svg viewBox="0 0 24 24" fill="none"><path d="M4 19V10M10 19V5M16 19v-7M22 19H2" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/></svg></span>
            Thống kê & doanh thu
          </router-link>
        </div>

        <div class="sidebar__group">
          <p class="sidebar__group-title">Nội dung</p>
          <router-link to="/admin/thong-bao" class="sidebar__item" :class="{ active: isActive('/admin/thong-bao') }">
            <span class="sidebar__item-icon"><svg viewBox="0 0 24 24" fill="none"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" stroke="currentColor" stroke-width="1.7"/><path d="M13.73 21a2 2 0 0 1-3.46 0" stroke="currentColor" stroke-width="1.7"/></svg></span>
            Quản lý thông báo
          </router-link>
          <router-link to="/admin/danh-gia" class="sidebar__item" :class="{ active: isActive('/admin/danh-gia') }">
            <span class="sidebar__item-icon"><svg viewBox="0 0 24 24" fill="none"><path d="M12 2.5l3 6.4 6.7.7-5 4.6 1.4 6.8L12 17.7 5.9 21l1.4-6.8-5-4.6 6.7-.7 3-6.4Z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/></svg></span>
            Quản lý đánh giá
          </router-link>
          <router-link
            to="/admin/danh-gia-he-thong"
            class="sidebar__item"
            :class="{ active: isActive('/admin/danh-gia-he-thong') }"
          >
            <span class="sidebar__item-icon">
              <svg viewBox="0 0 24 24" fill="none">
                <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </span>
            QuảnLý đánh giá hệ thống
          </router-link>
          <router-link to="/admin/bai-viet" class="sidebar__item" :class="{ active: isActive('/admin/bai-viet') }">
            <span class="sidebar__item-icon">
              <svg viewBox="0 0 24 24" fill="none">
                <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z" fill="currentColor"/>
              </svg>
            </span>
            Quản lý bài viết
          </router-link>
        </div>

  

        <div class="sidebar__group">
          <p class="sidebar__group-title">Tài khoản</p>
          <router-link to="/admin/ca-nhan" class="sidebar__item" :class="{ active: isActive('/admin/ca-nhan') }">
            <span class="sidebar__item-icon"><svg viewBox="0 0 24 24" fill="none"><circle cx="12" cy="8" r="3.5" stroke="currentColor" stroke-width="1.7"/><path d="M4 20c0-4 3.6-7 8-7s8 3 8 7" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/></svg></span>
            Thông tin cá nhân
          </router-link>
          <router-link to="/" class="sidebar__item sidebar__item--exit">
            <span class="sidebar__item-icon"><svg viewBox="0 0 24 24" fill="none"><path d="M19 12H5M5 12l7 7M5 12l7-7" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round"/></svg></span>
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
        <p class="admin-breadcrumb">DreamLeague <span class="admin-breadcrumb__sep">/</span> <span>{{ tieuDeTrang }}</span></p>
        <button class="btn-dang-xuat" @click="dangXuat">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none"><path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/><path d="M10 17l5-5-5-5M15 12H3" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/></svg>
          Đăng xuất
        </button>
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
import logo from '../Image/logo DreamLeague.webp'

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
/* ===== TOKENS: đồng bộ tông "sân đêm" với trang chủ ===== */
.admin-layout {
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
  --font-display: 'Oswald', 'Manrope', system-ui, sans-serif;
  --font-body: 'Manrope', system-ui, sans-serif;
  --font-mono: 'Space Mono', monospace;

  position: relative;
  display: flex;
  min-height: 100vh;
  background: var(--night-950);
  font-family: var(--font-body);
  overflow: hidden;
}

/* ===== NỀN HIỆU ỨNG: đèn pha + hạt sáng bay ===== */
.admin-ambient { position: fixed; inset: 0; z-index: 0; pointer-events: none; overflow: hidden; }
.admin-floodlight {
  position: absolute; top: -12%; width: 55vh; height: 145vh;
  background: conic-gradient(from 90deg at 50% 0%, transparent 42%, rgba(255,244,214,.07) 50%, transparent 58%);
  mix-blend-mode: screen;
}
.admin-floodlight--l { left: -14%; animation: admin-sweep 12s ease-in-out infinite alternate; }
.admin-floodlight--r { right: -14%; animation: admin-sweep 12s ease-in-out infinite alternate-reverse; }
@keyframes admin-sweep { 0% { transform: rotate(-8deg); } 100% { transform: rotate(8deg); } }
.admin-motes { position: absolute; inset: 0; }
.admin-mote {
  position: absolute; bottom: -10px; left: calc((var(--i) * 9.6%) + 1%);
  width: 3px; height: 3px; border-radius: 50%; background: var(--lime-300);
  opacity: 0; animation: admin-rise 10s linear infinite;
  animation-delay: calc(var(--i) * -0.9s);
}
@keyframes admin-rise {
  0% { transform: translateY(0) scale(.6); opacity: 0; }
  10% { opacity: .5; }
  90% { opacity: .12; }
  100% { transform: translateY(-100vh) scale(1.1); opacity: 0; }
}

/* Hạt rơi nhẹ xuống (rơi rơi) — đồng bộ Staff Dashboard */
.admin-falling { position: absolute; inset: 0; overflow: hidden; }
.admin-falling__item {
  position: absolute; top: -6%; left: calc((var(--i) * 6.2%) + 1%);
  width: 3px; height: 3px; border-radius: 50%; background: var(--chalk-050);
  opacity: 0; animation: admin-fall linear infinite;
  animation-duration: calc(7s + (var(--i) * 0.45s));
  animation-delay: calc(var(--i) * -0.6s);
}
@keyframes admin-fall {
  0% { transform: translateY(0) translateX(0); opacity: 0; }
  8% { opacity: .55; }
  50% { transform: translateY(48vh) translateX(14px); }
  92% { opacity: .12; }
  100% { transform: translateY(100vh) translateX(-10px); opacity: 0; }
}
@media (prefers-reduced-motion: reduce) {
  .admin-floodlight, .admin-mote, .admin-prop, .admin-falling__item { animation: none; }
}

/* Đạo cụ bóng đá trôi nổi (bay bay) — đồng bộ hiệu ứng Staff Dashboard */
.admin-props { position: absolute; inset: 0; }
.admin-prop { position: absolute; opacity: .16; animation: admin-prop-float 7s ease-in-out infinite; filter: drop-shadow(0 0 6px rgba(182,255,60,.15)); }
.admin-prop svg { width: 100%; height: 100%; }
.admin-prop--ball1 { width: 46px; height: 46px; top: 8%; right: 10%; }
.admin-prop--ball2 { width: 26px; height: 26px; bottom: 16%; left: 34%; opacity: .13; animation-delay: .9s; animation-duration: 8.5s; }
.admin-prop--whistle { width: 56px; height: 38px; top: 58%; right: 6%; animation-delay: .5s; }
.admin-prop--flag { width: 32px; height: 48px; top: 26%; left: 4%; animation-delay: 1.1s; }
.admin-prop--trophy { width: 40px; height: 46px; bottom: 8%; right: 28%; animation-delay: 1.6s; }
.admin-prop--boot { width: 60px; height: 42px; top: 4%; left: 30%; opacity: .13; animation-delay: 2s; animation-duration: 9s; }
@keyframes admin-prop-float { 0%, 100% { transform: translateY(0) rotate(-3deg); } 50% { transform: translateY(-16px) rotate(3deg); } }

/* ===== SIDEBAR ===== */
.sidebar {
  position: relative;
  z-index: 1;
  width: 258px;
  flex-shrink: 0;
  background: linear-gradient(180deg, rgba(10,31,19,.96), rgba(5,11,8,.98));
  border-right: 1px solid rgba(182,255,60,.12);
  display: flex;
  flex-direction: column;
  padding: 20px 14px;
  position: sticky;
  top: 0;
  height: 100vh;
  overflow-y: auto;
  backdrop-filter: blur(6px);
}
.sidebar::-webkit-scrollbar { width: 5px; }
.sidebar::-webkit-scrollbar-thumb { background: rgba(182,255,60,.25); border-radius: 10px; }

.sidebar__brand {
  position: relative;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 10px 22px;
  text-decoration: none;
}
.sidebar__brand-glow {
  position: absolute; left: 2px; top: 2px; width: 42px; height: 42px;
  background: radial-gradient(circle, rgba(182,255,60,.5), transparent 70%);
  filter: blur(5px); animation: admin-pulse 3.2s ease-in-out infinite;
}
@keyframes admin-pulse { 0%, 100% { opacity: .5; transform: scale(.9); } 50% { opacity: 1; transform: scale(1.15); } }
.sidebar__brand-icon {
  position: relative;
  width: 38px;
  height: 38px;
  border-radius: 10px;
  object-fit: cover;
  filter: drop-shadow(0 0 6px rgba(182,255,60,.4));
  transition: filter .2s ease, transform .2s ease;
}
.sidebar__brand:hover .sidebar__brand-icon { filter: drop-shadow(0 0 14px rgba(182,255,60,.75)); transform: scale(1.04); }
.sidebar__brand span:last-child {
  font-family: var(--font-display);
  font-size: 22px;
  font-weight: 800;
  letter-spacing: .01em;
  color: var(--lime-300);
  text-shadow: 0 0 16px rgba(182,255,60,.45);
}

.sidebar__nav { flex: 1; display: flex; flex-direction: column; gap: 22px; }
.sidebar__group-title {
  font-family: var(--font-mono);
  font-size: 10.5px;
  font-weight: 700;
  color: rgba(211,255,143,.72);
  text-transform: uppercase;
  letter-spacing: .12em;
  padding: 0 10px 8px;
}

.sidebar__item, .sidebar-nav-item {
  position: relative;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: 10px;
  font-size: 13.5px;
  font-weight: 500;
  color: rgba(247,251,244,.85);
  text-decoration: none;
  overflow: hidden;
  transition: background .2s ease, color .2s ease, transform .15s ease, box-shadow .2s ease;
}
.sidebar__item-icon, .sidebar-nav-item__icon { display: flex; flex-shrink: 0; }
.sidebar__item svg, .sidebar-nav-item svg { width: 17px; height: 17px; flex-shrink: 0; transition: filter .2s ease; }

/* hiệu ứng quét sáng khi rê chuột */
.sidebar__item::before, .sidebar-nav-item::before {
  content: '';
  position: absolute; inset: 0;
  background: linear-gradient(100deg, transparent 0%, rgba(182,255,60,.16) 45%, transparent 90%);
  transform: translateX(-120%);
  transition: transform .5s ease;
  pointer-events: none;
}
.sidebar__item:hover::before, .sidebar-nav-item:hover::before { transform: translateX(120%); }

.sidebar__item:hover, .sidebar-nav-item:hover {
  background: rgba(182,255,60,.08);
  color: var(--lime-300);
  transform: translateX(2px);
}
.sidebar__item:hover svg, .sidebar-nav-item:hover svg { filter: drop-shadow(0 0 5px rgba(182,255,60,.55)); }

.sidebar__item.active, .sidebar-nav-item.active {
  background: linear-gradient(90deg, rgba(182,255,60,.16), rgba(182,255,60,.04));
  color: var(--lime-300);
  font-weight: 700;
  box-shadow: inset 0 0 0 1px rgba(182,255,60,.22), 0 0 18px -6px rgba(182,255,60,.45);
}
.sidebar__item.active::after, .sidebar-nav-item.active::after {
  content: '';
  position: absolute; left: 0; top: 8%; bottom: 8%; width: 3px;
  border-radius: 3px;
  background: var(--lime-400);
  box-shadow: 0 0 10px 1px rgba(182,255,60,.8);
  animation: admin-bar-glow 2s ease-in-out infinite;
}
@keyframes admin-bar-glow { 0%, 100% { opacity: .7; } 50% { opacity: 1; } }
.sidebar__item.active svg, .sidebar-nav-item.active svg { filter: drop-shadow(0 0 6px rgba(182,255,60,.6)); }
.sidebar__item--exit:hover { color: #ff9686; background: rgba(255,71,87,.1); }
.sidebar__item--exit:hover::before { background: linear-gradient(100deg, transparent 0%, rgba(255,71,87,.18) 45%, transparent 90%); }

.sidebar-section { margin: 0; }
.sidebar-section__label {
  font-family: var(--font-mono);
  font-size: 10.5px;
  font-weight: 700;
  color: rgba(211,255,143,.72);
  text-transform: uppercase;
  letter-spacing: .12em;
  padding: 0 10px 8px;
}

.sidebar__user {
  position: relative;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 10px;
  border-top: 1px solid rgba(247,251,244,.08);
  margin-top: 10px;
}
.sidebar__avatar {
  position: relative;
  width: 36px; height: 36px; border-radius: 50%;
  background: linear-gradient(135deg, var(--turf-500), var(--turf-700));
  color: var(--chalk-050);
  display: flex; align-items: center; justify-content: center;
  font-size: 14px; font-weight: 700; flex-shrink: 0;
  overflow: hidden;
  box-shadow: 0 0 0 2px rgba(182,255,60,.25), 0 0 14px -2px rgba(182,255,60,.4);
  animation: admin-avatar-ring 3s ease-in-out infinite;
}
@keyframes admin-avatar-ring {
  0%, 100% { box-shadow: 0 0 0 2px rgba(182,255,60,.25), 0 0 14px -2px rgba(182,255,60,.4); }
  50% { box-shadow: 0 0 0 2px rgba(182,255,60,.5), 0 0 20px 0px rgba(182,255,60,.6); }
}
.sidebar__avatar img { width: 100%; height: 100%; object-fit: cover; }
.sidebar__user-name { font-size: 13.5px; font-weight: 700; color: var(--chalk-050); }
.sidebar__user-role {
  display: inline-block;
  margin-top: 2px;
  font-family: var(--font-mono);
  font-size: 10.5px;
  font-weight: 700;
  letter-spacing: .06em;
  color: var(--night-950);
  background: var(--lime-400);
  padding: 1px 8px;
  border-radius: 999px;
}

/* ===== MAIN CONTENT ===== */
.admin-main { position: relative; z-index: 1; flex: 1; display: flex; flex-direction: column; min-width: 0; }
.admin-topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 28px;
  background: rgba(10,31,19,.7);
  border-bottom: 1px solid rgba(182,255,60,.12);
  backdrop-filter: blur(8px);
}
.admin-breadcrumb {
  font-family: var(--font-mono);
  font-size: 13px;
  letter-spacing: .02em;
  color: rgba(247,251,244,.75);
}
.admin-breadcrumb__sep { color: rgba(182,255,60,.6); margin: 0 2px; }
.admin-breadcrumb span:last-child { color: var(--lime-300); font-weight: 700; text-shadow: 0 0 10px rgba(182,255,60,.35); }

.btn-dang-xuat {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 9px 18px;
  border-radius: 999px;
  border: 1px solid rgba(247,251,244,.2);
  background: rgba(247,251,244,.04);
  color: var(--chalk-200);
  font-size: 13.5px;
  font-weight: 600;
  cursor: pointer;
  transition: border-color .2s ease, color .2s ease, box-shadow .2s ease, transform .15s ease;
}
.btn-dang-xuat:hover {
  border-color: rgba(255,71,87,.55);
  color: #ff9686;
  box-shadow: 0 0 16px -4px rgba(255,71,87,.5);
  transform: translateY(-1px);
}

.admin-content {
  flex: 1;
  padding: 28px;
  color: var(--chalk-050);
}

/* để nội dung trang con (thường nền sáng) không bị nuốt trong nền tối,
   bọc trong một khối "panel" phát sáng nhẹ theo tông sân đêm */
.admin-content > * {
  animation: admin-content-in .5s ease both;
}
@keyframes admin-content-in {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

@media (max-width: 960px) {
  .sidebar { display: none; }
}
</style>