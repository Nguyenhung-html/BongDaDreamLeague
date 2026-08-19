<template>
  <div>
    <h1 class="dashboard__page-title">
        Quản lý đặt sân
    </h1>

    <p class="dashboard__page-desc">
        Quản lý tất cả đơn đặt sân của khách hàng.
    </p>

    <div class="toolbar">
        <input
            v-model="keyword"
            class="search-box"
            placeholder="Tìm khách hàng..."
        />

        <select
            v-model="statusFilter"
            class="filter-box"
        >
            <option value="">Tất cả trạng thái</option>
            <option value="CHO_XAC_NHAN">Chờ xác nhận</option>
            <option value="DA_COC">Đã cọc</option>
            <option value="HOAN_THANH">Hoàn thành</option>
            <option value="DA_HUY">Đã hủy</option>
        </select>

        <button
            class="btn-add"
            @click="openAdd"
        >
            + Thêm đặt sân
        </button>
    </div>

    <p v-if="loi" class="thong-bao-loi">{{ loi }}</p>
    <p v-if="loading" class="thong-bao-tai">Đang tải dữ liệu…</p>

    <div class="table-wrapper">
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Khách hàng</th>
                    <th>Sân</th>
                    <th>Ngày</th>
                    <th>Khung giờ</th>
                    <th>Tổng tiền</th>
                    <th>Trạng thái</th>
                    <th width="330">Thao tác</th>
                </tr>
            </thead>
            <tbody>
                <tr
                    v-for="item in bookings"
                    :key="item.id"
                >
                    <td>{{ item.maDon }}</td>
                    <td>{{ item.hoTenDat }}</td>
                    <td>{{ item.tenSan }}</td>
                    <td>{{ dinhDangNgay(item.ngayDa) }}</td>
                    <td>{{ item.gioBatDau }} - {{ item.gioKetThuc }}</td>
                    <td>{{ dinhDangTien(item.tongTien) }}</td>
                    <td>
                        <span
                            class="badge waiting"
                            v-if="item.trangThai == 'CHO_XAC_NHAN'"
                        >
                            Chờ xác nhận
                        </span>
                        <span
                            class="badge success"
                            v-else-if="item.trangThai == 'DA_COC'"
                        >
                            Đã cọc
                        </span>
                        <span
                            class="badge done"
                            v-else-if="item.trangThai == 'HOAN_THANH'"
                        >
                            Hoàn thành
                        </span>
                        <span
                            class="badge cancel"
                            v-else
                        >
                            Đã hủy
                        </span>
                    </td>
                    <td>
                        <button
                            class="btn-detail"
                            @click="viewBooking(item)"
                        >
                            Chi tiết
                        </button>

                        <button
                            class="btn-confirm"
                            v-if="item.trangThai == 'CHO_XAC_NHAN'"
                            @click="confirmBooking(item)"
                        >
                            Xác nhận
                        </button>

                        <button
                            class="btn-finish"
                            v-if="item.trangThai == 'DA_COC'"
                            @click="finishBooking(item)"
                        >
                            Hoàn thành
                        </button>

                        <button
                            class="btn-cancel"
                            v-if="item.trangThai != 'HOAN_THANH' && item.trangThai != 'DA_HUY'"
                            @click="cancelBooking(item)"
                        >
                            Hủy
                        </button>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>

    <div
        v-if="showModal"
        class="modal-overlay"
    >
        <div class="modal">
            <h2>Thêm đặt sân tại quầy</h2>

            <p v-if="loiForm" class="thong-bao-loi">{{ loiForm }}</p>

            <input
                v-model="form.hoTenDat"
                placeholder="Tên khách hàng"
            >
            <input
                v-model="form.soDienThoai"
                placeholder="Số điện thoại"
            >
            <select v-model="form.sanBongId">
                <option value="">-- Chọn sân --</option>
                <option
                    v-for="s in danhSachSan"
                    :key="s.id"
                    :value="s.id"
                >
                    {{ s.tenSan }}
                </option>
            </select>
            <input
                type="date"
                v-model="form.ngayDa"
            >
            <input
                type="time"
                v-model="form.gioBatDau"
            >
            <input
                type="time"
                v-model="form.gioKetThuc"
            >
            <select v-model="form.phuongThuc">
                <option value="TIEN_MAT">Tiền mặt</option>
                <option value="QR">Chuyển khoản QR</option>
            </select>
            <p class="ghi-chu-form">Tổng tiền và tiền cọc do hệ thống tự tính theo bảng giá của sân.</p>

            <div class="modal-action">
                <button
                    class="btn-save"
                    :disabled="dangLuu"
                    @click="saveBooking"
                >
                    {{ dangLuu ? 'Đang lưu…' : 'Lưu' }}
                </button>
                <button
                    class="btn-close"
                    @click="showModal = false"
                >
                    Hủy
                </button>
            </div>
        </div>
    </div>

    <div
        v-if="showDetail"
        class="modal-overlay"
    >
        <div class="modal">
            <h2>Chi tiết đặt sân</h2>

            <template v-if="chiTiet">
                <p><b>Mã đơn:</b> {{ chiTiet.thongTinDon.maDon }}</p>
                <p><b>Khách hàng:</b> {{ chiTiet.thongTinDon.hoTenDat }}</p>
                <p><b>SĐT:</b> {{ chiTiet.thongTinDon.soDienThoai }}</p>
                <p><b>Sân:</b> {{ chiTiet.thongTinDon.tenSan }} (sân {{ chiTiet.thongTinDon.loaiSan }} người)</p>
                <p><b>Ngày:</b> {{ dinhDangNgay(chiTiet.thongTinDon.ngayDa) }}</p>
                <p><b>Khung giờ:</b> {{ chiTiet.thongTinDon.gioBatDau }} - {{ chiTiet.thongTinDon.gioKetThuc }}</p>
                <p><b>Trạng thái:</b> {{ nhanTrangThai(chiTiet.thongTinDon.trangThai) }}</p>

                <p v-if="chiTiet.danhSachDichVu && chiTiet.danhSachDichVu.length">
                    <b>Dịch vụ kèm theo:</b>
                    {{ chiTiet.danhSachDichVu.length }} mục — {{ dinhDangTien(chiTiet.tongTienDichVu) }}
                </p>
                <p v-if="chiTiet.danhSachGiaHan && chiTiet.danhSachGiaHan.length">
                    <b>Số lần gia hạn giờ:</b> {{ chiTiet.danhSachGiaHan.length }}
                </p>

                <hr>
                <p><b>Tổng phải thu:</b> {{ dinhDangTien(chiTiet.tongPhaiThu) }}</p>
                <p><b>Đã thanh toán:</b> {{ dinhDangTien(chiTiet.daThanhToan) }}</p>
                <p><b>Còn lại:</b> {{ dinhDangTien(chiTiet.conLai) }}</p>
            </template>
            <p v-else>Đang tải chi tiết…</p>

            <div class="modal-action">
                <button
                    class="btn-close"
                    @click="showDetail = false"
                >
                    Đóng
                </button>
            </div>
        </div>
    </div>
  </div>
