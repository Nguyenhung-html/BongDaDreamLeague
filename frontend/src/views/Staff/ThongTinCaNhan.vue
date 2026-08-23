<template>
  <div class="staff-profile">
    <!-- Nền ambient đồng bộ các trang khác -->
    <div class="staff-profile__ambient" aria-hidden="true">
      <div class="floodlight floodlight--l"></div>
      <div class="floodlight floodlight--r"></div>
      <div class="motes"><span v-for="n in 10" :key="'m' + n" class="mote" :style="{ '--i': n }"></span></div>
      <div class="falling"><span v-for="n in 18" :key="'f' + n" class="falling__item" :style="{ '--i': n }"></span></div>
    </div>

    <div class="page-header reveal">
      <span class="eyebrow">Tài khoản · Hồ sơ</span>
      <h1 class="page-header__title">Thông tin cá nhân Nhân viên</h1>
      <p class="page-header__desc">
        Quản lý thông tin hồ sơ nhân viên và cập nhật mật khẩu đăng nhập hệ thống.
      </p>
    </div>

    <!-- Khung tải dữ liệu ban đầu -->
    <div v-if="isLoading" class="loading-state reveal" style="--d: .08s">
      <div class="spinner-large"></div>
      <p>Đang tải thông tin nhân viên...</p>
    </div>

    <div v-else class="profile-container">
      <!-- BÊN TRÁI: AVATAR & THÔNG TIN CHUNG -->
      <div class="profile-sidebar reveal" style="--d: .08s">
        <div class="avatar-wrapper">
          <img v-if="avatarPreview" :src="avatarPreview" alt="Avatar" class="avatar-img" />
          <div v-else class="avatar-circle">
            {{ userInitial }}
          </div>
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
        <span class="user-role">Nhân viên (Staff)</span>

        <div class="user-meta">
          <div class="meta-row">
            <span class="meta-label">Mã nhân viên</span>
            <span class="meta-val font-mono">#{{ shortId }}</span>
          </div>
          <div class="meta-row">
            <span class="meta-label">Ngày tham gia</span>
            <span class="meta-val">{{ formatDate(rawUser.ngayTao) }}</span>
          </div>
          <div class="meta-row">
            <span class="meta-label">Trạng thái</span>
            <span class="meta-status-tag">● Đang làm việc</span>
          </div>
        </div>
      </div>

      <!-- BÊN PHẢI: FORM CHỈNH SỬA & ĐỔI MẬT KHẨU -->
      <div class="profile-content">
        <!-- Form thông tin cá nhân -->
        <div class="profile-section reveal" style="--d: .16s">
          <div class="section-header">
            <h3 class="section-title">Hồ sơ nhân viên</h3>
            <span class="section-badge">Thông tin liên hệ</span>
          </div>

          <div v-if="profileAlert.text" :class="['alert-banner', profileAlert.type === 'error' ? 'alert-error' : 'alert-success']">
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
              <div class="form-group">
                <label class="form-label" for="staff-hoTen">Họ và tên <span class="required">*</span></label>
                <div class="input-wrap" :class="{ 'input-wrap--error': errors.hoTen }">
                  <input
                    id="staff-hoTen"
                    v-model="form.hoTen"
                    type="text"
                    class="form-control"
                    placeholder="Nguyễn Văn A"
                    @blur="validateField('hoTen')"
                    @input="errors.hoTen = ''"
                  />
                </div>
                <p v-if="errors.hoTen" class="field-error">{{ errors.hoTen }}</p>
              </div>

              <div class="form-group">
                <label class="form-label" for="staff-phone">Số điện thoại <span class="required">*</span></label>
                <div class="input-wrap" :class="{ 'input-wrap--error': errors.soDienThoai }">
                  <input
                    id="staff-phone"
                    v-model="form.soDienThoai"
                    type="tel"
                    class="form-control"
                    placeholder="09xxxxxxxx"
                    @blur="validateField('soDienThoai')"
                    @input="errors.soDienThoai = ''"
                  />
                </div>
                <p v-if="errors.soDienThoai" class="field-error">{{ errors.soDienThoai }}</p>
              </div>

              <div class="form-group">
                <label class="form-label" for="staff-email">Địa chỉ Email <span class="required">*</span></label>
                <div class="input-wrap" :class="{ 'input-wrap--error': errors.email }">
                  <input
                    id="staff-email"
                    v-model="form.email"
                    type="email"
                    class="form-control"
                    placeholder="staff@dreamleague.vn"
                    @blur="validateField('email')"
                    @input="errors.email = ''"
                  />
                </div>
                <p v-if="errors.email" class="field-error">{{ errors.email }}</p>
              </div>

              <div class="form-group">
                <label class="form-label" for="staff-address">Địa chỉ thường trú</label>
                <div class="input-wrap">
                  <input
                    id="staff-address"
                    v-model="form.diaChi"
                    type="text"
                    class="form-control"
                    placeholder="Số nhà, đường, quận/huyện..."
                  />
                </div>
              </div>
            </div>

            <div class="form-actions">
              <button type="submit" class="btn btn-primary" :disabled="isSaving">
                <span v-if="isSaving" class="spinner"></span>
                {{ isSaving ? 'Đang lưu thay đổi...' : 'Cập nhật thông tin' }}
              </button>
            </div>
          </form>
        </div>

        <!-- Khối thay đổi mật khẩu -->
        <div class="profile-section security-section reveal" style="--d: .24s">
          <div class="section-header">
            <div>
              <h3 class="section-title">Đổi mật khẩu nhân viên</h3>
              <p class="section-desc">Mật khẩu mới yêu cầu tối thiểu 6 ký tự để bảo vệ ca trực và dữ liệu.</p>
            </div>
            <span class="security-badge">Bảo mật tài khoản</span>
          </div>

          <div v-if="pwdAlert.text" :class="['alert-banner', pwdAlert.type === 'error' ? 'alert-error' : 'alert-success']">
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
            <div class="form-group">
              <label class="form-label" for="staff-pwd-current">Mật khẩu hiện tại <span class="required">*</span></label>
              <div class="input-wrap" :class="{ 'input-wrap--error': pwdErrors.current }">
                <input
                  id="staff-pwd-current"
                  v-model="passwordForm.current"
                  :type="showPwd.current ? 'text' : 'password'"
                  class="form-control"
                  placeholder="Nhập mật khẩu hiện tại"
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
              <div class="form-group">
                <label class="form-label" for="staff-pwd-new">Mật khẩu mới <span class="required">*</span></label>
                <div class="input-wrap" :class="{ 'input-wrap--error': pwdErrors.new }">
                  <input
                    id="staff-pwd-new"
                    v-model="passwordForm.new"
                    :type="showPwd.new ? 'text' : 'password'"
                    class="form-control"
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

              <div class="form-group">
                <label class="form-label" for="staff-pwd-confirm">Xác nhận mật khẩu mới <span class="required">*</span></label>
                <div class="input-wrap" :class="{ 'input-wrap--error': pwdErrors.confirm }">
                  <input
                    id="staff-pwd-confirm"
                    v-model="passwordForm.confirm"
                    :type="showPwd.confirm ? 'text' : 'password'"
                    class="form-control"
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
              <button type="submit" class="btn btn-primary" :disabled="isChangingPwd">
                <span v-if="isChangingPwd" class="spinner"></span>
                {{ isChangingPwd ? 'Đang cập nhật...' : 'Thay đổi mật khẩu' }}
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
  if (!form.hoTen) return 'S'
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
        }, 'STAFF')
      } catch (err) {
        console.warn('Lưu avatar lên server: ', err.message)
      }

      profileAlert.type = 'success'
      profileAlert.text = 'Đã cập nhật ảnh đại diện nhân viên!'
      setTimeout(() => { profileAlert.text = '' }, 3000)
    }
    reader.readAsDataURL(file)
  }
}

