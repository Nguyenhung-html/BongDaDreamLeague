<template>
  <div class="page">

    <!-- ===== NỀN AMBIENT: đèn pha + hạt sáng + hạt rơi + đạo cụ bay bay ===== -->
    <div class="page__ambient" aria-hidden="true">
      <div class="floodlight floodlight--l"></div>
      <div class="floodlight floodlight--r"></div>
      <div class="motes"><span v-for="n in 12" :key="'m'+n" class="mote" :style="{ '--i': n }"></span></div>
      <div class="falling"><span v-for="n in 16" :key="'f'+n" class="falling__item" :style="{ '--i': n }"></span></div>
      <div class="field-props">
        <div class="prop prop--ball"><svg viewBox="0 0 40 40"><circle cx="20" cy="20" r="18" fill="var(--chalk-050)" stroke="var(--night-950)" stroke-width="1.4"/><polygon points="20,10 26,14.5 24,21.5 16,21.5 14,14.5" fill="var(--night-950)"/></svg></div>
        <div class="prop prop--whistle"><svg viewBox="0 0 48 32" fill="none"><path d="M14 16a10 10 0 1 0 20 0 10 10 0 0 0-20 0Z" stroke="var(--lime-300)" stroke-width="1.6"/><circle cx="24" cy="16" r="3.2" fill="var(--lime-300)"/></svg></div>
        <div class="prop prop--flag"><svg viewBox="0 0 30 46" fill="none"><path d="M4 44V4" stroke="var(--lime-300)" stroke-width="1.8" stroke-linecap="round"/><path d="M4 5l20 6-20 6V5Z" fill="var(--lime-300)" opacity=".85"/></svg></div>
      </div>
    </div>

    <!-- Header -->
    <header class="page__header reveal">
      <h1 class="page__title">Quản lý sân bóng</h1>
      <p class="page__desc">Theo dõi, thêm mới và cập nhật toàn bộ sân bóng của hệ thống <span class="brand">Dream League</span></p>
    </header>

    <!-- Stat strip -->
    <section class="stats">
      <div class="stat-card reveal" style="--d:0s">
        <div class="stat-card__icon stat-card__icon--total">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="5" width="18" height="14" rx="1"/><line x1="12" y1="5" x2="12" y2="19"/><circle cx="12" cy="12" r="3"/></svg>
        </div>
        <div>
          <p class="stat-card__value">{{ stats.total }}</p>
          <p class="stat-card__label">Tổng số sân</p>
        </div>
      </div>

      <div class="stat-card reveal" style="--d:.06s">
        <div class="stat-card__icon stat-card__icon--active">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/><path d="M8.5 12.5l2.5 2.5 5-5"/></svg>
        </div>
        <div>
          <p class="stat-card__value">{{ stats.active }}</p>
          <p class="stat-card__label">Đang hoạt động</p>
        </div>
      </div>

      <div class="stat-card reveal" style="--d:.12s">
        <div class="stat-card__icon stat-card__icon--maint">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14.7 6.3a4 4 0 0 0-5.4 5.4L2 19l3 3 7.3-7.3a4 4 0 0 0 5.4-5.4l-2.8 2.8-2-2 2.8-2.8z"/></svg>
        </div>
        <div>
          <p class="stat-card__value">{{ stats.maintenance }}</p>
          <p class="stat-card__label">Bảo trì</p>
        </div>
      </div>

      <div class="stat-card reveal" style="--d:.18s">
        <div class="stat-card__icon stat-card__icon--closed">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/><line x1="9" y1="9" x2="15" y2="15"/><line x1="15" y1="9" x2="9" y2="15"/></svg>
        </div>
        <div>
          <p class="stat-card__value">{{ stats.closed }}</p>
          <p class="stat-card__label">Đóng cửa</p>
        </div>
      </div>
    </section>

    <!-- Toolbar -->
    <div class="toolbar reveal" style="--d:.22s">
      <div class="toolbar__filters">
        <div class="search">
          <svg class="search__icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="7"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
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

      <button class="btn btn--primary" @click="openAdd">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        Thêm sân
      </button>
    </div>

    <!-- Table -->
    <div class="table-card reveal" style="--d:.28s">

      <div v-if="loading" class="state">
        <div class="spinner"></div>
        <p>Đang tải danh sách sân...</p>
      </div>

      <div v-else-if="filteredFields.length === 0" class="state">
        <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M22 12h-6l-2 3h-4l-2-3H2"/><path d="M5.45 5.11L2 12v6a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-6l-3.45-6.89A2 2 0 0 0 16.76 4H7.24a2 2 0 0 0-1.79 1.11z"/></svg>
        <p>Không tìm thấy sân nào phù hợp.</p>
      </div>

      <table v-else>
        <thead>
          <tr>
            <th>Sân</th>
            <th>Địa chỉ</th>
            <th>Khung giờ</th>
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

            <!-- Cột riêng: Khung giờ -->
            <td class="time-column">
              <div v-if="!item.danhSachGia || item.danhSachGia.length === 0" class="price-empty">—</div>
              <div v-for="gia in item.danhSachGia" :key="'t-' + gia.id" class="time-line">
                {{ formatTime(gia.gioBatDau) }} – {{ formatTime(gia.gioKetThuc) }}
              </div>
            </td>

            <!-- Cột riêng: Bảng giá -->
            <td class="price-column">
              <div v-if="!item.danhSachGia || item.danhSachGia.length === 0" class="price-empty">Chưa có bảng giá</div>
              <div v-for="gia in item.danhSachGia" :key="'p-' + gia.id" class="price-line">
                {{ formatMoney(gia.giaTien) }}
              </div>
            </td>

            <td><span class="metric">{{ item.soDonDaDat }}</span></td>

            <td>
              <span class="badge" :class="statusMeta(item.trangThai).class">
                <span class="badge__dot"></span>
                {{ statusMeta(item.trangThai).label }}
              </span>
            </td>

            <td class="col-actions">
              <button class="icon-btn icon-btn--edit" title="Sửa" @click="editField(item)">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 20h9"/><path d="M16.5 3.5a2.12 2.12 0 0 1 3 3L7 19l-4 1 1-4 12.5-12.5z"/></svg>
              </button>
              <button class="icon-btn icon-btn--delete" title="Xóa" @click="openDeleteModal(item)">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/><path d="M10 11v6"/><path d="M14 11v6"/><path d="M9 6V4a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2v2"/></svg>
              </button>
            </td>

          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal Thêm/Sửa -->
    <Transition name="fade">
      <div class="overlay" v-if="showModal" @click.self="showModal = false">
        <div class="modal">

          <div class="modal__header">
            <div class="modal__icon">
              <svg v-if="!isEdit" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="5" width="18" height="14" rx="1"/><line x1="12" y1="5" x2="12" y2="19"/><circle cx="12" cy="12" r="3"/></svg>
              <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 20h9"/><path d="M16.5 3.5a2.12 2.12 0 0 1 3 3L7 19l-4 1 1-4 12.5-12.5z"/></svg>
            </div>
            <div>
              <h2>{{ isEdit ? "Cập nhật sân bóng" : "Thêm sân bóng" }}</h2>
              <p>{{ isEdit ? "Chỉnh sửa thông tin và bảng giá của sân" : "Tạo hồ sơ sân bóng mới" }}</p>
            </div>
            <button class="modal__close" @click="showModal = false">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
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

            <hr class="divider" />

            <label class="field-label">Bảng giá theo khung giờ</label>

            <div
              class="price-row"
              v-for="(gia, index) in form.danhSachGia"
              :key="gia.id ?? `moi-${index}`"
            >
              <input type="time" v-model="gia.gioBatDau" />
              <span class="price-row__sep">–</span>
              <input type="time" v-model="gia.gioKetThuc" />
              <input type="number" v-model.number="gia.giaTien" placeholder="Giá (VNĐ)" min="0" />
              <button type="button" class="btn-remove" @click="removePrice(index)">✕</button>
            </div>

            <p v-if="form.danhSachGia.length === 0" class="price-hint">
              Chưa có khung giờ nào. Nhấn "Thêm khung giờ" để thêm bảng giá cho sân.
            </p>

            <button type="button" class="btn-add-price" @click="addPrice">+ Thêm khung giờ</button>

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
            <button class="btn btn--ghost" @click="showModal = false">Hủy</button>
            <button class="btn btn--primary" :disabled="saving" @click="saveField">
              {{ saving ? "Đang lưu..." : (isEdit ? "Lưu thay đổi" : "Thêm sân") }}
            </button>
          </div>

        </div>
      </div>
    </Transition>

    <!-- Modal Xóa -->
    <Transition name="fade">
      <div class="overlay" v-if="showDelete" @click.self="showDelete = false">
        <div class="modal modal--sm">

          <div class="modal__icon modal__icon--danger">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/><path d="M10 11v6"/><path d="M14 11v6"/><path d="M9 6V4a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2v2"/></svg>
          </div>

          <h2>Xóa sân bóng?</h2>
          <p class="modal__text">
            Bạn có chắc muốn xóa <strong>{{ currentField?.tenSan }}</strong>?
            Toàn bộ bảng giá của sân sẽ bị xóa theo và không thể hoàn tác.
          </p>

          <div class="modal__footer modal__footer--center">
            <button class="btn btn--ghost" @click="showDelete = false">Hủy</button>
            <button class="btn btn--danger" :disabled="deleting" @click="deleteField">
              {{ deleting ? "Đang xóa..." : "Xóa sân" }}
            </button>
          </div>

        </div>
      </div>
    </Transition>

  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from "vue"
