<template>
  <div class="san-bong-page">

    <!-- ===== HERO SLIDER ===== -->
    <section class="hero">
      <div class="hero-slides">
        <div
          v-for="(slide, idx) in heroSlides"
          :key="idx"
          class="hero-slide"
          :class="{ 'hero-slide--active': currentSlide === idx }"
          :style="{ background: slide.bg }"
        >
          <div class="container hero__inner">
            <div>
              <span class="eyebrow">{{ slide.eyebrow }}</span>
              <h1 class="hero__title">{{ slide.title }}</h1>
              <p class="hero__sub">{{ slide.sub }}</p>
            </div>
            <div class="hero__stats">
              <div class="hero__stat">
                <strong>{{ slide.stat1.val }}</strong><span>{{ slide.stat1.label }}</span>
              </div>
              <div class="hero__stat-divider"></div>
              <div class="hero__stat">
                <strong>{{ slide.stat2.val }}</strong><span>{{ slide.stat2.label }}</span>
              </div>
              <div class="hero__stat-divider"></div>
              <div class="hero__stat">
                <strong>{{ slide.stat3.val }}</strong><span>{{ slide.stat3.label }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Dots -->
      <div class="hero-dots">
        <button
          v-for="(_, idx) in heroSlides"
          :key="idx"
          class="hero-dot"
          :class="{ 'hero-dot--active': currentSlide === idx }"
          @click="currentSlide = idx"
        />
      </div>

      <!-- Arrows -->
      <button class="hero-arrow hero-arrow--prev" @click="prevSlide">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
          <path d="M15 18l-6-6 6-6" stroke="white" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </button>
      <button class="hero-arrow hero-arrow--next" @click="nextSlide">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
          <path d="M9 18l6-6-6-6" stroke="white" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </button>
    </section>

    <!-- ===== THANH TÌM KIẾM + LỌC ===== -->
    <section class="toolbar">
      <div class="container toolbar__inner">

        <div class="search-box" ref="searchBoxRef">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
            <circle cx="11" cy="11" r="7" stroke="var(--green-600)" stroke-width="2.2"/>
            <path d="M16.5 16.5L21 21" stroke="var(--green-600)" stroke-width="2.2" stroke-linecap="round"/>
          </svg>
          <input
            v-model="tuKhoa"
            type="text"
            placeholder="Tìm theo tên sân A1, B2... hoặc loại sân"
            @input="onSearchInput"
            @focus="onSearchFocus"
            @keyup.enter="applySearch"
          />
          <button v-if="tuKhoa" class="search-clear" @click="clearSearch">
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
                <svg width="11" height="11" viewBox="0 0 24 24" fill="#f6c90e"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/></svg>
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
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none"><path d="M12 22s7-7.58 7-12.5A7 7 0 1 0 5 9.5C5 14.42 12 22 12 22Z" stroke="var(--green-600)" stroke-width="1.8"/><circle cx="12" cy="9.5" r="2.5" fill="var(--green-600)"/></svg>
            Hẻm 104 Tân Sơn, Tân Sơn, Hồ Chí Minh
          </p>
        </div>

        <div class="san-grid" v-if="sanHienThi.length > 0">
          <div class="san-card" v-for="san in sanHienThi" :key="san.id">

            <!-- Ảnh sân -->
            <div class="san-card__img" :style="{ background: san.gradient }">
              <svg viewBox="0 0 360 200" fill="none" class="pitch-svg">
                <rect width="360" height="200" fill="#1a8a36"/>
                <rect x="0" y="0" width="40" height="200" fill="rgba(0,0,0,0.06)"/>
                <rect x="80" y="0" width="40" height="200" fill="rgba(0,0,0,0.06)"/>
                <rect x="160" y="0" width="40" height="200" fill="rgba(0,0,0,0.06)"/>
                <rect x="240" y="0" width="40" height="200" fill="rgba(0,0,0,0.06)"/>
                <rect x="320" y="0" width="40" height="200" fill="rgba(0,0,0,0.06)"/>
                <rect x="16" y="12" width="328" height="176" rx="4" stroke="rgba(255,255,255,0.7)" stroke-width="2" fill="none"/>
                <line x1="180" y1="12" x2="180" y2="188" stroke="rgba(255,255,255,0.65)" stroke-width="1.5"/>
                <circle cx="180" cy="100" r="30" stroke="rgba(255,255,255,0.65)" stroke-width="1.5" fill="none"/>
                <circle cx="180" cy="100" r="3" fill="rgba(255,255,255,0.8)"/>
                <rect x="16" y="74" width="34" height="52" stroke="rgba(255,255,255,0.65)" stroke-width="1.5" fill="none"/>
                <rect x="16" y="84" width="18" height="32" stroke="rgba(255,255,255,0.4)" stroke-width="1.5" fill="none"/>
                <rect x="310" y="74" width="34" height="52" stroke="rgba(255,255,255,0.65)" stroke-width="1.5" fill="none"/>
                <rect x="326" y="84" width="18" height="32" stroke="rgba(255,255,255,0.4)" stroke-width="1.5" fill="none"/>
                <rect width="360" height="200" :fill="`url(#grad-${san.id})`"/>
                <defs>
                  <linearGradient :id="`grad-${san.id}`" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="0%" stop-color="rgba(0,0,0,0.15)"/>
                    <stop offset="100%" stop-color="rgba(0,0,0,0.45)"/>
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
                <svg width="12" height="12" viewBox="0 0 24 24" fill="#f6c90e"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/></svg>
                <strong>{{ san.rating }}</strong>
                <span>({{ san.soLuotDanhGia }})</span>
              </div>

              <button
                class="san-card__fav"
                :class="{ 'san-card__fav--active': sanYeuThich.includes(san.id) }"
                @click.stop="toggleYeuThich(san.id)"
              >
                <svg width="16" height="16" viewBox="0 0 24 24" :fill="sanYeuThich.includes(san.id) ? '#e53e3e' : 'none'" stroke="white" stroke-width="2">
                  <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
                </svg>
              </button>
            </div>

            <!-- Nội dung card -->
            <div class="san-card__body">
              <div class="san-card__top-row">
                <h3 class="san-card__ten">{{ san.ten }}</h3>
                <div class="san-card__stars-row">
                  <svg v-for="i in 5" :key="i" width="13" height="13" viewBox="0 0 24 24"
                    :fill="i <= Math.round(san.rating) ? '#f6c90e' : '#e2e8f0'">
                    <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
                  </svg>
                  <span class="san-card__rating-text">{{ san.rating }}/5</span>
                </div>
              </div>

              <p class="san-card__dia-chi">
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none"><path d="M12 22s7-7.58 7-12.5A7 7 0 1 0 5 9.5C5 14.42 12 22 12 22Z" stroke="#6b7280" stroke-width="2"/></svg>
                Hẻm 104 Tân Sơn, Tân Sơn, HCM
              </p>

              <div class="san-card__specs">
                <span class="spec-item">
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none"><rect x="2" y="3" width="20" height="18" rx="2" stroke="var(--green-600)" stroke-width="1.8"/></svg>
                  {{ san.kichThuoc }}
                </span>
                <span class="spec-item">
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="9" stroke="var(--green-600)" stroke-width="1.8"/><path d="M12 7v5l3 2" stroke="var(--green-600)" stroke-width="1.8" stroke-linecap="round"/></svg>
                  06:00 – 22:00
                </span>
                <span class="spec-item">
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none"><path d="M12 3v1M12 20v1M4.22 4.22l.7.7M18.36 18.36l.71.71M3 12h1M20 12h1" stroke="var(--green-600)" stroke-width="1.8" stroke-linecap="round"/><circle cx="12" cy="12" r="4" stroke="var(--green-600)" stroke-width="1.8"/></svg>
                  Đèn sáng
                </span>
                <span class="spec-item">
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none"><path d="M3 6h18M3 12h18M3 18h18" stroke="var(--green-600)" stroke-width="1.8" stroke-linecap="round"/></svg>
                  Cỏ nhân tạo
                </span>
              </div>

              <!-- Footer: giá + 2 nút không bị cắt -->
              <div class="san-card__footer">
                <div class="san-card__gia">
                  <span class="gia-label">Từ</span>
                  <strong class="gia-so">{{ san.gia }}</strong>
                  <span class="gia-label">đ/giờ</span>
                </div>
                <div class="san-card__actions">
                  <!-- ✅ Dùng router-link sang trang chi tiết riêng thay vì mở modal -->
                  <router-link :to="`/san-bong/${san.id}`" class="btn-chitiet">
                    Chi tiết
                  </router-link>
                  <router-link :to="`/dat-san/${san.id}`" class="btn-datsan">
                    Đặt sân
                  </router-link>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="no-result" v-else>
          <svg width="56" height="56" viewBox="0 0 24 24" fill="none"><circle cx="11" cy="11" r="7" stroke="var(--gray-300)" stroke-width="1.5"/><path d="M16.5 16.5L21 21" stroke="var(--gray-300)" stroke-width="1.5" stroke-linecap="round"/></svg>
          <p>Không tìm thấy sân phù hợp với "<strong>{{ tuKhoa }}</strong>"</p>
          <button @click="clearSearch">Xem tất cả sân</button>
        </div>

      </div>
    </section>

  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'

// ===== HERO SLIDES =====
const heroSlides = [
  {
    bg: 'linear-gradient(135deg, #0a2540 0%, #163e6b 55%, #1f7a34 100%)',
    eyebrow: 'Hẻm 104 Tân Sơn, Tân Sơn, HCM',
    title: 'Chọn sân của bạn',
    sub: '8 sân chất lượng cao — Đặt lịch nhanh, không cần gọi điện',
    stat1: { val: '8', label: 'Sân' },
    stat2: { val: '4.8★', label: 'Đánh giá' },
    stat3: { val: '06–22h', label: 'Mỗi ngày' },
  },
  {
    bg: 'linear-gradient(135deg, #1f7a34 0%, #155c28 50%, #0a2540 100%)',
    eyebrow: 'Sân cỏ nhân tạo cao cấp',
    title: 'Sân 5 người & 7 người',
    sub: 'Cỏ nhân tạo thế hệ mới, đèn LED cao áp — thi đấu thoải mái cả ban đêm',
    stat1: { val: '4', label: 'Sân 5 người' },
    stat2: { val: '4', label: 'Sân 7 người' },
    stat3: { val: '100%', label: 'Cỏ nhân tạo' },
  },
  {
    bg: 'linear-gradient(135deg, #163e6b 0%, #1a3a5c 45%, #1f7a34 100%)',
    eyebrow: 'Đặt sân dễ dàng',
    title: 'Không cần gọi điện',
    sub: 'Chọn giờ, chọn sân, thanh toán online — xác nhận ngay trong 30 giây',
    stat1: { val: '30s', label: 'Xác nhận' },
    stat2: { val: '50%', label: 'Phí đặt cọc' },
    stat3: { val: '24/7', label: 'Hỗ trợ' },
  },
]

const currentSlide = ref(0)
let slideTimer = null

function nextSlide() {
  currentSlide.value = (currentSlide.value + 1) % heroSlides.length
}
function prevSlide() {
  currentSlide.value = (currentSlide.value - 1 + heroSlides.length) % heroSlides.length
}
function startAutoSlide() {
  slideTimer = setInterval(nextSlide, 4500)
}
function stopAutoSlide() {
  clearInterval(slideTimer)
}

// ===== DỮ LIỆU SÂN =====
const danhSachSan = [
  {
    id: 1, maSan: 'A1', ten: 'Sân A1 — 5 người', loai: 'san5',
    kichThuoc: '25 × 16m', gia: '350.000', rating: 4.9, soLuotDanhGia: 128,
    hot: true,
    gradient: 'linear-gradient(135deg, #0a2540, #1a4a7a)',
    tags: ['a1', 'san 5', 'sân 5', 'san5', 'san a', '5 nguoi', '5 người']
  },
  {
    id: 2, maSan: 'A2', ten: 'Sân A2 — 5 người', loai: 'san5',
    kichThuoc: '25 × 16m', gia: '350.000', rating: 4.8, soLuotDanhGia: 104,
    hot: false,
    gradient: 'linear-gradient(135deg, #163e6b, #20517f)',
    tags: ['a2', 'san 5', 'sân 5', 'san5', 'san a', '5 nguoi', '5 người']
  },
  {
    id: 3, maSan: 'A3', ten: 'Sân A3 — 5 người', loai: 'san5',
    kichThuoc: '25 × 16m', gia: '320.000', rating: 4.7, soLuotDanhGia: 89,
    hot: false,
    gradient: 'linear-gradient(135deg, #0e3258, #1b4f80)',
    tags: ['a3', 'san 5', 'sân 5', 'san5', 'san a', '5 nguoi', '5 người']
  },
  {
    id: 4, maSan: 'A4', ten: 'Sân A4 — 5 người', loai: 'san5',
    kichThuoc: '25 × 16m', gia: '350.000', rating: 4.8, soLuotDanhGia: 112,
    hot: true,
    gradient: 'linear-gradient(135deg, #1a3a5c, #2563a8)',
    tags: ['a4', 'san 5', 'sân 5', 'san5', 'san a', '5 nguoi', '5 người']
  },
  {
    id: 5, maSan: 'B1', ten: 'Sân B1 — 7 người', loai: 'san7',
    kichThuoc: '50 × 30m', gia: '650.000', rating: 4.9, soLuotDanhGia: 96,
    hot: true,
    gradient: 'linear-gradient(135deg, #1f7a34, #2c9b41)',
    tags: ['b1', 'san 7', 'sân 7', 'san7', 'san b', '7 nguoi', '7 người']
  },
  {
    id: 6, maSan: 'B2', ten: 'Sân B2 — 7 người', loai: 'san7',
    kichThuoc: '50 × 30m', gia: '650.000', rating: 4.8, soLuotDanhGia: 81,
    hot: false,
    gradient: 'linear-gradient(135deg, #2c9b41, #3fb454)',
    tags: ['b2', 'san 7', 'sân 7', 'san7', 'san b', '7 nguoi', '7 người']
  },
  {
    id: 7, maSan: 'B3', ten: 'Sân B3 — 7 người', loai: 'san7',
    kichThuoc: '50 × 30m', gia: '600.000', rating: 4.7, soLuotDanhGia: 74,
    hot: false,
    gradient: 'linear-gradient(135deg, #1a8a36, #1f7a34)',
    tags: ['b3', 'san 7', 'sân 7', 'san7', 'san b', '7 nguoi', '7 người']
  },
  {
    id: 8, maSan: 'B4', ten: 'Sân B4 — 7 người', loai: 'san7',
    kichThuoc: '50 × 30m', gia: '680.000', rating: 4.9, soLuotDanhGia: 117,
    hot: true,
    gradient: 'linear-gradient(135deg, #155c28, #1f7a34)',
    tags: ['b4', 'san 7', 'sân 7', 'san7', 'san b', '7 nguoi', '7 người']
  },
]

// ===== TABS =====
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
  if (tab === 'tat-ca') return danhSachSan.length
  return danhSachSan.filter(s => s.loai === tab).length
}