// Lấy thông tin tài khoản từ API
async function fetchStaffProfile() {
  const token = localStorage.getItem('token')
  const userId = localStorage.getItem('userId')

  if (!token || !userId) {
    router.push('/dang-nhap')
    return
  }

  isLoading.value = true
  try {
    const data = await profileService.getProfile(userId, 'STAFF')
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
    profileAlert.text = err.message || 'Không thể tải thông tin nhân viên!'
  } finally {
    isLoading.value = false
    nextTick(() => document.querySelectorAll('.reveal').forEach(el => revealObserver?.observe(el)))
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
    const updated = await profileService.updateProfile(userId, payload, 'STAFF')
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
    profileAlert.text = '🎉 Cập nhật hồ sơ nhân viên thành công!'
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
    }, 'STAFF')

    pwdAlert.type = 'success'
    pwdAlert.text = '🔒 ' + (res.message || 'Đổi mật khẩu nhân viên thành công!')
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

let revealObserver = null

onMounted(() => {
  revealObserver = new IntersectionObserver(entries => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('is-visible')
        revealObserver.unobserve(entry.target)
      }
    })
  }, { threshold: 0.12 })
  nextTick(() => document.querySelectorAll('.reveal').forEach(el => revealObserver.observe(el)))

  fetchStaffProfile()
})

