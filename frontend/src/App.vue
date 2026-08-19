<template>
  <div id="app-root">
    <!-- User Layout -->
    <template v-if="route.meta.layout === 'user'">
      <Header />
      <main>
        <router-view />
      </main>
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

const route = useRoute()
</script>