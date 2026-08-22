<template>
  <footer class="footer">
    <div class="footer__ambient" aria-hidden="true">
      <div class="footer__floodlight footer__floodlight--l"></div>
      <div class="footer__floodlight footer__floodlight--r"></div>
    </div>

    <!-- Top -->
    <div class="footer__top">
      <div class="container footer__top-inner">
        <div class="footer__brand">
          <router-link to="/" class="footer__logo">
            <svg width="36" height="36" viewBox="0 0 38 38" fill="none">
              <circle cx="19" cy="19" r="18" stroke="var(--lime-400)" stroke-width="2"/>
              <path d="M11 14L19 9L27 14L25 23L19 27L13 23L11 14Z" fill="var(--chalk-050)"/>
              <circle cx="19" cy="18" r="4.5" fill="var(--night-950)"/>
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
              <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor"><path d="M22.54 6.42a2.78 2.78 0 0 0-1.95-1.96C18.88 4 12 4 12 4s-6.88 0-8.59.46A2.78 2.78 0 0 0 1.46 6.42 29 29 0 0 0 1 12a29 29 0 0 0 .46 5.58 2.78 2.78 0 0 0 1.95 1.96C5.12 20 12 20 12 20s6.88 0 8.59-.46a2.78 2.78 0 0 0 1.95-1.96A29 29 0 0 0 23 12a29 29 0 0 0-.46-5.58z"/><polygon points="9.75 15.02 15.5 12 9.75 8.98 9.75 15.02" fill="var(--night-950)"/></svg>
            </a>
          </div>
        </div>

        <div class="footer__col">
          <h4>Khám phá</h4>
          <ul>
            <li><router-link to="/">Trang chủ</router-link></li>
            <li><router-link to="/san">Sân bóng</router-link></li>
            <li><router-link to="/san">Đặt sân</router-link></li>
          </ul>
        </div>

        <div class="footer__col">
          <h4>Loại sân</h4>
          <ul>
            <li><router-link to="/san">Sân 5 người</router-link></li>
            <li><router-link to="/san">Sân 7 người</router-link></li>
          </ul>
        </div>

        <div class="footer__col">
          <h4>Hỗ trợ</h4>
          <ul>
            <li><router-link to="/ho-tro#huong-dan">Hướng dẫn đặt sân</router-link></li>
            <li><router-link to="/ho-tro#faq">Câu hỏi thường gặp</router-link></li>
            <li><router-link to="/ho-tro#chinh-sach-huy">Chính sách huỷ sân</router-link></li>
            <li><router-link to="/ho-tro#dieu-khoan">Điều khoản sử dụng</router-link></li>
          </ul>
        </div>

        <div class="footer__col">
          <h4>Liên hệ</h4>
          <ul class="footer__contact">
            <li>
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07A19.5 19.5 0 0 1 4.69 13 19.79 19.79 0 0 1 1.62 4.37 2 2 0 0 1 3.59 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l.91-.91a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 17.2z"/></svg>
              0343 831 316
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

<style>
@import url('https://fonts.googleapis.com/css2?family=Bebas+Neue&family=Oswald:wght@500;600;700&family=Manrope:wght@400;500;600;700;800&family=Space+Mono:wght@400;700&display=swap');
</style>

<style scoped>
.footer {
  --night-950: #050b08;
  --night-800: #0a1f13;
  --night-700: #123321;
  --turf-500: #23935a;
  --turf-700: #146239;
  --lime-400: #b6ff3c;
  --lime-300: #d3ff8f;
  --chalk-050: #f7fbf4;
  --chalk-200: #e3ecdf;
  --font-display: 'Oswald', 'Manrope', sans-serif;
  --font-mono: 'Space Mono', monospace;

  position: relative;
  overflow: hidden;
  background: linear-gradient(180deg, var(--night-800), var(--night-950));
  color: var(--chalk-200);
  font-family: 'Manrope', sans-serif;
  border-top: 1px solid rgba(182,255,60,.15);
}
.container { max-width: 1280px; margin: 0 auto; padding: 0 44px; }

.footer__ambient { position: absolute; inset: 0; pointer-events: none; z-index: 0; }
.footer__floodlight { position: absolute; top: -30%; width: 40vh; height: 90vh; background: conic-gradient(from 90deg at 50% 0%, transparent 42%, rgba(182,255,60,.06) 50%, transparent 58%); mix-blend-mode: screen; }
.footer__floodlight--l { left: -8%; }
.footer__floodlight--r { right: -8%; }

.footer__top {
  position: relative; z-index: 1;
  padding: 64px 0 48px;
  border-bottom: 1px solid rgba(247,251,244,.08);
}
.footer__top-inner { display: grid; grid-template-columns: 1.8fr 1fr 1fr 1fr 1.3fr; gap: 36px; }

.footer__logo { display: flex; align-items: center; gap: 10px; font-family: var(--font-display); font-size: 20px; color: var(--chalk-050); }
.footer__logo strong { color: var(--lime-400); }
.footer__tagline { margin-top: 14px; font-size: 13.5px; line-height: 1.75; max-width: 260px; opacity: .75; }

.footer__social { display: flex; gap: 8px; margin-top: 20px; }
.social-btn {
  width: 36px; height: 36px; border-radius: 50%;
  border: 1px solid rgba(182,255,60,.3); background: rgba(182,255,60,.08);
  display: flex; align-items: center; justify-content: center; color: var(--lime-300);
  transition: background .15s, border-color .15s, color .15s, transform .15s;
}
.social-btn:hover { background: var(--lime-400); border-color: var(--lime-400); color: var(--night-950); transform: translateY(-3px); box-shadow: 0 8px 18px -6px rgba(182,255,60,.5); }

.footer__col h4 { color: var(--chalk-050); font-size: 14px; font-weight: 700; margin-bottom: 18px; letter-spacing: .02em; font-family: var(--font-mono); text-transform: uppercase; }
.footer__col ul { display: flex; flex-direction: column; gap: 10px; }
.footer__col li, .footer__col a { font-size: 13.5px; line-height: 1.5; color: var(--chalk-200); opacity: .8; }
.footer__col a { position: relative; transition: color .15s, opacity .15s; }
.footer__col a::after { content: ''; position: absolute; left: 0; bottom: -2px; width: 0; height: 1px; background: var(--lime-400); transition: width .2s ease; }
.footer__col a:hover { color: var(--lime-300); opacity: 1; }
.footer__col a:hover::after { width: 100%; }

.footer__contact li { display: flex; align-items: flex-start; gap: 8px; opacity: .85; }
.footer__contact svg { flex-shrink: 0; margin-top: 2px; color: var(--lime-400); }

.footer__bottom { position: relative; z-index: 1; padding: 18px 0; }
.footer__bottom-inner { display: flex; align-items: center; justify-content: space-between; gap: 16px; flex-wrap: wrap; font-size: 12.5px; opacity: .65; }
.footer__legal { display: flex; gap: 20px; }
.footer__legal a { position: relative; }
.footer__legal a::after { content: ''; position: absolute; left: 0; bottom: -2px; width: 0; height: 1px; background: var(--lime-400); transition: width .2s ease; }
.footer__legal a:hover { color: var(--lime-300); }
.footer__legal a:hover::after { width: 100%; }

@media (max-width: 1100px) {
  .footer__top-inner { grid-template-columns: 1fr 1fr 1fr; }
  .footer__brand { grid-column: 1 / -1; }
}
@media (max-width: 640px) { .footer__top-inner { grid-template-columns: 1fr 1fr; } }
@media (max-width: 420px) { .footer__top-inner { grid-template-columns: 1fr; } }
</style>