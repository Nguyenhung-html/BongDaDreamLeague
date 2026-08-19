<template>
  <div class="auth-page">
    <!-- Panel trái: thương hiệu & hình ảnh sân bóng -->
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
        <span class="eyebrow">Bảo mật tài khoản đa kênh</span>
        <h1 class="auth-visual__title">Khôi phục tài khoản,<br />tiếp tục <span>đam mê</span></h1>
        <p class="auth-visual__desc">
          Linh hoạt nhận mã xác thực OTP qua Email hoặc SMS trực tiếp vào số điện thoại để lấy lại quyền truy cập tài khoản DreamLeague của bạn an toàn nhất.
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

    <!-- Panel phải: Form các bước quên mật khẩu -->
    <section class="auth-form-panel">
      <div class="auth-form-card">
        <!-- Nút quay lại đăng nhập -->
        <router-link to="/dang-nhap" class="auth-back">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
            <path d="M19 12H5M5 12L11 6M5 12L11 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
          </svg>
          Quay lại Đăng nhập
        </router-link>

        <!-- Tiêu đề từng bước -->
        <div class="auth-form-card__head">
          <h1>{{ tieuDeTheoBuoc }}</h1>
          <p>{{ moTaTheoBuoc }}</p>
        </div>

        <!-- Thanh chỉ báo tiến trình bước -->
        <div v-if="currentStep < 4" class="step-indicator">
          <div class="step-item" :class="{ 'step-item--active': currentStep >= 1, 'step-item--done': currentStep > 1 }">
            <span class="step-num">1</span>
            <span class="step-label">{{ selectedMethod === 'email' ? 'Nhập Email' : 'Nhập Số ĐT' }}</span>
          </div>
          <div class="step-line" :class="{ 'step-line--active': currentStep >= 2 }"></div>
          <div class="step-item" :class="{ 'step-item--active': currentStep >= 2, 'step-item--done': currentStep > 2 }">
            <span class="step-num">2</span>
            <span class="step-label">Nhập OTP</span>
          </div>
          <div class="step-line" :class="{ 'step-line--active': currentStep >= 3 }"></div>
          <div class="step-item" :class="{ 'step-item--active': currentStep >= 3 }">
            <span class="step-num">3</span>
            <span class="step-label">Đổi mật khẩu</span>
          </div>
        </div>

        <!-- Thông báo lỗi / thành công -->
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

        <!-- ================= BƯỚC 1: CHỌN PHƯƠNG THỨC & NHẬP THÔNG TIN ================= -->
        <div v-if="currentStep === 1">
          <!-- Bộ chuyển đổi 2 hướng: Email hoặc SMS -->
          <div class="method-selector">
            <label class="method-selector__label">Chọn hình thức nhận mã OTP:</label>
            <div class="method-tabs">
              <button
                type="button"
                class="method-tab"
                :class="{ 'method-tab--active': selectedMethod === 'email' }"
                @click="doiPhuongThuc('email')"
              >
                <span class="method-tab__icon">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                    <path d="M3 6.5C3 5.67 3.67 5 4.5 5H19.5C20.33 5 21 5.67 21 6.5V17.5C21 18.33 20.33 19 19.5 19H4.5C3.67 19 3 18.33 3 17.5V6.5Z" stroke="currentColor" stroke-width="1.8" />
                    <path d="M4 6.5L12 13L20 6.5" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" />
                  </svg>
                </span>
                <span class="method-tab__text">
                  <strong>Qua Email</strong>
                  <small>Nhận OTP vào hộp thư</small>
                </span>
              </button>

              <button
                type="button"
                class="method-tab"
                :class="{ 'method-tab--active': selectedMethod === 'sms' }"
                @click="doiPhuongThuc('sms')"
              >
                <span class="method-tab__icon">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                    <rect x="5" y="2" width="14" height="20" rx="3" stroke="currentColor" stroke-width="1.8" />
                    <circle cx="12" cy="18" r="1" fill="currentColor" />
                    <line x1="9" y1="5" x2="15" y2="5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
                  </svg>
                </span>
                <span class="method-tab__text">
                  <strong>Qua Tin nhắn SMS</strong>
                  <small>Nhận OTP vào số điện thoại</small>
                </span>
              </button>
            </div>
          </div>

          <!-- Form theo Email -->
          <form v-if="selectedMethod === 'email'" class="auth-form" novalidate @submit.prevent="onGuiMaOtp">
            <div class="field">
              <label class="field__label" for="email">Địa chỉ Email đã đăng ký</label>
              <div class="field__input-wrap" :class="{ 'field__input-wrap--error': errors.email }">
                <span class="field__icon">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                    <path d="M3 6.5C3 5.67 3.67 5 4.5 5H19.5C20.33 5 21 5.67 21 6.5V17.5C21 18.33 20.33 19 19.5 19H4.5C3.67 19 3 18.33 3 17.5V6.5Z" stroke="currentColor" stroke-width="1.6" />
                    <path d="M4 6.5L12 13L20 6.5" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
                  </svg>
                </span>
                <input
                  id="email"
                  v-model.trim="form.email"
                  type="email"
                  placeholder="example@gmail.com"
                  autocomplete="email"
                  :disabled="submitting"
                  @input="errors.email = ''"
                />
              </div>
              <p v-if="errors.email" class="field__error">{{ errors.email }}</p>
            </div>

            <button type="submit" class="btn btn-primary btn-block" :disabled="submitting">
              <span v-if="submitting" class="spinner"></span>
              {{ submitting ? 'Đang gửi mã...' : 'Gửi mã xác thực qua Email' }}
            </button>
          </form>

          <!-- Form theo SMS -->
          <form v-else class="auth-form" novalidate @submit.prevent="onGuiMaOtp">
            <div class="field">
              <label class="field__label" for="soDienThoai">Số điện thoại đã đăng ký</label>
              <div class="field__input-wrap" :class="{ 'field__input-wrap--error': errors.soDienThoai }">
                <span class="field__icon">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                    <path d="M22 16.92V19.92C22.0011 20.1985 21.9441 20.4742 21.8325 20.7294C21.7209 20.9845 21.5573 21.2137 21.3521 21.4019C21.1468 21.5901 20.9046 21.7331 20.6407 21.8219C20.3769 21.9107 20.0974 21.9432 19.82 21.917C16.7428 21.5826 13.787 20.5315 11.19 18.85C8.77382 17.3147 6.72533 15.2662 5.19 12.85C3.50209 10.2413 2.45071 7.27116 2.12 4.18C2.09385 3.90367 2.12607 3.62524 2.21453 3.36235C2.30299 3.09947 2.4457 2.85799 2.63334 2.65345C2.82098 2.44891 3.04944 2.28589 3.30386 2.17502C3.55828 2.06415 3.83311 2.00787 4.11 2.01H7.11C7.5953 1.99522 8.06579 2.16708 8.42876 2.49168C8.79174 2.81628 9.02047 3.26941 9.07 3.76C9.16278 4.67814 9.38792 5.57758 9.74 6.43C9.88414 6.77668 9.91979 7.15833 9.84279 7.52554C9.7658 7.89275 9.57946 8.22915 9.306 8.491L8.03 9.767C9.44473 12.2539 11.4861 14.2953 13.973 15.71L15.249 14.434C15.5108 14.1605 15.8473 13.9742 16.2145 13.8972C16.5817 13.8202 16.9633 13.8559 17.31 14C18.1624 14.3521 19.0619 14.5772 19.98 14.67C20.4754 14.7198 20.9324 14.9525 21.2583 15.3204C21.5843 15.6883 21.7547 16.1643 21.734 16.65L22 16.92Z" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" />
                  </svg>
                </span>
                <input
                  id="soDienThoai"
                  v-model.trim="form.soDienThoai"
                  type="tel"
                  placeholder="0987654321"
                  autocomplete="tel"
                  :disabled="submitting"
                  @input="errors.soDienThoai = ''"
                />
              </div>
              <p v-if="errors.soDienThoai" class="field__error">{{ errors.soDienThoai }}</p>
            </div>

            <button type="submit" class="btn btn-primary btn-block" :disabled="submitting">
              <span v-if="submitting" class="spinner"></span>
              {{ submitting ? 'Đang gửi mã...' : 'Gửi mã xác thực qua SMS' }}
            </button>
          </form>
        </div>

        <!-- ================= BƯỚC 2: NHẬP MÃ OTP ================= -->
        <form v-else-if="currentStep === 2" class="auth-form" novalidate @submit.prevent="onXacThucOtp">
          <div class="otp-recipient-box">
            <svg v-if="selectedMethod === 'email'" width="20" height="20" viewBox="0 0 24 24" fill="none">
              <path d="M3 8L10.89 13.26C11.56 13.71 12.44 13.71 13.11 13.26L21 8M5 19H19C20.1 19 21 18.1 21 17V7C21 5.9 20.1 5 19 5H5C3.9 5 3 5.9 3 7V17C3 18.1 3.9 19 5 19Z" stroke="var(--green-700)" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none">
              <rect x="5" y="2" width="14" height="20" rx="3" stroke="var(--green-700)" stroke-width="1.8" />
              <circle cx="12" cy="18" r="1" fill="var(--green-700)" />
              <line x1="9" y1="5" x2="15" y2="5" stroke="var(--green-700)" stroke-width="1.5" stroke-linecap="round" />
            </svg>
            <div>
              <span>Mã xác thực đã gửi tới {{ selectedMethod === 'email' ? 'Email:' : 'Số điện thoại:' }}</span>
              <strong>{{ selectedMethod === 'email' ? form.email : form.soDienThoai }}</strong>
            </div>
          </div>

          <div class="field">
            <label class="field__label" for="otp">Mã xác thực OTP (6 chữ số)</label>
            <div class="field__input-wrap field__input-wrap--otp" :class="{ 'field__input-wrap--error': errors.otp }">
              <input
                id="otp"
                v-model.trim="form.otp"
                type="text"
                maxlength="6"
                placeholder="123456"
                autocomplete="one-time-code"
                :disabled="submitting"
                @input="handleOtpInput"
              />
            </div>
            <p v-if="errors.otp" class="field__error">{{ errors.otp }}</p>
          </div>

          <!-- Nút đếm ngược gửi lại mã -->
          <div class="resend-row">
            <span v-if="countdown > 0" class="countdown-text">
              Gửi lại mã sau <strong>{{ countdown }}s</strong>
            </span>
            <button
              v-else
              type="button"
              class="btn-resend"
              :disabled="submitting"
              @click="onGuiLaiOtp"
            >
              🔄 Gửi lại mã xác thực
            </button>
            <button type="button" class="btn-change-email" @click="quayLaiBuoc1">
              Đổi phương thức / số khác
            </button>
          </div>

          <button type="submit" class="btn btn-primary btn-block" :disabled="submitting">
            <span v-if="submitting" class="spinner"></span>
            {{ submitting ? 'Đang kiểm tra...' : 'Xác thực mã OTP' }}
          </button>
        </form>

        <!-- ================= BƯỚC 3: ĐẶT LẠI MẬT KHẨU ================= -->
        <form v-else-if="currentStep === 3" class="auth-form" novalidate @submit.prevent="onDatLaiMatKhau">
          <div class="field">
            <label class="field__label" for="newPassword">Mật khẩu mới</label>
            <div class="field__input-wrap" :class="{ 'field__input-wrap--error': errors.newPassword }">
              <span class="field__icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                  <rect x="5" y="11" width="14" height="9" rx="2" stroke="currentColor" stroke-width="1.6" />
                  <path d="M8 11V8a4 4 0 0 1 8 0v3" stroke="currentColor" stroke-width="1.6" />
                </svg>
              </span>
              <input
                id="newPassword"
                v-model="form.newPassword"
                :type="showNewPassword ? 'text' : 'password'"
                placeholder="Tối thiểu 6 ký tự"
                autocomplete="new-password"
                :disabled="submitting"
                @input="errors.newPassword = ''"
              />
              <button
                type="button"
                class="field__toggle"
                @click="showNewPassword = !showNewPassword"
              >
                <svg v-if="showNewPassword" width="18" height="18" viewBox="0 0 24 24" fill="none">
                  <path d="M2 12C2 12 5.5 5.5 12 5.5S22 12 22 12 18.5 18.5 12 18.5 2 12 2 12Z" stroke="currentColor" stroke-width="1.6" />
                  <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="1.6" />
                </svg>
                <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none">
                  <path d="M3 3L21 21" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
                  <path d="M10.6 5.7C11.05 5.6 11.52 5.5 12 5.5C18.5 5.5 22 12 22 12C22 12 21.18 13.5 19.6 15.1M6.4 6.9C4 8.5 2 12 2 12C2 12 5.5 18.5 12 18.5C13.7 18.5 15.16 18.07 16.4 17.4" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
                </svg>
              </button>
            </div>
            <p v-if="errors.newPassword" class="field__error">{{ errors.newPassword }}</p>
          </div>

          <div class="field">
            <label class="field__label" for="confirmPassword">Xác nhận mật khẩu mới</label>
            <div class="field__input-wrap" :class="{ 'field__input-wrap--error': errors.confirmPassword }">
              <span class="field__icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                  <rect x="5" y="11" width="14" height="9" rx="2" stroke="currentColor" stroke-width="1.6" />
                  <path d="M8 11V8a4 4 0 0 1 8 0v3" stroke="currentColor" stroke-width="1.6" />
                </svg>
              </span>
              <input
                id="confirmPassword"
                v-model="form.confirmPassword"
                :type="showConfirmPassword ? 'text' : 'password'"
                placeholder="Nhập lại mật khẩu mới"
                autocomplete="new-password"
                :disabled="submitting"
                @input="errors.confirmPassword = ''"
              />
              <button
                type="button"
                class="field__toggle"
                @click="showConfirmPassword = !showConfirmPassword"
              >
                <svg v-if="showConfirmPassword" width="18" height="18" viewBox="0 0 24 24" fill="none">
                  <path d="M2 12C2 12 5.5 5.5 12 5.5S22 12 22 12 18.5 18.5 12 18.5 2 12 2 12Z" stroke="currentColor" stroke-width="1.6" />
                  <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="1.6" />
                </svg>
                <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none">
                  <path d="M3 3L21 21" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
                  <path d="M10.6 5.7C11.05 5.6 11.52 5.5 12 5.5C18.5 5.5 22 12 22 12C22 12 21.18 13.5 19.6 15.1M6.4 6.9C4 8.5 2 12 2 12C2 12 5.5 18.5 12 18.5C13.7 18.5 15.16 18.07 16.4 17.4" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
                </svg>
              </button>
            </div>
            <p v-if="errors.confirmPassword" class="field__error">{{ errors.confirmPassword }}</p>
          </div>

          <button type="submit" class="btn btn-primary btn-block" :disabled="submitting">
            <span v-if="submitting" class="spinner"></span>
            {{ submitting ? 'Đang cập nhật...' : 'Đổi mật khẩu & Hoàn tất' }}
          </button>
        </form>

        <!-- ================= BƯỚC 4: THÀNH CÔNG ================= -->
        <div v-else-if="currentStep === 4" class="success-screen">
          <div class="success-icon-wrap">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none">
              <circle cx="12" cy="12" r="10" fill="var(--green-500)" />
              <path d="M8 12.5L10.5 15L16 9.5" stroke="white" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" />
            </svg>
          </div>
          <h2>Cài lại mật khẩu thành công!</h2>
          <p>
            Mật khẩu mới của bạn đã được cập nhật thành công. Bạn có thể đăng nhập lại ngay bây giờ.
          </p>
          <p v-if="autoRedirectTimer > 0" class="auto-redirect-note">
            Tự động chuyển về trang Đăng nhập sau <strong>{{ autoRedirectTimer }}s</strong>...
          </p>

          <button type="button" class="btn btn-primary btn-block" @click="veTrangDangNhap">
            Đăng nhập ngay
          </button>
        </div>

        <p v-if="currentStep < 4" class="auth-switch">
          Đã nhớ mật khẩu? <router-link to="/dang-nhap">Đăng nhập ngay</router-link>
        </p>
      </div>
    </section>
  </div>
