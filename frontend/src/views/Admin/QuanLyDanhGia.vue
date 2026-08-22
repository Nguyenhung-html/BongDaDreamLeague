<template>
  <div class="admin-review-layout">
    <!-- CỘT TRÁI: DANH SÁCH SÂN BÓNG -->
    <div class="sidebar-san-bong">
      <h3 class="sidebar-title">⚽ Danh sách sân bóng</h3>
      <div class="search-san-wrapper">
        <input 
          v-model="searchSan" 
          type="text" 
          placeholder="Tìm nhanh sân bóng..." 
          class="sidebar-search"
        />
      </div>
      <ul class="san-bong-list">
        <li 
          v-for="san in filteredSanBong" 
          :key="san.id"
          :class="{ 'active-san': selectedSanBongId === san.id }"
          @click="selectSanBong(san.id)"
        >
          <span class="san-icon">⚽</span>
          <span class="san-name">{{ san.tenSan }}</span>
        </li>
        <li v-if="filteredSanBong.length === 0" class="no-san">
          Không tìm thấy sân bóng nào.
        </li>
      </ul>
    </div>

    <!-- CỘT PHẢI: QUẢN LÝ ĐÁNH GIÁ CỦA SÂN ĐƯỢC CHỌN -->
    <div class="main-content-review">
      <h1 class="dashboard__page-title">Quản lý đánh giá</h1>
      <p class="dashboard__page-desc">
        Chọn sân bóng ở cột bên trái để quản lý, ẩn/hiện hoặc xóa các đánh giá vi phạm tiêu chuẩn của khách hàng.
      </p>

      <!-- GIAO DIỆN KHI CHƯA CHỌN SÂN BÓNG -->
      <div v-if="!selectedSanBongId" class="empty-state">
        <div class="empty-icon">👈</div>
        <h3>Vui lòng chọn một sân bóng ở danh sách bên trái</h3>
        <p>Hệ thống sẽ hiển thị toàn bộ lịch sử đánh giá chi tiết của sân đó tại đây.</p>
      </div>

      <!-- GIAO DIỆN KHI ĐÃ CHỌN SÂN BÓNG -->
      <div v-else>
        <!-- Toolbar bộ lọc đánh giá -->
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
                <th width="180">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in filteredReviews" :key="item.id">
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
                </td>
                <td>{{ item.date }}</td>
                <td>
                  <span class="badge success" v-if="item.visible">
                    Đang hiển thị
                  </span>
                  <span class="badge cancel" v-else>
                    Đã ẩn
                  </span>
                </td>
                <td>
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
                  Sân bóng này hiện không có đánh giá nào phù hợp.
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue"
import axios from "axios"

// --- CÁC BIẾN REACTIVE QUẢN LÝ TRẠNG THÁI ---
const searchSan = ref("")
const selectedSanBongId = ref(null)
const keyword = ref("")
const ratingFilter = ref("")

// Mảng chứa dữ liệu thực tế lấy từ Database
const dsSanBong = ref([]) // Lấy từ API sân bóng
const reviews = ref([])   // Lấy từ API đánh giá của sân được chọn

// Cấu hình URL Backend (Bạn thay đổi Port nếu khác 8080 nhé)
const API_BASE_URL = "/api"

// Hàm lấy Token đính kèm vào Header để Spring Security kiểm tra quyền ADMIN
const getAuthConfig = () => {
  const token = localStorage.getItem("token")
  return {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  }
}

// ==========================================
// 1️⃣ LẤY DANH SÁCH TẤT CẢ SÂN BÓNG TỪ DATABASE (CỘT TRÁI)
// ==========================================
const taiDanhSachSanBong = async () => {
  try {
    // Gọi API lấy toàn bộ sân bóng của bạn (đổi endpoint /san-bong nếu khác nhé)
    const response = await axios.get(`${API_BASE_URL}/san-bong`, getAuthConfig())
    
    // Map dữ liệu từ database trả về (ví dụ: id, tenSanBong) vào mảng dsSanBong
    dsSanBong.value = response.data.map(san => ({
      id: san.id,
      tenSan: san.tenSanBong || san.tenSan
    }))
  } catch (error) {
    console.error("Lỗi lấy danh sách sân bóng:", error)
    alert("Không thể tải danh sách sân bóng từ database!")
  }
}

// ==========================================
// 2️⃣ KHI CLICK CHỌN SÂN BÓNG -> TẢI ĐÁNH GIÁ CỦA SÂN ĐÓ
// ==========================================
const selectSanBong = async (sanId) => {
  selectedSanBongId.value = sanId
  keyword.value = ""
  ratingFilter.value = ""
  
  try {
    // 🌟 TRUYỀN getAuthConfig() VÀO THAM SỐ THỨ 2 CỦA AXIOS.GET
    const response = await axios.get(
      `${API_BASE_URL}/danh-gia/admin/san/${sanId}`, 
      getAuthConfig()
    )
    
    reviews.value = response.data.map(item => ({
      id: item.id,
      customer: item.tenNguoiDung || "Ẩn danh",
      rating: item.soSao,
      comment: item.noiDung || "(Không có nội dung)",
      date: formatNgay(item.ngayDanhGia),
      visible: true 
    }))
  } catch (error) {
    console.error("Lỗi lấy danh sách đánh giá của sân:", error)
    alert("Không thể tải đánh giá của sân bóng này!")
  }
}

