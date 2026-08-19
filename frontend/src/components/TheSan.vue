<template>
  <article class="the-san">
    <div class="the-san__media" :style="{ background: mau }">
      <span class="the-san__loai">{{ loai }}</span>
      <span class="the-san__rating">★ {{ rating }}</span>
    </div>
    <div class="the-san__body">
      <h3 class="the-san__ten">{{ ten }}</h3>
      <p class="the-san__diachi">{{ diaChi }}</p>
      <div class="the-san__footer">
        <p class="the-san__gia"><strong>{{ gia }}</strong>đ/giờ</p>
        <router-link :to="`/dat-san/${id}`" class="btn btn-ghost-navy btn--sm">Đặt sân</router-link>
      </div>
    </div>
  </article>
</template>

<script setup>
defineProps({
  id: { type: [String, Number], required: true },
  ten: { type: String, required: true },
  diaChi: { type: String, required: true },
  loai: { type: String, default: 'Sân 5' },
  rating: { type: String, default: '4.8' },
  gia: { type: String, required: true },
  mau: { type: String, default: 'linear-gradient(135deg, #1f7a34, #2c9b41)' }
})
</script>

<style scoped>
.the-san {
  border-radius: var(--radius-md);
  overflow: hidden;
  box-shadow: var(--shadow-card);
  background: var(--white);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.the-san:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-card-hover);
}

.the-san__media {
  height: 150px;
  position: relative;
  padding: 16px;
}

.the-san__loai {
  background: rgba(255, 255, 255, 0.92);
  color: var(--navy-900);
  font-size: 12px;
  font-weight: 700;
  padding: 4px 11px;
  border-radius: 999px;
}

.the-san__rating {
  position: absolute;
  bottom: 16px;
  right: 16px;
  color: var(--white);
  font-size: 13px;
  font-weight: 600;
}

.the-san__body {
  padding: 18px 20px 22px;
}

.the-san__ten {
  font-size: 16.5px;
  font-weight: 700;
  color: var(--navy-900);
}

.the-san__diachi {
  margin-top: 6px;
  font-size: 13px;
  color: var(--gray-600);
}

.the-san__footer {
  margin-top: 18px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.the-san__gia {
  font-size: 13.5px;
  color: var(--gray-600);
}

.the-san__gia strong {
  font-size: 17px;
  color: var(--navy-900);
}
</style>