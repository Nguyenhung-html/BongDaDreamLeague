<template>
  <div
    class="scene"
    :class="{ 'scene--cursor': cursorEnabled }"
    :style="{ '--sx': spotlight.x + '%', '--sy': spotlight.y + '%' }"
    @mousemove="onSceneMove"
    @mouseleave="onSceneLeave"
  >
    <!-- ===== NỀN SÂN CỎ ĐÊM: đèn pha, cỏ, vạch sân, hạt sáng ===== -->
    <div class="scene__spotlight" aria-hidden="true"></div>
    <div class="floodlight floodlight--l" aria-hidden="true"></div>
    <div class="floodlight floodlight--r" aria-hidden="true"></div>
    <div class="grass-drift" aria-hidden="true"></div>
    <div class="grass-mow" aria-hidden="true"></div>
    <div class="scene__vignette" aria-hidden="true"></div>
    <div class="motes" aria-hidden="true">
      <span v-for="n in 14" :key="n" class="mote" :style="{ '--i': n }"></span>
    </div>

    <svg class="pitch-lines" viewBox="0 0 1600 900" preserveAspectRatio="xMidYMid slice" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
      <line x1="800" y1="0" x2="800" y2="900" class="draw-line" style="--delay:.1s" stroke="white" stroke-width="1.5" />
      <circle cx="800" cy="450" r="110" class="draw-line" style="--delay:.4s" stroke="white" stroke-width="1.5" fill="none" />
      <rect x="0" y="260" width="180" height="380" class="draw-line" style="--delay:.2s" stroke="white" stroke-width="1.5" fill="none" />
      <rect x="1420" y="260" width="180" height="380" class="draw-line" style="--delay:.3s" stroke="white" stroke-width="1.5" fill="none" />
    </svg>

    <!-- ===== ĐẠO CỤ BÓNG ĐÁ TRÔI NỔI ===== -->
    <div class="field-props" aria-hidden="true">
      <div class="prop prop--ball-1">
        <svg viewBox="0 0 40 40"><circle cx="20" cy="20" r="18" fill="var(--chalk-050)" stroke="var(--night-950)" stroke-width="1.4" /><polygon points="20,10 26,14.5 24,21.5 16,21.5 14,14.5" fill="var(--night-950)" /><path d="M20,10 L20,3 M26,14.5 L32.5,10.3 M24,21.5 L28.3,28 M16,21.5 L11.7,28 M14,14.5 L7.5,10.3" stroke="var(--night-950)" stroke-width="1.3" fill="none" /></svg>
      </div>
      <div class="prop prop--ball-2">
        <svg viewBox="0 0 40 40"><circle cx="20" cy="20" r="18" fill="var(--chalk-050)" stroke="var(--night-950)" stroke-width="1.4" /><polygon points="20,10 26,14.5 24,21.5 16,21.5 14,14.5" fill="var(--night-950)" /><path d="M20,10 L20,3 M26,14.5 L32.5,10.3 M24,21.5 L28.3,28 M16,21.5 L11.7,28 M14,14.5 L7.5,10.3" stroke="var(--night-950)" stroke-width="1.3" fill="none" /></svg>
      </div>
      <div class="prop prop--whistle">
        <svg viewBox="0 0 48 32" fill="none"><path d="M14 16a10 10 0 1 0 20 0 10 10 0 0 0-20 0Z" stroke="var(--lime-300)" stroke-width="1.6" /><circle cx="24" cy="16" r="3.2" fill="var(--lime-300)" /><path d="M14 16H4a2 2 0 0 0-2 2v0a2 2 0 0 0 2 2h5" stroke="var(--lime-300)" stroke-width="1.6" stroke-linecap="round" /></svg>
      </div>
      <div class="prop prop--boot">
        <svg viewBox="0 0 48 34" fill="none"><path d="M8 6v11.5c0 1.5.8 2.4 2 3.2l9 5.8c1 .7 2.2 1 3.4 1h16.4c1.8 0 2.9-2 1.9-3.5l-3-4.5a6 6 0 0 0-3.8-2.5l-8.4-1.7a3 3 0 0 1-2-1.4L19.5 6.6A3 3 0 0 0 17 5h-6a3 3 0 0 0-3 1Z" stroke="var(--lime-300)" stroke-width="1.6" stroke-linejoin="round" /><path d="M8 22.5h30" stroke="var(--lime-300)" stroke-width="1.6" /><path d="M12 6.3l3.3 6.4M17 6.3l3.3 6.4" stroke="var(--lime-300)" stroke-width="1.4" stroke-linecap="round" /></svg>
      </div>
      <div class="prop prop--trophy">
        <svg viewBox="0 0 40 46" fill="none"><path d="M11 6h18v10a9 9 0 0 1-18 0V6Z" stroke="var(--amber-400)" stroke-width="1.6" /><path d="M11 9H5a4 4 0 0 0 4.5 6.5M29 9h6a4 4 0 0 1-4.5 6.5" stroke="var(--amber-400)" stroke-width="1.6" /><path d="M20 25v6M13 37h14l-1.6-4.5a2 2 0 0 0-1.9-1.5h-6.9a2 2 0 0 0-1.9 1.5L13 37Z" stroke="var(--amber-400)" stroke-width="1.6" stroke-linejoin="round" /></svg>
      </div>
      <div class="prop prop--flag">
        <svg viewBox="0 0 30 46" fill="none"><path d="M4 44V4" stroke="var(--lime-300)" stroke-width="1.8" stroke-linecap="round" /><path d="M4 5l20 6-20 6V5Z" fill="var(--lime-300)" opacity=".85" /><path d="M2 44a10 10 0 0 1 10-10" stroke="var(--lime-300)" stroke-width="1.4" /></svg>
      </div>
    </div>

    <router-link to="/" class="brand">
  <img :src="logo" alt="DreamLeague" class="brand__logo" />
  <span><strong>Dream</strong>League</span>