</template>

<script setup>
import { reactive, ref, computed, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import authService from '../../services/authService'

const router = useRouter()

// Các bước: 1 = Chọn phương thức & Nhập Email/SĐT, 2 = Nhập OTP, 3 = Đổi Mật Khẩu, 4 = Thành Công
const currentStep = ref(1)

// Phương thức khôi phục: 'email' hoặc 'sms'
const selectedMethod = ref('email')

const form = reactive({
  email: '',
  soDienThoai: '',
  otp: '',
  newPassword: '',
  confirmPassword: ''
})

const errors = reactive({
  email: '',
  soDienThoai: '',
  otp: '',
  newPassword: '',
  confirmPassword: ''
})

const showNewPassword = ref(false)
const showConfirmPassword = ref(false)
const submitting = ref(false)
const errorMessage = ref('')
const infoMessage = ref('')

const countdown = ref(0)
let timerInterval = null

const autoRedirectTimer = ref(3)
let redirectInterval = null

const features = [
  'Xác thực OTP tức thì qua Email & SMS',
  'Mã hóa mật khẩu bảo mật chuẩn BCrypt',
  'Hỗ trợ 24/7 từ ban quản trị DreamLeague'
]

const tieuDeTheoBuoc = computed(() => {
  if (currentStep.value === 1) return 'Quên mật khẩu'
  if (currentStep.value === 2) return 'Xác thực OTP'
  if (currentStep.value === 3) return 'Cài lại mật khẩu'
  return 'Thành công'
})

const moTaTheoBuoc = computed(() => {
  if (currentStep.value === 1) {
    return selectedMethod.value === 'email'
      ? 'Nhập email đã đăng ký tài khoản để nhận mã xác thực OTP.'
      : 'Nhập số điện thoại đã đăng ký để nhận mã xác thực OTP qua tin nhắn SMS.'
  }
  if (currentStep.value === 2) {
    return selectedMethod.value === 'email'
      ? 'Nhập mã 6 chữ số đã được gửi tới hòm thư của bạn.'
      : 'Nhập mã 6 chữ số đã được gửi tới tin nhắn SMS trên điện thoại của bạn.'
  }
  if (currentStep.value === 3) return 'Thiết lập mật khẩu mới có ít nhất 6 ký tự.'
  return 'Mật khẩu tài khoản của bạn đã được thay đổi an toàn.'
})

function doiPhuongThuc(method) {
  selectedMethod.value = method
  errorMessage.value = ''
  infoMessage.value = ''
  errors.email = ''
  errors.soDienThoai = ''
}

function batDauDemNguoc(giay = 60) {
  countdown.value = giay
  clearInterval(timerInterval)
  timerInterval = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(timerInterval)
    }
  }, 1000)
}