</template>

<script setup>
import axios from "axios"
import { ref, onMounted, watch } from "vue"

const API_BASE = "http://localhost:8080/api/admin/dat-san"
const API_SAN = "http://localhost:8080/api/san-bong"
// Backend chưa có API tạo đơn ở khu vực admin, nên đặt hộ tại quầy dùng chung
// endpoint walk-in của staff.
const API_DAT_HO = "http://localhost:8080/api/staff/dat-san"

function authHeader() {
    return {
        Authorization: `Bearer ${localStorage.getItem("token")}`
    }
}

const keyword = ref("")
const statusFilter = ref("")

const loading = ref(false)
const dangLuu = ref(false)
const loi = ref("")
const loiForm = ref("")

const bookings = ref([])
const danhSachSan = ref([])

const showModal = ref(false)
const showDetail = ref(false)
const chiTiet = ref(null)

const form = ref({
    hoTenDat: "",
    soDienThoai: "",
    sanBongId: "",
    ngayDa: "",
    gioBatDau: "",
    gioKetThuc: "",
    phuongThuc: "TIEN_MAT"
})

const NHAN_TRANG_THAI = {
    CHO_XAC_NHAN: "Chờ xác nhận",
    DA_COC: "Đã cọc",
    HOAN_THANH: "Hoàn thành",
    DA_HUY: "Đã hủy"
}

