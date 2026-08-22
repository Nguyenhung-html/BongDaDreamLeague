<template>
  <div class="lich-su-page">

    <!-- ===== LỚP NỀN ẨN DỤ (đồng bộ trang chủ) ===== -->
    <div class="lich-su-page__ambient" aria-hidden="true">
      <div class="floodlight floodlight--l"></div>
      <div class="floodlight floodlight--r"></div>
      <div class="motes">
        <span v-for="n in 12" :key="n" class="mote" :style="{ '--i': n }"></span>
      </div>
    </div>

    <div class="container">
      <div class="page-header">
        <span class="eyebrow">⚽ Tài khoản của bạn</span>
        <h1>Lịch sử đặt sân</h1>
        <p>Quản lý tất cả các lần đặt sân của bạn</p>
      </div>

      <!-- Tabs trạng thái -->
      <div class="status-tabs">
        <button
          v-for="tab in tabs" :key="tab.value"
          class="status-tab"
          :class="{ 'status-tab--active': tabHienTai === tab.value }"
          @click="tabHienTai = tab.value"
        >
          {{ tab.label }}
          <span class="tab-count">{{ demTheoTrang(tab.value) }}</span>
        </button>
      </div>

      <!-- Loading -->
      <div v-if="dangTai" class="loading-state">
        <div class="spinner"></div>
        <p>Đang tải lịch sử...</p>
      </div>

      <!-- Lỗi -->
      <div v-else-if="loiTai" class="error-state">
        <p>{{ loiTai }}</p>
        <button @click="taiLichSu">Thử lại</button>
      </div>

      <!-- Rỗng -->
      <div v-else-if="danhSachHienThi.length === 0" class="empty-state">
        <svg width="60" height="60" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="10" stroke="rgba(247,251,244,0.25)" stroke-width="1.5"/><path d="M8 12h8M12 8v8" stroke="rgba(247,251,244,0.25)" stroke-width="1.5" stroke-linecap="round"/></svg>
        <p>Chưa có đơn đặt sân nào</p>
        <router-link to="/san" class="btn-dat">Đặt sân ngay</router-link>
      </div>

      <!-- Danh sách -->
      <div v-else class="booking-list">
        <div v-for="item in danhSachHienThi" :key="item.id" class="booking-item">
          <div class="booking-item__header">
            <div class="item-san">
              <span class="loai-badge" :class="item.loaiSan === 5 ? 'badge-blue' : 'badge-green'">
                Sân {{ item.loaiSan }}
              </span>
              <strong>{{ item.tenSan }}</strong>
            </div>
            <span class="trang-thai-badge" :class="trangThaiClass(item.trangThai)">
              {{ trangThaiLabel(item.trangThai) }}
            </span>
          </div>

          <div class="booking-item__body">
            <div class="info-grid">
              <div class="info-cell">
                <span class="info-label">📅 Ngày đá</span>
                <span class="info-val">{{ formatNgay(item.ngayDa) }}</span>
              </div>
              <div class="info-cell">
                <span class="info-label">⏰ Khung giờ</span>
                <span class="info-val">{{ item.gioBatDau }} - {{ item.gioKetThuc }}</span>
              </div>
              <div class="info-cell">
                <span class="info-label">💰 Tổng tiền</span>
                <span class="info-val">{{ formatTien(item.tongTien) }} VNĐ</span>
              </div>
              <div class="info-cell">
                <span class="info-label">💳 Đã cọc</span>
                <span class="info-val text-lime">{{ formatTien(item.tienCoc) }} VNĐ</span>
              </div>
            </div>
            <div class="ngay-dat">Đặt lúc: {{ item.ngayDat }}</div>
          </div>

          <div class="booking-item__footer" v-if="item.trangThai !== 'DA_HUY' && item.trangThai !== 'HOAN_THANH'">
            <div class="footer-actions-row">
              <button class="btn-dich-vu" @click="moModalDichVu(item)">
                🛒 Gọi đồ uống / dịch vụ
              </button>
              <button class="btn-gia-han" @click="giaHanGio(item)" :disabled="dangGiaHan === item.id">
                {{ dangGiaHan === item.id ? 'Đang gửi...' : '⏱️ Yêu cầu gia hạn +30p' }}
              </button>
            </div>

            <div v-if="item.coTheHuy" class="huy-group">
              <p class="huy-note">✅ Có thể huỷ - tiền cọc sẽ được hoàn đầy đủ</p>
              <button class="btn-huy" @click="xacNhanHuy(item)">Huỷ đặt sân</button>
            </div>
            <div v-else class="huy-group">
              <p class="huy-note warn">⚠️ Huỷ lúc này sẽ mất tiền cọc {{ formatTien(item.tienCoc) }} VNĐ</p>
              <button class="btn-huy btn-huy--warn" @click="xacNhanHuy(item)">Huỷ (mất cọc)</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal xác nhận huỷ -->
    <div v-if="itemDangHuy" class="modal-overlay" @click.self="itemDangHuy = null">
      <div class="modal">
        <h3>Xác nhận huỷ sân</h3>
        <p v-if="itemDangHuy.coTheHuy">
          Bạn có chắc muốn huỷ <strong>{{ itemDangHuy.tenSan }}</strong> ngày <strong>{{ formatNgay(itemDangHuy.ngayDa) }}</strong>?<br>
          Tiền cọc <strong>{{ formatTien(itemDangHuy.tienCoc) }} VNĐ</strong> sẽ được hoàn lại.
        </p>
        <p v-else class="warn-text">
          ⚠️ Chú ý: Vì huỷ trong vòng 20 phút trước giờ đá,
          <strong>{{ formatTien(itemDangHuy.tienCoc) }} VNĐ</strong> tiền cọc sẽ KHÔNG được hoàn lại!
        </p>
        <div class="modal-actions">
          <button class="btn-cancel" @click="itemDangHuy = null">Giữ lại</button>
          <button class="btn-confirm-huy" @click="thucHienHuy" :disabled="dangHuy">
            {{ dangHuy ? 'Đang xử lý...' : 'Xác nhận huỷ' }}
          </button>
        </div>
        <p v-if="loiHuy" class="loi-huy">{{ loiHuy }}</p>
      </div>
    </div>

    <!-- Modal gọi đồ uống / dịch vụ -->
    <div v-if="itemDangGoiDichVu" class="modal-overlay" @click.self="dongModalDichVu">
      <div class="modal modal--wide">
        <h3>Gọi đồ uống / dịch vụ</h3>
        <p class="modal-sub">{{ itemDangGoiDichVu.tenSan }} — {{ formatNgay(itemDangGoiDichVu.ngayDa) }}, {{ itemDangGoiDichVu.gioBatDau }}-{{ itemDangGoiDichVu.gioKetThuc }}</p>

        <div v-if="dangTaiMenu" class="modal-loading">Đang tải menu...</div>

        <template v-else>
          <div class="menu-grid" v-if="danhSachSanPham.length > 0">
            <div v-for="sp in danhSachSanPham" :key="sp.id" class="menu-item">
              <img :src="sp.hinhAnh || anhMacDinh" class="menu-item__img" alt="" />
              <div class="menu-item__info">
                <p class="menu-item__ten">{{ sp.tenSanPham }}</p>
                <p class="menu-item__gia">{{ formatTien(sp.gia) }}đ</p>
              </div>
              <button class="btn-them-mon" @click="themMon(sp)">+ Thêm</button>
            </div>
          </div>
          <p v-else class="menu-trong">Hiện chưa có sản phẩm nào để gọi.</p>

          <div class="gio-hang-box" v-if="gioHang">
            <h4>Giỏ hàng của bạn</h4>
            <div v-if="gioHang.danhSach.length === 0" class="gio-hang-trong">Chưa gọi món nào</div>
            <div v-else class="gio-hang-list">
              <div v-for="ct in gioHang.danhSach" :key="ct.id" class="gio-hang-row">
                <span class="gio-hang-row__ten">{{ ct.tenSanPham }} × {{ ct.soLuong }}</span>
                <span class="gio-hang-row__gia">{{ formatTien(ct.thanhTien) }}đ</span>
                <button class="btn-xoa-mon" @click="xoaMon(ct.id)" title="Xoá món này">✕</button>
              </div>
            </div>

            <div class="gio-hang-tong">
              <div class="tong-row">
                <span>Còn lại tiền sân (50%)</span>
                <strong>{{ formatTien(gioHang.tienConLaiSan) }}đ</strong>
              </div>
              <div class="tong-row">
                <span>Tiền đồ uống / dịch vụ</span>
                <strong>{{ formatTien(gioHang.tongTienDichVu) }}đ</strong>
              </div>
              <div class="tong-row tong-row--final">
                <span>Tổng cần thanh toán tại sân</span>
                <strong>{{ formatTien(gioHang.tongCanThanhToan) }}đ</strong>
              </div>
            </div>
          </div>
        </template>

        <p v-if="loiDichVu" class="loi-huy">{{ loiDichVu }}</p>

        <div class="modal-actions">
          <button class="btn-cancel" @click="dongModalDichVu">Đóng</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const API = '/api'
