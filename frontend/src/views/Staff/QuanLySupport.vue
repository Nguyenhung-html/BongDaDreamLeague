<template>
  <div class="support">
    <!-- Nền ambient đồng bộ Dashboard -->
    <div class="support__ambient" aria-hidden="true">
      <div class="floodlight floodlight--l"></div>
      <div class="floodlight floodlight--r"></div>
      <div class="motes"><span v-for="n in 10" :key="'m' + n" class="mote" :style="{ '--i': n }"></span></div>
      <div class="falling"><span v-for="n in 18" :key="'f' + n" class="falling__item" :style="{ '--i': n }"></span></div>
    </div>

    <!-- Header -->
    <div class="support__header reveal">
      <span class="eyebrow">Khách hàng · Hỗ trợ</span>
      <h1 class="support__title">Hỗ trợ khách hàng</h1>
      <p class="support__desc">Theo dõi và phản hồi các ticket hỗ trợ theo thời gian thực.</p>
    </div>

    <p v-if="loi" class="support__error reveal">⚠️ {{ loi }}</p>

    <div class="support-workspace reveal" style="--d: .1s">
      <!-- SIDEBAR BÊN TRÁI: DANH SÁCH TICKET -->
      <aside class="ticket-sidebar">
        <div class="sidebar-header">
          <h3>Danh sách Ticket</h3>
          <span class="ticket-count">{{ tickets.length }}</span>
        </div>

        <div class="ticket-list">
          <div v-if="dangTaiTicket" class="ticket-loading">
            <div class="spinner"></div>
            <p>Đang tải ticket...</p>
          </div>

          <div v-else-if="tickets.length === 0" class="ticket-empty">
            <p>Chưa có ticket nào.</p>
          </div>

          <div
            v-for="t in tickets"
            :key="t.id"
            class="ticket-card"
            :class="{ active: selectedTicket?.id === t.id }"
            @click="selectTicket(t)"
          >
            <div class="ticket-card-top">
              <button
                type="button"
                class="mini-avatar"
                :title="`Xem thông tin khách hàng: ${tenKhachHangThat(t) || 'chưa cập nhật tên'}`"
                @click.stop="openCustomerInfo(t)"
              >
                {{ getInitials(tenKhachHangThat(t)) }}
              </button>
              <div class="ticket-card-top__text">
                <span class="ticket-code">#{{ t.maTicket || (t.id ? t.id.slice(0, 8) : '') }}</span>
                <span class="status-badge" :class="statusClass(t.trangThai)">{{ statusLabel(t.trangThai) }}</span>
              </div>
            </div>

            <div class="ticket-card-body">
              <h4 class="customer-name">{{ tenKhachHangThat(t) || 'Khách hàng chưa cập nhật tên' }}</h4>
              <p class="customer-phone">{{ t.soDienThoai || 'Chưa có số điện thoại' }}</p>
            </div>
          </div>
        </div>
      </aside>

      <!-- KHUNG CHAT BÊN PHẢI -->
      <main class="chat-main" v-if="selectedTicket">
        <header class="chat-header">
          <div class="user-info">
            <button
              type="button"
              class="avatar-circle"
              title="Xem thông tin khách hàng"
              @click="openCustomerInfo(selectedTicket)"
            >
              {{ getInitials(tenKhachHangThat(selectedTicket)) }}
            </button>
            <div>
              <h3 class="header-title">{{ tenKhachHangThat(selectedTicket) || 'Khách hàng chưa cập nhật tên' }}</h3>
              <p class="header-sub">
                Mã: <strong>#{{ selectedTicket.maTicket || (selectedTicket.id ? selectedTicket.id.slice(0, 8) : '') }}</strong>
                · SĐT: <strong>{{ selectedTicket.soDienThoai || 'Chưa cập nhật' }}</strong>
              </p>
            </div>
          </div>

          <div class="chat-actions">
            <button
              v-if="selectedTicket.trangThai === 'CHO_XU_LY'"
              class="btn btn-primary"
              @click="tiepNhan"
            >
              Tiếp nhận Ticket
            </button>

            <button
              v-if="selectedTicket.trangThai !== 'DA_DONG'"
              class="btn btn-danger"
              @click="dongTicket"
            >
              Đóng Ticket
            </button>
          </div>
        </header>

        <!-- NỘI DUNG TIN NHẮN -->
        <div class="chat-messages" ref="adminMsgContainer">
          <div v-if="dangTaiTinNhan" class="ticket-loading">
            <div class="spinner"></div>
            <p>Đang tải tin nhắn...</p>
          </div>

          <div v-else-if="messages.length === 0" class="messages-empty">
            <p>Chưa có tin nhắn nào trong ticket này.</p>
          </div>

          <div
            v-for="msg in messages"
            :key="msg.id"
            class="message-row"
            :class="msg.loaiNguoiGui === 'KHACH_HANG' ? 'msg-left' : 'msg-right'"
          >
            <div class="msg-avatar" :class="msg.loaiNguoiGui?.toLowerCase()">
              {{ getInitials(msg.tenNguoiGui || (msg.loaiNguoiGui === 'KHACH_HANG' ? tenKhachHangThat(selectedTicket) : msg.loaiNguoiGui)) }}
            </div>

            <div class="msg-content">
              <div class="msg-sender-info">
                <span class="sender-name">
                  {{ msg.tenNguoiGui || (msg.loaiNguoiGui === 'KHACH_HANG' ? (tenKhachHangThat(selectedTicket) || 'Khách hàng') : 'Hỗ trợ viên') }}
                </span>
                <span class="role-badge" :class="msg.loaiNguoiGui?.toLowerCase()">
                  {{ msg.loaiNguoiGui === 'KHACH_HANG' ? 'Khách' : msg.loaiNguoiGui === 'ADMIN' ? 'Admin' : 'Staff' }}
                </span>
              </div>

              <div class="msg-bubble" :class="msg.loaiNguoiGui?.toLowerCase()">
                {{ msg.noiDung }}
              </div>

              <div class="msg-meta">
                <span class="msg-time">{{ formatTime(msg.thoiGian) }}</span>
                <span class="msg-status" v-if="msg.loaiNguoiGui !== 'KHACH_HANG'">
                  {{ msg.daDoc ? '✓✓ Đã đọc' : '✓ Đã gửi' }}
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- KHUNG NHẬP TIN NHẮN -->
        <footer class="chat-footer" v-if="selectedTicket.trangThai !== 'DA_DONG'">
          <input
            v-model="inputText"
            type="text"
            placeholder="Nhập câu trả lời cho khách hàng..."
            @keyup.enter="sendAdminMsg"
          />
          <button class="btn-send" :disabled="!inputText.trim()" @click="sendAdminMsg">
            <span>Gửi</span>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="22" y1="2" x2="11" y2="13"></line><polygon points="22 2 15 22 11 13 2 9 22 2"></polygon></svg>
          </button>
        </footer>
        <div v-else class="chat-closed-notice">Ticket này đã được đóng.</div>
      </main>

      <!-- PLACEHOLDER KHI CHƯA CHỌN TICKET -->
      <div class="empty-state" v-else>
        <div class="empty-icon">
          <svg width="30" height="30" viewBox="0 0 24 24" fill="none"><path d="M21 11.5a8.4 8.4 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.4 8.4 0 0 1-3.8-.9L3 21l1.9-5.7a8.4 8.4 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.4 8.4 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5Z" stroke="currentColor" stroke-width="1.6" stroke-linejoin="round"/></svg>
        </div>
        <h3>Chọn một ticket để bắt đầu hỗ trợ</h3>
        <p>Danh sách ticket nằm ở cột bên trái</p>
      </div>
    </div>

    <!-- MODAL THÔNG TIN KHÁCH HÀNG -->
    <div v-if="showCustomerModal" class="modal-overlay" @click.self="closeCustomerInfo">
      <div class="modal-box">
        <div class="modal-header">
          <h3>Thông tin khách hàng</h3>
          <button class="btn-close" @click="closeCustomerInfo">&times;</button>
        </div>

        <div class="modal-body">
          <div v-if="dangTaiKhachHang" class="ticket-loading">
            <div class="spinner"></div>
            <p>Đang tải thông tin...</p>
          </div>

          <template v-else>
            <div class="customer-profile">
              <div class="customer-profile__avatar">{{ getInitials(tenKhachHangThat(customerInfo)) }}</div>
              <div>
                <p class="customer-profile__name">{{ tenKhachHangThat(customerInfo) || 'Khách hàng chưa cập nhật tên' }}</p>
                <span class="status-badge" :class="statusClass(customerInfo?.trangThai)">{{ statusLabel(customerInfo?.trangThai) }}</span>
              </div>
            </div>

            <dl class="info-grid">
              <div class="info-row">
                <dt>Số điện thoại</dt>
                <dd>{{ customerInfo?.soDienThoai || 'Chưa cập nhật' }}</dd>
              </div>
              <div class="info-row" v-if="customerInfo?.email">
                <dt>Email</dt>
                <dd>{{ customerInfo.email }}</dd>
              </div>
              <div class="info-row" v-if="customerInfo?.diaChi">
                <dt>Địa chỉ</dt>
                <dd>{{ customerInfo.diaChi }}</dd>
              </div>
              <div class="info-row">
                <dt>Mã ticket</dt>
                <dd>#{{ customerInfo?.maTicket || (customerInfo?.id ? customerInfo.id.slice(0, 8) : '—') }}</dd>
              </div>
              <div class="info-row" v-if="customerInfo?.ngayTao">
                <dt>Ngày tạo ticket</dt>
                <dd>{{ formatDate(customerInfo.ngayTao) }}</dd>
              </div>
              <div class="info-row" v-if="customerInfo?.tongSoTicket !== undefined">
                <dt>Tổng số ticket đã gửi</dt>
                <dd>{{ customerInfo.tongSoTicket }}</dd>
              </div>
            </dl>
          </template>
        </div>

        <div class="modal-footer">
          <button type="button" class="btn-cancel" @click="closeCustomerInfo">Đóng</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { Client } from '@stomp/stompjs'

