## ADDED Requirements

### Requirement: Real-time Quote Display
The system SHALL display real-time stock quotes including current price, change, volume, and turnover, using Chinese market color convention (red = up, green = down).

#### Scenario: View stock quote
- **WHEN** user searches for or selects a stock
- **THEN** system SHALL display current price, price change, change percentage, volume, and turnover
- **AND** positive price changes SHALL be displayed in red
- **AND** negative price changes SHALL be displayed in green

#### Scenario: Auto-refresh quote
- **WHEN** a quote is displayed
- **THEN** system SHALL automatically refresh the data at configurable intervals

### Requirement: Historical Data
The system SHALL provide historical price data for technical analysis.

#### Scenario: View daily K-line data
- **WHEN** user requests historical data for a stock
- **THEN** system SHALL display daily OHLC (Open, High, Low, Close) data for the selected period

#### Scenario: View weekly K-line data
- **WHEN** user selects weekly timeframe
- **THEN** system SHALL display weekly aggregated K-line data

### Requirement: Technical Indicators
The system SHALL calculate and display technical indicators including MA, MACD, and KD.

#### Scenario: View Moving Average
- **WHEN** user enables MA indicator on a chart
- **THEN** system SHALL display MA5, MA10, MA20 lines overlaid on the price chart

#### Scenario: View MACD indicator
- **WHEN** user enables MACD indicator
- **THEN** system SHALL display MACD line, signal line, and histogram

#### Scenario: View KDJ indicator
- **WHEN** user enables KDJ indicator
- **THEN** system SHALL display K, D, J lines with overbought/oversold zones

### Requirement: Stock Search
The system SHALL provide stock search functionality by code or name.

#### Scenario: Search by stock code
- **WHEN** user enters a 6-digit stock code
- **THEN** system SHALL return matching stocks starting with that code

#### Scenario: Search by stock name
- **WHEN** user enters a stock name or part of name
- **THEN** system SHALL return all stocks with names containing the search term

### Requirement: Simulated Quote Endpoint
The system SHALL provide an API endpoint that returns simulated real-time stock quotes with random price fluctuations for multiple stocks at once.

#### Scenario: Batch simulated quotes request
- **WHEN** client requests simulated quotes for a list of stock codes
- **THEN** system SHALL return current price, price change, and change percentage for each stock
- **AND** each price SHALL include a random fluctuation from the previous price

#### Scenario: Fluctuation based on real data
- **WHEN** a stock has recent price data in the database or cache
- **THEN** the simulated price SHALL be the base price plus a random delta within ±1.5%

#### Scenario: Fluctuation fallback when no data
- **WHEN** a stock has no recent price data
- **THEN** system SHALL use a default seed price and apply random walk to generate a simulated price