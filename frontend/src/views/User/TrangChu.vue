<template>
  <div class="pitch" :style="{ '--sx': spotlight.x + '%', '--sy': spotlight.y + '%' }">

    <!-- ===== LỚP NỀN CHUNG: sân đêm, đèn pha, hạt sáng, đạo cụ trôi nổi ===== -->
    <div class="pitch__ambient" aria-hidden="true">
      <div class="floodlight floodlight--l"></div>
      <div class="floodlight floodlight--r"></div>
      <div class="grass-drift"></div>
      <div class="motes">
        <span v-for="n in 16" :key="n" class="mote" :style="{ '--i': n }"></span>
      </div>
      <div class="field-props">
        <div class="prop prop--ball"><svg viewBox="0 0 40 40"><circle cx="20" cy="20" r="18" fill="var(--chalk-050)" stroke="var(--night-950)" stroke-width="1.4"/><polygon points="20,10 26,14.5 24,21.5 16,21.5 14,14.5" fill="var(--night-950)"/></svg></div>
        <div class="prop prop--whistle"><svg viewBox="0 0 48 32" fill="none"><path d="M14 16a10 10 0 1 0 20 0 10 10 0 0 0-20 0Z" stroke="var(--lime-300)" stroke-width="1.6"/><circle cx="24" cy="16" r="3.2" fill="var(--lime-300)"/></svg></div>
        <div class="prop prop--flag"><svg viewBox="0 0 30 46" fill="none"><path d="M4 44V4" stroke="var(--lime-300)" stroke-width="1.8" stroke-linecap="round"/><path d="M4 5l20 6-20 6V5Z" fill="var(--lime-300)" opacity=".85"/></svg></div>
        <div class="prop prop--trophy"><svg viewBox="0 0 40 46" fill="none"><path d="M11 6h18v10a9 9 0 0 1-18 0V6Z" stroke="var(--amber-400)" stroke-width="1.6"/><path d="M20 25v6M13 37h14l-1.6-4.5a2 2 0 0 0-1.9-1.5h-6.9a2 2 0 0 0-1.9 1.5L13 37Z" stroke="var(--amber-400)" stroke-width="1.6" stroke-linejoin="round"/></svg></div>
      </div>
    </div>

    <!-- ===== HERO: SÂN ĐÊM ===== -->
    <section class="hero" @mousemove="onHeroMove" @mouseleave="onHeroLeave">
      <div class="hero__ticker" aria-hidden="true">
        <div class="hero__ticker-track">
          <span v-for="n in 2" :key="n">
            &nbsp;●&nbsp; SÂN A2 CÒN TRỐNG 19:00–20:00 &nbsp;●&nbsp; SÂN B1 GIẢM 20% KHUNG GIỜ TRƯA &nbsp;●&nbsp; ĐẶT SÂN B3 TẶNG NƯỚC UỐNG &nbsp;●&nbsp; 3 SÂN 5 · 3 SÂN 7 ĐANG MỞ &nbsp;●&nbsp; HOTLINE 0343 831 316&nbsp;
          </span>
        </div>
      </div>

      <div class="container hero__grid">
        <div class="hero__copy">
          <span class="eyebrow reveal">⚽ Hẻm 104 Tân Sơn · Mở cửa 06:00–22:00</span>
          <h1 class="headline reveal" style="--d:.1s">
            Giữ sân trong <span class="headline__accent">30 giây</span>,<br />
            ra sân tối nay
          </h1>
          <p class="desc reveal" style="--d:.2s">
            6 sân cỏ nhân tạo chuẩn thi đấu — 3 sân 5 người (A1–A3) và 3 sân 7 người (B1–B3).
            Xem lịch trống thời gian thực, đặt và thanh toán ngay trên điện thoại.
          </p>
          <div class="hero__cta reveal" style="--d:.3s">
            <router-link to="/san" class="btn-primary">
              <span>Đặt sân ngay</span>
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none"><path d="M5 12H19M19 12L13 6M19 12L13 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
            </router-link>
            <router-link to="/san" class="btn-ghost">Xem tất cả sân</router-link>
          </div>

          <div class="hero__slidecopy reveal" style="--d:.4s">
            <Transition name="slide-fade" mode="out-in">
              <p :key="slideHienTai">{{ slides[slideHienTai].desc }}</p>
            </Transition>
            <div class="hero__dots">
              <button v-for="(_, i) in slides" :key="i" class="hero__dot" :class="{ 'hero__dot--active': slideHienTai === i }" @click="slideHienTai = i"></button>
            </div>
          </div>
        </div>

        <div class="hero__scene">
          <svg viewBox="0 0 480 420" class="night-pitch">
            <defs>
              <radialGradient id="fl1" cx="50%" cy="0%" r="80%">
                <stop offset="0%" stop-color="#fff4d6" stop-opacity="0.35"/>
                <stop offset="100%" stop-color="#fff4d6" stop-opacity="0"/>
              </radialGradient>
              <linearGradient id="turf" x1="0" y1="0" x2="0" y2="1">
                <stop offset="0%" stop-color="#123321"/>
                <stop offset="100%" stop-color="#0a1f13"/>
              </linearGradient>
            </defs>

            <!-- khán đài + đèn pha -->
            <rect x="0" y="0" width="480" height="420" fill="var(--night-950)"/>
            <path d="M0 40 Q60 10 120 40 T240 40 T360 40 T480 40 V0 H0 Z" fill="#0d2417"/>
            <g class="stand-lights">
              <circle v-for="n in 12" :key="n" :cx="20 + n*38" cy="24" r="1.6" fill="var(--lime-300)" :style="{ '--i': n }" class="stand-dot"/>
            </g>
            <rect x="40" y="-10" width="16" height="70" fill="#0d2417"/>
            <rect x="424" y="-10" width="16" height="70" fill="#0d2417"/>
            <ellipse cx="48" cy="10" rx="60" ry="50" fill="url(#fl1)" class="beam beam--l"/>
            <ellipse cx="432" cy="10" rx="60" ry="50" fill="url(#fl1)" class="beam beam--r"/>

            <!-- mặt sân -->
            <rect x="20" y="70" width="440" height="330" rx="10" fill="url(#turf)" stroke="rgba(247,251,244,.12)"/>
            <line x1="240" y1="70" x2="240" y2="400" stroke="rgba(247,251,244,.22)" class="draw"/>
            <circle cx="240" cy="235" r="46" stroke="rgba(247,251,244,.22)" fill="none" class="draw"/>
            <rect x="20" y="150" width="70" height="170" stroke="rgba(247,251,244,.22)" fill="none" class="draw"/>
            <rect x="390" y="150" width="70" height="170" stroke="rgba(247,251,244,.22)" fill="none" class="draw"/>
            <!-- khung thành + lưới -->
            <rect x="20" y="205" width="10" height="60" fill="none" stroke="var(--chalk-200)" stroke-width="1.5"/>
            <g stroke="rgba(247,251,244,.35)" stroke-width="0.6">
              <line v-for="n in 6" :key="'h'+n" :x1="20" :y1="205 + n*10" x2="30" :y2="205 + n*10"/>
              <line v-for="n in 5" :key="'v'+n" :x1="20 + n*2" y1="205" :x2="20 + n*2" y2="265"/>
            </g>

            <!-- cầu thủ -->
            <g class="player" style="--pi:0"><circle cx="150" cy="300" r="7" fill="var(--night-950)" stroke="var(--chalk-050)" stroke-width="1.6"/></g>
            <g class="player" style="--pi:1"><circle cx="220" cy="240" r="7" fill="var(--night-950)" stroke="var(--chalk-050)" stroke-width="1.6"/></g>
            <g class="player" style="--pi:2"><circle cx="300" cy="180" r="7" fill="var(--night-950)" stroke="var(--lime-400)" stroke-width="1.8"/></g>
            <g class="player" style="--pi:3"><circle cx="70" cy="235" r="7" fill="var(--night-950)" stroke="#ff6a56" stroke-width="1.6"/></g>

            <path id="shotPath" d="M150,300 C185,275 200,255 220,240 C250,220 270,200 300,180 C325,165 340,145 355,120" class="draw shot-path"/>
            <circle r="4.5" fill="var(--lime-400)" class="ball">
              <animateMotion dur="3.4s" repeatCount="indefinite" rotate="auto">
                <mpath href="#shotPath"/>
              </animateMotion>
            </circle>

            <!-- bảng tỉ số góc -->
            <g class="scoreboard" transform="translate(300,300)">
              <rect width="150" height="70" rx="8" fill="rgba(5,11,8,.75)" stroke="var(--lime-400)" stroke-width="1.2"/>
              <text x="14" y="24" class="sb-text sb-label">A2 · B1</text>
              <text x="14" y="52" class="sb-text sb-score">19:00</text>
              <text x="90" y="52" class="sb-text sb-live">● TRỐNG</text>
            </g>
          </svg>
        </div>
      </div>
    </section>

    <!-- ===== THÔNG TIN SÂN NỔI BẬT ===== -->
    <section class="info-strip">
      <div class="container info-strip__grid">
        <div class="info-card reveal" v-for="(it, i) in thongTinSan" :key="it.label" :style="{ '--d': (i*0.08)+'s' }">
          <div class="info-card__icon" v-html="it.icon"></div>
          <div>
            <p class="info-card__label">{{ it.label }}</p>
            <p class="info-card__val">{{ it.val }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- ===== THỐNG KÊ / SCOREBOARD ===== -->
    <section class="stats" ref="statsSection">
      <div class="container stats__grid">
        <div class="stat" v-for="(s, i) in thongKe" :key="s.label">
          <div class="stat__icon" v-html="s.icon"></div>
          <p class="stat__num">{{ statsDisplay[i] }}<span>{{ s.suffix }}</span></p>
          <p class="stat__label">{{ s.label }}</p>
        </div>
      </div>
    </section>

    <!-- ===== LOẠI SÂN ===== -->
    <section class="section loai-san">
      <div class="container">
        <div class="section-head reveal">
          <div>
            <span class="eyebrow">Loại sân</span>
            <h2 class="section-title">2 loại sân tại DreamLeague</h2>
          </div>
          <router-link to="/san" class="see-all">Xem tất cả →</router-link>
        </div>

        <div class="loai-san__grid">
          <div class="pitch-card reveal" v-for="(ls, i) in loaiSanList" :key="ls.id" :style="{ '--d': (i*0.1)+'s' }" @click="router.push('/san')">
            <div class="pitch-card__media">
              <svg viewBox="0 0 320 170" class="mini-pitch">
                <rect x="3" y="3" width="314" height="164" rx="10" :fill="ls.turf"/>
                <line x1="160" y1="3" x2="160" y2="167" stroke="rgba(255,255,255,.35)"/>
                <circle cx="160" cy="85" r="26" stroke="rgba(255,255,255,.4)" fill="none"/>
                <rect x="3" y="55" width="30" height="60" stroke="rgba(255,255,255,.4)" fill="none"/>
                <rect x="287" y="55" width="30" height="60" stroke="rgba(255,255,255,.4)" fill="none"/>
                <circle v-for="n in 5" :key="n" :cx="30 + n*55" cy="18" r="1.3" fill="rgba(255,255,255,.5)"/>
              </svg>
              <span class="pitch-card__badge">{{ ls.badge }}</span>
              <span class="pitch-card__price">Từ {{ ls.giaFrom }}</span>
            </div>
            <div class="pitch-card__body">
              <h3>{{ ls.title }}</h3>
              <p>{{ ls.mo_ta }}</p>
              <div class="pitch-card__tags">
                <span v-for="ten in ls.tenSan" :key="ten" class="tag-pill">{{ ten }}</span>
              </div>
              <ul>
                <li v-for="spec in ls.specs" :key="spec">{{ spec }}</li>
              </ul>
              <router-link to="/san" class="btn-dark">Đặt sân ngay</router-link>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- ===== SÂN NỔI BẬT ===== -->
    <section class="section san-noi-bat">
      <div class="container">
        <div class="section-head reveal">
          <div>
            <span class="eyebrow">Đặt nhiều nhất</span>
            <h2 class="section-title">Sân nổi bật tuần này</h2>
          </div>
          <router-link to="/san" class="see-all">Xem tất cả →</router-link>
        </div>

        <div class="san-grid">
          <div class="san-card reveal" v-for="(san, i) in sanNoiBat" :key="san.id" :style="{ '--d': (i*0.08)+'s' }">
            <div class="san-card__media">
              <svg viewBox="0 0 300 140" class="mini-pitch">
                <rect x="2" y="2" width="296" height="136" rx="8" :fill="san.mau"/>
                <line x1="150" y1="2" x2="150" y2="138" stroke="rgba(255,255,255,.35)"/>
                <circle cx="150" cy="70" r="22" stroke="rgba(255,255,255,.4)" fill="none"/>
              </svg>
              <span class="san-card__loai">{{ san.loai }}</span>
              <span class="san-card__rating">★ {{ san.rating }}</span>
              <button class="san-card__fav" aria-label="Yêu thích">
                <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/></svg>
              </button>
            </div>
            <div class="san-card__body">
              <div class="san-card__row">
                <h3>{{ san.ten }}</h3>
                <span class="san-card__ma">{{ san.maSan }}</span>
              </div>
              <p class="san-card__dc">{{ san.diaChi }}</p>
              <div class="san-card__footer">
                <div><span class="muted">Từ</span> <strong>{{ san.gia }}đ</strong> <span class="muted">/giờ</span></div>
                <router-link :to="`/dat-san/${san.id}`" class="btn-small">Đặt sân</router-link>
              </div>
            </div>
          </div>
          <p v-if="!sanNoiBat.length" class="empty-note reveal">Đang tải danh sách sân…</p>
        </div>
      </div>
    </section>

    <!-- ===== THƯ VIỆN HÌNH ẢNH SÂN ===== -->
    <section class="section gallery-section">
      <div class="container">
        <div class="section-head reveal">
          <div>
            <span class="eyebrow">Tại DreamLeague</span>
            <h2 class="section-title">Một vòng quanh sân</h2>
          </div>
        </div>
        <div class="gallery-grid">
          <button
            class="gallery-tile reveal"
            v-for="(g, i) in thuVienAnh"
            :key="g.title"
            :style="{ '--d': (i*0.06)+'s' }"
            @click="selectedGallery = g"
          >
            <div class="gallery-tile__art" v-html="g.svg"></div>
            <span class="gallery-tile__label">{{ g.title }}</span>
          </button>
        </div>
      </div>
    </section>

    <!-- Modal thư viện -->
    <div class="lightbox" v-if="selectedGallery" @click.self="selectedGallery = null">
      <div class="lightbox__panel">
        <button class="lightbox__close" @click="selectedGallery = null">&times;</button>
        <div class="lightbox__art" v-html="selectedGallery.svg"></div>
        <h3>{{ selectedGallery.title }}</h3>
        <p>{{ selectedGallery.desc }}</p>
      </div>
    </div>

    <!-- ===== SECTION BÀI VIẾT ===== -->
    <section class="section posts-section" v-if="danhSachBaiViet.length > 0">
      <div class="container">
        <div class="section-head reveal">
          <div>
            <span class="eyebrow">Truyền thông & Tin tức</span>
            <h2 class="section-title">Bài viết mới nhất</h2>
          </div>
        </div>

        <div class="posts-grid">
          <div class="post-card reveal" v-for="(post, i) in danhSachBaiViet" :key="post.id" :style="{ '--d': (i*0.08)+'s' }" @click="xemChiTietBaiViet(post)">
            <div class="post-card__thumb">
              <img :src="post.thumbnailUrl || 'https://placehold.co/400x220/0a1f13/b6ff3c?text=DreamLeague'" :alt="post.title" />
              <span class="post-card__category" :class="getCategoryClass(post.category)">{{ getCategoryLabel(post.category) }}</span>
            </div>
            <div class="post-card__content">
              <div class="post-card__meta">
                <span>👤 {{ post.authorName || 'Ban Quản Lý' }}</span>
                <span class="dot">•</span>
                <span>📅 {{ formatDate(post.createdAt) }}</span>
                <span class="dot">•</span>
                <span>👁️ {{ post.viewsCount || 0 }}</span>
              </div>
              <h3 class="post-card__title">{{ post.title }}</h3>
              <p class="post-card__excerpt">{{ post.summary || getShortSummary(post.content) }}</p>
              <div class="post-card__link">
                <span>Đọc bài viết</span>
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/></svg>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- ===== MODAL XEM CHI TIẾT BÀI VIẾT ===== -->
    <div class="post-modal" v-if="selectedPost" @click.self="selectedPost = null">
      <div class="post-modal__content">
        <button class="post-modal__close" @click="selectedPost = null">&times;</button>
        <div class="post-modal__header">
          <span class="post-card__category" :class="getCategoryClass(selectedPost.category)">{{ getCategoryLabel(selectedPost.category) }}</span>
          <h2>{{ selectedPost.title }}</h2>
          <div class="post-card__meta">
            <span>👤 {{ selectedPost.authorName || 'Ban Quản Lý' }}</span>
            <span class="dot">•</span>
            <span>📅 {{ formatDate(selectedPost.createdAt) }}</span>
            <span class="dot">•</span>
            <span>👁️ {{ selectedPost.viewsCount || 0 }}</span>
          </div>
        </div>
        <div class="post-modal__body" v-html="selectedPost.content"></div>
      </div>
    </div>

    <!-- ===== CÁCH ĐẶT SÂN ===== -->
    <section class="section how-it-works">
      <div class="container">
        <div class="section-head section-head--center reveal">
          <span class="eyebrow">Quy trình</span>
          <h2 class="section-title">Đặt sân chỉ 3 bước</h2>
          <p class="section-sub">Nhanh chóng, tiện lợi — không cần gọi điện</p>
        </div>
        <div class="steps">
          <div class="step reveal" v-for="(s, i) in cacBuoc" :key="s.title" :style="{ '--d': (i*0.12)+'s' }">
            <div class="step__circle">
              <span class="step__num">{{ i + 1 }}</span>
              <div class="step__icon" v-html="s.icon"></div>
            </div>
            <div v-if="i < cacBuoc.length - 1" class="step__line"></div>
            <h3>{{ s.title }}</h3>
            <p>{{ s.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- ===== ĐÁNH GIÁ ===== -->
    <section class="section review-section">
      <div class="container">
        <div class="section-title-container reveal">
          <span class="eyebrow">Cộng đồng DreamLeague</span>
          <h2 class="section-title">Đánh giá từ khách hàng</h2>
          <p>Những phản hồi chân thực từ cộng đồng trải nghiệm dịch vụ sân bóng</p>
        </div>

        <div class="review-filter-bar reveal">
          <button class="filter-btn" :class="{ active: selectedFilter === 'ALL' }" @click="selectedFilter = 'ALL'">Tất cả ({{ getCountByStar('ALL') }})</button>
          <button v-for="star in [5,4,3,2,1]" :key="star" class="filter-btn" :class="{ active: selectedFilter === star }" @click="selectedFilter = star">{{ star }} ★ ({{ getCountByStar(star) }})</button>
        </div>

        <div class="review-list-container">
          <div v-if="filteredDanhGiaList && filteredDanhGiaList.length > 0" class="review-scroll-wrapper">
            <div v-for="item in filteredDanhGiaList" :key="item.id" class="review-card">
              <div class="card-header">
                <div class="user-meta">
                  <h4 class="user-name">{{ item.name || 'Khách hàng' }}</h4>
                  <div class="sub-info">
                    <span class="user-role">{{ item.role || 'Khách hàng' }}</span>
                    <span class="dot">•</span>
                    <span class="review-date">{{ formatDate(item.ngayDanhGia) }}</span>
                  </div>
                </div>
                <div class="star-rating">
                  <span v-for="s in 5" :key="s" :class="['star', { filled: s <= item.rating }]">★</span>
                </div>
              </div>
              <p class="review-quote">"{{ item.quote }}"</p>
              <div v-if="item.phanHoi" class="reply-box">
                <div class="reply-user-info">
                  <span class="reply-icon">💬</span>
                  <span class="replier-name">{{ item.tenNguoiPhanHoi || 'Đông Quân' }}</span>
                  <span class="role-badge" :class="item.vaiTroNguoiPhanHoi?.toLowerCase() || 'staff'">{{ item.vaiTroNguoiPhanHoi || 'STAFF' }}</span>
                </div>
                <div class="reply-time">{{ formatDate(item.ngayPhanHoi || item.ngayDanhGia) }}</div>
                <p class="reply-content">{{ item.phanHoi }}</p>
              </div>
            </div>
          </div>
          <div v-else class="empty-review-state">
            <div class="empty-icon">💬</div>
            <h3>Không có đánh giá nào</h3>
            <p>Chưa có đánh giá nào phù hợp với bộ lọc này.</p>
          </div>
        </div>

        <div class="add-review-wrapper reveal">
          <div class="add-review-form">
            <h3>Viết đánh giá của bạn</h3>
            <p class="sub-title">Chia sẻ trải nghiệm thực tế của bạn sau khi trải nghiệm sân đấu</p>
            <form @submit.prevent="guiDanhGia">
              <div class="star-rating-input">
                <div class="stars">
                  <span v-for="star in 5" :key="star" class="star-icon" :class="{ active: star <= soSaoDanhGia }" @click="soSaoDanhGia = star">★</span>
                </div>
                <span class="rating-text">({{ soSaoDanhGia }} / 5 sao)</span>
              </div>
              <div class="textarea-wrapper">
                <textarea v-model="noiDungBinhLuan" placeholder="Sân đấu có tốt không? Đèn chiếu sáng và chất lượng cỏ thế nào?..." rows="4" maxlength="500" required></textarea>
                <div class="char-count">{{ noiDungBinhLuan.length }}/500</div>
              </div>
              <button type="submit" class="btn-primary btn-primary--wide" :disabled="isSubmitting">
                {{ isSubmitting ? 'Đang gửi...' : 'Gửi đánh giá' }}
              </button>
            </form>
          </div>
        </div>
      </div>
    </section>

    <!-- ===== CTA ===== -->
    <section class="section cta-section">
      <div class="container">
        <div class="cta-panel reveal">
          <div class="cta-panel__glow" aria-hidden="true"></div>
          <div class="cta-panel__left">
            <h2>Sẵn sàng cho trận đấu tiếp theo?</h2>
            <p>Đăng ký miễn phí — đặt sân trong 30 giây</p>
          </div>
          <div class="cta-panel__right">
            <router-link to="/dang-ky" class="btn-primary">Tạo tài khoản miễn phí</router-link>
            <router-link to="/san" class="btn-ghost">Tìm sân ngay</router-link>
          </div>
        </div>
      </div>
    </section>

  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const currentUser = JSON.parse(localStorage.getItem('user') || '{}')

/* ============ SPOTLIGHT CON TRỎ TRÊN HERO ============ */
const spotlight = reactive({ x: 50, y: 30 })
function onHeroMove(e) {
  const rect = e.currentTarget.getBoundingClientRect()
  spotlight.x = ((e.clientX - rect.left) / rect.width) * 100
  spotlight.y = ((e.clientY - rect.top) / rect.height) * 100
}
function onHeroLeave() { spotlight.x = 50; spotlight.y = 30 }

/* ============ BANNER SLIDER (giữ dữ liệu, đổi cách hiển thị) ============ */
const slides = [
  { desc: 'Đặt sân nhanh chóng — Thanh toán tiện lợi — Hỗ trợ 24/7.' },
  { desc: 'Hẻm 104 Tân Sơn, Tân Sơn, Hồ Chí Minh — Mở cửa 06:00 đến 22:00.' },
  { desc: 'Giải phong trào & team building — Ưu đãi riêng cho nhóm từ 10 người.' }
]
const slideHienTai = ref(0)
let slideInterval = null

/* ============ ĐÁNH GIÁ ============ */
const danhGiaList = ref([])
const noiDungBinhLuan = ref('')
const soSaoDanhGia = ref(5)
const isSubmitting = ref(false)
const selectedFilter = ref('ALL')

const filteredDanhGiaList = computed(() => {
  if (selectedFilter.value === 'ALL') return danhGiaList.value
  return danhGiaList.value.filter(item => item.rating === selectedFilter.value)
})
const getCountByStar = (star) => {
  if (star === 'ALL') return danhGiaList.value.length
  return danhGiaList.value.filter(item => item.rating === star).length
}

/* ============ THÔNG TIN NỔI BẬT ============ */
const thongTinSan = [
  { label: 'Địa chỉ', val: 'Hẻm 104 Tân Sơn, Tân Sơn, Hồ Chí Minh',
    icon: `<svg width="20" height="20" viewBox="0 0 24 24" fill="none"><path d="M12 22s7-7.58 7-12.5A7 7 0 1 0 5 9.5C5 14.42 12 22 12 22Z" stroke="var(--lime-300)" stroke-width="1.8"/><circle cx="12" cy="9.5" r="2.5" stroke="var(--lime-300)" stroke-width="1.8"/></svg>` },
  { label: 'Giờ hoạt động', val: '06:00 — 22:00 (Tất cả các ngày)',
    icon: `<svg width="20" height="20" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="9" stroke="var(--lime-300)" stroke-width="1.8"/><path d="M12 7V12L15 14" stroke="var(--lime-300)" stroke-width="1.8" stroke-linecap="round"/></svg>` },
  { label: 'Số sân hiện có', val: '3 sân 5 (A1, A2, A3) · 3 sân 7 (B1, B2, B3)',
    icon: `<svg width="20" height="20" viewBox="0 0 24 24" fill="none"><rect x="2" y="3" width="20" height="18" rx="2" stroke="var(--lime-300)" stroke-width="1.8"/><line x1="12" y1="3" x2="12" y2="21" stroke="var(--lime-300)" stroke-width="1.8"/></svg>` },
  { label: 'Hotline đặt sân', val: '0343 831 316 · Hỗ trợ 24/7',
    icon: `<svg width="20" height="20" viewBox="0 0 24 24" fill="none"><path d="M22 16.92v3a2 2 0 0 1-2.18 2A19.79 19.79 0 0 1 11 17.65a19.5 19.5 0 0 1-6-6A19.79 19.79 0 0 1 2.12 2.18 2 2 0 0 1 4.11 0h3a2 2 0 0 1 2 1.72c.127.96.361 1.903.7 2.81a2 2 0 0 1-.45 2.11L8.09 7.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45c.907.339 1.85.573 2.81.7A2 2 0 0 1 22 16.92z" stroke="var(--lime-300)" stroke-width="1.8"/></svg>` }
]

/* ============ THỐNG KÊ + COUNT-UP ============ */
const thongKe = [
  { num: '6', suffix: ' sân', label: 'Sân hiện có', icon: `<svg width="22" height="22" viewBox="0 0 24 24" fill="none"><rect x="2" y="3" width="20" height="18" rx="2" stroke="var(--lime-400)" stroke-width="1.8"/><line x1="12" y1="3" x2="12" y2="21" stroke="var(--lime-400)" stroke-width="1.8"/></svg>` },
  { num: '35', suffix: 'k+', label: 'Lượt đặt sân', icon: `<svg width="22" height="22" viewBox="0 0 24 24" fill="none"><rect x="3" y="5" width="18" height="16" rx="2" stroke="var(--lime-400)" stroke-width="1.8"/><path d="M3 10H21M8 3v4M16 3v4" stroke="var(--lime-400)" stroke-width="1.8" stroke-linecap="round"/></svg>` },
  { num: '4.8', suffix: '/5', label: 'Đánh giá TB', icon: `<svg width="22" height="22" viewBox="0 0 24 24" fill="none"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z" stroke="var(--lime-400)" stroke-width="1.8" stroke-linejoin="round"/></svg>` },
  { num: '24', suffix: '/7', label: 'Hỗ trợ', icon: `<svg width="22" height="22" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="9" stroke="var(--lime-400)" stroke-width="1.8"/><path d="M12 7V12L15 14" stroke="var(--lime-400)" stroke-width="1.8" stroke-linecap="round"/></svg>` }
]
const statsDisplay = ref(thongKe.map(() => '0'))
const statsSection = ref(null)
let statsStarted = false
function startCounters() {
  if (statsStarted) return
  statsStarted = true
  thongKe.forEach((s, idx) => {
    const target = parseFloat(s.num)
    const isDecimal = s.num.includes('.')
    const duration = 1300
    const start = performance.now()
    function tick(now) {
      const p = Math.min((now - start) / duration, 1)
      const eased = 1 - Math.pow(1 - p, 3)
      const val = target * eased
      statsDisplay.value[idx] = isDecimal ? val.toFixed(1) : Math.round(val).toString()
      if (p < 1) requestAnimationFrame(tick)
    }
    requestAnimationFrame(tick)
  })
}

/* ============ LOẠI SÂN ============ */
const loaiSanList = [
  {
    id: 5, badge: 'SÂN 5', title: 'Sân 5 người',
    mo_ta: 'Lý tưởng cho tụ tập bạn bè, đồng nghiệp sau giờ làm. Trận đấu nhanh, sôi nổi.',
    tenSan: ['A1', 'A2', 'A3'],
    specs: ['Kích thước ~25 × 16m', 'Cỏ nhân tạo cao cấp', 'Đèn chiếu sáng buổi tối'],
    giaFrom: '350.000đ/giờ', turf: '#123321'
  },
  {
    id: 7, badge: 'SÂN 7', title: 'Sân 7 người',
    mo_ta: 'Chuẩn cho giải đấu phong trào, team building doanh nghiệp, trận giao hữu lớn.',
    tenSan: ['B1', 'B2', 'B3'],
    specs: ['Kích thước ~50 × 30m', 'Cỏ nhân tạo FIFA', 'Khán đài & phòng thay đồ'],
    giaFrom: '650.000đ/giờ', turf: '#146239'
  }
]

/* ============ SÂN NỔI BẬT ============ */
const sanNoiBat = ref([])
const API = '/api'

async function fetchSanNoiBat() {
  try {
    const res = await fetch(`${API}/san-bong`)
    if (res.ok) {
      const data = await res.json()
      sanNoiBat.value = data.slice(0, 6).map(san => ({
        id: san.id,
        ten: san.loaiSan === 5 ? 'Sân 5 người' : 'Sân 7 người',
        maSan: san.tenSan.split(' – ')[0] || san.tenSan,
        loai: san.loaiSan === 5 ? 'Sân 5' : 'Sân 7',
        diaChi: san.diaChi,
        rating: '4.8',
        gia: san.danhSachGia && san.danhSachGia.length > 0 ? Number(san.danhSachGia[0].giaTien).toLocaleString('vi-VN') : '350.000',
        mau: san.loaiSan === 5 ? '#123321' : '#146239'
      }))
    }
  } catch (err) {
    console.error('Lỗi tải danh sách sân nổi bật:', err)
  }
}

/* ============ THƯ VIỆN HÌNH ẢNH (minh hoạ gốc) ============ */
const thuVienAnh = [
  { title: 'Sân nhìn từ trên cao', desc: 'Toàn cảnh mặt cỏ và vạch kẻ chuẩn thi đấu.',
    svg: `<svg viewBox="0 0 200 140"><rect width="200" height="140" fill="#123321"/><line x1="100" y1="4" x2="100" y2="136" stroke="rgba(255,255,255,.35)"/><circle cx="100" cy="70" r="26" stroke="rgba(255,255,255,.4)" fill="none"/><rect x="4" y="45" width="34" height="50" stroke="rgba(255,255,255,.4)" fill="none"/><rect x="162" y="45" width="34" height="50" stroke="rgba(255,255,255,.4)" fill="none"/></svg>` },
  { title: 'Đèn pha ban đêm', desc: '4 cụm đèn LED công suất cao chiếu sáng toàn sân.',
    svg: `<svg viewBox="0 0 200 140"><rect width="200" height="140" fill="#0a1f13"/><rect x="94" y="60" width="12" height="70" fill="#0d2417"/><ellipse cx="100" cy="55" rx="55" ry="42" fill="#fff4d6" opacity=".18"/><rect x="72" y="30" width="56" height="26" rx="4" fill="#0d2417" stroke="var(--lime-300)"/></svg>` },
  { title: 'Phòng thay đồ', desc: 'Tủ khóa riêng, vòi sen và khu vực nghỉ ngơi sạch sẽ.',
    svg: `<svg viewBox="0 0 200 140"><rect width="200" height="140" fill="#0d2417"/><rect x="20" y="30" width="30" height="80" fill="none" stroke="var(--lime-300)"/><rect x="60" y="30" width="30" height="80" fill="none" stroke="var(--lime-300)"/><rect x="100" y="30" width="30" height="80" fill="none" stroke="var(--lime-300)"/><rect x="140" y="30" width="30" height="80" fill="none" stroke="var(--lime-300)"/></svg>` },
  { title: 'Bảng tỷ số điện tử', desc: 'Theo dõi thời gian và tỷ số trực tiếp trong trận.',
    svg: `<svg viewBox="0 0 200 140"><rect width="200" height="140" fill="#050b08"/><rect x="30" y="45" width="140" height="50" rx="6" fill="none" stroke="var(--lime-400)"/><text x="50" y="78" fill="var(--lime-400)" font-size="26" font-family="monospace">2 : 1</text></svg>` },
  { title: 'Khán đài cổ động viên', desc: 'Không gian cổ vũ sát đường biên, gần gũi trận đấu.',
    svg: `<svg viewBox="0 0 200 140"><rect width="200" height="140" fill="#123321"/><path d="M0 90 Q40 60 80 90 T160 90 T200 90 V140 H0 Z" fill="#0a1f13"/><circle v-for="n in 6" cx="20" cy="80" r="3" fill="var(--lime-300)"/></svg>` },
  { title: 'Góc lưu niệm', desc: 'Cúp và huy chương từ các giải phong trào tại sân.',
    svg: `<svg viewBox="0 0 200 140"><rect width="200" height="140" fill="#0d2417"/><path d="M80 30h40v22a20 20 0 0 1-40 0V30Z" fill="none" stroke="var(--amber-400)" stroke-width="2"/><path d="M100 72v14M84 100h32l-3-10a4 4 0 0 0-4-3H91a4 4 0 0 0-4 3l-3 10Z" fill="none" stroke="var(--amber-400)" stroke-width="2"/></svg>` }
]
const selectedGallery = ref(null)

/* ============ BÀI VIẾT ============ */
const danhSachBaiViet = ref([])
const selectedPost = ref(null)

async function fetchBaiVietTrangChu() {
  try {
    const res = await fetch(`${API}/posts/published`)
    if (res.ok) {
      const data = await res.json()
      danhSachBaiViet.value = data.slice(0, 3)
    }
  } catch (err) {
    console.error('Lỗi tải bài viết trang chủ:', err)
  }
}

async function xemChiTietBaiViet(post) {
  selectedPost.value = post
  if (post.viewsCount === null || post.viewsCount === undefined) post.viewsCount = 0
  post.viewsCount += 1
  try {
    await fetch(`${API}/posts/${post.id}/view`)
  } catch (err) {
    console.error('Lỗi khi gọi API tăng lượt xem:', err)
  }
}

function formatDate(dateStr) {
  if (!dateStr) return 'Mới đăng'
  const d = new Date(dateStr)
  if (isNaN(d.getTime())) return 'Mới đăng'
  return d.toLocaleDateString('vi-VN')
}
function getShortSummary(content) {
  if (!content) return ''
  const cleanText = content.replace(/<[^>]*>?/gm, '')
  return cleanText.length > 100 ? cleanText.substring(0, 100) + '...' : cleanText
}
function getCategoryLabel(cat) {
  const map = { NEWS: 'Tin tức', TOURNAMENT: 'Giải đấu', PROMOTION: 'Khuyến mãi', GUIDE: 'Hướng dẫn' }
  return map[cat] || 'Tin tức'
}
function getCategoryClass(cat) {
  const map = { NEWS: 'cat-news', TOURNAMENT: 'cat-tournament', PROMOTION: 'cat-promo', GUIDE: 'cat-guide' }
  return map[cat] || 'cat-news'
}

/* ============ CÁC BƯỚC ĐẶT SÂN ============ */
const cacBuoc = [
  { title: 'Chọn sân & khung giờ', desc: 'Tìm sân A1–A3 hoặc B1–B3, chọn loại sân 5 hoặc 7, xem lịch trống thực.',
    icon: `<svg width="22" height="22" viewBox="0 0 24 24" fill="none"><circle cx="11" cy="11" r="7" stroke="white" stroke-width="2"/><path d="M16.5 16.5L21 21" stroke="white" stroke-width="2" stroke-linecap="round"/></svg>` },
  { title: 'Xác nhận & thanh toán', desc: 'Điền thông tin, xác nhận đặt sân và thanh toán trực tuyến an toàn.',
    icon: `<svg width="22" height="22" viewBox="0 0 24 24" fill="none"><rect x="2" y="5" width="20" height="14" rx="2" stroke="white" stroke-width="2"/><path d="M2 10H22" stroke="white" stroke-width="2"/></svg>` },
  { title: 'Ra sân & tận hưởng', desc: 'Nhận mã xác nhận, đến Hẻm 104 Tân Sơn đúng giờ và bắt đầu trận đấu.',
    icon: `<svg width="22" height="22" viewBox="0 0 24 24" fill="none"><path d="M5 13L9.5 17.5L19 7" stroke="white" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/></svg>` }
]

/* ============ GỌI API ĐÁNH GIÁ ============ */
const fetchDanhGia = async () => {
  try {
    const res = await fetch('/api/danh-gia-he-thong')
    if (res.ok) {
      danhGiaList.value = await res.json()
    } else {
      console.error('Lỗi khi tải danh sách đánh giá. Status:', res.status)
    }
  } catch (err) {
    console.error('Lỗi kết nối API lấy đánh giá:', err)
  }
}

const guiDanhGia = async () => {
  if (!noiDungBinhLuan.value.trim()) {
    alert('Vui lòng nhập nội dung đánh giá!')
    return
  }
  const token = localStorage.getItem('token')
  if (!token) {
    alert('Vui lòng đăng nhập để gửi đánh giá!')
    return
  }
  isSubmitting.value = true
  try {
    const res = await fetch('/api/danh-gia-he-thong', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
      body: JSON.stringify({ quote: noiDungBinhLuan.value, rating: soSaoDanhGia.value })
    })
    if (res.ok) {
      alert('Cảm ơn bạn đã gửi đánh giá!')
      noiDungBinhLuan.value = ''
      soSaoDanhGia.value = 5
      await fetchDanhGia()
    } else if (res.status === 401 || res.status === 403) {
      alert('Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!')
    } else {
      const errorText = await res.text()
      alert('Không thể gửi đánh giá: ' + errorText)
    }
  } catch (err) {
    console.error('Lỗi khi gửi đánh giá:', err)
    alert('Có lỗi xảy ra khi kết nối máy chủ!')
  } finally {
    isSubmitting.value = false
  }
}

function getInitials(name) {
  if (!name || typeof name !== 'string') return 'KH'
  return name.trim().split(' ').filter(Boolean).map(w => w[0]).join('').toUpperCase().slice(0, 2)
}

/* ============ SCROLL REVEAL ============ */
let revealObserver = null

onMounted(() => {
slideInterval = setInterval(() => {
    slideHienTai.value = (slideHienTai.value + 1) % slides.length
  }, 4500)

  fetchSanNoiBat()
  fetchBaiVietTrangChu()
  fetchDanhGia()

  revealObserver = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('is-visible')
        revealObserver.unobserve(entry.target)
      }
    })
  }, { threshold: 0.15 })

  const statsObserver = new IntersectionObserver((entries) => {
    entries.forEach(entry => { if (entry.isIntersecting) startCounters() })
  }, { threshold: 0.4 })
  if (statsSection.value) statsObserver.observe(statsSection.value)

  nextTick(() => {
    document.querySelectorAll('.reveal').forEach(el => revealObserver.observe(el))
  })
})

