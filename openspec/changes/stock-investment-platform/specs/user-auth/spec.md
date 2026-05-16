## ADDED Requirements

### Requirement: User Account Management
The system SHALL provide user account creation, login, and profile management.

#### Scenario: Create new account
- **WHEN** user provides required information (username, password) to create an account
- **THEN** system SHALL create a new user account and return success confirmation

#### Scenario: Login to account
- **WHEN** user enters valid credentials
- **THEN** system SHALL authenticate the user, generate JWT token, store token in Redis with TTL, and return token to client

#### Scenario: View user profile
- **WHEN** user navigates to the profile page
- **THEN** system SHALL display user information including username, avatar URL, and account settings

#### Scenario: Update user profile
- **WHEN** user updates profile information
- **THEN** system SHALL update the user record and return success confirmation

### Requirement: Avatar Upload
The system SHALL allow users to upload avatar images stored in MinIO.

#### Scenario: Upload avatar
- **WHEN** user uploads an avatar image file
- **THEN** system SHALL store the file in MinIO, update user record with avatar URL, and return the new avatar URL

#### Scenario: View avatar
- **WHEN** user profile is displayed
- **THEN** system SHALL return the MinIO-hosted avatar URL

### Requirement: Token Management with Redis
The system SHALL manage JWT tokens with Redis storage for fast validation.

#### Scenario: Store token on login
- **WHEN** user successfully logs in
- **THEN** system SHALL generate JWT token, store in Redis with 24-hour TTL, and return token to client

#### Scenario: Validate token on request
- **WHEN** user makes a request with JWT token
- **THEN** system SHALL check Redis for token existence, validate JWT signature, and process the request

#### Scenario: Logout and invalidate token
- **WHEN** user clicks logout
- **THEN** system SHALL delete token from Redis, clear the session, and redirect to login page

### Requirement: Fund Account Binding
The system SHALL allow users to bind and manage their fund accounts.

#### Scenario: View bound accounts
- **WHEN** user navigates to account settings
- **THEN** system SHALL display all bound fund accounts with their status