onUnmounted(() => {
  if (revealObserver) revealObserver.disconnect()
})
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Oswald:wght@500;600;700&family=Manrope:wght@400;500;600;700;800&family=Space+Mono:wght@400;700&display=swap');
</style>

<style scoped>
.staff-profile {
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

  position: relative;
  width: 100%;
  max-width: 1100px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
  background: var(--night-950);
  color: var(--chalk-050);
  font-family: var(--font-body);
  border-radius: 20px;
  padding: 32px;
  overflow: hidden;
}

/* ===== REVEAL ===== */
.reveal { opacity: 0; transform: translateY(16px); transition: opacity .55s ease, transform .55s ease; transition-delay: var(--d, 0s); }
.reveal.is-visible { opacity: 1; transform: translateY(0); }
@media (prefers-reduced-motion: reduce) { .reveal { opacity: 1; transform: none; transition: none; } }

/* ===== AMBIENT ===== */
.staff-profile__ambient { position: absolute; inset: 0; z-index: 0; pointer-events: none; border-radius: 20px; overflow: hidden; }
.floodlight { position: absolute; top: -20%; width: 40vh; height: 100vh; background: conic-gradient(from 90deg at 50% 0%, transparent 42%, rgba(255,244,214,.06) 50%, transparent 58%); mix-blend-mode: screen; animation: sweep 11s ease-in-out infinite alternate; }
.floodlight--l { left: -6%; }
.floodlight--r { right: -6%; animation-direction: alternate-reverse; }
@keyframes sweep { 0% { transform: rotate(-8deg); } 100% { transform: rotate(8deg); } }
.motes { position: absolute; inset: 0; }
.mote { position: absolute; bottom: -10px; left: calc((var(--i) * 10%) + 1%); width: 3px; height: 3px; border-radius: 50%; background: var(--lime-300); opacity: 0; animation: rise 9s linear infinite; animation-delay: calc(var(--i) * -0.8s); }
@keyframes rise { 0% { transform: translateY(0) scale(.6); opacity: 0; } 10% { opacity: .45; } 90% { opacity: .1; } 100% { transform: translateY(-100%) scale(1.1); opacity: 0; } }
.falling { position: absolute; inset: 0; overflow: hidden; }
.falling__item {
  position: absolute; top: -6%; left: calc((var(--i) * 5.6%) + 1%);
  width: 3px; height: 3px; border-radius: 50%; background: var(--chalk-050);
  opacity: 0; animation: fall linear infinite;
  animation-duration: calc(7s + (var(--i) * 0.45s));
  animation-delay: calc(var(--i) * -0.6s);
}
@keyframes fall {
  0% { transform: translateY(0) translateX(0); opacity: 0; }
  8% { opacity: .55; }
  50% { transform: translateY(160px) translateX(14px); }
  92% { opacity: .12; }
  100% { transform: translateY(340px) translateX(-10px); opacity: 0; }
}

/* ===== HEADER ===== */
.page-header { position: relative; z-index: 1; }
.eyebrow { display: inline-block; font-family: var(--font-mono); font-size: 12px; letter-spacing: .14em; text-transform: uppercase; color: var(--lime-300); }
.page-header__title { font-family: var(--font-display); font-size: 26px; font-weight: 600; color: var(--chalk-050); margin: 8px 0 4px; }
.page-header__desc { font-size: 13.5px; color: var(--chalk-200); opacity: .75; margin: 0; }