onUnmounted(() => {
  clearInterval(slideInterval)
  if (revealObserver) revealObserver.disconnect()
})
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Bebas+Neue&family=Oswald:wght@500;600;700&family=Manrope:wght@400;500;600;700;800&family=Space+Mono:wght@400;700&display=swap');
</style>

<style scoped>
/* ===== TOKENS (đồng bộ tông màu với trang đăng nhập) ===== */
.pitch {
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
  --font-clock: 'Bebas Neue', 'Oswald', sans-serif;

  position: relative;
  background: var(--night-950);
  color: var(--chalk-050);
  font-family: var(--font-body);
  overflow: hidden;
}
.container { max-width: 1280px; margin: 0 auto; padding: 0 44px; }
@media (max-width: 780px) { .container { padding: 0 20px; } }

/* ===== REVEAL ANIMATION ===== */
.reveal { opacity: 0; transform: translateY(22px); transition: opacity .7s ease, transform .7s ease; transition-delay: var(--d, 0s); }
.reveal.is-visible { opacity: 1; transform: translateY(0); }
@media (prefers-reduced-motion: reduce) { .reveal { opacity: 1; transform: none; transition: none; } }

/* ===== LỚP NỀN CHUNG ===== */
.pitch__ambient { position: fixed; inset: 0; z-index: 0; pointer-events: none; }
.floodlight { position: absolute; top: -12%; width: 55vh; height: 145vh; background: conic-gradient(from 90deg at 50% 0%, transparent 42%, rgba(255,244,214,.08) 50%, transparent 58%); mix-blend-mode: screen; }
.floodlight--l { left: -14%; animation: sweep 11s ease-in-out infinite alternate; }
.floodlight--r { right: -14%; animation: sweep 11s ease-in-out infinite alternate-reverse; }
@keyframes sweep { 0% { transform: rotate(-10deg); } 100% { transform: rotate(10deg); } }
.grass-drift { position: absolute; inset: -10% -20%; opacity: .35; background: repeating-linear-gradient(100deg, rgba(35,147,90,.06) 0 70px, transparent 70px 140px); animation: drift 30s linear infinite; }
@keyframes drift { from { background-position: 0 0; } to { background-position: 400px 0; } }
.motes { position: absolute; inset: 0; }
.mote { position: absolute; bottom: -10px; left: calc((var(--i) * 6.4%) + 1%); width: 3px; height: 3px; border-radius: 50%; background: var(--lime-300); opacity: 0; animation: rise 9s linear infinite; animation-delay: calc(var(--i) * -0.6s); }
@keyframes rise { 0% { transform: translateY(0) scale(.6); opacity: 0; } 10% { opacity: .6; } 90% { opacity: .15; } 100% { transform: translateY(-100vh) scale(1.1); opacity: 0; } }
.field-props { position: absolute; inset: 0; }
.prop { position: absolute; opacity: .08; animation: prop-float 7s ease-in-out infinite; }
.prop svg { width: 100%; height: 100%; }
.prop--ball { width: 50px; height: 50px; top: 14%; right: 6%; }
.prop--whistle { width: 60px; height: 40px; top: 55%; left: 3%; animation-delay: .6s; }
.prop--flag { width: 34px; height: 52px; top: 30%; right: 3%; animation-delay: 1.1s; }
.prop--trophy { width: 44px; height: 50px; bottom: 10%; left: 12%; animation-delay: 1.6s; }
@keyframes prop-float { 0%, 100% { transform: translateY(0) rotate(-3deg); } 50% { transform: translateY(-14px) rotate(3deg); } }

