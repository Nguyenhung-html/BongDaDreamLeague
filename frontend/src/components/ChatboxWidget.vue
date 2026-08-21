<template>
  <div class="chatbox-wrapper">
    <!-- Button Floating -->
    <button class="chat-toggle-btn" @click="toggleChat">
      💬 <span v-if="unreadCount > 0" class="badge">{{ unreadCount }}</span>
    </button>

    <!-- Khung Chat -->
    <div v-if="isOpen" class="chat-window">
      <div class="chat-header">
        <div>
          <h4>Hỗ Trợ Khách Hàng</h4>
          <small v-if="ticket">Mã: #{{ ticket.maTicket }}</small>
        </div>
        <button @click="toggleChat" class="close-btn">✖</button>
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
        <button @click="sendMsg">Gửi</button>
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
      `http://localhost:8080/api/support/ticket/active?userId=${currentUserId || ''}&name=${encodeURIComponent(currentUserName)}&phone=${currentUserPhone}`,
      { headers }
    )
    
    if (res.ok) {
      ticket.value = await res.json()
      
      // Lấy lịch sử tin nhắn
      const msgRes = await fetch(`http://localhost:8080/api/support/ticket/${ticket.value.id}/messages`, { headers })
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
    await fetch(`http://localhost:8080/api/support/ticket/${ticket.value.id}/messages`, {
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
    await fetch(`http://localhost:8080/api/support/ticket/${ticket.value.id}/read?userId=${currentUserId || ''}`, { 
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
.chatbox-wrapper { position: fixed; bottom: 20px; right: 20px; z-index: 9999; }
.chat-toggle-btn { width: 55px; height: 55px; border-radius: 50%; background: #10b981; color: white; border: none; font-size: 24px; cursor: pointer; position: relative; }
.badge { position: absolute; top: -5px; right: -5px; background: #ef4444; color: white; font-size: 11px; padding: 2px 6px; border-radius: 10px; }
.chat-window { width: 340px; height: 450px; background: white; border-radius: 12px; box-shadow: 0 5px 20px rgba(0,0,0,0.15); display: flex; flex-direction: column; overflow: hidden; position: absolute; bottom: 65px; right: 0; }
.chat-header { background: #10b981; color: white; padding: 12px; display: flex; justify-content: space-between; align-items: center; }
.chat-header h4 { margin: 0; font-size: 15px; }
.close-btn { background: none; border: none; color: white; font-size: 16px; cursor: pointer; }
.chat-body { flex: 1; padding: 12px; overflow-y: auto; display: flex; flex-direction: column; gap: 8px; background: #f8fafc; }
.msg { max-width: 75%; padding: 8px 12px; border-radius: 10px; font-size: 13px; line-height: 1.4; }
.msg.me { align-self: flex-end; background: #10b981; color: white; }
.msg.them { align-self: flex-start; background: white; border: 1px solid #e2e8f0; color: #1e293b; }
.time { font-size: 10px; opacity: 0.7; display: block; text-align: right; margin-top: 2px; }
.seen { color: #60a5fa; font-weight: bold; }
.chat-footer { padding: 10px; display: flex; gap: 6px; border-top: 1px solid #e2e8f0; }
.chat-footer input { flex: 1; padding: 8px; border: 1px solid #cbd5e1; border-radius: 6px; outline: none; }
.chat-footer button { background: #10b981; color: white; border: none; padding: 0 12px; border-radius: 6px; cursor: pointer; }
</style>