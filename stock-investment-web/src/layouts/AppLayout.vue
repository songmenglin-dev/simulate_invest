<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import NotificationBell from '../components/NotificationBell.vue'
import NotificationPanel from '../components/NotificationPanel.vue'

const router = useRouter()
const route = useRoute()
const userId = ref<string>(localStorage.getItem('userId') || '')
const username = ref<string>(localStorage.getItem('username') || '用户')
const showNotifications = ref(false)
const openDropdown = ref<string | null>(null)
const showAvatarMenu = ref(false)

const navGroups = [
  {
    id: 'dashboard',
    path: '/home',
    label: '概览',
    icon: 'M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-4 0a1 1 0 01-1-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 01-1 1',
    children: [],
  },
  {
    id: 'market',
    label: '行情',
    icon: 'M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z',
    children: [
      { path: '/market', label: '行情看板', desc: '实时行情与K线' },
      { path: '/watchlist', label: '自选股', desc: '关注股票列表' },
      { path: '/alerts', label: '价格预警', desc: '价格提醒通知' },
    ],
  },
  {
    id: 'trading',
    label: '交易',
    icon: 'M13 7h8m0 0v8m0-8l-8 8-4-4-6 6',
    children: [
      { path: '/trading', label: '下单交易', desc: '买入卖出股票' },
      { path: '/conditional-orders', label: '条件单', desc: '止盈止损委托' },
      { path: '/portfolio', label: '我的持仓', desc: '资产与仓位管理' },
    ],
  },
  {
    id: 'analysis',
    label: '分析',
    icon: 'M11 3.055A9.001 9.001 0 1020.945 13H11V3.055z M20.488 9H15V3.512A9.025 9.025 0 0120.488 9z',
    children: [
      { path: '/analysis', label: '技术分析', desc: '指标与图表分析' },
      { path: '/backtest', label: '策略回测', desc: '历史数据验证' },
    ],
  },
]

const isGroupActive = (group: typeof navGroups[0]) => {
  if (group.id === 'dashboard') return route.path === '/home'
  return group.children.some((c) => route.path.startsWith(c.path))
}

const isChildActive = (path: string) => route.path.startsWith(path)

const avatarChar = computed(() => (username.value || '用户').charAt(0))

let closeTimer: ReturnType<typeof setTimeout> | null = null

const onDropdownEnter = (id: string) => {
  if (closeTimer) { clearTimeout(closeTimer); closeTimer = null }
  openDropdown.value = id
}

const onDropdownLeave = () => {
  closeTimer = setTimeout(() => { openDropdown.value = null }, 150)
}

const onAvatarEnter = () => {
  if (closeTimer) { clearTimeout(closeTimer); closeTimer = null }
  showAvatarMenu.value = true
}

const onAvatarLeave = () => {
  closeTimer = setTimeout(() => { showAvatarMenu.value = false }, 150)
}

const navigate = (path: string) => {
  openDropdown.value = null
  router.push(path)
}

const handleLogout = () => {
  localStorage.clear()
  router.push('/login')
}
</script>

<template>
  <div class="app-shell">
    <header class="app-header">
      <div class="header-inner">
        <!-- Logo -->
        <router-link to="/dashboard" class="brand">
          <div class="brand-icon">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"/>
            </svg>
          </div>
          <span class="brand-text">熵减纪元</span>
        </router-link>

        <!-- Navigation -->
        <nav class="main-nav">
          <template v-for="group in navGroups" :key="group.id">
            <!-- Direct link (no children) -->
            <router-link
              v-if="group.children.length === 0"
              :to="group.path"
              :class="['nav-item', isGroupActive(group) && 'nav-item--active']"
            >
              <svg class="nav-item-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" :d="group.icon"/>
              </svg>
              {{ group.label }}
            </router-link>

            <!-- Dropdown group -->
            <div
              v-else
              class="nav-dropdown-wrapper"
              @mouseenter="onDropdownEnter(group.id)"
              @mouseleave="onDropdownLeave"
            >
              <button
                :class="['nav-item', isGroupActive(group) && 'nav-item--active']"
                @click="onDropdownEnter(group.id)"
              >
                <svg class="nav-item-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" :d="group.icon"/>
                </svg>
                {{ group.label }}
                <svg
                  class="dropdown-chevron"
                  :class="{ 'dropdown-chevron--open': openDropdown === group.id }"
                  fill="none" stroke="currentColor" viewBox="0 0 24 24"
                >
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/>
                </svg>
              </button>

              <Transition name="dropdown">
                <div v-if="openDropdown === group.id" class="dropdown-menu">
                  <button
                    v-for="child in group.children"
                    :key="child.path"
                    :class="['dropdown-item', isChildActive(child.path) && 'dropdown-item--active']"
                    @click="navigate(child.path)"
                  >
                    <div class="dropdown-item-label">{{ child.label }}</div>
                    <div class="dropdown-item-desc">{{ child.desc }}</div>
                  </button>
                </div>
              </Transition>
            </div>
          </template>
        </nav>

        <!-- Right Section -->
        <div class="header-actions">
          <NotificationBell @click="showNotifications = !showNotifications" />
          <NotificationPanel :visible="showNotifications" @close="showNotifications = false" />

          <!-- Avatar Dropdown -->
          <div
            class="avatar-wrapper"
            @mouseenter="onAvatarEnter"
            @mouseleave="onAvatarLeave"
          >
            <button :class="['avatar-btn', showAvatarMenu && 'avatar-btn--active']" @click="onAvatarEnter">
              <span class="avatar-char">{{ avatarChar }}</span>
            </button>

            <Transition name="dropdown">
              <div v-if="showAvatarMenu" class="dropdown-menu dropdown-menu--right">
                <div class="dropdown-header">
                  <div class="dropdown-header-avatar">{{ avatarChar }}</div>
                  <div>
                    <div class="dropdown-header-name">{{ username }}</div>
                    <div class="dropdown-header-id">ID: {{ userId }}</div>
                  </div>
                </div>
                <div class="dropdown-divider" />
                <button class="dropdown-item" @click="navigate('/profile')">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
                  </svg>
                  <div class="dropdown-item-label">用户管理</div>
                </button>
                <button class="dropdown-item dropdown-item--danger" @click="handleLogout">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"/>
                  </svg>
                  <div class="dropdown-item-label">退出登录</div>
                </button>
              </div>
            </Transition>
          </div>
        </div>
      </div>
    </header>

    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<style>
