<template>
  <div class="qlvc">

    <!-- ===== HEADER ===== -->
    <div class="page-header">
      <div>
        <span class="eyebrow">⚽ Bảng điều khiển · Admin</span>
        <h1 class="page-title">Quản lý Voucher</h1>
        <p class="page-desc">Tạo ưu đãi theo hạng khách hàng và theo dõi lượt sử dụng.</p>
      </div>
      <button class="btn-primary" @click="moThem">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 5v14M5 12h14"/></svg>
        Thêm voucher
      </button>
    </div>

    <!-- ===== BỘ LỌC ===== -->
    <div class="filter-card">
      <div class="filter-top">
        <div class="search-wrap">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="7"/><path d="M21 21l-4.3-4.3"/></svg>
          <input v-model="tuKhoa" class="search-input" placeholder="Tìm mã hoặc tên voucher..."/>
        </div>

        <div class="pill-select">
          <label>Đối tượng</label>
          <select v-model="locDoiTuong" class="form-control">
            <option value="">Tất cả hạng</option>
            <option value="ALL">Tất cả</option>
            <option value="NEW_USER">Khách mới</option>
            <option value="SILVER">Silver</option>
            <option value="GOLD">Gold</option>
            <option value="DIAMOND">Diamond</option>
          </select>
        </div>
      </div>
    </div>

    <!-- ===== TABLE ===== -->
    <div class="table-card">
      <div class="table-responsive">
        <table class="data-table">
          <thead>
            <tr>
              <th>Mã</th>
              <th>Tên</th>
              <th>Giảm</th>
              <th>Đối tượng</th>
              <th>Đã dùng</th>
              <th>Thời gian</th>
              <th>Trạng thái</th>
              <th style="text-align:center">Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="v in dsLoc" :key="v.id">
              <td><strong class="ma-don">{{ v.ma }}</strong></td>
              <td><span class="bold">{{ v.ten }}</span></td>
              <td>
                <span class="price">
                  {{ v.loaiGiam === 'PHAN_TRAM' ? v.giaTriGiam + '%' : format(v.giaTriGiam) + 'đ' }}
                </span>
              </td>
              <td><span class="tag-pill">{{ nhanHang(v.doiTuongKhachHang) }}</span></td>
              <td>
                <div class="usage">
                  <span class="usage__text">{{ v.daSuDung }} / {{ v.soLuong }}</span>
                  <div class="usage__bar"><div class="usage__fill" :style="{ width: usagePercent(v) + '%' }"></div></div>
                </div>
              </td>
              <td><span class="sub">{{ date(v.ngayBatDau) }} → {{ date(v.ngayKetThuc) }}</span></td>
              <td>
                <span :class="['badge', badgeClass(v.trangThai)]">
                  <span class="badge__dot"></span>{{ nhanTrangThai(v.trangThai) }}
                </span>
              </td>
              <td>
                <div class="actions">
                  <button class="btn-icon btn-edit" title="Sửa" @click="moSua(v)">
                    <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                  </button>
                  <button class="btn-icon btn-delete" title="Xóa" @click="xoa(v)">
                    <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="!dsLoc.length">
              <td colspan="8" class="no-data">
                <div class="no-data__icon">🎟️</div>
                Chưa có voucher nào phù hợp.
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- ===== MODAL THÊM / SỬA VOUCHER ===== -->
    <div v-if="hienForm" class="overlay" @click.self="hienForm = false">
      <div class="modal-box wide">
        <div class="modal-header">
          <h2>{{ dangSua ? '✏️ Sửa voucher' : '🎟️ Thêm voucher' }}</h2>
          <button class="close-btn" @click="hienForm = false">&times;</button>
        </div>

        <div class="modal-body">
          <div class="grid">
            <label class="field">
              <span class="form-label">Mã voucher</span>
              <input v-model="form.ma" class="form-control" placeholder="VD: SUMMER50"/>
            </label>
            <label class="field">
              <span class="form-label">Tên voucher</span>
              <input v-model="form.ten" class="form-control" placeholder="VD: Ưu đãi mùa hè"/>
            </label>
            <label class="field">
              <span class="form-label">Loại giảm</span>
              <select v-model="form.loaiGiam" class="form-control">
                <option value="CO_DINH">Số tiền</option>
                <option value="PHAN_TRAM">Phần trăm</option>
              </select>
            </label>
            <label class="field">
              <span class="form-label">Giá trị</span>
              <input v-model.number="form.giaTriGiam" type="number" class="form-control"/>
            </label>
            <label class="field">
              <span class="form-label">Giảm tối đa</span>
              <input v-model.number="form.giamToiDa" type="number" class="form-control"/>
            </label>
            <label class="field">
              <span class="form-label">Đơn tối thiểu</span>
              <input v-model.number="form.donToiThieu" type="number" class="form-control"/>
            </label>
            <label class="field">
              <span class="form-label">Đối tượng</span>
              <select v-model="form.doiTuongKhachHang" class="form-control">
                <option value="ALL">Tất cả</option>
                <option value="NEW_USER">Khách mới</option>
                <option value="SILVER">Silver</option>
                <option value="GOLD">Gold</option>
                <option value="DIAMOND">Diamond</option>
              </select>
            </label>
            <label class="field">
              <span class="form-label">Số lượng</span>
              <input v-model.number="form.soLuong" type="number" class="form-control"/>
            </label>
            <label class="field">
              <span class="form-label">Giới hạn / khách</span>
              <input v-model.number="form.gioiHanMoiKhach" type="number" class="form-control"/>
            </label>
            <label class="field">
              <span class="form-label">Bắt đầu</span>
              <input v-model="form.ngayBatDau" type="datetime-local" class="form-control"/>
            </label>
            <label class="field">
              <span class="form-label">Kết thúc</span>
              <input v-model="form.ngayKetThuc" type="datetime-local" class="form-control"/>
            </label>
            <label class="field">
              <span class="form-label">Trạng thái</span>
              <select v-model="form.trangThai" class="form-control">
                <option value="HOAT_DONG">Hoạt động</option>
                <option value="TAM_DUNG">Tạm dừng</option>
                <option value="HET_HAN">Hết hạn</option>
              </select>
            </label>
          </div>

          <label class="field full">
            <span class="form-label">Mô tả</span>
            <textarea v-model="form.moTa" class="form-control" rows="3" placeholder="Mô tả điều kiện áp dụng voucher..."></textarea>
          </label>

          <p v-if="loi" class="thong-bao-loi">⚠ {{ loi }}</p>
        </div>

        <div class="modal-footer">
          <button class="btn-ghost" @click="hienForm = false">Hủy</button>
          <button class="btn-primary" @click="luu">Lưu voucher</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import voucherService from '../../services/voucherService'

