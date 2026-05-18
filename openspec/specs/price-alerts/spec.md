# price-alerts Specification

## Purpose
TBD - created by archiving change add-advanced-trading-features. Update Purpose after archive.
## Requirements
### Requirement: 创建价格预警
系统 SHALL 允许用户为关注的股票设置价格预警，当价格突破或跌破指定阈值时收到通知。

#### Scenario: 创建上涨预警
- **WHEN** 用户设定"当 贵州茅台 股价 >= 2000 元时提醒我"
- **THEN** 系统创建预警记录，alert_type=PRICE_ABOVE，target_price=2000，status=ACTIVE

#### Scenario: 创建下跌预警
- **WHEN** 用户设定"当 比亚迪 股价 <= 180 元时提醒我"
- **THEN** 系统创建预警记录，alert_type=PRICE_BELOW，target_price=180，status=ACTIVE

#### Scenario: 预警价格校验
- **WHEN** 用户设定的预警价格 <= 0
- **THEN** 系统返回错误提示"预警价格必须大于 0"

#### Scenario: 当前价已满足预警条件
- **WHEN** 用户设定上涨预警，但当前股价已 >= 目标价
- **THEN** 系统提示"当前价格已满足预警条件，是否仍要创建？"
- **AND** 用户确认后仍创建预警（可能立即触发）

### Requirement: 预警管理
系统 SHALL 提供预警列表查看和取消功能。

#### Scenario: 查看预警列表
- **WHEN** 用户访问预警管理页面
- **THEN** 系统返回所有预警记录，按创建时间倒序
- **AND** 每条显示：股票、预警类型、目标价、状态、创建时间

#### Scenario: 取消预警
- **WHEN** 用户取消一个 ACTIVE 状态的预警
- **THEN** 系统将预警 status 更新为 CANCELLED
- **AND** 该预警不再被监控

#### Scenario: 按状态筛选预警
- **WHEN** 用户筛选"活跃中"的预警
- **THEN** 系统仅返回 status=ACTIVE 的预警列表

### Requirement: 预警触发通知
系统 SHALL 在价格满足预警条件时生成通知记录。

#### Scenario: 价格上涨触发预警
- **WHEN** 某 PRICE_ABOVE 预警的 target_price=100，当前股价从 98 涨到 100.50
- **THEN** 系统生成一条通知记录，记录触发价格 100.50
- **AND** 预警 status 更新为 TRIGGERED

#### Scenario: 价格下跌触发预警
- **WHEN** 某 PRICE_BELOW 预警的 target_price=50，当前股价从 52 跌到 49.80
- **THEN** 系统生成一条通知记录，记录触发价格 49.80
- **AND** 预警 status 更新为 TRIGGERED

#### Scenario: 同一预警不重复触发
- **WHEN** 预警已经 TRIGGERED，价格后续再次穿越阈值
- **THEN** 系统不生成新的通知

### Requirement: 查看预警通知
系统 SHALL 允许用户查看预警触发通知。

#### Scenario: 查看未读通知列表
- **WHEN** 用户请求预警通知
- **THEN** 系统返回所有通知，按时间倒序
- **AND** 未读通知高亮显示
- **AND** 每条通知包含：股票、预警类型、目标价、触发价、时间

#### Scenario: 未读通知数量
- **WHEN** 用户访问导航栏
- **THEN** 系统返回未读通知数量（红色角标显示）

#### Scenario: 标记通知已读
- **WHEN** 用户查看某条通知详情
- **THEN** 系统将该通知的 is_read 更新为 1

#### Scenario: 全部标记已读
- **WHEN** 用户点击"全部已读"按钮
- **THEN** 系统将该用户所有未读通知标记为已读

### Requirement: 预警监控引擎
系统 SHALL 每 10 秒扫描所有 ACTIVE 预警，检测触发条件。

#### Scenario: 定时扫描预警
- **WHEN** 预警监控任务执行
- **THEN** 系统查询所有 ACTIVE 预警
- **AND** 逐一对比预警 target_price 与当前股价
- **AND** 对满足触发条件的预警生成通知并更新状态

