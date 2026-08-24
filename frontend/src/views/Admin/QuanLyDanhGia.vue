<template>
  <div class="qldg">

    <!-- ===== CỘT TRÁI: DANH SÁCH SÂN BÓNG ===== -->
    <div class="sidebar-san">
      <div class="sidebar-header">
        <span class="eyebrow">⚽ Chọn sân</span>
        <h3 class="sidebar-title">Danh sách sân bóng</h3>
      </div>

      <div class="search-wrap">
        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="7"/><path d="M21 21l-4.3-4.3"/></svg>
        <input v-model="searchSan" type="text" placeholder="Tìm nhanh sân bóng..." class="search-input"/>
      </div>

      <ul class="san-list">
        <li
          v-for="san in filteredSanBong"
          :key="san.id"
          :class="{ 'san-item--active': selectedSanBongId === san.id }"
          class="san-item"
          @click="selectSanBong(san.id)"
        >
          <span class="san-item__icon">⚽</span>
          <span class="san-item__name">{{ san.tenSan }}</span>
        </li>
        <li v-if="filteredSanBong.length === 0" class="san-empty">Không tìm thấy sân bóng nào.</li>
      </ul>
    </div>

    <!-- ===== CỘT PHẢI: QUẢN LÝ ĐÁNH GIÁ ===== -->
    <div class="main-review">
      <div class="page-header">
        <span class="eyebrow">⚽ Bảng điều khiển · Admin</span>
        <h1 class="page-title">Quản lý đánh giá</h1>
        <p class="page-desc">Chọn sân bóng ở cột bên trái để quản lý, ẩn/hiện hoặc xóa các đánh giá vi phạm tiêu chuẩn của khách hàng.</p>
      </div>

      <!-- CHƯA CHỌN SÂN -->
      <div v-if="!selectedSanBongId" class="empty-state">
        <div class="empty-state__icon">👈</div>
        <h3>Vui lòng chọn một sân bóng ở danh sách bên trái</h3>
        <p>Hệ thống sẽ hiển thị toàn bộ lịch sử đánh giá chi tiết của sân đó tại đây.</p>
      </div>

      <!-- ĐÃ CHỌN SÂN -->
      <div v-else>
        <!-- BỘ LỌC -->
        <div class="filter-card">
          <div class="filter-top">
            <div class="search-wrap search-wrap--wide">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="7"/><path d="M21 21l-4.3-4.3"/></svg>
              <input v-model="keyword" class="search-input" placeholder="Tìm tên khách, nội dung..."/>
            </div>

            <div class="pill-select">
              <label>Số sao</label>
              <select v-model="ratingFilter" class="form-control">
                <option value="">Tất cả số sao</option>
                <option value="5">5 Sao ⭐⭐⭐⭐⭐</option>
                <option value="4">4 Sao ⭐⭐⭐⭐</option>
                <option value="3">3 Sao ⭐⭐⭐</option>
                <option value="2">2 Sao ⭐⭐</option>
                <option value="1">1 Sao ⭐</option>
              </select>
            </div>
          </div>
        </div>

        <!-- BẢNG ĐÁNH GIÁ -->
        <div class="table-card">
          <div class="table-responsive">
            <table class="data-table">
              <thead>
                <tr>
                  <th width="60">ID</th>
                  <th>Khách hàng</th>
                  <th>Điểm số</th>
                  <th>Nội dung bình luận</th>
                  <th>Ngày đánh giá</th>
                  <th>Trạng thái hiển thị</th>
                  <th style="text-align:center" width="200">Thao tác</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in filteredReviews" :key="item.id">
                  <td><span class="sub">#{{ item.id }}</span></td>
                  <td><span class="bold">{{ item.customer }}</span></td>
                  <td><span class="stars-text">{{ item.rating }} ⭐</span></td>
                  <td><div class="comment-text">{{ item.comment }}</div></td>
                  <td><span class="sub">{{ item.date }}</span></td>
                  <td>
                    <span class="badge badge-success" v-if="item.visible"><span class="badge__dot"></span>Đang hiển thị</span>
                    <span class="badge badge-cancel" v-else><span class="badge__dot"></span>Đã ẩn</span>
                  </td>
                  <td>
                    <div class="actions">
                      <button :class="['btn-act', item.visible ? 'hide' : 'show']" @click="toggleVisibility(item)">
                        {{ item.visible ? '🙈 Ẩn đi' : '👁️ Hiện lại' }}
                      </button>
                      <button class="btn-act remove" @click="deleteReview(item)">🗑️ Xóa</button>
                    </div>
                  </td>
                </tr>
                <tr v-if="filteredReviews.length === 0">
                  <td colspan="7" class="no-data">
                    <div class="no-data__icon">💬</div>
                    Sân bóng này hiện không có đánh giá nào phù hợp.
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
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
    const response = await axios.get(`${API_BASE_URL}/san-bong`, getAuthConfig())
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
    const response = await axios.get(
      `${API_BASE_URL}/danh-gia/admin/san/${sanId}`,
      getAuthConfig()
    )

    // SỬA: lấy đúng trạng thái ẩn/hiện thật từ backend thay vì luôn gán cứng true.
    // Chấp nhận nhiều tên field khác nhau tuỳ backend trả về (hienThi / hienThiCongKhai / an),
    // và chỉ mặc định true khi API hoàn toàn không trả field nào liên quan.
    reviews.value = response.data.map(item => ({
      id: item.id,
      customer: item.tenNguoiDung || "Ẩn danh",
      rating: item.soSao,
      comment: item.noiDung || "(Không có nội dung)",
      date: formatNgay(item.ngayDanhGia),
      visible: resolveVisible(item)
    }))
  } catch (error) {
    console.error("Lỗi lấy danh sách đánh giá của sân:", error)
    alert("Không thể tải đánh giá của sân bóng này!")
  }
}

