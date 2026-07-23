<template>
  <div class="lich-su-page">
    <div class="container">
      <div class="page-header">
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
        <svg width="64" height="64" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="10" stroke="#d1d5db" stroke-width="1.5"/><path d="M8 12h8M12 8v8" stroke="#d1d5db" stroke-width="1.5" stroke-linecap="round"/></svg>
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
                <span class="info-val text-green">{{ formatTien(item.tienCoc) }} VNĐ</span>
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

const API = 'http://localhost:8080/api'
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
    const token = localStorage.getItem('token')
    const res = await fetch(`${API}/dat-san/lich-su`, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    if (!res.ok) throw new Error('Không tải được lịch sử')
    danhSach.value = await res.json()
  } catch (e) {
    loiTai.value = e.message
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
.lich-su-page {
  background: #f0f4f8;
  min-height: calc(100vh - 76px);
  padding: 40px 0 80px;
}
.page-header { text-align: center; margin-bottom: 30px; }
.page-header h1 { font-size: 32px; font-weight: 800; color: #0d1f3c; }
.page-header p { color: #6b7280; margin-top: 6px; }

/* Tabs */
.status-tabs { display: flex; gap: 8px; flex-wrap: wrap; margin-bottom: 28px; }
.status-tab {
  display: inline-flex; align-items: center; gap: 7px;
  padding: 9px 18px; border-radius: 999px; font-size: 13.5px;
  font-weight: 600; font-family: inherit; cursor: pointer;
  border: 1.5px solid #e2e8f0; background: white; color: #6b7280;
  transition: .15s;
}
.status-tab:hover { border-color: var(--green-600); color: var(--green-600); }
.status-tab--active { background: var(--green-600); border-color: var(--green-600); color: white; }
.tab-count {
  background: rgba(0,0,0,.1); border-radius: 999px;
  padding: 1px 8px; font-size: 11px;
}
.status-tab--active .tab-count { background: rgba(255,255,255,.25); }

/* List */
.booking-list { display: flex; flex-direction: column; gap: 16px; }
.booking-item {
  background: white; border-radius: 16px;
  box-shadow: 0 2px 12px rgba(10,37,64,.07);
  overflow: hidden; transition: box-shadow .2s;
}
.booking-item:hover { box-shadow: 0 6px 24px rgba(10,37,64,.12); }
.booking-item__header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 16px 20px; border-bottom: 1px solid #f1f5f9;
}
.item-san { display: flex; align-items: center; gap: 10px; }
.item-san strong { font-size: 16px; color: #0d1f3c; }
.loai-badge { padding: 3px 10px; border-radius: 999px; font-size: 11px; font-weight: 700; }
.badge-blue { background: #dbeafe; color: #1d4ed8; }
.badge-green { background: #dcfce7; color: #15803d; }
.trang-thai-badge { padding: 4px 12px; border-radius: 999px; font-size: 12px; font-weight: 700; }
.tt-cho { background: #fef3c7; color: #92400e; }
.tt-ok { background: #dcfce7; color: #15803d; }
.tt-done { background: #e0e7ff; color: #3730a3; }
.tt-huy { background: #fee2e2; color: #991b1b; }
.booking-item__body { padding: 16px 20px; }
.info-grid {
  display: grid; grid-template-columns: repeat(2, 1fr);
  gap: 12px; margin-bottom: 10px;
}
.info-cell { display: flex; flex-direction: column; gap: 3px; }
.info-label { font-size: 12px; color: #9ca3af; }
.info-val { font-size: 14px; font-weight: 600; color: #0d1f3c; }
.text-green { color: var(--green-600); }
.ngay-dat { font-size: 12px; color: #9ca3af; margin-top: 4px; }
.booking-item__footer {
  padding: 12px 20px 16px;
  background: #f8fafc; border-top: 1px solid #f1f5f9;
  display: flex; flex-direction: column; gap: 10px;
}

/* Hàng chứa 2 nút: gọi dịch vụ + gia hạn giờ */
.footer-actions-row { display: flex; gap: 10px; flex-wrap: wrap; }

/* Nút gọi dịch vụ */
.btn-dich-vu {
  padding: 8px 16px; border-radius: 10px; font-size: 13px; font-weight: 600;
  border: 1.5px solid var(--green-600); background: white; color: var(--green-600);
  cursor: pointer; transition: .2s;
}
.btn-dich-vu:hover { background: var(--green-50, #f0fdf4); }

/* Nút gia hạn thêm giờ */
.btn-gia-han {
  padding: 8px 16px; border-radius: 10px; font-size: 13px; font-weight: 600;
  border: 1.5px solid #d97706; background: white; color: #d97706;
  cursor: pointer; transition: .2s;
}
.btn-gia-han:hover:not(:disabled) { background: #fffbeb; }
.btn-gia-han:disabled { opacity: .6; cursor: not-allowed; }

.huy-group { display: flex; align-items: center; justify-content: space-between; gap: 12px; flex-wrap: wrap; }
.huy-note { font-size: 13px; color: #374151; }
.huy-note.warn { color: #b45309; }
.btn-huy {
  padding: 8px 18px; border-radius: 10px; font-size: 13px; font-weight: 600;
  border: 1.5px solid #ef4444; background: white; color: #ef4444;
  cursor: pointer; transition: .2s; white-space: nowrap;
}
.btn-huy:hover { background: #fef2f2; }
.btn-huy--warn { border-color: #f59e0b; color: #d97706; }
.btn-huy--warn:hover { background: #fffbeb; }

/* Empty / Loading / Error */
.loading-state, .error-state, .empty-state {
  text-align: center; padding: 80px 0; color: #9ca3af;
}
.spinner {
  width: 44px; height: 44px; border: 4px solid #e2e8f0;
  border-top-color: var(--green-600); border-radius: 50%;
  animation: spin .7s linear infinite; margin: 0 auto 16px;
}
@keyframes spin { to { transform: rotate(360deg); } }
.empty-state p { font-size: 16px; margin: 16px 0; }
.btn-dat {
  display: inline-block; padding: 12px 28px; background: var(--green-600);
  color: white; border-radius: 12px; text-decoration: none;
  font-weight: 600; transition: .2s;
}
.btn-dat:hover { background: var(--green-700, #15803d); }
.error-state button {
  margin-top: 12px; padding: 10px 24px; background: var(--green-600);
  color: white; border: none; border-radius: 10px; cursor: pointer;
}

/* Modal */
.modal-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,.5);
  z-index: 1000; display: flex; align-items: center; justify-content: center;
  padding: 20px;
}
.modal {
  background: white; border-radius: 20px; padding: 28px;
  max-width: 440px; width: 100%;
  box-shadow: 0 24px 64px rgba(0,0,0,.2);
  max-height: 90vh; overflow-y: auto;
}
.modal--wide { max-width: 560px; }
.modal h3 { font-size: 20px; font-weight: 800; color: #0d1f3c; margin-bottom: 14px; }
.modal p { font-size: 14px; color: #374151; line-height: 1.6; margin-bottom: 20px; }
.warn-text { color: #92400e; background: #fffbeb; padding: 12px; border-radius: 10px; border: 1px solid #fde68a; }
.modal-actions { display: flex; gap: 10px; }
.btn-cancel {
  flex: 1; padding: 13px; border: 1.5px solid #e2e8f0;
  background: white; border-radius: 12px; font-size: 14px;
  font-weight: 600; color: #6b7280; cursor: pointer;
}
.btn-confirm-huy {
  flex: 1; padding: 13px; border: none;
  background: #ef4444; border-radius: 12px;
  font-size: 14px; font-weight: 700; color: white; cursor: pointer;
  transition: .2s;
}
.btn-confirm-huy:hover:not(:disabled) { background: #dc2626; }
.btn-confirm-huy:disabled { opacity: .6; cursor: not-allowed; }
.loi-huy { color: #dc2626; font-size: 13px; margin-top: 12px; }

/* Modal gọi dịch vụ */
.modal-sub { font-size: 13px; color: #6b7280; margin-top: -10px; margin-bottom: 18px; }
.modal-loading { text-align: center; padding: 30px 0; color: #9ca3af; font-size: 14px; }
.menu-grid { display: flex; flex-direction: column; gap: 10px; margin-bottom: 20px; max-height: 260px; overflow-y: auto; }
.menu-item {
  display: flex; align-items: center; gap: 12px;
  padding: 8px; border: 1px solid #f1f5f9; border-radius: 12px;
}
.menu-item__img { width: 44px; height: 44px; border-radius: 8px; object-fit: cover; flex-shrink: 0; }
.menu-item__info { flex: 1; min-width: 0; }
.menu-item__ten { font-size: 13.5px; font-weight: 600; color: #0d1f3c; }
.menu-item__gia { font-size: 12.5px; color: var(--green-600); font-weight: 600; margin-top: 2px; }
.btn-them-mon {
  padding: 6px 12px; border-radius: 8px; font-size: 12.5px; font-weight: 600;
  border: none; background: var(--green-600); color: white; cursor: pointer;
  white-space: nowrap; transition: .15s;
}
.btn-them-mon:hover { background: var(--green-700, #15803d); }
.menu-trong { font-size: 13.5px; color: #9ca3af; text-align: center; padding: 20px 0; }

.gio-hang-box { border-top: 1.5px dashed #e2e8f0; padding-top: 16px; }
.gio-hang-box h4 { font-size: 14px; font-weight: 700; color: #0d1f3c; margin-bottom: 10px; }
.gio-hang-trong { font-size: 13px; color: #9ca3af; padding: 8px 0; }
.gio-hang-list { display: flex; flex-direction: column; gap: 6px; margin-bottom: 12px; }
.gio-hang-row {
  display: flex; align-items: center; gap: 8px;
  font-size: 13px; color: #374151;
}
.gio-hang-row__ten { flex: 1; }
.gio-hang-row__gia { font-weight: 600; color: #0d1f3c; }
.btn-xoa-mon {
  width: 22px; height: 22px; border-radius: 50%; border: none;
  background: #fee2e2; color: #dc2626; font-size: 12px; cursor: pointer;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.btn-xoa-mon:hover { background: #fecaca; }
.gio-hang-tong { border-top: 1px solid #f1f5f9; padding-top: 10px; display: flex; flex-direction: column; gap: 6px; }
.tong-row { display: flex; justify-content: space-between; font-size: 13px; color: #6b7280; }
.tong-row--final { font-size: 14.5px; color: #0d1f3c; font-weight: 700; padding-top: 6px; border-top: 1px dashed #e2e8f0; }

@media (max-width: 640px) {
  .info-grid { grid-template-columns: 1fr; }
  .huy-group { flex-direction: column; align-items: flex-start; }
}
</style>