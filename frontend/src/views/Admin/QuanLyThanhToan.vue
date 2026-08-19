<template>
  <div class="page">

    <div class="page-header">
      <div>
        <h2>Quản lý thanh toán</h2>
        <p>Đối soát toàn bộ giao dịch thanh toán trong hệ thống</p>
      </div>
    </div>

    <p v-if="loi" class="thong-bao-loi">{{ loi }}</p>

    <!-- Thẻ số liệu đối soát, lấy từ /api/admin/thanh-toan/tong-hop -->
    <div v-if="tongHop" class="cards">
      <div class="card">
        <p class="card-label">Tổng giao dịch</p>
        <p class="card-num">{{ tongHop.tongGiaoDich }}</p>
      </div>
      <div class="card">
        <p class="card-label">Thành công</p>
        <p class="card-num success-text">{{ tongHop.soThanhCong }}</p>
        <p class="card-sub">{{ tien(tongHop.tienThanhCong) }}</p>
      </div>
      <div class="card">
        <p class="card-label">Đang chờ</p>
        <p class="card-num warning-text">{{ tongHop.soDangCho }}</p>
        <p class="card-sub">{{ tien(tongHop.tienDangCho) }}</p>
      </div>
      <div class="card">
        <p class="card-label">Đã hoàn tiền</p>
        <p class="card-num danger-text">{{ tongHop.soHoanTien }}</p>
        <p class="card-sub">{{ tien(tongHop.tienHoanTien) }}</p>
      </div>
    </div>

    <div class="toolbar">

      <input
        v-model="search"
        placeholder="Tìm khách hàng, mã đơn..."
      />

      <select v-model="filterStatus">
        <option value="">Tất cả trạng thái</option>
        <option value="DANG_CHO">Đang chờ</option>
        <option value="THANH_CONG">Thành công</option>
        <option value="THAT_BAI">Thất bại</option>
        <option value="DA_HOAN_TIEN">Đã hoàn tiền</option>
      </select>

      <select v-model="filterLoai">
        <option value="">Tất cả loại</option>
        <option value="COC">Tiền cọc</option>
        <option value="THANH_TOAN_CON_LAI">Thanh toán còn lại</option>
      </select>

      <select v-model="filterPhuongThuc">
        <option value="">Tất cả phương thức</option>
        <option value="TIEN_MAT">Tiền mặt</option>
        <option value="QR">Chuyển khoản QR</option>
      </select>

    </div>

    <p v-if="loading" class="thong-bao-tai">Đang tải dữ liệu…</p>

    <table class="table">

      <thead>
        <tr>
          <th>Mã đơn</th>
          <th>Khách hàng</th>
          <th>Sân</th>
          <th>Số tiền</th>
          <th>Loại</th>
          <th>Phương thức</th>
          <th>Trạng thái</th>
          <th width="220">Thao tác</th>
        </tr>
      </thead>

      <tbody>

        <tr v-if="!loading && payments.length === 0">
          <td colspan="8" class="empty-row">Chưa có giao dịch nào.</td>
        </tr>

        <tr
          v-for="item in payments"
          :key="item.id"
        >

          <td>{{ item.maDon }}</td>

          <td>
            {{ item.tenKhach }}
            <span class="sub">{{ item.soDienThoai }}</span>
          </td>

          <td>{{ item.tenSan }}</td>

          <td>{{ tien(item.soTien) }}</td>

          <td>{{ nhanLoai(item.loai) }}</td>

          <td>{{ nhanPhuongThuc(item.phuongThuc) }}</td>

          <td>
            <span
              :class="[
                'status',
                item.trangThai === 'THANH_CONG'
                  ? 'success'
                  : item.trangThai === 'THAT_BAI' || item.trangThai === 'DA_HOAN_TIEN'
                  ? 'danger'
                  : 'warning'
              ]"
            >
              {{ nhanTrangThai(item.trangThai) }}
            </span>
          </td>

          <td>

            <button
              class="btn-edit"
              v-if="item.trangThai === 'DANG_CHO'"
              @click="xacNhan(item)"
            >
              Xác nhận
            </button>

            <button
              class="btn-delete"
              v-if="item.trangThai === 'THANH_CONG'"
              @click="hoanTien(item)"
            >
              Hoàn tiền
            </button>

            <span
              v-if="item.trangThai !== 'DANG_CHO' && item.trangThai !== 'THANH_CONG'"
              class="sub"
            >
              Không có thao tác
            </span>

          </td>

        </tr>

      </tbody>

    </table>

  </div>
</template>
<script setup>
import axios from "axios";
import { ref, onMounted, watch } from "vue";

const API_BASE = "http://localhost:8080/api/admin/thanh-toan";

function authHeader() {
  return {
    Authorization: `Bearer ${localStorage.getItem("token")}`,
  };
}

