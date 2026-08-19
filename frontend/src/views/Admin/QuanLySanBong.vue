<template>
  <div class="page">

    <!-- ===================== HEADER ===================== -->
    <header class="pitch-header">
      <svg class="pitch-header__lines" viewBox="0 0 520 220" preserveAspectRatio="none" aria-hidden="true">
        <line x1="392" y1="-20" x2="392" y2="240" stroke="rgba(255,255,255,0.14)" stroke-width="1.5"/>
        <circle cx="392" cy="110" r="58" fill="none" stroke="rgba(255,255,255,0.14)" stroke-width="1.5"/>
        <circle cx="392" cy="110" r="3" fill="rgba(255,255,255,0.22)"/>
        <path d="M 392 40 A 90 90 0 0 1 392 180" fill="none" stroke="rgba(255,255,255,0.08)" stroke-width="1.5"/>
      </svg>
      <div class="pitch-header__content">
        <p class="pitch-header__eyebrow">Bảng điều khiển · Quản trị sân bãi</p>
        <h1 class="pitch-header__title">Quản lý sân bóng</h1>
        <p class="pitch-header__desc">Theo dõi, thêm mới và cập nhật toàn bộ sân bóng trong hệ thống.</p>
      </div>

      <!-- ===================== SCOREBOARD ===================== -->
      <div class="scoreboard">
        <div class="scoreboard__panel">
          <span class="scoreboard__label">Tổng số sân</span>
          <span class="scoreboard__value">{{ String(stats.total).padStart(2, '0') }}</span>
        </div>
        <div class="scoreboard__panel scoreboard__panel--active">
          <span class="scoreboard__label"><i class="scoreboard__dot"></i>Hoạt động</span>
          <span class="scoreboard__value">{{ String(stats.active).padStart(2, '0') }}</span>
        </div>
        <div class="scoreboard__panel scoreboard__panel--maint">
          <span class="scoreboard__label"><i class="scoreboard__dot"></i>Bảo trì</span>
          <span class="scoreboard__value">{{ String(stats.maintenance).padStart(2, '0') }}</span>
        </div>
        <div class="scoreboard__panel scoreboard__panel--closed">
          <span class="scoreboard__label"><i class="scoreboard__dot"></i>Đóng cửa</span>
          <span class="scoreboard__value">{{ String(stats.closed).padStart(2, '0') }}</span>
        </div>
      </div>
    </header>

    <!-- ===================== TOOLBAR ===================== -->
    <section class="toolbar">
      <div class="toolbar__filters">
        <div class="search-box">
          <svg class="search-box__icon" width="16" height="16" viewBox="0 0 24 24" fill="none">
            <circle cx="11" cy="11" r="7" stroke="currentColor" stroke-width="2"/>
            <path d="M21 21L16.65 16.65" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
          <input v-model="keyword" placeholder="Tìm tên sân hoặc địa chỉ..." />
        </div>

        <select v-model="statusFilter" class="filter-select">
          <option value="ALL">Tất cả trạng thái</option>
          <option value="HOAT_DONG">Hoạt động</option>
          <option value="BAO_TRI">Bảo trì</option>
          <option value="DONG_CUA">Đóng cửa</option>
        </select>

        <select v-model="typeFilter" class="filter-select">
          <option value="ALL">Tất cả loại sân</option>
          <option :value="5">Sân 5</option>
          <option :value="7">Sân 7</option>
        </select>
      </div>

      <button class="btn-add" @click="openAdd">
        <span class="btn-add__icon">+</span>
        Thêm sân
      </button>
    </section>

    <!-- ===================== TABLE ===================== -->
    <section class="table-wrapper">

      <div v-if="loading" class="state-block">
        <div class="state-block__spinner"></div>
        <p>Đang tải danh sách sân...</p>
      </div>

      <div v-else-if="filteredFields.length === 0" class="state-block">
        <div class="state-block__jersey"><span class="jersey jersey--empty">?</span></div>
        <p class="state-block__title">Không tìm thấy sân nào</p>
        <p class="state-block__sub">Thử đổi từ khóa tìm kiếm hoặc bộ lọc, hoặc thêm một sân mới.</p>
      </div>

      <table v-else>
        <thead>
          <tr>
            <th>Sân</th>
            <th>Địa chỉ</th>
            <th>Bảng giá</th>
            <th>Đơn đặt</th>
            <th>Trạng thái</th>
            <th class="col-actions">Thao tác</th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="item in filteredFields" :key="item.id">

            <td>
              <div class="field-cell">
                <span class="jersey" :class="`jersey--${item.loaiSan}`">{{ item.loaiSan }}</span>
                <div class="field-cell__text">
                  <span class="field-cell__name">{{ item.tenSan }}</span>
                  <span class="field-cell__type">Sân {{ item.loaiSan }} người</span>
                </div>
              </div>
            </td>

            <td class="cell-muted">{{ item.diaChi }}</td>

            <td class="price-column">
              <div v-if="!item.danhSachGia || item.danhSachGia.length === 0" class="price-empty">
                Chưa có bảng giá
              </div>
              <div
                v-for="gia in item.danhSachGia"
                :key="gia.id"
                class="price-item"
              >
                <div class="time">{{ formatTime(gia.gioBatDau) }} – {{ formatTime(gia.gioKetThuc) }}</div>
                <div class="price">{{ formatMoney(gia.giaTien) }}</div>
              </div>
            </td>

            <td><span class="metric">{{ item.soDonDaDat }}</span></td>

            <td>
              <span class="badge" :class="statusMeta(item.trangThai).class">
                <i class="badge__dot"></i>
                {{ statusMeta(item.trangThai).label }}
              </span>
            </td>

            <td class="col-actions">
              <button class="icon-btn icon-btn--edit" title="Sửa" @click="editField(item)">
                <svg width="15" height="15" viewBox="0 0 24 24" fill="none">
                  <path d="M12 20h9" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                  <path d="M16.5 3.5a2.12 2.12 0 013 3L7 19l-4 1 1-4L16.5 3.5z" stroke="currentColor" stroke-width="2" stroke-linejoin="round"/>
                </svg>
              </button>
              <button class="icon-btn icon-btn--delete" title="Xóa" @click="deleteField(item)">
                <svg width="15" height="15" viewBox="0 0 24 24" fill="none">
                  <path d="M3 6h18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                  <path d="M8 6V4a2 2 0 012-2h4a2 2 0 012 2v2m3 0-1 14a2 2 0 01-2 2H8a2 2 0 01-2-2L5 6" stroke="currentColor" stroke-width="2" stroke-linejoin="round"/>
                </svg>
              </button>
            </td>

          </tr>
        </tbody>
      </table>

    </section>

    <!-- ===================== MODAL ===================== -->
    <div class="modal-overlay" v-if="showModal" @click.self="showModal = false">
      <div class="modal">

        <div class="modal__header">
          <p class="modal__eyebrow">{{ isEdit ? "Cập nhật" : "Sân mới" }}</p>
          <h2>{{ isEdit ? "Cập nhật sân bóng" : "Thêm sân bóng" }}</h2>
          <button class="modal__close" @click="showModal = false">✕</button>
        </div>

        <div class="modal__body">

          <label class="field-label">Tên sân</label>
          <input v-model="form.tenSan" placeholder="Ví dụ: Sân Thống Nhất" />

          <label class="field-label">Loại sân</label>
          <div class="jersey-picker">
            <button
              type="button"
              class="jersey-picker__option"
              :class="{ 'jersey-picker__option--active': form.loaiSan === 5 }"
              @click="form.loaiSan = 5"
            >
              <span class="jersey jersey--5">5</span> Sân 5
            </button>
            <button
              type="button"
              class="jersey-picker__option"
              :class="{ 'jersey-picker__option--active': form.loaiSan === 7 }"
              @click="form.loaiSan = 7"
            >
              <span class="jersey jersey--7">7</span> Sân 7
            </button>
          </div>

          <label class="field-label">Địa chỉ</label>
          <input v-model="form.diaChi" placeholder="Số nhà, đường, quận..." />

          <label class="field-label">Mô tả</label>
          <textarea v-model="form.moTa" rows="3" placeholder="Ghi chú thêm về sân..."></textarea>

          <hr class="divider">

          <label class="field-label">Bảng giá theo khung giờ</label>

          <div
            class="price-row"
            v-for="(gia, index) in form.danhSachGia"
            :key="gia.id ?? `moi-${index}`"
          >
            <input type="time" v-model="gia.gioBatDau" />
            <span class="price-row__sep">–</span>
            <input type="time" v-model="gia.gioKetThuc" />
            <input
              type="number"
              v-model.number="gia.giaTien"
              placeholder="Giá (VNĐ)"
              min="0"
            />
            <button type="button" class="btn-remove" @click="removePrice(index)">✕</button>
          </div>

          <p v-if="form.danhSachGia.length === 0" class="price-hint">
            Chưa có khung giờ nào. Nhấn "Thêm khung giờ" để thêm bảng giá cho sân.
          </p>

          <button type="button" class="btn-add-price" @click="addPrice">
            + Thêm khung giờ
          </button>

          <label class="field-label">Trạng thái</label>
          <div class="status-picker">
            <button
              type="button"
              class="status-chip status-chip--active"
              :class="{ 'status-chip--selected': form.trangThai === 'HOAT_DONG' }"
              @click="form.trangThai = 'HOAT_DONG'"
            >Hoạt động</button>
            <button
              type="button"
              class="status-chip status-chip--maint"
              :class="{ 'status-chip--selected': form.trangThai === 'BAO_TRI' }"
              @click="form.trangThai = 'BAO_TRI'"
            >Bảo trì</button>
            <button
              type="button"
              class="status-chip status-chip--closed"
              :class="{ 'status-chip--selected': form.trangThai === 'DONG_CUA' }"
              @click="form.trangThai = 'DONG_CUA'"
            >Đóng cửa</button>
          </div>

        </div>

        <div class="modal__footer">
          <button class="btn-close" @click="showModal = false">Hủy</button>
          <button class="btn-save" @click="saveField">
            {{ isEdit ? "Lưu thay đổi" : "Thêm sân" }}
          </button>
        </div>

      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue"
