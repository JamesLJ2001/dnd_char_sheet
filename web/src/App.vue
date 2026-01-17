<script setup>
import { RouterView } from 'vue-router'
</script>

<template>
  <div class="app-container">
    <RouterView />
  </div>
</template>

<style scoped>
/* 简单的全屏容器 */
.app-container {
  width: 100vw;
  min-height: 100vh;
  background-color: #181818; /* 确保背景是深色的 */
  color: white;
}
</style>