const ds = ref([]), tuKhoa = ref(''), locDoiTuong = ref(''), hienForm = ref(false), dangSua = ref(false), loi = ref(''), idSua = ref(null)

const empty = () => ({
  ma: '', ten: '', moTa: '', loaiGiam: 'CO_DINH', giaTriGiam: 50000, giamToiDa: null, donToiThieu: null,
  doiTuongKhachHang: 'ALL', soLuong: 100, gioiHanMoiKhach: 1,
  ngayBatDau: toLocal(new Date()), ngayKetThuc: toLocal(new Date(Date.now() + 30 * 86400000)),
  trangThai: 'HOAT_DONG'
})
const form = ref(empty())

function toLocal(d) { const z = new Date(d.getTime() - d.getTimezoneOffset() * 60000); return z.toISOString().slice(0, 16) }

const dsLoc = computed(() =>
  ds.value.filter(v =>
    (!locDoiTuong.value || v.doiTuongKhachHang === locDoiTuong.value) &&
    (!tuKhoa.value || `${v.ma} ${v.ten}`.toLowerCase().includes(tuKhoa.value.toLowerCase()))
  )
)

function format(n) { return Number(n || 0).toLocaleString('vi-VN') }
function date(x) { return new Date(x).toLocaleDateString('vi-VN') }
function nhanHang(x) { return { ALL: 'Tất cả', NEW_USER: 'Khách mới', SILVER: 'Silver', GOLD: 'Gold', DIAMOND: 'Diamond' }[x] || x }
function nhanTrangThai(x) { return { HOAT_DONG: 'Hoạt động', TAM_DUNG: 'Tạm dừng', HET_HAN: 'Hết hạn' }[x] || x }
function badgeClass(x) { return { HOAT_DONG: 'badge-active', TAM_DUNG: 'badge-paused', HET_HAN: 'badge-expired' }[x] || 'badge-paused' }
function usagePercent(v) { if (!v.soLuong) return 0; return Math.min(100, Math.round((v.daSuDung / v.soLuong) * 100)) }

async function tai() { try { ds.value = await voucherService.all() } catch (e) { loi.value = e.message } }
function moThem() { dangSua.value = false; idSua.value = null; form.value = empty(); loi.value = ''; hienForm.value = true }
function moSua(v) {
  dangSua.value = true; idSua.value = v.id
  form.value = { ...v, ngayBatDau: toLocal(new Date(v.ngayBatDau)), ngayKetThuc: toLocal(new Date(v.ngayKetThuc)) }
  loi.value = ''; hienForm.value = true
}

