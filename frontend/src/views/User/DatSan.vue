<template>
  <div class="booking-page">
    <div class="container">
      <!-- Loading -->
      <div v-if="dangTai" class="loading-state">
        <div class="spinner"></div>
        <p>Đang tải thông tin sân...</p>
      </div>

      <!-- Lỗi tải -->
      <div v-else-if="loiTai" class="error-state">
        <p>{{ loiTai }}</p>
        <button @click="$router.push('/san')">Quay lại danh sách sân</button>
      </div>

      <template v-else-if="sanBong">
        <div class="page-header">
          <button class="btn-back" @click="$router.back()">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none"><path d="M19 12H5M5 12l7 7M5 12l7-7" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
            Quay lại
          </button>
          <h1>Đặt sân bóng</h1>
          <p>Chọn thời gian và hoàn tất đặt sân nhanh chóng</p>
        </div>

        <div class="booking-layout">
          <!-- Thông tin sân -->
          <div class="field-info">
            <div class="field-img-wrap">
              <img :src="sanBong.hinhAnh || 'https://images.unsplash.com/photo-1574629810360-7efbbe195018'" alt="Sân bóng" />
              <span class="badge-loai" :class="sanBong.loaiSan === 5 ? 'badge-blue' : 'badge-green'">
                Sân {{ sanBong.loaiSan }} người
              </span>
            </div>
            <div class="field-content">
              <h2>{{ sanBong.tenSan }}</h2>
              <div class="field-detail">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none"><path d="M12 22s7-7.58 7-12.5A7 7 0 1 0 5 9.5C5 14.42 12 22 12 22Z" stroke="var(--green-600)" stroke-width="2"/></svg>
                {{ sanBong.diaChi }}
              </div>
              <div class="field-detail">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="9" stroke="var(--green-600)" stroke-width="2"/><path d="M12 7v5l3 2" stroke="var(--green-600)" stroke-width="2" stroke-linecap="round"/></svg>
                Hoạt động 06:00 – 22:00 hàng ngày
              </div>
              <div class="field-detail mo-ta">{{ sanBong.moTa }}</div>

              <!-- Bảng giá kiêm chọn khung giờ -->
              <div class="gia-section" v-if="sanBong.danhSachGia?.length">
                <h3>Bảng giá theo khung giờ</h3>
                <div class="gia-grid" :class="{ 'disabled-grid': buoc === 2 }">
                  <div
                    v-for="g in sanBong.danhSachGia" :key="g.id"
                    class="gia-item"
                    :class="{
                      'gia-sang': parseInt(g.gioBatDau) < 11,
                      'gia-toi': parseInt(g.gioBatDau) >= 17,
                      'gia-da-dat': form.ngayDa && khungGioBiKhoa(g),
                      'gia-dang-chon': form.khungGioId === g.id
                    }"
                    @click="chonKhungGio(g)"
                  >
                    <span class="gia-gio">{{ g.gioBatDau }}–{{ g.gioKetThuc }}</span>
                    <span class="gia-tien">{{ formatTien(g.giaTien) }}đ</span>
                    <span v-if="form.ngayDa && khungGioBiKhoa(g)" class="gia-tag-dat">
                      {{ khungGioBiTrung(g) ? 'Đã đặt' : 'Đã qua giờ' }}
                    </span>
                  </div>
                </div>
                <p v-if="!form.ngayDa" class="hint-ngay">Vui lòng chọn ngày đá ở form bên phải để xem khung giờ còn trống</p>
              </div>

              <!-- Chính sách -->
              <div class="policy-box">
                <h4>📋 Chính sách đặt sân</h4>
                <ul>
                  <li>💸 Đặt cọc <strong>50%</strong> tổng tiền để xác nhận booking</li>
                  <li>🏟️ Thanh toán 50% còn lại trực tiếp tại sân trước giờ đá</li>
                  <li>⏰ Huỷ trước <strong>20 phút</strong> được hoàn cọc đầy đủ</li>
                  <li>❌ Huỷ muộn hơn sẽ mất tiền cọc</li>
                  <li>📌 Tối đa <strong>2 sân / người / ngày</strong></li>
                  <li>💳 Thời gian thanh toán online tối đa <strong>3 phút</strong> để quét mã QR</li>
                </ul>
              </div>
            </div>
          </div>

          <!-- Form đặt sân -->
          <div class="booking-card">
            <!-- Bước 1: Thông tin -->
            <div v-if="buoc === 1">
              <h3>Thông tin đặt sân</h3>
              <div class="form-group">
                <label>Họ và tên <span class="req">*</span></label>
                <input v-model="form.hoTenDat" type="text" placeholder="Nhập họ và tên người đặt" />
              </div>
              <div class="form-group">
                <label>Số điện thoại <span class="req">*</span></label>
                <input v-model="form.soDienThoai" type="tel" placeholder="Nhập số điện thoại" />
              </div>
              <div class="voucher-box">
                <label>Mã voucher</label>
                <div class="voucher-row"><input v-model="voucherMa" placeholder="Nhập mã giảm giá..."/><button type="button" @click="kiemTraVoucher" :disabled="dangKiemVoucher">{{dangKiemVoucher?'Đang kiểm tra...':'Áp dụng mã'}}</button></div>
                <p v-if="voucherThongBao" :class="voucherHopLe?'voucher-ok':'voucher-error'">{{voucherThongBao}}</p>
                <p v-if="voucherHopLe" class="voucher-note">Giảm {{formatTien(voucherGiam)}}đ • Còn thanh toán {{formatTien(giaSauVoucher)}}đ</p>
              </div>

              <div class="form-group">
                <label>Ngày đá <span class="req">*</span></label>
                <input v-model="form.ngayDa" type="date" :min="ngayToiThieu" />
              </div>
              <div class="form-group">
                <label>Khung giờ <span class="req">*</span></label>
                <div class="khung-gio-da-chon" :class="{ 'chua-chon': !khungGioChon }">
                  {{ khungGioChon
                    ? `${khungGioChon.gioBatDau} – ${khungGioChon.gioKetThuc} | ${formatTien(khungGioChon.giaTien)}đ`
                    : 'Vui lòng chọn khung giờ ở bảng giá bên trái' }}
                </div>
              </div>

              <!-- Tóm tắt giá -->
              <div class="summary" v-if="khungGioChon">
                <div class="summary-row">
                  <span>Giá thuê</span>
                  <strong>{{ formatTien(khungGioChon.giaTien) }} VNĐ</strong>
                </div>
                <div v-if="voucherHopLe && voucherGiam > 0" class="summary-row">
                  <span>Giảm voucher</span>
                  <strong class="text-green">-{{ formatTien(voucherGiam) }} VNĐ</strong>
                </div>
                <div class="summary-row">
                  <span>Tiền cọc (50%)</span>
                  <strong class="text-green">{{ formatTien(tienCoc) }} VNĐ</strong>
                </div>
                <div class="summary-row total">
                  <span>Thanh toán ngay</span>
                  <strong>{{ formatTien(tienCoc) }} VNĐ</strong>
                </div>
              </div>
              <p v-if="loiForm" class="loi-form">{{ loiForm }}</p>
              <div class="btn-group">
                <select v-model="form.phuongThuc" class="select-tt">
                  <option value="QR">💳 Thanh toán QR (Online)</option>
                  <option value="TIEN_MAT">💵 Trả tiền mặt tại sân</option>
                </select>
                <button class="btn-book" @click="buocTiep" :disabled="dangGui">
                  {{ form.phuongThuc === 'QR' ? 'Tiếp tục – Quét mã QR thanh toán' : 'Xác nhận đặt sân' }}
                </button>
              </div>
            </div>

            <!-- Bước 2: QR thanh toán + Countdown -->
            <div v-else-if="buoc === 2" class="qr-step">
              <div class="countdown-wrapper">
                <CountdownTimer
                  v-if="expireAtTimestamp"
                  ref="countdownRef"
                  :expireAt="expireAtTimestamp"
                  :allow-dismiss="false"
                  @expired="dungKiemTraThanhToan"
                  @redirect="handleCountdownExpired"
                />
              </div>

              <div class="qr-header">
                <svg width="40" height="40" viewBox="0 0 24 24" fill="none"><path d="M9 12l2 2 4-4" stroke="#22c55e" stroke-width="2.5" stroke-linecap="round"/><circle cx="12" cy="12" r="10" stroke="#22c55e" stroke-width="2"/></svg>
                <h3>Quét mã QR để đặt cọc</h3>
                <p>Chuyển khoản <strong>{{ formatTien(soTienThanhToan) }} VNĐ</strong> để xác nhận booking</p>
              </div>
              <div class="qr-box">
                <img :src="qrUrl" alt="QR thanh toán" class="qr-img" />
              </div>
              <div class="qr-info">
                <div class="info-row"><span>Ngân hàng</span><strong>MB Bank (970422)</strong></div>
                <div class="info-row"><span>Số TK</span><strong>0973728967</strong></div>
                <div class="info-row"><span>Chủ TK</span><strong>NGUYEN TIEN HUNG</strong></div>
                <div class="info-row"><span>Số tiền</span><strong class="text-green">{{ formatTien(soTienThanhToan) }} VNĐ</strong></div>
                <div class="info-row"><span>Nội dung</span><strong>DatSan {{ maGiaoDichHienTai }}</strong></div>
              </div>
              <div class="dang-cho-thanh-toan">
                <div class="spinner-nho"></div>
                <p>Đang chờ xác nhận thanh toán tự động...</p>
              </div>
              <button class="btn-back-step" @click="huyChoThanhToan">← Quay lại chọn giờ khác</button>
            </div>

            <!-- Bước 3: Thành công -->
            <div v-else-if="buoc === 3" class="success-step">
              <template v-if="thanhToanQRThanhCong">
                <div class="success-icon">✅</div>
                <h3>Thanh toán thành công!</h3>
                <p>Bạn đã đặt cọc thành công cho sân <strong>{{ sanBong?.tenSan }}</strong>.</p>
                <p class="luu-y-xac-nhan">
                  🎉 Booking của bạn đã được <strong>xác nhận tự động</strong> ngay khi hệ thống nhận được tiền cọc.
                  Vui lòng đến đúng khung giờ đã đặt và thanh toán 50% còn lại tại sân.
                </p>
              </template>
              <template v-else>
                <div class="success-icon">⏳</div>
                <h3>Đã ghi nhận yêu cầu đặt sân!</h3>
                <p>{{ ketQua?.thongBao }}</p>
                <p class="luu-y-xac-nhan">
                  ⚠️ Đơn của bạn đang ở trạng thái <strong>Chờ xác nhận</strong>.
                  Nhân viên sẽ kiểm tra giao dịch chuyển khoản và xác nhận trong ít phút.
                  Bạn sẽ nhận thông báo khi đơn được xác nhận chính thức.
                </p>
              </template>
              <div class="success-actions">
                <button class="btn-book" @click="$router.push('/lich-su-dat-san')">Theo dõi trạng thái đơn</button>
                <button class="btn-outline" @click="$router.push('/san')">Đặt sân khác</button>
              </div>
            </div>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import CountdownTimer from '../../components/CountdownTimer.vue'

