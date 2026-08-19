<template>
  <div class="page">

    <!-- Header -->
    <header class="page__header">
      <h1 class="page__title">Quản lý nhân viên</h1>
      <p class="page__desc">
        Quản lý tài khoản nhân viên của hệ thống <span class="brand">Dream League</span>
      </p>
    </header>

    <!-- Stat strip -->
    <section class="stats">
      <div class="stat-card">
        <div class="stat-card__icon stat-card__icon--total">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H7a4 4 0 0 0-4 4v2"/><circle cx="10" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
        </div>
        <div>
          <p class="stat-card__value">{{ stats.total }}</p>
          <p class="stat-card__label">Tổng nhân viên</p>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-card__icon stat-card__icon--admin">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2l8 4v6c0 5-3.5 8.5-8 10-4.5-1.5-8-5-8-10V6l8-4z"/></svg>
        </div>
        <div>
          <p class="stat-card__value">{{ stats.admin }}</p>
          <p class="stat-card__label">Quản trị viên</p>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-card__icon stat-card__icon--active">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/><path d="M8.5 12.5l2.5 2.5 5-5"/></svg>
        </div>
        <div>
          <p class="stat-card__value">{{ stats.active }}</p>
          <p class="stat-card__label">Đang hoạt động</p>
        </div>
      </div>

      <div class="stat-card">
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
    <div class="toolbar">
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
    <div class="table-card">

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

    

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue"
import axios from "axios"

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

onMounted(() => {
    loadEmployees()
})

