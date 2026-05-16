<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

const animateSections = ref<Set<string>>(new Set())
let observer: IntersectionObserver | null = null

onMounted(() => {
  observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          const id = entry.target.getAttribute('data-anim-id')
          if (id) animateSections.value = new Set([...animateSections.value, id])
        }
      })
    },
    { threshold: 0.2, rootMargin: '0px 0px -40px 0px' }
  )

  document.querySelectorAll('[data-anim-id]').forEach((el) => observer!.observe(el))
})

onUnmounted(() => observer?.disconnect())

const animClass = (id: string) => ({
  'scroll-reveal': true,
  'scroll-reveal--visible': animateSections.value.has(id),
})

const advantages = [
  {
    icon: 'M13 10V3L4 14h7v7l9-11h-7z',
    title: '智能决策',
    desc: '多维度技术分析指标与策略回测引擎，用数据驱动每一次交易决策，降低情绪干扰。',
  },
  {
    icon: 'M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z',
    title: '风险可控',
    desc: '条件单（止盈/止损）与价格预警守护账户安全，QQ邮箱实时推送，不再错过关键价格。',
  },
  {
    icon: 'M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z',
    title: '实时行情',
    desc: '整合东方财富市场数据，自选股 + 实时报价 + K线图表，一站式掌握市场脉搏。',
  },
  {
    icon: 'M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z',
    title: '零成本上手',
    desc: '内置模拟交易与回测环境，无需真实资金即可学习和验证策略，新手也能快速入门。',
  },
]

const features = [
  { name: '条件单', desc: '止盈止损自动触发，无需盯盘', route: '/conditional-orders', color: 'emerald' },
  { name: '自选股', desc: '个性化股票列表，实时价格追踪', route: '/watchlist', color: 'blue' },
  { name: '价格预警', desc: '价格到达阈值时邮件通知', route: '/alerts', color: 'amber' },
  { name: '策略回测', desc: '4种策略，历史数据验证收益', route: '/backtest', color: 'purple' },
  { name: '技术分析', desc: 'K线图 + 多指标辅助研判', route: '/analysis', color: 'indigo' },
  { name: '交易下单', desc: '完整的买入/卖出/持仓管理', route: '/trading', color: 'rose' },
]

const quickstartSteps = [
  { step: '01', title: '浏览行情', desc: '在行情模块中搜索股票，查看实时报价与K线图，将感兴趣的股票加入自选列表。' },
  { step: '02', title: '创建预警', desc: '为自选股设置价格预警，当股价到达目标价位时，将通过邮件实时通知。' },
  { step: '03', title: '模拟交易', desc: '使用内置的模拟交易功能进行买卖，无需真实资金，零风险体验交易流程。' },
  { step: '04', title: '策略回测', desc: '配置回测参数，选择策略类型（均线/MACD/动量/布林带），验证交易策略的历史表现。' },
]
</script>

