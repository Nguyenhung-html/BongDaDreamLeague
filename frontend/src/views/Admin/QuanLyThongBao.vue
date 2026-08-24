<template>
  <div class="qltb">

    <!-- ===== HEADER ===== -->
    <div class="page-header">
      <div>
        <span class="eyebrow">⚽ Bảng điều khiển · Admin</span>
        <h1 class="page-title">Quản lý thông báo</h1>
        <p class="page-desc">Tạo và quản lý thông báo gửi tới nhân viên và người dùng.</p>
      </div>
      <button class="btn-primary" @click="openCreateModal">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 5v14M5 12h14"/></svg>
        Tạo thông báo mới
      </button>
    </div>

    <div v-if="loading" class="state-box"><span class="spinner"></span> Đang tải dữ liệu...</div>

    <template v-else>

      <!-- ===== BẢNG DÀNH CHO NHÂN VIÊN ===== -->
      <section class="tb-section">
        <div class="section-head">
          <span class="eyebrow">👷 Nội bộ</span>
          <h2 class="section-title">Thông báo dành cho Nhân viên</h2>
        </div>
        <div class="table-card">
          <div class="table-responsive">
            <table class="data-table">
              <thead>
                <tr>
                  <th>Tiêu đề</th>
                  <th>Đối tượng</th>
                  <th>Ngày bắt đầu</th>
                  <th>Ngày kết thúc</th>
                  <th>Trạng thái</th>
                  <th style="text-align:center" width="280">Thao tác</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="staffList.length === 0">
                  <td colspan="6" class="no-data">
                    <div class="no-data__icon">🔔</div>
                    Chưa có thông báo nào cho nhân viên.
                  </td>
                </tr>
                <tr v-for="tb in staffList" :key="tb.id">
                  <td><span class="bold">{{ tb.tieuDe }}</span></td>
                  <td><span :class="['tag-pill', targetClass(tb.doiTuongNhan)]">{{ nhanDoiTuong(tb.doiTuongNhan) }}</span></td>
                  <td><span class="sub">{{ formatDate(tb.ngayBatDau) }}</span></td>
                  <td><span class="sub">{{ formatDate(tb.ngayKetThuc) }}</span></td>
                  <td>
                    <span class="badge badge-active" v-if="tb.hienThi"><span class="badge__dot"></span>Hiển thị</span>
                    <span class="badge badge-lock" v-else><span class="badge__dot"></span>Đã ẩn</span>
                  </td>
                  <td>
                    <div class="actions">
                      <button class="btn-act edit" @click="openEditModal(tb)">✏️ Sửa</button>
                      <button v-if="tb.hienThi" class="btn-act hide" @click="toggleHienThi(tb)">🙈 Ẩn</button>
                      <button v-else class="btn-act show" @click="toggleHienThi(tb)">👁️ Hiện</button>
                      <button class="btn-act remove" @click="xoaThongBao(tb)">🗑️ Xóa</button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </section>

      <!-- ===== BẢNG DÀNH CHO NGƯỜI DÙNG ===== -->
      <section class="tb-section">
        <div class="section-head">
          <span class="eyebrow">🧑‍🤝‍🧑 Khách hàng</span>
          <h2 class="section-title">Thông báo dành cho Người dùng</h2>
        </div>
        <div class="table-card">
          <div class="table-responsive">
            <table class="data-table">
              <thead>
                <tr>
                  <th>Tiêu đề</th>
                  <th>Đối tượng</th>
                  <th>Ngày bắt đầu</th>
                  <th>Ngày kết thúc</th>
                  <th>Trạng thái</th>
                  <th style="text-align:center" width="280">Thao tác</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="userList.length === 0">
                  <td colspan="6" class="no-data">
                    <div class="no-data__icon">🔔</div>
                    Chưa có thông báo nào cho người dùng.
                  </td>
                </tr>
                <tr v-for="tb in userList" :key="tb.id">
                  <td><span class="bold">{{ tb.tieuDe }}</span></td>
                  <td><span :class="['tag-pill', targetClass(tb.doiTuongNhan)]">{{ nhanDoiTuong(tb.doiTuongNhan) }}</span></td>
                  <td><span class="sub">{{ formatDate(tb.ngayBatDau) }}</span></td>
                  <td><span class="sub">{{ formatDate(tb.ngayKetThuc) }}</span></td>
                  <td>
                    <span class="badge badge-active" v-if="tb.hienThi"><span class="badge__dot"></span>Hiển thị</span>
                    <span class="badge badge-lock" v-else><span class="badge__dot"></span>Đã ẩn</span>
                  </td>
                  <td>
                    <div class="actions">
                      <button class="btn-act edit" @click="openEditModal(tb)">✏️ Sửa</button>
                      <button v-if="tb.hienThi" class="btn-act hide" @click="toggleHienThi(tb)">🙈 Ẩn</button>
                      <button v-else class="btn-act show" @click="toggleHienThi(tb)">👁️ Hiện</button>
                      <button class="btn-act remove" @click="xoaThongBao(tb)">🗑️ Xóa</button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </section>

    </template>

    <!-- ===== MODAL TẠO / SỬA ===== -->
    <div v-if="showModal" class="overlay" @click.self="showModal = false">
      <div class="modal-box">
        <div class="modal-header">
          <h2>{{ isEdit ? '✏️ Cập nhật thông báo' : '🔔 Tạo thông báo mới' }}</h2>
          <button class="close-btn" @click="showModal = false">&times;</button>
        </div>

        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">Tiêu đề</label>
            <input v-model="form.tieuDe" class="form-control" placeholder="Nhập tiêu đề thông báo"/>
          </div>

          <div class="form-group">
            <label class="form-label">Nội dung</label>
            <textarea v-model="form.noiDung" class="form-control" rows="4" placeholder="Nhập nội dung thông báo"></textarea>
          </div>

          <div class="form-group">
            <label class="form-label">Đối tượng nhận</label>
            <select v-model="form.doiTuongNhan" class="form-control">
              <option value="TAT_CA">Tất cả</option>
              <option value="USER">Người dùng</option>
              <option value="STAFF">Nhân viên</option>
            </select>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label class="form-label">Ngày bắt đầu</label>
              <input type="date" v-model="form.ngayBatDau" class="form-control"/>
            </div>
            <div class="form-group">
              <label class="form-label">Ngày kết thúc</label>
              <input type="date" v-model="form.ngayKetThuc" class="form-control"/>
            </div>
          </div>

          <label class="checkbox-row" v-if="!isEdit">
            <input type="checkbox" v-model="form.hienThi"/>
            <span>Hiển thị ngay sau khi tạo</span>
          </label>
        </div>

        <div class="modal-footer">
          <button class="btn-ghost" @click="showModal = false">Hủy</button>
          <button class="btn-primary" @click="saveThongBao">{{ isEdit ? 'Cập nhật' : 'Tạo mới' }}</button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import axios from "axios"
