## Context

当前系统是 Spring Cloud 微服务架构（5 个业务服务 + 1 个 Gateway），使用 MyBatis-Plus 访问 MySQL，Redis 做缓存。现已实现基础的股票行情、手动交易、持仓管理和财务分析功能。本设计在现有架构上扩展三个新功能模块。

现有服务分布：
- `trading-service` (8082): 订单 CRUD、两阶段交易（下单→确认/取消）
- `market-service` (8083): 行情搜索、模拟报价、K线、技术指标、东方财富 API
- `analysis-service` (8085): 财务报表分析
- `portfolio-service` (8084): 持仓盈亏、资金管理
- `user-service` (8081): 用户注册登录、头像

## Goals / Non-Goals

**Goals:**
- 条件单：用户设定止盈/止损价格，系统自动监控并执行交易
- 自选股：用户收藏关注股票，集中查看行情
- 价格预警：用户设定价格阈值，触发时推送系统通知
- 策略回测：用户选择策略和股票，基于历史数据验证收益
- 新增 5 张数据库表，修改 orders 表

**Non-Goals:**
- 不涉及真实资金交易
- 不做移动端适配
- 不做实时推送（WebSocket），预警通知采用轮询拉取
- 不支持自定义策略脚本（回测仅支持预设策略模板）
- 不修改现有用户认证体系

## Decisions

### 1. 功能归属：复用现有服务而非新建服务

**决定**: 功能分配到现有 3 个服务，不新建微服务模块。

| 功能 | 归属服务 | 理由 |
|------|---------|------|
| 条件单 | `trading-service` | 本质是订单的扩展，最终调用现有下单+确认流程 |
| 自选股 | `market-service` | 与股票浏览、行情查询密切相关 |
| 价格预警 | `market-service` | 依赖行情数据监控，与自选股共处同一上下文 |
| 策略回测 | `analysis-service` | 已持有财务/历史数据处理逻辑，天然扩展 |

**替代方案**: 新建独立 `backtest-service` 和 `alert-service`。放弃了，因为每个新服务都需要独立的启动类、Nacos 注册、Gateway 路由、数据库配置，在当前项目规模下过度设计。

### 2. 价格监控机制：定时轮询而非事件驱动

**决定**: 使用 `@Scheduled` 每 10 秒轮询一次行情数据，检测条件单和价格预警的触发条件。

**替代方案**: 在每次报价更新时发布事件。放弃了，因为当前系统的模拟报价由前端驱动轮询，后端没有集中的"报价变更"事件源。定时轮询简单可靠，10 秒间隔对模拟平台足够。

**实现**: 在 `trading-service` 中新增 `ConditionalOrderScheduler`，在 `market-service` 中新增 `PriceAlertScheduler`。两者独立运行，不互相依赖。

### 3. 条件单执行：复用现有两阶段交易流程

**决定**: 条件单触发后，调用 `OrderService.place()` + `OrderService.confirm()` 直接生成 FILLED 订单，无需用户手动确认。

**替代方案**: 条件单触发后生成 PENDING 订单等用户确认。放弃了，因为"自动执行"是条件单的核心价值，要求用户二次确认就失去了"不盯盘"的意义。

**风控**: 下单前再次校验资金/持仓，校验失败则将条件单标记为 EXPIRED 并记录失败原因。

### 4. 预警通知存储：数据库而非 Redis

**决定**: 预警通知存入 `price_alert_notifications` 表，前端轮询 `GET /api/alert/notifications/{userId}` 拉取未读通知。

**替代方案**: Redis Pub/Sub 或 WebSocket 实时推送。放弃了，因为需要用户一直在线才能收到，且增加前端复杂度。轮询方式用户离线后下次登录仍能看到通知。

### 5. 回测引擎：内存模拟而非事件溯源

**决定**: 回测引擎在内存中逐条遍历 K 线数据，模拟买卖决策和持仓变化，最终计算出收益指标并持久化到 MySQL。

**替代方案**: 事件溯源模式，将每笔模拟交易作为事件存储。放弃了，因为回测是单次批量计算而非持续过程，内存计算足够。

**流程**:
1. 加载股票 K 线数据（按日期升序）
2. 逐日推进：计算指标 → 检查策略信号 → 模拟买卖 → 记录交易
3. 计算最终指标：总收益率、年化收益、最大回撤、胜率、夏普比率
4. 序列化净值和交易列表为 JSON 存入 MySQL

