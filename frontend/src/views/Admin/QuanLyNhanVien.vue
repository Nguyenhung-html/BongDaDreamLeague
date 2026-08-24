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
      <h1 class="page__title">Quản lý nhân viên</h1>
      <p class="page__desc">Quản lý tài khoản nhân viên của hệ thống <span class="brand">Dream League</span></p>
    </header>

    <!-- Stat strip -->
    <section class="stats">
      <div class="stat-card reveal" style="--d:0s">
        <div class="stat-card__icon stat-card__icon--total">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H7a4 4 0 0 0-4 4v2"/><circle cx="10" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
        </div>
        <div>
          <p class="stat-card__value">{{ stats.total }}</p>
          <p class="stat-card__label">Tổng nhân viên</p>
        </div>
      </div>

      <div class="stat-card reveal" style="--d:.06s">
        <div class="stat-card__icon stat-card__icon--admin">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2l8 4v6c0 5-3.5 8.5-8 10-4.5-1.5-8-5-8-10V6l8-4z"/></svg>
        </div>
        <div>
          <p class="stat-card__value">{{ stats.admin }}</p>
          <p class="stat-card__label">Quản trị viên</p>
        </div>
      </div>

      <div class="stat-card reveal" style="--d:.12s">
        <div class="stat-card__icon stat-card__icon--active">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/><path d="M8.5 12.5l2.5 2.5 5-5"/></svg>
        </div>
        <div>
          <p class="stat-card__value">{{ stats.active }}</p>
          <p class="stat-card__label">Đang hoạt động</p>
        </div>
      </div>

      <div class="stat-card reveal" style="--d:.18s">
        <div class="stat-card__icon stat-card__icon--locked">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="4" y="10" width="16" height="10" rx="2"/><path d="M8 10V7a4 4 0 0 1 8 0v3"/></svg>
        </div>
        <div>
          <p class="stat-card__value">{{ stats.locked }}</p>
          <p class="stat-card__label">Đã khóa</p>
        </div>
      </div>
    </section>

    <!-- Toolbar -->
    <div class="toolbar reveal" style="--d:.22s">
      <div class="search">
        <svg class="search__icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="7"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
        <input
          v-model="keyword"
          type="text"
          placeholder="Tìm theo tên hoặc số điện thoại..."
        />
      </div>

      <button class="btn btn--primary" @click="openAddModal">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        Thêm nhân viên
      </button>
    </div>

    <!-- Table -->
    <div class="table-card reveal" style="--d:.28s">

      <div v-if="loading" class="state">
        <div class="spinner"></div>
        <p>Đang tải dữ liệu...</p>
      </div>

      <div v-else-if="filteredEmployees.length === 0" class="state">
        <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M22 12h-6l-2 3h-4l-2-3H2"/><path d="M5.45 5.11L2 12v6a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-6l-3.45-6.89A2 2 0 0 0 16.76 4H7.24a2 2 0 0 0-1.79 1.11z"/></svg>
        <p>Không tìm thấy nhân viên nào phù hợp.</p>
      </div>

      <table v-else>
        <thead>
          <tr>
            <th>Nhân viên</th>
            <th>Liên hệ</th>
            <th>Chức vụ</th>
            <th>Trạng thái</th>
            <th class="col-actions">Thao tác</th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="item in filteredEmployees" :key="item.id">

            <td>
              <div class="employee">
                <div class="avatar" :class="item.vaiTro === 'ADMIN' ? 'avatar--admin' : 'avatar--staff'">
                  {{ initials(item.hoTen) }}
                </div>
                <div>
                  <p class="employee__name">{{ item.hoTen }}</p>
                  <p class="employee__id">ID #{{ item.id }}</p>
                </div>
              </div>
            </td>

            <td>
              <p class="contact__phone">{{ item.soDienThoai }}</p>
              <p class="contact__email">{{ item.email }}</p>
            </td>

            <td>
              <span class="badge" :class="item.vaiTro === 'ADMIN' ? 'badge--admin' : 'badge--staff'">
                {{ item.vaiTro === 'ADMIN' ? 'Admin' : 'Staff' }}
              </span>
            </td>

            <td>
              <span class="badge badge--status" :class="item.trangThai ? 'badge--active' : 'badge--locked'">
                <span class="badge__dot"></span>
                {{ item.trangThai ? 'Hoạt động' : 'Đã khóa' }}
              </span>
            </td>

            <td class="col-actions">
              <button class="icon-btn icon-btn--edit" title="Sửa" @click="openEditModal(item)">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 20h9"/><path d="M16.5 3.5a2.12 2.12 0 0 1 3 3L7 19l-4 1 1-4 12.5-12.5z"/></svg>
              </button>
              <button
                v-if="item.trangThai"
                class="icon-btn icon-btn--lock"
                title="Khóa tài khoản"
                @click="toggleLock(item)"
              >
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="4" y="10" width="16" height="10" rx="2"/><path d="M8 10V7a4 4 0 0 1 8 0v3"/></svg>
              </button>
              <button
                v-else
                class="icon-btn icon-btn--unlock"
                title="Mở khóa tài khoản"
                @click="toggleLock(item)"
              >
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="4" y="10" width="16" height="10" rx="2"/><path d="M8 10V7a4 4 0 0 1 7.75-1"/></svg>
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
      <div v-if="showModal" class="overlay" @click.self="showModal = false">
        <div class="modal">

          <div class="modal__header">
            <div class="modal__icon">
              <svg v-if="!isEdit" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><line x1="19" y1="8" x2="19" y2="14"/><line x1="22" y1="11" x2="16" y2="11"/></svg>
              <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 20h9"/><path d="M16.5 3.5a2.12 2.12 0 0 1 3 3L7 19l-4 1 1-4 12.5-12.5z"/></svg>
            </div>
            <div>
              <h2>{{ isEdit ? 'Cập nhật nhân viên' : 'Thêm nhân viên' }}</h2>
              <p>{{ isEdit ? 'Chỉnh sửa thông tin tài khoản' : 'Tạo tài khoản nhân viên mới' }}</p>
            </div>
            <button class="modal__close" @click="showModal = false">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </div>

          <div class="modal__body">
            <div class="field">
              <label>Họ tên</label>
              <input v-model="form.hoTen" placeholder="Nguyễn Văn A" />
            </div>

            <div class="field-row">
              <div class="field">
                <label>Số điện thoại</label>
                <input v-model="form.soDienThoai" placeholder="09xxxxxxxx" />
              </div>
              <div class="field">
                <label>Email</label>
                <input v-model="form.email" placeholder="email@domain.com" />
              </div>
            </div>

            <div class="field" v-if="!isEdit">
              <label>Mật khẩu</label>
              <input v-model="form.matKhau" type="password" placeholder="Tối thiểu 6 ký tự" />
            </div>

            <div class="field">
              <label>Địa chỉ</label>
              <input v-model="form.diaChi" placeholder="Địa chỉ liên hệ" />
            </div>

            <div class="field-row">
              <div class="field">
                <label>Chức vụ</label>
                <select v-model="form.vaiTro">
                  <option value="STAFF">Staff</option>
                  <option value="ADMIN">Admin</option>
                </select>
              </div>
              <div class="field" v-if="isEdit">
                <label>Trạng thái</label>
                <select v-model="form.trangThai">
                  <option :value="true">Mở khóa</option>
                  <option :value="false">Đã khóa</option>
                </select>
              </div>
            </div>
          </div>

          <div class="modal__footer">
            <button class="btn btn--ghost" @click="showModal = false">Hủy</button>
            <button class="btn btn--primary" :disabled="saving" @click="saveEmployee">
              {{ saving ? 'Đang lưu...' : (isEdit ? 'Cập nhật' : 'Thêm mới') }}
            </button>
          </div>

        </div>
      </div>
    </Transition>

    <!-- Modal Xóa -->
    <Transition name="fade">
      <div v-if="showDelete" class="overlay" @click.self="showDelete = false">
        <div class="modal modal--sm">

          <div class="modal__icon modal__icon--danger">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/><path d="M10 11v6"/><path d="M14 11v6"/><path d="M9 6V4a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2v2"/></svg>
          </div>

          <h2>Xóa nhân viên?</h2>
          <p class="modal__text">
            Bạn có chắc muốn xóa <strong>{{ currentEmployee?.hoTen }}</strong>?
            Hành động này không thể hoàn tác.
          </p>

          <div class="modal__footer modal__footer--center">
            <button class="btn btn--ghost" @click="showDelete = false">Hủy</button>
            <button class="btn btn--danger" :disabled="deleting" @click="deleteEmployee">
              {{ deleting ? 'Đang xóa...' : 'Xóa nhân viên' }}
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

