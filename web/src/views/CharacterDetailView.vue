<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useCharacterStore } from '../stores/character'

const route = useRoute()
const router = useRouter()
const characterStore = useCharacterStore()

const characterId = computed(() => parseInt(route.params.id))
const loading = ref(true)

// 使用 computed 确保 iOS Safari 上的响应式更新
const character = computed(() => characterStore.getCharacterById(characterId.value))

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

// 获取中文职业名
function getClassName(dndClass) {
  const names = {
    'Paladin': '圣武士',
    'Duskblade': '暮刃',
    'Warlock': '邪术师',
    'Bard': '吟游诗人'
  }
  return names[dndClass] || dndClass
}

// 获取中文种族名
function getRaceName(race) {
  const names = {
    'Human': '人类',
    'Half-Elf': '半精灵',
    'Half-Orc': '半兽人'
  }
  return names[race] || race
}

// 计算 BAB (基础攻击加值) - 简化版本，基于等级
const bab = computed(() => {
  if (!character.value) return 0
  const level = character.value.level
  // 根据职业类型计算 BAB（这里简化处理）
  const dndClass = character.value.dndClass
  if (dndClass === 'Paladin' || dndClass === 'Duskblade') {
    // 高 BAB 职业：每级 +1
    return level
  } else if (dndClass === 'Bard') {
    // 中 BAB 职业：每 2 级 +1（四舍五入）
    return Math.floor(level * 0.75)
  } else {
    // 低 BAB 职业：每 2 级 +1
    return Math.floor(level / 2)
  }
})

// 攻击检定
const attackRoll = computed(() => {
  if (!character.value) return '+0'
  const strMod = Math.floor((character.value.strength - 10) / 2)
  const total = bab.value + strMod
  return total >= 0 ? `+${total}` : `${total}`
})

// 强韧检定 (Fortitude Save) - 基于体质
const fortitudeSave = computed(() => {
  if (!character.value) return '+0'
  const conMod = Math.floor((character.value.constitution - 10) / 2)
  const level = character.value.level
  // 强壮豁免奖励（简化：高豁免职业每级 +2，其他 +0.5）
  const dndClass = character.value.dndClass
  let baseSave = 0
  if (dndClass === 'Paladin') {
    baseSave = Math.floor(level * 0.7) // 高豁免
  } else {
    baseSave = Math.floor(level / 3) // 低豁免
  }
  const total = baseSave + conMod
  return total >= 0 ? `+${total}` : `${total}`
})

// 反射检定 (Reflex Save) - 基于敏捷
const reflexSave = computed(() => {
  if (!character.value) return '+0'
  const dexMod = Math.floor((character.value.dexterity - 10) / 2)
  const level = character.value.level
  const dndClass = character.value.dndClass
  let baseSave = 0
  if (dndClass === 'Duskblade') {
    baseSave = Math.floor(level * 0.7) // 高豁免
  } else {
    baseSave = Math.floor(level / 3) // 低豁免
  }
  const total = baseSave + dexMod
  return total >= 0 ? `+${total}` : `${total}`
})

// 意志检定 (Will Save) - 基于感知
const willSave = computed(() => {
  if (!character.value) return '+0'
  const wisMod = Math.floor((character.value.wisdom - 10) / 2)
  const level = character.value.level
  const dndClass = character.value.dndClass
  let baseSave = 0
  if (dndClass === 'Bard' || dndClass === 'Warlock') {
    baseSave = Math.floor(level * 0.7) // 高豁免
  } else {
    baseSave = Math.floor(level / 3) // 低豁免
  }
  const total = baseSave + wisMod
  return total >= 0 ? `+${total}` : `${total}`
})

// 护甲等级计算
const calculatedAC = computed(() => {
  if (!character.value) return 10
  const baseAC = 10
  const dexMod = Math.floor((character.value.dexterity - 10) / 2)
  // 简化：AC = 基础 + 盔甲 + 敏捷调整值
  const total = baseAC + character.value.armorClass - 10 + dexMod
  return total
})

// 检定列表数据
const saves = computed(() => {
  if (!character.value) return []
  return [
    { name: '攻击', nameEn: 'Attack', value: attackRoll.value, color: 'from-red-500 to-rose-600', icon: '⚔️' },
    { name: '强韧', nameEn: 'Fortitude', value: fortitudeSave.value, color: 'from-orange-500 to-amber-600', icon: '💪' },
    { name: '反射', nameEn: 'Reflex', value: reflexSave.value, color: 'from-green-500 to-emerald-600', icon: '⚡' },
    { name: '意志', nameEn: 'Will', value: willSave.value, color: 'from-purple-500 to-violet-600', icon: '🧠' },
    { name: '护甲', nameEn: 'AC', value: calculatedAC.value, color: 'from-blue-500 to-cyan-600', icon: '🛡️' }
  ]
})

