<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useCharacterStore } from '../stores/character'

const route = useRoute()
const router = useRouter()
const characterStore = useCharacterStore()

const characterId = computed(() => parseInt(route.params.id))
const character = ref(null)
const loading = ref(true)

// 六维属性数据
const stats = computed(() => {
  if (!character.value) return []
  return [
    { name: '力量', nameEn: 'STR', value: character.value.strength, modifier: calculateModifier(character.value.strength), color: 'from-red-500 to-rose-600' },
    { name: '敏捷', nameEn: 'DEX', value: character.value.dexterity, modifier: calculateModifier(character.value.dexterity), color: 'from-green-500 to-emerald-600' },
    { name: '体质', nameEn: 'CON', value: character.value.constitution, modifier: calculateModifier(character.value.constitution), color: 'from-orange-500 to-amber-600' },
    { name: '智力', nameEn: 'INT', value: character.value.intelligence, modifier: calculateModifier(character.value.intelligence), color: 'from-blue-500 to-cyan-600' },
    { name: '感知', nameEn: 'WIS', value: character.value.wisdom, modifier: calculateModifier(character.value.wisdom), color: 'from-purple-500 to-violet-600' },
    { name: '魅力', nameEn: 'CHA', value: character.value.charisma, modifier: calculateModifier(character.value.charisma), color: 'from-pink-500 to-rose-600' }
  ]
})

// 计算属性调整值
function calculateModifier(value) {
  const modifier = Math.floor((value - 10) / 2)
  return modifier >= 0 ? `+${modifier}` : `${modifier}`
}

// HP 百分比
const hpPercent = computed(() => {
  if (!character.value) return 0
  return (character.value.currentHp / character.value.maxHp) * 100
})

// HP 颜色
const hpColor = computed(() => {
  const pct = hpPercent.value
  if (pct > 60) return 'from-emerald-500 to-emerald-600'
  if (pct > 30) return 'from-amber-500 to-amber-600'
  return 'from-red-500 to-rose-600'
})

// 格式化先攻值
const formattedInitiative = computed(() => {
  if (!character.value) return '+0'
  return character.value.initiative >= 0 ? `+${character.value.initiative}` : `${character.value.initiative}`
})

// HP 调整
async function adjustHp(delta) {
  try {
    await characterStore.adjustHp(characterId.value, delta)
    character.value = characterStore.getCharacterById(characterId.value)
  } catch (err) {
    console.error('Failed to adjust HP:', err)
  }
}

// 资源使用
async function useResource(resource) {
  if (resource.currentValue <= 0) return
  try {
    await characterStore.updateResource(characterId.value, resource.id, -1)
    character.value = characterStore.getCharacterById(characterId.value)
  } catch (err) {
    console.error('Failed to use resource:', err)
  }
}

// 资源恢复
async function recoverResource(resource) {
  if (resource.currentValue >= resource.maxValue) return
  try {
    await characterStore.updateResource(characterId.value, resource.id, 1)
    character.value = characterStore.getCharacterById(characterId.value)
  } catch (err) {
    console.error('Failed to recover resource:', err)
  }
}

// 长休
async function longRest() {
  try {
    await characterStore.longRest(characterId.value)
    character.value = characterStore.getCharacterById(characterId.value)
  } catch (err) {
    console.error('Failed to long rest:', err)
  }
}

// 资源百分比
function getResourcePercent(resource) {
  return (resource.currentValue / resource.maxValue) * 100
}

// 资源颜色
function getResourceColor(resource) {
  const percent = getResourcePercent(resource)
  if (percent > 60) return 'from-blue-500 to-cyan-600'
  if (percent > 30) return 'from-amber-500 to-orange-600'
  return 'from-red-500 to-rose-600'
}

// 等级调整
async function adjustLevel(delta) {
  if (!character.value) return
  const newLevel = character.value.level + delta
  if (newLevel < 1 || newLevel > 20) return

  try {
    await characterStore.updateCharacter(characterId.value, { level: newLevel })
    character.value = characterStore.getCharacterById(characterId.value)
  } catch (err) {
    console.error('Failed to adjust level:', err)
  }
}

// 属性调整
async function adjustStat(statName, delta) {
  if (!character.value) return
  const currentValue = character.value[statName]
  const newValue = currentValue + delta
  if (newValue < 1 || newValue > 30) return

  try {
    await characterStore.updateCharacter(characterId.value, { [statName]: newValue })
    character.value = characterStore.getCharacterById(characterId.value)
  } catch (err) {
    console.error('Failed to adjust stat:', err)
  }
}