/* ===== LOADING ===== */
.loading-state {
  position: relative; z-index: 1;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  padding: 60px 0; color: var(--chalk-200); gap: 16px;
  background: var(--night-800); border: 1px solid rgba(247,251,244,.1); border-radius: 16px;
}
.spinner-large {
  width: 36px; height: 36px; border: 3px solid rgba(247,251,244,.15);
  border-top-color: var(--lime-400); border-radius: 50%; animation: spin .8s linear infinite;
}

/* ===== LAYOUT ===== */
.profile-container { position: relative; z-index: 1; display: grid; grid-template-columns: 290px 1fr; gap: 24px; align-items: flex-start; }

/* ===== SIDEBAR ===== */
.profile-sidebar {
  background: var(--night-800);
  padding: 28px 20px;
  border-radius: 16px;
  border: 1px solid rgba(247,251,244,.1);
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.avatar-wrapper { position: relative; width: 110px; height: 110px; margin-bottom: 16px; }

.avatar-img {
  width: 110px; height: 110px; border-radius: 50%; object-fit: cover;
  border: 3.5px solid var(--lime-400);
  box-shadow: 0 4px 16px rgba(182,255,60,.2);
}

.avatar-circle {
  width: 110px; height: 110px; border-radius: 50%;
  background: linear-gradient(135deg, var(--turf-500) 0%, var(--turf-700) 100%);
  color: var(--chalk-050); font-family: var(--font-display); font-size: 42px; font-weight: 600;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 4px 16px rgba(35,147,90,.3);
}

.btn-upload-avatar {
  position: absolute; bottom: 0; right: 0;
  background: var(--night-950); color: var(--lime-300);
  font-size: 11px; font-weight: 600; padding: 5px 10px; border-radius: 20px;
  cursor: pointer; display: flex; align-items: center; gap: 4px;
  border: 2px solid var(--night-800); box-shadow: 0 2px 8px rgba(0,0,0,.4);
  transition: background .15s ease, color .15s ease;
}
.btn-upload-avatar:hover { background: var(--turf-500); color: var(--chalk-050); }

.user-name { font-family: var(--font-display); font-size: 18px; font-weight: 600; color: var(--chalk-050); margin: 0 0 6px; }

.user-role {
  font-size: 11.5px; color: var(--lime-300); font-weight: 700;
  background: rgba(182,255,60,.1); border: 1px solid rgba(182,255,60,.3);
  display: inline-block; padding: 3px 12px; border-radius: 20px; margin-bottom: 20px;
  text-transform: uppercase; letter-spacing: .03em;
}

.user-meta { width: 100%; border-top: 1px dashed rgba(247,251,244,.15); padding-top: 16px; display: flex; flex-direction: column; gap: 12px; }

.meta-row { display: flex; justify-content: space-between; align-items: center; font-size: 12.5px; }
.meta-label { color: var(--chalk-200); opacity: .65; }
.meta-val { color: var(--chalk-050); font-weight: 600; }
.font-mono { font-family: var(--font-mono); }
.meta-status-tag {
  background: rgba(35,147,90,.16); color: var(--lime-300);
  font-size: 11px; font-weight: 700; padding: 3px 9px; border-radius: 6px;
}

/* ===== CONTENT ===== */
.profile-content { display: flex; flex-direction: column; gap: 24px; min-width: 0; }

.profile-section { background: var(--night-800); padding: 24px; border-radius: 16px; border: 1px solid rgba(247,251,244,.1); }

.section-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 20px; gap: 12px; flex-wrap: wrap; }
.section-title { font-family: var(--font-display); font-size: 16px; font-weight: 600; margin: 0; color: var(--chalk-050); }
.section-desc { font-size: 12.5px; color: var(--chalk-200); opacity: .65; margin: 4px 0 0; }

.section-badge {
  font-size: 11px; font-weight: 700; color: var(--lime-300);
  background: rgba(182,255,60,.1); border: 1px solid rgba(182,255,60,.3);
  padding: 3px 10px; border-radius: 20px; white-space: nowrap;
}
.security-badge {
  font-size: 11px; font-weight: 700; color: #ff9686;
  background: rgba(255,71,87,.1); border: 1px solid rgba(255,71,87,.3);
  padding: 3px 10px; border-radius: 20px; white-space: nowrap;
}