const API = 'http://localhost:8080/api'
const route = useRoute()

// ── State Core ──
const dangTai = ref(true)
const loiTai = ref('')
const sanBong = ref(null)
const buoc = ref(1)
const dangGui = ref(false)
const loiForm = ref('')
const ketQua = ref(null)
const danhSachDaDat = ref([])

// ── Hold Slot & QR Payment State ──
const expireAtTimestamp = ref(null)
const datSanIdHienTai = ref(null)
const thanhToanIdHienTai = ref(null)
const qrUrl = ref('')
const maGiaoDichHienTai = ref('')
const thanhToanQRThanhCong = ref(false)
const soTienThanhToan = ref(0)
let pollingInterval = null

// BroadcastChannel đồng bộ nhiều Tab
const broadcastChannel = new BroadcastChannel('booking_sync_channel')

const form = ref({
  hoTenDat: '',
  soDienThoai: '',
  ngayDa: '',
  khungGioId: '',
  gioBatDau: '',
  gioKetThuc: '',
  phuongThuc: 'QR'
})

// ── Computed ──
const ngayToiThieu = computed(() => new Date().toISOString().split('T')[0])
const khungGioChon = computed(() =>
  sanBong.value?.danhSachGia?.find(g => g.id === form.value.khungGioId) || null
)
const voucherMa = ref('')
const voucherHopLe = ref(false)
const voucherGiam = ref(0)
const voucherThongBao = ref('')
const dangKiemVoucher = ref(false)
const giaSauVoucher = computed(() => Math.max(0, Number(khungGioChon.value?.giaTien || 0) - Number(voucherGiam.value || 0)))
const tienCoc = computed(() => Math.round(giaSauVoucher.value / 2))