</router-link>

    <div class="content">
      <!-- Cột thông tin -->
      <div class="pitch-info">
        <span class="eyebrow">Chào mừng trở lại</span>
        <h1 class="headline">
          Giữ sân trong&nbsp;<span class="headline__accent">30 giây,</span><br />
          đá ngay hôm nay
        </h1>
        <p class="desc">
          Đăng nhập để xem lịch trống theo thời gian thực, quản lý lịch sử đặt sân
          và nhận ưu đãi dành riêng cho thành viên DreamLeague.
        </p>

        <!-- Sơ đồ đường chuyền động -->
        <div class="pitch-diagram" role="img" aria-label="Sơ đồ đường chuyền minh hoạ">
          <svg viewBox="0 0 320 150" xmlns="http://www.w3.org/2000/svg">
            <rect x="4" y="4" width="312" height="142" rx="10" class="pitch-outline pitch-draw" style="--pd:0s" />
            <line x1="160" y1="4" x2="160" y2="146" class="pitch-outline pitch-draw" style="--pd:.15s" />
            <circle cx="160" cy="75" r="20" class="pitch-outline pitch-draw" style="--pd:.3s" />
            <path id="passPath" d="M40,118 C86,104 108,74 160,74 C206,74 220,42 268,26" class="pass-path pitch-draw" style="--pd:.45s" />
            <g class="players">
              <circle cx="40" cy="118" r="6" class="player" style="--pi:0" />
              <circle cx="118" cy="92" r="6" class="player" style="--pi:1" />
              <circle cx="196" cy="58" r="6" class="player" style="--pi:2" />
              <circle cx="268" cy="26" r="6" class="player player--target" style="--pi:3" />
            </g>
            <circle r="4" class="ball-dot">
              <animateMotion dur="4.2s" repeatCount="indefinite" rotate="auto" calcMode="linear">
                <mpath href="#passPath" />
              </animateMotion>
            </circle>
          </svg>
        </div>

        <ul class="feature-list">
          <li v-for="(f, i) in features" :key="f" class="feature" :style="{ '--i': i }">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none">
              <path class="check-draw" d="M5 13L9.5 17.5L19 7" stroke="var(--lime-400)" stroke-width="2.8" stroke-linecap="round" stroke-linejoin="round" />
            </svg>
            {{ f }}
          </li>
        </ul>

        <div class="stat-row">
          <div class="stat" v-for="(s, i) in stats" :key="s.label" :style="{ '--i': i }">
            <p class="stat__num">{{ s.value }}<span>{{ s.suffix }}</span></p>
            <p class="stat__label">{{ s.label }}</p>
          </div>
        </div>
      </div>

      <!-- Vé đăng nhập -->
      <div
        class="ticket-wrap"
        :style="{ transform: `perspective(1100px) rotateX(${tilt.x}deg) rotateY(${tilt.y}deg)` }"
        @mousemove="onCardMove"
        @mouseleave="onCardLeave"
      >
        <div class="ticket">
          <span class="ticket__notch ticket__notch--l"></span>
          <span class="ticket__notch ticket__notch--r"></span>
          <span class="ticket__chase" aria-hidden="true"></span>
          <div class="ticket__particles" aria-hidden="true">
            <span v-for="n in 12" :key="n" class="spark" :style="{ '--i': n }"></span>
          </div>

          <router-link to="/" class="ticket__mobile-brand">
  <img :src="logo" alt="DreamLeague" class="ticket__mobile-logo" />
  <span><strong>Dream</strong>League</span>