function handleOtpInput(e) {
  // Chỉ cho phép nhập ký tự số
  form.otp = e.target.value.replace(/\D/g, '')
  errors.otp = ''
}

// BƯỚC 1: GỬI MÃ OTP VỀ EMAIL HOẶC SMS
async function onGuiMaOtp() {
  errorMessage.value = ''
  infoMessage.value = ''
  errors.email = ''
  errors.soDienThoai = ''

  if (selectedMethod.value === 'email') {
    if (!form.email) {
      errors.email = 'Vui lòng nhập địa chỉ email của bạn'
      return
    }
    const isEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)
    if (!isEmail) {
      errors.email = 'Địa chỉ email không đúng định dạng'
      return
    }
  } else {
    if (!form.soDienThoai) {
      errors.soDienThoai = 'Vui lòng nhập số điện thoại của bạn'
      return
    }
    const isPhone = /^(0|\+84)[0-9]{9}$/.test(form.soDienThoai.replace(/\s+/g, ''))
    if (!isPhone) {
      errors.soDienThoai = 'Số điện thoại không đúng định dạng (10 số, VD: 0987654321)'
      return
    }
  }

  submitting.value = true
  try {
    if (selectedMethod.value === 'email') {
      const res = await authService.guiMaOtp(form.email)
      infoMessage.value = res.message || 'Mã xác thực đã được gửi về email của bạn!'
    } else {
      const res = await authService.guiMaOtpSms(form.soDienThoai.replace(/\s+/g, ''))
      infoMessage.value = res.message || 'Mã xác thực đã được gửi về số điện thoại của bạn!'
    }
    currentStep.value = 2
    batDauDemNguoc(60)
  } catch (err) {
    errorMessage.value = err.message || 'Không thể gửi mã OTP. Vui lòng thử lại.'
  } finally {
    submitting.value = false
  }
}

