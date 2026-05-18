# frontend-market Specification

## Purpose
TBD - created by archiving change frontend-feature-completion. Update Purpose after archive.
## Requirements
### Requirement: 股票搜索
系统 SHALL 提供股票搜索功能，用户通过输入股票代码或名称搜索股票。

#### Scenario: 按名称搜索
- **WHEN** 用户在搜索框输入"茅台"
- **THEN** 系统显示匹配的股票列表，包含代码和名称

#### Scenario: 选择股票后加载行情
- **WHEN** 用户从搜索结果中选择一只股票
- **THEN** 系统加载该股票的实时行情数据和 K 线图

### Requirement: K 线图表渲染
系统 SHALL 使用 ECharts 渲染股票 K 线图（蜡烛图），支持日线、周线、月线周期切换。

#### Scenario: 加载日线 K 线
- **WHEN** 用户选择日线周期并查看某股票
- **THEN** 系统显示该股票最近 30 个交易日的 K 线蜡烛图

#### Scenario: 切换周期
- **WHEN** 用户切换到周线或月线周期
- **THEN** K 线图按对应周期重新加载数据

### Requirement: 技术指标显示
系统 SHALL 在 K 线图上叠加显示技术指标（MA 均线、MACD、KDJ），并支持切换各指标的显示/隐藏。

#### Scenario: 显示 MA 均线
- **WHEN** 用户勾选 MA 均线指标
- **THEN** K 线图上叠加显示 MA5、MA10、MA20、MA60 四条均线

#### Scenario: 显示 MACD 指标
- **WHEN** 用户勾选 MACD 指标
- **THEN** K 线图下方显示 MACD 柱状图和信号线

#### Scenario: 隐藏指标
- **WHEN** 用户取消勾选某个技术指标
- **THEN** 对应指标从图表中移除