### 6. 策略模板：硬编码常量和参数化配置

**决定**: 预设 4 种策略模板，每种有可调参数。策略逻辑硬编码在 Java 中，参数通过 API 传入。

| 策略 | 买入信号 | 卖出信号 | 可调参数 |
|------|---------|---------|---------|
| 均线交叉 | MA(fast) 上穿 MA(slow) | MA(fast) 下穿 MA(slow) | fast, slow 周期 |
| MACD 信号 | MACD 线 上穿 信号线 | MACD 线 下穿 信号线 | fast(12), slow(26), signal(9) |
| 动量突破 | 价格突破 N 日最高价 | 价格跌破 M 日最低价 | lookback(20), holdDays(10) |
| 布林带 | 价格触及下轨 | 价格触及上轨 | period(20), multiplier(2) |

## Data Model

### 新增表

**conditional_orders（条件单）**:
```sql
CREATE TABLE conditional_orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(32) NOT NULL UNIQUE COMMENT '条件单编号, COT开头',
    user_id BIGINT NOT NULL,
    fund_account_id BIGINT NOT NULL,
    stock_code VARCHAR(10) NOT NULL,
    stock_name VARCHAR(50),
    condition_type VARCHAR(20) NOT NULL COMMENT 'STOP_LOSS/TAKE_PROFIT',
    trigger_price DECIMAL(10,2) NOT NULL COMMENT '触发价格',
    order_price DECIMAL(10,2) NOT NULL COMMENT '下单价格',
    quantity INT NOT NULL COMMENT '交易数量',
    direction INT NOT NULL COMMENT '方向: 1买入 2卖出',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE/TRIGGERED/CANCELLED/EXPIRED',
    triggered_order_id BIGINT COMMENT '触发后关联的订单ID',
    fail_reason VARCHAR(200) COMMENT '触发失败原因',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
);
```

**watchlist（自选股）**:
```sql
CREATE TABLE watchlist (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    stock_code VARCHAR(10) NOT NULL,
    stock_name VARCHAR(50),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_stock (user_id, stock_code),
    INDEX idx_user_id (user_id)
);
```

**price_alerts（价格预警）**:
```sql
CREATE TABLE price_alerts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    alert_no VARCHAR(32) NOT NULL UNIQUE COMMENT '预警编号',
    user_id BIGINT NOT NULL,
    stock_code VARCHAR(10) NOT NULL,
    stock_name VARCHAR(50),
    alert_type VARCHAR(20) NOT NULL COMMENT 'PRICE_ABOVE/PRICE_BELOW',
    target_price DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE/TRIGGERED/CANCELLED',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_status (user_id, status)
);
```

**price_alert_notifications（预警通知）**:
```sql
CREATE TABLE price_alert_notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    alert_id BIGINT NOT NULL,
    stock_code VARCHAR(10) NOT NULL,
    stock_name VARCHAR(50),
    alert_type VARCHAR(20) NOT NULL,
    target_price DECIMAL(10,2) NOT NULL,
    triggered_price DECIMAL(10,2) NOT NULL COMMENT '触发时的实际价格',
    is_read TINYINT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_read (user_id, is_read)
);
```

**backtest_strategies（回测策略定义）**:
```sql
CREATE TABLE backtest_strategies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    strategy_no VARCHAR(32) NOT NULL UNIQUE COMMENT '策略编号',
    user_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    strategy_type VARCHAR(30) NOT NULL COMMENT 'MA_CROSSOVER/MACD/MOMENTUM/BOLLINGER',
    stock_code VARCHAR(10) NOT NULL,
    parameter_json TEXT COMMENT '策略参数JSON',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
);
```

**backtest_results（回测结果）**:
```sql
CREATE TABLE backtest_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    result_no VARCHAR(32) NOT NULL UNIQUE COMMENT '结果编号',
    strategy_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    stock_code VARCHAR(10) NOT NULL,
    stock_name VARCHAR(50),
    strategy_type VARCHAR(30) NOT NULL,
    parameter_json TEXT,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    initial_capital DECIMAL(16,2) NOT NULL COMMENT '初始资金',
    final_capital DECIMAL(16,2) NOT NULL COMMENT '最终资金',
    total_return DECIMAL(10,4) COMMENT '总收益率',
    annual_return DECIMAL(10,4) COMMENT '年化收益率',
    max_drawdown DECIMAL(10,4) COMMENT '最大回撤率',
    win_rate DECIMAL(10,4) COMMENT '胜率',
    total_trades INT COMMENT '总交易次数',
    winning_trades INT COMMENT '盈利次数',
    sharpe_ratio DECIMAL(10,4) COMMENT '夏普比率',
    equity_curve_json MEDIUMTEXT COMMENT '净值曲线 [{date, value}]',
    trades_json MEDIUMTEXT COMMENT '交易记录 [{entryDate, exitDate, entryPrice, exitPrice, return}]',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_strategy_id (strategy_id)
);
```