// GỬI LẠI MÃ OTP
async function onGuiLaiOtp() {
  if (countdown.value > 0) return
  errorMessage.value = ''
  infoMessage.value = ''

  submitting.value = true
  try {
    if (selectedMethod.value === 'email') {
      const res = await authService.guiMaOtp(form.email)
      infoMessage.value = res.message || 'Đã gửi lại mã xác thực mới vào email của bạn!'
    } else {
      const res = await authService.guiMaOtpSms(form.soDienThoai.replace(/\s+/g, ''))
      infoMessage.value = res.message || 'Đã gửi lại mã xác thực mới vào số điện thoại của bạn!'
    }
    batDauDemNguoc(60)
  } catch (err) {
    errorMessage.value = err.message || 'Gửi lại mã thất bại. Vui lòng thử lại.'
  } finally {
    submitting.value = false
  }
}

function quayLaiBuoc1() {
  currentStep.value = 1
  errorMessage.value = ''
  infoMessage.value = ''
  form.otp = ''
}

// BƯỚC 2: XÁC THỰC MÃ OTP
async function onXacThucOtp() {
  errorMessage.value = ''
  infoMessage.value = ''
  errors.otp = ''

  if (!form.otp) {
    errors.otp = 'Vui lòng nhập mã xác thực 6 số'
    return
  }
  if (form.otp.length !== 6) {
    errors.otp = 'Mã xác thực phải gồm đúng 6 chữ số'
    return
  }

  submitting.value = true
  try {
    if (selectedMethod.value === 'email') {
      const res = await authService.xacThucMaOtp(form.email, form.otp)
      infoMessage.value = res.message || 'Xác thực mã OTP thành công!'
    } else {
      const res = await authService.xacThucMaOtpSms(form.soDienThoai.replace(/\s+/g, ''), form.otp)
      infoMessage.value = res.message || 'Xác thực mã OTP thành công!'
    }
    currentStep.value = 3
  } catch (err) {
    errorMessage.value = err.message || 'Mã xác thực không chính xác hoặc đã hết hạn.'
  } finally {
    submitting.value = false
  }
}