</router-link>

          <div class="ticket__head">
            <div>
              <span class="ticket__kicker">Vé vào sân · Thành viên</span>
              <h2>Đăng nhập</h2>
            </div>
            <router-link to="/" class="ticket__home">
              <svg width="15" height="15" viewBox="0 0 24 24" fill="none">
                <path d="M19 12H5M5 12L11 6M5 12L11 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
              </svg>
              <span>Quay lại</span>
            </router-link>
          </div>

          <div class="ticket__perf" aria-hidden="true"></div>

          <Transition name="alert-drop">
            <div v-if="errorMessage" class="alert alert--error">
              <svg width="17" height="17" viewBox="0 0 24 24" fill="none">
                <circle cx="12" cy="12" r="9" stroke="#c8483a" stroke-width="1.6" />
                <path d="M12 8V13" stroke="#c8483a" stroke-width="1.6" stroke-linecap="round" />
                <circle cx="12" cy="16.2" r="0.9" fill="#c8483a" />
              </svg>
              <span>{{ errorMessage }}</span>
            </div>
          </Transition>
          <Transition name="alert-drop">
            <div v-if="infoMessage" class="alert alert--success">
              <svg width="17" height="17" viewBox="0 0 24 24" fill="none">
                <circle cx="12" cy="12" r="9" stroke="var(--turf-700)" stroke-width="1.6" />
                <path d="M12 11V16" stroke="var(--turf-700)" stroke-width="1.6" stroke-linecap="round" />
                <circle cx="12" cy="8" r="0.9" fill="var(--turf-700)" />
              </svg>
              <span>{{ infoMessage }}</span>
            </div>
          </Transition>

          <form class="ticket-form" novalidate @submit.prevent="onSubmit">
            <div class="line-field" :class="{ 'line-field--filled': form.identifier, 'line-field--error': errors.identifier }">
              <label for="identifier">Email hoặc số điện thoại</label>
              <div class="line-field__row">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
                  <path d="M3 6.5C3 5.67 3.67 5 4.5 5H19.5C20.33 5 21 5.67 21 6.5V17.5C21 18.33 20.33 19 19.5 19H4.5C3.67 19 3 18.33 3 17.5V6.5Z" stroke="currentColor" stroke-width="1.5" />
                  <path d="M4 6.5L12 13L20 6.5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
                </svg>
                <input id="identifier" v-model.trim="form.identifier" type="text" autocomplete="username" @blur="validateField('identifier')" />
              </div>
              <span class="line-field__bar"></span>
              <p v-if="errors.identifier" class="line-field__error">{{ errors.identifier }}</p>
            </div>

            <div class="line-field" :class="{ 'line-field--filled': form.password, 'line-field--error': errors.password }">
              <label for="password">Mật khẩu</label>
              <div class="line-field__row">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
                  <rect x="5" y="11" width="14" height="9" rx="2" stroke="currentColor" stroke-width="1.5" />
                  <path d="M8 11V8a4 4 0 0 1 8 0v3" stroke="currentColor" stroke-width="1.5" />
                </svg>
                <input
                  id="password"
                  v-model="form.password"
                  :type="showPassword ? 'text' : 'password'"
                  autocomplete="current-password"
                  @blur="validateField('password')"
                />
                <button type="button" class="line-field__toggle" :aria-label="showPassword ? 'Ẩn mật khẩu' : 'Hiện mật khẩu'" @click="showPassword = !showPassword">
                  <svg v-if="showPassword" width="16" height="16" viewBox="0 0 24 24" fill="none">
                    <path d="M2 12C2 12 5.5 5.5 12 5.5S22 12 22 12 18.5 18.5 12 18.5 2 12 2 12Z" stroke="currentColor" stroke-width="1.5" />
                    <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="1.5" />
                  </svg>
                  <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none">
                    <path d="M3 3L21 21" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
                    <path d="M10.6 5.7C11.05 5.6 11.52 5.5 12 5.5C18.5 5.5 22 12 22 12C22 12 21.18 13.5 19.6 15.1M6.4 6.9C4 8.5 2 12 2 12C2 12 5.5 18.5 12 18.5C13.7 18.5 15.16 18.07 16.4 17.4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
                  </svg>
                </button>
              </div>
              <span class="line-field__bar"></span>
              <p v-if="errors.password" class="line-field__error">{{ errors.password }}</p>
            </div>

            <div class="ticket-options">
              <label class="check">
                <input type="checkbox" v-model="form.remember" />
                <span class="check__box"></span>
                Ghi nhớ đăng nhập
              </label>
              <router-link to="/quen-mat-khau" class="forgot">Quên mật khẩu?</router-link>
            </div>

            <button type="submit" class="kickoff-btn" :disabled="submitting">
              <span class="kickoff-btn__ring" aria-hidden="true"></span>
              <span class="kickoff-btn__shine" aria-hidden="true"></span>
              <span v-if="submitting" class="ball-spinner" aria-hidden="true">
                <svg viewBox="0 0 24 24" width="17" height="17">
                  <circle cx="12" cy="12" r="9" fill="#fff" />
                  <path d="M12 6L15.5 8.5L14.2 12.7H9.8L8.5 8.5Z" fill="#0e2a1b" />
                </svg>
              </span>
              <span>{{ submitting ? 'Đang đăng nhập...' : 'Vào sân · Đăng nhập' }}</span>
            </button>
          </form>

          <div class="ticket-divider">Hoặc tiếp tục với</div>
          <div class="google-box"><div id="googleSignInBtn"></div></div>

          <p class="ticket-switch">Chưa có tài khoản? <router-link to="/dang-ky">Đăng ký ngay</router-link></p>
        </div>
      </div>
    </div>

    <!-- ===== CON TRỎ CHUỘT HÌNH QUẢ BÓNG ===== -->
    <div v-if="cursorEnabled" ref="cursorBall" class="ball-cursor" :class="{ 'ball-cursor--target': isPointerTarget, 'ball-cursor--down': isPressed }" aria-hidden="true">
      <svg viewBox="0 0 40 40">
        <circle cx="20" cy="20" r="18" fill="var(--chalk-050)" stroke="var(--night-950)" stroke-width="1.6" />
        <polygon points="20,10 26,14.5 24,21.5 16,21.5 14,14.5" fill="var(--night-950)" />
        <path d="M20,10 L20,3 M26,14.5 L32.5,10.3 M24,21.5 L28.3,28 M16,21.5 L11.7,28 M14,14.5 L7.5,10.3" stroke="var(--night-950)" stroke-width="1.3" fill="none" />
      </svg>
      <span class="ball-cursor__shadow"></span>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import authService from '../../services/authService'
import { initGoogleSignIn } from '../../services/googleAuth'
import logo from '../../Image/logo DreamLeague.webp'

const router = useRouter()
const route = useRoute()

const form = reactive({ identifier: '', password: '', remember: true })
const errors = reactive({ identifier: '', password: '' })

const showPassword = ref(false)
const submitting = ref(false)
const errorMessage = ref('')
const infoMessage = ref('')

const features = [
  'Lưu sân yêu thích & đặt lại nhanh',
  'Theo dõi lịch sử & hoá đơn đặt sân',
  'Nhận ưu đãi dành riêng cho thành viên'
]

const stats = [
  { value: '120', suffix: '+', label: 'Sân liên kết' },
  { value: '35', suffix: 'k+', label: 'Lượt đặt / năm' },
  { value: '4.8', suffix: '/5', label: 'Đánh giá' }
]

// Spotlight theo con trỏ chuột trên toàn bộ khung cảnh
const spotlight = reactive({ x: 50, y: 30 })
function onSceneMove(e) {
  const rect = e.currentTarget.getBoundingClientRect()
  spotlight.x = ((e.clientX - rect.left) / rect.width) * 100
  spotlight.y = ((e.clientY - rect.top) / rect.height) * 100
}
function onSceneLeave() {
  spotlight.x = 50
  spotlight.y = 30
}

// Nghiêng nhẹ tấm vé theo chuột (bỏ qua nếu người dùng yêu cầu giảm chuyển động)
const prefersReducedMotion = typeof window !== 'undefined' && window.matchMedia
  ? window.matchMedia('(prefers-reduced-motion: reduce)').matches
  : false
const tilt = reactive({ x: 0, y: 0 })
function onCardMove(e) {
  if (prefersReducedMotion) return
  const rect = e.currentTarget.getBoundingClientRect()
  const px = (e.clientX - rect.left) / rect.width - 0.5
  const py = (e.clientY - rect.top) / rect.height - 0.5
  tilt.x = +(py * -5).toFixed(2)
  tilt.y = +(px * 5).toFixed(2)
}
function onCardLeave() {
  tilt.x = 0
  tilt.y = 0
}