function doiTab(val) {
  activeTab.value = val
  tuKhoa.value = ''
  showSuggest.value = false
  suggestList.value = []
}

// ===== TÌM KIẾM =====
const tuKhoa = ref('')
const showSuggest = ref(false)
const suggestList = ref([])
const searchBoxRef = ref(null)

function locSan(q) {
  const query = q.trim().toLowerCase()
  if (!query) return []
  return danhSachSan.filter(san =>
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

// ===== DANH SÁCH HIỆN THỊ =====
const sanHienThi = computed(() => {
  let list = danhSachSan
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

// ===== YÊU THÍCH =====
const sanYeuThich = ref([])
function toggleYeuThich(id) {
  const idx = sanYeuThich.value.indexOf(id)
  if (idx === -1) sanYeuThich.value.push(id)
  else sanYeuThich.value.splice(idx, 1)
}

// Click ngoài đóng suggest
function onClickOutside(e) {
  if (searchBoxRef.value && !searchBoxRef.value.contains(e.target)) {
    showSuggest.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', onClickOutside)
  startAutoSlide()
})
onUnmounted(() => {
  document.removeEventListener('click', onClickOutside)
  stopAutoSlide()
})
</script>

<style scoped>
.san-bong-page { background: var(--gray-50); min-height: 100vh; }

/* ===== HERO SLIDER ===== */
.hero {
  position: relative;
  overflow: hidden;
  height: 220px;
}

.hero-slides {
  position: relative;
  width: 100%;
  height: 100%;
}

.hero-slide {
  position: absolute;
  inset: 0;
  padding: 40px 0 36px;
  opacity: 0;
  transition: opacity 0.7s ease;
  pointer-events: none;
}

.hero-slide--active {
  opacity: 1;
  pointer-events: auto;
}

.hero__inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 20px;
  height: 100%;
}

.eyebrow {
  display: inline-flex; align-items: center; gap: 6px;
  font-size: 11px; font-weight: 700; text-transform: uppercase;
  letter-spacing: 0.08em; color: var(--green-400, #4ade80);
  margin-bottom: 8px;
}

.hero__title {
  font-family: var(--font-score, inherit);
  font-size: clamp(22px, 3.5vw, 38px);
  font-weight: 700; color: white; line-height: 1.15;
}

.hero__sub {
  margin-top: 8px; font-size: 14px;
  color: rgba(255,255,255,0.7); line-height: 1.5;
}

.hero__stats {
  display: flex; align-items: center;
  background: rgba(255,255,255,0.08);
  border: 1px solid rgba(255,255,255,0.15);
  border-radius: 14px; padding: 14px 20px;
  backdrop-filter: blur(8px); flex-shrink: 0;
}

.hero__stat {
  display: flex; flex-direction: column; align-items: center; gap: 3px;
  padding: 0 20px;
}
.hero__stat strong {
  font-size: 20px; font-weight: 700; color: white;
}
.hero__stat span { font-size: 11px; color: rgba(255,255,255,0.6); }
.hero__stat-divider { width: 1px; height: 36px; background: rgba(255,255,255,0.2); }

/* Dots */
.hero-dots {
  position: absolute; bottom: 12px; left: 50%; transform: translateX(-50%);
  display: flex; gap: 7px; z-index: 10;
}
.hero-dot {
  width: 8px; height: 8px; border-radius: 50%;
  background: rgba(255,255,255,0.4); border: none; cursor: pointer;
  transition: all 0.25s; padding: 0;
}
.hero-dot--active {
  background: white; width: 22px; border-radius: 4px;
}

/* Arrows */
.hero-arrow {
  position: absolute; top: 50%; transform: translateY(-50%);
  width: 36px; height: 36px; border-radius: 50%;
  background: rgba(0,0,0,0.3); border: none; cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  z-index: 10; transition: background 0.2s;
}
.hero-arrow:hover { background: rgba(0,0,0,0.55); }
.hero-arrow--prev { left: 16px; }
.hero-arrow--next { right: 16px; }

/* ===== TOOLBAR ===== */
.toolbar {
  background: white;
  box-shadow: 0 2px 16px rgba(10,37,64,0.08);
  position: sticky; top: 60px; z-index: 50;
}

.toolbar__inner {
  display: flex; align-items: center; gap: 20px;
  padding: 14px 0; flex-wrap: wrap;
}

.search-box {
  position: relative; display: flex; align-items: center; gap: 8px;
  flex: 1; max-width: 400px;
  background: var(--gray-50); border: 1.5px solid var(--gray-200, #e2e8e5);
  border-radius: 999px; padding: 0 6px 0 14px; height: 44px;
  transition: border-color 0.15s;
}
.search-box:focus-within { border-color: var(--green-600); background: white; }

.search-box input {
  flex: 1; border: none; background: transparent; outline: none;
  font-size: 14px; font-family: inherit; color: var(--navy-900);
}
.search-box input::placeholder { color: var(--gray-400, #9ca3af); }

.search-clear {
  background: none; border: none; cursor: pointer; color: var(--gray-400);
  display: flex; align-items: center; padding: 4px; transition: color 0.15s;
}
.search-clear:hover { color: var(--navy-900); }

.search-suggest {
  position: absolute; top: calc(100% + 8px); left: 0; right: 0;
  background: white; border-radius: 14px;
  box-shadow: 0 16px 48px rgba(10,37,64,0.16);
  border: 1px solid var(--gray-100); overflow: hidden; z-index: 200;
}

.search-suggest__item {
  display: flex; align-items: center; gap: 12px;
  padding: 12px 16px; cursor: pointer; transition: background 0.12s;
}
.search-suggest__item:hover { background: var(--gray-50); }

.suggest-badge {
  width: 38px; height: 38px; border-radius: 10px; flex-shrink: 0;
  display: flex; align-items: center; justify-content: center;
  font-size: 12px; font-weight: 800;
}
.badge--blue { background: #dbeafe; color: #1d4ed8; }
.badge--green { background: var(--green-50); color: var(--green-700); }

.suggest-info { flex: 1; }
.suggest-ten { font-size: 14px; font-weight: 600; color: var(--navy-900); }
.suggest-loai { font-size: 12px; color: var(--gray-500); margin-top: 2px; }

.suggest-stars {
  display: flex; align-items: center; gap: 3px;
  font-size: 12px; font-weight: 700; color: var(--navy-900);
}

.search-suggest__all {
  padding: 10px 16px; font-size: 13px; color: var(--green-600);
  border-top: 1px solid var(--gray-100); cursor: pointer; transition: background 0.12s;
}
.search-suggest__all:hover { background: var(--gray-50); }

.filter-tabs { display: flex; gap: 8px; flex-wrap: wrap; }

.filter-tab {
  display: inline-flex; align-items: center; gap: 7px;
  padding: 9px 18px; border-radius: 999px;
  font-size: 13.5px; font-weight: 600; font-family: inherit;
  border: 1.5px solid var(--gray-200, #e2e8e5);
  background: white; color: var(--gray-600); cursor: pointer;
  transition: all 0.15s; white-space: nowrap;
}
.filter-tab:hover { border-color: var(--green-600); color: var(--green-600); }
.filter-tab--active {
  background: var(--green-600); border-color: var(--green-600);
  color: white; box-shadow: 0 4px 12px rgba(44,155,65,0.3);
}
.filter-tab__count {
  background: rgba(0,0,0,0.1); border-radius: 999px;
  padding: 1px 8px; font-size: 11px;
}
.filter-tab--active .filter-tab__count { background: rgba(255,255,255,0.25); }

/* ===== DANH SÁCH SÂN ===== */
.san-list { padding: 36px 0 72px; }

.list-header { margin-bottom: 28px; }

.list-header__title {
  font-size: 22px; font-weight: 700; color: var(--navy-900);
  display: flex; align-items: center; gap: 12px;
}

.list-header__count {
  font-size: 14px; font-weight: 600; color: var(--green-600);
  background: var(--green-50); padding: 3px 12px; border-radius: 999px;
}

.list-header__addr {
  display: flex; align-items: center; gap: 5px;
  margin-top: 6px; font-size: 13px; color: var(--gray-600);
}

/* Grid */
.san-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}
@media (max-width: 1200px) { .san-grid { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 860px)  { .san-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 540px)  { .san-grid { grid-template-columns: 1fr; } }

/* Card */
.san-card {
  background: white; border-radius: 16px; overflow: visible;
  box-shadow: 0 2px 12px rgba(10,37,64,0.08);
  transition: transform 0.2s, box-shadow 0.2s;
  display: flex; flex-direction: column;
}
.san-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 36px rgba(10,37,64,0.16);
}

.san-card__img {
  position: relative; height: 160px; overflow: hidden; flex-shrink: 0;
}
.pitch-svg {
  position: absolute; inset: 0; width: 100%; height: 100%;
}

.san-card__badges {
  position: absolute; top: 12px; left: 12px;
  display: flex; gap: 6px; z-index: 1;
}
.badge-loai {
  font-size: 11px; font-weight: 700; padding: 3px 10px; border-radius: 999px;
}
.badge-loai--blue { background: rgba(219,234,254,0.95); color: #1d4ed8; }
.badge-loai--green { background: rgba(220,252,231,0.95); color: #15803d; }
.badge-hot {
  font-size: 11px; font-weight: 700; padding: 3px 10px; border-radius: 999px;
  background: rgba(254,226,226,0.95); color: #dc2626;
}

.san-card__ma {
  position: absolute; bottom: 12px; left: 14px;
  font-size: 32px; font-weight: 800;
  color: rgba(255,255,255,0.9); line-height: 1;
  text-shadow: 0 2px 8px rgba(0,0,0,0.4); z-index: 1;
}

.san-card__rating {
  position: absolute; bottom: 14px; right: 12px;
  display: flex; align-items: center; gap: 4px;
  background: rgba(0,0,0,0.5); backdrop-filter: blur(4px);
  color: white; font-size: 12px; font-weight: 700;
  padding: 4px 10px; border-radius: 999px; z-index: 1;
}
.san-card__rating span { font-weight: 400; opacity: 0.8; font-size: 11px; }

.san-card__fav {
  position: absolute; top: 12px; right: 12px; z-index: 1;
  width: 32px; height: 32px; border-radius: 50%;
  background: rgba(0,0,0,0.35); border: none;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; transition: background 0.15s;
}
.san-card__fav:hover { background: rgba(229,62,62,0.6); }
.san-card__fav--active { background: rgba(229,62,62,0.7); }

/* Body — dùng flex để footer luôn xuống đáy */
.san-card__body {
  padding: 14px 16px 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.san-card__top-row {
  display: flex; align-items: flex-start;
  justify-content: space-between; gap: 8px;
}

.san-card__ten {
  font-size: 14px; font-weight: 700; color: var(--navy-900); line-height: 1.3;
}

.san-card__stars-row {
  display: flex; align-items: center; gap: 2px; flex-shrink: 0;
}
.san-card__rating-text {
  font-size: 11px; color: var(--gray-500); margin-left: 4px; white-space: nowrap;
}

.san-card__dia-chi {
  display: flex; align-items: center; gap: 4px;
  margin-top: 5px; font-size: 11px; color: var(--gray-500); line-height: 1.4;
}

.san-card__specs {
  display: flex; flex-wrap: wrap; gap: 5px; margin-top: 10px;
}
.spec-item {
  display: inline-flex; align-items: center; gap: 4px;
  font-size: 11px; color: var(--navy-900); font-weight: 500;
  background: var(--gray-50); border: 1px solid var(--gray-100);
  padding: 3px 8px; border-radius: 999px;
}

/* ✅ Footer: margin-top: auto đẩy xuống đáy, không bị cắt */
.san-card__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: auto;
  padding-top: 12px;
  padding-bottom: 2px;
  border-top: 1px solid var(--gray-100);
  gap: 8px;
}

.san-card__gia { display: flex; align-items: baseline; gap: 3px; flex-shrink: 0; }
.gia-so { font-size: 15px; font-weight: 800; color: var(--navy-900); }
.gia-label { font-size: 11px; color: var(--gray-500); }

/* ✅ Nút không bị cắt: flex-shrink: 0 */
.san-card__actions {
  display: flex;
  gap: 6px;
  flex-shrink: 0;
}

.btn-chitiet {
  display: inline-flex; align-items: center;
  padding: 7px 13px; border-radius: 999px;
  border: 1.5px solid var(--gray-200, #e2e8e5);
  background: white; color: var(--navy-900);
  font-size: 12px; font-weight: 600; font-family: inherit;
  cursor: pointer; transition: all 0.15s; white-space: nowrap;
  text-decoration: none;
}
.btn-chitiet:hover { border-color: var(--navy-900); }

.btn-datsan {
  display: inline-flex; align-items: center;
  padding: 7px 14px; background: var(--green-600); color: white;
  border-radius: 999px; font-size: 12px; font-weight: 700;
  transition: background 0.15s; white-space: nowrap;
  text-decoration: none; border: none;
}
.btn-datsan:hover { background: var(--green-700); }

/* No result */
.no-result {
  text-align: center; padding: 80px 20px;
  display: flex; flex-direction: column; align-items: center; gap: 16px;
}
.no-result p { font-size: 15px; color: var(--gray-600); }
.no-result button {
  padding: 10px 24px; background: var(--green-600); color: white;
  border: none; border-radius: 999px; font-size: 14px; font-weight: 600;
  font-family: inherit; cursor: pointer;
}

@media (max-width: 640px) {
  .toolbar__inner { flex-direction: column; align-items: stretch; }
  .search-box { max-width: none; }
  .hero__stats { width: 100%; justify-content: center; }
  .hero { height: auto; min-height: 200px; }
}
</style>