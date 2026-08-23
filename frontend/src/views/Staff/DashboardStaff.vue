<template>
  <div class="dash">
    <!-- Nền ambient đồng bộ trang chủ -->
    <div class="dash__ambient" aria-hidden="true">
      <!-- Sân bóng mờ làm nền, tránh trống trải -->
      <svg class="pitch-silhouette" viewBox="0 0 800 500" preserveAspectRatio="xMidYMid slice">
        <rect x="24" y="24" width="752" height="452" rx="16" class="ps-line"/>
        <line x1="400" y1="24" x2="400" y2="476" class="ps-line"/>
        <circle cx="400" cy="250" r="92" class="ps-line"/>
        <circle cx="400" cy="250" r="3.5" class="ps-dot"/>
        <rect x="24" y="130" width="130" height="240" class="ps-line"/>
        <rect x="646" y="130" width="130" height="240" class="ps-line"/>
        <rect x="24" y="190" width="46" height="120" class="ps-line"/>
        <rect x="730" y="190" width="46" height="120" class="ps-line"/>
        <path d="M64 24a40 40 0 0 1 0 0" class="ps-line"/>
      </svg>

      <div class="floodlight floodlight--l"></div>
      <div class="floodlight floodlight--r"></div>

      <!-- Hạt sáng bay lên -->
      <div class="motes"><span v-for="n in 14" :key="'m'+n" class="mote" :style="{ '--i': n }"></span></div>

      <!-- Hạt rơi nhẹ xuống, tạo chiều sâu -->
      <div class="falling"><span v-for="n in 18" :key="'f'+n" class="falling__item" :style="{ '--i': n }"></span></div>

      <!-- Đạo cụ bóng đá trôi nổi -->
      <div class="field-props">
        <div class="prop prop--ball1"><svg viewBox="0 0 40 40"><circle cx="20" cy="20" r="18" fill="var(--chalk-050)" stroke="var(--night-950)" stroke-width="1.4"/><polygon points="20,10 26,14.5 24,21.5 16,21.5 14,14.5" fill="var(--night-950)"/></svg></div>
        <div class="prop prop--ball2"><svg viewBox="0 0 40 40"><circle cx="20" cy="20" r="18" fill="var(--chalk-050)" stroke="var(--night-950)" stroke-width="1.4"/><polygon points="20,10 26,14.5 24,21.5 16,21.5 14,14.5" fill="var(--night-950)"/></svg></div>
        <div class="prop prop--whistle"><svg viewBox="0 0 48 32" fill="none"><path d="M14 16a10 10 0 1 0 20 0 10 10 0 0 0-20 0Z" stroke="var(--lime-300)" stroke-width="1.6"/><circle cx="24" cy="16" r="3.2" fill="var(--lime-300)"/></svg></div>
        <div class="prop prop--flag"><svg viewBox="0 0 30 46" fill="none"><path d="M4 44V4" stroke="var(--lime-300)" stroke-width="1.8" stroke-linecap="round"/><path d="M4 5l20 6-20 6V5Z" fill="var(--lime-300)" opacity=".85"/></svg></div>
        <div class="prop prop--trophy"><svg viewBox="0 0 40 46" fill="none"><path d="M11 6h18v10a9 9 0 0 1-18 0V6Z" stroke="var(--amber-400)" stroke-width="1.6"/><path d="M20 25v6M13 37h14l-1.6-4.5a2 2 0 0 0-1.9-1.5h-6.9a2 2 0 0 0-1.9 1.5L13 37Z" stroke="var(--amber-400)" stroke-width="1.6" stroke-linejoin="round"/></svg></div>
        <div class="prop prop--boot"><svg viewBox="0 0 48 34" fill="none"><path d="M8 6v11.5c0 1.5.8 2.4 2 3.2l9 5.8c1 .7 2.2 1 3.4 1h16.4c1.8 0 2.9-2 1.9-3.5l-3-4.5a6 6 0 0 0-3.8-2.5l-8.4-1.7a3 3 0 0 1-2-1.4L19.5 6.6A3 3 0 0 0 17 5h-6a3 3 0 0 0-3 1Z" stroke="var(--lime-300)" stroke-width="1.6" stroke-linejoin="round"/><path d="M8 22.5h30" stroke="var(--lime-300)" stroke-width="1.6"/></svg></div>
      </div>
    </div>

    <!-- Header -->
    <div class="dash__header reveal">
      <span class="eyebrow">{{ loiChao }} · {{ ngayHomNay }}</span>
      <h1 class="dash__title">Dashboard</h1>
      <p class="dash__desc">Tổng quan số liệu thật từ hệ thống — cập nhật theo dữ liệu hôm nay.</p>
    </div>

    <p v-if="loi" class="dash__error reveal">⚠️ {{ loi }}</p>

    <!-- ===== SỐ LIỆU THẬT ===== -->
    <div class="stat-grid">
      <div class="stat-card reveal" style="--d:0s">
        <div class="stat-card__icon">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none"><rect x="4" y="3" width="16" height="16" rx="1" stroke="currentColor" stroke-width="1.6"/><path d="M4 8.5h16M9 14h2M15 14h2" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"/></svg>
        </div>
        <p class="stat-card__label">Đặt sân hôm nay</p>
        <p class="stat-card__num">{{ dangTai ? '···' : hienThi.datSan }}</p>
      </div>

      <div class="stat-card reveal" style="--d:.08s">
        <div class="stat-card__icon stat-card__icon--lime">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none"><path d="M12 2v20M17 6.5c0-2-2.2-3.5-5-3.5s-5 1.7-5 4 2.2 3.5 5 3.5 5 1.5 5 3.5-2.2 4-5 4-5-1.5-5-3.5" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"/></svg>
        </div>
        <p class="stat-card__label">Doanh thu hôm nay</p>
        <p class="stat-card__num">{{ dangTai ? '···' : dinhDangTien(hienThi.doanhThu) }}</p>
      </div>

      <div class="stat-card reveal" style="--d:.16s" :class="{ 'stat-card--warning': !dangTai && thanhToanChoXuLy > 0 }">
        <div class="stat-card__icon stat-card__icon--warning">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none"><rect x="2" y="5" width="20" height="14" rx="1.5" stroke="currentColor" stroke-width="1.6"/><path d="M2 9.5h20" stroke="currentColor" stroke-width="1.6"/></svg>
        </div>
        <p class="stat-card__label">Thanh toán chờ xử lý</p>
        <p class="stat-card__num">{{ dangTai ? '···' : hienThi.choXuLy }}</p>
        <p v-if="!dangTai" class="stat-card__note" :class="thanhToanChoXuLy > 0 ? 'is-warning' : 'is-ok'">
          {{ thanhToanChoXuLy > 0 ? '● Cần xác nhận ngay' : '✓ Không có khoản nào chờ' }}
        </p>
      </div>

      <div class="stat-card reveal" style="--d:.24s">
        <div class="stat-card__icon">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" stroke="currentColor" stroke-width="1.6"/><path d="M2 8h20" stroke="currentColor" stroke-width="1.6"/></svg>
        </div>
        <p class="stat-card__label">Tổng giao dịch ghi nhận</p>
        <p class="stat-card__num">{{ dangTai ? '···' : hienThi.tongGD }}</p>
      </div>
    </div>

    <!-- ===== ĐIỀU HƯỚNG NHANH — đúng theo cấu trúc menu thật ===== -->
    <div class="quicknav">
      <div
        v-for="(nhom, ni) in duongDanQuanLy"
        :key="nhom.nhom"
        class="quicknav__group reveal"
        :style="{ '--d': (ni * 0.1) + 's' }"
      >
        <p class="quicknav__group-title">{{ nhom.nhom }}</p>
        <div class="quicknav__grid">
          <router-link
            v-for="item in nhom.items"
            :key="item.to"
            :to="item.to"
            class="quicknav__card"
            :class="{ 'quicknav__card--active': item.active }"
          >
            <div class="quicknav__icon" v-html="item.icon"></div>
            <span class="quicknav__label">{{ item.label }}</span>
            <svg class="quicknav__arrow" width="15" height="15" viewBox="0 0 24 24" fill="none"><path d="M5 12H19M19 12L13 6M19 12L13 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// Dashboard Staff - toàn bộ số liệu lấy thật từ API, không dùng dữ liệu cứng/giả lập
