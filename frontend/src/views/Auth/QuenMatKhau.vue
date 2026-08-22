<template>
  <div
    class="scene"
    :class="{ 'scene--cursor': cursorEnabled }"
    :style="{ '--sx': spotlight.x + '%', '--sy': spotlight.y + '%' }"
    @mousemove="onSceneMove"
    @mouseleave="onSceneLeave"
  >
    <!-- ===== NỀN SÂN CỎ ĐÊM ===== -->
    <div class="scene__spotlight" aria-hidden="true"></div>
    <div class="floodlight floodlight--l" aria-hidden="true"></div>
    <div class="floodlight floodlight--r" aria-hidden="true"></div>
    <div class="grass-drift" aria-hidden="true"></div>
    <div class="grass-mow" aria-hidden="true"></div>
    <div class="scene__vignette" aria-hidden="true"></div>
    <div class="motes" aria-hidden="true">
      <span v-for="n in 14" :key="n" class="mote" :style="{ '--i': n }"></span>
    </div>

    <svg class="pitch-lines" viewBox="0 0 1600 900" preserveAspectRatio="xMidYMid slice" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
      <line x1="800" y1="0" x2="800" y2="900" class="draw-line" style="--delay:.1s" stroke="white" stroke-width="1.5" />
      <circle cx="800" cy="450" r="110" class="draw-line" style="--delay:.4s" stroke="white" stroke-width="1.5" fill="none" />
      <rect x="0" y="260" width="180" height="380" class="draw-line" style="--delay:.2s" stroke="white" stroke-width="1.5" fill="none" />
      <rect x="1420" y="260" width="180" height="380" class="draw-line" style="--delay:.3s" stroke="white" stroke-width="1.5" fill="none" />
    </svg>

    <!-- ===== ĐẠO CỤ BÓNG ĐÁ TRÔI NỔI ===== -->
    <div class="field-props" aria-hidden="true">
      <div class="prop prop--ball-1">
        <svg viewBox="0 0 40 40"><circle cx="20" cy="20" r="18" fill="var(--chalk-050)" stroke="var(--night-950)" stroke-width="1.4" /><polygon points="20,10 26,14.5 24,21.5 16,21.5 14,14.5" fill="var(--night-950)" /><path d="M20,10 L20,3 M26,14.5 L32.5,10.3 M24,21.5 L28.3,28 M16,21.5 L11.7,28 M14,14.5 L7.5,10.3" stroke="var(--night-950)" stroke-width="1.3" fill="none" /></svg>
      </div>
      <div class="prop prop--ball-2">
        <svg viewBox="0 0 40 40"><circle cx="20" cy="20" r="18" fill="var(--chalk-050)" stroke="var(--night-950)" stroke-width="1.4" /><polygon points="20,10 26,14.5 24,21.5 16,21.5 14,14.5" fill="var(--night-950)" /><path d="M20,10 L20,3 M26,14.5 L32.5,10.3 M24,21.5 L28.3,28 M16,21.5 L11.7,28 M14,14.5 L7.5,10.3" stroke="var(--night-950)" stroke-width="1.3" fill="none" /></svg>
      </div>
      <div class="prop prop--whistle">
        <svg viewBox="0 0 48 32" fill="none"><path d="M14 16a10 10 0 1 0 20 0 10 10 0 0 0-20 0Z" stroke="var(--lime-300)" stroke-width="1.6" /><circle cx="24" cy="16" r="3.2" fill="var(--lime-300)" /><path d="M14 16H4a2 2 0 0 0-2 2v0a2 2 0 0 0 2 2h5" stroke="var(--lime-300)" stroke-width="1.6" stroke-linecap="round" /></svg>
      </div>
      <div class="prop prop--boot">
        <svg viewBox="0 0 48 34" fill="none"><path d="M8 6v11.5c0 1.5.8 2.4 2 3.2l9 5.8c1 .7 2.2 1 3.4 1h16.4c1.8 0 2.9-2 1.9-3.5l-3-4.5a6 6 0 0 0-3.8-2.5l-8.4-1.7a3 3 0 0 1-2-1.4L19.5 6.6A3 3 0 0 0 17 5h-6a3 3 0 0 0-3 1Z" stroke="var(--lime-300)" stroke-width="1.6" stroke-linejoin="round" /><path d="M8 22.5h30" stroke="var(--lime-300)" stroke-width="1.6" /><path d="M12 6.3l3.3 6.4M17 6.3l3.3 6.4" stroke="var(--lime-300)" stroke-width="1.4" stroke-linecap="round" /></svg>
      </div>
      <div class="prop prop--trophy">
        <svg viewBox="0 0 40 46" fill="none"><path d="M11 6h18v10a9 9 0 0 1-18 0V6Z" stroke="var(--amber-400)" stroke-width="1.6" /><path d="M11 9H5a4 4 0 0 0 4.5 6.5M29 9h6a4 4 0 0 1-4.5 6.5" stroke="var(--amber-400)" stroke-width="1.6" /><path d="M20 25v6M13 37h14l-1.6-4.5a2 2 0 0 0-1.9-1.5h-6.9a2 2 0 0 0-1.9 1.5L13 37Z" stroke="var(--amber-400)" stroke-width="1.6" stroke-linejoin="round" /></svg>
      </div>
      <div class="prop prop--flag">
        <svg viewBox="0 0 30 46" fill="none"><path d="M4 44V4" stroke="var(--lime-300)" stroke-width="1.8" stroke-linecap="round" /><path d="M4 5l20 6-20 6V5Z" fill="var(--lime-300)" opacity=".85" /><path d="M2 44a10 10 0 0 1 10-10" stroke="var(--lime-300)" stroke-width="1.4" /></svg>
      </div>
    </div>

    <router-link to="/" class="brand">
      <svg width="30" height="30" viewBox="0 0 36 36" fill="none" class="brand__mark">
        <circle cx="18" cy="18" r="17" stroke="var(--lime-400)" stroke-width="2" />
        <path d="M10 13L18 8L26 13L24 22L18 26L12 22L10 13Z" fill="var(--chalk-050)" />
        <circle cx="18" cy="17" r="4.2" fill="var(--night-950)" />
      </svg>
      <span><strong>Dream</strong>League</span>
    </router-link>

    <div class="content">
      <!-- Cột thông tin -->
      <div class="pitch-info">
        <span class="eyebrow">Bảo mật tài khoản đa kênh</span>
        <h1 class="headline">
          Khôi phục tài khoản,<br />
          tiếp tục <span class="headline__accent">đam mê</span>
        </h1>
        <p class="desc">
          Linh hoạt nhận mã xác thực OTP qua Email hoặc SMS trực tiếp vào số điện thoại
          để lấy lại quyền truy cập tài khoản DreamLeague của bạn an toàn nhất.
        </p>

        <!-- Sơ đồ đường chuyền động -->
        <div class="pitch-diagram" role="img" aria-label="Sơ đồ đường chuyền minh hoạ">
          <svg viewBox="0 0 320 150" xmlns="http://www.w3.org/2000/svg">
            <rect x="4" y="4" width="312" height="142" rx="10" class="pitch-outline pitch-draw" style="--pd:0s" />
            <line x1="160" y1="4" x2="160" y2="146" class="pitch-outline pitch-draw" style="--pd:.15s" />
            <circle cx="160" cy="75" r="20" class="pitch-outline pitch-draw" style="--pd:.3s" />
            <path id="passPath3" d="M40,118 C86,104 108,74 160,74 C206,74 220,42 268,26" class="pass-path pitch-draw" style="--pd:.45s" />
            <g class="players">
              <circle cx="40" cy="118" r="6" class="player" style="--pi:0" />
              <circle cx="118" cy="92" r="6" class="player" style="--pi:1" />
              <circle cx="196" cy="58" r="6" class="player" style="--pi:2" />
              <circle cx="268" cy="26" r="6" class="player player--target" style="--pi:3" />
            </g>
            <circle r="4" class="ball-dot">
              <animateMotion dur="4.2s" repeatCount="indefinite" rotate="auto" calcMode="linear">
                <mpath href="#passPath3" />
              </animateMotion>
            </circle>
          </svg>
        </div>

        <ul class="feature-list">
          <li v-for="(f, i) in features" :key="f" class="feature" :style="{ '--i': i }">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none">
              <path class="check-draw" d="M5 13L9.5 17.5L19 7" stroke="var(--lime-400)" stroke-width="2.8" stroke-linecap="round" stroke-linejoin="round" />
            </svg>
            {{ f }}
          </li>
        </ul>

        <div class="stat-row">
          <div class="stat" v-for="(s, i) in stats" :key="s.label" :style="{ '--i': i }">
            <p class="stat__num">{{ s.value }}<span>{{ s.suffix }}</span></p>
            <p class="stat__label">{{ s.label }}</p>
          </div>
        </div>
      </div>

      <!-- Thẻ khôi phục mật khẩu -->
      <div
        class="ticket-wrap"
        :style="{ transform: `perspective(1100px) rotateX(${tilt.x}deg) rotateY(${tilt.y}deg)` }"
        @mousemove="onCardMove"
        @mouseleave="onCardLeave"
      >
        <div class="ticket">
          <span class="ticket__notch ticket__notch--l"></span>
          <span class="ticket__notch ticket__notch--r"></span>
          <span class="ticket__chase" aria-hidden="true"></span>
          <div class="ticket__particles" aria-hidden="true">
            <span v-for="n in 12" :key="n" class="spark" :style="{ '--i': n }"></span>
          </div>

          <router-link to="/" class="ticket__mobile-brand">
            <svg width="26" height="26" viewBox="0 0 36 36" fill="none">
              <circle cx="18" cy="18" r="17" stroke="var(--turf-500)" stroke-width="2" />
              <path d="M10 13L18 8L26 13L24 22L18 26L12 22L10 13Z" fill="var(--night-950)" />
              <circle cx="18" cy="17" r="4.2" fill="var(--chalk-050)" />
            </svg>
            <span><strong>Dream</strong>League</span>
          </router-link>

          <div class="ticket__head">
            <div>
              <span class="ticket__kicker">Khôi phục · Bảo mật</span>
              <h2>{{ tieuDeTheoBuoc }}</h2>
              <p class="ticket__desc">{{ moTaTheoBuoc }}</p>
            </div>
            <router-link to="/dang-nhap" class="ticket__home">
              <svg width="15" height="15" viewBox="0 0 24 24" fill="none">
                <path d="M19 12H5M5 12L11 6M5 12L11 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
              </svg>
              <span>Đăng nhập</span>
            </router-link>
          </div>

          <div class="ticket__perf" aria-hidden="true"></div>

          <!-- Thanh chỉ báo tiến trình -->
          <div v-if="currentStep < 4" class="step-indicator">
            <div class="step-item" :class="{ 'step-item--active': currentStep >= 1, 'step-item--done': currentStep > 1 }">
              <span class="step-num">1</span>
              <span class="step-label">{{ selectedMethod === 'email' ? 'Email' : 'Số ĐT' }}</span>
            </div>
            <div class="step-line" :class="{ 'step-line--active': currentStep >= 2 }"></div>
            <div class="step-item" :class="{ 'step-item--active': currentStep >= 2, 'step-item--done': currentStep > 2 }">
              <span class="step-num">2</span>
              <span class="step-label">OTP</span>
            </div>
            <div class="step-line" :class="{ 'step-line--active': currentStep >= 3 }"></div>
            <div class="step-item" :class="{ 'step-item--active': currentStep >= 3 }">
              <span class="step-num">3</span>
              <span class="step-label">Mật khẩu</span>
            </div>
          </div>

          <Transition name="alert-drop">
            <div v-if="errorMessage" class="alert alert--error">
              <svg width="17" height="17" viewBox="0 0 24 24" fill="none">
                <circle cx="12" cy="12" r="9" stroke="#c8483a" stroke-width="1.6" />
                <path d="M12 8V13" stroke="#c8483a" stroke-width="1.6" stroke-linecap="round" />
                <circle cx="12" cy="16.2" r="0.9" fill="#c8483a" />
              </svg>
              <span>{{ errorMessage }}</span>
            </div>
          </Transition>
          <Transition name="alert-drop">
            <div v-if="infoMessage" class="alert alert--success">
              <svg width="17" height="17" viewBox="0 0 24 24" fill="none">
                <circle cx="12" cy="12" r="9" stroke="var(--turf-700)" stroke-width="1.6" />
                <path d="M12 11V16" stroke="var(--turf-700)" stroke-width="1.6" stroke-linecap="round" />
                <circle cx="12" cy="8" r="0.9" fill="var(--turf-700)" />
              </svg>
              <span>{{ infoMessage }}</span>
            </div>
          </Transition>

          <!-- ===== BƯỚC 1 ===== -->
          <div v-if="currentStep === 1">
            <div class="method-selector">
              <label class="method-selector__label">Chọn hình thức nhận mã OTP</label>
              <div class="method-tabs">
                <button type="button" class="method-tab" :class="{ 'method-tab--active': selectedMethod === 'email' }" @click="doiPhuongThuc('email')">
                  <span class="method-tab__icon">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                      <path d="M3 6.5C3 5.67 3.67 5 4.5 5H19.5C20.33 5 21 5.67 21 6.5V17.5C21 18.33 20.33 19 19.5 19H4.5C3.67 19 3 18.33 3 17.5V6.5Z" stroke="currentColor" stroke-width="1.8" />
                      <path d="M4 6.5L12 13L20 6.5" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" />
                    </svg>
                  </span>
                  <span class="method-tab__text">
                    <strong>Qua Email</strong>
                    <small>Nhận OTP vào hộp thư</small>
                  </span>
                </button>
                <button type="button" class="method-tab" :class="{ 'method-tab--active': selectedMethod === 'sms' }" @click="doiPhuongThuc('sms')">
                  <span class="method-tab__icon">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                      <rect x="5" y="2" width="14" height="20" rx="3" stroke="currentColor" stroke-width="1.8" />
                      <circle cx="12" cy="18" r="1" fill="currentColor" />
                      <line x1="9" y1="5" x2="15" y2="5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
                    </svg>
                  </span>
                  <span class="method-tab__text">
                    <strong>Qua SMS</strong>
                    <small>Nhận OTP vào điện thoại</small>
                  </span>
                </button>
              </div>
            </div>

            <form v-if="selectedMethod === 'email'" class="ticket-form" novalidate @submit.prevent="onGuiMaOtp">
              <div class="line-field" :class="{ 'line-field--filled': form.email, 'line-field--error': errors.email }">
                <label for="email">Địa chỉ Email đã đăng ký</label>
                <div class="line-field__row">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
                    <path d="M3 6.5C3 5.67 3.67 5 4.5 5H19.5C20.33 5 21 5.67 21 6.5V17.5C21 18.33 20.33 19 19.5 19H4.5C3.67 19 3 18.33 3 17.5V6.5Z" stroke="currentColor" stroke-width="1.5" />
                    <path d="M4 6.5L12 13L20 6.5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
                  </svg>
                  <input id="email" v-model.trim="form.email" type="email" placeholder="example@gmail.com" autocomplete="email" :disabled="submitting" @input="errors.email = ''" />
                </div>
                <span class="line-field__bar"></span>
                <p v-if="errors.email" class="line-field__error">{{ errors.email }}</p>
              </div>

              <button type="submit" class="kickoff-btn" :disabled="submitting">
                <span class="kickoff-btn__ring" aria-hidden="true"></span>
                <span class="kickoff-btn__shine" aria-hidden="true"></span>
                <span v-if="submitting" class="ball-spinner" aria-hidden="true">
                  <svg viewBox="0 0 24 24" width="17" height="17"><circle cx="12" cy="12" r="9" fill="#fff" /><path d="M12 6L15.5 8.5L14.2 12.7H9.8L8.5 8.5Z" fill="#0e2a1b" /></svg>
                </span>
                <span>{{ submitting ? 'Đang gửi mã...' : 'Gửi mã xác thực qua Email' }}</span>
              </button>
            </form>

            <form v-else class="ticket-form" novalidate @submit.prevent="onGuiMaOtp">
              <div class="line-field" :class="{ 'line-field--filled': form.soDienThoai, 'line-field--error': errors.soDienThoai }">
                <label for="soDienThoai">Số điện thoại đã đăng ký</label>
                <div class="line-field__row">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
                    <path d="M4.5 4.5h3.2l1.3 4.3-2 1.5a11.5 11.5 0 0 0 5.7 5.7l1.5-2 4.3 1.3v3.2c0 .8-.7 1.4-1.5 1.3C9.5 19.2 4.8 14.5 4.2 6c0-.8.5-1.5 1.3-1.5Z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round" />
                  </svg>
                  <input id="soDienThoai" v-model.trim="form.soDienThoai" type="tel" placeholder="0987654321" autocomplete="tel" :disabled="submitting" @input="errors.soDienThoai = ''" />
                </div>
                <span class="line-field__bar"></span>
                <p v-if="errors.soDienThoai" class="line-field__error">{{ errors.soDienThoai }}</p>
              </div>

              <button type="submit" class="kickoff-btn" :disabled="submitting">
                <span class="kickoff-btn__ring" aria-hidden="true"></span>
                <span class="kickoff-btn__shine" aria-hidden="true"></span>
                <span v-if="submitting" class="ball-spinner" aria-hidden="true">
                  <svg viewBox="0 0 24 24" width="17" height="17"><circle cx="12" cy="12" r="9" fill="#fff" /><path d="M12 6L15.5 8.5L14.2 12.7H9.8L8.5 8.5Z" fill="#0e2a1b" /></svg>
                </span>
                <span>{{ submitting ? 'Đang gửi mã...' : 'Gửi mã xác thực qua SMS' }}</span>
              </button>
            </form>
          </div>

          <!-- ===== BƯỚC 2 ===== -->
          <form v-else-if="currentStep === 2" class="ticket-form" novalidate @submit.prevent="onXacThucOtp">
            <div class="otp-recipient-box">
              <svg v-if="selectedMethod === 'email'" width="18" height="18" viewBox="0 0 24 24" fill="none">
                <path d="M3 8L10.89 13.26C11.56 13.71 12.44 13.71 13.11 13.26L21 8M5 19H19C20.1 19 21 18.1 21 17V7C21 5.9 20.1 5 19 5H5C3.9 5 3 5.9 3 7V17C3 18.1 3.9 19 5 19Z" stroke="var(--lime-300)" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" />
              </svg>
              <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none">
                <rect x="5" y="2" width="14" height="20" rx="3" stroke="var(--lime-300)" stroke-width="1.8" />
                <circle cx="12" cy="18" r="1" fill="var(--lime-300)" />
                <line x1="9" y1="5" x2="15" y2="5" stroke="var(--lime-300)" stroke-width="1.5" stroke-linecap="round" />
              </svg>
              <div>
                <span>Mã xác thực đã gửi tới {{ selectedMethod === 'email' ? 'Email:' : 'Số điện thoại:' }}</span>
                <strong>{{ selectedMethod === 'email' ? form.email : form.soDienThoai }}</strong>
              </div>
            </div>

            <div class="line-field" :class="{ 'line-field--error': errors.otp }">
              <label for="otp">Mã xác thực OTP (6 chữ số)</label>
              <div class="line-field__row line-field__row--otp">
                <input id="otp" v-model.trim="form.otp" type="text" maxlength="6" placeholder="123456" autocomplete="one-time-code" :disabled="submitting" @input="handleOtpInput" />
              </div>
              <span class="line-field__bar"></span>
              <p v-if="errors.otp" class="line-field__error">{{ errors.otp }}</p>
            </div>

            <div class="resend-row">
              <span v-if="countdown > 0" class="countdown-text">Gửi lại mã sau <strong>{{ countdown }}s</strong></span>
              <button v-else type="button" class="btn-resend" :disabled="submitting" @click="onGuiLaiOtp">↻ Gửi lại mã xác thực</button>
              <button type="button" class="btn-change-method" @click="quayLaiBuoc1">Đổi phương thức / số khác</button>
            </div>

            <button type="submit" class="kickoff-btn" :disabled="submitting">
              <span class="kickoff-btn__ring" aria-hidden="true"></span>
              <span class="kickoff-btn__shine" aria-hidden="true"></span>
              <span v-if="submitting" class="ball-spinner" aria-hidden="true">
                <svg viewBox="0 0 24 24" width="17" height="17"><circle cx="12" cy="12" r="9" fill="#fff" /><path d="M12 6L15.5 8.5L14.2 12.7H9.8L8.5 8.5Z" fill="#0e2a1b" /></svg>
              </span>
              <span>{{ submitting ? 'Đang kiểm tra...' : 'Xác thực mã OTP' }}</span>
            </button>
          </form>

          <!-- ===== BƯỚC 3 ===== -->
          <form v-else-if="currentStep === 3" class="ticket-form" novalidate @submit.prevent="onDatLaiMatKhau">
            <div class="line-field" :class="{ 'line-field--filled': form.newPassword, 'line-field--error': errors.newPassword }">
              <label for="newPassword">Mật khẩu mới</label>
              <div class="line-field__row">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
                  <rect x="5" y="11" width="14" height="9" rx="2" stroke="currentColor" stroke-width="1.5" />
                  <path d="M8 11V8a4 4 0 0 1 8 0v3" stroke="currentColor" stroke-width="1.5" />
                </svg>
                <input id="newPassword" v-model="form.newPassword" :type="showNewPassword ? 'text' : 'password'" placeholder="Tối thiểu 6 ký tự" autocomplete="new-password" :disabled="submitting" @input="errors.newPassword = ''" />
                <button type="button" class="line-field__toggle" @click="showNewPassword = !showNewPassword">
                  <svg v-if="showNewPassword" width="16" height="16" viewBox="0 0 24 24" fill="none"><path d="M2 12C2 12 5.5 5.5 12 5.5S22 12 22 12 18.5 18.5 12 18.5 2 12 2 12Z" stroke="currentColor" stroke-width="1.5" /><circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="1.5" /></svg>
                  <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none"><path d="M3 3L21 21" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" /><path d="M10.6 5.7C11.05 5.6 11.52 5.5 12 5.5C18.5 5.5 22 12 22 12C22 12 21.18 13.5 19.6 15.1M6.4 6.9C4 8.5 2 12 2 12C2 12 5.5 18.5 12 18.5C13.7 18.5 15.16 18.07 16.4 17.4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" /></svg>
                </button>
              </div>
              <span class="line-field__bar"></span>
              <p v-if="errors.newPassword" class="line-field__error">{{ errors.newPassword }}</p>
            </div>

            <div class="line-field" :class="{ 'line-field--filled': form.confirmPassword, 'line-field--error': errors.confirmPassword }">
              <label for="confirmPassword">Xác nhận mật khẩu mới</label>
              <div class="line-field__row">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
                  <rect x="5" y="11" width="14" height="9" rx="2" stroke="currentColor" stroke-width="1.5" />
                  <path d="M8 11V8a4 4 0 0 1 8 0v3" stroke="currentColor" stroke-width="1.5" />
                </svg>
                <input id="confirmPassword" v-model="form.confirmPassword" :type="showConfirmPassword ? 'text' : 'password'" placeholder="Nhập lại mật khẩu mới" autocomplete="new-password" :disabled="submitting" @input="errors.confirmPassword = ''" />
                <button type="button" class="line-field__toggle" @click="showConfirmPassword = !showConfirmPassword">
                  <svg v-if="showConfirmPassword" width="16" height="16" viewBox="0 0 24 24" fill="none"><path d="M2 12C2 12 5.5 5.5 12 5.5S22 12 22 12 18.5 18.5 12 18.5 2 12 2 12Z" stroke="currentColor" stroke-width="1.5" /><circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="1.5" /></svg>
                  <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none"><path d="M3 3L21 21" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" /><path d="M10.6 5.7C11.05 5.6 11.52 5.5 12 5.5C18.5 5.5 22 12 22 12C22 12 21.18 13.5 19.6 15.1M6.4 6.9C4 8.5 2 12 2 12C2 12 5.5 18.5 12 18.5C13.7 18.5 15.16 18.07 16.4 17.4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" /></svg>
                </button>
              </div>
              <span class="line-field__bar"></span>
              <p v-if="errors.confirmPassword" class="line-field__error">{{ errors.confirmPassword }}</p>
            </div>

            <button type="submit" class="kickoff-btn" :disabled="submitting">
              <span class="kickoff-btn__ring" aria-hidden="true"></span>
              <span class="kickoff-btn__shine" aria-hidden="true"></span>
              <span v-if="submitting" class="ball-spinner" aria-hidden="true">
                <svg viewBox="0 0 24 24" width="17" height="17"><circle cx="12" cy="12" r="9" fill="#fff" /><path d="M12 6L15.5 8.5L14.2 12.7H9.8L8.5 8.5Z" fill="#0e2a1b" /></svg>
              </span>
              <span>{{ submitting ? 'Đang cập nhật...' : 'Đổi mật khẩu & Hoàn tất' }}</span>
            </button>
          </form>

          <!-- ===== BƯỚC 4: THÀNH CÔNG ===== -->
          <div v-else-if="currentStep === 4" class="success-screen">
            <div class="success-icon-wrap">
              <svg width="30" height="30" viewBox="0 0 24 24" fill="none">
                <path d="M8 12.5L10.5 15L16 9.5" stroke="var(--night-950)" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round" />
              </svg>
            </div>
            <h2>Cài lại mật khẩu thành công!</h2>
            <p>Mật khẩu mới của bạn đã được cập nhật thành công. Bạn có thể đăng nhập lại ngay bây giờ.</p>
            <p v-if="autoRedirectTimer > 0" class="auto-redirect-note">
              Tự động chuyển về trang Đăng nhập sau <strong>{{ autoRedirectTimer }}s</strong>...
            </p>
            <button type="button" class="kickoff-btn" @click="veTrangDangNhap">
              <span class="kickoff-btn__ring" aria-hidden="true"></span>
              <span class="kickoff-btn__shine" aria-hidden="true"></span>
              <span>Đăng nhập ngay</span>
            </button>
          </div>

          <p v-if="currentStep < 4" class="ticket-switch">
            Đã nhớ mật khẩu? <router-link to="/dang-nhap">Đăng nhập ngay</router-link>
          </p>
        </div>
      </div>
    </div>

    <!-- ===== CON TRỎ CHUỘT HÌNH QUẢ BÓNG ===== -->
    <div v-if="cursorEnabled" ref="cursorBall" class="ball-cursor" :class="{ 'ball-cursor--target': isPointerTarget, 'ball-cursor--down': isPressed }" aria-hidden="true">
      <svg viewBox="0 0 40 40">
        <circle cx="20" cy="20" r="18" fill="var(--chalk-050)" stroke="var(--night-950)" stroke-width="1.6" />
        <polygon points="20,10 26,14.5 24,21.5 16,21.5 14,14.5" fill="var(--night-950)" />
        <path d="M20,10 L20,3 M26,14.5 L32.5,10.3 M24,21.5 L28.3,28 M16,21.5 L11.7,28 M14,14.5 L7.5,10.3" stroke="var(--night-950)" stroke-width="1.3" fill="none" />
      </svg>
      <span class="ball-cursor__shadow"></span>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import authService from '../../services/authService'

