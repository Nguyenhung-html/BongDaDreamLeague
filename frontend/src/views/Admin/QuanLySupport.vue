<template>
  <div class="support-container">
    <!-- SIDEBAR BÊN TRÁI: DANH SÁCH TICKET -->
    <aside class="ticket-sidebar">
      <div class="sidebar-header">
        <h3>💬 Hỗ Trợ Khách Hàng</h3>
        <span class="ticket-count">{{ tickets.length }} Ticket</span>
      </div>

      <div class="ticket-list">
        <div 
          v-for="t in tickets" 
          :key="t.id" 
          class="ticket-card"
          :class="{ active: selectedTicket?.id === t.id }"
          @click="selectTicket(t)"
        >
          <div class="ticket-card-header">
            <span class="ticket-code">#{{ t.maTicket || t.id.slice(0, 8) }}</span>
            <span class="status-badge" :class="t.trangThai?.toLowerCase()">
              {{ t.trangThai === 'DANG_XU_LY' ? 'Đang xử lý' : t.trangThai === 'DA_DONG' ? 'Đã đóng' : 'Mới' }}
            </span>
          </div>

          <div class="ticket-card-body">
            <h4 class="customer-name">{{ t.tenKhachHang || 'Khách Hàng' }}</h4>
            <p class="customer-phone">{{ t.soDienThoai || 'Chưa có SĐT' }}</p>
          </div>
        </div>
      </div>
    </aside>

    <!-- KHUNG CHAT BÊN PHẢI -->
    <main class="chat-main" v-if="selectedTicket">
      <!-- HEADER KHUNG CHAT -->
      <header class="chat-header" v-if="selectedTicket">
        <div class="user-info">
          <div class="avatar-circle">
            {{ (selectedTicket.tenKhachHang || 'K').charAt(0).toUpperCase() }}
          </div>
          <div>
            <h3 class="header-title">{{ selectedTicket.tenKhachHang || 'Khách Hàng' }}</h3>
            <p class="header-sub">
              Mã: <strong>#{{ selectedTicket.maTicket || selectedTicket.id.slice(0, 8) }}</strong>
              • SĐT: <strong>{{ selectedTicket.soDienThoai || 'Chưa cập nhật' }}</strong>
            </p>
          </div>
        </div>

        <div class="chat-actions">
          <button 
            v-if="selectedTicket.trangThai === 'CHO_XU_LY'" 
            class="btn btn-primary" 
            @click="tiepNhan"
          >
            ⚡ Tiếp nhận Ticket
          </button>
          
          <button 
            v-if="selectedTicket.trangThai !== 'DA_DONG'" 
            class="btn btn-danger" 
            @click="dongTicket"
          >
            🔒 Đóng Ticket
          </button>
        </div>
      </header>

      <!-- NỘI DUNG TIN NHẮN -->
      <div class="chat-messages" ref="adminMsgContainer">
        <div 
          v-for="msg in messages" 
          :key="msg.id" 
          class="message-row"
          :class="msg.loaiNguoiGui === 'KHACH_HANG' ? 'msg-left' : 'msg-right'"
        >
          <!-- Avatar phân loại -->
          <div class="msg-avatar" :class="msg.loaiNguoiGui?.toLowerCase()">
            {{ msg.loaiNguoiGui === 'KHACH_HANG' ? '👤' : msg.loaiNguoiGui === 'ADMIN' ? '👑' : '🎧' }}
          </div>

          <div class="msg-content">
            <!-- TÊN & BADGE CHỨC VỤ -->
            <div class="msg-sender-info">
              <!-- Tên người gửi (nếu backend gửi tenNguoiGui thì lấy, không thì lấy mặc định) -->
              <span class="sender-name">
                {{ msg.tenNguoiGui || (msg.loaiNguoiGui === 'KHACH_HANG' ? selectedTicket.tenKhachHang : 'Hỗ trợ viên') }}
              </span>

              <!-- Badge phân biệt Role -->
              <span class="role-badge" :class="msg.loaiNguoiGui?.toLowerCase()">
                {{ msg.loaiNguoiGui === 'KHACH_HANG' ? 'Khách' : msg.loaiNguoiGui === 'ADMIN' ? 'ADMIN' : 'STAFF' }}
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
        <button class="btn-send" @click="sendAdminMsg">
          <span>Gửi</span>
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="22" y1="2" x2="11" y2="13"></line><polygon points="22 2 15 22 11 13 2 9 22 2"></polygon></svg>
        </button>
      </footer>
      <div v-else class="chat-closed-notice">
        🔒 Ticket này đã được đóng.
      </div>
    </main>

    <!-- PLACEHOLDER KHI CHƯA CHỌN TICKET -->
    <div class="empty-state" v-else>
      <div class="empty-icon">💬</div>
      <h3>Chọn một Ticket để bắt đầu hỗ trợ</h3>
      <p>Danh sách ticket chưa giải quyết nằm ở cột bên trái</p>
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

