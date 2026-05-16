## ADDED Requirements

### Requirement: Stock Trading
The system SHALL provide stock trading functionality including security search, order placement, and trade confirmation for A-share market.

#### Scenario: Search for securities
- **WHEN** user enters a stock code or name in the search box
- **THEN** system SHALL display matching securities with their current prices

#### Scenario: Place a buy order
- **WHEN** user selects a security and clicks "Buy"
- **THEN** system SHALL show order confirmation dialog with price, quantity, and estimated amount
- **AND** user SHALL confirm to submit the order

#### Scenario: Place a sell order
- **WHEN** user selects a held security and clicks "Sell"
- **THEN** system SHALL show order confirmation dialog pre-filled with holding information
- **AND** user SHALL confirm to submit the order

#### Scenario: View order history
- **WHEN** user navigates to the order history page
- **THEN** system SHALL display all historical orders with status, time, and details

### Requirement: Order Management
The system SHALL manage trading orders with states: pending, filled, cancelled, rejected.

#### Scenario: Cancel pending order
- **WHEN** user views a pending order and clicks "Cancel"
- **THEN** system SHALL update the order status to "cancelled"

#### Scenario: View order details
- **WHEN** user clicks on a specific order
- **THEN** system SHALL display full order details including symbol, price, quantity, status, and timestamp

### Requirement: Trade Confirmation
The system SHALL require explicit confirmation before executing any trade.

#### Scenario: Confirm trade with insufficient balance
- **WHEN** user attempts to buy with insufficient funds
- **THEN** system SHALL display an error message and prevent order submission

#### Scenario: Confirm trade with valid information
- **WHEN** user confirms a buy order with valid information
- **THEN** system SHALL record the trade and update the user's positions