function nhanTrangThai(ma) {
    return NHAN_TRANG_THAI[ma] || ma
}

function dinhDangTien(so) {
    return new Intl.NumberFormat("vi-VN").format(so || 0) + "đ"
}

/** Backend trả ngày dạng YYYY-MM-DD, hiển thị sang DD/MM/YYYY */
function dinhDangNgay(ngay) {
    if (!ngay) return ""
    const [y, m, d] = ngay.split("-")
    return `${d}/${m}/${y}`
}

function docLoi(e) {
    return e?.response?.data?.message || e.message || "Đã xảy ra lỗi!"
}

/** Tải danh sách đơn — lọc phía server để đúng dữ liệu toàn hệ thống */
async function loadBookings() {
    loading.value = true
    loi.value = ""
    try {
        const res = await axios.get(API_BASE, {
            params: {
                tuKhoa: keyword.value || undefined,
                trangThai: statusFilter.value || undefined
            },
            headers: authHeader()
        })
        bookings.value = res.data
    } catch (e) {
        loi.value = docLoi(e)
        bookings.value = []
    } finally {
        loading.value = false
    }
}

/** Danh sách sân cho dropdown khi đặt hộ tại quầy */
async function loadDanhSachSan() {
    try {
        const res = await axios.get(API_SAN)
        danhSachSan.value = res.data
    } catch (e) {
        // Không chặn màn hình chính nếu chỉ lỗi dropdown
        console.error("Không tải được danh sách sân:", docLoi(e))
    }
}

onMounted(() => {
    loadBookings()
    loadDanhSachSan()
})

// Gõ tìm kiếm / đổi bộ lọc thì tải lại từ server
let timer = null
watch([keyword, statusFilter], () => {
    clearTimeout(timer)
    timer = setTimeout(loadBookings, 300)
})

function openAdd() {
    loiForm.value = ""
    form.value = {
        hoTenDat: "",
        soDienThoai: "",
        sanBongId: "",
        ngayDa: "",
        gioBatDau: "",
        gioKetThuc: "",
        phuongThuc: "TIEN_MAT"
    }
    showModal.value = true
}

/** Đặt sân hộ khách tại quầy */
async function saveBooking() {
    loiForm.value = ""
    const f = form.value
    if (!f.hoTenDat || !f.soDienThoai || !f.sanBongId || !f.ngayDa || !f.gioBatDau || !f.gioKetThuc) {
        loiForm.value = "Vui lòng nhập đủ thông tin khách, sân, ngày và khung giờ."
        return
    }
    if (f.gioBatDau >= f.gioKetThuc) {
        loiForm.value = "Giờ kết thúc phải sau giờ bắt đầu."
        return
    }

    dangLuu.value = true
    try {
        await axios.post(API_DAT_HO, f, { headers: authHeader() })
        showModal.value = false
        await loadBookings()
    } catch (e) {
        loiForm.value = docLoi(e)
    } finally {
        dangLuu.value = false
    }
}

/** Gọi 1 hành động đổi trạng thái rồi tải lại danh sách */
async function doiTrangThai(item, duongDan, body) {
    try {
        await axios.put(`${API_BASE}/${item.id}/${duongDan}`, body || {}, { headers: authHeader() })
        await loadBookings()
    } catch (e) {
        alert(docLoi(e))
    }
}

function confirmBooking(item) {
    doiTrangThai(item, "xac-nhan")
}

function finishBooking(item) {
    doiTrangThai(item, "hoan-thanh")
}

function cancelBooking(item) {
    const lyDo = prompt("Lý do huỷ đơn:", "")
    if (lyDo === null) return
    doiTrangThai(item, "huy", { lyDo })
}