const router = useRouter()

// Các bước: 1 = Chọn phương thức & Nhập Email/SĐT, 2 = Nhập OTP, 3 = Đổi Mật Khẩu, 4 = Thành Công
const currentStep = ref(1)

// Phương thức khôi phục: 'email' hoặc 'sms'
const selectedMethod = ref('email')

const form = reactive({
  email: '',
  soDienThoai: '',
  otp: '',
  newPassword: '',
  confirmPassword: ''
})

const errors = reactive({
  email: '',
  soDienThoai: '',
  otp: '',
  newPassword: '',
  confirmPassword: ''
})

const showNewPassword = ref(false)
const showConfirmPassword = ref(false)
const submitting = ref(false)
const errorMessage = ref('')
const infoMessage = ref('')

const countdown = ref(0)
let timerInterval = null

const autoRedirectTimer = ref(3)
let redirectInterval = null

const features = [
  'Xác thực OTP tức thì qua Email & SMS',
  'Mã hóa mật khẩu bảo mật chuẩn BCrypt',
  'Hỗ trợ 24/7 từ ban quản trị DreamLeague'
]

const stats = [
  { value: '120', suffix: '+', label: 'Sân liên kết' },
  { value: '35', suffix: 'k+', label: 'Lượt đặt / năm' },
  { value: '4.8', suffix: '/5', label: 'Đánh giá' }
]

