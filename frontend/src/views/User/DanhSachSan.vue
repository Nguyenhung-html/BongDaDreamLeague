<template>
  <div class="pitch-list" :style="{ '--sx': spotlight.x + '%', '--sy': spotlight.y + '%' }">

    <!-- ===== LỚP NỀN CHUNG (đồng bộ trang chủ) ===== -->
    <div class="pitch-list__ambient" aria-hidden="true">
      <div class="floodlight floodlight--l"></div>
      <div class="floodlight floodlight--r"></div>
      <div class="grass-drift"></div>
      <div class="motes">
        <span v-for="n in 14" :key="n" class="mote" :style="{ '--i': n }"></span>
      </div>
    </div>

    <!-- ===== HERO ===== -->
    <section class="hero" @mousemove="onHeroMove" @mouseleave="onHeroLeave">
      <div class="hero__ticker" aria-hidden="true">
        <div class="hero__ticker-track">
          <span v-for="n in 2" :key="n">
            &nbsp;●&nbsp; SÂN A2 CÒN TRỐNG 19:00–20:00 &nbsp;●&nbsp; SÂN B1 GIẢM 20% KHUNG GIỜ TRƯA &nbsp;●&nbsp; ĐẶT SÂN B3 TẶNG NƯỚC UỐNG &nbsp;●&nbsp; 3 SÂN 5 · 3 SÂN 7 ĐANG MỞ &nbsp;●&nbsp; HOTLINE 0343 831 316&nbsp;
          </span>
        </div>
      </div>

      <div class="container hero__inner">
        <div class="hero__copy">
          <span class="eyebrow">⚽ Hẻm 104 Tân Sơn · Mở cửa 06:00–22:00</span>
          <h1 class="headline">Chọn sân của bạn</h1>
          <p class="desc">Xem lịch trống thời gian thực, so sánh khung giờ và đặt sân trong 30 giây.</p>
        </div>

        <div class="hero__stats">
          <div class="hero__stat">
            <strong>{{ danhSachSan.length || 6 }}</strong>
            <span>Sân</span>
          </div>
          <div class="hero__stat-divider"></div>
          <div class="hero__stat">
            <strong>4.8★</strong>
            <span>Đánh giá</span>
          </div>
          <div class="hero__stat-divider"></div>
          <div class="hero__stat">
            <strong>06–22h</strong>
            <span>Mỗi ngày</span>
          </div>
        </div>
      </div>
    </section>

    <!-- ===== TOOLBAR: TÌM KIẾM + LỌC ===== -->
    <section class="toolbar">
      <div class="container toolbar__inner">

        <div class="search-box" ref="searchBoxRef">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
            <circle cx="11" cy="11" r="7" stroke="var(--lime-300)" stroke-width="2.2"/>
            <path d="M16.5 16.5L21 21" stroke="var(--lime-300)" stroke-width="2.2" stroke-linecap="round"/>
          </svg>
          <input
            v-model="tuKhoa"
            type="text"
            placeholder="Tìm theo tên sân A1, B2... hoặc loại sân"
            @input="onSearchInput"
            @focus="onSearchFocus"
            @keyup.enter="applySearch"
          />
          <button v-if="tuKhoa" class="search-clear" @click="clearSearch" aria-label="Xoá tìm kiếm">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none">
              <path d="M18 6L6 18M6 6l12 12" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/>
            </svg>
          </button>

          <div class="search-suggest" v-if="showSuggest && suggestList.length > 0">
            <div
              v-for="item in suggestList"
              :key="item.id"
              class="search-suggest__item"
              @mousedown.prevent="chonSuggest(item)"
            >
              <span class="suggest-badge" :class="item.loai === 'san5' ? 'badge--blue' : 'badge--green'">
                {{ item.maSan }}
              </span>
              <div class="suggest-info">
                <p class="suggest-ten">{{ item.ten }}</p>
                <p class="suggest-loai">{{ item.loai === 'san5' ? 'Sân 5 người' : 'Sân 7 người' }} · {{ item.gia }}đ/giờ</p>
              </div>
              <div class="suggest-stars">
                <svg width="11" height="11" viewBox="0 0 24 24" fill="var(--amber-400)"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/></svg>
                {{ item.rating }}
              </div>
            </div>
            <div class="search-suggest__all" @mousedown.prevent="applySearch">
              Xem tất cả kết quả cho "<strong>{{ tuKhoa }}</strong>" →
            </div>
          </div>
        </div>

        <div class="filter-tabs">
          <button
            v-for="tab in tabs"
            :key="tab.value"
            class="filter-tab"
            :class="{ 'filter-tab--active': activeTab === tab.value }"
            @click="doiTab(tab.value)"
          >
            <span v-html="tab.icon"></span>
            {{ tab.label }}
            <span class="filter-tab__count">{{ demSan(tab.value) }}</span>
          </button>
        </div>

      </div>
    </section>

    <!-- ===== DANH SÁCH SÂN ===== -->
    <section class="san-list">
      <div class="container">

        <div class="list-header">
          <h2 class="list-header__title">
            {{ tieuDeHienTai }}
            <span class="list-header__count">{{ sanHienThi.length }} sân</span>
          </h2>
          <p class="list-header__addr">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none"><path d="M12 22s7-7.58 7-12.5A7 7 0 1 0 5 9.5C5 14.42 12 22 12 22Z" stroke="var(--lime-300)" stroke-width="1.8"/><circle cx="12" cy="9.5" r="2.5" fill="var(--lime-300)"/></svg>
            Hẻm 104 Tân Sơn, Tân Sơn, Hồ Chí Minh
          </p>
        </div>

        <div class="san-grid" v-if="sanHienThi.length > 0">
          <div class="san-card" v-for="san in sanHienThi" :key="san.id">

            <!-- Ảnh sân -->
            <div class="san-card__img">
              <svg viewBox="0 0 360 200" fill="none" class="pitch-svg">
                <rect width="360" height="200" :fill="san.loai === 'san5' ? '#0d2417' : '#123321'"/>
                <rect x="0" y="0" width="40" height="200" fill="rgba(0,0,0,0.08)"/>
                <rect x="80" y="0" width="40" height="200" fill="rgba(0,0,0,0.08)"/>
                <rect x="160" y="0" width="40" height="200" fill="rgba(0,0,0,0.08)"/>
                <rect x="240" y="0" width="40" height="200" fill="rgba(0,0,0,0.08)"/>
                <rect x="320" y="0" width="40" height="200" fill="rgba(0,0,0,0.08)"/>
                <rect x="16" y="12" width="328" height="176" rx="4" stroke="rgba(247,251,244,0.35)" stroke-width="2" fill="none"/>
                <line x1="180" y1="12" x2="180" y2="188" stroke="rgba(247,251,244,0.3)" stroke-width="1.5"/>
                <circle cx="180" cy="100" r="30" stroke="rgba(247,251,244,0.3)" stroke-width="1.5" fill="none"/>
                <circle cx="180" cy="100" r="3" fill="var(--lime-300)"/>
                <rect x="16" y="74" width="34" height="52" stroke="rgba(247,251,244,0.3)" stroke-width="1.5" fill="none"/>
                <rect x="16" y="84" width="18" height="32" stroke="rgba(247,251,244,0.18)" stroke-width="1.5" fill="none"/>
                <rect x="310" y="74" width="34" height="52" stroke="rgba(247,251,244,0.3)" stroke-width="1.5" fill="none"/>
                <rect x="326" y="84" width="18" height="32" stroke="rgba(247,251,244,0.18)" stroke-width="1.5" fill="none"/>
                <rect width="360" height="200" :fill="`url(#grad-${san.id})`"/>
                <defs>
                  <linearGradient :id="`grad-${san.id}`" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="0%" stop-color="rgba(0,0,0,0.05)"/>
                    <stop offset="100%" stop-color="rgba(5,11,8,0.65)"/>
                  </linearGradient>
                </defs>
              </svg>

              <div class="san-card__badges">
                <span class="badge-loai" :class="san.loai === 'san5' ? 'badge-loai--blue' : 'badge-loai--green'">
                  {{ san.loai === 'san5' ? 'Sân 5' : 'Sân 7' }}
                </span>
                <span v-if="san.hot" class="badge-hot">🔥 Hot</span>
              </div>

              <div class="san-card__ma">{{ san.maSan }}</div>

              <div class="san-card__rating">
                <svg width="12" height="12" viewBox="0 0 24 24" fill="var(--amber-400)"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/></svg>
                <strong>{{ san.rating }}</strong>
                <span>({{ san.soLuotDanhGia }})</span>
              </div>

              <button
                class="san-card__fav"
                :class="{ 'san-card__fav--active': sanYeuThich.includes(san.id) }"
                @click.stop="toggleYeuThich(san.id)"
                aria-label="Yêu thích"
              >
                <svg width="16" height="16" viewBox="0 0 24 24" :fill="sanYeuThich.includes(san.id) ? 'var(--crimson-500)' : 'none'" stroke="var(--chalk-050)" stroke-width="2">
                  <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
                </svg>
              </button>
            </div>

            <!-- Nội dung card -->
            <div class="san-card__body">
              <div class="san-card__top-row">
                <h3 class="san-card__ten">{{ san.ten }}</h3>
                <div class="san-card__stars-row">
                  <svg v-for="i in 5" :key="i" width="12" height="12" viewBox="0 0 24 24"
                    :fill="i <= Math.round(san.rating) ? 'var(--amber-400)' : 'rgba(247,251,244,0.15)'">
                    <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
                  </svg>
                </div>
              </div>

              <p class="san-card__dia-chi">
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none"><path d="M12 22s7-7.58 7-12.5A7 7 0 1 0 5 9.5C5 14.42 12 22 12 22Z" stroke="var(--chalk-200)" stroke-width="2"/></svg>
                Hẻm 104 Tân Sơn, Tân Sơn, HCM
              </p>

              <div class="san-card__specs">
                <span class="spec-item">
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none"><rect x="2" y="3" width="20" height="18" rx="2" stroke="var(--lime-300)" stroke-width="1.8"/></svg>
                  {{ san.kichThuoc }}
                </span>
                <span class="spec-item">
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="9" stroke="var(--lime-300)" stroke-width="1.8"/><path d="M12 7v5l3 2" stroke="var(--lime-300)" stroke-width="1.8" stroke-linecap="round"/></svg>
                  06:00 – 22:00
                </span>
                <span class="spec-item">
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none"><path d="M12 3v1M12 20v1M4.22 4.22l.7.7M18.36 18.36l.71.71M3 12h1M20 12h1" stroke="var(--lime-300)" stroke-width="1.8" stroke-linecap="round"/><circle cx="12" cy="12" r="4" stroke="var(--lime-300)" stroke-width="1.8"/></svg>
                  Đèn sáng
                </span>
              </div>

              <div class="san-card__footer">
                <div class="san-card__gia">
                  <span class="gia-label">Từ</span>
                  <strong class="gia-so">{{ san.gia }}</strong>
                  <span class="gia-label">đ/giờ</span>
                </div>
                <div class="san-card__actions">
                  <router-link :to="`/san/${san.id}`" class="btn-chitiet">Chi tiết</router-link>
                  <router-link :to="`/dat-san/${san.id}`" class="btn-datsan">Đặt sân</router-link>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="no-result" v-else>
          <svg width="52" height="52" viewBox="0 0 24 24" fill="none"><circle cx="11" cy="11" r="7" stroke="rgba(247,251,244,0.25)" stroke-width="1.5"/><path d="M16.5 16.5L21 21" stroke="rgba(247,251,244,0.25)" stroke-width="1.5" stroke-linecap="round"/></svg>
          <p>Không tìm thấy sân phù hợp với "<strong>{{ tuKhoa }}</strong>"</p>
          <button @click="clearSearch">Xem tất cả sân</button>
        </div>

      </div>
    </section>

  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'

