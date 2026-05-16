## 1. 基础设施搭建

- [x] 1.1 安装 ECharts 依赖 (`npm install echarts`)
- [x] 1.2 创建 `AppLayout.vue` 共享布局组件（顶部导航栏 + `<router-view>` 插槽）
- [x] 1.3 重构 `router/index.ts`：新增所有路由（/market, /trading, /portfolio, /analysis, /profile），嵌套在 AppLayout 下
- [x] 1.4 创建各页面占位组件（MarketView, TradingView, PortfolioView, AnalysisView, ProfileView）

## 2. 导航栏修复

- [x] 2.1 导航栏菜单项绑定 `router-link`，移除 `href="#"` 死链接
- [x] 2.2 导航栏当前路由高亮激活状态
- [x] 2.3 导航栏显示用户信息（用户名/ID + 退出按钮）

## 3. 行情页面 (MarketView)

- [x] 3.1 实现 `StockSearch.vue` 股票搜索组件（输入框 + 下拉结果列表）
- [x] 3.2 实现 `KLineChart.vue` K线图组件（ECharts candlestick），支持日线/周线/月线切换
- [x] 3.3 技术指标叠加显示切换（MA 均线、MACD、KDJ 复选框）
- [x] 3.4 行情页面布局：搜索框 + 实时报价 + K线图 + 指标控制

## 4. 交易页面 (TradingView)

- [x] 4.1 实现 `OrderForm.vue` 下单表单组件（股票选择、买卖方向、价格、数量）
- [x] 4.2 实现下单确认对话框（显示订单详情，确认/取消按钮）
- [x] 4.3 实现 `OrderList.vue` 订单历史列表组件
- [x] 4.4 交易页面布局：下单表单 + 订单历史

## 5. 持仓页面 (PortfolioView)

- [x] 5.1 实现持仓概览卡片（总资产、总市值、盈亏、可用资金、冻结资金）
- [x] 5.2 实现持仓明细表格（代码、名称、数量、均价、市价、盈亏）
- [x] 5.3 盈亏数字颜色区分（盈利红色、亏损绿色）
- [x] 5.4 空持仓状态提示

## 6. 分析页面 (AnalysisView)

- [x] 6.1 实现 `FinancialTable.vue` 财务报表通用表格组件
- [x] 6.2 实现财务概览指标卡片（营收、净利润、ROE、EPS、PE、PB、股息率）
- [x] 6.3 实现营收趋势图（ECharts 折线图 + 增长率标注）
- [x] 6.4 实现利润表、资产负债表、现金流量表三个财务报表展示
- [x] 6.5 集成股票搜索组件选择分析标的

## 7. 用户页面 (ProfileView)

- [x] 7.1 实现个人资料展示（用户名、手机号、邮箱）
- [x] 7.2 实现头像上传功能（文件选择 + 上传按钮 + 头像预览）

## 8. 集成验证

- [x] 8.1 验证导航栏所有链接可点击且正确跳转
- [x] 8.2 验证登录/退出流程正常
- [x] 8.3 验证行情页面 K线图和技术指标渲染
- [x] 8.4 验证交易下单→确认→历史完整流程
- [x] 8.5 验证持仓页面盈亏计算和展示
- [x] 8.6 验证分析页面财务报表数据和趋势图
- [x] 8.7 使用 agent-browser 验证所有页面对接后端接口正常