<template>
  <div class="landing">
    <!-- Hero -->
    <section data-anim-id="hero" :class="animClass('hero')" class="hero">
      <div class="hero-bg" />
      <div class="hero-content">
        <div class="hero-badge">
          <span class="hero-badge-dot" />
          熵减纪元 · 股票投资平台
        </div>
        <h1 class="hero-title">
          让每一次投资<br /><span class="hero-title-accent">都有据可依</span>
        </h1>
        <p class="hero-desc">
          集行情分析、策略回测、条件交易、价格预警于一体的智能投资工具。
          用数据与逻辑对抗市场噪声，从熵增走向熵减。
        </p>
        <div class="hero-actions">
          <a href="/market" class="hero-btn-primary">开始使用</a>
          <a href="#quickstart" class="hero-btn-secondary">
            快速入门
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/>
            </svg>
          </a>
        </div>
      </div>
      <div class="hero-visual">
        <div class="hero-card hero-card--1">
          <div class="hero-card-bar" />
          <div class="hero-card-bar hero-card-bar--short" />
          <div class="hero-card-row">
            <span class="hero-card-tag">↑ +2.34%</span>
            <span class="hero-card-tag green">↑ +1.06%</span>
          </div>
        </div>
        <div class="hero-card hero-card--2">
          <div class="hero-card-dot" />
          <div class="hero-card-line" />
          <div class="hero-card-line hero-card-line--med" />
        </div>
      </div>
    </section>

    <!-- Advantages -->
    <section data-anim-id="advantages" :class="animClass('advantages')" class="section">
      <div class="section-header">
        <h2 class="section-title">为什么选择熵减纪元</h2>
        <p class="section-subtitle">不只是工具，更是一套完整的投资决策体系</p>
      </div>
      <div class="advantages-grid">
        <div v-for="(item, i) in advantages" :key="i" class="adv-card">
          <div class="adv-icon">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" :d="item.icon"/>
            </svg>
          </div>
          <h3 class="adv-title">{{ item.title }}</h3>
          <p class="adv-desc">{{ item.desc }}</p>
        </div>
      </div>
    </section>

    <!-- Features -->
    <section data-anim-id="features" :class="animClass('features')" class="section section--alt">
      <div class="section-header">
        <h2 class="section-title">核心功能</h2>
        <p class="section-subtitle">六大功能模块覆盖完整投资流程</p>
      </div>
      <div class="features-grid">
        <a
          v-for="f in features"
          :key="f.name"
          :href="f.route"
          :class="['feature-card', `feature-card--${f.color}`]"
        >
          <h3 class="feature-name">{{ f.name }}</h3>
          <p class="feature-desc">{{ f.desc }}</p>
        </a>
      </div>
    </section>

    <!-- Quickstart -->
    <section id="quickstart" data-anim-id="quickstart" :class="animClass('quickstart')" class="section">
      <div class="section-header">
        <h2 class="section-title">快速入门</h2>
        <p class="section-subtitle">四步上手，从零到一的投资之旅</p>
      </div>
      <div class="quickstart-list">
        <div v-for="(item, i) in quickstartSteps" :key="i" class="qs-step">
          <div class="qs-number">{{ item.step }}</div>
          <div class="qs-connector" v-if="i < quickstartSteps.length - 1" />
          <div class="qs-body">
            <h3 class="qs-title">{{ item.title }}</h3>
            <p class="qs-desc">{{ item.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Footer CTA -->
    <section class="cta">
      <h2 class="cta-title">准备好开始了吗？</h2>
      <p class="cta-sub">从行情分析开始你的熵减之旅</p>
      <a href="/market" class="cta-btn">进入行情模块</a>
    </section>
  </div>
</template>

<style scoped>
.landing {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  color: #0F172A;
}

/* ===== Scroll Reveal ===== */
.scroll-reveal {
  opacity: 0;
  transform: translateY(32px);
  transition: opacity 600ms ease, transform 600ms ease;
}
.scroll-reveal--visible {
  opacity: 1;
  transform: translateY(0);
}

/* ===== Sections ===== */
.section {
  padding: 80px 0;
}
.section--alt {
  background: #F8FAFC;
  margin: 0 -24px;
  padding-left: 24px;
  padding-right: 24px;
}
.section-header {
  text-align: center;
  margin-bottom: 48px;
}
.section-title {
  font-size: 32px;
  font-weight: 700;
  color: #0F172A;
  letter-spacing: -0.02em;
}
.section-subtitle {
  margin-top: 8px;
  font-size: 16px;
  color: #64748B;
}

/* ===== Hero ===== */
.hero {
  display: flex;
  align-items: center;
  gap: 64px;
  min-height: 560px;
  padding: 40px 0 80px;
  position: relative;
  overflow: hidden;
}
.hero-content {
  flex: 1;
  max-width: 560px;
}
.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 16px;
  background: #ECFDF5;
  color: #059669;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 500;
  margin-bottom: 24px;
}
.hero-badge-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #059669;
  animation: pulse-dot 2s infinite;
}
@keyframes pulse-dot {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.3; }
}
.hero-title {
  font-size: 52px;
  font-weight: 700;
  line-height: 1.15;
  letter-spacing: -0.03em;
  color: #0F172A;
  margin-bottom: 20px;
}
.hero-title-accent {
  color: #059669;
  position: relative;
}
.hero-desc {
  font-size: 17px;
  line-height: 1.7;
  color: #475569;
  margin-bottom: 36px;
}
.hero-actions {
  display: flex;
  gap: 16px;
}
.hero-btn-primary {
  display: inline-flex;
  align-items: center;
  padding: 14px 32px;
  background: #059669;
  color: #fff;
  border-radius: 12px;
  font-weight: 600;
  font-size: 15px;
  text-decoration: none;
  transition: background 200ms, transform 200ms;
}
.hero-btn-primary:hover {
  background: #047857;
  transform: translateY(-1px);
}
.hero-btn-secondary {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 14px 24px;
  background: #F1F5F9;
  color: #334155;
  border-radius: 12px;
  font-weight: 600;
  font-size: 15px;
  text-decoration: none;
  transition: background 200ms;
}
.hero-btn-secondary:hover {
  background: #E2E8F0;
}