// ── Helpers ──
function gioToPhut(gio) {
  const [h, m] = gio.split(':').map(Number)
  return h * 60 + (m || 0)
}

function khungGioBiTrung(g) {
  const batMoi = gioToPhut(g.gioBatDau)
  const ketMoi = gioToPhut(g.gioKetThuc)
  return danhSachDaDat.value.some(d => {
    const bat = gioToPhut(d.gioBatDau)
    const ket = gioToPhut(d.gioKetThuc)
    return batMoi < ket && bat < ketMoi
  })
}

function khungGioDaQua(g) {
  const homNay = new Date().toISOString().split('T')[0]
  if (form.value.ngayDa !== homNay) return false
  const bayGio = new Date()
  const phutHienTai = bayGio.getHours() * 60 + bayGio.getMinutes()
  const phutBatDau = gioToPhut(g.gioBatDau)
  return phutBatDau <= phutHienTai
}

function khungGioBiKhoa(g) {
  return khungGioBiTrung(g) || khungGioDaQua(g)
}

function formatTien(so) {
  return Number(so).toLocaleString('vi-VN')
}

// ── API Actions ──
async function taiKhungGioDaDat() {
  if (!form.value.ngayDa || !sanBong.value) {
    danhSachDaDat.value = []
    return
  }
  try {
    const res = await fetch(
      `${API}/dat-san/da-dat?sanBongId=${sanBong.value.id}&ngay=${form.value.ngayDa}`
    )
    danhSachDaDat.value = res.ok ? await res.json() : []
  } catch {
    danhSachDaDat.value = []
  }
}