import { ref, computed, onMounted } from "vue"

const API_BASE = "/api/admin/thong-bao"

const loading = ref(false)
const thongBaoList = ref([])

const showModal = ref(false)
const isEdit = ref(false)

const form = ref({
    id: null,
    tieuDe: '',
    noiDung: '',
    doiTuongNhan: 'TAT_CA',
    ngayBatDau: '',
    ngayKetThuc: '',
    hienThi: true
})

// "Tất cả" xuất hiện ở cả 2 bảng vì nó dành cho mọi người
const staffList = computed(() =>
    thongBaoList.value.filter(tb => tb.doiTuongNhan === 'STAFF' || tb.doiTuongNhan === 'TAT_CA')
)

const userList = computed(() =>
    thongBaoList.value.filter(tb => tb.doiTuongNhan === 'USER' || tb.doiTuongNhan === 'TAT_CA')
)

function authHeader() {
    return { Authorization: `Bearer ${localStorage.getItem("token")}` }
}

onMounted(loadThongBao)

async function loadThongBao() {
    loading.value = true
    try {
        const res = await axios.get(API_BASE, { headers: authHeader() })
        thongBaoList.value = res.data
    } catch (e) {
        console.log(e)
        alert("Không tải được danh sách thông báo")
    } finally {
        loading.value = false
    }
}

function resetForm() {
    form.value = {
        id: null,
        tieuDe: '',
        noiDung: '',
        doiTuongNhan: 'TAT_CA',
        ngayBatDau: '',
        ngayKetThuc: '',
        hienThi: true
    }
}

function openCreateModal() {
    isEdit.value = false
    resetForm()
    showModal.value = true
}

function openEditModal(item) {
    isEdit.value = true
    form.value = {
        id: item.id,
        tieuDe: item.tieuDe,
        noiDung: item.noiDung,
        doiTuongNhan: item.doiTuongNhan,
        ngayBatDau: item.ngayBatDau || '',
        ngayKetThuc: item.ngayKetThuc || '',
        hienThi: item.hienThi
    }
    showModal.value = true
}

