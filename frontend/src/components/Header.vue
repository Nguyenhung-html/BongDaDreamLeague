<template>
  <header class="header">

    <!-- THANH 1: Top bar -->
    <div class="topbar">
      <div class="container topbar__inner">
        <router-link to="/" class="topbar__brand">
          <img :src="logo" alt="DreamLeague" class="topbar__logo-img" />
          <div class="topbar__brand-text">
            <span class="topbar__brand-name"><strong>Dream</strong>League</span>
            <span class="topbar__date">{{ ngayHomNay }}</span>
          </div>
        </router-link>

        <div v-if="!dangNhap" class="topbar__auth">
          <router-link to="/dang-nhap" class="btn-auth btn-auth--outline">Đăng nhập</router-link>
          <router-link to="/dang-ky" class="btn-auth btn-auth--fill">Đăng ký</router-link>
        </div>

        <!-- KHU VỰC ĐÃ ĐĂNG NHẬP -->
        <div v-else class="topbar__signed-in">

          <router-link
            v-if="vaiTro === 'Staff' || vaiTro === 'Admin'"
            :to="vaiTro === 'Admin' ? '/admin' : '/staff'"
            class="btn-auth btn-auth--fill btn-auth--manage"
          >
            {{ vaiTro === 'Admin' ? 'Quay lại trang Admin' : 'Quay lại trang Staff' }}
          </router-link>

          <div class="topbar__user" ref="accountRef" @click="showDropdown = !showDropdown">
            <div class="topbar__avatar">
              <img v-if="avatarUrl" :src="avatarUrl" alt="Avatar" class="avatar-img-round" />
              <span v-else>{{ tenVietTat }}</span>
            </div>
            <div class="topbar__user-info">
              <span class="topbar__user-name">{{ tenNguoiDung }}</span>
              <span class="topbar__user-role">
                {{ vaiTro === 'Admin' ? 'Admin' : (vaiTro === 'Staff' ? 'Nhân viên' : 'Khách hàng') }}
              </span>
            </div>
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" class="topbar__chevron" :style="{ transform: showDropdown ? 'rotate(180deg)' : '' }">
              <path d="M6 9L12 15L18 9" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>

            <Transition name="pop">
              <div class="topbar__dropdown" v-show="showDropdown" @click.stop>
                <div class="dropdown__header">
                  <div class="topbar__avatar topbar__avatar--lg">
                    <img v-if="avatarUrl" :src="avatarUrl" alt="Avatar" class="avatar-img-round" />
                    <span v-else>{{ tenVietTat }}</span>
                  </div>
                  <div>
                    <p class="dropdown__user-name">{{ tenNguoiDung }}</p>
                    <p class="dropdown__user-email">
                      {{ vaiTro === 'Admin' ? 'Quản trị viên' : (vaiTro === 'Staff' ? 'Tài khoản nhân viên' : 'Khách hàng') }}
                    </p>
                  </div>
                </div>
                <div class="dropdown__divider"></div>

                <router-link v-if="vaiTro === 'Staff' || vaiTro === 'Admin'" :to="vaiTro === 'Admin' ? '/admin' : '/staff'" class="dropdown__item dropdown__item--accent">
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
                  <span v-if="soThongBao > 0" class="dropdown__badge">{{ soThongBao }}</span>
                </router-link>
                <div class="dropdown__divider"></div>
                <button class="dropdown__item dropdown__item--danger" @click="dangXuat">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4M16 17l5-5-5-5M21 12H9" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round"/></svg>
                  Đăng xuất
                </button>
              </div>
            </Transition>
          </div>
        </div>

      </div>
    </div>

    <!-- THANH 2: Search + Nav bar -->
    <div class="navbar">
      <div class="container navbar__inner">

        <div class="search-wrap" ref="searchRef">
          <svg class="search-icon-left" width="16" height="16" viewBox="0 0 24 24" fill="none">
            <circle cx="11" cy="11" r="7" stroke="var(--lime-400)" stroke-width="2.2"/>
            <path d="M16.5 16.5L21 21" stroke="var(--lime-400)" stroke-width="2.2" stroke-linecap="round"/>
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
          <button class="search-submit" @click="timKiem">Tìm</button>

          <Transition name="pop">
            <div class="search-dropdown" v-show="showGoiY && ketQuaGoiY.length > 0">
              <div
                v-for="item in ketQuaGoiY"
                :key="item.id"
                class="search-dropdown__item"
                @mousedown.prevent="chonGoiY(item)"
              >
                <span class="search-dropdown__tag" :class="item.loai === 'san5' ? 'tag--xanh' : 'tag--la'">{{ item.maSan }}</span>
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
          </Transition>
        </div>

        <nav class="navbar__nav" :class="{ 'navbar__nav--open': menuOpen }">
          <router-link to="/" class="nav-item" @click="menuOpen = false">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none"><path d="M3 12L12 3l9 9" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/><path d="M9 21V12h6v9" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/></svg>
            Trang chủ
          </router-link>
          <router-link to="/san" class="nav-item" @click="menuOpen = false">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none"><rect x="2" y="3" width="20" height="18" rx="2" stroke="currentColor" stroke-width="1.8"/><line x1="12" y1="3" x2="12" y2="21" stroke="currentColor" stroke-width="1.8"/><circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="1.8"/></svg>
            Sân bóng
          </router-link>
          <a href="https://maps.app.goo.gl/wbhViBnZAy8o53dEA" target="_blank" class="nav-item">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none"><path d="M12 22s7-7.58 7-12.5A7 7 0 1 0 5 9.5C5 14.42 12 22 12 22Z" stroke="currentColor" stroke-width="1.8"/><circle cx="12" cy="9.5" r="2.5" stroke="currentColor" stroke-width="1.8"/></svg>
            Bản đồ
          </a>
          <router-link to="/lich-su-dat-san" class="nav-item" @click="menuOpen = false">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none"><rect x="3" y="5" width="18" height="16" rx="2" stroke="currentColor" stroke-width="1.8"/><path d="M3 10H21M8 3v4M16 3v4" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/></svg>
            Sân đã đặt
          </router-link>
          <router-link to="/thong-bao" class="nav-item nav-item--bell" @click="menuOpen = false">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" stroke="currentColor" stroke-width="1.8"/><path d="M13.73 21a2 2 0 0 1-3.46 0" stroke="currentColor" stroke-width="1.8"/></svg>
            Thông báo
            <span v-if="soThongBao > 0 && dangNhap" class="nav-badge">{{ soThongBao }}</span>
          </router-link>
          <router-link to="/ho-tro" class="nav-item" @click="menuOpen = false">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="9" stroke="currentColor" stroke-width="1.8"/><path d="M9.5 9a2.5 2.5 0 0 1 4.7 1.2c0 1.6-2.2 1.8-2.2 3.3" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/><circle cx="12" cy="17" r="0.9" fill="currentColor"/></svg>
            Hỗ trợ
          </router-link>
        </nav>

        <button class="burger" :class="{ 'burger--open': menuOpen }" @click="menuOpen = !menuOpen" aria-label="Menu">
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

