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
      const response = await axios.put(`/api/characters/${id}`, payload)
      // 更新本地状态
      const index = characters.value.findIndex(char => char.id === id)
      if (index !== -1) {
        characters.value[index] = { ...characters.value[index], ...response.data }
      }
      return response.data
    } catch (err) {
      console.error('Failed to update character:', err)
      throw err
    }
  }

  async function adjustHp(charId, delta) {
    try {
      const response = await axios.patch(`/api/characters/${charId}/hp`, { delta })
      // 更新本地状态
      const index = characters.value.findIndex(char => char.id === charId)
      if (index !== -1) {
        characters.value[index] = { ...characters.value[index], ...response.data }
      }
      return response.data
    } catch (err) {
      console.error('Failed to adjust HP:', err)
      throw err
    }
  }

  async function updateResource(charId, resId, delta) {
    try {
      const response = await axios.patch(`/api/characters/${charId}/resources/${resId}`, { delta })
      // 更新本地状态
      const index = characters.value.findIndex(char => char.id === charId)
      if (index !== -1) {
        characters.value[index] = { ...characters.value[index], ...response.data }
      }
      return response.data
    } catch (err) {
      console.error('Failed to update resource:', err)
      throw err
    }
  }

  async function longRest(charId) {
    try {
      const response = await axios.post(`/api/characters/${charId}/long-rest`)
      // 更新本地状态
      const index = characters.value.findIndex(char => char.id === charId)
      if (index !== -1) {
        characters.value[index] = { ...characters.value[index], ...response.data }
      }
      return response.data
    } catch (err) {
      console.error('Failed to long rest:', err)
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
