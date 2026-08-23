<template>
  <div class="qlhd">

    <!-- ===== HEADER ===== -->
    <div class="page-header">
      <div>
        <span class="eyebrow">⚽ Bảng điều khiển · Staff</span>
        <h1 class="page-title">Quản lý hóa đơn</h1>
        <p class="page-desc">Theo dõi trạng thái thanh toán, xuất và in hóa đơn cho khách hàng.</p>
      </div>
      <button class="btn-ghost" @click="loadInvoices">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 12a9 9 0 1 1-2.64-6.36"/><path d="M21 4v6h-6"/></svg>
        Làm mới
      </button>
    </div>

    <!-- ===== BỘ LỌC ===== -->
    <div class="filter-card">
      <div class="filter-top">
        <div class="search-wrap">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="7"/><path d="M21 21l-4.3-4.3"/></svg>
          <input v-model="keyword" class="search-input" placeholder="Tìm mã HD, khách hàng..."/>
        </div>

        <div class="pill-select">
          <label>Trạng thái thanh toán</label>
          <select v-model="paymentFilter" class="form-control">
            <option value="">Tất cả trạng thái</option>
            <option value="CHUA_THANH_TOAN">Chưa thanh toán</option>
            <option value="THANH_TOAN_MOT_PHAN">Thanh toán một phần</option>
            <option value="DA_THANH_TOAN_DU">Đã thanh toán đủ</option>
          </select>
        </div>

        <button class="btn-ghost btn-ghost--sm" @click="resetFilters" v-if="hasActiveFilters">
          <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4"><path d="M18 6L6 18M6 6l12 12"/></svg>
          Xóa lọc
        </button>
      </div>

      <!-- Bộ lọc theo ngày: dải ngày rõ ràng -->
      <div class="day-strip">
        <span class="day-strip__label">Chọn ngày</span>

        <button class="day-nav" @click="dayOffset -= 7" aria-label="Tuần trước">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4"><path d="M15 18l-6-6 6-6"/></svg>
        </button>

        <button class="day-chip day-chip--all" :class="{ 'day-chip--active': !ngayFilter }" @click="ngayFilter = ''">
          Tất cả
        </button>

        <div class="day-chip-track">
          <button
            v-for="d in ngayOptions"
            :key="d.iso"
            class="day-chip"
            :class="{ 'day-chip--active': ngayFilter === d.iso, 'day-chip--today': d.isToday }"
            @click="ngayFilter = ngayFilter === d.iso ? '' : d.iso"
          >
            <span class="day-chip__dow">{{ d.dow }}</span>
            <span class="day-chip__num">{{ d.dayNum }}</span>
            <span class="day-chip__mon">Th{{ d.month }}</span>
            <span v-if="d.isToday" class="day-chip__tag">Hôm nay</span>
          </button>
        </div>

        <button class="day-nav" @click="dayOffset += 7" aria-label="Tuần sau">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4"><path d="M9 18l6-6-6-6"/></svg>
        </button>

        <div class="day-jump">
          <input type="date" v-model="ngayFilter" class="day-jump__input" title="Chọn ngày bất kỳ"/>
        </div>
      </div>
    </div>

    <p v-if="loi" class="thong-bao-loi">⚠ {{ loi }}</p>
    <div v-if="loading" class="state-box"><span class="spinner"></span> Đang tải hóa đơn...</div>

    <!-- ===== TABLE ===== -->
    <div v-else class="table-card">
      <table class="data-table">
        <thead>
          <tr>
            <th>Mã HD</th>
            <th>Khách hàng</th>
            <th>Ngày đá</th>
            <th>Tiền sân</th>
            <th>Dịch vụ thêm</th>
            <th>Tổng tiền</th>
            <th>Trạng thái</th>
            <th style="text-align:center">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="invoice in filteredInvoices" :key="invoice.datSanId">
            <td><strong class="ma-don">{{ invoice.maHoaDon }}</strong></td>
            <td>
              <div class="cell-stack">
                <span class="bold">{{ invoice.tenKhach }}</span>
              </div>
            </td>
            <td><span class="sub">{{ dinhDangNgay(invoice.ngayDa) }}</span></td>
            <td>{{ formatPrice(invoice.tienSan) }}</td>
            <td>{{ formatPrice(invoice.tienDichVu) }}</td>
            <td><strong class="price">{{ formatPrice(invoice.tongCong) }}</strong></td>
            <td>
              <span :class="['badge', badgeClass(invoice.trangThaiThanhToan)]">
                <span class="badge__dot"></span>{{ nhanTrangThai(invoice.trangThaiThanhToan) }}
              </span>
            </td>
            <td>
              <div class="actions">
                <button class="btn-act detail" @click="viewInvoice(invoice)">🧾 Xem &amp; In</button>
                <button
                  class="btn-act confirm"
                  v-if="invoice.conLai > 0 && invoice.trangThaiDon !== 'DA_HUY'"
                  @click="payInvoice(invoice)"
                >
                  💵 Thu {{ formatPrice(invoice.conLai) }}
                </button>
              </div>
            </td>
          </tr>
          <tr v-if="!loading && filteredInvoices.length === 0">
            <td colspan="8" class="no-data">
              <div class="no-data__icon">🔍</div>
              Không tìm thấy hóa đơn nào phù hợp với bộ lọc hiện tại.
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- ===== MODAL XEM & IN HÓA ĐƠN ===== -->
    <div v-if="showDetail" class="overlay" @click.self="showDetail = false">
      <div class="modal-box modal-box--invoice">
        <div class="modal-header">
          <h2>🧾 Chi tiết hóa đơn</h2>
          <button class="close-btn" @click="showDetail = false">&times;</button>
        </div>

        <div class="modal-body">
          <div v-if="!chiTiet" class="state-box" style="padding:30px 0"><span class="spinner"></span> Đang tải...</div>

          <div v-else id="invoice-print-area" class="invoice-box">
            <div class="invoice-header">
              <h2>BÓNG ĐÁ DREAM LEAGUE</h2>
              <p>Địa chỉ: Hẻm 104 Tân Sơn, Tân Sơn, Hồ Chí Minh</p>
              <p>Hotline: 0343 831 316</p>
            </div>

            <hr class="invoice-divider" />

            <h3 style="text-align:center; margin: 14px 0;">HÓA ĐƠN THANH TOÁN</h3>

            <div class="invoice-info">
              <p><b>Mã hóa đơn:</b> {{ chung.maHoaDon }}</p>
              <p><b>Khách hàng:</b> {{ chung.tenKhach }}</p>
              <p><b>Số điện thoại:</b> {{ chung.soDienThoai }}</p>
              <p><b>Sân:</b> {{ chung.tenSan }} ({{ chung.gioBatDau }} - {{ chung.gioKetThuc }})</p>
              <p><b>Ngày đá:</b> {{ dinhDangNgay(chung.ngayDa) }}</p>
              <p><b>Trạng thái:</b> {{ nhanTrangThai(chung.trangThaiThanhToan) }}</p>
            </div>

            <table class="invoice-table">
              <thead>
                <tr>
                  <th>Nội dung</th>
                  <th style="text-align:right">Thành tiền</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(dong, i) in cacDong" :key="i">
                  <td>{{ dong.moTa }}<span v-if="dong.soLuong > 1"> × {{ dong.soLuong }}</span></td>
                  <td style="text-align:right">{{ formatPrice(dong.thanhTien) }}</td>
                </tr>
                <tr class="invoice-total-row">
                  <td><b>Tổng cộng:</b></td>
                  <td style="text-align:right" class="text-success"><b>{{ formatPrice(chung.tongCong) }}</b></td>
                </tr>
                <tr>
                  <td>Đã thanh toán:</td>
                  <td style="text-align:right">{{ formatPrice(chung.daThanhToan) }}</td>
                </tr>
                <tr>
                  <td><b>Còn lại:</b></td>
                  <td style="text-align:right"><b>{{ formatPrice(chung.conLai) }}</b></td>
                </tr>
              </tbody>
            </table>

            <div class="invoice-footer">
              <p>Cảm ơn quý khách đã tin tưởng và đồng hành!</p>
              <p style="font-size:11px; color:#94a3b8; margin-top:5px;">Hệ thống quản lý trực tuyến Dream League</p>
            </div>
          </div>
        </div>

        <div class="modal-footer">
          <button class="btn-ghost" @click="showDetail = false">Đóng</button>
          <button class="btn-primary" @click="printInvoice" :disabled="!chiTiet">In hóa đơn</button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import axios from "axios"
