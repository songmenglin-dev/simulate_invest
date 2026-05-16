## ADDED Requirements

### Requirement: Real-time Stock Dashboard
The system SHALL provide a real-time dashboard displaying multiple stocks with auto-refreshing price changes, using Chinese market color convention (red = up, green = down).

#### Scenario: View dashboard with stock cards
- **WHEN** user navigates to the overview/dashboard page
- **THEN** system SHALL display a grid of stock cards showing stock name, current price, price change, and change percentage for at least 6 popular stocks
- **AND** price increases SHALL be displayed in red
- **AND** price decreases SHALL be displayed in green

#### Scenario: Auto-refresh stock prices
- **WHEN** the dashboard is displayed
- **THEN** system SHALL automatically refresh all displayed stock prices every 3-5 seconds
- **AND** price changes SHALL be visually animated when values update

#### Scenario: Price simulation with real data base
- **WHEN** real market data is available from the database or external API
- **THEN** system SHALL apply random fluctuation of ±0.3% to ±1.5% on top of the real base price for each refresh cycle

#### Scenario: Price simulation without real data
- **WHEN** no real market data is available
- **THEN** system SHALL generate price changes using a random walk algorithm starting from a seed price

#### Scenario: Simulated data indicator
- **WHEN** prices are simulated (not real-time from exchange)
- **THEN** system SHALL display an indicator (e.g., "模拟数据" label) to distinguish from live market data

### Requirement: Dashboard Stock Selection
The system SHALL allow users to customize which stocks are displayed on the dashboard.

#### Scenario: Default popular stocks
- **WHEN** dashboard loads for the first time
- **THEN** system SHALL display a preset list of popular A-share stocks

#### Scenario: Add stock to dashboard
- **WHEN** user selects a stock from the dropdown to add to dashboard
- **THEN** system SHALL add the stock card to the dashboard display

#### Scenario: Remove stock from dashboard
- **WHEN** user clicks remove on a stock card
- **THEN** system SHALL remove that stock from the dashboard display
