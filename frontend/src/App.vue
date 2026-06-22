<template>
  <div id="app-root">
    <Header v-if="route.meta.layout === 'user'" />
    <main>
      <router-view />
    </main>
    <Footer v-if="route.meta.layout === 'user'" />
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'
import Header from './components/Header.vue'
import Footer from './components/Footer.vue'

const route = useRoute()
</script>