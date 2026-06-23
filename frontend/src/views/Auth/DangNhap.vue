<template>
  <div class="auth-page">
    <!-- Panel trái: thương hiệu -->
    <section class="auth-visual">
      <div class="auth-visual__glow auth-visual__glow--1"></div>
      <div class="auth-visual__glow auth-visual__glow--2"></div>

      <svg class="auth-visual__pitch" viewBox="0 0 480 900" preserveAspectRatio="none" xmlns="http://www.w3.org/2000/svg">
        <rect x="20" y="20" width="440" height="860" rx="14" stroke="white" stroke-width="2" fill="none" />
        <line x1="20" y1="450" x2="460" y2="450" stroke="white" stroke-width="2" />
        <circle cx="240" cy="450" r="70" stroke="white" stroke-width="2" fill="none" />
        <rect x="120" y="20" width="240" height="110" stroke="white" stroke-width="2" fill="none" />
        <rect x="120" y="770" width="240" height="110" stroke="white" stroke-width="2" fill="none" />
      </svg>

      <div class="auth-visual__top">
        <router-link to="/" class="auth-brand">
          <svg width="34" height="34" viewBox="0 0 36 36" fill="none">
            <circle cx="18" cy="18" r="17" stroke="var(--green-500)" stroke-width="2" />
            <path d="M10 13L18 8L26 13L24 22L18 26L12 22L10 13Z" fill="var(--white)" />
            <circle cx="18" cy="17" r="4.2" fill="var(--navy-900)" />
          </svg>
          <span><strong>Dream</strong>League</span>
        </router-link>
      </div>

      <div class="auth-visual__body">
        <span class="eyebrow">Chào mừng trở lại</span>
        <h1 class="auth-visual__title">Giữ sân trong 30 giây,<br />đá ngay <span>hôm nay</span></h1>
        <p class="auth-visual__desc">
          Đăng nhập để xem lịch trống theo thời gian thực, quản lý lịch sử đặt sân
          và nhận ưu đãi dành riêng cho thành viên DreamLeague.
        </p>

        <div class="auth-features">
          <div class="auth-feature" v-for="f in features" :key="f">
            <span class="auth-feature__icon">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
                <path d="M5 13L9.5 17.5L19 7" stroke="var(--green-500)" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round" />
              </svg>
            </span>
            {{ f }}
          </div>
        </div>
      </div>

      <div class="auth-visual__stats">
        <div class="auth-stat">
          <p class="auth-stat__num">120<span>+</span></p>
          <p class="auth-stat__label">Sân liên kết</p>
        </div>
        <div class="auth-stat">
          <p class="auth-stat__num">35<span>k+</span></p>
          <p class="auth-stat__label">Lượt đặt / năm</p>
        </div>
        <div class="auth-stat">
          <p class="auth-stat__num">4.8<span>/5</span></p>
          <p class="auth-stat__label">Đánh giá</p>
        </div>
      </div>
    </section>

    <!-- Panel phải: form đăng nhập -->
    <section class="auth-form-panel">
      <div class="auth-form-card">
        <router-link to="/" class="auth-form-card__mobile-brand auth-brand auth-brand--on-light">
          <svg width="30" height="30" viewBox="0 0 36 36" fill="none">
            <circle cx="18" cy="18" r="17" stroke="var(--green-600)" stroke-width="2" />
            <path d="M10 13L18 8L26 13L24 22L18 26L12 22L10 13Z" fill="var(--navy-900)" />
            <circle cx="18" cy="17" r="4.2" fill="var(--white)" />
          </svg>
          <span><strong>Dream</strong>League</span>
        </router-link>

        <router-link to="/" class="auth-back">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
            <path d="M19 12H5M5 12L11 6M5 12L11 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
          </svg>
          Về trang chủ
        </router-link>

        <div class="auth-form-card__head">
          <h1>Đăng nhập</h1>
          <p>Truy cập tài khoản để tiếp tục đặt sân tại DreamLeague.</p>
        </div>

        <div v-if="errorMessage" class="auth-alert auth-alert--error">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
            <circle cx="12" cy="12" r="9" stroke="#b13030" stroke-width="1.6" />
            <path d="M12 8V13" stroke="#b13030" stroke-width="1.6" stroke-linecap="round" />
            <circle cx="12" cy="16.2" r="0.9" fill="#b13030" />
          </svg>
          <span>{{ errorMessage }}</span>
        </div>
        <div v-if="infoMessage" class="auth-alert auth-alert--success">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
            <circle cx="12" cy="12" r="9" stroke="var(--green-700)" stroke-width="1.6" />
            <path d="M12 11V16" stroke="var(--green-700)" stroke-width="1.6" stroke-linecap="round" />
            <circle cx="12" cy="8" r="0.9" fill="var(--green-700)" />
          </svg>
          <span>{{ infoMessage }}</span>
        </div>

        <form class="auth-form" novalidate @submit.prevent="onSubmit">
          <div class="field">
            <label class="field__label" for="identifier">Email hoặc số điện thoại</label>
            <div class="field__input-wrap" :class="{ 'field__input-wrap--error': errors.identifier }">
              <span class="field__icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                  <path d="M3 6.5C3 5.67 3.67 5 4.5 5H19.5C20.33 5 21 5.67 21 6.5V17.5C21 18.33 20.33 19 19.5 19H4.5C3.67 19 3 18.33 3 17.5V6.5Z" stroke="currentColor" stroke-width="1.6" />
                  <path d="M4 6.5L12 13L20 6.5" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
                </svg>
              </span>
              <input
                id="identifier"
                v-model.trim="form.identifier"
                type="text"
                placeholder="ban@email.com hoặc 09xxxxxxxx"
                autocomplete="username"
                @blur="validateField('identifier')"
              />
            </div>
            <p v-if="errors.identifier" class="field__error">{{ errors.identifier }}</p>
          </div>

          <div class="field">
            <div class="field__label-row">
              <label class="field__label" for="password">Mật khẩu</label>
             
            </div>
            <div class="field__input-wrap" :class="{ 'field__input-wrap--error': errors.password }">
              <span class="field__icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                  <rect x="5" y="11" width="14" height="9" rx="2" stroke="currentColor" stroke-width="1.6" />
                  <path d="M8 11V8a4 4 0 0 1 8 0v3" stroke="currentColor" stroke-width="1.6" />
                </svg>
              </span>
              <input
                id="password"
                v-model="form.password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="••••••••"
                autocomplete="current-password"
                @blur="validateField('password')"
              />
              <button
                type="button"
                class="field__toggle"
                :aria-label="showPassword ? 'Ẩn mật khẩu' : 'Hiện mật khẩu'"
                @click="showPassword = !showPassword"
              >
                <svg v-if="showPassword" width="18" height="18" viewBox="0 0 24 24" fill="none">
                  <path d="M2 12C2 12 5.5 5.5 12 5.5S22 12 22 12 18.5 18.5 12 18.5 2 12 2 12Z" stroke="currentColor" stroke-width="1.6" />
                  <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="1.6" />
                </svg>
                <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none">
                  <path d="M3 3L21 21" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
                  <path
                    d="M10.6 5.7C11.05 5.6 11.52 5.5 12 5.5C18.5 5.5 22 12 22 12C22 12 21.18 13.5 19.6 15.1M6.4 6.9C4 8.5 2 12 2 12C2 12 5.5 18.5 12 18.5C13.7 18.5 15.16 18.07 16.4 17.4"
                    stroke="currentColor"
                    stroke-width="1.6"
                    stroke-linecap="round"
                  />
                </svg>
              </button>
            </div>
            <p v-if="errors.password" class="field__error">{{ errors.password }}</p>
          </div>

          <div class="auth-options">
            <label class="check">
              <input type="checkbox" v-model="form.remember" />
              <span class="check__box"></span>
              Ghi nhớ đăng nhập
            </label>
          </div>

          <button type="submit" class="btn btn-primary btn-block" :disabled="submitting">
            <span v-if="submitting" class="spinner"></span>
            {{ submitting ? 'Đang đăng nhập...' : 'Đăng nhập' }}
          </button>
        </form>

        <div class="auth-divider">Hoặc tiếp tục với</div>

        <div class="social-row">
          <button type="button" class="social-btn" @click="socialNotice">
            <svg width="18" height="18" viewBox="0 0 24 24">
              <path fill="#4285F4" d="M23.49 12.27c0-.79-.07-1.54-.2-2.27H12v4.51h6.47a5.53 5.53 0 0 1-2.4 3.63v3.02h3.86c2.26-2.09 3.56-5.17 3.56-8.89Z" />
              <path fill="#34A853" d="M12 24c3.24 0 5.95-1.08 7.93-2.84l-3.86-3.02c-1.07.72-2.45 1.15-4.07 1.15-3.13 0-5.78-2.11-6.73-4.96H1.27v3.12C3.24 21.3 7.27 24 12 24Z" />
              <path fill="#FBBC05" d="M5.27 14.33A7.2 7.2 0 0 1 4.89 12c0-.81.14-1.6.38-2.33V6.55H1.27A11.98 11.98 0 0 0 0 12c0 1.93.46 3.76 1.27 5.45l4-3.12Z" />
              <path fill="#EA4335" d="M12 4.75c1.77 0 3.35.61 4.6 1.8l3.42-3.42C17.95 1.19 15.24 0 12 0 7.27 0 3.24 2.7 1.27 6.55l4 3.12C6.22 6.86 8.87 4.75 12 4.75Z" />
            </svg>
            Google
          </button>
          <button type="button" class="social-btn" @click="socialNotice">
            <svg width="18" height="18" viewBox="0 0 24 24">
              <path
                fill="#1877F2"
                d="M24 12.07C24 5.4 18.63 0 12 0S0 5.4 0 12.07c0 5.99 4.39 10.96 10.13 11.86v-8.39H7.1v-3.47h3.03V9.41c0-3 1.79-4.66 4.53-4.66 1.31 0 2.69.23 2.69.23v2.96h-1.51c-1.49 0-1.96.92-1.96 1.87v2.26h3.33l-.53 3.47h-2.8V24C19.6 23.03 24 18.06 24 12.07Z"
              />
            </svg>
            Facebook
          </button>
        </div>

        <p class="auth-switch">Chưa có tài khoản? <router-link to="/dang-ky">Đăng ký ngay</router-link></p>
      </div>
    </section>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import authService from '../../services/authService'

