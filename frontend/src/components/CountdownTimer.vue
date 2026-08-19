<template>
  <!-- Widget đếm ngược -->
  <div class="ct-wrap" :class="stateClass" role="timer" :aria-label="`Còn ${displayMin} phút ${displaySec} giây`">
    <div class="ct-icon" aria-hidden="true">
      <svg v-if="state === 'normal'" width="18" height="18" viewBox="0 0 24 24" fill="none">
        <circle cx="12" cy="12" r="9" stroke="currentColor" stroke-width="2"/>
        <path d="M12 7v5l3.5 2" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
      </svg>
      <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none">
        <path d="M12 2L2 20h20L12 2Z" stroke="currentColor" stroke-width="2" stroke-linejoin="round"/>
        <path d="M12 9v5M12 17v.5" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
      </svg>
    </div>

    <div class="ct-info">
      <span class="ct-label">Thời gian thanh toán</span>
      <div class="ct-digits" aria-hidden="true">
        <div class="ct-digit-group">
          <div class="ct-digit">{{ displayMin[0] }}</div>
          <div class="ct-digit">{{ displayMin[1] }}</div>
          <span class="ct-sublabel">phút</span>
        </div>
        <span class="ct-colon">:</span>
        <div class="ct-digit-group">
          <div class="ct-digit">{{ displaySec[0] }}</div>
          <div class="ct-digit">{{ displaySec[1] }}</div>
          <span class="ct-sublabel">giây</span>
        </div>
      </div>
    </div>

    <div v-if="state !== 'normal'" class="ct-alert" aria-live="polite">
      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" aria-hidden="true">
        <path d="M12 2L2 20h20L12 2Z" stroke="currentColor" stroke-width="2" stroke-linejoin="round"/>
        <path d="M12 9v5M12 17v.5" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
      </svg>
      <span>{{ state === 'critical' ? 'Hết giờ ngay!' : 'Sắp hết thời gian!' }}</span>
    </div>
  </div>

  <!-- Modal khi hết giờ -->
  <Teleport to="body">
    <div v-if="showModal" class="ct-modal-overlay" role="dialog" aria-modal="true" aria-labelledby="ct-modal-title">
      <div class="ct-modal" @click.stop>
        <div class="ct-modal__icon" aria-hidden="true">
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none">
            <circle cx="12" cy="12" r="9" stroke="currentColor" stroke-width="2"/>
            <path d="M12 7v5l3.5 2" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </div>
        <p id="ct-modal-title" class="ct-modal__title">Phiên đặt sân đã hết hạn</p>
        <p class="ct-modal__desc">
          Khung giờ bạn chọn đã được <strong>giải phóng</strong> sau 3 phút không hoàn tất thanh toán.<br /><br />
          Bạn có thể quay lại chọn sân và đặt lại khung giờ phù hợp.
        </p>
        <div class="ct-modal__actions">
          <button class="btn btn-danger" @click="handleRedirect">
            Quay về chọn sân
          </button>
          <button v-if="allowDismiss" class="btn btn-outline" @click="showModal = false">
            Xem lại thông tin
          </button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'

const props = defineProps({
  expireAt: { type: Number, required: true }, // Mốc Epoch Timestamp (ms) từ Server
  warningAt: { type: Number, default: 90 },
  criticalAt: { type: Number, default: 45 },
  allowDismiss: { type: Boolean, default: false }
})

const emit = defineEmits(['warning', 'expired', 'redirect'])

const remaining = ref(0)
const showModal = ref(false)
let timerId = null
let warnedOnce = false

const updateTimer = () => {
  const now = Date.now()
  const diff = Math.floor((props.expireAt - now) / 1000)

  if (diff <= 0) {
    remaining.value = 0
    clearInterval(timerId)
    if (!showModal.value) {
      showModal.value = true
      emit('expired')
    }
  } else {
    remaining.value = diff
    if (!warnedOnce && remaining.value <= props.warningAt) {
      warnedOnce = true
      emit('warning', remaining.value)
    }
  }
}

function handleRedirect() {
  showModal.value = false
  emit('redirect')
}

onMounted(() => {
  updateTimer()
  timerId = setInterval(updateTimer, 1000)
})

onUnmounted(() => {
  if (timerId) clearInterval(timerId)
})

watch(() => props.expireAt, () => {
  warnedOnce = false
  showModal.value = false
  updateTimer()
})

const displayMin = computed(() => {
  const m = Math.floor(remaining.value / 60)
  return String(m).padStart(2, '0')
})

const displaySec = computed(() => {
  const s = remaining.value % 60
  return String(s).padStart(2, '0')
})

const state = computed(() => {
  if (remaining.value <= props.criticalAt) return 'critical'
  if (remaining.value <= props.warningAt) return 'warning'
  return 'normal'
})

const stateClass = computed(() => ({
  'ct-wrap--warning': state.value === 'warning',
  'ct-wrap--critical': state.value === 'critical'
}))