const anhMacDinh = 'https://images.unsplash.com/photo-1544145945-f90425340c7e?w=200'

const dangTai = ref(true)
const loiTai = ref('')
const danhSach = ref([])
const tabHienTai = ref('TAT_CA')
const itemDangHuy = ref(null)
const dangHuy = ref(false)
const loiHuy = ref('')
const dangGiaHan = ref(null) // lưu id của đơn đang gọi API gia hạn, để hiện "Đang gia hạn..."

// ===== State cho modal gọi đồ uống/dịch vụ =====
const itemDangGoiDichVu = ref(null)
const danhSachSanPham = ref([])
const dangTaiMenu = ref(false)
const gioHang = ref(null)
const loiDichVu = ref('')

const tabs = [
  { value: 'TAT_CA', label: 'Tất cả' },
  { value: 'CHO_XAC_NHAN', label: 'Chờ xác nhận' },
  { value: 'DA_XAC_NHAN', label: 'Đã xác nhận' },
  { value: 'HOAN_THANH', label: 'Hoàn thành' },
  { value: 'DA_HUY', label: 'Đã huỷ' },
]

const danhSachHienThi = computed(() => {
  if (tabHienTai.value === 'TAT_CA') return danhSach.value
  return danhSach.value.filter(i => i.trangThai === tabHienTai.value)
})

