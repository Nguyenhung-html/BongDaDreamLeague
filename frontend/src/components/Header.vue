
<template>
  <header class="header">

    <!-- THANH 1: Top bar -->
    <div class="topbar">
  <div class="container topbar__inner">
    <router-link to="/" class="topbar__brand">
      <img :src="logo" alt="DreamLeague" class="topbar__logo-img" />
      <div class="topbar__brand-text">
        <span class="topbar__brand-name">
          <strong>Dream</strong>League
        </span>
        <span class="topbar__date">
          {{ ngayHomNay }}
        </span>
      </div>
    </router-link>

    <div v-if="!dangNhap" class="topbar__auth">
      <router-link to="/dang-nhap" class="btn-auth btn-auth--outline">Đăng nhập</router-link>
      <router-link to="/dang-ky" class="btn-auth btn-auth--fill">Đăng ký</router-link>
    </div>

    <!-- KHU VỰC ĐÃ ĐĂNG NHẬP -->
    <div v-else style="display: flex; align-items: center; gap: 16px;">
      
      <!-- [THÊM MỚI] Nút truy cập lại trang quản lý (Nằm ngay bên trái khối User) -->
      <router-link 
        v-if="vaiTro === 'Staff' || vaiTro === 'Admin'" 
        :to="vaiTro === 'Admin' ? '/admin' : '/staff'" 
        class="btn-auth btn-auth--fill"
        style="background-color: var(--green-600); color: #fff; padding: 8px 16px; font-size: 14px; border-radius: 6px; text-decoration: none; font-weight: 500;"
      >
        {{ vaiTro === 'Admin' ? 'Quay Lại Trang Admin' : 'Quay Lại Trang Staff' }}
      </router-link>

      <!-- Khối thông tin User -->
      <div class="topbar__user" ref="accountRef" @click="showDropdown = !showDropdown">
        <div class="topbar__avatar">{{ tenVietTat }}</div>
        <div class="topbar__user-info">
          <span class="topbar__user-name">{{ tenNguoiDung }}</span>
          
          <!-- [CẬP NHẬT] Hiển thị chức danh động thay vì cố định chữ Khách hàng -->
          <span class="topbar__user-role">
            {{ vaiTro === 'Admin' ? 'Admin' : (vaiTro === 'Staff' ? 'Nhân viên' : 'Khách hàng') }}
          </span>
        </div>
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" :style="{ transform: showDropdown ? 'rotate(180deg)' : '', transition: '0.2s' }">
          <path d="M6 9L12 15L18 9" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
        </svg>

        <div class="topbar__dropdown" v-show="showDropdown" @click.stop>
          <div class="dropdown__header">
            <div class="topbar__avatar topbar__avatar--lg">{{ tenVietTat }}</div>
            <div>
              <p class="dropdown__user-name">{{ tenNguoiDung }}</p>
              <!-- Hiển thị vai trò trong dropdown luôn cho đồng bộ -->
              <p class="dropdown__user-email">
                {{ vaiTro === 'Admin' ? 'Quản trị viên' : (vaiTro === 'Staff' ? 'Tài khoản nhân viên' : 'Khách hàng') }}
              </p>
            </div>
          </div>
          <div class="dropdown__divider"></div>
          
          <!-- Thêm một lối tắt vào trang quản lý ngay bên trong Menu Dropdown phòng trường hợp cần -->
          <router-link v-if="vaiTro === 'Staff' || vaiTro === 'Admin'" :to="vaiTro === 'ADMIN' ? '/admin' : '/staff'" class="dropdown__item" style="color: var(--green-600); font-weight: 600;">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7"><rect x="3" y="3" width="18" height="18" rx="2"/><path d="M9 3v18M3 9h18"/></svg>
            Vào trang quản lý
          </router-link>
          <div v-if="vaiTro === 'Staff' || vaiTro === 'Admin'" class="dropdown__divider"></div>

          <router-link to="/thong-tin-ca-nhan" class="dropdown__item">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="8" r="4" stroke="currentColor" stroke-width="1.7"/><path d="M4 20c0-4 3.6-7 8-7s8 3 8 7" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/></svg>
            Thông tin cá nhân
          </router-link>
          <router-link to="/lich-su-dat-san" class="dropdown__item">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none"><rect x="3" y="5" width="18" height="16" rx="2" stroke="currentColor" stroke-width="1.7"/><path d="M3 10H21M8 3v4M16 3v4" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/></svg>
            Lịch sử đặt sân
          </router-link>
          <router-link to="/thong-bao" class="dropdown__item">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" stroke="currentColor" stroke-width="1.7"/><path d="M13.73 21a2 2 0 0 1-3.46 0" stroke="currentColor" stroke-width="1.7"/></svg>
            Thông báo
            <span class="dropdown__badge">{{ soThongBao }}</span>
          </router-link>
          <div class="dropdown__divider"></div>
          <button class="dropdown__item dropdown__item--danger" @click="dangXuat">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4M16 17l5-5-5-5M21 12H9" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round"/></svg>
            Đăng xuất
          </button>
        </div>
      </div>
    </div>

  </div>
