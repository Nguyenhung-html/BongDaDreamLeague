<template>
  <div class="user-profile-page">

    <!-- ===== LỚP NỀN ẨN DỤ (đồng bộ trang chủ) ===== -->
    <div class="user-profile-page__ambient" aria-hidden="true">
      <div class="floodlight floodlight--l"></div>
      <div class="floodlight floodlight--r"></div>
      <div class="motes">
        <span v-for="n in 12" :key="n" class="mote" :style="{ '--i': n }"></span>
      </div>
    </div>

    <div class="container">
      <div class="page-header">
        <span class="eyebrow">⚽ Tài khoản</span>
        <h1 class="page-header__title">Tài khoản của tôi</h1>
        <p class="page-header__desc">Quản lý thông tin hồ sơ cá nhân và cài đặt bảo mật tài khoản thành viên.</p>
      </div>

      <!-- Khung tải dữ liệu ban đầu -->
      <div v-if="isLoading" class="loading-state">
        <div class="spinner-large"></div>
        <p>Đang tải thông tin tài khoản...</p>
      </div>

      <div v-else class="profile-container">
        <!-- BÊN TRÁI: AVATAR & THÔNG TIN CHUNG -->
        <div class="profile-sidebar">
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
          <span class="user-role">{{ roleDisplay }}</span>

          <div class="user-meta">
            <div class="meta-row">
              <span class="meta-label">Mã thành viên:</span>
              <span class="meta-val font-mono">#{{ shortId }}</span>
            </div>
            <div class="meta-row">
              <span class="meta-label">Ngày tham gia:</span>
              <span class="meta-val">{{ formatDate(rawUser.ngayTao) }}</span>
            </div>
            <div class="meta-row">
              <span class="meta-label">Trạng thái:</span>
              <span class="meta-status-tag">Thành viên tích cực</span>
            </div>
          </div>
        </div>

        <!-- BÊN PHẢI: FORM CHỈNH SỬA & ĐỔI MẬT KHẨU -->
        <div class="profile-content">
          <!-- Form thông tin cá nhân -->
          <div class="profile-section">
            <div class="section-header">
              <h3 class="section-title">Hồ sơ cá nhân</h3>
              <span class="section-badge">Thông tin cơ bản</span>
            </div>

            <!-- Thông báo banner cho Form thông tin -->
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
                  <label class="form-label" for="user-hoTen">Họ và tên <span class="required">*</span></label>
                  <div class="input-wrap" :class="{ 'input-wrap--error': errors.hoTen }">
                    <input
                      id="user-hoTen"
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
                  <label class="form-label" for="user-phone">Số điện thoại <span class="required">*</span></label>
                  <div class="input-wrap" :class="{ 'input-wrap--error': errors.soDienThoai }">
                    <input
                      id="user-phone"
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
                  <label class="form-label" for="user-email">Địa chỉ Email <span class="required">*</span></label>
                  <div class="input-wrap" :class="{ 'input-wrap--error': errors.email }">
                    <input
                      id="user-email"
                      v-model="form.email"
                      type="email"
                      class="form-control"
                      placeholder="name@example.com"
                      @blur="validateField('email')"
                      @input="errors.email = ''"
                    />
                  </div>
                  <p v-if="errors.email" class="field-error">{{ errors.email }}</p>
                </div>

                <div class="form-group">
                  <label class="form-label" for="user-address">Địa chỉ (Tùy chọn)</label>
                  <div class="input-wrap">
                    <input
                      id="user-address"
                      v-model="form.diaChi"
                      type="text"
                      class="form-control"
                      placeholder="Số nhà, tên đường, quận/huyện..."
                    />
                  </div>
                </div>
              </div>

              <div class="form-actions">
                <button type="submit" class="btn btn-primary" :disabled="isSaving">
                  <span v-if="isSaving" class="spinner"></span>
                  {{ isSaving ? 'Đang lưu...' : 'Cập nhật thông tin' }}
                </button>
              </div>
            </form>
          </div>

          <!-- Khối thay đổi mật khẩu -->
          <div class="profile-section security-section">
            <div class="section-header">
              <div>
                <h3 class="section-title">Đổi mật khẩu tài khoản</h3>
                <p class="section-desc">Mật khẩu mới yêu cầu tối thiểu 6 ký tự để bảo vệ tài khoản cá nhân.</p>
              </div>
              <span class="security-badge">Bảo mật mật khẩu</span>
            </div>

            <!-- Thông báo banner cho Form mật khẩu -->
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
                <label class="form-label" for="user-pwd-current">Mật khẩu hiện tại <span class="required">*</span></label>
                <div class="input-wrap" :class="{ 'input-wrap--error': pwdErrors.current }">
                  <input
                    id="user-pwd-current"
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
                  <label class="form-label" for="user-pwd-new">Mật khẩu mới <span class="required">*</span></label>
                  <div class="input-wrap" :class="{ 'input-wrap--error': pwdErrors.new }">
                    <input
                      id="user-pwd-new"
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

                  <!-- Thanh đo độ mạnh mật khẩu (Password Strength Meter) -->
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
                  <label class="form-label" for="user-pwd-confirm">Xác nhận mật khẩu mới <span class="required">*</span></label>
                  <div class="input-wrap" :class="{ 'input-wrap--error': pwdErrors.confirm }">
                    <input
                      id="user-pwd-confirm"
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
                <button type="submit" class="btn btn-dark" :disabled="isChangingPwd">
                  <span v-if="isChangingPwd" class="spinner"></span>
                  {{ isChangingPwd ? 'Đang cập nhật...' : 'Thay đổi mật khẩu' }}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import profileService from '../../services/profileService'