async function luu() {
  try {
    loi.value = ''
    if (dangSua.value) await voucherService.edit(idSua.value, form.value)
    else await voucherService.add(form.value)
    hienForm.value = false
    await tai()
  } catch (e) { loi.value = e.message }
}

async function xoa(v) {
  if (!confirm(`Xóa voucher ${v.ma}?`)) return
  try { await voucherService.remove(v.id); await tai() } catch (e) { alert(e.message) }
}

onMounted(tai)
</script>

<style scoped>
/* ============================================================
   TOKENS — đồng bộ với trang chủ & các trang quản lý khác
   ============================================================ */
.qlvc {
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
  white-space: nowrap;
}
.btn-primary:hover { transform: translateY(-2px); box-shadow: 0 14px 28px -8px rgba(182,255,60,.35); }

.btn-ghost {
  display: inline-flex; align-items: center; gap: 6px; padding: 10px 20px;
  background: rgba(247,251,244,.06); color: var(--chalk-050); border: 1.5px solid rgba(247,251,244,.2);
  border-radius: 999px; font-weight: 600; font-size: 13.5px; cursor: pointer; transition: all .15s ease;
}
.btn-ghost:hover { border-color: var(--lime-400); color: var(--lime-300); background: rgba(182,255,60,.08); }

/* ============================================================
   FILTER CARD
   ============================================================ */
.filter-card {
  background: rgba(247,251,244,.03); border: 1px solid rgba(247,251,244,.1); border-radius: 18px;
  padding: 18px 20px; margin-bottom: 22px; backdrop-filter: blur(6px);
}
.filter-top { display: flex; flex-wrap: wrap; gap: 12px; align-items: flex-end; }

.search-wrap {
  flex: 1.6; min-width: 220px; display: flex; align-items: center; gap: 8px;
  background: rgba(5,11,8,.4); border: 1px solid rgba(247,251,244,.15); border-radius: 10px; padding: 0 12px;
}
.search-wrap svg { color: var(--chalk-200); opacity: .6; flex-shrink: 0; }
.search-input { flex: 1; border: none; background: transparent; outline: none; padding: 10px 0; font-size: 13.5px; color: var(--chalk-050); font-family: var(--font-body); }
.search-input::placeholder { color: var(--chalk-200); opacity: .45; }

.pill-select { display: flex; flex-direction: column; gap: 5px; min-width: 180px; }
.pill-select label { font-family: var(--font-mono); font-size: 10.5px; text-transform: uppercase; letter-spacing: .06em; color: var(--lime-300); padding-left: 2px; }

.form-control {
  padding: 10px 13px; border: 1px solid rgba(247,251,244,.15); border-radius: 10px; font-size: 13.5px;
  outline: none; background: rgba(5,11,8,.4); color: var(--chalk-050); font-family: var(--font-body);
  transition: border-color .15s; width: 100%; box-sizing: border-box;
}
.form-control:focus { border-color: var(--lime-400); box-shadow: 0 0 0 3px rgba(182,255,60,.12); }
select.form-control { cursor: pointer; }
select.form-control option { background: var(--night-800); color: var(--chalk-050); }
textarea.form-control { resize: vertical; font-family: var(--font-body); }

/* ============================================================
   TABLE
   ============================================================ */
.table-card { background: rgba(247,251,244,.03); border: 1px solid rgba(247,251,244,.1); border-radius: 18px; overflow: hidden; }
.table-responsive { overflow-x: auto; }
.data-table { width: 100%; border-collapse: collapse; font-size: 13.5px; }
.data-table th {
  background: rgba(247,251,244,.04); color: var(--lime-300); font-family: var(--font-mono); font-weight: 600;
  font-size: 11px; text-transform: uppercase; letter-spacing: .05em; padding: 14px 16px; text-align: left;
  border-bottom: 1px solid rgba(247,251,244,.1); white-space: nowrap;
}
.data-table td { padding: 13px 16px; border-bottom: 1px solid rgba(247,251,244,.06); vertical-align: middle; white-space: nowrap; }
.data-table tbody tr:hover { background: rgba(182,255,60,.04); }
.bold { font-weight: 600; color: var(--chalk-050); }
.sub { font-size: 12.5px; color: var(--chalk-200); opacity: .75; }
.ma-don { color: var(--lime-300); font-size: 12.5px; font-family: var(--font-mono); }
.price { font-weight: 700; color: var(--chalk-050); }
.tag-pill { padding: 4px 12px; background: rgba(182,255,60,.1); color: var(--lime-300); border: 1px solid rgba(182,255,60,.25); border-radius: 999px; font-size: 11.5px; font-weight: 700; }

