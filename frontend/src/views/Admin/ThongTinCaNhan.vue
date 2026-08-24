<template>
  <div class="page">

    <!-- ===== NỀN AMBIENT: đèn pha + hạt sáng + hạt rơi + đạo cụ bay bay ===== -->
    <div class="page__ambient" aria-hidden="true">
      <div class="floodlight floodlight--l"></div>
      <div class="floodlight floodlight--r"></div>
      <div class="motes"><span v-for="n in 12" :key="'m'+n" class="mote" :style="{ '--i': n }"></span></div>
      <div class="falling"><span v-for="n in 16" :key="'f'+n" class="falling__item" :style="{ '--i': n }"></span></div>
      <div class="field-props">
        <div class="prop prop--ball"><svg viewBox="0 0 40 40"><circle cx="20" cy="20" r="18" fill="var(--chalk-050)" stroke="var(--night-950)" stroke-width="1.4"/><polygon points="20,10 26,14.5 24,21.5 16,21.5 14,14.5" fill="var(--night-950)"/></svg></div>
        <div class="prop prop--whistle"><svg viewBox="0 0 48 32" fill="none"><path d="M14 16a10 10 0 1 0 20 0 10 10 0 0 0-20 0Z" stroke="var(--lime-300)" stroke-width="1.6"/><circle cx="24" cy="16" r="3.2" fill="var(--lime-300)"/></svg></div>
        <div class="prop prop--flag"><svg viewBox="0 0 30 46" fill="none"><path d="M4 44V4" stroke="var(--lime-300)" stroke-width="1.8" stroke-linecap="round"/><path d="M4 5l20 6-20 6V5Z" fill="var(--lime-300)" opacity=".85"/></svg></div>
      </div>
    </div>

    <!-- Header -->
    <header class="page__header reveal">
      <h1 class="page__title">Thông tin cá nhân Quản trị viên</h1>
      <p class="page__desc">Quản lý hồ sơ cá nhân, thông tin liên hệ và cài đặt bảo mật mật khẩu của <span class="brand">Dream League</span></p>
    </header>

    <!-- Khung tải dữ liệu ban đầu -->
    <div v-if="isLoading" class="state reveal">
      <div class="spinner"></div>
      <p>Đang tải thông tin tài khoản...</p>
    </div>

    <div v-else class="profile-container">

      <!-- BÊN TRÁI: AVATAR & THÔNG TIN CHUNG -->
      <div class="sidebar-card reveal" style="--d:0s">
        <div class="avatar-wrapper">
          <img v-if="avatarPreview" :src="avatarPreview" alt="Avatar" class="avatar-img" />
          <div v-else class="avatar-circle">{{ userInitial }}</div>
          <label class="btn-upload-avatar" title="Tải ảnh đại diện">
            <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"/>
              <circle cx="12" cy="13" r="4"/>
            </svg>
            <span>Đổi ảnh</span>
            <input type="file" accept="image/*" @change="handleAvatarUpload" style="display: none;" />
          </label>
        </div>

        <h2 class="user-name">{{ form.hoTen || 'Chưa đặt tên' }}</h2>
        <span class="badge badge--role">Quản trị viên (Admin)</span>

        <div class="user-meta">
          <div class="meta-row">
            <span class="meta-label">Mã quản trị:</span>
            <span class="meta-val font-mono">#{{ shortId }}</span>
          </div>
          <div class="meta-row">
            <span class="meta-label">Ngày tham gia:</span>
            <span class="meta-val">{{ formatDate(rawUser.ngayTao) }}</span>
          </div>
          <div class="meta-row">
            <span class="meta-label">Trạng thái:</span>
            <span class="badge badge--active"><span class="badge__dot"></span>Đang hoạt động</span>
          </div>
        </div>
      </div>

      <!-- BÊN PHẢI: FORM CHỈNH SỬA & ĐỔI MẬT KHẨU -->
      <div class="profile-content">

        <!-- Form thông tin cá nhân -->
        <div class="profile-section reveal" style="--d:.08s">
          <div class="section-header">
            <h3 class="section-title">Hồ sơ cá nhân</h3>
            <span class="badge badge--info">Thông tin chính</span>
          </div>

          <div v-if="profileAlert.text" :class="['alert-banner', profileAlert.type === 'error' ? 'alert-banner--error' : 'alert-banner--success']">
            <svg v-if="profileAlert.type === 'error'" width="18" height="18" viewBox="0 0 24 24" fill="none">
              <circle cx="12" cy="12" r="9" stroke="currentColor" stroke-width="1.6" />
              <path d="M12 8V13" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
              <circle cx="12" cy="16.2" r="0.9" fill="currentColor" />
            </svg>
            <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none">
              <circle cx="12" cy="12" r="9" stroke="currentColor" stroke-width="1.6" />
              <path d="M8 12.5L11 15.5L16 9.5" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" />
            </svg>
            <span>{{ profileAlert.text }}</span>
          </div>

          <form @submit.prevent="onSaveProfile" novalidate>
            <div class="form-grid">
              <div class="field">
                <label for="admin-hoTen">Họ và tên <span class="required">*</span></label>
                <div class="field-wrap" :class="{ 'field-wrap--error': errors.hoTen }">
                  <input
                    id="admin-hoTen"
                    v-model="form.hoTen"
                    type="text"
                    placeholder="Nguyễn Văn A"
                    @blur="validateField('hoTen')"
                    @input="errors.hoTen = ''"
                  />
                </div>
                <p v-if="errors.hoTen" class="field-error">{{ errors.hoTen }}</p>
              </div>

              <div class="field">
                <label for="admin-phone">Số điện thoại <span class="required">*</span></label>
                <div class="field-wrap" :class="{ 'field-wrap--error': errors.soDienThoai }">
                  <input
                    id="admin-phone"
                    v-model="form.soDienThoai"
                    type="tel"
                    placeholder="09xxxxxxxx"
                    @blur="validateField('soDienThoai')"
                    @input="errors.soDienThoai = ''"
                  />
                </div>
                <p v-if="errors.soDienThoai" class="field-error">{{ errors.soDienThoai }}</p>
              </div>

              <div class="field">
                <label for="admin-email">Địa chỉ Email <span class="required">*</span></label>
                <div class="field-wrap" :class="{ 'field-wrap--error': errors.email }">
                  <input
                    id="admin-email"
                    v-model="form.email"
                    type="email"
                    placeholder="admin@dreamleague.vn"
                    @blur="validateField('email')"
                    @input="errors.email = ''"
                  />
                </div>
                <p v-if="errors.email" class="field-error">{{ errors.email }}</p>
              </div>

              <div class="field">
                <label for="admin-address">Địa chỉ liên hệ</label>
                <div class="field-wrap">
                  <input
                    id="admin-address"
                    v-model="form.diaChi"
                    type="text"
                    placeholder="Quận/Huyện, Tỉnh/Thành phố..."
                  />
                </div>
              </div>
            </div>

            <div class="form-actions">
              <button type="submit" class="btn btn--primary" :disabled="isSaving">
                <span v-if="isSaving" class="btn__spinner"></span>
                {{ isSaving ? 'Đang lưu thay đổi...' : 'Lưu hồ sơ' }}
              </button>
            </div>
          </form>
        </div>

        <!-- Khối thay đổi mật khẩu -->
        <div class="profile-section profile-section--security reveal" style="--d:.16s">
          <div class="section-header">
            <div>
              <h3 class="section-title">Đổi mật khẩu bảo mật</h3>
              <p class="section-desc">Mật khẩu mới cần tối thiểu 6 ký tự để đảm bảo an toàn cho tài khoản Quản trị.</p>
            </div>
            <span class="badge badge--danger">Bảo mật cấp cao</span>
          </div>

          <div v-if="pwdAlert.text" :class="['alert-banner', pwdAlert.type === 'error' ? 'alert-banner--error' : 'alert-banner--success']">
            <svg v-if="pwdAlert.type === 'error'" width="18" height="18" viewBox="0 0 24 24" fill="none">
              <circle cx="12" cy="12" r="9" stroke="currentColor" stroke-width="1.6" />
              <path d="M12 8V13" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
              <circle cx="12" cy="16.2" r="0.9" fill="currentColor" />
            </svg>
            <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none">
              <circle cx="12" cy="12" r="9" stroke="currentColor" stroke-width="1.6" />
              <path d="M8 12.5L11 15.5L16 9.5" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" />
            </svg>
            <span>{{ pwdAlert.text }}</span>
          </div>

          <form @submit.prevent="onChangePassword" novalidate>
            <div class="field">
              <label for="admin-pwd-current">Mật khẩu hiện tại <span class="required">*</span></label>
              <div class="field-wrap" :class="{ 'field-wrap--error': pwdErrors.current }">
                <input
                  id="admin-pwd-current"
                  v-model="passwordForm.current"
                  :type="showPwd.current ? 'text' : 'password'"
                  placeholder="Nhập mật khẩu hiện tại của bạn"
                  @blur="validatePwdField('current')"
                  @input="pwdErrors.current = ''"
                />
                <button
                  type="button"
                  class="btn-toggle-eye"
                  :title="showPwd.current ? 'Ẩn mật khẩu' : 'Hiện mật khẩu'"
                  @click="showPwd.current = !showPwd.current"
                >
                  <svg v-if="showPwd.current" width="18" height="18" viewBox="0 0 24 24" fill="none">
                    <path d="M2 12C2 12 5.5 5.5 12 5.5S22 12 22 12 18.5 18.5 12 18.5 2 12 2 12Z" stroke="currentColor" stroke-width="1.6" />
                    <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="1.6" />
                  </svg>
                  <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none">
                    <path d="M3 3L21 21" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
                    <path d="M10.6 5.7C11.05 5.6 11.52 5.5 12 5.5C18.5 5.5 22 12 22 12C22 12 21.18 13.5 19.6 15.1M6.4 6.9C4 8.5 2 12 2 12C2 12 5.5 18.5 12 18.5C13.7 18.5 15.16 18.07 16.4 17.4" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
                  </svg>
                </button>
              </div>
              <p v-if="pwdErrors.current" class="field-error">{{ pwdErrors.current }}</p>
            </div>

            <div class="form-grid">
              <div class="field">
                <label for="admin-pwd-new">Mật khẩu mới <span class="required">*</span></label>
                <div class="field-wrap" :class="{ 'field-wrap--error': pwdErrors.new }">
                  <input
                    id="admin-pwd-new"
                    v-model="passwordForm.new"
                    :type="showPwd.new ? 'text' : 'password'"
                    placeholder="Tối thiểu 6 ký tự"
                    @blur="validatePwdField('new')"
                    @input="onPwdInput"
                  />
                  <button
                    type="button"
                    class="btn-toggle-eye"
                    :title="showPwd.new ? 'Ẩn mật khẩu' : 'Hiện mật khẩu'"
                    @click="showPwd.new = !showPwd.new"
                  >
                    <svg v-if="showPwd.new" width="18" height="18" viewBox="0 0 24 24" fill="none">
                      <path d="M2 12C2 12 5.5 5.5 12 5.5S22 12 22 12 18.5 18.5 12 18.5 2 12 2 12Z" stroke="currentColor" stroke-width="1.6" />
                      <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="1.6" />
                    </svg>
                    <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none">
                      <path d="M3 3L21 21" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
                      <path d="M10.6 5.7C11.05 5.6 11.52 5.5 12 5.5C18.5 5.5 22 12 22 12C22 12 21.18 13.5 19.6 15.1M6.4 6.9C4 8.5 2 12 2 12C2 12 5.5 18.5 12 18.5C13.7 18.5 15.16 18.07 16.4 17.4" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
                    </svg>
                  </button>
                </div>

                <!-- Thanh đo độ mạnh mật khẩu -->
                <div class="password-strength" v-if="passwordForm.new">
                  <span class="password-strength__bar" :class="barClass(1)"></span>
                  <span class="password-strength__bar" :class="barClass(2)"></span>
                  <span class="password-strength__bar" :class="barClass(3)"></span>
                  <span class="password-strength__bar" :class="barClass(4)"></span>
                </div>
                <p v-if="passwordForm.new" class="password-strength__label">
                  Độ mạnh: <span :class="'strength-text--' + passwordScore">{{ passwordLabel }}</span>
                </p>
                <p v-if="pwdErrors.new" class="field-error">{{ pwdErrors.new }}</p>
              </div>

              <div class="field">
                <label for="admin-pwd-confirm">Xác nhận mật khẩu mới <span class="required">*</span></label>
                <div class="field-wrap" :class="{ 'field-wrap--error': pwdErrors.confirm }">
                  <input
                    id="admin-pwd-confirm"
                    v-model="passwordForm.confirm"
                    :type="showPwd.confirm ? 'text' : 'password'"
                    placeholder="Nhập lại mật khẩu mới"
                    @blur="validatePwdField('confirm')"
                    @input="pwdErrors.confirm = ''"
                  />
                  <button
                    type="button"
                    class="btn-toggle-eye"
                    :title="showPwd.confirm ? 'Ẩn mật khẩu' : 'Hiện mật khẩu'"
                    @click="showPwd.confirm = !showPwd.confirm"
                  >
                    <svg v-if="showPwd.confirm" width="18" height="18" viewBox="0 0 24 24" fill="none">
                      <path d="M2 12C2 12 5.5 5.5 12 5.5S22 12 22 12 18.5 18.5 12 18.5 2 12 2 12Z" stroke="currentColor" stroke-width="1.6" />
                      <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="1.6" />
                    </svg>
                    <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none">
                      <path d="M3 3L21 21" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
                      <path d="M10.6 5.7C11.05 5.6 11.52 5.5 12 5.5C18.5 5.5 22 12 22 12C22 12 21.18 13.5 19.6 15.1M6.4 6.9C4 8.5 2 12 2 12C2 12 5.5 18.5 12 18.5C13.7 18.5 15.16 18.07 16.4 17.4" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
                    </svg>
                  </button>
                </div>
                <p v-if="pwdErrors.confirm" class="field-error">{{ pwdErrors.confirm }}</p>
              </div>
            </div>

            <div class="form-actions">
              <button type="submit" class="btn btn--dark" :disabled="isChangingPwd">
                <span v-if="isChangingPwd" class="btn__spinner"></span>
                {{ isChangingPwd ? 'Đang cập nhật...' : 'Cập nhật mật khẩu' }}
              </button>
            </div>
          </form>
        </div>

      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import profileService from '../../services/profileService'