const router = useRouter()

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
  if (!form.hoTen) return 'U'
  const parts = form.hoTen.trim().split(' ')
  return parts[parts.length - 1].charAt(0).toUpperCase()
})

const shortId = computed(() => {
  if (!rawUser.value.id) return '---'
  return rawUser.value.id.toString().substring(0, 8).toUpperCase()
})

const roleDisplay = computed(() => {
  const r = rawUser.value.vaiTro
  if (r === 'ADMIN') return 'Quản trị viên (Admin)'
  if (r === 'STAFF') return 'Nhân viên (Staff)'
  return 'Khách hàng / Thành viên'
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
      const role = localStorage.getItem('userRole') || 'User'
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
        }, role)
      } catch (err) {
        console.warn('Lưu avatar lên server: ', err.message)
      }

      profileAlert.type = 'success'
      profileAlert.text = 'Đã cập nhật ảnh đại diện!'
      setTimeout(() => { profileAlert.text = '' }, 3000)
    }
    reader.readAsDataURL(file)
  }
}

// Lấy thông tin tài khoản từ API
async function fetchUserProfile() {
  const token = localStorage.getItem('token')
  const userId = localStorage.getItem('userId')
  const role = localStorage.getItem('userRole') || 'User'

  if (!token || !userId) {
    router.push('/dang-nhap')
    return
  }

  isLoading.value = true
  try {
    const data = await profileService.getProfile(userId, role)
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
    profileAlert.text = err.message || 'Không thể tải thông tin người dùng!'
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  fetchUserProfile()
})

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
  const role = localStorage.getItem('userRole') || 'User'
  isSaving.value = true
  try {
    const payload = {
      hoTen: form.hoTen.trim(),
      email: form.email.trim(),
      soDienThoai: form.soDienThoai.trim(),
      diaChi: form.diaChi ? form.diaChi.trim() : '',
      avatar: avatarPreview.value || ''
    }
    const updated = await profileService.updateProfile(userId, payload, role)
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
    profileAlert.text = '🎉 Cập nhật thông tin cá nhân thành công!'
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
  const role = localStorage.getItem('userRole') || 'User'
  isChangingPwd.value = true
  try {
    const res = await profileService.changePassword(userId, {
      matKhauHienTai: passwordForm.current,
      matKhauMoi: passwordForm.new
    }, role)

    pwdAlert.type = 'success'
    pwdAlert.text = '🔒 ' + (res.message || 'Đổi mật khẩu thành công!')
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
/* ===== TOKENS (đồng bộ trang chủ / danh sách sân / lịch sử / voucher / chi tiết / hỗ trợ) ===== */
.user-profile-page {
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
  background: var(--night-950);
  color: var(--chalk-050);
  font-family: var(--font-body);
  min-height: calc(100vh - 76px);
  padding: 44px 0 70px;
  overflow: hidden;
}
.container { max-width: 1100px; margin: 0 auto; padding: 0 24px; position: relative; z-index: 1; }

/* ===== LỚP NỀN ẨN DỤ ===== */
.user-profile-page__ambient { position: fixed; inset: 0; z-index: 0; pointer-events: none; }
.floodlight { position: absolute; top: -12%; width: 55vh; height: 145vh; background: conic-gradient(from 90deg at 50% 0%, transparent 42%, rgba(255,244,214,.06) 50%, transparent 58%); mix-blend-mode: screen; }
.floodlight--l { left: -14%; animation: sweep 12s ease-in-out infinite alternate; }
.floodlight--r { right: -14%; animation: sweep 12s ease-in-out infinite alternate-reverse; }
@keyframes sweep { 0% { transform: rotate(-10deg); } 100% { transform: rotate(10deg); } }
.motes { position: absolute; inset: 0; }
.mote { position: absolute; bottom: -10px; left: calc((var(--i) * 8%) + 1%); width: 3px; height: 3px; border-radius: 50%; background: var(--lime-300); opacity: 0; animation: rise 10s linear infinite; animation-delay: calc(var(--i) * -0.7s); }
@keyframes rise { 0% { transform: translateY(0) scale(.6); opacity: 0; } 10% { opacity: .45; } 90% { opacity: .1; } 100% { transform: translateY(-100vh) scale(1.1); opacity: 0; } }

/* ===== HEADER ===== */
.page-header { margin-bottom: 28px; }
.eyebrow { display: inline-block; font-family: var(--font-mono); font-size: 12px; letter-spacing: .16em; text-transform: uppercase; color: var(--lime-300); }
.page-header__title { font-family: var(--font-display); font-size: 26px; font-weight: 600; color: var(--chalk-050); margin: 8px 0 6px 0; }
.page-header__desc { color: var(--chalk-200); opacity: .7; font-size: 14px; margin: 0; }

/* Loading State */
.loading-state {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  padding: 60px 0; color: var(--chalk-200); opacity: .75; gap: 16px;
}
.spinner-large {
  width: 40px; height: 40px; border: 3px solid rgba(247,251,244,.14);
  border-top-color: var(--lime-400); border-radius: 50%; animation: spin .8s linear infinite;
}

/* Bố cục chính Profile chia làm 2 cột */
.profile-container { display: grid; grid-template-columns: 290px 1fr; gap: 24px; align-items: flex-start; }

/* Cột trái */
.profile-sidebar {
  background: rgba(247,251,244,.03); padding: 28px 20px; border-radius: 18px;
  border: 1px solid rgba(247,251,244,.09);
  text-align: center; display: flex; flex-direction: column; align-items: center;
}

.avatar-wrapper { position: relative; width: 110px; height: 110px; margin-bottom: 16px; }
.avatar-img {
  width: 110px; height: 110px; border-radius: 50%; object-fit: cover;
  border: 3px solid var(--lime-400); box-shadow: 0 4px 20px rgba(182,255,60,.2);
}
.avatar-circle {
  width: 110px; height: 110px; border-radius: 50%;
  background: linear-gradient(135deg, var(--turf-500), var(--turf-700));
  color: var(--chalk-050); font-family: var(--font-display); font-size: 42px; font-weight: 600;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 4px 20px rgba(35,147,90,.35);
}
.btn-upload-avatar {
  position: absolute; bottom: 0; right: 0;
  background: var(--night-950); color: var(--lime-300);
  font-size: 11px; font-weight: 600; padding: 5px 10px; border-radius: 999px;
  cursor: pointer; display: flex; align-items: center; gap: 4px;
  border: 2px solid var(--night-950); box-shadow: 0 2px 8px rgba(0,0,0,.4);
  transition: background .15s;
}
.btn-upload-avatar:hover { background: var(--night-800); }

.user-name { font-family: var(--font-display); font-size: 18px; font-weight: 600; color: var(--chalk-050); margin: 0 0 6px 0; }
.user-role {
  font-size: 12px; color: var(--lime-300); font-weight: 700;
  background: rgba(182,255,60,.1); border: 1px solid rgba(182,255,60,.25);
  display: inline-block; padding: 3px 12px; border-radius: 999px; margin-bottom: 20px;
}

.user-meta { width: 100%; border-top: 1px dashed rgba(247,251,244,.14); padding-top: 16px; display: flex; flex-direction: column; gap: 12px; }
.meta-row { display: flex; justify-content: space-between; align-items: center; font-size: 13px; }
.meta-label { color: var(--chalk-200); opacity: .6; }
.meta-val { color: var(--chalk-050); font-weight: 600; }
.font-mono { font-family: var(--font-mono); }
.meta-status-tag { background: rgba(182,255,60,.1); color: var(--lime-300); font-size: 11px; font-weight: 700; padding: 2px 8px; border-radius: 6px; }

/* Cột phải */
.profile-content { display: flex; flex-direction: column; gap: 24px; }

.profile-section {
  background: rgba(247,251,244,.03); padding: 24px; border-radius: 18px;
  border: 1px solid rgba(247,251,244,.09);
}

.section-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 20px; }
.section-title { font-family: var(--font-display); font-size: 16px; font-weight: 600; margin: 0; color: var(--chalk-050); }
.section-desc { font-size: 13px; color: var(--chalk-200); opacity: .65; margin: 4px 0 0 0; }

.section-badge {
  font-size: 11px; font-weight: 600; color: var(--lime-300);
  background: rgba(182,255,60,.1); border: 1px solid rgba(182,255,60,.25);
  padding: 3px 10px; border-radius: 999px;
}
.security-badge {
  font-size: 11px; font-weight: 600; color: #ff9686;
  background: rgba(255,71,87,.12); border: 1px solid rgba(255,71,87,.28);
  padding: 3px 10px; border-radius: 999px;
}

/* Alert Banner */
.alert-banner {
  display: flex; align-items: center; gap: 10px; padding: 12px 16px; border-radius: 10px;
  font-size: 13.5px; font-weight: 500; margin-bottom: 20px; line-height: 1.4;
}
.alert-error { background: rgba(255,71,87,.1); border: 1px solid rgba(255,71,87,.28); color: #ff9686; }
.alert-success { background: rgba(182,255,60,.08); border: 1px solid rgba(182,255,60,.28); color: var(--lime-300); }

/* Form layout */
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px 20px; margin-bottom: 20px; }
.form-group { display: flex; flex-direction: column; }
.form-label { font-size: 13px; font-weight: 600; color: var(--chalk-200); opacity: .85; margin-bottom: 6px; }
.required { color: #ff9686; }

.input-wrap {
  position: relative; display: flex; align-items: center;
  background: rgba(5,11,8,.4); border: 1.5px solid rgba(247,251,244,.16);
  border-radius: 10px; height: 44px; padding: 0 12px;
  transition: border-color .15s, box-shadow .15s;
}
.input-wrap:focus-within { border-color: var(--lime-400); box-shadow: 0 0 0 3px rgba(182,255,60,.12); }
.input-wrap--error { border-color: var(--crimson-500) !important; background-color: rgba(255,71,87,.05); }
.input-wrap--error:focus-within { box-shadow: 0 0 0 3px rgba(255,71,87,.12); }

.form-control { flex: 1; height: 100%; border: none; background: transparent; outline: none; font-size: 14px; color: var(--chalk-050); font-family: inherit; }
.form-control::placeholder { color: var(--chalk-200); opacity: .4; }

.btn-toggle-eye { background: none; border: none; color: var(--chalk-200); opacity: .6; padding: 4px; display: flex; align-items: center; justify-content: center; cursor: pointer; transition: opacity .15s; }
.btn-toggle-eye:hover { opacity: 1; color: var(--lime-300); }

.field-error { color: #ff9686; font-size: 12px; font-weight: 500; margin: 5px 0 0 0; }

/* Password Strength Meter */
.password-strength { display: flex; gap: 5px; margin-top: 8px; }
.password-strength__bar { flex: 1; height: 4px; background-color: rgba(247,251,244,.12); border-radius: 999px; transition: background-color .2s; }
.password-strength__bar--filled-1 { background-color: var(--crimson-500); }
.password-strength__bar--filled-2 { background-color: var(--amber-400); }
.password-strength__bar--filled-3 { background-color: var(--lime-400); }

.password-strength__label { font-size: 12px; color: var(--chalk-200); opacity: .65; margin: 5px 0 0 0; font-weight: 500; }
.strength-text--0, .strength-text--1 { color: #ff9686; font-weight: 700; }
.strength-text--2 { color: var(--amber-400); font-weight: 700; }
.strength-text--3, .strength-text--4 { color: var(--lime-300); font-weight: 700; }

/* Buttons & Actions */
.form-actions { display: flex; justify-content: flex-end; border-top: 1px solid rgba(247,251,244,.08); padding-top: 16px; }

.btn {
  padding: 10px 22px; border-radius: 999px; font-size: 13.5px; font-weight: 700;
  cursor: pointer; border: none; display: inline-flex; align-items: center;
  justify-content: center; gap: 8px; transition: all .15s;
}
.btn-primary { background: linear-gradient(135deg, var(--turf-500), var(--turf-700)); color: var(--chalk-050); }
.btn-primary:hover:not(:disabled) { box-shadow: 0 10px 22px -8px rgba(182,255,60,.4); transform: translateY(-2px); }

.btn-dark { background: var(--night-950); color: var(--chalk-050); border: 1.5px solid rgba(247,251,244,.16); }
.btn-dark:hover:not(:disabled) { border-color: var(--crimson-500); color: #ff9686; }

.btn:disabled { opacity: .55; cursor: not-allowed; }

.spinner {
  width: 16px; height: 16px; border: 2px solid rgba(255,255,255,.3);
  border-top-color: #ffffff; border-radius: 50%; animation: spin .8s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

.security-section { border-top: 2px solid var(--crimson-500); }

@media (max-width: 900px) { .profile-container { grid-template-columns: 1fr; } }
@media (max-width: 600px) { .form-grid { grid-template-columns: 1fr; } }
</style>