/* ===== ALERT ===== */
.alert-banner { display: flex; align-items: center; gap: 10px; padding: 12px 16px; border-radius: 10px; font-size: 13px; font-weight: 500; margin-bottom: 20px; line-height: 1.4; }
.alert-error { background: rgba(255,71,87,.1); border: 1px solid rgba(255,71,87,.3); color: #ff9686; }
.alert-success { background: rgba(182,255,60,.08); border: 1px solid rgba(182,255,60,.28); color: var(--lime-300); }

/* ===== FORM ===== */
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px 20px; margin-bottom: 20px; }
.form-group { display: flex; flex-direction: column; }
.form-label { font-size: 12.5px; font-weight: 600; color: var(--chalk-200); margin-bottom: 6px; }
.required { color: var(--crimson-500); }

.input-wrap {
  position: relative; display: flex; align-items: center;
  background: rgba(5,11,8,.5); border: 1.5px solid rgba(247,251,244,.15);
  border-radius: 9px; height: 44px; padding: 0 12px;
  transition: border-color .15s ease, box-shadow .15s ease;
}
.input-wrap:focus-within { border-color: var(--lime-400); box-shadow: 0 0 0 3px rgba(182,255,60,.14); }
.input-wrap--error { border-color: var(--crimson-500) !important; background-color: rgba(255,71,87,.06); }
.input-wrap--error:focus-within { box-shadow: 0 0 0 3px rgba(255,71,87,.14); }

.form-control { flex: 1; height: 100%; border: none; background: transparent; outline: none; font-size: 13.5px; color: var(--chalk-050); font-family: var(--font-body); }
.form-control::placeholder { color: rgba(227,236,223,.4); }

.btn-toggle-eye { background: none; border: none; color: var(--chalk-200); opacity: .7; padding: 4px; display: flex; align-items: center; justify-content: center; cursor: pointer; transition: color .15s ease, opacity .15s ease; }
.btn-toggle-eye:hover { color: var(--lime-300); opacity: 1; }

.field-error { color: #ff9686; font-size: 12px; font-weight: 500; margin: 5px 0 0; }

/* ===== PASSWORD STRENGTH ===== */
.password-strength { display: flex; gap: 5px; margin-top: 8px; }
.password-strength__bar { flex: 1; height: 4px; background-color: rgba(247,251,244,.12); border-radius: 999px; transition: background-color .2s ease; }
.password-strength__bar--filled-1 { background-color: var(--crimson-500); }
.password-strength__bar--filled-2 { background-color: var(--amber-400); }
.password-strength__bar--filled-3 { background-color: var(--lime-400); }

.password-strength__label { font-size: 12px; color: var(--chalk-200); opacity: .7; margin: 5px 0 0; font-weight: 500; }
.strength-text--0, .strength-text--1 { color: #ff9686; font-weight: 700; }
.strength-text--2 { color: var(--amber-400); font-weight: 700; }
.strength-text--3, .strength-text--4 { color: var(--lime-300); font-weight: 700; }

/* ===== BUTTONS ===== */
.form-actions { display: flex; justify-content: flex-end; border-top: 1px solid rgba(247,251,244,.08); padding-top: 16px; }

.btn {
  padding: 10px 22px; border-radius: 9px; font-size: 13px; font-weight: 600; cursor: pointer;
  border: none; display: inline-flex; align-items: center; justify-content: center; gap: 8px;
  transition: background .15s ease, opacity .15s ease; font-family: var(--font-body);
}
.btn-primary { background: var(--turf-500); color: var(--chalk-050); }
.btn-primary:hover:not(:disabled) { background: var(--turf-700); }
.btn:disabled { opacity: .6; cursor: not-allowed; }

.spinner { width: 16px; height: 16px; border: 2px solid rgba(255,255,255,.3); border-top-color: #ffffff; border-radius: 50%; animation: spin .8s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }

.security-section { border-top: 3px solid var(--crimson-500); }

@media (max-width: 900px) {
  .profile-container { grid-template-columns: 1fr; }
}
@media (max-width: 600px) {
  .form-grid { grid-template-columns: 1fr; }
}
</style>