const tieuDeTheoBuoc = computed(() => {
  if (currentStep.value === 1) return 'Quên mật khẩu'
  if (currentStep.value === 2) return 'Xác thực OTP'
  if (currentStep.value === 3) return 'Cài lại mật khẩu'
  return 'Thành công'
})

const moTaTheoBuoc = computed(() => {
  if (currentStep.value === 1) {
    return selectedMethod.value === 'email'
      ? 'Nhập email đã đăng ký tài khoản để nhận mã xác thực OTP.'
      : 'Nhập số điện thoại đã đăng ký để nhận mã xác thực OTP qua tin nhắn SMS.'
  }
  if (currentStep.value === 2) {
    return selectedMethod.value === 'email'
      ? 'Nhập mã 6 chữ số đã được gửi tới hòm thư của bạn.'
      : 'Nhập mã 6 chữ số đã được gửi tới tin nhắn SMS trên điện thoại của bạn.'
  }
  if (currentStep.value === 3) return 'Thiết lập mật khẩu mới có ít nhất 6 ký tự.'
  return 'Mật khẩu tài khoản của bạn đã được thay đổi an toàn.'
})

function doiPhuongThuc(method) {
  selectedMethod.value = method
  errorMessage.value = ''
  infoMessage.value = ''
  errors.email = ''
  errors.soDienThoai = ''
}