</div>

    <!-- THANH 2: Search + Nav bar -->
    <div class="navbar">
      <div class="container navbar__inner">

        <!-- Tìm kiếm nội bộ -->
        <div class="search-wrap" ref="searchRef">
          <svg class="search-icon-left" width="16" height="16" viewBox="0 0 24 24" fill="none">
            <circle cx="11" cy="11" r="7" stroke="var(--green-600)" stroke-width="2.2"/>
            <path d="M16.5 16.5L21 21" stroke="var(--green-600)" stroke-width="2.2" stroke-linecap="round"/>
          </svg>
          <input
            v-model="tuKhoa"
            type="text"
            class="search-input"
            placeholder="Tìm sân A1, A2, B1... hoặc loại sân"
            @input="onInput"
            @keyup.enter="timKiem"
            @focus="showGoiY = ketQuaGoiY.length > 0"
          />
          <button v-if="tuKhoa" class="search-clear" @click="xoaTimKiem">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none"><path d="M18 6L6 18M6 6l12 12" stroke="currentColor" stroke-width="2.2" stroke-linecap="round"/></svg>
          </button>
          <button class="search-submit" @click="timKiem">
            Tìm
          </button>

          <!-- Dropdown gợi ý -->
          <div class="search-dropdown" v-show="showGoiY && ketQuaGoiY.length > 0">
            <div
              v-for="item in ketQuaGoiY"
              :key="item.id"
              class="search-dropdown__item"
              @mousedown.prevent="chonGoiY(item)"
            >
              <span class="search-dropdown__tag" :class="item.loai === 'san5' ? 'tag--xanh' : 'tag--la'">
                {{ item.maSan }}
              </span>
              <div>
                <p class="search-dropdown__ten">{{ item.ten }}</p>
                <p class="search-dropdown__dia">Hẻm 104 Tân Sơn, Tân Sơn, HCM</p>
              </div>
              <span class="search-dropdown__gia">{{ item.gia }}đ/giờ</span>
            </div>
            <div class="search-dropdown__footer" @mousedown.prevent="timKiem">
              Xem tất cả kết quả cho "<strong>{{ tuKhoa }}</strong>" →
            </div>
          </div>
        </div>

        <!-- Nav links -->
        <nav class="navbar__nav">
          <router-link to="/" class="nav-item">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none"><path d="M3 12L12 3l9 9" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/><path d="M9 21V12h6v9" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/></svg>
            Trang chủ
          </router-link>
          <router-link to="/san" class="nav-item">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none"><rect x="2" y="3" width="20" height="18" rx="2" stroke="currentColor" stroke-width="1.8"/><line x1="12" y1="3" x2="12" y2="21" stroke="currentColor" stroke-width="1.8"/><circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="1.8"/></svg>
            Sân bóng
          </router-link>
          <a href="https://maps.app.goo.gl/wbhViBnZAy8o53dEA" target="_blank" class="nav-item">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none"><path d="M12 22s7-7.58 7-12.5A7 7 0 1 0 5 9.5C5 14.42 12 22 12 22Z" stroke="currentColor" stroke-width="1.8"/><circle cx="12" cy="9.5" r="2.5" stroke="currentColor" stroke-width="1.8"/></svg>
            Bản đồ
          </a>
          <router-link to="/lich-su-dat-san" class="nav-item">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none"><rect x="3" y="5" width="18" height="16" rx="2" stroke="currentColor" stroke-width="1.8"/><path d="M3 10H21M8 3v4M16 3v4" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/></svg>
            Sân đã đặt
          </router-link>
          <router-link to="/thong-bao" class="nav-item nav-item--bell">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" stroke="currentColor" stroke-width="1.8"/><path d="M13.73 21a2 2 0 0 1-3.46 0" stroke="currentColor" stroke-width="1.8"/></svg>
            Thông báo
            <span v-if="soThongBao > 0 && dangNhap" class="nav-badge">{{ soThongBao }}</span>
          </router-link>
        </nav>

        <button class="burger" @click="menuOpen = !menuOpen">
          <span></span><span></span><span></span>
        </button>
      </div>
    </div>

  </header>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import logo from '../Image/logo DreamLeague.webp'

