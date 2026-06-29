<template>
  <div>
    <h1 class="dashboard__page-title">
        Quản lý đánh giá
    </h1>

    <p class="dashboard__page-desc">
        Xem phản hồi, quản lý số sao và trả lời đánh giá từ khách hàng về chất lượng dịch vụ.
    </p>

    <!-- Toolbar bộ lọc -->
    <div class="toolbar">
        <input
            v-model="keyword"
            class="search-box"
            placeholder="Tìm tên khách, nội dung..."
        />

        <select
            v-model="ratingFilter"
            class="filter-box"
        >
            <option value="">Tất cả số sao</option>
            <option value="5">5 Sao ⭐⭐⭐⭐⭐</option>
            <option value="4">4 Sao ⭐⭐⭐⭐</option>
            <option value="3">3 Sao ⭐⭐⭐</option>
            <option value="2">2 Sao ⭐⭐</option>
            <option value="1">1 Sao ⭐</option>
        </select>
    </div>

    <!-- TABLE LỊCH SỬ ĐÁNH GIÁ -->
    <div class="table-wrapper">
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Khách hàng</th>
                    <th>Điểm số</th>
                    <th>Nội dung bình luận</th>
                    <th>Ngày đánh giá</th>
                    <th>Trạng thái hiển thị</th>
                    <th width="280">Thao tác</th>
                </tr>
            </thead>
            <tbody>
                <tr
                    v-for="item in filteredReviews"
                    :key="item.id"
                >
                    <td>{{ item.id }}</td>
                    <td><b>{{ item.customer }}</b></td>
                    <td>
                        <span class="stars-text">
                            {{ item.rating }} ⭐
                        </span>
                    </td>
                    <td>
                        <div class="comment-text">
                            {{ item.comment }}
                        </div>
                        <div v-if="item.reply" class="reply-badge">
                            <b>Phản hồi của bạn:</b> {{ item.reply }}
                        </div>
                    </td>
                    <td>{{ item.date }}</td>
                    <td>
                        <span
                            class="badge success"
                            v-if="item.visible"
                        >
                            Đang hiển thị
                        </span>
                        <span
                            class="badge cancel"
                            v-else
                        >
                            Đã ẩn
                        </span>
                    </td>
                    <td>
                        <button
                            class="btn-detail"
                            @click="openReplyModal(item)"
                        >
                            {{ item.reply ? 'Sửa phản hồi' : 'Trả lời' }}
                        </button>

                        <button
                            :class="item.visible ? 'btn-cancel' : 'btn-confirm'"
                            @click="toggleVisibility(item)"
                        >
                            {{ item.visible ? 'Ẩn đi' : 'Hiện lại' }}
                        </button>

                        <button
                            class="btn-delete"
                            @click="deleteReview(item)"
                        >
                            Xóa
                        </button>
                    </td>
                </tr>
                <tr v-if="filteredReviews.length === 0">
                    <td colspan="7" style="text-align: center; color: #64748b;">
                        Không tìm thấy đánh giá nào phù hợp.
                    </td>
                </tr>
            </tbody>
        </table>
    </div>

    <!-- MODAL TRẢ LỜI / PHẢN HỒI ĐÁNH GIÁ -->
    <div
        v-if="showModal"
        class="modal-overlay"
    >
        <div class="modal">
            <h2>Phản hồi đánh giá</h2>
            
            <div class="customer-review-summary">
                <p><b>Khách hàng:</b> {{ selectedReview.customer }} ({{ selectedReview.rating }} ⭐)</p>
                <p><b>Nội dung:</b> <i>"{{ selectedReview.comment }}"</i></p>
            </div>

            <label class="form-label">Nội dung phản hồi của Admin</label>
            <textarea
                v-model="replyContent"
                placeholder="Nhập lời cảm ơn hoặc giải trình gửi tới khách hàng..."
            ></textarea>

            <div class="modal-action">
                <button
                    class="btn-save"
                    @click="submitReply"
                >
                    Gửi phản hồi
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
  </div>
</template>

<script setup>
import { ref, computed } from "vue"

