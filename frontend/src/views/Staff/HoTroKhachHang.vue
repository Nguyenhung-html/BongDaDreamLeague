<template>
  <div class="hotro-manager">
    <!-- HEADER -->
    <div class="page-header">
      <div>
        <h1 class="page-title">Hỗ trợ khách hàng</h1>
        <p class="page-desc">Chatbox, Zalo và kênh liên hệ trực tiếp.</p>
      </div>
    </div>

    <!-- TABS -->
    <div class="tab-bar">
      <button v-for="tab in tabs" :key="tab.key" :class="['tab-btn', activeTab === tab.key ? 'active' : '']" @click="activeTab = tab.key">
        <span>{{ tab.icon }}</span> {{ tab.label }}
      </button>
    </div>

    <!-- ══ TAB 1: CHATBOX ══ -->
    <div v-if="activeTab === 'chatbox'" class="chatbox-layout">
      <!-- Ticket list -->
      <div class="ticket-list">
        <div class="ticket-search">
          <input v-model="searchTicket" class="form-control" placeholder="Tìm khách..."/>
        </div>
        <div
          v-for="t in filteredTickets" :key="t.id"
          :class="['ticket-item', selectedTicket?.id === t.id ? 'selected' : '']"
          @click="selectTicket(t)"
        >
          <div class="ticket-avatar">{{ t.name[0] }}</div>
          <div class="ticket-meta">
            <span class="ticket-name">{{ t.name }}</span>
            <span class="ticket-last">{{ t.messages[t.messages.length - 1]?.text?.slice(0, 30) }}...</span>
          </div>
          <span :class="['ticket-status', t.status]">{{ t.status === 'open' ? '🟡' : '✅' }}</span>
        </div>
      </div>

      <!-- Chat window -->
      <div class="chat-window" v-if="selectedTicket">
        <div class="chat-header">
          <div class="chat-avatar">{{ selectedTicket.name[0] }}</div>
          <div>
            <div class="chat-name">{{ selectedTicket.name }}</div>
            <div class="chat-phone">{{ selectedTicket.phone }}</div>
          </div>
          <button class="btn-resolve" @click="resolveTicket" v-if="selectedTicket.status === 'open'">✅ Đóng ticket</button>
        </div>
        <div class="chat-messages" ref="messagesEl">
          <div v-for="(msg, i) in selectedTicket.messages" :key="i" :class="['msg-bubble', msg.sender]">
            <span class="msg-text">{{ msg.text }}</span>
            <span class="msg-time">{{ msg.time }}</span>
          </div>
        </div>
        <div class="chat-input-row">
          <input v-model="replyText" class="form-control" placeholder="Nhập câu trả lời..." @keydown.enter="sendReply"/>
          <button class="btn btn-primary" @click="sendReply">Gửi ↗</button>
        </div>
      </div>
      <div class="chat-empty" v-else>
        <span>💬</span>
        <p>Chọn một khách hàng để bắt đầu hỗ trợ</p>
      </div>
    </div>

    <!-- ══ TAB 2: ZALO & SĐT ══ -->
    <div v-if="activeTab === 'zalo'" class="contact-layout">
      <div class="contact-card zalo-card">
        <div class="contact-icon">💬</div>
        <h3>Zalo Official Account</h3>
        <p>Khách hàng nhắn tin trực tiếp qua Zalo OA. Staff phản hồi trong vòng 15 phút.</p>
        <div class="qr-box">
          <div class="qr-placeholder">
            <span style="font-size:64px">📱</span>
            <span style="font-size:13px;color:#64748b">QR Zalo OA</span>
          </div>
        </div>
        <a href="https://zalo.me/0901234567" target="_blank" class="btn btn-zalo">Mở Zalo OA →</a>
      </div>

      <div class="contact-card phone-card">
        <div class="contact-icon">📞</div>
        <h3>Đường dây Hotline</h3>
        <p>Hỗ trợ trực tiếp qua điện thoại trong giờ hoạt động (06:00 – 22:00).</p>
        <div class="hotline-list">
          <div class="hotline-item">
            <span class="hotline-label">Hotline chính</span>
            <a href="tel:0901234567" class="hotline-number">0901 234 567</a>
          </div>
          <div class="hotline-item">
            <span class="hotline-label">Hỗ trợ kỹ thuật</span>
            <a href="tel:0912345678" class="hotline-number">0912 345 678</a>
          </div>
        </div>
        <div class="working-hours">
          <span class="hours-icon">🕐</span>
          <span>Giờ làm việc: <strong>06:00 – 22:00</strong> tất cả các ngày</span>
        </div>
      </div>

      <div class="contact-card info-card">
        <div class="contact-icon">📍</div>
        <h3>Địa chỉ sân bóng</h3>
        <p><strong>Dream League Football</strong></p>
        <p>Hẻm 104 Tân Sơn, Phường Tân Sơn, Quận 12, TP. Hồ Chí Minh</p>
        <div class="map-placeholder">🗺️ Bản đồ Google Maps</div>
      </div>
    </div>

    <!-- ══ TAB 3: LỊCH SỬ HỖ TRỢ ══ -->
    <div v-if="activeTab === 'lichsu'" class="table-card">
      <table class="data-table">
        <thead>
          <tr>
            <th>Khách hàng</th>
            <th>Nội dung yêu cầu</th>
            <th>Kênh</th>
            <th>Ngày tạo</th>
            <th>Trạng thái</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="r in lichSuHoTro" :key="r.id">
            <td>
              <div class="cell-stack">
                <span class="bold">{{ r.name }}</span>
                <span class="sub">{{ r.phone }}</span>
              </div>
            </td>
            <td><span class="req-text">{{ r.request }}</span></td>
            <td><span :class="['kenh-badge', 'kenh-' + r.kenh]">{{ r.kenh }}</span></td>
            <td class="sub">{{ r.ngay }}</td>
            <td><span :class="['badge', r.status === 'Đã xử lý' ? 'badge-success' : 'badge-warning']">{{ r.status }}</span></td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick } from 'vue'