onMounted(async () => {
  await characterStore.fetchCharacters()
  character.value = characterStore.getCharacterById(characterId.value)
  loading.value = false

  if (!character.value) {
    router.push('/')
  }
})
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-950 via-slate-900 to-slate-950 p-4 md:p-6">
    <!-- 背景装饰 -->
    <div class="fixed inset-0 overflow-hidden pointer-events-none">
      <div class="absolute top-1/4 left-0 w-96 h-96 bg-amber-500/5 rounded-full blur-3xl"></div>
      <div class="absolute bottom-1/4 right-0 w-96 h-96 bg-purple-500/5 rounded-full blur-3xl"></div>
    </div>

    <div class="relative max-w-7xl mx-auto">
      <!-- 返回按钮 -->
      <button
        @click="router.push('/')"
        class="mb-6 px-4 py-2 bg-gradient-to-r from-slate-800 to-slate-700 hover:from-slate-700 hover:to-slate-600 rounded-xl text-white font-medium transition-all duration-300 shadow-lg border border-slate-600 hover:border-amber-500/50 flex items-center gap-2"
      >
        <span>←</span>
        <span>返回大厅</span>
      </button>

      <!-- 加载状态 -->
      <div v-if="loading" class="text-center text-gray-400 text-xl py-32">
        <div class="inline-block animate-spin rounded-full h-16 w-16 border-4 border-amber-500 border-t-transparent mb-6"></div>
        <p>加载中...</p>
      </div>

      <!-- 角色未找到 -->
      <div v-else-if="!character" class="text-center text-red-400 text-xl py-32">
        <div class="text-6xl mb-4">⚠️</div>
        <p>角色未找到</p>
      </div>

      <!-- 三栏仪表盘布局 -->
      <div v-else class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- 左栏 - 生存状态 (Survival) -->
        <div class="survival-panel bg-gradient-to-br from-slate-800/90 to-slate-900/90 backdrop-blur-xl rounded-2xl p-6 shadow-2xl border border-slate-700/50">
          <!-- 头像区域 -->
          <div class="flex flex-col items-center mb-6">
            <div class="relative mb-4">
              <img
                :src="character.imageUrl"
                :alt="character.name"
                class="w-32 h-32 rounded-2xl object-cover border-4 border-amber-500/50 shadow-2xl"
              >
              <div class="absolute -bottom-2 -right-2 w-10 h-10 bg-gradient-to-br from-amber-500 to-amber-600 rounded-full flex items-center justify-center text-lg font-bold shadow-lg border-2 border-amber-400">
                {{ character.level }}
              </div>
            </div>

            <!-- 基本信息 -->
            <h2 class="text-3xl font-black text-white mb-1">{{ character.name }}</h2>
            <div class="flex items-center gap-2 mb-6">
              <span class="px-3 py-1 bg-gradient-to-r from-amber-600 to-amber-700 rounded-full text-sm font-bold text-white shadow-md">
                {{ character.dndClass }}
              </span>
              <span class="text-gray-400 text-sm">{{ character.race }}</span>
            </div>

            <!-- HP 血条 -->
            <div class="w-full mb-6">
              <div class="flex justify-between items-center mb-3">
                <span class="text-lg font-bold text-white flex items-center gap-2">
                  <span class="text-2xl">❤️</span>
                  <span>生命值</span>
                </span>
                <div class="flex gap-2">
                  <button
                    @click="adjustHp(-1)"
                    class="w-10 h-10 bg-gradient-to-br from-red-600 to-rose-700 hover:from-red-500 hover:to-rose-600 rounded-xl text-white font-bold text-lg shadow-lg transform hover:scale-110 transition-all duration-200"
                  >
                    −
                  </button>
                  <button
                    @click="adjustHp(1)"
                    class="w-10 h-10 bg-gradient-to-br from-emerald-600 to-green-700 hover:from-emerald-500 hover:to-green-600 rounded-xl text-white font-bold text-lg shadow-lg transform hover:scale-110 transition-all duration-200"
                  >
                    +
                  </button>
                </div>
              </div>
              <div class="text-2xl text-center text-white font-black mb-3">
                {{ character.currentHp }} <span class="text-gray-500 text-lg">/ {{ character.maxHp }}</span>
              </div>
              <div class="w-full bg-slate-700/50 rounded-full h-5 overflow-hidden backdrop-blur-sm shadow-inner">
                <div
                  :class="['h-full rounded-full transition-all duration-700 shadow-lg', hpColor]"
                  :style="{ width: hpPercent + '%' }"
                ></div>
              </div>
            </div>

            <!-- 防御属性 -->
            <div class="defense-grid grid grid-cols-3 gap-3 w-full">
              <div class="bg-gradient-to-br from-slate-700/50 to-slate-800/50 rounded-xl p-4 text-center border border-slate-600/30 backdrop-blur-sm">
                <div class="text-2xl mb-1">🛡️</div>
                <div class="text-xs text-gray-400 mb-1">护甲</div>
                <div class="text-xl font-black text-amber-400">AC {{ character.armorClass }}</div>
              </div>
              <div class="bg-gradient-to-br from-slate-700/50 to-slate-800/50 rounded-xl p-4 text-center border border-slate-600/30 backdrop-blur-sm">
                <div class="text-2xl mb-1">⚡</div>
                <div class="text-xs text-gray-400 mb-1">先攻</div>
                <div class="text-xl font-black text-cyan-400">{{ formattedInitiative }}</div>
              </div>
              <div class="bg-gradient-to-br from-slate-700/50 to-slate-800/50 rounded-xl p-4 text-center border border-slate-600/30 backdrop-blur-sm">
                <div class="text-2xl mb-1">👟</div>
                <div class="text-xs text-gray-400 mb-1">速度</div>
                <div class="text-xl font-black text-green-400">{{ character.speed }} ft</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 中栏 - 战斗与属性 (Combat & Stats) -->
        <div class="combat-panel bg-gradient-to-br from-slate-800/90 to-slate-900/90 backdrop-blur-xl rounded-2xl p-6 shadow-2xl border border-slate-700/50">
          <!-- 主武器卡片 -->
          <div class="bg-gradient-to-r from-amber-900/50 via-amber-800/50 to-amber-900/50 rounded-xl p-5 mb-6 border border-amber-700/30 shadow-lg">
            <div class="flex items-center gap-3 mb-3">
              <div class="text-3xl">⚔️</div>
              <h3 class="text-lg font-bold text-amber-300">主武器</h3>
            </div>
            <p class="text-white font-medium leading-relaxed">{{ character.mainWeapon }}</p>
          </div>

          <!-- 六维属性 -->
          <div class="mb-4 flex items-center gap-2">
            <div class="text-xl">📊</div>
            <h3 class="text-xl font-bold text-amber-400">属性</h3>
          </div>
          <div class="stats-grid grid grid-cols-3 gap-3">
            <div
              v-for="stat in stats"
              :key="stat.name"
              class="group bg-gradient-to-br from-slate-700/50 to-slate-800/50 rounded-xl p-4 text-center border border-slate-600/30 backdrop-blur-sm hover:border-slate-500/50 transition-all duration-300"
            >
              <div class="text-xs text-gray-400 uppercase font-bold mb-1">{{ stat.nameEn }}</div>
              <div
                :class="['text-3xl font-black mb-1 bg-gradient-to-r bg-clip-text text-transparent', stat.color]"
              >
                {{ stat.modifier }}
              </div>
              <div class="text-xs text-gray-500 mb-2">({{ stat.name }} {{ stat.value }})</div>
              <div class="text-xs text-amber-400 mb-2">检定 {{ stat.modifier }}</div>
              <div class="flex gap-1 justify-center">
                <button
                  @click="adjustStat(stat.nameEn === 'STR' ? 'strength' : stat.nameEn === 'DEX' ? 'dexterity' : stat.nameEn === 'CON' ? 'constitution' : stat.nameEn === 'INT' ? 'intelligence' : stat.nameEn === 'WIS' ? 'wisdom' : 'charisma', -1)"
                  :disabled="stat.value <= 1"
                  class="flex-1 bg-gradient-to-br from-red-600/80 to-rose-700/80 hover:from-red-500/80 hover:to-rose-600/80 disabled:from-slate-600/50 disabled:to-slate-700/50 disabled:cursor-not-allowed py-1 rounded text-white font-bold text-xs shadow-md transition-all duration-200 disabled:opacity-50"
                >
                  −
                </button>
                <button
                  @click="adjustStat(stat.nameEn === 'STR' ? 'strength' : stat.nameEn === 'DEX' ? 'dexterity' : stat.nameEn === 'CON' ? 'constitution' : stat.nameEn === 'INT' ? 'intelligence' : stat.nameEn === 'WIS' ? 'wisdom' : 'charisma', 1)"
                  :disabled="stat.value >= 30"
                  class="flex-1 bg-gradient-to-br from-emerald-600/80 to-green-700/80 hover:from-emerald-500/80 hover:to-green-600/80 disabled:from-slate-600/50 disabled:to-slate-700/50 disabled:cursor-not-allowed py-1 rounded text-white font-bold text-xs shadow-md transition-all duration-200 disabled:opacity-50"
                >
                  +
                </button>
              </div>
            </div>
          </div>

          <!-- 附加信息 -->
          <div class="mt-6 bg-gradient-to-br from-slate-700/30 to-slate-800/30 rounded-xl p-4 border border-slate-600/30">
            <div class="flex justify-between items-center">
              <span class="text-gray-400 font-medium">等级</span>
              <div class="flex items-center gap-3">
                <button
                  @click="adjustLevel(-1)"
                  :disabled="character.level <= 1"
                  class="w-8 h-8 bg-gradient-to-br from-red-600 to-rose-700 hover:from-red-500 hover:to-rose-600 disabled:from-slate-600 disabled:to-slate-700 disabled:cursor-not-allowed rounded-lg text-white font-bold text-sm shadow-md transition-all duration-200 disabled:opacity-50"
                >
                  −
                </button>
                <span class="text-2xl font-black text-amber-400">Lv. {{ character.level }}</span>
                <button
                  @click="adjustLevel(1)"
                  :disabled="character.level >= 20"
                  class="w-8 h-8 bg-gradient-to-br from-emerald-600 to-green-700 hover:from-emerald-500 hover:to-green-600 disabled:from-slate-600 disabled:to-slate-700 disabled:cursor-not-allowed rounded-lg text-white font-bold text-sm shadow-md transition-all duration-200 disabled:opacity-50"
                >
                  +
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- 右栏 - 资源管理 (Resource Manager) -->
        <div class="resource-panel bg-gradient-to-br from-slate-800/90 to-slate-900/90 backdrop-blur-xl rounded-2xl p-6 shadow-2xl border border-slate-700/50">
          <!-- 长休按钮 -->
          <button
            @click="longRest"
            class="w-full mb-6 bg-gradient-to-r from-indigo-600 via-purple-600 to-pink-600 hover:from-indigo-500 hover:via-purple-500 hover:to-pink-500 text-white font-black py-4 px-6 rounded-xl transition-all duration-300 shadow-lg border border-indigo-400/50 hover:shadow-2xl transform hover:scale-105 flex items-center justify-center gap-3"
          >
            <span class="text-2xl">🌙</span>
            <span class="text-lg">长休 (Long Rest)</span>
          </button>

          <!-- 标题 -->
          <div class="flex items-center gap-2 mb-6">
            <div class="text-xl">💎</div>
            <h3 class="text-xl font-bold text-amber-400">职业资源</h3>
          </div>

          <!-- 资源列表 -->
          <div class="space-y-4">
            <div
              v-for="resource in character.resources"
              :key="resource.id"
              class="resource-card bg-gradient-to-br from-slate-700/50 to-slate-800/50 rounded-xl p-4 border border-slate-600/30 backdrop-blur-sm hover:border-slate-500/50 transition-all duration-300"
            >
              <div class="flex justify-between items-center mb-3">
                <span class="font-bold text-white">{{ resource.resourceName }}</span>
                <div class="flex items-center gap-2">
                  <span class="text-sm text-gray-400">{{ resource.currentValue }}</span>
                  <span class="text-sm text-gray-500">/</span>
                  <span class="text-sm text-gray-400">{{ resource.maxValue }}</span>
                </div>
              </div>

              <!-- 进度条 -->
              <div class="w-full bg-slate-600/50 rounded-full h-3 mb-4 overflow-hidden backdrop-blur-sm">
                <div
                  :class="['h-full rounded-full transition-all duration-700 shadow-md', getResourceColor(resource)]"
                  :style="{ width: getResourcePercent(resource) + '%' }"
                ></div>
              </div>

              <!-- 操作按钮 -->
              <div class="flex gap-2">
                <button
                  @click="useResource(resource)"
                  :disabled="resource.currentValue <= 0"
                  class="flex-1 bg-gradient-to-r from-blue-600 to-cyan-700 hover:from-blue-500 hover:to-cyan-600 disabled:from-slate-600 disabled:to-slate-700 disabled:cursor-not-allowed py-3 rounded-xl text-sm font-bold text-white shadow-md transition-all duration-200 disabled:opacity-50"
                >
                  使用 (-1)
                </button>
                <button
                  @click="recoverResource(resource)"
                  :disabled="resource.currentValue >= resource.maxValue"
                  class="flex-1 bg-gradient-to-r from-emerald-600 to-green-700 hover:from-emerald-500 hover:to-green-600 disabled:from-slate-600 disabled:to-slate-700 disabled:cursor-not-allowed py-3 rounded-xl text-sm font-bold text-white shadow-md transition-all duration-200 disabled:opacity-50"
                >
                  恢复 (+1)
                </button>
              </div>
            </div>
          </div>

          <!-- 空资源提示 -->
          <div v-if="!character.resources || character.resources.length === 0" class="text-center py-12">
            <div class="text-4xl mb-3">💫</div>
            <p class="text-gray-500">此职业没有可管理的资源</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
</style>