/* ===== HERO ===== */
.hero { position: relative; z-index: 1; padding-top: 44px; }
.hero__ticker { background: rgba(182,255,60,.08); border-top: 1px solid rgba(182,255,60,.2); border-bottom: 1px solid rgba(182,255,60,.2); overflow: hidden; white-space: nowrap; padding: 8px 0; margin-bottom: 32px; }
.hero__ticker-track { display: inline-block; font-family: var(--font-mono); font-size: 12.5px; letter-spacing: .04em; color: var(--lime-300); animation: ticker 26s linear infinite; }
@keyframes ticker { from { transform: translateX(0); } to { transform: translateX(-50%); } }

.hero__grid { display: grid; grid-template-columns: 1.05fr .95fr; gap: 36px; align-items: center; padding-bottom: 56px; }
.eyebrow { display: inline-block; font-family: var(--font-mono); font-size: 12px; letter-spacing: .16em; text-transform: uppercase; color: var(--lime-300); }
.headline { font-family: var(--font-display); font-weight: 600; font-size: clamp(32px, 3.6vw, 50px); line-height: 1.18; margin-top: 14px; }
.headline__accent { color: var(--lime-400); text-shadow: 0 0 24px rgba(182,255,60,.4); }
.desc { margin-top: 16px; font-size: 15.5px; color: var(--chalk-200); opacity: .82; line-height: 1.65; max-width: 480px; }

