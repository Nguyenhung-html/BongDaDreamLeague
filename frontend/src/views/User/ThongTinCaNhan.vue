<template>
  <div class="profile-container">
    <div class="profile-header">
      <h1 class="profile-header__title">Tài khoản của tôi</h1>
      <p class="profile-header__desc">Quản lý thông tin hồ sơ cá nhân và cài đặt bảo mật tài khoản.</p>
    </div>

    <div class="profile-layout">
      <div class="profile-sidebar">
        <div class="card card-overview">
          <div class="avatar-wrapper">
            <div class="avatar-circle">
              {{ getUserInitial(user.fullName) }}
            </div>
            <button class="btn-change-avatar" title="Đổi ảnh đại diện">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"/>
                <circle cx="12" cy="13" r="4"/>
              </svg>
            </button>
          </div>
          
          <h2 class="overview-name">{{ user.fullName || 'Chưa cập nhật' }}</h2>
          <span class="badge-role">{{ user.role }}</span>

          <div class="overview-meta">
            <div class="meta-item">
              <span class="meta-label">Mã tài khoản:</span>
              <span class="meta-value font-mono">#{{ user.id }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">Ngày tham gia:</span>
              <span class="meta-value">{{ formatDate(user.createdAt) }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="profile-main">
        <div class="card card-form">
          <h3 class="card-title">Thông tin cá nhân</h3>
          <form @submit.prevent="saveProfile">
            <div class="form-grid">
              <div class="form-group">
                <label class="form-label">Họ và tên *</label>
                <input type="text" v-model="user.fullName" required class="form-control" placeholder="Nhập họ tên của bạn" />
              </div>

              <div class="form-group">
                <label class="form-label">Số điện thoại *</label>
                <input type="tel" v-model="user.phone" required class="form-control" placeholder="Nhập số điện thoại" />
              </div>

              <div class="form-group">
                <label class="form-label">Địa chỉ Email *</label>
                <input type="email" v-model="user.email" required class="form-control" placeholder="name@example.com" />
              </div>

              <div class="form-group">
                <label class="form-label">Địa chỉ (Không bắt buộc)</label>
                <input type="text" v-model="user.address" class="form-control" placeholder="Số nhà, tên đường, quận/huyện..." />
              </div>
            </div>

            <div class="form-actions">
              <button type="submit" class="btn btn-primary" :disabled="isSavingProfile">
                {{ isSavingProfile ? 'Đang lưu...' : 'Cập nhật thông tin' }}
              </button>
            </div>
          </form>
        </div>

        <div class="card card-form security-section">
          <h3 class="card-title">Đổi mật khẩu</h3>
          <p class="card-subtitle">Nên sử dụng mật khẩu mạnh dài ít nhất 6 ký tự bao gồm chữ cái và số.</p>
          
          <form @submit.prevent="changePassword">
            <div class="form-group password-field">
              <label class="form-label">Mật khẩu hiện tại</label>
              <div class="input-password-wrapper">
                <input :type="showPwd.current ? 'text' : 'password'" v-model="passwordForm.current" class="form-control" placeholder="••••••••" />
                <button type="button" class="btn-toggle-pwd" @click="showPwd.current = !showPwd.current">
                  {{ showPwd.current ? 'Ẩn' : 'Hiện' }}
                </button>
              </div>
            </div>

            <div class="form-grid">
              <div class="form-group password-field">
                <label class="form-label">Mật khẩu mới</label>
                <div class="input-password-wrapper">
                  <input :type="showPwd.new ? 'text' : 'password'" v-model="passwordForm.new" class="form-control" placeholder="Tối thiểu 6 ký tự" />
                  <button type="button" class="btn-toggle-pwd" @click="showPwd.new = !showPwd.new">
                    {{ showPwd.new ? 'Ẩn' : 'Hiện' }}
                  </button>
                </div>
              </div>

              <div class="form-group password-field">
                <label class="form-label">Xác nhận mật khẩu mới</label>
                <div class="input-password-wrapper">
                  <input :type="showPwd.confirm ? 'text' : 'password'" v-model="passwordForm.confirm" class="form-control" placeholder="Nhập lại mật khẩu mới" />
                  <button type="button" class="btn-toggle-pwd" @click="showPwd.confirm = !showPwd.confirm">
                    {{ showPwd.confirm ? 'Ẩn' : 'Hiện' }}
                  </button>
                </div>
              </div>
            </div>

            <div class="form-actions">
              <button type="submit" class="btn btn-dark" :disabled="isChangingPwd">
                Thay đổi mật khẩu
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// Dữ liệu User mẫu lấy từ LocalStorage hoặc API về
const user = ref({
  id: 'US7821',
  fullName: 'Nguyễn Minh Đức',
  phone: '0905123456',
  email: 'minhduc.dev@gmail.com',
  address: '123 Nguyễn Văn Linh, Quận Thanh Khê, Đà Nẵng',
  role: 'User / Thành viên',
  createdAt: '2026-03-15'
})

// Trạng thái xử lý form
const isSavingProfile = ref(false)
const isChangingPwd = ref(false)

// Form đổi mật khẩu riêng biệt
const passwordForm = ref({
  current: '',
  new: '',
  confirm: ''
})

// Ẩn/hiện mật khẩu cho từng ô input
const showPwd = ref({
  current: false,
  new: false,
  confirm: false
})

// Trích xuất chữ cái đầu của tên làm avatar mặc định chuyên nghiệp
function getUserInitial(name) {
  if (!name) return 'U'
  const parts = name.trim().split(' ')
  return parts[parts.length - 1].charAt(0).toUpperCase()
}

// Định dạng ngày hiển thị (DD/MM/YYYY)
function formatDate(dateStr) {
  if (!dateStr) return ''
  const [year, month, day] = dateStr.split('-')
  return `${day}/${month}/${year}`
}

// Hàm: Lưu thông tin cá nhân
function saveProfile() {
  isSavingProfile.value = true
  
  // Giả lập gọi API mất 1 giây
  setTimeout(() => {
    isSavingProfile.value = false
    alert('🎉 Cập nhật thông tin hồ sơ cá nhân thành công!')
  }, 1000)
}

// Hàm: Đổi mật khẩu
function changePassword() {
  if (!passwordForm.value.current || !passwordForm.value.new || !passwordForm.value.confirm) {
    alert('Vui lòng nhập đầy đủ các trường mật khẩu.');
    return;
  }
  if (passwordForm.value.new.length < 6) {
    alert('Mật khẩu mới phải từ 6 ký tự trở lên.');
    return;
  }
  if (passwordForm.value.new !== passwordForm.value.confirm) {
    alert('Mật khẩu xác nhận không khớp với mật khẩu mới.');
    return;
  }

  isChangingPwd.value = true
  setTimeout(() => {
    isChangingPwd.value = false
    alert('🔒 Đổi mật khẩu thành công! Hãy ghi nhớ mật khẩu mới của bạn.');
    // Reset form mật khẩu
    passwordForm.value = { current: '', new: '', confirm: '' }
  }, 1200)
}
</script>

<style scoped>
/* ===== TOÀN BỘ TRANG ===== */
.profile-container {
  max-width: 1100px;
  margin: 0 auto;
  padding: 12px 0;
  font-family: system-ui, -apple-system, sans-serif;
}

/* ===== HEADER ===== */
.profile-header {
  margin-bottom: 28px;
}
.profile-header__title {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 6px 0;
}
.profile-header__desc {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

/* ===== BỐ CỤC CHÍNH GRID ===== */
.profile-layout {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 24px;
  align-items: start;
}

@media (max-width: 768px) {
  .profile-layout {
    grid-template-columns: 1fr;
  }
}

/* ===== THÀNH PHẦN CARD CHUNG ===== */
.card {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(15, 23, 42, 0.03);
}

/* ===== CỘT TRÁI - TỔNG QUAN HỒ SƠ ===== */
.card-overview {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%);
}

.avatar-wrapper {
  position: relative;
  margin-bottom: 16px;
}

.avatar-circle {
  width: 96px;
  height: 96px;
  border-radius: 50%;
  background: #28a745;
  color: #ffffff;
  font-size: 36px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(40, 167, 69, 0.2);
}

.btn-change-avatar {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #1e293b;
  color: #ffffff;
  border: 2px solid #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background 0.2s;
}
.btn-change-avatar:hover {
  background: #0f172a;
}

.overview-name {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 8px 0;
}

.badge-role {
  display: inline-block;
  padding: 4px 12px;
  background: rgba(40, 167, 69, 0.1);
  color: #28a745;
  font-size: 12px;
  font-weight: 600;
  border-radius: 20px;
  margin-bottom: 24px;
}

.overview-meta {
  width: 100%;
  border-top: 1px dashed #e2e8f0;
  padding-top: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.meta-item {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
}
.meta-label {
  color: #64748b;
}
.meta-value {
  color: #1e293b;
  font-weight: 600;
}
.font-mono {
  font-family: monospace;
}

/* ===== CỘT PHẢI - CHI TIẾT FORM ===== */
.profile-main {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.card-title {
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 16px 0;
}

.card-subtitle {
  font-size: 13px;
  color: #64748b;
  margin: -12px 0 20px 0;
}

/* Form Grid */
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 20px;
}

@media (max-width: 576px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 14px;
}

.form-label {
  font-size: 13px;
  font-weight: 600;
  color: #475569;
}

/* Ô Nhập liệu Input */
.form-control {
  width: 100%;
  padding: 10px 14px;
  font-size: 14px;
  border: 1px solid #cbd5e1;
  border-radius: 8px;
  background-color: #ffffff;
  color: #1e293b;
  outline: none;
  box-sizing: border-box;
  transition: all 0.2s;
}
.form-control:focus {
  border-color: #28a745;
  box-shadow: 0 0 0 3px rgba(40, 167, 69, 0.1);
}

/* Tính năng ẩn/hiện password */
.input-password-wrapper {
  position: relative;
  width: 100%;
}
.input-password-wrapper .form-control {
  padding-right: 50px;
}
.btn-toggle-pwd {
  position: absolute;
  top: 50%;
  right: 12px;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: #64748b;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
}
.btn-toggle-pwd:hover {
  color: #1e293b;
}

/* Nút Bấm nút Hành Động */
.form-actions {
  display: flex;
  justify-content: flex-end;
  border-top: 1px solid #f1f5f9;
  padding-top: 16px;
  margin-top: 8px;
}

.btn {
  padding: 10px 20px;
  font-size: 14px;
  font-weight: 600;
  border-radius: 8px;
  cursor: pointer;
  border: none;
  transition: all 0.2s;
}

.btn-primary {
  background: #28a745;
  color: #ffffff;
}
.btn-primary:hover {
  background: #218838;
}
.btn-primary:disabled {
  background: #a3e635;
  cursor: not-allowed;
}

.btn-dark {
  background: #1e293b;
  color: #ffffff;
}
.btn-dark:hover {
  background: #0f172a;
}

.security-section {
  border-top: 2px solid #ef4444; /* Tạo vạch đỏ cảnh báo nhẹ phần bảo mật */
}
</style>