async function saveThongBao() {
    if (!form.value.tieuDe.trim()) {
        alert("Vui lòng nhập tiêu đề")
        return
    }

    try {
        if (isEdit.value) {
            await axios.put(
                `${API_BASE}/${form.value.id}`,
                {
                    tieuDe: form.value.tieuDe,
                    noiDung: form.value.noiDung,
                    doiTuongNhan: form.value.doiTuongNhan,
                    ngayBatDau: form.value.ngayBatDau || null,
                    ngayKetThuc: form.value.ngayKetThuc || null
                },
                { headers: authHeader() }
            )
        } else {
            await axios.post(
                API_BASE,
                {
                    tieuDe: form.value.tieuDe,
                    noiDung: form.value.noiDung,
                    doiTuongNhan: form.value.doiTuongNhan,
                    ngayBatDau: form.value.ngayBatDau || null,
                    ngayKetThuc: form.value.ngayKetThuc || null,
                    hienThi: form.value.hienThi
                },
                { headers: authHeader() }
            )
        }

        await loadThongBao()
        showModal.value = false
        alert(isEdit.value ? "Cập nhật thành công" : "Tạo thông báo thành công")
    } catch (e) {
        console.log(e)
        alert(e.response?.data?.message || "Thao tác thất bại")
    }
}

async function toggleHienThi(item) {
    const action = item.hienThi ? "an" : "hien"
    try {
        await axios.put(`${API_BASE}/${item.id}/${action}`, {}, { headers: authHeader() })
        await loadThongBao()
    } catch (e) {
        console.log(e)
        alert(e.response?.data?.message || "Thao tác thất bại")
    }
}

async function xoaThongBao(item) {
    if (!confirm(`Xóa thông báo "${item.tieuDe}"? Hành động này không thể hoàn tác.`)) return
    try {
        await axios.delete(`${API_BASE}/${item.id}`, { headers: authHeader() })
        await loadThongBao()
    } catch (e) {
        console.log(e)
        alert(e.response?.data?.message || "Xóa thất bại")
    }
}

function nhanDoiTuong(gt) {
    if (gt === 'STAFF') return 'Nhân viên'
    if (gt === 'USER') return 'Người dùng'
    return 'Tất cả'
}

// Mỗi đối tượng nhận một màu riêng biệt
function targetClass(gt) {
    if (gt === 'STAFF') return 'tag-staff'
    if (gt === 'USER') return 'tag-user'
    return 'tag-all'
}

function formatDate(dateStr) {
    if (!dateStr) return '—'
    const d = new Date(dateStr)
    if (isNaN(d)) return dateStr
    return d.toLocaleDateString('vi-VN')
}
</script>

<style scoped>
/* ============================================================
   TOKENS — đồng bộ với trang chủ & các trang quản lý khác
   ============================================================ */