const activeTab = ref('chatbox')
const tabs = [
  { key: 'chatbox', icon: '💬', label: 'Chatbox' },
  { key: 'zalo', icon: '📱', label: 'Zalo & Hotline' },
  { key: 'lichsu', icon: '📋', label: 'Lịch sử hỗ trợ' }
]

const searchTicket = ref('')
const replyText = ref('')
const selectedTicket = ref(null)
const messagesEl = ref(null)

// Dữ liệu chat mẫu — trong thực tế sẽ kết nối Zalo API hoặc WebSocket
const tickets = ref([
  {
    id: 1, name: 'Nguyễn Văn A', phone: '0987654321', status: 'open',
    messages: [
      { sender: 'customer', text: 'Sân A1 ngày mai còn trống không ạ?', time: '08:30' },
      { sender: 'staff', text: 'Dạ chào anh! Sân A1 ngày mai vẫn còn slot 17h-18h và 19h-20h ạ.', time: '08:35' },
      { sender: 'customer', text: 'Cho tôi đặt slot 19h-20h nhé', time: '08:36' }
    ]
  },
  {
    id: 2, name: 'Trần Thị B', phone: '0911222333', status: 'open',
    messages: [
      { sender: 'customer', text: 'Tôi muốn hủy đơn đặt sân ngày 28/06, có được không ạ?', time: '09:15' }
    ]
  },
  {
    id: 3, name: 'Lê Minh C', phone: '0922444555', status: 'resolved',
    messages: [
      { sender: 'customer', text: 'Sân bóng có phòng thay đồ không ạ?', time: '10:00' },
      { sender: 'staff', text: 'Dạ có ạ! Sân A4 và B1 đều có phòng thay đồ riêng.', time: '10:05' }
    ]
  }
])

const filteredTickets = computed(() => {
  const s = searchTicket.value.toLowerCase()
  return tickets.value.filter(t => !s || t.name.toLowerCase().includes(s))
})

function selectTicket(t) {
  selectedTicket.value = t
  nextTick(() => {
    if (messagesEl.value) messagesEl.value.scrollTop = messagesEl.value.scrollHeight
  })
}