const router = useRouter()

// Dữ liệu thô từ backend
function getMyAvatar() {
  const uid = localStorage.getItem('userId')
  return uid ? (localStorage.getItem(`avatar_${uid}`) || '') : ''
}

const rawUser = ref({})
const isLoading = ref(true)
const isSaving = ref(false)
const isChangingPwd = ref(false)
const avatarPreview = ref(getMyAvatar())

// Form thông tin cá nhân
const form = reactive({
  hoTen: '',
  email: '',
  soDienThoai: '',
  diaChi: ''
})

// Lỗi validation form thông tin
const errors = reactive({
  hoTen: '',
  email: '',
  soDienThoai: ''
})

// Form đổi mật khẩu
const passwordForm = reactive({
  current: '',
  new: '',
  confirm: ''
})

// Lỗi validation đổi mật khẩu
const pwdErrors = reactive({
  current: '',
  new: '',
  confirm: ''
})

// Trạng thái ẩn/hiện mật khẩu
const showPwd = reactive({
  current: false,
  new: false,
  confirm: false
})

// Banner thông báo
const profileAlert = reactive({ type: '', text: '' })
const pwdAlert = reactive({ type: '', text: '' })

// Chữ cái đầu làm Avatar
const userInitial = computed(() => {
  if (!form.hoTen) return 'A'
  const parts = form.hoTen.trim().split(' ')
  return parts[parts.length - 1].charAt(0).toUpperCase()
})

