# 第四章 概要设计

## 4.1 业务流程设计

本互联网金融股票投资平台的核心业务流程围绕投资者从开户到投资决策、交易执行的全生命周期展开。平台将整体业务划分为普通用户业务流程和管理员业务流程两条主线，覆盖了面向个人投资者的核心使用场景。

普通用户的主线业务流程始于账户注册环节。用户通过注册页面提交用户名、密码、电子邮箱及手机号码等基本信息，系统在验证用户名唯一性后完成用户记录创建，同时自动为该用户开立资金账户，并注入100,000元虚拟初始资金，使注册用户能够即刻开展模拟交易体验。注册成功后，用户通过登录页面输入凭证，系统采用JWT（JSON Web Token）机制签发身份令牌，并将令牌存储至Redis缓存，设置7天有效期。用户在登录状态下方可访问平台的各项功能模块。

登录后，用户进入行情浏览环节。平台提供股票搜索功能，支持按股票代码或名称关键词模糊匹配，返回符合条件的股票列表。用户可进一步查询单只股票的实时行情数据，包括当前价格、涨跌幅、成交量等信息，也可按日、周、月不同周期查看K线历史数据。平台集成了MA（移动平均线）、MACD（指数平滑异同移动平均线）、KDJ（随机指标）、RSI（相对强弱指标）及BOLL（布林带）等常用技术指标的计算与展示，辅助用户进行技术分析。

在交易决策阶段，用户根据行情分析结果下达买卖订单。买入方向时，系统在订单提交阶段即获取实时价格并计算订单总金额，核验资金账户可用余额是否充足，验证通过后冻结相应资金；卖出方向时，系统核验用户对目标股票的持仓数量是否充足，验证通过后冻结相应股份。订单初始状态为"待成交"，用户可主动确认订单以完成交易执行，也可在确认前撤销订单以解冻资金或股份。

交易完成后，用户可在资产组合模块中查看自身的综合资产状况。系统汇总资金账户余额、冻结资金及各持仓股票的实时市值，计算总资产、持仓市值及持仓盈亏等关键指标。盈亏计算基于当前市价与持仓均价之间的差额，并以百分比形式呈现盈亏比例。同时，用户可通过充值功能增加账户余额、通过提现功能提取可用资金，系统对充值提现金额进行正数合法性校验。

平台的财务分析模块为用户提供上市公司基本面研究工具，包括财务数据总览（营业收入、净利润、ROE、每股收益、市盈率、市净率及股息率等）、利润表摘要、资产负债表摘要及现金流量表摘要。同时，营收趋势分析功能以折线图方式展示公司营收的季节性变化和同比增长率。策略回测模块支持用户配置并运行MA交叉、MACD信号、动量突破及布林带四种量化策略，回测引擎自动计算总收益率、年化收益率、最大回撤率、胜率、夏普比率等绩效指标，并以净值曲线和交易记录方式展示回测结果。

管理员业务流程侧重平台运营管理。管理员可查看所有用户的列表及状态信息，可对违规用户执行账户禁用操作，对已禁用的用户执行恢复操作。管理员同时负责监控平台各微服务的注册状态和健康检查状态，通过Nacos控制台进行服务治理，确保平台整体运行稳定。

以上业务流程构成了平台的核心操作闭环：注册开户、行情研究、下单交易、持仓管理、财务分析，形成了一条完整的互联网金融投资服务链。

## 4.2 业务架构设计

本平台采用面向业务领域的模块化架构划分方式，将整体系统划分为三大业务域，每个业务域内部聚合高内聚的业务功能，域间通过微服务间的轻量级REST通信实现松耦合协作。平台的业务架构如图7所示。

第一个业务域为用户服务域，涵盖用户身份管理和资产管理的核心功能。该域包含用户微服务（user-service）和资产组合微服务（portfolio-service）两个服务组件。用户微服务负责用户注册、登录认证、个人信息管理及头像上传等基础功能；资产组合微服务负责持仓查询、资产总览、盈亏计算及资金管理（充值、提现）等功能。两个服务协同工作，为用户提供从账户开通到资产跟踪的一站式服务体验。用户微服务作为身份认证中心，向网关层暴露登录态校验逻辑，其他微服务通过解析JWT令牌获取用户身份信息。

