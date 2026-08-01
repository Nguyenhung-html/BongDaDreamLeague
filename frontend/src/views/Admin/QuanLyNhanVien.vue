<template>
  <div>

    <h1 class="dashboard__page-title">
      Quản lý nhân viên
    </h1>

    <p class="dashboard__page-desc">
      Quản lý tài khoản nhân viên của hệ thống Dream League.
    </p>

    <!-- Toolbar -->

    <div class="toolbar">

      <input
        type="text"
        class="search-box"
        placeholder="Tìm theo tên hoặc số điện thoại..."
        v-model="keyword"
      />

      <button
        class="btn-add"
        @click="openAddModal"
      >
        + Thêm nhân viên
      </button>

    </div>

    <!-- Table -->

    <div class="table-wrapper">

      <table>

        <thead>

          <tr>

            <th>ID</th>

            <th>Họ tên</th>

            <th>SĐT</th>

            <th>Email</th>

            <th>Chức vụ</th>

            <th>Trạng thái</th>

            <th width="220">
              Thao tác
            </th>

          </tr>

        </thead>

        <tbody>

          <tr
            v-for="item in filteredEmployees"
            :key="item.id"
          >

            <td>{{ item.id }}</td>

            <td>{{ item.name }}</td>

            <td>{{ item.phone }}</td>

            <td>{{ item.email }}</td>

            <td>

              <span
                class="role admin"
                v-if="item.role=='Admin'"
              >
                Admin
              </span>

              <span
                class="role staff"
                v-else
              >
                Staff
              </span>

            </td>

            <td>

              <span
                class="status active"
                v-if="item.status"
              >
                Đang làm
              </span>

              <span
                class="status lock"
                v-else
              >
                Nghỉ việc
              </span>

            </td>

            <td>

              <button
                class="btn-edit"
                @click="openEditModal(item)"
              >
                Sửa
              </button>

              <button
                class="btn-delete"
                @click="openDeleteModal(item)"
              >
                Xóa
              </button>

            </td>

          </tr>

        </tbody>

      </table>

    </div>

    <!-- Modal Thêm/Sửa -->

    <div
      v-if="showModal"
      class="modal-overlay"
    >

      <div class="modal">

        <h2>

          {{ isEdit ? 'Cập nhật nhân viên' : 'Thêm nhân viên' }}

        </h2>

        <input
          v-model="form.name"
          placeholder="Họ tên"
        >

        <input
          v-model="form.phone"
          placeholder="Số điện thoại"
        >

        <input
          v-model="form.email"
          placeholder="Email"
        >

        <select
          v-model="form.role"
        >

          <option>Staff</option>

          <option>Admin</option>

        </select>

        <select
          v-model="form.status"
        >

          <option :value="true">
            Đang làm
          </option>

          <option :value="false">
            Nghỉ việc
          </option>

        </select>

        <div class="modal-action">

          <button
            class="btn-cancel"
            @click="showModal=false"
          >
            Hủy
          </button>

          <button
            class="btn-save"
            @click="saveEmployee"
          >
            {{ isEdit ? 'Cập nhật' : 'Thêm mới' }}
          </button>

        </div>

      </div>

    </div>

    <!-- Modal Xóa -->

    <div
      v-if="showDelete"
      class="modal-overlay"
    >

      <div class="modal delete-modal">

        <h2>Xóa nhân viên</h2>

        <p>

          Bạn có chắc muốn xóa

          <b>{{ currentEmployee?.name }}</b>

          ?

        </p>

        <div class="modal-action">

          <button
            class="btn-cancel"
            @click="showDelete=false"
          >
            Hủy
          </button>

          <button
            class="btn-delete"
            @click="deleteEmployee"
          >
            Xóa
          </button>

        </div>

      </div>

    </div>

  </div>
</template>
<script setup>
import { ref, computed } from 'vue'

const keyword = ref('')

const showModal = ref(false)
const showDelete = ref(false)
const isEdit = ref(false)

const currentEmployee = ref(null)

const employees = ref([
  {
    id:1,
    name:'Nguyễn Văn An',
    phone:'0987654321',
    email:'an@gmail.com',
    role:'Admin',
    status:true
  },
  {
    id:2,
    name:'Trần Văn Bình',
    phone:'0911222333',
    email:'binh@gmail.com',
    role:'Staff',
    status:true
  },
  {
    id:3,
    name:'Lê Thị Hương',
    phone:'0909888777',
    email:'huong@gmail.com',
    role:'Staff',
    status:false
  }
])

