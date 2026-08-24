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
      <h1 class="page__title">Quản lý sản phẩm</h1>
      <p class="page__desc">Quản lý danh sách đồ uống, dịch vụ khách có thể gọi khi đến sân của <span class="brand">Dream League</span></p>
    </header>

    <!-- Stat strip -->
    <section class="stats">
      <div class="stat-card reveal" style="--d:0s">
        <div class="stat-card__icon stat-card__icon--total">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M6 2l1.5 4h9L18 2"/><path d="M3.5 8h17l-1.5 12a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2L3.5 8z"/><path d="M9 12v3"/><path d="M15 12v3"/></svg>
        </div>
        <div>
          <p class="stat-card__value">{{ stats.total }}</p>
          <p class="stat-card__label">Tổng sản phẩm</p>
        </div>
      </div>

      <div class="stat-card reveal" style="--d:.06s">
        <div class="stat-card__icon stat-card__icon--drink">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M18 8h1a4 4 0 0 1 0 8h-1"/><path d="M2 8h16v9a4 4 0 0 1-4 4H6a4 4 0 0 1-4-4V8z"/><line x1="6" y1="1" x2="6" y2="4"/><line x1="10" y1="1" x2="10" y2="4"/><line x1="14" y1="1" x2="14" y2="4"/></svg>
        </div>
        <div>
          <p class="stat-card__value">{{ stats.douong }}</p>
          <p class="stat-card__label">Đồ uống</p>
        </div>
      </div>

      <div class="stat-card reveal" style="--d:.12s">
        <div class="stat-card__icon stat-card__icon--service">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"/></svg>
        </div>
        <div>
          <p class="stat-card__value">{{ stats.dichvu }}</p>
          <p class="stat-card__label">Dịch vụ</p>
        </div>
      </div>

      <div class="stat-card reveal" style="--d:.18s">
        <div class="stat-card__icon stat-card__icon--off">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/><line x1="9" y1="9" x2="15" y2="15"/><line x1="15" y1="9" x2="9" y2="15"/></svg>
        </div>
        <div>
          <p class="stat-card__value">{{ stats.ngungBan }}</p>
          <p class="stat-card__label">Ngừng bán</p>
        </div>
      </div>
    </section>

    <!-- Toolbar -->
    <div class="toolbar reveal" style="--d:.22s">
      <div class="toolbar__filters">
        <div class="search">
          <svg class="search__icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="7"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
          <input v-model="tuKhoa" placeholder="Tìm sản phẩm..." />
        </div>

        <select v-model="locLoai" class="filter-select">
          <option value="">Tất cả loại</option>
          <option value="DO_UONG">Đồ uống</option>
          <option value="DICH_VU">Dịch vụ</option>
        </select>
      </div>

      <button class="btn btn--primary" @click="moFormThem">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        Thêm sản phẩm
      </button>
    </div>

    <!-- Table -->
    <div class="table-card reveal" style="--d:.28s">

      <div v-if="dangTai" class="state">
        <div class="spinner"></div>
        <p>Đang tải danh sách sản phẩm...</p>
      </div>

      <div v-else-if="danhSachLoc.length === 0" class="state">
        <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="8" height="8" rx="1"/><rect x="13" y="3" width="8" height="8" rx="1"/><rect x="3" y="13" width="8" height="8" rx="1"/><rect x="13" y="13" width="8" height="8" rx="1"/></svg>
        <p>Chưa có sản phẩm nào phù hợp.</p>
      </div>

      <table v-else>
        <thead>
          <tr>
            <th>Hình</th>
            <th>Tên sản phẩm</th>
            <th>Loại</th>
            <th>Giá</th>
            <th>Trạng thái</th>
            <th class="col-actions">Thao tác</th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="sp in danhSachLoc" :key="sp.id">

            <td><img :src="sp.hinhAnh || anhMacDinh" class="sp-anh" alt="" /></td>

            <td class="sp-ten">{{ sp.tenSanPham }}</td>

            <td>
              <span class="badge" :class="sp.loai === 'DO_UONG' ? 'badge--drink' : 'badge--service'">
                {{ sp.loai === 'DO_UONG' ? 'Đồ uống' : 'Dịch vụ' }}
              </span>
            </td>

            <td class="sp-gia">{{ formatTien(sp.gia) }}đ</td>

            <td>
              <span class="badge badge--status" :class="sp.conBan ? 'badge--active' : 'badge--off'">
                <span class="badge__dot"></span>
                {{ sp.conBan ? 'Còn bán' : 'Ngừng bán' }}
              </span>
            </td>

            <td class="col-actions">
              <button class="icon-btn icon-btn--edit" title="Sửa" @click="moFormSua(sp)">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 20h9"/><path d="M16.5 3.5a2.12 2.12 0 0 1 3 3L7 19l-4 1 1-4 12.5-12.5z"/></svg>
              </button>
              <button class="icon-btn icon-btn--delete" title="Xoá" @click="moModalXoa(sp)">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/><path d="M10 11v6"/><path d="M14 11v6"/><path d="M9 6V4a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2v2"/></svg>
              </button>
            </td>

          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal Thêm/Sửa -->
    <Transition name="fade">
      <div v-if="formMoRa" class="overlay" @click.self="dongForm">
        <div class="modal">

          <div class="modal__header">
            <div class="modal__icon">
              <svg v-if="!dangSua" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M6 2l1.5 4h9L18 2"/><path d="M3.5 8h17l-1.5 12a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2L3.5 8z"/></svg>
              <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 20h9"/><path d="M16.5 3.5a2.12 2.12 0 0 1 3 3L7 19l-4 1 1-4 12.5-12.5z"/></svg>
            </div>
            <div>
              <h2>{{ dangSua ? 'Sửa sản phẩm' : 'Thêm sản phẩm mới' }}</h2>
              <p>{{ dangSua ? 'Chỉnh sửa thông tin sản phẩm' : 'Tạo đồ uống hoặc dịch vụ mới' }}</p>
            </div>
            <button class="modal__close" @click="dongForm">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </div>

          <div class="modal__body">
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
              <div class="image-picker">
                <img :src="form.hinhAnh || anhMacDinh" class="image-picker__preview" alt="" />
                <input v-model="form.hinhAnh" placeholder="https://..." />
              </div>
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

            <label class="field-checkbox">
              <input type="checkbox" v-model="form.conBan" />
              Còn bán (hiện cho khách gọi)
            </label>

            <p v-if="loiForm" class="loi-form">{{ loiForm }}</p>
          </div>

          <div class="modal__footer">
            <button class="btn btn--ghost" @click="dongForm">Huỷ</button>
            <button class="btn btn--primary" :disabled="dangLuu" @click="luuSanPham">
              {{ dangLuu ? 'Đang lưu...' : 'Lưu' }}
            </button>
          </div>

        </div>
      </div>
    </Transition>

    <!-- Modal Xoá -->
    <Transition name="fade">
      <div v-if="showDelete" class="overlay" @click.self="showDelete = false">
        <div class="modal modal--sm">

          <div class="modal__icon modal__icon--danger">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/><path d="M10 11v6"/><path d="M14 11v6"/><path d="M9 6V4a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2v2"/></svg>
          </div>

          <h2>Xoá sản phẩm?</h2>
          <p class="modal__text">
            Bạn có chắc muốn xoá <strong>{{ spDangXoa?.tenSanPham }}</strong>?
            Hành động này không thể hoàn tác.
          </p>

          <div class="modal__footer modal__footer--center">
            <button class="btn btn--ghost" @click="showDelete = false">Huỷ</button>
            <button class="btn btn--danger" :disabled="dangXoa" @click="xoaSanPham">
              {{ dangXoa ? 'Đang xoá...' : 'Xoá sản phẩm' }}
            </button>
          </div>

        </div>
      </div>
    </Transition>

  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'