watch(() => form.value.khungGioId, () => {
  if (voucherMa.value) { voucherHopLe.value = false; voucherGiam.value = 0; voucherThongBao.value = 'Khung giờ đã thay đổi, vui lòng áp dụng lại voucher.' }
})

watch(() => form.value.ngayDa, async () => {
  form.value.khungGioId = ''
  form.value.gioBatDau = ''
  form.value.gioKetThuc = ''
  await taiKhungGioDaDat()
})

function chonKhungGio(g) {
  // 1. Không cho chọn nếu đang ở bước quét QR thanh toán
  if (buoc.value === 2) {
    return
  }

  // 2. Không cho chọn nếu chưa chọn ngày
  if (!form.value.ngayDa) {
    loiForm.value = 'Vui lòng chọn ngày đá trước!'
    return
  }

  // 3. Không cho chọn nếu khung giờ bị khóa / đã qua
  if (khungGioBiKhoa(g)) return

  loiForm.value = ''
  form.value.khungGioId = g.id
  form.value.gioBatDau = g.gioBatDau
  form.value.gioKetThuc = g.gioKetThuc
}

async function kiemTraVoucher(){
  voucherThongBao.value=''; voucherHopLe.value=false; voucherGiam.value=0
  if(!voucherMa.value.trim()){voucherThongBao.value='Vui lòng nhập mã voucher!';return}
  if(!khungGioChon.value){voucherThongBao.value='Vui lòng chọn khung giờ trước!';return}
  dangKiemVoucher.value=true
  try{const token=localStorage.getItem('token');const res=await fetch(`${API}/voucher/kiem-tra`,{method:'POST',headers:{'Content-Type':'application/json','Authorization':`Bearer ${token}`},body:JSON.stringify({ma:voucherMa.value,donHang:Number(khungGioChon.value.giaTien)})});const data=await res.json();if(!res.ok)throw new Error(data.message||'Voucher không hợp lệ!');voucherHopLe.value=true;voucherGiam.value=Number(data.soTienGiam||0);voucherThongBao.value=data.thongBao||'Áp dụng voucher thành công!'}catch(e){voucherThongBao.value=e.message}finally{dangKiemVoucher.value=false}
}

async function buocTiep() {
  loiForm.value = ''
  if (!form.value.hoTenDat.trim()) { loiForm.value = 'Vui lòng nhập họ và tên!'; return }
  if (!form.value.soDienThoai.trim()) { loiForm.value = 'Vui lòng nhập số điện thoại!'; return }
  if (!form.value.ngayDa) { loiForm.value = 'Vui lòng chọn ngày đá!'; return }
  if (!form.value.khungGioId) { loiForm.value = 'Vui lòng chọn khung giờ!'; return }

  if (khungGioChon.value && khungGioBiKhoa(khungGioChon.value)) {
    loiForm.value = khungGioBiTrung(khungGioChon.value)
      ? 'Khung giờ này đã có người đặt, vui lòng chọn khung giờ khác!'
      : 'Khung giờ này đã qua giờ đá hôm nay, vui lòng chọn khung giờ khác!'
    return
  }

  if (voucherMa.value.trim() && !voucherHopLe.value) { await kiemTraVoucher(); if (!voucherHopLe.value) return }

  if (form.value.phuongThuc === 'QR') {
    await handleHoldSlot()
  } else {
    await xacNhanDatTienMat()
  }
}

// 1. Luồng QR: Gọi Hold Slot để giữ chỗ 5 phút
async function handleHoldSlot() {
  dangGui.value = true
  loiForm.value = ''
  try {
    const token = localStorage.getItem('token')
    const res = await fetch(`${API}/dat-san/hold-slot`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({
        sanBongId: route.params.id,
        hoTenDat: form.value.hoTenDat,
        soDienThoai: form.value.soDienThoai,
        ngayDa: form.value.ngayDa,
        gioBatDau: form.value.gioBatDau,
        gioKetThuc: form.value.gioKetThuc,
        phuongThuc: 'QR',
        voucherMa: voucherMa.value.trim()
      })
    })

    const data = await res.json()
    if (!res.ok) throw new Error(data.message || 'Lỗi khi giữ chỗ tạm thời!')

    datSanIdHienTai.value = data.datSanId
    thanhToanIdHienTai.value = data.thanhToanId
    expireAtTimestamp.value = Number(data.thongBao) // Epoch millis trả về từ server

    // Lưu SessionStorage để F5 không mất
    sessionStorage.setItem('current_hold_booking', JSON.stringify({
      datSanId: data.datSanId,
      thanhToanId: data.thanhToanId,
      expireAt: expireAtTimestamp.value,
      sanBongId: route.params.id,
      form: form.value
    }))

    await taoGiaoDichSePayVaHienQR(data.thanhToanId)
  } catch (e) {
    loiForm.value = e.message
    await taiKhungGioDaDat()
  } finally {
    dangGui.value = false
  }
}

