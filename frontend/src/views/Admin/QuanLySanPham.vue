<template>
  <div>
    <h1 class="dashboard__page-title">Quản lý sản phẩm</h1>
    <p class="dashboard__page-desc">Quản lý danh sách đồ uống, dịch vụ khách có thể gọi khi đến sân.</p>

    <div class="toolbar">
      <input v-model="tuKhoa" class="search-box" placeholder="Tìm sản phẩm..." />
      <select v-model="locLoai" class="filter-box">
        <option value="">Tất cả loại</option>
        <option value="DO_UONG">Đồ uống</option>
        <option value="DICH_VU">Dịch vụ</option>
      </select>
      <button class="btn-add" @click="moFormThem">+ Thêm sản phẩm</button>
    </div>

    <div v-if="dangTai" class="empty-state">
      <p>Đang tải danh sách sản phẩm...</p>
    </div>

    <div v-else-if="danhSachLoc.length === 0" class="empty-state">
      <div class="empty-state__icon">
        <svg width="32" height="32" viewBox="0 0 24 24" fill="none"><rect x="3" y="3" width="8" height="8" rx="1" stroke="currentColor" stroke-width="1.6"/><rect x="13" y="3" width="8" height="8" rx="1" stroke="currentColor" stroke-width="1.6"/></svg>
      </div>
      <h2 class="empty-state__title">Chưa có sản phẩm nào</h2>
      <p class="empty-state__desc">Bấm "+ Thêm sản phẩm" để thêm đồ uống hoặc dịch vụ đầu tiên vào hệ thống.</p>
    </div>

    <div v-else class="table-wrapper">
      <table>
        <thead>
          <tr>
            <th>Hình</th>
            <th>Tên sản phẩm</th>
            <th>Loại</th>
            <th>Giá</th>
            <th>Trạng thái</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="sp in danhSachLoc" :key="sp.id">
            <td><img :src="sp.hinhAnh || anhMacDinh" class="sp-anh" alt="" /></td>
            <td class="sp-ten">{{ sp.tenSanPham }}</td>
            <td>
              <span class="loai-tag" :class="sp.loai === 'DO_UONG' ? 'loai-tag--uong' : 'loai-tag--dv'">
                {{ sp.loai === 'DO_UONG' ? 'Đồ uống' : 'Dịch vụ' }}
              </span>
            </td>
            <td class="sp-gia">{{ formatTien(sp.gia) }}đ</td>
            <td>
              <span class="trang-thai" :class="sp.conBan ? 'trang-thai--con' : 'trang-thai--het'">
                {{ sp.conBan ? 'Còn bán' : 'Ngừng bán' }}
              </span>
            </td>
            <td class="cot-thao-tac">
              <button class="btn-ghost-gray btn-nho" @click="moFormSua(sp)">Sửa</button>
              <button class="btn-danger btn-nho" @click="xoaSanPham(sp)">Xoá</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal thêm/sửa -->
    <div v-if="formMoRa" class="modal-overlay" @click.self="dongForm">
      <div class="modal">
        <h3>{{ dangSua ? 'Sửa sản phẩm' : 'Thêm sản phẩm mới' }}</h3>

        <div class="field">
          <label>Tên sản phẩm</label>
          <input v-model="form.tenSanPham" placeholder="VD: Sting dâu" />
        </div>
        <div class="field">
          <label>Giá (VNĐ)</label>
          <input v-model.number="form.gia" type="number" min="0" placeholder="VD: 15000" />
        </div>
        <div class="field">
          <label>Link hình ảnh (không bắt buộc)</label>
          <input v-model="form.hinhAnh" placeholder="https://..." />
        </div>
        <div class="field">
          <label>Mô tả (không bắt buộc)</label>
          <textarea v-model="form.moTa" rows="2" placeholder="Ghi chú thêm về sản phẩm..."></textarea>
        </div>
        <div class="field">
          <label>Loại</label>
          <select v-model="form.loai">
            <option value="DO_UONG">Đồ uống</option>
            <option value="DICH_VU">Dịch vụ</option>
          </select>
        </div>
        <label class="field field--checkbox">
          <input type="checkbox" v-model="form.conBan" />
          Còn bán (hiện cho khách gọi)
        </label>

        <p v-if="loiForm" class="loi-form">{{ loiForm }}</p>

        <div class="modal-actions">
          <button class="btn-ghost-gray" @click="dongForm">Huỷ</button>
          <button class="btn-primary" @click="luuSanPham" :disabled="dangLuu">
            {{ dangLuu ? 'Đang lưu...' : 'Lưu' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const API = 'http://localhost:8080/api'
const anhMacDinh = 'https://images.unsplash.com/photo-1544145945-f90425340c7e?w=200'

const dangTai = ref(true)
const danhSach = ref([])
const tuKhoa = ref('')
const locLoai = ref('')

const formMoRa = ref(false)
const dangSua = ref(false)
const dangLuu = ref(false)
const loiForm = ref('')
const idDangSua = ref(null)
const form = ref({ tenSanPham: '', gia: 0, hinhAnh: '', moTa: '', loai: 'DO_UONG', conBan: true })

const danhSachLoc = computed(() => {
  return danhSach.value.filter(sp => {
    const khopTen = sp.tenSanPham.toLowerCase().includes(tuKhoa.value.trim().toLowerCase())
    const khopLoai = !locLoai.value || sp.loai === locLoai.value
    return khopTen && khopLoai
  })
})

function formatTien(so) { return Number(so).toLocaleString('vi-VN') }

async function taiDanhSach() {
  dangTai.value = true
  const token = localStorage.getItem('token')
  try {
    const res = await fetch(`${API}/san-pham/tat-ca`, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    if (!res.ok) throw new Error('Không tải được danh sách sản phẩm')
    danhSach.value = await res.json()
  } catch (e) {
    danhSach.value = []
  } finally {
    dangTai.value = false
  }
}

function moFormThem() {
  dangSua.value = false
  idDangSua.value = null
  form.value = { tenSanPham: '', gia: 0, hinhAnh: '', moTa: '', loai: 'DO_UONG', conBan: true }
  loiForm.value = ''
  formMoRa.value = true
}

function moFormSua(sp) {
  dangSua.value = true
  idDangSua.value = sp.id
  form.value = {
    tenSanPham: sp.tenSanPham,
    gia: sp.gia,
    hinhAnh: sp.hinhAnh || '',
    moTa: sp.moTa || '',
    loai: sp.loai,
    conBan: sp.conBan
  }
  loiForm.value = ''
  formMoRa.value = true
}

function dongForm() {
  formMoRa.value = false
}

async function luuSanPham() {
  loiForm.value = ''
  if (!form.value.tenSanPham.trim()) { loiForm.value = 'Vui lòng nhập tên sản phẩm!'; return }
  if (!form.value.gia || form.value.gia <= 0) { loiForm.value = 'Giá sản phẩm không hợp lệ!'; return }

  dangLuu.value = true
  const token = localStorage.getItem('token')
  try {
    const url = dangSua.value ? `${API}/san-pham/${idDangSua.value}` : `${API}/san-pham`
    const method = dangSua.value ? 'PUT' : 'POST'
    const res = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
      body: JSON.stringify(form.value)
    })
    const data = await res.json()
    if (!res.ok) throw new Error(data.message || 'Lưu thất bại!')
    await taiDanhSach()
    formMoRa.value = false
  } catch (e) {
    loiForm.value = e.message
  } finally {
    dangLuu.value = false
  }
}

async function xoaSanPham(sp) {
  if (!confirm(`Xoá sản phẩm "${sp.tenSanPham}"? Hành động này không thể hoàn tác.`)) return
  const token = localStorage.getItem('token')
  try {
    const res = await fetch(`${API}/san-pham/${sp.id}`, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${token}` }
    })
    const data = await res.json()
    if (!res.ok) throw new Error(data.message || 'Xoá thất bại!')
    await taiDanhSach()
  } catch (e) {
    alert(e.message)
  }
}

onMounted(taiDanhSach)
</script>

<style scoped>
/* ===== Kế thừa style tokens chung của khu vực Admin ===== */
.dashboard__page-title { font-size: 24px; font-weight: 700; color: #1e293b; margin: 0 0 6px 0; }
.dashboard__page-desc { font-size: 14px; color: #64748b; margin: 0 0 28px 0; }

.btn { display: inline-flex; align-items: center; justify-content: center; padding: 10px 22px; font-size: 14px; font-weight: 600; border-radius: 20px; text-decoration: none; cursor: pointer; transition: all 0.2s ease; }
.btn-primary { background-color: #16a34a; color: #ffffff; border: none; padding: 10px 22px; font-size: 14px; font-weight: 600; border-radius: 20px; cursor: pointer; transition: all .2s; }
.btn-primary:hover:not(:disabled) { background-color: #15803d; box-shadow: 0 4px 12px rgba(22, 163, 74, 0.2); }
.btn-primary:disabled { opacity: .6; cursor: not-allowed; }
.btn-ghost-gray { background-color: transparent; color: #475569; border: 1px solid #cbd5e1; padding: 10px 22px; font-size: 14px; font-weight: 600; border-radius: 20px; cursor: pointer; transition: all .2s; }
.btn-ghost-gray:hover { color: #0f172a; border-color: #94a3b8; background-color: #f8fafc; }
.btn-danger { background-color: transparent; color: #dc2626; border: 1px solid #fecaca; padding: 10px 22px; font-size: 14px; font-weight: 600; border-radius: 20px; cursor: pointer; transition: all .2s; }
.btn-danger:hover { background-color: #fef2f2; border-color: #dc2626; }
.btn-nho { padding: 6px 14px; font-size: 12.5px; }

.empty-state { background-color: #ffffff; border: 1px dashed #cbd5e1; border-radius: 16px; padding: 48px 24px; text-align: center; display: flex; flex-direction: column; align-items: center; justify-content: center; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05); }
.empty-state__icon { width: 64px; height: 64px; border-radius: 50%; background-color: #f1f5f9; color: #94a3b8; display: flex; align-items: center; justify-content: center; margin-bottom: 20px; }
.empty-state__title { font-size: 18px; font-weight: 600; color: #0f172a; margin: 0 0 10px 0; }
.empty-state__desc { font-size: 14px; color: #64748b; max-width: 460px; line-height: 1.6; margin: 0 0 24px 0; }

/* ===== Toolbar ===== */
.toolbar { display: flex; gap: 12px; margin-bottom: 20px; flex-wrap: wrap; }
.search-box {
  flex: 1; min-width: 220px; padding: 10px 16px; border-radius: 10px;
  border: 1px solid #e2e8f0; font-size: 14px; font-family: inherit; color: #1e293b;
}
.search-box:focus { outline: none; border-color: #16a34a; }
.filter-box {
  padding: 10px 14px; border-radius: 10px; border: 1px solid #e2e8f0;
  font-size: 14px; font-family: inherit; color: #1e293b; background: white;
}
.btn-add {
  padding: 10px 20px; border-radius: 10px; border: none;
  background: #16a34a; color: white; font-size: 14px; font-weight: 600;
  cursor: pointer; transition: .2s; white-space: nowrap;
}
.btn-add:hover { background: #15803d; }

/* ===== Table ===== */
.table-wrapper {
  background: white; border: 1px solid #e2e8f0; border-radius: 12px;
  overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,.05);
}
table { width: 100%; border-collapse: collapse; }
thead { background: #f8fafc; }
th {
  text-align: left; padding: 14px 16px; font-size: 12.5px; font-weight: 700;
  color: #64748b; text-transform: uppercase; letter-spacing: .4px;
  border-bottom: 1px solid #e2e8f0;
}
td { padding: 12px 16px; font-size: 14px; color: #1e293b; border-bottom: 1px solid #f1f5f9; vertical-align: middle; }
tbody tr:last-child td { border-bottom: none; }
tbody tr:hover { background: #f8fafc; }

.sp-anh { width: 40px; height: 40px; border-radius: 8px; object-fit: cover; }
.sp-ten { font-weight: 600; }
.sp-gia { font-weight: 700; color: #16a34a; }

.loai-tag { padding: 3px 10px; border-radius: 999px; font-size: 11.5px; font-weight: 700; }
.loai-tag--uong { background: #e0f2fe; color: #0369a1; }
.loai-tag--dv { background: #f3e8ff; color: #7e22ce; }

.trang-thai { padding: 3px 10px; border-radius: 999px; font-size: 11.5px; font-weight: 700; }
.trang-thai--con { background: #dcfce7; color: #15803d; }
.trang-thai--het { background: #fee2e2; color: #991b1b; }

.cot-thao-tac { display: flex; gap: 8px; }

/* ===== Modal ===== */
.modal-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,.5);
  z-index: 1000; display: flex; align-items: center; justify-content: center;
  padding: 20px;
}
.modal {
  background: white; border-radius: 20px; padding: 28px;
  max-width: 460px; width: 100%;
  box-shadow: 0 24px 64px rgba(0,0,0,.2);
  max-height: 90vh; overflow-y: auto;
}
.modal h3 { font-size: 19px; font-weight: 800; color: #0f172a; margin-bottom: 18px; }
.field { margin-bottom: 14px; display: flex; flex-direction: column; gap: 6px; }
.field label { font-size: 13px; font-weight: 600; color: #334155; }
.field input, .field select, .field textarea {
  padding: 10px 12px; border-radius: 8px; border: 1px solid #e2e8f0;
  font-size: 14px; font-family: inherit; color: #1e293b; resize: vertical;
}
.field input:focus, .field select:focus, .field textarea:focus { outline: none; border-color: #16a34a; }
.field--checkbox { flex-direction: row; align-items: center; gap: 8px; font-size: 13.5px; font-weight: 500; color: #334155; cursor: pointer; }
.field--checkbox input { width: auto; }

.loi-form { color: #dc2626; font-size: 13px; margin: 8px 0 0; }
.modal-actions { display: flex; gap: 10px; margin-top: 20px; }
.modal-actions .btn-ghost-gray, .modal-actions .btn-primary { flex: 1; }

@media (max-width: 640px) {
  .table-wrapper { overflow-x: auto; }
  table { min-width: 640px; }
}
</style>