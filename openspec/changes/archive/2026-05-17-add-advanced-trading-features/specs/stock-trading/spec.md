## ADDED Requirements

### Requirement: 条件单关联订单
系统 SHALL 在订单中记录其是否为条件单触发，以及关联的条件单信息。

#### Scenario: 条件单触发生成的订单
- **WHEN** 某订单由条件单自动触发创建
- **THEN** 该订单的 conditional_order_id 字段记录关联的条件单 ID
- **AND** 订单详情页标注"由条件单触发"

#### Scenario: 手动创建的订单
- **WHEN** 用户手动下单
- **THEN** 该订单的 conditional_order_id 为 NULL
- **AND** 订单详情页不显示条件单相关标注

## MODIFIED Requirements

### Requirement: Stock Trading
系统 SHALL 在订单历史页面展示订单来源（手动下单或条件单触发）。

#### Scenario: View order history
- **WHEN** user navigates to the order history page
- **THEN** system SHALL display all historical orders with status, time, and details
- **AND** buy orders SHALL be marked in red, sell orders in green
- **AND** orders triggered by conditional orders SHALL be marked with "条件单" tag
