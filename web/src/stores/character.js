import { ref } from 'vue'
import { defineStore } from 'pinia'
import axios from 'axios'

export const useCharacterStore = defineStore('character', () => {
  const characters = ref([])
  const loading = ref(false)
  const error = ref(null)

  async function fetchCharacters() {
    loading.value = true
    error.value = null
    try {
      const response = await axios.get('/api/characters')
      characters.value = response.data
    } catch (err) {
      error.value = err.message
      console.error('Failed to fetch characters:', err)
    } finally {
      loading.value = false
    }
  }

  async function updateCharacter(id, payload) {
    try {
      // 添加时间戳参数，强制破解 iOS Safari 缓存
      const response = await axios.put(`/api/characters/${id}?t=${Date.now()}`, payload)
      // 直接替换整个对象，简单高效
      const index = characters.value.findIndex(char => char.id === id)
      if (index !== -1) {
        characters.value[index] = response.data
        console.log('✅ [Store] Updated character:', response.data.name, response.data)
      }
      return response.data
    } catch (err) {
      console.error('❌ [Store] Failed to update character:', err)
      throw err
    }
  }

  async function adjustHp(charId, delta) {
    try {
      // 添加时间戳参数，强制破解 iOS Safari 缓存
      const response = await axios.patch(`/api/characters/${charId}/hp?t=${Date.now()}`, { delta })
      // 直接替换整个对象
      const index = characters.value.findIndex(char => char.id === charId)
      if (index !== -1) {
        characters.value[index] = response.data
        console.log('✅ [Store] HP adjusted:', response.data.currentHp, '/', response.data.maxHp)
      }
      return response.data
    } catch (err) {
      console.error('❌ [Store] Failed to adjust HP:', err)
      throw err
    }
  }

  async function updateResource(charId, resId, delta) {
    try {
      // 添加时间戳参数，强制破解 iOS Safari 缓存
      const response = await axios.patch(`/api/characters/${charId}/resources/${resId}?t=${Date.now()}`, { delta })
      // 直接替换整个对象
      const index = characters.value.findIndex(char => char.id === charId)
      if (index !== -1) {
        characters.value[index] = response.data
        console.log('✅ [Store] Resource updated')
      }
      return response.data
    } catch (err) {
      console.error('❌ [Store] Failed to update resource:', err)
      throw err
    }
  }

  async function longRest(charId) {
    try {
      // 添加时间戳参数，强制破解 iOS Safari 缓存
      const response = await axios.post(`/api/characters/${charId}/long-rest?t=${Date.now()}`)
      // 直接替换整个对象
      const index = characters.value.findIndex(char => char.id === charId)
      if (index !== -1) {
        characters.value[index] = response.data
        console.log('✅ [Store] Long rest completed')
      }
      return response.data
    } catch (err) {
      console.error('❌ [Store] Failed to long rest:', err)
      throw err
    }
  }

  function getCharacterById(id) {
    return characters.value.find(char => char.id === id)
  }

  return {
    characters,
    loading,
    error,
    fetchCharacters,
    updateCharacter,
    adjustHp,
    updateResource,
    longRest,
    getCharacterById
  }
})
