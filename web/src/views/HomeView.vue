<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCharacterStore } from '../stores/character'

const router = useRouter()
const characterStore = useCharacterStore()

onMounted(() => {
  characterStore.fetchCharacters()
})

function selectCharacter(id) {
  router.push(`/character/${id}`)
}

function getAvatarColor(id) {
  const colors = [
    'from-red-600 to-red-800',
    'from-blue-600 to-blue-800',
    'from-green-600 to-green-800',
    'from-purple-600 to-purple-800'
  ]
  return colors[(id - 1) % colors.length]
}

function getHpPercentage(currentHp, maxHp) {
  if (!maxHp || maxHp === 0) return 0
  return Math.max(0, Math.min(100, (currentHp / maxHp) * 100))
}

function getHpColor(percentage) {
  if (percentage > 60) return 'bg-green-600'
  if (percentage > 30) return 'bg-yellow-600'
  return 'bg-red-600'
}
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 p-8">
    <div class="max-w-7xl mx-auto">
      <h1 class="text-5xl font-bold text-center mb-4 text-amber-500" style="text-shadow: 2px 2px 4px rgba(0,0,0,0.5);">
        角色大厅
      </h1>
      <p class="text-center text-gray-400 mb-12 text-lg">选择你的英雄开始冒险</p>

      <div v-if="characterStore.loading" class="text-center text-gray-400 text-xl py-20">
        加载中...
      </div>

      <div v-else-if="characterStore.error" class="text-center text-red-500 text-xl py-20">
        加载失败: {{ characterStore.error }}
      </div>

      <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
        <div
          v-for="character in characterStore.characters"
          :key="character.id"
          @click="selectCharacter(character.id)"
          class="bg-gradient-to-b from-gray-800 to-gray-900 rounded-xl p-6 cursor-pointer transform transition-all duration-300 hover:scale-105 hover:shadow-2xl border-2 border-amber-700 hover:border-amber-500"
          style="box-shadow: 0 4px 6px rgba(0, 0, 0, 0.3);"
        >
          <div class="flex flex-col items-center">
            <div
              :class="[
                'w-24 h-24 rounded-full bg-gradient-to-br mb-4 flex items-center justify-center text-4xl font-bold text-white border-4 border-amber-600',
                getAvatarColor(character.id)
              ]"
            >
              {{ character.name.charAt(0) }}
            </div>

            <h2 class="text-2xl font-bold text-amber-400 mb-2">{{ character.name }}</h2>
            <div class="text-gray-400 mb-4">
              <span class="text-lg">{{ character.dndClass }}</span>
              <span class="mx-2">•</span>
              <span class="text-lg">Lv.{{ character.level }}</span>
            </div>

            <div class="w-full">
              <div class="flex justify-between text-sm text-gray-400 mb-1">
                <span>HP</span>
                <span>{{ character.currentHp }} / {{ character.maxHp }}</span>
              </div>
              <div class="w-full bg-gray-700 rounded-full h-4 overflow-hidden">
                <div
                  :class="[
                    'h-full transition-all duration-500',
                    getHpColor(getHpPercentage(character.currentHp, character.maxHp))
                  ]"
                  :style="{ width: getHpPercentage(character.currentHp, character.maxHp) + '%' }"
                ></div>
              </div>
            </div>

            <div class="mt-4 text-sm text-gray-500">
              <span>AC: {{ character.armorClass }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
</style>
