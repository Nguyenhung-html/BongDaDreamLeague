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

              <!-- Bảng giá -->
              <div class="gia-section" v-if="sanBong.danhSachGia?.length">
                <h3>Bảng giá theo khung giờ</h3>
                <div class="gia-grid">
                  <div
                    v-for="g in sanBong.danhSachGia" :key="g.id"
                    class="gia-item"
                    :class="{ 'gia-sang': parseInt(g.gioBatDau) < 11, 'gia-toi': parseInt(g.gioBatDau) >= 17 }"
                  >
                    <span class="gia-gio">{{ g.gioBatDau }}–{{ g.gioKetThuc }}</span>
                    <span class="gia-tien">{{ formatTien(g.giaTien) }}đ</span>
                  </div>
                </div>
              </div>

              <!-- Chính sách -->
              <div class="policy-box">
                <h4>📋 Chính sách đặt sân</h4>
                <ul>
                  <li>💳 Đặt cọc <strong>50%</strong> tổng tiền để xác nhận booking</li>
                  <li>🏟️ Thanh toán 50% còn lại trực tiếp tại sân trước giờ đá</li>
                  <li>⏰ Huỷ trước <strong>20 phút</strong> được hoàn cọc đầy đủ</li>
                  <li>❌ Huỷ muộn hơn sẽ mất tiền cọc</li>
                  <li>📌 Tối đa <strong>2 sân / người / ngày</strong></li>
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

              <div class="form-group">
                <label>Ngày đá <span class="req">*</span></label>
                <input v-model="form.ngayDa" type="date" :min="ngayToiThieu" />
              </div>

              <div class="form-group">
                <label>Khung giờ <span class="req">*</span></label>
                <select v-model="form.khungGioId" @change="onChonKhungGio">
                  <option value="">-- Chọn khung giờ --</option>
                  <option v-for="g in sanBong.danhSachGia" :key="g.id" :value="g.id">
                    {{ g.gioBatDau }} – {{ g.gioKetThuc }} &nbsp;|&nbsp; {{ formatTien(g.giaTien) }}đ
                  </option>
                </select>
              </div>

              <!-- Tóm tắt giá -->
              <div class="summary" v-if="khungGioChon">
                <div class="summary-row">
                  <span>Giá thuê</span>
                  <strong>{{ formatTien(khungGioChon.giaTien) }} VNĐ</strong>
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
                  {{ form.phuongThuc === 'QR' ? 'Tiếp tục – Quét QR' : 'Xác nhận đặt sân' }}
                </button>
              </div>
            </div>

            <!-- Bước 2: QR thanh toán -->
            <div v-else-if="buoc === 2" class="qr-step">
              <div class="qr-header">
                <svg width="40" height="40" viewBox="0 0 24 24" fill="none"><path d="M9 12l2 2 4-4" stroke="#22c55e" stroke-width="2.5" stroke-linecap="round"/><circle cx="12" cy="12" r="10" stroke="#22c55e" stroke-width="2"/></svg>
                <h3>Quét mã QR để đặt cọc</h3>
                <p>Chuyển khoản <strong>{{ formatTien(tienCoc) }} VNĐ</strong> để xác nhận booking</p>
              </div>

              <div class="qr-box">
                <img
                  :src="`https://img.vietqr.io/image/970422-0973728967-compact2.png?amount=${tienCoc}&addInfo=DatSan+${sanBong.tenSan?.replace(/\s/g,'+')}+${form.ngayDa}&accountName=NguyenTienHung`"
                  alt="QR thanh toán"
                  class="qr-img"
                />
              </div>

              <div class="qr-info">
                <div class="info-row"><span>Ngân hàng</span><strong>MB Bank (970422)</strong></div>
                <div class="info-row"><span>Số TK</span><strong>0973728967</strong></div>
                <div class="info-row"><span>Chủ TK</span><strong>NGUYEN TIEN HUNG</strong></div>
                <div class="info-row"><span>Số tiền</span><strong class="text-green">{{ formatTien(tienCoc) }} VNĐ</strong></div>
                <div class="info-row"><span>Nội dung</span><strong>DatSan {{ sanBong.tenSan }} {{ form.ngayDa }}</strong></div>
              </div>

              <button class="btn-book" @click="xacNhanDat" :disabled="dangGui">
                <span v-if="dangGui">Đang xử lý...</span>
                <span v-else>✅ Tôi đã chuyển khoản xong</span>
              </button>
              <button class="btn-back-step" @click="buoc = 1">← Quay lại</button>
            </div>

            <!-- Bước 3: Thành công -->
            <div v-else-if="buoc === 3" class="success-step">
              <div class="success-icon">🎉</div>
              <h3>Đặt sân thành công!</h3>
              <p>{{ ketQua?.thongBao }}</p>

              <div class="result-card" v-if="ketQua">
                <div class="result-row"><span>Sân</span><strong>{{ ketQua.tenSan }}</strong></div>
                <div class="result-row"><span>Ngày</span><strong>{{ ketQua.ngayDa }}</strong></div>
                <div class="result-row"><span>Giờ</span><strong>{{ ketQua.gioBatDau }} – {{ ketQua.gioKetThuc }}</strong></div>
                <div class="result-row"><span>Tổng tiền</span><strong>{{ formatTien(ketQua.tongTien) }} VNĐ</strong></div>
                <div class="result-row"><span>Đã cọc</span><strong class="text-green">{{ formatTien(ketQua.tienCoc) }} VNĐ</strong></div>
                <div class="result-row"><span>Trạng thái</span><span class="badge-trang-thai">Chờ xác nhận</span></div>
              </div>

              <div class="success-actions">
                <button class="btn-book" @click="$router.push('/lich-su-dat-san')">Xem lịch sử đặt sân</button>
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
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const API = 'http://localhost:8080/api'

