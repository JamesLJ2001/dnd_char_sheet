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

function getHpPercentage(currentHp, maxHp) {
  if (!maxHp || maxHp === 0) return 0
  return Math.max(0, Math.min(100, (currentHp / maxHp) * 100))
}

function getHpColor(percentage) {
  if (percentage > 60) return 'from-emerald-500 to-emerald-600'
  if (percentage > 30) return 'from-amber-500 to-amber-600'
  return 'from-red-500 to-red-600'
}
</script>

<template>
  <div class="min-h-screen p-8 relative overflow-hidden" style="background: linear-gradient(to bottom right, #020617, #0f172a, #1e1b4b);">

    <!-- 多层背景装饰 -->
    <div class="fixed inset-0 overflow-hidden pointer-events-none">
      <!-- 大型渐变光晕 -->
      <div class="absolute top-0 left-0 w-[600px] h-[600px] rounded-full blur-[120px] animate-pulse" style="background: rgba(217, 119, 6, 0.1);"></div>
      <div class="absolute bottom-0 right-0 w-[600px] h-[600px] rounded-full blur-[120px] animate-pulse" style="background: rgba(147, 51, 234, 0.1); animation-delay: 1s;"></div>
      <div class="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[800px] h-[800px] rounded-full blur-[150px]" style="background: rgba(79, 70, 229, 0.05);"></div>

      <!-- 中型光斑 -->
      <div class="absolute top-1/4 right-1/4 w-64 h-64 rounded-full blur-3xl" style="background: rgba(6, 182, 212, 0.1);"></div>
      <div class="absolute bottom-1/3 left-1/4 w-72 h-72 rounded-full blur-3xl" style="background: rgba(244, 63, 94, 0.1);"></div>

      <!-- 网格纹理 -->
      <div class="absolute inset-0" style="background-image: radial-gradient(circle at 1px 1px, white 1px, transparent 0); background-size: 50px 50px; opacity: 0.02;"></div>

      <!-- 扫描线效果 -->
      <div class="absolute inset-0" style="background: linear-gradient(to bottom, transparent, rgba(15, 23, 42, 0.2), transparent); opacity: 0.3;"></div>
    </div>

    <div class="relative max-w-7xl mx-auto">
      <!-- 标题区 -->
      <div class="text-center mb-16">
        <h1 class="text-6xl font-black text-transparent bg-clip-text bg-gradient-to-r from-amber-400 via-yellow-300 to-amber-500 mb-4 tracking-tight" style="filter: drop-shadow(0 0 20px rgba(251, 191, 36, 0.3));">
          ⚔️ 角色大厅 ⚔️
        </h1>
        <p class="text-xl text-gray-400 font-medium">选择你的英雄，开启史诗冒险</p>
      </div>

      <!-- 加载状态 -->
      <div v-if="characterStore.loading" class="text-center py-32">
        <div class="inline-block animate-spin rounded-full h-16 w-16 border-4 border-amber-500 border-t-transparent"></div>
        <p class="mt-6 text-gray-400 text-lg">加载中...</p>
      </div>

      <!-- 错误状态 -->
      <div v-else-if="characterStore.error" class="text-center py-32">
        <div class="text-6xl mb-4">⚠️</div>
        <p class="text-red-400 text-xl">加载失败: {{ characterStore.error }}</p>
      </div>

      <!-- 角色卡片网格 -->
      <div v-else class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-6">
        <div
          v-for="character in characterStore.characters"
          :key="character.id"
          @click="selectCharacter(character.id)"
          class="group relative bg-gradient-to-br from-slate-800/80 to-slate-900/80 backdrop-blur-xl rounded-2xl p-6 cursor-pointer transform transition-all duration-500 hover:scale-105 hover:shadow-2xl border border-slate-700/50 hover:border-amber-500/50 overflow-hidden"
          style="box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);"
        >
          <!-- 悬浮光效 -->
          <div class="absolute inset-0 bg-gradient-to-br from-amber-500/0 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-500"></div>

          <div class="relative">
            <!-- 职业图标 -->
            <div class="flex justify-center mb-4">
              <div
                :class="[
                  'w-20 h-20 rounded-2xl bg-gradient-to-br flex items-center justify-center text-4xl shadow-lg transform group-hover:scale-110 transition-transform duration-300',
                  getClassColor(character.dndClass)
                ]"
              >
                {{ getClassIcon(character.dndClass) }}
              </div>
            </div>

            <!-- 角色信息 -->
            <h2 class="text-2xl font-black text-white mb-1 text-center group-hover:text-amber-400 transition-colors">
              {{ character.name }}
            </h2>
            <div class="text-center mb-4">
              <span class="inline-block px-3 py-1 bg-gradient-to-r from-amber-600 to-amber-700 rounded-full text-sm font-bold text-white shadow-md">
                {{ character.dndClass }}
              </span>
              <span class="mx-2 text-gray-500">•</span>
              <span class="text-amber-400 font-bold">Lv.{{ character.level }}</span>
            </div>

            <!-- HP 进度条 -->
            <div class="mb-4">
              <div class="flex justify-between text-sm mb-2">
                <span class="text-gray-400 font-medium">HP</span>
                <span class="text-white font-bold">{{ character.currentHp }} / {{ character.maxHp }}</span>
              </div>
              <div class="w-full bg-slate-700/50 rounded-full h-3 overflow-hidden backdrop-blur-sm">
                <div
                  :class="[
                    'h-full rounded-full transition-all duration-700 shadow-lg',
                    getHpColor(getHpPercentage(character.currentHp, character.maxHp))
                  ]"
                  :style="{ width: getHpPercentage(character.currentHp, character.maxHp) + '%' }"
                ></div>
              </div>
            </div>

            <!-- 底部信息 -->
            <div class="flex justify-between items-center text-sm border-t border-slate-700/50 pt-4">
              <div class="flex items-center gap-2 text-gray-400">
                <span>🛡️</span>
                <span class="font-bold">AC {{ character.armorClass }}</span>
              </div>
              <div class="text-amber-500 font-bold">
                查看详情 →
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部装饰 -->
      <div class="text-center mt-16 text-gray-500 text-sm">
        <p>🎲 Dungeons & Dragons Character Sheet v2.0</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
</style>
