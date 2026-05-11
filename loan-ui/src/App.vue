<template>
  <transition v-if="isAuthenticated" name="page" mode="out-in">
    <AppLayout />
  </transition>
  <router-view v-else v-slot="{ Component }">
    <transition name="page" mode="out-in">
      <component :is="Component" />
    </transition>
  </router-view>
</template>

<script setup lang="ts">
import { onMounted, computed } from 'vue'
import AppLayout from '@/components/global/AppLayout.vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const isAuthenticated = computed(() => authStore.isAuthenticated)

onMounted(() => { authStore.loadFromStorage() })
</script>
