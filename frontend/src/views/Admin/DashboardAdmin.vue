<template>
  <div>
    <h1 class="dashboard__page-title">Dashboard</h1>
    <p class="dashboard__page-desc">Tổng quan về các hoạt động quản lý của hệ thống DreamLeague.</p>

    <div class="dashboard-grid">
      <div class="dashboard-card">
        <div class="dashboard-card__icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
            <circle cx="8" cy="8" r="3.5" stroke="currentColor" stroke-width="1.6" />
            <path d="M2 18c0-2.21 2.69-4 6-4s6 1.79 6 4" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
            <path d="M16 8c0 2.21 1.34 4 3 4s3-1.79 3-4" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
            <path d="M16 14c-1.8 0-3.48.4-4.66 1.12" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
          </svg>
        </div>
        <div>
          <p class="dashboard-card__label">Tổng khách hàng</p>
          <p class="dashboard-card__num">2,850</p>
          <p class="dashboard-card__trend">↑ 12% <span class="trend-text">so với tuần trước</span></p>
        </div>
      </div>

      <div class="dashboard-card">
        <div class="dashboard-card__icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
            <rect x="4" y="3" width="16" height="16" rx="1" stroke="currentColor" stroke-width="1.6" />
            <path d="M4 8.5h16M9 14h2M15 14h2" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
          </svg>
        </div>
        <div>
          <p class="dashboard-card__label">Đặt sân hôm nay</p>
          <p class="dashboard-card__num">48</p>
          <p class="dashboard-card__trend">↑ 8% <span class="trend-text">so với hôm qua</span></p>
        </div>
      </div>

      <div class="dashboard-card">
        <div class="dashboard-card__icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
            <rect x="2" y="5" width="20" height="14" rx="1.5" stroke="currentColor" stroke-width="1.6" />
            <path d="M2 9.5h20" stroke="currentColor" stroke-width="1.6" />
          </svg>
        </div>
        <div>
          <p class="dashboard-card__label">Doanh thu hôm nay</p>
          <p class="dashboard-card__num">28.5M</p>
          <p class="dashboard-card__trend">↑ 15% <span class="trend-text">so với hôm qua</span></p>
        </div>
      </div>
    </div>

    <div class="empty-state" style="margin-top: 32px;">
      <div class="empty-state__icon">
        <svg width="32" height="32" viewBox="0 0 24 24" fill="none">
          <rect x="3" y="3" width="8" height="8" rx="1" stroke="currentColor" stroke-width="1.6" />
          <rect x="13" y="3" width="8" height="8" rx="1" stroke="currentColor" stroke-width="1.6" />
          <rect x="3" y="13" width="8" height="8" rx="1" stroke="currentColor" stroke-width="1.6" />
          <rect x="13" y="13" width="8" height="8" rx="1" stroke="currentColor" stroke-width="1.6" />
        </svg>
      </div>
      <h2 class="empty-state__title">Dashboard đang được phát triển</h2>
      <p class="empty-state__desc">
        Biểu đồ chi tiết, báo cáo phân tích, và các widget thống kê sẽ sớm được thêm vào. Hãy quay lại sau để xem nhiều tính năng hơn!
      </p>
      <div class="empty-state__actions">
        <router-link to="/admin/dat-san" class="btn btn-primary">Xem đặt sân</router-link>
        <router-link to="/admin/doanh-thu" class="btn btn-ghost-gray">Xem doanh thu</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
// Dashboard Admin Light Mode Component
</script>

<style scoped>
/* ===== TIÊU ĐỀ TRANG TỐI MÀU (NỀN SÁNG) ===== */
.dashboard__page-title {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b; /* Chữ tối màu nổi bật trên nền xám trắng */
  margin: 0 0 6px 0;
}

.dashboard__page-desc {
  font-size: 14px;
  color: #64748b; /* Chữ phụ xám */
  margin: 0 0 28px 0;
}

/* ===== GRID THỐNG KÊ (3 CỘT) ===== */
.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(1, 1fr);
  gap: 20px;
}

@media (min-width: 768px) {
  .dashboard-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

/* ===== CARD THỐNG KÊ (NỀN TRẮNG TINH) ===== */
.dashboard-card {
  background-color: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: flex-start;
  gap: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

/* Biểu tượng màu xanh cỏ tươi mát */
.dashboard-card__icon {
  width: 44px;
  height: 44px;
  border-radius: 8px;
  background-color: rgba(22, 163, 74, 0.1); /* Xanh lá mờ nhạt */
  color: #16a34a; /* Khớp với mã màu --green-600 của layout mới */
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.dashboard-card__label {
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
  margin: 0 0 4px 0;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.dashboard-card__num {
  font-size: 26px;
  font-weight: 700;
  color: #0f172a; /* Màu chữ chính thẫm */
  margin: 0 0 4px 0;
  letter-spacing: -0.5px;
}

.dashboard-card__trend {
  font-size: 13px;
  color: #16a34a; /* Chỉ số tăng trưởng màu xanh lá cây */
  font-weight: 600;
  margin: 0;
}

.trend-text {
  color: #94a3b8;
  font-weight: 400;
}

/* ===== TRẠNG THÁI TRỐNG (EMPTY STATE NỀN TRẮNG) ===== */
.empty-state {
  background-color: #ffffff;
  border: 1px dashed #cbd5e1;
  border-radius: 16px;
  padding: 48px 24px;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.empty-state__icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background-color: #f1f5f9;
  color: #94a3b8;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}

.empty-state__title {
  font-size: 18px;
  font-weight: 600;
  color: #0f172a;
  margin: 0 0 10px 0;
}

.empty-state__desc {
  font-size: 14px;
  color: #64748b;
  max-width: 460px;
  line-height: 1.6;
  margin: 0 0 24px 0;
}

/* ===== HỆ THỐNG NÚT BẤM (BUTTONS) ===== */
.empty-state__actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 10px 22px;
  font-size: 14px;
  font-weight: 600;
  border-radius: 20px;
  text-decoration: none;
  cursor: pointer;
  transition: all 0.2s ease;
}

/* Nút Xanh lá chủ đạo của hệ thống */
.btn-primary {
  background-color: #16a34a;
  color: #ffffff;
  border: none;
}

.btn-primary:hover {
  background-color: #15803d;
  box-shadow: 0 4px 12px rgba(22, 163, 74, 0.2);
}

/* Nút phụ Viền xám nhạt */
.btn-ghost-gray {
  background-color: transparent;
  color: #475569;
  border: 1px solid #cbd5e1;
}

.btn-ghost-gray:hover {
  color: #0f172a;
  border-color: #94a3b8;
  background-color: #f8fafc;
}
</style>