const API = '/api'
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

const showDelete = ref(false)
const dangXoa = ref(false)
const spDangXoa = ref(null)

const danhSachLoc = computed(() => {
  return danhSach.value.filter(sp => {
    const khopTen = sp.tenSanPham.toLowerCase().includes(tuKhoa.value.trim().toLowerCase())
    const khopLoai = !locLoai.value || sp.loai === locLoai.value
    return khopTen && khopLoai
  })
})

const stats = computed(() => {
  const total = danhSach.value.length
  const douong = danhSach.value.filter(sp => sp.loai === 'DO_UONG').length
  const dichvu = danhSach.value.filter(sp => sp.loai === 'DICH_VU').length
  const ngungBan = danhSach.value.filter(sp => !sp.conBan).length
  return { total, douong, dichvu, ngungBan }
})

function formatTien(so) { return Number(so).toLocaleString('vi-VN') }

// ===== Hiệu ứng xuất hiện khi cuộn tới =====
let revealObserver = null

onMounted(() => {
  taiDanhSach()

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

function moModalXoa(sp) {
  spDangXoa.value = sp
  showDelete.value = true
}

async function xoaSanPham() {
  dangXoa.value = true
  const token = localStorage.getItem('token')
  try {
    const res = await fetch(`${API}/san-pham/${spDangXoa.value.id}`, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${token}` }
    })
    const data = await res.json()
    if (!res.ok) throw new Error(data.message || 'Xoá thất bại!')
    await taiDanhSach()
    showDelete.value = false
  } catch (e) {
    alert(e.message)
  } finally {
    dangXoa.value = false
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
.stat-card__icon--drink{ background:rgba(255,176,32,.14); color:var(--amber-400); }
.stat-card__icon--service{ background:rgba(167,139,250,.16); color:var(--purple-400); }
.stat-card__icon--off{ background:rgba(255,71,87,.14); color:#ff9686; }

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
    padding:12px 18px;
    border-bottom:1px solid rgba(247,251,244,.07);
    vertical-align:middle;
    color:var(--chalk-050);
}

tbody tr:last-child td{ border-bottom:none; }
tbody tr:hover{ background:rgba(182,255,60,.045); }

.col-actions{ width:90px; }

.sp-anh{
    width:42px;
    height:42px;
    border-radius:10px;
    object-fit:cover;
    border:1px solid rgba(247,251,244,.12);
}

.sp-ten{ font-weight:700; font-size:14px; color:var(--chalk-050); }

.sp-gia{
    font-family:var(--font-mono);
    font-weight:700;
    color:var(--lime-300);
}

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

.badge--drink{ background:rgba(255,176,32,.14); color:var(--amber-400); }
.badge--service{ background:rgba(167,139,250,.16); color:var(--purple-400); }

.badge--active{ background:rgba(182,255,60,.14); color:var(--lime-300); }
.badge--off{ background:rgba(255,71,87,.14); color:#ff9686; }

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

.field{ display:flex; flex-direction:column; gap:6px; }

.field label{ font-size:12.5px; font-weight:700; color:var(--chalk-200); opacity:.8; }

.field input,
.field select,
.field textarea{
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

.field input:focus,
.field select:focus,
.field textarea:focus{
    border-color:var(--lime-400);
    box-shadow:0 0 0 3px rgba(182,255,60,.15);
}

.field input::placeholder,
.field textarea::placeholder{ color:rgba(247,251,244,.35); }

/* Ảnh xem trước cho link hình ảnh */
.image-picker{ display:flex; align-items:center; gap:10px; }

.image-picker__preview{
    width:44px;
    height:44px;
    min-width:44px;
    border-radius:9px;
    object-fit:cover;
    border:1px solid rgba(247,251,244,.14);
}

.image-picker input{ flex:1; }

.field-checkbox{
    display:flex;
    align-items:center;
    gap:9px;
    font-size:13.5px;
    font-weight:600;
    color:var(--chalk-200);
    cursor:pointer;
}

.field-checkbox input{
    width:16px;
    height:16px;
    accent-color:var(--lime-400);
    cursor:pointer;
}

.loi-form{ color:#ff9686; font-size:13px; margin:0; }

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
    table{ font-size:13px; min-width:640px; }
}

@media(max-width:520px){
    .stats{ grid-template-columns:1fr 1fr; }
}
</style>