<template>
  <div class="user-profile-page">
    <div class="page-header">
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
.user-profile-page {
  max-width: 1100px;
  margin: 0 auto;
  padding: 16px 0;
  font-family: system-ui, -apple-system, sans-serif;
}

.page-header {
  margin-bottom: 24px;
}

.page-header__title {
  font-size: 24px;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 6px 0;
}

.page-header__desc {
  color: #64748b;
  font-size: 14px;
  margin: 0;
}

/* Loading State */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
  color: #64748b;
  gap: 16px;
}
.spinner-large {
  width: 40px;
  height: 40px;
  border: 3px solid #e2e8f0;
  border-top-color: #28a745;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

/* Bố cục chính Profile chia làm 2 cột */
.profile-container {
  display: grid;
  grid-template-columns: 290px 1fr;
  gap: 24px;
  align-items: flex-start;
}

/* Cột trái */
.profile-sidebar {
  background: #ffffff;
  padding: 28px 20px;
  border-radius: 14px;
  box-shadow: 0 1px 3px rgba(15, 23, 42, 0.04);
  border: 1px solid #e2e8f0;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.avatar-wrapper {
  position: relative;
  width: 110px;
  height: 110px;
  margin-bottom: 16px;
}

.avatar-img {
  width: 110px;
  height: 110px;
  border-radius: 50%;
  object-fit: cover;
  border: 3.5px solid #28a745;
  box-shadow: 0 4px 12px rgba(40, 167, 69, 0.15);
}

.avatar-circle {
  width: 110px;
  height: 110px;
  border-radius: 50%;
  background: linear-gradient(135deg, #28a745 0%, #1e7e34 100%);
  color: #ffffff;
  font-size: 42px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 14px rgba(40, 167, 69, 0.25);
}

.btn-upload-avatar {
  position: absolute;
  bottom: 0;
  right: 0;
  background: #0f172a;
  color: #ffffff;
  font-size: 11px;
  font-weight: 600;
  padding: 5px 10px;
  border-radius: 20px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
  border: 2px solid #ffffff;
  box-shadow: 0 2px 6px rgba(0,0,0,0.15);
  transition: background 0.15s ease;
}
.btn-upload-avatar:hover {
  background: #1e293b;
}

.user-name {
  font-size: 18px;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 6px 0;
}

.user-role {
  font-size: 12px;
  color: #1e7e34;
  font-weight: 700;
  background: #eafaf1;
  border: 1px solid #c3e6cb;
  display: inline-block;
  padding: 3px 12px;
  border-radius: 20px;
  margin-bottom: 20px;
}

.user-meta {
  width: 100%;
  border-top: 1px dashed #e2e8f0;
  padding-top: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.meta-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
}
.meta-label {
  color: #64748b;
}
.meta-val {
  color: #1e293b;
  font-weight: 600;
}
.font-mono {
  font-family: monospace;
}
.meta-status-tag {
  background: #eafaf1;
  color: #28a745;
  font-size: 11px;
  font-weight: 700;
  padding: 2px 8px;
  border-radius: 6px;
}

/* Cột phải */
.profile-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.profile-section {
  background: #ffffff;
  padding: 24px;
  border-radius: 14px;
  box-shadow: 0 1px 3px rgba(15, 23, 42, 0.04);
  border: 1px solid #e2e8f0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.section-title {
  font-size: 16px;
  font-weight: 700;
  margin: 0;
  color: #0f172a;
}

.section-desc {
  font-size: 13px;
  color: #64748b;
  margin: 4px 0 0 0;
}

.section-badge {
  font-size: 11px;
  font-weight: 600;
  color: #28a745;
  background: #eafaf1;
  border: 1px solid #c3e6cb;
  padding: 3px 10px;
  border-radius: 20px;
}

.security-badge {
  font-size: 11px;
  font-weight: 600;
  color: #b91c1c;
  background: #fef2f2;
  border: 1px solid #fecaca;
  padding: 3px 10px;
  border-radius: 20px;
}

/* Alert Banner */
.alert-banner {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 13.5px;
  font-weight: 500;
  margin-bottom: 20px;
  line-height: 1.4;
}
.alert-error {
  background: #fef2f2;
  border: 1px solid #fecaca;
  color: #b91c1c;
}
.alert-success {
  background: #eafaf1;
  border: 1px solid #c3e6cb;
  color: #1e7e34;
}

/* Form layout */
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px 20px;
  margin-bottom: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-label {
  font-size: 13px;
  font-weight: 600;
  color: #334155;
  margin-bottom: 6px;
}
.required {
  color: #ef4444;
}

.input-wrap {
  position: relative;
  display: flex;
  align-items: center;
  background: #ffffff;
  border: 1.5px solid #cbd5e1;
  border-radius: 8px;
  height: 44px;
  padding: 0 12px;
  transition: border-color 0.15s ease, box-shadow 0.15s ease;
}
.input-wrap:focus-within {
  border-color: #28a745;
  box-shadow: 0 0 0 3px rgba(40, 167, 69, 0.12);
}
.input-wrap--error {
  border-color: #ef4444 !important;
  background-color: #fffafb;
}
.input-wrap--error:focus-within {
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.12);
}

.form-control {
  flex: 1;
  height: 100%;
  border: none;
  background: transparent;
  outline: none;
  font-size: 14px;
  color: #0f172a;
  font-family: inherit;
}
.form-control::placeholder {
  color: #94a3b8;
}

.btn-toggle-eye {
  background: none;
  border: none;
  color: #64748b;
  padding: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: color 0.15s ease;
}
.btn-toggle-eye:hover {
  color: #0f172a;
}

.field-error {
  color: #ef4444;
  font-size: 12px;
  font-weight: 500;
  margin: 5px 0 0 0;
}

/* Password Strength Meter */
.password-strength {
  display: flex;
  gap: 5px;
  margin-top: 8px;
}
.password-strength__bar {
  flex: 1;
  height: 4px;
  background-color: #e2e8f0;
  border-radius: 999px;
  transition: background-color 0.2s ease;
}
.password-strength__bar--filled-1 { background-color: #ef4444; }
.password-strength__bar--filled-2 { background-color: #f59e0b; }
.password-strength__bar--filled-3 { background-color: #28a745; }

.password-strength__label {
  font-size: 12px;
  color: #64748b;
  margin: 5px 0 0 0;
  font-weight: 500;
}
.strength-text--0, .strength-text--1 { color: #ef4444; font-weight: 700; }
.strength-text--2 { color: #f59e0b; font-weight: 700; }
.strength-text--3, .strength-text--4 { color: #28a745; font-weight: 700; }

/* Buttons & Actions */
.form-actions {
  display: flex;
  justify-content: flex-end;
  border-top: 1px solid #f1f5f9;
  padding-top: 16px;
}

.btn {
  padding: 10px 22px;
  border-radius: 8px;
  font-size: 13.5px;
  font-weight: 600;
  cursor: pointer;
  border: none;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.15s ease;
}

.btn-primary {
  background: #28a745;
  color: #ffffff;
}
.btn-primary:hover:not(:disabled) {
  background: #218838;
}

.btn-dark {
  background: #0f172a;
  color: #ffffff;
}
.btn-dark:hover:not(:disabled) {
  background: #1e293b;
}

.btn:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

.spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: #ffffff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.security-section {
  border-top: 3px solid #ef4444;
}

@media (max-width: 900px) {
  .profile-container {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 600px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>