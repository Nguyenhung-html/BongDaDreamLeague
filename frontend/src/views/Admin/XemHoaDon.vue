<template>
  <div>
    <h1 class="dashboard__page-title">
        Quản lý hóa đơn
    </h1>

    <p class="dashboard__page-desc">
        Theo dõi trạng thái thanh toán, xuất và in hóa đơn cho khách hàng.
    </p>

    <div class="toolbar">
        <input
            v-model="keyword"
            class="search-box"
            placeholder="Tìm mã HD, khách hàng..."
        />

        <select
            v-model="paymentFilter"
            class="filter-box"
        >
            <option value="">Tất cả trạng thái</option>
            <option value="CHUA_THANH_TOAN">Chưa thanh toán</option>
            <option value="THANH_TOAN_MOT_PHAN">Thanh toán một phần</option>
            <option value="DA_THANH_TOAN_DU">Đã thanh toán đủ</option>
        </select>
    </div>

    <p v-if="loi" class="thong-bao-loi">{{ loi }}</p>
    <p v-if="loading" class="thong-bao-tai">Đang tải hóa đơn…</p>

    <div class="table-wrapper">
        <table>
            <thead>
                <tr>
                    <th>Mã HD</th>
                    <th>Khách hàng</th>
                    <th>Ngày lập</th>
                    <th>Tiền sân</th>
                    <th>Dịch vụ thêm</th>
                    <th>Tổng tiền</th>
                    <th>Trạng thái</th>
                    <th width="260">Thao tác</th>
                </tr>
            </thead>
            <tbody>
                <tr
                    v-for="invoice in invoices"
                    :key="invoice.datSanId"
                >
                    <td><b>{{ invoice.maHoaDon }}</b></td>
                    <td>{{ invoice.tenKhach }}</td>
                    <td>{{ dinhDangNgay(invoice.ngayDa) }}</td>
                    <td>{{ formatPrice(invoice.tienSan) }}</td>
                    <td>{{ formatPrice(invoice.tienDichVu) }}</td>
                    <td class="text-success"><b>{{ formatPrice(invoice.tongCong) }}</b></td>
                    <td>
                        <span
                            class="badge waiting"
                            v-if="invoice.trangThaiThanhToan === 'CHUA_THANH_TOAN'"
                        >
                            Chưa thanh toán
                        </span>
                        <span
                            class="badge waiting"
                            v-else-if="invoice.trangThaiThanhToan === 'THANH_TOAN_MOT_PHAN'"
                        >
                            Thanh toán một phần
                        </span>
                        <span
                            class="badge success"
                            v-else
                        >
                            Đã thanh toán đủ
                        </span>
                    </td>
                    <td>
                        <button
                            class="btn-detail"
                            @click="viewInvoice(invoice)"
                        >
                            Xem &amp; In
                        </button>

                        <button
                            class="btn-confirm"
                            v-if="invoice.conLai > 0 && invoice.trangThaiDon !== 'DA_HUY'"
                            @click="payInvoice(invoice)"
                        >
                            Thu {{ formatPrice(invoice.conLai) }}
                        </button>
                    </td>
                </tr>
                <tr v-if="!loading && invoices.length === 0">
                    <td colspan="8" style="text-align: center; color: #64748b;">
                        Không tìm thấy hóa đơn nào phù hợp.
                    </td>
                </tr>
            </tbody>
        </table>
    </div>

    <div
        v-if="showDetail"
        class="modal-overlay"
    >
        <div class="modal modal-invoice-print">
            <div id="invoice-print-area" class="invoice-box">
                <div class="invoice-header">
                    <h2>BÓNG ĐÁ DREAM LEAGUE</h2>
                    <p>Địa chỉ: Số 12 Dịch Vọng Hậu, Cầu Giấy, Hà Nội</p>
                    <p>Hotline: 0988.888.888</p>
                </div>
                
                <hr class="invoice-divider" />
                
                <h3 style="text-align: center; margin: 15px 0;">HÓA ĐƠN THANH TOÁN</h3>
                
                <div class="invoice-info">
                    <p><b>Mã hóa đơn:</b> {{ chung.maHoaDon }}</p>
                    <p><b>Khách hàng:</b> {{ chung.tenKhach }}</p>
                    <p><b>Số điện thoại:</b> {{ chung.soDienThoai }}</p>
                    <p><b>Sân:</b> {{ chung.tenSan }} ({{ chung.gioBatDau }} - {{ chung.gioKetThuc }})</p>
                    <p><b>Ngày đá:</b> {{ dinhDangNgay(chung.ngayDa) }}</p>
                    <p><b>Trạng thái:</b> {{ nhanTrangThai(chung.trangThaiThanhToan) }}</p>
                </div>

                <table class="invoice-table">
                    <thead>
                        <tr>
                            <th>Nội dung</th>
                            <th style="text-align: right;">Thành tiền</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr
                            v-for="(dong, i) in cacDong"
                            :key="i"
                        >
                            <td>
                                {{ dong.moTa }}
                                <span v-if="dong.soLuong > 1"> × {{ dong.soLuong }}</span>
                            </td>
                            <td style="text-align: right;">{{ formatPrice(dong.thanhTien) }}</td>
                        </tr>
                        <tr class="invoice-total-row">
                            <td><b>Tổng cộng:</b></td>
                            <td style="text-align: right;" class="text-success"><b>{{ formatPrice(chung.tongCong) }}</b></td>
                        </tr>
                        <tr>
                            <td>Đã thanh toán:</td>
                            <td style="text-align: right;">{{ formatPrice(chung.daThanhToan) }}</td>
                        </tr>
                        <tr>
                            <td><b>Còn lại:</b></td>
                            <td style="text-align: right;"><b>{{ formatPrice(chung.conLai) }}</b></td>
                        </tr>
                    </tbody>
                </table>

                <div class="invoice-footer">
                    <p>Cảm ơn quý khách đã tin tưởng và đồng hành!</p>
                    <p style="font-size: 11px; color: #94a3b8; margin-top: 5px;">Hệ thống quản lý trực tuyến Dream League</p>
                </div>
            </div>

            <div class="modal-action">
                <button
                    class="btn-finish"
                    @click="printInvoice"
                >
                    In hóa đơn
                </button>
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
import { ref, computed, onMounted, watch } from "vue"

