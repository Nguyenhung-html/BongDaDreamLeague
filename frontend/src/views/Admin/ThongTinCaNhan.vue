<template>
  <div>
    <h1 class="dashboard__page-title">
        Thông tin cá nhân
    </h1>

    <p class="dashboard__page-desc">
        Quản lý thông tin hồ sơ, thay đổi ảnh đại diện và cập nhật mật khẩu tài khoản.
    </p>

    <div class="profile-container">
        <!-- BÊN TRÁI: AVATAR & THÔNG TIN CHUNG -->
        <div class="profile-sidebar">
            <div class="avatar-wrapper">
                <img :src="user.avatar" alt="Avatar" class="avatar-img" />
                <label class="btn-upload-avatar">
                    📷 Thay ảnh
                    <input type="file" accept="image/*" @change="handleAvatarUpload" style="display: none;" />
                </label>
            </div>
            
            <h2 class="user-name">{{ user.fullName }}</h2>
            <p class="user-role">Quản trị viên cấp cao</p>
            <div class="user-meta">
                <span>Ngày tham gia: <b>01/01/2025</b></span>
            </div>
        </div>

        <!-- BÊN PHẢI: FORM CHỈNH SỬA & ĐỔI MẬT KHẨU -->
        <div class="profile-content">
            <!-- Form thông tin cá nhân -->
            <div class="profile-section">
                <h3 class="section-title">Hồ sơ cá nhân</h3>
                
                <div class="form-grid">
                    <div class="form-group">
                        <label class="form-label">Họ và tên</label>
                        <input v-model="user.fullName" type="text" class="form-control" />
                    </div>

                    <div class="form-group">
                        <label class="form-label">Số điện thoại</label>
                        <input v-model="user.phone" type="text" class="form-control" />
                    </div>

                    <div class="form-group">
                        <label class="form-label">Email</label>
                        <input v-model="user.email" type="email" class="form-control" />
                    </div>

                    <div class="form-group">
                        <label class="form-label">Địa chỉ</label>
                        <input v-model="user.address" type="text" class="form-control" />
                    </div>
                </div>

                <button class="btn-save" @click="saveProfile">Lưu thay đổi</button>
            </div>

            <!-- Khối thay đổi mật khẩu -->
            <div class="profile-section" style="margin-top: 25px;">
                <h3 class="section-title">Đổi mật khẩu</h3>
                
                <div class="form-group">
                    <label class="form-label">Mật khẩu hiện tại</label>
                    <input v-model="passwordForm.current" type="password" class="form-control" placeholder="••••••••" />
                </div>

                <div class="form-grid">
                    <div class="form-group">
                        <label class="form-label">Mật khẩu mới</label>
                        <input v-model="passwordForm.new" type="password" class="form-control" placeholder="Tối thiểu 6 ký tự" />
                    </div>

                    <div class="form-group">
                        <label class="form-label">Xác nhận mật khẩu mới</label>
                        <input v-model="passwordForm.confirm" type="password" class="form-control" placeholder="Nhập lại mật khẩu mới" />
                    </div>
                </div>

                <button class="btn-change-pass" @click="changePassword">Cập nhật mật khẩu</button>
            </div>
        </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue"

// Thông tin tài khoản mẫu của Admin
const user = ref({
    fullName: "Nguyễn Quốc Trung",
    phone: "0988.888.888",
    email: "quoctrung.admin@gmail.com",
    address: "Cầu Giấy, Hà Nội",
    avatar: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150"
})

// Form quản lý đổi mật khẩu
const passwordForm = ref({
    current: "",
    new: "",
    confirm: ""
})

// Xử lý upload ảnh đại diện (đọc file local dạng Base64 để hiển thị luôn)
function handleAvatarUpload(event) {
    const file = event.target.files[0]
    if (file) {
        const reader = new FileReader()
        reader.onload = (e) => {
            user.value.avatar = e.target.result
        }
        reader.readAsDataURL(file)
    }
}

// Lưu thông tin cá nhân
function saveProfile() {
    if (!user.value.fullName.trim() || !user.value.phone.trim() || !user.value.email.trim()) {
        alert("Vui lòng điền đầy đủ các thông tin bắt buộc (Họ tên, SĐT, Email)!")
        return
    }
    alert("Cập nhật hồ sơ cá nhân thành công!")
}

