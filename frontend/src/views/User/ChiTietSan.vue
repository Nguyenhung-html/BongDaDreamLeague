<template>
  <div class="page-detail">

    <!-- ===== LỚP NỀN ẨN DỤ (đồng bộ trang chủ) ===== -->
    <div class="page-detail__ambient" aria-hidden="true">
      <div class="floodlight floodlight--l"></div>
      <div class="floodlight floodlight--r"></div>
      <div class="motes">
        <span v-for="n in 12" :key="n" class="mote" :style="{ '--i': n }"></span>
      </div>
    </div>

    <div class="container">
      <span class="eyebrow">⚽ Hẻm 104 Tân Sơn · Chi tiết sân</span>
      <h1>Chi tiết sân</h1>

      <!-- PHẦN 1: THÔNG TIN TẠM THỜI CỦA SÂN BÓNG -->
      <div class="san-bong-info">
        <div class="san-bong-info__media">
          <svg viewBox="0 0 360 160" fill="none" class="pitch-svg">
            <rect width="360" height="160" fill="#123321"/>
            <rect x="14" y="10" width="332" height="140" rx="4" stroke="rgba(247,251,244,0.3)" stroke-width="2" fill="none"/>
            <line x1="180" y1="10" x2="180" y2="150" stroke="rgba(247,251,244,0.25)" stroke-width="1.5"/>
            <circle cx="180" cy="80" r="26" stroke="rgba(247,251,244,0.25)" stroke-width="1.5" fill="none"/>
            <circle cx="180" cy="80" r="3" fill="var(--lime-300)"/>
            <rect x="14" y="55" width="30" height="50" stroke="rgba(247,251,244,0.25)" stroke-width="1.5" fill="none"/>
            <rect x="316" y="55" width="30" height="50" stroke="rgba(247,251,244,0.25)" stroke-width="1.5" fill="none"/>
          </svg>
        </div>
        <div class="san-bong-info__body">
          <h3>Sân bóng cỏ nhân tạo chất lượng cao</h3>
          <p>⚽ Loại sân: 5 hoặc 7 người tiêu chuẩn</p>
          <p>📍 Địa chỉ: Khu liên hợp thể thao</p>
        </div>
      </div>

      <!-- PHẦN 2: KHU VỰC BÌNH LUẬN & ĐÁNH GIÁ -->
      <div class="rating-section">
        <h2>Đánh giá từ khách hàng</h2>

        <!-- Danh sách bình luận thực tế từ DB -->
        <div v-if="Array.isArray(danhSachDanhGia) && danhSachDanhGia.length > 0" class="comment-list">
          <div v-for="dg in danhSachDanhGia" :key="dg.id" class="comment-item">
            <div class="comment-header">
              <span class="user-name">{{ dg.tenNguoiDung || 'Người dùng ẩn danh' }}</span>
              <span class="stars">
                <span v-for="star in 5" :key="star" :class="['star-icon', star <= (dg.soSao || 0) ? 'filled' : '']">★</span>
              </span>
            </div>
            <p class="comment-content">{{ dg.noiDung || 'Khách hàng không để lại bình luận chữ.' }}</p>
            <small class="comment-date">{{ formatNgay(dg.ngayDanhGia) }}</small>
          </div>
        </div>

        <!-- Hiển thị khi chưa có bình luận nào -->
        <div v-else class="no-comment">
          <p>Sân bóng này chưa có đánh giá nào. Hãy là người trải nghiệm đầu tiên!</p>
        </div>

        <!-- Khung điền khi duocQuyenDanhGia = true -->
        <div v-if="duocQuyenDanhGia" class="form-danh-gia">
          <h3>Viết đánh giá của bạn</h3>
          <p class="form-subtitle">Chia sẻ trải nghiệm thực tế của bạn sau khi trải nghiệm sân đấu</p>

          <div class="star-rating-input">
            <span
              v-for="star in 5"
              :key="star"
              class="star-clickable"
              :class="{ 'active': star <= form.soSao }"
              @click="form.soSao = star"
            >
              ★
            </span>
            <span class="star-label">({{ form.soSao }} / 5 sao)</span>
          </div>

          <div class="input-group">
            <textarea
              v-model="form.noiDung"
              placeholder="Sân đấu có tốt không? Đèn chiếu sáng và chất lượng cỏ thế nào?..."
              maxlength="500"
              rows="4"
            ></textarea>
            <span class="char-count">{{ form.noiDung.length }}/500</span>
          </div>

          <!-- Nút bấm động theo trạng thái loadingSubmit -->
          <button @click="guiDanhGia" class="btn-submit" :disabled="loadingSubmit">
            {{ loadingSubmit ? 'Đang gửi...' : 'Gửi đánh giá' }}
          </button>
        </div>

        <!-- Hiển thị ổ khóa nếu chưa đăng nhập -->
        <div v-else class="locked-comment">
          <p>🔒 Vui lòng đăng nhập tài khoản để viết đánh giá cho sân bóng này.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useRoute } from 'vue-router';