function batDauDemNguoc(giay = 60) {
  countdown.value = giay
  clearInterval(timerInterval)
  timerInterval = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(timerInterval)
    }
  }, 1000)
}

function handleOtpInput(e) {
  form.otp = e.target.value.replace(/\D/g, '')
  errors.otp = ''
}

// BƯỚC 1: GỬI MÃ OTP VỀ EMAIL HOẶC SMS
async function onGuiMaOtp() {
  errorMessage.value = ''
  infoMessage.value = ''
  errors.email = ''
  errors.soDienThoai = ''

  if (selectedMethod.value === 'email') {
    if (!form.email) {
      errors.email = 'Vui lòng nhập địa chỉ email của bạn'
      return
    }
    const isEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)
    if (!isEmail) {
      errors.email = 'Địa chỉ email không đúng định dạng'
      return
    }
  } else {
    if (!form.soDienThoai) {
      errors.soDienThoai = 'Vui lòng nhập số điện thoại của bạn'
      return
    }
    const isPhone = /^(0|\+84)[0-9]{9}$/.test(form.soDienThoai.replace(/\s+/g, ''))
    if (!isPhone) {
      errors.soDienThoai = 'Số điện thoại không đúng định dạng (10 số, VD: 0987654321)'
      return
    }
  }

  submitting.value = true
  try {
    if (selectedMethod.value === 'email') {
      const res = await authService.guiMaOtp(form.email)
      infoMessage.value = res.message || 'Mã xác thực đã được gửi về email của bạn!'
    } else {
      const res = await authService.guiMaOtpSms(form.soDienThoai.replace(/\s+/g, ''))
      infoMessage.value = res.message || 'Mã xác thực đã được gửi về số điện thoại của bạn!'
    }
    currentStep.value = 2
    batDauDemNguoc(60)
  } catch (err) {
    errorMessage.value = err.message || 'Không thể gửi mã OTP. Vui lòng thử lại.'
  } finally {
    submitting.value = false
  }
}

// GỬI LẠI MÃ OTP
async function onGuiLaiOtp() {
  if (countdown.value > 0) return
  errorMessage.value = ''
  infoMessage.value = ''

  submitting.value = true
  try {
    if (selectedMethod.value === 'email') {
      const res = await authService.guiMaOtp(form.email)
      infoMessage.value = res.message || 'Đã gửi lại mã xác thực mới vào email của bạn!'
    } else {
      const res = await authService.guiMaOtpSms(form.soDienThoai.replace(/\s+/g, ''))
      infoMessage.value = res.message || 'Đã gửi lại mã xác thực mới vào số điện thoại của bạn!'
    }
    batDauDemNguoc(60)
  } catch (err) {
    errorMessage.value = err.message || 'Gửi lại mã thất bại. Vui lòng thử lại.'
  } finally {
    submitting.value = false
  }
}

function quayLaiBuoc1() {
  currentStep.value = 1
  errorMessage.value = ''
  infoMessage.value = ''
  form.otp = ''
}

// BƯỚC 2: XÁC THỰC MÃ OTP
async function onXacThucOtp() {
  errorMessage.value = ''
  infoMessage.value = ''
  errors.otp = ''

  if (!form.otp) {
    errors.otp = 'Vui lòng nhập mã xác thực 6 số'
    return
  }
  if (form.otp.length !== 6) {
    errors.otp = 'Mã xác thực phải gồm đúng 6 chữ số'
    return
  }

  submitting.value = true
  try {
    if (selectedMethod.value === 'email') {
      const res = await authService.xacThucMaOtp(form.email, form.otp)
      infoMessage.value = res.message || 'Xác thực mã OTP thành công!'
    } else {
      const res = await authService.xacThucMaOtpSms(form.soDienThoai.replace(/\s+/g, ''), form.otp)
      infoMessage.value = res.message || 'Xác thực mã OTP thành công!'
    }
    currentStep.value = 3
  } catch (err) {
    errorMessage.value = err.message || 'Mã xác thực không chính xác hoặc đã hết hạn.'
  } finally {
    submitting.value = false
  }
}

