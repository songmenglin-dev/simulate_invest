# frontend-trading Specification

## Purpose
TBD - created by archiving change frontend-feature-completion. Update Purpose after archive.
## Requirements
### Requirement: 下单表单
系统 SHALL 提供股票买卖下单表单，包含股票选择、买卖方向、价格、数量输入，提交后创建订单。

#### Scenario: 买入下单成功
- **WHEN** 用户选择股票、输入价格和数量、选择买入方向并提交
- **THEN** 系统创建买入订单，返回订单号，弹出确认对话框

#### Scenario: 卖出下单成功
- **WHEN** 用户选择持有股票、输入价格和数量、选择卖出方向并提交
- **THEN** 系统创建卖出订单，返回订单号，弹出确认对话框

#### Scenario: 表单校验失败
- **WHEN** 用户未填写必填字段（股票代码、价格、数量）直接提交
- **THEN** 系统提示"请填写完整信息"，不提交请求

### Requirement: 订单确认与撤单
系统 SHALL 在下单后提供确认或撤销操作，确认后订单进入成交状态，撤销后订单取消。

#### Scenario: 确认订单
- **WHEN** 用户在下单成功弹出确认对话框后点击"确认"
- **THEN** 系统调用确认接口，订单状态变为已成交

#### Scenario: 取消订单
- **WHEN** 用户在下单成功弹出确认对话框后点击"取消"
- **THEN** 系统调用撤单接口，订单状态变为已撤销

### Requirement: 订单历史
系统 SHALL 展示当前用户的所有历史订单，包含订单号、股票、方向、价格、数量、金额、状态等信息。

#### Scenario: 查看订单历史
- **WHEN** 用户进入交易页面并查看订单历史区域
- **THEN** 系统显示该用户所有订单列表，按时间倒序排列

#### Scenario: 空订单历史
- **WHEN** 用户尚无任何订单记录
- **THEN** 系统显示"暂无订单记录"提示

