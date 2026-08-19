<template>
  <div class="ho-tro-page">
    <div class="container">
      <div class="page-header">
        <h1>Trung tâm hỗ trợ</h1>
        <p>Mọi thông tin bạn cần khi đặt sân tại DreamLeague</p>
      </div>

      <div class="ho-tro-layout">
        <!-- Menu neo bên trái -->
        <nav class="ho-tro-nav">
          <a
            v-for="muc in cacMuc"
            :key="muc.id"
            :href="`#${muc.id}`"
            class="ho-tro-nav__item"
            :class="{ active: mucDangXem === muc.id }"
            @click="cuonToi(muc.id)"
          >
            {{ muc.icon }} {{ muc.tieuDe }}
          </a>
        </nav>

        <!-- Nội dung -->
        <div class="ho-tro-content">
          <!-- 1. Hướng dẫn đặt sân -->
          <section id="huong-dan" class="ho-tro-section">
            <h2>📖 Hướng dẫn đặt sân</h2>
            <div class="step-list">
              <div class="step-item">
                <span class="step-num">1</span>
                <div>
                  <h3>Đăng ký / Đăng nhập tài khoản</h3>
                  <p>Tạo tài khoản mới hoặc đăng nhập bằng email, số điện thoại đã đăng ký với DreamLeague.</p>
                </div>
              </div>
              <div class="step-item">
                <span class="step-num">2</span>
                <div>
                  <h3>Chọn sân và khung giờ</h3>
                  <p>Vào mục "Sân bóng", chọn sân phù hợp, chọn ngày đá và khung giờ còn trống trên bảng giá.</p>
                </div>
              </div>
              <div class="step-item">
                <span class="step-num">3</span>
                <div>
                  <h3>Đặt cọc giữ sân</h3>
                  <p>Thanh toán cọc 50% qua mã QR để xác nhận giữ sân ngay lập tức, hoặc chọn trả tiền mặt tại sân.</p>
                </div>
              </div>
              <div class="step-item">
                <span class="step-num">4</span>
                <div>
                  <h3>Nhận xác nhận & tới sân</h3>
                  <p>Sau khi thanh toán, đơn được xác nhận tự động. Tới sân đúng giờ và thanh toán 50% còn lại tại quầy.</p>
                </div>
              </div>
            </div>
          </section>

          <!-- 2. Câu hỏi thường gặp -->
          <section id="faq" class="ho-tro-section">
            <h2>❓ Câu hỏi thường gặp</h2>
            <div class="faq-list">
              <details class="faq-item" v-for="(item, i) in faqList" :key="i">
                <summary>{{ item.hoi }}</summary>
                <p>{{ item.dap }}</p>
              </details>
            </div>
          </section>

          <!-- 3. Chính sách huỷ sân -->
          <section id="chinh-sach-huy" class="ho-tro-section">
            <h2>📋 Chính sách huỷ sân</h2>
            <ul class="policy-list">
              <li>💸 Đặt cọc <strong>50%</strong> tổng tiền để xác nhận booking.</li>
              <li>🏟️ Thanh toán <strong>50%</strong> còn lại trực tiếp tại sân trước giờ đá.</li>
              <li>⏰ Huỷ trước <strong>20 phút</strong> so với giờ đá được hoàn cọc đầy đủ.</li>
              <li>❌ Huỷ muộn hơn 20 phút hoặc không tới sẽ <strong>mất tiền cọc</strong>.</li>
              <li>📌 Mỗi tài khoản tối đa <strong>2 sân/người/ngày</strong>.</li>
              <li>💳 Thời gian thanh toán online tối đa <strong>3 phút</strong> để quét mã QR</li>
              <li>⏱️ Có thể yêu cầu gia hạn thêm 30 phút nếu khung giờ tiếp theo còn trống, nhân viên sẽ xác nhận và cộng thêm phí tương ứng.</li>
            </ul>
          </section>

          <!-- 4. Điều khoản sử dụng -->
          <section id="dieu-khoan" class="ho-tro-section">
            <h2>📄 Điều khoản sử dụng</h2>
            <div class="terms-content">
              <p>Khi sử dụng dịch vụ của DreamLeague, bạn đồng ý với các điều khoản sau:</p>
              <ol>
                <li>Thông tin tài khoản (họ tên, số điện thoại) phải chính xác để đảm bảo quyền lợi khi đặt sân.</li>
                <li>DreamLeague có quyền từ chối phục vụ với các tài khoản vi phạm chính sách huỷ sân nhiều lần.</li>
                <li>Mọi giao dịch thanh toán được xử lý qua cổng thanh toán đối tác, đảm bảo an toàn thông tin.</li>
                <li>Giá thuê sân có thể thay đổi theo khung giờ và được hiển thị công khai trước khi đặt.</li>
                <li>DreamLeague không chịu trách nhiệm với các thiệt hại phát sinh do lỗi thiết bị cá nhân của người dùng.</li>
              </ol>
            </div>
          </section>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const cacMuc = [
  { id: 'huong-dan', tieuDe: 'Hướng dẫn đặt sân', icon: '📖' },
  { id: 'faq', tieuDe: 'Câu hỏi thường gặp', icon: '❓' },
  { id: 'chinh-sach-huy', tieuDe: 'Chính sách huỷ sân', icon: '📋' },
  { id: 'dieu-khoan', tieuDe: 'Điều khoản sử dụng', icon: '📄' }
]