.hero__cta { display: flex; gap: 12px; margin-top: 30px; flex-wrap: wrap; }
.btn-primary {
  display: inline-flex; align-items: center; gap: 8px; padding: 13px 26px;
  background: linear-gradient(135deg, var(--turf-500), var(--turf-700)); color: var(--chalk-050);
  border: none; border-radius: 999px; font-weight: 700; font-size: 15px; cursor: pointer;
  box-shadow: 0 10px 26px -8px rgba(20,98,57,.7); transition: transform .15s ease, box-shadow .15s ease;
}
.btn-primary:hover { transform: translateY(-2px); box-shadow: 0 16px 32px -8px rgba(182,255,60,.35); }
.btn-primary--wide { width: 100%; justify-content: center; margin-top: 4px; }
.btn-ghost { display: inline-flex; align-items: center; padding: 13px 24px; background: rgba(247,251,244,.06); color: var(--chalk-050); border: 1.5px solid rgba(247,251,244,.25); border-radius: 999px; font-weight: 600; font-size: 15px; transition: all .15s ease; }
.btn-ghost:hover { border-color: var(--lime-400); color: var(--lime-300); background: rgba(182,255,60,.08); }
.btn-dark { display: inline-flex; margin-top: 18px; padding: 11px 24px; background: var(--night-950); color: var(--chalk-050); border: 1px solid rgba(182,255,60,.25); border-radius: 999px; font-size: 14px; font-weight: 600; transition: background .15s, border-color .15s; }
.btn-dark:hover { background: var(--turf-700); border-color: var(--lime-400); }
.btn-small { padding: 7px 16px; background: var(--turf-500); color: var(--chalk-050); border-radius: 999px; font-size: 13px; font-weight: 700; transition: background .15s; }
.btn-small:hover { background: var(--lime-400); color: var(--night-950); }