// BƯỚC 3: ĐỔI MẬT KHẨU MỚI
async function onDatLaiMatKhau() {
  errorMessage.value = ''
  infoMessage.value = ''
  errors.newPassword = ''
  errors.confirmPassword = ''

  if (!form.newPassword) {
    errors.newPassword = 'Vui lòng nhập mật khẩu mới'
    return
  }
  if (form.newPassword.length < 6) {
    errors.newPassword = 'Mật khẩu mới phải có ít nhất 6 ký tự'
    return
  }
  if (form.newPassword !== form.confirmPassword) {
    errors.confirmPassword = 'Mật khẩu xác nhận không khớp'
    return
  }

  submitting.value = true
  try {
    if (selectedMethod.value === 'email') {
      await authService.datLaiMatKhau(form.email, form.otp, form.newPassword)
    } else {
      await authService.datLaiMatKhauSms(form.soDienThoai.replace(/\s+/g, ''), form.otp, form.newPassword)
    }
    currentStep.value = 4

    autoRedirectTimer.value = 3
    clearInterval(redirectInterval)
    redirectInterval = setInterval(() => {
      autoRedirectTimer.value--
      if (autoRedirectTimer.value <= 0) {
        clearInterval(redirectInterval)
        veTrangDangNhap()
      }
    }, 1000)
  } catch (err) {
    errorMessage.value = err.message || 'Đặt lại mật khẩu thất bại. Vui lòng thử lại.'
  } finally {
    submitting.value = false
  }
}

function veTrangDangNhap() {
  clearInterval(redirectInterval)
  router.push('/dang-nhap')
}

// Spotlight theo con trỏ chuột trên toàn bộ khung cảnh
const spotlight = reactive({ x: 50, y: 30 })
function onSceneMove(e) {
  const rect = e.currentTarget.getBoundingClientRect()
  spotlight.x = ((e.clientX - rect.left) / rect.width) * 100
  spotlight.y = ((e.clientY - rect.top) / rect.height) * 100
}
function onSceneLeave() {
  spotlight.x = 50
  spotlight.y = 30
}

// Nghiêng nhẹ tấm thẻ theo chuột
const prefersReducedMotion = typeof window !== 'undefined' && window.matchMedia
  ? window.matchMedia('(prefers-reduced-motion: reduce)').matches
  : false
const tilt = reactive({ x: 0, y: 0 })
function onCardMove(e) {
  if (prefersReducedMotion) return
  const rect = e.currentTarget.getBoundingClientRect()
  const px = (e.clientX - rect.left) / rect.width - 0.5
  const py = (e.clientY - rect.top) / rect.height - 0.5
  tilt.x = +(py * -4).toFixed(2)
  tilt.y = +(px * 4).toFixed(2)
}
function onCardLeave() {
  tilt.x = 0
  tilt.y = 0
}

// Con trỏ chuột hình quả bóng
const cursorEnabled = ref(false)
const cursorBall = ref(null)
const isPointerTarget = ref(false)
const isPressed = ref(false)
let mouseX = 0
let mouseY = 0
let ballX = 0
let ballY = 0
let rotation = 0
let rafId = null

function onWindowMouseMove(e) {
  mouseX = e.clientX
  mouseY = e.clientY
}
function onWindowMouseOver(e) {
  isPointerTarget.value = !!e.target.closest('a, button, input, label, [role="button"]')
}
function onWindowMouseDown() {
  isPressed.value = true
}
function onWindowMouseUp() {
  isPressed.value = false
}
function tickCursor() {
  const ease = 0.22
  const dx = mouseX - ballX
  const dy = mouseY - ballY
  ballX += dx * ease
  ballY += dy * ease
  rotation += dx * 0.7
  if (cursorBall.value) {
    cursorBall.value.style.transform = `translate(${ballX - 15}px, ${ballY - 15}px) rotate(${rotation}deg)`
  }
  rafId = requestAnimationFrame(tickCursor)
}

onMounted(() => {
  const hasFinePointer = typeof window !== 'undefined' && window.matchMedia
    ? window.matchMedia('(pointer: fine)').matches
    : false
  cursorEnabled.value = hasFinePointer && !prefersReducedMotion
  if (cursorEnabled.value) {
    window.addEventListener('mousemove', onWindowMouseMove)
    window.addEventListener('mouseover', onWindowMouseOver)
    window.addEventListener('mousedown', onWindowMouseDown)
    window.addEventListener('mouseup', onWindowMouseUp)
    rafId = requestAnimationFrame(tickCursor)
  }
})

onUnmounted(() => {
  clearInterval(timerInterval)
  clearInterval(redirectInterval)
  window.removeEventListener('mousemove', onWindowMouseMove)
  window.removeEventListener('mouseover', onWindowMouseOver)
  window.removeEventListener('mousedown', onWindowMouseDown)
  window.removeEventListener('mouseup', onWindowMouseUp)
  if (rafId) cancelAnimationFrame(rafId)
})
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Bebas+Neue&family=Oswald:wght@500;600;700&family=Manrope:wght@400;500;600;700;800&family=Space+Mono:wght@400;700&display=swap');
</style>

<style scoped>
.scene {
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
  --ink-900: #0b1710;
  --slate-500: #5c6b60;
  --font-display: 'Oswald', 'Manrope', sans-serif;
  --font-body: 'Manrope', sans-serif;
  --font-mono: 'Space Mono', monospace;
  --font-clock: 'Bebas Neue', 'Oswald', sans-serif;

  position: relative;
  min-height: 100vh;
  width: 100%;
  overflow: hidden;
  background:
    repeating-linear-gradient(115deg, rgba(20, 60, 38, 0.55) 0 90px, rgba(9, 34, 21, 0.55) 90px 180px),
    radial-gradient(120% 100% at 15% 0%, var(--night-700) 0%, var(--night-950) 62%);
  font-family: var(--font-body);
  color: var(--chalk-050);
}
.scene--cursor, .scene--cursor * { cursor: none !important; }
.scene--cursor input, .scene--cursor textarea { cursor: text !important; }

/* ===== LỚP NỀN ===== */
.scene__spotlight {
  position: absolute; inset: 0; pointer-events: none; z-index: 1;
  background: radial-gradient(560px circle at var(--sx) var(--sy), rgba(182, 255, 60, 0.10), transparent 62%);
}
.scene__vignette { position: absolute; inset: 0; pointer-events: none; z-index: 1; box-shadow: inset 0 0 240px rgba(0, 0, 0, 0.6); }
.floodlight {
  position: absolute; top: -12%; width: 55vh; height: 145vh;
  background: conic-gradient(from 90deg at 50% 0%, transparent 42%, rgba(255, 244, 214, 0.16) 50%, transparent 58%);
  mix-blend-mode: screen; transform-origin: top center; pointer-events: none; z-index: 1;
}
.floodlight--l { left: -14%; }
.floodlight--r { right: -14%; }
.grass-drift {
  position: absolute; inset: -10% -20%; z-index: 0; opacity: 0.5;
  background: repeating-linear-gradient(100deg, rgba(35, 147, 90, 0.09) 0 70px, transparent 70px 140px);
}
.grass-mow {
  position: absolute; inset: 0; z-index: 0; opacity: 0.6;
  background: repeating-linear-gradient(115deg, rgba(255, 255, 255, 0.025) 0 90px, transparent 90px 180px);
}
.motes { position: absolute; inset: 0; pointer-events: none; z-index: 1; }
.mote {
  position: absolute; bottom: -10px; left: calc((var(--i) * 7.2%) + 1%);
  width: 3px; height: 3px; border-radius: 50%; background: var(--lime-300); opacity: 0;
}
.pitch-lines { position: absolute; inset: 0; width: 100%; height: 100%; opacity: 0.1; z-index: 1; pointer-events: none; }
.draw-line { stroke-dasharray: 1400; stroke-dashoffset: 0; }

/* ===== ĐẠO CỤ BÓNG ĐÁ TRÔI NỔI ===== */
.field-props { position: absolute; inset: 0; z-index: 1; pointer-events: none; }
.prop { position: absolute; opacity: 0.16; filter: drop-shadow(0 6px 10px rgba(0, 0, 0, 0.35)); }
.prop svg { width: 100%; height: 100%; display: block; }
.prop--ball-1 { width: 58px; height: 58px; top: 12%; right: 7%; }
.prop--ball-2 { width: 34px; height: 34px; bottom: 16%; right: 22%; opacity: 0.12; }
.prop--whistle { width: 70px; height: 46px; top: 68%; left: 4%; }
.prop--boot { width: 76px; height: 54px; top: 8%; left: 3%; opacity: 0.13; }
.prop--trophy { width: 52px; height: 60px; bottom: 8%; left: 20%; opacity: 0.13; }
.prop--flag { width: 40px; height: 62px; top: 32%; right: 3%; opacity: 0.13; }

