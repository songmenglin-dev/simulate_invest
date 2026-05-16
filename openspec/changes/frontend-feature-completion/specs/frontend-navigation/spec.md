## ADDED Requirements

### Requirement: 多页面路由系统
系统 SHALL 实现 vue-router 路由系统，支持 Dashboard、行情、交易、持仓、分析、用户共 6 个功能页面，所有页面通过顶部导航栏可访问。

#### Scenario: 导航栏链接可点击跳转
- **WHEN** 用户点击导航栏中的任意菜单项（行情、交易、持仓、分析、用户）
- **THEN** 页面跳转到对应路由，且当前激活菜单项高亮显示

#### Scenario: 未登录访问受保护页面
- **WHEN** 用户未登录状态下访问任意非登录页面
- **THEN** 系统自动跳转到登录页 `/login`

#### Scenario: 已登录访问登录页
- **WHEN** 用户已登录状态下访问登录页 `/login`
- **THEN** 系统自动跳转到 Dashboard 页面

### Requirement: 共享布局组件
系统 SHALL 提供 `AppLayout` 共享布局组件，包含顶部导航栏和内容插槽，所有已登录页面使用该布局。

#### Scenario: 导航栏显示用户信息
- **WHEN** 用户已登录并访问任意页面
- **THEN** 顶部导航栏显示当前用户 ID 和退出按钮

#### Scenario: 退出登录
- **WHEN** 用户点击退出按钮
- **THEN** 系统清除 token 并跳转到登录页