import { ref, reactive, computed, onMounted, onUnmounted, nextTick } from 'vue'
import thongKeService from '@/services/thongKeService'
import staffService from '@/services/staffService'

const datSanHomNay = ref(0)
const doanhThuHomNay = ref(0)
const dsThanhToan = ref([])
const dangTai = ref(true)
const loi = ref('')

// Số liệu suy ra trực tiếp từ danh sách thanh toán đã tải (vẫn là dữ liệu thật, không thêm giả định)
const thanhToanChoXuLy = computed(() => dsThanhToan.value.filter(tt => tt.trangThai === 'DANG_CHO').length)
const tongGiaoDich = computed(() => dsThanhToan.value.length)

function dinhDangTien(so) {
  return new Intl.NumberFormat('vi-VN').format(Math.round(so) || 0) + ' đ'
}

const ngayHomNay = computed(() =>
  new Date().toLocaleDateString('vi-VN', { weekday: 'long', day: '2-digit', month: '2-digit', year: 'numeric' })
)
const loiChao = computed(() => {
  const gio = new Date().getHours()
  if (gio < 11) return 'Chào buổi sáng'
  if (gio < 14) return 'Chào buổi trưa'
  if (gio < 18) return 'Chào buổi chiều'
  return 'Chào buổi tối'
})

