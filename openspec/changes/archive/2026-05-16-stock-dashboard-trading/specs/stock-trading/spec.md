## MODIFIED Requirements

### Requirement: Stock Trading
The system SHALL provide stock trading functionality with dropdown and button-based stock selection, order placement, and trade confirmation for A-share market, using Chinese market color convention.

#### Scenario: Select stock for trading
- **WHEN** user selects a stock from the dropdown list or clicks a hot stock quick-select button
- **THEN** system SHALL auto-fill the stock code, stock name, and current market price
- **AND** user SHALL NOT be required to manually type a stock code

#### Scenario: Place a buy order
- **WHEN** user selects a security and clicks "Buy" (red button)
- **THEN** system SHALL show order confirmation dialog with price, quantity, and estimated amount
- **AND** user SHALL confirm to submit the order

#### Scenario: Place a sell order
- **WHEN** user selects a held security and clicks "Sell" (green button)
- **THEN** system SHALL show order confirmation dialog pre-filled with holding information
- **AND** user SHALL confirm to submit the order

#### Scenario: View order history
- **WHEN** user navigates to the order history page
- **THEN** system SHALL display all historical orders with status, time, and details
- **AND** buy orders SHALL be marked in red, sell orders in green