function sendReply() {
  if (!replyText.value.trim() || !selectedTicket.value) return
  const now = new Date()
  selectedTicket.value.messages.push({
    sender: 'staff',
    text: replyText.value.trim(),
    time: now.getHours().toString().padStart(2,'0') + ':' + now.getMinutes().toString().padStart(2,'0')
  })
  replyText.value = ''
  nextTick(() => {
    if (messagesEl.value) messagesEl.value.scrollTop = messagesEl.value.scrollHeight
  })
}

function resolveTicket() {
  if (selectedTicket.value) selectedTicket.value.status = 'resolved'
}

const lichSuHoTro = ref([
  { id: 1, name: 'Nguyễn Văn A', phone: '0987654321', request: 'Hỏi về khung giờ trống sân A1', kenh: 'Chatbox', ngay: '25/06/2026', status: 'Đã xử lý' },
  { id: 2, name: 'Trần Thị B', phone: '0911222333', request: 'Yêu cầu hủy đặt sân ngày 28/06', kenh: 'Zalo', ngay: '25/06/2026', status: 'Đang xử lý' },
  { id: 3, name: 'Phạm Văn D', phone: '0933111222', request: 'Hỏi về giá sân 7 người cuối tuần', kenh: 'Hotline', ngay: '24/06/2026', status: 'Đã xử lý' }
])
</script>