.hero__slidecopy { margin-top: 30px; padding-top: 20px; border-top: 1px solid rgba(247,251,244,.12); max-width: 460px; }
.hero__slidecopy p { font-size: 13.5px; color: var(--chalk-200); opacity: .75; min-height: 20px; }
.slide-fade-enter-active, .slide-fade-leave-active { transition: opacity .35s ease, transform .35s ease; }
.slide-fade-enter-from { opacity: 0; transform: translateY(6px); }
.slide-fade-leave-to { opacity: 0; transform: translateY(-6px); }
.hero__dots { display: flex; gap: 8px; margin-top: 12px; }
.hero__dot { width: 8px; height: 8px; border-radius: 50%; background: rgba(247,251,244,.3); border: none; cursor: pointer; transition: background .2s, width .2s; }
.hero__dot--active { background: var(--lime-400); width: 22px; border-radius: 999px; }

.hero__scene { display: flex; justify-content: center; }
.night-pitch { width: 100%; max-width: 460px; filter: drop-shadow(0 30px 60px rgba(0,0,0,.55)); }
.stand-dot { animation: twinkle 3s ease-in-out infinite; animation-delay: calc(var(--i) * -0.25s); }
@keyframes twinkle { 0%, 100% { opacity: .3; } 50% { opacity: 1; } }
.draw { stroke-dasharray: 900; stroke-dashoffset: 900; animation: draw 1.8s ease forwards; }
@keyframes draw { to { stroke-dashoffset: 0; } }
.shot-path { fill: none; stroke: var(--lime-400); stroke-width: 1.6; stroke-dasharray: 6 6; opacity: .7; }
.player { transform-box: fill-box; transform-origin: center; opacity: 0; animation: pop .4s cubic-bezier(.34,1.56,.64,1) forwards; animation-delay: calc(var(--pi) * .15s + .4s); }
@keyframes pop { from { opacity: 0; transform: scale(0); } to { opacity: 1; transform: scale(1); } }
.beam { animation: beam-pulse 5s ease-in-out infinite; }
@keyframes beam-pulse { 0%, 100% { opacity: .7; } 50% { opacity: 1; } }
.sb-text { font-family: var(--font-mono); fill: var(--chalk-050); }
.sb-label { font-size: 10px; letter-spacing: .05em; opacity: .7; }
.sb-score { font-family: var(--font-clock); font-size: 22px; fill: var(--lime-400); }
.sb-live { font-size: 10px; fill: var(--lime-300); }
.scoreboard { animation: fade-in-late 1s ease forwards; opacity: 0; animation-delay: 1.4s; }
@keyframes fade-in-late { to { opacity: 1; } }
@media (max-width: 900px) { .hero__grid { grid-template-columns: 1fr; text-align: center; } .desc { margin-left: auto; margin-right: auto; } .hero__cta, .hero__slidecopy { justify-content: center; margin-left: auto; margin-right: auto; } .hero__dots { justify-content: center; } }