// ── State ──
const dangTai = ref(true)
const loiTai = ref('')
const sanBong = ref(null)
const buoc = ref(1)
const dangGui = ref(false)
const loiForm = ref('')
const ketQua = ref(null)

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

const tienCoc = computed(() => {
  if (!khungGioChon.value) return 0
  return Math.round(Number(khungGioChon.value.giaTien) / 2)
})

// ── Methods ──
function onChonKhungGio() {
  const g = khungGioChon.value
  if (g) {
    form.value.gioBatDau = g.gioBatDau
    form.value.gioKetThuc = g.gioKetThuc
  }
}

function formatTien(so) {
  return Number(so).toLocaleString('vi-VN')
}

async function taiThongTinSan() {
  const sanId = route.params.id
  if (!sanId) {
    loiTai.value = 'Không xác định được sân. Vui lòng chọn lại từ danh sách.'
    dangTai.value = false
    return
  }
  try {
    const res = await fetch(`${API}/san-bong/${sanId}`)
    if (!res.ok) throw new Error('Không tải được thông tin sân')
    sanBong.value = await res.json()
    // Prefill họ tên & SĐT từ localStorage
    const tenLuu = localStorage.getItem('userName')
    if (tenLuu) form.value.hoTenDat = tenLuu
  } catch (e) {
    loiTai.value = e.message
  } finally {
    dangTai.value = false
  }
}

function buocTiep() {
  loiForm.value = ''
  if (!form.value.hoTenDat.trim()) { loiForm.value = 'Vui lòng nhập họ và tên!'; return }
  if (!form.value.soDienThoai.trim()) { loiForm.value = 'Vui lòng nhập số điện thoại!'; return }
  if (!form.value.ngayDa) { loiForm.value = 'Vui lòng chọn ngày đá!'; return }
  if (!form.value.khungGioId) { loiForm.value = 'Vui lòng chọn khung giờ!'; return }

  if (form.value.phuongThuc === 'QR') {
    buoc.value = 2
  } else {
    xacNhanDat()
  }
}

async function xacNhanDat() {
  dangGui.value = true
  loiForm.value = ''
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
        phuongThuc: form.value.phuongThuc
      })
    })

    // Đọc response dạng text trước để tránh crash khi backend trả về plain text
    const rawText = await res.text()

    if (!res.ok) {
      // Thử parse lỗi dạng JSON {"message": "..."} nếu có, còn không thì dùng text thẳng
      try {
        const errJson = JSON.parse(rawText)
        throw new Error(errJson.message || errJson.error || rawText)
      } catch {
        // rawText không phải JSON → hiển thị trực tiếp (loại bỏ prefix "Lỗi: " nếu có)
        throw new Error(rawText.replace(/^Lỗi:\s*/, '') || 'Đặt sân thất bại!')
      }
    }

    // Thành công → parse JSON kết quả
    ketQua.value = JSON.parse(rawText)
    buoc.value = 3
  } catch (e) {
    loiForm.value = e.message
    if (buoc.value === 2) buoc.value = 1
  } finally {
    dangGui.value = false
  }
}

onMounted(taiThongTinSan)
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
}
.gia-sang { border-color: #bfdbfe; background: #eff6ff; }
.gia-toi { border-color: #bbf7d0; background: #f0fdf4; }

.gia-gio { font-size: 11px; font-weight: 600; color: #374151; }
.gia-tien { font-size: 13px; font-weight: 700; color: var(--green-600); }

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
</style>