/* ===== BẢNG ===== */
.brand {
  position: relative; z-index: 3; display: inline-flex; align-items: center; gap: 9px;
  font-size: 20px; color: var(--chalk-050); font-weight: 500; font-family: var(--font-display);
  margin: 26px 0 0 44px;
}
.brand span strong { color: var(--lime-400); font-weight: 700; }

/* ===== BỐ CỤC NỘI DUNG ===== */
.content {
  position: relative; z-index: 3;
  display: grid; grid-template-columns: 1.05fr 0.95fr; align-items: center;
  gap: 24px; max-width: 1320px; margin: 0 auto; padding: 40px 44px 64px;
  min-height: calc(100vh - 78px);
}

.pitch-info { max-width: 540px; }
.eyebrow {
  display: inline-block; font-family: var(--font-mono); font-size: 12px;
  letter-spacing: 0.16em; text-transform: uppercase; color: var(--lime-300);
}
.headline {
  font-family: var(--font-display); font-weight: 600; color: var(--chalk-050);
  font-size: clamp(32px, 3.4vw, 44px); line-height: 1.2; margin-top: 14px;
}
.headline__accent { color: var(--lime-400); }
.desc { margin-top: 16px; font-size: 15.5px; color: var(--chalk-200); opacity: 0.78; line-height: 1.65; max-width: 460px; }

/* ===== SƠ ĐỒ ĐƯỜNG CHUYỀN ===== */
.pitch-diagram { margin-top: 26px; width: 100%; max-width: 400px; }
.pitch-diagram svg { width: 100%; height: auto; display: block; }
.pitch-outline { fill: none; stroke: rgba(247, 251, 244, 0.28); stroke-width: 1.4; }
.pass-path { fill: none; stroke: var(--lime-400); stroke-width: 1.6; stroke-dasharray: 5 6; opacity: 0.85; }
.player { fill: var(--night-950); stroke: var(--chalk-050); stroke-width: 1.6; }
.player--target { stroke: var(--lime-400); }
.ball-dot { fill: var(--lime-400); }
.pitch-draw { stroke-dasharray: 900; stroke-dashoffset: 900; }

.feature-list { margin: 26px 0 0; padding: 0; list-style: none; display: flex; flex-direction: column; gap: 14px; }
.feature { display: flex; align-items: center; gap: 11px; color: var(--chalk-050); font-size: 14px; font-weight: 500; }
.feature svg { flex-shrink: 0; background: rgba(182, 255, 60, 0.1); border-radius: 50%; padding: 4px; box-sizing: content-box; }

.stat-row { display: flex; gap: 40px; margin-top: 30px; padding-top: 24px; border-top: 1px solid rgba(255, 255, 255, 0.12); }
.stat__num { font-family: var(--font-clock); font-size: 32px; font-weight: 400; color: var(--chalk-050); line-height: 1; letter-spacing: 0.01em; }
.stat__num span { color: var(--lime-400); }
.stat__label { font-size: 12px; color: var(--chalk-200); opacity: 0.65; margin-top: 6px; font-family: var(--font-mono); }

/* ===== THẺ KHÔI PHỤC MẬT KHẨU ===== */
.ticket-wrap { justify-self: center; width: 100%; max-width: 440px; transition: transform 0.25s ease-out; will-change: transform; }
.ticket {
  position: relative;
  background: linear-gradient(165deg, rgba(24, 61, 41, 0.94), rgba(6, 15, 10, 0.97));
  backdrop-filter: blur(14px);
  border: 1px solid rgba(182, 255, 60, 0.16);
  border-radius: 22px; padding: 34px 36px 30px; color: var(--chalk-050);
  box-shadow: 0 40px 90px -30px rgba(0, 0, 0, 0.65), 0 0 0 1px rgba(255, 255, 255, 0.04), 0 0 70px -20px rgba(182, 255, 60, 0.18);
  overflow: hidden;
}
.ticket > * { position: relative; z-index: 1; }
.ticket__notch, .ticket__chase, .ticket__particles { z-index: 0; }
.ticket__notch {
  position: absolute; top: 138px; width: 26px; height: 26px; border-radius: 50%;
  background: radial-gradient(circle at 40% 35%, var(--night-700), var(--night-950));
}
.ticket__notch--l { left: -13px; }
.ticket__notch--r { right: -13px; }
.ticket__perf {
  margin: 22px -36px 22px; height: 0;
  border-top: 1.5px dashed rgba(247, 251, 244, 0.18);
}
.ticket__chase {
  position: absolute; inset: -2px; border-radius: 24px; padding: 2px;
  background: conic-gradient(from var(--chase-angle, 0deg), transparent 0deg, var(--lime-400) 25deg, transparent 70deg);
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor; mask-composite: exclude; opacity: 0.6; pointer-events: none;
}
.ticket__particles { position: absolute; inset: 0; overflow: hidden; pointer-events: none; }
.spark {
  position: absolute; top: -8%; left: calc((var(--i) * 8%) + 2%);
  width: 3px; height: 3px; border-radius: 50%; background: var(--lime-300);
  box-shadow: 0 0 6px 1px rgba(182, 255, 60, 0.55); opacity: 0;
}

.ticket__mobile-brand { display: none; align-items: center; gap: 8px; color: var(--chalk-050); font-family: var(--font-display); font-size: 17px; margin-bottom: 18px; }
.ticket__mobile-brand strong { color: var(--lime-300); }

.ticket__head { display: flex; align-items: flex-start; justify-content: space-between; gap: 16px; }
.ticket__kicker { font-family: var(--font-mono); font-size: 11px; letter-spacing: 0.1em; text-transform: uppercase; color: var(--lime-300); }
.ticket__head h2 { font-family: var(--font-display); font-size: 24px; font-weight: 600; margin-top: 6px; color: var(--chalk-050); }
.ticket__desc { font-size: 13px; color: var(--chalk-200); opacity: 0.7; margin-top: 6px; line-height: 1.5; max-width: 280px; }
.ticket__home {
  display: inline-flex; align-items: center; gap: 6px; height: 32px; padding: 0 14px 0 10px; flex-shrink: 0;
  border-radius: 999px; border: 1.5px solid rgba(247, 251, 244, 0.18); color: var(--chalk-200);
  font-family: var(--font-mono); font-size: 12px; font-weight: 600; letter-spacing: 0.02em; white-space: nowrap;
  transition: border-color .15s ease, color .15s ease, transform .15s ease, background-color .15s ease;
}
.ticket__home:hover { border-color: var(--lime-400); color: var(--lime-300); background: rgba(182, 255, 60, 0.08); transform: translateX(-2px); }

/* ===== STEP INDICATOR ===== */
.step-indicator {
  display: flex; align-items: center; justify-content: space-between;
  margin-bottom: 20px; padding: 10px 12px;
  background: rgba(247, 251, 244, 0.05); border: 1px solid rgba(247, 251, 244, 0.12); border-radius: 12px;
}
.step-item { display: flex; align-items: center; gap: 7px; color: rgba(247, 251, 244, 0.45); font-size: 12px; font-weight: 600; }
.step-item--active { color: var(--chalk-050); }
.step-item--done { color: var(--lime-300); }
.step-num {
  width: 20px; height: 20px; border-radius: 50%; background: rgba(247, 251, 244, 0.14); color: var(--chalk-050);
  display: flex; align-items: center; justify-content: center; font-size: 10.5px; font-weight: 700; flex-shrink: 0;
}
.step-item--active .step-num { background: var(--chalk-050); color: var(--night-950); }
.step-item--done .step-num { background: var(--lime-400); color: var(--night-950); }
.step-line { flex: 1; height: 2px; background: rgba(247, 251, 244, 0.14); margin: 0 8px; border-radius: 2px; }
.step-line--active { background: var(--lime-400); }