// 2. Luồng Tiền mặt
async function xacNhanDatTienMat() {
  dangGui.value = true
  try {
    const token = localStorage.getItem('token')
    const res = await fetch(`${API}/dat-san`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({
        sanBongId: route.params.id,
        hoTenDat: form.value.hoTenDat,
        soDienThoai: form.value.soDienThoai,
        ngayDa: form.value.ngayDa,
        gioBatDau: form.value.gioBatDau,
        gioKetThuc: form.value.gioKetThuc,
        phuongThuc: 'TIEN_MAT',
        voucherMa: voucherMa.value.trim()
      })
    })

    if (!res.ok) throw new Error('Đặt sân tiền mặt thất bại!')
    ketQua.value = await res.json()
    thanhToanQRThanhCong.value = false
    buoc.value = 3
  } catch (e) {
    loiForm.value = e.message
  } finally {
    dangGui.value = false
  }
}

async function taoGiaoDichSePayVaHienQR(thanhToanId) {
  const token = localStorage.getItem('token')
  const resSePay = await fetch(`${API}/sepay/tao-giao-dich`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({ thanhToanId })
  })

  if (!resSePay.ok) throw new Error('Không tạo được QR thanh toán SePay!')
  const data = await resSePay.json()

  qrUrl.value = data.qrUrl
  maGiaoDichHienTai.value = data.maGiaoDich
  
  // Gán số tiền lấy từ SePay trả về (hoặc fallback dùng tienCoc nếu SePay chưa có field soTien)
  soTienThanhToan.value = data.soTien || data.soTienThanhToan || tienCoc.value

  buoc.value = 2
  batDauKiemTraThanhToan(thanhToanId)
}

function batDauKiemTraThanhToan(thanhToanId) {
  dungKiemTraThanhToan()
  pollingInterval = setInterval(async () => {
    try {
      const token = localStorage.getItem('token')
      const res = await fetch(`${API}/sepay/trang-thai/${thanhToanId}`, {
        headers: { 'Authorization': `Bearer ${token}` }
      })
      if (!res.ok) return
      const data = await res.json()
      if (data.trangThai === 'THANH_CONG') {
        dungKiemTraThanhToan()
        sessionStorage.removeItem('current_hold_booking')
        thanhToanQRThanhCong.value = true
        buoc.value = 3
      }
    } catch {}
  }, 3000)
}

function dungKiemTraThanhToan() {
  if (pollingInterval) {
    clearInterval(pollingInterval)
    pollingInterval = null
  }
}

// 3. Hủy Giữ chỗ chủ động (khi bấm Quay lại)
async function huyChoThanhToan() {
  dungKiemTraThanhToan()
  if (datSanIdHienTai.value) {
    try {
      const token = localStorage.getItem('token')
      await fetch(`${API}/dat-san/${datSanIdHienTai.value}/cancel-hold`, {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${token}` }
      })
    } catch (e) {
      console.error('Lỗi khi gửi yêu cầu hủy:', e)
    }
  }

  clearHoldState()
  broadcastChannel.postMessage({ type: 'SLOT_CANCELLED' })
  await taiKhungGioDaDat()
}

async function handleCountdownExpired() {
  dungKiemTraThanhToan()
  
  // 1. Gọi API giải phóng slot ngay lập tức khi Hết giờ
  if (datSanIdHienTai.value) {
    try {
      const token = localStorage.getItem('token')
      await fetch(`${API}/dat-san/${datSanIdHienTai.value}/cancel-hold`, {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${token}` }
      })
    } catch (e) {
      console.error('Lỗi khi hủy slot tự động:', e)
    }
  }

  // 2. Dọn dẹp State & Session
  clearHoldState()

  // 3. Thông báo cho các Tab khác (nếu mở nhiều tab)
  broadcastChannel.postMessage({ type: 'SLOT_CANCELLED' })

  // 4. Tải lại danh sách khung giờ mới nhất
  await taiKhungGioDaDat()
}

function clearHoldState() {
  localStorage.removeItem('current_hold_booking')
  datSanIdHienTai.value = null
  thanhToanIdHienTai.value = null
  expireAtTimestamp.value = null
  soTienThanhToan.value = 0 // <--- Reset về 0
  thanhToanQRThanhCong.value = false
  buoc.value = 1
}