// Đọc đúng trạng thái hiển thị thật từ dữ liệu backend trả về
function resolveVisible(item) {
  if (typeof item.hienThi === 'boolean') return item.hienThi
  if (typeof item.hienThiCongKhai === 'boolean') return item.hienThiCongKhai
  if (typeof item.an === 'boolean') return !item.an
  if (typeof item.daAn === 'boolean') return !item.daAn
  return true
}

// ==========================================
// 3️⃣ THAY ĐỔI TRẠNG THÁI ẨN / HIỆN ĐÁNH GIÁ
// ==========================================
async function toggleVisibility(item) {
  const hanhDong = item.visible ? "ẩn" : "hiển thị lại"
  if (!confirm(`Bạn có chắc chắn muốn ${hanhDong} đánh giá này không?`)) return

  try {
    await axios.put(`${API_BASE_URL}/danh-gia/admin/an/${item.id}`, {}, getAuthConfig())
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
      await axios.delete(`${API_BASE_URL}/danh-gia/admin/xoa/${item.id}`, getAuthConfig())
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
const filteredSanBong = computed(() => {
  return dsSanBong.value.filter(san =>
    san.tenSan.toLowerCase().includes(searchSan.value.toLowerCase())
  )
})

const filteredReviews = computed(() => {
  return reviews.value.filter(item => {
    const search = item.customer.toLowerCase().includes(keyword.value.toLowerCase()) ||
                   item.comment.toLowerCase().includes(keyword.value.toLowerCase())
    const rating = ratingFilter.value == "" || item.rating.toString() === ratingFilter.value
    return search && rating
  })
})

const formatNgay = (dateString) => {
  if (!dateString) return ""
  const date = new Date(dateString)
  return date.toLocaleDateString("vi-VN", { day: "2-digit", month: "2-digit", year: "numeric" })
}

onMounted(() => {
  taiDanhSachSanBong()
})
</script>

<style scoped>
/* ============================================================
   TOKENS — đồng bộ với trang chủ & các trang quản lý khác
   ============================================================ */
.qldg {
  --night-950: #050b08;
  --night-800: #0a1f13;
  --night-700: #123321;
  --turf-500: #23935a;
  --turf-700: #146239;
  --lime-400: #b6ff3c;
  --lime-300: #d3ff8f;
  --amber-400: #ffb020;
  --crimson-500: #ff4757;
  --info-400: #6fa8ff;
  --chalk-050: #f7fbf4;
  --chalk-200: #e3ecdf;
  --font-display: 'Oswald', 'Manrope', sans-serif;
  --font-body: 'Manrope', sans-serif;
  --font-mono: 'Space Mono', monospace;

  display: flex;
  gap: 22px;
  min-height: 80vh;
  width: 100%;
  background: var(--night-950);
  color: var(--chalk-050);
  font-family: var(--font-body);
  padding: 32px clamp(16px, 3vw, 40px) 60px;
  box-sizing: border-box;
}

.eyebrow { display: inline-block; font-family: var(--font-mono); font-size: 11.5px; letter-spacing: .14em; text-transform: uppercase; color: var(--lime-300); }

/* ============================================================
   SIDEBAR SÂN BÓNG
   ============================================================ */
.sidebar-san {
  width: 280px; flex-shrink: 0; background: rgba(247,251,244,.03); border: 1px solid rgba(247,251,244,.1);
  border-radius: 18px; padding: 20px; display: flex; flex-direction: column; height: fit-content;
  position: sticky; top: 20px;
}
.sidebar-header { margin-bottom: 14px; }
.sidebar-title { font-family: var(--font-display); font-size: 17px; font-weight: 600; margin: 6px 0 0; color: var(--chalk-050); }

.search-wrap {
  display: flex; align-items: center; gap: 8px; background: rgba(5,11,8,.4);
  border: 1px solid rgba(247,251,244,.15); border-radius: 10px; padding: 0 12px; margin-bottom: 14px;
}
.search-wrap svg { color: var(--chalk-200); opacity: .6; flex-shrink: 0; }
.search-input { flex: 1; border: none; background: transparent; outline: none; padding: 10px 0; font-size: 13.5px; color: var(--chalk-050); font-family: var(--font-body); width: 100%; }
.search-input::placeholder { color: var(--chalk-200); opacity: .45; }

.san-list { list-style: none; padding: 0; margin: 0; overflow-y: auto; max-height: 60vh; display: flex; flex-direction: column; gap: 6px; }
.san-item {
  padding: 11px 12px; border-radius: 10px; cursor: pointer; display: flex; align-items: center; gap: 10px;
  transition: all .15s; color: var(--chalk-200); border: 1px solid transparent;
}
.san-item:hover { background: rgba(182,255,60,.06); color: var(--chalk-050); }
.san-item--active {
  background: linear-gradient(135deg, var(--turf-500), var(--turf-700)); color: var(--chalk-050); font-weight: 700;
  border-color: var(--turf-500); box-shadow: 0 8px 18px -8px rgba(182,255,60,.35);
}
.san-item__icon { font-size: 15px; }
.san-item__name { font-size: 13.5px; }
.san-empty { text-align: center; color: var(--chalk-200); opacity: .55; padding: 20px 0; font-size: 13px; }

/* ============================================================
   MAIN CONTENT
   ============================================================ */
.main-review { flex: 1; min-width: 0; }
.page-header { margin-bottom: 22px; }
.page-title { font-family: var(--font-display); font-weight: 600; font-size: clamp(24px, 2.6vw, 30px); margin: 8px 0 4px; color: var(--chalk-050); }
.page-desc { font-size: 13.5px; color: var(--chalk-200); opacity: .72; margin: 0; max-width: 640px; }

/* EMPTY STATE */
.empty-state {
  text-align: center; padding: 60px 24px; background: rgba(247,251,244,.03);
  border: 2px dashed rgba(247,251,244,.15); border-radius: 18px; color: var(--chalk-200);
}
.empty-state h3 { color: var(--chalk-050); font-family: var(--font-display); font-weight: 600; font-size: 17px; margin: 0 0 6px; }
.empty-state p { opacity: .7; font-size: 13.5px; margin: 0; }
.empty-state__icon { font-size: 36px; margin-bottom: 12px; animation: bounce 2s infinite; }
@keyframes bounce { 0%, 100% { transform: translateX(0); } 50% { transform: translateX(-6px); } }

/* ============================================================
   FILTER CARD
   ============================================================ */
.filter-card {
  background: rgba(247,251,244,.03); border: 1px solid rgba(247,251,244,.1); border-radius: 18px;
  padding: 18px 20px; margin-bottom: 22px; backdrop-filter: blur(6px);
}
.filter-top { display: flex; flex-wrap: wrap; gap: 12px; align-items: flex-end; }
.search-wrap--wide { flex: 1.6; min-width: 220px; margin-bottom: 0; }

.pill-select { display: flex; flex-direction: column; gap: 5px; min-width: 200px; }
.pill-select label { font-family: var(--font-mono); font-size: 10.5px; text-transform: uppercase; letter-spacing: .06em; color: var(--lime-300); padding-left: 2px; }

.form-control {
  padding: 10px 13px; border: 1px solid rgba(247,251,244,.15); border-radius: 10px; font-size: 13.5px;
  outline: none; background: rgba(5,11,8,.4); color: var(--chalk-050); font-family: var(--font-body);
  transition: border-color .15s; width: 100%; box-sizing: border-box;
}
.form-control:focus { border-color: var(--lime-400); box-shadow: 0 0 0 3px rgba(182,255,60,.12); }
select.form-control { cursor: pointer; }
select.form-control option { background: var(--night-800); color: var(--chalk-050); }

/* ============================================================
   TABLE
   ============================================================ */
.table-card { background: rgba(247,251,244,.03); border: 1px solid rgba(247,251,244,.1); border-radius: 18px; overflow: hidden; }
.table-responsive { overflow-x: auto; }
.data-table { width: 100%; border-collapse: collapse; font-size: 13.5px; }
.data-table th {
  background: rgba(247,251,244,.04); color: var(--lime-300); font-family: var(--font-mono); font-weight: 600;
  font-size: 11px; text-transform: uppercase; letter-spacing: .05em; padding: 14px 16px; text-align: left;
  border-bottom: 1px solid rgba(247,251,244,.1);
}
.data-table td { padding: 13px 16px; border-bottom: 1px solid rgba(247,251,244,.06); vertical-align: middle; }
.data-table tbody tr:hover { background: rgba(182,255,60,.04); }
.bold { font-weight: 600; color: var(--chalk-050); }
.sub { font-size: 12.5px; color: var(--chalk-200); opacity: .7; }
.stars-text { color: var(--amber-400); font-weight: 700; white-space: nowrap; }
.comment-text { color: var(--chalk-200); opacity: .85; line-height: 1.55; max-width: 380px; word-wrap: break-word; font-size: 13px; }

.badge { display: inline-flex; align-items: center; gap: 6px; padding: 5px 12px; border-radius: 20px; font-size: 11.5px; font-weight: 700; }
.badge__dot { width: 6px; height: 6px; border-radius: 50%; flex-shrink: 0; }
.badge-success { background: rgba(182,255,60,.15); color: var(--lime-300); }
.badge-success .badge__dot { background: var(--lime-400); }
.badge-cancel { background: rgba(255,71,87,.14); color: #ff9686; }
.badge-cancel .badge__dot { background: var(--crimson-500); }

.actions { display: flex; gap: 6px; justify-content: center; flex-wrap: wrap; }
.btn-act { padding: 6px 11px; border-radius: 7px; border: none; font-size: 11.5px; font-weight: 700; cursor: pointer; transition: .15s; white-space: nowrap; }
.btn-act.hide { background: rgba(255,176,32,.15); color: var(--amber-400); }
.btn-act.hide:hover { background: rgba(255,176,32,.26); }
.btn-act.show { background: rgba(182,255,60,.12); color: var(--lime-300); }
.btn-act.show:hover { background: rgba(182,255,60,.2); }
.btn-act.remove { background: rgba(255,71,87,.14); color: #ff9686; }
.btn-act.remove:hover { background: rgba(255,71,87,.24); }

.no-data { text-align: center; padding: 50px 20px; color: var(--chalk-200); opacity: .6; }
.no-data__icon { font-size: 26px; margin-bottom: 8px; }

/* ============================================================
   RESPONSIVE
   ============================================================ */
@media (max-width: 992px) {
  .qldg { flex-direction: column; }
  .sidebar-san { width: 100%; position: static; }
  .san-list { max-height: 240px; }
}
@media (max-width: 720px) {
  .filter-top { flex-direction: column; align-items: stretch; }
  .pill-select { min-width: 0; }
  .data-table { font-size: 12.5px; }
}
</style>