/* ============ SPOTLIGHT CON TRỎ TRÊN HERO ============ */
const spotlight = reactive({ x: 50, y: 30 })
function onHeroMove(e) {
  const rect = e.currentTarget.getBoundingClientRect()
  spotlight.x = ((e.clientX - rect.left) / rect.width) * 100
  spotlight.y = ((e.clientY - rect.top) / rect.height) * 100
}
function onHeroLeave() { spotlight.x = 50; spotlight.y = 30 }

/* ============ DỮ LIỆU SÂN ============ */
const danhSachSan = ref([])
const API = '/api'

async function fetchSanBong() {
  try {
    const res = await fetch(`${API}/san-bong`)
    if (res.ok) {
      const data = await res.json()
      danhSachSan.value = data.map(san => ({
        id: san.id,
        maSan: san.tenSan.split(' – ')[0] || san.tenSan,
        ten: san.tenSan,
        loai: san.loaiSan === 5 ? 'san5' : 'san7',
        kichThuoc: san.loaiSan === 5 ? '25 × 16m' : '50 × 30m',
        gia: san.danhSachGia && san.danhSachGia.length > 0 ? Number(san.danhSachGia[0].giaTien).toLocaleString('vi-VN') : '350.000',
        rating: 4.8,
        soLuotDanhGia: 100,
        hot: true,
        tags: [san.tenSan.toLowerCase(), san.loaiSan === 5 ? 'san5' : 'san7', san.loaiSan === 5 ? '5 người' : '7 người'],
        hinhAnh: san.hinhAnh
      }))
    }
  } catch (err) {
    console.error('Lỗi tải danh sách sân:', err)
  }
}