import axios from "axios"

const API = "http://localhost:8080/api/admin/san-bong"
const token = localStorage.getItem("token")

const config = {
  headers: { Authorization: `Bearer ${token}` }
}

const keyword = ref("")
const statusFilter = ref("ALL")
const typeFilter = ref("ALL")

const showModal = ref(false)
const isEdit = ref(false)
const loading = ref(false)

const fields = ref([])

// Danh sách id khung giờ bị xoá khỏi form khi đang sửa, sẽ gọi API xoá lúc lưu
const deletedGiaIds = ref([])

function ganLaiForm() {
  form.value = {
    id: null,
    tenSan: "",
    loaiSan: 5,
    diaChi: "",
    moTa: "",
    trangThai: "HOAT_DONG",
    danhSachGia: []
  }
  deletedGiaIds.value = []
}

const form = ref({
  id: null,
  tenSan: "",
  loaiSan: 5,
  diaChi: "",
  moTa: "",
  trangThai: "HOAT_DONG",
  danhSachGia: []
})

// =========================
// Lấy danh sách sân
// =========================
const loadFields = async () => {
  loading.value = true
  try {
    const res = await axios.get(API, config)
    fields.value = res.data
  } catch (err) {
    console.error(err)
    alert(err.response?.data?.message || "Không tải được danh sách sân.")
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadFields()
})