function demTheoTrang(val) {
  if (val === 'TAT_CA') return danhSach.value.length
  return danhSach.value.filter(i => i.trangThai === val).length
}

function formatTien(so) { return Number(so).toLocaleString('vi-VN') }

function formatNgay(s) {
  if (!s) return ''
  const [y, m, d] = s.split('-')
  return `${d}/${m}/${y}`
}

function trangThaiLabel(tt) {
  const map = { CHO_XAC_NHAN: 'Chờ xác nhận', DA_XAC_NHAN: 'Đã xác nhận', HOAN_THANH: 'Hoàn thành', DA_HUY: 'Đã huỷ' }
  return map[tt] || tt
}

function trangThaiClass(tt) {
  return { 'tt-cho': tt === 'CHO_XAC_NHAN', 'tt-ok': tt === 'DA_XAC_NHAN', 'tt-done': tt === 'HOAN_THANH', 'tt-huy': tt === 'DA_HUY' }
}

async function taiLichSu() {
  dangTai.value = true
  loiTai.value = ''
  try {
    // Kiểm tra tất cả các key token phổ biến nếu bị đặt tên khác
    const token = localStorage.getItem('token') || localStorage.getItem('accessToken') || localStorage.getItem('jwt')

    if (!token) {
      throw new Error('Chưa đăng nhập hoặc phiên làm việc đã hết hạn. Vui lòng đăng nhập lại!')
    }

    const res = await fetch(`${API}/dat-san/lich-su`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    })

    if (res.status === 403) {
      throw new Error('Lỗi 403: Tài khoản không có quyền xem lịch sử hoặc phiên đăng nhập bị hết hạn!')
    }

    if (res.status === 401) {
      throw new Error('Lỗi 401: Token không hợp lệ. Vui lòng đăng nhập lại!')
    }

    if (!res.ok) {
      throw new Error(`Không tải được lịch sử đặt sân (Mã lỗi: ${res.status})`)
    }

    danhSach.value = await res.json()
  } catch (e) {
    loiTai.value = e.message
    console.error('Lỗi taiLichSu:', e)
  } finally {
    dangTai.value = false
  }
}

function xacNhanHuy(item) {
  loiHuy.value = ''
  itemDangHuy.value = item
}