import axios from "axios"

const API = "/api/admin/san-bong"
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
const saving = ref(false)

const showDelete = ref(false)
const deleting = ref(false)
const currentField = ref(null)

const fields = ref([])

// Danh sách id khung giờ bị xoá khỏi form khi đang sửa, sẽ gọi API xoá lúc lưu
const deletedGiaIds = ref([])

const form = ref({
  id: null,
  tenSan: "",
  loaiSan: 5,
  diaChi: "",
  moTa: "",
  trangThai: "HOAT_DONG",
  danhSachGia: []
})

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

// =========================
// Hiệu ứng xuất hiện khi cuộn tới
// =========================
let revealObserver = null

onMounted(() => {
  loadFields()

  revealObserver = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('is-visible')
        revealObserver.unobserve(entry.target)
      }
    })
  }, { threshold: 0.1 })

  nextTick(() => document.querySelectorAll('.reveal').forEach(el => revealObserver.observe(el)))
})

onUnmounted(() => {
  if (revealObserver) revealObserver.disconnect()
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

  saving.value = true

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

    showModal.value = false
    await loadFields()
  } catch (err) {
    console.error(err)
    alert(err.response?.data?.message || "Có lỗi xảy ra.")
  } finally {
    saving.value = false
  }
}

// =========================
// Xóa
// =========================
function openDeleteModal(item) {
  currentField.value = item
  showDelete.value = true
}