// =========================
// Lọc & tìm kiếm
// =========================
const filteredFields = computed(() => {
  return fields.value.filter(item => {
    const matchKeyword =
      item.tenSan.toLowerCase().includes(keyword.value.toLowerCase()) ||
      item.diaChi?.toLowerCase().includes(keyword.value.toLowerCase())

    const matchStatus =
      statusFilter.value === "ALL" || item.trangThai === statusFilter.value

    const matchType =
      typeFilter.value === "ALL" || item.loaiSan === typeFilter.value

    return matchKeyword && matchStatus && matchType
  })
})

// =========================
// Thống kê nhanh
// =========================
const stats = computed(() => {
  const total = fields.value.length
  const active = fields.value.filter(f => f.trangThai === "HOAT_DONG").length
  const maintenance = fields.value.filter(f => f.trangThai === "BAO_TRI").length
  const closed = fields.value.filter(f => f.trangThai === "DONG_CUA").length
  return { total, active, maintenance, closed }
})

function statusMeta(trangThai) {
  if (trangThai === "HOAT_DONG") return { label: "Hoạt động", class: "badge--active" }
  if (trangThai === "BAO_TRI") return { label: "Bảo trì", class: "badge--maint" }
  return { label: "Đóng cửa", class: "badge--closed" }
}