const route = useRoute()
const tickets = ref([])
const selectedTicket = ref(null)
const messages = ref([])
const inputText = ref('')
const adminMsgContainer = ref(null)
const loi = ref('')

const dangTaiTicket = ref(false)
const dangTaiTinNhan = ref(false)
const dangTaiKhachHang = ref(false)

const showCustomerModal = ref(false)
const customerInfo = ref(null)

let stompClient = null

// Lấy Token và User thật từ Storage — không dùng ID giả định
const token = localStorage.getItem('token') || ''
const currentUser = JSON.parse(localStorage.getItem('user') || '{}')
const currentUserId = currentUser.id || currentUser.userId || localStorage.getItem('userId') || ''

// Tự động xác định Role (ADMIN / STAFF) dựa trên route thật
const currentRole = route.meta?.roles?.includes('Admin') ? 'ADMIN' : 'STAFF'

function statusLabel(trangThai) {
  if (trangThai === 'DANG_XU_LY') return 'Đang xử lý'
  if (trangThai === 'DA_DONG') return 'Đã đóng'
  if (trangThai === 'CHO_XU_LY') return 'Mới'
  if (trangThai === 'CHUA_TIEP_NHAN') return 'Chưa tiếp nhận'
  return trangThai || 'Không rõ'
}

