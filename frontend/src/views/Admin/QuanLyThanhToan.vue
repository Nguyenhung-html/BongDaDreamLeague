<template>
  <div class="page">

    <div class="page-header">
      <div>
        <h2>Quản lý thanh toán</h2>
        <p>Quản lý các hóa đơn thanh toán trong hệ thống</p>
      </div>

      <button class="btn-add" @click="openAdd">
        + Thêm thanh toán
      </button>
    </div>

    <div class="toolbar">

      <input
        v-model="search"
        placeholder="Tìm khách hàng..."
      />

      <select v-model="filterStatus">
        <option value="">Tất cả trạng thái</option>
        <option>Đã thanh toán</option>
        <option>Chưa thanh toán</option>
        <option>Đã hủy</option>
      </select>

    </div>

    <table class="table">

      <thead>
        <tr>
          <th>Mã</th>
          <th>Khách hàng</th>
          <th>Số tiền</th>
          <th>Phương thức</th>
          <th>Trạng thái</th>
          <th width="220">Thao tác</th>
        </tr>
      </thead>

      <tbody>

        <tr
          v-for="item in filteredPayments"
          :key="item.id"
        >

          <td>{{ item.id }}</td>

          <td>{{ item.khachHang }}</td>

          <td>{{ item.soTien.toLocaleString() }} đ</td>

          <td>{{ item.phuongThuc }}</td>

          <td>

            <span
              :class="[
                'status',
                item.trangThai=='Đã thanh toán'
                  ? 'success'
                  : item.trangThai=='Đã hủy'
                  ? 'danger'
                  : 'warning'
              ]"
            >
              {{ item.trangThai }}
            </span>

          </td>

          <td>

            <button
              class="btn-edit"
              @click="openEdit(item)"
            >
              Sửa
            </button>

            <button
              class="btn-delete"
              @click="removePayment(item.id)"
            >
              Xóa
            </button>

          </td>

        </tr>

      </tbody>

    </table>

  </div>
</template>
<script setup>
import { ref, computed } from "vue";

const search = ref("");
const filterStatus = ref("");

const showModal = ref(false);
const isEdit = ref(false);

const form = ref({
  id: null,
  khachHang: "",
  soTien: 0,
  phuongThuc: "Tiền mặt",
  trangThai: "Chưa thanh toán",
});

const payments = ref([
  {
    id: 1,
    khachHang: "Nguyễn Văn A",
    soTien: 500000,
    phuongThuc: "Tiền mặt",
    trangThai: "Đã thanh toán",
  },
  {
    id: 2,
    khachHang: "Trần Văn B",
    soTien: 700000,
    phuongThuc: "Chuyển khoản",
    trangThai: "Chưa thanh toán",
  },
  {
    id: 3,
    khachHang: "Lê Văn C",
    soTien: 450000,
    phuongThuc: "Ví điện tử",
    trangThai: "Đã hủy",
  },
]);

const filteredPayments = computed(() => {
  return payments.value.filter((item) => {
    const matchName = item.khachHang
      .toLowerCase()
      .includes(search.value.toLowerCase());

    const matchStatus =
      filterStatus.value === "" ||
      item.trangThai === filterStatus.value;

    return matchName && matchStatus;
  });
});

function openAdd() {
  isEdit.value = false;

  form.value = {
    id: null,
    khachHang: "",
    soTien: 0,
    phuongThuc: "Tiền mặt",
    trangThai: "Chưa thanh toán",
  };

  showModal.value = true;
}

function openEdit(item) {
  isEdit.value = true;
  form.value = { ...item };
  showModal.value = true;
}

function savePayment() {
  if (isEdit.value) {
    const index = payments.value.findIndex(
      (x) => x.id === form.value.id
    );

    payments.value[index] = { ...form.value };
  } else {
    payments.value.push({
      ...form.value,
      id: Date.now(),
    });
  }

  showModal.value = false;
}

function removePayment(id) {
  if (confirm("Bạn có chắc muốn xóa thanh toán này?")) {
    payments.value = payments.value.filter(
      (x) => x.id !== id
    );
  }
}
</script>
<style scoped>
.page{
  padding:24px;
}

.page-header{
  display:flex;
  justify-content:space-between;
  align-items:center;
  margin-bottom:20px;
}

.page-header h2{
  margin:0;
  color:#1e293b;
}

.page-header p{
  margin-top:5px;
  color:#64748b;
}

.toolbar{
  display:flex;
  gap:15px;
  margin-bottom:20px;
}

.toolbar input,
.toolbar select{
  padding:10px;
  border:1px solid #ddd;
  border-radius:8px;
}

.table{
  width:100%;
  border-collapse:collapse;
  background:#fff;
}

.table th,
.table td{
  padding:14px;
  border-bottom:1px solid #eee;
  text-align:left;
}

.table th{
  background:#f8fafc;
}

.success{
  color:#16a34a;
  font-weight:bold;
}

.warning{
  color:#f59e0b;
  font-weight:bold;
}

.danger{
  color:#dc2626;
  font-weight:bold;
}

.btn-add{
  background:#16a34a;
  color:#fff;
  border:none;
  padding:10px 18px;
  border-radius:8px;
  cursor:pointer;
}

.btn-view{
  background:#2563eb;
  color:#fff;
  border:none;
  padding:7px 12px;
  border-radius:6px;
  cursor:pointer;
  margin-right:6px;
}

.btn-pay{
  background:#16a34a;
  color:#fff;
  border:none;
  padding:7px 12px;
  border-radius:6px;
  cursor:pointer;
}

.modal{
  position:fixed;
  inset:0;
  background:rgba(0,0,0,.5);
  display:flex;
  align-items:center;
  justify-content:center;
}

.modal-box{
  width:420px;
  background:#fff;
  padding:25px;
  border-radius:12px;
}

.modal-box h3{
  margin-top:0;
}

.modal-box p{
  margin:10px 0;
}

.modal-action{
  margin-top:20px;
  text-align:right;
}

.btn-close{
  background:#64748b;
  color:#fff;
  border:none;
  padding:8px 18px;
  border-radius:8px;
  cursor:pointer;
}
</style>