.qltb {
  --night-950: #050b08;
  --night-800: #0a1f13;
  --night-700: #123321;
  --turf-500: #23935a;
  --turf-700: #146239;
  --lime-400: #b6ff3c;
  --lime-300: #d3ff8f;
  --amber-400: #ffb020;
  --crimson-500: #ff4757;
  --violet-400: #a78bfa;
  --info-400: #6fa8ff;
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
.page-header { display: flex; justify-content: space-between; align-items: flex-end; gap: 16px; margin-bottom: 26px; flex-wrap: wrap; }
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
   STATE BOX
   ============================================================ */
.state-box { text-align: center; padding: 60px 20px; color: var(--chalk-200); opacity: .75; font-size: 14.5px; display: flex; align-items: center; justify-content: center; gap: 10px; }
.spinner { width: 16px; height: 16px; border-radius: 50%; border: 2px solid rgba(182,255,60,.25); border-top-color: var(--lime-400); animation: spin .7s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }

/* ============================================================
   SECTION
   ============================================================ */
.tb-section { margin-bottom: 32px; }
.section-head { margin-bottom: 12px; }
.section-title { font-family: var(--font-display); font-size: 17px; font-weight: 600; margin: 6px 0 0; color: var(--chalk-050); }

/* ============================================================
   TABLE
   ============================================================ */
.table-card { background: rgba(247,251,244,.03); border: 1px solid rgba(247,251,244,.1); border-radius: 18px; overflow: hidden; }
.table-responsive { overflow-x: auto; }
.data-table { width: 100%; border-collapse: collapse; font-size: 13.5px; }
.data-table th {
  background: rgba(247,251,244,.04); color: var(--lime-300); font-family: var(--font-mono); font-weight: 600;
  font-size: 11px; text-transform: uppercase; letter-spacing: .05em; padding: 14px 16px; text-align: left;
  border-bottom: 1px solid rgba(247,251,244,.1);
}
.data-table td { padding: 13px 16px; border-bottom: 1px solid rgba(247,251,244,.06); vertical-align: middle; }
.data-table tbody tr:hover { background: rgba(182,255,60,.04); }
.bold { font-weight: 600; color: var(--chalk-050); }
.sub { font-size: 12.5px; color: var(--chalk-200); opacity: .75; }

/* Đối tượng nhận — mỗi loại một màu */
.tag-pill { padding: 4px 12px; border-radius: 999px; font-size: 11.5px; font-weight: 700; }
.tag-all { background: rgba(182,255,60,.12); color: var(--lime-300); }
.tag-staff { background: rgba(255,176,32,.15); color: var(--amber-400); }
.tag-user { background: rgba(111,168,255,.15); color: var(--info-400); }

/* Trạng thái hiển thị */
.badge { display: inline-flex; align-items: center; gap: 6px; padding: 5px 12px; border-radius: 20px; font-size: 11.5px; font-weight: 700; }
.badge__dot { width: 6px; height: 6px; border-radius: 50%; flex-shrink: 0; }
.badge-active { background: rgba(182,255,60,.15); color: var(--lime-300); }
.badge-active .badge__dot { background: var(--lime-400); }
.badge-lock { background: rgba(255,71,87,.14); color: #ff9686; }
.badge-lock .badge__dot { background: var(--crimson-500); }

/* ACTIONS */
.actions { display: flex; gap: 6px; justify-content: center; flex-wrap: wrap; }
.btn-act { padding: 6px 11px; border-radius: 7px; border: none; font-size: 11.5px; font-weight: 700; cursor: pointer; transition: .15s; white-space: nowrap; }
.btn-act.edit { background: rgba(111,168,255,.15); color: var(--info-400); }
.btn-act.edit:hover { background: rgba(111,168,255,.26); }
.btn-act.hide { background: rgba(255,176,32,.15); color: var(--amber-400); }
.btn-act.hide:hover { background: rgba(255,176,32,.26); }
.btn-act.show { background: rgba(182,255,60,.12); color: var(--lime-300); }
.btn-act.show:hover { background: rgba(182,255,60,.2); }
.btn-act.remove { background: rgba(255,71,87,.14); color: #ff9686; }
.btn-act.remove:hover { background: rgba(255,71,87,.24); }

.no-data { text-align: center; padding: 40px 20px; color: var(--chalk-200); opacity: .6; }
.no-data__icon { font-size: 24px; margin-bottom: 8px; }

/* ============================================================
   MODAL
   ============================================================ */
.overlay { position: fixed; inset: 0; background: rgba(5,11,8,.75); backdrop-filter: blur(3px); display: flex; align-items: center; justify-content: center; z-index: 200; padding: 20px; }
.modal-box { background: var(--night-800); border: 1px solid rgba(182,255,60,.2); border-radius: 18px; width: 100%; max-width: 480px; box-shadow: 0 24px 50px rgba(0,0,0,.5); overflow: hidden; max-height: 90vh; display: flex; flex-direction: column; }
.modal-header { padding: 18px 24px; border-bottom: 1px solid rgba(247,251,244,.1); display: flex; justify-content: space-between; align-items: center; flex-shrink: 0; }
.modal-header h2 { font-family: var(--font-display); font-size: 17px; font-weight: 600; color: var(--chalk-050); margin: 0; }
.close-btn { background: none; border: none; font-size: 22px; color: var(--chalk-200); opacity: .6; cursor: pointer; }
.close-btn:hover { opacity: 1; color: var(--lime-300); }
.modal-body { padding: 20px 24px; overflow-y: auto; }
.modal-footer { padding: 16px 24px; border-top: 1px solid rgba(247,251,244,.1); display: flex; justify-content: flex-end; gap: 10px; flex-shrink: 0; }

.form-group { margin-bottom: 16px; display: flex; flex-direction: column; gap: 6px; }
.form-row { display: flex; gap: 14px; }
.form-row .form-group { flex: 1; }
.form-label { font-size: 12.5px; font-weight: 600; color: var(--lime-300); font-family: var(--font-mono); text-transform: uppercase; letter-spacing: .04em; }

.form-control {
  padding: 10px 13px; border: 1px solid rgba(247,251,244,.15); border-radius: 10px; font-size: 13.5px;
  outline: none; background: rgba(5,11,8,.4); color: var(--chalk-050); font-family: var(--font-body);
  transition: border-color .15s; width: 100%; box-sizing: border-box;
}
.form-control:focus { border-color: var(--lime-400); box-shadow: 0 0 0 3px rgba(182,255,60,.12); }
select.form-control { cursor: pointer; }
select.form-control option { background: var(--night-800); color: var(--chalk-050); }
textarea.form-control { resize: vertical; font-family: var(--font-body); }

.checkbox-row { display: flex; align-items: center; gap: 8px; font-size: 13px; color: var(--chalk-200); cursor: pointer; }
.checkbox-row input { width: 16px; height: 16px; accent-color: var(--lime-400); cursor: pointer; }

/* ============================================================
   RESPONSIVE
   ============================================================ */
@media (max-width: 768px) {
  .page-header { flex-direction: column; align-items: stretch; }
  .form-row { flex-direction: column; gap: 0; }
  .data-table { font-size: 12.5px; }
}
</style>