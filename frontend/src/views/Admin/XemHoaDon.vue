Tiếp tục hệ thống quản lý sân bóng, dưới đây là component **Quản lý hóa đơn** (`QuanLyHoaDon.vue`).

Component này tuân thủ chuẩn phong cách thiết kế (giao diện, màu sắc bảng, cách tổ chức modal/bộ lọc) tương tự như các trang trước, tích hợp thêm tính năng **In hóa đơn** chuyên nghiệp để admin xuất file hoặc in ra giấy cho khách.

Bạn tạo file `QuanLyHoaDon.vue` mới và dán trọn vẹn mã nguồn dưới đây vào nhé:

```vue
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
            <option>Chưa thanh toán</option>
            <option>Đã thanh toán</option>
        </select>
    </div>

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
                    v-for="invoice in filteredInvoices"
                    :key="invoice.id"
                >
                    <td><b>{{ invoice.code }}</b></td>
                    <td>{{ invoice.customer }}</td>
                    <td>{{ invoice.createdAt }}</td>
                    <td>{{ formatPrice(invoice.fieldPrice) }}</td>
                    <td>{{ formatPrice(invoice.servicePrice) }}</td>
                    <td class="text-success"><b>{{ formatPrice(invoice.total) }}</b></td>
                    <td>
                        <span
                            class="badge waiting"
                            v-if="invoice.status == 'Chưa thanh toán'"
                        >
                            Chưa thanh toán
                        </span>
                        <span
                            class="badge success"
                            v-else
                        >
                            Đã thanh toán
                        </span>
                    </td>
                    <td>
                        <button
                            class="btn-detail"
                            @click="viewInvoice(invoice)"
                        >
                            Xem & In
                        </button>

                        <button
                            class="btn-confirm"
                            v-if="invoice.status == 'Chưa thanh toán'"
                            @click="payInvoice(invoice)"
                        >
                            Thanh toán
                        </button>

                        <button
                            class="btn-delete"
                            @click="deleteInvoice(invoice)"
                        >
                            Xóa
                        </button>
                    </td>
                </tr>
                <tr v-if="filteredInvoices.length === 0">
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
                    <p><b>Mã hóa đơn:</b> {{ selected.code }}</p>
                    <p><b>Khách hàng:</b> {{ selected.customer }}</p>
                    <p><b>Số điện thoại:</b> {{ selected.phone }}</p>
                    <p><b>Ngày lập:</b> {{ selected.createdAt }}</p>
                    <p><b>Trạng thái:</b> {{ selected.status }}</p>
                </div>

                <table class="invoice-table">
                    <thead>
                        <tr>
                            <th>Nội dung</th>
                            <th style="text-align: right;">Thành tiền</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>Tiền thuê sân ({{ selected.field }})</td>
                            <td style="text-align: right;">{{ formatPrice(selected.fieldPrice) }}</td>
                        </tr>
                        <tr v-if="selected.servicePrice > 0">
                            <td>Dịch vụ bổ sung (Nước uống/Áo bib)</td>
                            <td style="text-align: right;">{{ formatPrice(selected.servicePrice) }}</td>
                        </tr>
                        <tr class="invoice-total-row">
                            <td><b>Tổng cộng:</b></td>
                            <td style="text-align: right;" class="text-success"><b>{{ formatPrice(selected.total) }}</b></td>
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
import { ref, computed } from "vue"

const keyword = ref("")
const paymentFilter = ref("")
const showDetail = ref(false)
const selected = ref({})

// Dữ liệu danh sách hóa đơn mẫu sinh ra tự động từ các đơn đặt sân đã kết thúc hoặc hoàn thành
const invoices = ref([
    {
        id: 1,
        code: "HD-001",
        customer: "Nguyễn Quốc Trung",
        phone: "0988888888",
        field: "Sân 5 (18:00 - 20:00)",
        fieldPrice: 400000,
        servicePrice: 50000, // Tiền nước uống, thuê bóng...
        total: 450000,
        createdAt: "28/06/2026",
        status: "Chưa thanh toán"
    },
    {
        id: 2,
        code: "HD-002",
        customer: "Lê Văn A",
        phone: "0912345678",
        field: "Sân 7 (19:00 - 21:00)",
        fieldPrice: 500000,
        servicePrice: 120000,
        total: 620000,
        createdAt: "29/06/2026",
        status: "Đã thanh toán"
    },
    {
        id: 3,
        code: "HD-003",
        customer: "Trần Văn B",
        phone: "0933444555",
        field: "Sân 3 (17:00 - 19:00)",
        fieldPrice: 300000,
        servicePrice: 0,
        total: 300000,
        createdAt: "30/06/2026",
        status: "Đã thanh toán"
    }
])

// Bộ lọc hóa đơn thông minh
const filteredInvoices = computed(() => {
    return invoices.value.filter(item => {
        const search = item.customer.toLowerCase().includes(keyword.value.toLowerCase()) || 
                       item.code.toLowerCase().includes(keyword.value.toLowerCase())

        const status = paymentFilter.value == "" || item.status == paymentFilter.value

        return search && status
    })
})

// Chuyển trạng thái sang Đã thanh toán
function payInvoice(item) {
    item.status = "Đã thanh toán"
}

// Xóa hóa đơn
function deleteInvoice(item) {
    if (confirm(`Bạn chắc chắn muốn xóa hóa đơn ${item.code}?`)) {
        invoices.value = invoices.value.filter(x => x.id != item.id)
    }
}

// Xem chi tiết hóa đơn
function viewInvoice(item) {
    selected.value = item
    showDetail.value = true
}

// Hàm kích hoạt lệnh in của trình duyệt cho khu vực hóa đơn
function printInvoice() {
    const printContent = document.getElementById("invoice-print-area").innerHTML
    const originalContent = document.body.innerHTML

    // Thay thế body bằng nội dung hóa đơn để tối ưu giao diện in ấn công cộng
    document.body.innerHTML = `
        <div style="padding: 40px; font-family: sans-serif; max-width: 600px; margin: 0 auto;">
            ${printContent}
        </div>
    `
    window.print()
    
    // Khôi phục lại giao diện trang quản trị sau khi đóng hộp thoại in
    window.location.reload()
}

// Định dạng tiền tệ VND
function formatPrice(value) {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' })
        .format(value)
        .replace('₫', 'đ')
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

```