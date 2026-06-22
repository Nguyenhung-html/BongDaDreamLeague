<template>
  <footer class="footer">
    <!-- Top -->
    <div class="footer__top">
      <div class="container footer__top-inner">
        <div class="footer__brand">
          <router-link to="/" class="footer__logo">
            <svg width="36" height="36" viewBox="0 0 38 38" fill="none">
              <circle cx="19" cy="19" r="18" stroke="var(--green-500)" stroke-width="2"/>
              <path d="M11 14L19 9L27 14L25 23L19 27L13 23L11 14Z" fill="white"/>
              <circle cx="19" cy="18" r="4.5" fill="var(--navy-900)"/>
            </svg>
            <span><strong>Dream</strong>League</span>
          </router-link>
          <p class="footer__tagline">
            Nền tảng đặt sân bóng hiện đại — kết nối người chơi với sân 5 và sân 7 chất lượng khắp TP.HCM.
          </p>
          <div class="footer__social">
            <a href="#" aria-label="Facebook" class="social-btn">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor"><path d="M18 2h-3a5 5 0 0 0-5 5v3H7v4h3v8h4v-8h3l1-4h-4V7a1 1 0 0 1 1-1h3z"/></svg>
            </a>
            <a href="#" aria-label="Instagram" class="social-btn">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="2" width="20" height="20" rx="5"/><circle cx="12" cy="12" r="4"/><circle cx="17.5" cy="6.5" r="1" fill="currentColor" stroke="none"/></svg>
            </a>
            <a href="#" aria-label="Zalo" class="social-btn">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
            </a>
            <a href="#" aria-label="YouTube" class="social-btn">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor"><path d="M22.54 6.42a2.78 2.78 0 0 0-1.95-1.96C18.88 4 12 4 12 4s-6.88 0-8.59.46A2.78 2.78 0 0 0 1.46 6.42 29 29 0 0 0 1 12a29 29 0 0 0 .46 5.58 2.78 2.78 0 0 0 1.95 1.96C5.12 20 12 20 12 20s6.88 0 8.59-.46a2.78 2.78 0 0 0 1.95-1.96A29 29 0 0 0 23 12a29 29 0 0 0-.46-5.58z"/><polygon points="9.75 15.02 15.5 12 9.75 8.98 9.75 15.02" fill="var(--navy-900)"/></svg>
            </a>
          </div>
        </div>

        <div class="footer__col">
          <h4>Khám phá</h4>
          <ul>
            <li><router-link to="/">Trang chủ</router-link></li>
            <li><router-link to="/san">Sân bóng</router-link></li>
            <li><router-link to="/dat-san">Đặt sân</router-link></li>
            <li><a href="#">Tin tức</a></li>
            <li><a href="#">Liên hệ</a></li>
          </ul>
        </div>

        <div class="footer__col">
          <h4>Loại sân</h4>
          <ul>
            <li><router-link to="/san">Sân 5 người</router-link></li>
            <li><router-link to="/san">Sân 7 người</router-link></li>
            <li><a href="#">Bảng giá thuê sân</a></li>
            <li><a href="#">Cho thuê sân cùng DreamLeague</a></li>
          </ul>
        </div>

        <div class="footer__col">
          <h4>Hỗ trợ</h4>
          <ul>
            <li><a href="#">Hướng dẫn đặt sân</a></li>
            <li><a href="#">Câu hỏi thường gặp</a></li>
            <li><a href="#">Chính sách huỷ sân</a></li>
            <li><a href="#">Điều khoản sử dụng</a></li>
          </ul>
        </div>

        <div class="footer__col">
          <h4>Liên hệ</h4>
          <ul class="footer__contact">
            <li>
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07A19.5 19.5 0 0 1 4.69 13 19.79 19.79 0 0 1 1.62 4.37 2 2 0 0 1 3.59 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l.91-.91a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 17.2z"/></svg>
              1900 6868
            </li>
            <li>
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/><polyline points="22,6 12,13 2,6"/></svg>
              hotro@dreamleague.vn
            </li>
            <li>
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></svg>
              Hẻm 104 Tân Sơn, Tân Sơn, Hồ Chí Minh
            </li>
          </ul>

          <div class="footer__app">
            <p>Tải ứng dụng</p>
            <div class="footer__app-btns">
              <a href="#" class="app-btn">App Store</a>
              <a href="#" class="app-btn">Google Play</a>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Bottom -->
    <div class="footer__bottom">
      <div class="container footer__bottom-inner">
        <p>© {{ year }} DreamLeague. Mọi quyền được bảo lưu.</p>
        <div class="footer__legal">
          <a href="#">Điều khoản sử dụng</a>
          <a href="#">Chính sách bảo mật</a>
          <a href="#">Chính sách cookie</a>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup>
const year = new Date().getFullYear()
</script>

<style scoped>
.footer {
  background: var(--navy-900);
  color: rgba(255, 255, 255, 0.65);
}

.footer__top {
  padding: 64px 0 48px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.footer__top-inner {
  display: grid;
  grid-template-columns: 1.8fr 1fr 1fr 1fr 1.3fr;
  gap: 36px;
}

.footer__logo {
  display: flex;
  align-items: center;
  gap: 10px;
  font-family: var(--font-score);
  font-size: 20px;
  color: var(--white);
}
.footer__logo strong { color: var(--green-500); }

.footer__tagline {
  margin-top: 14px;
  font-size: 13.5px;
  line-height: 1.75;
  max-width: 240px;
}

.footer__social {
  display: flex;
  gap: 8px;
  margin-top: 20px;
}

.social-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(255, 255, 255, 0.7);
  transition: background 0.15s, border-color 0.15s, color 0.15s;
}
.social-btn:hover {
  background: var(--green-600);
  border-color: var(--green-600);
  color: var(--white);
}

.footer__col h4 {
  color: var(--white);
  font-size: 14px;
  font-weight: 700;
  margin-bottom: 18px;
  letter-spacing: 0.02em;
}

.footer__col ul {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.footer__col li,
.footer__col a {
  font-size: 13.5px;
  line-height: 1.5;
}

.footer__col a:hover {
  color: var(--green-500);
}

.footer__contact li {
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.footer__contact svg {
  flex-shrink: 0;
  margin-top: 2px;
  color: var(--green-500);
}

.footer__app {
  margin-top: 20px;
}

.footer__app p {
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  color: var(--white);
  margin-bottom: 8px;
}

.footer__app-btns {
  display: flex;
  gap: 8px;
}

.app-btn {
  font-size: 12px;
  font-weight: 600;
  padding: 6px 14px;
  border-radius: 6px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: rgba(255, 255, 255, 0.8);
  transition: background 0.15s;
}
.app-btn:hover {
  background: rgba(255, 255, 255, 0.08);
  color: var(--white);
}

/* BOTTOM BAR */
.footer__bottom {
  padding: 18px 0;
}

.footer__bottom-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
  font-size: 12.5px;
}

.footer__legal {
  display: flex;
  gap: 20px;
}

.footer__legal a:hover {
  color: var(--green-500);
}

@media (max-width: 1100px) {
  .footer__top-inner { grid-template-columns: 1fr 1fr 1fr; }
  .footer__brand { grid-column: 1 / -1; }
}

@media (max-width: 640px) {
  .footer__top-inner { grid-template-columns: 1fr 1fr; }
}

@media (max-width: 420px) {
  .footer__top-inner { grid-template-columns: 1fr; }
}
</style>