/* ============ TABS ============ */
const tabs = [
  {
    value: 'tat-ca', label: 'Tất cả sân',
    icon: `<svg width="15" height="15" viewBox="0 0 24 24" fill="none"><rect x="2" y="3" width="20" height="18" rx="2" stroke="currentColor" stroke-width="1.8"/><line x1="12" y1="3" x2="12" y2="21" stroke="currentColor" stroke-width="1.8"/></svg>`
  },
  {
    value: 'san5', label: 'Sân 5 người',
    icon: `<svg width="15" height="15" viewBox="0 0 24 24" fill="none"><rect x="2" y="3" width="20" height="18" rx="2" stroke="currentColor" stroke-width="1.8"/></svg>`
  },
  {
    value: 'san7', label: 'Sân 7 người',
    icon: `<svg width="15" height="15" viewBox="0 0 24 24" fill="none"><rect x="2" y="3" width="20" height="18" rx="2" stroke="currentColor" stroke-width="1.8"/><line x1="12" y1="3" x2="12" y2="21" stroke="currentColor" stroke-width="1.8"/><circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="1.8"/></svg>`
  },
]

const activeTab = ref('tat-ca')

function demSan(tab) {
  if (tab === 'tat-ca') return danhSachSan.value.length
  return danhSachSan.value.filter(s => s.loai === tab).length
}

