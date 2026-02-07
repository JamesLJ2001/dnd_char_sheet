import { ref } from 'vue'
import { defineStore } from 'pinia'
import axios from 'axios'

export const useCharacterStore = defineStore('character', () => {
  const characters = ref([])
  const loading = ref(false)
  const error = ref(null)

  // 防缓存配置 - 双重保险
  const getNoCacheConfig = () => ({
    headers: {
      'Cache-Control': 'no-cache, no-store, must-revalidate',
      'Pragma': 'no-cache',
      'Expires': '0'
    }
  })

  async function fetchCharacters() {
    loading.value = true
    error.value = null
    try {
      // 添加时间戳 + 防缓存头
      const response = await axios.get(`/api/characters?t=${Date.now()}`, getNoCacheConfig())
      characters.value = response.data
    } catch (err) {
      error.value = err.message
    } finally {
      loading.value = false
    }
  }

  async function updateCharacter(id, payload) {
    try {
      // 添加时间戳参数 + 防缓存头
      const response = await axios.put(
        `/api/characters/${id}?t=${Date.now()}`,
        payload,
        getNoCacheConfig()
      )
      // 直接替换整个对象，简单高效
      const index = characters.value.findIndex(char => char.id === id)
      if (index !== -1) {
        characters.value[index] = response.data
      }
      return response.data
    } catch (err) {
      throw err
    }
  }

  async function adjustHp(charId, delta) {
    try {
      // 添加时间戳参数 + 防缓存头
      const response = await axios.patch(
        `/api/characters/${charId}/hp?t=${Date.now()}`,
        { delta },
        getNoCacheConfig()
      )
      // 直接替换整个对象
      const index = characters.value.findIndex(char => char.id === charId)
      if (index !== -1) {
        characters.value[index] = response.data
      }
      return response.data
    } catch (err) {
      throw err
    }
  }

  async function updateResource(charId, resId, delta) {
    try {
      // 添加时间戳参数 + 防缓存头
      const response = await axios.patch(
        `/api/characters/${charId}/resources/${resId}?t=${Date.now()}`,
        { delta },
        getNoCacheConfig()
      )
      // 直接替换整个对象
      const index = characters.value.findIndex(char => char.id === charId)
      if (index !== -1) {
        characters.value[index] = response.data
      }
      return response.data
    } catch (err) {
      throw err
    }
  }

  async function longRest(charId) {
    try {
      // 添加时间戳参数 + 防缓存头
      const response = await axios.post(
        `/api/characters/${charId}/long-rest?t=${Date.now()}`,
        {},
        getNoCacheConfig()
      )
      // 直接替换整个对象
      const index = characters.value.findIndex(char => char.id === charId)
      if (index !== -1) {
        characters.value[index] = response.data
      }
      return response.data
    } catch (err) {
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
