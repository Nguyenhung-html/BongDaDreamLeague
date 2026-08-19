<template>
  <div class="voucher-notifications-page">
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
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const API = 'http://localhost:8080/api/voucher'
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
.voucher-notifications-page {
  max-width: 900px;
  margin: 0 auto;
  padding: 30px 20px 50px;
  font-family: system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
}
.voucher-notifications-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 24px;
}
.page-kicker {
  color: var(--green-700, #16803a);
  font-size: 12px;
  font-weight: 800;
  letter-spacing: .08em;
  margin-bottom: 6px;
}
.voucher-notifications-header h1 {
  margin: 0;
  color: var(--navy-900, #0a2540);
  font-size: 28px;
  line-height: 1.2;
}
.voucher-notifications-header p {
  margin: 7px 0 0;
  color: var(--gray-500, #64748b);
  font-size: 14px;
}
.refresh-btn, .copy-btn, .primary-btn {
  border: 0;
  cursor: pointer;
  font-family: inherit;
}
.refresh-btn {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  padding: 9px 14px;
  border-radius: 9px;
  background: #f0f7f2;
  color: #16803a;
  font-weight: 700;
}
.refresh-btn:disabled { opacity: .55; cursor: wait; }
.state-card {
  min-height: 230px;
  background: #fff;
  border: 1px solid #e8eeeb;
  border-radius: 16px;
  box-shadow: 0 8px 28px rgba(10,37,64,.06);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 30px;
  color: #64748b;
}
.state-card h3 { margin: 12px 0 5px; color: #0a2540; }
.state-card p { margin: 0; font-size: 14px; }
.state-icon {
  width: 58px; height: 58px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  background: #edf8f0; color: #16803a; font-size: 25px; font-weight: 800;
}
.error-state .state-icon { background: #fff1f1; color: #dc2626; }
.spinner {
  width: 30px; height: 30px; border: 3px solid #dce8df;
  border-top-color: #1f9d49; border-radius: 50%; animation: spin .8s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }
.primary-btn {
  margin-top: 16px; padding: 9px 18px; border-radius: 9px;
  background: #1f9d49; color: #fff; font-weight: 700;
}
.voucher-list { display: grid; gap: 14px; }
.voucher-card {
  display: flex; gap: 16px; background: #fff; border: 1px solid #e5ece8;
  border-radius: 16px; padding: 18px; box-shadow: 0 8px 28px rgba(10,37,64,.055);
}
.voucher-card__icon {
  flex: 0 0 50px; width: 50px; height: 50px; border-radius: 13px;
  background: #e9f7ed; display: flex; align-items: center; justify-content: center; font-size: 24px;
}
.voucher-card__body { flex: 1; min-width: 0; }
.voucher-card__top { display: flex; align-items: flex-start; justify-content: space-between; gap: 12px; }
.voucher-card__label { color: #16803a; font-size: 10px; font-weight: 800; letter-spacing: .08em; }
.voucher-card h2 { margin: 4px 0 0; color: #0a2540; font-size: 18px; }
.voucher-code {
  flex-shrink: 0; padding: 6px 10px; border: 1px dashed #7dbd91;
  border-radius: 7px; background: #f6fbf7; color: #16803a; font-size: 13px; font-weight: 800;
}
.voucher-description { margin: 9px 0 12px; color: #475569; font-size: 14px; line-height: 1.55; }
.voucher-meta { display: flex; flex-wrap: wrap; gap: 8px 14px; color: #5b6b7d; font-size: 12.5px; }
.voucher-card__bottom {
  margin-top: 14px; padding-top: 12px; border-top: 1px solid #eef2ef;
  display: flex; align-items: center; justify-content: space-between; gap: 12px;
}
.voucher-target { font-size: 12px; color: #475569; font-weight: 600; }
.copy-btn { padding: 8px 12px; border-radius: 8px; background: #1f9d49; color: #fff; font-size: 12px; font-weight: 700; }
.copy-btn:hover { background: #16803a; }
.page-note { margin: 18px 2px 0; color: #94a3b8; font-size: 12px; line-height: 1.5; }
@media (max-width: 620px) {
  .voucher-notifications-header { align-items: flex-start; flex-direction: column; }
  .voucher-card { padding: 14px; }
  .voucher-card__top { flex-direction: column; }
  .voucher-code { align-self: flex-start; }
  .voucher-card__bottom { align-items: flex-start; flex-direction: column; }
}
</style>