// ===== ĐẾM SỐ CHẠY KHI DỮ LIỆU THẬT ĐÃ VỀ =====
const hienThi = reactive({ datSan: 0, doanhThu: 0, choXuLy: 0, tongGD: 0 })
function batDauDemSo() {
  const targets = {
    datSan: datSanHomNay.value,
    doanhThu: doanhThuHomNay.value,
    choXuLy: thanhToanChoXuLy.value,
    tongGD: tongGiaoDich.value
  }
  Object.keys(targets).forEach(key => {
    const target = Number(targets[key]) || 0
    const start = performance.now()
    const duration = 1100
    function tick(now) {
      const p = Math.min((now - start) / duration, 1)
      const eased = 1 - Math.pow(1 - p, 3)
      hienThi[key] = Math.round(target * eased)
      if (p < 1) requestAnimationFrame(tick)
    }
    requestAnimationFrame(tick)
  })
}

// ===== ĐIỀU HƯỚNG NHANH — theo đúng nhóm & tên mục trong menu thật =====
// Lưu ý: chỉnh lại "to" cho khớp router thực tế nếu đường dẫn dự án đặt khác.
const duongDanQuanLy = [
  {
    nhom: 'Quản lý',
    items: [
      { to: '/staff', label: 'Dashboard', active: true,
        icon: `<svg width="19" height="19" viewBox="0 0 24 24" fill="none"><rect x="3" y="3" width="8" height="8" rx="1" stroke="currentColor" stroke-width="1.7"/><rect x="13" y="3" width="8" height="8" rx="1" stroke="currentColor" stroke-width="1.7"/><rect x="3" y="13" width="8" height="8" rx="1" stroke="currentColor" stroke-width="1.7"/><rect x="13" y="13" width="8" height="8" rx="1" stroke="currentColor" stroke-width="1.7"/></svg>` },
      { to: '/staff/dat-san', label: 'Quản lý đặt sân',
        icon: `<svg width="19" height="19" viewBox="0 0 24 24" fill="none"><rect x="3" y="5" width="18" height="16" rx="2" stroke="currentColor" stroke-width="1.7"/><path d="M3 10H21M8 3v4M16 3v4" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/></svg>` },
      { to: '/staff/lich-san', label: 'Quản lý lịch sân',
        icon: `<svg width="19" height="19" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="9" stroke="currentColor" stroke-width="1.7"/><path d="M12 7v5l3 2" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/></svg>` },
      { to: '/staff/thanh-toan', label: 'Quản lý thanh toán',
        icon: `<svg width="19" height="19" viewBox="0 0 24 24" fill="none"><rect x="2" y="5" width="20" height="14" rx="1.5" stroke="currentColor" stroke-width="1.7"/><path d="M2 9.5h20" stroke="currentColor" stroke-width="1.7"/></svg>` },
      { to: '/staff/hoa-don', label: 'Xem hoá đơn',
        icon: `<svg width="19" height="19" viewBox="0 0 24 24" fill="none"><path d="M6 2h12v20l-3-2-3 2-3-2-3 2V2Z" stroke="currentColor" stroke-width="1.7" stroke-linejoin="round"/><path d="M9 8h6M9 12h6" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/></svg>` }
    ]
  },
  {
    nhom: 'Truyền thông',
    items: [
      { to: '/staff/bai-viet', label: 'Bài viết & Tin tức',
        icon: `<svg width="19" height="19" viewBox="0 0 24 24" fill="none"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20" stroke="currentColor" stroke-width="1.7"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2Z" stroke="currentColor" stroke-width="1.7"/></svg>` },
      { to: '/staff/phan-hoi', label: 'Phản hồi Trang chủ',
        icon: `<svg width="19" height="19" viewBox="0 0 24 24" fill="none"><path d="M21 11.5a8.4 8.4 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.4 8.4 0 0 1-3.8-.9L3 21l1.9-5.7a8.4 8.4 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.4 8.4 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5Z" stroke="currentColor" stroke-width="1.7" stroke-linejoin="round"/></svg>` }
    ]
  },
  {
    nhom: 'Khách hàng',
    items: [
      { to: '/staff/ho-tro-khach-hang', label: 'Hỗ trợ khách hàng',
        icon: `<svg width="19" height="19" viewBox="0 0 24 24" fill="none"><path d="M3 18v-6a9 9 0 0 1 18 0v6" stroke="currentColor" stroke-width="1.7"/><path d="M21 19a2 2 0 0 1-2 2h-1v-6h1a2 2 0 0 1 2 2v2ZM3 19a2 2 0 0 0 2 2h1v-6H5a2 2 0 0 0-2 2v2Z" stroke="currentColor" stroke-width="1.7"/></svg>` }
    ]
  },
  {
    nhom: 'Tài khoản',
    items: [
      { to: '/thong-tin-ca-nhan', label: 'Thông tin cá nhân',
        icon: `<svg width="19" height="19" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="8" r="4" stroke="currentColor" stroke-width="1.7"/><path d="M4 20c0-4 3.6-7 8-7s8 3 8 7" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/></svg>` },
      { to: '/', label: 'Quay lại trang chủ',
        icon: `<svg width="19" height="19" viewBox="0 0 24 24" fill="none"><path d="M19 12H5M5 12l7 7M5 12l7-7" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/></svg>` }
    ]
  }
]

