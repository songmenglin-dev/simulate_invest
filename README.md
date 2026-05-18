# Stock Investment Platform

互联网金融股票投资平台 - A microservices-based stock investment platform built with Spring Boot.

## Tech Stack

**Backend**
- Spring Boot 2.7.18
- Spring Cloud 2021.0.8 + Spring Cloud Alibaba 2021.0.5.0
- MyBatis-Plus 3.5.3.1
- MySQL + Redis
- Nacos (service discovery & config)

**Frontend**
- Vue 3 + TypeScript
- Vite + Tailwind CSS

## Architecture

```
┌─────────────────────────────────────────┐
│                  Gateway                │
└─────────┬─────┬─────┬─────┬─────┬─────────┘
          │     │     │     │     │
    ┌─────┴─┐ ┌─┴─┐ ┌─┴─┐ ┌─┴─┐ ┌─┴─────┐
    │ User  │ │Market│ │Trading│ │Portfolio│ │Analysis│
    │Service│ │Service│ │Service│ │Service│  │Service│
    └───────┘ └─────┘ └─────┘ └─────┘ └───────┘
```

## Services

| Service | Port | Description |
|---------|------|-------------|
| gateway | 8080 | API Gateway |
| user-service | 8081 | User management, authentication |
| market-service | 8082 | Market data, stock quotes |
| trading-service | 8083 | Trading operations |
| portfolio-service | 8084 | Portfolio management |
| analysis-service | 8085 | Analytics, backtesting |

## Getting Started

### Prerequisites

- JDK 8+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Node.js 18+ (for frontend)

### Backend Setup

```bash
cd stock-investment-platform

# Build all modules
mvn clean install -DskipTests

# Run a specific service (from parent directory)
mvn spring-boot:run -pl <service-name> -DskipTests

# Example: run user-service
mvn spring-boot:run -pl user-service -DskipTests
```

### Frontend Setup

```bash
cd stock-investment-web
npm install
npm run dev
```

### Database Setup

```bash
# Import SQL scripts from stock-investment-platform/sql/
mysql -u root -p < stock-investment-platform/sql/init.sql
```

## Configuration

Services use `application.yml` for configuration. For sensitive settings, use environment variables or Nacos config center.

Key configurations:
- `spring.datasource.url` - MySQL connection
- `spring.redis.*` - Redis connection
- `spring.cloud.nacos.*` - Nacos discovery & config

## Project Structure

```
stock-investment-platform/
├── common/              # Shared entities, utils, constants
├── user-service/        # User management
├── market-service/      # Market data service
├── trading-service/     # Trading operations
├── portfolio-service/   # Portfolio management
├── analysis-service/    # Analytics & backtesting
├── gateway/             # API Gateway
└── sql/                 # Database scripts

stock-investment-web/    # Vue 3 frontend
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.