const API_BASE = "http://localhost:8080/api/admin/hoa-don"
// Backend khu vực admin chỉ đọc hoá đơn; việc thu nốt tiền còn lại dùng
// endpoint thu tại quầy của staff.
const API_THU_TIEN = "http://localhost:8080/api/staff/thanh-toan/thanh-toan-cuoi"

function authHeader() {
    return {
        Authorization: `Bearer ${localStorage.getItem("token")}`
    }
}

const keyword = ref("")
const paymentFilter = ref("")

const loading = ref(false)
const loi = ref("")

const invoices = ref([])
const showDetail = ref(false)
const chiTiet = ref(null)

/** Rút gọn cho template: thông tin chung và các dòng của hoá đơn đang xem */
const chung = computed(() => chiTiet.value?.thongTinChung || {})
const cacDong = computed(() => chiTiet.value?.cacDong || [])

const NHAN_TRANG_THAI = {
    CHUA_THANH_TOAN: "Chưa thanh toán",
    THANH_TOAN_MOT_PHAN: "Thanh toán một phần",
    DA_THANH_TOAN_DU: "Đã thanh toán đủ"
}

function nhanTrangThai(ma) {
    return NHAN_TRANG_THAI[ma] || ma
}

function formatPrice(so) {
    return new Intl.NumberFormat("vi-VN").format(so || 0) + "đ"
}

/** Backend trả YYYY-MM-DD, hiển thị DD/MM/YYYY */
function dinhDangNgay(ngay) {
    if (!ngay) return ""
    const [y, m, d] = ngay.split("-")
    return `${d}/${m}/${y}`
}

function docLoi(e) {
    return e?.response?.data?.message || e.message || "Đã xảy ra lỗi!"
}

async function loadInvoices() {
    loading.value = true
    loi.value = ""
    try {
        const res = await axios.get(API_BASE, {
            params: {
                tuKhoa: keyword.value || undefined,
                trangThaiThanhToan: paymentFilter.value || undefined
            },
            headers: authHeader()
        })
        invoices.value = res.data
    } catch (e) {
        loi.value = docLoi(e)
        invoices.value = []
    } finally {
        loading.value = false
    }
}

