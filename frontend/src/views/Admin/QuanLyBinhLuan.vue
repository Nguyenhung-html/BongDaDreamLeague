Để khép lại trọn vẹn cụm tính năng tương tác với khách hàng, dưới đây là component **Quản lý bình luận** (`QuanLyBinhLuan.vue`).

Khác với Trang Đánh giá (vốn gắn liền với số sao và dịch vụ sân), Trang Bình luận này là nơi Admin kiểm soát toàn bộ các cuộc thảo luận, hỏi đáp của người dùng trên **mục Tin tức, Cẩm nang bóng đá hoặc Post thảo luận ghép đội** của hệ thống. Trang này tích hợp bộ lọc từ khóa vi phạm (chặn từ tục tĩu) và duyệt/xóa nhanh bình luận.

Bạn tạo một file mới tên là `QuanLyBinhLuan.vue` và dán toàn bộ đoạn mã dưới đây vào nhé:

```vue
<template>
  <div>
    <h1 class="dashboard__page-title">
        Quản lý bình luận
    </h1>

    <p class="dashboard__page-desc">
        Kiểm duyệt các thảo luận, câu hỏi ghép kèo hoặc bình luận bài viết từ phía người dùng.
    </p>

    <!-- Toolbar bộ lọc và xử lý nhanh -->
    <div class="toolbar">
        <input
            v-model="keyword"
            class="search-box"
            placeholder="Tìm theo tên, nội dung bình luận..."
        />

        <select
            v-model="statusFilter"
            class="filter-box"
        >
            <option value="">Tất cả trạng thái</option>
            <option value="Chờ duyệt">Chờ duyệt ⏳</option>
            <option value="Đã duyệt">Đã duyệt  v></option>
            <option value="Vi phạm">Vi phạm ⚠️</option>
        </select>

        <button 
            class="btn-clean"
            @click="autoCheckSpam"
        >
            🛡️ Quét từ cấm tự động
        </button>
    </div>

    <!-- TABLE DANH SÁCH BÌNH LUẬN -->
    <div class="table-wrapper">
        <table>
            <thead>
                <tr>
                    <th width="60">ID</th>
                    <th width="160">Người dùng</th>
                    <th>Nội dung bình luận</th>
                    <th width="200">Tại bài viết / Chuyên mục</th>
                    <th width="140">Thời gian</th>
                    <th width="130">Trạng thái</th>
                    <th width="220">Thao tác</th>
                </tr>
            </thead>
            <tbody>
                <tr
                    v-for="item in filteredComments"
                    :key="item.id"
                >
                    <td>{{ item.id }}</td>
                    <td>
                        <div class="user-info">
                            <b>{{ item.user }}</b>
                            <span>{{ item.role }}</span>
                        </div>
                    </td>
                    <td>
                        <div :class="['comment-body', { 'text-strike': item.status === 'Vi phạm' }]">
                            {{ item.content }}
                        </div>
                    </td>
                    <td>
                        <a href="#" class="post-link">{{ item.postTitle }}</a>
                    </td>
                    <td>{{ item.date }}</td>
                    <td>
                        <span :class="['badge', getStatusClass(item.status)]">
                            {{ item.status }}
                        </span>
                    </td>
                    <td>
                        <button
                            class="btn-confirm"
                            v-if="item.status === 'Chờ duyệt' || item.status === 'Vi phạm'"
                            @click="approveComment(item)"
                        >
                            Duyệt
                        </button>

                        <button
                            class="btn-cancel"
                            v-if="item.status === 'Đã duyệt'"
                            @click="markAsViolation(item)"
                        >
                            Báo vi phạm
                        </button>

                        <button
                            class="btn-delete"
                            @click="deleteComment(item)"
                        >
                            Xóa
                        </button>
                    </td>
                </tr>
                <tr v-if="filteredComments.length === 0">
                    <td colspan="7" style="text-align: center; color: #64748b;">
                        Không tìm thấy bình luận nào cần xử lý.
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue"

const keyword = ref("")
const statusFilter = ref("")

// Danh sách từ cấm giả lập để hệ thống tự động quét spam/tục tĩu
const toxicWords = ["lừa đảo", "admin ngu", "sân tệ", "đm", "vl"]

// Dữ liệu danh sách bình luận mẫu từ người dùng
const comments = ref([
    {
        id: 1,
        user: "Trần Minh Hoàng",
        role: "Thành viên",
        content: "Cần tìm thêm 2 chân đá cánh tối nay lúc 19h sân số 3, kèo chia tiền sân nhẹ nhàng ai đi được chấm mút nha!",
        postTitle: "Góc Ghép Kèo Đá Bồi",
        date: "28/06/2026 21:15",
        status: "Đã duyệt"
    },
    {
        id: 2,
        user: "Phạm Văn Đạt",
        role: "Khách vãng lai",
        content: "Hệ thống app đặt sân này xịn vl, mượt hơn mấy bên kia nhiều.",
        postTitle: "Hướng dẫn dùng App Dream League",
        date: "28/06/2026 22:30",
        status: "Chờ duyệt"
    },
    {
        id: 3,
        user: "Ẩn danh công nghệ",
        role: "Thành viên",
        content: "Trang web này lừa đảo đó anh em ơi đừng nạp tiền đặt sân!!",
        postTitle: "Thông báo cập nhật điều khoản",
        date: "27/06/2026 10:05",
        status: "Chờ duyệt"
    },
    {
        id: 4,
        user: "Lê Nhật Linh",
        role: "Thành viên",
        content: "Cho mình hỏi sân 7 người có hỗ trợ mượn sẵn găng tay thủ môn không ạ?",
        postTitle: "Bảng giá dịch vụ đi kèm",
        date: "26/06/2026 14:20",
        status: "Đã duyệt"
    }
])

// Lọc thông báo dựa trên thanh tìm kiếm và bộ lọc trạng thái duyệt
const filteredComments = computed(() => {
    return comments.value.filter(item => {
        const search = item.user.toLowerCase().includes(keyword.value.toLowerCase()) ||
                       item.content.toLowerCase().includes(keyword.value.toLowerCase())

        const status = statusFilter.value === "" || item.status === statusFilter.value

        return search && status
    })
})

// Chấp thuận hiển thị bình luận công khai
function approveComment(item) {
    item.status = "Đã duyệt"
}

// Đánh dấu bình luận chứa từ ngữ không phù hợp
function markAsViolation(item) {
    item.status = "Vi phạm"
}

// Xóa vĩnh viễn bình luận khỏi cơ sở dữ liệu
function deleteComment(item) {
    if (confirm("Bạn chắc chắn muốn xóa vĩnh viễn bình luận này không?")) {
        comments.value = comments.value.filter(x => x.id !== item.id)
    }
}

// Hàm quét tự động từ cấm (Tính năng cộng thêm cho Admin)
function autoCheckSpam() {
    let count = 0
    comments.value.forEach(item => {
        const contentLower = item.content.toLowerCase()
        const isToxic = toxicWords.some(word => contentLower.includes(word))
        
        if (isToxic && item.status !== "Vi phạm") {
            item.status = "Vi phạm"
            count++
        }
    })
    alert(`Hệ thống quét xong! Đã tự động chuyển đổi thành công ${count} bình luận nghi vấn sang trạng thái 'Vi phạm'.`)
}

// Trả về class màu sắc hiển thị phù hợp với từng trạng thái
function getStatusClass(status) {
    if (status === "Đã duyệt") return "success"
    if (status === "Chờ duyệt") return "waiting"
    return "cancel"
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
    align-items: center;
}

.search-box,
.filter-box {
    padding: 10px 14px;
    border: 1px solid #ddd;
    border-radius: 8px;
    min-width: 240px;
    box-sizing: border-box;
}

.btn-clean {
    background: #4f46e5;
    color: white;
    border: none;
    padding: 10px 16px;
    border-radius: 8px;
    font-weight: 500;
    cursor: pointer;
}

.btn-clean:hover {
    background: #4338ca;
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
    color: #475569;
}

/* User Info Layout */
.user-info {
    display: flex;
    flex-direction: column;
}
.user-info b {
    color: #1e293b;
}
.user-info span {
    font-size: 11px;
    color: #94a3b8;
}

/* Định dạng nội dung comment */
.comment-body {
    color: #334155;
    line-height: 1.5;
    word-break: break-word;
}
.text-strike {
    text-decoration: line-through;
    color: #94a3b8 !important;
    font-style: italic;
}

.post-link {
    color: #2563eb;
    text-decoration: none;
    font-weight: 500;
}
.post-link:hover {
    text-decoration: underline;
}

/* Trạng thái Badge màu sắc */
.badge {
    color: #fff;
    padding: 5px 12px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 500;
    display: inline-block;
    text-align: center;
}
.success { background: #16a34a; }
.waiting { background: #f59e0b; }
.cancel { background: #dc2626; }

/* Buttons */
button {
    cursor: pointer;
}

.btn-confirm {
    background: #16a34a;
    color: #fff;
    border: none;
    padding: 6px 12px;
    border-radius: 6px;
    margin-right: 6px;
}

.btn-cancel {
    background: #ea580c;
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

@media(max-width:768px){
    .table-wrapper {
        overflow: auto;
    }
    .toolbar {
        flex-direction: column;
        align-items: stretch;
    }
    .search-box,
    .filter-box,
    .btn-clean {
        width: 100%;
    }
}
</style>

```