let revealObserver = null

onMounted(async () => {
  revealObserver = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('is-visible')
        revealObserver.unobserve(entry.target)
      }
    })
  }, { threshold: 0.12 })
  nextTick(() => document.querySelectorAll('.reveal').forEach(el => revealObserver.observe(el)))

  try {
    const nam = new Date().getFullYear()
    const [tongQuan, danhSach] = await Promise.all([
      thongKeService.layDashboard(nam),
      staffService.layDanhSachThanhToan()
    ])

    datSanHomNay.value = tongQuan.datSanHomNay ?? 0
    doanhThuHomNay.value = tongQuan.doanhThuHomNay ?? 0
    dsThanhToan.value = Array.isArray(danhSach) ? danhSach : []

    batDauDemSo()
  } catch (e) {
    loi.value = e.message || 'Không tải được số liệu dashboard'
  } finally {
    dangTai.value = false
  }
})

onUnmounted(() => {
  if (revealObserver) revealObserver.disconnect()
})
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Bebas+Neue&family=Oswald:wght@500;600;700&family=Manrope:wght@400;500;600;700;800&family=Space+Mono:wght@400;700&display=swap');
</style>

<style scoped>
.dash {
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
  border-radius: 20px;
  padding: 32px;
  overflow: hidden;
  min-height: calc(100vh - 40px);
}

/* ===== REVEAL ===== */
.reveal { opacity: 0; transform: translateY(18px); transition: opacity .6s ease, transform .6s ease; transition-delay: var(--d, 0s); }
.reveal.is-visible { opacity: 1; transform: translateY(0); }
@media (prefers-reduced-motion: reduce) { .reveal { opacity: 1; transform: none; transition: none; } }

