Dưới đây là thiết kế giao diện trang **"Thông báo cá nhân" (`UserNotifications.vue`)** dành cho hệ thống của bạn.

Trang này được thiết kế theo phong cách tối giản, hiện đại và chuyên nghiệp nhất với các tính năng thực tế:

1. **Bộ lọc thông minh:** Cho phép lọc nhanh theo trạng thái "Tất cả", "Chưa đọc", hoặc phân loại theo "Hệ thống", "Đơn đặt sân", "Khuyến mãi".
2. **Hành động nhanh:** Nút "Đánh dấu tất cả là đã đọc" để tối ưu hóa trải nghiệm người dùng.
3. **Màu sắc đồng bộ:** Tiếp tục sử dụng tone màu xanh lá chủ đạo thích hợp cho dự án của bạn (kết hợp với các hiệu ứng chuyển màu mượt mà khi hover).

```html
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

    <div class="noti-list" v-if="filteredNotifications.length > 0">
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
import { ref, computed } from 'vue'

// Định nghĩa các loại bộ lọc
const currentFilter = ref('all')
const filterTabs = [
  { label: 'Tất cả', value: 'all' },
  { label: 'Chưa đọc', value: 'unread' },
  { label: 'Đơn đặt sân', value: 'booking' },
  { label: 'Hệ thống', value: 'system' }
]

// Dữ liệu mẫu danh sách thông báo thực tế của hệ thống đặt sân
const notifications = ref([
  {
    id: 1,
    title: 'Đặt sân thành công 🎉',
    message: 'Yêu cầu đặt Sân 5A vào lúc 17:00 - 18:30 hôm nay đã được phê duyệt. Vui lòng đến đúng giờ.',
    type: 'booking',
    isRead: false,
    createdAt: '2026-06-26T00:30:00Z'
  },
  {
    id: 2,
    title: 'Xác nhận hoàn tiền 💳',
    message: 'Số tiền 300,000đ cho đơn đặt sân hủy ngày 24/06 đã được hoàn về ví tài khoản của bạn.',
    type: 'booking',
    isRead: false,
    createdAt: '2026-06-25T15:10:00Z'
  },
  {
    id: 3,
    title: 'Bảo trì hệ thống định kỳ 🛠️',
    message: 'Hệ thống sẽ tiến hành bảo trì nâng cấp từ 01:00 đến 03:00 ngày 28/06. Giao dịch đặt sân có thể bị gián đoạn trong thời gian này.',
    type: 'system',
    isRead: true,
    createdAt: '2026-06-25T08:00:00Z'
  },
  {
    id: 4,
    title: 'Cập nhật thông tin tài khoản thành công',
    message: 'Mật khẩu tài khoản của bạn đã thay đổi thành công vào lúc 19:30 ngày hôm qua.',
    type: 'system',
    isRead: true,
    createdAt: '2026-06-25T12:30:00Z'
  }
])

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

// Đánh dấu 1 thông báo cụ thể là đã đọc
function markAsRead(noti) {
  if (!noti.isRead) {
    noti.isRead = true
  }
}

// Đánh dấu tất cả là đã đọc
function markAllAsRead() {
  notifications.value.forEach(n => n.isRead = true)
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
  const diffHours = Math.floor(diffMins / 600)
  
  if (diffMins < 60) {
    return diffMins <= 1 ? 'Vừa xong' : `${diffMins} phút trước`
  } else if (diffHours < 24) {
    return `${diffHours} giờ trước`
  } else {
    return past.toLocaleDateString('vi-VN', { day: 'numeric', month: 'numeric' })
  }
}
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
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

@media (max-width: 576px) {
  .noti-header {
    flex-direction: column;
    gap: 12px;
  }
}

.noti-header__title {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 6px 0;
}

.noti-header__desc {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

.btn-mark-all {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: transparent;
  border: 1px solid #cbd5e1;
  padding: 8px 14px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
  color: #475569;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-mark-all:hover {
  background: #f8fafc;
  border-color: #94a3b8;
  color: #1e293b;
}

/* ===== THANH BỘ LỌC TABS ===== */
.noti-filters {
  display: flex;
  gap: 8px;
  border-bottom: 1px solid #e2e8f0;
  padding-bottom: 12px;
  margin-bottom: 16px;
  overflow-x: auto;
}

.filter-tab {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: transparent;
  border: none;
  padding: 8px 16px;
  font-size: 14px;
  font-weight: 500;
  color: #64748b;
  cursor: pointer;
  border-radius: 20px;
  white-space: nowrap;
  transition: all 0.2s;
}

.filter-tab:hover {
  background: #f1f5f9;
  color: #1e293b;
}

.filter-tab.active {
  background: rgba(40, 167, 69, 0.1);
  color: #28a745;
  font-weight: 600;
}

.filter-badge {
  background: #ef4444;
  color: #ffffff;
  font-size: 11px;
  font-weight: 600;
  padding: 1px 6px;
  border-radius: 10px;
}

/* ===== DANH SÁCH THÔNG BÁO ===== */
.noti-list {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.02);
  overflow: hidden;
}

.noti-item {
  display: flex;
  gap: 16px;
  padding: 18px 20px;
  border-bottom: 1px solid #f1f5f9;
  cursor: pointer;
  position: relative;
  transition: background-color 0.2s;
}

.noti-item:last-child {
  border-bottom: none;
}

.noti-item:hover {
  background-color: #f8fafc;
}

/* Kiểu dáng riêng cho thông báo chưa đọc */
.noti-item.unread {
  background-color: rgba(40, 167, 69, 0.02);
}
.noti-item.unread:hover {
  background-color: rgba(40, 167, 69, 0.05);
}

/* Wrapper cho các Icon */
.noti-item__icon-wrapper {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

/* Màu sắc của từng loại loại icon */
.noti-item__icon-wrapper.booking {
  background-color: rgba(40, 167, 69, 0.1);
  color: #28a745;
}

.noti-item__icon-wrapper.system {
  background-color: rgba(14, 165, 233, 0.1);
  color: #0ea5e9;
}

/* Nội dung text */
.noti-item__content {
  flex-grow: 1;
}

.noti-item__title-row {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 4px;
}

.noti-item__title {
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}
.noti-item.unread .noti-item__title {
  font-weight: 700;
  color: #0f172a;
}

.noti-item__time {
  font-size: 12px;
  color: #94a3b8;
}

.noti-item__message {
  font-size: 14px;
  color: #475569;
  margin: 0;
  line-height: 1.45;
}
.noti-item.unread .noti-item__message {
  color: #334155;
}

/* Chấm xanh trạng thái chưa đọc */
.noti-item__status {
  display: flex;
  align-items: center;
  justify-content: center;
  padding-left: 8px;
}

.unread-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #28a745;
}

/* ===== GIAO DIỆN TRỐNG KHI KHÔNG CÓ THÔNG BÁO ===== */
.noti-empty {
  text-align: center;
  padding: 60px 20px;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
}

.empty-icon-box {
  width: 80px;
  height: 80px;
  background: #f8fafc;
  color: #94a3b8;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px auto;
}

.empty-title {
  font-size: 16px;
  font-weight: 700;
  color: #334155;
  margin: 0 0 6px 0;
}

.empty-desc {
  font-size: 14px;
  color: #64748b;
  max-width: 320px;
  margin: 0 auto;
}
</style>

```