defineExpose({ remaining, state })
</script>

<style scoped>
.ct-wrap {
  display: inline-flex;
  align-items: center;
  gap: 16px;
  background: #ffffff;
  border: 1.5px solid #dcfce7;
  border-radius: 16px;
  padding: 12px 20px;
  box-shadow: 0 4px 20px rgba(34, 197, 94, 0.08);
  position: relative;
  user-select: none;
  transition: all 0.3s ease;
}
.ct-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  color: #16a34a;
  transition: all 0.3s ease;
}
.ct-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.ct-label {
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.05em;
  text-transform: uppercase;
  color: #6b7280;
  transition: color 0.3s ease;
}
.ct-digits {
  display: flex;
  align-items: center;
  gap: 6px;
}
.ct-digit-group {
  display: flex;
  align-items: center;
  gap: 3px;
  position: relative;
}
.ct-digit {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 26px;
  height: 34px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-family: 'Oswald', 'Be Vietnam Pro', sans-serif;
  font-size: 20px;
  font-weight: 700;
  color: #0f172a;
  box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.03);
  transition: all 0.3s ease;
}
.ct-sublabel {
  font-size: 10px;
  font-weight: 600;
  color: #94a3b8;
  margin-left: 3px;
  align-self: flex-end;
  padding-bottom: 3px;
}
.ct-colon {
  font-size: 20px;
  font-weight: 700;
  color: #94a3b8;
  margin: 0 2px;
  align-self: flex-start;
  line-height: 32px;
  animation: colonBlink 1s step-end infinite;
}
@keyframes colonBlink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.2; }
}
.ct-wrap--warning {
  background: #fffbeb;
  border-color: #fde68a;
  box-shadow: 0 4px 20px rgba(245, 158, 11, 0.12);
}
.ct-wrap--warning .ct-icon {
  background: #fef3c7;
  border-color: #fde68a;
  color: #d97706;
}
.ct-wrap--warning .ct-label { color: #b45309; }
.ct-wrap--warning .ct-digit {
  background: #ffffff;
  border-color: #fde68a;
  color: #b45309;
}
.ct-wrap--critical {
  background: #fef2f2;
  border-color: #fecaca;
  box-shadow: 0 4px 20px rgba(239, 68, 68, 0.15);
  animation: criticalPulse 1.4s ease-in-out infinite;
}
.ct-wrap--critical .ct-icon {
  background: #fee2e2;
  border-color: #fecaca;
  color: #dc2626;
}
.ct-wrap--critical .ct-label { color: #b91c1c; }
.ct-wrap--critical .ct-digit {
  background: #ffffff;
  border-color: #fca5a5;
  color: #dc2626;
}
@keyframes criticalPulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.02); }
}
.ct-alert {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  font-weight: 700;
  color: #d97706;
  padding-left: 8px;
  border-left: 1px solid rgba(0, 0, 0, 0.08);
}
.ct-wrap--critical .ct-alert { color: #dc2626; }
</style>

<style>
.ct-modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.55);
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  padding: 16px;
  animation: overlayFadeIn 0.2s ease;
}
@keyframes overlayFadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}
.ct-modal {
  background: #ffffff;
  border-radius: 20px;
  padding: 32px 28px;
  width: min(400px, 100%);
  text-align: center;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
  position: relative;
  overflow: hidden;
  animation: modalSlideUp 0.25s cubic-bezier(0.34, 1.56, 0.64, 1);
}
@keyframes modalSlideUp {
  from { transform: translateY(20px) scale(0.96); opacity: 0; }
  to { transform: translateY(0) scale(1); opacity: 1; }
}
.ct-modal::before {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0;
  height: 4px;
  background: #ef4444;
}
.ct-modal__icon {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  background: #fef2f2;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  color: #ef4444;
}
.ct-modal__title {
  font-size: 20px;
  font-weight: 800;
  color: #0f172a;
  margin-bottom: 8px;
}
.ct-modal__desc {
  font-size: 14px;
  line-height: 1.6;
  color: #475569;
}
.ct-modal__desc strong {
  color: #0f172a;
}
.ct-modal__actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 24px;
}
.ct-modal .btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  padding: 12px 20px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 700;
  border: 1.5px solid transparent;
  cursor: pointer;
  transition: all 0.2s ease;
}
.ct-modal .btn-danger {
  background: #ef4444;
  color: #ffffff;
  box-shadow: 0 4px 14px rgba(239, 68, 68, 0.3);
}
.ct-modal .btn-danger:hover {
  background: #dc2626;
  transform: translateY(-1px);
}
.ct-modal .btn-outline {
  background: #ffffff;
  border-color: #e2e8f0;
  color: #475569;
}
.ct-modal .btn-outline:hover {
  background: #f8fafc;
  color: #0f172a;
}
</style>