<template>
  <div class="modal-overlay" v-if="open" @click.self="$emit('close')">
    <div class="modal-box">
      <h3>Xác nhận đặt sân</h3>
      <p>Nội dung modal sẽ hoàn thiện ở bước làm trang Đặt sân.</p>
      <button class="btn btn-primary" @click="$emit('close')">Đóng</button>
    </div>
  </div>
</template>

<script setup>
defineProps({ open: Boolean })
defineEmits(['close'])
</script>

<style scoped>
.modal-overlay {
  position: fixed; inset: 0; background: rgba(10, 37, 64, 0.5);
  display: flex; align-items: center; justify-content: center; z-index: 200;
}
.modal-box {
  background: var(--white); border-radius: var(--radius-lg); padding: 28px; width: 360px;
}
</style>