const shortId = computed(() => {
  if (!rawUser.value.id) return '---'
  return rawUser.value.id.toString().substring(0, 8).toUpperCase()
})

// Password score calculation
const passwordScore = computed(() => profileService.calculatePasswordScore(passwordForm.new))
const passwordLabel = computed(() => profileService.getPasswordLabel(passwordScore.value))

function barClass(n) {
  if (n > passwordScore.value) return ''
  const level = passwordScore.value <= 1 ? 1 : passwordScore.value === 2 ? 2 : 3
  return `password-strength__bar--filled-${level}`
}

function onPwdInput() {
  pwdErrors.new = ''
  if (passwordForm.confirm) {
    validatePwdField('confirm')
  }
}

function formatDate(dateStr) {
  if (!dateStr) return 'Mới tham gia'
  try {
    const cleanDate = dateStr.includes('T') ? dateStr.split('T')[0] : dateStr
    const [year, month, day] = cleanDate.split('-')
    return `${day}/${month}/${year}`
  } catch {
    return dateStr
  }
}

// ===== Hiệu ứng xuất hiện khi cuộn tới =====
let revealObserver = null

onMounted(() => {
  fetchAdminProfile()

  revealObserver = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('is-visible')
        revealObserver.unobserve(entry.target)
      }
    })
  }, { threshold: 0.1 })

  nextTick(() => document.querySelectorAll('.reveal').forEach(el => revealObserver.observe(el)))
})