// =========================
// Định dạng hiển thị
// =========================
function formatMoney(v) {
  if (v === null || v === undefined || v === "") return "—"
  const so = Number(v)
  if (Number.isNaN(so)) return "—"
  return new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(so)
}

function formatTime(t) {
  if (!t) return ""
  // Backend trả về "HH:mm:ss" hoặc "HH:mm" — chỉ lấy giờ:phút để hiển thị
  return t.length >= 5 ? t.slice(0, 5) : t
}

// =========================
// Bảng giá trong form (thêm/sửa)
// =========================
function addPrice() {
  form.value.danhSachGia.push({
    id: null,
    gioBatDau: "",
    gioKetThuc: "",
    giaTien: null
  })
}

function removePrice(index) {
  const item = form.value.danhSachGia[index]
  if (item?.id) {
    deletedGiaIds.value.push(item.id)
  }
  form.value.danhSachGia.splice(index, 1)
}

// =========================
// Thêm
// =========================
function openAdd() {
  isEdit.value = false
  ganLaiForm()
  showModal.value = true
}

// =========================
// Sửa
// =========================
function editField(item) {
  isEdit.value = true
  form.value = {
    id: item.id,
    tenSan: item.tenSan,
    loaiSan: item.loaiSan,
    diaChi: item.diaChi,
    moTa: item.moTa,
    trangThai: item.trangThai,
    danhSachGia: (item.danhSachGia || []).map(g => ({
      id: g.id,
      gioBatDau: formatTime(g.gioBatDau),
      gioKetThuc: formatTime(g.gioKetThuc),
      giaTien: g.giaTien
    }))
  }
  deletedGiaIds.value = []
  showModal.value = true
}