function doiTab(val) {
  activeTab.value = val
  tuKhoa.value = ''
  showSuggest.value = false
  suggestList.value = []
}

/* ============ TÌM KIẾM ============ */
const tuKhoa = ref('')
const showSuggest = ref(false)
const suggestList = ref([])
const searchBoxRef = ref(null)

function locSan(q) {
  const query = q.trim().toLowerCase()
  if (!query) return []
  return danhSachSan.value.filter(san =>
    san.maSan.toLowerCase().includes(query) ||
    san.ten.toLowerCase().includes(query) ||
    san.tags.some(t => t.includes(query))
  )
}

function onSearchInput() {
  suggestList.value = locSan(tuKhoa.value)
  showSuggest.value = suggestList.value.length > 0
}

function onSearchFocus() {
  if (tuKhoa.value.trim()) {
    suggestList.value = locSan(tuKhoa.value)
    showSuggest.value = suggestList.value.length > 0
  }
}

function applySearch() {
  showSuggest.value = false
}

function chonSuggest(item) {
  tuKhoa.value = item.maSan
  showSuggest.value = false
  activeTab.value = item.loai
}

function clearSearch() {
  tuKhoa.value = ''
  suggestList.value = []
  showSuggest.value = false
  activeTab.value = 'tat-ca'
}

