<template>
  <canvas ref="canvasRef" class="particle-canvas"></canvas>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useParticleBg } from '@/composables/useParticleBg'

const canvasRef = ref<HTMLCanvasElement | null>(null)
useParticleBg(canvasRef)
</script>

<style scoped>
.particle-canvas {
  position: fixed; inset: 0; z-index: 0; pointer-events: none;
}
</style>