const router = useRouter()
const route = useRoute()

const form = reactive({
  identifier: '',
  password: '',
  remember: true
})

const errors = reactive({
  identifier: '',
  password: ''
})

const showPassword = ref(false)
const submitting = ref(false)
const errorMessage = ref('')
const infoMessage = ref('')

const features = [
  'Lưu sân yêu thích & đặt lại nhanh',
  'Theo dõi lịch sử & hoá đơn đặt sân',
  'Nhận ưu đãi dành riêng cho thành viên'
]

onMounted(() => {
  if (route.query.registered) {
    infoMessage.value = 'Tạo tài khoản thành công! Vui lòng đăng nhập để tiếp tục.'
  }
})

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
    // ⚠️ Backend chưa hoàn thiện: authService.login() hiện chỉ trả về Promise
    // giả lập (resolve) để dựng giao diện trước. Khi API thật sẵn sàng:
    // 1) kiểm tra res.user / res.token trả về từ server
    // 2) lưu token thực sự (vd. cookie httpOnly hoặc localStorage có hạn dùng)
    // 3) điều hướng theo vai trò (user/staff/admin) nếu cần
    await authService.login({ ...form })
    localStorage.setItem('dreamleague_user', JSON.stringify({ identifier: form.identifier }))
    router.push('/')
  } catch (err) {
    errorMessage.value = 'Email/số điện thoại hoặc mật khẩu không đúng. Vui lòng thử lại.'
  } finally {
    submitting.value = false
  }
}

function socialNotice() {
  infoMessage.value = 'Đăng nhập bằng mạng xã hội sẽ sớm được hỗ trợ.'
  setTimeout(() => {
    infoMessage.value = ''
  }, 3500)
}
</script>