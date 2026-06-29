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
            <option>Chờ xác nhận</option>
            <option>Đã xác nhận</option>
            <option>Hoàn thành</option>
            <option>Đã hủy</option>
        </select>

        <button
            class="btn-add"
            @click="openAdd"
        >
            + Thêm đặt sân
        </button>
    </div>

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
                    v-for="item in filteredBookings"
                    :key="item.id"
                >
                    <td>{{ item.id }}</td>
                    <td>{{ item.customer }}</td>
                    <td>{{ item.field }}</td>
                    <td>{{ item.date }}</td>
                    <td>{{ item.time }}</td>
                    <td>{{ item.total }}</td>
                    <td>
                        <span
                            class="badge waiting"
                            v-if="item.status == 'Chờ xác nhận'"
                        >
                            Chờ xác nhận
                        </span>
                        <span
                            class="badge success"
                            v-else-if="item.status == 'Đã xác nhận'"
                        >
                            Đã xác nhận
                        </span>
                        <span
                            class="badge done"
                            v-else-if="item.status == 'Hoàn thành'"
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
                            class="btn-edit"
                            @click="editBooking(item)"
                        >
                            Sửa
                        </button>

                        <button
                            class="btn-confirm"
                            v-if="item.status == 'Chờ xác nhận'"
                            @click="confirmBooking(item)"
                        >
                            Xác nhận
                        </button>

                        <button
                            class="btn-finish"
                            v-if="item.status == 'Đã xác nhận'"
                            @click="finishBooking(item)"
                        >
                            Hoàn thành
                        </button>

                        <button
                            class="btn-cancel"
                            v-if="item.status != 'Hoàn thành' && item.status != 'Đã hủy'"
                            @click="cancelBooking(item)"
                        >
                            Hủy
                        </button>

                        <button
                            class="btn-delete"
                            @click="deleteBooking(item)"
                        >
                            Xóa
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
            <h2>{{ editing ? 'Sửa đặt sân' : 'Thêm đặt sân' }}</h2>

            <input
                v-model="form.customer"
                placeholder="Tên khách hàng"
            >
            <input
                v-model="form.phone"
                placeholder="Số điện thoại"
            >
            <input
                v-model="form.field"
                placeholder="Tên sân"
            >
            <input
                v-model="form.date"
                placeholder="Ngày"
            >
            <input
                v-model="form.time"
                placeholder="Khung giờ"
            >
            <input
                v-model="form.total"
                placeholder="Tổng tiền"
            >
            <textarea
                v-model="form.note"
                placeholder="Ghi chú"
            ></textarea>

            <div class="modal-action">
                <button
                    class="btn-save"
                    @click="saveBooking"
                >
                    Lưu
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

            <p><b>Khách hàng:</b> {{ selected.customer }}</p>
            <p><b>SĐT:</b> {{ selected.phone }}</p>
            <p><b>Sân:</b> {{ selected.field }}</p>
            <p><b>Ngày:</b> {{ selected.date }}</p>
            <p><b>Khung giờ:</b> {{ selected.time }}</p>
            <p><b>Tổng tiền:</b> {{ selected.total }}</p>
            <p><b>Ghi chú:</b> {{ selected.note }}</p>
            <p><b>Trạng thái:</b> {{ selected.status }}</p>

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
import { ref, computed } from "vue"

const keyword = ref("")
const statusFilter = ref("")

const showModal = ref(false)
const showDetail = ref(false)
const editing = ref(false)

const selected = ref({})

const form = ref({
    id: null,
    customer: "",
    phone: "",
    field: "",
    date: "",
    time: "",
    total: "",
    note: "",
    status: "Chờ xác nhận"
})

const bookings = ref([
    {
        id: 1,
        customer: "Nguyễn Quốc Trung",
        phone: "0988888888",
        field: "Sân 5",
        date: "28/06/2026",
        time: "18:00 - 20:00",
        total: "400.000đ",
        note: "Đá giao lưu",
        status: "Chờ xác nhận"
    },
    {
        id: 2,
        customer: "Lê Văn A",
        phone: "0912345678",
        field: "Sân 7",
        date: "29/06/2026",
        time: "19:00 - 21:00",
        total: "500.000đ",
        note: "",
        status: "Đã xác nhận"
    },
    {
        id: 3,
        customer: "Trần Văn B",
        phone: "0933444555",
        field: "Sân 3",
        date: "30/06/2026",
        time: "17:00 - 19:00",
        total: "300.000đ",
        note: "",
        status: "Hoàn thành"
    }
])

const filteredBookings = computed(() => {
    return bookings.value.filter(item => {
        const search = item.customer
            .toLowerCase()
            .includes(keyword.value.toLowerCase())

        const status = statusFilter.value == ""
            || item.status == statusFilter.value

        return search && status
    })
})

function openAdd() {
    editing.value = false
    form.value = {
        id: null,
        customer: "",
        phone: "",
        field: "",
        date: "",
        time: "",
        total: "",
        note: "",
        status: "Chờ xác nhận"
    }
    showModal.value = true
}

function editBooking(item) {
    editing.value = true
    form.value = { ...item }
    showModal.value = true
}

function saveBooking() {
    if (editing.value) {
        const index = bookings.value.findIndex(
            x => x.id == form.value.id
        )
        if (index !== -1) {
            bookings.value[index] = { ...form.value }
        }
    } else {
        bookings.value.push({
            ...form.value,
            id: Date.now()
        })
    }
    showModal.value = false
}

function deleteBooking(item) {
    if (confirm("Bạn muốn xóa đặt sân này?")) {
        bookings.value = bookings.value.filter(
            x => x.id != item.id
        )
    }
}

function confirmBooking(item) {
    item.status = "Đã xác nhận"
}

function finishBooking(item) {
    item.status = "Hoàn thành"
}

function cancelBooking(item) {
    item.status = "Đã hủy"
}

function viewBooking(item) {
    selected.value = item
    showDetail.value = true
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
.modal textarea {
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