async function loadEmployees() {
    loading.value = true
    try {
        const res = await axios.get(
            "http://localhost:8080/api/admin/nhan-vien",
            {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem("token")}`
                }
            }
        )

        employees.value = res.data

    } catch (e) {
        console.error(e)
    } finally {
        loading.value = false
    }
}

function openAddModal(){

  isEdit.value=false

 form.value = {
    id:null,
    hoTen:"",
    soDienThoai:"",
    email:"",
    matKhau:"",
    diaChi:"",
    vaiTro:"STAFF",
    trangThai:true
}

  showModal.value=true
}

function openEditModal(item){

  isEdit.value=true

  form.value={...item}

  showModal.value=true
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
                `http://localhost:8080/api/admin/nhan-vien/${form.value.id}`,
                {
                    hoTen: form.value.hoTen,
                    email: form.value.email,
                    soDienThoai: form.value.soDienThoai,
                    diaChi: form.value.diaChi,
                    vaiTro: form.value.vaiTro
                },
                {
                    headers: {
                        Authorization: `Bearer ${localStorage.getItem("token")}`
                    }
                }
            )

        } else {

            await axios.post(
                "http://localhost:8080/api/admin/nhan-vien",
                {
                    hoTen: form.value.hoTen,
                    email: form.value.email,
                    soDienThoai: form.value.soDienThoai,
                    matKhau: form.value.matKhau,
                    diaChi: form.value.diaChi,
                    vaiTro: form.value.vaiTro
                },
                {
                    headers: {
                        Authorization: `Bearer ${localStorage.getItem("token")}`
                    }
                }
            )

        }
if (form.value.trangThai) {

    await axios.put(
        `http://localhost:8080/api/admin/nhan-vien/${form.value.id}/mo-khoa`,
        {},
        {
            headers: {
                Authorization: `Bearer ${localStorage.getItem("token")}`
            }
        }
    )

} else {

    await axios.put(
        `http://localhost:8080/api/admin/nhan-vien/${form.value.id}/khoa`,
        {},
        {
            headers: {
                Authorization: `Bearer ${localStorage.getItem("token")}`
            }
        }
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

function openDeleteModal(item){

  currentEmployee.value=item

  showDelete.value=true
}

async function deleteEmployee() {

    deleting.value = true

    try {

        await axios.delete(
            `http://localhost:8080/api/admin/nhan-vien/${currentEmployee.value.id}`,
            {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem("token")}`
                }
            }
        )

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
@import url('https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@500;600;700&family=Inter:wght@400;500;600;700&display=swap');

.page{
    --bg:#f5f7fa;
    --surface:#ffffff;
    --surface-alt:#f8fafc;
    --border:#e6eaf0;
    --ink:#0f172a;
    --ink-soft:#64748b;
    --ink-faint:#94a3b8;

    --primary:#16a34a;
    --primary-dark:#15803d;
    --primary-soft:#e8f8ee;

    --admin:#b45309;
    --admin-soft:#fef3c7;

    --staff:#1d4ed8;
    --staff-soft:#e0e9ff;

    --active:#15803d;
    --active-soft:#dcfce7;

    --locked:#c2410c;
    --locked-soft:#ffedd5;

    --danger:#dc2626;
    --danger-dark:#b91c1c;
    --danger-soft:#fee2e2;

    --radius:14px;
    --radius-sm:9px;
    --shadow:0 1px 2px rgba(15,23,42,.04), 0 8px 24px rgba(15,23,42,.06);

    font-family:'Inter',sans-serif;
    color:var(--ink);
}

/* Header */

.page__title{
    font-family:'Space Grotesk',sans-serif;
    font-size:30px;
    font-weight:700;
    letter-spacing:-.01em;
    margin-bottom:6px;
}

.page__desc{
    color:var(--ink-soft);
    font-size:15px;
    margin-bottom:26px;
}

.brand{
    color:var(--primary-dark);
    font-weight:600;
}

/* Stats */

.stats{
    display:grid;
    grid-template-columns:repeat(4,1fr);
    gap:16px;
    margin-bottom:22px;
}

.stat-card{
    background:var(--surface);
    border:1px solid var(--border);
    border-radius:var(--radius);
    padding:18px 20px;
    display:flex;
    align-items:center;
    gap:14px;
    box-shadow:var(--shadow);
}

.stat-card__icon{
    width:42px;
    height:42px;
    min-width:42px;
    border-radius:11px;
    display:flex;
    align-items:center;
    justify-content:center;
}

.stat-card__icon--total{ background:var(--staff-soft); color:var(--staff); }
.stat-card__icon--admin{ background:var(--admin-soft); color:var(--admin); }
.stat-card__icon--active{ background:var(--active-soft); color:var(--active); }
.stat-card__icon--locked{ background:var(--locked-soft); color:var(--locked); }

.stat-card__value{
    font-family:'Space Grotesk',sans-serif;
    font-size:22px;
    font-weight:700;
    line-height:1.1;
}

.stat-card__label{
    color:var(--ink-soft);
    font-size:13px;
    margin-top:2px;
}

/* Toolbar */

.toolbar{
    display:flex;
    justify-content:space-between;
    align-items:center;
    gap:16px;
    margin-bottom:18px;
}

.search{
    position:relative;
    width:340px;
}

.search__icon{
    position:absolute;
    left:14px;
    top:50%;
    transform:translateY(-50%);
    color:var(--ink-faint);
}

.search input{
    width:100%;
    padding:11px 14px 11px 42px;
    border:1px solid var(--border);
    border-radius:var(--radius-sm);
    background:var(--surface);
    font-family:inherit;
    font-size:14px;
    outline:none;
    transition:border-color .15s, box-shadow .15s;
}

.search input:focus{
    border-color:var(--primary);
    box-shadow:0 0 0 3px var(--primary-soft);
}

.btn{
    display:inline-flex;
    align-items:center;
    gap:8px;
    border:none;
    border-radius:var(--radius-sm);
    padding:11px 20px;
    font-family:inherit;
    font-size:14px;
    font-weight:600;
    cursor:pointer;
    transition:background .15s, transform .05s, opacity .15s;
}

.btn:active{ transform:scale(.98); }
.btn:disabled{ opacity:.6; cursor:not-allowed; }

.btn--primary{ background:var(--primary); color:#fff; }
.btn--primary:hover:not(:disabled){ background:var(--primary-dark); }

.btn--ghost{ background:var(--surface-alt); color:var(--ink); }
.btn--ghost:hover{ background:var(--border); }

.btn--danger{ background:var(--danger); color:#fff; }
.btn--danger:hover:not(:disabled){ background:var(--danger-dark); }

/* Table */

.table-card{
    background:var(--surface);
    border:1px solid var(--border);
    border-radius:var(--radius);
    box-shadow:var(--shadow);
    overflow:hidden;
}

table{
    width:100%;
    border-collapse:collapse;
}

th{
    background:var(--surface-alt);
    padding:14px 18px;
    text-align:left;
    font-size:12.5px;
    font-weight:600;
    text-transform:uppercase;
    letter-spacing:.04em;
    color:var(--ink-soft);
    border-bottom:1px solid var(--border);
}

td{
    padding:14px 18px;
    border-bottom:1px solid var(--border);
    vertical-align:middle;
}

tbody tr:last-child td{ border-bottom:none; }
tbody tr:hover{ background:var(--surface-alt); }

.col-actions{ width:110px; }

/* Employee cell */

.employee{
    display:flex;
    align-items:center;
    gap:12px;
}

.avatar{
    width:38px;
    height:38px;
    min-width:38px;
    border-radius:50%;
    display:flex;
    align-items:center;
    justify-content:center;
    font-family:'Space Grotesk',sans-serif;
    font-weight:700;
    font-size:13px;
}

.avatar--admin{ background:var(--admin-soft); color:var(--admin); }
.avatar--staff{ background:var(--staff-soft); color:var(--staff); }

.employee__name{ font-weight:600; font-size:14px; }
.employee__id{ color:var(--ink-faint); font-size:12.5px; margin-top:1px; }

.contact__phone{ font-size:14px; font-weight:500; }
.contact__email{ color:var(--ink-soft); font-size:13px; margin-top:2px; }

/* Badges */

.badge{
    display:inline-flex;
    align-items:center;
    gap:6px;
    padding:5px 12px;
    border-radius:20px;
    font-size:12.5px;
    font-weight:600;
}

.badge--admin{ background:var(--admin-soft); color:var(--admin); }
.badge--staff{ background:var(--staff-soft); color:var(--staff); }

.badge__dot{
    width:6px;
    height:6px;
    border-radius:50%;
    background:currentColor;
}

.badge--active{ background:var(--active-soft); color:var(--active); }
.badge--locked{ background:var(--locked-soft); color:var(--locked); }

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
    transition:background .15s, color .15s;
}

.icon-btn--edit{ background:var(--staff-soft); color:var(--staff); }
.icon-btn--edit:hover{ background:var(--staff); color:#fff; }

.icon-btn--delete{ background:var(--danger-soft); color:var(--danger); }
.icon-btn--delete:hover{ background:var(--danger); color:#fff; }

/* Loading / empty state */

.state{
    display:flex;
    flex-direction:column;
    align-items:center;
    justify-content:center;
    gap:12px;
    padding:64px 20px;
    color:var(--ink-faint);
    font-size:14px;
}

.spinner{
    width:30px;
    height:30px;
    border:3px solid var(--border);
    border-top-color:var(--primary);
    border-radius:50%;
    animation:spin .7s linear infinite;
}

@keyframes spin{ to{ transform:rotate(360deg); } }

/* Modal */

.overlay{
    position:fixed;
    inset:0;
    background:rgba(15,23,42,.5);
    backdrop-filter:blur(2px);
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
    background:var(--surface);
    border-radius:16px;
    box-shadow:0 20px 60px rgba(15,23,42,.25);
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
    border-bottom:1px solid var(--border);
}

.modal__header h2{
    font-family:'Space Grotesk',sans-serif;
    font-size:18px;
    font-weight:700;
}

.modal__header p{
    color:var(--ink-soft);
    font-size:13px;
    margin-top:2px;
}

.modal__icon{
    width:40px;
    height:40px;
    min-width:40px;
    border-radius:10px;
    background:var(--primary-soft);
    color:var(--primary-dark);
    display:flex;
    align-items:center;
    justify-content:center;
}

.modal--sm .modal__icon{ margin:0 auto; }

.modal__icon--danger{
    width:52px;
    height:52px;
    border-radius:50%;
    background:var(--danger-soft);
    color:var(--danger);
}

.modal--sm h2{
    font-family:'Space Grotesk',sans-serif;
    font-size:18px;
    font-weight:700;
    margin:18px 0 8px;
}

.modal__text{
    color:var(--ink-soft);
    font-size:14px;
    line-height:1.5;
}

.modal__close{
    margin-left:auto;
    width:30px;
    height:30px;
    border:none;
    background:var(--surface-alt);
    border-radius:8px;
    color:var(--ink-soft);
    display:flex;
    align-items:center;
    justify-content:center;
    cursor:pointer;
}

.modal__close:hover{ background:var(--border); color:var(--ink); }

.modal__body{
    padding:20px 24px;
    display:flex;
    flex-direction:column;
    gap:15px;
}

.field-row{
    display:grid;
    grid-template-columns:1fr 1fr;
    gap:15px;
}

.field{
    display:flex;
    flex-direction:column;
    gap:6px;
}

.field label{
    font-size:12.5px;
    font-weight:600;
    color:var(--ink-soft);
}

.field input,
.field select{
    padding:10px 12px;
    border:1px solid var(--border);
    border-radius:var(--radius-sm);
    font-family:inherit;
    font-size:14px;
    outline:none;
    background:var(--surface);
    transition:border-color .15s, box-shadow .15s;
}

.field input:focus,
.field select:focus{
    border-color:var(--primary);
    box-shadow:0 0 0 3px var(--primary-soft);
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