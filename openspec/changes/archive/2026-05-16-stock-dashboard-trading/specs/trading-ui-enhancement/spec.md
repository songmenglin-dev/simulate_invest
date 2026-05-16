## ADDED Requirements

### Requirement: Stock Selection by Dropdown
The system SHALL allow users to select stocks for trading via a dropdown list with fuzzy filtering, replacing text-based code input.

#### Scenario: Select stock from dropdown
- **WHEN** user opens the stock selector dropdown on the trading page
- **THEN** system SHALL display all available stocks with their codes and names
- **AND** user SHALL be able to filter the list by typing part of the code or name

#### Scenario: Quick select via hot stock buttons
- **WHEN** the trading page loads
- **THEN** system SHALL display preset hot stock quick-select buttons (e.g., 贵州茅台, 招商银行, 比亚迪)
- **AND** clicking a button SHALL select that stock and auto-fill its current price

#### Scenario: Auto-fill current price on selection
- **WHEN** user selects a stock from dropdown or quick button
- **THEN** system SHALL fetch and auto-fill the current market price for the selected stock

#### Scenario: No stock code manual input required
- **WHEN** user places a trade
- **THEN** system SHALL NOT require manual stock code entry
- **AND** the stock code SHALL come from the dropdown or button selection

### Requirement: Trade Direction Color Convention
The system SHALL use Chinese market color convention for buy/sell and price movement throughout the trading interface.

#### Scenario: Buy button color
- **WHEN** the buy/sell direction toggle is displayed
- **THEN** the buy button SHALL be styled in red

#### Scenario: Sell button color
- **WHEN** the buy/sell direction toggle is displayed
- **THEN** the sell button SHALL be styled in green
