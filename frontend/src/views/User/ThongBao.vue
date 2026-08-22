<template>
  <div class="voucher-notifications-page">

    <!-- ===== LỚP NỀN ẨN DỤ (đồng bộ trang chủ) ===== -->
    <div class="voucher-notifications-page__ambient" aria-hidden="true">
      <div class="floodlight floodlight--l"></div>
      <div class="floodlight floodlight--r"></div>
      <div class="motes">
        <span v-for="n in 12" :key="n" class="mote" :style="{ '--i': n }"></span>
      </div>
    </div>

    <div class="container">
      <div class="voucher-notifications-header">
        <div>
          <div class="page-kicker">🎟 ƯU ĐÃI</div>
          <h1>Thông báo voucher</h1>
          <p>Những voucher đang được hệ thống mở và còn lượt sử dụng.</p>
        </div>
        <button class="refresh-btn" :disabled="dangTai" @click="taiVoucherDangHoatDong">
          <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M20 11a8.1 8.1 0 0 0-15.5-2M4 5v4h4"></path>
            <path d="M4 13a8.1 8.1 0 0 0 15.5 2M20 19v-4h-4"></path>
          </svg>
          Cập nhật
        </button>
      </div>

      <div v-if="dangTai" class="state-card">
        <div class="spinner"></div>
        <p>Đang tải voucher đang hoạt động...</p>
      </div>

      <div v-else-if="loi" class="state-card error-state">
        <div class="state-icon">!</div>
        <h3>Không tải được danh sách voucher</h3>
        <p>{{ loi }}</p>
        <button class="primary-btn" @click="taiVoucherDangHoatDong">Thử lại</button>
      </div>

      <div v-else-if="voucherDangHoatDong.length === 0" class="state-card empty-state">
        <div class="state-icon">🎟</div>
        <h3>Hiện chưa có voucher đang hoạt động</h3>
        <p>Khi Admin mở một voucher mới, thông tin sẽ tự động xuất hiện tại đây.</p>
      </div>

      <div v-else class="voucher-list">
        <article v-for="voucher in voucherDangHoatDong" :key="voucher.id" class="voucher-card">
          <div class="voucher-card__icon">🎟</div>
          <div class="voucher-card__body">
            <div class="voucher-card__top">
              <div>
                <div class="voucher-card__label">VOUCHER ĐANG HOẠT ĐỘNG</div>
                <h2>{{ voucher.ten }}</h2>
              </div>
              <div class="voucher-code">{{ voucher.ma }}</div>
            </div>

            <p v-if="voucher.moTa" class="voucher-description">{{ voucher.moTa }}</p>
            <p v-else class="voucher-description">Mã {{ voucher.ma }} đang được áp dụng trong thời gian hiệu lực.</p>

            <div class="voucher-meta">
              <span>🏷 {{ moTaGiam(voucher) }}</span>
              <span v-if="voucher.donToiThieu != null">🧾 Đơn từ {{ formatMoney(voucher.donToiThieu) }}</span>
              <span>⏳ Hạn đến {{ formatDate(voucher.ngayKetThuc) }}</span>
            </div>

            <div class="voucher-card__bottom">
              <span class="voucher-target">{{ moTaDoiTuong(voucher.doiTuongKhachHang) }}</span>
              <button class="copy-btn" @click="saoChepMa(voucher.ma)">
                {{ maVuaSaoChep === voucher.ma ? 'Đã sao chép' : 'Sao chép mã' }}
              </button>
            </div>
          </div>
        </article>
      </div>

      <p class="page-note">Thông báo này chỉ dùng để giới thiệu voucher đang hoạt động. Việc áp dụng voucher vẫn được kiểm tra tại bước đặt sân.</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const API = '/api/voucher'
const voucherDangHoatDong = ref([])
const dangTai = ref(true)
const loi = ref('')
const maVuaSaoChep = ref('')
let interval = null

async function taiVoucherDangHoatDong() {
  dangTai.value = true
  loi.value = ''
  try {
    const res = await fetch(`${API}/dang-hoat-dong`, {
      method: 'GET',
      headers: { 'Accept': 'application/json' }
    })

    // Đọc text trước để tránh lỗi "Unexpected end of JSON input"
    // khi server trả response rỗng hoặc lỗi không có JSON.
    const text = await res.text()
    let data = null
    if (text.trim()) {
      try {
        data = JSON.parse(text)
      } catch {
        throw new Error(`Máy chủ trả dữ liệu không hợp lệ (HTTP ${res.status}).`)
      }
    }

    if (!res.ok) {
      throw new Error(data?.message || `Không thể tải voucher (HTTP ${res.status}).`)
    }

    voucherDangHoatDong.value = Array.isArray(data) ? data : []
  } catch (e) {
    voucherDangHoatDong.value = []
    loi.value = e.message || 'Không thể kết nối tới máy chủ.'
  } finally {
    dangTai.value = false
  }
}

