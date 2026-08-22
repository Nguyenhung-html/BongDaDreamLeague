<template>
  <div>

    <div class="page-header">
      <div>
        <h1 class="dashboard__page-title">Quản lý thông báo</h1>
        <p class="dashboard__page-desc">
          Tạo và quản lý thông báo gửi tới nhân viên và người dùng.
        </p>
      </div>
      <button class="btn-add" @click="openCreateModal">+ Tạo thông báo mới</button>
    </div>

    <div v-if="loading" class="empty-row">Đang tải dữ liệu...</div>

    <template v-else>

      <!-- Bảng dành cho Nhân viên -->
      <section class="tb-section">
        <h2 class="tb-section__title">Thông báo dành cho Nhân viên</h2>
        <div class="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>Tiêu đề</th>
                <th>Đối tượng</th>
                <th>Ngày bắt đầu</th>
                <th>Ngày kết thúc</th>
                <th>Trạng thái</th>
                <th width="280">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="staffList.length === 0">
                <td colspan="6" class="empty-row">Chưa có thông báo nào cho nhân viên.</td>
              </tr>
              <tr v-for="tb in staffList" :key="tb.id">
                <td>{{ tb.tieuDe }}</td>
                <td><span class="badge-target">{{ nhanDoiTuong(tb.doiTuongNhan) }}</span></td>
                <td>{{ formatDate(tb.ngayBatDau) }}</td>
                <td>{{ formatDate(tb.ngayKetThuc) }}</td>
                <td>
                  <span class="status active" v-if="tb.hienThi">Hiển thị</span>
                  <span class="status lock" v-else>Đã ẩn</span>
                </td>
                <td class="action-cell">
                  <button class="btn-edit" @click="openEditModal(tb)">Sửa</button>
                  <button v-if="tb.hienThi" class="btn-hide" @click="toggleHienThi(tb)">Ẩn</button>
                  <button v-else class="btn-show" @click="toggleHienThi(tb)">Hiện</button>
                  <button class="btn-remove" @click="xoaThongBao(tb)">Xóa</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      <!-- Bảng dành cho Người dùng -->
      <section class="tb-section">
        <h2 class="tb-section__title">Thông báo dành cho Người dùng</h2>
        <div class="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>Tiêu đề</th>
                <th>Đối tượng</th>
                <th>Ngày bắt đầu</th>
                <th>Ngày kết thúc</th>
                <th>Trạng thái</th>
                <th width="280">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="userList.length === 0">
                <td colspan="6" class="empty-row">Chưa có thông báo nào cho người dùng.</td>
              </tr>
              <tr v-for="tb in userList" :key="tb.id">
                <td>{{ tb.tieuDe }}</td>
                <td><span class="badge-target">{{ nhanDoiTuong(tb.doiTuongNhan) }}</span></td>
                <td>{{ formatDate(tb.ngayBatDau) }}</td>
                <td>{{ formatDate(tb.ngayKetThuc) }}</td>
                <td>
                  <span class="status active" v-if="tb.hienThi">Hiển thị</span>
                  <span class="status lock" v-else>Đã ẩn</span>
                </td>
                <td class="action-cell">
                  <button class="btn-edit" @click="openEditModal(tb)">Sửa</button>
                  <button v-if="tb.hienThi" class="btn-hide" @click="toggleHienThi(tb)">Ẩn</button>
                  <button v-else class="btn-show" @click="toggleHienThi(tb)">Hiện</button>
                  <button class="btn-remove" @click="xoaThongBao(tb)">Xóa</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

    </template>

    <!-- Modal Tạo / Sửa -->
    <div
      v-if="showModal"
      class="modal-overlay"
      @click.self="showModal = false"
    >
      <div class="modal">

        <h2>{{ isEdit ? 'Cập nhật thông báo' : 'Tạo thông báo mới' }}</h2>

        <label class="field-label">Tiêu đề</label>
        <input v-model="form.tieuDe" placeholder="Nhập tiêu đề thông báo" />

        <label class="field-label">Nội dung</label>
        <textarea v-model="form.noiDung" rows="4" placeholder="Nhập nội dung thông báo"></textarea>

        <label class="field-label">Đối tượng nhận</label>
        <select v-model="form.doiTuongNhan">
          <option value="TAT_CA">Tất cả</option>
          <option value="USER">Người dùng</option>
          <option value="STAFF">Nhân viên</option>
        </select>

        <div class="field-row">
          <div>
            <label class="field-label">Ngày bắt đầu</label>
            <input type="date" v-model="form.ngayBatDau" />
          </div>
          <div>
            <label class="field-label">Ngày kết thúc</label>
            <input type="date" v-model="form.ngayKetThuc" />
          </div>
        </div>

        <label class="field-checkbox" v-if="!isEdit">
          <input type="checkbox" v-model="form.hienThi" />
          Hiển thị ngay sau khi tạo
        </label>

        <div class="modal-action">
          <button class="btn-cancel" @click="showModal = false">Hủy</button>
          <button class="btn-save" @click="saveThongBao">
            {{ isEdit ? 'Cập nhật' : 'Tạo mới' }}
          </button>
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