const router = useRouter()
const route = useRoute() 

// ===== AUTH STATE =====
const dangNhap = ref(false)
const tenNguoiDung = ref('')
const soThongBao = ref(3)
const vaiTro = ref('') // Sẽ nhận các giá trị chuẩn hóa: 'Admin', 'Staff', hoặc 'User'

const tenVietTat = computed(() => {
  if (!tenNguoiDung.value) return '?'
  const parts = tenNguoiDung.value.trim().split(' ')
  return parts[parts.length - 1].charAt(0).toUpperCase()
})

const ngayHomNay = computed(() => {
  return new Date().toLocaleDateString('vi-VN', {
    weekday: 'long', day: '2-digit', month: '2-digit', year: 'numeric'
  })
})

// ===== KIỂM TRA ĐĂNG NHẬP (ĐÃ ĐỒNG BỘ VỚI ROUTER MỚI) =====
function kiemTraDangNhap() {
  const token = localStorage.getItem('token')
  const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true'

  if (token && isLoggedIn) {
    dangNhap.value = true
    tenNguoiDung.value = localStorage.getItem('hoTen') || 'Người dùng'
    // ĐỔI TỪ 'vaiTro' THÀNH 'userRole' ĐỂ ĐỌC ĐÚNG GIÁ TRỊ CHUẨN HÓA ('Admin', 'Staff')
    vaiTro.value = localStorage.getItem('userRole') || '' 
  } else {
    dangNhap.value = false
    tenNguoiDung.value = ''
    vaiTro.value = '' 
  }
}

// Theo dõi sát sao mỗi khi chuyển trang để cập nhật giao diện ngay lập tức mà không cần F5
watch(() => route.path, () => {
  kiemTraDangNhap()
})

// ===== DỮ LIỆU SÂN NỘI BỘ =====
const danhSachSan = [
  { id: 1, maSan: 'A1', ten: 'Sân 5 người — A1', loai: 'san5', gia: '350.000', tuKhoa: ['a1', 'sân 5', 'san 5', 'san5'] },
  { id: 2, maSan: 'A2', ten: 'Sân 5 người — A2', loai: 'san5', gia: '350.000', tuKhoa: ['a2', 'sân 5', 'san 5', 'san5'] },
  { id: 3, maSan: 'A3', ten: 'Sân 5 người — A3', loai: 'san5', gia: '350.000', tuKhoa: ['a3', 'sân 5', 'san 5', 'san5'] },
  { id: 4, maSan: 'B1', ten: 'Sân 7 người — B1', loai: 'san7', gia: '650.000', tuKhoa: ['b1', 'sân 7', 'san 7', 'san7'] },
  { id: 5, maSan: 'B2', ten: 'Sân 7 người — B2', loai: 'san7', gia: '650.000', tuKhoa: ['b2', 'sân 7', 'san 7', 'san7'] },
  { id: 6, maSan: 'B3', tile: 'Sân 7 người — B3', loai: 'san7', gia: '650.000', tuKhoa: ['b3', 'sân 7', 'san 7', 'san7'] },
]

// ===== TÌM KIẾM NỘI BỘ =====
const tuKhoa = ref('')
const showGoiY = ref(false)
const searchRef = ref(null)

const ketQuaGoiY = computed(() => {
  const q = tuKhoa.value.trim().toLowerCase()
  if (!q) return []
  return danhSachSan.filter(san =>
    san.maSan.toLowerCase().includes(q) ||
    san.ten.toLowerCase().includes(q) ||
    san.tuKhoa.some(k => k.includes(q))
  )
})

function onInput() {
  showGoiY.value = ketQuaGoiY.value.length > 0
}

function timKiem() {
  if (tuKhoa.value.trim()) {
    showGoiY.value = false
    router.push({ path: '/san', query: { q: tuKhoa.value } })
    tuKhoa.value = ''
  }
}

function chonGoiY(item) {
  showGoiY.value = false
  tuKhoa.value = ''
  router.push(`/dat-san/${item.id}`)
}