const API_BASE = "/api/admin/nhan-vien"

const keyword = ref('')

const showModal = ref(false)
const showDelete = ref(false)
const isEdit = ref(false)

const loading = ref(true)
const saving = ref(false)
const deleting = ref(false)

const currentEmployee = ref(null)
const employees = ref([])
const form = ref({
  id: null,
  hoTen: "",
  email: "",
  soDienThoai: "",
  matKhau: "",
  diaChi: "",
  vaiTro: "STAFF",
  trangThai: true
})

const filteredEmployees = computed(() => {
  return employees.value.filter(item => {
    const key = keyword.value.toLowerCase()

    return (
      item.hoTen?.toLowerCase().includes(key) ||
      item.soDienThoai?.includes(keyword.value)
    )
  })
})

const stats = computed(() => {
  const total = employees.value.length
  const admin = employees.value.filter(e => e.vaiTro === 'ADMIN').length
  const active = employees.value.filter(e => e.trangThai).length
  return { total, admin, active, locked: total - active }
})

function initials(name) {
  if (!name) return '?'
  const parts = name.trim().split(/\s+/).filter(Boolean)
  if (parts.length === 1) return parts[0].charAt(0).toUpperCase()
  return (parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase()
}

function authHeader() {
  return {
    Authorization: `Bearer ${localStorage.getItem("token")}`
  }
}

let revealObserver = null

onMounted(() => {
  loadEmployees()

  // hiệu ứng xuất hiện khi cuộn tới (giống trang Quản lý khách hàng)
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

async function loadEmployees() {
  loading.value = true
  try {
    const res = await axios.get(API_BASE, { headers: authHeader() })
    employees.value = res.data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function openAddModal() {
  isEdit.value = false

  form.value = {
    id: null,
    hoTen: "",
    soDienThoai: "",
    email: "",
    matKhau: "",
    diaChi: "",
    vaiTro: "STAFF",
    trangThai: true
  }

  showModal.value = true
}

function openEditModal(item) {
  isEdit.value = true
  form.value = { ...item }
  showModal.value = true
}

async function saveEmployee() {
  if (
    form.value.hoTen.trim() === "" ||
    form.value.email.trim() === "" ||
    form.value.soDienThoai.trim() === ""
  ) {
    alert("Vui lòng nhập đầy đủ thông tin")
    return
  }

  saving.value = true

  try {
    if (isEdit.value) {
      await axios.put(
        `${API_BASE}/${form.value.id}`,
        {
          hoTen: form.value.hoTen,
          email: form.value.email,
          soDienThoai: form.value.soDienThoai,
          diaChi: form.value.diaChi,
          vaiTro: form.value.vaiTro
        },
        { headers: authHeader() }
      )

      // chỉ gọi khóa/mở khóa khi đang sửa nhân viên có sẵn
      // (nhân viên mới tạo luôn ở trạng thái hoạt động nên không cần gọi thêm)
      const action = form.value.trangThai ? "mo-khoa" : "khoa"
      await axios.put(`${API_BASE}/${form.value.id}/${action}`, {}, { headers: authHeader() })

    } else {
      await axios.post(
        API_BASE,
        {
          hoTen: form.value.hoTen,
          email: form.value.email,
          soDienThoai: form.value.soDienThoai,
          matKhau: form.value.matKhau,
          diaChi: form.value.diaChi,
          vaiTro: form.value.vaiTro
        },
        { headers: authHeader() }
      )
    }

    await loadEmployees()
    showModal.value = false

  } catch (e) {
    console.error(e)
    alert(e.response?.data?.message || "Có lỗi xảy ra")
  } finally {
    saving.value = false
  }
}

// khóa / mở khóa nhanh ngay trên bảng, không cần mở modal sửa
async function toggleLock(item) {
  const confirmMsg = item.trangThai
    ? `Khóa tài khoản "${item.hoTen}"?`
    : `Mở khóa tài khoản "${item.hoTen}"?`

  if (!confirm(confirmMsg)) return

  try {
    const action = item.trangThai ? "khoa" : "mo-khoa"
    await axios.put(`${API_BASE}/${item.id}/${action}`, {}, { headers: authHeader() })
    await loadEmployees()
  } catch (e) {
    console.error(e)
    alert("Thao tác thất bại")
  }
}

function openDeleteModal(item) {
  currentEmployee.value = item
  showDelete.value = true
}

async function deleteEmployee() {
  deleting.value = true

  try {
    await axios.delete(`${API_BASE}/${currentEmployee.value.id}`, { headers: authHeader() })
    await loadEmployees()
    showDelete.value = false
  } catch (e) {
    console.error(e)
    alert(e.response?.data?.message || "Không thể xóa")
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
.stat-card__icon--admin{ background:rgba(255,176,32,.14); color:var(--amber-400); }
.stat-card__icon--active{ background:rgba(182,255,60,.14); color:var(--lime-300); }
.stat-card__icon--locked{ background:rgba(255,71,87,.14); color:#ff9686; }

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
}

.search{ position:relative; width:340px; }

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

.col-actions{ width:150px; }

/* Employee cell */
.employee{ display:flex; align-items:center; gap:12px; }

.avatar{
    width:38px;
    height:38px;
    min-width:38px;
    border-radius:50%;
    display:flex;
    align-items:center;
    justify-content:center;
    font-family:var(--font-display);
    font-weight:700;
    font-size:13px;
}

.avatar--admin{ background:rgba(255,176,32,.16); color:var(--amber-400); }
.avatar--staff{ background:rgba(56,189,248,.16); color:var(--sky-300); }

.employee__name{ font-weight:700; font-size:14px; color:var(--chalk-050); }
.employee__id{ color:var(--chalk-200); opacity:.55; font-size:12.5px; margin-top:1px; }

.contact__phone{ font-size:14px; font-weight:600; color:var(--chalk-050); }
.contact__email{ color:var(--chalk-200); opacity:.7; font-size:13px; margin-top:2px; }

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

.badge--admin{ background:rgba(255,176,32,.14); color:var(--amber-400); }
.badge--staff{ background:rgba(56,189,248,.14); color:var(--sky-300); }

.badge--active{ background:rgba(182,255,60,.14); color:var(--lime-300); }
.badge--locked{ background:rgba(255,71,87,.14); color:#ff9686; }

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

.icon-btn--lock{ background:rgba(255,71,87,.14); color:#ff9686; }
.icon-btn--lock:hover{ background:var(--crimson-500); color:#fff; box-shadow:0 0 12px -2px rgba(255,71,87,.6); }

.icon-btn--unlock{ background:rgba(182,255,60,.14); color:var(--lime-300); }
.icon-btn--unlock:hover{ background:var(--lime-400); color:var(--night-950); box-shadow:0 0 12px -2px rgba(182,255,60,.6); }

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
    width:460px;
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
    padding:20px 24px;
    display:flex;
    flex-direction:column;
    gap:15px;
}

.field-row{ display:grid; grid-template-columns:1fr 1fr; gap:15px; }

.field{ display:flex; flex-direction:column; gap:6px; }

.field label{ font-size:12.5px; font-weight:700; color:var(--chalk-200); opacity:.8; }

.field input,
.field select{
    padding:10px 12px;
    border:1px solid rgba(247,251,244,.14);
    border-radius:9px;
    font-family:inherit;
    font-size:14px;
    outline:none;
    background:rgba(5,11,8,.5);
    color:var(--chalk-050);
    transition:border-color .15s, box-shadow .15s;
}

.field input:focus,
.field select:focus{
    border-color:var(--lime-400);
    box-shadow:0 0 0 3px rgba(182,255,60,.15);
}

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
    .search{ width:100%; }
    .field-row{ grid-template-columns:1fr; }
    .table-card{ overflow-x:auto; }
    table{ font-size:13px; min-width:640px; }
}

@media(max-width:520px){
    .stats{ grid-template-columns:1fr 1fr; }
}
</style>