/* ===== AMBIENT ===== */
.dash__ambient { position: absolute; inset: 0; z-index: 0; pointer-events: none; border-radius: 20px; overflow: hidden; }
.floodlight { position: absolute; top: -20%; width: 40vh; height: 100vh; background: conic-gradient(from 90deg at 50% 0%, transparent 42%, rgba(255,244,214,.07) 50%, transparent 58%); mix-blend-mode: screen; animation: sweep 11s ease-in-out infinite alternate; }
.floodlight--l { left: -6%; }
.floodlight--r { right: -6%; animation-direction: alternate-reverse; }
@keyframes sweep { 0% { transform: rotate(-8deg); } 100% { transform: rotate(8deg); } }
.motes { position: absolute; inset: 0; }
.mote { position: absolute; bottom: -10px; left: calc((var(--i) * 8.4%) + 1%); width: 3px; height: 3px; border-radius: 50%; background: var(--lime-300); opacity: 0; animation: rise 9s linear infinite; animation-delay: calc(var(--i) * -0.7s); }
@keyframes rise { 0% { transform: translateY(0) scale(.6); opacity: 0; } 10% { opacity: .5; } 90% { opacity: .1; } 100% { transform: translateY(-100%) scale(1.1); opacity: 0; } }

/* Sân bóng mờ làm nền */
.pitch-silhouette { position: absolute; inset: 0; width: 100%; height: 100%; opacity: .07; }
.ps-line { fill: none; stroke: var(--lime-300); stroke-width: 2; }
.ps-dot { fill: var(--lime-300); }

/* Hạt rơi nhẹ xuống (rơi rơi) */
.falling { position: absolute; inset: 0; overflow: hidden; }
.falling__item {
  position: absolute; top: -6%; left: calc((var(--i) * 5.6%) + 1%);
  width: 3px; height: 3px; border-radius: 50%; background: var(--chalk-050);
  opacity: 0; animation: fall linear infinite;
  animation-duration: calc(7s + (var(--i) * 0.45s));
  animation-delay: calc(var(--i) * -0.6s);
}
@keyframes fall {
  0% { transform: translateY(0) translateX(0); opacity: 0; }
  8% { opacity: .55; }
  50% { transform: translateY(160px) translateX(14px); }
  92% { opacity: .12; }
  100% { transform: translateY(340px) translateX(-10px); opacity: 0; }
}

/* Đạo cụ bóng đá trôi nổi (bay bay) */
.field-props { position: absolute; inset: 0; }
.prop { position: absolute; opacity: .1; animation: prop-float 7s ease-in-out infinite; }
.prop svg { width: 100%; height: 100%; }
.prop--ball1 { width: 46px; height: 46px; top: 10%; right: 8%; }
.prop--ball2 { width: 26px; height: 26px; bottom: 18%; left: 6%; opacity: .08; animation-delay: .9s; animation-duration: 8.5s; }
.prop--whistle { width: 56px; height: 38px; top: 62%; right: 4%; animation-delay: .5s; }
.prop--flag { width: 32px; height: 48px; top: 30%; left: 3%; animation-delay: 1.1s; }
.prop--trophy { width: 40px; height: 46px; bottom: 8%; right: 22%; animation-delay: 1.6s; }
.prop--boot { width: 60px; height: 42px; top: 6%; left: 22%; opacity: .08; animation-delay: 2s; animation-duration: 9s; }
@keyframes prop-float { 0%, 100% { transform: translateY(0) rotate(-3deg); } 50% { transform: translateY(-16px) rotate(3deg); } }