function statusClass(trangThai) {
  return (trangThai || '').toLowerCase()
}

// Dò nhiều tên field khả dĩ cho họ tên khách hàng, tuỳ backend đặt tên field khác nhau.
// Nếu API của bạn dùng field khác, thêm vào danh sách bên dưới.
function tenKhachHangThat(obj) {
  if (!obj) return ''
  const value =
    obj.tenKhachHang || obj.hoTen || obj.hoVaTen || obj.fullName || obj.fullname ||
    obj.hoTenKhachHang || obj.name || obj.tenNguoiDung || ''
  // Nếu backend trả về placeholder mặc định (chưa có tên thật), không coi đó là tên thật
  const placeholderMacDinh = ['khách hàng', 'khach hang', 'customer', 'khách']
  if (value && placeholderMacDinh.includes(value.trim().toLowerCase())) return ''
  return value
}

function getInitials(name) {
  if (!name) return 'K'
  const parts = name.trim().split(/\s+/)
  return parts[parts.length - 1].charAt(0).toUpperCase()
}

function formatDate(dateStr) {
  if (!dateStr) return '—'
  const d = new Date(dateStr)
  if (isNaN(d.getTime())) return '—'
  return d.toLocaleDateString('vi-VN')
}

// 1. Fetch danh sách Ticket (dữ liệu thật từ API)
const fetchTickets = async () => {
  dangTaiTicket.value = true
  loi.value = ''
  try {
    const res = await fetch('/api/support/admin/tickets', {
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`
      }
    })
    if (res.ok) {
      tickets.value = await res.json()
    } else {
      loi.value = 'Không tải được danh sách ticket'
    }
  } catch (err) {
    console.error('Lỗi lấy danh sách Ticket:', err)
    loi.value = 'Không tải được danh sách ticket'
  } finally {
    dangTaiTicket.value = false
  }
}

// 2. Chọn Ticket & Tải lịch sử tin nhắn thật
const selectTicket = async (t) => {
  if (!t) return
  selectedTicket.value = t
  dangTaiTinNhan.value = true
  messages.value = []

  try {
    const res = await fetch(`/api/support/ticket/${t.id}/messages`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    if (res.ok) {
      messages.value = await res.json()
    }

    if (currentUserId) {
      await fetch(`/api/support/ticket/${t.id}/read?userId=${currentUserId}`, {
        method: 'PUT',
        headers: { Authorization: `Bearer ${token}` }
      })
    }

    connectWebSocket(t.id)
    scrollToBottom()
  } catch (err) {
    console.error('Lỗi lấy tin nhắn:', err)
  } finally {
    dangTaiTinNhan.value = false
  }
}

// 3. Kết nối WebSocket realtime (@stomp/stompjs)
const connectWebSocket = (ticketId) => {
  if (stompClient) {
    stompClient.deactivate()
  }

  stompClient = new Client({
    brokerURL: 'ws://localhost:8080/ws-support/websocket',
    debug: () => {},
    reconnectDelay: 5000,
    onConnect: () => {
      stompClient.subscribe(`/topic/ticket/${ticketId}`, (message) => {
        if (message.body) {
          messages.value.push(JSON.parse(message.body))
          scrollToBottom()
        }
      })

      stompClient.subscribe(`/topic/ticket/${ticketId}/seen`, () => {
        messages.value.forEach(m => { m.daDoc = true })
      })
    },
    onStompError: (frame) => {
      console.error('Lỗi STOMP Broker:', frame.headers['message'])
    }
  })

  stompClient.activate()
}

// 4. Gửi tin nhắn thật
const sendAdminMsg = async () => {
  if (!inputText.value.trim() || !selectedTicket.value || !currentUserId) return

  const body = {
    ticketId: selectedTicket.value.id,
    nguoiGuiId: currentUserId,
    loaiNguoiGui: currentRole,
    noiDung: inputText.value
  }

  try {
    await fetch(`/api/support/ticket/${selectedTicket.value.id}/messages`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`
      },
      body: JSON.stringify(body)
    })
    inputText.value = ''
  } catch (err) {
    console.error('Lỗi gửi tin nhắn:', err)
  }
}

