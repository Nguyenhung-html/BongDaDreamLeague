<template>
  <div class="booking-page">
    <div class="container">
      <div class="page-header">
        <h1>Đặt sân bóng</h1>
        <p>Chọn thời gian phù hợp để đặt sân nhanh chóng</p>
      </div>

      <div class="booking-layout">
        <!-- Thông tin sân -->
        <div class="field-info">
          <img
            src="https://images.unsplash.com/photo-1574629810360-7efbbe195018"
            alt="Sân bóng"
          />

          <div class="field-content">
            <h2>Sân Dream League</h2>

            <div class="detail">
              📍 Quận 12, TP.HCM
            </div>

            <div class="detail">
              ⭐ 4.9 / 5
            </div>

            <div class="detail">
              ⚽ Sân 7 người
            </div>

            <div class="price">
              300.000 VNĐ / giờ
            </div>
          </div>
        </div>

        <!-- Form đặt sân -->
        <div class="booking-card">
          <h3>Thông tin đặt sân</h3>

          <div class="form-group">
            <label>Ngày đặt</label>
            <input type="date" />
          </div>

          <div class="form-group">
            <label>Khung giờ</label>
            <select>
              <option>06:00 - 07:00</option>
              <option>07:00 - 08:00</option>
              <option>08:00 - 09:00</option>
              <option>17:00 - 18:00</option>
              <option>18:00 - 19:00</option>
              <option>19:00 - 20:00</option>
            </select>
          </div>

          <div class="form-group">
            <label>Họ và tên</label>
            <input type="text" placeholder="Nhập họ tên" />
          </div>

          <div class="form-group">
            <label>Số điện thoại</label>
            <input type="text" placeholder="Nhập số điện thoại" />
          </div>

          <div class="summary">
            <div>
              <span>Giá thuê:</span>
              <strong>300.000 VNĐ</strong>
            </div>

            <div>
              <span>Phí dịch vụ:</span>
              <strong>0 VNĐ</strong>
            </div>

            <div class="total">
              <span>Tổng cộng:</span>
              <strong>300.000 VNĐ</strong>
            </div>
          </div>

          <button class="btn-book">
            Xác nhận đặt sân
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.booking-page {
  background: #f8fafc;
  min-height: calc(100vh - 76px);
  padding: 60px 0;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
}

.page-header h1 {
  font-size: 38px;
  font-weight: 800;
  color: var(--navy-900);
}

.page-header p {
  margin-top: 10px;
  color: var(--gray-600);
}

.booking-layout {
  display: grid;
  grid-template-columns: 1.1fr 0.9fr;
  gap: 30px;
}

.field-info,
.booking-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(10, 37, 64, 0.08);
}

.field-info img {
  width: 100%;
  height: 320px;
  object-fit: cover;
}

.field-content {
  padding: 24px;
}

.field-content h2 {
  color: var(--navy-900);
  margin-bottom: 15px;
}

.detail {
  margin-bottom: 10px;
  color: var(--gray-600);
}

.price {
  margin-top: 20px;
  color: var(--green-600);
  font-size: 28px;
  font-weight: 700;
}

.booking-card {
  padding: 28px;
}

.booking-card h3 {
  color: var(--navy-900);
  margin-bottom: 24px;
}

.form-group {
  margin-bottom: 18px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: var(--navy-900);
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 13px 14px;
  border: 1px solid #dbe3ea;
  border-radius: 12px;
  outline: none;
  font-size: 15px;
}

.form-group input:focus,
.form-group select:focus {
  border-color: var(--green-600);
}

.summary {
  margin-top: 25px;
  padding-top: 20px;
  border-top: 1px solid #edf2f7;
}

.summary div {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
}

.total {
  font-size: 20px;
  font-weight: 700;
  color: var(--green-600);
}

.btn-book {
  width: 100%;
  margin-top: 15px;
  border: none;
  padding: 15px;
  border-radius: 14px;
  background: linear-gradient(
    135deg,
    var(--green-500),
    var(--green-600)
  );
  color: white;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: .3s;
}

.btn-book:hover {
  transform: translateY(-2px);
}

@media (max-width: 900px) {
  .booking-layout {
    grid-template-columns: 1fr;
  }

  .field-info img {
    height: 250px;
  }

  .page-header h1 {
    font-size: 30px;
  }
}
</style>