// ===== Con trỏ chuột hình quả bóng: chỉ bật với chuột thật, không phải cảm ứng =====
const cursorEnabled = ref(false)
const cursorBall = ref(null)
const isPointerTarget = ref(false)
const isPressed = ref(false)
let mouseX = 0
let mouseY = 0
let ballX = 0
let ballY = 0
let rotation = 0
let rafId = null

function onWindowMouseMove(e) {
  mouseX = e.clientX
  mouseY = e.clientY
}
function onWindowMouseOver(e) {
  isPointerTarget.value = !!e.target.closest('a, button, input, label, [role="button"]')
}
function onWindowMouseDown() {
  isPressed.value = true
}
function onWindowMouseUp() {
  isPressed.value = false
}
function tickCursor() {
  const ease = 0.22
  const dx = mouseX - ballX
  const dy = mouseY - ballY
  ballX += dx * ease
  ballY += dy * ease
  rotation += dx * 0.7
  if (cursorBall.value) {
    cursorBall.value.style.transform = `translate(${ballX - 15}px, ${ballY - 15}px) rotate(${rotation}deg)`
  }
  rafId = requestAnimationFrame(tickCursor)
}

onMounted(() => {
  if (route.query.registered) {
    infoMessage.value = 'Tạo tài khoản thành công! Vui lòng đăng nhập để tiếp tục.'
  }
  initGoogleSignIn('googleSignInBtn', handleGoogleSuccess)

  const hasFinePointer = typeof window !== 'undefined' && window.matchMedia
    ? window.matchMedia('(pointer: fine)').matches
    : false
  cursorEnabled.value = hasFinePointer && !prefersReducedMotion
  if (cursorEnabled.value) {
    window.addEventListener('mousemove', onWindowMouseMove)
    window.addEventListener('mouseover', onWindowMouseOver)
    window.addEventListener('mousedown', onWindowMouseDown)
    window.addEventListener('mouseup', onWindowMouseUp)
    rafId = requestAnimationFrame(tickCursor)
  }
})

onUnmounted(() => {
  window.removeEventListener('mousemove', onWindowMouseMove)
  window.removeEventListener('mouseover', onWindowMouseOver)
  window.removeEventListener('mousedown', onWindowMouseDown)
  window.removeEventListener('mouseup', onWindowMouseUp)
  if (rafId) cancelAnimationFrame(rafId)
})

async function handleGoogleSuccess(credential) {
  errorMessage.value = ''
  infoMessage.value = ''
  submitting.value = true
  try {
    const user = await authService.loginWithGoogle(credential)
    const redirectUrl = authService.duongDanTheoVaiTro(user.vaiTro)
    router.push(redirectUrl)
  } catch (err) {
    errorMessage.value = err.message || 'Đăng nhập bằng Google thất bại. Vui lòng thử lại!'
  } finally {
    submitting.value = false
  }
}

function validateField(field) {
  if (field === 'identifier') {
    if (!form.identifier) {
      errors.identifier = 'Vui lòng nhập email hoặc số điện thoại'
    } else {
      const isEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.identifier)
      const isPhone = /^0\d{9,10}$/.test(form.identifier)
      errors.identifier = isEmail || isPhone ? '' : 'Email hoặc số điện thoại không hợp lệ'
    }
  }
  if (field === 'password') {
    errors.password = form.password.length >= 6 ? '' : 'Mật khẩu phải có ít nhất 6 ký tự'
  }
}

function validateAll() {
  validateField('identifier')
  validateField('password')
  return !errors.identifier && !errors.password
}

async function onSubmit() {
  errorMessage.value = ''
  infoMessage.value = ''
  if (!validateAll()) return
  submitting.value = true
  try {
    const user = await authService.login({ identifier: form.identifier, password: form.password })
    const redirectUrl = authService.duongDanTheoVaiTro(user.vaiTro)
    router.push(redirectUrl)
  } catch (err) {
    errorMessage.value = err.message || 'Email hoặc mật khẩu không đúng. Vui lòng thử lại.'
  } finally {
    submitting.value = false
  }
}
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Bebas+Neue&family=Oswald:wght@500;600;700&family=Manrope:wght@400;500;600;700;800&family=Space+Mono:wght@400;700&display=swap');
</style>

<style scoped>
.scene {
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
  --ink-900: #0b1710;
  --slate-500: #5c6b60;
  --font-display: 'Oswald', 'Manrope', sans-serif;
  --font-body: 'Manrope', sans-serif;
  --font-mono: 'Space Mono', monospace;
  --font-clock: 'Bebas Neue', 'Oswald', sans-serif;

  position: relative;
  min-height: 100vh;
  width: 100%;
  overflow: hidden;
  background:
    repeating-linear-gradient(115deg, rgba(20, 60, 38, 0.55) 0 90px, rgba(9, 34, 21, 0.55) 90px 180px),
    radial-gradient(120% 100% at 15% 0%, var(--night-700) 0%, var(--night-950) 62%);
  font-family: var(--font-body);
  color: var(--chalk-050);
}
.scene--cursor, .scene--cursor * { cursor: none !important; }
.scene--cursor input, .scene--cursor textarea { cursor: text !important; }
.brand__logo {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  object-fit: contain;
  background: var(--chalk-050);
  padding: 2px;
  box-shadow: 0 0 0 2px rgba(182, 255, 60, .35);
}
.ticket__mobile-logo {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  object-fit: contain;
  background: var(--chalk-050);
  padding: 2px;
}

