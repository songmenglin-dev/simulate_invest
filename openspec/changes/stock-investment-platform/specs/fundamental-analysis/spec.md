## ADDED Requirements

### Requirement: Financial Data Display
The system SHALL display financial data for selected securities including revenue, profit, assets, and liabilities.

#### Scenario: View financial overview
- **WHEN** user selects a security and navigates to financial page
- **THEN** system SHALL display key financial metrics including revenue, net profit, total assets, and total liabilities

#### Scenario: View income statement
- **WHEN** user navigates to the income statement section
- **THEN** system SHALL display revenue, cost of goods sold, operating expenses, and net profit

#### Scenario: View balance sheet
- **WHEN** user navigates to the balance sheet section
- **THEN** system SHALL display assets, liabilities, and shareholders' equity

#### Scenario: View cash flow statement
- **WHEN** user navigates to the cash flow section
- **THEN** system SHALL display operating cash flow, investing cash flow, and financing cash flow

### Requirement: Revenue Analysis
The system SHALL provide revenue trend analysis over multiple periods.

#### Scenario: View revenue trend
- **WHEN** user requests revenue analysis for a security
- **THEN** system SHALL display revenue for the last 4 quarters or 2 years

#### Scenario: Compare revenue growth
- **WHEN** user views revenue analysis
- **THEN** system SHALL calculate and display year-over-year growth rate

### Requirement: Valuation Metrics
The system SHALL calculate and display key valuation metrics including PE ratio, PB ratio, and ROE.

#### Scenario: View PE ratio
- **WHEN** user views valuation metrics for a security
- **THEN** system SHALL display the current PE (Price-to-Earnings) ratio

#### Scenario: View PB ratio
- **WHEN** user views valuation metrics for a security
- **THEN** system SHALL display the current PB (Price-to-Book) ratio

#### Scenario: View ROE
- **WHEN** user views valuation metrics for a security
- **THEN** system SHALL display the Return on Equity (ROE) percentage

#### Scenario: View dividend yield
- **WHEN** user views valuation metrics for a security
- **THEN** system SHALL display the dividend yield percentage