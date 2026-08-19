<template>
  <div class="invoice-manager">

    <!-- Header -->
    <div class="page-header">
      <div>
        <h2 class="page-header__title">Xem hóa đơn</h2>
        <p class="page-header__desc">
          Danh sách hóa đơn và thông tin thanh toán
        </p>
      </div>
    </div>

    <!-- Bộ lọc -->
    <div class="filter-card">
      <div class="filter-group">

        <div class="search-box">
          <input
            type="text"
            class="form-control"
            placeholder="Tìm mã hóa đơn hoặc khách hàng..."
          />
        </div>

        <div class="filter-item">
          <select class="form-control">
            <option>Tất cả trạng thái</option>
            <option>Đã thanh toán</option>
            <option>Chưa thanh toán</option>
            <option>Đã hủy</option>
          </select>
        </div>

      </div>
    </div>

    <!-- Bảng hóa đơn -->
    <div class="table-card">
      <table class="data-table">
        <thead>
          <tr>
            <th>Mã HĐ</th>
            <th>Khách hàng</th>
            <th>Ngày đặt</th>
            <th>Tổng tiền</th>
            <th>Thanh toán</th>
            <th class="text-center">Thao tác</th>
          </tr>
        </thead>

        <tbody>

          <tr>
            <td>#HD001</td>

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

            <td>25/06/2026</td>

            <td>
              <span class="price-text">
                600.000 VNĐ
              </span>
            </td>

            <td>
              <span class="badge badge-success">
                Đã thanh toán
              </span>
            </td>

            <td>
              <div class="table-actions">

                <button class="btn-action btn-view">
                  👁
                </button>

                <button class="btn-action btn-print">
                  🖨
                </button>

                <button class="btn-action btn-download">
                  📄
                </button>

              </div>
            </td>
          </tr>

          <tr>
            <td>#HD002</td>

            <td>
              <div class="customer-info">
                <span class="customer-name">
                  Trần Văn B
                </span>
                <span class="customer-phone">
                  0912345678
                </span>
              </div>
            </td>

            <td>25/06/2026</td>

            <td>
              <span class="price-text">
                850.000 VNĐ
              </span>
            </td>

            <td>
              <span class="badge badge-warning">
                Chưa thanh toán
              </span>
            </td>

            <td>
              <div class="table-actions">

                <button class="btn-action btn-view">
                  👁
                </button>

                <button class="btn-action btn-print">
                  🖨
                </button>

                <button class="btn-action btn-download">
                  📄
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

.invoice-manager{
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
  color:#64748b;
  margin-top:4px;
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
}

.search-box{
  flex:2;
}

.filter-item{
  flex:1;
}

.form-control{
  width:100%;
  padding:10px 14px;
  border:1px solid #cbd5e1;
  border-radius:8px;
  outline:none;
}

.form-control:focus{
  border-color:#22c55e;
}

.table-card{
  background:#fff;
  border:1px solid #e2e8f0;
  border-radius:12px;
  overflow:hidden;
}

.data-table{
  width:100%;
  border-collapse:collapse;
}

.data-table th{
  background:#f8fafc;
  color:#64748b;
  font-weight:600;
  padding:16px;
  text-align:left;
}

.data-table td{
  padding:16px;
  border-top:1px solid #e2e8f0;
}

.data-table tbody tr:hover{
  background:#f8fafc;
}

.customer-info{
  display:flex;
  flex-direction:column;
  gap:3px;
}

.customer-name{
  font-weight:600;
  color:#1e293b;
}

.customer-phone{
  font-size:12px;
  color:#64748b;
}

.price-text{
  font-weight:700;
}

.badge{
  padding:4px 10px;
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

.btn-view{
  background:#dbeafe;
  color:#2563eb;
}

.btn-print{
  background:#f3f4f6;
  color:#475569;
}

.btn-download{
  background:#dcfce7;
  color:#16a34a;
}

.btn-action:hover{
  transform:scale(1.05);
}

.text-center{
  text-align:center;
}

</style>