const route = useRoute();

// 1. Khai báo các biến quản lý trạng thái
const sanBongId = ref(route.params.id || '');
const danhSachDanhGia = ref([]);
const duocQuyenDanhGia = ref(false);
const loadingSubmit = ref(false);

const form = ref({
  sanBongId: sanBongId.value,
  soSao: 5,
  noiDung: ''
});

// 2. Hàm kiểm tra đăng nhập
const kiemTraDangNhap = () => {
  const token = localStorage.getItem('token');
  if (token) {
    duocQuyenDanhGia.value = true;
  } else {
    duocQuyenDanhGia.value = false;
  }
};

// 🌟 BỔ SUNG ĐOẠN NÀY: Hàm lấy danh sách bình luận bị thiếu khiến code bị lỗi
const layDanhSachBinhLuan = async () => {
  if (!sanBongId.value) return;
  try {
    const res = await axios.get(`/api/danh-gia/san/${sanBongId.value}`);
    danhSachDanhGia.value = res.data;
  } catch (error) {
    console.error("Lỗi lấy danh sách đánh giá:", error);
  }
};

// 3. Hàm gửi bình luận
const guiDanhGia = async () => {
  const token = localStorage.getItem('token');
  if (!token) return;
  if (!form.value.soSao) {
    alert("Vui lòng chọn số sao đánh giá!");
    return;
  }

  loadingSubmit.value = true;
  try {
    await axios.post(`/api/danh-gia/gui`, form.value, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    alert("Gửi đánh giá thành công!");

    // Reset form nhập liệu
    form.value.noiDung = '';
    form.value.soSao = 5;

    // 🌟 SỬA ĐOẠN NÀY: Trì hoãn 300ms rồi mới gọi lại để đảm bảo DB đã lưu xong xuôi
    setTimeout(async () => {
      await layDanhSachBinhLuan();
    }, 300);

  } catch (error) {
    alert(error.response?.data || "Có lỗi xảy ra khi gửi đánh giá.");
  } finally {
    loadingSubmit.value = false;
  }
};

// 4. Hàm format ngày tháng hiển thị
const formatNgay = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};

// 5. Chạy khi load trang
onMounted(() => {
  layDanhSachBinhLuan(); // Chạy hàm này để lấy dữ liệu
  kiemTraDangNhap();     // Chạy hàm này để mở/khóa ô bình luận
});
</script>

<style scoped>
/* ===== TOKENS (đồng bộ trang chủ / danh sách sân / lịch sử / voucher) ===== */
.page-detail {
  --night-950: #050b08;
  --night-800: #0a1f13;
  --night-700: #123321;
  --turf-500: #23935a;
  --turf-700: #146239;
  --lime-400: #b6ff3c;
  --lime-300: #d3ff8f;
  --amber-400: #ffb020;
  --crimson-500: #ff4757;
  --chalk-050: #f7fbf4;
  --chalk-200: #e3ecdf;
  --font-display: 'Oswald', 'Manrope', sans-serif;
  --font-body: 'Manrope', sans-serif;
  --font-mono: 'Space Mono', monospace;

  position: relative;
  background: var(--night-950);
  color: var(--chalk-050);
  font-family: var(--font-body);
  padding: 44px 0 70px;
  min-height: calc(100vh - 76px);
  overflow: hidden;
}
.container { max-width: 800px; margin: 0 auto; padding: 0 24px; position: relative; z-index: 1; }

/* ===== LỚP NỀN ẨN DỤ ===== */
.page-detail__ambient { position: fixed; inset: 0; z-index: 0; pointer-events: none; }
.floodlight { position: absolute; top: -12%; width: 55vh; height: 145vh; background: conic-gradient(from 90deg at 50% 0%, transparent 42%, rgba(255,244,214,.06) 50%, transparent 58%); mix-blend-mode: screen; }
.floodlight--l { left: -14%; animation: sweep 12s ease-in-out infinite alternate; }
.floodlight--r { right: -14%; animation: sweep 12s ease-in-out infinite alternate-reverse; }
@keyframes sweep { 0% { transform: rotate(-10deg); } 100% { transform: rotate(10deg); } }
.motes { position: absolute; inset: 0; }
.mote { position: absolute; bottom: -10px; left: calc((var(--i) * 8%) + 1%); width: 3px; height: 3px; border-radius: 50%; background: var(--lime-300); opacity: 0; animation: rise 10s linear infinite; animation-delay: calc(var(--i) * -0.7s); }
@keyframes rise { 0% { transform: translateY(0) scale(.6); opacity: 0; } 10% { opacity: .45; } 90% { opacity: .1; } 100% { transform: translateY(-100vh) scale(1.1); opacity: 0; } }