const keyword = ref("")
const ratingFilter = ref("")
const showModal = ref(false)
const selectedReview = ref({})
const replyContent = ref("")

// Dữ liệu danh sách đánh giá mẫu
const reviews = ref([
    {
        id: 1,
        customer: "Nguyễn Quốc Trung",
        rating: 5,
        comment: "Sân cỏ rất đẹp, đèn chiếu sáng ban đêm cực tốt, chủ sân vui tính nhiệt tình. Sẽ ủng hộ dài dài!",
        reply: "Cảm ơn bạn đã tin tưởng lựa chọn sân bóng Dream League nhé!",
        date: "28/06/2026",
        visible: true
    },
    {
        id: 2,
        customer: "Lê Văn A",
        rating: 4,
        comment: "Chất lượng sân ổn, tuy nhiên lưới rào sau gôn hơi thấp bóng dễ bay ra ngoài đường. Hy vọng sân khắc phục.",
        reply: "",
        date: "26/06/2026",
        visible: true
    },
    {
        id: 3,
        customer: "Trần Văn B",
        rating: 2,
        comment: "Thái độ nhân viên bảo vệ trông xe tối hôm qua chưa tốt, cằn nhằn với khách hàng.",
        reply: "Dạ rất xin lỗi anh về trải nghiệm này ạ. Ban quản lý sẽ làm việc lại với đội bảo vệ để cải thiện ngay.",
        date: "25/06/2026",
        visible: true
    }
])

// Bộ lọc tìm kiếm thông minh theo từ khóa và số sao
const filteredReviews = computed(() => {
    return reviews.value.filter(item => {
        const search = item.customer.toLowerCase().includes(keyword.value.toLowerCase()) ||
                       item.comment.toLowerCase().includes(keyword.value.toLowerCase())

        const rating = ratingFilter.value == "" || item.rating.toString() === ratingFilter.value

        return search && rating
    })
})

// Mở modal để nhập/sửa câu trả lời
function openReplyModal(item) {
    selectedReview.value = item
    replyContent.value = item.reply
    showModal.value = true
}

// Gửi nội dung phản hồi
function submitReply() {
    if (!replyContent.value.trim()) {
        alert("Vui lòng nhập nội dung phản hồi!")
        return
    }
    selectedReview.value.reply = replyContent.value
    showModal.value = false
}

// Ẩn hoặc hiện đánh giá khỏi ứng dụng của khách hàng
function toggleVisibility(item) {
    item.visible = !item.visible
}

// Xóa hẳn đánh giá
function deleteReview(item) {
    if (confirm(`Bạn chắc chắn muốn xóa đánh giá của ${item.customer}?`)) {
        reviews.value = reviews.value.filter(x => x.id !== item.id)
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

.stars-text {
    color: #f59e0b;
    font-weight: 600;
    white-space: nowrap;
}

.comment-text {
    color: #334155;
    line-height: 1.5;
    max-width: 400px;
}

/* Khối phụ hiển thị lời nhắn trả lời của admin ngay trong bảng */
.reply-badge {
    margin-top: 6px;
    padding: 6px 10px;
    background: #f1f5f9;
    border-left: 3px solid #6366f1;
    border-radius: 4px;
    font-size: 13px;
    color: #475569;
}

.badge {
    color: #fff;
    padding: 5px 12px;
    border-radius: 20px;
    font-size: 13px;
    display: inline-block;
}

.success { background: #16a34a; }
.cancel { background: #dc2626; }

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

/* Modal style */
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
    margin-bottom: 15px;
}

.customer-review-summary {
    background: #f8fafc;
    border: 1px solid #e2e8f0;
    padding: 12px;
    border-radius: 8px;
    margin-bottom: 16px;
    font-size: 14px;
    color: #475569;
}

.form-label {
    display: block;
    font-size: 14px;
    font-weight: 500;
    margin-bottom: 6px;
    color: #334155;
}

.modal textarea {
    width: 100%;
    height: 100px;
    padding: 10px;
    margin-bottom: 16px;
    border: 1px solid #ddd;
    border-radius: 8px;
    box-sizing: border-box;
    resize: none;
}

.modal-action {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
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