function formatDate(dateStr) {
    if (!dateStr) return '—'
    const d = new Date(dateStr)
    if (isNaN(d)) return dateStr
    return d.toLocaleDateString('vi-VN')
}
</script>

<style scoped>

.page-header{
    display:flex;
    justify-content:space-between;
    align-items:flex-start;
    margin-bottom:25px;
    gap:20px;
}

.dashboard__page-title{
    font-size:28px;
    font-weight:700;
    color:#1e293b;
    margin-bottom:5px;
}

.dashboard__page-desc{
    color:#64748b;
}

.btn-add{
    background:#16a34a;
    color:white;
    border:none;
    padding:11px 18px;
    border-radius:8px;
    cursor:pointer;
    font-weight:600;
    white-space:nowrap;
}

.btn-add:hover{
    background:#15803d;
}

.tb-section{
    margin-bottom:30px;
}

.tb-section__title{
    font-size:16px;
    font-weight:700;
    color:#1e293b;
    margin-bottom:12px;
}

.table-wrapper{
    background:#fff;
    border-radius:12px;
    overflow:hidden;
    box-shadow:0 2px 10px rgba(0,0,0,.08);
}

table{
    width:100%;
    border-collapse:collapse;
}

th{
    background:#f8fafc;
    padding:15px;
    text-align:left;
    font-weight:600;
    color:#334155;
}

td{
    padding:15px;
    border-top:1px solid #eef2f7;
}

tr:hover{
    background:#fafafa;
}

.empty-row{
    text-align:center;
    padding:30px;
    color:#94a3b8;
}

.badge-target{
    padding:4px 10px;
    border-radius:999px;
    font-size:12px;
    font-weight:600;
    background:#eef2ff;
    color:#4338ca;
}

.status{
    padding:6px 14px;
    border-radius:30px;
    font-size:13px;
    font-weight:600;
}

.active{
    background:#dcfce7;
    color:#15803d;
}

.lock{
    background:#fee2e2;
    color:#dc2626;
}

.action-cell{
    display:flex;
    flex-wrap:wrap;
    gap:8px;
}

.btn-edit{
    background:#2563eb;
    color:white;
    border:none;
    padding:8px 14px;
    border-radius:6px;
    cursor:pointer;
}

.btn-edit:hover{
    background:#1d4ed8;
}

.btn-hide{
    background:#f59e0b;
    color:white;
    border:none;
    padding:8px 14px;
    border-radius:6px;
    cursor:pointer;
}

.btn-hide:hover{
    background:#d97706;
}

.btn-show{
    background:#16a34a;
    color:white;
    border:none;
    padding:8px 14px;
    border-radius:6px;
    cursor:pointer;
}

.btn-show:hover{
    background:#15803d;
}

.btn-remove{
    background:#ef4444;
    color:white;
    border:none;
    padding:8px 14px;
    border-radius:6px;
    cursor:pointer;
}

.btn-remove:hover{
    background:#dc2626;
}

/* Modal */

.modal-overlay{
    position:fixed;
    inset:0;
    background:rgba(0,0,0,.45);
    display:flex;
    justify-content:center;
    align-items:center;
    z-index:999;
}

.modal{
    width:460px;
    background:white;
    border-radius:12px;
    padding:25px;
}

.modal h2{
    margin-bottom:20px;
    font-size:22px;
}

.field-label{
    display:block;
    font-size:13px;
    font-weight:600;
    color:#475569;
    margin-bottom:6px;
}

.modal input,
.modal select,
.modal textarea{
    width:100%;
    padding:11px;
    margin-bottom:15px;
    border:1px solid #ddd;
    border-radius:8px;
    font-size:14px;
    font-family:inherit;
    resize:vertical;
}

.modal input:focus,
.modal select:focus,
.modal textarea:focus{
    outline:none;
    border-color:#16a34a;
}

.field-row{
    display:flex;
    gap:12px;
}

.field-row > div{
    flex:1;
}

.field-checkbox{
    display:flex;
    align-items:center;
    gap:8px;
    font-size:13.5px;
    color:#475569;
    margin-bottom:15px;
}

.field-checkbox input{
    width:auto;
    margin:0;
}

.modal-action{
    display:flex;
    justify-content:flex-end;
    gap:10px;
    margin-top:10px;
}

.btn-save{
    background:#16a34a;
    color:white;
    border:none;
    padding:10px 18px;
    border-radius:8px;
    cursor:pointer;
}

.btn-save:hover{
    background:#15803d;
}

.btn-cancel{
    background:#e5e7eb;
    border:none;
    padding:10px 18px;
    border-radius:8px;
    cursor:pointer;
}

.btn-cancel:hover{
    background:#d1d5db;
}

@media(max-width:768px){

.page-header{
    flex-direction:column;
}

table{
    font-size:13px;
}

.modal{
    width:95%;
}

.field-row{
    flex-direction:column;
    gap:0;
}

}

</style>