onUnmounted(() => {
  if (revealObserver) revealObserver.disconnect()
})

// Xử lý upload ảnh đại diện preview
async function handleAvatarUpload(event) {
  const file = event.target.files[0]
  if (file) {
    if (file.size > 2 * 1024 * 1024) {
      profileAlert.type = 'error'
      profileAlert.text = 'Dung lượng ảnh không được vượt quá 2MB!'
      return
    }
    const reader = new FileReader()
    reader.onload = async (e) => {
      const uid = localStorage.getItem('userId')
      const base64Img = e.target.result
      avatarPreview.value = base64Img
      if (uid) {
        localStorage.setItem(`avatar_${uid}`, base64Img)
      }
      localStorage.removeItem('user_avatar')
      localStorage.removeItem('admin_avatar')
      localStorage.removeItem('staff_avatar')

      window.dispatchEvent(new CustomEvent('user-profile-updated', { 
        detail: { 
          userId: uid,
          hoTen: form.hoTen, 
          avatar: base64Img 
        } 
      }))

      // Lưu trực tiếp vào Database Backend
      try {
        await profileService.updateProfile(uid, {
          hoTen: form.hoTen || rawUser.value.hoTen,
          email: form.email || rawUser.value.email,
          soDienThoai: form.soDienThoai || rawUser.value.soDienThoai,
          diaChi: form.diaChi || rawUser.value.diaChi,
          avatar: base64Img
        }, 'ADMIN')
      } catch (err) {
        console.warn('Lưu avatar lên server: ', err.message)
      }

      profileAlert.type = 'success'
      profileAlert.text = 'Đã cập nhật ảnh đại diện quản trị!'
      setTimeout(() => { profileAlert.text = '' }, 3000)
    }
    reader.readAsDataURL(file)
  }
}