// =========================
// Lưu (sân + bảng giá)
// =========================
async function saveField() {
  if (!form.value.tenSan?.trim()) {
    alert("Vui lòng nhập tên sân.")
    return
  }
  if (!form.value.diaChi?.trim()) {
    alert("Vui lòng nhập địa chỉ sân.")
    return
  }
  for (const gia of form.value.danhSachGia) {
    if (!gia.gioBatDau || !gia.gioKetThuc || gia.giaTien === null || gia.giaTien === "") {
      alert("Vui lòng nhập đầy đủ giờ bắt đầu, giờ kết thúc và giá tiền cho mỗi khung giờ.")
      return
    }
  }

  try {
    const body = {
      tenSan: form.value.tenSan,
      loaiSan: form.value.loaiSan,
      diaChi: form.value.diaChi,
      moTa: form.value.moTa,
      trangThai: form.value.trangThai
    }

    let sanId = form.value.id

    if (isEdit.value) {
      await axios.put(`${API}/${sanId}`, body, config)
    } else {
      const res = await axios.post(API, body, config)
      sanId = res.data.id
    }

    // Xoá các khung giờ đã bị gỡ khỏi form (chỉ áp dụng khi sửa)
    for (const giaId of deletedGiaIds.value) {
      await axios.delete(`${API}/gia/${giaId}`, config)
    }

    // Thêm mới / cập nhật từng khung giờ trong bảng giá
    for (const gia of form.value.danhSachGia) {
      const giaBody = {
        gioBatDau: gia.gioBatDau,
        gioKetThuc: gia.gioKetThuc,
        giaTien: gia.giaTien
      }
      if (gia.id) {
        await axios.put(`${API}/gia/${gia.id}`, giaBody, config)
      } else {
        await axios.post(`${API}/${sanId}/gia`, giaBody, config)
      }
    }

    alert(isEdit.value ? "Cập nhật thành công." : "Thêm sân thành công.")
    showModal.value = false
    loadFields()
  } catch (err) {
    console.error(err)
    alert(err.response?.data?.message || "Có lỗi xảy ra.")
  }
}