// HP 调整
async function adjustHp(delta) {
  try {
    await characterStore.adjustHp(characterId.value, delta)
    // 使用 nextTick 确保 iOS Safari 触发响应式更新
    await nextTick()

    // 检测死亡
    if (character.value && character.value.currentHp <= 0) {
      showDeathModal.value = true
    }
  } catch (err) {
    console.error('Failed to adjust HP:', err)
  }
}

// 资源使用
async function useResource(resource) {
  if (resource.currentValue <= 0) return
  try {
    await characterStore.updateResource(characterId.value, resource.id, -1)
    await nextTick() // 确保 iOS Safari 触发响应式更新
  } catch (err) {
    console.error('Failed to use resource:', err)
  }
}

// 资源恢复
async function recoverResource(resource) {
  if (resource.currentValue >= resource.maxValue) return
  try {
    await characterStore.updateResource(characterId.value, resource.id, 1)
    await nextTick() // 确保 iOS Safari 触发响应式更新
  } catch (err) {
    console.error('Failed to recover resource:', err)
  }
}

// 长休
async function longRest() {
  try {
    await characterStore.longRest(characterId.value)
    await nextTick() // 确保 iOS Safari 触发响应式更新
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

// 技能特效系统
const effectRefs = ref(new Map())

// 死亡弹窗状态
const showDeathModal = ref(false)
const isDying = ref(false)

// iOS Safari 触摸事件防抖
const touchLock = ref(false)
let touchTimer = null

// 通用的触摸/点击处理函数（带防抖）
async function handleTouchOrClick(handler, event, ...args) {
  // 如果在触摸锁定期，忽略click事件（防止重复执行）
  if (touchLock.value) {
    console.log('🔒 [TOUCH] Locked, ignoring duplicate click')
    return
  }

  // 执行处理函数（传递event作为第一个参数）
  await handler(event, ...args)

  // 触摸后的短暂锁定期，防止iOS的click重复触发
  touchLock.value = true
  clearTimeout(touchTimer)
  touchTimer = setTimeout(() => {
    touchLock.value = false
  }, 300)
}

// 包装useResourceWithEffect以适应新的签名
async function wrappedUseResource(event, resource) {
  await useResourceWithEffect(resource, event)
}

// 包装recoverResource以适应新的签名
async function wrappedRecoverResource(event, resource) {
  await recoverResource(resource)
}

// 包装adjustHp以适应新的签名
async function wrappedAdjustHp(event, delta) {
  await adjustHp(delta)
}

// 包装adjustStat以适应新的签名
async function wrappedAdjustStat(event, statName, delta) {
  await adjustStat(statName, delta)
}

// 包装adjustLevel以适应新的签名
async function wrappedAdjustLevel(event, delta) {
  await adjustLevel(delta)
}

// 包装longRest以适应新的签名
async function wrappedLongRest(event) {
  await longRest()
}

function setEffectRef(resourceId, el) {
  if (el) {
    effectRefs.value.set(resourceId, el)
  }
}

function getEffectClass(resourceName) {
  const name = resourceName.toLowerCase()
  if (name.includes('圣疗') || name.includes('lay') || name.includes('heal')) return 'effect-holy'
  if (name.includes('奥术') || name.includes('arcane') || name.includes('pool')) return 'effect-arcane'
  if (name.includes('邪术') || name.includes('warlock') || name.includes('eldritch')) return 'effect-infernal'
  if (name.includes('激励') || name.includes('inspire') || name.includes('bardic')) return 'effect-inspire'
  return 'effect-magic'
}

async function useResourceWithEffect(resource, event) {
  console.log('🖱️ [CLICK] useResourceWithEffect called!')
  console.log('🖱️ [CLICK] Resource:', resource.resourceName, 'current value:', resource.currentValue)

  if (resource.currentValue <= 0) {
    console.log('❌ [CLICK] Resource depleted, ignoring click')
    return
  }

  // 触发特效
  const button = event.currentTarget
  console.log('🎯 [CLICK] Button element obtained:', button)
  triggerEffectOnButton(button, resource.resourceName)

  // 执行使用
  await useResource(resource)
  console.log('✅ [CLICK] Resource updated via API')
}

function triggerEffectOnButton(button, resourceName) {
  console.log('🎨 [EFFECT] Fullscreen effect triggered for resource:', resourceName)

  // 获取按钮位置作为特效中心点
  const rect = button.getBoundingClientRect()
  const centerX = rect.left + rect.width / 2
  const centerY = rect.top + rect.height / 2
  console.log('📐 [EFFECT] Effect center - X:', centerX, 'Y:', centerY)

  // 创建全屏特效容器
  const container = document.createElement('div')
  container.style.cssText = `
    position: fixed;
    left: 0;
    top: 0;
    width: 100vw;
    height: 100vh;
    pointer-events: none;
    z-index: 9999;
    overflow: visible;
  `
  document.body.appendChild(container)
  console.log('✅ [EFFECT] Fullscreen container created')

  // 根据资源名称确定颜色
  const effectClass = getEffectClass(resourceName)
  console.log('🏷️ [EFFECT] Effect class:', effectClass)

  const colors = {
    'effect-holy': { primary: '#ffd700', secondary: '#ffec8b', glow: 'rgba(255, 215, 0, 0.8)' },
    'effect-arcane': { primary: '#9333ea', secondary: '#7c3aed', glow: 'rgba(147, 51, 234, 0.8)' },
    'effect-infernal': { primary: '#22c55e', secondary: '#166534', glow: 'rgba(34, 197, 94, 0.8)' },
    'effect-inspire': { primary: '#ec4899', secondary: '#f472b6', glow: 'rgba(236, 72, 153, 0.8)' },
    'effect-magic': { primary: '#60a5fa', secondary: '#3b82f6', glow: 'rgba(96, 165, 250, 0.8)' }
  }
  const color = colors[effectClass] || colors['effect-magic']
  console.log('🎨 [EFFECT] Color scheme:', color)

  // 创建多个震撼的光环效果（从按钮位置扩散）
  for (let ring = 0; ring < 3; ring++) {
    const ripple = document.createElement('div')
    ripple.style.cssText = `
      position: absolute;
      left: ${centerX}px;
      top: ${centerY}px;
      transform: translate(-50%, -50%);
      width: 0;
      height: 0;
      border: ${4 + ring * 2}px solid ${color.glow};
      border-radius: 50%;
      box-shadow: 0 0 ${50 + ring * 20}px ${color.glow}, 0 0 ${100 + ring * 30}px ${color.primary};
      animation: rippleExpand 1.5s ease-out ${ring * 0.15}s forwards;
    `
    container.appendChild(ripple)
  }
  console.log('✅ [EFFECT] 3 shockwave ripples created')

  // 创建大量粒子（100个）从按钮爆发到全屏
  console.log('🎆 [EFFECT] Creating 100 explosion particles...')
  for (let i = 0; i < 100; i++) {
    const particle = document.createElement('div')
    const angle = (Math.PI * 2 * i) / 100
    const distance = 200 + Math.random() * 600
    const tx = Math.cos(angle) * distance
    const ty = Math.sin(angle) * distance
    const delay = Math.random() * 0.4
    const duration = 1 + Math.random() * 0.8
    const size = 6 + Math.random() * 12

    particle.style.cssText = `
      position: absolute;
      left: ${centerX}px;
      top: ${centerY}px;
      width: ${size}px;
      height: ${size}px;
      background: radial-gradient(circle, ${color.primary}, ${color.secondary});
      border-radius: 50%;
      box-shadow: 0 0 ${size}px ${color.primary}, 0 0 ${size * 2}px ${color.primary};
      animation: particleBurst ${duration}s ease-out ${delay}s forwards;
      --tx: ${tx}px;
      --ty: ${ty}px;
    `

    container.appendChild(particle)

    setTimeout(() => {
      particle.remove()
    }, (delay + duration) * 1000)
  }
  console.log('✅ [EFFECT] 100 particles created')

  // 添加额外的闪光粒子（50个）
  console.log('✨ [EFFECT] Creating 50 sparkle particles...')
  for (let i = 0; i < 50; i++) {
    const sparkle = document.createElement('div')
    const angle = Math.random() * Math.PI * 2
    const distance = 100 + Math.random() * 800
    const tx = Math.cos(angle) * distance
    const ty = Math.sin(angle) * distance
    const delay = Math.random() * 0.6
    const duration = 0.8 + Math.random() * 0.6
    const size = 3 + Math.random() * 8

    sparkle.style.cssText = `
      position: absolute;
      left: ${centerX}px;
      top: ${centerY}px;
      width: ${size}px;
      height: ${size}px;
      background: ${color.primary};
      clip-path: polygon(50% 0%, 61% 35%, 98% 35%, 68% 57%, 79% 91%, 50% 70%, 21% 91%, 32% 57%, 2% 35%, 39% 35%);
      box-shadow: 0 0 ${size * 3}px ${color.primary};
      animation: sparkleFloat ${duration}s ease-out ${delay}s forwards;
      --tx: ${tx}px;
      --ty: ${ty}px;
    `

    container.appendChild(sparkle)

    setTimeout(() => {
      sparkle.remove()
    }, (delay + duration) * 1000)
  }
  console.log('✅ [EFFECT] 50 sparkles created')

  // 添加动画关键帧
  const existingStyle = document.getElementById('effect-animations')
  if (!existingStyle) {
    const style = document.createElement('style')
    style.id = 'effect-animations'
    style.textContent = `
      @keyframes rippleExpand {
        0% {
          width: 0;
          height: 0;
          opacity: 1;
        }
        100% {
          width: 150vmax;
          height: 150vmax;
          opacity: 0;
        }
      }
      @keyframes particleBurst {
        0% {
          opacity: 1;
          transform: translate(0, 0) scale(1);
        }
        100% {
          opacity: 0;
          transform: translate(var(--tx), var(--ty)) scale(0);
        }
      }
      @keyframes sparkleFloat {
        0% {
          opacity: 1;
          transform: translate(0, 0) scale(1) rotate(0deg);
        }
        50% {
          opacity: 0.8;
          transform: translate(calc(var(--tx) * 0.5), calc(var(--ty) * 0.5)) scale(1.5) rotate(180deg);
        }
        100% {
          opacity: 0;
          transform: translate(var(--tx), var(--ty)) scale(0) rotate(360deg);
        }
      }
    `
    document.head.appendChild(style)
    console.log('✅ [EFFECT] Fullscreen animation keyframes injected')
  } else {
    console.log('♻️ [EFFECT] Animation keyframes already exist, skipping injection')
  }

  // 清理容器（延长时间让全屏特效播放完整）
  setTimeout(() => {
    container.remove()
    console.log('🗑️ [EFFECT] Fullscreen container cleaned up')
  }, 3000)
}

// 等级调整
async function adjustLevel(delta) {
  if (!character.value) return
  const newLevel = character.value.level + delta
  if (newLevel < 1 || newLevel > 20) return

  try {
    await characterStore.updateCharacter(characterId.value, { level: newLevel })
    await nextTick() // 确保 iOS Safari 触发响应式更新
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
    await nextTick() // 确保 iOS Safari 触发响应式更新
  } catch (err) {
    console.error('Failed to adjust stat:', err)
  }
}

onMounted(async () => {
  await characterStore.fetchCharacters()
  loading.value = false

  // computed 会自动更新，但检查是否需要跳转
  if (!character.value) {
    router.push('/')
  }
})
</script>

<template>
  <div class="min-h-screen p-4 md:p-6 dungeon-bg">

    <div class="relative max-w-7xl mx-auto">
      <!-- 返回按钮 -->
      <button
        @click="router.push('/')"
        class="wood-button mb-6 px-4 py-2 rounded-xl font-heading font-medium transition-all duration-300 flex items-center gap-2"
      >
        <span>←</span>
        <span class="font-heavy">返回大厅</span>
      </button>

      <!-- 加载状态 -->
      <div v-if="loading" class="text-center text-xl py-32">
        <div class="inline-block p-6 rounded-2xl wood-texture iron-border" style="min-width: 200px;">
          <div class="text-2xl font-black mb-3" style="color: #f4e4bc; font-family: 'Times New Roman', serif;">加载中...</div>
          <div class="animate-spin rounded-full h-12 w-12 border-4 mx-auto" style="border-color: #f4e4bc; border-top-color: transparent;"></div>
        </div>
      </div>

      <!-- 角色未找到 -->
      <div v-else-if="!character" class="text-center text-xl py-32">
        <div class="text-6xl mb-4">⚠️</div>
        <p style="color: #8b4513;">角色未找到</p>
      </div>

      <!-- 三栏仪表盘布局 -->
      <div v-else class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- 左栏 - 生存状态 (Survival) -->
        <div class="survival-panel wood-texture iron-border rounded-2xl p-6">
          <!-- 头像区域 -->
          <div class="flex flex-col items-center mb-6">
            <div class="relative mb-4">
              <img
                :src="character.imageUrl"
                :alt="character.name"
                class="rounded-2xl object-cover shadow-2xl"
                style="width: 160px; height: 160px; border: 4px solid #4a4a4a; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.6);"
              >
              <div class="absolute -bottom-2 -right-2 w-10 h-10 rounded-full flex items-center justify-center text-lg font-bold shadow-lg border-2" style="background: linear-gradient(180deg, #5a5a5a 0%, #3a3a3a 100%); border-color: #4a4a4a; color: #f4e4bc;">
                {{ character.level }}
              </div>
            </div>

            <!-- 基本信息 -->
            <h2 class="text-3xl font-black mb-1 font-title" style="color: #f4e4bc; text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.6);">{{ character.name }}</h2>
            <div class="flex items-center gap-2 mb-6">
              <span class="px-3 py-1 rounded-full text-sm font-bold shadow-md" style="background: linear-gradient(180deg, #5a5a5a 0%, #3a3a3a 100%); color: #f4e4bc; border: 2px solid #4a4a4a;">
                {{ getClassName(character.dndClass) }}
              </span>
              <span style="color: #c4a777;" class="text-sm">{{ getRaceName(character.race) }}</span>
            </div>

            <!-- HP 血条 -->
            <div class="w-full mb-6">
              <div class="flex justify-between items-center mb-3">
                <span class="text-lg font-bold flex items-center gap-2 font-heading font-heavy" style="color: #f4e4bc;">
                  <span class="text-2xl">❤️</span>
                  <span>生命值</span>
                </span>
                <div class="flex gap-2">
                  <button
                    @touchstart="handleTouchOrClick(wrappedAdjustHp, $event, -1)"
                    @click="handleTouchOrClick(wrappedAdjustHp, $event, -1)"
                    class="wood-button w-10 h-10 rounded-xl font-bold text-lg transform hover:scale-110 transition-all duration-200"
                  >
                    −
                  </button>
                  <button
                    @touchstart="handleTouchOrClick(wrappedAdjustHp, $event, 1)"
                    @click="handleTouchOrClick(wrappedAdjustHp, $event, 1)"
                    class="wood-button w-10 h-10 rounded-xl font-bold text-lg transform hover:scale-110 transition-all duration-200"
                  >
                    +
                  </button>
                </div>
              </div>
              <div class="text-2xl text-center font-black mb-3" style="color: #f4e4bc;">
                {{ character.currentHp }} <span style="color: #8b4513;" class="text-lg">/ {{ character.maxHp }}</span>
              </div>
              <div class="w-full rounded-full h-5 overflow-hidden" style="background: #4a3520; border: 2px solid #3a2510;">
                <div
                  class="h-full rounded-full transition-all duration-700"
                  :style="{
                    width: hpPercent + '%',
                    background: hpPercent > 60
                      ? 'linear-gradient(to right, #1a3d1a, #2d5a27)'
                      : hpPercent > 30
                      ? 'linear-gradient(to right, #3d3515, #5a4a20)'
                      : 'linear-gradient(to right, #3d1515, #5a2020)'
                  }"
                ></div>
              </div>
            </div>

            <!-- 防御属性 -->
            <div class="defense-grid grid grid-cols-3 gap-3 w-full mb-6">
              <div class="parchment iron-border rounded-xl p-4 text-center">
                <div class="text-2xl mb-1">🛡️</div>
                <div class="text-xs mb-1" style="color: #5d4025;">护甲</div>
                <div class="text-xl font-black" style="color: #8b4513;">AC {{ character.armorClass }}</div>
              </div>
              <div class="parchment iron-border rounded-xl p-4 text-center">
                <div class="text-2xl mb-1">⚡</div>
                <div class="text-xs mb-1" style="color: #5d4025;">先攻</div>
                <div class="text-xl font-black" style="color: #8b4513;">{{ formattedInitiative }}</div>
              </div>
              <div class="parchment iron-border rounded-xl p-4 text-center">
                <div class="text-2xl mb-1">👟</div>
                <div class="text-xs mb-1" style="color: #5d4025;">速度</div>
                <div class="text-xl font-black" style="color: #8b4513;">{{ character.speed }} ft</div>
              </div>
            </div>

            <!-- 检定列表 -->
            <div class="saves-grid grid grid-cols-1 gap-3 w-full">
              <div class="text-sm font-bold mb-2 flex items-center gap-2 font-heading font-heavy" style="color: #f4e4bc;">
                <span>🎯</span>
                <span>检定</span>
              </div>
              <div
                v-for="save in saves"
                :key="save.nameEn"
                class="parchment iron-border rounded-xl p-3"
              >
                <div class="flex items-center justify-between">
                  <div class="flex items-center gap-2">
                    <span class="text-xl">{{ save.icon }}</span>
                    <div>
                      <div class="text-xs uppercase font-bold" style="color: #5d4025;">{{ save.nameEn }}</div>
                      <div class="text-sm font-medium" style="color: #3d2914;">{{ save.name }}</div>
                    </div>
                  </div>
                  <div class="text-2xl font-black" style="color: #8b4513;">
                    {{ save.value }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 中栏 - 战斗与属性 (Combat & Stats) -->
        <div class="combat-panel wood-texture iron-border rounded-2xl p-6">
          <!-- 主武器卡片 -->
          <div class="parchment iron-border rounded-xl p-5 mb-6">
            <div class="flex items-center gap-3 mb-3">
              <div class="text-3xl">⚔️</div>
              <h3 class="text-lg font-bold font-heading font-heavy" style="color: #3d2914;">主武器</h3>
            </div>
            <p class="parchment-text font-body font-medium leading-relaxed">{{ character.mainWeapon }}</p>
          </div>

          <!-- 六维属性 -->
          <div class="mb-4 flex items-center gap-2">
            <div class="text-xl">📊</div>
            <h3 class="text-xl font-bold font-heading font-heavy" style="color: #f4e4bc;">属性</h3>
          </div>
          <div class="stats-grid grid grid-cols-3 gap-3">
            <div
              v-for="stat in stats"
              :key="stat.name"
              class="group parchment iron-border rounded-xl p-4 text-center hover:scale-105 transition-all duration-300 cursor-default"
            >
              <div class="text-xs uppercase font-bold mb-1" style="color: #5d4025;">{{ stat.nameEn }}</div>
              <div class="text-3xl font-black mb-1" style="color: #8b4513;">
                {{ stat.modifier }}
              </div>
              <div class="text-xs mb-2" style="color: #5d4025;">({{ stat.name }} {{ stat.value }})</div>
              <div class="text-xs mb-2" style="color: #5d4025;">检定 {{ stat.modifier }}</div>
              <div class="flex gap-1 justify-center">
                <button
                  @touchstart="handleTouchOrClick(wrappedAdjustStat, $event, stat.nameEn === 'STR' ? 'strength' : stat.nameEn === 'DEX' ? 'dexterity' : stat.nameEn === 'CON' ? 'constitution' : stat.nameEn === 'INT' ? 'intelligence' : stat.nameEn === 'WIS' ? 'wisdom' : 'charisma', -1)"
                  @click="handleTouchOrClick(wrappedAdjustStat, $event, stat.nameEn === 'STR' ? 'strength' : stat.nameEn === 'DEX' ? 'dexterity' : stat.nameEn === 'CON' ? 'constitution' : stat.nameEn === 'INT' ? 'intelligence' : stat.nameEn === 'WIS' ? 'wisdom' : 'charisma', -1)"
                  :disabled="stat.value <= 1"
                  class="flex-1 wood-button disabled:cursor-not-allowed py-1 rounded font-bold text-xs disabled:opacity-50"
                >
                  −
                </button>
                <button
                  @touchstart="handleTouchOrClick(wrappedAdjustStat, $event, stat.nameEn === 'STR' ? 'strength' : stat.nameEn === 'DEX' ? 'dexterity' : stat.nameEn === 'CON' ? 'constitution' : stat.nameEn === 'INT' ? 'intelligence' : stat.nameEn === 'WIS' ? 'wisdom' : 'charisma', 1)"
                  @click="handleTouchOrClick(wrappedAdjustStat, $event, stat.nameEn === 'STR' ? 'strength' : stat.nameEn === 'DEX' ? 'dexterity' : stat.nameEn === 'CON' ? 'constitution' : stat.nameEn === 'INT' ? 'intelligence' : stat.nameEn === 'WIS' ? 'wisdom' : 'charisma', 1)"
                  :disabled="stat.value >= 30"
                  class="flex-1 wood-button disabled:cursor-not-allowed py-1 rounded font-bold text-xs disabled:opacity-50"
                >
                  +
                </button>
              </div>
            </div>
          </div>

          <!-- 附加信息 -->
          <div class="mt-6 parchment iron-border rounded-xl p-4">
            <div class="flex justify-between items-center">
              <span class="font-medium" style="color: #5d4025;">等级</span>
              <div class="flex items-center gap-3">
                <button
                  @touchstart="handleTouchOrClick(wrappedAdjustLevel, $event, -1)"
                  @click="handleTouchOrClick(wrappedAdjustLevel, $event, -1)"
                  :disabled="character.level <= 1"
                  class="wood-button w-8 h-8 disabled:cursor-not-allowed rounded-lg font-bold text-sm disabled:opacity-50"
                >
                  −
                </button>
                <span class="text-2xl font-black" style="color: #8b4513;">Lv. {{ character.level }}</span>
                <button
                  @touchstart="handleTouchOrClick(wrappedAdjustLevel, $event, 1)"
                  @click="handleTouchOrClick(wrappedAdjustLevel, $event, 1)"
                  :disabled="character.level >= 20"
                  class="wood-button w-8 h-8 disabled:cursor-not-allowed rounded-lg font-bold text-sm disabled:opacity-50"
                >
                  +
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- 右栏 - 资源管理 (Resource Manager) -->
        <div class="resource-panel wood-texture iron-border rounded-2xl p-6">
          <!-- 长休按钮 -->
          <button
            @touchstart="handleTouchOrClick(wrappedLongRest, $event)"
            @click="handleTouchOrClick(wrappedLongRest, $event)"
            class="wood-button w-full mb-6 font-heading font-black py-4 px-6 rounded-xl transition-all duration-300 transform hover:scale-105 flex items-center justify-center gap-3"
          >
            <span class="text-2xl">🌙</span>
            <span class="text-lg font-heavy">长休 (Long Rest)</span>
          </button>

          <!-- 标题 -->
          <div class="flex items-center gap-2 mb-6">
            <div class="text-xl">💎</div>
            <h3 class="text-xl font-bold font-heading font-heavy" style="color: #f4e4bc;">职业资源</h3>
          </div>

          <!-- 资源列表 -->
          <div class="space-y-4">
            <div
              v-for="resource in character.resources"
              :key="resource.id"
              class="resource-card parchment iron-border rounded-xl p-4"
            >
              <div class="flex justify-between items-center mb-3">
                <span class="font-bold parchment-text">{{ resource.resourceName }}</span>
                <div class="flex items-center gap-2">
                  <span class="text-sm" style="color: #5d4025;">{{ resource.currentValue }}</span>
                  <span class="text-sm" style="color: #8b4513;">/</span>
                  <span class="text-sm" style="color: #5d4025;">{{ resource.maxValue }}</span>
                </div>
              </div>

              <!-- 进度条 -->
              <div class="w-full rounded-full h-3 mb-4 overflow-hidden" style="background: #4a3520; border: 2px solid #3a2510;">
                <div
                  class="h-full rounded-full transition-all duration-700"
                  :style="{
                    width: getResourcePercent(resource) + '%',
                    background: getResourceColor(resource) === 'from-blue-500 to-cyan-600'
                      ? 'linear-gradient(to right, #1e40af, #0891b2)'
                      : getResourceColor(resource) === 'from-amber-500 to-orange-600'
                      ? 'linear-gradient(to right, #b45309, #c2410c)'
                      : 'linear-gradient(to right, #991b1b, #be123c)'
                  }"
                ></div>
              </div>

              <!-- 操作按钮 -->
              <div class="flex gap-2 relative" style="position: relative;">
                <button
                  :id="`use-btn-${resource.id}`"
                  @touchstart="handleTouchOrClick(wrappedUseResource, $event, resource)"
                  @click="handleTouchOrClick(wrappedUseResource, $event, resource)"
                  :disabled="resource.currentValue <= 0"
                  class="flex-1 wood-button disabled:cursor-not-allowed py-3 rounded-xl text-sm font-bold disabled:opacity-50 relative z-10"
                  style="position: relative;"
                >
                  使用 (-1)
                </button>
                <button
                  @touchstart="handleTouchOrClick(wrappedRecoverResource, $event, resource)"
                  @click="handleTouchOrClick(wrappedRecoverResource, $event, resource)"
                  :disabled="resource.currentValue >= resource.maxValue"
                  class="flex-1 wood-button disabled:cursor-not-allowed py-3 rounded-xl text-sm font-bold disabled:opacity-50 relative z-10"
                >
                  恢复 (+1)
                </button>
              </div>
            </div>
          </div>

          <!-- 空资源提示 -->
          <div v-if="!character.resources || character.resources.length === 0" class="text-center py-12">
            <div class="text-4xl mb-3">💫</div>
            <p style="color: #8b7355;">此职业没有可管理的资源</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 死亡弹窗 -->
    <div v-if="showDeathModal" class="fixed inset-0 z-[10000] flex items-center justify-center" style="background: rgba(0, 0, 0, 0.95);">
      <div class="relative max-w-4xl w-full mx-4">
        <!-- die.jpg 图片 -->
        <img
          src="/images/die.jpg"
          alt="YOU DIED"
          class="w-full rounded-2xl shadow-2xl"
          style="border: 8px solid #5a0000; box-shadow: 0 0 60px rgba(255, 0, 0, 0.6), 0 0 120px rgba(139, 0, 0, 0.4);"
        >

        <!-- 彩蛋文字 -->
        <div class="absolute bottom-8 left-0 right-0 text-center">
          <h2 class="font-gothic text-red-500 mb-6 animate-pulse" style="font-size: 5rem; text-shadow: 0 0 30px rgba(255, 0, 0, 1), 0 0 60px rgba(255, 0, 0, 0.8), 0 0 90px rgba(139, 0, 0, 0.6);">
            彩蛋
          </h2>
          <button
            @click="showDeathModal = false"
            class="wood-button px-12 py-4 rounded-2xl text-2xl font-bold font-heading transform hover:scale-110 transition-all duration-300"
            style="background: linear-gradient(180deg, #8b0000 0%, #5a0000 50%, #3a0000 100%); border: 4px solid #ff4444; box-shadow: 0 0 30px rgba(255, 0, 0, 0.8);"
          >
            复活
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 圣疗术特效 - 金色圣光 + 羽毛飘落 */
.effect-holy .particle {
  background: radial-gradient(circle, #ffd700, #ffec8b);
  border-radius: 50% 50% 50% 0;
  animation: holyParticle 1s ease-out forwards;
  box-shadow: 0 0 10px #ffd700, 0 0 20px #ffd700;
}

.effect-holy .ripple {
  border: 3px solid rgba(255, 215, 0, 0.8);
  animation: holyRipple 1s ease-out forwards;
  box-shadow: 0 0 30px rgba(255, 215, 0, 0.5), inset 0 0 30px rgba(255, 215, 0, 0.3);
}

@keyframes holyParticle {
  0% {
    opacity: 1;
    transform: translate(0, 0) scale(1) rotate(0deg);
  }
  100% {
    opacity: 0;
    transform: translate(var(--tx, 20px), -100px) scale(0.3) rotate(360deg);
  }
}

@keyframes holyRipple {
  0% {
    width: 0;
    height: 0;
    opacity: 1;
  }
  100% {
    width: 200px;
    height: 200px;
    opacity: 0;
  }
}

/* 奥术池特效 - 紫色闪电 + 魔法符文 */
.effect-arcane .particle {
  background: linear-gradient(135deg, #9333ea, #7c3aed);
  clip-path: polygon(50% 0%, 100% 50%, 50% 100%, 0% 50%);
  animation: arcaneParticle 0.8s ease-out forwards;
  box-shadow: 0 0 15px #9333ea, 0 0 30px #9333ea;
}

.effect-arcane .ripple {
  border: 3px solid rgba(147, 51, 234, 0.8);
  animation: arcaneRipple 1s ease-out forwards;
  box-shadow: 0 0 30px rgba(147, 51, 234, 0.5), inset 0 0 30px rgba(147, 51, 234, 0.3);
}

@keyframes arcaneParticle {
  0% {
    opacity: 1;
    transform: translate(0, 0) scale(1) rotate(0deg);
  }
  50% {
    transform: translate(var(--tx, 30px), var(--ty, -30px)) scale(1.2) rotate(180deg);
  }
  100% {
    opacity: 0;
    transform: translate(var(--tx, 60px), var(--ty, -60px)) scale(0) rotate(360deg);
  }
}

@keyframes arcaneRipple {
  0% {
    width: 0;
    height: 0;
    opacity: 1;
    border-radius: 50%;
  }
  50% {
    border-radius: 0%;
  }
  100% {
    width: 180px;
    height: 180px;
    opacity: 0;
    border-radius: 50%;
  }
}

/* 邪术精华特效 - 绿色邪火 + 神秘烟雾 */
.effect-infernal .particle {
  background: radial-gradient(circle, #22c55e, #166534);
  border-radius: 50%;
  animation: infernalParticle 1s ease-out forwards;
  box-shadow: 0 0 15px #22c55e, 0 0 30px #22c55e;
}

.effect-infernal .ripple {
  border: 3px solid rgba(34, 197, 94, 0.8);
  animation: infernalRipple 1s ease-out forwards;
  box-shadow: 0 0 30px rgba(34, 197, 94, 0.5), inset 0 0 30px rgba(34, 197, 94, 0.3);
}

@keyframes infernalParticle {
  0% {
    opacity: 1;
    transform: translate(0, 0) scale(1);
  }
  30% {
    transform: translate(0, -20px) scale(1.3);
  }
  100% {
    opacity: 0;
    transform: translate(var(--tx, -30px), -80px) scale(0.2);
  }
}

@keyframes infernalRipple {
  0% {
    width: 0;
    height: 0;
    opacity: 1;
    filter: blur(0px);
  }
  100% {
    width: 220px;
    height: 220px;
    opacity: 0;
    filter: blur(10px);
  }
}

/* 诗人激励特效 - 彩色音符 + 音波扩散 */
.effect-inspire .particle {
  background: linear-gradient(135deg, #ec4899, #f472b6, #fb7185);
  border-radius: 50% 50% 0 50%;
  animation: inspireParticle 1.2s ease-out forwards;
  box-shadow: 0 0 12px #ec4899, 0 0 24px #ec4899;
}

.effect-inspire .ripple {
  border: 3px solid rgba(236, 72, 153, 0.8);
  animation: inspireRipple 1.2s ease-out forwards;
  box-shadow: 0 0 30px rgba(236, 72, 153, 0.5), inset 0 0 30px rgba(236, 72, 153, 0.3);
}

@keyframes inspireParticle {
  0% {
    opacity: 1;
    transform: translate(0, 0) scale(1) rotate(0deg);
  }
  40% {
    transform: translate(var(--tx, 25px), -40px) scale(1.1) rotate(90deg);
  }
  100% {
    opacity: 0;
    transform: translate(var(--tx, 50px), -120px) scale(0.4) rotate(180deg);
  }
}

@keyframes inspireRipple {
  0% {
    width: 0;
    height: 0;
    opacity: 1;
    border-radius: 50%;
  }
  70% {
    border-radius: 50%;
  }
  100% {
    width: 250px;
    height: 250px;
    opacity: 0;
    border-radius: 30%;
  }
}

/* 默认魔法特效 */
.effect-magic .particle {
  background: radial-gradient(circle, #60a5fa, #3b82f6);
  border-radius: 50%;
  animation: magicParticle 1s ease-out forwards;
  box-shadow: 0 0 10px #60a5fa;
}

.effect-magic .ripple {
  border: 3px solid rgba(96, 165, 250, 0.8);
  animation: magicRipple 1s ease-out forwards;
}

@keyframes magicParticle {
  0% {
    opacity: 1;
    transform: translate(0, 0) scale(1);
  }
  100% {
    opacity: 0;
    transform: translate(var(--tx, 30px), var(--ty, -30px)) scale(0);
  }
}

@keyframes magicRipple {
  0% {
    width: 0;
    height: 0;
    opacity: 1;
  }
  100% {
    width: 200px;
    height: 200px;
    opacity: 0;
  }
}

/* 彩蛋文字脉冲动画 */
@keyframes deathPulse {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.7;
    transform: scale(1.05);
  }
}

.animate-pulse {
  animation: deathPulse 2s ease-in-out infinite;
}
</style>