/* ===== LỚP NỀN: ÁNH SÁNG, CỎ, HẠT BỤI ===== */
.scene__spotlight {
  position: absolute; inset: 0; pointer-events: none; z-index: 1;
  background: radial-gradient(560px circle at var(--sx) var(--sy), rgba(182, 255, 60, 0.10), transparent 62%);
}
.scene__vignette { position: absolute; inset: 0; pointer-events: none; z-index: 1; box-shadow: inset 0 0 240px rgba(0, 0, 0, 0.6); }
.floodlight {
  position: absolute; top: -12%; width: 55vh; height: 145vh;
  background: conic-gradient(from 90deg at 50% 0%, transparent 42%, rgba(255, 244, 214, 0.16) 50%, transparent 58%);
  mix-blend-mode: screen; transform-origin: top center; pointer-events: none; z-index: 1;
}
.floodlight--l { left: -14%; }
.floodlight--r { right: -14%; }
.grass-drift {
  position: absolute; inset: -10% -20%; z-index: 0; opacity: 0.5;
  background: repeating-linear-gradient(100deg, rgba(35, 147, 90, 0.09) 0 70px, transparent 70px 140px);
}
.grass-mow {
  position: absolute; inset: 0; z-index: 0; opacity: 0.6;
  background: repeating-linear-gradient(115deg, rgba(255, 255, 255, 0.025) 0 90px, transparent 90px 180px);
}
.motes { position: absolute; inset: 0; pointer-events: none; z-index: 1; }
.mote {
  position: absolute; bottom: -10px; left: calc((var(--i) * 7.2%) + 1%);
  width: 3px; height: 3px; border-radius: 50%; background: var(--lime-300); opacity: 0;
}
.pitch-lines { position: absolute; inset: 0; width: 100%; height: 100%; opacity: 0.1; z-index: 1; pointer-events: none; }
.draw-line { stroke-dasharray: 1400; stroke-dashoffset: 0; }

/* ===== ĐẠO CỤ BÓNG ĐÁ TRÔI NỔI ===== */
.field-props { position: absolute; inset: 0; z-index: 1; pointer-events: none; }
.prop { position: absolute; opacity: 0.16; filter: drop-shadow(0 6px 10px rgba(0, 0, 0, 0.35)); }
.prop svg { width: 100%; height: 100%; display: block; }
.prop--ball-1 { width: 58px; height: 58px; top: 12%; right: 7%; }
.prop--ball-2 { width: 34px; height: 34px; bottom: 16%; right: 22%; opacity: 0.12; }
.prop--whistle { width: 70px; height: 46px; top: 68%; left: 4%; }
.prop--boot { width: 76px; height: 54px; top: 8%; left: 3%; opacity: 0.13; }
.prop--trophy { width: 52px; height: 60px; bottom: 8%; left: 20%; opacity: 0.13; }
.prop--flag { width: 40px; height: 62px; top: 32%; right: 3%; opacity: 0.13; }

/* ===== BẢNG ===== */
.brand {
  position: relative; z-index: 3; display: inline-flex; align-items: center; gap: 9px;
  font-size: 20px; color: var(--chalk-050); font-weight: 500; font-family: var(--font-display);
  margin: 26px 0 0 44px;
}
.brand span strong { color: var(--lime-400); font-weight: 700; }

/* ===== BỐ CỤC NỘI DUNG ===== */
.content {
  position: relative; z-index: 3;
  display: grid; grid-template-columns: 1.05fr 0.95fr; align-items: center;
  gap: 24px; max-width: 1320px; margin: 0 auto; padding: 40px 44px 64px;
  min-height: calc(100vh - 78px);
}

.pitch-info { max-width: 540px; }
.eyebrow {
  display: inline-block; font-family: var(--font-mono); font-size: 12px;
  letter-spacing: 0.16em; text-transform: uppercase; color: var(--lime-300);
}
.headline {
  font-family: var(--font-display); font-weight: 600; color: var(--chalk-050);
  font-size: clamp(32px, 3.4vw, 44px); line-height: 1.2; margin-top: 14px;
}
.headline__accent { color: var(--lime-400); }
.desc { margin-top: 16px; font-size: 15.5px; color: var(--chalk-200); opacity: 0.78; line-height: 1.65; max-width: 460px; }

/* ===== SƠ ĐỒ ĐƯỜNG CHUYỀN ===== */
.pitch-diagram { margin-top: 26px; width: 100%; max-width: 400px; }
.pitch-diagram svg { width: 100%; height: auto; display: block; }
.pitch-outline { fill: none; stroke: rgba(247, 251, 244, 0.28); stroke-width: 1.4; }
.pass-path { fill: none; stroke: var(--lime-400); stroke-width: 1.6; stroke-dasharray: 5 6; opacity: 0.85; }
.player { fill: var(--night-950); stroke: var(--chalk-050); stroke-width: 1.6; }
.player--target { stroke: var(--lime-400); }
.ball-dot { fill: var(--lime-400); }
.pitch-draw { stroke-dasharray: 900; stroke-dashoffset: 900; }

.feature-list { margin: 26px 0 0; padding: 0; list-style: none; display: flex; flex-direction: column; gap: 14px; }
.feature { display: flex; align-items: center; gap: 11px; color: var(--chalk-050); font-size: 14px; font-weight: 500; }
.feature svg { flex-shrink: 0; background: rgba(182, 255, 60, 0.1); border-radius: 50%; padding: 4px; box-sizing: content-box; }

.stat-row { display: flex; gap: 40px; margin-top: 30px; padding-top: 24px; border-top: 1px solid rgba(255, 255, 255, 0.12); }
.stat__num { font-family: var(--font-clock); font-size: 32px; font-weight: 400; color: var(--chalk-050); line-height: 1; letter-spacing: 0.01em; }
.stat__num span { color: var(--lime-400); }
.stat__label { font-size: 12px; color: var(--chalk-200); opacity: 0.65; margin-top: 6px; font-family: var(--font-mono); }