const form = ref({
  id:null,
  name:'',
  phone:'',
  email:'',
  role:'Staff',
  status:true
})

const filteredEmployees = computed(()=>{
  return employees.value.filter(item=>{

    return (
      item.name.toLowerCase().includes(keyword.value.toLowerCase()) ||

      item.phone.includes(keyword.value)
    )

  })
})

function openAddModal(){

  isEdit.value=false

  form.value={
    id:null,
    name:'',
    phone:'',
    email:'',
    role:'Staff',
    status:true
  }

  showModal.value=true
}

function openEditModal(item){

  isEdit.value=true

  form.value={...item}

  showModal.value=true
}

function saveEmployee(){

  if(
    form.value.name=='' ||
    form.value.phone=='' ||
    form.value.email==''
  ){
    alert('Vui lòng nhập đầy đủ thông tin!')
    return
  }

  if(isEdit.value){

    const index=employees.value.findIndex(x=>x.id==form.value.id)

    employees.value[index]={...form.value}

  }else{

    form.value.id=employees.value.length+1

    employees.value.push({...form.value})

  }

  showModal.value=false
}

function openDeleteModal(item){

  currentEmployee.value=item

  showDelete.value=true
}

function deleteEmployee(){

  employees.value=employees.value.filter(
    x=>x.id!=currentEmployee.value.id
  )

  showDelete.value=false
}
</script>
<style scoped>

.dashboard__page-title{
    font-size:28px;
    font-weight:700;
    color:#1e293b;
    margin-bottom:5px;
}

.dashboard__page-desc{
    color:#64748b;
    margin-bottom:25px;
}

.toolbar{
    display:flex;
    justify-content:space-between;
    align-items:center;
    margin-bottom:20px;
    gap:20px;
}

.search-box{
    width:320px;
    padding:11px 15px;
    border:1px solid #dbe2ea;
    border-radius:8px;
    outline:none;
}

.search-box:focus{
    border-color:#16a34a;
}

.btn-add{
    background:#16a34a;
    color:white;
    border:none;
    padding:11px 20px;
    border-radius:8px;
    cursor:pointer;
    font-weight:600;
}

.btn-add:hover{
    background:#15803d;
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
    color:#334155;
}

td{
    padding:15px;
    border-top:1px solid #eef2f7;
}

tr:hover{
    background:#fafafa;
}

/* ROLE */

.role{
    padding:6px 12px;
    border-radius:20px;
    font-size:13px;
    font-weight:600;
}

.admin{
    background:#fee2e2;
    color:#dc2626;
}

.staff{
    background:#dbeafe;
    color:#2563eb;
}

/* STATUS */

.status{
    padding:6px 12px;
    border-radius:20px;
    font-size:13px;
    font-weight:600;
}

.active{
    background:#dcfce7;
    color:#15803d;
}

.lock{
    background:#fef3c7;
    color:#d97706;
}

/* BUTTON */

.btn-edit{
    background:#2563eb;
    color:white;
    border:none;
    padding:8px 14px;
    border-radius:6px;
    cursor:pointer;
    margin-right:8px;
}

.btn-edit:hover{
    background:#1d4ed8;
}

.btn-delete{
    background:#ef4444;
    color:white;
    border:none;
    padding:8px 14px;
    border-radius:6px;
    cursor:pointer;
}

.btn-delete:hover{
    background:#dc2626;
}

/* MODAL */

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
    width:430px;
    background:white;
    border-radius:12px;
    padding:25px;
}

.modal h2{
    margin-bottom:20px;
}

.modal input,
.modal select{
    width:100%;
    padding:11px;
    margin-bottom:15px;
    border:1px solid #ddd;
    border-radius:8px;
}

.modal input:focus,
.modal select:focus{
    outline:none;
    border-color:#16a34a;
}

.modal-action{
    display:flex;
    justify-content:flex-end;
    gap:10px;
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

.delete-modal{
    width:380px;
    text-align:center;
}

.delete-modal p{
    margin:20px 0;
    color:#475569;
}

@media(max-width:768px){

.toolbar{
    flex-direction:column;
    align-items:stretch;
}

.search-box{
    width:100%;
}

.modal{
    width:95%;
}

table{
    font-size:13px;
}

}

</style>