// Lấy thông tin tài khoản từ API
async function fetchAdminProfile() {
  const token = localStorage.getItem('token')
  const userId = localStorage.getItem('userId')

  if (!token || !userId) {
    router.push('/dang-nhap')
    return
  }

  isLoading.value = true
  try {
    const data = await profileService.getProfile(userId, 'ADMIN')
    rawUser.value = data
    form.hoTen = data.hoTen || ''
    form.email = data.email || ''
    form.soDienThoai = data.soDienThoai || ''
    form.diaChi = data.diaChi || ''
    if (data.avatar) {
      avatarPreview.value = data.avatar
      localStorage.setItem(`avatar_${userId}`, data.avatar)
    }
  } catch (err) {
    profileAlert.type = 'error'
    profileAlert.text = err.message || 'Không thể tải thông tin quản trị viên!'
  } finally {
    isLoading.value = false
    nextTick(() => {
      if (revealObserver) document.querySelectorAll('.reveal').forEach(el => revealObserver.observe(el))
    })
  }
}

// Validate từng ô thông tin
function validateField(field) {
  if (field === 'hoTen') {
    errors.hoTen = profileService.validateHoTen(form.hoTen)
  }
  if (field === 'email') {
    errors.email = profileService.validateEmail(form.email)
  }
  if (field === 'soDienThoai') {
    errors.soDienThoai = profileService.validateSoDienThoai(form.soDienThoai)
  }
}

function validateAllProfile() {
  ;['hoTen', 'email', 'soDienThoai'].forEach(validateField)
  return !errors.hoTen && !errors.email && !errors.soDienThoai
}

// Lưu thông tin cá nhân
async function onSaveProfile() {
  profileAlert.type = ''
  profileAlert.text = ''

  if (!validateAllProfile()) return

  const userId = localStorage.getItem('userId')
  isSaving.value = true
  try {
    const payload = {
      hoTen: form.hoTen.trim(),
      email: form.email.trim(),
      soDienThoai: form.soDienThoai.trim(),
      diaChi: form.diaChi ? form.diaChi.trim() : '',
      avatar: avatarPreview.value || ''
    }
    const updated = await profileService.updateProfile(userId, payload, 'ADMIN')
    rawUser.value = updated
    localStorage.setItem('hoTen', updated.hoTen)
    window.dispatchEvent(new CustomEvent('user-profile-updated', { 
      detail: { 
        userId,
        hoTen: updated.hoTen, 
        avatar: updated.avatar || avatarPreview.value 
      } 
    }))
    profileAlert.type = 'success'
    profileAlert.text = '🎉 Cập nhật hồ sơ quản trị viên thành công!'
    setTimeout(() => { profileAlert.text = '' }, 4000)
  } catch (err) {
    profileAlert.type = 'error'
    profileAlert.text = err.message || 'Cập nhật thất bại. Vui lòng thử lại!'
  } finally {
    isSaving.value = false
  }
}

// Validate từng ô mật khẩu
function validatePwdField(field) {
  if (field === 'current') {
    pwdErrors.current = passwordForm.current ? '' : 'Vui lòng nhập mật khẩu hiện tại'
  }
  if (field === 'new') {
    pwdErrors.new = profileService.validatePassword(passwordForm.new)
  }
  if (field === 'confirm') {
    pwdErrors.confirm = profileService.validateConfirmPassword(passwordForm.new, passwordForm.confirm)
  }
}

function validateAllPwd() {
  ;['current', 'new', 'confirm'].forEach(validatePwdField)
  return !pwdErrors.current && !pwdErrors.new && !pwdErrors.confirm
}