/* ===== INFO STRIP ===== */
.info-strip { position: relative; z-index: 1; padding: 0 0 40px; }
.info-strip__grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 14px; }
.info-card { display: flex; gap: 12px; align-items: center; background: rgba(247,251,244,.04); border: 1px solid rgba(247,251,244,.1); border-radius: 16px; padding: 18px; backdrop-filter: blur(6px); transition: border-color .2s, transform .2s; }
.info-card:hover { border-color: rgba(182,255,60,.35); transform: translateY(-3px); }
.info-card__icon { width: 40px; height: 40px; border-radius: 12px; background: rgba(182,255,60,.1); display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.info-card__label { font-family: var(--font-mono); font-size: 10.5px; text-transform: uppercase; letter-spacing: .06em; color: var(--lime-300); margin-bottom: 3px; }
.info-card__val { font-size: 13px; font-weight: 600; color: var(--chalk-050); line-height: 1.4; }
@media (max-width: 900px) { .info-strip__grid { grid-template-columns: 1fr 1fr; } }
@media (max-width: 560px) { .info-strip__grid { grid-template-columns: 1fr; } }

/* ===== STATS ===== */
.stats { position: relative; z-index: 1; padding: 20px 0 56px; }
.stats__grid { display: grid; grid-template-columns: repeat(4, 1fr); background: rgba(247,251,244,.03); border: 1px solid rgba(247,251,244,.08); border-radius: 20px; overflow: hidden; }
.stat { text-align: center; padding: 28px 14px; border-right: 1px solid rgba(247,251,244,.08); }
.stat:last-child { border-right: none; }
.stat__icon { width: 42px; height: 42px; margin: 0 auto 10px; background: rgba(182,255,60,.1); border-radius: 12px; display: flex; align-items: center; justify-content: center; }
.stat__num { font-family: var(--font-clock); font-size: 34px; letter-spacing: .01em; color: var(--chalk-050); }
.stat__num span { color: var(--lime-400); }
.stat__label { font-size: 12px; color: var(--chalk-200); opacity: .7; margin-top: 4px; font-family: var(--font-mono); }
@media (max-width: 700px) { .stats__grid { grid-template-columns: 1fr 1fr; } .stat:nth-child(2) { border-right: none; } }

/* ===== SECTION GENERIC ===== */
.section { position: relative; z-index: 1; padding: 64px 0; }
.section-head { display: flex; align-items: flex-end; justify-content: space-between; margin-bottom: 34px; flex-wrap: wrap; gap: 12px; }
.section-head--center { flex-direction: column; align-items: center; text-align: center; }
.section-title { font-family: var(--font-display); font-size: clamp(24px, 2.4vw, 32px); font-weight: 600; color: var(--chalk-050); margin-top: 8px; }
.section-sub { margin-top: 8px; font-size: 14.5px; color: var(--chalk-200); opacity: .7; }
.see-all { font-weight: 700; font-size: 13.5px; color: var(--lime-300); }
.see-all:hover { color: var(--lime-400); }

/* ===== LOẠI SÂN ===== */
.loai-san__grid { display: grid; grid-template-columns: 1fr 1fr; gap: 24px; }
.pitch-card { background: rgba(247,251,244,.03); border: 1px solid rgba(247,251,244,.08); border-radius: 20px; overflow: hidden; cursor: pointer; transition: transform .25s ease, border-color .25s ease; }
.pitch-card:hover { transform: translateY(-6px); border-color: rgba(182,255,60,.3); }
.pitch-card__media { position: relative; padding: 18px; }
.mini-pitch { width: 100%; display: block; border-radius: 10px; }
.pitch-card__badge { position: absolute; top: 30px; left: 30px; background: var(--chalk-050); color: var(--night-950); font-family: var(--font-clock); font-size: 13px; padding: 4px 12px; border-radius: 999px; }
.pitch-card__price { position: absolute; bottom: 30px; right: 30px; background: rgba(5,11,8,.6); color: var(--lime-300); font-size: 12.5px; font-weight: 700; padding: 5px 12px; border-radius: 999px; }
.pitch-card__body { padding: 22px 24px 26px; }
.pitch-card__body h3 { font-family: var(--font-display); font-size: 20px; font-weight: 600; }
.pitch-card__body p { margin-top: 8px; font-size: 14px; color: var(--chalk-200); opacity: .8; line-height: 1.65; }
.pitch-card__tags { display: flex; gap: 8px; flex-wrap: wrap; margin-top: 14px; }
.tag-pill { padding: 5px 14px; background: rgba(182,255,60,.1); color: var(--lime-300); border: 1px solid rgba(182,255,60,.3); border-radius: 999px; font-size: 12.5px; font-weight: 700; }
.pitch-card__body ul { margin-top: 14px; display: flex; flex-direction: column; gap: 6px; }
.pitch-card__body li { font-size: 13.5px; color: var(--chalk-200); padding-left: 18px; position: relative; }
.pitch-card__body li::before { content: ''; position: absolute; left: 0; top: 7px; width: 6px; height: 6px; border-radius: 50%; background: var(--lime-400); }
@media (max-width: 760px) { .loai-san__grid { grid-template-columns: 1fr; } }

/* ===== SÂN NỔI BẬT ===== */
.san-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; }
.san-card { background: rgba(247,251,244,.03); border: 1px solid rgba(247,251,244,.08); border-radius: 16px; overflow: hidden; transition: transform .2s ease, border-color .2s ease; }
.san-card:hover { transform: translateY(-5px); border-color: rgba(182,255,60,.3); }
.san-card__media { position: relative; padding: 10px; }
.san-card__loai { position: absolute; top: 18px; left: 18px; background: rgba(247,251,244,.92); color: var(--night-950); font-size: 11px; font-weight: 700; padding: 3px 10px; border-radius: 999px; }
.san-card__rating { position: absolute; bottom: 18px; left: 18px; background: rgba(5,11,8,.6); color: var(--amber-400); font-size: 12px; font-weight: 700; padding: 3px 9px; border-radius: 999px; }
.san-card__fav { position: absolute; top: 18px; right: 18px; width: 28px; height: 28px; border-radius: 50%; background: rgba(5,11,8,.45); border: none; display: flex; align-items: center; justify-content: center; cursor: pointer; }
.san-card__fav:hover { background: rgba(229,62,62,.7); }
.san-card__body { padding: 16px 18px 18px; }
.san-card__row { display: flex; align-items: center; justify-content: space-between; gap: 8px; }
.san-card__row h3 { font-size: 15px; font-weight: 700; }
.san-card__ma { background: var(--turf-500); color: var(--chalk-050); font-size: 12px; font-weight: 700; padding: 3px 10px; border-radius: 999px; flex-shrink: 0; }
.san-card__dc { margin-top: 5px; font-size: 12.5px; color: var(--chalk-200); opacity: .7; }
.san-card__footer { display: flex; align-items: center; justify-content: space-between; margin-top: 14px; padding-top: 12px; border-top: 1px solid rgba(247,251,244,.1); }
.san-card__footer strong { font-size: 16px; color: var(--lime-300); }
.muted { font-size: 12px; color: var(--chalk-200); opacity: .6; }
.empty-note { grid-column: 1/-1; text-align: center; color: var(--chalk-200); opacity: .6; font-size: 14px; }
@media (max-width: 960px) { .san-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 560px) { .san-grid { grid-template-columns: 1fr; } }

