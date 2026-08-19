<template>
  <div class="page-detail">
    <div class="container">
      <h1>Chi tiết sân - User</h1>
      
      <!-- PHẦN 1: THÔNG TIN TẠM THỜI CỦA SÂN BÓNG -->
      <div class="san-bong-info">
        <h3>Sân bóng cỏ nhân tạo chất lượng cao</h3>
        <p>⚽ Loại sân: 5 hoặc 7 người tiêu chuẩn</p>
        <p>📍 Địa chỉ: Khu liên hợp thể thao</p>
      </div>

      <hr class="divider" />

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
    const res = await axios.get(`http://localhost:8080/api/danh-gia/san/${sanBongId.value}`);
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
    await axios.post(`http://localhost:8080/api/danh-gia/gui`, form.value, {
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
.page-detail {
  padding: 40px 0;
  min-height: 60vh;
  background-color: #f8f9fa;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

h1 {
  font-size: 28px;
  font-weight: 700;
  color: #1a252c;
  margin-bottom: 20px;
}

.san-bong-info {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
  margin-bottom: 25px;
}

.divider {
  border: 0;
  height: 1px;
  background: #e2e8f0;
  margin: 30px 0;
}

.rating-section h2 {
  font-size: 22px;
  color: #1a252c;
  margin-bottom: 20px;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-bottom: 30px;
}

.comment-item {
  background: white;
  padding: 15px 20px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  border-left: 4px solid #4caf50;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.user-name {
  font-weight: 600;
  color: #2d3748;
}

.star-icon {
  color: #cbd5e1;
  font-size: 18px;
}

.star-icon.filled {
  color: #ffb703;
}

.comment-content {
  color: #4a5568;
  margin: 5px 0;
  line-height: 1.5;
}

.comment-date {
  color: #a0aec0;
  font-size: 12px;
}

.no-comment {
  text-align: center;
  color: #718096;
  padding: 20px;
  background: white;
  border-radius: 8px;
  font-style: italic;
}

.form-danh-gia {
  background: white;
  padding: 25px;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0,0,0,0.05);
  border: 1px solid #e2e8f0;
  margin-top: 30px;
}

.form-subtitle {
  color: #718096;
  font-size: 14px;
  margin-bottom: 15px;
}

.star-rating-input {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 5px;
}

.star-clickable {
  font-size: 30px;
  color: #cbd5e1;
  cursor: pointer;
  transition: color 0.2s;
}

.star-clickable:hover,
.star-clickable.active {
  color: #ffb703;
}

.star-label {
  margin-left: 10px;
  font-weight: 500;
  color: #4a5568;
}

.input-group {
  position: relative;
  margin-bottom: 15px;
}

textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #cbd5e1;
  border-radius: 6px;
  font-family: inherit;
  font-size: 15px;
  resize: vertical;
  box-sizing: border-box;
}

textarea:focus {
  outline: none;
  border-color: #4caf50;
  box-shadow: 0 0 0 3px rgba(76, 175, 80, 0.1);
}

.char-count {
  position: absolute;
  right: 10px;
  bottom: -20px;
  font-size: 12px;
  color: #a0aec0;
}

.btn-submit {
  background: #4caf50;
  color: white;
  border: none;
  padding: 12px 24px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s;
  margin-top: 10px;
}

.btn-submit:hover {
  background: #43a047;
}

.btn-submit:disabled {
  background: #a8e6cf;
  cursor: not-allowed;
}

.locked-comment {
  background: #edf2f7;
  border-radius: 8px;
  padding: 15px;
  text-align: center;
  color: #4a5568;
  font-size: 14px;
  border: 1px dashed #cbd5e1;
  margin-top: 30px;
}
</style>