### 修改表

**orders（订单表）新增字段**:
```sql
ALTER TABLE orders ADD COLUMN conditional_order_id BIGINT DEFAULT NULL COMMENT '关联的条件单ID';
```

### 索引策略
- 条件单/预警按 `user_id + status` 联合索引，支撑定时任务按状态筛选
- 自选股按 `user_id + stock_code` 唯一索引，防止重复添加
- 通知按 `user_id + is_read` 联合索引，支撑未读查询

## API Design

### 条件单（Gateway 路径: `/api/conditional-order/**` → `trading-service`）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/conditional-order/create` | 创建条件单 |
| GET | `/api/conditional-order/list/{userId}` | 用户条件单列表 |
| GET | `/api/conditional-order/{id}` | 条件单详情 |
| POST | `/api/conditional-order/cancel/{id}` | 取消条件单 |

### 自选股（Gateway 路径: `/api/watchlist/**` → `market-service`）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/watchlist/add` | 添加自选股 |
| DELETE | `/api/watchlist/remove/{userId}/{stockCode}` | 移除自选股 |
| GET | `/api/watchlist/{userId}` | 自选股列表 |
| GET | `/api/watchlist/quotes/{userId}` | 自选股实时行情（带报价） |

### 价格预警（Gateway 路径: `/api/alert/**` → `market-service`）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/alert/create` | 创建价格预警 |
| GET | `/api/alert/list/{userId}` | 预警列表 |
| POST | `/api/alert/cancel/{id}` | 取消预警 |
| GET | `/api/alert/notifications/{userId}` | 获取未读通知 |
| POST | `/api/alert/notifications/read/{id}` | 标记通知已读 |

### 策略回测（Gateway 路径: `/api/backtest/**` → `analysis-service`）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/backtest/run` | 运行回测 |
| GET | `/api/backtest/strategies/templates` | 获取策略模板列表（含参数说明） |
| GET | `/api/backtest/result/{id}` | 回测结果详情 |
| GET | `/api/backtest/history/{userId}` | 用户回测历史 |

### Gateway 新增路由

```yaml
# 条件单
- id: conditional-order-service
  uri: lb://trading-service
  predicates: Path=/api/conditional-order/**
  filters: StripPrefix=1

# 自选股
- id: watchlist-service
  uri: lb://market-service
  predicates: Path=/api/watchlist/**
  filters: StripPrefix=1

# 价格预警
- id: alert-service
  uri: lb://market-service
  predicates: Path=/api/alert/**
  filters: StripPrefix=1

# 策略回测
- id: backtest-service
  uri: lb://analysis-service
  predicates: Path=/api/backtest/**
  filters: StripPrefix=1
```

## Risks / Trade-offs

- **[风险] 条件单在价格剧烈波动时可能无法以期望价格成交** → 缓解：下单价使用 `order_price`（用户指定），如果当前价与下单价偏差超过 3% 则标记失败，不执行
- **[风险] 定时轮询可能遗漏瞬间的价格触发机会** → 缓解：在模拟平台中，10 秒轮询足够覆盖模拟报价的更新频率（3-5 秒）。如果未来接入真实高频数据，可升级为逐笔事件驱动
- **[风险] 回测可能存在前视偏差（look-ahead bias）** → 缓解：策略引擎严格按日期顺序处理，买入/卖出信号只使用信号日期之前的数据，不引用未来数据
- **[风险] 回测大数据量时可能执行很慢** → 缓解：限制回测区间最长 3 年，单次最多 1 只股票；异步执行，前端显示加载状态

## Open Questions

- 条件单是否需要支持"买入"方向（如突破买入）？当前先实现卖出方向的止盈止损，买入方向可在后续迭代中扩展
- 预警通知是否需要保留历史记录？当前设计保留全部通知记录，不做定期清理