/* ===== THƯ VIỆN HÌNH ẢNH ===== */
.gallery-section { background: rgba(247,251,244,.02); }
.gallery-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; }
.gallery-tile { position: relative; border: 1px solid rgba(247,251,244,.1); border-radius: 16px; overflow: hidden; background: none; padding: 0; cursor: pointer; transition: transform .25s ease, border-color .25s ease; }
.gallery-tile:hover { transform: scale(1.02); border-color: rgba(182,255,60,.4); }
.gallery-tile__art { line-height: 0; }
.gallery-tile__art :deep(svg) { width: 100%; height: auto; display: block; transition: transform .4s ease; }
.gallery-tile:hover .gallery-tile__art :deep(svg) { transform: scale(1.08); }
.gallery-tile__label { position: absolute; left: 0; right: 0; bottom: 0; padding: 10px 14px; background: linear-gradient(to top, rgba(5,11,8,.85), transparent); font-size: 13px; font-weight: 700; text-align: left; color: var(--chalk-050); }
@media (max-width: 760px) { .gallery-grid { grid-template-columns: 1fr 1fr; } }

.lightbox { position: fixed; inset: 0; z-index: 200; background: rgba(5,11,8,.82); backdrop-filter: blur(4px); display: flex; align-items: center; justify-content: center; padding: 20px; }
.lightbox__panel { background: var(--night-800); border: 1px solid rgba(182,255,60,.25); border-radius: 18px; padding: 24px; max-width: 480px; width: 100%; position: relative; }
.lightbox__art :deep(svg) { width: 100%; border-radius: 10px; }
.lightbox__panel h3 { margin-top: 16px; font-family: var(--font-display); font-size: 19px; }
.lightbox__panel p { margin-top: 6px; font-size: 13.5px; color: var(--chalk-200); opacity: .75; }
.lightbox__close { position: absolute; top: 12px; right: 16px; background: none; border: none; color: var(--chalk-200); font-size: 26px; cursor: pointer; }