// BƯỚC 3: ĐỔI MẬT KHẨU MỚI
async function onDatLaiMatKhau() {
  errorMessage.value = ''
  infoMessage.value = ''
  errors.newPassword = ''
  errors.confirmPassword = ''

  if (!form.newPassword) {
    errors.newPassword = 'Vui lòng nhập mật khẩu mới'
    return
  }
  if (form.newPassword.length < 6) {
    errors.newPassword = 'Mật khẩu mới phải có ít nhất 6 ký tự'
    return
  }
  if (form.newPassword !== form.confirmPassword) {
    errors.confirmPassword = 'Mật khẩu xác nhận không khớp'
    return
  }

  submitting.value = true
  try {
    if (selectedMethod.value === 'email') {
      await authService.datLaiMatKhau(form.email, form.otp, form.newPassword)
    } else {
      await authService.datLaiMatKhauSms(form.soDienThoai.replace(/\s+/g, ''), form.otp, form.newPassword)
    }
    currentStep.value = 4

    // Đếm ngược 3 giây tự động chuyển về trang Đăng nhập
    autoRedirectTimer.value = 3
    clearInterval(redirectInterval)
    redirectInterval = setInterval(() => {
      autoRedirectTimer.value--
      if (autoRedirectTimer.value <= 0) {
        clearInterval(redirectInterval)
        veTrangDangNhap()
      }
    }, 1000)
  } catch (err) {
    errorMessage.value = err.message || 'Đặt lại mật khẩu thất bại. Vui lòng thử lại.'
  } finally {
    submitting.value = false
  }
}

