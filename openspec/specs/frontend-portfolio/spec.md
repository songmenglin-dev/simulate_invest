# frontend-portfolio Specification

## Purpose
TBD - created by archiving change frontend-feature-completion. Update Purpose after archive.
## Requirements
### Requirement: 持仓概览
系统 SHALL 展示用户的持仓概览，包含总资产、总市值、持仓盈亏、盈亏比例、可用资金、冻结资金。

#### Scenario: 展示持仓概览
- **WHEN** 用户进入持仓页面
- **THEN** 系统显示总资产（现金 + 市值）、总市值、持仓盈亏金额和比例

#### Scenario: 空持仓展示
- **WHEN** 用户暂无任何持仓
- **THEN** 系统显示总资产 = 可用资金，总市值和持仓盈亏均为 0

### Requirement: 持仓列表
系统 SHALL 以表格形式展示每只持仓股票的详细信息：股票代码、名称、持仓数量、可用数量、冻结数量、平均成本、当前市价、浮动盈亏。

#### Scenario: 展示持仓明细
- **WHEN** 用户进入持仓页面
- **THEN** 系统以表格展示每只持仓股票的代码、名称、数量、均价、市价、盈亏

#### Scenario: 盈亏颜色区分
- **WHEN** 某只持仓股票浮动盈利
- **THEN** 盈亏数字以红色显示；浮动亏损则以绿色显示

### Requirement: 资金账户
系统 SHALL 展示用户的资金账户信息，包含账户号、可用资金、冻结资金、账户状态。

#### Scenario: 展示资金账户
- **WHEN** 用户进入持仓页面
- **THEN** 系统显示账户号、可用资金、冻结资金和账户状态