const mucDangXem = ref('huong-dan')

const faqList = [
  { hoi: 'Tôi có thể huỷ đơn đặt sân không?', dap: 'Có. Vào "Lịch sử đặt sân", chọn đơn cần huỷ. Nếu huỷ trước giờ đá 20 phút, bạn được hoàn cọc đầy đủ.' },
  { hoi: 'Thanh toán online qua QR có an toàn không?', dap: 'Có. Hệ thống xác nhận thanh toán tự động qua cổng SePay, không cần chờ nhân viên xác nhận thủ công.' },
  { hoi: 'Tôi có thể gọi thêm đồ uống/dịch vụ khi đã đặt sân không?', dap: 'Có. Vào "Lịch sử đặt sân", chọn đơn của bạn, bấm "Gọi đồ uống/dịch vụ" để chọn món, tiền sẽ cộng vào phần thanh toán tại sân.' },
  { hoi: 'Làm sao để gia hạn thêm giờ đá?', dap: 'Ở "Lịch sử đặt sân", bấm "Yêu cầu gia hạn +30 phút" - nhân viên tại sân sẽ xác nhận nếu khung giờ tiếp theo còn trống.' },
  { hoi: 'Tôi quên chưa thanh toán 50% còn lại thì sao?', dap: 'Bạn thanh toán phần còn lại trực tiếp bằng tiền mặt hoặc QR ngay tại quầy khi tới sân.' }
]

function cuonToi(id) {
  mucDangXem.value = id
}

onMounted(() => {
  // Quan sát khi cuộn trang để tự cập nhật mục đang active trên menu neo
  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          mucDangXem.value = entry.target.id
        }
      })
    },
    { rootMargin: '-40% 0px -50% 0px' }
  )
  document.querySelectorAll('.ho-tro-section').forEach((el) => observer.observe(el))
})
</script>

<style scoped>
.ho-tro-page {
  background: #f8fafc;
  min-height: calc(100vh - 76px);
  padding: 40px 0 80px;
}
.page-header { text-align: center; margin-bottom: 40px; }
.page-header h1 { font-size: 32px; font-weight: 800; color: var(--navy-900, #0d1f3c); }
.page-header p { color: #64748b; margin-top: 6px; }

.ho-tro-layout {
  display: grid;
  grid-template-columns: 240px 1fr;
  gap: 32px;
  align-items: start;
}

/* Menu neo */
.ho-tro-nav {
  position: sticky;
  top: 90px;
  display: flex;
  flex-direction: column;
  gap: 4px;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 14px;
  padding: 12px;
}
.ho-tro-nav__item {
  padding: 10px 12px;
  border-radius: 8px;
  font-size: 13.5px;
  font-weight: 600;
  color: #475569;
  transition: background .15s, color .15s;
}
.ho-tro-nav__item:hover { background: #f1f5f9; }
.ho-tro-nav__item.active { background: var(--green-50, #f0fdf4); color: var(--green-700, #15803d); }

/* Nội dung */
.ho-tro-content { display: flex; flex-direction: column; gap: 32px; }
.ho-tro-section {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  padding: 28px 32px;
  scroll-margin-top: 90px;
}
.ho-tro-section h2 { font-size: 20px; font-weight: 800; color: var(--navy-900, #0d1f3c); margin-bottom: 20px; }

/* Hướng dẫn - step list */
.step-list { display: flex; flex-direction: column; gap: 18px; }
.step-item { display: flex; gap: 16px; }
.step-num {
  width: 32px; height: 32px; border-radius: 50%;
  background: var(--green-600, #16a34a); color: white;
  display: flex; align-items: center; justify-content: center;
  font-weight: 700; flex-shrink: 0;
}
.step-item h3 { font-size: 14.5px; font-weight: 700; color: var(--navy-900, #0d1f3c); margin-bottom: 4px; }
.step-item p { font-size: 13.5px; color: #64748b; line-height: 1.6; }

/* FAQ */
.faq-list { display: flex; flex-direction: column; gap: 10px; }
.faq-item {
  border: 1px solid #e2e8f0; border-radius: 10px; padding: 14px 16px;
}
.faq-item summary { font-size: 14px; font-weight: 600; color: var(--navy-900, #0d1f3c); cursor: pointer; }
.faq-item p { font-size: 13.5px; color: #64748b; margin-top: 10px; line-height: 1.6; }

/* Chính sách */
.policy-list { display: flex; flex-direction: column; gap: 10px; list-style: none; padding: 0; }
.policy-list li { font-size: 14px; color: #334155; line-height: 1.5; }

/* Điều khoản */
.terms-content p { font-size: 14px; color: #334155; margin-bottom: 14px; }
.terms-content ol { padding-left: 20px; display: flex; flex-direction: column; gap: 10px; }
.terms-content li { font-size: 13.5px; color: #475569; line-height: 1.6; }

@media (max-width: 860px) {
  .ho-tro-layout { grid-template-columns: 1fr; }
  .ho-tro-nav { position: static; flex-direction: row; overflow-x: auto; }
}
</style>