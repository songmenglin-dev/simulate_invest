-- 条件单表
CREATE TABLE IF NOT EXISTS conditional_orders (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='条件单表';

-- 自选股表
CREATE TABLE IF NOT EXISTS watchlist (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    stock_code VARCHAR(10) NOT NULL,
    stock_name VARCHAR(50),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_stock (user_id, stock_code),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自选股表';

-- 价格预警表
CREATE TABLE IF NOT EXISTS price_alerts (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='价格预警表';

-- 预警通知表
CREATE TABLE IF NOT EXISTS price_alert_notifications (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预警通知表';

-- 回测策略表
CREATE TABLE IF NOT EXISTS backtest_strategies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    strategy_no VARCHAR(32) NOT NULL UNIQUE COMMENT '策略编号',
    user_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    strategy_type VARCHAR(30) NOT NULL COMMENT 'MA_CROSSOVER/MACD/MOMENTUM/BOLLINGER',
    stock_code VARCHAR(10) NOT NULL,
    parameter_json TEXT COMMENT '策略参数JSON',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回测策略表';

-- 回测结果表
CREATE TABLE IF NOT EXISTS backtest_results (
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
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_strategy_id (strategy_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回测结果表';

-- 修改订单表：新增条件单关联字段
ALTER TABLE orders ADD COLUMN IF NOT EXISTS conditional_order_id BIGINT DEFAULT NULL COMMENT '关联的条件单ID';