// =========================
// Xóa
// =========================
async function deleteField(item) {
  if (!confirm(`Xóa ${item.tenSan}?`)) return

  try {
    await axios.delete(`${API}/${item.id}`, config)
    alert("Đã xóa thành công.")
    loadFields()
  } catch (err) {
    console.error(err)
    alert(err.response?.data?.message || "Không thể xóa sân.")
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Oswald:wght@500;600;700&family=Inter:wght@400;500;600;700&family=JetBrains+Mono:wght@500;600;700&display=swap');

.page {
  /* ---- Token system: stadium at dusk ---- */
  --turf-950: #0a2318;
  --turf-800: #123424;
  --turf-700: #1b4d33;
  --turf-500: #2f8f55;
  --turf-300: #8fd9a8;
  --gold: #f0c674;

  --paper: #f6f4ec;
  --card: #ffffff;
  --ink: #14211a;
  --muted: #6e7b72;
  --line: #e4e1d3;

  --amber: #b9790f;
  --amber-bg: #fbf0dd;
  --coral: #c1425a;
  --coral-bg: #fbe8ec;

  font-family: 'Inter', sans-serif;
  color: var(--ink);
  background: var(--paper);
  padding: 0 0 56px;
  max-width: 1200px;
  margin: 0 auto;
}

/* ===================== HEADER ===================== */
.pitch-header {
  position: relative;
  background: linear-gradient(160deg, var(--turf-950) 0%, var(--turf-800) 62%, var(--turf-700) 100%);
  border-radius: 0 0 24px 24px;
  padding: 36px 36px 0;
  overflow: hidden;
  margin-bottom: 26px;
}

.pitch-header__lines {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
}

.pitch-header__content {
  position: relative;
  padding-bottom: 26px;
}

.pitch-header__eyebrow {
  font-family: 'JetBrains Mono', monospace;
  font-size: 12px;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  color: var(--turf-300);
  margin: 0 0 10px;
}

.pitch-header__title {
  font-family: 'Oswald', sans-serif;
  font-weight: 700;
  font-size: 42px;
  letter-spacing: 0.01em;
  text-transform: uppercase;
  color: #ffffff;
  margin: 0 0 8px;
}

.pitch-header__desc {
  color: #cfe4d5;
  font-size: 14.5px;
  margin: 0;
  max-width: 460px;
}

/* ---- Scoreboard: the signature element ---- */
.scoreboard {
  position: relative;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  background: #071a12;
  border-radius: 14px 14px 0 0;
  border: 1px solid rgba(255, 255, 255, 0.07);
  border-bottom: none;
  overflow: hidden;
}

.scoreboard__panel {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 18px 20px 20px;
  border-right: 1px solid rgba(255, 255, 255, 0.07);
  border-top: 3px solid var(--turf-500);
}

.scoreboard__panel:last-child { border-right: none; }
.scoreboard__panel--active { border-top-color: var(--turf-300); }
.scoreboard__panel--maint { border-top-color: var(--amber); }
.scoreboard__panel--closed { border-top-color: var(--coral); }

.scoreboard__label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-family: 'JetBrains Mono', monospace;
  font-size: 11.5px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #9db3a5;
}

.scoreboard__dot {
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: currentColor;
}

.scoreboard__panel--active .scoreboard__label { color: var(--turf-300); }
.scoreboard__panel--maint .scoreboard__label { color: var(--gold); }
.scoreboard__panel--closed .scoreboard__label { color: #f0a2b1; }

.scoreboard__value {
  font-family: 'JetBrains Mono', monospace;
  font-weight: 700;
  font-size: 34px;
  letter-spacing: 0.03em;
  color: #f4f2e8;
  text-shadow: 0 0 18px rgba(143, 217, 168, 0.25);
}

/* ===================== TOOLBAR ===================== */
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  padding: 0 4px;
  margin-bottom: 18px;
  flex-wrap: wrap;
}

.toolbar__filters {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 280px;
  padding: 9px 14px;
  background: var(--card);
  border: 1px solid var(--line);
  border-radius: 10px;
}

.search-box__icon { color: var(--muted); flex-shrink: 0; }

.search-box input {
  border: none;
  outline: none;
  font-family: 'Inter', sans-serif;
  font-size: 14px;
  width: 100%;
  background: transparent;
  color: var(--ink);
}

.filter-select {
  padding: 9px 12px;
  border: 1px solid var(--line);
  border-radius: 10px;
  background: var(--card);
  font-family: 'Inter', sans-serif;
  font-size: 13.5px;
  color: var(--ink);
  cursor: pointer;
}

.btn-add {
  display: flex;
  align-items: center;
  gap: 6px;
  background: var(--turf-500);
  color: #fff;
  border: none;
  padding: 11px 20px;
  border-radius: 10px;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  box-shadow: 0 4px 14px rgba(27, 77, 51, 0.3);
  transition: transform 0.12s ease, box-shadow 0.12s ease, background 0.12s ease;
}

.btn-add:hover {
  background: var(--turf-700);
  transform: translateY(-1px);
  box-shadow: 0 6px 18px rgba(27, 77, 51, 0.36);
}

.btn-add__icon { font-size: 16px; line-height: 1; }

/* ===================== TABLE ===================== */
.table-wrapper {
  background: var(--card);
  border: 1px solid var(--line);
  border-radius: 16px;
  overflow: hidden;
  margin: 0 4px;
}

table { width: 100%; border-collapse: collapse; }

th, td {
  padding: 15px 18px;
  border-bottom: 1px solid var(--line);
  text-align: left;
}

th {
  background: #eef3ec;
  font-family: 'JetBrains Mono', monospace;
  font-size: 11.5px;
  letter-spacing: 0.07em;
  text-transform: uppercase;
  color: var(--turf-700);
  font-weight: 600;
}

tbody tr { transition: background 0.12s ease; }
tbody tr:hover { background: #fafbf7; }
tbody tr:last-child td { border-bottom: none; }

.col-actions { text-align: right; width: 110px; }

.cell-muted { color: var(--muted); font-size: 13.5px; }

.metric {
  font-family: 'JetBrains Mono', monospace;
  font-weight: 700;
  color: var(--ink);
}

/* Jersey badge for field type (5 vs 7), shaped like a kit */
.field-cell { display: flex; align-items: center; gap: 13px; }

.jersey {
  flex-shrink: 0;
  width: 36px;
  height: 40px;
  clip-path: polygon(30% 0%, 70% 0%, 100% 22%, 82% 36%, 74% 26%, 74% 100%, 26% 100%, 26% 26%, 18% 36%, 0% 22%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding-top: 4px;
  font-family: 'Oswald', sans-serif;
  font-weight: 700;
  font-size: 16px;
  color: #fff;
}

.jersey--5 { background: var(--turf-950); }
.jersey--7 { background: var(--turf-500); }
.jersey--empty { background: var(--muted); width: 40px; height: 44px; font-size: 18px; }

.field-cell__text { display: flex; flex-direction: column; gap: 2px; }
.field-cell__name { font-weight: 600; font-size: 14.5px; }
.field-cell__type { font-size: 12px; color: var(--muted); }

/* Price column in the table */
.price-column { min-width: 160px; }

.price-item {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  font-size: 12.5px;
  padding: 3px 0;
}

.price-item .time { color: var(--muted); font-family: 'JetBrains Mono', monospace; font-size: 11.5px; }
.price-item .price {
  font-family: 'JetBrains Mono', monospace;
  font-weight: 700;
  color: var(--turf-700);
  white-space: nowrap;
}

.price-empty { font-size: 12.5px; color: var(--muted); font-style: italic; }

/* Status badge */
.badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 5px 12px;
  border-radius: 20px;
  font-size: 12.5px;
  font-weight: 600;
}

.badge__dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
}

.badge--active { background: #e6f4e9; color: #1f7a37; }
.badge--maint { background: var(--amber-bg); color: var(--amber); }
.badge--closed { background: var(--coral-bg); color: var(--coral); }

/* Action icon buttons */
.icon-btn {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  border: 1px solid var(--line);
  background: #fff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  margin-left: 6px;
  transition: background 0.12s ease, color 0.12s ease, border-color 0.12s ease;
  color: var(--muted);
}

.icon-btn--edit:hover { background: #eaf1ff; border-color: #bcd3fb; color: #2563eb; }
.icon-btn--delete:hover { background: var(--coral-bg); border-color: #f2c1c9; color: var(--coral); }

/* ===================== EMPTY / LOADING STATE ===================== */
.state-block {
  padding: 64px 20px;
  text-align: center;
  color: var(--muted);
}

.state-block__spinner {
  width: 32px;
  height: 32px;
  margin: 0 auto 14px;
  border: 3px solid var(--line);
  border-top-color: var(--turf-500);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin { to { transform: rotate(360deg); } }

.state-block__jersey { margin-bottom: 12px; display: flex; justify-content: center; }
.state-block__title { font-weight: 600; color: var(--ink); margin: 0 0 4px; }
.state-block__sub { font-size: 13.5px; margin: 0; }

/* ===================== MODAL ===================== */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(10, 35, 24, 0.5);
  backdrop-filter: blur(3px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 50;
}

.modal {
  width: 460px;
  max-height: 88vh;
  overflow-y: auto;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 24px 56px rgba(0, 0, 0, 0.28);
}

.modal__header {
  position: relative;
  background: linear-gradient(150deg, var(--turf-950), var(--turf-700));
  padding: 22px 26px;
  border-radius: 16px 16px 0 0;
}

.modal__eyebrow {
  font-family: 'JetBrains Mono', monospace;
  font-size: 11px;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  color: var(--turf-300);
  margin: 0 0 4px;
}

.modal__header h2 {
  font-family: 'Oswald', sans-serif;
  text-transform: uppercase;
  color: #fff;
  font-size: 24px;
  font-weight: 700;
  margin: 0;
}

.modal__close {
  position: absolute;
  top: 18px;
  right: 20px;
  background: rgba(255, 255, 255, 0.12);
  border: none;
  color: #fff;
  width: 26px;
  height: 26px;
  border-radius: 50%;
  cursor: pointer;
  font-size: 13px;
}

.modal__body { padding: 22px 26px 6px; display: flex; flex-direction: column; }

.field-label {
  font-size: 12.5px;
  font-weight: 600;
  color: var(--muted);
  margin: 14px 0 6px;
}

.field-label:first-child { margin-top: 0; }

.divider {
  border: none;
  border-top: 1px solid var(--line);
  margin: 18px 0 4px;
}

.modal input,
.modal textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid var(--line);
  border-radius: 9px;
  font-family: 'Inter', sans-serif;
  font-size: 14px;
  color: var(--ink);
  outline: none;
  transition: border-color 0.12s ease;
  resize: vertical;
}

.modal input:focus,
.modal textarea:focus { border-color: var(--turf-500); }

.jersey-picker { display: flex; gap: 10px; }

.jersey-picker__option {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 9px 10px;
  border: 1.5px solid var(--line);
  border-radius: 10px;
  background: #fff;
  font-family: 'Inter', sans-serif;
  font-size: 13.5px;
  font-weight: 500;
  color: var(--ink);
  cursor: pointer;
}

.jersey-picker__option--active {
  border-color: var(--turf-500);
  background: #eef7f0;
  font-weight: 600;
}

.jersey-picker .jersey { width: 26px; height: 28px; font-size: 12px; padding-top: 2px; }

/* Bảng giá trong form */
.price-row {
  display: grid;
  grid-template-columns: 1fr auto 1fr 1fr auto;
  align-items: center;
  gap: 6px;
  margin-bottom: 8px;
}

.price-row input { width: auto; }
.price-row__sep { color: var(--muted); font-size: 12px; text-align: center; }

.btn-remove {
  width: 28px;
  height: 34px;
  border: 1px solid var(--line);
  background: var(--coral-bg);
  color: var(--coral);
  border-radius: 8px;
  font-weight: 600;
  font-size: 12px;
  cursor: pointer;
}

.price-hint {
  font-size: 12.5px;
  color: var(--muted);
  font-style: italic;
  margin: 2px 0 10px;
}

.btn-add-price {
  align-self: flex-start;
  background: #eef7f0;
  color: var(--turf-700);
  border: 1px dashed var(--turf-500);
  padding: 8px 14px;
  border-radius: 9px;
  font-weight: 600;
  font-size: 13px;
  cursor: pointer;
  margin-bottom: 6px;
}

.status-picker { display: flex; gap: 8px; flex-wrap: wrap; }

.status-chip {
  padding: 8px 14px;
  border-radius: 20px;
  border: 1.5px solid var(--line);
  background: #fff;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  color: var(--muted);
}

.status-chip--selected.status-chip--active { background: #e6f4e9; border-color: var(--turf-500); color: #1f7a37; }
.status-chip--selected.status-chip--maint { background: var(--amber-bg); border-color: var(--amber); color: var(--amber); }
.status-chip--selected.status-chip--closed { background: var(--coral-bg); border-color: var(--coral); color: var(--coral); }

.modal__footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 20px 26px 24px;
}

.btn-save {
  background: var(--turf-500);
  color: #fff;
  border: none;
  padding: 10px 20px;
  border-radius: 9px;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
}

.btn-save:hover { background: var(--turf-700); }

.btn-close {
  background: #f1f2ee;
  color: var(--ink);
  border: none;
  padding: 10px 18px;
  border-radius: 9px;
  font-weight: 500;
  font-size: 14px;
  cursor: pointer;
}

/* ===================== RESPONSIVE ===================== */
@media (max-width: 900px) {
  .scoreboard { grid-template-columns: repeat(2, 1fr); }
  .scoreboard__panel:nth-child(2) { border-right: none; }
}

@media (max-width: 640px) {
  .pitch-header__title { font-size: 30px; }
  .scoreboard { grid-template-columns: 1fr; }
  .scoreboard__panel { border-right: none; }
  .toolbar { flex-direction: column; align-items: stretch; }
  .search-box { width: 100%; }
  .table-wrapper { overflow-x: auto; }
  table { min-width: 640px; }
}
</style>