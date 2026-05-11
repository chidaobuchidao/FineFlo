import { defineStore } from 'pinia'
import { ref } from 'vue'

export type ThemeMode = 'dark' | 'light' | 'system'

export const useThemeStore = defineStore('theme', () => {
  const mode = ref<ThemeMode>('dark')

  function init() {
    const stored = localStorage.getItem('theme') as ThemeMode | null
    mode.value = (stored === 'dark' || stored === 'light' || stored === 'system') ? stored : 'dark'
    apply()
  }

  function setMode(m: ThemeMode) {
    mode.value = m
    localStorage.setItem('theme', m)
    apply()
  }

  function apply() {
    document.documentElement.setAttribute('data-theme', mode.value)
  }

  return { mode, init, setMode }
})