.usage { display: flex; flex-direction: column; gap: 5px; min-width: 100px; }
.usage__text { font-size: 12px; color: var(--chalk-200); font-family: var(--font-mono); }
.usage__bar { width: 100%; height: 5px; border-radius: 999px; background: rgba(247,251,244,.1); overflow: hidden; }
.usage__fill { height: 100%; background: linear-gradient(90deg, var(--turf-500), var(--lime-400)); border-radius: 999px; }

/* BADGES — mỗi trạng thái một màu riêng */
.badge { display: inline-flex; align-items: center; gap: 6px; padding: 5px 12px; border-radius: 20px; font-size: 11.5px; font-weight: 700; }
.badge__dot { width: 6px; height: 6px; border-radius: 50%; flex-shrink: 0; }
.badge-active { background: rgba(182,255,60,.15); color: var(--lime-300); }
.badge-active .badge__dot { background: var(--lime-400); }
.badge-paused { background: rgba(255,176,32,.15); color: var(--amber-400); }
.badge-paused .badge__dot { background: var(--amber-400); }
.badge-expired { background: rgba(255,71,87,.14); color: #ff9686; }
.badge-expired .badge__dot { background: var(--crimson-500); }

/* ACTIONS */
.actions { display: flex; gap: 6px; justify-content: center; }
.btn-icon {
  width: 30px; height: 30px; border-radius: 8px; border: 1px solid rgba(247,251,244,.14);
  background: rgba(247,251,244,.03); display: inline-flex; align-items: center; justify-content: center;
  cursor: pointer; color: var(--chalk-200); transition: all .15s;
}
.btn-edit:hover { color: var(--lime-300); border-color: var(--lime-400); background: rgba(182,255,60,.08); }
.btn-delete:hover { color: #ff9686; border-color: var(--crimson-500); background: rgba(255,71,87,.1); }

.no-data { text-align: center; padding: 50px 20px; color: var(--chalk-200); opacity: .6; white-space: normal; }
.no-data__icon { font-size: 26px; margin-bottom: 8px; }

/* ============================================================
   MODAL
   ============================================================ */
.overlay { position: fixed; inset: 0; background: rgba(5,11,8,.75); backdrop-filter: blur(3px); display: flex; align-items: center; justify-content: center; z-index: 200; padding: 20px; }
.modal-box { background: var(--night-800); border: 1px solid rgba(182,255,60,.2); border-radius: 18px; width: 100%; max-width: 520px; box-shadow: 0 24px 50px rgba(0,0,0,.5); overflow: hidden; max-height: 90vh; display: flex; flex-direction: column; }
.modal-box.wide { max-width: 760px; }
.modal-header { padding: 18px 24px; border-bottom: 1px solid rgba(247,251,244,.1); display: flex; justify-content: space-between; align-items: center; flex-shrink: 0; }
.modal-header h2 { font-family: var(--font-display); font-size: 17px; font-weight: 600; color: var(--chalk-050); margin: 0; }
.close-btn { background: none; border: none; font-size: 22px; color: var(--chalk-200); opacity: .6; cursor: pointer; }
.close-btn:hover { opacity: 1; color: var(--lime-300); }
.modal-body { padding: 20px 24px; overflow-y: auto; }
.modal-footer { padding: 16px 24px; border-top: 1px solid rgba(247,251,244,.1); display: flex; justify-content: flex-end; gap: 10px; flex-shrink: 0; }

.grid { display: grid; grid-template-columns: 1fr 1fr; gap: 14px; }
.field { display: flex; flex-direction: column; gap: 6px; }
.field.full { margin-top: 16px; }
.form-label { font-size: 12px; font-weight: 600; color: var(--lime-300); font-family: var(--font-mono); text-transform: uppercase; letter-spacing: .04em; }

.thong-bao-loi {
  color: #ff9686; background: rgba(255,71,87,.1); border: 1px solid rgba(255,71,87,.3);
  border-radius: 10px; padding: 10px 14px; margin-top: 14px; font-size: 13px;
}

/* ============================================================
   RESPONSIVE
   ============================================================ */
@media (max-width: 720px) {
  .filter-top { flex-direction: column; align-items: stretch; }
  .pill-select { min-width: 0; }
  .grid { grid-template-columns: 1fr; }
}
</style>