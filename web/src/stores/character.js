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
      const response = await axios.put(`/api/characters/${id}`, payload);      // 更新本地状态
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

  return {
    characters,
    loading,
    error,
    fetchCharacters,
    updateCharacter
  }
})
