## ADDED Requirements

### Requirement: 创建条件单
系统 SHALL 允许用户为已持有的股票创建止盈止损条件单，设定触发价格和下单参数。

#### Scenario: 创建止盈条件单
- **WHEN** 用户持有某股票，设定"当股价 >= 15.00 元时以 15.00 元卖出 100 股"
- **THEN** 系统创建一条条件单记录，condition_type=TAKE_PROFIT，trigger_price=15.00，status=ACTIVE
- **AND** 条件单编号以"COT"开头

#### Scenario: 创建止损条件单
- **WHEN** 用户持有某股票，设定"当股价 <= 8.00 元时以 8.00 元卖出 100 股"
- **THEN** 系统创建一条条件单记录，condition_type=STOP_LOSS，trigger_price=8.00，status=ACTIVE

#### Scenario: 未持有股票时创建条件单失败
- **WHEN** 用户为未持有的股票创建止盈/止损条件单
- **THEN** 系统返回错误提示"该股票无持仓，无法创建条件单"

#### Scenario: 重复创建同一股票的条件单
- **WHEN** 用户已有一个 ACTIVE 状态的条件单，再为同一股票创建同类型条件单
- **THEN** 系统返回错误提示"该股票已有活跃条件单"

### Requirement: 条件单自动触发
系统 SHALL 定时监控行情价格，当条件单触发条件满足时自动执行交易。

#### Scenario: 止盈触发
- **WHEN** 某止盈条件单的 trigger_price=15.00，当前股价首次 >= 15.00
- **THEN** 系统自动执行卖出交易，扣除持仓、增加余额
- **AND** 条件单 status 更新为 TRIGGERED
- **AND** 生成的订单关联该条件单（conditional_order_id）

#### Scenario: 止损触发
- **WHEN** 某止损条件单的 trigger_price=8.00，当前股价首次 <= 8.00
- **THEN** 系统自动执行卖出交易，扣除持仓、增加余额
- **AND** 条件单 status 更新为 TRIGGERED

#### Scenario: 触发时持仓不足
- **WHEN** 条件单触发执行，但用户持仓数量已不足（部分持仓已被卖出）
- **THEN** 系统将条件单 status 更新为 EXPIRED
- **AND** 记录 fail_reason="触发时持仓不足"

#### Scenario: 触发时价格偏差过大
- **WHEN** 条件单触发价格与当前市价偏差超过 3%
- **THEN** 系统将条件单 status 更新为 EXPIRED
- **AND** 记录 fail_reason="价格偏差过大"

### Requirement: 条件单管理
系统 SHALL 提供条件单的查询、详情查看和取消功能。

#### Scenario: 查看条件单列表
- **WHEN** 用户请求条件单列表
- **THEN** 系统返回该用户所有条件单，按创建时间倒序
- **AND** 每条包含：编号、股票、触发价、类型、状态、创建时间

#### Scenario: 查看条件单详情
- **WHEN** 用户点击某个条件单
- **THEN** 系统显示完整信息：编号、股票、类型、触发价、下单价、数量、状态、创建时间
- **AND** 如果已触发，显示关联的订单编号和触发时间

#### Scenario: 取消条件单
- **WHEN** 用户取消一个 ACTIVE 状态的条件单
- **THEN** 系统将条件单 status 更新为 CANCELLED
- **AND** 该条件单不再被定时任务扫描

#### Scenario: 取消已触发的条件单失败
- **WHEN** 用户尝试取消一个 TRIGGERED 状态的条件单
- **THEN** 系统返回错误提示"条件单已触发，无法取消"

### Requirement: 定时触发引擎
系统 SHALL 每 10 秒扫描所有 ACTIVE 条件单，检测触发条件。

#### Scenario: 定时扫描执行
- **WHEN** 定时任务执行时
- **THEN** 系统查询所有 ACTIVE 状态的条件单
- **AND** 逐一对比条件单 trigger_price 与当前股价
- **AND** 对满足触发条件的条件单执行交易
- **AND** 更新条件单状态