/* ===== BÀI VIẾT ===== */
.posts-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 22px; }
.post-card { background: rgba(247,251,244,.03); border: 1px solid rgba(247,251,244,.08); border-radius: 16px; overflow: hidden; cursor: pointer; display: flex; flex-direction: column; transition: transform .25s ease, border-color .25s ease; }
.post-card:hover { transform: translateY(-5px); border-color: rgba(182,255,60,.3); }
.post-card__thumb { position: relative; height: 170px; }
.post-card__thumb img { width: 100%; height: 100%; object-fit: cover; }
.post-card__category { position: absolute; top: 12px; left: 12px; padding: 4px 10px; border-radius: 20px; font-size: 11px; font-weight: 700; color: #fff; text-transform: uppercase; }
.cat-news { background: #2563eb; } .cat-tournament { background: var(--amber-400); color: var(--night-950); } .cat-promo { background: var(--crimson-500); } .cat-guide { background: var(--turf-500); }
.post-card__content { padding: 18px; display: flex; flex-direction: column; flex: 1; }
.post-card__meta { display: flex; gap: 8px; align-items: center; font-size: 11.5px; color: var(--chalk-200); opacity: .65; margin-bottom: 10px; flex-wrap: wrap; }
.post-card__title { font-size: 15.5px; font-weight: 700; line-height: 1.4; margin-bottom: 8px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.post-card__excerpt { font-size: 13px; color: var(--chalk-200); opacity: .75; line-height: 1.5; margin-bottom: 14px; display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; overflow: hidden; flex: 1; }
.post-card__link { display: flex; align-items: center; gap: 6px; font-size: 13px; font-weight: 700; color: var(--lime-300); }
.dot { color: rgba(247,251,244,.35); }
@media (max-width: 820px) { .posts-grid { grid-template-columns: 1fr; } }

.post-modal { position: fixed; inset: 0; background: rgba(5,11,8,.85); z-index: 1000; display: flex; align-items: center; justify-content: center; padding: 20px; }
.post-modal__content { background: var(--night-800); border: 1px solid rgba(182,255,60,.2); border-radius: 16px; width: 100%; max-width: 700px; max-height: 85vh; overflow-y: auto; padding: 30px; position: relative; }
.post-modal__close { position: absolute; top: 15px; right: 20px; background: none; border: none; font-size: 28px; cursor: pointer; color: var(--chalk-200); }
.post-modal__header { margin-bottom: 20px; padding-bottom: 15px; border-bottom: 1px solid rgba(247,251,244,.12); }
.post-modal__header h2 { font-size: 22px; margin: 12px 0 8px; font-family: var(--font-display); }
.post-modal__body { font-size: 15px; line-height: 1.7; color: var(--chalk-200); }

/* ===== HOW IT WORKS ===== */
.how-it-works { background: rgba(247,251,244,.02); }
.steps { display: flex; align-items: flex-start; margin-top: 50px; gap: 12px; }
.step { flex: 1; display: flex; flex-direction: column; align-items: center; text-align: center; position: relative; }
.step__circle { width: 64px; height: 64px; border-radius: 50%; background: linear-gradient(135deg, var(--turf-500), var(--turf-700)); display: flex; align-items: center; justify-content: center; position: relative; box-shadow: 0 8px 22px -8px rgba(182,255,60,.35); }
.step__num { position: absolute; top: -8px; right: -4px; width: 22px; height: 22px; background: var(--chalk-050); color: var(--night-950); font-size: 11px; font-weight: 800; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-family: var(--font-mono); }
.step__line { position: absolute; top: 32px; left: calc(50% + 38px); right: calc(-50% + 38px); height: 2px; background: rgba(247,251,244,.12); }
.step h3 { margin-top: 18px; font-size: 16px; font-weight: 700; }
.step p { margin-top: 8px; font-size: 13.5px; line-height: 1.65; color: var(--chalk-200); opacity: .7; padding: 0 10px; }
@media (max-width: 760px) { .steps { flex-direction: column; gap: 32px; } .step__line { display: none; } }

/* ===== ĐÁNH GIÁ ===== */
.review-section { background: rgba(247,251,244,.02); }
.section-title-container { text-align: center; margin-bottom: 30px; }
.section-title-container p { color: var(--chalk-200); opacity: .7; font-size: 14.5px; margin-top: 6px; }
.review-filter-bar { display: flex; justify-content: center; flex-wrap: wrap; gap: 8px; margin-bottom: 26px; }
.filter-btn { padding: 8px 16px; background: rgba(247,251,244,.05); border: 1px solid rgba(247,251,244,.15); border-radius: 999px; font-size: 13.5px; font-weight: 600; color: var(--chalk-200); cursor: pointer; transition: all .2s ease; }
.filter-btn:hover { border-color: rgba(182,255,60,.4); }
.filter-btn.active { background: var(--turf-500); color: var(--chalk-050); border-color: var(--turf-500); }
.empty-review-state { text-align: center; background: rgba(247,251,244,.03); border: 2px dashed rgba(247,251,244,.15); border-radius: 16px; padding: 40px 20px; margin-bottom: 36px; }
.empty-icon { font-size: 36px; margin-bottom: 10px; }
.empty-review-state h3 { font-size: 17px; font-weight: 700; }
.empty-review-state p { color: var(--chalk-200); opacity: .7; font-size: 13.5px; margin-top: 4px; }
.review-list-container { margin-bottom: 36px; }
.review-scroll-wrapper { display: flex; gap: 18px; overflow-x: auto; padding: 10px 6px 18px; scroll-behavior: smooth; }
.review-scroll-wrapper::-webkit-scrollbar { height: 6px; }
.review-scroll-wrapper::-webkit-scrollbar-track { background: rgba(247,251,244,.05); border-radius: 10px; }
.review-scroll-wrapper::-webkit-scrollbar-thumb { background: rgba(182,255,60,.3); border-radius: 10px; }
.review-card { flex: 0 0 320px; max-width: 320px; background: rgba(247,251,244,.04); border: 1px solid rgba(247,251,244,.1); border-radius: 14px; padding: 18px 20px; transition: transform .2s ease, border-color .2s ease; }
.review-card:hover { transform: translateY(-3px); border-color: rgba(182,255,60,.3); }
.card-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 12px; }
.user-name { font-size: 1rem; font-weight: 700; color: var(--chalk-050); }
.sub-info { display: flex; align-items: center; gap: 6px; font-size: .8rem; color: var(--chalk-200); opacity: .7; }
.user-role { color: var(--lime-300); font-weight: 600; opacity: 1; }
.star-rating .star { font-size: 1.05rem; color: rgba(247,251,244,.25); }
.star-rating .star.filled { color: var(--amber-400); }
.review-quote { font-size: .92rem; color: var(--chalk-200); line-height: 1.55; margin: 0 0 14px; font-style: italic; }
.reply-box { background: rgba(182,255,60,.05); border-left: 3px solid var(--lime-400); border-radius: 0 10px 10px 0; padding: 10px 12px; }
.reply-user-info { display: flex; align-items: center; gap: 6px; }
.reply-icon { font-size: 13px; }
.replier-name { font-size: 13px; font-weight: 700; color: var(--chalk-050); }
.role-badge { font-size: 10px; font-weight: 800; padding: 2px 6px; border-radius: 4px; letter-spacing: .5px; }
.role-badge.staff { background: rgba(182,255,60,.15); color: var(--lime-300); }
.role-badge.admin { background: rgba(255,71,87,.18); color: #ff9686; }
.reply-time { font-size: 11px; color: var(--chalk-200); opacity: .55; padding-left: 19px; margin-top: 4px; }
.reply-content { margin: 4px 0 0; padding-left: 19px; font-size: 13px; color: var(--chalk-200); opacity: .85; line-height: 1.45; }

.add-review-wrapper { display: flex; justify-content: center; }
.add-review-form { background: rgba(247,251,244,.04); border: 1px solid rgba(247,251,244,.1); border-radius: 16px; padding: 28px; width: 100%; max-width: 640px; }
.add-review-form h3 { font-size: 19px; font-weight: 700; }
.sub-title { font-size: 13.5px; color: var(--chalk-200); opacity: .7; margin: 4px 0 18px; }
.star-rating-input { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; }
.star-rating-input .stars { display: flex; gap: 4px; }
.star-icon { font-size: 27px; color: rgba(247,251,244,.25); cursor: pointer; transition: transform .1s, color .1s; user-select: none; }
.star-icon:hover { transform: scale(1.15); }
.star-icon.active { color: var(--amber-400); }
.rating-text { font-weight: 600; color: var(--chalk-200); font-size: 14px; }
.textarea-wrapper { position: relative; margin-bottom: 16px; }
.textarea-wrapper textarea { width: 100%; padding: 12px 14px; background: rgba(5,11,8,.4); border: 1px solid rgba(247,251,244,.15); border-radius: 10px; font-size: 14px; color: var(--chalk-050); outline: none; resize: vertical; box-sizing: border-box; font-family: var(--font-body); }
.textarea-wrapper textarea:focus { border-color: var(--lime-400); box-shadow: 0 0 0 3px rgba(182,255,60,.12); }
.char-count { text-align: right; font-size: 12px; color: var(--chalk-200); opacity: .55; margin-top: 4px; }

/* ===== CTA ===== */
.cta-panel { position: relative; overflow: hidden; background: linear-gradient(135deg, var(--night-700), var(--night-950)); border: 1px solid rgba(182,255,60,.18); border-radius: 22px; padding: 48px 52px; display: flex; align-items: center; justify-content: space-between; gap: 24px; flex-wrap: wrap; }
.cta-panel__glow { position: absolute; inset: -40% -10%; background: radial-gradient(circle at 80% 20%, rgba(182,255,60,.18), transparent 60%); pointer-events: none; }
.cta-panel__left { position: relative; z-index: 1; }
.cta-panel__left h2 { font-family: var(--font-display); font-size: 26px; font-weight: 600; }
.cta-panel__left p { margin-top: 8px; font-size: 15px; color: var(--chalk-200); opacity: .75; }
.cta-panel__right { position: relative; z-index: 1; display: flex; gap: 12px; flex-wrap: wrap; }
@media (max-width: 700px) { .cta-panel { padding: 32px 24px; } }
</style>