// 5. Tiếp nhận Ticket
const tiepNhan = async () => {
  if (!selectedTicket.value || !currentUserId) return
  try {
    const res = await fetch(`/api/support/admin/ticket/${selectedTicket.value.id}/tiep-nhan?staffId=${currentUserId}`, {
      method: 'PUT',
      headers: { Authorization: `Bearer ${token}` }
    })
    if (res.ok) {
      selectedTicket.value = await res.json()
      fetchTickets()
    }
  } catch (err) {
    console.error('Lỗi tiếp nhận ticket:', err)
  }
}

// 6. Đóng Ticket
const dongTicket = async () => {
  if (!selectedTicket.value) return
  try {
    const res = await fetch(`/api/support/admin/ticket/${selectedTicket.value.id}/dong`, {
      method: 'PUT',
      headers: { Authorization: `Bearer ${token}` }
    })
    if (res.ok) {
      selectedTicket.value = await res.json()
      fetchTickets()
    }
  } catch (err) {
    console.error('Lỗi đóng ticket:', err)
  }
}

// 7. Xem thông tin khách hàng (khi bấm vào avatar) — cố gắng lấy hồ sơ đầy đủ từ API thật,
// nếu backend chưa có endpoint riêng thì hiển thị các thông tin thật đã có sẵn trên ticket.
const openCustomerInfo = async (ticket) => {
  if (!ticket) return
  showCustomerModal.value = true
  customerInfo.value = ticket
  dangTaiKhachHang.value = true

  const khachHangId = ticket.khachHangId || ticket.userId || ticket.maKhachHang
  if (khachHangId) {
    try {
      const res = await fetch(`/api/khach-hang/${khachHangId}`, {
        headers: { Authorization: `Bearer ${token}` }
      })
      if (res.ok) {
        const chiTiet = await res.json()
        customerInfo.value = { ...ticket, ...chiTiet }
      }
    } catch (err) {
      console.error('Lỗi lấy thông tin khách hàng:', err)
    }
  }

  dangTaiKhachHang.value = false
}