// 4. Giải phóng Slot bằng sendBeacon khi đóng tab/rời trang
function handleBeforeUnload() {
  const saved = sessionStorage.getItem('current_hold_booking')
  if (saved) {
    const { datSanId } = JSON.parse(saved)
    const token = localStorage.getItem('token')
    const url = `${API}/dat-san/${datSanId}/cancel-hold`
    
    // Gửi beacon dạng Blob có kèm Header Token
    const headers = { type: 'application/json' }
    const blob = new Blob([JSON.stringify({})], headers)
    navigator.sendBeacon(url, blob)
  }
}

async function taiThongTinSan() {
  const sanId = route.params.id
  if (!sanId) {
    loiTai.value = 'Không xác định được sân.'
    dangTai.value = false
    return
  }
  try {
    const res = await fetch(`${API}/san-bong/${sanId}`)
    if (!res.ok) throw new Error('Không tải được thông tin sân')
    sanBong.value = await res.json()

    const tenLuu = localStorage.getItem('hoTen')
    const sdtLuu = localStorage.getItem('soDienThoai')
    if (tenLuu) form.value.hoTenDat = tenLuu
    if (sdtLuu) form.value.soDienThoai = sdtLuu

    // Khôi phục giữ chỗ nếu F5 trang
    const saved = sessionStorage.getItem('current_hold_booking')
    if (saved) {
      const parsed = JSON.parse(saved)
      if (parsed.sanBongId === sanId && Date.now() < parsed.expireAt) {
        datSanIdHienTai.value = parsed.datSanId
        thanhToanIdHienTai.value = parsed.thanhToanId
        expireAtTimestamp.value = parsed.expireAt
        form.value = parsed.form
        
        await taoGiaoDichSePayVaHienQR(parsed.thanhToanId)
      } else {
        sessionStorage.removeItem('current_hold_booking')
      }
    }

    await taiKhungGioDaDat()
  } catch (e) {
    loiTai.value = e.message
  } finally {
    dangTai.value = false
  }
}

onMounted(() => {
  taiThongTinSan()
  window.addEventListener('beforeunload', handleBeforeUnload)

  broadcastChannel.onmessage = (event) => {
    if (event.data?.type === 'SLOT_CANCELLED') {
      clearHoldState()
      taiKhungGioDaDat()
    }
  }
})

onUnmounted(() => {
  dungKiemTraThanhToan()
  window.removeEventListener('beforeunload', handleBeforeUnload)
  broadcastChannel.close()
})
</script>

