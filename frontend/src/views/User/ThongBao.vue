<template>
  <div class="notifications-container">
    <div class="noti-header">
      <div class="noti-header__left">
        <h1 class="noti-header__title">Thông báo của tôi</h1>
        <p class="noti-header__desc">Cập nhật những thông tin mới nhất về lịch đặt sân, ưu đãi và tài khoản.</p>
      </div>
      <div class="noti-header__right" v-if="unreadCount > 0">
        <button class="btn-mark-all" @click="markAllAsRead">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <polyline points="20 6 9 17 4 12"></polyline>
          </svg>
          Đánh dấu tất cả đã đọc
        </button>
      </div>
    </div>

    <div class="noti-filters">
      <button
        v-for="tab in filterTabs"
        :key="tab.value"
        class="filter-tab"
        :class="{ active: currentFilter === tab.value }"
        @click="currentFilter = tab.value"
      >
        {{ tab.label }}
        <span class="filter-badge" v-if="tab.value === 'unread' && unreadCount > 0">{{ unreadCount }}</span>
      </button>
    </div>

    <!-- Đang tải -->
    <div class="noti-loading" v-if="dangTai">
      <div class="spinner-nho"></div>
      <p>Đang tải thông báo...</p>
    </div>

    <div class="noti-list" v-else-if="filteredNotifications.length > 0">
      <div
        v-for="noti in filteredNotifications"
        :key="noti.id"
        class="noti-item"
        :class="{ unread: !noti.isRead }"
        @click="markAsRead(noti)"
      >
        <div class="noti-item__icon-wrapper" :class="noti.type">
          <component :is="getIcon(noti.type)" />
        </div>

        <div class="noti-item__content">
          <div class="noti-item__title-row">
            <h3 class="noti-item__title">{{ noti.title }}</h3>
            <span class="noti-item__time">{{ timeAgo(noti.createdAt) }}</span>
          </div>
          <p class="noti-item__message">{{ noti.message }}</p>
        </div>

        <div class="noti-item__status" v-if="!noti.isRead">
          <span class="unread-dot"></span>
        </div>
      </div>
    </div>

    <div class="noti-empty" v-else>
      <div class="empty-icon-box">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
          <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
          <path d="M13.73 21a2 2 0 0 1-3.46 0"></path>
        </svg>
      </div>
      <h3 class="empty-title">Không có thông báo nào</h3>
      <p class="empty-desc">Hộp thư của bạn hiện đang trống. Các thông báo mới sẽ xuất hiện tại đây.</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const API = 'http://localhost:8080/api'

// Định nghĩa các loại bộ lọc
const currentFilter = ref('all')
const filterTabs = [
  { label: 'Tất cả', value: 'all' },
  { label: 'Chưa đọc', value: 'unread' },
  { label: 'Đơn đặt sân', value: 'booking' },
  { label: 'Hệ thống', value: 'system' }
]

const dangTai = ref(true)
const notifications = ref([])