function xoaTimKiem() {
  tuKhoa.value = ''
  showGoiY.value = false
}

// ===== DROPDOWN ACCOUNT & ĐĂNG XUẤT =====
const showDropdown = ref(false)
const menuOpen = ref(false)
const accountRef = ref(null)

function dangXuat() {
  // Xóa sạch toàn bộ session đăng nhập cũ
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

function onClickOutside(e) {
  if (accountRef.value && !accountRef.value.contains(e.target)) {
    showDropdown.value = false
  }
  if (searchRef.value && !searchRef.value.contains(e.target)) {
    showGoiY.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', onClickOutside)
  kiemTraDangNhap() // Gọi hàm kiểm tra ngay khi vừa mount trang chủ
})

onUnmounted(() => {
  document.removeEventListener('click', onClickOutside)
})
</script>

<style scoped>
/* ===== TOPBAR ===== */
.topbar { background: var(--navy-900); padding: 10px 0; }
.topbar__inner { display: flex; align-items: center; justify-content: space-between; gap: 16px; }
.topbar__brand { display: flex; align-items: center; gap: 10px; color: var(--white); text-decoration: none; }
.topbar__logo-img { width: 44px; height: 44px; border-radius: 50%; object-fit: contain; background: white; padding: 2px; }
.topbar__brand-name { font-family: var(--font-score); font-size: 18px; color: var(--white); display: block; line-height: 1.2; }
.topbar__brand-name strong { color: var(--green-500); }
.topbar__date { font-size: 11.5px; color: rgba(255,255,255,0.55); display: block; }
.topbar__auth { display: flex; gap: 10px; }
.btn-auth {
  padding: 8px 22px; border-radius: 8px; font-size: 14px;
  font-weight: 600; font-family: inherit; cursor: pointer; transition: all 0.15s;
}
.btn-auth--outline { background: transparent; border: 1.5px solid rgba(255,255,255,0.4); color: var(--white); }
.btn-auth--outline:hover { background: rgba(255,255,255,0.08); }
.btn-auth--fill { background: var(--green-600); border: 1.5px solid var(--green-600); color: var(--white); }
.btn-auth--fill:hover { background: var(--green-700); }

.topbar__user {
  position: relative; display: flex; align-items: center; gap: 10px;
  padding: 6px 14px 6px 6px; border-radius: 999px;
  border: 1.5px solid rgba(255,255,255,0.2); cursor: pointer;
  user-select: none; transition: background 0.15s;
}
.topbar__user:hover { background: rgba(255,255,255,0.06); }
.topbar__avatar {
  width: 34px; height: 34px; border-radius: 50%; background: var(--green-600);
  color: var(--white); font-size: 14px; font-weight: 700;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.topbar__avatar--lg { width: 40px; height: 40px; font-size: 16px; }
.topbar__user-name { font-size: 14px; font-weight: 600; color: var(--white); display: block; line-height: 1.2; }
.topbar__user-role { font-size: 11px; color: rgba(255,255,255,0.5); display: block; }

.topbar__dropdown {
  position: absolute; top: calc(100% + 10px); right: 0;
  background: var(--white); border-radius: var(--radius-md);
  box-shadow: 0 16px 48px rgba(10,37,64,0.18);
  border: 1px solid var(--gray-100); min-width: 240px; overflow: hidden; z-index: 300;
}
.dropdown__header { display: flex; align-items: center; gap: 12px; padding: 16px; background: var(--gray-50); }
.dropdown__user-name { font-size: 14px; font-weight: 700; color: var(--navy-900); }
.dropdown__user-email { font-size: 12px; color: var(--gray-500); margin-top: 2px; }
.dropdown__divider { height: 1px; background: var(--gray-100); margin: 4px 0; }
.dropdown__item {
  display: flex; align-items: center; gap: 10px; padding: 11px 16px;
  font-size: 14px; color: var(--navy-900); background: none; border: none;
  width: 100%; text-align: left; cursor: pointer; transition: background 0.12s; font-family: inherit;
}
.dropdown__item:hover { background: var(--gray-50); }
.dropdown__item--danger { color: #e53e3e; }
.dropdown__item--danger:hover { background: #fff5f5; }
.dropdown__badge {
  margin-left: auto; background: #e53e3e; color: white;
  font-size: 11px; font-weight: 700; padding: 1px 7px; border-radius: 999px;
}

/* ===== NAVBAR ===== */
.navbar {
  background: var(--white); border-bottom: 1.5px solid var(--gray-100);
  position: sticky; top: 0; z-index: 99;
  box-shadow: 0 2px 12px rgba(10,37,64,0.06);
}
.navbar__inner { display: flex; align-items: center; gap: 16px; height: 60px; }

/* ===== SEARCH NỘI BỘ ===== */
.search-wrap {
  position: relative; display: flex; align-items: center;
  flex: 1; max-width: 440px;
  background: var(--gray-50); border: 1.5px solid var(--gray-200, #e2e8e5);
  border-radius: 999px; padding: 0 6px 0 12px; height: 42px;
  transition: border-color 0.15s;
}
.search-wrap:focus-within { border-color: var(--green-600); background: var(--white); }

.search-icon-left { flex-shrink: 0; margin-right: 6px; }

.search-input {
  flex: 1; border: none; background: transparent; outline: none;
  font-size: 14px; font-family: inherit; color: var(--navy-900);
}
.search-input::placeholder { color: var(--gray-500); }

.search-clear {
  background: none; border: none; cursor: pointer; color: var(--gray-400);
  display: flex; align-items: center; padding: 4px;
  transition: color 0.15s;
}
.search-clear:hover { color: var(--navy-900); }

.search-submit {
  padding: 0 16px; height: 32px; border-radius: 999px;
  background: var(--green-600); border: none;
  color: white; font-size: 13px; font-weight: 600; font-family: inherit;
  cursor: pointer; flex-shrink: 0; transition: background 0.15s; margin-left: 4px;
}
.search-submit:hover { background: var(--green-700); }

/* Dropdown gợi ý */
.search-dropdown {
  position: absolute; top: calc(100% + 8px); left: 0; right: 0;
  background: var(--white); border-radius: var(--radius-md);
  box-shadow: 0 12px 40px rgba(10,37,64,0.14);
  border: 1px solid var(--gray-100); overflow: hidden; z-index: 200;
}
.search-dropdown__item {
  display: flex; align-items: center; gap: 12px; padding: 12px 16px;
  cursor: pointer; transition: background 0.12s;
}
.search-dropdown__item:hover { background: var(--gray-50); }
.search-dropdown__tag {
  width: 36px; height: 36px; border-radius: 10px;
  display: flex; align-items: center; justify-content: center;
  font-size: 12px; font-weight: 800; flex-shrink: 0;
}
.tag--xanh { background: #e0f2fe; color: #0369a1; }
.tag--la { background: var(--green-50); color: var(--green-700); }
.search-dropdown__ten { font-size: 14px; font-weight: 600; color: var(--navy-900); }
.search-dropdown__dia { font-size: 12px; color: var(--gray-500); margin-top: 2px; }
.search-dropdown__gia { margin-left: auto; font-size: 13px; font-weight: 700; color: var(--green-700); white-space: nowrap; }
.search-dropdown__footer {
  padding: 10px 16px; font-size: 13px; color: var(--green-600);
  border-top: 1px solid var(--gray-100); cursor: pointer;
  transition: background 0.12s;
}
.search-dropdown__footer:hover { background: var(--gray-50); }

/* ===== NAV ===== */
.navbar__nav { display: flex; align-items: center; gap: 2px; margin-left: auto; }
.nav-item {
  display: flex; align-items: center; gap: 6px; padding: 8px 14px;
  border-radius: 10px; font-size: 13.5px; font-weight: 500; color: var(--gray-700);
  transition: background 0.15s, color 0.15s; white-space: nowrap; position: relative;
}
.nav-item:hover { background: var(--green-50); color: var(--green-700); }
.nav-item.router-link-exact-active { background: var(--green-50); color: var(--green-700); font-weight: 600; }
.nav-badge {
  position: absolute; top: 4px; right: 8px;
  width: 16px; height: 16px; background: #e53e3e; color: white;
  font-size: 10px; font-weight: 700; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
}

/* BURGER */
.burger { display: none; flex-direction: column; gap: 5px; background: none; border: none; cursor: pointer; padding: 6px; }
.burger span { width: 22px; height: 2px; background: var(--navy-900); border-radius: 2px; display: block; }

@media (max-width: 960px) {
  .navbar__nav { display: none; }
  .burger { display: flex; }
  .search-wrap { max-width: none; }
}
@media (max-width: 480px) {
  .topbar__date { display: none; }
}
</style>