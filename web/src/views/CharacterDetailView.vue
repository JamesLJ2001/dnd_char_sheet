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
    { name: 'STR', value: character.value.strength, modifier: calculateModifier(character.value.strength) },
    { name: 'DEX', value: character.value.dexterity, modifier: calculateModifier(character.value.dexterity) },
    { name: 'CON', value: character.value.constitution, modifier: calculateModifier(character.value.constitution) },
    { name: 'INT', value: character.value.intelligence, modifier: calculateModifier(character.value.intelligence) },
    { name: 'WIS', value: character.value.wisdom, modifier: calculateModifier(character.value.wisdom) },
    { name: 'CHA', value: character.value.charisma, modifier: calculateModifier(character.value.charisma) }
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
  if (percent > 60) return 'bg-blue-500'
  if (percent > 30) return 'bg-yellow-500'
  return 'bg-red-500'
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
  <div class="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 p-6">
    <div class="max-w-7xl mx-auto">
      <!-- 返回按钮 -->
      <button
        @click="router.push('/')"
        class="mb-6 px-4 py-2 bg-gray-700 hover:bg-gray-600 rounded-lg text-white transition-all"
      >
        ← 返回角色大厅
      </button>

      <!-- 加载状态 -->
      <div v-if="loading" class="text-center text-gray-400 text-xl py-20">
        加载中...
      </div>

      <!-- 角色未找到 -->
      <div v-else-if="!character" class="text-center text-red-500 text-xl py-20">
        角色未找到
      </div>

      <!-- 三栏仪表盘布局 -->
      <div v-else class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- 左栏 - 生存状态 (Survival) -->
        <div class="survival-panel bg-gray-800 rounded-xl p-6 shadow-xl">
          <div class="flex flex-col items-center">
            <!-- 头像 -->
            <img
              :src="character.imageUrl"
              :alt="character.name"
              class="w-32 h-32 rounded-full border-4 border-amber-600 mb-4 object-cover"
            >

            <!-- 基本信息 -->
            <h2 class="text-3xl font-bold text-amber-400 mb-2">{{ character.name }}</h2>
            <p class="text-gray-400 text-lg mb-6">{{ character.dndClass }} • {{ character.race }}</p>

            <!-- HP 血条 -->
            <div class="w-full mb-6">
              <div class="flex justify-between items-center mb-2">
                <span class="text-xl font-bold text-white">HP</span>
                <div class="flex gap-2">
                  <button
                    @click="adjustHp(-1)"
                    class="bg-red-600 hover:bg-red-700 px-3 py-1 rounded font-bold transition-all"
                  >
                    [-]
                  </button>
                  <button
                    @click="adjustHp(1)"
                    class="bg-green-600 hover:bg-green-700 px-3 py-1 rounded font-bold transition-all"
                  >
                    [+]
                  </button>
                </div>
              </div>
              <div class="text-2xl text-center text-white font-bold mb-2">
                {{ character.currentHp }} / {{ character.maxHp }}
              </div>
              <div class="w-full bg-gray-700 rounded-full h-6 overflow-hidden">
                <div
                  class="bg-red-600 h-6 rounded-full transition-all duration-500"
                  :style="{ width: hpPercent + '%' }"
                ></div>
              </div>
            </div>

            <!-- 防御属性 -->
            <div class="defense-grid grid grid-cols-3 gap-4 w-full mt-4">
              <div class="text-center bg-gray-700 rounded-lg p-3">
                <div class="text-3xl mb-1">🛡️</div>
                <div class="text-xl font-bold text-amber-400">AC {{ character.armorClass }}</div>
              </div>
              <div class="text-center bg-gray-700 rounded-lg p-3">
                <div class="text-3xl mb-1">⚡</div>
                <div class="text-xl font-bold text-amber-400">Init {{ formattedInitiative }}</div>
              </div>
              <div class="text-center bg-gray-700 rounded-lg p-3">
                <div class="text-3xl mb-1">👟</div>
                <div class="text-xl font-bold text-amber-400">{{ character.speed }} ft</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 中栏 - 战斗与属性 (Combat & Stats) -->
        <div class="combat-panel bg-gray-800 rounded-xl p-6 shadow-xl">
          <!-- 主武器卡片 -->
          <div class="weapon-card bg-gradient-to-r from-amber-900 to-amber-800 rounded-lg p-4 mb-6">
            <h3 class="text-lg font-bold text-amber-300 mb-2">⚔️ {{ character.mainWeapon }}</h3>
          </div>

          <!-- 六维网格 (2x3) -->
          <h3 class="text-xl font-bold text-amber-400 mb-4">属性</h3>
          <div class="stats-grid grid grid-cols-3 gap-3">
            <div
              v-for="stat in stats"
              :key="stat.name"
              class="stat-card bg-gray-700 rounded-lg p-3 text-center hover:bg-gray-600 transition-all"
            >
              <div class="text-xs text-gray-400 uppercase font-bold">{{ stat.name }}</div>
              <div class="text-3xl font-bold text-amber-400 my-1">{{ stat.modifier }}</div>
              <div class="text-xs text-gray-500">({{ stat.value }})</div>
            </div>
          </div>

          <!-- 等级信息 -->
          <div class="mt-6 bg-gray-700 rounded-lg p-4 text-center">
            <div class="text-sm text-gray-400">等级</div>
            <div class="text-3xl font-bold text-amber-400">Lv. {{ character.level }}</div>
          </div>
        </div>

        <!-- 右栏 - 资源管理 (Resource Manager) -->
        <div class="resource-panel bg-gray-800 rounded-xl p-6 shadow-xl">
          <!-- 长休按钮 -->
          <button
            @click="longRest"
            class="w-full bg-gradient-to-r from-amber-600 to-amber-700 hover:from-amber-500 hover:to-amber-600 text-white font-bold py-3 px-6 rounded-lg mb-6 transition-all shadow-lg"
          >
            🌙 Long Rest (长休)
          </button>

          <h3 class="text-xl font-bold text-amber-400 mb-4">Class Resources</h3>

          <!-- 资源列表 -->
          <div
            v-for="resource in character.resources"
            :key="resource.id"
            class="resource-card bg-gray-700 rounded-lg p-4 mb-3"
          >
            <div class="flex justify-between items-center mb-2">
              <span class="font-bold text-white">{{ resource.resourceName }}</span>
              <span class="text-sm text-gray-400">{{ resource.currentValue }} / {{ resource.maxValue }}</span>
            </div>

            <!-- 进度条 -->
            <div class="w-full bg-gray-600 rounded-full h-3 mb-3 overflow-hidden">
              <div
                :class="['h-3 rounded-full transition-all duration-500', getResourceColor(resource)]"
                :style="{ width: getResourcePercent(resource) + '%' }"
              ></div>
            </div>

            <!-- 操作按钮 -->
            <div class="flex gap-2">
              <button
                @click="useResource(resource)"
                :disabled="resource.currentValue <= 0"
                class="flex-1 bg-blue-600 hover:bg-blue-700 disabled:bg-gray-600 disabled:cursor-not-allowed py-2 rounded text-sm font-bold transition-all"
              >
                Use (-1)
              </button>
              <button
                @click="recoverResource(resource)"
                :disabled="resource.currentValue >= resource.maxValue"
                class="flex-1 bg-green-600 hover:bg-green-700 disabled:bg-gray-600 disabled:cursor-not-allowed py-2 rounded text-sm font-bold transition-all"
              >
                Recover (+1)
              </button>
            </div>
          </div>

          <!-- 空资源提示 -->
          <div v-if="!character.resources || character.resources.length === 0" class="text-gray-500 text-center py-8">
            此职业没有可管理的资源
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
</style>