function veTrangDangNhap() {
  clearInterval(redirectInterval)
  router.push('/dang-nhap')
}

onUnmounted(() => {
  clearInterval(timerInterval)
  clearInterval(redirectInterval)
})
</script>

<style scoped>
/* ===== BỐ CỤC CHÍNH ===== */
.auth-page {
  display: flex;
  min-height: 100vh;
  width: 100%;
  background-color: var(--white);
  font-family: var(--font-display);
  color: var(--navy-900);
}

/* ===== PANEL TRÁI ===== */
.auth-visual {
  flex: 1;
  position: relative;
  background: linear-gradient(135deg, var(--navy-900) 0%, var(--navy-700) 100%);
  padding: 60px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  overflow: hidden;
}

.auth-visual__glow {
  position: absolute;
  border-radius: 50%;
  pointer-events: none;
}
.auth-visual__glow--1 {
  width: 400px;
  height: 400px;
  background: var(--green-600);
  filter: blur(140px);
  opacity: 0.15;
  top: -100px;
  left: -100px;
}
.auth-visual__glow--2 {
  width: 350px;
  height: 350px;
  background: var(--green-500);
  filter: blur(120px);
  opacity: 0.12;
  bottom: -80px;
  right: -80px;
}

.auth-visual__pitch {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  opacity: 0.08;
  pointer-events: none;
}

.auth-visual__top {
  position: relative;
  z-index: 2;
}

.auth-brand {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  font-size: 22px;
  color: var(--white);
  font-weight: 500;
}
.auth-brand span strong {
  color: var(--green-500);
  font-weight: 800;
}

.auth-visual__body {
  position: relative;
  z-index: 2;
  margin: auto 0;
}

.auth-visual__title {
  font-size: clamp(32px, 3.5vw, 44px);
  font-weight: 700;
  color: var(--white);
  line-height: 1.25;
  margin-top: 16px;
}
.auth-visual__title span {
  color: var(--green-500);
}

.auth-visual__desc {
  margin-top: 16px;
  font-size: 15.5px;
  color: var(--gray-300);
  line-height: 1.65;
  max-width: 480px;
}

