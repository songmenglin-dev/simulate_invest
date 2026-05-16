## 1. Project Infrastructure Setup

- [ ] 1.1 Initialize Spring Cloud project with Spring Boot 2.7.x and JDK8
- [ ] 1.2 Create project structure (user-service, trading-service, market-service, portfolio-service, analysis-service, gateway)
- [ ] 1.3 Configure Maven multi-module project
- [ ] 1.4 Add Spring Cloud Alibaba dependencies (Nacos, OpenFeign, Sentinel)
- [ ] 1.5 Download and configure Nacos Server (standalone mode)
- [ ] 1.6 Register all services to Nacos with spring.cloud.nacos.discovery
- [ ] 1.7 Configure MySQL 8.0 database connection per service
- [ ] 1.8 Configure Redis for token and captcha storage
- [ ] 1.9 Configure MinIO client for file storage
- [ ] 1.10 Set up API Gateway (Spring Cloud Gateway) with Nacos routing

## 2. User Service (user-service)

- [ ] 2.1 Create User entity with MySQL mapping
- [ ] 2.2 Implement user registration with validation
- [ ] 2.3 Implement login with JWT token generation
- [ ] 2.4 Store token in Redis with TTL
- [ ] 2.5 Create captcha generation endpoint
- [ ] 2.6 Store captcha in Redis with expiration
- [ ] 2.7 Implement user profile endpoint
- [ ] 2.8 Implement avatar upload to MinIO
- [ ] 2.9 Configure Spring Cloud OpenFeign for inter-service calls

## 3. Market Data Service (market-service)

- [ ] 3.1 Set up market data API integration (East Money/Sina)
- [ ] 3.2 Implement stock quote fetching with caching
- [ ] 3.3 Create stock search by code and name
- [ ] 3.4 Implement K-line data fetching
- [ ] 3.5 Calculate technical indicators (MA, MACD, KDJ)
- [ ] 3.6 Add Redis caching layer for rate limiting
- [ ] 3.7 Expose market data via REST API

## 4. Trading Service (trading-service)

- [ ] 4.1 Create Order entity with MySQL mapping
- [ ] 4.2 Implement order placement (buy/sell)
- [ ] 4.3 Create order confirmation flow
- [ ] 4.4 Implement order cancellation
- [ ] 4.5 Create order history query
- [ ] 4.6 Add trade execution simulation
- [ ] 4.7 Implement order state machine
- [ ] 4.8 Integrate with market service for current prices

## 5. Portfolio Service (portfolio-service)

- [ ] 5.1 Create Position entity with MySQL mapping
- [ ] 5.2 Update position on trade execution
- [ ] 5.3 Create portfolio overview endpoint
- [ ] 5.4 Calculate profit/loss for positions
- [ ] 5.5 Create position details endpoint
- [ ] 5.6 Manage cash balance with transactions

## 6. Analysis Service (analysis-service)

- [ ] 6.1 Integrate financial data API
- [ ] 6.2 Create financial data entities
- [ ] 6.3 Implement financial overview endpoint
- [ ] 6.4 Query income statement data
- [ ] 6.5 Query balance sheet data
- [ ] 6.6 Query cash flow statement
- [ ] 6.7 Calculate revenue trend analysis
- [ ] 6.8 Calculate valuation metrics (PE, PB, ROE, dividend yield)

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
- [ ] 8.3 Set up Spring Cloud Gateway routes
- [ ] 8.4 Integrate frontend with backend APIs via OpenFeign
- [ ] 8.5 Test end-to-end stock trading flow
- [ ] 8.6 Test portfolio profit/loss calculation
- [ ] 8.7 Test financial data display
- [ ] 8.8 Test technical chart rendering