let stompClient = null

// Lấy Token và User Info từ Storage
const token = localStorage.getItem('token') || ''
const currentUser = JSON.parse(localStorage.getItem('user') || '{}')
const currentUserId = currentUser.id || localStorage.getItem('userId') || '22222222-2222-2222-2222-222222222222'

// Tự động xác định Role (ADMIN / STAFF)
const currentRole = route.meta?.roles?.includes('Admin') ? 'ADMIN' : 'STAFF'

// 1. Fetch danh sách Ticket
const fetchTickets = async () => {
  try {
    const res = await fetch('http://localhost:8080/api/support/admin/tickets', {
      headers: { 
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}` 
      }
    })
    if (res.ok) {
      tickets.value = await res.json()
    }
  } catch (err) {
    console.error("Lỗi lấy danh sách Ticket:", err)
  }
}

// 2. Chọn Ticket & Tải lịch sử tin nhắn
const selectTicket = async (t) => {
  if (!t) return
  selectedTicket.value = t
  
  try {
    // Tải tin nhắn
    const res = await fetch(`http://localhost:8080/api/support/ticket/${t.id}/messages`, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    if (res.ok) {
      messages.value = await res.json()
    }

    // Đánh dấu đã đọc
    await fetch(`http://localhost:8080/api/support/ticket/${t.id}/read?userId=${currentUserId}`, {
      method: 'PUT',
      headers: { 'Authorization': `Bearer ${token}` }
    })

    // Kết nối Realtime WebSocket qua @stomp/stompjs
    connectWebSocket(t.id)
    scrollToBottom()
  } catch (err) {
    console.error("Lỗi lấy tin nhắn:", err)
  }
}

// 3. Kết nối WebSocket chuẩn hiện đại (@stomp/stompjs)
const connectWebSocket = (ticketId) => {
  // Hủy kết nối cũ nếu có
  if (stompClient) {
    stompClient.deactivate()
  }

  stompClient = new Client({
    brokerURL: 'ws://localhost:8080/ws-support/websocket',
    debug: () => {}, // Tắt log dư thừa
    reconnectDelay: 5000,
    onConnect: () => {
      // Lắng nghe tin nhắn mới trong phòng chat này
      stompClient.subscribe(`/topic/ticket/${ticketId}`, (message) => {
        if (message.body) {
          messages.value.push(JSON.parse(message.body))
          scrollToBottom()
        }
      })

      // Lắng nghe sự kiện "Đã xem"
      stompClient.subscribe(`/topic/ticket/${ticketId}/seen`, () => {
        messages.value.forEach(m => m.daDoc = true)
      })
    },
    onStompError: (frame) => {
      console.error('Lỗi STOMP Broker:', frame.headers['message'])
    }
  })

  stompClient.activate()
}

// 4. Gửi tin nhắn
const sendAdminMsg = async () => {
  if (!inputText.value.trim() || !selectedTicket.value) return
  
  const body = {
    ticketId: selectedTicket.value.id,
    nguoiGuiId: currentUserId,
    loaiNguoiGui: currentRole,
    noiDung: inputText.value
  }

  try {
    await fetch(`http://localhost:8080/api/support/ticket/${selectedTicket.value.id}/messages`, {
      method: 'POST',
      headers: { 
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(body)
    })
    inputText.value = ''
  } catch (err) {
    console.error("Lỗi gửi tin nhắn:", err)
  }
}

// 5. Tiếp nhận Ticket
const tiepNhan = async () => {
  if (!selectedTicket.value) return
  try {
    const res = await fetch(`http://localhost:8080/api/support/admin/ticket/${selectedTicket.value.id}/tiep-nhan?staffId=${currentUserId}`, {
      method: 'PUT',
      headers: { 'Authorization': `Bearer ${token}` }
    })
    if (res.ok) {
      selectedTicket.value = await res.json()
      fetchTickets()
    }
  } catch (err) {
    console.error("Lỗi tiếp nhận ticket:", err)
  }
}

// 6. Đóng Ticket
const dongTicket = async () => {
  if (!selectedTicket.value) return
  try {
    const res = await fetch(`http://localhost:8080/api/support/admin/ticket/${selectedTicket.value.id}/dong`, {
      method: 'PUT',
      headers: { 'Authorization': `Bearer ${token}` }
    })
    if (res.ok) {
      selectedTicket.value = await res.json()
      fetchTickets()
    }
  } catch (err) {
    console.error("Lỗi đóng ticket:", err)
  }
}

// Cuộn khung chat xuống cuối
const scrollToBottom = () => {
  nextTick(() => {
    if (adminMsgContainer.value) {
      adminMsgContainer.value.scrollTop = adminMsgContainer.value.scrollHeight
    }
  })
}

// Format giờ
const formatTime = (time) => time ? new Date(time).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : ''

onMounted(() => { 
  fetchTickets() 
})

// Tắt WebSocket khi thoát khỏi trang để tránh rò rỉ bộ nhớ
onUnmounted(() => {
  if (stompClient) {
    stompClient.deactivate()
  }
})
</script>

<style scoped>
/* ==========================================
   1. CONTAINER CHÍNH & SIDEBAR BÊN TRÁI
   ========================================== */
.support-container {
  display: flex;
  height: calc(100vh - 80px);
  background-color: #f8fafc;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  margin: 10px;
}

.ticket-sidebar {
  width: 320px;
  background-color: #ffffff;
  border-right: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid #f1f5f9;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sidebar-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
}

.ticket-count {
  background: #f1f5f9;
  color: #64748b;
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 20px;
  font-weight: 600;
}

.ticket-list {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
}

.ticket-card {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  padding: 14px;
  margin-bottom: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.ticket-card:hover {
  border-color: #cbd5e1;
  transform: translateY(-1px);
}

.ticket-card.active {
  border-color: #10b981;
  background-color: #f0fdf4;
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.12);
}

.ticket-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.ticket-code {
  font-weight: 700;
  font-size: 13px;
  color: #0f172a;
}

.status-badge {
  font-size: 11px;
  padding: 3px 8px;
  border-radius: 12px;
  font-weight: 600;
}

.status-badge.dang_xu_ly { background: #e0f2fe; color: #0369a1; }
.status-badge.da_dong { background: #f1f5f9; color: #64748b; }
.status-badge.cho_xu_ly { background: #fef3c7; color: #b45309; }

.customer-name {
  margin: 0 0 4px 0;
  font-size: 14px;
  color: #334155;
  font-weight: 600;
}

.customer-phone {
  margin: 0;
  font-size: 12px;
  color: #94a3b8;
}

/* ==========================================
   2. KHUNG CHAT BÊN PHẢI & HEADER
   ========================================== */
.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #f8fafc;
}

.chat-header {
  background: #ffffff;
  padding: 16px 24px;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar-circle {
  width: 42px;
  height: 42px;
  background: linear-gradient(135deg, #10b981, #059669);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 18px;
}

.header-title {
  margin: 0;
  font-size: 16px;
  color: #0f172a;
}

.header-sub {
  margin: 2px 0 0 0;
  font-size: 12px;
  color: #64748b;
}

.chat-actions {
  display: flex;
  gap: 8px;
}

.btn {
  padding: 8px 16px;
  border-radius: 6px;
  font-weight: 600;
  font-size: 13px;
  border: none;
  cursor: pointer;
  transition: opacity 0.2s;
}

.btn:hover { opacity: 0.9; }
.btn-primary { background: #10b981; color: white; }
.btn-danger { background: #ef4444; color: white; }

/* ==========================================
   3. NỘI DUNG CHAT & CÁC BÓNG TIN NHẮN
   ========================================== */
.chat-messages {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message-row {
  display: flex;
  gap: 10px;
  max-width: 65%;
}

.msg-left { align-self: flex-start; }
.msg-right { align-self: flex-end; flex-direction: row-reverse; }

.msg-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  flex-shrink: 0;
}

/* ĐỔI MÀU AVATAR THEO ROLE */
.msg-avatar.admin { background: #f3e8ff; border: 1px solid #d8b4fe; }
.msg-avatar.staff { background: #e0f2fe; border: 1px solid #bae6fd; }
.msg-avatar.khach_hang { background: #f1f5f9; border: 1px solid #e2e8f0; }

.msg-sender-info {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 4px;
}

.msg-right .msg-sender-info {
  justify-content: flex-end;
}

.sender-name {
  font-size: 12px;
  font-weight: 700;
  color: #475569;
}

/* ROLE BADGE 2 MÀU TÁCH BIỆT */
.role-badge {
  font-size: 10px;
  font-weight: 800;
  padding: 2px 6px;
  border-radius: 4px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.role-badge.admin {
  background-color: #8b5cf6;
  color: #ffffff;
}

.role-badge.staff {
  background-color: #0284c7;
  color: #ffffff;
}

.role-badge.khach_hang {
  background-color: #e2e8f0;
  color: #475569;
}

/* STYLE CÁC BÓNG CHAT */
.msg-bubble {
  padding: 12px 16px;
  border-radius: 14px;
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.msg-left .msg-bubble {
  background: #ffffff;
  color: #1e293b;
  border-top-left-radius: 2px;
  border: 1px solid #e2e8f0;
}

/* ADMIN: Gradient Tím */
.msg-bubble.admin {
  background: linear-gradient(135deg, #8b5cf6, #7c3aed) !important;
  color: #ffffff !important;
  border-top-right-radius: 2px;
}

/* STAFF: Gradient Xanh Dương */
.msg-bubble.staff {
  background: linear-gradient(135deg, #0284c7, #0369a1) !important;
  color: #ffffff !important;
  border-top-right-radius: 2px;
}

.msg-meta {
  display: flex;
  gap: 6px;
  font-size: 11px;
  color: #94a3b8;
  margin-top: 4px;
}

.msg-right .msg-meta { justify-content: flex-end; }

/* ==========================================
   4. FOOTER NHẬP TIN NHẮN & EMPTY STATE
   ========================================== */
.chat-footer {
  padding: 16px 24px;
  background: #ffffff;
  border-top: 1px solid #e2e8f0;
  display: flex;
  gap: 12px;
}

.chat-footer input {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid #cbd5e1;
  border-radius: 8px;
  outline: none;
  font-size: 14px;
}

.chat-footer input:focus {
  border-color: #10b981;
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.15);
}

.btn-send {
  background: #10b981;
  color: white;
  border: none;
  padding: 0 20px;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
}

.chat-closed-notice {
  padding: 16px;
  background: #f1f5f9;
  text-align: center;
  color: #64748b;
  font-size: 13px;
  font-weight: 600;
}

.empty-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #64748b;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 12px;
}
</style>