.auth-features {
  margin-top: 36px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.auth-feature {
  display: flex;
  align-items: center;
  gap: 12px;
  color: var(--white);
  font-size: 14.5px;
  font-weight: 500;
}

.auth-feature__icon {
  width: 24px;
  height: 24px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.auth-visual__stats {
  position: relative;
  z-index: 2;
  display: flex;
  gap: 48px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  padding-top: 32px;
}

.auth-stat__num {
  font-family: var(--font-score);
  font-size: 32px;
  font-weight: 700;
  color: var(--white);
  line-height: 1;
}
.auth-stat__num span {
  color: var(--green-500);
}

.auth-stat__label {
  font-size: 13px;
  color: var(--gray-500);
  margin-top: 6px;
}

/* ===== PANEL PHẢI: FORM ===== */
.auth-form-panel {
  flex: 1;
  background-color: var(--gray-50);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 24px;
}

.auth-form-card {
  width: 100%;
  max-width: 460px;
  background-color: var(--white);
  padding: 40px;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);
}

.auth-back {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: var(--gray-600);
  margin-bottom: 24px;
  transition: color 0.15s ease;
}
.auth-back:hover {
  color: var(--green-600);
}

.auth-form-card__head h1 {
  font-size: 28px;
  font-weight: 700;
  color: var(--navy-900);
}
.auth-form-card__head p {
  font-size: 14.5px;
  color: var(--gray-600);
  line-height: 1.5;
  margin-top: 6px;
  margin-bottom: 20px;
}

/* ===== STEP INDICATOR ===== */
.step-indicator {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
  padding: 12px 14px;
  background-color: var(--gray-50);
  border-radius: var(--radius-sm);
  border: 1px solid var(--gray-100);
}

.step-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--gray-500);
  font-size: 12.5px;
  font-weight: 600;
}
.step-item--active {
  color: var(--navy-900);
}
.step-item--done {
  color: var(--green-600);
}

.step-num {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background-color: var(--gray-300);
  color: var(--white);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  font-weight: 700;
}
.step-item--active .step-num {
  background-color: var(--navy-900);
}
.step-item--done .step-num {
  background-color: var(--green-600);
}

.step-line {
  flex: 1;
  height: 2px;
  background-color: var(--gray-300);
  margin: 0 10px;
  border-radius: 2px;
}
.step-line--active {
  background-color: var(--green-600);
}

/* ===== METHOD SELECTOR (EMAIL vs SMS) ===== */
.method-selector {
  margin-bottom: 22px;
}
.method-selector__label {
  display: block;
  font-size: 13.5px;
  font-weight: 600;
  color: var(--navy-900);
  margin-bottom: 10px;
}
.method-tabs {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}
.method-tab {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
  background-color: var(--white);
  border: 2px solid var(--gray-100);
  border-radius: var(--radius-md, 12px);
  cursor: pointer;
  text-align: left;
  transition: all 0.2s ease;
}
.method-tab:hover {
  border-color: var(--green-200, #bbf7d0);
  background-color: #f8fafc;
}
.method-tab--active {
  border-color: var(--green-600) !important;
  background-color: var(--green-50) !important;
}
.method-tab__icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background-color: var(--gray-100);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--gray-600);
  flex-shrink: 0;
  transition: all 0.2s ease;
}
.method-tab--active .method-tab__icon {
  background-color: var(--green-600);
  color: var(--white);
}
.method-tab__text {
  display: flex;
  flex-direction: column;
}
.method-tab__text strong {
  font-size: 13.5px;
  color: var(--navy-900);
  line-height: 1.2;
}
.method-tab__text small {
  font-size: 11px;
  color: var(--gray-500);
  margin-top: 2px;
}

/* ===== HỘP THÔNG BÁO NGƯỜI NHẬN OTP ===== */
.otp-recipient-box {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background-color: var(--green-50);
  border: 1px solid var(--green-100);
  border-radius: var(--radius-sm);
  margin-bottom: 20px;
  font-size: 13.5px;
  color: var(--green-700);
}
.otp-recipient-box strong {
  display: block;
  color: var(--navy-900);
  font-size: 14px;
  word-break: break-all;
}

/* ===== ALERT ===== */
.auth-alert {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 14px 16px;
  border-radius: var(--radius-sm);
  margin-bottom: 20px;
  font-size: 14px;
  line-height: 1.4;
}
.auth-alert--error {
  background-color: #fdf2f2;
  border: 1px solid #fbd5d5;
  color: #b13030;
}
.auth-alert--success {
  background-color: var(--green-50);
  border: 1px solid var(--green-100);
  color: var(--green-700);
}
.auth-alert svg {
  flex-shrink: 0;
  margin-top: 2px;
}

