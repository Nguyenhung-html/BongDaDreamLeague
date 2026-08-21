<template>
  <div id="app-root">
    <!-- User Layout -->
    <template v-if="route.meta.layout === 'user'">
      <Header />
      <main>
        <router-view />
      </main>
      <!-- Chatbox Widget chỉ hiển thị ở giao diện Khách hàng -->
      <ChatboxWidget />
      <Footer />
    </template>

    <!-- Admin Layout -->
    <template v-else-if="route.meta.layout === 'admin'">
      <AdminLayout />
    </template>

    <!-- Staff Layout -->
    <template v-else-if="route.meta.layout === 'staff'">
      <StaffLayout />
    </template>

    <!-- Default Layout -->
    <template v-else>
      <router-view />
    </template>
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'
import Header from './components/Header.vue'
import Footer from './components/Footer.vue'
import AdminLayout from './layouts/AdminLayout.vue'
import StaffLayout from './layouts/StaffLayout.vue'
import ChatboxWidget from './components/ChatboxWidget.vue' // Import Widget Chatbox

const route = useRoute()
</script>