/* ===== HEADER ===== */
.dash__header { position: relative; z-index: 1; margin-bottom: 28px; }
.eyebrow { display: inline-block; font-family: var(--font-mono); font-size: 12px; letter-spacing: .14em; text-transform: uppercase; color: var(--lime-300); }
.dash__title { font-family: var(--font-display); font-size: 28px; font-weight: 600; color: var(--chalk-050); margin: 8px 0 4px; }
.dash__desc { font-size: 14px; color: var(--chalk-200); opacity: .75; margin: 0; }
.dash__error { position: relative; z-index: 1; font-size: 14px; color: #ff9686; background: rgba(255,71,87,.1); border: 1px solid rgba(255,71,87,.28); border-radius: 12px; padding: 10px 14px; margin: 0 0 20px; }

/* ===== STAT GRID ===== */
.stat-grid { position: relative; z-index: 1; display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
@media (max-width: 1100px) { .stat-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 560px) { .stat-grid { grid-template-columns: 1fr; } }

.stat-card {
  background: rgba(247,251,244,.04); border: 1px solid rgba(247,251,244,.1); border-radius: 16px;
  padding: 20px; transition: transform .2s ease, box-shadow .2s ease, border-color .2s ease;
}
.stat-card:hover { transform: translateY(-4px); border-color: rgba(182,255,60,.35); box-shadow: 0 14px 30px -16px rgba(182,255,60,.25); }
.stat-card--warning { border-color: rgba(255,176,32,.4); }

.stat-card__icon { width: 40px; height: 40px; border-radius: 10px; background: rgba(182,255,60,.1); color: var(--lime-300); display: flex; align-items: center; justify-content: center; margin-bottom: 14px; }
.stat-card__icon--lime { background: rgba(182,255,60,.14); color: var(--lime-400); }
.stat-card__icon--warning { background: rgba(255,176,32,.12); color: var(--amber-400); }

.stat-card__label { font-size: 11.5px; font-weight: 700; text-transform: uppercase; letter-spacing: .05em; color: var(--chalk-200); opacity: .65; margin: 0 0 6px; }
.stat-card__num { font-family: var(--font-display); font-size: 26px; font-weight: 600; color: var(--chalk-050); letter-spacing: -.01em; margin: 0; }
.stat-card__note { font-size: 12.5px; font-weight: 600; margin: 6px 0 0; }
.stat-card__note.is-warning { color: var(--amber-400); }
.stat-card__note.is-ok { color: var(--lime-300); }

/* ===== QUICK NAV ===== */
.quicknav { position: relative; z-index: 1; margin-top: 36px; display: flex; flex-direction: column; gap: 26px; }
.quicknav__group-title { font-family: var(--font-mono); font-size: 11.5px; font-weight: 700; letter-spacing: .1em; text-transform: uppercase; color: var(--lime-300); opacity: .85; margin: 0 0 12px; }

.quicknav__grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 12px; }

.quicknav__card {
  display: flex; align-items: center; gap: 12px;
  background: rgba(247,251,244,.03); border: 1px solid rgba(247,251,244,.09); border-radius: 14px;
  padding: 14px 16px; text-decoration: none; color: var(--chalk-050);
  transition: transform .18s ease, box-shadow .18s ease, border-color .18s ease, background .18s ease;
  position: relative; overflow: hidden;
}
.quicknav__card::before {
  content: ''; position: absolute; left: 0; top: 0; bottom: 0; width: 3px;
  background: var(--lime-400); transform: scaleY(0); transition: transform .18s ease;
}
.quicknav__card:hover { transform: translateY(-3px); border-color: rgba(182,255,60,.35); background: rgba(182,255,60,.05); box-shadow: 0 14px 28px -16px rgba(0,0,0,.5); }
.quicknav__card:hover::before { transform: scaleY(1); }
.quicknav__card:hover .quicknav__arrow { opacity: 1; transform: translateX(0); }
.quicknav__card:hover .quicknav__icon { background: var(--turf-500); color: var(--chalk-050); }

.quicknav__card--active { border-color: rgba(182,255,60,.45); background: rgba(182,255,60,.08); }
.quicknav__card--active::before { transform: scaleY(1); }
.quicknav__card--active .quicknav__icon { background: var(--lime-400); color: var(--night-950); }
.quicknav__card--active .quicknav__label { color: var(--lime-300); font-weight: 700; }

.quicknav__icon { width: 36px; height: 36px; border-radius: 10px; flex-shrink: 0; background: rgba(247,251,244,.07); color: var(--chalk-200); display: flex; align-items: center; justify-content: center; transition: background .18s ease, color .18s ease; }
.quicknav__label { flex: 1; font-size: 13.5px; font-weight: 600; }
.quicknav__arrow { color: var(--lime-300); opacity: 0; transform: translateX(-4px); transition: opacity .18s ease, transform .18s ease; flex-shrink: 0; }
</style>