// ==========================================
// 3️⃣ THAY ĐỔI TRẠNG THÁI ẨN / HIỆN ĐÁNH GIÁ
// ==========================================
async function toggleVisibility(item) {
  const hanhDong = item.visible ? "ẩn" : "hiển thị lại"
  if (!confirm(`Bạn có chắc chắn muốn ${hanhDong} đánh giá này không?`)) return

  try {
    // Gọi API ẩn/hiển thị lại đánh giá (PUT /api/danh-gia/admin/an/{id})
    await axios.put(`${API_BASE_URL}/danh-gia/admin/an/${item.id}`, {}, getAuthConfig())
    
    // Cập nhật trạng thái trực tiếp trên UI
    item.visible = !item.visible
    alert(`Đã ${hanhDong} đánh giá thành công!`)
  } catch (error) {
    console.error("Lỗi thay đổi trạng thái ẩn/hiện:", error)
    alert(error.response?.data || "Có lỗi xảy ra khi cập nhật trạng thái!")
  }
}

// ==========================================
// 4️⃣ XÓA HẲN ĐÁNH GIÁ KHỎI DATABASE
// ==========================================
async function deleteReview(item) {
  if (confirm(`Bạn chắc chắn muốn XÓA VĨNH VIỄN đánh giá của khách ${item.customer}?`)) {
    try {
      // Gọi API xóa vĩnh viễn (DELETE /api/danh-gia/admin/xoa/${id})
      await axios.delete(`${API_BASE_URL}/danh-gia/admin/xoa/${item.id}`, getAuthConfig())
      
      // Xóa item trên giao diện table ngay lập tức
      reviews.value = reviews.value.filter(x => x.id !== item.id)
      alert("Đã xóa đánh giá thành công!")
    } catch (error) {
      console.error("Lỗi xóa đánh giá:", error)
      alert(error.response?.data || "Có lỗi xảy ra khi xóa đánh giá!")
    }
  }
}

// ==========================================
// BỘ LỌC SEARCH VÀ FORMAT DỮ LIỆU GIAO DIỆN
// ==========================================

// Tìm kiếm nhanh tên sân bóng ở cột bên trái
const filteredSanBong = computed(() => {
  return dsSanBong.value.filter(san => 
    san.tenSan.toLowerCase().includes(searchSan.value.toLowerCase())
  )
})

// Bộ lọc tìm kiếm từ khóa và số sao trên danh sách đánh giá của sân đang chọn
const filteredReviews = computed(() => {
  return reviews.value.filter(item => {
    const search = item.customer.toLowerCase().includes(keyword.value.toLowerCase()) ||
                   item.comment.toLowerCase().includes(keyword.value.toLowerCase())
    const rating = ratingFilter.value == "" || item.rating.toString() === ratingFilter.value
    return search && rating
  })
})

// Định dạng ngày tháng
const formatNgay = (dateString) => {
  if (!dateString) return ""
  const date = new Date(dateString)
  return date.toLocaleDateString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric"
  })
}

// Khi vừa load trang Admin lên, tự động lấy danh sách sân bóng đổ vào sidebar trái
onMounted(() => {
  taiDanhSachSanBong()
})
</script>

<style scoped>
/* Bố cục chia đôi: Trái 300px làm menu sân bóng, Phải làm nội dung chính */
.admin-review-layout {
  display: flex;
  gap: 25px;
  min-height: 80vh;
  font-family: system-ui, -apple-system, sans-serif;
}

/* Giao diện cột bên trái chứa danh sách sân bóng */
.sidebar-san-bong {
  width: 300px;
  background: #ffffff;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  padding: 20px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
}

.sidebar-title {
  font-size: 18px;
  font-weight: 600;
  margin-top: 0;
  margin-bottom: 15px;
  color: #1e293b;
}

.search-san-wrapper {
  margin-bottom: 15px;
}

.sidebar-search {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #cbd5e1;
  border-radius: 6px;
  box-sizing: border-box;
}

.san-bong-list {
  list-style: none;
  padding: 0;
  margin: 0;
  overflow-y: auto;
  flex-grow: 1;
}

.san-bong-list li {
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 10px;
  transition: all 0.2s;
  color: #475569;
}

.san-bong-list li:hover {
  background: #f1f5f9;
  color: #0f172a;
}

.san-bong-list li.active-san {
  background: #eff6ff;
  color: #2563eb;
  font-weight: 600;
  border-left: 4px solid #2563eb;
}

.san-icon {
  font-size: 16px;
}

.no-san {
  text-align: center;
  color: #94a3b8;
  padding: 20px 0;
  font-size: 14px;
}

/* Giao diện vùng nội dung chính bên phải */
.main-content-review {
  flex-grow: 1;
  background: transparent;
}

.dashboard__page-title {
  font-size: 28px;
  font-weight: 700;
  margin-top: 0;
  margin-bottom: 6px;
}

.dashboard__page-desc {
  color: #64748b;
  margin-bottom: 25px;
}

/* Trạng thái trống khi chưa bấm chọn gì */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: #f8fafc;
  border: 2px dashed #cbd5e1;
  border-radius: 12px;
  color: #64748b;
  margin-top: 20px;
}

.empty-icon {
  font-size: 40px;
  margin-bottom: 10px;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateX(0); }
  50% { transform: translateX(-5px); }
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
  word-wrap: break-word;
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
  font-weight: 500;
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

.btn-confirm:hover, .btn-cancel:hover, .btn-delete:hover {
  opacity: 0.85;
}

@media(max-width: 992px){
  .admin-review-layout {
    flex-direction: column;
  }
  .sidebar-san-bong {
    width: 100%;
  }
}
</style>