/* ============ DANH SÁCH HIỂN THỊ ============ */
const sanHienThi = computed(() => {
  let list = danhSachSan.value
  if (activeTab.value !== 'tat-ca') {
    list = list.filter(s => s.loai === activeTab.value)
  }
  const q = tuKhoa.value.trim().toLowerCase()
  if (q) {
    list = list.filter(san =>
      san.maSan.toLowerCase().includes(q) ||
      san.ten.toLowerCase().includes(q) ||
      san.tags.some(t => t.includes(q))
    )
  }
  return list
})

const tieuDeHienTai = computed(() => {
  if (tuKhoa.value.trim()) return `Kết quả tìm kiếm`
  if (activeTab.value === 'san5') return 'Sân 5 người (A1 – A4)'
  if (activeTab.value === 'san7') return 'Sân 7 người (B1 – B4)'
  return 'Tất cả sân'
})

/* ============ YÊU THÍCH ============ */
const sanYeuThich = ref([])
function toggleYeuThich(id) {
  const idx = sanYeuThich.value.indexOf(id)
  if (idx === -1) sanYeuThich.value.push(id)
  else sanYeuThich.value.splice(idx, 1)
}

/* ============ CLICK NGOÀI ĐÓNG SUGGEST ============ */
function onClickOutside(e) {
  if (searchBoxRef.value && !searchBoxRef.value.contains(e.target)) {
    showSuggest.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', onClickOutside)
  fetchSanBong()
})
onUnmounted(() => {
  document.removeEventListener('click', onClickOutside)
})
</script>

<style scoped>
/* ===== TOKENS (đồng bộ trang chủ) ===== */
.pitch-list {
  --night-950: #050b08;
  --night-800: #0a1f13;
  --night-700: #123321;
  --turf-500: #23935a;
  --turf-700: #146239;
  --lime-400: #b6ff3c;
  --lime-300: #d3ff8f;
  --amber-400: #ffb020;
  --crimson-500: #ff4757;
  --chalk-050: #f7fbf4;
  --chalk-200: #e3ecdf;
  --font-display: 'Oswald', 'Manrope', sans-serif;
  --font-body: 'Manrope', sans-serif;
  --font-mono: 'Space Mono', monospace;

  position: relative;
  background: var(--night-950);
  color: var(--chalk-050);
  font-family: var(--font-body);
  min-height: 100vh;
  overflow: hidden;
}
.container { max-width: 1280px; margin: 0 auto; padding: 0 44px; }
@media (max-width: 780px) { .container { padding: 0 20px; } }

/* ===== LỚP NỀN ẨN DỤ (giống trang chủ) ===== */
.pitch-list__ambient { position: fixed; inset: 0; z-index: 0; pointer-events: none; }
.floodlight { position: absolute; top: -12%; width: 55vh; height: 145vh; background: conic-gradient(from 90deg at 50% 0%, transparent 42%, rgba(255,244,214,.08) 50%, transparent 58%); mix-blend-mode: screen; }
.floodlight--l { left: -14%; animation: sweep 11s ease-in-out infinite alternate; }
.floodlight--r { right: -14%; animation: sweep 11s ease-in-out infinite alternate-reverse; }
@keyframes sweep { 0% { transform: rotate(-10deg); } 100% { transform: rotate(10deg); } }
.grass-drift { position: absolute; inset: -10% -20%; opacity: .3; background: repeating-linear-gradient(100deg, rgba(35,147,90,.06) 0 70px, transparent 70px 140px); animation: drift 30s linear infinite; }
@keyframes drift { from { background-position: 0 0; } to { background-position: 400px 0; } }
.motes { position: absolute; inset: 0; }
.mote { position: absolute; bottom: -10px; left: calc((var(--i) * 7%) + 1%); width: 3px; height: 3px; border-radius: 50%; background: var(--lime-300); opacity: 0; animation: rise 9s linear infinite; animation-delay: calc(var(--i) * -0.6s); }
@keyframes rise { 0% { transform: translateY(0) scale(.6); opacity: 0; } 10% { opacity: .5; } 90% { opacity: .12; } 100% { transform: translateY(-100vh) scale(1.1); opacity: 0; } }