// Xử lý đổi mật khẩu bảo mật
function changePassword() {
    const { current, new: newPass, confirm } = passwordForm.value

    if (!current || !newPass || !confirm) {
        alert("Vui lòng nhập đầy đủ thông tin vào các ô mật khẩu!")
        return
    }
    if (newPass.length < 6) {
        alert("Mật khẩu mới phải có độ dài từ 6 ký tự trở lên!")
        return
    }
    if (newPass !== confirm) {
        alert("Mật khẩu xác nhận mới không trùng khớp!")
        return
    }

    alert("Thay đổi mật khẩu thành công! Vui lòng sử dụng mật khẩu mới cho lần đăng nhập sau.")
    passwordForm.value = { current: "", new: "", confirm: "" } // Reset form
}
</script>

<style scoped>
.dashboard__page-title {
    font-size: 28px;
    font-weight: 700;
    margin-bottom: 6px;
}

.dashboard__page-desc {
    color: #64748b;
    margin-bottom: 25px;
}

/* Bố cục chính Profile chia làm 2 cột */
.profile-container {
    display: flex;
    gap: 25px;
    align-items: flex-start;
}

/* Cột trái */
.profile-sidebar {
    width: 280px;
    background: #fff;
    padding: 30px 20px;
    border-radius: 12px;
    box-shadow: 0 2px 10px rgba(0,0,0,.06);
    border: 1px solid #f1f5f9;
    text-align: center;
    flex-shrink: 0;
}

.avatar-wrapper {
    position: relative;
    width: 120px;
    height: 120px;
    margin: 0 auto 15px auto;
}

.avatar-img {
    width: 120px;
    height: 120px;
    border-radius: 50%;
    object-fit: cover;
    border: 3px solid #6366f1;
}

.btn-upload-avatar {
    position: absolute;
    bottom: 0;
    left: 50%;
    transform: translateX(-50%);
    background: rgba(15, 23, 42, 0.8);
    color: #fff;
    font-size: 11px;
    padding: 4px 10px;
    border-radius: 20px;
    cursor: pointer;
    white-space: nowrap;
}

.user-name {
    font-size: 20px;
    font-weight: 600;
    color: #0f172a;
    margin-bottom: 4px;
}

.user-role {
    font-size: 13px;
    color: #6366f1;
    font-weight: 500;
    background: #e0e7ff;
    display: inline-block;
    padding: 2px 10px;
    border-radius: 20px;
    margin-bottom: 15px;
}

.user-meta {
    font-size: 12px;
    color: #64748b;
    border-top: 1px dashed #e2e8f0;
    padding-top: 15px;
}

/* Cột phải */
.profile-content {
    flex-grow: 1;
}

.profile-section {
    background: #fff;
    padding: 25px;
    border-radius: 12px;
    box-shadow: 0 2px 10px rgba(0,0,0,.06);
    border: 1px solid #f1f5f9;
}

.section-title {
    font-size: 18px;
    font-weight: 600;
    margin-bottom: 20px;
    color: #1e293b;
    border-left: 4px solid #6366f1;
    padding-left: 10px;
}

/* Layout Form ô nhập dữ liệu */
.form-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 15px 20px;
    margin-bottom: 20px;
}

.form-group {
    display: flex;
    flex-direction: column;
    margin-bottom: 15px;
}

.form-label {
    font-size: 14px;
    font-weight: 500;
    color: #475569;
    margin-bottom: 6px;
}

.form-control {
    padding: 10px 14px;
    border: 1px solid #ddd;
    border-radius: 8px;
    font-size: 14px;
    color: #334155;
    outline: none;
    box-sizing: border-box;
}

.form-control:focus {
    border-color: #6366f1;
}

/* Buttons */
.btn-save {
    background: #16a34a;
    color: #fff;
    border: none;
    padding: 10px 24px;
    border-radius: 8px;
    font-weight: 500;
    cursor: pointer;
}

.btn-change-pass {
    background: #475569;
    color: #fff;
    border: none;
    padding: 10px 24px;
    border-radius: 8px;
    font-weight: 500;
    cursor: pointer;
}

@media (max-width: 992px) {
    .profile-container {
        flex-direction: column;
        align-items: stretch;
    }
    .profile-sidebar {
        width: 100%;
    }
}

@media (max-width: 576px) {
    .form-grid {
        grid-template-columns: 1fr;
    }
}
</style>