const API = '/api'

const dangNhap = ref(false)
const tenNguoiDung = ref('')
const soThongBao = ref(0)
const vaiTro = ref('')

function getActiveAvatar() {
  const uid = localStorage.getItem('userId')
  if (!uid) return ''
  return localStorage.getItem(`avatar_${uid}`) || ''
}

const avatarUrl = ref('')
let thongBaoInterval = null

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

function kiemTraDangNhap() {
  const token = localStorage.getItem('token')
  const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true'

  if (token && isLoggedIn) {
    dangNhap.value = true
    tenNguoiDung.value = localStorage.getItem('hoTen') || 'Người dùng'
    vaiTro.value = localStorage.getItem('userRole') || ''
    avatarUrl.value = getActiveAvatar()
  } else {
    dangNhap.value = false
    tenNguoiDung.value = ''
    vaiTro.value = ''
    avatarUrl.value = ''
    soThongBao.value = 0
  }
}

async function taiSoThongBaoChuaDoc() {
  if (!dangNhap.value) {
    soThongBao.value = 0
    return
  }
  const token = localStorage.getItem('token')
  try {
    const res = await fetch('/api/dat-san/lich-su', {
      method: 'GET',
      headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` }
    })
    if (!res.ok) return
    const data = await res.json()
    soThongBao.value = data.soLuong
  } catch {
    // im lặng bỏ qua lỗi tạm thời, sẽ thử lại ở lần polling tiếp theo
  }
}

function batDauPollingThongBao() {
  dungPollingThongBao()
  thongBaoInterval = setInterval(taiSoThongBaoChuaDoc, 10000)
}
function dungPollingThongBao() {
  if (thongBaoInterval) { clearInterval(thongBaoInterval); thongBaoInterval = null }
}

watch(() => route.path, () => {
  kiemTraDangNhap()
  taiSoThongBaoChuaDoc()
})

const danhSachSan = [
  { id: 1, maSan: 'A1', ten: 'Sân 5 người — A1', loai: 'san5', gia: '350.000', tuKhoa: ['a1', 'sân 5', 'san 5', 'san5'] },
  { id: 2, maSan: 'A2', ten: 'Sân 5 người — A2', loai: 'san5', gia: '350.000', tuKhoa: ['a2', 'sân 5', 'san 5', 'san5'] },
  { id: 3, maSan: 'A3', ten: 'Sân 5 người — A3', loai: 'san5', gia: '350.000', tuKhoa: ['a3', 'sân 5', 'san 5', 'san5'] },
  { id: 4, maSan: 'B1', ten: 'Sân 7 người — B1', loai: 'san7', gia: '650.000', tuKhoa: ['b1', 'sân 7', 'san 7', 'san7'] },
  { id: 5, maSan: 'B2', ten: 'Sân 7 người — B2', loai: 'san7', gia: '650.000', tuKhoa: ['b2', 'sân 7', 'san 7', 'san7'] },
  { id: 6, maSan: 'B3', tile: 'Sân 7 người — B3', loai: 'san7', gia: '650.000', tuKhoa: ['b3', 'sân 7', 'san 7', 'san7'] },
]

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

function onInput() { showGoiY.value = ketQuaGoiY.value.length > 0 }

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

function xoaTimKiem() { tuKhoa.value = ''; showGoiY.value = false }

const showDropdown = ref(false)
const menuOpen = ref(false)
const accountRef = ref(null)

function dangXuat() {
  localStorage.removeItem('token')
  localStorage.removeItem('hoTen')
  localStorage.removeItem('isLoggedIn')
  localStorage.removeItem('userRole')
  localStorage.removeItem('vaiTro')

  dangNhap.value = false
  tenNguoiDung.value = ''
  vaiTro.value = ''
  showDropdown.value = false
  dungPollingThongBao()

  alert('Đã đăng xuất tài khoản thành công!')
  router.push('/dang-nhap')
}

function onClickOutside(e) {
  if (accountRef.value && !accountRef.value.contains(e.target)) showDropdown.value = false
  if (searchRef.value && !searchRef.value.contains(e.target)) showGoiY.value = false
}

onMounted(() => {
  localStorage.removeItem('user_avatar')
  localStorage.removeItem('admin_avatar')
  localStorage.removeItem('staff_avatar')

  document.addEventListener('click', onClickOutside)
  window.addEventListener('user-profile-updated', kiemTraDangNhap)
  kiemTraDangNhap()
  taiSoThongBaoChuaDoc()
  batDauPollingThongBao()
})

onUnmounted(() => {
  document.removeEventListener('click', onClickOutside)
  window.removeEventListener('user-profile-updated', kiemTraDangNhap)
  dungPollingThongBao()
})
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Bebas+Neue&family=Oswald:wght@500;600;700&family=Manrope:wght@400;500;600;700;800&family=Space+Mono:wght@400;700&display=swap');
</style>

<style scoped>
.header {
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
  --font-clock: 'Bebas Neue', 'Oswald', sans-serif;
  position: relative;
  z-index: 100;
  font-family: var(--font-body);
}
.container { max-width: 1280px; margin: 0 auto; padding: 0 44px; }
@media (max-width: 780px) { .container { padding: 0 20px; } }

/* ===== TOPBAR ===== */
.topbar {
  background: linear-gradient(120deg, var(--night-950), var(--night-700) 140%);
  padding: 10px 0;
  border-bottom: 1px solid rgba(182,255,60,.12);
}
.topbar__inner { display: flex; align-items: center; justify-content: space-between; gap: 16px; }
.topbar__brand { display: flex; align-items: center; gap: 10px; color: var(--chalk-050); text-decoration: none; }
.topbar__logo-img { width: 42px; height: 42px; border-radius: 50%; object-fit: contain; background: var(--chalk-050); padding: 2px; box-shadow: 0 0 0 2px rgba(182,255,60,.35); }
.topbar__brand-name { font-family: var(--font-display); font-size: 18px; color: var(--chalk-050); display: block; line-height: 1.2; }
.topbar__brand-name strong { color: var(--lime-400); }
.topbar__date { font-size: 11px; color: var(--chalk-200); opacity: .55; display: block; font-family: var(--font-mono); }

.topbar__auth { display: flex; gap: 10px; }
.btn-auth { padding: 8px 22px; border-radius: 999px; font-size: 14px; font-weight: 600; font-family: inherit; cursor: pointer; transition: all .15s; }
.btn-auth--outline { background: transparent; border: 1.5px solid rgba(247,251,244,.3); color: var(--chalk-050); }
.btn-auth--outline:hover { border-color: var(--lime-400); color: var(--lime-300); background: rgba(182,255,60,.08); }
.btn-auth--fill { background: linear-gradient(135deg, var(--turf-500), var(--turf-700)); border: 1.5px solid transparent; color: var(--chalk-050); }
.btn-auth--fill:hover { filter: brightness(1.1); }
.btn-auth--manage { padding: 8px 18px; font-size: 13.5px; }

.topbar__signed-in { display: flex; align-items: center; gap: 16px; }

.topbar__user {
  position: relative; display: flex; align-items: center; gap: 10px;
  padding: 6px 14px 6px 6px; border-radius: 999px;
  border: 1.5px solid rgba(247,251,244,.18); cursor: pointer; user-select: none;
  transition: background .15s, border-color .15s;
}
.topbar__user:hover { background: rgba(182,255,60,.06); border-color: rgba(182,255,60,.3); }
.topbar__avatar {
  width: 34px; height: 34px; border-radius: 50%;
  background: linear-gradient(135deg, var(--turf-500), var(--turf-700));
  color: var(--chalk-050); font-size: 14px; font-weight: 700;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0; overflow: hidden;
}
.topbar__avatar--lg { width: 40px; height: 40px; font-size: 16px; }
.avatar-img-round { width: 100%; height: 100%; object-fit: cover; }
.topbar__user-name { font-size: 14px; font-weight: 600; color: var(--chalk-050); display: block; line-height: 1.2; }
.topbar__user-role { font-size: 11px; color: var(--lime-300); display: block; }
.topbar__chevron { color: var(--chalk-200); transition: transform .2s; }

.topbar__dropdown {
  position: absolute; top: calc(100% + 10px); right: 0;
  background: var(--night-800); border-radius: 16px;
  box-shadow: 0 20px 50px rgba(0,0,0,.5);
  border: 1px solid rgba(182,255,60,.18); min-width: 250px; overflow: hidden; z-index: 300;
}
.dropdown__header { display: flex; align-items: center; gap: 12px; padding: 16px; background: rgba(247,251,244,.03); }
.dropdown__user-name { font-size: 14px; font-weight: 700; color: var(--chalk-050); }
.dropdown__user-email { font-size: 12px; color: var(--chalk-200); opacity: .65; margin-top: 2px; }
.dropdown__divider { height: 1px; background: rgba(247,251,244,.08); margin: 4px 0; }
.dropdown__item {
  display: flex; align-items: center; gap: 10px; padding: 11px 16px;
  font-size: 14px; color: var(--chalk-200); background: none; border: none;
  width: 100%; text-align: left; cursor: pointer; transition: background .12s, color .12s; font-family: inherit;
}
.dropdown__item:hover { background: rgba(182,255,60,.08); color: var(--lime-300); }
.dropdown__item--accent { color: var(--lime-300); font-weight: 600; }
.dropdown__item--danger { color: #ff9686; }
.dropdown__item--danger:hover { background: rgba(255,71,87,.1); color: #ff9686; }
.dropdown__badge { margin-left: auto; background: var(--crimson-500); color: white; font-size: 11px; font-weight: 700; padding: 1px 7px; border-radius: 999px; }

.pop-enter-active, .pop-leave-active { transition: opacity .18s ease, transform .18s ease; }
.pop-enter-from, .pop-leave-to { opacity: 0; transform: translateY(-6px) scale(.98); }

/* ===== NAVBAR ===== */
.navbar {
  background: rgba(5,11,8,.9); backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(247,251,244,.08);
  position: sticky; top: 0; z-index: 99;
}
.navbar__inner { display: flex; align-items: center; gap: 16px; height: 62px; }

/* ===== SEARCH ===== */
.search-wrap {
  position: relative; display: flex; align-items: center;
  flex: 1; max-width: 440px;
  background: rgba(247,251,244,.05); border: 1.5px solid rgba(247,251,244,.12);
  border-radius: 999px; padding: 0 6px 0 12px; height: 42px;
  transition: border-color .15s, background .15s;
}
.search-wrap:focus-within { border-color: var(--lime-400); background: rgba(182,255,60,.05); }
.search-icon-left { flex-shrink: 0; margin-right: 6px; }
.search-input { flex: 1; border: none; background: transparent; outline: none; font-size: 14px; font-family: inherit; color: var(--chalk-050); }
.search-input::placeholder { color: var(--chalk-200); opacity: .5; }
.search-clear { background: none; border: none; cursor: pointer; color: var(--chalk-200); opacity: .6; display: flex; align-items: center; padding: 4px; transition: opacity .15s; }
.search-clear:hover { opacity: 1; }
.search-submit { padding: 0 16px; height: 32px; border-radius: 999px; background: var(--turf-500); border: none; color: var(--chalk-050); font-size: 13px; font-weight: 700; font-family: inherit; cursor: pointer; flex-shrink: 0; transition: background .15s; margin-left: 4px; }
.search-submit:hover { background: var(--lime-400); color: var(--night-950); }

.search-dropdown {
  position: absolute; top: calc(100% + 8px); left: 0; right: 0;
  background: var(--night-800); border-radius: 14px;
  box-shadow: 0 16px 40px rgba(0,0,0,.45);
  border: 1px solid rgba(182,255,60,.18); overflow: hidden; z-index: 200;
}
.search-dropdown__item { display: flex; align-items: center; gap: 12px; padding: 12px 16px; cursor: pointer; transition: background .12s; }
.search-dropdown__item:hover { background: rgba(182,255,60,.07); }
.search-dropdown__tag { width: 36px; height: 36px; border-radius: 10px; display: flex; align-items: center; justify-content: center; font-size: 12px; font-weight: 800; flex-shrink: 0; }
.tag--xanh { background: rgba(56,189,248,.15); color: #7dd3fc; }
.tag--la { background: rgba(182,255,60,.12); color: var(--lime-300); }
.search-dropdown__ten { font-size: 14px; font-weight: 600; color: var(--chalk-050); }
.search-dropdown__dia { font-size: 12px; color: var(--chalk-200); opacity: .6; margin-top: 2px; }
.search-dropdown__gia { margin-left: auto; font-size: 13px; font-weight: 700; color: var(--lime-300); white-space: nowrap; }
.search-dropdown__footer { padding: 10px 16px; font-size: 13px; color: var(--lime-300); border-top: 1px solid rgba(247,251,244,.08); cursor: pointer; transition: background .12s; }
.search-dropdown__footer:hover { background: rgba(182,255,60,.07); }

/* ===== NAV ===== */
.navbar__nav { display: flex; align-items: center; gap: 2px; margin-left: auto; }
.nav-item {
  display: flex; align-items: center; gap: 6px; padding: 8px 14px;
  border-radius: 999px; font-size: 13.5px; font-weight: 500; color: var(--chalk-200);
  transition: background .15s, color .15s; white-space: nowrap; position: relative;
}
.nav-item:hover { background: rgba(182,255,60,.08); color: var(--lime-300); }
.nav-item.router-link-exact-active { background: rgba(182,255,60,.12); color: var(--lime-300); font-weight: 700; }
.nav-badge {
  position: absolute; top: 4px; right: 8px; width: 16px; height: 16px;
  background: var(--crimson-500); color: white; font-size: 10px; font-weight: 700;
  border-radius: 50%; display: flex; align-items: center; justify-content: center;
  animation: badge-pulse 1.8s ease-in-out infinite;
}
@keyframes badge-pulse { 0%, 100% { box-shadow: 0 0 0 0 rgba(255,71,87,.5); } 50% { box-shadow: 0 0 0 5px rgba(255,71,87,0); } }

/* BURGER */
.burger { display: none; flex-direction: column; gap: 5px; background: none; border: none; cursor: pointer; padding: 6px; }
.burger span { width: 22px; height: 2px; background: var(--chalk-050); border-radius: 2px; display: block; transition: transform .2s ease, opacity .2s ease; }
.burger--open span:nth-child(1) { transform: translateY(7px) rotate(45deg); }
.burger--open span:nth-child(2) { opacity: 0; }
.burger--open span:nth-child(3) { transform: translateY(-7px) rotate(-45deg); }

@media (max-width: 960px) {
  .navbar__nav {
    display: none; position: absolute; top: 100%; left: 0; right: 0;
    flex-direction: column; align-items: stretch; gap: 2px;
    background: var(--night-800); border-bottom: 1px solid rgba(182,255,60,.15);
    padding: 10px 20px 16px;
  }
  .navbar__nav--open { display: flex; }
  .burger { display: flex; }
  .search-wrap { max-width: none; }
}
@media (max-width: 480px) { .topbar__date { display: none; } }
</style>