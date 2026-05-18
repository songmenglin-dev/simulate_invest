/*
 Navicat Premium Dump SQL

 Source Server         : win_mysql
 Source Server Type    : MySQL
 Source Server Version : 80036 (8.0.36)
 Source Host           : localhost:3306
 Source Schema         : stock_investment

 Target Server Type    : MySQL
 Target Server Version : 80036 (8.0.36)
 File Encoding         : 65001

 Date: 18/05/2026 00:13:17
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for backtest_results
-- ----------------------------
DROP TABLE IF EXISTS `backtest_results`;
CREATE TABLE `backtest_results`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `result_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `strategy_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `stock_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `stock_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `strategy_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `parameter_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `initial_capital` decimal(16, 2) NOT NULL,
  `final_capital` decimal(16, 2) NOT NULL,
  `total_return` decimal(10, 4) NULL DEFAULT NULL,
  `annual_return` decimal(10, 4) NULL DEFAULT NULL,
  `max_drawdown` decimal(10, 4) NULL DEFAULT NULL,
  `win_rate` decimal(10, 4) NULL DEFAULT NULL,
  `total_trades` int NULL DEFAULT NULL,
  `winning_trades` int NULL DEFAULT NULL,
  `sharpe_ratio` decimal(10, 4) NULL DEFAULT NULL,
  `equity_curve_json` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `trades_json` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `result_no`(`result_no` ASC) USING BTREE,
  INDEX `idx_br_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_br_strategy_id`(`strategy_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of backtest_results
-- ----------------------------
INSERT INTO `backtest_results` VALUES (1, 'BTR17790064606407686', 1, 1, '600519', '贵州茅台', 'MA_CROSS', '{\"shortPeriod\":5,\"longPeriod\":20}', '2025-12-01', '2026-05-17', 100000.00, 100000.00, 0.0000, 0.0000, 0.0000, 0.0000, 0, 0, 0.0000, '[{\"date\":\"2026-04-14\",\"value\":100000},{\"date\":\"2026-04-15\",\"value\":100000},{\"date\":\"2026-04-16\",\"value\":100000},{\"date\":\"2026-04-17\",\"value\":100000},{\"date\":\"2026-04-20\",\"value\":100000},{\"date\":\"2026-04-21\",\"value\":100000},{\"date\":\"2026-04-22\",\"value\":100000},{\"date\":\"2026-04-23\",\"value\":100000},{\"date\":\"2026-04-24\",\"value\":100000},{\"date\":\"2026-04-27\",\"value\":100000},{\"date\":\"2026-04-28\",\"value\":100000},{\"date\":\"2026-04-29\",\"value\":100000},{\"date\":\"2026-04-30\",\"value\":100000},{\"date\":\"2026-05-01\",\"value\":100000},{\"date\":\"2026-05-04\",\"value\":100000},{\"date\":\"2026-05-05\",\"value\":100000},{\"date\":\"2026-05-06\",\"value\":100000},{\"date\":\"2026-05-07\",\"value\":100000},{\"date\":\"2026-05-08\",\"value\":100000},{\"date\":\"2026-05-11\",\"value\":100000},{\"date\":\"2026-05-12\",\"value\":100000},{\"date\":\"2026-05-13\",\"value\":100000},{\"date\":\"2026-05-14\",\"value\":100000},{\"date\":\"2026-05-15\",\"value\":100000}]', '[]', NULL, NULL);
INSERT INTO `backtest_results` VALUES (2, 'BTR17790329116376498', 2, 1, '600036', '招商银行', 'MA_CROSSOVER', '{\"fast\":5,\"slow\":20}', '2025-05-17', '2026-05-17', 100000.00, 135438.56, 35.4400, 533.0400, 3.0900, 100.0000, 1, 1, 3.8519, '[{\"date\":\"2026-04-15\",\"value\":100000},{\"date\":\"2026-04-16\",\"value\":100000},{\"date\":\"2026-04-17\",\"value\":100000},{\"date\":\"2026-04-20\",\"value\":100000},{\"date\":\"2026-04-21\",\"value\":100000},{\"date\":\"2026-04-22\",\"value\":100000},{\"date\":\"2026-04-23\",\"value\":100000},{\"date\":\"2026-04-24\",\"value\":100000},{\"date\":\"2026-04-27\",\"value\":100000},{\"date\":\"2026-04-28\",\"value\":99900.10579},{\"date\":\"2026-04-29\",\"value\":98439.76579},{\"date\":\"2026-04-30\",\"value\":98057.29579},{\"date\":\"2026-05-01\",\"value\":102646.93579},{\"date\":\"2026-05-04\",\"value\":103585.72579},{\"date\":\"2026-05-05\",\"value\":103238.02579},{\"date\":\"2026-05-06\",\"value\":103272.79579},{\"date\":\"2026-05-07\",\"value\":134600.56579},{\"date\":\"2026-05-08\",\"value\":131992.81579},{\"date\":\"2026-05-11\",\"value\":135991.36579},{\"date\":\"2026-05-12\",\"value\":135887.05579},{\"date\":\"2026-05-13\",\"value\":135921.82579},{\"date\":\"2026-05-14\",\"value\":131784.19579},{\"date\":\"2026-05-15\",\"value\":135574.12579}]', '[{\"entryPrice\":28.73,\"exitPrice\":38.99,\"quantity\":3477,\"entryDate\":\"2026-04-28\",\"exitDate\":\"2026-05-15\",\"return\":35.476100,\"pnl\":35438.55756}]', NULL, NULL);

-- ----------------------------
-- Table structure for backtest_strategies
-- ----------------------------
DROP TABLE IF EXISTS `backtest_strategies`;
CREATE TABLE `backtest_strategies`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `strategy_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_id` bigint NOT NULL,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `strategy_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `stock_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `parameter_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `strategy_no`(`strategy_no` ASC) USING BTREE,
  INDEX `idx_bs_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of backtest_strategies
-- ----------------------------
INSERT INTO `backtest_strategies` VALUES (1, 'BTS17790064602298111', 1, '自定义策略', 'MA_CROSS', '600519', '{\"shortPeriod\":5,\"longPeriod\":20}', NULL, NULL);
INSERT INTO `backtest_strategies` VALUES (2, 'BTS17790329116162006', 1, '均线交叉策略', 'MA_CROSSOVER', '600036', '{\"fast\":5,\"slow\":20}', NULL, NULL);

-- ----------------------------
-- Table structure for conditional_orders
-- ----------------------------
DROP TABLE IF EXISTS `conditional_orders`;
CREATE TABLE `conditional_orders`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '条件单编号',
  `user_id` bigint NOT NULL,
  `fund_account_id` bigint NOT NULL,
  `stock_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `stock_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `condition_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'STOP_LOSS/TAKE_PROFIT',
  `trigger_price` decimal(10, 2) NOT NULL,
  `order_price` decimal(10, 2) NOT NULL,
  `quantity` int NOT NULL,
  `direction` int NOT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'ACTIVE',
  `triggered_order_id` bigint NULL DEFAULT NULL,
  `fail_reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_co_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_co_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of conditional_orders
-- ----------------------------
INSERT INTO `conditional_orders` VALUES (1, 'COT17789675698874731', 1, 1, '600519', '贵州茅台', 'STOP_LOSS', 1600.00, 1590.00, 100, 1, 'CANCELLED', NULL, NULL, '2026-05-13 14:30:21', '2026-05-17 16:29:21');
INSERT INTO `conditional_orders` VALUES (2, 'COT17790005090245860', 1, 1, '600519', '贵州茅台', 'STOP_LOSS', 1500.00, 1490.00, 100, 1, 'ACTIVE', NULL, NULL, '2026-05-13 14:30:21', '2026-05-17 16:29:21');
INSERT INTO `conditional_orders` VALUES (3, 'COT17790005260056786', 1, 1, '600519', '贵州茅台', 'TAKE_PROFIT', 1800.00, 1790.00, 100, 1, 'ACTIVE', NULL, NULL, '2026-05-13 14:30:21', '2026-05-17 16:29:21');
INSERT INTO `conditional_orders` VALUES (4, 'COT17790064088388020', 1, 1, '600036', '招商银行', 'TAKE_PROFIT', 36.00, 38.98, 100, 2, 'EXPIRED', NULL, '价格偏差过大', '2026-05-13 14:30:21', '2026-05-17 16:29:21');

-- ----------------------------
-- Table structure for financial_data
-- ----------------------------
DROP TABLE IF EXISTS `financial_data`;
CREATE TABLE `financial_data`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `stock_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '股票代码',
  `report_type` tinyint NOT NULL COMMENT '报告类型：1-季报，2-半年报，3-年报',
  `report_date` date NOT NULL COMMENT '报告日期',
  `revenue` decimal(18, 2) NULL DEFAULT NULL COMMENT '营业收入',
  `net_profit` decimal(18, 2) NULL DEFAULT NULL COMMENT '净利润',
  `total_assets` decimal(18, 2) NULL DEFAULT NULL COMMENT '总资产',
  `total_liabilities` decimal(18, 2) NULL DEFAULT NULL COMMENT '总负债',
  `shareholders_equity` decimal(18, 2) NULL DEFAULT NULL COMMENT '股东权益',
  `roe` decimal(8, 4) NULL DEFAULT NULL COMMENT '净资产收益率',
  `eps` decimal(10, 4) NULL DEFAULT NULL COMMENT '每股收益',
  `pe_ratio` decimal(10, 2) NULL DEFAULT NULL COMMENT '市盈率',
  `pb_ratio` decimal(10, 2) NULL DEFAULT NULL COMMENT '市净率',
  `operating_cash_flow` decimal(18, 2) NULL DEFAULT NULL,
  `investing_cash_flow` decimal(18, 2) NULL DEFAULT NULL,
  `financing_cash_flow` decimal(18, 2) NULL DEFAULT NULL,
  `net_cash_flow` decimal(18, 2) NULL DEFAULT NULL,
  `total_shares` decimal(18, 2) NULL DEFAULT NULL,
  `dividend_yield` decimal(8, 4) NULL DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_stock_code`(`stock_code` ASC) USING BTREE,
  INDEX `idx_report_date`(`report_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 65 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of financial_data
-- ----------------------------
INSERT INTO `financial_data` VALUES (1, '600519', 1, '2023-03-31', 393.80, 207.90, 2800.00, 480.00, 2150.00, 0.0967, 16.5500, 35.20, 10.50, 210.00, -30.00, -160.00, 20.00, 12.56, 0.0180, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (2, '600519', 2, '2023-06-30', 316.10, 151.90, 2850.00, 450.00, 2200.00, 0.0691, 12.0900, 33.80, 10.20, 170.00, -25.00, -140.00, 5.00, 12.56, 0.0180, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (3, '600519', 1, '2023-09-30', 343.30, 169.00, 2920.00, 460.00, 2280.00, 0.0741, 13.4500, 32.50, 10.00, 185.00, -28.00, -150.00, 7.00, 12.56, 0.0180, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (4, '600519', 3, '2023-12-31', 452.20, 218.60, 3000.00, 500.00, 2350.00, 0.0930, 17.4000, 31.00, 9.80, 240.00, -35.00, -190.00, 15.00, 12.56, 0.0180, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (5, '600519', 1, '2024-03-31', 421.50, 228.50, 3100.00, 490.00, 2450.00, 0.0933, 18.1900, 33.50, 11.00, 225.00, -32.00, -170.00, 23.00, 12.56, 0.0180, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (6, '600519', 2, '2024-06-30', 345.80, 167.30, 3180.00, 460.00, 2520.00, 0.0664, 13.3200, 32.00, 10.80, 185.00, -28.00, -150.00, 7.00, 12.56, 0.0180, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (7, '600519', 1, '2024-09-30', 378.90, 186.10, 3260.00, 470.00, 2600.00, 0.0716, 14.8200, 31.50, 10.30, 200.00, -30.00, -160.00, 10.00, 12.56, 0.0180, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (8, '600519', 3, '2024-12-31', 502.70, 240.50, 3350.00, 510.00, 2680.00, 0.0897, 19.1500, 30.00, 9.50, 260.00, -38.00, -200.00, 22.00, 12.56, 0.0180, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (9, '000858', 1, '2023-03-31', 205.50, 78.60, 1450.00, 250.00, 1100.00, 0.0715, 2.0200, 28.50, 6.50, 85.00, -15.00, -50.00, 20.00, 38.82, 0.0250, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (10, '000858', 2, '2023-06-30', 168.20, 58.30, 1480.00, 240.00, 1130.00, 0.0516, 1.5000, 27.00, 6.30, 65.00, -12.00, -40.00, 13.00, 38.82, 0.0250, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (11, '000858', 1, '2023-09-30', 185.70, 65.00, 1520.00, 260.00, 1150.00, 0.0565, 1.6700, 26.50, 6.00, 72.00, -14.00, -45.00, 13.00, 38.82, 0.0250, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (12, '000858', 3, '2023-12-31', 222.80, 82.10, 1600.00, 280.00, 1200.00, 0.0684, 2.1100, 25.00, 5.80, 88.00, -18.00, -55.00, 15.00, 38.82, 0.0250, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (13, '000858', 1, '2024-03-31', 218.30, 82.50, 1650.00, 270.00, 1250.00, 0.0660, 2.1300, 27.50, 6.70, 82.00, -14.00, -48.00, 20.00, 38.82, 0.0250, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (14, '000858', 2, '2024-06-30', 181.50, 63.20, 1700.00, 260.00, 1280.00, 0.0494, 1.6300, 26.00, 6.50, 68.00, -12.00, -40.00, 16.00, 38.82, 0.0250, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (15, '000858', 1, '2024-09-30', 198.70, 69.80, 1750.00, 280.00, 1320.00, 0.0529, 1.8000, 25.50, 6.20, 76.00, -15.00, -48.00, 13.00, 38.82, 0.0250, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (16, '000858', 3, '2024-12-31', 244.60, 88.00, 1820.00, 300.00, 1380.00, 0.0638, 2.2700, 24.00, 6.00, 92.00, -20.00, -58.00, 14.00, 38.82, 0.0250, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (17, '600036', 1, '2023-03-31', 845.20, 388.40, 109800.00, 101500.00, 8300.00, 0.0468, 1.5400, 6.20, 0.85, 120.00, -80.00, -30.00, 10.00, 252.20, 0.0520, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (18, '600036', 2, '2023-06-30', 826.30, 369.70, 110500.00, 102200.00, 8300.00, 0.0446, 1.4700, 5.90, 0.82, -50.00, -30.00, 100.00, 20.00, 252.20, 0.0520, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (19, '600036', 1, '2023-09-30', 838.60, 378.50, 111300.00, 102900.00, 8400.00, 0.0451, 1.5000, 5.80, 0.80, 200.00, -60.00, -120.00, 20.00, 252.20, 0.0520, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (20, '600036', 3, '2023-12-31', 868.90, 395.20, 112500.00, 103800.00, 8700.00, 0.0454, 1.5700, 5.50, 0.78, 380.00, -90.00, -250.00, 40.00, 252.20, 0.0520, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (21, '600036', 1, '2024-03-31', 864.30, 391.20, 113600.00, 104700.00, 8900.00, 0.0439, 1.5500, 6.00, 0.88, 130.00, -85.00, -32.00, 13.00, 252.20, 0.0520, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (22, '600036', 2, '2024-06-30', 845.50, 378.50, 114800.00, 105600.00, 9200.00, 0.0411, 1.5000, 5.80, 0.85, -60.00, -35.00, 120.00, 25.00, 252.20, 0.0520, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (23, '600036', 1, '2024-09-30', 858.20, 385.30, 116000.00, 106500.00, 9500.00, 0.0406, 1.5300, 5.60, 0.83, 220.00, -65.00, -135.00, 20.00, 252.20, 0.0520, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (24, '600036', 3, '2024-12-31', 888.60, 402.80, 117500.00, 107800.00, 9700.00, 0.0415, 1.6000, 5.30, 0.80, 400.00, -100.00, -270.00, 30.00, 252.20, 0.0520, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (25, '601318', 1, '2023-03-31', 2028.50, 209.80, 110000.00, 102000.00, 8000.00, 0.0262, 1.1500, 10.50, 1.05, 350.00, -200.00, -120.00, 30.00, 182.10, 0.0550, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (26, '601318', 2, '2023-06-30', 1950.80, 185.60, 110800.00, 102500.00, 8300.00, 0.0224, 1.0200, 10.00, 1.02, 280.00, -180.00, -80.00, 20.00, 182.10, 0.0550, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (27, '601318', 1, '2023-09-30', 1985.30, 198.20, 111500.00, 103000.00, 8500.00, 0.0233, 1.0900, 9.80, 0.98, 310.00, -190.00, -100.00, 20.00, 182.10, 0.0550, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (28, '601318', 3, '2023-12-31', 2105.60, 225.30, 112800.00, 104000.00, 8800.00, 0.0256, 1.2400, 9.50, 0.95, 420.00, -220.00, -160.00, 40.00, 182.10, 0.0550, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (29, '601318', 1, '2024-03-31', 2150.20, 228.50, 113900.00, 104800.00, 9100.00, 0.0251, 1.2500, 10.20, 1.10, 380.00, -210.00, -130.00, 40.00, 182.10, 0.0550, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (30, '601318', 2, '2024-06-30', 2085.60, 202.30, 115000.00, 105500.00, 9500.00, 0.0213, 1.1100, 9.80, 1.05, 300.00, -190.00, -85.00, 25.00, 182.10, 0.0550, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (31, '601318', 1, '2024-09-30', 2120.80, 218.50, 116200.00, 106300.00, 9900.00, 0.0221, 1.2000, 9.50, 1.02, 330.00, -200.00, -105.00, 25.00, 182.10, 0.0550, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (32, '601318', 3, '2024-12-31', 2250.30, 248.60, 117800.00, 107500.00, 10300.00, 0.0241, 1.3700, 9.00, 0.98, 450.00, -240.00, -175.00, 35.00, 182.10, 0.0550, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (33, '002594', 1, '2023-03-31', 1305.80, 32.80, 6200.00, 4800.00, 1400.00, 0.0234, 1.1300, 42.50, 5.50, 180.00, -150.00, -20.00, 10.00, 29.10, 0.0060, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (34, '002594', 2, '2023-06-30', 1398.20, 39.50, 6400.00, 4950.00, 1450.00, 0.0272, 1.3600, 40.00, 5.20, 200.00, -180.00, -10.00, 10.00, 29.10, 0.0060, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (35, '002594', 1, '2023-09-30', 1520.50, 45.20, 6600.00, 5100.00, 1500.00, 0.0301, 1.5500, 38.50, 5.00, 220.00, -200.00, -15.00, 5.00, 29.10, 0.0060, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (36, '002594', 3, '2023-12-31', 1658.30, 50.50, 6800.00, 5250.00, 1550.00, 0.0326, 1.7400, 36.00, 4.80, 260.00, -230.00, -20.00, 10.00, 29.10, 0.0060, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (37, '002594', 1, '2024-03-31', 1428.60, 38.20, 7100.00, 5400.00, 1700.00, 0.0225, 1.3100, 44.00, 5.80, 195.00, -165.00, -22.00, 8.00, 29.10, 0.0060, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (38, '002594', 2, '2024-06-30', 1520.30, 45.80, 7350.00, 5580.00, 1770.00, 0.0259, 1.5700, 41.50, 5.50, 220.00, -195.00, -12.00, 13.00, 29.10, 0.0060, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (39, '002594', 1, '2024-09-30', 1685.20, 52.50, 7580.00, 5750.00, 1830.00, 0.0287, 1.8000, 39.50, 5.20, 245.00, -220.00, -18.00, 7.00, 29.10, 0.0060, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (40, '002594', 3, '2024-12-31', 1820.50, 58.30, 7850.00, 5920.00, 1930.00, 0.0302, 2.0000, 37.00, 5.00, 285.00, -255.00, -22.00, 8.00, 29.10, 0.0060, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (41, '000333', 1, '2023-03-31', 898.50, 78.20, 4500.00, 2800.00, 1700.00, 0.0460, 1.1200, 14.50, 3.50, 95.00, -40.00, -50.00, 5.00, 69.80, 0.0380, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (42, '000333', 2, '2023-06-30', 935.20, 85.60, 4600.00, 2850.00, 1750.00, 0.0489, 1.2300, 14.00, 3.40, 105.00, -45.00, -55.00, 5.00, 69.80, 0.0380, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (43, '000333', 1, '2023-09-30', 958.80, 88.50, 4700.00, 2900.00, 1800.00, 0.0492, 1.2700, 13.80, 3.30, 110.00, -48.00, -58.00, 4.00, 69.80, 0.0380, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (44, '000333', 3, '2023-12-31', 988.50, 95.20, 4800.00, 2980.00, 1820.00, 0.0523, 1.3600, 13.00, 3.20, 130.00, -55.00, -65.00, 10.00, 69.80, 0.0380, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (45, '000333', 1, '2024-03-31', 968.50, 85.30, 4950.00, 3020.00, 1930.00, 0.0442, 1.2200, 15.00, 3.80, 102.00, -42.00, -52.00, 8.00, 69.80, 0.0380, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (46, '000333', 2, '2024-06-30', 998.80, 92.50, 5050.00, 3080.00, 1970.00, 0.0470, 1.3300, 14.20, 3.60, 112.00, -48.00, -58.00, 6.00, 69.80, 0.0380, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (47, '000333', 1, '2024-09-30', 1025.30, 96.20, 5180.00, 3150.00, 2030.00, 0.0474, 1.3800, 13.80, 3.50, 118.00, -52.00, -60.00, 6.00, 69.80, 0.0380, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (48, '000333', 3, '2024-12-31', 1058.60, 103.80, 5300.00, 3220.00, 2080.00, 0.0499, 1.4900, 13.00, 3.30, 140.00, -60.00, -68.00, 12.00, 69.80, 0.0380, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (49, '600887', 1, '2023-03-31', 285.30, 25.80, 1450.00, 850.00, 600.00, 0.0430, 0.4100, 20.50, 3.80, 35.00, -18.00, -12.00, 5.00, 63.70, 0.0320, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (50, '600887', 2, '2023-06-30', 298.50, 22.30, 1480.00, 860.00, 620.00, 0.0360, 0.3500, 19.80, 3.60, 30.00, -15.00, -10.00, 5.00, 63.70, 0.0320, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (51, '600887', 1, '2023-09-30', 305.80, 24.50, 1500.00, 870.00, 630.00, 0.0389, 0.3800, 19.50, 3.50, 32.00, -16.00, -11.00, 5.00, 63.70, 0.0320, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (52, '600887', 3, '2023-12-31', 320.50, 28.20, 1550.00, 890.00, 660.00, 0.0427, 0.4400, 18.50, 3.40, 38.00, -20.00, -14.00, 4.00, 63.70, 0.0320, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (53, '600887', 1, '2024-03-31', 298.60, 27.30, 1580.00, 880.00, 700.00, 0.0390, 0.4300, 21.00, 4.00, 37.00, -18.00, -12.00, 7.00, 63.70, 0.0320, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (54, '600887', 2, '2024-06-30', 312.50, 24.50, 1600.00, 890.00, 710.00, 0.0345, 0.3800, 20.00, 3.80, 32.00, -16.00, -11.00, 5.00, 63.70, 0.0320, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (55, '600887', 1, '2024-09-30', 318.80, 26.80, 1630.00, 900.00, 730.00, 0.0367, 0.4200, 19.50, 3.70, 34.00, -17.00, -12.00, 5.00, 63.70, 0.0320, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (56, '600887', 3, '2024-12-31', 335.20, 30.50, 1680.00, 920.00, 760.00, 0.0401, 0.4800, 18.50, 3.50, 40.00, -22.00, -15.00, 3.00, 63.70, 0.0320, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (57, '000001', 1, '2023-03-31', 420.30, 148.50, 54000.00, 50000.00, 4000.00, 0.0371, 0.7700, 5.80, 0.68, 80.00, -40.00, -30.00, 10.00, 194.10, 0.0350, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (58, '000001', 2, '2023-06-30', 408.60, 135.20, 54500.00, 50400.00, 4100.00, 0.0330, 0.7000, 5.50, 0.65, -20.00, -15.00, 50.00, 15.00, 194.10, 0.0350, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (59, '000001', 1, '2023-09-30', 415.20, 140.30, 55000.00, 50800.00, 4200.00, 0.0334, 0.7200, 5.40, 0.63, 100.00, -30.00, -60.00, 10.00, 194.10, 0.0350, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (60, '000001', 3, '2023-12-31', 435.80, 152.50, 55800.00, 51500.00, 4300.00, 0.0355, 0.7900, 5.10, 0.60, 180.00, -50.00, -110.00, 20.00, 194.10, 0.0350, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (61, '000001', 1, '2024-03-31', 432.50, 150.20, 56500.00, 52000.00, 4500.00, 0.0334, 0.7700, 5.60, 0.70, 85.00, -42.00, -32.00, 11.00, 194.10, 0.0350, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (62, '000001', 2, '2024-06-30', 420.80, 142.50, 57200.00, 52500.00, 4700.00, 0.0303, 0.7300, 5.40, 0.68, -25.00, -18.00, 55.00, 12.00, 194.10, 0.0350, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (63, '000001', 1, '2024-09-30', 428.50, 147.80, 58000.00, 53200.00, 4800.00, 0.0308, 0.7600, 5.20, 0.66, 110.00, -35.00, -65.00, 10.00, 194.10, 0.0350, '2026-05-16 20:51:46', '2026-05-16 23:02:55');
INSERT INTO `financial_data` VALUES (64, '000001', 3, '2024-12-31', 448.30, 158.60, 59000.00, 54000.00, 5000.00, 0.0317, 0.8200, 5.00, 0.62, 195.00, -55.00, -120.00, 20.00, 194.10, 0.0350, '2026-05-16 20:51:46', '2026-05-16 23:02:55');

-- ----------------------------
-- Table structure for fund_accounts
-- ----------------------------
DROP TABLE IF EXISTS `fund_accounts`;
CREATE TABLE `fund_accounts`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `account_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '资金账号',
  `balance` decimal(18, 2) NULL DEFAULT 0.00 COMMENT '账户余额',
  `frozen_balance` decimal(18, 2) NULL DEFAULT 0.00 COMMENT '冻结金额',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_account_no`(`account_no` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fund_accounts
-- ----------------------------
INSERT INTO `fund_accounts` VALUES (1, 1, 'FA10000001', 85770.00, 11550.00, 1, '2026-05-16 16:41:59', '2026-05-16 22:55:39');
INSERT INTO `fund_accounts` VALUES (2, 2, 'FA10000002', 200000.00, 0.00, 1, '2026-05-16 16:41:59', '2026-05-16 16:41:59');
INSERT INTO `fund_accounts` VALUES (3, 4, 'FA17789281638049859', 100000.00, 0.00, 1, NULL, NULL);

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `fund_account_id` bigint NOT NULL COMMENT '资金账户ID',
  `stock_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '股票代码',
  `stock_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '股票名称',
  `direction` tinyint NOT NULL COMMENT '方向：1-买入，2-卖出',
  `price` decimal(10, 3) NOT NULL COMMENT '委托价格',
  `quantity` int NOT NULL COMMENT '委托数量',
  `amount` decimal(18, 2) NOT NULL COMMENT '委托金额',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1-待成交，2-已成交，3-已取消，4-已拒绝',
  `order_type` tinyint NULL DEFAULT 1 COMMENT '订单类型：1-市价，2-限价',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `conditional_order_id` bigint NULL DEFAULT NULL COMMENT '关联的条件单ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_stock_code`(`stock_code` ASC) USING BTREE,
  INDEX `idx_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (1, 'ORD10000001', 1, 1, '600036', '招商银行', 1, 35.500, 100, 3550.00, 2, 1, '2026-05-16 16:41:59', '2026-05-16 16:41:59', NULL);
INSERT INTO `orders` VALUES (2, 'ORD10000002', 1, 1, '600519', '贵州茅台', 1, 1680.000, 10, 16800.00, 1, 1, '2026-05-16 16:41:59', '2026-05-16 16:41:59', NULL);
INSERT INTO `orders` VALUES (3, 'ORD10000003', 2, 2, '000858', '五粮液', 1, 145.300, 50, 7265.00, 1, 2, '2026-05-16 16:41:59', '2026-05-16 16:41:59', NULL);
INSERT INTO `orders` VALUES (4, 'ORD17789306791317280', 1, 1, '600519', '贵州茅台', 1, 1680.000, 10, 16800.00, 2, 1, NULL, NULL, NULL);
INSERT INTO `orders` VALUES (5, 'ORD17789307661195398', 1, 1, '600036', '招商银行', 1, 35.500, 100, 3550.00, 3, 1, NULL, NULL, NULL);
INSERT INTO `orders` VALUES (9, 'ORD17789450752677157', 1, 1, '000858', '五粮液', 1, 142.300, 100, 14230.00, 2, 1, NULL, NULL, NULL);
INSERT INTO `orders` VALUES (10, 'ORD17790063616294839', 1, 1, '600036', '招商银行', 1, 38.500, 100, 3850.00, 2, 1, NULL, NULL, NULL);
INSERT INTO `orders` VALUES (11, 'ORD17790063824997635', 1, 1, '600036', '招商银行', 2, 38.500, 200, 7700.00, 2, 1, NULL, NULL, NULL);
INSERT INTO `orders` VALUES (12, 'ORD17790325865633593', 1, 1, '600036', '招商银行', 1, 38.500, 100, 3850.00, 2, 1, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for positions
-- ----------------------------
DROP TABLE IF EXISTS `positions`;
CREATE TABLE `positions`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `fund_account_id` bigint NOT NULL COMMENT '资金账户ID',
  `stock_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '股票代码',
  `stock_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '股票名称',
  `total_quantity` int NOT NULL DEFAULT 0 COMMENT '总持仓数量',
  `available_quantity` int NOT NULL DEFAULT 0 COMMENT '可用数量',
  `frozen_quantity` int NOT NULL DEFAULT 0 COMMENT '冻结数量（挂单）',
  `avg_cost` decimal(10, 3) NOT NULL DEFAULT 0.000 COMMENT '平均成本',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_stock`(`user_id` ASC, `stock_code` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_stock_code`(`stock_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of positions
-- ----------------------------
INSERT INTO `positions` VALUES (1, 1, 1, '600036', '招商银行', 200, 200, 0, 37.500, '2026-05-16 16:41:59', '2026-05-16 16:41:59');
INSERT INTO `positions` VALUES (2, 1, 1, '600519', '贵州茅台', 20, 20, 0, 1620.000, '2026-05-16 16:41:59', '2026-05-16 22:54:54');
INSERT INTO `positions` VALUES (3, 2, 2, '000858', '五粮液', 50, 50, 0, 138.000, '2026-05-16 16:41:59', '2026-05-16 22:54:54');
INSERT INTO `positions` VALUES (4, 1, 1, '000858', '五粮液', 100, 100, 0, 142.300, NULL, NULL);

-- ----------------------------
-- Table structure for price_alert_notifications
-- ----------------------------
DROP TABLE IF EXISTS `price_alert_notifications`;
CREATE TABLE `price_alert_notifications`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `alert_id` bigint NOT NULL,
  `stock_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `stock_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `alert_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `target_price` decimal(10, 2) NOT NULL,
  `triggered_price` decimal(10, 2) NOT NULL,
  `is_read` tinyint NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_pan_user_read`(`user_id` ASC, `is_read` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of price_alert_notifications
-- ----------------------------
INSERT INTO `price_alert_notifications` VALUES (1, 1, 4, '000858', '五粮液', 'PRICE_ABOVE', 120.00, 142.30, 1, '2026-06-13 13:30:21', '2026-05-17 16:16:59');
INSERT INTO `price_alert_notifications` VALUES (2, 1, 5, '600519', '贵州茅台', 'PRICE_ABOVE', 1600.00, 1650.00, 0, NULL, NULL);

-- ----------------------------
-- Table structure for price_alerts
-- ----------------------------
DROP TABLE IF EXISTS `price_alerts`;
CREATE TABLE `price_alerts`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `alert_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_id` bigint NOT NULL,
  `stock_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `stock_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `alert_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `target_price` decimal(10, 2) NOT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'ACTIVE',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `alert_no`(`alert_no` ASC) USING BTREE,
  INDEX `idx_pa_user_status`(`user_id` ASC, `status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of price_alerts
-- ----------------------------
INSERT INTO `price_alerts` VALUES (1, 'ALT17789675699929672', 1, '600519', '贵州茅台', 'PRICE_ABOVE', 2000.00, 'CANCELLED', '2026-05-11 08:24:33', '2026-05-17 16:34:02');
INSERT INTO `price_alerts` VALUES (2, 'ALT17789688513296537', 1, '000001', '平安银行', 'PRICE_BELOW', 10.00, 'ACTIVE', '2026-05-11 08:24:33', '2026-05-17 16:34:02');
INSERT INTO `price_alerts` VALUES (3, 'ALT17790000443094348', 1, '600036', '招商银行', 'PRICE_ABOVE', 50.00, 'ACTIVE', '2026-05-11 08:24:33', '2026-05-17 16:34:02');
INSERT INTO `price_alerts` VALUES (4, 'ALT17790004087510408', 1, '000858', '五粮液', 'PRICE_ABOVE', 120.00, 'TRIGGERED', '2026-05-11 08:24:33', '2026-05-17 16:34:02');
INSERT INTO `price_alerts` VALUES (5, 'ALT17790330139234368', 1, '600519', '贵州茅台', 'PRICE_ABOVE', 1600.00, 'TRIGGERED', NULL, '2026-05-17 23:50:21');

-- ----------------------------
-- Table structure for stock_kline
-- ----------------------------
DROP TABLE IF EXISTS `stock_kline`;
CREATE TABLE `stock_kline`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `stock_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `trade_date` date NOT NULL,
  `open_price` decimal(10, 2) NULL DEFAULT NULL,
  `high_price` decimal(10, 2) NULL DEFAULT NULL,
  `low_price` decimal(10, 2) NULL DEFAULT NULL,
  `close_price` decimal(10, 2) NULL DEFAULT NULL,
  `volume` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_stock_date`(`stock_code` ASC, `trade_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 353 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of stock_kline
-- ----------------------------
INSERT INTO `stock_kline` VALUES (1, '600036', '2026-03-17', 31.39, 31.47, 31.33, 31.36, 21654492);
INSERT INTO `stock_kline` VALUES (2, '600036', '2026-03-18', 31.11, 31.37, 30.77, 31.19, 7791741);
INSERT INTO `stock_kline` VALUES (3, '600036', '2026-03-19', 31.18, 31.89, 30.45, 30.91, 17738124);
INSERT INTO `stock_kline` VALUES (4, '600036', '2026-03-20', 30.16, 30.85, 30.14, 30.69, 24169695);
INSERT INTO `stock_kline` VALUES (5, '600036', '2026-03-23', 29.91, 30.70, 29.49, 30.52, 33793026);
INSERT INTO `stock_kline` VALUES (6, '600036', '2026-03-24', 30.87, 31.01, 30.47, 30.67, 31751707);
INSERT INTO `stock_kline` VALUES (7, '600036', '2026-03-25', 31.01, 31.21, 30.99, 31.18, 10358428);
INSERT INTO `stock_kline` VALUES (8, '600036', '2026-03-26', 31.00, 31.16, 30.72, 30.82, 34410296);
INSERT INTO `stock_kline` VALUES (9, '600036', '2026-03-27', 29.78, 30.31, 28.80, 29.22, 20906335);
INSERT INTO `stock_kline` VALUES (10, '600036', '2026-03-30', 29.16, 29.27, 28.95, 28.95, 20675739);
INSERT INTO `stock_kline` VALUES (11, '600036', '2026-03-31', 30.26, 30.71, 29.66, 29.93, 16348698);
INSERT INTO `stock_kline` VALUES (12, '600036', '2026-04-01', 29.63, 29.97, 29.49, 29.77, 3038379);
INSERT INTO `stock_kline` VALUES (13, '600036', '2026-04-02', 29.44, 29.62, 29.17, 29.22, 19164618);
INSERT INTO `stock_kline` VALUES (14, '600036', '2026-04-03', 28.77, 29.22, 28.72, 28.84, 38749305);
INSERT INTO `stock_kline` VALUES (15, '600036', '2026-04-06', 28.75, 29.31, 28.54, 28.88, 17213536);
INSERT INTO `stock_kline` VALUES (16, '600036', '2026-04-07', 28.03, 29.17, 27.71, 28.59, 28059825);
INSERT INTO `stock_kline` VALUES (17, '600036', '2026-04-08', 28.05, 28.18, 27.92, 27.97, 17778538);
INSERT INTO `stock_kline` VALUES (18, '600036', '2026-04-09', 28.23, 28.25, 28.23, 28.24, 25174680);
INSERT INTO `stock_kline` VALUES (19, '600036', '2026-04-10', 28.71, 29.29, 28.27, 28.63, 34720415);
INSERT INTO `stock_kline` VALUES (20, '600036', '2026-04-13', 28.96, 28.99, 28.62, 28.83, 26776507);
INSERT INTO `stock_kline` VALUES (21, '600036', '2026-04-14', 27.62, 28.33, 27.36, 28.27, 25770212);
INSERT INTO `stock_kline` VALUES (22, '600036', '2026-04-15', 28.72, 29.10, 28.48, 28.58, 15313673);
INSERT INTO `stock_kline` VALUES (23, '600036', '2026-04-16', 29.21, 29.65, 28.48, 28.52, 39278242);
INSERT INTO `stock_kline` VALUES (24, '600036', '2026-04-17', 28.27, 29.60, 28.15, 28.96, 35590824);
INSERT INTO `stock_kline` VALUES (25, '600036', '2026-04-20', 27.82, 28.12, 27.11, 27.82, 33535094);
INSERT INTO `stock_kline` VALUES (26, '600036', '2026-04-21', 27.71, 28.23, 27.49, 27.96, 35659965);
INSERT INTO `stock_kline` VALUES (27, '600036', '2026-04-22', 27.79, 28.86, 27.74, 28.24, 34996735);
INSERT INTO `stock_kline` VALUES (28, '600036', '2026-04-23', 28.57, 29.13, 28.53, 28.65, 15470502);
INSERT INTO `stock_kline` VALUES (29, '600036', '2026-04-24', 29.58, 30.02, 28.42, 28.86, 5667017);
INSERT INTO `stock_kline` VALUES (30, '600036', '2026-04-27', 29.31, 29.62, 28.41, 28.96, 16707602);
INSERT INTO `stock_kline` VALUES (31, '600036', '2026-04-28', 28.95, 29.10, 28.42, 28.73, 7868286);
INSERT INTO `stock_kline` VALUES (32, '600036', '2026-04-29', 28.23, 28.32, 28.15, 28.31, 36949347);
INSERT INTO `stock_kline` VALUES (33, '600036', '2026-04-30', 27.92, 28.32, 27.28, 28.20, 23301581);
INSERT INTO `stock_kline` VALUES (34, '600036', '2026-05-01', 29.13, 29.52, 28.64, 29.52, 23090967);
INSERT INTO `stock_kline` VALUES (35, '600036', '2026-05-04', 30.50, 31.10, 29.67, 29.79, 21908343);
INSERT INTO `stock_kline` VALUES (36, '600036', '2026-05-05', 29.57, 30.07, 29.41, 29.69, 35784817);
INSERT INTO `stock_kline` VALUES (37, '600036', '2026-05-06', 29.55, 29.71, 29.49, 29.70, 11779158);
INSERT INTO `stock_kline` VALUES (38, '600036', '2026-05-07', 38.46, 38.90, 38.46, 38.71, 8049529);
INSERT INTO `stock_kline` VALUES (39, '600036', '2026-05-08', 38.08, 38.18, 37.95, 37.96, 27471348);
INSERT INTO `stock_kline` VALUES (40, '600036', '2026-05-11', 38.23, 39.31, 37.99, 39.11, 9898363);
INSERT INTO `stock_kline` VALUES (41, '600036', '2026-05-12', 38.02, 39.35, 37.83, 39.08, 14881783);
INSERT INTO `stock_kline` VALUES (42, '600036', '2026-05-13', 39.02, 39.29, 38.84, 39.09, 19654221);
INSERT INTO `stock_kline` VALUES (43, '600036', '2026-05-14', 37.67, 38.73, 37.20, 37.90, 23481012);
INSERT INTO `stock_kline` VALUES (44, '600036', '2026-05-15', 39.68, 40.10, 38.70, 38.99, 7658747);
INSERT INTO `stock_kline` VALUES (45, '600519', '2026-03-17', 1455.34, 1487.79, 1452.80, 1467.13, 20529855);
INSERT INTO `stock_kline` VALUES (46, '600519', '2026-03-18', 1505.40, 1510.74, 1499.19, 1509.19, 24050528);
INSERT INTO `stock_kline` VALUES (47, '600519', '2026-03-19', 1427.70, 1430.34, 1422.78, 1429.84, 37891263);
INSERT INTO `stock_kline` VALUES (48, '600519', '2026-03-20', 1433.00, 1493.15, 1405.40, 1462.61, 27442489);
INSERT INTO `stock_kline` VALUES (49, '600519', '2026-03-23', 1372.55, 1403.43, 1337.28, 1398.55, 37032415);
INSERT INTO `stock_kline` VALUES (50, '600519', '2026-03-24', 1435.11, 1440.48, 1425.48, 1427.79, 31215675);
INSERT INTO `stock_kline` VALUES (51, '600519', '2026-03-25', 1433.38, 1446.99, 1386.35, 1414.30, 23392702);
INSERT INTO `stock_kline` VALUES (52, '600519', '2026-03-26', 1401.06, 1412.15, 1383.37, 1391.24, 32672841);
INSERT INTO `stock_kline` VALUES (53, '600519', '2026-03-27', 1351.76, 1399.48, 1342.05, 1376.94, 25494103);
INSERT INTO `stock_kline` VALUES (54, '600519', '2026-03-30', 1345.89, 1351.28, 1337.91, 1348.21, 12888757);
INSERT INTO `stock_kline` VALUES (55, '600519', '2026-03-31', 1367.17, 1378.43, 1352.62, 1368.85, 16048327);
INSERT INTO `stock_kline` VALUES (56, '600519', '2026-04-01', 1385.26, 1389.91, 1351.00, 1372.67, 10152952);
INSERT INTO `stock_kline` VALUES (57, '600519', '2026-04-02', 1360.72, 1396.04, 1329.01, 1376.78, 11144237);
INSERT INTO `stock_kline` VALUES (58, '600519', '2026-04-03', 1343.54, 1380.44, 1330.27, 1358.42, 32700599);
INSERT INTO `stock_kline` VALUES (59, '600519', '2026-04-06', 1313.40, 1347.16, 1296.02, 1326.20, 34854862);
INSERT INTO `stock_kline` VALUES (60, '600519', '2026-04-07', 1281.63, 1305.23, 1265.21, 1299.37, 35520755);
INSERT INTO `stock_kline` VALUES (61, '600519', '2026-04-08', 1287.88, 1290.73, 1280.64, 1285.46, 12286126);
INSERT INTO `stock_kline` VALUES (62, '600519', '2026-04-09', 1273.08, 1275.47, 1266.75, 1271.38, 25205074);
INSERT INTO `stock_kline` VALUES (63, '600519', '2026-04-10', 1258.06, 1265.47, 1237.77, 1246.59, 28526938);
INSERT INTO `stock_kline` VALUES (64, '600519', '2026-04-13', 1234.11, 1248.57, 1215.39, 1240.38, 31120042);
INSERT INTO `stock_kline` VALUES (65, '600519', '2026-04-14', 1187.08, 1209.95, 1172.49, 1201.75, 29095765);
INSERT INTO `stock_kline` VALUES (66, '600519', '2026-04-15', 1186.57, 1195.68, 1179.10, 1193.04, 11564956);
INSERT INTO `stock_kline` VALUES (67, '600519', '2026-04-16', 1200.47, 1225.75, 1185.96, 1222.91, 33984058);
INSERT INTO `stock_kline` VALUES (68, '600519', '2026-04-17', 1223.68, 1228.97, 1215.94, 1219.87, 28442729);
INSERT INTO `stock_kline` VALUES (69, '600519', '2026-04-20', 1201.05, 1223.97, 1200.07, 1209.89, 26485441);
INSERT INTO `stock_kline` VALUES (70, '600519', '2026-04-21', 1281.91, 1282.45, 1268.61, 1269.03, 19594901);
INSERT INTO `stock_kline` VALUES (71, '600519', '2026-04-22', 1278.78, 1285.10, 1268.84, 1271.28, 20196223);
INSERT INTO `stock_kline` VALUES (72, '600519', '2026-04-23', 1295.56, 1318.64, 1273.29, 1276.82, 13990137);
INSERT INTO `stock_kline` VALUES (73, '600519', '2026-04-24', 1294.30, 1301.27, 1286.48, 1289.37, 16309686);
INSERT INTO `stock_kline` VALUES (74, '600519', '2026-04-27', 1281.73, 1288.21, 1274.87, 1287.30, 11123867);
INSERT INTO `stock_kline` VALUES (75, '600519', '2026-04-28', 1292.30, 1298.01, 1287.24, 1297.47, 3849071);
INSERT INTO `stock_kline` VALUES (76, '600519', '2026-04-29', 1262.49, 1297.92, 1241.38, 1266.00, 33852585);
INSERT INTO `stock_kline` VALUES (77, '600519', '2026-04-30', 1251.52, 1283.22, 1235.70, 1259.46, 35446968);
INSERT INTO `stock_kline` VALUES (78, '600519', '2026-05-01', 1221.97, 1240.73, 1216.93, 1234.12, 8815848);
INSERT INTO `stock_kline` VALUES (79, '600519', '2026-05-04', 1210.32, 1239.68, 1195.87, 1229.85, 24811331);
INSERT INTO `stock_kline` VALUES (80, '600519', '2026-05-05', 1266.52, 1270.60, 1246.39, 1256.95, 21540070);
INSERT INTO `stock_kline` VALUES (81, '600519', '2026-05-06', 1233.99, 1240.66, 1226.22, 1229.11, 34950067);
INSERT INTO `stock_kline` VALUES (82, '600519', '2026-05-07', 1656.96, 1665.98, 1637.13, 1659.63, 23549138);
INSERT INTO `stock_kline` VALUES (83, '600519', '2026-05-08', 1641.16, 1661.44, 1630.96, 1653.53, 23577120);
INSERT INTO `stock_kline` VALUES (84, '600519', '2026-05-11', 1635.16, 1642.18, 1623.61, 1633.59, 15425195);
INSERT INTO `stock_kline` VALUES (85, '600519', '2026-05-12', 1615.99, 1666.90, 1589.11, 1635.25, 22880763);
INSERT INTO `stock_kline` VALUES (86, '600519', '2026-05-13', 1640.28, 1644.53, 1616.93, 1632.01, 22604958);
INSERT INTO `stock_kline` VALUES (87, '600519', '2026-05-14', 1657.72, 1687.89, 1644.98, 1674.32, 15372436);
INSERT INTO `stock_kline` VALUES (88, '600519', '2026-05-15', 1678.55, 1681.96, 1669.20, 1680.72, 6597644);
INSERT INTO `stock_kline` VALUES (89, '000858', '2026-03-17', 106.30, 106.61, 105.43, 105.88, 18145607);
INSERT INTO `stock_kline` VALUES (90, '000858', '2026-03-18', 103.38, 104.11, 99.69, 101.14, 31778283);
INSERT INTO `stock_kline` VALUES (91, '000858', '2026-03-19', 98.31, 100.65, 97.88, 100.23, 8448398);
INSERT INTO `stock_kline` VALUES (92, '000858', '2026-03-20', 102.98, 102.98, 102.50, 102.91, 34535292);
INSERT INTO `stock_kline` VALUES (93, '000858', '2026-03-23', 101.52, 104.18, 99.37, 102.58, 16275422);
INSERT INTO `stock_kline` VALUES (94, '000858', '2026-03-24', 103.02, 105.55, 101.68, 104.07, 7792233);
INSERT INTO `stock_kline` VALUES (95, '000858', '2026-03-25', 105.59, 108.50, 104.59, 107.56, 23407214);
INSERT INTO `stock_kline` VALUES (96, '000858', '2026-03-26', 107.78, 110.22, 107.58, 109.01, 5675011);
INSERT INTO `stock_kline` VALUES (97, '000858', '2026-03-27', 112.30, 113.81, 109.86, 111.79, 4392203);
INSERT INTO `stock_kline` VALUES (98, '000858', '2026-03-30', 108.62, 109.64, 106.14, 109.51, 34574775);
INSERT INTO `stock_kline` VALUES (99, '000858', '2026-03-31', 106.65, 108.25, 105.41, 107.21, 34541863);
INSERT INTO `stock_kline` VALUES (100, '000858', '2026-04-01', 107.97, 109.23, 106.51, 106.99, 36250363);
INSERT INTO `stock_kline` VALUES (101, '000858', '2026-04-02', 104.60, 106.66, 103.73, 105.82, 30123728);
INSERT INTO `stock_kline` VALUES (102, '000858', '2026-04-03', 110.83, 113.40, 109.47, 110.83, 6637574);
INSERT INTO `stock_kline` VALUES (103, '000858', '2026-04-06', 109.66, 112.23, 108.19, 111.69, 22329308);
INSERT INTO `stock_kline` VALUES (104, '000858', '2026-04-07', 113.48, 115.47, 112.38, 113.70, 19058980);
INSERT INTO `stock_kline` VALUES (105, '000858', '2026-04-08', 113.41, 113.92, 113.20, 113.27, 10883019);
INSERT INTO `stock_kline` VALUES (106, '000858', '2026-04-09', 113.63, 114.32, 109.76, 111.43, 30883084);
INSERT INTO `stock_kline` VALUES (107, '000858', '2026-04-10', 114.16, 115.82, 113.51, 114.65, 12160919);
INSERT INTO `stock_kline` VALUES (108, '000858', '2026-04-13', 111.46, 112.66, 106.46, 109.17, 37073698);
INSERT INTO `stock_kline` VALUES (109, '000858', '2026-04-14', 107.74, 110.88, 105.55, 109.21, 32967288);
INSERT INTO `stock_kline` VALUES (110, '000858', '2026-04-15', 110.54, 111.64, 108.50, 109.42, 24594112);
INSERT INTO `stock_kline` VALUES (111, '000858', '2026-04-16', 114.74, 115.25, 113.27, 113.67, 34740898);
INSERT INTO `stock_kline` VALUES (112, '000858', '2026-04-17', 108.11, 110.70, 106.08, 109.06, 13207615);
INSERT INTO `stock_kline` VALUES (113, '000858', '2026-04-20', 106.86, 110.19, 106.02, 108.52, 38295979);
INSERT INTO `stock_kline` VALUES (114, '000858', '2026-04-21', 109.02, 111.65, 105.67, 108.59, 8021638);
INSERT INTO `stock_kline` VALUES (115, '000858', '2026-04-22', 102.12, 104.20, 100.94, 103.02, 38810787);
INSERT INTO `stock_kline` VALUES (116, '000858', '2026-04-23', 105.19, 106.34, 102.79, 104.11, 5409147);
INSERT INTO `stock_kline` VALUES (117, '000858', '2026-04-24', 103.56, 103.77, 100.81, 103.04, 9744926);
INSERT INTO `stock_kline` VALUES (118, '000858', '2026-04-27', 100.60, 102.16, 99.29, 102.08, 22687578);
INSERT INTO `stock_kline` VALUES (119, '000858', '2026-04-28', 101.53, 101.73, 100.57, 100.77, 28672113);
INSERT INTO `stock_kline` VALUES (120, '000858', '2026-04-29', 104.40, 104.70, 101.51, 102.48, 20044130);
INSERT INTO `stock_kline` VALUES (121, '000858', '2026-04-30', 100.38, 101.88, 99.39, 99.59, 26190516);
INSERT INTO `stock_kline` VALUES (122, '000858', '2026-05-01', 103.25, 105.31, 99.02, 100.38, 23275042);
INSERT INTO `stock_kline` VALUES (123, '000858', '2026-05-04', 102.04, 103.03, 99.34, 101.05, 22812600);
INSERT INTO `stock_kline` VALUES (124, '000858', '2026-05-05', 102.76, 102.99, 102.27, 102.99, 6291303);
INSERT INTO `stock_kline` VALUES (125, '000858', '2026-05-06', 103.80, 106.55, 101.61, 105.92, 17697145);
INSERT INTO `stock_kline` VALUES (126, '000858', '2026-05-07', 140.96, 145.02, 138.50, 143.08, 9520560);
INSERT INTO `stock_kline` VALUES (127, '000858', '2026-05-08', 139.55, 141.41, 138.88, 141.21, 30883940);
INSERT INTO `stock_kline` VALUES (128, '000858', '2026-05-11', 139.32, 141.07, 138.25, 140.45, 14042389);
INSERT INTO `stock_kline` VALUES (129, '000858', '2026-05-12', 140.99, 142.61, 137.54, 142.20, 12446043);
INSERT INTO `stock_kline` VALUES (130, '000858', '2026-05-13', 142.72, 144.36, 141.43, 143.75, 27546748);
INSERT INTO `stock_kline` VALUES (131, '000858', '2026-05-14', 141.81, 142.11, 140.53, 140.96, 27929174);
INSERT INTO `stock_kline` VALUES (132, '000858', '2026-05-15', 141.41, 142.26, 138.68, 140.07, 17855701);
INSERT INTO `stock_kline` VALUES (133, '601318', '2026-03-17', 38.50, 38.64, 38.47, 38.60, 36059478);
INSERT INTO `stock_kline` VALUES (134, '601318', '2026-03-18', 38.13, 38.25, 37.86, 37.97, 39251704);
INSERT INTO `stock_kline` VALUES (135, '601318', '2026-03-19', 36.93, 37.09, 36.77, 36.90, 21773043);
INSERT INTO `stock_kline` VALUES (136, '601318', '2026-03-20', 37.61, 37.82, 37.32, 37.51, 9667575);
INSERT INTO `stock_kline` VALUES (137, '601318', '2026-03-23', 38.18, 38.53, 37.16, 38.24, 33515566);
INSERT INTO `stock_kline` VALUES (138, '601318', '2026-03-24', 38.35, 38.62, 38.16, 38.17, 25381420);
INSERT INTO `stock_kline` VALUES (139, '601318', '2026-03-25', 38.77, 38.92, 38.05, 38.21, 18636276);
INSERT INTO `stock_kline` VALUES (140, '601318', '2026-03-26', 39.09, 39.09, 38.19, 38.63, 12709845);
INSERT INTO `stock_kline` VALUES (141, '601318', '2026-03-27', 40.47, 40.60, 39.26, 39.61, 18967319);
INSERT INTO `stock_kline` VALUES (142, '601318', '2026-03-30', 39.76, 39.86, 39.22, 39.55, 10626875);
INSERT INTO `stock_kline` VALUES (143, '601318', '2026-03-31', 39.44, 40.04, 38.53, 39.55, 5910178);
INSERT INTO `stock_kline` VALUES (144, '601318', '2026-04-01', 38.08, 38.94, 37.50, 38.67, 5090484);
INSERT INTO `stock_kline` VALUES (145, '601318', '2026-04-02', 39.22, 40.05, 39.18, 39.51, 8421757);
INSERT INTO `stock_kline` VALUES (146, '601318', '2026-04-03', 39.94, 40.26, 39.57, 39.87, 24822825);
INSERT INTO `stock_kline` VALUES (147, '601318', '2026-04-06', 38.89, 39.48, 38.83, 39.10, 39999480);
INSERT INTO `stock_kline` VALUES (148, '601318', '2026-04-07', 40.25, 40.46, 39.53, 39.84, 33433796);
INSERT INTO `stock_kline` VALUES (149, '601318', '2026-04-08', 38.95, 39.42, 38.48, 38.82, 34827579);
INSERT INTO `stock_kline` VALUES (150, '601318', '2026-04-09', 39.57, 39.62, 39.37, 39.59, 11731900);
INSERT INTO `stock_kline` VALUES (151, '601318', '2026-04-10', 38.71, 38.97, 38.13, 38.62, 31382811);
INSERT INTO `stock_kline` VALUES (152, '601318', '2026-04-13', 37.29, 38.89, 36.93, 38.28, 37051879);
INSERT INTO `stock_kline` VALUES (153, '601318', '2026-04-14', 38.91, 39.06, 38.25, 38.56, 18177279);
INSERT INTO `stock_kline` VALUES (154, '601318', '2026-04-15', 38.71, 40.50, 38.10, 39.68, 4761864);
INSERT INTO `stock_kline` VALUES (155, '601318', '2026-04-16', 39.04, 39.70, 38.00, 38.80, 26556783);
INSERT INTO `stock_kline` VALUES (156, '601318', '2026-04-17', 37.99, 38.18, 37.96, 38.10, 5057363);
INSERT INTO `stock_kline` VALUES (157, '601318', '2026-04-20', 38.99, 39.16, 38.72, 38.90, 8701370);
INSERT INTO `stock_kline` VALUES (158, '601318', '2026-04-21', 40.25, 40.30, 39.35, 39.44, 32736808);
INSERT INTO `stock_kline` VALUES (159, '601318', '2026-04-22', 39.45, 39.52, 38.71, 38.98, 22008348);
INSERT INTO `stock_kline` VALUES (160, '601318', '2026-04-23', 37.79, 38.49, 37.60, 38.23, 6676355);
INSERT INTO `stock_kline` VALUES (161, '601318', '2026-04-24', 39.00, 39.13, 38.90, 38.90, 31900768);
INSERT INTO `stock_kline` VALUES (162, '601318', '2026-04-27', 39.21, 39.39, 38.65, 38.95, 26767756);
INSERT INTO `stock_kline` VALUES (163, '601318', '2026-04-28', 40.59, 40.80, 40.22, 40.36, 30140360);
INSERT INTO `stock_kline` VALUES (164, '601318', '2026-04-29', 41.19, 41.21, 40.78, 41.08, 33876580);
INSERT INTO `stock_kline` VALUES (165, '601318', '2026-04-30', 41.94, 42.19, 41.78, 42.01, 29559453);
INSERT INTO `stock_kline` VALUES (166, '601318', '2026-05-01', 40.15, 41.23, 40.07, 40.39, 10652198);
INSERT INTO `stock_kline` VALUES (167, '601318', '2026-05-04', 40.82, 41.45, 39.75, 39.89, 9899095);
INSERT INTO `stock_kline` VALUES (168, '601318', '2026-05-05', 40.02, 40.28, 39.58, 39.81, 14888075);
INSERT INTO `stock_kline` VALUES (169, '601318', '2026-05-06', 40.51, 41.36, 39.91, 40.90, 24690137);
INSERT INTO `stock_kline` VALUES (170, '601318', '2026-05-07', 47.96, 48.40, 47.59, 47.89, 21433037);
INSERT INTO `stock_kline` VALUES (171, '601318', '2026-05-08', 46.68, 46.90, 45.91, 46.40, 9059695);
INSERT INTO `stock_kline` VALUES (172, '601318', '2026-05-11', 47.88, 48.54, 46.67, 47.42, 33181117);
INSERT INTO `stock_kline` VALUES (173, '601318', '2026-05-12', 45.71, 47.03, 44.95, 46.61, 26047718);
INSERT INTO `stock_kline` VALUES (174, '601318', '2026-05-13', 48.10, 48.35, 47.76, 48.11, 38169645);
INSERT INTO `stock_kline` VALUES (175, '601318', '2026-05-14', 47.15, 47.49, 46.51, 46.96, 25457937);
INSERT INTO `stock_kline` VALUES (176, '601318', '2026-05-15', 46.28, 46.63, 46.06, 46.38, 13091383);
INSERT INTO `stock_kline` VALUES (177, '000001', '2026-03-17', 9.63, 9.77, 9.55, 9.72, 8685396);
INSERT INTO `stock_kline` VALUES (178, '000001', '2026-03-18', 9.77, 9.87, 9.59, 9.67, 38325659);
INSERT INTO `stock_kline` VALUES (179, '000001', '2026-03-19', 9.96, 10.13, 9.85, 9.91, 35633089);
INSERT INTO `stock_kline` VALUES (180, '000001', '2026-03-20', 10.06, 10.11, 9.98, 10.08, 37070479);
INSERT INTO `stock_kline` VALUES (181, '000001', '2026-03-23', 10.23, 10.44, 10.10, 10.14, 28281242);
INSERT INTO `stock_kline` VALUES (182, '000001', '2026-03-24', 9.82, 9.87, 9.68, 9.76, 32371765);
INSERT INTO `stock_kline` VALUES (183, '000001', '2026-03-25', 9.80, 10.11, 9.62, 9.94, 18703671);
INSERT INTO `stock_kline` VALUES (184, '000001', '2026-03-26', 10.15, 10.24, 9.85, 10.00, 27685388);
INSERT INTO `stock_kline` VALUES (185, '000001', '2026-03-27', 9.77, 9.97, 9.70, 9.89, 13875539);
INSERT INTO `stock_kline` VALUES (186, '000001', '2026-03-30', 9.63, 9.72, 9.47, 9.66, 27911622);
INSERT INTO `stock_kline` VALUES (187, '000001', '2026-03-31', 9.51, 9.77, 9.44, 9.58, 37450519);
INSERT INTO `stock_kline` VALUES (188, '000001', '2026-04-01', 9.63, 9.65, 9.51, 9.55, 26460257);
INSERT INTO `stock_kline` VALUES (189, '000001', '2026-04-02', 9.79, 9.80, 9.70, 9.73, 24386579);
INSERT INTO `stock_kline` VALUES (190, '000001', '2026-04-03', 9.47, 9.87, 9.43, 9.65, 22252150);
INSERT INTO `stock_kline` VALUES (191, '000001', '2026-04-06', 9.72, 10.00, 9.49, 9.84, 4275329);
INSERT INTO `stock_kline` VALUES (192, '000001', '2026-04-07', 9.67, 9.81, 9.61, 9.73, 10461623);
INSERT INTO `stock_kline` VALUES (193, '000001', '2026-04-08', 9.81, 9.86, 9.76, 9.76, 36542971);
INSERT INTO `stock_kline` VALUES (194, '000001', '2026-04-09', 9.72, 9.77, 9.59, 9.62, 31805734);
INSERT INTO `stock_kline` VALUES (195, '000001', '2026-04-10', 9.93, 10.03, 9.73, 9.80, 38303251);
INSERT INTO `stock_kline` VALUES (196, '000001', '2026-04-13', 9.72, 9.81, 9.56, 9.81, 5921545);
INSERT INTO `stock_kline` VALUES (197, '000001', '2026-04-14', 9.53, 9.57, 9.37, 9.45, 9956764);
INSERT INTO `stock_kline` VALUES (198, '000001', '2026-04-15', 8.98, 9.06, 8.86, 9.05, 14750032);
INSERT INTO `stock_kline` VALUES (199, '000001', '2026-04-16', 9.24, 9.28, 9.00, 9.19, 33714356);
INSERT INTO `stock_kline` VALUES (200, '000001', '2026-04-17', 8.99, 9.04, 8.93, 8.95, 23808840);
INSERT INTO `stock_kline` VALUES (201, '000001', '2026-04-20', 8.54, 8.57, 8.36, 8.42, 6208074);
INSERT INTO `stock_kline` VALUES (202, '000001', '2026-04-21', 8.41, 8.49, 8.18, 8.38, 32510225);
INSERT INTO `stock_kline` VALUES (203, '000001', '2026-04-22', 8.63, 8.86, 8.38, 8.51, 8543964);
INSERT INTO `stock_kline` VALUES (204, '000001', '2026-04-23', 8.53, 8.69, 8.30, 8.41, 24556134);
INSERT INTO `stock_kline` VALUES (205, '000001', '2026-04-24', 8.61, 8.63, 8.48, 8.53, 33006363);
INSERT INTO `stock_kline` VALUES (206, '000001', '2026-04-27', 8.61, 8.68, 8.60, 8.63, 6849234);
INSERT INTO `stock_kline` VALUES (207, '000001', '2026-04-28', 8.77, 8.97, 8.75, 8.85, 32405280);
INSERT INTO `stock_kline` VALUES (208, '000001', '2026-04-29', 9.01, 9.03, 8.95, 8.97, 5173342);
INSERT INTO `stock_kline` VALUES (209, '000001', '2026-04-30', 8.95, 9.09, 8.63, 8.76, 29711612);
INSERT INTO `stock_kline` VALUES (210, '000001', '2026-05-01', 8.71, 8.71, 8.58, 8.60, 37745297);
INSERT INTO `stock_kline` VALUES (211, '000001', '2026-05-04', 8.48, 8.58, 8.41, 8.44, 35963887);
INSERT INTO `stock_kline` VALUES (212, '000001', '2026-05-05', 8.60, 8.61, 8.53, 8.59, 8891536);
INSERT INTO `stock_kline` VALUES (213, '000001', '2026-05-06', 8.58, 8.90, 8.50, 8.76, 23129751);
INSERT INTO `stock_kline` VALUES (214, '000001', '2026-05-07', 12.79, 13.21, 12.64, 12.98, 11144496);
INSERT INTO `stock_kline` VALUES (215, '000001', '2026-05-08', 12.80, 12.90, 12.45, 12.67, 11708799);
INSERT INTO `stock_kline` VALUES (216, '000001', '2026-05-11', 12.95, 13.02, 12.81, 12.86, 30476618);
INSERT INTO `stock_kline` VALUES (217, '000001', '2026-05-12', 13.23, 13.32, 12.80, 13.04, 39997347);
INSERT INTO `stock_kline` VALUES (218, '000001', '2026-05-13', 12.82, 12.98, 12.81, 12.87, 11332311);
INSERT INTO `stock_kline` VALUES (219, '000001', '2026-05-14', 12.97, 13.16, 12.53, 12.63, 8292198);
INSERT INTO `stock_kline` VALUES (220, '000001', '2026-05-15', 12.64, 12.75, 12.50, 12.67, 36727112);
INSERT INTO `stock_kline` VALUES (221, '600887', '2026-03-17', 22.09, 22.35, 21.90, 22.28, 5764502);
INSERT INTO `stock_kline` VALUES (222, '600887', '2026-03-18', 22.00, 22.36, 21.65, 21.97, 17147110);
INSERT INTO `stock_kline` VALUES (223, '600887', '2026-03-19', 22.32, 22.45, 21.59, 22.03, 35409878);
INSERT INTO `stock_kline` VALUES (224, '600887', '2026-03-20', 21.49, 21.70, 21.32, 21.59, 13608599);
INSERT INTO `stock_kline` VALUES (225, '600887', '2026-03-23', 21.07, 21.29, 20.91, 21.14, 26177425);
INSERT INTO `stock_kline` VALUES (226, '600887', '2026-03-24', 21.56, 21.91, 21.13, 21.33, 34304444);
INSERT INTO `stock_kline` VALUES (227, '600887', '2026-03-25', 21.85, 22.01, 21.60, 21.68, 13717262);
INSERT INTO `stock_kline` VALUES (228, '600887', '2026-03-26', 21.62, 22.13, 21.53, 22.02, 37542599);
INSERT INTO `stock_kline` VALUES (229, '600887', '2026-03-27', 21.30, 21.47, 21.06, 21.15, 26682388);
INSERT INTO `stock_kline` VALUES (230, '600887', '2026-03-30', 21.42, 21.53, 21.25, 21.34, 22176249);
INSERT INTO `stock_kline` VALUES (231, '600887', '2026-03-31', 20.31, 20.80, 20.19, 20.55, 29882577);
INSERT INTO `stock_kline` VALUES (232, '600887', '2026-04-01', 20.33, 20.42, 20.28, 20.38, 33222770);
INSERT INTO `stock_kline` VALUES (233, '600887', '2026-04-02', 20.35, 21.01, 20.11, 20.42, 25755629);
INSERT INTO `stock_kline` VALUES (234, '600887', '2026-04-03', 20.15, 20.38, 19.95, 20.10, 5701461);
INSERT INTO `stock_kline` VALUES (235, '600887', '2026-04-06', 20.44, 20.60, 20.18, 20.32, 25009415);
INSERT INTO `stock_kline` VALUES (236, '600887', '2026-04-07', 20.62, 20.98, 19.90, 20.39, 25573640);
INSERT INTO `stock_kline` VALUES (237, '600887', '2026-04-08', 19.75, 19.92, 19.64, 19.91, 10355439);
INSERT INTO `stock_kline` VALUES (238, '600887', '2026-04-09', 19.43, 20.33, 19.03, 19.90, 4788260);
INSERT INTO `stock_kline` VALUES (239, '600887', '2026-04-10', 19.41, 19.75, 19.39, 19.65, 35242372);
INSERT INTO `stock_kline` VALUES (240, '600887', '2026-04-13', 19.37, 19.63, 19.34, 19.48, 19253536);
INSERT INTO `stock_kline` VALUES (241, '600887', '2026-04-14', 20.47, 20.49, 20.31, 20.42, 30539961);
INSERT INTO `stock_kline` VALUES (242, '600887', '2026-04-15', 20.46, 20.94, 20.20, 20.63, 12266558);
INSERT INTO `stock_kline` VALUES (243, '600887', '2026-04-16', 20.34, 20.92, 19.83, 20.11, 38777551);
INSERT INTO `stock_kline` VALUES (244, '600887', '2026-04-17', 20.27, 20.47, 19.89, 20.06, 20741045);
INSERT INTO `stock_kline` VALUES (245, '600887', '2026-04-20', 19.82, 20.09, 19.75, 19.87, 22394739);
INSERT INTO `stock_kline` VALUES (246, '600887', '2026-04-21', 19.49, 19.92, 19.24, 19.71, 21351424);
INSERT INTO `stock_kline` VALUES (247, '600887', '2026-04-22', 19.46, 19.73, 19.18, 19.60, 12829846);
INSERT INTO `stock_kline` VALUES (248, '600887', '2026-04-23', 20.29, 20.38, 20.00, 20.11, 20209663);
INSERT INTO `stock_kline` VALUES (249, '600887', '2026-04-24', 20.41, 20.55, 20.06, 20.12, 19057947);
INSERT INTO `stock_kline` VALUES (250, '600887', '2026-04-27', 19.73, 19.99, 19.72, 19.85, 30736185);
INSERT INTO `stock_kline` VALUES (251, '600887', '2026-04-28', 20.06, 20.48, 19.57, 19.64, 34923662);
INSERT INTO `stock_kline` VALUES (252, '600887', '2026-04-29', 20.37, 20.48, 19.82, 20.04, 9003607);
INSERT INTO `stock_kline` VALUES (253, '600887', '2026-04-30', 19.86, 20.31, 19.67, 19.88, 14796980);
INSERT INTO `stock_kline` VALUES (254, '600887', '2026-05-01', 20.12, 20.56, 19.55, 19.65, 22425799);
INSERT INTO `stock_kline` VALUES (255, '600887', '2026-05-04', 20.07, 20.18, 19.33, 19.70, 39460278);
INSERT INTO `stock_kline` VALUES (256, '600887', '2026-05-05', 19.62, 19.85, 19.52, 19.67, 10559596);
INSERT INTO `stock_kline` VALUES (257, '600887', '2026-05-06', 19.36, 19.51, 19.24, 19.37, 15962834);
INSERT INTO `stock_kline` VALUES (258, '600887', '2026-05-07', 26.55, 27.51, 26.49, 27.14, 9401186);
INSERT INTO `stock_kline` VALUES (259, '600887', '2026-05-08', 27.38, 27.61, 27.02, 27.31, 23263242);
INSERT INTO `stock_kline` VALUES (260, '600887', '2026-05-11', 27.78, 27.91, 27.23, 27.59, 34232129);
INSERT INTO `stock_kline` VALUES (261, '600887', '2026-05-12', 27.27, 27.59, 26.58, 27.50, 22799638);
INSERT INTO `stock_kline` VALUES (262, '600887', '2026-05-13', 27.21, 27.48, 26.93, 27.35, 28073538);
INSERT INTO `stock_kline` VALUES (263, '600887', '2026-05-14', 27.36, 27.60, 26.98, 27.47, 8494223);
INSERT INTO `stock_kline` VALUES (264, '600887', '2026-05-15', 27.99, 28.05, 27.11, 27.53, 36922662);
INSERT INTO `stock_kline` VALUES (265, '000333', '2026-03-17', 42.34, 43.13, 41.05, 41.59, 37250710);
INSERT INTO `stock_kline` VALUES (266, '000333', '2026-03-18', 41.59, 41.87, 41.12, 41.50, 34029682);
INSERT INTO `stock_kline` VALUES (267, '000333', '2026-03-19', 41.19, 41.24, 41.17, 41.19, 31433850);
INSERT INTO `stock_kline` VALUES (268, '000333', '2026-03-20', 41.14, 41.66, 40.55, 40.59, 34680162);
INSERT INTO `stock_kline` VALUES (269, '000333', '2026-03-23', 40.45, 42.08, 40.03, 41.43, 28667772);
INSERT INTO `stock_kline` VALUES (270, '000333', '2026-03-24', 41.37, 41.67, 40.72, 41.62, 39191828);
INSERT INTO `stock_kline` VALUES (271, '000333', '2026-03-25', 42.62, 42.79, 42.29, 42.75, 3233088);
INSERT INTO `stock_kline` VALUES (272, '000333', '2026-03-26', 43.54, 44.79, 43.09, 44.03, 28452812);
INSERT INTO `stock_kline` VALUES (273, '000333', '2026-03-27', 44.92, 45.36, 44.28, 44.35, 31134760);
INSERT INTO `stock_kline` VALUES (274, '000333', '2026-03-30', 42.98, 43.98, 42.92, 43.77, 5253265);
INSERT INTO `stock_kline` VALUES (275, '000333', '2026-03-31', 42.61, 43.11, 41.94, 42.56, 6787895);
INSERT INTO `stock_kline` VALUES (276, '000333', '2026-04-01', 42.91, 43.34, 42.70, 43.00, 24519471);
INSERT INTO `stock_kline` VALUES (277, '000333', '2026-04-02', 42.64, 43.52, 41.93, 43.17, 14752989);
INSERT INTO `stock_kline` VALUES (278, '000333', '2026-04-03', 45.41, 46.31, 44.08, 44.95, 28578854);
INSERT INTO `stock_kline` VALUES (279, '000333', '2026-04-06', 45.41, 45.93, 44.82, 45.15, 18122616);
INSERT INTO `stock_kline` VALUES (280, '000333', '2026-04-07', 43.95, 44.91, 43.64, 44.40, 30951369);
INSERT INTO `stock_kline` VALUES (281, '000333', '2026-04-08', 44.29, 44.74, 43.57, 43.98, 9205153);
INSERT INTO `stock_kline` VALUES (282, '000333', '2026-04-09', 43.80, 44.14, 43.31, 43.84, 5653991);
INSERT INTO `stock_kline` VALUES (283, '000333', '2026-04-10', 43.67, 44.77, 42.74, 44.26, 25344856);
INSERT INTO `stock_kline` VALUES (284, '000333', '2026-04-13', 43.75, 43.97, 43.34, 43.87, 35139835);
INSERT INTO `stock_kline` VALUES (285, '000333', '2026-04-14', 45.29, 45.62, 44.76, 45.05, 34289538);
INSERT INTO `stock_kline` VALUES (286, '000333', '2026-04-15', 44.57, 44.99, 43.96, 44.36, 9617486);
INSERT INTO `stock_kline` VALUES (287, '000333', '2026-04-16', 45.06, 45.40, 44.79, 44.84, 17849453);
INSERT INTO `stock_kline` VALUES (288, '000333', '2026-04-17', 44.07, 44.67, 42.76, 43.50, 22810019);
INSERT INTO `stock_kline` VALUES (289, '000333', '2026-04-20', 43.65, 44.39, 43.21, 43.97, 20268906);
INSERT INTO `stock_kline` VALUES (290, '000333', '2026-04-21', 45.42, 46.22, 44.14, 44.64, 26634142);
INSERT INTO `stock_kline` VALUES (291, '000333', '2026-04-22', 44.93, 45.85, 44.88, 45.35, 5269873);
INSERT INTO `stock_kline` VALUES (292, '000333', '2026-04-23', 47.63, 47.69, 46.63, 46.64, 12226900);
INSERT INTO `stock_kline` VALUES (293, '000333', '2026-04-24', 47.58, 47.98, 46.38, 47.04, 35427281);
INSERT INTO `stock_kline` VALUES (294, '000333', '2026-04-27', 47.02, 47.43, 46.47, 46.49, 21437538);
INSERT INTO `stock_kline` VALUES (295, '000333', '2026-04-28', 45.42, 46.53, 45.28, 45.77, 9463515);
INSERT INTO `stock_kline` VALUES (296, '000333', '2026-04-29', 46.14, 46.35, 45.34, 45.63, 29635268);
INSERT INTO `stock_kline` VALUES (297, '000333', '2026-04-30', 48.23, 48.47, 47.03, 47.68, 8410109);
INSERT INTO `stock_kline` VALUES (298, '000333', '2026-05-01', 48.06, 48.95, 47.66, 48.85, 34944161);
INSERT INTO `stock_kline` VALUES (299, '000333', '2026-05-04', 49.20, 49.56, 48.74, 49.00, 9399624);
INSERT INTO `stock_kline` VALUES (300, '000333', '2026-05-05', 48.76, 50.28, 47.83, 49.07, 34910284);
INSERT INTO `stock_kline` VALUES (301, '000333', '2026-05-06', 49.39, 50.10, 48.61, 48.80, 24104710);
INSERT INTO `stock_kline` VALUES (302, '000333', '2026-05-07', 56.17, 56.64, 55.17, 55.70, 23084132);
INSERT INTO `stock_kline` VALUES (303, '000333', '2026-05-08', 57.28, 58.20, 57.16, 57.56, 8263193);
INSERT INTO `stock_kline` VALUES (304, '000333', '2026-05-11', 57.53, 58.93, 56.23, 56.80, 29622290);
INSERT INTO `stock_kline` VALUES (305, '000333', '2026-05-12', 57.42, 58.20, 56.57, 57.10, 15464655);
INSERT INTO `stock_kline` VALUES (306, '000333', '2026-05-13', 57.97, 58.95, 56.33, 57.21, 8505477);
INSERT INTO `stock_kline` VALUES (307, '000333', '2026-05-14', 57.62, 57.67, 56.96, 57.44, 32845718);
INSERT INTO `stock_kline` VALUES (308, '000333', '2026-05-15', 56.60, 56.90, 56.11, 56.48, 30383381);
INSERT INTO `stock_kline` VALUES (309, '002594', '2026-03-17', 184.58, 187.86, 183.03, 187.81, 8684088);
INSERT INTO `stock_kline` VALUES (310, '002594', '2026-03-18', 186.08, 191.52, 183.79, 190.45, 28550493);
INSERT INTO `stock_kline` VALUES (311, '002594', '2026-03-19', 192.39, 193.01, 191.34, 192.01, 8162105);
INSERT INTO `stock_kline` VALUES (312, '002594', '2026-03-20', 192.79, 196.11, 191.11, 194.76, 33726574);
INSERT INTO `stock_kline` VALUES (313, '002594', '2026-03-23', 200.98, 205.54, 195.59, 197.46, 25463767);
INSERT INTO `stock_kline` VALUES (314, '002594', '2026-03-24', 197.60, 201.52, 196.65, 199.65, 16478915);
INSERT INTO `stock_kline` VALUES (315, '002594', '2026-03-25', 199.44, 201.31, 194.41, 197.27, 19268819);
INSERT INTO `stock_kline` VALUES (316, '002594', '2026-03-26', 199.73, 200.68, 195.27, 197.69, 16408354);
INSERT INTO `stock_kline` VALUES (317, '002594', '2026-03-27', 189.56, 195.60, 186.54, 193.63, 32424213);
INSERT INTO `stock_kline` VALUES (318, '002594', '2026-03-30', 190.73, 195.10, 186.94, 191.06, 36507671);
INSERT INTO `stock_kline` VALUES (319, '002594', '2026-03-31', 194.17, 194.99, 192.26, 193.24, 35160896);
INSERT INTO `stock_kline` VALUES (320, '002594', '2026-04-01', 192.32, 199.19, 190.72, 195.86, 5467268);
INSERT INTO `stock_kline` VALUES (321, '002594', '2026-04-02', 193.45, 197.35, 189.30, 196.89, 26824238);
INSERT INTO `stock_kline` VALUES (322, '002594', '2026-04-03', 192.39, 194.53, 191.24, 193.39, 25253077);
INSERT INTO `stock_kline` VALUES (323, '002594', '2026-04-06', 198.03, 198.57, 196.90, 197.50, 17833188);
INSERT INTO `stock_kline` VALUES (324, '002594', '2026-04-07', 199.15, 201.02, 195.11, 196.70, 26150369);
INSERT INTO `stock_kline` VALUES (325, '002594', '2026-04-08', 201.11, 203.39, 199.29, 201.32, 26375940);
INSERT INTO `stock_kline` VALUES (326, '002594', '2026-04-09', 201.88, 203.12, 200.12, 201.35, 32404818);
INSERT INTO `stock_kline` VALUES (327, '002594', '2026-04-10', 201.63, 201.66, 200.22, 200.68, 10339205);
INSERT INTO `stock_kline` VALUES (328, '002594', '2026-04-13', 200.68, 201.73, 195.69, 196.44, 13746525);
INSERT INTO `stock_kline` VALUES (329, '002594', '2026-04-14', 192.49, 195.12, 190.37, 194.52, 29547084);
INSERT INTO `stock_kline` VALUES (330, '002594', '2026-04-15', 197.98, 198.87, 197.54, 198.20, 17414722);
INSERT INTO `stock_kline` VALUES (331, '002594', '2026-04-16', 200.63, 205.03, 199.18, 200.12, 24876477);
INSERT INTO `stock_kline` VALUES (332, '002594', '2026-04-17', 208.03, 209.03, 204.67, 206.65, 24032193);
INSERT INTO `stock_kline` VALUES (333, '002594', '2026-04-20', 205.45, 207.90, 198.85, 202.09, 22961184);
INSERT INTO `stock_kline` VALUES (334, '002594', '2026-04-21', 195.27, 200.75, 193.94, 199.01, 10848308);
INSERT INTO `stock_kline` VALUES (335, '002594', '2026-04-22', 202.10, 203.47, 200.94, 201.61, 39062912);
INSERT INTO `stock_kline` VALUES (336, '002594', '2026-04-23', 202.74, 209.94, 200.47, 207.37, 12415141);
INSERT INTO `stock_kline` VALUES (337, '002594', '2026-04-24', 206.43, 212.63, 205.59, 210.54, 28766057);
INSERT INTO `stock_kline` VALUES (338, '002594', '2026-04-27', 214.97, 222.46, 212.33, 218.11, 32786386);
INSERT INTO `stock_kline` VALUES (339, '002594', '2026-04-28', 216.05, 216.75, 215.38, 216.41, 14288313);
INSERT INTO `stock_kline` VALUES (340, '002594', '2026-04-29', 212.55, 219.28, 209.84, 216.65, 10664278);
INSERT INTO `stock_kline` VALUES (341, '002594', '2026-04-30', 211.28, 221.04, 208.44, 216.15, 17666100);
INSERT INTO `stock_kline` VALUES (342, '002594', '2026-05-01', 227.10, 232.05, 219.67, 221.63, 5162945);
INSERT INTO `stock_kline` VALUES (343, '002594', '2026-05-04', 223.34, 227.63, 216.53, 217.84, 26530437);
INSERT INTO `stock_kline` VALUES (344, '002594', '2026-05-05', 216.71, 222.78, 212.15, 219.20, 16887690);
INSERT INTO `stock_kline` VALUES (345, '002594', '2026-05-06', 220.57, 225.22, 219.38, 221.25, 20941089);
INSERT INTO `stock_kline` VALUES (346, '002594', '2026-05-07', 255.83, 260.24, 252.35, 259.61, 30621754);
INSERT INTO `stock_kline` VALUES (347, '002594', '2026-05-08', 258.21, 260.24, 256.46, 258.74, 29353701);
INSERT INTO `stock_kline` VALUES (348, '002594', '2026-05-11', 256.94, 259.18, 251.75, 254.89, 9247485);
INSERT INTO `stock_kline` VALUES (349, '002594', '2026-05-12', 260.37, 264.40, 250.98, 256.99, 9250630);
INSERT INTO `stock_kline` VALUES (350, '002594', '2026-05-13', 255.84, 257.63, 253.58, 257.36, 17618319);
INSERT INTO `stock_kline` VALUES (351, '002594', '2026-05-14', 256.77, 257.21, 252.11, 254.95, 26067975);
INSERT INTO `stock_kline` VALUES (352, '002594', '2026-05-15', 262.92, 268.13, 258.92, 259.02, 13602094);

-- ----------------------------
-- Table structure for stock_quotes
-- ----------------------------
DROP TABLE IF EXISTS `stock_quotes`;
CREATE TABLE `stock_quotes`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `stock_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `current_price` decimal(10, 2) NULL DEFAULT NULL,
  `price_change` decimal(10, 2) NULL DEFAULT NULL,
  `change_percent` decimal(10, 4) NULL DEFAULT NULL,
  `open_price` decimal(10, 2) NULL DEFAULT NULL,
  `high_price` decimal(10, 2) NULL DEFAULT NULL,
  `low_price` decimal(10, 2) NULL DEFAULT NULL,
  `close_price` decimal(10, 2) NULL DEFAULT NULL,
  `volume` bigint NULL DEFAULT NULL,
  `turnover` decimal(18, 2) NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_stock_code`(`stock_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of stock_quotes
-- ----------------------------
INSERT INTO `stock_quotes` VALUES (1, '600036', 38.50, 0.32, 0.8400, 38.40, 38.85, 38.18, 38.50, 11878334, 457315859.00, '2026-05-16 23:01:17');
INSERT INTO `stock_quotes` VALUES (2, '600519', 1650.00, 17.49, 1.0600, 1638.51, 1660.40, 1631.31, 1650.00, 6999657, 11549434050.00, '2026-05-16 23:01:17');
INSERT INTO `stock_quotes` VALUES (3, '000858', 142.30, -3.47, -2.4400, 143.66, 144.30, 140.39, 142.30, 48613006, 6917630753.80, '2026-05-16 23:01:17');
INSERT INTO `stock_quotes` VALUES (4, '601318', 47.20, 0.57, 1.2100, 46.93, 47.75, 46.77, 47.20, 5436124, 256585052.80, '2026-05-16 23:01:17');
INSERT INTO `stock_quotes` VALUES (5, '000001', 12.80, 0.20, 1.5500, 12.73, 12.95, 12.68, 12.80, 19449461, 248953100.80, '2026-05-16 23:01:17');
INSERT INTO `stock_quotes` VALUES (6, '600887', 27.50, -0.15, -0.5400, 27.30, 27.80, 27.10, 27.50, 8250000, 226875000.00, '2026-05-16 23:01:17');
INSERT INTO `stock_quotes` VALUES (7, '000333', 56.80, 0.35, 0.6200, 56.50, 57.30, 56.20, 56.80, 10200000, 579360000.00, '2026-05-16 23:01:17');
INSERT INTO `stock_quotes` VALUES (8, '002594', 255.00, -1.80, -0.7000, 253.80, 258.20, 252.50, 255.00, 3850000, 981750000.00, '2026-05-16 23:01:17');

-- ----------------------------
-- Table structure for stocks
-- ----------------------------
DROP TABLE IF EXISTS `stocks`;
CREATE TABLE `stocks`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `stock_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '股票代码',
  `stock_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '股票名称',
  `industry` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '所属行业',
  `market_type` tinyint NULL DEFAULT 1 COMMENT '市场类型：1-A股，2-港股，3-美股',
  `list_date` date NULL DEFAULT NULL COMMENT '上市日期',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1-正常，0-停牌',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `stock_code`(`stock_code` ASC) USING BTREE,
  INDEX `idx_stock_code`(`stock_code` ASC) USING BTREE,
  INDEX `idx_stock_name`(`stock_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of stocks
-- ----------------------------
INSERT INTO `stocks` VALUES (1, '600036', '招商银行', '金融', 1, '2002-04-09', 1, '2026-05-16 16:41:59', '2026-05-16 16:41:59');
INSERT INTO `stocks` VALUES (2, '600519', '贵州茅台', '白酒', 1, '2001-08-27', 1, '2026-05-16 16:41:59', '2026-05-16 16:41:59');
INSERT INTO `stocks` VALUES (3, '000858', '五粮液', '白酒', 2, '1998-04-27', 1, '2026-05-16 16:41:59', '2026-05-16 16:41:59');
INSERT INTO `stocks` VALUES (4, '601318', '中国平安', '金融', 1, '2007-02-09', 1, '2026-05-16 16:41:59', '2026-05-16 16:41:59');
INSERT INTO `stocks` VALUES (5, '000001', '平安银行', '金融', 2, '1991-04-03', 1, '2026-05-16 16:41:59', '2026-05-16 16:41:59');
INSERT INTO `stocks` VALUES (6, '600887', '伊利股份', '食品', 1, '1996-03-12', 1, '2026-05-16 16:41:59', '2026-05-16 16:41:59');
INSERT INTO `stocks` VALUES (7, '000333', '美的集团', '家电', 2, '2013-09-18', 1, '2026-05-16 16:41:59', '2026-05-16 16:41:59');
INSERT INTO `stocks` VALUES (8, '002594', '比亚迪', '汽车', 2, '2011-06-30', 1, '2026-05-16 16:41:59', '2026-05-16 16:41:59');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码（加密）',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `avatar_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'testuser', 'bc6c0416a0ad733988eed8a7ee18e6bb', 'test@example.com', '13800138000', '/avatars/1/0cbca74d-cab2-442f-833e-2abac6a75f18.jpg', 1, '2026-05-16 16:40:33', '2026-05-16 17:29:34');
INSERT INTO `users` VALUES (2, 'admin', '123456', 'admin@example.com', '13900139000', NULL, 1, '2026-05-16 16:40:33', '2026-05-16 16:40:33');
INSERT INTO `users` VALUES (3, 'test', '0aaac58b64025f580f3339bb2f893849', 'test@example.com', '13800138000', NULL, 1, '2026-05-16 17:26:57', '2026-05-16 17:29:13');
INSERT INTO `users` VALUES (4, 'verifyuser', 'fed265dd0ae3ebf65bca4ac2f6a31a99', 'test@test.com', '13800000001', NULL, 1, NULL, NULL);

-- ----------------------------
-- Table structure for watchlist
-- ----------------------------
DROP TABLE IF EXISTS `watchlist`;
CREATE TABLE `watchlist`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `stock_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `stock_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_stock`(`user_id` ASC, `stock_code` ASC) USING BTREE,
  INDEX `idx_wl_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of watchlist
-- ----------------------------
INSERT INTO `watchlist` VALUES (2, 1, '000001', '平安银行', NULL, NULL);
INSERT INTO `watchlist` VALUES (3, 1, '600036', '招商银行', NULL, NULL);
INSERT INTO `watchlist` VALUES (4, 1, '600519', '贵州茅台', NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;