const search = ref("");
const filterStatus = ref("");
const filterLoai = ref("");
const filterPhuongThuc = ref("");

const loading = ref(false);
const loi = ref("");

const payments = ref([]);
const tongHop = ref(null);

const NHAN_TRANG_THAI = {
  DANG_CHO: "Đang chờ",
  THANH_CONG: "Thành công",
  THAT_BAI: "Thất bại",
  DA_HOAN_TIEN: "Đã hoàn tiền",
};

const NHAN_LOAI = {
  COC: "Tiền cọc",
  THANH_TOAN_CON_LAI: "Thanh toán còn lại",
};

const NHAN_PHUONG_THUC = {
  TIEN_MAT: "Tiền mặt",
  QR: "Chuyển khoản QR",
};

function nhanTrangThai(ma) {
  return NHAN_TRANG_THAI[ma] || ma;
}

function nhanLoai(ma) {
  return NHAN_LOAI[ma] || ma;
}

function nhanPhuongThuc(ma) {
  return NHAN_PHUONG_THUC[ma] || ma;
}

function tien(so) {
  return new Intl.NumberFormat("vi-VN").format(so || 0) + " đ";
}

function docLoi(e) {
  return e?.response?.data?.message || e.message || "Đã xảy ra lỗi!";
}

async function loadPayments() {
  loading.value = true;
  loi.value = "";
  try {
    const res = await axios.get(API_BASE, {
      params: {
        tuKhoa: search.value || undefined,
        trangThai: filterStatus.value || undefined,
        loai: filterLoai.value || undefined,
        phuongThuc: filterPhuongThuc.value || undefined,
      },
      headers: authHeader(),
    });
    payments.value = res.data;
  } catch (e) {
    loi.value = docLoi(e);
    payments.value = [];
  } finally {
    loading.value = false;
  }
}

async function loadTongHop() {
  try {
    const res = await axios.get(`${API_BASE}/tong-hop`, { headers: authHeader() });
    tongHop.value = res.data;
  } catch (e) {
    console.error("Không tải được số liệu đối soát:", docLoi(e));
  }
}

onMounted(() => {
  loadPayments();
  loadTongHop();
});

let timer = null;
watch([search, filterStatus, filterLoai, filterPhuongThuc], () => {
  clearTimeout(timer);
  timer = setTimeout(loadPayments, 300);
});

/** Xác nhận đã nhận được tiền của giao dịch đang chờ */
async function xacNhan(item) {
  if (!confirm(`Xác nhận đã nhận ${tien(item.soTien)} của đơn ${item.maDon}?`)) return;
  try {
    await axios.put(`${API_BASE}/${item.id}/xac-nhan`, {}, { headers: authHeader() });
    await Promise.all([loadPayments(), loadTongHop()]);
  } catch (e) {
    alert(docLoi(e));
  }
}

/** Hoàn tiền cho giao dịch đã thành công */
async function hoanTien(item) {
  if (!confirm(`Hoàn ${tien(item.soTien)} cho đơn ${item.maDon}?`)) return;
  try {
    await axios.put(`${API_BASE}/${item.id}/hoan-tien`, {}, { headers: authHeader() });
    await Promise.all([loadPayments(), loadTongHop()]);
  } catch (e) {
    alert(docLoi(e));
  }
}
</script>
<style scoped>
/* ===== THẺ SỐ LIỆU ĐỐI SOÁT ===== */
.cards{
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
    gap: 16px;
    margin-bottom: 22px;
}

.card{
    background: #fff;
    border: 1px solid #e2e8f0;
    border-radius: 12px;
    padding: 18px;
}

.card-label{
    font-size: 13px;
    color: #64748b;
    margin: 0 0 6px 0;
}

.card-num{
    font-size: 26px;
    font-weight: 700;
    margin: 0;
    color: #1e293b;
}

.card-sub{
    font-size: 13px;
    color: #64748b;
    margin: 4px 0 0 0;
}

.success-text{ color: #16a34a; }
.warning-text{ color: #d97706; }
.danger-text{ color: #dc2626; }

/* ===== NÚT THAO TÁC ===== */
.btn-edit,
.btn-delete{
    padding: 6px 12px;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    font-size: 13px;
    margin-right: 6px;
    color: #fff;
}

.btn-edit{ background: #16a34a; }
.btn-delete{ background: #dc2626; }

/* ===== TRẠNG THÁI TẢI / LỖI ===== */
.thong-bao-loi{
    color: #b91c1c;
    background: #fef2f2;
    border: 1px solid #fecaca;
    border-radius: 8px;
    padding: 10px 14px;
    margin-bottom: 16px;
}

.thong-bao-tai{
    color: #64748b;
    margin-bottom: 12px;
}

.sub{
    display: block;
    font-size: 12px;
    color: #94a3b8;
}

.empty-row{
    text-align: center;
    color: #94a3b8;
    padding: 24px 0;
}

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