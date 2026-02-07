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

function getClassColor(dndClass) {
  const colors = {
    'Paladin': 'from-yellow-600 via-amber-500 to-yellow-700',
    'Duskblade': 'from-purple-600 via-indigo-500 to-purple-700',
    'Warlock': 'from-red-600 via-rose-500 to-red-700',
    'Bard': 'from-blue-600 via-cyan-500 to-blue-700'
  }
  return colors[dndClass] || 'from-gray-600 to-gray-800'
}

function getClassIcon(dndClass) {
  const icons = {
    'Paladin': '⚔️',
    'Duskblade': '🗡️',
    'Warlock': '🔮',
    'Bard': '🎸'
  }
  return icons[dndClass] || '⭐'
}

function getClassName(dndClass) {
  const names = {
    'Paladin': '圣武士',
    'Duskblade': '暮刃',
    'Warlock': '邪术师',
    'Bard': '吟游诗人'
  }
  return names[dndClass] || dndClass
}

function getHpPercentage(currentHp, maxHp) {
  if (!maxHp || maxHp === 0) return 0
  return Math.max(0, Math.min(100, (currentHp / maxHp) * 100))
}

function getHpColor(percentage) {
  if (percentage > 60) return 'from-emerald-500 to-emerald-600'
  if (percentage > 30) return 'from-amber-500 to-amber-600'
  return 'from-red-500 to-red-600'
}

function getHpBarColor(percentage) {
  if (percentage > 60) return 'linear-gradient(to right, #2d5a27, #1a3d1a)'
  if (percentage > 30) return 'linear-gradient(to right, #5a4a20, #3d3515)'
  return 'linear-gradient(to right, #5a2020, #3d1515)'
}
</script>

<template>
  <div class="min-h-screen p-8 dungeon-bg">

    <div class="relative max-w-7xl mx-auto">
      <!-- 标题区 -->
      <div class="text-center mb-16">
        <div class="inline-block iron-header px-12 py-6 mb-4" style="border: 4px solid #4a4a4a; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.5);">
          <h1 class="text-5xl font-black mb-2 font-gothic" style="color: #f4e4bc; text-shadow: 3px 3px 6px rgba(0, 0, 0, 0.7), 0 0 30px rgba(255, 215, 0, 0.3);">
            ⚔️ 角色大厅 ⚔️
          </h1>
        </div>
        <p class="text-xl font-heavy font-heading" style="color: #f4e4bc; text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.6);">选择你的英雄，开启史诗冒险</p>
      </div>

      <!-- 加载状态 -->
      <div v-if="characterStore.loading" class="text-center py-32">
        <div class="inline-block p-6 rounded-2xl wood-texture iron-border" style="min-width: 200px;">
          <div class="text-2xl font-black mb-3 parchment-text" style="font-family: 'Times New Roman', serif;">加载中...</div>
          <div class="animate-spin rounded-full h-12 w-12 border-4 mx-auto" style="border-color: #f4e4bc; border-top-color: transparent;"></div>
        </div>
      </div>

      <!-- 错误状态 -->
      <div v-else-if="characterStore.error" class="text-center py-32">
        <div class="text-6xl mb-4">⚠️</div>
        <p class="text-xl font-bold" style="color: #8b4513;">加载失败: {{ characterStore.error }}</p>
      </div>

      <!-- 角色卡片网格 -->
      <div v-else class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-6">
        <div
          v-for="character in characterStore.characters"
          :key="character.id"
          @click="selectCharacter(character.id)"
          class="group relative wood-texture iron-border iron-rivet p-6 cursor-pointer transform transition-all duration-500 hover:scale-105"
          style="box-shadow: 0 8px 32px rgba(0, 0, 0, 0.6); min-height: 320px;"
        >
          <div class="relative h-full flex flex-col">
            <!-- 职业图标 -->
            <div class="flex justify-center mb-4">
              <div
                :class="[
                  'w-20 h-20 rounded-lg flex items-center justify-center text-4xl shadow-lg transform group-hover:scale-110 transition-transform duration-300 border-4 border-gray-500',
                  getClassColor(character.dndClass)
                ]"
                style="background: linear-gradient(135deg, #5a5a5a 0%, #3a3a3a 100%);"
              >
                {{ getClassIcon(character.dndClass) }}
              </div>
            </div>

            <!-- 角色信息 -->
            <h2 class="text-3xl font-black mb-2 text-center font-title" style="color: #f4e4bc; text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.6);">
              {{ character.name }}
            </h2>
            <div class="text-center mb-4">
              <span class="inline-block px-4 py-2 rounded text-sm font-bold" style="background: linear-gradient(180deg, #5a5a5a 0%, #3a3a3a 100%); color: #f4e4bc; border: 2px solid #4a4a4a;">
                {{ getClassName(character.dndClass) }}
              </span>
              <div class="mt-2 text-amber-400 font-bold text-lg">等级 {{ character.level }}</div>
            </div>

            <!-- HP 进度条 -->
            <div class="mb-4 flex-grow">
              <div class="parchment p-3 rounded" style="border: 2px solid #5a4025;">
                <div class="flex justify-between text-sm mb-2">
                  <span class="font-bold parchment-text">生命值</span>
                  <span class="font-black" style="color: #8b4513;">{{ character.currentHp }} / {{ character.maxHp }}</span>
                </div>
                <div class="w-full rounded-full h-4" style="background: #4a3520; border: 2px solid #3a2510;">
                  <div
                    :style="{
                      width: getHpPercentage(character.currentHp, character.maxHp) + '%',
                      background: getHpBarColor(getHpPercentage(character.currentHp, character.maxHp))
                    }"
                    class="h-full rounded-full transition-all duration-700"
                  ></div>
                </div>
              </div>
            </div>

            <!-- 底部信息 -->
            <div class="flex justify-between items-center text-sm border-t-2 pt-4" style="border-color: #5a4025;">
              <div class="flex items-center gap-2">
                <span style="font-size: 1.5rem;">🛡️</span>
                <span class="font-bold parchment-text" style="font-size: 1.1rem;">护甲 {{ character.armorClass }}</span>
              </div>
              <div class="font-bold metal-shine">
                查看详情 →
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部装饰 -->
      <div class="text-center mt-16">
        <p class="text-lg font-heading font-bold" style="color: #f4e4bc; text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.6);">🎲 Dungeons & Dragons 角色表 v2.0</p>
        <p class="text-sm mt-2 font-body" style="color: #c4a777;">中世纪复古风格</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
</style>