function formatMoney(value) {
  return new Intl.NumberFormat('vi-VN').format(Number(value || 0)) + 'đ'
}

function formatDate(value) {
  if (!value) return ''
  const d = new Date(value)
  return d.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' })
}

function moTaGiam(voucher) {
  if (voucher.loaiGiam === 'PHAN_TRAM') {
    const max = voucher.giamToiDa != null ? `, tối đa ${formatMoney(voucher.giamToiDa)}` : ''
    return `Giảm ${Number(voucher.giaTriGiam)}%${max}`
  }
  return `Giảm ${formatMoney(voucher.giaTriGiam)}`
}

function moTaDoiTuong(target) {
  const labels = {
    ALL: 'Dành cho mọi khách hàng',
    NEW_USER: 'Dành cho khách hàng mới',
    SILVER: 'Dành cho khách hạng Bạc',
    GOLD: 'Dành cho khách hạng Vàng',
    DIAMOND: 'Dành cho khách hạng Kim Cương'
  }
  return labels[target] || 'Điều kiện áp dụng theo voucher'
}

async function saoChepMa(ma) {
  try {
    await navigator.clipboard.writeText(ma)
    maVuaSaoChep.value = ma
    setTimeout(() => {
      if (maVuaSaoChep.value === ma) maVuaSaoChep.value = ''
    }, 1800)
  } catch {
    // Clipboard có thể bị trình duyệt chặn; không làm ảnh hưởng trang thông báo.
  }
}

onMounted(() => {
  taiVoucherDangHoatDong()
  interval = setInterval(taiVoucherDangHoatDong, 30000)
})

onUnmounted(() => {
  if (interval) clearInterval(interval)
})
</script>

<style scoped>
/* ===== TOKENS (đồng bộ trang chủ / danh sách sân / lịch sử) ===== */
.voucher-notifications-page {
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
  padding: 44px 0 60px;
  overflow: hidden;
}
.container { max-width: 900px; margin: 0 auto; padding: 0 24px; position: relative; z-index: 1; }

/* ===== LỚP NỀN ẨN DỤ ===== */
.voucher-notifications-page__ambient { position: fixed; inset: 0; z-index: 0; pointer-events: none; }
.floodlight { position: absolute; top: -12%; width: 55vh; height: 145vh; background: conic-gradient(from 90deg at 50% 0%, transparent 42%, rgba(255,244,214,.06) 50%, transparent 58%); mix-blend-mode: screen; }
.floodlight--l { left: -14%; animation: sweep 12s ease-in-out infinite alternate; }
.floodlight--r { right: -14%; animation: sweep 12s ease-in-out infinite alternate-reverse; }
@keyframes sweep { 0% { transform: rotate(-10deg); } 100% { transform: rotate(10deg); } }
.motes { position: absolute; inset: 0; }
.mote { position: absolute; bottom: -10px; left: calc((var(--i) * 8%) + 1%); width: 3px; height: 3px; border-radius: 50%; background: var(--lime-300); opacity: 0; animation: rise 10s linear infinite; animation-delay: calc(var(--i) * -0.7s); }
@keyframes rise { 0% { transform: translateY(0) scale(.6); opacity: 0; } 10% { opacity: .45; } 90% { opacity: .1; } 100% { transform: translateY(-100vh) scale(1.1); opacity: 0; } }

/* ===== HEADER ===== */
.voucher-notifications-header {
  display: flex; align-items: flex-end; justify-content: space-between;
  gap: 20px; margin-bottom: 28px; flex-wrap: wrap;
}
.page-kicker {
  font-family: var(--font-mono); color: var(--lime-300);
  font-size: 12px; font-weight: 700; letter-spacing: .1em; margin-bottom: 8px;
}
.voucher-notifications-header h1 {
  margin: 0; font-family: var(--font-display); color: var(--chalk-050);
  font-size: 30px; font-weight: 600; line-height: 1.2;
}
.voucher-notifications-header p { margin: 8px 0 0; color: var(--chalk-200); opacity: .7; font-size: 14px; }