/* ===== METHOD SELECTOR ===== */
.method-selector { margin-bottom: 20px; }
.method-selector__label { display: block; font-size: 12.5px; font-weight: 600; color: var(--chalk-200); opacity: .85; margin-bottom: 10px; }
.method-tabs { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; }
.method-tab {
  display: flex; align-items: center; gap: 10px; padding: 11px 12px;
  background: rgba(247, 251, 244, 0.04); border: 1.5px solid rgba(247, 251, 244, 0.14); border-radius: 12px;
  cursor: pointer; text-align: left; transition: all 0.2s ease;
}
.method-tab:hover { border-color: rgba(182, 255, 60, 0.35); background: rgba(247, 251, 244, 0.07); }
.method-tab--active { border-color: var(--lime-400) !important; background: rgba(182, 255, 60, 0.1) !important; }
.method-tab__icon {
  width: 32px; height: 32px; border-radius: 9px; background: rgba(247, 251, 244, 0.1);
  display: flex; align-items: center; justify-content: center; color: var(--chalk-200); flex-shrink: 0; transition: all .2s ease;
}
.method-tab--active .method-tab__icon { background: var(--lime-400); color: var(--night-950); }
.method-tab__text { display: flex; flex-direction: column; }
.method-tab__text strong { font-size: 12.5px; color: var(--chalk-050); line-height: 1.2; }
.method-tab__text small { font-size: 10.5px; color: var(--chalk-200); opacity: .65; margin-top: 2px; }

/* ===== HỘP THÔNG BÁO NGƯỜI NHẬN OTP ===== */
.otp-recipient-box {
  display: flex; align-items: center; gap: 11px; padding: 11px 14px;
  background: rgba(182, 255, 60, 0.08); border: 1px solid rgba(182, 255, 60, 0.22); border-radius: 12px;
  margin-bottom: 18px; font-size: 13px; color: var(--lime-300);
}
.otp-recipient-box strong { display: block; color: var(--chalk-050); font-size: 13.5px; word-break: break-all; }