import { ref, computed, onMounted, watch } from "vue"

// Backend chỉ có 1 endpoint đọc hoá đơn dùng chung cho cả Admin và Staff
const API_BASE = "/api/admin/hoa-don"
const API_THU_TIEN = "/api/staff/thanh-toan/thanh-toan-cuoi"

function authHeader() {
  return { Authorization: `Bearer ${localStorage.getItem("token")}` }
}

const keyword = ref("")
const paymentFilter = ref("")
const ngayFilter = ref("")

const loading = ref(false)
const loi = ref("")

const invoices = ref([])
const showDetail = ref(false)
const chiTiet = ref(null)

const chung = computed(() => chiTiet.value?.thongTinChung || {})
const cacDong = computed(() => chiTiet.value?.cacDong || [])

const NHAN_TRANG_THAI = {
  CHUA_THANH_TOAN: "Chưa thanh toán",
  THANH_TOAN_MOT_PHAN: "Thanh toán một phần",
  DA_THANH_TOAN_DU: "Đã thanh toán đủ"
}
function nhanTrangThai(ma) { return NHAN_TRANG_THAI[ma] || ma }

// Mỗi trạng thái một màu riêng biệt, không dùng chung 1 màu "waiting" như trước
function badgeClass(ma) {
  return {
    CHUA_THANH_TOAN: "badge-unpaid",
    THANH_TOAN_MOT_PHAN: "badge-partial",
    DA_THANH_TOAN_DU: "badge-paid"
  }[ma] || "badge-unpaid"
}

