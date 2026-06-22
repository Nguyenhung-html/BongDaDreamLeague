<template>
  <div class="trang-chu">

    <!-- ===== BANNER SLIDER ===== -->
    <section class="banner">
      <div class="banner__slides">
        <div
          v-for="(slide, i) in slides"
          :key="i"
          class="banner__slide"
          :class="{ 'banner__slide--active': slideHienTai === i }"
          :style="{ background: slide.bg }"
        >
          <div class="container banner__content">
            <div class="banner__copy">
              <span class="banner__tag">{{ slide.tag }}</span>
              <h1 class="banner__title">{{ slide.title }}</h1>
              <p class="banner__desc">{{ slide.desc }}</p>
              <div class="banner__cta">
                <router-link to="/san" class="btn-banner-primary">{{ slide.btn }}</router-link>
                <router-link to="/san" class="btn-banner-ghost">Xem tất cả sân →</router-link>
              </div>
            </div>
            <div class="banner__visual">
              <div class="pitch-3d">
                <svg viewBox="0 0 340 200" fill="none">
                  <rect x="4" y="4" width="332" height="192" rx="12" fill="#1a8a36"/>
                  <rect x="4" y="4" width="332" height="192" rx="12" stroke="rgba(255,255,255,0.3)" stroke-width="2"/>
                  <line x1="170" y1="4" x2="170" y2="196" stroke="rgba(255,255,255,0.4)" stroke-width="1.5"/>
                  <circle cx="170" cy="100" r="28" stroke="rgba(255,255,255,0.45)" stroke-width="1.5" fill="none"/>
                  <circle cx="170" cy="100" r="3" fill="rgba(255,255,255,0.7)"/>
                  <rect x="4" y="72" width="32" height="56" rx="2" stroke="rgba(255,255,255,0.45)" stroke-width="1.5" fill="none"/>
                  <rect x="4" y="84" width="18" height="32" rx="1" stroke="rgba(255,255,255,0.3)" stroke-width="1.5" fill="none"/>
                  <rect x="304" y="72" width="32" height="56" rx="2" stroke="rgba(255,255,255,0.45)" stroke-width="1.5" fill="none"/>
                  <rect x="318" y="84" width="18" height="32" rx="1" stroke="rgba(255,255,255,0.3)" stroke-width="1.5" fill="none"/>
                  <rect x="4" y="40" width="90" height="120" rx="4" stroke="rgba(255,255,255,0.25)" stroke-width="1" fill="none"/>
                  <rect x="246" y="40" width="90" height="120" rx="4" stroke="rgba(255,255,255,0.25)" stroke-width="1" fill="none"/>
                  <circle cx="36" cy="100" r="3" fill="rgba(255,255,255,0.5)"/>
                  <circle cx="304" cy="100" r="3" fill="rgba(255,255,255,0.5)"/>
                  <circle cx="170" cy="100" r="14" fill="white" opacity="0.9"/>
                  <path d="M160 94 L170 86 L180 94 L177 106 L163 106Z" fill="#222" opacity="0.15"/>
                </svg>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="banner__dots">
        <button
          v-for="(_, i) in slides"
          :key="i"
          class="banner__dot"
          :class="{ 'banner__dot--active': slideHienTai === i }"
          @click="slideHienTai = i"
        ></button>
      </div>
    </section>

    <!-- ===== THÔNG TIN SÂN NỔI BẬT (thay thanh tìm kiếm cũ) ===== -->
    <section class="san-highlight">
      <div class="container san-highlight__inner">
        <div class="san-highlight__item">
          <div class="san-highlight__icon">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none"><path d="M12 22s7-7.58 7-12.5A7 7 0 1 0 5 9.5C5 14.42 12 22 12 22Z" stroke="var(--green-600)" stroke-width="1.8"/><circle cx="12" cy="9.5" r="2.5" stroke="var(--green-600)" stroke-width="1.8"/></svg>
          </div>
          <div>
            <p class="san-highlight__label">Địa chỉ</p>
            <p class="san-highlight__val">Hẻm 104 Tân Sơn, Tân Sơn, Hồ Chí Minh</p>
          </div>
        </div>
        <div class="san-highlight__divider"></div>
        <div class="san-highlight__item">
          <div class="san-highlight__icon">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="9" stroke="var(--green-600)" stroke-width="1.8"/><path d="M12 7V12L15 14" stroke="var(--green-600)" stroke-width="1.8" stroke-linecap="round"/></svg>
          </div>
          <div>
            <p class="san-highlight__label">Giờ hoạt động</p>
            <p class="san-highlight__val">06:00 — 22:00 (Tất cả các ngày)</p>
          </div>
        </div>
        <div class="san-highlight__divider"></div>
        <div class="san-highlight__item">
          <div class="san-highlight__icon">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none"><rect x="2" y="3" width="20" height="18" rx="2" stroke="var(--green-600)" stroke-width="1.8"/><line x1="12" y1="3" x2="12" y2="21" stroke="var(--green-600)" stroke-width="1.8"/></svg>
          </div>
          <div>
            <p class="san-highlight__label">Số sân hiện có</p>
            <p class="san-highlight__val">3 sân 5 (A1, A2, A3) &nbsp;·&nbsp; 3 sân 7 (B1, B2, B3)</p>
          </div>
        </div>
        <div class="san-highlight__divider"></div>
        <div class="san-highlight__item">
          <div class="san-highlight__icon">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none"><path d="M22 16.92v3a2 2 0 0 1-2.18 2A19.79 19.79 0 0 1 11 17.65a19.5 19.5 0 0 1-6-6A19.79 19.79 0 0 1 2.12 2.18 2 2 0 0 1 4.11 0h3a2 2 0 0 1 2 1.72c.127.96.361 1.903.7 2.81a2 2 0 0 1-.45 2.11L8.09 7.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45c.907.339 1.85.573 2.81.7A2 2 0 0 1 22 16.92z" stroke="var(--green-600)" stroke-width="1.8"/></svg>
          </div>
          <div>
            <p class="san-highlight__label">Hotline đặt sân</p>
            <p class="san-highlight__val">0901 234 567 &nbsp;·&nbsp; Hỗ trợ 24/7</p>
          </div>
        </div>
      </div>
    </section>

    <!-- ===== THỐNG KÊ ===== -->
    <section class="stats">
      <div class="container stats__grid">
        <div class="stat-item" v-for="s in thongKe" :key="s.label">
          <div class="stat-item__icon" v-html="s.icon"></div>
          <div>
            <p class="stat-item__num">{{ s.num }}<span>{{ s.suffix }}</span></p>
            <p class="stat-item__label">{{ s.label }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- ===== LOẠI SÂN ===== -->
    <section class="loai-san">
      <div class="container">
        <div class="section-head">
          <div>
            <span class="eyebrow">Loại sân</span>
            <h2 class="section-title">2 loại sân tại DreamLeague</h2>
          </div>
          <router-link to="/san" class="see-all">Xem tất cả →</router-link>
        </div>

        <div class="loai-san__grid">
          <div
            class="loai-san__card"
            v-for="ls in loaiSanList"
            :key="ls.id"
            @click="router.push('/san')"
          >
            <div class="loai-san__card-top" :style="{ background: ls.bg }">
              <svg viewBox="0 0 320 180" class="pitch-svg">
                <rect x="4" y="4" width="312" height="172" rx="10" fill="#1a8a36"/>
                <rect x="4" y="4" width="312" height="172" rx="10" fill="none" stroke="rgba(255,255,255,0.3)" stroke-width="2"/>
                <line x1="160" y1="4" x2="160" y2="176" stroke="rgba(255,255,255,0.4)" stroke-width="1.5"/>
                <circle cx="160" cy="90" r="24" stroke="rgba(255,255,255,0.45)" stroke-width="1.5" fill="none"/>
                <rect x="4" y="65" width="28" height="50" stroke="rgba(255,255,255,0.4)" stroke-width="1.5" fill="none"/>
                <rect x="288" y="65" width="28" height="50" stroke="rgba(255,255,255,0.4)" stroke-width="1.5" fill="none"/>
              </svg>
              <div class="loai-san__badge">{{ ls.badge }}</div>
              <div class="loai-san__price-tag">Từ {{ ls.giaFrom }}</div>
            </div>
            <div class="loai-san__card-body">
              <h3>{{ ls.title }}</h3>
              <p>{{ ls.mo_ta }}</p>

              <!-- Danh sách tên sân -->
              <div class="loai-san__ten-san">
                <span
                  v-for="ten in ls.tenSan"
                  :key="ten"
                  class="loai-san__san-tag"
                >{{ ten }}</span>
              </div>

              <ul>
                <li v-for="spec in ls.specs" :key="spec">{{ spec }}</li>
              </ul>
              <router-link to="/san" class="loai-san__btn">Đặt sân ngay</router-link>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- ===== SÂN NỔI BẬT ===== -->
    <section class="san-noi-bat">
      <div class="container">
        <div class="section-head">
          <div>
            <span class="eyebrow">Đặt nhiều nhất</span>
            <h2 class="section-title">Sân nổi bật tuần này</h2>
          </div>
          <router-link to="/san" class="see-all">Xem tất cả →</router-link>
        </div>

        <div class="san-grid">
          <div class="san-card" v-for="san in sanNoiBat" :key="san.id">
            <div class="san-card__media" :style="{ background: san.mau }">
              <span class="san-card__loai">{{ san.loai }}</span>
              <span class="san-card__rating">
                <svg width="12" height="12" viewBox="0 0 24 24" fill="#f6c90e"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/></svg>
                {{ san.rating }}
              </span>
              <button class="san-card__fav">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/></svg>
              </button>
            </div>
            <div class="san-card__body">
              <div class="san-card__header-row">
                <h3 class="san-card__ten">{{ san.ten }}</h3>
                <span class="san-card__ma-san">{{ san.maSan }}</span>
              </div>
              <p class="san-card__dia-chi">
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none"><path d="M12 22s7-7.58 7-12.5A7 7 0 1 0 5 9.5C5 14.42 12 22 12 22Z" stroke="var(--gray-500)" stroke-width="2"/></svg>
                {{ san.diaChi }}
              </p>
              <div class="san-card__footer">
                <div class="san-card__gia">
                  <span class="san-card__gia-label">Từ</span>
                  <strong>{{ san.gia }}đ</strong>
                  <span class="san-card__gia-label">/giờ</span>
                </div>
                <router-link :to="`/dat-san/${san.id}`" class="san-card__btn">Đặt sân</router-link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- ===== CÁCH ĐẶT SÂN ===== -->
    <section class="how-it-works">
      <div class="container">
        <div class="section-head section-head--center">
          <span class="eyebrow">Quy trình</span>
          <h2 class="section-title">Đặt sân chỉ 3 bước</h2>
          <p class="section-sub">Nhanh chóng, tiện lợi — không cần gọi điện</p>
        </div>

        <div class="steps">
          <div class="step" v-for="(s, i) in cacBuoc" :key="s.title">
            <div class="step__circle">
              <div class="step__num">{{ i + 1 }}</div>
              <div class="step__icon" v-html="s.icon"></div>
            </div>
            <div v-if="i < cacBuoc.length - 1" class="step__line"></div>
            <div class="step__body">
              <h3>{{ s.title }}</h3>
              <p>{{ s.desc }}</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- ===== ĐÁNH GIÁ ===== -->
    <section class="danh-gia">
      <div class="container">
        <div class="section-head section-head--center">
          <span class="eyebrow">Đánh giá</span>
          <h2 class="section-title">Người chơi nói gì?</h2>
        </div>

        <div class="review-grid">
          <div class="review-card" v-for="r in danhGia" :key="r.name">
            <div class="review-card__stars">
              <svg v-for="n in 5" :key="n" width="14" height="14" viewBox="0 0 24 24" fill="#f6c90e"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/></svg>
            </div>
            <p class="review-card__quote">"{{ r.quote }}"</p>
            <div class="review-card__author">
              <div class="review-card__avatar">{{ r.initials }}</div>
              <div>
                <p class="review-card__name">{{ r.name }}</p>
                <p class="review-card__role">{{ r.role }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- ===== CTA ===== -->
    <section class="cta-section">
      <div class="container cta-section__inner">
        <div class="cta-section__left">
          <h2>Sẵn sàng cho trận đấu tiếp theo?</h2>
          <p>Đăng ký miễn phí — đặt sân trong 30 giây</p>
        </div>
        <div class="cta-section__right">
          <router-link to="/dang-ky" class="btn-banner-primary">Tạo tài khoản miễn phí</router-link>
          <router-link to="/san" class="btn-banner-ghost btn-banner-ghost--dark">Tìm sân ngay</router-link>
        </div>
      </div>
    </section>

  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// BANNER SLIDER
const slides = [
  {
    tag: '⚽ Sân 5 người & Sân 7 người',
    title: 'DREAM FOOTBALL FIELD',
    desc: 'Đặt sân nhanh chóng — Thanh toán tiện lợi — Hỗ trợ 24/7',
    btn: 'Đặt sân ngay',
    bg: 'linear-gradient(135deg, #0a2540 0%, #163e6b 60%, #1f7a34 100%)'
  },
  {
    tag: '🏆 Giải đấu phong trào',
    title: 'SÂN CHUẨN — GIÁ TỐT',
    desc: 'Hẻm 104 Tân Sơn, Tân Sơn, Hồ Chí Minh — Mở cửa 06:00 đến 22:00',
    btn: 'Xem sân ngay',
    bg: 'linear-gradient(135deg, #1f7a34 0%, #2c9b41 60%, #0a2540 100%)'
  }
]
const slideHienTai = ref(0)
let slideInterval = null

onMounted(() => {
  slideInterval = setInterval(() => {
    slideHienTai.value = (slideHienTai.value + 1) % slides.length
  }, 4000)
})
onUnmounted(() => clearInterval(slideInterval))

// DATA
const thongKe = [
  {
    num: '6', suffix: ' sân', label: 'Sân hiện có',
    icon: `<svg width="22" height="22" viewBox="0 0 24 24" fill="none"><rect x="2" y="3" width="20" height="18" rx="2" stroke="var(--green-600)" stroke-width="1.8"/><line x1="12" y1="3" x2="12" y2="21" stroke="var(--green-600)" stroke-width="1.8"/></svg>`
  },
  {
    num: '35', suffix: 'k+', label: 'Lượt đặt sân',
    icon: `<svg width="22" height="22" viewBox="0 0 24 24" fill="none"><rect x="3" y="5" width="18" height="16" rx="2" stroke="var(--green-600)" stroke-width="1.8"/><path d="M3 10H21M8 3v4M16 3v4" stroke="var(--green-600)" stroke-width="1.8" stroke-linecap="round"/></svg>`
  },
  {
    num: '4.8', suffix: '/5', label: 'Đánh giá TB',
    icon: `<svg width="22" height="22" viewBox="0 0 24 24" fill="none"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z" stroke="var(--green-600)" stroke-width="1.8" stroke-linejoin="round"/></svg>`
  },
  {
    num: '24', suffix: '/7', label: 'Hỗ trợ',
    icon: `<svg width="22" height="22" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="9" stroke="var(--green-600)" stroke-width="1.8"/><path d="M12 7V12L15 14" stroke="var(--green-600)" stroke-width="1.8" stroke-linecap="round"/></svg>`
  }
]

const loaiSanList = [
  {
    id: 5,
    badge: 'SÂN 5',
    title: 'Sân 5 người',
    mo_ta: 'Lý tưởng cho tụ tập bạn bè, đồng nghiệp sau giờ làm. Trận đấu nhanh, sôi nổi.',
    tenSan: ['A1', 'A2', 'A3'],
    specs: ['Kích thước ~25 × 16m', 'Cỏ nhân tạo cao cấp', 'Đèn chiếu sáng buổi tối'],
    giaFrom: '350.000đ/giờ',
    bg: 'linear-gradient(135deg, #0a2540, #163e6b)'
  },
  {
    id: 7,
    badge: 'SÂN 7',
    title: 'Sân 7 người',
    mo_ta: 'Chuẩn cho giải đấu phong trào, team building doanh nghiệp, trận giao hữu lớn.',
    tenSan: ['B1', 'B2', 'B3'],
    specs: ['Kích thước ~50 × 30m', 'Cỏ nhân tạo FIFA', 'Khán đài & phòng thay đồ'],
    giaFrom: '650.000đ/giờ',
    bg: 'linear-gradient(135deg, #1f7a34, #2c9b41)'
  }
]

const sanNoiBat = [
  { id: 1, ten: 'Sân 5 người', maSan: 'A1', loai: 'Sân 5', diaChi: 'Hẻm 104 Tân Sơn, Tân Sơn, HCM', rating: '4.9', gia: '350.000', mau: 'linear-gradient(135deg,#0a2540,#163e6b)' },
  { id: 2, ten: 'Sân 5 người', maSan: 'A2', loai: 'Sân 5', diaChi: 'Hẻm 104 Tân Sơn, Tân Sơn, HCM', rating: '4.8', gia: '350.000', mau: 'linear-gradient(135deg,#163e6b,#20517f)' },
  { id: 3, ten: 'Sân 5 người', maSan: 'A3', loai: 'Sân 5', diaChi: 'Hẻm 104 Tân Sơn, Tân Sơn, HCM', rating: '4.7', gia: '350.000', mau: 'linear-gradient(135deg,#0e3258,#0a2540)' },
  { id: 4, ten: 'Sân 7 người', maSan: 'B1', loai: 'Sân 7', diaChi: 'Hẻm 104 Tân Sơn, Tân Sơn, HCM', rating: '4.9', gia: '650.000', mau: 'linear-gradient(135deg,#1f7a34,#2c9b41)' },
  { id: 5, ten: 'Sân 7 người', maSan: 'B2', loai: 'Sân 7', diaChi: 'Hẻm 104 Tân Sơn, Tân Sơn, HCM', rating: '4.8', gia: '650.000', mau: 'linear-gradient(135deg,#2c9b41,#3fb454)' },
  { id: 6, ten: 'Sân 7 người', maSan: 'B3', loai: 'Sân 7', diaChi: 'Hẻm 104 Tân Sơn, Tân Sơn, HCM', rating: '4.8', gia: '650.000', mau: 'linear-gradient(135deg,#1a8a36,#1f7a34)' }
]

const cacBuoc = [
  {
    title: 'Chọn sân & khung giờ',
    desc: 'Tìm sân A1–A3 hoặc B1–B3, chọn loại sân 5 hoặc 7, xem lịch trống thực.',
    icon: `<svg width="24" height="24" viewBox="0 0 24 24" fill="none"><circle cx="11" cy="11" r="7" stroke="white" stroke-width="2"/><path d="M16.5 16.5L21 21" stroke="white" stroke-width="2" stroke-linecap="round"/></svg>`
  },
  {
    title: 'Xác nhận & thanh toán',
    desc: 'Điền thông tin, xác nhận đặt sân và thanh toán trực tuyến an toàn.',
    icon: `<svg width="24" height="24" viewBox="0 0 24 24" fill="none"><rect x="2" y="5" width="20" height="14" rx="2" stroke="white" stroke-width="2"/><path d="M2 10H22" stroke="white" stroke-width="2"/></svg>`
  },
  {
    title: 'Ra sân & tận hưởng',
    desc: 'Nhận mã xác nhận, đến Hẻm 104 Tân Sơn đúng giờ và bắt đầu trận đấu.',
    icon: `<svg width="24" height="24" viewBox="0 0 24 24" fill="none"><path d="M5 13L9.5 17.5L19 7" stroke="white" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/></svg>`
  }
]

const danhGia = [
  { quote: 'Đặt sân A2 cho nhóm công ty chỉ mất 1 phút, lịch trống cập nhật rất chính xác.', name: 'Minh Khôi', role: 'Đội bóng FPT FC', initials: 'MK' },
  { quote: 'Giá rõ ràng, không phát sinh thêm phí khi ra sân. Sẽ tiếp tục đặt qua DreamLeague.', name: 'Thuỳ Trang', role: 'CLB nữ Sài Gòn', initials: 'TT' },
  { quote: 'Sân B1 cỏ đẹp, đèn sáng, đặt được khung giờ tối cuối tuần dễ dàng hơn hẳn.', name: 'Anh Tuấn', role: 'Giải phong trào Tân Sơn', initials: 'AT' }
]
</script>

<style scoped>
/* ===== BANNER ===== */
.banner { position: relative; overflow: hidden; }
.banner__slides { position: relative; height: 380px; }
.banner__slide {
  position: absolute; inset: 0; opacity: 0;
  transition: opacity 0.6s ease; display: flex; align-items: center;
}
.banner__slide--active { opacity: 1; }
.banner__content {
  display: grid; grid-template-columns: 1fr 400px; gap: 40px; align-items: center;
}
.banner__tag {
  display: inline-block;
  background: rgba(63,180,84,0.25); color: var(--green-500);
  border: 1px solid rgba(63,180,84,0.4);
  padding: 5px 14px; border-radius: 999px; font-size: 13px; font-weight: 600;
}
.banner__title {
  font-family: var(--font-score); font-size: clamp(30px, 4vw, 52px);
  font-weight: 700; color: var(--white); line-height: 1.15; margin-top: 14px;
  text-shadow: 0 2px 12px rgba(0,0,0,0.3);
}
.banner__desc { margin-top: 14px; font-size: 15.5px; color: rgba(255,255,255,0.75); line-height: 1.65; }
.banner__cta { display: flex; gap: 12px; margin-top: 28px; flex-wrap: wrap; }

.btn-banner-primary {
  display: inline-flex; align-items: center; gap: 6px;
  padding: 13px 28px; background: var(--green-600); color: var(--white);
  border-radius: 999px; font-weight: 700; font-size: 15px;
  box-shadow: 0 8px 24px rgba(44,155,65,0.4); transition: background 0.15s, transform 0.15s;
}
.btn-banner-primary:hover { background: var(--green-700); transform: translateY(-1px); }

.btn-banner-ghost {
  display: inline-flex; align-items: center; padding: 13px 24px;
  background: rgba(255,255,255,0.1); color: var(--white);
  border-radius: 999px; font-weight: 600; font-size: 15px;
  border: 1.5px solid rgba(255,255,255,0.3); transition: background 0.15s;
}
.btn-banner-ghost:hover { background: rgba(255,255,255,0.18); }
.btn-banner-ghost--dark {
  background: rgba(10,37,64,0.08); color: var(--navy-900); border-color: var(--navy-900);
}
.btn-banner-ghost--dark:hover { background: var(--navy-900); color: white; }

.pitch-3d svg { width: 100%; height: auto; filter: drop-shadow(0 20px 40px rgba(0,0,0,0.4)); }

.banner__dots {
  position: absolute; bottom: 16px; left: 50%; transform: translateX(-50%);
  display: flex; gap: 8px;
}
.banner__dot {
  width: 8px; height: 8px; border-radius: 50%;
  background: rgba(255,255,255,0.35); border: none; cursor: pointer;
  transition: background 0.2s, width 0.2s;
}
.banner__dot--active { background: var(--white); width: 24px; border-radius: 999px; }

/* ===== SAN HIGHLIGHT (thay search section) ===== */
.san-highlight {
  background: var(--white);
  box-shadow: 0 6px 24px rgba(10,37,64,0.1);
  position: relative; z-index: 10;
}

.san-highlight__inner {
  display: flex; align-items: stretch; min-height: 80px;
}

.san-highlight__item {
  display: flex; align-items: center; gap: 14px;
  padding: 20px 28px; flex: 1;
}

.san-highlight__icon {
  width: 44px; height: 44px; border-radius: 12px;
  background: var(--green-50); display: flex; align-items: center;
  justify-content: center; flex-shrink: 0;
}

.san-highlight__label {
  font-size: 11px; font-weight: 700; text-transform: uppercase;
  letter-spacing: 0.07em; color: var(--gray-500); margin-bottom: 4px;
}

.san-highlight__val {
  font-size: 13.5px; font-weight: 600; color: var(--navy-900); line-height: 1.4;
}

.san-highlight__divider {
  width: 1px; background: var(--gray-100); margin: 16px 0;
}

@media (max-width: 960px) {
  .san-highlight__inner { flex-direction: column; }
  .san-highlight__item { border-bottom: 1px solid var(--gray-100); }
  .san-highlight__divider { display: none; }
}

/* ===== STATS ===== */
.stats { padding: 32px 0; background: var(--gray-50); }
.stats__grid {
  display: grid; grid-template-columns: repeat(4, 1fr);
  background: var(--white); border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card); overflow: hidden;
}
.stat-item {
  display: flex; align-items: center; gap: 14px;
  padding: 24px 28px; border-right: 1px solid var(--gray-100);
}
.stat-item:last-child { border-right: none; }
.stat-item__icon {
  width: 46px; height: 46px; background: var(--green-50); border-radius: 12px;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.stat-item__num { font-family: var(--font-score); font-size: 28px; font-weight: 600; color: var(--navy-900); line-height: 1; }
.stat-item__num span { color: var(--green-600); }
.stat-item__label { font-size: 12.5px; color: var(--gray-600); margin-top: 4px; }
@media (max-width: 700px) {
  .stats__grid { grid-template-columns: repeat(2, 1fr); }
  .stat-item:nth-child(2) { border-right: none; }
}

/* ===== LOẠI SÂN ===== */
.loai-san { padding: 72px 0; background: var(--white); }
.section-head {
  display: flex; align-items: flex-end; justify-content: space-between;
  margin-bottom: 36px; flex-wrap: wrap; gap: 12px;
}
.section-head--center { flex-direction: column; align-items: center; text-align: center; }
.section-head--center .section-sub { margin-top: 10px; font-size: 15.5px; color: var(--gray-600); }
.see-all { font-weight: 600; font-size: 14px; color: var(--green-600); }

.loai-san__grid { display: grid; grid-template-columns: 1fr 1fr; gap: 24px; }
.loai-san__card {
  border-radius: var(--radius-lg); overflow: hidden;
  box-shadow: var(--shadow-card); cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}
.loai-san__card:hover { transform: translateY(-4px); box-shadow: var(--shadow-card-hover); }
.loai-san__card-top { position: relative; padding: 20px; height: 180px; }
.pitch-svg { width: 100%; height: 100%; object-fit: cover; }
.loai-san__badge {
  position: absolute; top: 16px; left: 16px;
  background: white; color: var(--navy-900);
  font-family: var(--font-score); font-size: 12px; font-weight: 700;
  letter-spacing: 0.05em; padding: 4px 12px; border-radius: 999px;
}
.loai-san__price-tag {
  position: absolute; bottom: 16px; right: 16px;
  background: rgba(0,0,0,0.45); color: white;
  font-size: 13px; font-weight: 600; padding: 5px 12px; border-radius: 999px;
}
.loai-san__card-body { background: var(--white); padding: 24px; }
.loai-san__card-body h3 { font-size: 20px; font-weight: 700; color: var(--navy-900); }
.loai-san__card-body p { margin-top: 8px; font-size: 14px; line-height: 1.65; color: var(--gray-600); }

/* Tags tên sân A1/A2/A3 hay B1/B2/B3 */
.loai-san__ten-san {
  display: flex; gap: 8px; flex-wrap: wrap; margin-top: 14px;
}
.loai-san__san-tag {
  padding: 5px 14px;
  background: var(--green-50);
  color: var(--green-700);
  border: 1.5px solid var(--green-200, #bbf7d0);
  border-radius: 999px;
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.04em;
}

.loai-san__card-body ul { margin-top: 14px; display: flex; flex-direction: column; gap: 6px; }
.loai-san__card-body li {
  font-size: 13.5px; color: var(--navy-900); padding-left: 18px; position: relative;
}
.loai-san__card-body li::before {
  content: ''; position: absolute; left: 0; top: 7px;
  width: 7px; height: 7px; border-radius: 50%; background: var(--green-600);
}
.loai-san__btn {
  display: inline-flex; align-items: center; margin-top: 20px;
  padding: 11px 24px; background: var(--navy-900); color: var(--white);
  border-radius: 999px; font-size: 14px; font-weight: 600; transition: background 0.15s;
}
.loai-san__btn:hover { background: var(--green-600); }
@media (max-width: 700px) { .loai-san__grid { grid-template-columns: 1fr; } }

/* ===== SÂN NỔI BẬT ===== */
.san-noi-bat { padding: 72px 0; background: var(--gray-50); }
.san-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; }
.san-card {
  background: var(--white); border-radius: var(--radius-md);
  overflow: hidden; box-shadow: var(--shadow-card);
  transition: transform 0.2s, box-shadow 0.2s;
}
.san-card:hover { transform: translateY(-4px); box-shadow: var(--shadow-card-hover); }
.san-card__media { height: 140px; position: relative; padding: 12px; }
.san-card__loai {
  background: rgba(255,255,255,0.92); color: var(--navy-900);
  font-size: 11px; font-weight: 700; padding: 3px 10px; border-radius: 999px;
}
.san-card__rating {
  position: absolute; bottom: 12px; left: 12px;
  display: flex; align-items: center; gap: 4px;
  background: rgba(0,0,0,0.45); color: white;
  font-size: 12px; font-weight: 600; padding: 3px 8px; border-radius: 999px;
}
.san-card__fav {
  position: absolute; top: 12px; right: 12px;
  width: 30px; height: 30px; border-radius: 50%;
  background: rgba(0,0,0,0.3); border: none;
  display: flex; align-items: center; justify-content: center; cursor: pointer;
}
.san-card__fav:hover { background: rgba(229,62,62,0.7); }
.san-card__body { padding: 16px 18px 18px; }

.san-card__header-row {
  display: flex; align-items: center; justify-content: space-between; gap: 8px;
}
.san-card__ten { font-size: 15px; font-weight: 700; color: var(--navy-900); }
.san-card__ma-san {
  background: var(--green-600); color: white;
  font-size: 12px; font-weight: 700; padding: 3px 10px;
  border-radius: 999px; flex-shrink: 0;
}

.san-card__dia-chi {
  display: flex; align-items: center; gap: 4px;
  margin-top: 5px; font-size: 12.5px; color: var(--gray-600);
}
.san-card__footer {
  display: flex; align-items: center; justify-content: space-between;
  margin-top: 14px; padding-top: 12px; border-top: 1px solid var(--gray-100);
}
.san-card__gia { display: flex; align-items: baseline; gap: 3px; }
.san-card__gia strong { font-size: 16px; font-weight: 700; color: var(--navy-900); }
.san-card__gia-label { font-size: 12px; color: var(--gray-500); }
.san-card__btn {
  padding: 7px 16px; background: var(--green-600); color: white;
  border-radius: 999px; font-size: 13px; font-weight: 600; transition: background 0.15s;
}
.san-card__btn:hover { background: var(--green-700); }
@media (max-width: 960px) { .san-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 560px) { .san-grid { grid-template-columns: 1fr; } }

/* ===== STEPS ===== */
.how-it-works { padding: 80px 0; background: var(--navy-900); }
.how-it-works .eyebrow { color: var(--green-500); }
.how-it-works .eyebrow::before { background: var(--green-500); }
.how-it-works .section-title { color: var(--white); }
.how-it-works .section-sub { color: rgba(255,255,255,0.6); margin-top: 8px; font-size: 15px; }
.steps { display: flex; align-items: flex-start; margin-top: 56px; }
.step { flex: 1; display: flex; flex-direction: column; align-items: center; text-align: center; position: relative; }
.step__circle {
  width: 68px; height: 68px; border-radius: 50%; background: var(--green-600);
  display: flex; align-items: center; justify-content: center; position: relative;
  box-shadow: 0 8px 24px rgba(44,155,65,0.4);
}
.step__num {
  position: absolute; top: -8px; right: -4px;
  width: 22px; height: 22px; background: var(--white); color: var(--navy-900);
  font-size: 11px; font-weight: 700; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
}
.step__line {
  position: absolute; top: 34px;
  left: calc(50% + 40px); right: calc(-50% + 40px);
  height: 2px; background: rgba(255,255,255,0.12);
}
.step__body { margin-top: 20px; padding: 0 16px; }
.step__body h3 { font-size: 16px; font-weight: 700; color: var(--white); }
.step__body p { margin-top: 8px; font-size: 13.5px; line-height: 1.65; color: rgba(255,255,255,0.6); }
@media (max-width: 700px) { .steps { flex-direction: column; gap: 32px; } .step__line { display: none; } }

/* ===== ĐÁNH GIÁ ===== */
.danh-gia { padding: 72px 0; background: var(--gray-50); }
.review-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; margin-top: 40px; }
.review-card { background: var(--white); border-radius: var(--radius-md); padding: 26px 22px; box-shadow: var(--shadow-card); }
.review-card__stars { display: flex; gap: 2px; margin-bottom: 14px; }
.review-card__quote { font-size: 14px; line-height: 1.75; color: var(--navy-900); font-style: italic; }
.review-card__author {
  display: flex; align-items: center; gap: 10px;
  margin-top: 18px; padding-top: 16px; border-top: 1px solid var(--gray-100);
}
.review-card__avatar {
  width: 38px; height: 38px; border-radius: 50%;
  background: var(--green-100); color: var(--green-700);
  font-weight: 700; font-size: 13px;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.review-card__name { font-size: 14px; font-weight: 700; color: var(--navy-900); }
.review-card__role { font-size: 12px; color: var(--gray-500); margin-top: 2px; }
@media (max-width: 820px) { .review-grid { grid-template-columns: 1fr; } }

/* ===== CTA ===== */
.cta-section { padding: 56px 0; background: var(--white); }
.cta-section__inner {
  background: linear-gradient(135deg, var(--navy-900), var(--navy-700));
  border-radius: var(--radius-lg); padding: 48px 52px;
  display: flex; align-items: center; justify-content: space-between; gap: 24px; flex-wrap: wrap;
}
.cta-section__left h2 { font-size: 26px; font-weight: 700; color: var(--white); }
.cta-section__left p { margin-top: 8px; font-size: 15px; color: rgba(255,255,255,0.65); }
.cta-section__right { display: flex; gap: 12px; flex-wrap: wrap; }
@media (max-width: 700px) { .cta-section__inner { padding: 32px 24px; } }

@media (max-width: 780px) {
  .banner__content { grid-template-columns: 1fr; }
  .pitch-3d { display: none; }
  .banner__slides { height: 300px; }
}
</style>