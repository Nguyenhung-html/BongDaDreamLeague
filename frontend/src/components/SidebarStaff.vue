<template>
  <aside class="sidebar">
    <p class="sidebar__brand"><strong>Dream</strong>League</p>
    <nav class="sidebar__nav">
      <router-link to="/staff">Dashboard</router-link>
      <router-link to="/staff/dat-san">Quản lý đặt sân</router-link>
      <router-link to="/staff/thanh-toan">Quản lý thanh toán</router-link>
      <router-link to="/staff/lich-san">Quản lý lịch sân</router-link>
      <router-link to="/staff/hoa-don">Xem hoá đơn</router-link>
      <router-link to="/staff/ho-tro">Hỗ trợ khách hàng</router-link>
    </nav>
  </aside>
</template>

<script setup></script>

<style scoped>
.sidebar { width: 240px; min-height: 100vh; background: var(--navy-900); color: var(--white); padding: 24px 18px; }
.sidebar__brand { font-family: var(--font-score); font-size: 20px; margin-bottom: 24px; }
.sidebar__nav { display: flex; flex-direction: column; gap: 4px; }
.sidebar__nav a { padding: 10px 12px; border-radius: 8px; font-size: 14px; color: rgba(255,255,255,0.75); }
.sidebar__nav a.router-link-exact-active { background: var(--green-600); color: var(--white); }
</style>