function formatPrice(so) { return new Intl.NumberFormat("vi-VN").format(so || 0) + "đ" }

function dinhDangNgay(ngay) {
  if (!ngay) return ""
  const [y, m, d] = ngay.slice(0, 10).split("-")
  return `${d}/${m}/${y}`
}

function docLoi(e) { return e?.response?.data?.message || e.message || "Đã xảy ra lỗi!" }

async function loadInvoices() {
  loading.value = true
  loi.value = ""
  try {
    const res = await axios.get(API_BASE, {
      params: {
        tuKhoa: keyword.value || undefined,
        trangThaiThanhToan: paymentFilter.value || undefined,
        ngay: ngayFilter.value || undefined
      },
      headers: authHeader()
    })
    invoices.value = res.data
  } catch (e) {
    loi.value = docLoi(e)
    invoices.value = []
  } finally {
    loading.value = false
  }
}

onMounted(loadInvoices)

let timer = null
watch([keyword, paymentFilter, ngayFilter], () => {
  clearTimeout(timer)
  timer = setTimeout(loadInvoices, 300)
})

// Lọc thêm ở phía client để đảm bảo đúng ngay cả khi backend chưa hỗ trợ tham số "ngay"
const filteredInvoices = computed(() => {
  if (!ngayFilter.value) return invoices.value
  return invoices.value.filter(inv => (inv.ngayDa || "").slice(0, 10) === ngayFilter.value)
})

const hasActiveFilters = computed(() => !!(keyword.value || paymentFilter.value || ngayFilter.value))
function resetFilters() {
  keyword.value = ""
  paymentFilter.value = ""
  ngayFilter.value = ""
}

/* ================= DẢI CHỌN NGÀY ================= */
const dayOffset = ref(0)
const DOW = ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7']