/* ===== FORM FIELDS ===== */
.auth-form {
  display: flex;
  flex-direction: column;
}

.field {
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
}

.field__label {
  font-size: 14px;
  font-weight: 600;
  color: var(--navy-900);
  margin-bottom: 8px;
}

.field__input-wrap {
  display: flex;
  align-items: center;
  background-color: var(--white);
  border: 1.5px solid var(--gray-100);
  border-radius: var(--radius-sm);
  padding: 0 14px;
  height: 48px;
  transition: border-color 0.15s ease, box-shadow 0.15s ease;
}
.field__input-wrap:focus-within {
  border-color: var(--green-600);
  box-shadow: 0 0 0 3.5px var(--green-50);
}

.field__input-wrap--error {
  border-color: #ef4444 !important;
  background-color: #fffafb;
}
.field__input-wrap--error:focus-within {
  box-shadow: 0 0 0 3.5px #fef2f2;
}

.field__input-wrap--otp input {
  font-family: 'Courier New', Courier, monospace;
  font-size: 26px !important;
  font-weight: 800;
  letter-spacing: 8px;
  text-align: center;
}

.field__icon {
  color: var(--gray-500);
  display: flex;
  align-items: center;
  justify-content: center;
}

.field__input-wrap input {
  flex: 1;
  border: none;
  background: transparent;
  padding: 0 12px;
  font-family: var(--font-display);
  font-size: 14.5px;
  color: var(--navy-900);
  height: 100%;
  outline: none;
}
.field__input-wrap input::placeholder {
  color: var(--gray-500);
}

.field__toggle {
  background: none;
  border: none;
  color: var(--gray-500);
  padding: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.15s ease;
}
.field__toggle:hover {
  color: var(--navy-900);
}

.field__error {
  color: #ef4444;
  font-size: 12.5px;
  margin-top: 6px;
  font-weight: 500;
}

/* ===== RESEND & OTP ROW ===== */
.resend-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
  font-size: 13.5px;
}

.countdown-text {
  color: var(--gray-600);
}
.countdown-text strong {
  color: var(--green-700);
}

.btn-resend {
  background: none;
  border: none;
  color: var(--green-600);
  font-weight: 600;
  font-size: 13.5px;
  padding: 0;
  transition: color 0.15s;
}
.btn-resend:hover:not(:disabled) {
  color: var(--green-700);
  text-decoration: underline;
}

.btn-change-email {
  background: none;
  border: none;
  color: var(--gray-500);
  font-size: 13px;
  padding: 0;
  transition: color 0.15s;
}
.btn-change-email:hover {
  color: var(--navy-900);
  text-decoration: underline;
}

/* ===== BUTTONS & SPINNER ===== */
.btn-block {
  width: 100%;
  height: 48px;
  border: none;
  border-radius: 999px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}
.spinner {
  width: 18px;
  height: 18px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: var(--white);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-right: 8px;
}
@keyframes spin {
  to { transform: rotate(360deg); }
}

/* ===== SUCCESS SCREEN ===== */
.success-screen {
  text-align: center;
  padding: 20px 0;
}
.success-icon-wrap {
  margin-bottom: 16px;
  display: flex;
  justify-content: center;
}
.success-screen h2 {
  font-size: 22px;
  font-weight: 700;
  color: var(--navy-900);
  margin-bottom: 10px;
}
.success-screen p {
  font-size: 14.5px;
  color: var(--gray-600);
  line-height: 1.6;
  margin-bottom: 20px;
}
.auto-redirect-note {
  font-size: 13px !important;
  color: var(--green-700) !important;
}

/* ===== SWITCH LINK ===== */
.auth-switch {
  text-align: center;
  font-size: 14.5px;
  color: var(--gray-600);
  margin-top: 24px;
}
.auth-switch a {
  color: var(--green-600);
  font-weight: 700;
  transition: color 0.15s;
}
.auth-switch a:hover {
  color: var(--green-700);
  text-decoration: underline;
}

/* ===== RESPONSIVE ===== */
@media (max-width: 1024px) {
  .auth-visual {
    display: none;
  }
  .auth-form-panel {
    background-color: var(--white);
    padding: 24px 16px;
  }
  .auth-form-card {
    box-shadow: none;
    padding: 10px;
  }
  .step-label {
    display: none;
  }
  .method-tabs {
    grid-template-columns: 1fr;
  }
}
</style>