<style scoped>
.hotro-manager { width: 100%; }
.page-header { margin-bottom: 20px; }
.page-title { font-size: 24px; font-weight: 700; color: #1e293b; margin: 0 0 4px; }
.page-desc { font-size: 14px; color: #64748b; margin: 0; }

.tab-bar { display: flex; gap: 4px; margin-bottom: 20px; background: #f1f5f9; padding: 4px; border-radius: 10px; width: max-content; }
.tab-btn { padding: 9px 20px; border: none; border-radius: 8px; background: transparent; font-size: 14px; font-weight: 500; cursor: pointer; color: #64748b; transition: .2s; display: flex; align-items: center; gap: 6px; }
.tab-btn.active { background: #fff; color: #1E3932; font-weight: 700; box-shadow: 0 1px 4px rgba(0,0,0,.08); }

/* CHATBOX */
.chatbox-layout { display: grid; grid-template-columns: 300px 1fr; gap: 0; background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; overflow: hidden; height: 540px; }
.ticket-list { border-right: 1px solid #e2e8f0; display: flex; flex-direction: column; }
.ticket-search { padding: 12px; border-bottom: 1px solid #f1f5f9; }
.form-control { width: 100%; padding: 9px 13px; border: 1px solid #cbd5e1; border-radius: 8px; font-size: 14px; outline: none; background: #fff; color: #1e293b; box-sizing: border-box; }
.form-control:focus { border-color: #1E3932; }
.ticket-item { display: flex; align-items: center; gap: 10px; padding: 12px 14px; cursor: pointer; transition: .15s; border-bottom: 1px solid #f8fafc; }
.ticket-item:hover, .ticket-item.selected { background: #f0fdf4; }
.ticket-avatar { width: 38px; height: 38px; border-radius: 50%; background: #1E3932; color: #fff; display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 16px; flex-shrink: 0; }
.ticket-meta { flex: 1; overflow: hidden; }
.ticket-name { font-weight: 600; font-size: 14px; color: #1e293b; display: block; }
.ticket-last { font-size: 12px; color: #94a3b8; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; display: block; }
.ticket-status { font-size: 16px; }

.chat-window { display: flex; flex-direction: column; }
.chat-header { display: flex; align-items: center; gap: 12px; padding: 14px 18px; border-bottom: 1px solid #e2e8f0; background: #f8fafc; }
.chat-avatar { width: 40px; height: 40px; border-radius: 50%; background: #1E3932; color: #fff; display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 17px; }
.chat-name { font-weight: 700; color: #1e293b; }
.chat-phone { font-size: 12px; color: #64748b; }
.btn-resolve { margin-left: auto; padding: 6px 14px; border: 1px solid #86efac; border-radius: 8px; background: #dcfce7; color: #15803d; font-size: 13px; font-weight: 600; cursor: pointer; }
.chat-messages { flex: 1; overflow-y: auto; padding: 16px 18px; display: flex; flex-direction: column; gap: 10px; background: #fafafa; }
.msg-bubble { display: flex; flex-direction: column; max-width: 70%; }
.msg-bubble.customer { align-self: flex-start; }
.msg-bubble.staff { align-self: flex-end; }
.msg-text { padding: 10px 14px; border-radius: 12px; font-size: 14px; line-height: 1.5; }
.customer .msg-text { background: #fff; border: 1px solid #e2e8f0; color: #1e293b; border-bottom-left-radius: 4px; }
.staff .msg-text { background: #1E3932; color: #fff; border-bottom-right-radius: 4px; }
.msg-time { font-size: 11px; color: #94a3b8; margin-top: 3px; }
.staff .msg-time { text-align: right; }
.chat-input-row { display: flex; gap: 8px; padding: 12px 16px; border-top: 1px solid #e2e8f0; background: #fff; }
.chat-input-row .form-control { flex: 1; }
.chat-empty { display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 10px; color: #94a3b8; font-size: 15px; }
.chat-empty span { font-size: 48px; }

/* CONTACT */
.contact-layout { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; }
.contact-card { background: #fff; border: 1px solid #e2e8f0; border-radius: 14px; padding: 24px; display: flex; flex-direction: column; align-items: center; text-align: center; gap: 12px; }
.contact-icon { font-size: 40px; }
.contact-card h3 { font-size: 17px; font-weight: 700; color: #1e293b; margin: 0; }
.contact-card p { font-size: 14px; color: #64748b; margin: 0; line-height: 1.6; }
.qr-box { margin: 8px 0; }
.qr-placeholder { width: 140px; height: 140px; border: 2px dashed #cbd5e1; border-radius: 12px; display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 8px; }
.btn { display: inline-flex; align-items: center; gap: 6px; padding: 10px 18px; font-size: 14px; font-weight: 600; border-radius: 8px; border: none; cursor: pointer; transition: .2s; text-decoration: none; }
.btn-primary { background: #1E3932; color: #fff; }
.btn-primary:hover { background: #14261f; }
.btn-zalo { background: #0068FF; color: #fff; }
.hotline-list { width: 100%; display: flex; flex-direction: column; gap: 10px; }
.hotline-item { display: flex; justify-content: space-between; align-items: center; padding: 10px 14px; background: #f8fafc; border-radius: 8px; }
.hotline-label { font-size: 13px; color: #64748b; }
.hotline-number { font-size: 16px; font-weight: 700; color: #1E3932; text-decoration: none; }
.working-hours { display: flex; align-items: center; gap: 8px; font-size: 13px; color: #475569; }
.hours-icon { font-size: 18px; }
.map-placeholder { width: 100%; height: 100px; background: #f1f5f9; border-radius: 10px; display: flex; align-items: center; justify-content: center; font-size: 15px; color: #94a3b8; }

/* TABLE */
.table-card { background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; overflow: hidden; }
.data-table { width: 100%; border-collapse: collapse; font-size: 14px; }
.data-table th { background: #f8fafc; color: #64748b; font-weight: 600; padding: 14px 16px; text-align: left; border-bottom: 1px solid #e2e8f0; }
.data-table td { padding: 12px 16px; border-bottom: 1px solid #f1f5f9; vertical-align: middle; }
.cell-stack { display: flex; flex-direction: column; gap: 3px; }
.bold { font-weight: 600; color: #1e293b; }
.sub { font-size: 12px; color: #64748b; }
.req-text { font-size: 13px; color: #334155; }
.kenh-badge { display: inline-flex; padding: 3px 9px; border-radius: 6px; font-size: 12px; font-weight: 600; }
.kenh-Chatbox { background: #ede9fe; color: #6d28d9; }
.kenh-Zalo { background: #dbeafe; color: #1d4ed8; }
.kenh-Hotline { background: #fef3c7; color: #92400e; }
.badge { display: inline-flex; padding: 4px 10px; border-radius: 20px; font-size: 12px; font-weight: 600; }
.badge-success { background: #dcfce7; color: #15803d; }
.badge-warning { background: #fef3c7; color: #b45309; }
</style>