/* ===== HERO ===== */
.hero { position: relative; z-index: 1; padding-top: 40px; }
.hero__ticker { background: rgba(182,255,60,.08); border-top: 1px solid rgba(182,255,60,.2); border-bottom: 1px solid rgba(182,255,60,.2); overflow: hidden; white-space: nowrap; padding: 8px 0; margin-bottom: 28px; }
.hero__ticker-track { display: inline-block; font-family: var(--font-mono); font-size: 12px; letter-spacing: .04em; color: var(--lime-300); animation: ticker 26s linear infinite; }
@keyframes ticker { from { transform: translateX(0); } to { transform: translateX(-50%); } }

.hero__inner { display: flex; align-items: center; justify-content: space-between; gap: 24px; flex-wrap: wrap; padding-bottom: 32px; }
.eyebrow { display: inline-block; font-family: var(--font-mono); font-size: 12px; letter-spacing: .16em; text-transform: uppercase; color: var(--lime-300); }
.headline { font-family: var(--font-display); font-weight: 600; font-size: clamp(26px, 3vw, 38px); line-height: 1.15; margin-top: 10px; }
.desc { margin-top: 8px; font-size: 14.5px; color: var(--chalk-200); opacity: .78; max-width: 460px; line-height: 1.55; }

.hero__stats {
  display: flex; align-items: center;
  background: rgba(247,251,244,.05);
  border: 1px solid rgba(247,251,244,.14);
  border-radius: 16px; padding: 14px 26px;
  backdrop-filter: blur(6px); flex-shrink: 0;
}
.hero__stat { display: flex; flex-direction: column; align-items: center; gap: 3px; padding: 0 22px; }
.hero__stat strong { font-size: 20px; font-weight: 700; color: var(--chalk-050); }
.hero__stat span { font-size: 11px; color: var(--chalk-200); opacity: .7; }
.hero__stat-divider { width: 1px; height: 34px; background: rgba(247,251,244,.16); }

@media (max-width: 720px) { .hero__inner { justify-content: center; text-align: center; } .desc { margin: 8px auto 0; } .hero__stats { margin: 0 auto; } }

/* ===== TOOLBAR ===== */
.toolbar {
  position: relative; z-index: 30;
  background: rgba(10,31,19,.85);
  backdrop-filter: blur(10px);
  border-top: 1px solid rgba(247,251,244,.08);
  border-bottom: 1px solid rgba(247,251,244,.08);
  position: sticky; top: 0;
}
.toolbar__inner { display: flex; align-items: center; gap: 18px; padding: 14px 0; flex-wrap: wrap; }

.search-box {
  position: relative; display: flex; align-items: center; gap: 8px;
  flex: 1; max-width: 400px;
  background: rgba(247,251,244,.05); border: 1.5px solid rgba(247,251,244,.14);
  border-radius: 999px; padding: 0 6px 0 14px; height: 44px;
  transition: border-color .15s, background .15s;
}
.search-box:focus-within { border-color: var(--lime-400); background: rgba(247,251,244,.08); }
.search-box input { flex: 1; border: none; background: transparent; outline: none; font-size: 14px; font-family: inherit; color: var(--chalk-050); }
.search-box input::placeholder { color: var(--chalk-200); opacity: .5; }

.search-clear { background: none; border: none; cursor: pointer; color: var(--chalk-200); opacity: .7; display: flex; align-items: center; padding: 4px; transition: opacity .15s; }
.search-clear:hover { opacity: 1; }