/* ===== HEADER ===== */
.eyebrow { display: inline-block; font-family: var(--font-mono); font-size: 12px; letter-spacing: .16em; text-transform: uppercase; color: var(--lime-300); }
h1 {
  font-family: var(--font-display); font-size: 28px; font-weight: 600;
  color: var(--chalk-050); margin: 10px 0 24px;
}

/* ===== THÔNG TIN SÂN ===== */
.san-bong-info {
  background: rgba(247,251,244,.03); border: 1px solid rgba(247,251,244,.09);
  border-radius: 18px; overflow: hidden; margin-bottom: 30px;
}
.san-bong-info__media { line-height: 0; }
.pitch-svg { width: 100%; height: auto; display: block; }
.san-bong-info__body { padding: 20px 22px 22px; }
.san-bong-info h3 { font-family: var(--font-display); font-size: 19px; font-weight: 600; color: var(--chalk-050); margin-bottom: 8px; }
.san-bong-info p { color: var(--chalk-200); opacity: .8; font-size: 14px; margin: 4px 0; }

/* ===== ĐÁNH GIÁ ===== */
.rating-section h2 { font-family: var(--font-display); font-size: 21px; font-weight: 600; color: var(--chalk-050); margin-bottom: 20px; }

.comment-list { display: flex; flex-direction: column; gap: 14px; margin-bottom: 30px; }
.comment-item {
  background: rgba(247,251,244,.03); border: 1px solid rgba(247,251,244,.09);
  padding: 16px 20px; border-radius: 14px; border-left: 3px solid var(--lime-400);
  transition: border-color .2s;
}
.comment-item:hover { border-color: rgba(182,255,60,.3); border-left-color: var(--lime-400); }
.comment-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.user-name { font-weight: 700; color: var(--chalk-050); }
.star-icon { color: rgba(247,251,244,.18); font-size: 17px; }
.star-icon.filled { color: var(--amber-400); }
.comment-content { color: var(--chalk-200); opacity: .85; margin: 5px 0; line-height: 1.55; font-size: 14px; }
.comment-date { color: var(--chalk-200); opacity: .45; font-size: 12px; }

.no-comment {
  text-align: center; color: var(--chalk-200); opacity: .6; padding: 26px;
  background: rgba(247,251,244,.03); border: 1px dashed rgba(247,251,244,.14);
  border-radius: 14px; font-style: italic;
}

/* ===== FORM ĐÁNH GIÁ ===== */
.form-danh-gia {
  background: rgba(247,251,244,.03); border: 1px solid rgba(182,255,60,.18);
  padding: 26px; border-radius: 16px; margin-top: 30px;
}
.form-danh-gia h3 { font-family: var(--font-display); font-size: 18px; font-weight: 600; color: var(--chalk-050); }
.form-subtitle { color: var(--chalk-200); opacity: .65; font-size: 13.5px; margin: 4px 0 18px; }

.star-rating-input { margin-bottom: 20px; display: flex; align-items: center; gap: 5px; }
.star-clickable { font-size: 28px; color: rgba(247,251,244,.18); cursor: pointer; transition: color .2s, transform .1s; }
.star-clickable:hover { transform: scale(1.1); }
.star-clickable:hover, .star-clickable.active { color: var(--amber-400); }
.star-label { margin-left: 10px; font-weight: 600; color: var(--chalk-200); opacity: .8; font-size: 14px; }

.input-group { position: relative; margin-bottom: 20px; }
textarea {
  width: 100%; padding: 12px 14px;
  background: rgba(5,11,8,.4); border: 1px solid rgba(247,251,244,.16);
  border-radius: 10px; font-family: inherit; font-size: 14px; color: var(--chalk-050);
  resize: vertical; box-sizing: border-box; outline: none; transition: border-color .15s, box-shadow .15s;
}
textarea::placeholder { color: var(--chalk-200); opacity: .4; }
textarea:focus { border-color: var(--lime-400); box-shadow: 0 0 0 3px rgba(182,255,60,.12); }
.char-count { position: absolute; right: 4px; bottom: -20px; font-size: 12px; color: var(--chalk-200); opacity: .5; }

.btn-submit {
  background: linear-gradient(135deg, var(--turf-500), var(--turf-700)); color: var(--chalk-050);
  border: none; padding: 12px 26px; font-size: 15px; font-weight: 700;
  border-radius: 999px; cursor: pointer; transition: box-shadow .2s, transform .2s; margin-top: 6px;
}
.btn-submit:hover:not(:disabled) { box-shadow: 0 10px 22px -8px rgba(182,255,60,.4); transform: translateY(-2px); }
.btn-submit:disabled { background: rgba(35,147,90,.35); color: rgba(247,251,244,.6); cursor: not-allowed; transform: none; box-shadow: none; }

.locked-comment {
  background: rgba(247,251,244,.03); border-radius: 14px; padding: 16px;
  text-align: center; color: var(--chalk-200); opacity: .75; font-size: 14px;
  border: 1px dashed rgba(247,251,244,.16); margin-top: 30px;
}
</style>