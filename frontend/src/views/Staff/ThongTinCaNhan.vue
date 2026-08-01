<template>
  <div class="profile-page">

    <!-- HEADER -->
    <div class="page-header">
      <div>
        <h2 class="page-header__title">Thông tin cá nhân</h2>
        <p class="page-header__desc">
          Quản lý thông tin tài khoản nhân viên
        </p>
      </div>
    </div>

    <!-- PROFILE CARD -->
    <div class="profile-card">

      <div class="avatar-section">
        <div class="avatar">
          <img
            src="https://i.pravatar.cc/200"
            alt="avatar"
          />
        </div>

        <button class="btn btn-primary">
          Đổi ảnh đại diện
        </button>
      </div>

      <div class="profile-form">

        <div class="form-row">
          <div class="form-group">
            <label>Họ và tên</label>
            <input
              type="text"
              class="form-control"
              value="Nguyễn Văn A"
            />
          </div>

          <div class="form-group">
            <label>Tên đăng nhập</label>
            <input
              type="text"
              class="form-control"
              value="staff01"
              disabled
            />
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Email</label>
            <input
              type="email"
              class="form-control"
              value="staff@gmail.com"
            />
          </div>

          <div class="form-group">
            <label>Số điện thoại</label>
            <input
              type="text"
              class="form-control"
              value="0987654321"
            />
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Mật khẩu hiện tại</label>
            <input
              type="password"
              class="form-control"
              placeholder="Nhập mật khẩu hiện tại"
            />
          </div>

          <div class="form-group">
            <label>Mật khẩu mới</label>
            <input
              type="password"
              class="form-control"
              placeholder="Nhập mật khẩu mới"
            />
          </div>
        </div>

        <div class="form-row">
          <div class="form-group full-width">
            <label>Xác nhận mật khẩu mới</label>
            <input
              type="password"
              class="form-control"
              placeholder="Nhập lại mật khẩu mới"
            />
          </div>
        </div>

        <div class="action-group">
          <button class="btn btn-ghost">
            Hủy
          </button>

          <button class="btn btn-primary">
            Cập nhật thông tin
          </button>
        </div>

      </div>

    </div>

  </div>
</template>

<script setup>
</script>

<style scoped>

.profile-page{
  width:100%;
}

.page-header{
  margin-bottom:24px;
}

.page-header__title{
  font-size:24px;
  font-weight:700;
  color:#1e293b;
}

.page-header__desc{
  font-size:14px;
  color:#64748b;
  margin-top:4px;
}

.profile-card{
  background:#fff;
  border:1px solid #e2e8f0;
  border-radius:16px;
  padding:30px;
  display:flex;
  gap:40px;
  align-items:flex-start;
}

.avatar-section{
  width:220px;
  display:flex;
  flex-direction:column;
  align-items:center;
  gap:16px;
}

.avatar{
  width:180px;
  height:180px;
  border-radius:50%;
  overflow:hidden;
  border:4px solid #f1f5f9;
}

.avatar img{
  width:100%;
  height:100%;
  object-fit:cover;
}

.profile-form{
  flex:1;
}

.form-row{
  display:flex;
  gap:20px;
  margin-bottom:18px;
}

.form-group{
  flex:1;
  display:flex;
  flex-direction:column;
}

.form-group label{
  font-size:13px;
  font-weight:600;
  color:#475569;
  margin-bottom:6px;
}

.full-width{
  width:100%;
}

.form-control{
  padding:12px 14px;
  border:1px solid #cbd5e1;
  border-radius:8px;
  outline:none;
  font-size:14px;
}

.form-control:focus{
  border-color:#22c55e;
}

.form-control:disabled{
  background:#f8fafc;
}

.action-group{
  margin-top:25px;
  display:flex;
  justify-content:flex-end;
  gap:12px;
}

.btn{
  padding:10px 20px;
  border:none;
  border-radius:8px;
  cursor:pointer;
  font-size:14px;
  font-weight:600;
}

.btn-primary{
  background:#22c55e;
  color:#fff;
}

.btn-primary:hover{
  background:#16a34a;
}

.btn-ghost{
  background:#f1f5f9;
  color:#475569;
}

.btn-ghost:hover{
  background:#e2e8f0;
}

@media(max-width:900px){

  .profile-card{
    flex-direction:column;
  }

  .avatar-section{
    width:100%;
  }

  .form-row{
    flex-direction:column;
    gap:12px;
  }

}

</style>