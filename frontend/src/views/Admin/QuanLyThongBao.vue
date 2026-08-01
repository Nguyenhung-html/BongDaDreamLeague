Tiếp tục mạch dự án, dưới đây là component **Quản lý thông báo** (`QuanLyThongBao.vue`).

Trang này giúp Admin soạn thảo và gửi các thông báo (khuyến mãi, bảo trì sân, giải đấu mới) tới ứng dụng khách hàng hoặc hệ thống chung, đi kèm bộ lọc loại thông báo và lịch sử các thông báo đã gửi.

Bạn tạo một file mới tên là `QuanLyThongBao.vue` và dán trọn vẹn mã nguồn này vào dự án:

```vue
<template>
  <div>
    <h1 class="dashboard__page-title">
        Quản lý thông báo
    </h1>

    <p class="dashboard__page-desc">
        Tạo và gửi thông báo mới đến ứng dụng của khách hàng hoặc toàn bộ hệ thống.
    </p>

    <!-- Toolbar thao tác -->
    <div class="toolbar">
        <input
            v-model="keyword"
            class="search-box"
            placeholder="Tìm tiêu đề thông báo..."
        />

        <select
            v-model="typeFilter"
            class="filter-box"
        >
            <option value="">Tất cả loại tin</option>
            <option>Khuyến mãi</option>
            <option>Hệ thống</option>
            <option>Bảo trì</option>
        </select>

        <button
            class="btn-add"
            @click="openAdd"
        >
            + Tạo thông báo
        </button>
    </div>

    <!-- TABLE LỊCH SỬ THÔNG BÁO -->
    <div class="table-wrapper">
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Tiêu đề</th>
                    <th>Loại thông báo</th>
                    <th>Ngày gửi</th>
                    <th>Người gửi</th>
                    <th>Trạng thái</th>
                    <th width="260">Thao tác</th>
                </tr>
            </thead>
            <tbody>
                <tr
                    v-for="item in filteredNotifications"
                    :key="item.id"
                >
                    <td>{{ item.id }}</td>
                    <td><b>{{ item.title }}</b></td>
                    <td>
                        <span :class="['badge-type', getTypeClass(item.type)]">
                            {{ item.type }}
                        </span>
                    </td>
                    <td>{{ item.date }}</td>
                    <td>{{ item.sender }}</td>
                    <td>
                        <span
                            class="badge success"
                            v-if="item.status == 'Đã gửi'"
                        >
                            Đã gửi
                        </span>
                        <span
                            class="badge waiting"
                            v-else
                        >
                            Nháp
                        </span>
                    </td>
                    <td>
                        <button
                            class="btn-detail"
                            @click="viewNotification(item)"
                        >
                            Xem
                        </button>

                        <button
                            class="btn-edit"
                            v-if="item.status == 'Nháp'"
                            @click="editNotification(item)"
                        >
                            Sửa
                        </button>

                        <button
                            class="btn-confirm"
                            v-if="item.status == 'Nháp'"
                            @click="sendNotification(item)"
                        >
                            Gửi ngay
                        </button>

                        <button
                            class="btn-delete"
                            @click="deleteNotification(item)"
                        >
                            Xóa
                        </button>
                    </td>
                </tr>
                <tr v-if="filteredNotifications.length === 0">
                    <td colspan="7" style="text-align: center; color: #64748b;">
                        Không tìm thấy thông báo nào phù hợp.
                    </td>
                </tr>
            </tbody>
        </table>
    </div>

    <!-- MODAL SOẠN THẢO THÔNG BÁO -->
    <div
        v-if="showModal"
        class="modal-overlay"
    >
        <div class="modal">
            <h2>{{ editing ? 'Sửa thông báo' : 'Tạo thông báo mới' }}</h2>

            <label class="form-label">Tiêu đề thông báo</label>
            <input
                v-model="form.title"
                placeholder="Nhập tiêu đề ngắn gọn, thu hút..."
            >

            <label class="form-label">Loại thông báo</label>
            <select v-model="form.type" class="modal-select">
                <option>Khuyến mãi</option>
                <option>Hệ thống</option>
                <option>Bảo trì</option>
            </select>

            <label class="form-label">Nội dung chi tiết</label>
            <textarea
                v-model="form.content"
                placeholder="Nhập nội dung chi tiết của thông báo gửi tới khách hàng..."
            ></textarea>

            <div class="modal-action">
                <button
                    class="btn-save"
                    @click="saveNotification('Nháp')"
                >
                    Lưu nháp
                </button>
                <button
                    class="btn-confirm"
                    style="padding: 10px 20px; border-radius: 8px;"
                    @click="saveNotification('Đã gửi')"
                >
                    Gửi luôn
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

    <!-- MODAL XEM CHI TIẾT THÔNG BÁO -->
    <div
        v-if="showDetail"
        class="modal-overlay"
    >
        <div class="modal">
            <span :class="['badge-type', getTypeClass(selected.type)]" style="float: right;">
                {{ selected.type }}
            </span>
            <h2 style="margin-bottom: 5px;">{{ selected.title }}</h2>
            <p style="font-size: 13px; color: #94a3b8; margin-bottom: 15px;">
                Người gửi: {{ selected.sender }} | Ngày: {{ selected.date }}
            </p>
            
            <div class="notification-content-box">
                {{ selected.content }}
            </div>

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
const typeFilter = ref("")
const showModal = ref(false)
const showDetail = ref(false)
const editing = ref(false)
const selected = ref({})

const form = ref({
    id: null,
    title: "",
    type: "Khuyến mãi",
    content: "",
    status: "Nháp"
})

// Dữ liệu danh sách thông báo mẫu
const notifications = ref([
    {
        id: 1,
        title: "Khuyến mãi Giờ Vàng: Giảm ngay 15% tiền sân",
        type: "Khuyến mãi",
        content: "Ưu đãi đặc biệt dành cho các đội đặt khung giờ từ 13:00 - 16:00 hằng ngày. Thời gian áp dụng từ nay đến hết tháng 07/2026.",
        date: "28/06/2026",
        sender: "Admin Trung",
        status: "Đã gửi"
    },
    {
        id: 2,
        title: "Bảo trì định kỳ hệ thống chiếu sáng Sân số 5",
        type: "Bảo trì",
        content: "Để nâng cao chất lượng trải nghiệm, Sân 5 sẽ tạm dừng đón khách vào tối ngày 30/06/2026 nhằm thay mới hệ thống đèn LED cao áp.",
        date: "27/06/2026",
        sender: "Kỹ thuật viên",
        status: "Đã gửi"
    },
    {
        id: 3,
        title: "Thông báo cập nhật điều khoản đặt cọc giữ sân",
        type: "Hệ thống",
        content: "Từ ngày 01/07/2026, tất cả các đơn đặt sân trước 3 ngày vui lòng chuyển cọc tối thiểu 30% để hệ thống tự động khóa lịch.",
        date: "28/06/2026",
        sender: "Ban Quản Lý",
        status: "Nháp"
    }
])

// Lọc thông báo theo từ khóa và phân loại
const filteredNotifications = computed(() => {
    return notifications.value.filter(item => {
        const search = item.title.toLowerCase().includes(keyword.value.toLowerCase())
        const status = typeFilter.value == "" || item.type == typeFilter.value
        return search && status
    })
})

// Mở modal thêm mới
function openAdd() {
    editing.value = false
    form.value = {
        id: null,
        title: "",
        type: "Khuyến mãi",
        content: "",
        status: "Nháp"
    }
    showModal.value = true
}

// Mở modal sửa (Chỉ áp dụng với tin nháp)
function editNotification(item) {
    editing.value = true
    form.value = { ...item }
    showModal.value = true
}

// Lưu thông báo (Có thể lưu nháp hoặc phát hành trực tiếp)
function saveNotification(statusTarget) {
    if (!form.value.title.trim() || !form.value.content.trim()) {
        alert("Vui lòng điền đầy đủ tiêu đề và nội dung!")
        return
    }

    if (editing.value) {
        const index = notifications.value.findIndex(x => x.id == form.value.id)
        if (index !== -1) {
            notifications.value[index] = { 
                ...form.value, 
                status: statusTarget,
                date: "28/06/2026",
                sender: "Admin Trung" 
            }
        }
    } else {
        notifications.value.unshift({
            ...form.value,
            id: Date.now(),
            status: statusTarget,
            date: "28/06/2026",
            sender: "Admin Trung"
        })
    }
    showModal.value = false
}

// Đổi trạng thái tin Nháp -> Đã gửi
function sendNotification(item) {
    item.status = "Đã gửi"
}

// Xóa thông báo
function deleteNotification(item) {
    if (confirm("Bạn có chắc chắn muốn xóa thông báo này?")) {
        notifications.value = notifications.value.filter(x => x.id != item.id)
    }
}

// Xem chi tiết
function viewNotification(item) {
    selected.value = item
    showDetail.value = true
}

// Lấy style class theo loại thông báo để tô màu phân biệt
function getTypeClass(type) {
    if (type === 'Khuyến mãi') return 'type-promo'
    if (type === 'Bảo trì') return 'type-maintenance'
    return 'type-system'
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

/* Các loại Badge trạng thái */
.badge {
    color: #fff;
    padding: 5px 12px;
    border-radius: 20px;
    font-size: 13px;
    display: inline-block;
}

.waiting { background: #64748b; }
.success { background: #16a34a; }

/* Tag phân biệt Loại thông báo */
.badge-type {
    padding: 4px 10px;
    border-radius: 6px;
    font-size: 12px;
    font-weight: 600;
}
.type-promo { background: #fee2e2; color: #dc2626; }
.type-maintenance { background: #fef3c7; color: #d97706; }
.type-system { background: #dbeafe; color: #2563eb; }

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

.btn-delete {
    background: #dc2626;
    color: #fff;
    border: none;
    padding: 6px 12px;
    border-radius: 6px;
}

.btn-save {
    background: #475569;
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

/* Modal form style */
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
    width: 540px;
    background: #fff;
    border-radius: 12px;
    padding: 25px;
    box-sizing: border-box;
}

.modal h2 {
    margin-bottom: 18px;
}

.form-label {
    display: block;
    font-size: 14px;
    font-weight: 500;
    margin-bottom: 6px;
    color: #334155;
}

.modal input,
.modal-select,
.modal textarea {
    width: 100%;
    padding: 10px;
    margin-bottom: 16px;
    border: 1px solid #ddd;
    border-radius: 8px;
    box-sizing: border-box;
}

.modal textarea {
    height: 120px;
    resize: none;
}

.modal-action {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
    margin-top: 10px;
}

/* Khung hiển thị nội dung đọc tin */
.notification-content-box {
    background: #f8fafc;
    border: 1px solid #e2e8f0;
    padding: 15px;
    border-radius: 8px;
    min-height: 100px;
    line-height: 1.6;
    color: #334155;
    white-space: pre-line;
}

@media(max-width:768px){
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

```