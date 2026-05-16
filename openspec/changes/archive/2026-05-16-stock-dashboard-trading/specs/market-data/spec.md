## ADDED Requirements

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

## MODIFIED Requirements

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