@import url('https://fonts.googleapis.com/css2?family=IBM+Plex+Sans:wght@400;500;600;700&display=swap');
</style>

<style scoped>
/* ===== Shell ===== */
.app-shell {
  min-height: 100vh;
  background: #F8FAFC;
  font-family: 'IBM Plex Sans', -apple-system, BlinkMacSystemFont, sans-serif;
}

/* ===== Header ===== */
.app-header {
  background: #fff;
  border-bottom: 1px solid #E6E8EA;
  position: sticky;
  top: 0;
  z-index: 50;
}

.header-inner {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  height: 60px;
  gap: 8px;
}

/* ===== Brand ===== */
.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
  flex-shrink: 0;
  margin-right: 8px;
}

.brand-icon {
  width: 36px;
  height: 36px;
  background: #059669;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.brand-text {
  font-size: 17px;
  font-weight: 700;
  color: #0F172A;
  letter-spacing: -0.01em;
}

/* ===== Main Nav ===== */
.main-nav {
  display: flex;
  align-items: center;
  gap: 2px;
  flex: 1;
}

/* ===== Nav Item ===== */
.nav-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #475569;
  text-decoration: none;
  border: none;
  background: transparent;
  cursor: pointer;
  transition: color 150ms, background-color 150ms;
  white-space: nowrap;
  font-family: inherit;
}

.nav-item:hover {
  color: #059669;
  background: #F0FDF6;
}

.nav-item--active {
  color: #059669;
  background: #ECFDF5;
}

.nav-item-icon {
  width: 18px;
  height: 18px;
  flex-shrink: 0;
}

/* ===== Dropdown ===== */
.nav-dropdown-wrapper {
  position: relative;
}

.dropdown-chevron {
  width: 14px;
  height: 14px;
  transition: transform 200ms ease;
}

.dropdown-chevron--open {
  transform: rotate(180deg);
}

.dropdown-menu {
  position: absolute;
  top: calc(100% + 6px);
  left: 0;
  min-width: 200px;
  background: #fff;
  border: 1px solid #E6E8EA;
  border-radius: 12px;
  padding: 6px;
  box-shadow: 0 16px 32px -12px rgba(15, 23, 42, 0.12);
}

.dropdown-menu--right {
  left: auto;
  right: 0;
  min-width: 220px;
}

.dropdown-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
}

.dropdown-header-avatar {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: #059669;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 600;
  flex-shrink: 0;
}

.dropdown-header-name {
  font-size: 14px;
  font-weight: 600;
  color: #0F172A;
}

.dropdown-header-id {
  font-size: 12px;
  color: #94A3B8;
  margin-top: 2px;
}

.dropdown-divider {
  height: 1px;
  background: #E6E8EA;
  margin: 6px 0;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  padding: 10px 12px;
  border-radius: 8px;
  border: none;
  background: transparent;
  cursor: pointer;
  text-align: left;
  font-family: inherit;
  font-size: 14px;
  color: #334155;
  transition: background-color 150ms, color 150ms;
}

.dropdown-item:hover {
  background: #F1F5F9;
  color: #0F172A;
}

.dropdown-item--active {
  background: #ECFDF5;
  color: #059669;
}

.dropdown-item--active .dropdown-item-desc {
  color: #34D399;
}

.dropdown-item-label {
  font-weight: 500;
  white-space: nowrap;
}

.dropdown-item-desc {
  font-size: 12px;
  color: #94A3B8;
  margin-top: 1px;
  white-space: nowrap;
}

.dropdown-item--danger {
  color: #DC2626;
}

.dropdown-item--danger:hover {
  background: #FEF2F2;
  color: #DC2626;
}

/* Dropdown transition */
.dropdown-enter-active {
  transition: opacity 150ms ease, transform 150ms ease;
}
.dropdown-leave-active {
  transition: opacity 100ms ease, transform 100ms ease;
}
.dropdown-enter-from {
  opacity: 0;
  transform: translateY(-4px);
}
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}

/* ===== Avatar ===== */
.avatar-wrapper {
  position: relative;
  flex-shrink: 0;
}

.avatar-btn {
  width: 38px;
  height: 38px;
  border-radius: 10px;
  border: 2px solid transparent;
  background: #ECFDF5;
  color: #059669;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: border-color 150ms, transform 150ms;
  font-family: inherit;
}

.avatar-btn:hover,
.avatar-btn--active {
  border-color: #059669;
  transform: scale(1.03);
}

.avatar-char {
  font-size: 16px;
  font-weight: 700;
}

/* ===== Header Actions ===== */
.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
  margin-left: auto;
}

/* ===== Main Content ===== */
.main-content {
  max-width: 1280px;
  margin: 0 auto;
  padding: 32px 24px;
}

/* ===== Responsive ===== */
@media (max-width: 900px) {
  .brand-text {
    display: none;
  }

  .nav-item {
    padding: 8px 10px;
    font-size: 13px;
    gap: 4px;
  }

  .nav-item-icon {
    width: 16px;
    height: 16px;
  }

  .header-actions {
    gap: 8px;
  }
}
</style>