// Tải danh sách thông báo thật từ backend
async function taiThongBao() {
  dangTai.value = true
  const token = localStorage.getItem('token')
  try {
    const res = await fetch(`${API}/thong-bao`, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    if (!res.ok) throw new Error('Không tải được thông báo')
    const data = await res.json()

    // Map từ tên field tiếng Việt của backend (tieuDe, noiDung, loai, daDoc, ngayTao)
    // sang đúng tên field mà giao diện đang dùng (title, message, type, isRead, createdAt)
    notifications.value = data.map(tb => ({
      id: tb.id,
      title: tb.tieuDe,
      message: tb.noiDung,
      type: tb.loai,
      isRead: tb.daDoc,
      createdAt: tb.ngayTao
    }))
  } catch (e) {
    notifications.value = []
  } finally {
    dangTai.value = false
  }
}

// Tính toán số lượng thông báo chưa đọc
const unreadCount = computed(() => {
  return notifications.value.filter(n => !n.isRead).length
})

// Bộ lọc danh sách dựa theo tab đang chọn
const filteredNotifications = computed(() => {
  if (currentFilter.value === 'all') return notifications.value
  if (currentFilter.value === 'unread') return notifications.value.filter(n => !n.isRead)
  return notifications.value.filter(n => n.type === currentFilter.value)
})

// Đánh dấu 1 thông báo cụ thể là đã đọc (cập nhật giao diện ngay, đồng bộ backend ngầm)
async function markAsRead(noti) {
  if (noti.isRead) return
  noti.isRead = true

  const token = localStorage.getItem('token')
  try {
    await fetch(`${API}/thong-bao/${noti.id}/da-doc`, {
      method: 'PUT',
      headers: { 'Authorization': `Bearer ${token}` }
    })
  } catch {
    // Nếu lỗi mạng tạm thời, lần tải lại trang tiếp theo sẽ đồng bộ đúng trạng thái thật
  }
}

// Đánh dấu tất cả là đã đọc
async function markAllAsRead() {
  notifications.value.forEach(n => n.isRead = true)

  const token = localStorage.getItem('token')
  try {
    await fetch(`${API}/thong-bao/da-doc-tat-ca`, {
      method: 'PUT',
      headers: { 'Authorization': `Bearer ${token}` }
    })
  } catch {
    // im lặng bỏ qua, lần tải lại trang tiếp theo sẽ đồng bộ đúng trạng thái thật
  }
}

// Hàm render SVG components inline cho các icon (Booking, System)
const getIcon = (type) => {
  if (type === 'booking') {
    return {
      template: `
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
          <line x1="16" y1="2" x2="16" y2="6"></line>
          <line x1="8" y1="2" x2="8" y2="6"></line>
          <line x1="3" y1="10" x2="21" y2="10"></line>
        </svg>`
    }
  }
  return {
    template: `
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <circle cx="12" cy="12" r="10"></circle>
        <line x1="12" y1="16" x2="12" y2="12"></line>
        <line x1="12" y1="8" x2="12.01" y2="8"></line>
      </svg>`
  }
}

// Hàm tính toán khoảng thời gian tương đối (Vừa xong, vài giờ trước...)
function timeAgo(dateStr) {
  const now = new Date()
  const past = new Date(dateStr)
  const diffMs = now - past

  const diffMins = Math.floor(diffMs / 60000)
  const diffHours = Math.floor(diffMins / 60)

  if (diffMins < 60) {
    return diffMins <= 1 ? 'Vừa xong' : `${diffMins} phút trước`
  } else if (diffHours < 24) {
    return `${diffHours} giờ trước`
  } else {
    return past.toLocaleDateString('vi-VN', { day: 'numeric', month: 'numeric' })
  }
}

onMounted(taiThongBao)
</script>

<style scoped>
/* ===== TOÀN BỘ KHUNG ===== */
.notifications-container {
  max-width: 850px;
  margin: 0 auto;
  padding: 12px 0;
  font-family: system-ui, -apple-system, sans-serif;
}

/* ===== HEADER ===== */
.noti-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}
.noti-header__title {
  font-size: 24px;
  font-weight: 700;
  color: var(--navy-900, #0a2540);
}
.noti-header__desc {
  font-size: 14px;
  color: var(--gray-500, #667085);
  margin-top: 4px;
}
.btn-mark-all {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  border-radius: 8px;
  border: 1.5px solid var(--gray-200, #e2e8e5);
  background: var(--white, #fff);
  font-size: 13px;
  font-weight: 600;
  color: var(--green-700, #15803d);
  cursor: pointer;
  transition: background 0.15s, border-color 0.15s;
  white-space: nowrap;
}
.btn-mark-all:hover {
  background: var(--green-50, #f0fdf4);
  border-color: var(--green-600, #16a34a);
}

/* ===== FILTERS ===== */
.noti-filters {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}
.filter-tab {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 999px;
  border: 1.5px solid var(--gray-200, #e2e8e5);
  background: var(--white, #fff);
  font-size: 13.5px;
  font-weight: 500;
  color: var(--gray-600, #475569);
  cursor: pointer;
  transition: background 0.15s, border-color 0.15s, color 0.15s;
}
.filter-tab:hover {
  background: var(--gray-50, #f8fafc);
}
.filter-tab.active {
  background: var(--green-600, #16a34a);
  border-color: var(--green-600, #16a34a);
  color: white;
}
.filter-badge {
  background: #e53e3e;
  color: white;
  font-size: 10.5px;
  font-weight: 700;
  padding: 1px 6px;
  border-radius: 999px;
}

/* ===== LOADING ===== */
.noti-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 60px 0;
  color: var(--gray-500, #667085);
}
.spinner-nho {
  width: 28px;
  height: 28px;
  border: 3px solid var(--gray-100, #f1f5f4);
  border-top-color: var(--green-600, #16a34a);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
@keyframes spin {
  to { transform: rotate(360deg); }
}

/* ===== LIST ===== */
.noti-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.noti-item {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  padding: 16px;
  border-radius: 12px;
  border: 1.5px solid var(--gray-100, #f1f5f4);
  background: var(--white, #fff);
  cursor: pointer;
  transition: background 0.15s, border-color 0.15s;
}
.noti-item:hover {
  background: var(--gray-50, #f8fafc);
}
.noti-item.unread {
  background: var(--green-50, #f0fdf4);
  border-color: var(--green-100, #dcfce7);
}
.noti-item__icon-wrapper {
  width: 38px;
  height: 38px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  color: var(--green-700, #15803d);
  background: var(--green-100, #dcfce7);
}
.noti-item__icon-wrapper.system {
  color: #0369a1;
  background: #e0f2fe;
}
.noti-item__content {
  flex: 1;
  min-width: 0;
}
.noti-item__title-row {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 10px;
}
.noti-item__title {
  font-size: 14.5px;
  font-weight: 700;
  color: var(--navy-900, #0a2540);
}
.noti-item__time {
  font-size: 12px;
  color: var(--gray-500, #667085);
  white-space: nowrap;
  flex-shrink: 0;
}
.noti-item__message {
  font-size: 13.5px;
  color: var(--gray-600, #475569);
  margin-top: 4px;
  line-height: 1.5;
}
.noti-item__status {
  flex-shrink: 0;
  padding-top: 4px;
}
.unread-dot {
  display: block;
  width: 9px;
  height: 9px;
  border-radius: 50%;
  background: var(--green-600, #16a34a);
}

/* ===== EMPTY ===== */
.noti-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 60px 20px;
  color: var(--gray-500, #667085);
}
.empty-icon-box {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: var(--gray-50, #f8fafc);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--gray-400, #94a3b8);
  margin-bottom: 16px;
}
.empty-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--navy-900, #0a2540);
}
.empty-desc {
  font-size: 13.5px;
  margin-top: 6px;
  max-width: 320px;
}

@media (max-width: 600px) {
  .noti-header { flex-direction: column; }
}
</style>