/* ===== VÉ ĐĂNG NHẬP (TICKET) — nền tối kiểu sân đêm ===== */
.ticket-wrap { justify-self: center; width: 100%; max-width: 420px; transition: transform 0.25s ease-out; will-change: transform; }
.ticket {
  position: relative;
  background: linear-gradient(165deg, rgba(24, 61, 41, 0.94), rgba(6, 15, 10, 0.97));
  backdrop-filter: blur(14px);
  border: 1px solid rgba(182, 255, 60, 0.16);
  border-radius: 22px; padding: 34px 36px 30px; color: var(--chalk-050);
  box-shadow: 0 40px 90px -30px rgba(0, 0, 0, 0.65), 0 0 0 1px rgba(255, 255, 255, 0.04), 0 0 70px -20px rgba(182, 255, 60, 0.18);
  overflow: hidden;
}
/* nội dung thật luôn nổi trên lớp hạt sáng/viền chạy */
.ticket > * { position: relative; z-index: 1; }
.ticket__notch, .ticket__chase, .ticket__particles { z-index: 0; }
.ticket__notch {
  position: absolute; top: 128px; width: 26px; height: 26px; border-radius: 50%;
  background: radial-gradient(circle at 40% 35%, var(--night-700), var(--night-950));
}
.ticket__notch--l { left: -13px; }
.ticket__notch--r { right: -13px; }
.ticket__perf {
  margin: 22px -36px 22px; height: 0;
  border-top: 1.5px dashed rgba(247, 251, 244, 0.18);
}
.ticket__chase {
  position: absolute; inset: -2px; border-radius: 24px; padding: 2px;
  background: conic-gradient(from var(--chase-angle, 0deg), transparent 0deg, var(--lime-400) 25deg, transparent 70deg);
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor; mask-composite: exclude; opacity: 0.6; pointer-events: none;
}
/* hạt sáng rơi bên trong thẻ */
.ticket__particles { position: absolute; inset: 0; overflow: hidden; pointer-events: none; }
.spark {
  position: absolute; top: -8%; left: calc((var(--i) * 8%) + 2%);
  width: 3px; height: 3px; border-radius: 50%; background: var(--lime-300);
  box-shadow: 0 0 6px 1px rgba(182, 255, 60, 0.55); opacity: 0;
}

.ticket__mobile-brand { display: none; align-items: center; gap: 8px; color: var(--chalk-050); font-family: var(--font-display); font-size: 17px; margin-bottom: 18px; }
.ticket__mobile-brand strong { color: var(--lime-300); }

.ticket__head { display: flex; align-items: flex-start; justify-content: space-between; }
.ticket__kicker { font-family: var(--font-mono); font-size: 11px; letter-spacing: 0.1em; text-transform: uppercase; color: var(--lime-300); }
.ticket__head h2 { font-family: var(--font-display); font-size: 26px; font-weight: 600; margin-top: 6px; color: var(--chalk-050); }
.ticket__home {
  display: inline-flex; align-items: center; gap: 6px; height: 32px; padding: 0 14px 0 10px;
  border-radius: 999px; border: 1.5px solid rgba(247, 251, 244, 0.18); color: var(--chalk-200);
  font-family: var(--font-mono); font-size: 12px; font-weight: 600; letter-spacing: 0.02em; white-space: nowrap;
  transition: border-color .15s ease, color .15s ease, transform .15s ease, background-color .15s ease;
}
.ticket__home:hover { border-color: var(--lime-400); color: var(--lime-300); background: rgba(182, 255, 60, 0.08); transform: translateX(-2px); }