<style scoped>
.booking-page {
  background: #f0f4f8;
  min-height: calc(100vh - 76px);
  padding: 40px 0 80px;
}
.page-header {
  text-align: center;
  margin-bottom: 36px;
  position: relative;
}
.btn-back {
  position: absolute;
  left: 0;
  top: 0;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: white;
  border: 1.5px solid #e2e8f0;
  color: #374151;
  padding: 8px 16px;
  border-radius: 999px;
  font-size: 13.5px;
  cursor: pointer;
  transition: .2s;
}
.btn-back:hover { border-color: var(--green-600); color: var(--green-600); }
.page-header h1 { font-size: 34px; font-weight: 800; color: #0d1f3c; }
.page-header p { margin-top: 6px; color: #6b7280; }
.booking-layout {
  display: grid;
  grid-template-columns: 1.1fr 0.9fr;
  gap: 28px;
  align-items: start;
}
/* ── Field Info ── */
.field-info {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 24px rgba(10,37,64,.08);
}
.field-img-wrap { position: relative; }
.field-img-wrap img { width: 100%; height: 280px; object-fit: cover; display: block; }
.badge-loai {
  position: absolute; top: 14px; left: 14px;
  padding: 4px 14px; border-radius: 999px; font-size: 12px; font-weight: 700;
}
.badge-blue { background: rgba(219,234,254,.95); color: #1d4ed8; }
.badge-green { background: rgba(220,252,231,.95); color: #15803d; }
.field-content { padding: 24px; }
.field-content h2 { font-size: 22px; font-weight: 800; color: #0d1f3c; margin-bottom: 12px; }
.field-detail {
  display: flex; align-items: flex-start; gap: 8px;
  margin-bottom: 8px; color: #4b5563; font-size: 14px;
}
.mo-ta { color: #6b7280; line-height: 1.6; margin-top: 4px; }
.gia-section { margin-top: 20px; }
.gia-section h3 { font-size: 14px; font-weight: 700; color: #0d1f3c; margin-bottom: 10px; }
.gia-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(130px, 1fr));
  gap: 8px;
}
.gia-item {
  display: flex; flex-direction: column; gap: 2px;
  padding: 8px 10px; border-radius: 10px;
  background: #f8fafc; border: 1px solid #e2e8f0;
  cursor: pointer; transition: .15s; position: relative;
}
.gia-item:hover:not(.gia-da-dat) {
  border-color: var(--green-600);
  transform: translateY(-1px);
}
.gia-sang { border-color: #bfdbfe; background: #eff6ff; }
.gia-toi { border-color: #bbf7d0; background: #f0fdf4; }
.gia-item.gia-dang-chon {
  border-color: var(--green-600);
  background: #dcfce7;
  box-shadow: 0 0 0 2px var(--green-600);
}
.gia-item.gia-da-dat {
  opacity: 0.45;
  filter: grayscale(60%);
  cursor: not-allowed;
  pointer-events: none;
}
.gia-tag-dat {
  position: absolute;
  top: 4px;
  right: 4px;
  font-size: 10px;
  background: #999;
  color: white;
  padding: 1px 5px;
  border-radius: 4px;
}
.gia-gio { font-size: 11px; font-weight: 600; color: #374151; }
.gia-tien { font-size: 13px; font-weight: 700; color: var(--green-600); }
.hint-ngay {
  font-size: 12px;
  color: #888;
  margin-top: 8px;
}
.policy-box {
  margin-top: 20px; padding: 16px;
  background: #fffbeb; border: 1px solid #fde68a;
  border-radius: 12px;
}
.policy-box h4 { font-size: 13px; font-weight: 700; color: #92400e; margin-bottom: 8px; }
.policy-box ul { padding-left: 4px; list-style: none; }
.policy-box li { font-size: 13px; color: #78350f; margin-bottom: 4px; }
/* ── Booking Card ── */
.booking-card {
  background: white; border-radius: 20px; padding: 28px;
  box-shadow: 0 4px 24px rgba(10,37,64,.08);
  position: sticky; top: 90px;
}
.booking-card h3 { font-size: 20px; font-weight: 800; color: #0d1f3c; margin-bottom: 22px; }
.form-group { margin-bottom: 16px; }
.form-group label { display: block; margin-bottom: 6px; font-size: 13.5px; font-weight: 600; color: #0d1f3c; }
.req { color: #ef4444; }
.form-group input,
.form-group select {
  width: 100%; padding: 12px 14px;
  border: 1.5px solid #d1d5db; border-radius: 12px;
  outline: none; font-size: 14px; font-family: inherit;
  background: #f9fafb; transition: border-color .15s;
  box-sizing: border-box;
}
.form-group input:focus,
.form-group select:focus { border-color: var(--green-600); background: white; }
.khung-gio-da-chon {
  padding: 12px 14px;
  border: 1.5px solid var(--green-600);
  border-radius: 12px;
  background: #f0fdf4;
  font-size: 14px;
  font-weight: 600;
  color: #0d1f3c;
}
.khung-gio-da-chon.chua-chon {
  border-color: #d1d5db;
  background: #f9fafb;
  font-weight: 400;
  color: #9ca3af;
}
.summary {
  margin: 20px 0 0;
  padding: 16px; background: #f0fdf4;
  border: 1.5px solid #bbf7d0; border-radius: 14px;
}
.summary-row {
  display: flex; justify-content: space-between;
  font-size: 14px; color: #374151; margin-bottom: 8px;
}
.summary-row.total {
  font-size: 17px; font-weight: 700; color: #0d1f3c;
  border-top: 1px solid #bbf7d0; padding-top: 10px; margin-top: 4px; margin-bottom: 0;
}
.text-green { color: var(--green-600); }
.loi-form {
  color: #dc2626; font-size: 13px; background: #fef2f2;
  border: 1px solid #fecaca; padding: 10px 14px;
  border-radius: 10px; margin-top: 14px;
}
.btn-group { margin-top: 20px; display: flex; flex-direction: column; gap: 10px; }
.select-tt {
  width: 100%; padding: 11px 14px; border: 1.5px solid #d1d5db;
  border-radius: 12px; font-size: 14px; font-family: inherit;
  background: #f9fafb; outline: none; cursor: pointer;
}
.btn-book {
  width: 100%; padding: 15px; border: none;
  border-radius: 14px;
  background: linear-gradient(135deg, var(--green-500), var(--green-600));
  color: white; font-size: 16px; font-weight: 700;
  cursor: pointer; transition: .3s;
}
.btn-book:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 8px 24px rgba(34,197,94,.35); }
.btn-book:disabled { opacity: .6; cursor: not-allowed; }
/* ── QR Step ── */
.qr-step { text-align: center; }
.qr-header { margin-bottom: 20px; }
.qr-header h3 { font-size: 20px; font-weight: 800; color: #0d1f3c; margin: 10px 0 6px; }
.qr-header p { color: #6b7280; font-size: 14px; }
.qr-box { display: flex; justify-content: center; margin: 16px 0; }
.qr-img { width: 220px; height: 220px; border-radius: 16px; border: 2px solid #e2e8f0; }
.qr-info { text-align: left; background: #f8fafc; border-radius: 12px; padding: 14px; margin-bottom: 20px; }
.info-row { display: flex; justify-content: space-between; font-size: 13.5px; color: #374151; padding: 5px 0; border-bottom: 1px solid #e2e8f0; }
.info-row:last-child { border-bottom: none; }
.btn-back-step {
  width: 100%; margin-top: 10px; padding: 12px;
  border: 1.5px solid #e2e8f0; background: white;
  border-radius: 12px; font-size: 14px; color: #6b7280;
  cursor: pointer; transition: .2s;
}
.btn-back-step:hover { border-color: var(--green-600); color: var(--green-600); }
/* ── Success Step ── */
.success-step { text-align: center; }
.success-icon { font-size: 56px; margin-bottom: 12px; }
.success-step h3 { font-size: 22px; font-weight: 800; color: #0d1f3c; margin-bottom: 8px; }
.success-step > p { color: #374151; font-size: 14px; line-height: 1.6; margin-bottom: 20px; }
.result-card {
  text-align: left; background: #f0fdf4;
  border: 1.5px solid #bbf7d0; border-radius: 14px;
  padding: 16px; margin-bottom: 20px;
}
.result-row {
  display: flex; justify-content: space-between; align-items: center;
  font-size: 13.5px; color: #374151; padding: 6px 0;
  border-bottom: 1px solid #dcfce7;
}
.result-row:last-child { border-bottom: none; }
.badge-trang-thai {
  background: #fef3c7; color: #92400e;
  padding: 2px 10px; border-radius: 999px; font-size: 12px; font-weight: 700;
}
.success-actions { display: flex; flex-direction: column; gap: 10px; }
.btn-outline {
  width: 100%; padding: 13px; border: 1.5px solid var(--green-600);
  background: white; border-radius: 14px; color: var(--green-600);
  font-size: 14px; font-weight: 600; cursor: pointer; transition: .2s;
}
.btn-outline:hover { background: var(--green-50); }
/* ── Loading / Error ── */
.loading-state, .error-state {
  text-align: center; padding: 80px 0; color: #6b7280;
}
.spinner {
  width: 44px; height: 44px; border: 4px solid #e2e8f0;
  border-top-color: var(--green-600); border-radius: 50%;
  animation: spin .7s linear infinite; margin: 0 auto 16px;
}
@keyframes spin { to { transform: rotate(360deg); } }
.error-state button {
  margin-top: 16px; padding: 10px 24px; background: var(--green-600);
  color: white; border: none; border-radius: 12px; cursor: pointer;
}
/* ── Responsive ── */
@media (max-width: 900px) {
  .booking-layout { grid-template-columns: 1fr; }
  .booking-card { position: static; }
  .btn-back { position: static; display: inline-flex; margin-bottom: 16px; }
}
.luu-y-xac-nhan {
  background: #fffbeb;
  border: 1px solid #fde68a;
  color: #92400e;
  font-size: 13px;
  padding: 12px 16px;
  border-radius: 10px;
  text-align: left;
  margin-bottom: 20px;
}
.dang-cho-thanh-toan {
  display: flex; align-items: center; justify-content: center; gap: 10px;
  padding: 14px; background: #eff6ff; border: 1px solid #bfdbfe;
  border-radius: 12px; margin-bottom: 14px; color: #1d4ed8; font-size: 13.5px;
}
.spinner-nho {
  width: 18px; height: 18px; border: 3px solid #bfdbfe;
  border-top-color: #1d4ed8; border-radius: 50%;
  animation: spin .7s linear infinite;
}
.countdown-wrapper {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
  pointer-events: auto;
}
@media (max-width: 640px) {
  .countdown-wrapper {
    position: fixed;
    top: auto;
    bottom: 80px;
    right: 16px;
    padding: 0;
  }
}

/* Khóa bảng chọn khung giờ khi đang thanh toán QR */
.gia-grid.disabled-grid {
  opacity: 0.6;
  pointer-events: none; /* Chặn hoàn toàn mọi thao tác rê chuột / click */
  cursor: not-allowed;
}

.voucher-box{margin:16px 0;padding:14px;border:1px dashed #bbf7d0;border-radius:12px;background:#f0fdf4}.voucher-box label{display:block;font-size:13px;font-weight:700;color:#334155;margin-bottom:7px}.voucher-row{display:flex;gap:8px}.voucher-row input{flex:1;border:1px solid #cbd5e1;border-radius:8px;padding:10px}.voucher-row button{border:0;border-radius:8px;background:#16a34a;color:#fff;padding:0 14px;font-weight:700;cursor:pointer}.voucher-row button:disabled{opacity:.6}.voucher-ok{color:#15803d;font-size:13px;margin:8px 0 0}.voucher-error{color:#dc2626;font-size:13px;margin:8px 0 0}.voucher-note{color:#475569;font-size:12px;margin:5px 0 0}
</style>