async function thucHienHuy() {
  if (!itemDangHuy.value) return
  dangHuy.value = true
  loiHuy.value = ''
  try {
    const token = localStorage.getItem('token')
    const res = await fetch(`${API}/dat-san/${itemDangHuy.value.id}/huy`, {
      method: 'PUT',
      headers: { 'Authorization': `Bearer ${token}` }
    })
    const data = await res.text()
    if (!res.ok) throw new Error(data || 'Huỷ thất bại')

    const idx = danhSach.value.findIndex(i => i.id === itemDangHuy.value.id)
    if (idx !== -1) { danhSach.value[idx].trangThai = 'DA_HUY'; danhSach.value[idx].coTheHuy = false }
    itemDangHuy.value = null
  } catch (e) {
    loiHuy.value = e.message
  } finally {
    dangHuy.value = false
  }
}

// ===== Yêu cầu gia hạn thêm 30 phút (CHỈ gửi thông báo cho Staff, không tự cộng tiền/đổi giờ) =====
async function giaHanGio(item) {
  if (!confirm(`Gửi yêu cầu gia hạn thêm 30 phút cho ${item.tenSan}? Nhân viên sẽ xác nhận và cộng tiền khi bạn tới sân.`)) return

  dangGiaHan.value = item.id
  const token = localStorage.getItem('token')
  try {
    const res = await fetch(`${API}/dat-san/${item.id}/yeu-cau-gia-han`, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${token}` }
    })
    const data = await res.json()
    if (!res.ok) throw new Error(data.message || 'Gửi yêu cầu thất bại!')

    // KHÔNG cập nhật item.gioKetThuc/item.tongTien ở đây nữa - chỉ Staff xác nhận
    // thật ở "Quản lý đặt sân" mới thực sự cộng tiền/đổi giờ, tránh cộng trùng 2 lần.
    alert(data.message)
  } catch (e) {
    alert(e.message)
  } finally {
    dangGiaHan.value = null
  }
}

// ===== Gọi đồ uống / dịch vụ =====

async function moModalDichVu(item) {
  itemDangGoiDichVu.value = item
  loiDichVu.value = ''
  gioHang.value = null
  dangTaiMenu.value = true
  const token = localStorage.getItem('token')
  try {
    const [resMenu, resGio] = await Promise.all([
      fetch(`${API}/san-pham`, { headers: { 'Authorization': `Bearer ${token}` } }),
      fetch(`${API}/dat-san/${item.id}/dich-vu`, { headers: { 'Authorization': `Bearer ${token}` } })
    ])
    danhSachSanPham.value = resMenu.ok ? await resMenu.json() : []
    gioHang.value = resGio.ok ? await resGio.json() : null
  } catch (e) {
    loiDichVu.value = 'Không tải được menu, vui lòng thử lại!'
  } finally {
    dangTaiMenu.value = false
  }
}

function dongModalDichVu() {
  itemDangGoiDichVu.value = null
  danhSachSanPham.value = []
  gioHang.value = null
  loiDichVu.value = ''
}

async function themMon(sp) {
  loiDichVu.value = ''
  const token = localStorage.getItem('token')
  try {
    const res = await fetch(`${API}/dat-san/${itemDangGoiDichVu.value.id}/dich-vu`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
      body: JSON.stringify({ sanPhamId: sp.id, soLuong: 1 })
    })
    const data = await res.json()
    if (!res.ok) throw new Error(data.message || 'Không thêm được món này!')
    gioHang.value = data
  } catch (e) {
    loiDichVu.value = e.message
  }
}

async function xoaMon(chiTietId) {
  loiDichVu.value = ''
  const token = localStorage.getItem('token')
  try {
    const res = await fetch(`${API}/dich-vu/${chiTietId}`, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${token}` }
    })
    const data = await res.json()
    if (!res.ok) throw new Error(data.message || 'Không xoá được món này!')
    gioHang.value = data
  } catch (e) {
    loiDichVu.value = e.message
  }
}

onMounted(taiLichSu)
</script>