/* Alert */
.alert { display: flex; align-items: flex-start; gap: 9px; padding: 12px 14px; border-radius: 12px; margin-bottom: 16px; font-size: 13.5px; line-height: 1.4; }
.alert--error { background: rgba(255, 90, 80, 0.12); border: 1px solid rgba(255, 90, 80, 0.32); color: #ff9686; }
.alert--success { background: rgba(182, 255, 60, 0.1); border: 1px solid rgba(182, 255, 60, 0.28); color: var(--lime-300); }
.alert-drop-enter-active { animation: alert-in .35s cubic-bezier(.34, 1.56, .64, 1); }
.alert-drop-leave-active { animation: alert-in .2s ease reverse; }
@keyframes alert-in { from { opacity: 0; transform: translateY(-8px); } to { opacity: 1; transform: translateY(0); } }

/* Trường nhập kiểu gạch chân (line field) */
.ticket-form { display: flex; flex-direction: column; margin-top: 4px; }
.line-field { margin-bottom: 20px; position: relative; }
.line-field label { display: block; font-size: 12.5px; font-weight: 600; color: var(--chalk-200); opacity: .85; margin-bottom: 6px; letter-spacing: 0.01em; }
.line-field__row { display: flex; align-items: center; gap: 10px; padding-bottom: 9px; color: var(--chalk-200); }
.line-field__row input {
  flex: 1; border: none; background: transparent; font-family: var(--font-body);
  font-size: 15px; color: var(--chalk-050); outline: none;
}
.line-field__row input::placeholder { color: rgba(247, 251, 244, 0.35); }
.line-field__bar { position: relative; display: block; height: 1.5px; background: rgba(247, 251, 244, 0.18); }
.line-field__bar::after {
  content: ""; position: absolute; left: 0; top: 0; height: 100%; width: 0;
  background: var(--lime-400); transition: width .28s cubic-bezier(.4, 0, .2, 1);
}
.line-field:focus-within .line-field__bar::after { width: 100%; }
.line-field:focus-within label { color: var(--lime-300); }
.line-field:focus-within .line-field__row { color: var(--lime-300); }
.line-field--error .line-field__bar { background: rgba(255, 90, 80, 0.32); }
.line-field--error .line-field__bar::after { background: #ff6a56; width: 100%; }
.line-field--error label { color: #ff9686; }
.line-field__toggle { background: none; border: none; color: inherit; padding: 2px; display: flex; transition: transform .15s ease; }
.line-field__toggle:hover { transform: scale(1.08); }
.line-field__error { color: #ff9686; font-size: 12px; margin-top: 6px; font-weight: 500; }

.ticket-options { display: flex; align-items: center; justify-content: space-between; margin: 6px 0 22px; }
.forgot { font-size: 13px; font-weight: 600; color: var(--lime-300); }
.forgot:hover { color: var(--lime-400); text-decoration: underline; }
.check { display: inline-flex; align-items: center; gap: 9px; font-size: 13.5px; color: var(--chalk-200); cursor: pointer; user-select: none; }
.check input { position: absolute; opacity: 0; height: 0; width: 0; }
.check__box { width: 17px; height: 17px; background: rgba(247, 251, 244, 0.06); border: 1.5px solid rgba(247, 251, 244, 0.3); border-radius: 4px; position: relative; transition: background-color .15s, border-color .15s; }
.check input:checked ~ .check__box { background: var(--lime-400); border-color: var(--lime-400); }
.check__box::after { content: ""; position: absolute; display: none; left: 4px; top: 1px; width: 4px; height: 8px; border: solid var(--night-950); border-width: 0 2px 2px 0; transform: rotate(45deg); }
.check input:checked ~ .check__box::after { display: block; }

/* Nút "vào sân" */
.kickoff-btn {
  position: relative; width: 100%; height: 50px; border: none; border-radius: 999px; cursor: pointer;
  background: linear-gradient(135deg, var(--turf-500), var(--turf-700)); color: #fff;
  font-weight: 700; font-family: var(--font-body); font-size: 15px;
  display: flex; align-items: center; justify-content: center; gap: 8px;
  overflow: visible; isolation: isolate; transition: transform .15s ease, box-shadow .15s ease;
}
.kickoff-btn:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 14px 28px -10px rgba(20, 90, 51, 0.5); }
.kickoff-btn:active:not(:disabled) { transform: translateY(0); }
.kickoff-btn:disabled { opacity: 0.85; cursor: default; }
.kickoff-btn__ring {
  position: absolute; inset: -4px; border-radius: 999px; border: 1.5px solid var(--lime-400);
  opacity: 0; z-index: -1;
}
.kickoff-btn:hover:not(:disabled) .kickoff-btn__ring { opacity: 0.5; }
.kickoff-btn__shine {
  position: absolute; inset: 0; z-index: 1; border-radius: 999px; overflow: hidden;
  background: linear-gradient(115deg, transparent 20%, rgba(255, 255, 255, 0.35) 45%, transparent 70%);
  transform: translateX(-120%);
}
.kickoff-btn:hover:not(:disabled) .kickoff-btn__shine { animation: shine .9s ease; }
@keyframes shine { to { transform: translateX(120%); } }
.kickoff-btn > span:not(.kickoff-btn__ring):not(.kickoff-btn__shine) { position: relative; z-index: 2; }
.ball-spinner { display: inline-flex; }

.ticket-divider { display: flex; align-items: center; text-align: center; color: var(--chalk-200); opacity: .75; font-size: 12.5px; font-weight: 500; margin: 22px 0; }
.ticket-divider::before, .ticket-divider::after { content: ""; flex: 1; border-bottom: 1px solid rgba(247, 251, 244, 0.18); }
.ticket-divider::before { margin-right: 14px; }
.ticket-divider::after { margin-left: 14px; }

.google-box { display: flex; justify-content: center; width: 100%; margin-bottom: 20px; padding: 4px; border-radius: 14px; background: rgba(247, 251, 244, 0.05); }

.ticket-switch { text-align: center; font-size: 14px; color: var(--chalk-200); opacity: .8; }
.ticket-switch a { color: var(--lime-300); font-weight: 700; }
.ticket-switch a:hover { color: var(--lime-400); text-decoration: underline; }

/* ===== CON TRỎ CHUỘT HÌNH QUẢ BÓNG ===== */
.ball-cursor {
  position: fixed; top: 0; left: 0; width: 30px; height: 30px; z-index: 999;
  pointer-events: none; will-change: transform; transition: width .18s ease, height .18s ease;
}
.ball-cursor svg { width: 100%; height: 100%; display: block; filter: drop-shadow(0 4px 6px rgba(0, 0, 0, 0.45)); transition: transform .18s cubic-bezier(.34, 1.56, .64, 1); }
.ball-cursor__shadow {
  position: absolute; left: 50%; bottom: -6px; width: 60%; height: 6px; border-radius: 50%;
  background: rgba(0, 0, 0, 0.35); transform: translateX(-50%); filter: blur(2px);
}
.ball-cursor--target { width: 40px; height: 40px; }
.ball-cursor--target svg { transform: scale(1.08); filter: drop-shadow(0 0 10px rgba(182, 255, 60, 0.65)) drop-shadow(0 4px 6px rgba(0, 0, 0, 0.45)); }
.ball-cursor--down svg { transform: scale(0.78); }

/* ===== HIỆU ỨNG CHUYỂN ĐỘNG (chỉ bật khi trình duyệt cho phép) ===== */
@media (prefers-reduced-motion: no-preference) {
  .floodlight { animation: sweep 9s ease-in-out infinite alternate; }
  .floodlight--l { animation-delay: 0s; }
  .floodlight--r { animation-delay: -3.2s; }
  @keyframes sweep { 0% { transform: rotate(-13deg); } 100% { transform: rotate(13deg); } }

  .grass-drift { animation: drift 26s linear infinite; }
  .grass-mow { animation: drift 40s linear infinite reverse; }
  @keyframes drift { from { background-position: 0 0; } to { background-position: 400px 0; } }

  .mote { animation: rise 8s linear infinite; animation-delay: calc(var(--i) * -0.55s); }
  @keyframes rise { 0% { transform: translateY(0) scale(.6); opacity: 0; } 10% { opacity: .8; } 92% { opacity: .25; } 100% { transform: translateY(-100vh) scale(1.1); opacity: 0; } }

  .draw-line { stroke-dasharray: 1400; stroke-dashoffset: 1400; animation: draw 2.6s ease forwards; animation-delay: var(--delay); }
  @keyframes draw { to { stroke-dashoffset: 0; } }

  .prop { animation: prop-float 6s ease-in-out infinite, prop-in .6s ease forwards; opacity: 0; }
  .prop--ball-1 { animation-delay: .2s, .2s; animation-duration: 7s, .6s; }
  .prop--ball-2 { animation-delay: 1.1s, .35s; animation-duration: 5.5s, .6s; }
  .prop--whistle { animation-delay: .5s, .5s; animation-duration: 6.5s, .6s; }
  .prop--boot { animation-delay: .8s, .3s; animation-duration: 8s, .6s; }
  .prop--trophy { animation-delay: 1.4s, .65s; animation-duration: 7.2s, .6s; }
  .prop--flag { animation-delay: .3s, .45s; animation-duration: 6s, .6s; }
  @keyframes prop-in { to { opacity: 0.16; } }
  @keyframes prop-float { 0%, 100% { transform: translateY(0) rotate(-4deg); } 50% { transform: translateY(-16px) rotate(4deg); } }

  .brand { opacity: 0; animation: fade-down .6s ease forwards; }
  @keyframes fade-down { from { opacity: 0; transform: translateY(-8px); } to { opacity: 1; transform: translateY(0); } }
  .brand__mark { animation: spin-in .9s cubic-bezier(.34, 1.56, .64, 1) both; }
  @keyframes spin-in { from { transform: rotate(-90deg) scale(.4); opacity: 0; } to { transform: rotate(0) scale(1); opacity: 1; } }

  .eyebrow, .headline, .desc { opacity: 0; animation: fade-up .6s ease forwards; }
  .eyebrow { animation-delay: .15s; }
  .headline { animation-delay: .28s; }
  .desc { animation-delay: .42s; }
  @keyframes fade-up { to { opacity: 1; transform: translateY(0); } from { transform: translateY(10px); } }

  .headline__accent { display: inline-block; animation: accent-glow 2.8s ease-in-out infinite; animation-delay: 1.3s; }
  @keyframes accent-glow { 0%, 100% { text-shadow: 0 0 0 rgba(182, 255, 60, 0); } 50% { text-shadow: 0 0 22px rgba(182, 255, 60, 0.55); } }

  .pitch-diagram { opacity: 0; animation: fade-up .5s ease forwards; animation-delay: .5s; }
  .pitch-draw { animation: draw-pitch 1.2s ease forwards; animation-delay: calc(var(--pd) + .6s); }
  @keyframes draw-pitch { to { stroke-dashoffset: 0; } }
  .player { opacity: 0; transform-box: fill-box; transform-origin: center; animation: player-pop .4s cubic-bezier(.34,1.56,.64,1) forwards; animation-delay: calc(var(--pi) * 0.12s + 1.6s); }
  @keyframes player-pop { from { opacity: 0; transform: scale(0); } to { opacity: 1; transform: scale(1); } }
  .ball-dot { opacity: 0; animation: ball-in .3s ease forwards; animation-delay: 2.1s; }
  @keyframes ball-in { to { opacity: 1; } }

  .feature { opacity: 0; transform: translateX(-12px); animation: slide-right .5s ease forwards; animation-delay: calc(var(--i) * 0.12s + .95s); }
  @keyframes slide-right { to { opacity: 1; transform: translateX(0); } }
  .check-draw { stroke-dasharray: 30; stroke-dashoffset: 30; animation: draw-check .45s ease forwards; animation-delay: calc(var(--i, 0) * 0.12s + 1.25s); }
  @keyframes draw-check { to { stroke-dashoffset: 0; } }

  .stat { opacity: 0; animation: fade-up .55s ease forwards; animation-delay: calc(var(--i) * 0.1s + 1.5s); }
  .stat__num { display: inline-block; animation: num-glow 3.4s ease-in-out infinite; animation-delay: calc(var(--i) * 0.2s + 2.4s); }
  @keyframes num-glow { 0%, 100% { opacity: 1; } 50% { opacity: .72; text-shadow: 0 0 12px rgba(182, 255, 60, 0.4); } }

  .ticket { opacity: 0; animation: ticket-in .75s cubic-bezier(.16, 1, .3, 1) forwards; animation-delay: .3s; }
  @keyframes ticket-in { from { opacity: 0; transform: translateY(26px) scale(.97); } to { opacity: 1; transform: translateY(0) scale(1); } }
  .ticket__chase { animation: chase 5s linear infinite; }
  @keyframes chase { to { --chase-angle: 360deg; } }
  @property --chase-angle { syntax: '<angle>'; inherits: false; initial-value: 0deg; }

  .spark { animation: spark-fall calc(6s + (var(--i) * 0.5s)) linear infinite; animation-delay: calc(var(--i) * -0.8s); }
  @keyframes spark-fall {
    0% { transform: translateY(0) translateX(0); opacity: 0; }
    8% { opacity: .9; }
    50% { transform: translateY(240px) translateX(6px); }
    90% { opacity: .2; }
    100% { transform: translateY(480px) translateX(14px); opacity: 0; }
  }

  .kickoff-btn { opacity: 0; animation: fade-up .5s ease forwards; animation-delay: 1.4s; }
  .ball-spinner { animation: ball-bounce .6s ease-in-out infinite; }
  @keyframes ball-bounce { 0%, 100% { transform: translateY(0) rotate(0); } 50% { transform: translateY(-5px) rotate(160deg); } }
}

/* ===== RESPONSIVE ===== */
@media (max-width: 980px) {
  .content { grid-template-columns: 1fr; padding: 32px 22px 48px; gap: 40px; }
  .pitch-info { max-width: 100%; text-align: center; margin: 0 auto; }
  .stat-row { justify-content: center; }
  .feature-list { align-items: center; }
  .feature { justify-content: center; }
  .pitch-diagram { margin-left: auto; margin-right: auto; }
  .ticket-wrap { max-width: 440px; }
  .brand { margin-left: 22px; }
  .prop--boot, .prop--trophy, .prop--flag { display: none; }
}
@media (max-width: 640px) {
  .ticket { padding: 28px 22px 24px; }
  .ticket__mobile-brand { display: inline-flex; }
  .ticket__notch { top: 150px; }
  .ball-cursor { display: none; }
}
@media (pointer: coarse) {
  .scene--cursor, .scene--cursor * { cursor: auto !important; }
}
</style>