function toIso(d) {
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

const ngayOptions = computed(() => {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  const arr = []
  for (let i = 0; i < 7; i++) {
    const d = new Date(today)
    d.setDate(d.getDate() + dayOffset.value + i)
    const iso = toIso(d)
    arr.push({
      iso,
      dow: DOW[d.getDay()],
      dayNum: d.getDate(),
      month: d.getMonth() + 1,
      isToday: iso === toIso(today)
    })
  }
  return arr
})

/** Mở hoá đơn chi tiết */
async function viewInvoice(item) {
  chiTiet.value = null
  showDetail.value = true
  try {
    const res = await axios.get(`${API_BASE}/${item.datSanId}`, { headers: authHeader() })
    chiTiet.value = res.data
  } catch (e) {
    showDetail.value = false
    alert(docLoi(e))
  }
}

/** Thu nốt phần tiền còn lại của hoá đơn */
async function payInvoice(item) {
  const phuongThuc = confirm(
    `Thu ${formatPrice(item.conLai)} của đơn ${item.maHoaDon}.\n\nOK = Tiền mặt, Cancel = Chuyển khoản QR`
  ) ? "TIEN_MAT" : "QR"

  try {
    await axios.post(API_THU_TIEN, { datSanId: item.datSanId, phuongThuc }, { headers: authHeader() })
    await loadInvoices()
  } catch (e) {
    alert(docLoi(e))
  }
}

/** In hoá đơn: mở cửa sổ in chỉ chứa vùng hoá đơn */
function printInvoice() {
  const vung = document.getElementById("invoice-print-area")
  if (!vung) return
  const cuaSo = window.open("", "_blank")
  if (!cuaSo) return
  cuaSo.document.write(`
        <html>
          <head>
            <title>Hoá đơn ${chung.value.maHoaDon || ""}</title>
            <style>
              body { font-family: Arial, sans-serif; padding: 24px; }
              table { width: 100%; border-collapse: collapse; margin-top: 12px; }
              th, td { padding: 8px; border-bottom: 1px solid #ddd; }
              .invoice-header { text-align: center; }
            </style>
          </head>
          <body>${vung.innerHTML}</body>
        </html>
    `)
  cuaSo.document.close()
  cuaSo.focus()
  cuaSo.print()
  cuaSo.close()
}
</script>

<style scoped>
/* ============================================================
   TOKENS — đồng bộ với trang chủ, Quản lý đặt sân & Thanh toán
   ============================================================ */
.qlhd {
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

  width: 100%;
  min-height: 100%;
  background: var(--night-950);
  color: var(--chalk-050);
  font-family: var(--font-body);
  padding: 32px clamp(16px, 3vw, 40px) 60px;
  box-sizing: border-box;
}

/* ============================================================
   HEADER
   ============================================================ */
.page-header { display: flex; justify-content: space-between; align-items: flex-end; gap: 16px; margin-bottom: 22px; flex-wrap: wrap; }
.eyebrow { display: inline-block; font-family: var(--font-mono); font-size: 11.5px; letter-spacing: .14em; text-transform: uppercase; color: var(--lime-300); }
.page-title { font-family: var(--font-display); font-weight: 600; font-size: clamp(24px, 2.6vw, 30px); margin: 8px 0 4px; color: var(--chalk-050); }
.page-desc { font-size: 13.5px; color: var(--chalk-200); opacity: .72; margin: 0; }

/* ============================================================
   BUTTONS
   ============================================================ */
.btn-primary {
  display: inline-flex; align-items: center; gap: 8px; padding: 12px 22px;
  background: linear-gradient(135deg, var(--turf-500), var(--turf-700)); color: var(--chalk-050);
  border: none; border-radius: 999px; font-weight: 700; font-size: 14px; cursor: pointer;
  box-shadow: 0 10px 24px -10px rgba(20,98,57,.75); transition: transform .15s ease, box-shadow .15s ease;
}
.btn-primary:hover { transform: translateY(-2px); box-shadow: 0 14px 28px -8px rgba(182,255,60,.35); }
.btn-primary:disabled { opacity: .5; cursor: not-allowed; transform: none; box-shadow: none; }

.btn-ghost {
  display: inline-flex; align-items: center; gap: 6px; padding: 10px 20px;
  background: rgba(247,251,244,.06); color: var(--chalk-050); border: 1.5px solid rgba(247,251,244,.2);
  border-radius: 999px; font-weight: 600; font-size: 13.5px; cursor: pointer; transition: all .15s ease; white-space: nowrap;
}
.btn-ghost:hover { border-color: var(--lime-400); color: var(--lime-300); background: rgba(182,255,60,.08); }
.btn-ghost--sm { padding: 8px 15px; font-size: 12.5px; flex-shrink: 0; }

/* ============================================================
   FILTER CARD
   ============================================================ */
.filter-card {
  background: rgba(247,251,244,.03); border: 1px solid rgba(247,251,244,.1); border-radius: 18px;
  padding: 18px 20px; margin-bottom: 22px; backdrop-filter: blur(6px);
}
.filter-top { display: flex; flex-wrap: wrap; gap: 12px; align-items: flex-end; margin-bottom: 16px; }

.search-wrap {
  flex: 1.6; min-width: 220px; display: flex; align-items: center; gap: 8px;
  background: rgba(5,11,8,.4); border: 1px solid rgba(247,251,244,.15); border-radius: 10px; padding: 0 12px;
}
.search-wrap svg { color: var(--chalk-200); opacity: .6; flex-shrink: 0; }
.search-input { flex: 1; border: none; background: transparent; outline: none; padding: 10px 0; font-size: 13.5px; color: var(--chalk-050); font-family: var(--font-body); }
.search-input::placeholder { color: var(--chalk-200); opacity: .45; }

.pill-select { display: flex; flex-direction: column; gap: 5px; min-width: 200px; }
.pill-select label { font-family: var(--font-mono); font-size: 10.5px; text-transform: uppercase; letter-spacing: .06em; color: var(--lime-300); padding-left: 2px; }

.form-control {
  padding: 10px 13px; border: 1px solid rgba(247,251,244,.15); border-radius: 10px; font-size: 13.5px;
  outline: none; background: rgba(5,11,8,.4); color: var(--chalk-050); font-family: var(--font-body);
  transition: border-color .15s;
}
.form-control:focus { border-color: var(--lime-400); box-shadow: 0 0 0 3px rgba(182,255,60,.12); }
select.form-control { cursor: pointer; }
select.form-control option { background: var(--night-800); color: var(--chalk-050); }

/* ---- Dải chọn ngày ---- */
.day-strip { display: flex; align-items: center; gap: 10px; padding-top: 14px; border-top: 1px solid rgba(247,251,244,.08); flex-wrap: wrap; }
.day-strip__label { font-family: var(--font-mono); font-size: 10.5px; text-transform: uppercase; letter-spacing: .06em; color: var(--lime-300); flex-shrink: 0; margin-right: 2px; }

.day-nav {
  width: 30px; height: 30px; border-radius: 50%; border: 1px solid rgba(247,251,244,.18);
  background: rgba(247,251,244,.04); color: var(--chalk-200); display: flex; align-items: center; justify-content: center;
  cursor: pointer; flex-shrink: 0; transition: all .15s;
}
.day-nav:hover { border-color: var(--lime-400); color: var(--lime-300); }

.day-chip-track { display: flex; gap: 8px; overflow-x: auto; padding: 2px; scrollbar-width: thin; }
.day-chip-track::-webkit-scrollbar { height: 5px; }
.day-chip-track::-webkit-scrollbar-thumb { background: rgba(182,255,60,.25); border-radius: 10px; }

.day-chip {
  flex-shrink: 0; display: flex; flex-direction: column; align-items: center; justify-content: center;
  gap: 1px; width: 58px; padding: 7px 4px; border-radius: 12px; border: 1px solid rgba(247,251,244,.14);
  background: rgba(247,251,244,.03); color: var(--chalk-200); cursor: pointer; transition: all .15s; position: relative;
}
.day-chip:hover { border-color: rgba(182,255,60,.4); background: rgba(182,255,60,.06); }
.day-chip--today { border-color: rgba(182,255,60,.35); }
.day-chip--active {
  background: linear-gradient(135deg, var(--turf-500), var(--turf-700)); border-color: var(--turf-500); color: var(--chalk-050);
  box-shadow: 0 8px 18px -8px rgba(182,255,60,.4);
}
.day-chip__dow { font-family: var(--font-mono); font-size: 10px; text-transform: uppercase; opacity: .75; }
.day-chip__num { font-family: var(--font-display); font-size: 18px; font-weight: 600; line-height: 1.1; }
.day-chip__mon { font-size: 9.5px; opacity: .6; }
.day-chip__tag {
  position: absolute; top: -8px; left: 50%; transform: translateX(-50%); background: var(--lime-400); color: var(--night-950);
  font-size: 8.5px; font-weight: 800; padding: 1px 6px; border-radius: 999px; white-space: nowrap;
}
.day-chip--all { width: auto; padding: 7px 14px; flex-direction: row; font-size: 12.5px; font-weight: 600; }

.day-jump { margin-left: auto; flex-shrink: 0; }
.day-jump__input {
  padding: 8px 12px; border-radius: 10px; border: 1px dashed rgba(247,251,244,.25); background: rgba(5,11,8,.4);
  color: var(--chalk-200); font-size: 12.5px; font-family: var(--font-mono); outline: none; cursor: pointer;
}
.day-jump__input:focus { border-color: var(--lime-400); border-style: solid; }

/* ============================================================
   STATE / ERROR BOX
   ============================================================ */
.thong-bao-loi {
  color: #ff9686; background: rgba(255,71,87,.1); border: 1px solid rgba(255,71,87,.3);
  border-radius: 10px; padding: 12px 16px; margin-bottom: 16px; font-size: 13.5px;
}
.state-box { text-align: center; padding: 60px 20px; color: var(--chalk-200); opacity: .75; font-size: 14.5px; display: flex; align-items: center; justify-content: center; gap: 10px; }
.spinner { width: 16px; height: 16px; border-radius: 50%; border: 2px solid rgba(182,255,60,.25); border-top-color: var(--lime-400); animation: spin .7s linear infinite; flex-shrink: 0; }
@keyframes spin { to { transform: rotate(360deg); } }

/* ============================================================
   TABLE
   ============================================================ */
.table-card { background: rgba(247,251,244,.03); border: 1px solid rgba(247,251,244,.1); border-radius: 18px; overflow: hidden; }
.data-table { width: 100%; border-collapse: collapse; font-size: 13.5px; }
.data-table th {
  background: rgba(247,251,244,.04); color: var(--lime-300); font-family: var(--font-mono); font-weight: 600;
  font-size: 11px; text-transform: uppercase; letter-spacing: .05em; padding: 14px 16px; text-align: left;
  border-bottom: 1px solid rgba(247,251,244,.1);
}
.data-table td { padding: 13px 16px; border-bottom: 1px solid rgba(247,251,244,.06); vertical-align: middle; }
.data-table tbody tr:hover { background: rgba(182,255,60,.04); }
.cell-stack { display: flex; flex-direction: column; gap: 3px; }
.bold { font-weight: 600; color: var(--chalk-050); }
.sub { font-size: 12.5px; color: var(--chalk-200); opacity: .75; }
.ma-don { color: var(--lime-300); font-size: 12.5px; font-family: var(--font-mono); }
.price { color: var(--lime-300); }
.text-success { color: var(--lime-300); }

/* BADGES — mỗi trạng thái một màu riêng */
.badge { display: inline-flex; align-items: center; gap: 6px; padding: 5px 12px; border-radius: 20px; font-size: 12px; font-weight: 700; }
.badge__dot { width: 6px; height: 6px; border-radius: 50%; flex-shrink: 0; }

.badge-unpaid { background: rgba(255,71,87,.14); color: #ff9686; }
.badge-unpaid .badge__dot { background: var(--crimson-500); }

.badge-partial { background: rgba(255,176,32,.15); color: var(--amber-400); }
.badge-partial .badge__dot { background: var(--amber-400); }

.badge-paid { background: rgba(182,255,60,.15); color: var(--lime-300); }
.badge-paid .badge__dot { background: var(--lime-400); }

/* ACTIONS */
.actions { display: flex; gap: 6px; justify-content: center; flex-wrap: wrap; }
.btn-act { padding: 7px 12px; border-radius: 7px; border: none; font-size: 12px; font-weight: 700; cursor: pointer; transition: .15s; white-space: nowrap; }
.btn-act.detail { background: rgba(182,255,60,.1); color: var(--lime-300); }
.btn-act.detail:hover { background: rgba(182,255,60,.18); }
.btn-act.confirm { background: rgba(35,147,90,.2); color: #6fe0a6; }
.btn-act.confirm:hover { background: rgba(35,147,90,.32); }

.no-data { text-align: center; padding: 50px 20px; color: var(--chalk-200); opacity: .6; }
.no-data__icon { font-size: 26px; margin-bottom: 8px; }

/* ============================================================
   MODAL — khung tối, nhưng vùng hoá đơn giữ nền giấy sáng để in đẹp
   ============================================================ */
.overlay { position: fixed; inset: 0; background: rgba(5,11,8,.75); backdrop-filter: blur(3px); display: flex; align-items: center; justify-content: center; z-index: 200; padding: 20px; }
.modal-box { background: var(--night-800); border: 1px solid rgba(182,255,60,.2); border-radius: 18px; width: 100%; max-width: 480px; box-shadow: 0 24px 50px rgba(0,0,0,.5); overflow: hidden; max-height: 88vh; display: flex; flex-direction: column; }
.modal-box--invoice { max-width: 560px; }
.modal-header { padding: 18px 22px; border-bottom: 1px solid rgba(247,251,244,.1); display: flex; justify-content: space-between; align-items: center; flex-shrink: 0; }
.modal-header h2 { font-family: var(--font-display); font-size: 17px; font-weight: 600; color: var(--chalk-050); margin: 0; }
.close-btn { background: none; border: none; font-size: 22px; color: var(--chalk-200); opacity: .6; cursor: pointer; }
.close-btn:hover { opacity: 1; color: var(--lime-300); }
.modal-body { padding: 20px 22px; overflow-y: auto; }
.modal-footer { padding: 14px 22px; border-top: 1px solid rgba(247,251,244,.1); display: flex; justify-content: flex-end; gap: 10px; flex-shrink: 0; }

/* Hóa đơn giữ tông giấy sáng thật để xem trước khi in cho đúng thực tế */
.invoice-box { border: 1px dashed #cbd5e1; padding: 20px; border-radius: 8px; background: #fafafa; color: #1e293b; }
.invoice-header { text-align: center; font-size: 14px; color: #475569; }
.invoice-header h2 { font-size: 20px; color: #0f172a; margin-bottom: 5px; font-family: var(--font-display); }
.invoice-divider { border: none; border-top: 1px dashed #cbd5e1; margin: 15px 0; }
.invoice-info p { margin-bottom: 6px; font-size: 14px; }
.invoice-table { width: 100%; margin-top: 15px; border-collapse: collapse; }
.invoice-table th { background: #f1f5f9; padding: 8px; font-size: 13px; }
.invoice-table td { padding: 10px 8px; font-size: 14px; }
.invoice-total-row td { border-top: 2px solid #e2e8f0; padding-top: 12px; }
.invoice-box .text-success { color: #16a34a; }
.invoice-footer { text-align: center; margin-top: 25px; font-style: italic; font-size: 13px; color: #64748b; }

/* ============================================================
   RESPONSIVE
   ============================================================ */
@media (max-width: 720px) {
  .filter-top { flex-direction: column; align-items: stretch; }
  .pill-select { min-width: 0; }
  .day-jump { margin-left: 0; }
  .data-table { font-size: 12.5px; }
}
</style>