onMounted(loadInvoices)

let timer = null
watch([keyword, paymentFilter], () => {
    clearTimeout(timer)
    timer = setTimeout(loadInvoices, 300)
})

/** Mở hoá đơn chi tiết (có đầy đủ các dòng tiền sân / gia hạn / dịch vụ) */
async function viewInvoice(item) {
    chiTiet.value = null
    showDetail.value = true
    try {
        const res = await axios.get(`${API_BASE}/${item.datSanId}`, { headers: authHeader() })
        chiTiet.value = res.data
    } catch (e) {
        showDetail.value = false
        alert(docLoi(e))
    }
}

/** Thu nốt phần tiền còn lại của hoá đơn */
async function payInvoice(item) {
    const phuongThuc = confirm(
        `Thu ${formatPrice(item.conLai)} của đơn ${item.maHoaDon}.

OK = Tiền mặt, Cancel = Chuyển khoản QR`
    ) ? "TIEN_MAT" : "QR"

    try {
        await axios.post(
            API_THU_TIEN,
            { datSanId: item.datSanId, phuongThuc },
            { headers: authHeader() }
        )
        await loadInvoices()
    } catch (e) {
        alert(docLoi(e))
    }
}

/** In hoá đơn: mở cửa sổ in chỉ chứa vùng hoá đơn */
function printInvoice() {
    const vung = document.getElementById("invoice-print-area")
    if (!vung) return
    const cuaSo = window.open("", "_blank")
    if (!cuaSo) return
    cuaSo.document.write(`
        <html>
          <head>
            <title>Hoá đơn ${chung.value.maHoaDon || ""}</title>
            <style>
              body { font-family: Arial, sans-serif; padding: 24px; }
              table { width: 100%; border-collapse: collapse; margin-top: 12px; }
              th, td { padding: 8px; border-bottom: 1px solid #ddd; }
              .invoice-header { text-align: center; }
            </style>
          </head>
          <body>${vung.innerHTML}</body>
        </html>
    `)
    cuaSo.document.close()
    cuaSo.focus()
    cuaSo.print()
    cuaSo.close()
}
</script>

<style scoped>
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
    margin-bottom: 12px;
}

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
    min-width: 240px;
    box-sizing: border-box;
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

.text-success {
    color: #16a34a;
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
    padding: 10px 20px;
    border-radius: 8px;
}

.btn-delete {
    background: #dc2626;
    color: #fff;
    border: none;
    padding: 6px 12px;
    border-radius: 6px;
}

.btn-close {
    background: #64748b;
    color: #fff;
    border: none;
    padding: 10px 20px;
    border-radius: 8px;
}

/* Modal overlays & popup styles */
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

.modal-action {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
    margin-top: 20px;
}

/* Thiết kế chuyên dụng mẫu Hóa Đơn In Ấn */
.invoice-box {
    border: 1px dashed #cbd5e1;
    padding: 20px;
    border-radius: 8px;
    background: #fafafa;
}

.invoice-header {
    text-align: center;
    font-size: 14px;
    color: #475569;
}

.invoice-header h2 {
    font-size: 20px;
    color: #0f172a;
    margin-bottom: 5px;
}

.invoice-divider {
    border: none;
    border-top: 1px dashed #cbd5e1;
    margin: 15px 0;
}

.invoice-info p {
    margin-bottom: 6px;
    font-size: 14px;
}

.invoice-table {
    width: 100%;
    margin-top: 15px;
    border-collapse: collapse;
}

.invoice-table th {
    background: #f1f5f9;
    padding: 8px;
    font-size: 13px;
}

.invoice-table td {
    padding: 10px 8px;
    font-size: 14px;
}

.invoice-total-row td {
    border-top: 2px solid #e2e8f0;
    padding-top: 12px;
}

.invoice-footer {
    text-align: center;
    margin-top: 25px;
    font-style: italic;
    font-size: 13px;
    color: #64748b;
}

@media(max-width:768px){
    .table-wrapper {
        overflow: auto;
    }
    .toolbar {
        flex-direction: column;
    }
    .search-box,
    .filter-box {
        width: 100%;
    }
}
</style>