/** Xem chi tiết đơn (kèm dịch vụ, gia hạn, thanh toán) */
async function viewBooking(item) {
    chiTiet.value = null
    showDetail.value = true
    try {
        const res = await axios.get(`${API_BASE}/${item.id}`, { headers: authHeader() })
        chiTiet.value = res.data
    } catch (e) {
        showDetail.value = false
        alert(docLoi(e))
    }
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

.toolbar {
    display: flex;
    gap: 12px;
    margin-bottom: 20px;
    flex-wrap: wrap;
}

.search-box,
.filter-box {
    padding: 10px 14px;
    border: 1px solid #ddd;
    border-radius: 8px;
    min-width: 220px;
    box-sizing: border-box;
}

.btn-add {
    background: #16a34a;
    color: #fff;
    border: none;
    padding: 10px 18px;
    border-radius: 8px;
    cursor: pointer;
}

.table-wrapper {
    background: #fff;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 2px 10px rgba(0,0,0,.08);
}

table {
    width: 100%;
    border-collapse: collapse;
}

th, td {
    padding: 15px;
    border-bottom: 1px solid #eee;
    text-align: left;
}

th {
    background: #f8fafc;
}

.badge {
    color: #fff;
    padding: 5px 12px;
    border-radius: 20px;
    font-size: 13px;
    display: inline-block;
}

.waiting {
    background: #f59e0b;
}

.success {
    background: #16a34a;
}

.done {
    background: #2563eb;
}

.cancel {
    background: #dc2626;
}

button {
    cursor: pointer;
}

.btn-detail {
    background: #6366f1;
    color: #fff;
    border: none;
    padding: 6px 12px;
    border-radius: 6px;
    margin-right: 6px;
}

.btn-edit {
    background: #2563eb;
    color: white;
    border: none;
    padding: 6px 12px;
    border-radius: 6px;
    margin-right: 6px;
}

.btn-confirm {
    background: #16a34a;
    color: #fff;
    border: none;
    padding: 6px 12px;
    border-radius: 6px;
    margin-right: 6px;
}

.btn-finish {
    background: #0f766e;
    color: #fff;
    border: none;
    padding: 6px 12px;
    border-radius: 6px;
    margin-right: 6px;
}

.btn-cancel {
    background: #f59e0b;
    color: #fff;
    border: none;
    padding: 6px 12px;
    border-radius: 6px;
    margin-right: 6px;
}

.btn-delete {
    background: #dc2626;
    color: #fff;
    border: none;
    padding: 6px 12px;
    border-radius: 6px;
}

.modal-overlay {
    position: fixed;
    inset: 0;
    background: rgba(0,0,0,.45);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
}

.modal {
    width: 520px;
    background: #fff;
    border-radius: 12px;
    padding: 25px;
    box-sizing: border-box;
}

.modal h2 {
    margin-bottom: 18px;
}

.modal input,
.modal textarea,
.modal select {
    width: 100%;
    padding: 10px;
    margin-bottom: 12px;
    border: 1px solid #ddd;
    border-radius: 8px;
    box-sizing: border-box;
}

.modal textarea {
    height: 90px;
    resize: none;
}

/* ===== TRẠNG THÁI TẢI / LỖI ===== */
.thong-bao-loi {
    color: #b91c1c;
    background: #fef2f2;
    border: 1px solid #fecaca;
    border-radius: 8px;
    padding: 10px 14px;
    margin-bottom: 16px;
}

.thong-bao-tai {
    color: #64748b;
    margin-bottom: 16px;
}

.ghi-chu-form {
    font-size: 13px;
    color: #64748b;
    margin: 0 0 12px 0;
}

.btn-save:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

.modal-action {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
}

.btn-save {
    background: #16a34a;
    color: #fff;
    border: none;
    padding: 10px 20px;
    border-radius: 8px;
}

.btn-close {
    background: #64748b;
    color: #fff;
    border: none;
    padding: 10px 20px;
    border-radius: 8px;
}

@media(max-width: 768px) {
    .table-wrapper {
        overflow: auto;
    }
    .toolbar {
        flex-direction: column;
    }
    .search-box,
    .filter-box,
    .btn-add {
        width: 100%;
    }
}
</style>