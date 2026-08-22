<template>
  <div class="chatbox-wrapper">
    <!-- Button Floating -->
    <button class="chat-toggle-btn" @click="toggleChat">
      <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"/>
      </svg>
      <span v-if="unreadCount > 0" class="badge">{{ unreadCount }}</span>
    </button>

    <!-- Khung Chat -->
    <div v-if="isOpen" class="chat-window">
      <div class="chat-window__ambient" aria-hidden="true"></div>

      <div class="chat-header">
        <div>
          <span class="chat-header__eyebrow">⚽ DreamLeague</span>
          <h4>Hỗ trợ khách hàng</h4>
          <small v-if="ticket">Mã: #{{ ticket.maTicket }}</small>
        </div>
        <button @click="toggleChat" class="close-btn" aria-label="Đóng khung chat">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none"><path d="M18 6L6 18M6 6l12 12" stroke="currentColor" stroke-width="2.2" stroke-linecap="round"/></svg>
        </button>
      </div>

      <div class="chat-body" ref="msgContainer">
        <div v-for="m in messages" :key="m.id" :class="['msg', m.loaiNguoiGui === 'KHACH_HANG' ? 'me' : 'them']">
          <p>{{ m.noiDung }}</p>
          <span class="time">
            {{ formatTime(m.thoiGianGui) }}
            <template v-if="m.loaiNguoiGui === 'KHACH_HANG'">
              <span v-if="m.daDoc" class="seen">✓✓</span>
              <span v-else>✓</span>
            </template>
          </span>
        </div>
      </div>

      <div class="chat-footer">
        <input v-model="inputText" @keyup.enter="sendMsg" placeholder="Nhập tin nhắn..." />
        <button @click="sendMsg" class="send-btn" aria-label="Gửi tin nhắn">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none"><path d="M5 12H19M19 12L13 6M19 12L13 18" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/></svg>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { Client } from '@stomp/stompjs'

const isOpen = ref(false)
const inputText = ref('')
const ticket = ref(null)
const messages = ref([])
const unreadCount = ref(0)
const msgContainer = ref(null)

let stompClient = null

// Lấy thông tin User đã đăng nhập (nếu có)
const userStorage = JSON.parse(localStorage.getItem('user') || '{}')
const currentUserId = userStorage.id || localStorage.getItem('userId') || null
const currentUserName = userStorage.hoTen || userStorage.ten || 'Khách Hàng'
const currentUserPhone = userStorage.soDienThoai || ''

// Mở / Đóng khung chat
const toggleChat = async () => {
  isOpen.value = !isOpen.value
  if (isOpen.value && ticket.value) {
    unreadCount.value = 0
    await markRead()
    scrollToBottom()
  }
}

// 1. Khởi tạo Ticket & Lấy lịch sử tin nhắn
const initChat = async () => {
  try {
    const token = localStorage.getItem('token')
    const headers = token ? { 'Authorization': `Bearer ${token}` } : {}

    // API lấy hoặc tạo ticket active
    const res = await fetch(
      `/api/support/ticket/active?userId=${currentUserId || ''}&name=${encodeURIComponent(currentUserName)}&phone=${currentUserPhone}`,
      { headers }
    )

    if (res.ok) {
      ticket.value = await res.json()

      // Lấy lịch sử tin nhắn
      const msgRes = await fetch(`/api/support/ticket/${ticket.value.id}/messages`, { headers })
      if (msgRes.ok) {
        messages.value = await msgRes.json()
      }

      // Kết nối Realtime WebSocket qua @stomp/stompjs
      connectWebSocket(ticket.value.id)
    }
  } catch (err) {
    console.error('Lỗi khởi tạo Chatbox Widget:', err)
  }
}

