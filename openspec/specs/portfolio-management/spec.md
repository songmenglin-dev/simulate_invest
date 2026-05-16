## ADDED Requirements

### Requirement: Portfolio Overview
The system SHALL display an overview of the user's investment portfolio including total value, cash balance, and positions.

#### Scenario: View portfolio summary
- **WHEN** user navigates to the portfolio page
- **THEN** system SHALL display total market value, cash balance, and total profit/loss

#### Scenario: View position list
- **WHEN** user views the portfolio page
- **THEN** system SHALL display all current positions with quantity, average cost, and current value

### Requirement: Profit/Loss Calculation
The system SHALL calculate and display profit/loss for each position and overall portfolio.

#### Scenario: Calculate position P/L
- **WHEN** user views a position
- **THEN** system SHALL calculate and display the absolute profit/loss and percentage gain/loss

#### Scenario: Calculate portfolio total P/L
- **WHEN** user views the portfolio summary
- **THEN** system SHALL calculate and display the total profit/loss across all positions

### Requirement: Position Details
The system SHALL show detailed information for each position.

#### Scenario: View position details
- **WHEN** user clicks on a specific position
- **THEN** system SHALL display symbol, name, quantity, average cost, current price, market value, and P/L

#### Scenario: View cost basis
- **WHEN** user views position details
- **THEN** system SHALL display the average cost per share calculated from all buy transactions

### Requirement: Cash Management
The system SHALL track available cash and frozen funds for pending orders.

#### Scenario: View cash balance
- **WHEN** user views the portfolio page
- **THEN** system SHALL display available cash and frozen funds separately

#### Scenario: Update cash after trade
- **WHEN** a trade is executed
- **THEN** system SHALL update the cash balance accordingly