/* Hero Visual */
.hero-visual {
  flex: 1;
  position: relative;
  height: 380px;
}
.hero-card {
  position: absolute;
  background: #fff;
  border: 1px solid #E6E8EA;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 24px rgba(15, 23, 42, 0.06);
}
.hero-card--1 {
  top: 40px;
  left: 20px;
  width: 200px;
}
.hero-card-bar {
  height: 8px;
  background: #059669;
  border-radius: 4px;
  width: 100%;
  margin-bottom: 8px;
}
.hero-card-bar--short {
  width: 60%;
}
.hero-card-row {
  display: flex;
  gap: 8px;
  margin-top: 16px;
}
.hero-card-tag {
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  background: #FEF2F2;
  color: #DC2626;
}
.hero-card-tag.green {
  background: #ECFDF5;
  color: #059669;
}
.hero-card--2 {
  top: 120px;
  right: 20px;
  width: 180px;
}
.hero-card-dot {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #ECFDF5;
  border: 3px solid #059669;
  margin-bottom: 12px;
}
.hero-card-line {
  height: 4px;
  background: #CBD5E1;
  border-radius: 2px;
  margin-bottom: 6px;
  width: 80%;
}
.hero-card-line--med {
  width: 55%;
}
@media (max-width: 860px) {
  .hero { flex-direction: column; min-height: auto; }
  .hero-title { font-size: 36px; }
  .hero-visual { display: none; }
}

/* ===== Advantages ===== */
.advantages-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}
.adv-card {
  background: #fff;
  border: 1px solid #E6E8EA;
  border-radius: 16px;
  padding: 28px;
  transition: box-shadow 200ms, transform 200ms;
}
.adv-card:hover {
  box-shadow: 0 8px 32px rgba(15, 23, 42, 0.08);
  transform: translateY(-2px);
}
.adv-icon {
  width: 48px;
  height: 48px;
  background: #ECFDF5;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #059669;
  margin-bottom: 16px;
}
.adv-icon svg { width: 24px; height: 24px; }
.adv-title {
  font-size: 18px;
  font-weight: 600;
  color: #0F172A;
  margin-bottom: 8px;
}
.adv-desc {
  font-size: 14px;
  color: #64748B;
  line-height: 1.7;
}
@media (max-width: 640px) {
  .advantages-grid { grid-template-columns: 1fr; }
}

/* ===== Features ===== */
.features-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}
.feature-card {
  display: block;
  padding: 24px;
  border-radius: 16px;
  text-decoration: none;
  transition: box-shadow 200ms, transform 200ms;
  border: 1px solid #E6E8EA;
  background: #fff;
}
.feature-card:hover {
  box-shadow: 0 8px 32px rgba(15, 23, 42, 0.1);
  transform: translateY(-2px);
}
.feature-name {
  font-size: 17px;
  font-weight: 600;
  color: #0F172A;
  margin-bottom: 6px;
}
.feature-desc {
  font-size: 13px;
  color: #64748B;
  line-height: 1.5;
}
@media (max-width: 640px) {
  .features-grid { grid-template-columns: 1fr; }
}

/* ===== Quickstart ===== */
.quickstart-list {
  max-width: 640px;
  margin: 0 auto;
}
.qs-step {
  display: flex;
  gap: 20px;
  position: relative;
  padding-bottom: 32px;
}
.qs-number {
  flex-shrink: 0;
  width: 48px;
  height: 48px;
  border-radius: 14px;
  background: #ECFDF5;
  color: #059669;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 700;
  z-index: 1;
}
.qs-connector {
  position: absolute;
  left: 23px;
  top: 52px;
  bottom: 0;
  width: 2px;
  background: #E6E8EA;
}
.qs-title {
  font-size: 18px;
  font-weight: 600;
  color: #0F172A;
  margin-bottom: 4px;
}
.qs-desc {
  font-size: 14px;
  color: #64748B;
  line-height: 1.7;
}

/* ===== CTA ===== */
.cta {
  text-align: center;
  padding: 80px 0;
}
.cta-title {
  font-size: 32px;
  font-weight: 700;
  color: #0F172A;
  margin-bottom: 8px;
}
.cta-sub {
  font-size: 16px;
  color: #64748B;
  margin-bottom: 32px;
}
.cta-btn {
  display: inline-flex;
  align-items: center;
  padding: 16px 40px;
  background: #059669;
  color: #fff;
  border-radius: 14px;
  font-weight: 600;
  font-size: 17px;
  text-decoration: none;
  transition: background 200ms, transform 200ms;
}
.cta-btn:hover {
  background: #047857;
  transform: translateY(-2px);
}
</style>