async function deleteField() {
  deleting.value = true
  try {
    await axios.delete(`${API}/${currentField.value.id}`, config)
    await loadFields()
    showDelete.value = false
  } catch (err) {
    console.error(err)
    alert(err.response?.data?.message || "Không thể xóa sân.")
  } finally {
    deleting.value = false
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Oswald:wght@500;600;700&family=Manrope:wght@400;500;600;700;800&family=Space+Mono:wght@400;700&display=swap');

.page{
    --night-950:#050b08;
    --night-800:#0a1f13;
    --night-700:#123321;
    --turf-500:#23935a;
    --turf-700:#146239;
    --lime-400:#b6ff3c;
    --lime-300:#d3ff8f;
    --sky-400:#38bdf8;
    --sky-300:#7dd3fc;
    --purple-400:#a78bfa;
    --amber-400:#ffb020;
    --crimson-500:#ff4757;
    --chalk-050:#f7fbf4;
    --chalk-200:#e3ecdf;

    --font-display:'Oswald','Manrope',sans-serif;
    --font-body:'Manrope',sans-serif;
    --font-mono:'Space Mono',monospace;

    position:relative;
    background:var(--night-950);
    color:var(--chalk-050);
    font-family:var(--font-body);
    border-radius:20px;
    padding:32px;
    overflow:hidden;
    min-height:calc(100vh - 40px);
}

/* ===== REVEAL ===== */
.reveal{ opacity:0; transform:translateY(18px); transition:opacity .6s ease, transform .6s ease; transition-delay:var(--d,0s); }
.reveal.is-visible{ opacity:1; transform:translateY(0); }
@media (prefers-reduced-motion: reduce){ .reveal{ opacity:1; transform:none; transition:none; } }

/* ===== AMBIENT: đèn pha + hạt sáng + hạt rơi + đạo cụ ===== */
.page__ambient{ position:absolute; inset:0; z-index:0; pointer-events:none; border-radius:20px; overflow:hidden; }
.floodlight{ position:absolute; top:-20%; width:40vh; height:100vh; background:conic-gradient(from 90deg at 50% 0%, transparent 42%, rgba(255,244,214,.07) 50%, transparent 58%); mix-blend-mode:screen; animation:sweep 11s ease-in-out infinite alternate; }
.floodlight--l{ left:-6%; }
.floodlight--r{ right:-6%; animation-direction:alternate-reverse; }
@keyframes sweep{ 0%{ transform:rotate(-8deg); } 100%{ transform:rotate(8deg); } }

.motes{ position:absolute; inset:0; }
.mote{ position:absolute; bottom:-10px; left:calc((var(--i) * 8.6%) + 1%); width:3px; height:3px; border-radius:50%; background:var(--lime-300); opacity:0; animation:rise 9s linear infinite; animation-delay:calc(var(--i) * -0.7s); }
@keyframes rise{ 0%{ transform:translateY(0) scale(.6); opacity:0; } 10%{ opacity:.5; } 90%{ opacity:.1; } 100%{ transform:translateY(-100%) scale(1.1); opacity:0; } }

/* Hạt rơi */
.falling{ position:absolute; inset:0; overflow:hidden; }
.falling__item{
  position:absolute; top:-6%; left:calc((var(--i) * 6.2%) + 1%);
  width:3px; height:3px; border-radius:50%; background:var(--chalk-050);
  opacity:0; animation:fall linear infinite;
  animation-duration:calc(7s + (var(--i) * 0.45s));
  animation-delay:calc(var(--i) * -0.6s);
}
@keyframes fall{
  0%{ transform:translateY(0) translateX(0); opacity:0; }
  8%{ opacity:.5; }
  50%{ transform:translateY(48vh) translateX(14px); }
  92%{ opacity:.12; }
  100%{ transform:translateY(100vh) translateX(-10px); opacity:0; }
}

.field-props{ position:absolute; inset:0; }
.prop{ position:absolute; opacity:.1; animation:prop-float 7s ease-in-out infinite; }
.prop svg{ width:100%; height:100%; }
.prop--ball{ width:44px; height:44px; top:8%; right:8%; }
.prop--whistle{ width:52px; height:36px; bottom:12%; right:5%; animation-delay:.6s; }
.prop--flag{ width:30px; height:44px; top:20%; left:3%; animation-delay:1.1s; }
@keyframes prop-float{ 0%,100%{ transform:translateY(0) rotate(-3deg); } 50%{ transform:translateY(-14px) rotate(3deg); } }

@media (prefers-reduced-motion: reduce){
  .floodlight, .mote, .falling__item, .prop{ animation:none; }
}

/* Header */
.page__header{ position:relative; z-index:1; }
.page__title{
    font-family:var(--font-display);
    font-size:28px;
    font-weight:600;
    letter-spacing:-.01em;
    margin-bottom:6px;
    color:var(--chalk-050);
}

.page__desc{
    color:var(--chalk-200);
    opacity:.75;
    font-size:14.5px;
    margin-bottom:26px;
}

.brand{
    color:var(--lime-300);
    font-weight:700;
    text-shadow:0 0 10px rgba(182,255,60,.35);
}

/* Stats */
.stats{
    position:relative; z-index:1;
    display:grid;
    grid-template-columns:repeat(4,1fr);
    gap:16px;
    margin-bottom:22px;
}

.stat-card{
    background:rgba(247,251,244,.04);
    border:1px solid rgba(247,251,244,.1);
    border-radius:16px;
    padding:18px 20px;
    display:flex;
    align-items:center;
    gap:14px;
    transition:transform .2s ease, box-shadow .2s ease, border-color .2s ease;
}
.stat-card:hover{ transform:translateY(-3px); border-color:rgba(182,255,60,.3); box-shadow:0 14px 28px -16px rgba(182,255,60,.25); }

.stat-card__icon{
    width:42px;
    height:42px;
    min-width:42px;
    border-radius:11px;
    display:flex;
    align-items:center;
    justify-content:center;
}

.stat-card__icon--total{ background:rgba(56,189,248,.14); color:var(--sky-300); }
.stat-card__icon--active{ background:rgba(182,255,60,.14); color:var(--lime-300); }
.stat-card__icon--maint{ background:rgba(255,176,32,.14); color:var(--amber-400); }
.stat-card__icon--closed{ background:rgba(255,71,87,.14); color:#ff9686; }

.stat-card__value{
    font-family:var(--font-display);
    font-size:22px;
    font-weight:600;
    line-height:1.1;
    color:var(--chalk-050);
}

.stat-card__label{
    color:var(--chalk-200);
    opacity:.7;
    font-size:12.5px;
    margin-top:2px;
}

/* Toolbar */
.toolbar{
    position:relative; z-index:1;
    display:flex;
    justify-content:space-between;
    align-items:center;
    gap:16px;
    margin-bottom:18px;
    flex-wrap:wrap;
}

.toolbar__filters{
    display:flex;
    gap:10px;
    flex-wrap:wrap;
}

.search{ position:relative; width:280px; }

.search__icon{
    position:absolute;
    left:14px;
    top:50%;
    transform:translateY(-50%);
    color:rgba(247,251,244,.4);
}

.search input{
    width:100%;
    padding:11px 14px 11px 42px;
    border:1px solid rgba(247,251,244,.14);
    border-radius:10px;
    background:rgba(5,11,8,.5);
    color:var(--chalk-050);
    font-family:inherit;
    font-size:14px;
    outline:none;
    transition:border-color .15s, box-shadow .15s;
}
.search input::placeholder{ color:rgba(247,251,244,.4); }

.search input:focus{
    border-color:var(--lime-400);
    box-shadow:0 0 0 3px rgba(182,255,60,.15);
}

.filter-select{
    padding:11px 14px;
    border:1px solid rgba(247,251,244,.14);
    border-radius:10px;
    background:rgba(5,11,8,.5);
    font-family:inherit;
    font-size:14px;
    color:var(--chalk-050);
    outline:none;
    cursor:pointer;
    transition:border-color .15s, box-shadow .15s;
}

.filter-select:focus{
    border-color:var(--lime-400);
    box-shadow:0 0 0 3px rgba(182,255,60,.15);
}

.btn{
    display:inline-flex;
    align-items:center;
    gap:8px;
    border:none;
    border-radius:10px;
    padding:11px 20px;
    font-family:inherit;
    font-size:14px;
    font-weight:600;
    cursor:pointer;
    transition:background .15s, transform .05s, opacity .15s, box-shadow .15s;
}

.btn:active{ transform:scale(.98); }
.btn:disabled{ opacity:.6; cursor:not-allowed; }

.btn--primary{ background:var(--turf-500); color:var(--chalk-050); }
.btn--primary:hover:not(:disabled){ background:var(--lime-400); color:var(--night-950); box-shadow:0 8px 20px -8px rgba(182,255,60,.5); }

.btn--ghost{ background:rgba(247,251,244,.06); color:var(--chalk-050); border:1px solid rgba(247,251,244,.14); }
.btn--ghost:hover{ background:rgba(247,251,244,.12); }

.btn--danger{ background:var(--crimson-500); color:#fff; }
.btn--danger:hover:not(:disabled){ background:#e8384a; box-shadow:0 8px 20px -8px rgba(255,71,87,.5); }

/* Table */
.table-card{
    position:relative; z-index:1;
    background:rgba(247,251,244,.03);
    border:1px solid rgba(247,251,244,.09);
    border-radius:16px;
    overflow:hidden;
}

table{ width:100%; border-collapse:collapse; }

th{
    background:rgba(247,251,244,.04);
    padding:14px 18px;
    text-align:left;
    font-size:11.5px;
    font-weight:700;
    text-transform:uppercase;
    letter-spacing:.05em;
    color:var(--chalk-200);
    opacity:.7;
    border-bottom:1px solid rgba(247,251,244,.1);
}

td{
    padding:14px 18px;
    border-bottom:1px solid rgba(247,251,244,.07);
    vertical-align:middle;
    color:var(--chalk-050);
}

tbody tr:last-child td{ border-bottom:none; }
tbody tr:hover{ background:rgba(182,255,60,.045); }

.col-actions{ width:90px; }

.cell-muted{ color:var(--chalk-200); opacity:.75; font-size:13.5px; }

.metric{
    font-family:var(--font-mono);
    font-weight:700;
    color:var(--lime-300);
}

/* Jersey badge cho loại sân (5 vs 7) */
.field-cell{ display:flex; align-items:center; gap:13px; }

.jersey{
    flex-shrink:0;
    width:36px;
    height:40px;
    clip-path:polygon(30% 0%, 70% 0%, 100% 22%, 82% 36%, 74% 26%, 74% 100%, 26% 100%, 26% 26%, 18% 36%, 0% 22%);
    display:flex;
    align-items:center;
    justify-content:center;
    padding-top:4px;
    font-family:var(--font-display);
    font-weight:700;
    font-size:16px;
}

.jersey--5{ background:rgba(56,189,248,.16); color:var(--sky-300); border:1px solid rgba(56,189,248,.3); }
.jersey--7{ background:var(--lime-400); color:var(--night-950); }

.field-cell__text{ display:flex; flex-direction:column; gap:2px; }
.field-cell__name{ font-weight:700; font-size:14.5px; color:var(--chalk-050); }
.field-cell__type{ font-size:12px; color:var(--chalk-200); opacity:.65; }

/* Cột Khung giờ / Bảng giá — tách riêng, các dòng thẳng hàng theo từng khung giờ */
.time-column{ min-width:130px; }
.price-column{ min-width:120px; }

.time-line{
    font-family:var(--font-mono);
    font-size:12px;
    color:var(--chalk-200);
    opacity:.85;
    line-height:1.9;
    white-space:nowrap;
}

.price-line{
    font-family:var(--font-mono);
    font-weight:700;
    font-size:12.5px;
    color:var(--lime-300);
    line-height:1.9;
    white-space:nowrap;
}

.price-empty{ font-size:12.5px; color:var(--chalk-200); opacity:.5; font-style:italic; }

/* Badges */
.badge{
    display:inline-flex;
    align-items:center;
    gap:6px;
    padding:5px 12px;
    border-radius:20px;
    font-size:12.5px;
    font-weight:700;
}

.badge__dot{ width:6px; height:6px; border-radius:50%; background:currentColor; }

.badge--active{ background:rgba(182,255,60,.14); color:var(--lime-300); }
.badge--maint{ background:rgba(255,176,32,.14); color:var(--amber-400); }
.badge--closed{ background:rgba(255,71,87,.14); color:#ff9686; }

/* Icon buttons */
.icon-btn{
    width:32px;
    height:32px;
    border:none;
    border-radius:8px;
    display:inline-flex;
    align-items:center;
    justify-content:center;
    cursor:pointer;
    margin-right:6px;
    transition:background .15s, color .15s, box-shadow .15s;
}

.icon-btn--edit{ background:rgba(56,189,248,.14); color:var(--sky-300); }
.icon-btn--edit:hover{ background:var(--sky-400); color:var(--night-950); box-shadow:0 0 12px -2px rgba(56,189,248,.6); }

.icon-btn--delete{ background:rgba(247,251,244,.07); color:var(--chalk-200); }
.icon-btn--delete:hover{ background:var(--crimson-500); color:#fff; box-shadow:0 0 12px -2px rgba(255,71,87,.6); }

/* Loading / empty state */
.state{
    display:flex;
    flex-direction:column;
    align-items:center;
    justify-content:center;
    gap:12px;
    padding:64px 20px;
    color:var(--chalk-200);
    opacity:.75;
    font-size:14px;
}

.spinner{
    width:30px;
    height:30px;
    border:3px solid rgba(247,251,244,.15);
    border-top-color:var(--lime-400);
    border-radius:50%;
    animation:spin .7s linear infinite;
}

@keyframes spin{ to{ transform:rotate(360deg); } }

/* Modal */
.overlay{
    position:fixed;
    inset:0;
    background:rgba(5,11,8,.72);
    backdrop-filter:blur(3px);
    display:flex;
    justify-content:center;
    align-items:center;
    z-index:999;
    padding:20px;
}

.modal{
    width:480px;
    max-width:100%;
    max-height:90vh;
    overflow-y:auto;
    background:var(--night-800);
    border:1px solid rgba(182,255,60,.18);
    border-radius:16px;
    box-shadow:0 24px 60px rgba(0,0,0,.5);
    color:var(--chalk-050);
}

.modal--sm{
    width:400px;
    padding:32px;
    text-align:center;
}

.modal__header{
    display:flex;
    align-items:flex-start;
    gap:14px;
    padding:24px 24px 18px;
    border-bottom:1px solid rgba(247,251,244,.1);
}

.modal__header h2{
    font-family:var(--font-display);
    font-size:18px;
    font-weight:700;
    color:var(--chalk-050);
}

.modal__header p{
    color:var(--chalk-200);
    opacity:.7;
    font-size:13px;
    margin-top:2px;
}

.modal__icon{
    width:40px;
    height:40px;
    min-width:40px;
    border-radius:10px;
    background:rgba(182,255,60,.14);
    color:var(--lime-300);
    display:flex;
    align-items:center;
    justify-content:center;
}

.modal--sm .modal__icon{ margin:0 auto; }

.modal__icon--danger{
    width:52px;
    height:52px;
    border-radius:50%;
    background:rgba(255,71,87,.14);
    color:#ff9686;
}

.modal--sm h2{
    font-family:var(--font-display);
    font-size:18px;
    font-weight:700;
    margin:18px 0 8px;
    color:var(--chalk-050);
}

.modal__text{
    color:var(--chalk-200);
    opacity:.75;
    font-size:14px;
    line-height:1.5;
}

.modal__text strong{ color:var(--chalk-050); opacity:1; }

.modal__close{
    margin-left:auto;
    width:30px;
    height:30px;
    border:none;
    background:rgba(247,251,244,.06);
    border-radius:8px;
    color:var(--chalk-200);
    display:flex;
    align-items:center;
    justify-content:center;
    cursor:pointer;
}

.modal__close:hover{ background:rgba(247,251,244,.14); color:var(--chalk-050); }

.modal__body{
    padding:20px 24px 6px;
    display:flex;
    flex-direction:column;
}

.field-label{
    font-size:12.5px;
    font-weight:700;
    color:var(--chalk-200);
    opacity:.8;
    margin:14px 0 6px;
}

.field-label:first-child{ margin-top:0; }

.divider{
    border:none;
    border-top:1px solid rgba(247,251,244,.1);
    margin:18px 0 4px;
}

.modal input,
.modal textarea{
    width:100%;
    padding:10px 12px;
    border:1px solid rgba(247,251,244,.14);
    border-radius:9px;
    font-family:inherit;
    font-size:14px;
    outline:none;
    background:rgba(5,11,8,.5);
    color:var(--chalk-050);
    transition:border-color .15s, box-shadow .15s;
    resize:vertical;
}

.modal input:focus,
.modal textarea:focus{
    border-color:var(--lime-400);
    box-shadow:0 0 0 3px rgba(182,255,60,.15);
}

.modal input::placeholder,
.modal textarea::placeholder{ color:rgba(247,251,244,.35); }

/* Jersey picker */
.jersey-picker{ display:flex; gap:10px; }

.jersey-picker__option{
    flex:1;
    display:flex;
    align-items:center;
    justify-content:center;
    gap:8px;
    padding:9px 10px;
    border:1.5px solid rgba(247,251,244,.14);
    border-radius:10px;
    background:rgba(5,11,8,.5);
    font-family:inherit;
    font-size:13.5px;
    font-weight:600;
    color:var(--chalk-050);
    cursor:pointer;
    transition:border-color .15s, background .15s;
}

.jersey-picker__option--active{
    border-color:var(--lime-400);
    background:rgba(182,255,60,.1);
}

.jersey-picker .jersey{ width:26px; height:28px; font-size:12px; padding-top:2px; }

/* Bảng giá trong form */
.price-row{
    display:grid;
    grid-template-columns:1fr auto 1fr 1fr auto;
    align-items:center;
    gap:6px;
    margin-bottom:8px;
}

.price-row input{ width:auto; }
.price-row__sep{ color:var(--chalk-200); opacity:.6; font-size:12px; text-align:center; }

.btn-remove{
    width:28px;
    height:34px;
    border:1px solid rgba(255,71,87,.3);
    background:rgba(255,71,87,.14);
    color:#ff9686;
    border-radius:8px;
    font-weight:700;
    font-size:12px;
    cursor:pointer;
    transition:background .15s, color .15s;
}

.btn-remove:hover{ background:var(--crimson-500); color:#fff; }

.price-hint{
    font-size:12.5px;
    color:var(--chalk-200);
    opacity:.6;
    font-style:italic;
    margin:2px 0 10px;
}

.btn-add-price{
    align-self:flex-start;
    background:rgba(182,255,60,.1);
    color:var(--lime-300);
    border:1px dashed var(--lime-400);
    padding:8px 14px;
    border-radius:9px;
    font-weight:700;
    font-size:13px;
    cursor:pointer;
    margin-bottom:6px;
    transition:background .15s;
}

.btn-add-price:hover{ background:rgba(182,255,60,.18); }

/* Status picker */
.status-picker{ display:flex; gap:8px; flex-wrap:wrap; }

.status-chip{
    padding:8px 14px;
    border-radius:20px;
    border:1.5px solid rgba(247,251,244,.14);
    background:rgba(5,11,8,.5);
    font-size:13px;
    font-weight:600;
    cursor:pointer;
    color:var(--chalk-200);
    transition:border-color .15s, background .15s, color .15s;
}

.status-chip--selected.status-chip--active{ background:rgba(182,255,60,.14); border-color:var(--lime-400); color:var(--lime-300); }
.status-chip--selected.status-chip--maint{ background:rgba(255,176,32,.14); border-color:var(--amber-400); color:var(--amber-400); }
.status-chip--selected.status-chip--closed{ background:rgba(255,71,87,.14); border-color:var(--crimson-500); color:#ff9686; }

.modal__footer{
    display:flex;
    justify-content:flex-end;
    gap:10px;
    padding:18px 24px 24px;
}

.modal__footer--center{ justify-content:center; margin-top:22px; padding:0; }

/* Transition */
.fade-enter-active,
.fade-leave-active{ transition:opacity .18s ease; }

.fade-enter-from,
.fade-leave-to{ opacity:0; }

.fade-enter-active .modal,
.fade-leave-active .modal{ transition:transform .18s ease, opacity .18s ease; }

.fade-enter-from .modal,
.fade-leave-to .modal{ transform:scale(.96); opacity:0; }

/* Responsive */
@media(max-width:900px){
    .stats{ grid-template-columns:repeat(2,1fr); }
}

@media(max-width:768px){
    .toolbar{ flex-direction:column; align-items:stretch; }
    .toolbar__filters{ flex-direction:column; }
    .search{ width:100%; }
    .filter-select{ width:100%; }
    .table-card{ overflow-x:auto; }
    table{ font-size:13px; min-width:760px; }
}

@media(max-width:520px){
    .stats{ grid-template-columns:1fr 1fr; }
    .price-row{ grid-template-columns:1fr; }
}
</style>