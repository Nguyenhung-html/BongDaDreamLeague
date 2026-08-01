<template>
  <div>

    <h1 class="dashboard__page-title">
      Quản lý sân bóng
    </h1>

    <p class="dashboard__page-desc">
      Quản lý danh sách sân bóng trong hệ thống.
    </p>

    <div class="toolbar">

      <input
        v-model="keyword"
        class="search-box"
        placeholder="Tìm tên sân..."
      />

      <button
        class="btn-add"
        @click="openAdd"
      >
        + Thêm sân
      </button>

    </div>

    <div class="table-wrapper">

      <table>

        <thead>

          <tr>

            <th>ID</th>
            <th>Tên sân</th>
            <th>Loại sân</th>
            <th>Giá</th>
            <th>Trạng thái</th>
            <th width="220">Thao tác</th>

          </tr>

        </thead>

        <tbody>

          <tr
            v-for="item in filteredFields"
            :key="item.id"
          >

            <td>{{ item.id }}</td>

            <td>{{ item.name }}</td>

            <td>{{ item.type }}</td>

            <td>{{ item.price }}</td>

            <td>

              <span
                class="badge success"
                v-if="item.status=='Hoạt động'"
              >
                Hoạt động
              </span>

              <span
                class="badge cancel"
                v-else
              >
                Bảo trì
              </span>

            </td>

            <td>

              <button
                class="btn-edit"
                @click="editField(item)"
              >
                Sửa
              </button>

              <button
                class="btn-delete"
                @click="deleteField(item)"
              >
                Xóa
              </button>

            </td>

          </tr>

        </tbody>

      </table>

    </div>

    <!-- Modal -->

    <div
      v-if="showModal"
      class="modal-overlay"
    >

      <div class="modal">

        <h2>
          {{ isEdit ? "Cập nhật sân" : "Thêm sân mới" }}
        </h2>

        <input
          v-model="form.name"
          placeholder="Tên sân"
        >

        <input
          v-model="form.type"
          placeholder="Loại sân"
        >

        <input
          v-model="form.price"
          placeholder="Giá thuê"
        >

        <select v-model="form.status">

          <option>Hoạt động</option>

          <option>Bảo trì</option>

        </select>

        <div class="modal-action">

          <button
            class="btn-save"
            @click="saveField"
          >
            Lưu
          </button>

          <button
            class="btn-close"
            @click="showModal=false"
          >
            Hủy
          </button>

        </div>

      </div>

    </div>

  </div>
</template>
<script setup>
import { ref, computed } from "vue"

const keyword = ref("")

const showModal = ref(false)

const isEdit = ref(false)

const form = ref({
    id:null,
    name:"",
    type:"",
    price:"",
    status:"Hoạt động"
})

const fields = ref([
    {
        id:1,
        name:"Sân 5A",
        type:"Sân 5",
        price:"300.000đ",
        status:"Hoạt động"
    },
    {
        id:2,
        name:"Sân 7A",
        type:"Sân 7",
        price:"500.000đ",
        status:"Hoạt động"
    },
    {
        id:3,
        name:"Sân 11A",
        type:"Sân 11",
        price:"900.000đ",
        status:"Bảo trì"
    }
])

const filteredFields = computed(() => {

    return fields.value.filter(item =>
        item.name.toLowerCase().includes(keyword.value.toLowerCase())
    )

})

function openAdd(){

    isEdit.value=false

    form.value={
        id:null,
        name:"",
        type:"",
        price:"",
        status:"Hoạt động"
    }

    showModal.value=true

}

function editField(item){

    isEdit.value=true

    form.value={...item}

    showModal.value=true

}

function saveField(){

    if(isEdit.value){

        const index=fields.value.findIndex(x=>x.id==form.value.id)

        fields.value[index]={...form.value}

    }else{

        fields.value.push({

            ...form.value,

            id:Date.now()

        })

    }

    showModal.value=false

}

function deleteField(item){

    if(confirm("Bạn có chắc muốn xóa sân này?")){

        fields.value=fields.value.filter(x=>x.id!=item.id)

    }

}
</script>
<style scoped>

.dashboard__page-title{
    font-size:28px;
    font-weight:700;
    margin-bottom:8px;
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
}

.search-box{
    width:300px;
    padding:10px 15px;
    border:1px solid #ddd;
    border-radius:8px;
}

.btn-add{
    background:#16a34a;
    color:#fff;
    border:none;
    padding:10px 18px;
    border-radius:8px;
    cursor:pointer;
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

th,
td{
    padding:15px;
    border-bottom:1px solid #eee;
    text-align:left;
}

th{
    background:#f8fafc;
}

.badge{
    color:white;
    padding:6px 12px;
    border-radius:20px;
    font-size:13px;
}

.success{
    background:#16a34a;
}

.cancel{
    background:#ef4444;
}

.btn-edit{
    background:#2563eb;
    color:white;
    border:none;
    padding:7px 14px;
    border-radius:6px;
    margin-right:8px;
    cursor:pointer;
}

.btn-delete{
    background:#dc2626;
    color:white;
    border:none;
    padding:7px 14px;
    border-radius:6px;
    cursor:pointer;
}

.modal-overlay{
    position:fixed;
    inset:0;
    background:rgba(0,0,0,.45);
    display:flex;
    justify-content:center;
    align-items:center;
}

.modal{
    width:420px;
    background:#fff;
    border-radius:12px;
    padding:25px;
}

.modal input,
.modal select{
    width:100%;
    padding:10px;
    margin-top:12px;
    border:1px solid #ddd;
    border-radius:8px;
}

.modal-action{
    margin-top:20px;
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

.btn-close{
    background:#e5e7eb;
    border:none;
    padding:10px 18px;
    border-radius:8px;
    cursor:pointer;
}

</style>