<style scoped>
/* ===== TOKENS (đồng bộ trang chủ / danh sách sân) ===== */
.lich-su-page {
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
.container { max-width: 900px; margin: 0 auto; padding: 0 24px; position: relative; z-index: 1; }

/* ===== LỚP NỀN ẨN DỤ ===== */
.lich-su-page__ambient { position: fixed; inset: 0; z-index: 0; pointer-events: none; }
.floodlight { position: absolute; top: -12%; width: 55vh; height: 145vh; background: conic-gradient(from 90deg at 50% 0%, transparent 42%, rgba(255,244,214,.06) 50%, transparent 58%); mix-blend-mode: screen; }
.floodlight--l { left: -14%; animation: sweep 12s ease-in-out infinite alternate; }
.floodlight--r { right: -14%; animation: sweep 12s ease-in-out infinite alternate-reverse; }
@keyframes sweep { 0% { transform: rotate(-10deg); } 100% { transform: rotate(10deg); } }
.motes { position: absolute; inset: 0; }
.mote { position: absolute; bottom: -10px; left: calc((var(--i) * 8%) + 1%); width: 3px; height: 3px; border-radius: 50%; background: var(--lime-300); opacity: 0; animation: rise 10s linear infinite; animation-delay: calc(var(--i) * -0.7s); }
@keyframes rise { 0% { transform: translateY(0) scale(.6); opacity: 0; } 10% { opacity: .45; } 90% { opacity: .1; } 100% { transform: translateY(-100vh) scale(1.1); opacity: 0; } }

/* ===== HEADER ===== */
.page-header { text-align: center; margin-bottom: 32px; }
.eyebrow { display: inline-block; font-family: var(--font-mono); font-size: 12px; letter-spacing: .16em; text-transform: uppercase; color: var(--lime-300); }
.page-header h1 { font-family: var(--font-display); font-size: 32px; font-weight: 600; color: var(--chalk-050); margin-top: 10px; }
.page-header p { color: var(--chalk-200); opacity: .7; margin-top: 6px; font-size: 14.5px; }

/* Tabs */
.status-tabs { display: flex; gap: 8px; flex-wrap: wrap; justify-content: center; margin-bottom: 30px; }
.status-tab {
  display: inline-flex; align-items: center; gap: 7px;
  padding: 9px 18px; border-radius: 999px; font-size: 13.5px;
  font-weight: 600; font-family: inherit; cursor: pointer;
  border: 1.5px solid rgba(247,251,244,.14); background: transparent; color: var(--chalk-200);
  transition: all .15s;
}
.status-tab:hover { border-color: var(--lime-400); color: var(--lime-300); }
.status-tab--active {
  background: linear-gradient(135deg, var(--turf-500), var(--turf-700));
  border-color: transparent; color: var(--chalk-050);
  box-shadow: 0 6px 16px -6px rgba(182,255,60,.35);
}
.tab-count { background: rgba(247,251,244,.1); border-radius: 999px; padding: 1px 8px; font-size: 11px; }
.status-tab--active .tab-count { background: rgba(255,255,255,.22); }

/* List */
.booking-list { display: flex; flex-direction: column; gap: 16px; }
.booking-item {
  background: rgba(247,251,244,.03); border: 1px solid rgba(247,251,244,.09);
  border-radius: 16px; overflow: hidden; transition: border-color .2s, transform .2s;
}
.booking-item:hover { border-color: rgba(182,255,60,.28); transform: translateY(-2px); }
.booking-item__header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 16px 20px; border-bottom: 1px solid rgba(247,251,244,.08);
}
.item-san { display: flex; align-items: center; gap: 10px; }
.item-san strong { font-size: 16px; color: var(--chalk-050); font-weight: 700; }
.loai-badge { padding: 3px 10px; border-radius: 999px; font-size: 11px; font-weight: 700; font-family: var(--font-mono); }
.badge-blue { background: rgba(59,130,246,.16); color: #93c5fd; }
.badge-green { background: rgba(182,255,60,.14); color: var(--lime-300); }
.trang-thai-badge { padding: 4px 12px; border-radius: 999px; font-size: 12px; font-weight: 700; }
.tt-cho { background: rgba(255,176,32,.16); color: var(--amber-400); }
.tt-ok { background: rgba(182,255,60,.14); color: var(--lime-300); }
.tt-done { background: rgba(147,197,253,.16); color: #93c5fd; }
.tt-huy { background: rgba(255,71,87,.16); color: #ff9686; }
.booking-item__body { padding: 16px 20px; }
.info-grid {
  display: grid; grid-template-columns: repeat(2, 1fr);
  gap: 12px; margin-bottom: 10px;
}
.info-cell { display: flex; flex-direction: column; gap: 3px; }
.info-label { font-size: 12px; color: var(--chalk-200); opacity: .55; }
.info-val { font-size: 14px; font-weight: 600; color: var(--chalk-050); }
.text-lime { color: var(--lime-300); }
.ngay-dat { font-size: 12px; color: var(--chalk-200); opacity: .5; margin-top: 4px; }
.booking-item__footer {
  padding: 12px 20px 16px;
  background: rgba(5,11,8,.35); border-top: 1px solid rgba(247,251,244,.08);
  display: flex; flex-direction: column; gap: 10px;
}

/* Hàng chứa 2 nút: gọi dịch vụ + gia hạn giờ */
.footer-actions-row { display: flex; gap: 10px; flex-wrap: wrap; }

/* Nút gọi dịch vụ */
.btn-dich-vu {
  padding: 8px 16px; border-radius: 10px; font-size: 13px; font-weight: 600;
  border: 1.5px solid var(--lime-400); background: transparent; color: var(--lime-300);
  cursor: pointer; transition: .2s;
}
.btn-dich-vu:hover { background: rgba(182,255,60,.1); }

/* Nút gia hạn thêm giờ */
.btn-gia-han {
  padding: 8px 16px; border-radius: 10px; font-size: 13px; font-weight: 600;
  border: 1.5px solid var(--amber-400); background: transparent; color: var(--amber-400);
  cursor: pointer; transition: .2s;
}
.btn-gia-han:hover:not(:disabled) { background: rgba(255,176,32,.1); }
.btn-gia-han:disabled { opacity: .5; cursor: not-allowed; }

.huy-group { display: flex; align-items: center; justify-content: space-between; gap: 12px; flex-wrap: wrap; }
.huy-note { font-size: 13px; color: var(--chalk-200); opacity: .8; }
.huy-note.warn { color: var(--amber-400); opacity: 1; }
.btn-huy {
  padding: 8px 18px; border-radius: 10px; font-size: 13px; font-weight: 600;
  border: 1.5px solid var(--crimson-500); background: transparent; color: #ff9686;
  cursor: pointer; transition: .2s; white-space: nowrap;
}
.btn-huy:hover { background: rgba(255,71,87,.1); }
.btn-huy--warn { border-color: var(--amber-400); color: var(--amber-400); }
.btn-huy--warn:hover { background: rgba(255,176,32,.1); }

/* Empty / Loading / Error */
.loading-state, .error-state, .empty-state {
  text-align: center; padding: 80px 0; color: var(--chalk-200); opacity: .8;
}
.spinner {
  width: 42px; height: 42px; border: 4px solid rgba(247,251,244,.12);
  border-top-color: var(--lime-400); border-radius: 50%;
  animation: spin .7s linear infinite; margin: 0 auto 16px;
}
@keyframes spin { to { transform: rotate(360deg); } }
.empty-state p { font-size: 15.5px; margin: 16px 0; }
.btn-dat {
  display: inline-block; padding: 12px 28px;
  background: linear-gradient(135deg, var(--turf-500), var(--turf-700));
  color: var(--chalk-050); border-radius: 999px; text-decoration: none;
  font-weight: 700; transition: box-shadow .2s, transform .2s;
}
.btn-dat:hover { box-shadow: 0 10px 24px -8px rgba(182,255,60,.4); transform: translateY(-2px); }
.error-state p { color: #ff9686; }
.error-state button {
  margin-top: 12px; padding: 10px 24px;
  background: linear-gradient(135deg, var(--turf-500), var(--turf-700));
  color: var(--chalk-050); border: none; border-radius: 999px; cursor: pointer; font-weight: 600;
}

/* Modal */
.modal-overlay {
  position: fixed; inset: 0; background: rgba(5,11,8,.72); backdrop-filter: blur(3px);
  z-index: 1000; display: flex; align-items: center; justify-content: center;
  padding: 20px;
}
.modal {
  background: var(--night-800); border: 1px solid rgba(182,255,60,.18);
  border-radius: 20px; padding: 28px;
  max-width: 440px; width: 100%;
  box-shadow: 0 24px 64px rgba(0,0,0,.5);
  max-height: 90vh; overflow-y: auto;
}
.modal--wide { max-width: 560px; }
.modal h3 { font-family: var(--font-display); font-size: 20px; font-weight: 600; color: var(--chalk-050); margin-bottom: 14px; }
.modal p { font-size: 14px; color: var(--chalk-200); opacity: .85; line-height: 1.6; margin-bottom: 20px; }
.warn-text { color: var(--amber-400); opacity: 1; background: rgba(255,176,32,.08); padding: 12px; border-radius: 10px; border: 1px solid rgba(255,176,32,.3); }
.modal-actions { display: flex; gap: 10px; }
.btn-cancel {
  flex: 1; padding: 13px; border: 1.5px solid rgba(247,251,244,.16);
  background: transparent; border-radius: 12px; font-size: 14px;
  font-weight: 600; color: var(--chalk-200); cursor: pointer; transition: .2s;
}
.btn-cancel:hover { border-color: rgba(247,251,244,.32); color: var(--chalk-050); }
.btn-confirm-huy {
  flex: 1; padding: 13px; border: none;
  background: var(--crimson-500); border-radius: 12px;
  font-size: 14px; font-weight: 700; color: white; cursor: pointer;
  transition: .2s;
}
.btn-confirm-huy:hover:not(:disabled) { background: #e6394a; }
.btn-confirm-huy:disabled { opacity: .6; cursor: not-allowed; }
.loi-huy { color: #ff9686; font-size: 13px; margin-top: 12px; }

/* Modal gọi dịch vụ */
.modal-sub { font-size: 13px; color: var(--chalk-200); opacity: .65; margin-top: -10px; margin-bottom: 18px; }
.modal-loading { text-align: center; padding: 30px 0; color: var(--chalk-200); opacity: .6; font-size: 14px; }
.menu-grid { display: flex; flex-direction: column; gap: 10px; margin-bottom: 20px; max-height: 260px; overflow-y: auto; }
.menu-item {
  display: flex; align-items: center; gap: 12px;
  padding: 8px; border: 1px solid rgba(247,251,244,.08); border-radius: 12px;
}
.menu-item__img { width: 44px; height: 44px; border-radius: 8px; object-fit: cover; flex-shrink: 0; }
.menu-item__info { flex: 1; min-width: 0; }
.menu-item__ten { font-size: 13.5px; font-weight: 600; color: var(--chalk-050); }
.menu-item__gia { font-size: 12.5px; color: var(--lime-300); font-weight: 600; margin-top: 2px; }
.btn-them-mon {
  padding: 6px 12px; border-radius: 8px; font-size: 12.5px; font-weight: 600;
  border: none; background: var(--turf-500); color: var(--chalk-050); cursor: pointer;
  white-space: nowrap; transition: .15s;
}
.btn-them-mon:hover { background: var(--turf-700); }
.menu-trong { font-size: 13.5px; color: var(--chalk-200); opacity: .55; text-align: center; padding: 20px 0; }

.gio-hang-box { border-top: 1.5px dashed rgba(247,251,244,.14); padding-top: 16px; }
.gio-hang-box h4 { font-size: 14px; font-weight: 700; color: var(--chalk-050); margin-bottom: 10px; }
.gio-hang-trong { font-size: 13px; color: var(--chalk-200); opacity: .55; padding: 8px 0; }
.gio-hang-list { display: flex; flex-direction: column; gap: 6px; margin-bottom: 12px; }
.gio-hang-row {
  display: flex; align-items: center; gap: 8px;
  font-size: 13px; color: var(--chalk-200);
}
.gio-hang-row__ten { flex: 1; }
.gio-hang-row__gia { font-weight: 600; color: var(--chalk-050); }
.btn-xoa-mon {
  width: 22px; height: 22px; border-radius: 50%; border: none;
  background: rgba(255,71,87,.16); color: #ff9686; font-size: 12px; cursor: pointer;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.btn-xoa-mon:hover { background: rgba(255,71,87,.28); }
.gio-hang-tong { border-top: 1px solid rgba(247,251,244,.1); padding-top: 10px; display: flex; flex-direction: column; gap: 6px; }
.tong-row { display: flex; justify-content: space-between; font-size: 13px; color: var(--chalk-200); opacity: .75; }
.tong-row--final { font-size: 14.5px; color: var(--lime-300); font-weight: 700; padding-top: 6px; border-top: 1px dashed rgba(247,251,244,.14); opacity: 1; }

@media (max-width: 640px) {
  .info-grid { grid-template-columns: 1fr; }
  .huy-group { flex-direction: column; align-items: flex-start; }
}
</style>