const closeCustomerInfo = () => {
  showCustomerModal.value = false
  customerInfo.value = null
}

const scrollToBottom = () => {
  nextTick(() => {
    if (adminMsgContainer.value) {
      adminMsgContainer.value.scrollTop = adminMsgContainer.value.scrollHeight
    }
  })
}

const formatTime = (time) => (time ? new Date(time).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : '')

let revealObserver = null

onMounted(() => {
  revealObserver = new IntersectionObserver(entries => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('is-visible')
        revealObserver.unobserve(entry.target)
      }
    })
  }, { threshold: 0.12 })
  nextTick(() => document.querySelectorAll('.reveal').forEach(el => revealObserver.observe(el)))

  fetchTickets()
})

onUnmounted(() => {
  if (stompClient) stompClient.deactivate()
  if (revealObserver) revealObserver.disconnect()
})
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Oswald:wght@500;600;700&family=Manrope:wght@400;500;600;700;800&family=Space+Mono:wght@400;700&display=swap');
</style>

<style scoped>
.support {
  --night-950: #050b08;
  --night-800: #0a1f13;
  --night-700: #123321;
  --turf-500: #23935a;
  --turf-700: #146239;
  --lime-400: #b6ff3c;
  --lime-300: #d3ff8f;
  --amber-400: #ffb020;
  --crimson-500: #ff4757;
  --violet-400: #a78bfa;
  --chalk-050: #f7fbf4;
  --chalk-200: #e3ecdf;
  --font-display: 'Oswald', 'Manrope', sans-serif;
  --font-body: 'Manrope', sans-serif;
  --font-mono: 'Space Mono', monospace;

  position: relative;
  display: flex;
  flex-direction: column;
  gap: 20px;
  background: var(--night-950);
  color: var(--chalk-050);
  font-family: var(--font-body);
  border-radius: 20px;
  padding: 32px;
  overflow: hidden;
  min-height: calc(100vh - 40px);
}

/* ===== REVEAL ===== */
.reveal { opacity: 0; transform: translateY(16px); transition: opacity .55s ease, transform .55s ease; transition-delay: var(--d, 0s); }
.reveal.is-visible { opacity: 1; transform: translateY(0); }
@media (prefers-reduced-motion: reduce) { .reveal { opacity: 1; transform: none; transition: none; } }

/* ===== AMBIENT ===== */
.support__ambient { position: absolute; inset: 0; z-index: 0; pointer-events: none; border-radius: 20px; overflow: hidden; }
.floodlight { position: absolute; top: -20%; width: 40vh; height: 100vh; background: conic-gradient(from 90deg at 50% 0%, transparent 42%, rgba(255,244,214,.06) 50%, transparent 58%); mix-blend-mode: screen; animation: sweep 11s ease-in-out infinite alternate; }
.floodlight--l { left: -6%; }
.floodlight--r { right: -6%; animation-direction: alternate-reverse; }
@keyframes sweep { 0% { transform: rotate(-8deg); } 100% { transform: rotate(8deg); } }
.motes { position: absolute; inset: 0; }
.mote { position: absolute; bottom: -10px; left: calc((var(--i) * 10%) + 1%); width: 3px; height: 3px; border-radius: 50%; background: var(--lime-300); opacity: 0; animation: rise 9s linear infinite; animation-delay: calc(var(--i) * -0.8s); }
@keyframes rise { 0% { transform: translateY(0) scale(.6); opacity: 0; } 10% { opacity: .45; } 90% { opacity: .1; } 100% { transform: translateY(-100%) scale(1.1); opacity: 0; } }
.falling { position: absolute; inset: 0; overflow: hidden; }
.falling__item {
  position: absolute; top: -6%; left: calc((var(--i) * 5.6%) + 1%);
  width: 3px; height: 3px; border-radius: 50%; background: var(--chalk-050);
  opacity: 0; animation: fall linear infinite;
  animation-duration: calc(7s + (var(--i) * 0.45s));
  animation-delay: calc(var(--i) * -0.6s);
}
@keyframes fall {
  0% { transform: translateY(0) translateX(0); opacity: 0; }
  8% { opacity: .55; }
  50% { transform: translateY(160px) translateX(14px); }
  92% { opacity: .12; }
  100% { transform: translateY(340px) translateX(-10px); opacity: 0; }
}