// Đổi mật khẩu
async function onChangePassword() {
  pwdAlert.type = ''
  pwdAlert.text = ''

  if (!validateAllPwd()) return

  const userId = localStorage.getItem('userId')
  isChangingPwd.value = true
  try {
    const res = await profileService.changePassword(userId, {
      matKhauHienTai: passwordForm.current,
      matKhauMoi: passwordForm.new
    }, 'ADMIN')

    pwdAlert.type = 'success'
    pwdAlert.text = '🔒 ' + (res.message || 'Đổi mật khẩu quản trị thành công!')
    passwordForm.current = ''
    passwordForm.new = ''
    passwordForm.confirm = ''
    setTimeout(() => { pwdAlert.text = '' }, 4000)
  } catch (err) {
    pwdAlert.type = 'error'
    pwdAlert.text = err.message || 'Mật khẩu hiện tại không chính xác!'
  } finally {
    isChangingPwd.value = false
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Oswald:wght@500;600;700&family=Manrope:wght@400;500;600;700;800&family=Space+Mono:wght@400;700&display=swap');

.page{
    --night-950:#050b08;
    --night-800:#0a1f13;
    --night-700:#123321;
    --turf-500:#23935a;
    --turf-700:#146239;
    --lime-400:#b6ff3c;
    --lime-300:#d3ff8f;
    --sky-400:#38bdf8;
    --sky-300:#7dd3fc;
    --purple-400:#a78bfa;
    --amber-400:#ffb020;
    --crimson-500:#ff4757;
    --chalk-050:#f7fbf4;
    --chalk-200:#e3ecdf;

    --font-display:'Oswald','Manrope',sans-serif;
    --font-body:'Manrope',sans-serif;
    --font-mono:'Space Mono',monospace;

    position:relative;
    background:var(--night-950);
    color:var(--chalk-050);
    font-family:var(--font-body);
    border-radius:20px;
    padding:32px;
    overflow:hidden;
    min-height:calc(100vh - 40px);
}

/* ===== REVEAL ===== */
.reveal{ opacity:0; transform:translateY(18px); transition:opacity .6s ease, transform .6s ease; transition-delay:var(--d,0s); }
.reveal.is-visible{ opacity:1; transform:translateY(0); }
@media (prefers-reduced-motion: reduce){ .reveal{ opacity:1; transform:none; transition:none; } }

/* ===== AMBIENT: đèn pha + hạt sáng + hạt rơi + đạo cụ ===== */
.page__ambient{ position:absolute; inset:0; z-index:0; pointer-events:none; border-radius:20px; overflow:hidden; }
.floodlight{ position:absolute; top:-20%; width:40vh; height:100vh; background:conic-gradient(from 90deg at 50% 0%, transparent 42%, rgba(255,244,214,.07) 50%, transparent 58%); mix-blend-mode:screen; animation:sweep 11s ease-in-out infinite alternate; }
.floodlight--l{ left:-6%; }
.floodlight--r{ right:-6%; animation-direction:alternate-reverse; }
@keyframes sweep{ 0%{ transform:rotate(-8deg); } 100%{ transform:rotate(8deg); } }

.motes{ position:absolute; inset:0; }
.mote{ position:absolute; bottom:-10px; left:calc((var(--i) * 8.6%) + 1%); width:3px; height:3px; border-radius:50%; background:var(--lime-300); opacity:0; animation:rise 9s linear infinite; animation-delay:calc(var(--i) * -0.7s); }
@keyframes rise{ 0%{ transform:translateY(0) scale(.6); opacity:0; } 10%{ opacity:.5; } 90%{ opacity:.1; } 100%{ transform:translateY(-100%) scale(1.1); opacity:0; } }

/* Hạt rơi */
.falling{ position:absolute; inset:0; overflow:hidden; }
.falling__item{
  position:absolute; top:-6%; left:calc((var(--i) * 6.2%) + 1%);
  width:3px; height:3px; border-radius:50%; background:var(--chalk-050);
  opacity:0; animation:fall linear infinite;
  animation-duration:calc(7s + (var(--i) * 0.45s));
  animation-delay:calc(var(--i) * -0.6s);
}
@keyframes fall{
  0%{ transform:translateY(0) translateX(0); opacity:0; }
  8%{ opacity:.5; }
  50%{ transform:translateY(48vh) translateX(14px); }
  92%{ opacity:.12; }
  100%{ transform:translateY(100vh) translateX(-10px); opacity:0; }
}

.field-props{ position:absolute; inset:0; }
.prop{ position:absolute; opacity:.1; animation:prop-float 7s ease-in-out infinite; }
.prop svg{ width:100%; height:100%; }
.prop--ball{ width:44px; height:44px; top:8%; right:8%; }
.prop--whistle{ width:52px; height:36px; bottom:12%; right:5%; animation-delay:.6s; }
.prop--flag{ width:30px; height:44px; top:20%; left:3%; animation-delay:1.1s; }
@keyframes prop-float{ 0%,100%{ transform:translateY(0) rotate(-3deg); } 50%{ transform:translateY(-14px) rotate(3deg); } }

@media (prefers-reduced-motion: reduce){
  .floodlight, .mote, .falling__item, .prop{ animation:none; }
}

/* Header */
.page__header{ position:relative; z-index:1; }
.page__title{
    font-family:var(--font-display);
    font-size:28px;
    font-weight:600;
    letter-spacing:-.01em;
    margin-bottom:6px;
    color:var(--chalk-050);
}

.page__desc{
    color:var(--chalk-200);
    opacity:.75;
    font-size:14.5px;
    margin-bottom:26px;
}

.brand{
    color:var(--lime-300);
    font-weight:700;
    text-shadow:0 0 10px rgba(182,255,60,.35);
}

/* Loading state */
.state{
    position:relative; z-index:1;
    display:flex;
    flex-direction:column;
    align-items:center;
    justify-content:center;
    gap:12px;
    padding:64px 20px;
    color:var(--chalk-200);
    opacity:.75;
    font-size:14px;
}

.spinner{
    width:30px;
    height:30px;
    border:3px solid rgba(247,251,244,.15);
    border-top-color:var(--lime-400);
    border-radius:50%;
    animation:spin .7s linear infinite;
}

@keyframes spin{ to{ transform:rotate(360deg); } }

/* ===== Bố cục chính: 2 cột ===== */
.profile-container{
    position:relative; z-index:1;
    display:grid;
    grid-template-columns:290px 1fr;
    gap:20px;
    align-items:flex-start;
}

/* Sidebar */
.sidebar-card{
    background:rgba(247,251,244,.04);
    border:1px solid rgba(247,251,244,.1);
    border-radius:16px;
    padding:28px 22px;
    text-align:center;
    display:flex;
    flex-direction:column;
    align-items:center;
}

.avatar-wrapper{ position:relative; width:104px; height:104px; margin-bottom:16px; }

.avatar-img{
    width:104px;
    height:104px;
    border-radius:50%;
    object-fit:cover;
    border:3px solid var(--lime-400);
    box-shadow:0 0 0 4px rgba(182,255,60,.12);
}

.avatar-circle{
    width:104px;
    height:104px;
    border-radius:50%;
    background:linear-gradient(135deg, var(--turf-500), var(--turf-700));
    color:var(--chalk-050);
    font-family:var(--font-display);
    font-size:40px;
    font-weight:700;
    display:flex;
    align-items:center;
    justify-content:center;
    box-shadow:0 0 0 4px rgba(182,255,60,.1);
}

.btn-upload-avatar{
    position:absolute;
    bottom:0;
    right:0;
    background:var(--night-950);
    color:var(--chalk-050);
    font-size:10.5px;
    font-weight:700;
    padding:5px 10px;
    border-radius:20px;
    cursor:pointer;
    display:flex;
    align-items:center;
    gap:4px;
    border:2px solid var(--night-800);
    transition:background .15s ease, color .15s ease;
}
.btn-upload-avatar:hover{ background:var(--lime-400); color:var(--night-950); }

.user-name{
    font-family:var(--font-display);
    font-size:18px;
    font-weight:700;
    color:var(--chalk-050);
    margin:0 0 8px;
}

.user-meta{
    width:100%;
    border-top:1px dashed rgba(247,251,244,.14);
    padding-top:16px;
    margin-top:20px;
    display:flex;
    flex-direction:column;
    gap:12px;
}

.meta-row{ display:flex; justify-content:space-between; align-items:center; font-size:13px; }
.meta-label{ color:var(--chalk-200); opacity:.7; }
.meta-val{ color:var(--chalk-050); font-weight:700; }
.font-mono{ font-family:var(--font-mono); }

/* Badges */
.badge{
    display:inline-flex;
    align-items:center;
    gap:6px;
    padding:5px 12px;
    border-radius:20px;
    font-size:12px;
    font-weight:700;
}

.badge__dot{ width:6px; height:6px; border-radius:50%; background:currentColor; }

.badge--role{ background:rgba(182,255,60,.14); color:var(--lime-300); margin-bottom:4px; }
.badge--active{ background:rgba(182,255,60,.14); color:var(--lime-300); }
.badge--info{ background:rgba(56,189,248,.14); color:var(--sky-300); }
.badge--danger{ background:rgba(255,71,87,.14); color:#ff9686; }

/* Content column */
.profile-content{ display:flex; flex-direction:column; gap:20px; }

.profile-section{
    background:rgba(247,251,244,.04);
    border:1px solid rgba(247,251,244,.1);
    border-radius:16px;
    padding:24px;
}

.profile-section--security{ border-top:3px solid var(--crimson-500); }

.section-header{
    display:flex;
    justify-content:space-between;
    align-items:flex-start;
    gap:12px;
    margin-bottom:20px;
}

.section-title{
    font-family:var(--font-display);
    font-size:16px;
    font-weight:700;
    color:var(--chalk-050);
    margin:0;
}

.section-desc{ font-size:13px; color:var(--chalk-200); opacity:.7; margin:4px 0 0; }

/* Alert banner */
.alert-banner{
    display:flex;
    align-items:center;
    gap:10px;
    padding:12px 16px;
    border-radius:10px;
    font-size:13.5px;
    font-weight:500;
    margin-bottom:20px;
    line-height:1.4;
}

.alert-banner--error{ background:rgba(255,71,87,.1); border:1px solid rgba(255,71,87,.3); color:#ff9686; }
.alert-banner--success{ background:rgba(182,255,60,.1); border:1px solid rgba(182,255,60,.3); color:var(--lime-300); }

/* Form */
.form-grid{
    display:grid;
    grid-template-columns:1fr 1fr;
    gap:16px 20px;
    margin-bottom:20px;
}

.field{ display:flex; flex-direction:column; gap:6px; }

.field label{ font-size:12.5px; font-weight:700; color:var(--chalk-200); opacity:.8; }

.required{ color:var(--crimson-500); opacity:1; }

.field-wrap{
    position:relative;
    display:flex;
    align-items:center;
    background:rgba(5,11,8,.5);
    border:1px solid rgba(247,251,244,.14);
    border-radius:9px;
    height:44px;
    padding:0 12px;
    transition:border-color .15s ease, box-shadow .15s ease;
}

.field-wrap:focus-within{
    border-color:var(--lime-400);
    box-shadow:0 0 0 3px rgba(182,255,60,.15);
}

.field-wrap--error{ border-color:var(--crimson-500) !important; }
.field-wrap--error:focus-within{ box-shadow:0 0 0 3px rgba(255,71,87,.15); }

.field-wrap input{
    flex:1;
    height:100%;
    border:none;
    background:transparent;
    outline:none;
    font-family:inherit;
    font-size:14px;
    color:var(--chalk-050);
}

.field-wrap input::placeholder{ color:rgba(247,251,244,.35); }

.btn-toggle-eye{
    background:none;
    border:none;
    color:var(--chalk-200);
    opacity:.7;
    padding:4px;
    display:flex;
    align-items:center;
    justify-content:center;
    cursor:pointer;
    transition:color .15s ease, opacity .15s ease;
}
.btn-toggle-eye:hover{ color:var(--lime-300); opacity:1; }

.field-error{ color:#ff9686; font-size:12px; font-weight:600; margin:5px 0 0; }

/* Password strength meter */
.password-strength{ display:flex; gap:5px; margin-top:8px; }

.password-strength__bar{
    flex:1;
    height:4px;
    background:rgba(247,251,244,.12);
    border-radius:999px;
    transition:background-color .2s ease;
}

.password-strength__bar--filled-1{ background:var(--crimson-500); }
.password-strength__bar--filled-2{ background:var(--amber-400); }
.password-strength__bar--filled-3{ background:var(--lime-400); }

.password-strength__label{ font-size:12px; color:var(--chalk-200); opacity:.7; margin:5px 0 0; }

.strength-text--0, .strength-text--1{ color:#ff9686; font-weight:700; }
.strength-text--2{ color:var(--amber-400); font-weight:700; }
.strength-text--3, .strength-text--4{ color:var(--lime-300); font-weight:700; }

/* Buttons */
.form-actions{
    display:flex;
    justify-content:flex-end;
    border-top:1px solid rgba(247,251,244,.1);
    padding-top:16px;
}

.btn{
    display:inline-flex;
    align-items:center;
    justify-content:center;
    gap:8px;
    border:none;
    border-radius:10px;
    padding:11px 22px;
    font-family:inherit;
    font-size:13.5px;
    font-weight:700;
    cursor:pointer;
    transition:background .15s, transform .05s, opacity .15s, box-shadow .15s;
}

.btn:active{ transform:scale(.98); }
.btn:disabled{ opacity:.6; cursor:not-allowed; }

.btn--primary{ background:var(--turf-500); color:var(--chalk-050); }
.btn--primary:hover:not(:disabled){ background:var(--lime-400); color:var(--night-950); box-shadow:0 8px 20px -8px rgba(182,255,60,.5); }

.btn--dark{ background:var(--night-950); color:var(--chalk-050); border:1px solid rgba(255,71,87,.3); }
.btn--dark:hover:not(:disabled){ background:#0e2116; border-color:var(--crimson-500); box-shadow:0 8px 20px -8px rgba(255,71,87,.35); }

.btn__spinner{
    width:15px;
    height:15px;
    border:2px solid rgba(247,251,244,.35);
    border-top-color:currentColor;
    border-radius:50%;
    animation:spin .7s linear infinite;
}

/* Responsive */
@media (max-width:900px){
    .profile-container{ grid-template-columns:1fr; }
}

@media (max-width:600px){
    .form-grid{ grid-template-columns:1fr; }
}
</style>