// 2. Kết nối WebSocket chuẩn hiện đại (@stomp/stompjs)
const connectWebSocket = (ticketId) => {
  if (stompClient) {
    stompClient.deactivate()
  }

  stompClient = new Client({
    brokerURL: 'ws://localhost:8080/ws-support/websocket',
    debug: () => {}, // Tắt log dư thừa
    reconnectDelay: 5000,
    onConnect: () => {
      // Subscribe nhận tin nhắn mới
      stompClient.subscribe(`/topic/ticket/${ticketId}`, (message) => {
        if (message.body) {
          const newMsg = JSON.parse(message.body)
          messages.value.push(newMsg)

          // Tăng số tin chưa đọc nếu đang đóng khung chat và người gửi không phải là Khách
          if (!isOpen.value && newMsg.loaiNguoiGui !== 'KHACH_HANG') {
            unreadCount.value++
          }

          scrollToBottom()
        }
      })
    },
    onStompError: (frame) => {
      console.error('Lỗi STOMP Broker:', frame.headers['message'])
    }
  })

  stompClient.activate()
}

// 3. Gửi tin nhắn từ Khách hàng
const sendMsg = async () => {
  if (!inputText.value.trim() || !ticket.value) return

  const token = localStorage.getItem('token')
  const headers = {
    'Content-Type': 'application/json',
    ...(token && { 'Authorization': `Bearer ${token}` })
  }

  const body = {
    ticketId: ticket.value.id,
    nguoiGuiId: currentUserId,
    loaiNguoiGui: 'KHACH_HANG',
    noiDung: inputText.value
  }

  try {
    await fetch(`/api/support/ticket/${ticket.value.id}/messages`, {
      method: 'POST',
      headers,
      body: JSON.stringify(body)
    })
    inputText.value = ''
  } catch (err) {
    console.error('Lỗi gửi tin nhắn:', err)
  }
}

// 4. Đánh dấu đã đọc
const markRead = async () => {
  if (!ticket.value) return
  const token = localStorage.getItem('token')
  const headers = token ? { 'Authorization': `Bearer ${token}` } : {}

  try {
    await fetch(`/api/support/ticket/${ticket.value.id}/read?userId=${currentUserId || ''}`, {
      method: 'PUT',
      headers
    })
  } catch (err) {
    console.error('Lỗi đánh dấu đã đọc:', err)
  }
}

// Tự động cuộn khung chat xuống cuối
const scrollToBottom = () => {
  nextTick(() => {
    if (msgContainer.value) {
      msgContainer.value.scrollTop = msgContainer.value.scrollHeight
    }
  })
}

// Format giờ
const formatTime = (time) => time ? new Date(time).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : ''

onMounted(() => {
  initChat()
})

// Ngắt kết nối khi component bị hủy
onUnmounted(() => {
  if (stompClient) {
    stompClient.deactivate()
  }
})
</script>

<style scoped>
/* ===== TOKENS (đồng bộ toàn hệ thống night-pitch) ===== */
.chatbox-wrapper {
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

  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 9999;
  font-family: var(--font-body);
}

/* ===== NÚT NỔI ===== */
.chat-toggle-btn {
  width: 56px; height: 56px; border-radius: 50%;
  background: linear-gradient(135deg, var(--turf-500), var(--turf-700));
  color: var(--chalk-050); border: none; cursor: pointer;
  position: relative; display: flex; align-items: center; justify-content: center;
  box-shadow: 0 10px 28px -8px rgba(182,255,60,.35), 0 4px 14px rgba(0,0,0,.35);
  transition: transform .18s ease, box-shadow .18s ease;
}
.chat-toggle-btn:hover { transform: translateY(-2px); box-shadow: 0 14px 34px -8px rgba(182,255,60,.5), 0 4px 14px rgba(0,0,0,.4); }
.badge {
  position: absolute; top: -4px; right: -4px;
  background: var(--crimson-500); color: white; font-size: 11px; font-weight: 700;
  padding: 2px 6px; border-radius: 999px; border: 2px solid var(--night-950);
}