第二个业务域为市场数据域，围绕股票行情数据的获取、加工与展示展开。该域以行情微服务（market-service）为核心，整合了以下功能：股票搜索（关键词模糊匹配）、实时行情查询（当前价格、涨跌幅、成交量等）、K线数据服务（日线、周线、月线）、技术指标计算（MA、MACD、KDJ、RSI、BOLL）以及批量模拟行情接口。此外，自选股管理（/watchlist/**）和价格预警管理（/alert/**）作为市场数据域的扩展功能，同样由行情微服务承载。自选股功能允许用户维护个人关注的股票列表并查看实时行情；价格预警功能允许用户设置预警条件（高于或低于目标价），系统定时扫描行情数据并在条件触发时生成通知。

第三个业务域为交易分析域，聚焦订单执行和投资研究两大核心能力。该域包含交易微服务（trading-service）和分析微服务（analysis-service）。交易微服务负责普通订单的提交、确认和撤销操作，以及条件单（止损单、止盈单）的全生命周期管理，包括条件单的定时触发调度、价格偏离校验及自动下单执行。分析微服务提供上市公司财务数据分析（总览、利润表、资产负债表、现金流量表、营收趋势）以及量化策略回测引擎（MA交叉、MACD、动量突破、布林带），回测结果包含总收益率、年化收益率、最大回撤率、胜率、夏普比率等专业绩效指标。Spring Cloud Gateway网关层将条件单请求（/conditional-order/**）路由至交易微服务，将回测请求（/backtest/**）路由至分析微服务，实现了业务域内部的路由收敛。

三大业务域通过Nacos注册中心实现服务发现，各微服务启动时自动将自身注册至Nacos，网关层基于服务名进行负载均衡转发，不需要硬编码服务地址。业务域之间的交互遵循统一的API契约，采用RESTful风格接口，响应体统一封装为包含code、message和data字段的JSON格式，保证了前后端及服务间通信的一致性。图7展示了平台的整体业务架构。

（图7 系统业务架构图）

## 4.3 功能模块设计

基于三大业务域的划分，平台进一步将系统功能拆分为五个独立的功能模块。每个模块封装一组紧密相关的业务能力，对外提供统一的服务接口。图8展示了平台的功能模块设计。

用户模块是整个系统的入口模块，负责用户身份的生命周期管理。该模块包含以下子功能：用户注册子模块，接收用户提交的注册信息，完成用户名唯一性校验，持久化用户记录并自动创建资金账户；用户登录子模块，验证用户名和密码的正确性，生成JWT令牌并写入Redis缓存，返回令牌及用户基本信息；个人信息管理子模块，支持查看和修改个人资料；头像上传子模块，接收用户上传的图片文件，调用MinIO对象存储服务完成文件持久化，并更新用户头像URL；资金账户管理子模块，提供账户余额查询、账户状态管理等功能。

行情模块是平台的数据展示层，为用户提供全面的市场信息。股票搜索子模块支持按代码或名称关键词进行模糊检索，返回匹配的股票基础信息列表。实时行情查询子模块通过stockCode精确查询单只股票的当前价格、涨跌额、涨跌幅、最高价、最低价、开盘价及成交量等实时指标。K线数据子模块支持日线、周线、月线三种周期，返回指定时间范围内的OHLC（开盘价、最高价、最低价、收盘价）数据序列。技术指标子模块在K线数据基础上计算MA、MACD、KDJ、RSI、BOLL五项指标，以结构化对象返回各项指标的具体数值。自选股子模块支持用户添加、删除和查看自选股票列表，每个用户对同一股票只能添加一次（唯一约束），自选股列表可关联实时行情数据一并返回。价格预警子模块支持用户为指定股票设置价格预警条件（PRICE_ABOVE高于目标价或PRICE_BELOW低于目标价），预警状态包括ACTIVE（生效中）、TRIGGERED（已触发）和CANCELLED（已取消），触发时生成通知记录。

交易模块是平台的核心业务模块，承载订单处理的关键逻辑。普通下单子模块支持市价单和限价单两种类型，买入方向在提交阶段完成余额校验和资金冻结，卖出方向完成持仓校验和股份冻结，订单状态流转遵循"待成交→已成交或已取消"的状态机。订单管理子模块提供订单历史查询，支持按用户筛选，返回订单列表及详情。条件单管理子模块支持止损单（STOP_LOSS）和止盈单（TAKE_PROFIT）两种类型，用户设置触发价格后，后台定时调度器（@Scheduled每10秒轮询）自动扫描ACTIVE状态的条件单，当股价达到触发条件且价格偏离不超过3%时，自动通过OrderService完成下单和成交操作。

资产模块主要负责用户投资组合的综合展示和资金管理。持仓查询子模块返回用户所有持仓的详细信息，包括股票代码、名称、持有数量（总量、可用量、冻结量）、持仓均价，并基于实时行情数据计算当前市值、浮动盈亏和盈亏百分比。资产总览子模块聚合计算用户的总资产（可用资金+冻结资金+持仓市值）、总市值和总盈亏，为用户提供一站式的资产状况视图。盈亏计算子模块采用"当前市值-持仓成本"的公式计算绝对盈亏额，以"盈亏额/持仓成本×100%"的公式计算盈亏百分比，保留四位小数精度。充值提现子模块分别处理资金增加和资金减少操作，均要求金额为正数的合法性校验，提现操作附加余额充足性校验。

分析模块为平台提供深度投资研究功能。财务分析子模块包含以下接口：财务总览（overview）返回最新报告期的核心指标，包括营业收入、净利润、总资产、总负债、股东权益、ROE、EPS、PE比率、PB比率、股息率等；利润表接口返回营业收入、净利润和每股收益；资产负债表接口返回总资产、总负债和股东权益；现金流量表接口返回经营活动、投资活动、筹资活动现金流净额及现金净增加额；营收趋势接口按报告日期升序返回各期营业收入及基于同季度同比计算的增长率序列。策略回测子模块内置均值交叉（MA_CROSSOVER）、MACD信号、动量突破（MOMENTUM）和布林带（BOLLINGER）四种策略模板，用户配置参数后系统加载指定时间范围的K线数据（要求最少60个交易日，最长3年），由回测引擎执行模拟交易，输出总收益率、年化收益率、最大回撤率、胜率、交易总次数、盈利次数及夏普比率等专业绩效指标，并以JSON格式保存净值曲线和逐笔交易记录。

（图8 功能模块设计）

## 4.4 技术架构设计

本平台采用基于Spring Cloud微服务体系的分层架构设计，自顶向下分为客户端层、网关层、服务层和数据层四个逻辑层次。图9展示了平台的整体技术架构。

客户端层即Web前端，采用Vue 3框架配合TypeScript语言和Vite构建工具开发，使用Tailwind CSS原子化CSS框架进行页面样式编排。前端项目独立部署，通过Vite的开发代理（proxy）功能将API请求转发至后端服务，在开发阶段不依赖Spring Cloud Gateway。用户的所有操作均通过前端页面的HTTP请求发起，后端以JSON格式返回数据，前端进行渲染展示。

网关层由Spring Cloud Gateway微服务承担，运行于8080端口。网关层是外部请求进入后端服务体系的唯一入口，承担请求路由、负载均衡和统一跨域处理等职责。网关配置了以下路由规则：/user/**路由至用户微服务，/trade/**和/conditional-order/**路由至交易微服务，/market/**、/watchlist/**和/alert/**路由至行情微服务，/portfolio/**路由至资产微服务，/analysis/**和/backtest/**路由至分析微服务。网关启用服务发现定位器（discovery locator），通过Nacos注册中心动态获取目标微服务实例列表，基于Spring Cloud LoadBalancer实现客户端负载均衡。

服务层是平台业务逻辑的核心承载层，由五个独立的微服务及通用模块（common）组成。通用模块定义了所有微服务共享的实体类（User、FundAccount、Order、Position、Stock、StockKLine、FinancialData等）、通用工具类（JwtUtil、PasswordUtil等）、异常类（BusinessException等）和常量定义，避免了代码重复。五个微服务均遵循Spring Boot 2.7.18规范，通过MyBatis-Plus 3.5.3.1操作MySQL数据库，内部采用经典的Controller-Service-Mapper三层架构。各微服务启动时自动向Nacos注册中心汇报自身的服务名和网络地址。

服务注册与配置中心采用Nacos 2.1.0，部署于127.0.0.1:8848。Nacos同时承载服务发现和配置管理两大职责：服务发现方面，所有微服务实例均在启动时注册至Nacos的public命名空间，网关和各服务之间通过服务名进行调用，无需硬编码IP和端口；配置管理方面，各微服务的动态配置（如数据库连接信息、Redis连接参数、MinIO端点等）以yaml格式存储在Nacos配置中心，微服务启动时自动拉取，实现配置的集中管理和动态刷新。

数据层采用MySQL 8.0关系型数据库、Redis缓存数据库和MinIO对象存储的组合方案。MySQL作为主数据存储，承载所有业务数据的持久化，包含用户、资金账户、订单、持仓、股票基础信息、K线数据、行情快照、财务数据、价格预警、自选股、回测策略和回测结果等十余张核心数据表。Redis作为缓存层，承担两个核心职责：一是存储用户登录令牌（key格式为user:token:userId，过期时间7天），支撑网关层的令牌校验和无状态会话管理；二是作为行情数据的临时缓存。MinIO对象存储用于保存用户上传的头像等静态文件，服务启动时自动检查并创建avatars存储桶，文件以"userId/filename"的路径组织。

（图9 技术架构设计）

## 4.5 数据库设计

本平台数据库采用MySQL 8.0关系型数据库，数据库名为stock_investment，部署于172.20.10.6:3306。系统共设计11张核心数据表，覆盖用户身份、资金账户、交易订单、持仓管理、股票行情、财务数据及策略回测等业务领域。图10展示了数据库的E-R关系图。

以下为核心数据表的详细设计。

**表1 users（用户表）**

用户表记录平台注册用户的基本身份信息，是系统的核心实体之一。用户注册时写入，密码经哈希加密后存储，状态字段支持启用（1）和禁用（0）两种状态。该表通过MyBatis-Plus的自动填充机制管理create_time和update_time审计字段。

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 用户主键ID |
| username | VARCHAR(50) | NOT NULL, UNIQUE | 用户名，用于登录 |
| password | VARCHAR(255) | NOT NULL | 密码，BCrypt哈希加密 |
| email | VARCHAR(100) | - | 电子邮箱 |
| phone | VARCHAR(20) | - | 手机号码 |
| avatar_url | VARCHAR(500) | - | 头像URL，指向MinIO存储 |
| status | TINYINT | NOT NULL, DEFAULT 1 | 状态：1-正常，0-禁用 |
| create_time | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | NOT NULL, ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**表2 fund_accounts（资金账户表）**

资金账户表与用户表一对一关联，是用户进行股票交易的资金基础。账户号（account_no）以"FA"为前缀加时间戳和随机数生成，确保唯一性。初始余额设定为100,000元，frozen_balance用于记录下单后在成交前被冻结的资金金额。balance与frozen_balance的差值即为用户当前可用资金。

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 账户主键ID |
| user_id | BIGINT | NOT NULL, FK(users.id) | 关联用户ID |
| account_no | VARCHAR(32) | NOT NULL, UNIQUE | 资金账户编号，FA前缀 |
| balance | DECIMAL(16,2) | NOT NULL, DEFAULT 0.00 | 账户余额 |
| frozen_balance | DECIMAL(16,2) | NOT NULL, DEFAULT 0.00 | 冻结金额 |
| status | TINYINT | NOT NULL, DEFAULT 1 | 状态：1-正常，0-禁用 |
| create_time | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | NOT NULL, ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**表3 orders（订单表）**

订单表记录每一笔交易委托的完整信息，是交易模块的核心数据实体。订单号（order_no）以"ORD"为前缀生成，确保业务唯一性。direction字段区分买入（1）和卖出（2），status字段流转遵循"1待成交→2已成交/3已取消/4已拒绝"的状态机，order_type区分市价单（1）和限价单（2），conditional_order_id关联条件单表，标识由条件触发自动生成的订单。

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 订单主键ID |
| order_no | VARCHAR(32) | NOT NULL, UNIQUE | 订单编号，ORD前缀 |
| user_id | BIGINT | NOT NULL, FK(users.id) | 下单用户ID |
| fund_account_id | BIGINT | NOT NULL, FK(fund_accounts.id) | 关联资金账户ID |
| stock_code | VARCHAR(10) | NOT NULL | 股票代码 |
| stock_name | VARCHAR(50) | - | 股票名称 |
| direction | INT | NOT NULL | 方向：1-买入，2-卖出 |
| price | DECIMAL(10,2) | NOT NULL | 委托价格 |
| quantity | INT | NOT NULL | 委托数量（股） |
| amount | DECIMAL(16,2) | NOT NULL | 订单金额（price × quantity） |
| status | INT | NOT NULL, DEFAULT 1 | 状态：1-待成交，2-已成交，3-已取消，4-已拒绝 |
| order_type | INT | NOT NULL | 类型：1-市价，2-限价 |
| conditional_order_id | BIGINT | DEFAULT NULL | 关联条件单ID |
| create_time | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | NOT NULL, ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**表4 conditional_orders（条件单表）**

条件单表存储用户设置的止损和止盈条件委托。condition_type取值为STOP_LOSS（止损）或TAKE_PROFIT（止盈），status字段流转遵循"ACTIVE→TRIGGERED/CANCELLED/EXPIRED"的状态机。triggered_order_id记录条件触发后实际生成的订单ID，实现条件单与普通订单的追溯关联。定时调度器每10秒扫描状态为ACTIVE的记录，检测触发条件是否满足。

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 条件单主键ID |
| order_no | VARCHAR(32) | NOT NULL, UNIQUE | 条件单编号，COT前缀 |
| user_id | BIGINT | NOT NULL, FK(users.id) | 用户ID |
| fund_account_id | BIGINT | NOT NULL | 资金账户ID |
| stock_code | VARCHAR(10) | NOT NULL | 股票代码 |
| stock_name | VARCHAR(50) | - | 股票名称 |
| condition_type | VARCHAR(20) | NOT NULL | 类型：STOP_LOSS/TAKE_PROFIT |
| trigger_price | DECIMAL(10,2) | NOT NULL | 触发价格 |
| order_price | DECIMAL(10,2) | NOT NULL | 委托下单价格 |
| quantity | INT | NOT NULL | 交易数量 |
| direction | INT | NOT NULL | 方向：1-买入，2-卖出 |
| status | VARCHAR(20) | NOT NULL, DEFAULT ACTIVE | 状态：ACTIVE/TRIGGERED/CANCELLED/EXPIRED |
| triggered_order_id | BIGINT | DEFAULT NULL | 触发后关联的订单ID |
| fail_reason | VARCHAR(200) | DEFAULT NULL | 触发失败原因 |
| create_time | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | NOT NULL, ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**表5 positions（持仓表）**

持仓表记录用户对每只股票的持有情况。total_quantity为总持有数量，available_quantity为可卖数量（扣除冻结部分），frozen_quantity为因在途卖出订单而冻结的数量。avg_cost为加权平均持仓成本，买入成交时根据新买入量与原持有量的加权平均重新计算。total_quantity为零时记录将被删除。

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 持仓主键ID |
| user_id | BIGINT | NOT NULL, FK(users.id) | 用户ID |
| fund_account_id | BIGINT | NOT NULL, FK(fund_accounts.id) | 资金账户ID |
| stock_code | VARCHAR(10) | NOT NULL | 股票代码 |
| stock_name | VARCHAR(50) | - | 股票名称 |
| total_quantity | INT | NOT NULL, DEFAULT 0 | 总持仓数量 |
| available_quantity | INT | NOT NULL, DEFAULT 0 | 可用数量（可卖出） |
| frozen_quantity | INT | NOT NULL, DEFAULT 0 | 冻结数量（在途卖出） |
| avg_cost | DECIMAL(10,3) | NOT NULL, DEFAULT 0.000 | 加权平均成本 |
| create_time | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | NOT NULL, ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**表6 financial_data（财务数据表）**

财务数据表存储上市公司的财务报告指标，按report_type区分季报（1）、半年报（2）和年报（3）。每条记录包含利润表、资产负债表及现金流量表的核心科目数据，以及据此计算的ROE、EPS、市盈率、市净率、股息率等投资分析指标。

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 财务数据主键ID |
| stock_code | VARCHAR(10) | NOT NULL | 股票代码 |
| report_type | INT | NOT NULL | 报告类型：1-季报，2-半年报，3-年报 |
| report_date | DATE | NOT NULL | 报告日期 |
| revenue | DECIMAL(16,2) | - | 营业收入 |
| net_profit | DECIMAL(16,2) | - | 净利润 |
| total_assets | DECIMAL(16,2) | - | 总资产 |
| total_liabilities | DECIMAL(16,2) | - | 总负债 |
| shareholders_equity | DECIMAL(16,2) | - | 股东权益 |
| roe | DECIMAL(10,4) | - | 净资产收益率（ROE） |
| eps | DECIMAL(10,4) | - | 每股收益（EPS） |
| pe_ratio | DECIMAL(10,4) | - | 市盈率（PE） |
| pb_ratio | DECIMAL(10,4) | - | 市净率（PB） |
| operating_cash_flow | DECIMAL(16,2) | - | 经营活动现金流净额 |
| investing_cash_flow | DECIMAL(16,2) | - | 投资活动现金流净额 |
| financing_cash_flow | DECIMAL(16,2) | - | 筹资活动现金流净额 |
| net_cash_flow | DECIMAL(16,2) | - | 现金净增加额 |
| dividend_yield | DECIMAL(10,4) | - | 股息率 |
| create_time | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | NOT NULL, ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

除以上六张核心表外，系统还设计了stocks（股票基础信息表，包含stock_code、stock_name、industry行业分类、market_type市场类型及status上市状态）、stock_klines（K线数据表，按日期存储OHLC及成交量）、stock_quotes（实时行情快照表，存储当前价格、涨跌幅等）、price_alerts（价格预警表，记录用户设置的预警条件和触发状态）、watchlist（自选股表，以user_id和stock_code的联合唯一约束限制重复添加）、backtest_strategies（回测策略表，保存用户创建的策略配置及参数JSON）及backtest_results（回测结果表，存储策略运行后的绩效指标、净值曲线和交易记录JSON）。以上各表共同构成了平台的数据存储基础。

（图10 ER图）


# 第五章 详细设计与实现

## 5.1 环境介绍

本系统的开发、构建与运行涉及前后端多技术栈的协同，完整的开发环境配置如表20所示。

**表20 系统开发环境**

| 类别 | 工具/技术 | 版本/说明 |
|------|-----------|-----------|
| 操作系统 | Windows 10 / Linux（WSL2） | 开发与运行环境 |
| 开发语言 | Java / TypeScript | 后端JDK 1.8，前端Node.js 14.20.0 |
| 后端框架 | Spring Boot + Spring Cloud + Spring Cloud Alibaba | Spring Boot 2.7.18, Spring Cloud 2021.0.8, Spring Cloud Alibaba 2021.0.5.0 |
| ORM框架 | MyBatis-Plus | 3.5.3.1 |
| 前端框架 | Vue 3 + TypeScript + Vite + Tailwind CSS | Vue 3组合式API，Vite开发服务器 |
| 关系型数据库 | MySQL | 8.0，部署于172.20.10.6:3306 |
| 缓存数据库 | Redis | 用于JWT令牌缓存和会话管理 |
| 注册与配置中心 | Nacos | 2.1.0，部署于127.0.0.1:8848 |
| 对象存储 | MinIO | 用于用户头像等静态文件存储 |
| 项目构建 | Maven | 3.6.1 |
| 版本管理 | Git | 分布式版本控制 |
| 开发工具 | IntelliJ IDEA + VS Code + Navicat | Java开发、前端开发、数据库管理 |
| JWT库 | jjwt | 0.11.5 |
| 工具库 | Hutool | 5.8.22（Java工具类） |
| JSON处理 | Fastjson2 | 2.0.43 |

项目整体采用Maven多模块父子工程结构，根POM定义公共依赖版本管理（dependencyManagement），子模块继承后按需引入具体依赖。common模块作为基础通用模块，封装了所有微服务共享的实体类、工具类和常量定义，其他服务模块依赖common以实现代码复用。各微服务遵循标准的Controller-Service-Mapper分层架构，Controller层暴露RESTful接口并处理请求参数校验，Service层承载核心业务逻辑并管理事务边界，Mapper层通过MyBatis-Plus的BaseMapper接口实现数据的增删改查。微服务间通过Spring Cloud OpenFeign声明式HTTP客户端进行同步调用，同时借助Nacos实现服务注册与自动发现。

## 5.2 功能模块实现

### 5.2.1 用户注册与登录

用户注册与登录是平台的入口功能，由用户微服务（user-service）的UserService类集中实现。注册流程采用@Transactional事务注解保证数据一致性：首先检查用户名是否已存在，若不存在则创建User实体并写入数据库；随后自动生成以"FA"为前缀、时间戳拼接随机数构成的资金账户编号，创建FundAccount实体，设置初始余额为100,000元，冻结余额为零，状态为正常，完成写入。注册成功即拥有虚拟交易资金，用户无需额外的开户操作。

登录流程基于JWT令牌认证与Redis会话管理相结合的方式实现。系统接收用户名和密码后，先通过MyBatis-Plus的selectList查询所有用户记录，以Stream过滤匹配用户名（此方式在用户量较大的生产环境中应替换为索引查询），未找到匹配用户或密码不匹配时统一返回"用户名或密码错误"，避免暴露用户存在性信息。密码验证使用BCrypt哈希比对。验证通过后，调用JwtUtil工具类生成包含userId和username声明的JWT令牌，并将令牌以"user:token:用户ID"为key存入Redis，过期时间设置为7天（604,800秒）。登录响应中携带令牌、用户ID、用户名和头像URL等信息供前端存储和使用。退出登录操作通过删除Redis中对应的token key实现。

登录功能的核心代码实现如下：

```java
public LoginResponse login(LoginRequest request) {
    User user = userMapper.selectList(null).stream()
            .filter(u -> u.getUsername().equals(request.getUsername()))
            .findFirst()
            .orElseThrow(() -> new BusinessException(401, "用户名或密码错误"));
    if (!PasswordUtil.match(request.getPassword(), user.getPassword())) {
        throw new BusinessException(401, "用户名或密码错误");
    }
    String token = JwtUtil.generateToken(user.getId(), user.getUsername());
    redisTemplate.opsForValue().set(TOKEN_PREFIX + user.getId(), token,
            TOKEN_EXPIRE_SECONDS, TimeUnit.SECONDS);
    LoginResponse response = new LoginResponse();
    response.setToken(token);
    response.setUserId(user.getId());
    response.setUsername(user.getUsername());
    // ...
}
```

图11展示了用户登录界面的实际效果。

（图11 用户登录界面）

### 5.2.2 股票行情查询

股票行情查询功能由行情微服务（market-service）的MarketService类实现，通过MarketController暴露RESTful接口。系统主要提供以下行情数据服务：

股票搜索接口（GET /market/search）接收keyword可选参数，当keyword为空时返回全部股票列表，否则按股票代码或名称进行模糊匹配筛选。实时行情接口（GET /market/quote/{stockCode}）根据股票代码精确查询StockQuoteEntity表中最近更新的行情快照，返回当前价格、涨跌额、涨跌幅、最高价、最低价、开盘价及成交量等字段。K线数据接口（GET /market/kline/{stockCode}?period=daily|weekly|monthly）按指定周期类型查询stock_klines表中的历史K线数据，返回OHLC序列及对应的成交量。批量模拟行情接口（POST /market/simulated-quotes）接收股票代码列表，批量返回对应的实时行情数据。技术指标接口（GET /market/indicators/{stockCode}?period=daily）在K线数据基础上调用指标计算引擎，返回包含MA、MACD、KDJ、RSI、BOLL五项指标的TechnicalIndicators对象。

行情微服务配置了RateLimitInterceptor拦截器，对API请求实施速率限制，防止恶意高频调用。同时配置了GlobalExceptionHandler全局异常处理器，统一捕获并返回结构化的错误响应，避免内部异常信息泄露至客户端。

图12展示了行情查询界面的实际效果。

（图12 行情查询）

### 5.2.3 股票交易下单

股票交易下单是本平台的核心业务逻辑，由交易微服务（trading-service）的OrderService类实现。整个下单流程分为三个阶段：订单提交（placeOrder）、订单确认（confirmOrder）和订单取消（cancelOrder），均标注@Transactional事务注解以保证数据一致性。

订单提交阶段的核心逻辑为：首先根据委托价格和数量计算订单总金额；然后查询资金账户信息并校验其存在性和状态；买入方向时，计算可用余额（余额减冻结余额），若不足以支付订单金额则抛出"余额不足"异常，校验通过后将订单金额加入冻结余额；卖出方向时，查询持仓记录，若不存在或可用数量不足则抛出"持仓不足"异常，校验通过后将卖出数量从可用数量转入冻结数量。最后创建Order实体，生成以"ORD"为前缀的唯一订单号，初始化状态为PENDING（待成交），写入数据库。

订单确认阶段接收订单ID和用户ID，验证订单归属及状态合法性后，调用executeOrder方法执行实际交易：买入成交时，扣除冻结余额和账户余额，创建或更新持仓记录（增量买入按加权平均法重新计算持仓均价）；卖出成交时，增加账户余额，扣减持仓数量（数量归零则删除持仓记录）。状态更新为FILLED（已成交）。

订单取消阶段仅处理PENDING状态的订单，买入方向解冻资金（减少冻结余额），卖出方向解冻股份（将冻结数量转回可用数量），状态更新为CANCELLED（已取消）。

下单提交的核心代码实现如下：

```java
@Transactional
public Order placeOrder(OrderRequest request) {
    BigDecimal amount = request.getPrice()
        .multiply(new BigDecimal(request.getQuantity()));
    FundAccount fundAccount = fundAccountMapper.selectById(
        request.getFundAccountId());
    if (fundAccount == null) {
        throw new BusinessException(400, "资金账户不存在");
    }
    if (request.getDirection() == OrderDirection.BUY) {
        BigDecimal available = fundAccount.getBalance()
            .subtract(fundAccount.getFrozenBalance());
        if (available.compareTo(amount) < 0) {
            throw new BusinessException(400, "余额不足");
        }
        fundAccount.setFrozenBalance(
            fundAccount.getFrozenBalance().add(amount));
        fundAccountMapper.updateById(fundAccount);
    }
    // ... sell position check, create order, insert
}
```

图13展示了下单界面的实际效果。

（图13 下单界面）

### 5.2.4 条件单管理

条件单管理功能允许用户预设止损和止盈触发条件，由系统自动监控行情并在条件满足时代为执行交易。该功能由交易微服务的ConditionalOrderService和ConditionalOrderScheduler协作完成。

ConditionalOrderService负责条件单的CRUD操作：创建条件单时，接收用户设定的条件类型（STOP_LOSS止损或TAKE_PROFIT止盈）、触发价格、委托价格、交易数量和方向，生成以"COT"为前缀的唯一条件单编号，初始状态设为ACTIVE（生效中）。用户可随时将ACTIVE状态的条件单撤销为CANCELLED状态。

ConditionalOrderScheduler是条件单自动触发的核心调度组件。该类利用Spring的@Scheduled注解，以10秒固定频率（fixedRate=10000）轮询数据库中所有状态为ACTIVE的条件单记录。每个轮询周期中，调度器遍历所有生效的条件单，首先从stock_quotes表查询目标股票的最新实时价格；获取当前价格后，根据条件类型判断触发逻辑——止损单（STOP_LOSS）在当前价不高于触发价格时触发，止盈单（TAKE_PROFIT）在当前价不低于触发价格时触发。

触发条件满足后，调度器执行价格偏离校验：计算当前价格与触发价格的绝对偏离百分比，若偏离超过3%阈值，则将条件单标记为EXPIRED（过期）并记录失败原因"价格偏差过大"，避免因行情剧烈波动导致不合理的交易执行。偏离校验通过的，调度器通过OrderService的placeOrder和confirmOrder接口自动依次完成下单和成交操作，并将条件单状态更新为TRIGGERED（已触发），记录关联的订单ID。若自动下单过程发生异常，条件单被标记为EXPIRED并记录失败原因。

条件单状态机设计为ACTIVE→TRIGGERED（触发成功）、ACTIVE→CANCELLED（用户主动撤销）、ACTIVE→EXPIRED（价格偏离过大或执行异常）三条转换路径，保证了各类场景下条件单状态的可追溯性。

### 5.2.5 资产组合管理

资产组合管理功能由资产微服务（portfolio-service）的PortfolioService类实现，为用户提供持仓查询、资产总览、盈亏计算和资金管理的综合服务。

资产总览（getOverview）方法是组合管理的核心聚合逻辑。方法按用户ID筛选资金账户和持仓记录，分别聚合计算三部分资产：现金资产方面，分别获取账户余额（可用现金）和冻结余额（冻结现金），两者合计为总现金；持仓资产方面，遍历所有持仓记录，对每只持仓股票查询实时价格（优先取stock_quotes表的当前价格，若无则基于持仓均价生成模拟波动价格），计算每只股票的市值（当前价格乘以持有数量）和成本（持仓均价乘以持有数量），汇总所有持仓的市值和成本；汇总层面，总资产等于可用现金加冻结现金加总持仓市值，总盈亏等于总市值减总成本，盈亏百分比以盈亏额除以总成本的百分比表示。

持仓明细（getPositions）方法为每只持仓构建PositionDetail对象，包含股票标识、持有数量、持仓均价、当前价格、市值、浮动盈亏（市值减成本）和盈亏百分比等字段。盈亏百分比计算时，以成本为分母，保留四位小数精度后乘以100转换为百分比形式。

现金管理功能提供充值（deposit）和提现（withdraw）两个接口。充值操作校验金额为正数后直接增加账户余额；提现操作在金额正数校验的基础上，增加余额充足性校验。两者均直接更新fund_accounts表的balance字段。

盈亏计算的核心代码实现如下：

```java
BigDecimal currentPrice = getCurrentPrice(
    position.getStockCode(), position.getAvgCost());
BigDecimal marketValue = currentPrice.multiply(
    new BigDecimal(position.getTotalQuantity()));
BigDecimal cost = position.getAvgCost().multiply(
    new BigDecimal(position.getTotalQuantity()));
BigDecimal profitLoss = marketValue.subtract(cost);
detail.setProfitLoss(profitLoss);
if (cost.compareTo(BigDecimal.ZERO) > 0) {
    BigDecimal profitLossPercent = profitLoss
        .divide(cost, 4, RoundingMode.HALF_UP)
        .multiply(new BigDecimal("100"));
    detail.setProfitLossPercent(profitLossPercent);
}
```

图14展示了资产组合界面的实际效果。

（图14 资产组合）

### 5.2.6 财务分析与策略回测

财务分析与策略回测功能由分析微服务（analysis-service）的AnalysisService和BacktestService两个核心服务类实现，分别面向基本面分析和技术量化分析两大投资研究场景。

AnalysisService负责财务数据分析，提供五个核心接口。财务总览（getOverview）接口按stock_code查询financial_data表的最新一条记录（按report_date降序排列），返回包含营业收入、净利润、总资产、总负债、股东权益、ROE、EPS、市盈率、市净率和股息率等核心指标的FinancialOverview对象。利润表接口返回营业收入、净利润和每股收益；资产负债表接口返回总资产、总负债和股东权益；现金流量表接口返回经营、投资、筹资三类活动的现金流净额及汇总的现金净增加额。营收趋势（getRevenueTrend）接口按报告日期升序查询营收序列数据，并基于同季度同比方法计算每个报告期的营收同比增长率：当数据点索引大于等于4（即至少有一年前数据）时，用当期营收减去一年前同季度营收后除以一年前营收，结果以百分比表示；不足四个季度的早期数据点的增长率置为null。

BacktestService负责量化策略回测的完整流程管理。回测执行（runBacktest）方法首先校验日期参数：起止日期范围不得超过3年，开始日期必须在结束日期之前。然后从stock_klines表加载指定股票在回测时间范围内的K线数据，要求至少60个交易日数据方能执行回测。回测开始前，系统自动保存用户使用的策略配置（BacktestStrategy实体，包含策略类型、股票代码和参数JSON），随后将K线数据和策略配置传递给BacktestEngine引擎执行模拟交易计算。

回测引擎根据策略类型执行相应逻辑：MA_CROSSOVER策略在短期均线上穿长期均线时发出买入信号、下穿时发出卖出信号；MACD策略在MACD线上穿信号线时买入、下穿时卖出；MOMENTUM策略在价格突破过去N日最高价时买入、持有指定天数后卖出；BOLLINGER策略在价格触及布林带下轨时买入、触及上轨时卖出。引擎运行时逐日遍历K线数据，根据策略信号模拟买卖操作，记录每次交易的入场价、出场价和单笔收益率，同时维护净值曲线（每日账户净值序列）。

回测完成后，引擎返回BacktestResult对象，其中包含以下核心绩效指标：total_return（总收益率，最终资金减去初始资金后除以初始资金）、annual_return（年化收益率，按复利公式折算）、max_drawdown（最大回撤率，净值曲线中的最大峰值到谷底的跌幅）、win_rate（胜率，盈利交易次数除以总交易次数）、total_trades（总交易次数）、winning_trades（盈利交易次数）、sharpe_ratio（夏普比率，衡量风险调整后收益）。此外，结果中还包含equity_curve_json（净值曲线JSON数组）和trades_json（逐笔交易记录JSON数组），供前端可视化展示。完整的回测结果持久化至backtest_results表，生成以"BTR"为前缀的唯一结果编号。

图15展示了回测结果界面的实际效果。

（图15 回测结果）

### 5.2.7 价格预警与自选股

价格预警与自选股功能由行情微服务（market-service）统一承载，分别通过PriceAlertController和WatchlistController暴露管理接口。

价格预警功能允许用户为关注的股票设置价格监控条件。用户创建预警时需指定股票代码、预警类型（PRICE_ABOVE价格上破或PRICE_BELOW价格下破）和目标价格。系统生成以"ALT"或类似格式的预警编号，初始状态为ACTIVE。后台通过定时任务定期扫描生效中的预警记录，当股票最新价格满足预警条件时，将预警状态更新为TRIGGERED，同时在price_alert_notifications表中创建通知记录，记录触发价格和触发时间。用户可通过读状态（is_read）追踪通知的查看情况。预警支持手动撤销操作，撤销后状态变为CANCELLED。

自选股功能为用户提供个性化的股票关注列表管理。添加自选股时，系统通过user_id和stock_code的数据库联合唯一约束（uk_user_stock）防止重复添加，用户尝试添加已存在的自选股时将收到相应的错误提示。自选股查询接口支持按用户ID返回其关注的全部股票列表，并可关联实时行情数据一并返回WatchlistQuoteVO对象，包含股票代码、名称、当前价格、涨跌幅等信息，使用户能够在自选股页面直观地掌握关注股票的实时市场表现。删除自选股操作按照用户ID和股票代码进行精确匹配移除。