/* Alert */
.alert { display: flex; align-items: flex-start; gap: 9px; padding: 12px 14px; border-radius: 12px; margin-bottom: 16px; font-size: 13.5px; line-height: 1.4; }
.alert--error { background: rgba(255, 90, 80, 0.12); border: 1px solid rgba(255, 90, 80, 0.32); color: #ff9686; }
.alert--success { background: rgba(182, 255, 60, 0.1); border: 1px solid rgba(182, 255, 60, 0.28); color: var(--lime-300); }
.alert-drop-enter-active { animation: alert-in .35s cubic-bezier(.34, 1.56, .64, 1); }
.alert-drop-leave-active { animation: alert-in .2s ease reverse; }
@keyframes alert-in { from { opacity: 0; transform: translateY(-8px); } to { opacity: 1; transform: translateY(0); } }

/* Trường nhập kiểu gạch chân */
.ticket-form { display: flex; flex-direction: column; margin-top: 4px; }
.line-field { margin-bottom: 20px; position: relative; }
.line-field label { display: block; font-size: 12.5px; font-weight: 600; color: var(--chalk-200); opacity: .85; margin-bottom: 6px; letter-spacing: 0.01em; }
.line-field__row { display: flex; align-items: center; gap: 10px; padding-bottom: 9px; color: var(--chalk-200); }
.line-field__row input {
  flex: 1; min-width: 0; border: none; background: transparent; font-family: var(--font-body);
  font-size: 15px; color: var(--chalk-050); outline: none;
}
.line-field__row input::placeholder { color: rgba(247, 251, 244, 0.35); }
.line-field__row--otp input {
  font-family: var(--font-mono); font-size: 26px; font-weight: 700; letter-spacing: 10px; text-align: center; padding-left: 10px;
}
.line-field__bar { position: relative; display: block; height: 1.5px; background: rgba(247, 251, 244, 0.18); }
.line-field__bar::after {
  content: ""; position: absolute; left: 0; top: 0; height: 100%; width: 0;
  background: var(--lime-400); transition: width .28s cubic-bezier(.4, 0, .2, 1);
}
.line-field:focus-within .line-field__bar::after { width: 100%; }
.line-field:focus-within label { color: var(--lime-300); }
.line-field:focus-within .line-field__row { color: var(--lime-300); }
.line-field--error .line-field__bar { background: rgba(255, 90, 80, 0.32); }
.line-field--error .line-field__bar::after { background: #ff6a56; width: 100%; }
.line-field--error label { color: #ff9686; }
.line-field__toggle { background: none; border: none; color: inherit; padding: 2px; display: flex; transition: transform .15s ease; }
.line-field__toggle:hover { transform: scale(1.08); }
.line-field__error { color: #ff9686; font-size: 12px; margin-top: 6px; font-weight: 500; }

/* ===== RESEND ROW ===== */
.resend-row { display: flex; align-items: center; justify-content: space-between; margin-bottom: 22px; font-size: 12.5px; flex-wrap: wrap; gap: 8px; }
.countdown-text { color: var(--chalk-200); opacity: .75; }
.countdown-text strong { color: var(--lime-300); }
.btn-resend { background: none; border: none; color: var(--lime-300); font-weight: 600; font-size: 12.5px; padding: 0; transition: color .15s; }
.btn-resend:hover:not(:disabled) { color: var(--lime-400); text-decoration: underline; }
.btn-change-method { background: none; border: none; color: var(--chalk-200); opacity: .6; font-size: 12px; padding: 0; transition: color .15s, opacity .15s; }
.btn-change-method:hover { color: var(--chalk-050); opacity: 1; text-decoration: underline; }

/* Nút chính */
.kickoff-btn {
  position: relative; width: 100%; height: 50px; border: none; border-radius: 999px; cursor: pointer;
  background: linear-gradient(135deg, var(--turf-500), var(--turf-700)); color: #fff;
  font-weight: 700; font-family: var(--font-body); font-size: 15px;
  display: flex; align-items: center; justify-content: center; gap: 8px;
  overflow: visible; isolation: isolate; transition: transform .15s ease, box-shadow .15s ease;
}
.kickoff-btn:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 14px 28px -10px rgba(20, 90, 51, 0.5); }
.kickoff-btn:active:not(:disabled) { transform: translateY(0); }
.kickoff-btn:disabled { opacity: 0.85; cursor: default; }
.kickoff-btn__ring { position: absolute; inset: -4px; border-radius: 999px; border: 1.5px solid var(--lime-400); opacity: 0; z-index: -1; }
.kickoff-btn:hover:not(:disabled) .kickoff-btn__ring { opacity: 0.5; }
.kickoff-btn__shine {
  position: absolute; inset: 0; z-index: 1; border-radius: 999px; overflow: hidden;
  background: linear-gradient(115deg, transparent 20%, rgba(255, 255, 255, 0.35) 45%, transparent 70%);
  transform: translateX(-120%);
}
.kickoff-btn:hover:not(:disabled) .kickoff-btn__shine { animation: shine .9s ease; }
@keyframes shine { to { transform: translateX(120%); } }
.kickoff-btn > span:not(.kickoff-btn__ring):not(.kickoff-btn__shine) { position: relative; z-index: 2; }
.ball-spinner { display: inline-flex; }

/* ===== MÀN HÌNH THÀNH CÔNG ===== */
.success-screen { text-align: center; padding: 6px 0 2px; }
.success-icon-wrap {
  margin: 0 auto 18px; width: 62px; height: 62px; border-radius: 50%;
  background: var(--lime-400); display: flex; align-items: center; justify-content: center;
  box-shadow: 0 0 0 8px rgba(182, 255, 60, 0.12), 0 10px 26px -10px rgba(182, 255, 60, 0.5);
}
.success-screen h2 { font-family: var(--font-display); font-size: 21px; font-weight: 600; color: var(--chalk-050); margin-bottom: 10px; }
.success-screen p { font-size: 13.5px; color: var(--chalk-200); opacity: .78; line-height: 1.6; margin-bottom: 18px; }
.auto-redirect-note { font-size: 12.5px !important; color: var(--lime-300) !important; opacity: 1 !important; }

.ticket-switch { text-align: center; font-size: 14px; color: var(--chalk-200); opacity: .8; margin-top: 22px; }
.ticket-switch a { color: var(--lime-300); font-weight: 700; }
.ticket-switch a:hover { color: var(--lime-400); text-decoration: underline; }

/* ===== CON TRỎ CHUỘT HÌNH QUẢ BÓNG ===== */
.ball-cursor {
  position: fixed; top: 0; left: 0; width: 30px; height: 30px; z-index: 999;
  pointer-events: none; will-change: transform; transition: width .18s ease, height .18s ease;
}
.ball-cursor svg { width: 100%; height: 100%; display: block; filter: drop-shadow(0 4px 6px rgba(0, 0, 0, 0.45)); transition: transform .18s cubic-bezier(.34, 1.56, .64, 1); }
.ball-cursor__shadow { position: absolute; left: 50%; bottom: -6px; width: 60%; height: 6px; border-radius: 50%; background: rgba(0, 0, 0, 0.35); transform: translateX(-50%); filter: blur(2px); }
.ball-cursor--target { width: 40px; height: 40px; }
.ball-cursor--target svg { transform: scale(1.08); filter: drop-shadow(0 0 10px rgba(182, 255, 60, 0.65)) drop-shadow(0 4px 6px rgba(0, 0, 0, 0.45)); }
.ball-cursor--down svg { transform: scale(0.78); }

/* ===== HIỆU ỨNG CHUYỂN ĐỘNG ===== */
@media (prefers-reduced-motion: no-preference) {
  .floodlight { animation: sweep 9s ease-in-out infinite alternate; }
  .floodlight--l { animation-delay: 0s; }
  .floodlight--r { animation-delay: -3.2s; }
  @keyframes sweep { 0% { transform: rotate(-13deg); } 100% { transform: rotate(13deg); } }

  .grass-drift { animation: drift 26s linear infinite; }
  .grass-mow { animation: drift 40s linear infinite reverse; }
  @keyframes drift { from { background-position: 0 0; } to { background-position: 400px 0; } }

  .mote { animation: rise 8s linear infinite; animation-delay: calc(var(--i) * -0.55s); }
  @keyframes rise { 0% { transform: translateY(0) scale(.6); opacity: 0; } 10% { opacity: .8; } 92% { opacity: .25; } 100% { transform: translateY(-100vh) scale(1.1); opacity: 0; } }

  .draw-line { stroke-dasharray: 1400; stroke-dashoffset: 1400; animation: draw 2.6s ease forwards; animation-delay: var(--delay); }
  @keyframes draw { to { stroke-dashoffset: 0; } }

  .prop { animation: prop-float 6s ease-in-out infinite, prop-in .6s ease forwards; opacity: 0; }
  .prop--ball-1 { animation-delay: .2s, .2s; animation-duration: 7s, .6s; }
  .prop--ball-2 { animation-delay: 1.1s, .35s; animation-duration: 5.5s, .6s; }
  .prop--whistle { animation-delay: .5s, .5s; animation-duration: 6.5s, .6s; }
  .prop--boot { animation-delay: .8s, .3s; animation-duration: 8s, .6s; }
  .prop--trophy { animation-delay: 1.4s, .65s; animation-duration: 7.2s, .6s; }
  .prop--flag { animation-delay: .3s, .45s; animation-duration: 6s, .6s; }
  @keyframes prop-in { to { opacity: 0.16; } }
  @keyframes prop-float { 0%, 100% { transform: translateY(0) rotate(-4deg); } 50% { transform: translateY(-16px) rotate(4deg); } }

  .brand { opacity: 0; animation: fade-down .6s ease forwards; }
  @keyframes fade-down { from { opacity: 0; transform: translateY(-8px); } to { opacity: 1; transform: translateY(0); } }
  .brand__mark { animation: spin-in .9s cubic-bezier(.34, 1.56, .64, 1) both; }
  @keyframes spin-in { from { transform: rotate(-90deg) scale(.4); opacity: 0; } to { transform: rotate(0) scale(1); opacity: 1; } }

  .eyebrow, .headline, .desc { opacity: 0; animation: fade-up .6s ease forwards; }
  .eyebrow { animation-delay: .15s; }
  .headline { animation-delay: .28s; }
  .desc { animation-delay: .42s; }
  @keyframes fade-up { to { opacity: 1; transform: translateY(0); } from { transform: translateY(10px); } }

  .headline__accent { display: inline-block; animation: accent-glow 2.8s ease-in-out infinite; animation-delay: 1.3s; }
  @keyframes accent-glow { 0%, 100% { text-shadow: 0 0 0 rgba(182, 255, 60, 0); } 50% { text-shadow: 0 0 22px rgba(182, 255, 60, 0.55); } }

  .pitch-diagram { opacity: 0; animation: fade-up .5s ease forwards; animation-delay: .5s; }
  .pitch-draw { animation: draw-pitch 1.2s ease forwards; animation-delay: calc(var(--pd) + .6s); }
  @keyframes draw-pitch { to { stroke-dashoffset: 0; } }
  .player { opacity: 0; transform-box: fill-box; transform-origin: center; animation: player-pop .4s cubic-bezier(.34,1.56,.64,1) forwards; animation-delay: calc(var(--pi) * 0.12s + 1.6s); }
  @keyframes player-pop { from { opacity: 0; transform: scale(0); } to { opacity: 1; transform: scale(1); } }
  .ball-dot { opacity: 0; animation: ball-in .3s ease forwards; animation-delay: 2.1s; }
  @keyframes ball-in { to { opacity: 1; } }

  .feature { opacity: 0; transform: translateX(-12px); animation: slide-right .5s ease forwards; animation-delay: calc(var(--i) * 0.12s + .95s); }
  @keyframes slide-right { to { opacity: 1; transform: translateX(0); } }
  .check-draw { stroke-dasharray: 30; stroke-dashoffset: 30; animation: draw-check .45s ease forwards; animation-delay: calc(var(--i, 0) * 0.12s + 1.25s); }
  @keyframes draw-check { to { stroke-dashoffset: 0; } }
  .feature svg { animation: icon-breathe 3.2s ease-in-out infinite; animation-delay: calc(var(--i) * 0.15s + 2s); }
  @keyframes icon-breathe { 0%, 100% { box-shadow: 0 0 0 0 rgba(182, 255, 60, 0); } 50% { box-shadow: 0 0 0 5px rgba(182, 255, 60, 0.12); } }

  .stat { opacity: 0; animation: fade-up .55s ease forwards; animation-delay: calc(var(--i) * 0.1s + 1.5s); }
  .stat__num { display: inline-block; animation: num-glow 3.4s ease-in-out infinite; animation-delay: calc(var(--i) * 0.2s + 2.4s); }
  @keyframes num-glow { 0%, 100% { opacity: 1; } 50% { opacity: .72; text-shadow: 0 0 12px rgba(182, 255, 60, 0.4); } }

  .ticket { opacity: 0; animation: ticket-in .75s cubic-bezier(.16, 1, .3, 1) forwards; animation-delay: .3s; }
  @keyframes ticket-in { from { opacity: 0; transform: translateY(26px) scale(.97); } to { opacity: 1; transform: translateY(0) scale(1); } }
  .ticket__chase { animation: chase 5s linear infinite; }
  @keyframes chase { to { --chase-angle: 360deg; } }
  @property --chase-angle { syntax: '<angle>'; inherits: false; initial-value: 0deg; }

  .spark { animation: spark-fall calc(6.5s + (var(--i) * 0.5s)) linear infinite; animation-delay: calc(var(--i) * -0.9s); }
  @keyframes spark-fall {
    0% { transform: translateY(0) translateX(0); opacity: 0; }
    8% { opacity: .9; }
    50% { transform: translateY(280px) translateX(6px); }
    90% { opacity: .2; }
    100% { transform: translateY(560px) translateX(14px); opacity: 0; }
  }

  .kickoff-btn { opacity: 0; animation: fade-up .5s ease forwards; animation-delay: 1.5s; }
  .ball-spinner { animation: ball-bounce .6s ease-in-out infinite; }
  @keyframes ball-bounce { 0%, 100% { transform: translateY(0) rotate(0); } 50% { transform: translateY(-5px) rotate(160deg); } }

  .success-icon-wrap { animation: success-pop .5s cubic-bezier(.34, 1.56, .64, 1) both; }
  @keyframes success-pop { from { opacity: 0; transform: scale(.5); } to { opacity: 1; transform: scale(1); } }
}

/* ===== RESPONSIVE ===== */
@media (max-width: 980px) {
  .content { grid-template-columns: 1fr; padding: 32px 22px 48px; gap: 40px; }
  .pitch-info { max-width: 100%; text-align: center; margin: 0 auto; }
  .stat-row { justify-content: center; }
  .feature-list { align-items: center; }
  .feature { justify-content: center; }
  .pitch-diagram { margin-left: auto; margin-right: auto; }
  .ticket-wrap { max-width: 460px; }
  .brand { margin-left: 22px; }
  .prop--boot, .prop--trophy, .prop--flag { display: none; }
}
@media (max-width: 640px) {
  .ticket { padding: 28px 22px 24px; }
  .ticket__mobile-brand { display: inline-flex; }
  .ticket__notch { top: 150px; }
  .ticket__head { flex-wrap: wrap; }
  .step-label { display: none; }
  .method-tabs { grid-template-columns: 1fr; }
  .ball-cursor { display: none; }
}
@media (pointer: coarse) {
  .scene--cursor, .scene--cursor * { cursor: auto !important; }
}
</style>