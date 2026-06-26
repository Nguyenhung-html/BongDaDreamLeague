<template>
  <div class="customer-manager">

    <!-- HEADER -->
    <div class="page-header">
      <div>
        <h2 class="page-header__title">Quản lý khách hàng</h2>
        <p class="page-header__desc">
          Quản lý thông tin khách hàng trong hệ thống
        </p>
      </div>

      <button class="btn btn-primary">
        + Thêm khách hàng
      </button>
    </div>

    <!-- FILTER -->
    <div class="filter-card">
      <div class="filter-group">

        <div class="search-box">
          <input
            type="text"
            class="form-control"
            placeholder="Tìm kiếm tên, số điện thoại..."
          >
        </div>

        <div class="filter-item">
          <select class="form-control">
            <option>Tất cả khách hàng</option>
            <option>Hoạt động</option>
            <option>Khóa</option>
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
            <th>Email</th>
            <th>Số lần đặt</th>
            <th>Tổng chi tiêu</th>
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

            <td>nguyenvana@gmail.com</td>

            <td>
              <strong>25</strong>
            </td>

            <td>
              <span class="price-text">
                12.500.000 VNĐ
              </span>
            </td>

            <td>
              <span class="badge badge-success">
                Hoạt động
              </span>
            </td>

            <td>
              <div class="table-actions">
                <button class="btn-action btn-edit">
                  ✏️
                </button>

                <button class="btn-action btn-delete">
                  🗑️
                </button>
              </div>
            </td>
          </tr>

          <tr>
            <td>
              <div class="customer-info">
                <span class="customer-name">
                  Trần Thị B
                </span>
                <span class="customer-phone">
                  0912345678
                </span>
              </div>
            </td>

            <td>tranthib@gmail.com</td>

            <td>
              <strong>8</strong>
            </td>

            <td>
              <span class="price-text">
                3.200.000 VNĐ
              </span>
            </td>

            <td>
              <span class="badge badge-warning">
                Chờ xác thực
              </span>
            </td>

            <td>
              <div class="table-actions">
                <button class="btn-action btn-edit">
                  ✏️
                </button>

                <button class="btn-action btn-delete">
                  🗑️
                </button>
              </div>
            </td>
          </tr>

        </tbody>
      </table>
    </div>

  </div>
</template>
<style scoped>

.customer-manager {
  width: 100%;
}

/* HEADER */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header__title {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
}

.page-header__desc {
  color: #64748b;
  font-size: 14px;
}

/* FILTER */

.filter-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 20px;
  border: 1px solid #e2e8f0;
}

.filter-group {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.search-box {
  flex: 2;
}

.filter-item {
  flex: 1;
}

.form-control {
  width: 100%;
  padding: 10px 14px;
  border-radius: 8px;
  border: 1px solid #cbd5e1;
  outline: none;
}

.form-control:focus {
  border-color: #28a745;
}

/* TABLE */

.table-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #e2e8f0;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th {
  background: #f8fafc;
  color: #64748b;
  font-weight: 600;
  padding: 16px;
  text-align: left;
}

.data-table td {
  padding: 16px;
  border-top: 1px solid #e2e8f0;
}

.data-table tbody tr:hover {
  background: #f8fafc;
}

/* CUSTOMER */

.customer-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.customer-name {
  font-weight: 600;
  color: #1e293b;
}

.customer-phone {
  font-size: 12px;
  color: #64748b;
}

.price-text {
  font-weight: 700;
  color: #1e293b;
}

/* STATUS */

.badge {
  padding: 5px 10px;
  border-radius: 30px;
  font-size: 12px;
  font-weight: 600;
}

.badge-success {
  background: #dcfce7;
  color: #15803d;
}

.badge-warning {
  background: #fef3c7;
  color: #b45309;
}

.badge-danger {
  background: #fee2e2;
  color: #dc2626;
}

/* BUTTON */

.btn {
  padding: 10px 20px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  font-weight: 600;
}

.btn-primary {
  background: #28a745;
  color: white;
}

.btn-primary:hover {
  background: #218838;
}

.table-actions {
  display: flex;
  justify-content: center;
  gap: 8px;
}

.btn-action {
  width: 34px;
  height: 34px;
  border-radius: 8px;
  border: 1px solid #cbd5e1;
  background: white;
  cursor: pointer;
  transition: .2s;
}

.btn-edit:hover {
  border-color: #28a745;
  color: #28a745;
}

.btn-delete:hover {
  border-color: #ef4444;
  color: #ef4444;
}

.text-center {
  text-align: center;
}

</style>