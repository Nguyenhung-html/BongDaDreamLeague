<template>
  <div class="ho-tro-page">

    <!-- ===== LỚP NỀN ẨN DỤ (đồng bộ trang chủ) ===== -->
    <div class="ho-tro-page__ambient" aria-hidden="true">
      <div class="floodlight floodlight--l"></div>
      <div class="floodlight floodlight--r"></div>
      <div class="motes">
        <span v-for="n in 12" :key="n" class="mote" :style="{ '--i': n }"></span>
      </div>
    </div>

    <div class="container">
      <div class="page-header">
        <span class="eyebrow">⚽ DreamLeague</span>
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
/* ===== TOKENS (đồng bộ trang chủ / danh sách sân / lịch sử / voucher / chi tiết) ===== */
.ho-tro-page {
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
  min-height: calc(100vh - 76px);
  padding: 44px 0 80px;
  overflow: hidden;
}
.container { max-width: 1180px; margin: 0 auto; padding: 0 24px; position: relative; z-index: 1; }

/* ===== LỚP NỀN ẨN DỤ ===== */
.ho-tro-page__ambient { position: fixed; inset: 0; z-index: 0; pointer-events: none; }
.floodlight { position: absolute; top: -12%; width: 55vh; height: 145vh; background: conic-gradient(from 90deg at 50% 0%, transparent 42%, rgba(255,244,214,.06) 50%, transparent 58%); mix-blend-mode: screen; }
.floodlight--l { left: -14%; animation: sweep 12s ease-in-out infinite alternate; }
.floodlight--r { right: -14%; animation: sweep 12s ease-in-out infinite alternate-reverse; }
@keyframes sweep { 0% { transform: rotate(-10deg); } 100% { transform: rotate(10deg); } }
.motes { position: absolute; inset: 0; }
.mote { position: absolute; bottom: -10px; left: calc((var(--i) * 8%) + 1%); width: 3px; height: 3px; border-radius: 50%; background: var(--lime-300); opacity: 0; animation: rise 10s linear infinite; animation-delay: calc(var(--i) * -0.7s); }
@keyframes rise { 0% { transform: translateY(0) scale(.6); opacity: 0; } 10% { opacity: .45; } 90% { opacity: .1; } 100% { transform: translateY(-100vh) scale(1.1); opacity: 0; } }

/* ===== HEADER ===== */
.page-header { text-align: center; margin-bottom: 40px; }
.eyebrow { display: inline-block; font-family: var(--font-mono); font-size: 12px; letter-spacing: .16em; text-transform: uppercase; color: var(--lime-300); }
.page-header h1 { font-family: var(--font-display); font-size: 32px; font-weight: 600; color: var(--chalk-050); margin-top: 10px; }
.page-header p { color: var(--chalk-200); opacity: .7; margin-top: 6px; font-size: 14.5px; }

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
  background: rgba(247,251,244,.03);
  border: 1px solid rgba(247,251,244,.09);
  border-radius: 16px;
  padding: 12px;
}
.ho-tro-nav__item {
  padding: 10px 12px;
  border-radius: 10px;
  font-size: 13.5px;
  font-weight: 600;
  color: var(--chalk-200);
  opacity: .75;
  transition: background .15s, color .15s, opacity .15s;
}
.ho-tro-nav__item:hover { background: rgba(247,251,244,.05); opacity: 1; }
.ho-tro-nav__item.active { background: rgba(182,255,60,.1); color: var(--lime-300); opacity: 1; }

/* Nội dung */
.ho-tro-content { display: flex; flex-direction: column; gap: 32px; }
.ho-tro-section {
  background: rgba(247,251,244,.03);
  border: 1px solid rgba(247,251,244,.09);
  border-radius: 18px;
  padding: 28px 32px;
  scroll-margin-top: 90px;
  transition: border-color .2s;
}
.ho-tro-section:hover { border-color: rgba(182,255,60,.2); }
.ho-tro-section h2 { font-family: var(--font-display); font-size: 20px; font-weight: 600; color: var(--chalk-050); margin-bottom: 22px; }

/* Hướng dẫn - step list */
.step-list { display: flex; flex-direction: column; gap: 18px; }
.step-item { display: flex; gap: 16px; }
.step-num {
  width: 32px; height: 32px; border-radius: 50%;
  background: linear-gradient(135deg, var(--turf-500), var(--turf-700)); color: var(--chalk-050);
  display: flex; align-items: center; justify-content: center;
  font-weight: 700; font-family: var(--font-mono); flex-shrink: 0;
}
.step-item h3 { font-size: 14.5px; font-weight: 700; color: var(--chalk-050); margin-bottom: 4px; }
.step-item p { font-size: 13.5px; color: var(--chalk-200); opacity: .75; line-height: 1.6; }

/* FAQ */
.faq-list { display: flex; flex-direction: column; gap: 10px; }
.faq-item {
  border: 1px solid rgba(247,251,244,.1); border-radius: 12px; padding: 14px 16px;
  transition: border-color .15s;
}
.faq-item:hover { border-color: rgba(182,255,60,.25); }
.faq-item summary { font-size: 14px; font-weight: 600; color: var(--chalk-050); cursor: pointer; }
.faq-item summary::marker { color: var(--lime-300); }
.faq-item p { font-size: 13.5px; color: var(--chalk-200); opacity: .75; margin-top: 10px; line-height: 1.6; }

/* Chính sách */
.policy-list { display: flex; flex-direction: column; gap: 12px; list-style: none; padding: 0; }
.policy-list li { font-size: 14px; color: var(--chalk-200); opacity: .85; line-height: 1.5; }
.policy-list strong { color: var(--lime-300); }

/* Điều khoản */
.terms-content p { font-size: 14px; color: var(--chalk-200); opacity: .85; margin-bottom: 14px; }
.terms-content ol { padding-left: 20px; display: flex; flex-direction: column; gap: 10px; }
.terms-content li { font-size: 13.5px; color: var(--chalk-200); opacity: .75; line-height: 1.6; }

@media (max-width: 860px) {
  .ho-tro-layout { grid-template-columns: 1fr; }
  .ho-tro-nav { position: static; flex-direction: row; overflow-x: auto; }
}
</style>