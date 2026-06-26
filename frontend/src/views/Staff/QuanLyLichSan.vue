<template>
  <div class="schedule-manager">

    <!-- HEADER -->
    <div class="page-header">
      <div>
        <h2 class="page-header__title">Quản lý lịch sân</h2>
        <p class="page-header__desc">
          Quản lý các lịch đặt sân trong hệ thống
        </p>
      </div>
    </div>

    <!-- FILTER -->
    <div class="filter-card">
      <div class="filter-group">

        <div class="search-box">
          <input
            type="text"
            class="form-control"
            placeholder="Tìm theo khách hàng hoặc sân..."
          />
        </div>

        <div class="filter-item">
          <input
            type="date"
            class="form-control"
          />
        </div>

        <div class="filter-item">
          <select class="form-control">
            <option>Tất cả trạng thái</option>
            <option>Chờ xác nhận</option>
            <option>Đã xác nhận</option>
            <option>Đã hủy</option>
          </select>
        </div>

      </div>
    </div>

    <!-- TABLE -->
    <div class="table-card">
      <table class="data-table">
        <thead>
          <tr>
            <th>Khách hàng</th>
            <th>Sân</th>
            <th>Thời gian</th>
            <th>Giá thuê</th>
            <th>Trạng thái</th>
            <th class="text-center">Thao tác</th>
          </tr>
        </thead>

        <tbody>

          <tr>
            <td>
              <div class="customer-info">
                <span class="customer-name">
                  Nguyễn Văn A
                </span>
                <span class="customer-phone">
                  0987654321
                </span>
              </div>
            </td>

            <td>
              <div class="field-info">
                <span class="field-name">
                  Sân 5
                </span>
                <span class="field-type-badge">
                  Sân 7 người
                </span>
              </div>
            </td>

            <td>
              <div class="time-info">
                <span class="time-slot">
                  18:00 - 20:00
                </span>
                <span class="booking-date">
                  25/06/2026
                </span>
              </div>
            </td>

            <td>
              <span class="price-text">
                600.000 VNĐ
              </span>
            </td>

            <td>
              <span class="badge badge-warning">
                Chờ xác nhận
              </span>
            </td>

            <td>
              <div class="table-actions">
                <button class="btn-action btn-success">
                  ✓
                </button>

                <button class="btn-action btn-danger">
                  ✕
                </button>

                <button class="btn-action btn-view">
                  👁
                </button>
              </div>
            </td>
          </tr>

          <tr>
            <td>
              <div class="customer-info">
                <span class="customer-name">
                  Trần Văn B
                </span>
                <span class="customer-phone">
                  0911222333
                </span>
              </div>
            </td>

            <td>
              <div class="field-info">
                <span class="field-name">
                  Sân 2
                </span>
                <span class="field-type-badge">
                  Sân 11 người
                </span>
              </div>
            </td>

            <td>
              <div class="time-info">
                <span class="time-slot">
                  20:00 - 22:00
                </span>
                <span class="booking-date">
                  25/06/2026
                </span>
              </div>
            </td>

            <td>
              <span class="price-text">
                1.000.000 VNĐ
              </span>
            </td>

            <td>
              <span class="badge badge-success">
                Đã xác nhận
              </span>
            </td>

            <td>
              <div class="table-actions">
                <button class="btn-action btn-view">
                  👁
                </button>
              </div>
            </td>
          </tr>

        </tbody>
      </table>
    </div>

  </div>
</template>

<script setup>
</script>

<style scoped>

.schedule-manager{
  width:100%;
}

.page-header{
  display:flex;
  justify-content:space-between;
  align-items:center;
  margin-bottom:24px;
}

.page-header__title{
  font-size:24px;
  font-weight:700;
  color:#1e293b;
}

.page-header__desc{
  color:#64748b;
  font-size:14px;
}

.filter-card{
  background:#fff;
  border:1px solid #e2e8f0;
  border-radius:12px;
  padding:16px;
  margin-bottom:20px;
}

.filter-group{
  display:flex;
  gap:12px;
  flex-wrap:wrap;
}

.search-box{
  flex:2;
}

.filter-item{
  flex:1;
  min-width:180px;
}

.form-control{
  width:100%;
  padding:10px 14px;
  border-radius:8px;
  border:1px solid #cbd5e1;
  outline:none;
}

.form-control:focus{
  border-color:#28a745;
}

.table-card{
  background:#fff;
  border-radius:12px;
  overflow:hidden;
  border:1px solid #e2e8f0;
}

.data-table{
  width:100%;
  border-collapse:collapse;
}

.data-table th{
  background:#f8fafc;
  padding:16px;
  text-align:left;
  color:#64748b;
  font-weight:600;
}

.data-table td{
  padding:16px;
  border-top:1px solid #e2e8f0;
}

.data-table tbody tr:hover{
  background:#f8fafc;
}

.customer-info,
.field-info,
.time-info{
  display:flex;
  flex-direction:column;
  gap:4px;
}

.customer-name,
.field-name,
.time-slot{
  font-weight:600;
}

.customer-phone,
.booking-date{
  color:#64748b;
  font-size:12px;
}

.field-type-badge{
  width:max-content;
  background:#f1f5f9;
  padding:2px 8px;
  border-radius:4px;
  font-size:11px;
}

.price-text{
  font-weight:700;
}

.badge{
  padding:5px 10px;
  border-radius:20px;
  font-size:12px;
  font-weight:600;
}

.badge-success{
  background:#dcfce7;
  color:#15803d;
}

.badge-warning{
  background:#fef3c7;
  color:#b45309;
}

.badge-danger{
  background:#fee2e2;
  color:#dc2626;
}

.table-actions{
  display:flex;
  justify-content:center;
  gap:8px;
}

.btn-action{
  width:34px;
  height:34px;
  border:none;
  border-radius:8px;
  cursor:pointer;
  transition:0.2s;
}

.btn-success{
  background:#dcfce7;
  color:#15803d;
}

.btn-danger{
  background:#fee2e2;
  color:#dc2626;
}

.btn-view{
  background:#e0f2fe;
  color:#0284c7;
}

.text-center{
  text-align:center;
}

</style>