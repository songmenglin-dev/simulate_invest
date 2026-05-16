## 1. Project Infrastructure Setup

- [x] 1.1 Initialize Spring Cloud project with Spring Boot 2.7.x and JDK8
- [x] 1.2 Create project structure (user-service, trading-service, market-service, portfolio-service, analysis-service, gateway)
- [x] 1.3 Configure Maven multi-module project
- [x] 1.4 Add Spring Cloud Alibaba dependencies (Nacos, OpenFeign, Sentinel)
- [x] 1.5 Download and configure Nacos Server (standalone mode)
- [ ] 1.6 Register all services to Nacos with spring.cloud.nacos.discovery
- [x] 1.7 Configure MySQL 8.0 database connection per service
- [x] 1.8 Configure Redis for token and captcha storage
- [x] 1.9 Configure MinIO client for file storage
- [x] 1.10 Set up API Gateway (Spring Cloud Gateway) with Nacos routing

## 2. User Service (user-service)

- [x] 2.1 Create User entity with MySQL mapping
- [x] 2.2 Implement user registration with validation
- [x] 2.3 Implement login with JWT token generation
- [x] 2.4 Store token in Redis with TTL
- [x] 2.7 Implement user profile endpoint
- [x] 2.8 Implement avatar upload to MinIO
- [ ] 2.9 Configure Spring Cloud OpenFeign for inter-service calls

## 3. Market Data Service (market-service)

- [ ] 3.1 Set up market data API integration (East Money/Sina)
- [x] 3.2 Implement stock quote fetching with caching
- [x] 3.3 Create stock search by code and name
- [x] 3.4 Implement K-line data fetching
- [x] 3.5 Calculate technical indicators (MA, MACD, KDJ)
- [ ] 3.6 Add Redis caching layer for rate limiting
- [x] 3.7 Expose market data via REST API

## 4. Trading Service (trading-service)

- [x] 4.1 Create Order entity with MySQL mapping
- [x] 4.2 Implement order placement (buy/sell)
- [ ] 4.3 Create order confirmation flow
- [x] 4.4 Implement order cancellation
- [x] 4.5 Create order history query
- [x] 4.6 Add trade execution simulation
- [x] 4.7 Implement order state machine
- [ ] 4.8 Integrate with market service for current prices

## 5. Portfolio Service (portfolio-service)

- [x] 5.1 Create Position entity with MySQL mapping
- [x] 5.2 Update position on trade execution
- [x] 5.3 Create portfolio overview endpoint
- [x] 5.4 Calculate profit/loss for positions
- [x] 5.5 Create position details endpoint
- [ ] 5.6 Manage cash balance with transactions

## 6. Analysis Service (analysis-service)

- [x] 6.1 Integrate financial data API
- [x] 6.2 Create financial data entities
- [x] 6.3 Implement financial overview endpoint
- [x] 6.4 Query income statement data
- [x] 6.5 Query balance sheet data
- [x] 6.6 Query cash flow statement
- [x] 6.7 Calculate revenue trend analysis
- [x] 6.8 Calculate valuation metrics (PE, PB, ROE, dividend yield)

## 7. Frontend Development (web)

- [ ] 7.1 Set up React project with Ant Design
- [ ] 7.2 Create login and registration pages with captcha
- [ ] 7.3 Implement main dashboard layout
- [ ] 7.4 Create stock search component
- [ ] 7.5 Build trading page with order form
- [ ] 7.6 Implement portfolio overview page
- [ ] 7.7 Create position details view
- [ ] 7.8 Build fundamental analysis page with financial tables
- [ ] 7.9 Implement K-line chart with ECharts
- [ ] 7.10 Add technical indicator toggles

## 8. Infrastructure & Integration

- [ ] 8.1 Configure Nacos for service discovery
- [ ] 8.2 Configure Sentinel for flow control
- [x] 8.3 Set up Spring Cloud Gateway routes
- [ ] 8.4 Integrate frontend with backend APIs via OpenFeign
- [ ] 8.5 Test end-to-end stock trading flow
- [ ] 8.6 Test portfolio profit/loss calculation
- [ ] 8.7 Test financial data display
- [ ] 8.8 Test technical chart rendering