.search-suggest {
  position: absolute; top: calc(100% + 8px); left: 0; right: 0;
  background: var(--night-800); border-radius: 14px;
  box-shadow: 0 20px 50px rgba(0,0,0,.5);
  border: 1px solid rgba(182,255,60,.18); overflow: hidden; z-index: 200;
}
.search-suggest__item { display: flex; align-items: center; gap: 12px; padding: 12px 16px; cursor: pointer; transition: background .12s; }
.search-suggest__item:hover { background: rgba(247,251,244,.06); }
.suggest-badge { width: 38px; height: 38px; border-radius: 10px; flex-shrink: 0; display: flex; align-items: center; justify-content: center; font-size: 12px; font-weight: 800; font-family: var(--font-mono); }
.badge--blue { background: rgba(59,130,246,.16); color: #93c5fd; }
.badge--green { background: rgba(182,255,60,.14); color: var(--lime-300); }
.suggest-info { flex: 1; }
.suggest-ten { font-size: 14px; font-weight: 600; color: var(--chalk-050); }
.suggest-loai { font-size: 12px; color: var(--chalk-200); opacity: .65; margin-top: 2px; }
.suggest-stars { display: flex; align-items: center; gap: 3px; font-size: 12px; font-weight: 700; color: var(--amber-400); }
.search-suggest__all { padding: 10px 16px; font-size: 13px; color: var(--lime-300); border-top: 1px solid rgba(247,251,244,.08); cursor: pointer; transition: background .12s; }
.search-suggest__all:hover { background: rgba(247,251,244,.06); }

.filter-tabs { display: flex; gap: 8px; flex-wrap: wrap; }
.filter-tab {
  display: inline-flex; align-items: center; gap: 7px;
  padding: 9px 18px; border-radius: 999px;
  font-size: 13.5px; font-weight: 600; font-family: inherit;
  border: 1.5px solid rgba(247,251,244,.14);
  background: transparent; color: var(--chalk-200); cursor: pointer;
  transition: all .15s; white-space: nowrap;
}
.filter-tab:hover { border-color: var(--lime-400); color: var(--lime-300); }
.filter-tab--active {
  background: linear-gradient(135deg, var(--turf-500), var(--turf-700));
  border-color: transparent; color: var(--chalk-050);
  box-shadow: 0 6px 16px -6px rgba(182,255,60,.35);
}
.filter-tab__count { background: rgba(247,251,244,.1); border-radius: 999px; padding: 1px 8px; font-size: 11px; }
.filter-tab--active .filter-tab__count { background: rgba(255,255,255,.2); }

/* ===== DANH SÁCH SÂN ===== */
.san-list { position: relative; z-index: 1; padding: 36px 0 72px; }
.list-header { margin-bottom: 26px; }
.list-header__title { font-family: var(--font-display); font-size: 22px; font-weight: 600; color: var(--chalk-050); display: flex; align-items: center; gap: 12px; }
.list-header__count { font-family: var(--font-mono); font-size: 13px; font-weight: 600; color: var(--lime-300); background: rgba(182,255,60,.1); padding: 3px 12px; border-radius: 999px; }
.list-header__addr { display: flex; align-items: center; gap: 5px; margin-top: 6px; font-size: 13px; color: var(--chalk-200); opacity: .7; }

.san-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; }
@media (max-width: 1200px) { .san-grid { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 860px)  { .san-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 540px)  { .san-grid { grid-template-columns: 1fr; } }

.san-card {
  background: rgba(247,251,244,.03); border: 1px solid rgba(247,251,244,.08);
  border-radius: 16px; overflow: visible;
  transition: transform .2s ease, border-color .2s ease;
  display: flex; flex-direction: column;
}
.san-card:hover { transform: translateY(-5px); border-color: rgba(182,255,60,.32); }

.san-card__img { position: relative; height: 160px; overflow: hidden; flex-shrink: 0; border-radius: 16px 16px 0 0; }
.pitch-svg { position: absolute; inset: 0; width: 100%; height: 100%; }

.san-card__badges { position: absolute; top: 12px; left: 12px; display: flex; gap: 6px; z-index: 1; }
.badge-loai { font-size: 11px; font-weight: 700; padding: 3px 10px; border-radius: 999px; font-family: var(--font-mono); }
.badge-loai--blue { background: rgba(219,234,254,.92); color: #1d4ed8; }
.badge-loai--green { background: rgba(211,255,143,.92); color: #146239; }
.badge-hot { font-size: 11px; font-weight: 700; padding: 3px 10px; border-radius: 999px; background: rgba(255,71,87,.92); color: white; }

.san-card__ma { position: absolute; bottom: 12px; left: 14px; font-family: var(--font-display); font-size: 30px; font-weight: 600; color: rgba(247,251,244,.92); line-height: 1; text-shadow: 0 2px 10px rgba(0,0,0,.5); z-index: 1; }

.san-card__rating {
  position: absolute; bottom: 14px; right: 12px;
  display: flex; align-items: center; gap: 4px;
  background: rgba(5,11,8,.6); backdrop-filter: blur(4px);
  color: var(--chalk-050); font-size: 12px; font-weight: 700;
  padding: 4px 10px; border-radius: 999px; z-index: 1;
}
.san-card__rating span { font-weight: 400; opacity: .7; font-size: 11px; }

.san-card__fav {
  position: absolute; top: 12px; right: 12px; z-index: 1;
  width: 32px; height: 32px; border-radius: 50%;
  background: rgba(5,11,8,.45); border: none;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; transition: background .15s;
}
.san-card__fav:hover { background: rgba(255,71,87,.55); }
.san-card__fav--active { background: rgba(255,71,87,.65); }

.san-card__body { padding: 14px 16px 16px; flex: 1; display: flex; flex-direction: column; }
.san-card__top-row { display: flex; align-items: flex-start; justify-content: space-between; gap: 8px; }
.san-card__ten { font-size: 14px; font-weight: 700; color: var(--chalk-050); line-height: 1.3; }
.san-card__stars-row { display: flex; align-items: center; gap: 2px; flex-shrink: 0; }

.san-card__dia-chi { display: flex; align-items: center; gap: 4px; margin-top: 5px; font-size: 11px; color: var(--chalk-200); opacity: .6; line-height: 1.4; }

.san-card__specs { display: flex; flex-wrap: wrap; gap: 5px; margin-top: 10px; }
.spec-item {
  display: inline-flex; align-items: center; gap: 4px;
  font-size: 11px; color: var(--chalk-200); font-weight: 500;
  background: rgba(247,251,244,.04); border: 1px solid rgba(247,251,244,.1);
  padding: 3px 8px; border-radius: 999px;
}

.san-card__footer {
  display: flex; align-items: center; justify-content: space-between;
  margin-top: auto; padding-top: 12px; padding-bottom: 2px;
  border-top: 1px solid rgba(247,251,244,.08); gap: 8px;
}
.san-card__gia { display: flex; align-items: baseline; gap: 3px; flex-shrink: 0; }
.gia-so { font-family: var(--font-display); font-size: 16px; font-weight: 600; color: var(--lime-300); }
.gia-label { font-size: 11px; color: var(--chalk-200); opacity: .6; }

.san-card__actions { display: flex; gap: 6px; flex-shrink: 0; }
.btn-chitiet {
  display: inline-flex; align-items: center; padding: 7px 13px; border-radius: 999px;
  border: 1.5px solid rgba(247,251,244,.16); background: transparent; color: var(--chalk-050);
  font-size: 12px; font-weight: 600; font-family: inherit; cursor: pointer;
  transition: all .15s; white-space: nowrap; text-decoration: none;
}
.btn-chitiet:hover { border-color: var(--lime-400); color: var(--lime-300); }
.btn-datsan {
  display: inline-flex; align-items: center; padding: 7px 14px;
  background: linear-gradient(135deg, var(--turf-500), var(--turf-700)); color: var(--chalk-050);
  border-radius: 999px; font-size: 12px; font-weight: 700;
  transition: box-shadow .15s, transform .15s; white-space: nowrap;
  text-decoration: none; border: none;
}
.btn-datsan:hover { box-shadow: 0 8px 18px -6px rgba(182,255,60,.4); transform: translateY(-1px); }

/* No result */
.no-result { text-align: center; padding: 80px 20px; display: flex; flex-direction: column; align-items: center; gap: 16px; }
.no-result p { font-size: 15px; color: var(--chalk-200); opacity: .75; }
.no-result button {
  padding: 10px 24px; background: linear-gradient(135deg, var(--turf-500), var(--turf-700)); color: var(--chalk-050);
  border: none; border-radius: 999px; font-size: 14px; font-weight: 600; font-family: inherit; cursor: pointer;
}

@media (max-width: 640px) {
  .toolbar__inner { flex-direction: column; align-items: stretch; }
  .search-box { max-width: none; }
}
</style>