.refresh-btn, .copy-btn, .primary-btn { border: 0; cursor: pointer; font-family: inherit; }
.refresh-btn {
  display: inline-flex; align-items: center; gap: 7px;
  padding: 10px 16px; border-radius: 999px;
  background: transparent; border: 1.5px solid rgba(247,251,244,.16);
  color: var(--lime-300); font-weight: 700; transition: all .15s;
}
.refresh-btn:hover:not(:disabled) { border-color: var(--lime-400); background: rgba(182,255,60,.08); }
.refresh-btn:disabled { opacity: .5; cursor: wait; }

/* ===== STATE CARDS ===== */
.state-card {
  min-height: 220px;
  background: rgba(247,251,244,.03); border: 1px solid rgba(247,251,244,.09);
  border-radius: 18px;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  text-align: center; padding: 30px; color: var(--chalk-200); opacity: .85;
}
.state-card h3 { margin: 12px 0 5px; color: var(--chalk-050); font-family: var(--font-display); font-weight: 600; }
.state-card p { margin: 0; font-size: 14px; }
.state-icon {
  width: 56px; height: 56px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  background: rgba(182,255,60,.1); color: var(--lime-300); font-size: 24px; font-weight: 800;
}
.error-state .state-icon { background: rgba(255,71,87,.14); color: #ff9686; }
.spinner {
  width: 30px; height: 30px; border: 3px solid rgba(247,251,244,.14);
  border-top-color: var(--lime-400); border-radius: 50%; animation: spin .8s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }
.primary-btn {
  margin-top: 16px; padding: 10px 20px; border-radius: 999px;
  background: linear-gradient(135deg, var(--turf-500), var(--turf-700));
  color: var(--chalk-050); font-weight: 700;
}

/* ===== VOUCHER LIST ===== */
.voucher-list { display: grid; gap: 14px; }
.voucher-card {
  display: flex; gap: 16px;
  background: rgba(247,251,244,.03); border: 1px solid rgba(247,251,244,.09);
  border-radius: 16px; padding: 18px; transition: border-color .2s, transform .2s;
}
.voucher-card:hover { border-color: rgba(182,255,60,.28); transform: translateY(-2px); }
.voucher-card__icon {
  flex: 0 0 50px; width: 50px; height: 50px; border-radius: 13px;
  background: rgba(182,255,60,.1); display: flex; align-items: center; justify-content: center; font-size: 24px;
}
.voucher-card__body { flex: 1; min-width: 0; }
.voucher-card__top { display: flex; align-items: flex-start; justify-content: space-between; gap: 12px; }
.voucher-card__label { color: var(--lime-300); font-family: var(--font-mono); font-size: 10px; font-weight: 700; letter-spacing: .08em; }
.voucher-card h2 { margin: 4px 0 0; color: var(--chalk-050); font-size: 18px; font-family: var(--font-display); font-weight: 600; }
.voucher-code {
  flex-shrink: 0; padding: 6px 10px; border: 1px dashed rgba(182,255,60,.4);
  border-radius: 8px; background: rgba(182,255,60,.06); color: var(--lime-300);
  font-size: 13px; font-weight: 800; font-family: var(--font-mono);
}
.voucher-description { margin: 9px 0 12px; color: var(--chalk-200); opacity: .8; font-size: 14px; line-height: 1.55; }
.voucher-meta { display: flex; flex-wrap: wrap; gap: 8px 14px; color: var(--chalk-200); opacity: .7; font-size: 12.5px; }
.voucher-card__bottom {
  margin-top: 14px; padding-top: 12px; border-top: 1px solid rgba(247,251,244,.08);
  display: flex; align-items: center; justify-content: space-between; gap: 12px;
}
.voucher-target { font-size: 12px; color: var(--chalk-200); opacity: .75; font-weight: 600; }
.copy-btn {
  padding: 8px 14px; border-radius: 999px;
  background: linear-gradient(135deg, var(--turf-500), var(--turf-700));
  color: var(--chalk-050); font-size: 12px; font-weight: 700; transition: box-shadow .15s, transform .15s;
}
.copy-btn:hover { box-shadow: 0 8px 18px -6px rgba(182,255,60,.4); transform: translateY(-1px); }
.page-note { margin: 20px 2px 0; color: var(--chalk-200); opacity: .45; font-size: 12px; line-height: 1.5; }

@media (max-width: 620px) {
  .voucher-notifications-header { align-items: flex-start; flex-direction: column; }
  .voucher-card { padding: 14px; }
  .voucher-card__top { flex-direction: column; }
  .voucher-code { align-self: flex-start; }
  .voucher-card__bottom { align-items: flex-start; flex-direction: column; }
}
</style>