/* ===== HEADER ===== */
.support__header { position: relative; z-index: 1; }
.eyebrow { display: inline-block; font-family: var(--font-mono); font-size: 12px; letter-spacing: .14em; text-transform: uppercase; color: var(--lime-300); }
.support__title { font-family: var(--font-display); font-size: 26px; font-weight: 600; color: var(--chalk-050); margin: 8px 0 4px; }
.support__desc { font-size: 13.5px; color: var(--chalk-200); opacity: .75; margin: 0; }
.support__error { position: relative; z-index: 1; font-size: 14px; color: #ff9686; background: rgba(255,71,87,.1); border: 1px solid rgba(255,71,87,.28); border-radius: 12px; padding: 10px 14px; margin: 0; }

/* ===== WORKSPACE ===== */
.support-workspace {
  position: relative; z-index: 1;
  display: flex;
  height: calc(100vh - 220px);
  min-height: 480px;
  background: var(--night-800);
  border: 1px solid rgba(247,251,244,.1);
  border-radius: 18px;
  overflow: hidden;
}

/* ===== SIDEBAR ===== */
.ticket-sidebar { width: 300px; flex-shrink: 0; border-right: 1px solid rgba(247,251,244,.1); display: flex; flex-direction: column; }
.sidebar-header { padding: 18px 18px 14px; border-bottom: 1px solid rgba(247,251,244,.08); display: flex; justify-content: space-between; align-items: center; }
.sidebar-header h3 { margin: 0; font-family: var(--font-display); font-size: 14.5px; font-weight: 600; color: var(--chalk-050); }
.ticket-count { background: rgba(182,255,60,.12); color: var(--lime-300); font-size: 11.5px; padding: 3px 9px; border-radius: 20px; font-weight: 700; font-family: var(--font-mono); }

.ticket-list { flex: 1; overflow-y: auto; padding: 10px; display: flex; flex-direction: column; gap: 8px; }
.ticket-loading, .ticket-empty, .messages-empty { padding: 40px 16px; text-align: center; color: var(--chalk-200); opacity: .7; font-size: 12.5px; }
.spinner { width: 22px; height: 22px; margin: 0 auto 10px; border: 3px solid rgba(247,251,244,.15); border-top-color: var(--lime-400); border-radius: 50%; animation: spin .8s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }

.ticket-card { background: rgba(247,251,244,.03); border: 1px solid rgba(247,251,244,.08); border-radius: 12px; padding: 12px; cursor: pointer; transition: border-color .15s ease, background .15s ease, transform .15s ease; }
.ticket-card:hover { border-color: rgba(182,255,60,.3); transform: translateY(-1px); }
.ticket-card.active { border-color: var(--turf-500); background: rgba(35,147,90,.14); }

.ticket-card-top { display: flex; align-items: center; gap: 10px; margin-bottom: 8px; }
.ticket-card-top__text { display: flex; flex-direction: column; gap: 4px; flex: 1; min-width: 0; }
.mini-avatar {
  width: 30px; height: 30px; border-radius: 50%; flex-shrink: 0;
  background: var(--turf-500); color: var(--chalk-050); border: none;
  display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 12px; cursor: pointer;
  transition: background .15s ease, transform .15s ease;
}
.mini-avatar:hover { background: var(--lime-400); color: var(--night-950); transform: scale(1.06); }

.ticket-code { font-weight: 700; font-size: 12.5px; color: var(--chalk-050); font-family: var(--font-mono); }

.status-badge { align-self: flex-start; font-size: 10.5px; padding: 3px 9px; border-radius: 12px; font-weight: 700; text-transform: uppercase; letter-spacing: .03em; }
.status-badge.dang_xu_ly { background: rgba(35,147,90,.18); color: var(--lime-300); }
.status-badge.da_dong { background: rgba(247,251,244,.08); color: var(--chalk-200); }
.status-badge.cho_xu_ly { background: rgba(255,176,32,.16); color: var(--amber-400); }
.status-badge.chua_tiep_nhan { background: rgba(255,71,87,.16); color: #ff9686; }

.customer-name { margin: 0 0 3px; font-size: 13.5px; color: var(--chalk-050); font-weight: 600; line-height: 1.3; }
.customer-phone { margin: 0; font-size: 11.5px; color: var(--chalk-200); opacity: .6; }

/* ===== CHAT MAIN ===== */
.chat-main { flex: 1; display: flex; flex-direction: column; min-width: 0; }

.chat-header { padding: 16px 22px; border-bottom: 1px solid rgba(247,251,244,.1); display: flex; justify-content: space-between; align-items: center; gap: 12px; flex-wrap: wrap; }
.user-info { display: flex; align-items: center; gap: 12px; min-width: 0; }
.avatar-circle {
  width: 42px; height: 42px; border-radius: 50%; flex-shrink: 0; border: none; cursor: pointer;
  background: linear-gradient(135deg, var(--turf-500), var(--turf-700));
  color: var(--chalk-050); display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 16px;
  transition: transform .15s ease, box-shadow .15s ease;
}
.avatar-circle:hover { transform: scale(1.06); box-shadow: 0 0 0 3px rgba(182,255,60,.25); }
.header-title { margin: 0; font-family: var(--font-display); font-size: 16px; font-weight: 600; color: var(--chalk-050); }
.header-sub { margin: 3px 0 0; font-size: 12px; color: var(--chalk-200); opacity: .7; }

.chat-actions { display: flex; gap: 8px; }
.btn { padding: 8px 15px; border-radius: 8px; font-weight: 600; font-size: 12.5px; border: none; cursor: pointer; transition: opacity .15s ease; font-family: var(--font-body); }
.btn:hover { opacity: .88; }
.btn-primary { background: var(--turf-500); color: var(--chalk-050); }
.btn-danger { background: rgba(255,71,87,.15); color: #ff9686; border: 1px solid rgba(255,71,87,.35); }

/* ===== MESSAGES ===== */
.chat-messages { flex: 1; padding: 22px; overflow-y: auto; display: flex; flex-direction: column; gap: 16px; }
.message-row { display: flex; gap: 10px; max-width: 68%; }
.msg-left { align-self: flex-start; }
.msg-right { align-self: flex-end; flex-direction: row-reverse; }

.msg-avatar { width: 30px; height: 30px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 12px; font-weight: 700; flex-shrink: 0; color: var(--chalk-050); }
.msg-avatar.admin { background: var(--violet-400); }
.msg-avatar.staff { background: var(--turf-500); }
.msg-avatar.khach_hang { background: rgba(247,251,244,.12); }

.msg-sender-info { display: flex; align-items: center; gap: 6px; margin-bottom: 4px; }
.msg-right .msg-sender-info { justify-content: flex-end; }
.sender-name { font-size: 11.5px; font-weight: 700; color: var(--chalk-200); }

.role-badge { font-size: 9.5px; font-weight: 800; padding: 2px 7px; border-radius: 4px; text-transform: uppercase; letter-spacing: .04em; }
.role-badge.admin { background-color: var(--violet-400); color: var(--night-950); }
.role-badge.staff { background-color: var(--turf-500); color: var(--chalk-050); }
.role-badge.khach_hang { background-color: rgba(247,251,244,.12); color: var(--chalk-200); }

.msg-bubble { padding: 11px 15px; border-radius: 14px; font-size: 13.5px; line-height: 1.5; word-break: break-word; }
.msg-left .msg-bubble { background: rgba(247,251,244,.08); color: var(--chalk-050); border-top-left-radius: 2px; border: 1px solid rgba(247,251,244,.08); }
.msg-bubble.admin { background: linear-gradient(135deg, var(--violet-400), #7c5cf0); color: var(--night-950); border-top-right-radius: 2px; }
.msg-bubble.staff { background: linear-gradient(135deg, var(--turf-500), var(--turf-700)); color: var(--chalk-050); border-top-right-radius: 2px; }

.msg-meta { display: flex; gap: 6px; font-size: 10.5px; color: var(--chalk-200); opacity: .55; margin-top: 4px; }
.msg-right .msg-meta { justify-content: flex-end; }

/* ===== FOOTER ===== */
.chat-footer { padding: 14px 22px; border-top: 1px solid rgba(247,251,244,.1); display: flex; gap: 10px; }
.chat-footer input { flex: 1; padding: 11px 15px; border: 1px solid rgba(247,251,244,.15); background: rgba(5,11,8,.5); border-radius: 9px; outline: none; font-size: 13.5px; color: var(--chalk-050); font-family: var(--font-body); }
.chat-footer input::placeholder { color: rgba(227,236,223,.4); }
.chat-footer input:focus { border-color: var(--lime-400); }

.btn-send { background: var(--turf-500); color: var(--chalk-050); border: none; padding: 0 18px; border-radius: 9px; font-weight: 600; cursor: pointer; display: flex; align-items: center; gap: 6px; transition: background .15s ease; }
.btn-send:hover:not(:disabled) { background: var(--turf-700); }
.btn-send:disabled { opacity: .5; cursor: not-allowed; }

.chat-closed-notice { padding: 14px; text-align: center; color: var(--chalk-200); opacity: .65; font-size: 12.5px; font-weight: 600; }

.empty-state { flex: 1; display: flex; flex-direction: column; align-items: center; justify-content: center; color: var(--chalk-200); text-align: center; padding: 24px; }
.empty-icon { width: 56px; height: 56px; border-radius: 50%; background: rgba(247,251,244,.06); border: 1px solid rgba(247,251,244,.12); display: flex; align-items: center; justify-content: center; color: var(--chalk-200); opacity: .8; margin-bottom: 14px; }
.empty-state h3 { margin: 0 0 4px; font-family: var(--font-display); font-size: 15px; color: var(--chalk-050); }
.empty-state p { margin: 0; font-size: 12.5px; opacity: .6; }

/* ===== MODAL: THÔNG TIN KHÁCH HÀNG ===== */
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,.6); display: flex; align-items: center; justify-content: center; z-index: 1000; padding: 16px; }
.modal-box { background: var(--night-800); border: 1px solid rgba(247,251,244,.12); width: 100%; max-width: 440px; border-radius: 16px; overflow: hidden; color: var(--chalk-050); }
.modal-header { padding: 16px 20px; border-bottom: 1px solid rgba(247,251,244,.1); display: flex; justify-content: space-between; align-items: center; }
.modal-header h3 { margin: 0; font-size: 15px; font-family: var(--font-display); font-weight: 600; }
.btn-close { border: none; background: transparent; font-size: 20px; cursor: pointer; color: var(--chalk-200); line-height: 1; }
.modal-body { padding: 20px; display: flex; flex-direction: column; gap: 16px; min-height: 100px; }

.customer-profile { display: flex; align-items: center; gap: 14px; }
.customer-profile__avatar { width: 52px; height: 52px; border-radius: 50%; background: linear-gradient(135deg, var(--turf-500), var(--turf-700)); color: var(--chalk-050); display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 19px; flex-shrink: 0; }
.customer-profile__name { margin: 0 0 6px; font-weight: 700; font-size: 15px; color: var(--chalk-050); }

.info-grid { display: flex; flex-direction: column; gap: 10px; margin: 0; }
.info-row { display: flex; justify-content: space-between; gap: 12px; font-size: 13px; padding: 9px 0; border-bottom: 1px solid rgba(247,251,244,.07); }
.info-row:last-child { border-bottom: none; }
.info-row dt { color: var(--chalk-200); opacity: .65; margin: 0; }
.info-row dd { margin: 0; color: var(--chalk-050); font-weight: 600; text-align: right; }

.modal-footer { padding: 14px 20px; background: rgba(247,251,244,.03); border-top: 1px solid rgba(247,251,244,.1); display: flex; justify-content: flex-end; }
.btn-cancel { padding: 9px 16px; border-radius: 8px; border: 1px solid rgba(247,251,244,.18); background: transparent; color: var(--chalk-200); cursor: pointer; }
.btn-cancel:hover { background: rgba(247,251,244,.06); }

@media (max-width: 860px) {
  .support-workspace { flex-direction: column; height: auto; }
  .ticket-sidebar { width: 100%; border-right: none; border-bottom: 1px solid rgba(247,251,244,.1); max-height: 320px; }
  .chat-main { min-height: 480px; }
}
</style>