/* ===== CỬA SỔ CHAT ===== */
.chat-window {
  width: 340px; height: 460px;
  background: var(--night-800);
  border: 1px solid rgba(182,255,60,.18);
  border-radius: 18px;
  box-shadow: 0 24px 60px rgba(0,0,0,.5);
  display: flex; flex-direction: column; overflow: hidden;
  position: absolute; bottom: 68px; right: 0;
}
.chat-window__ambient {
  position: absolute; inset: 0; pointer-events: none; z-index: 0;
  background: radial-gradient(circle at 85% -10%, rgba(182,255,60,.12), transparent 55%);
}

/* Header */
.chat-header {
  position: relative; z-index: 1;
  background: linear-gradient(135deg, var(--turf-700), var(--night-700));
  color: var(--chalk-050); padding: 14px 16px;
  display: flex; justify-content: space-between; align-items: flex-start;
  border-bottom: 1px solid rgba(182,255,60,.15);
}
.chat-header__eyebrow {
  display: block; font-family: var(--font-mono); font-size: 10px;
  letter-spacing: .1em; text-transform: uppercase; color: var(--lime-300); margin-bottom: 2px;
}
.chat-header h4 { margin: 0; font-family: var(--font-display); font-size: 15.5px; font-weight: 600; }
.chat-header small { color: var(--chalk-200); opacity: .7; font-size: 11px; }
.close-btn {
  background: rgba(247,251,244,.08); border: none; color: var(--chalk-050);
  width: 26px; height: 26px; border-radius: 50%; cursor: pointer;
  display: flex; align-items: center; justify-content: center; transition: background .15s;
  flex-shrink: 0;
}
.close-btn:hover { background: rgba(255,71,87,.35); }

/* Body */
.chat-body {
  position: relative; z-index: 1;
  flex: 1; padding: 14px; overflow-y: auto;
  display: flex; flex-direction: column; gap: 9px;
  background: var(--night-950);
}
.chat-body::-webkit-scrollbar { width: 5px; }
.chat-body::-webkit-scrollbar-thumb { background: rgba(182,255,60,.25); border-radius: 10px; }

.msg { max-width: 75%; padding: 9px 13px; border-radius: 12px; font-size: 13px; line-height: 1.45; }
.msg p { margin: 0; }
.msg.me {
  align-self: flex-end;
  background: linear-gradient(135deg, var(--turf-500), var(--turf-700));
  color: var(--chalk-050); border-bottom-right-radius: 4px;
}
.msg.them {
  align-self: flex-start;
  background: rgba(247,251,244,.05); border: 1px solid rgba(247,251,244,.1);
  color: var(--chalk-050); border-bottom-left-radius: 4px;
}
.time { font-size: 10px; opacity: .65; display: block; text-align: right; margin-top: 3px; }
.seen { color: var(--lime-300); font-weight: bold; }

/* Footer */
.chat-footer {
  position: relative; z-index: 1;
  padding: 10px; display: flex; gap: 8px;
  border-top: 1px solid rgba(247,251,244,.1);
  background: var(--night-800);
}
.chat-footer input {
  flex: 1; padding: 9px 12px;
  background: rgba(5,11,8,.5); border: 1.5px solid rgba(247,251,244,.14);
  border-radius: 999px; outline: none; color: var(--chalk-050); font-family: inherit; font-size: 13px;
  transition: border-color .15s;
}
.chat-footer input::placeholder { color: var(--chalk-200); opacity: .45; }
.chat-footer input:focus { border-color: var(--lime-400); }
.send-btn {
  background: linear-gradient(135deg, var(--turf-500), var(--turf-700));
  color: var(--chalk-050); border: none; width: 36px; height: 36px; border-radius: 50%;
  cursor: pointer; flex-shrink: 0; display: flex; align-items: center; justify-content: center;
  transition: box-shadow .15s, transform .15s;
}
.send-btn:hover { box-shadow: 0 6px 16px -4px rgba(182,255,60,.45); transform: translateY(-1px); }
</style>