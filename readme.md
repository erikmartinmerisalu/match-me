Match-Me - Gamer Matching Platform
Project Overview

Match-Me is a full-stack gamer-themed matching platform that connects users based on their gaming preferences, availability, skill levels, and age compatibility. Built with Spring Boot and React, it helps gamers find compatible teammates and friends.
Technology Stack

    Backend: Java 17, Spring Boot 3.2.4
    Frontend: React with TypeScript (to be implemented)
    Database: PostgreSQL
    Security: JWT, BCrypt, Spring Security
    Build Tool: Maven

PostGreSQL server setup:
# 1. Update package list
sudo apt update

# 2. Install PostgreSQL server and client
sudo apt install -y postgresql postgresql-contrib postgresql-client

# 3. Check PostgreSQL service status
sudo service postgresql status

# 4. Start PostgreSQL service if not running
sudo service postgresql start

# 5. Switch to the postgres system user
sudo -u postgres psql

# 6. Inside the psql shell, create your database
CREATE DATABASE matchme_gaming;

# 7. Set the postgres user password
ALTER USER postgres PASSWORD 'password';

# 8. Exit psql
\q

# 9. Test connection to the new database
psql -U postgres -d matchme_gaming -h localhost -W
# Enter password: password
service postgresql status should show active (exited)—that means PostgreSQL is running.
Make sure the password you set matches the one in your application.properties.
After this, your Spring Boot app should be able to connect to the database.


Check if post
# 1. Navigate to your project directory
cd ~/match-me

# 2. Build the project with Maven
mvn clean install

# 3. Run the Spring Boot application
mvn spring-boot:run


Security Implementation
Authentication & Authorization

    JWT (JSON Web Tokens) for stateless session management
    BCrypt with salt for secure password hashing
    Spring Security for endpoint protection
    CORS configured for cross-origin requests


Security Flow

    User registers/login → JWT generated → Token sent to client
    Client includes token in Authorization: Bearer <token> header
    JwtAuthenticationFilter validates token on each request
    If valid, access granted to protected endpoints

Current Functionalities
✅ Fully Implemented
1. User Authentication
    User registration with email/password validation
    Secure login with JWT token generation
    Password hashing using BCrypt
    Logout functionality

2. User Profile Management
    Complete gaming profile with 5 main matching criteria:
        Preferred Servers - Game servers user plays on
        Games - Games the user plays
        Gaming Hours - When they typically play
        Rank - Skill level (Bronze to Grandmaster)
        Age - Calculated from birth date (±3 year matching)
    Profile completion validation (requires 5+ data points)
    Public/private profile visibility controls

3. Smart Recommendation System
    Weighted matching algorithm with factors:
        25% Server compatibility
        25% Game overlap
        20% Gaming hour alignment
        15% Rank proximity (±1-2 tiers)
        15% Age compatibility (±3 years)
    Returns top 10 compatible matches

    Only suggests users with completed profiles

4. Data Persistence
    PostgreSQL database integration
    JPA entities with proper relationships
    Automatic schema generation
    Test data loader with sample gamers

⚠️ Partially Implemented
Connection Management
    Basic connection endpoints defined
    Connection status tracking (PENDING, ACCEPTED, REJECTED)
    Connection-based profile visibility

❌ Not Yet Implemented
    Real-time chat functionality
    File upload for profile pictures
    Online status indicators
    Chat notifications
    Typing indicators

Project Structure
text

src/main/java/com/matchme/
├── config/
│   ├── SecurityConfig.java
│   └── DataLoader.java
├── controller/
│   ├── AuthController.java
│   ├── UserController.java
│   ├── RecommendationsController.java
│   └── ConnectionsController.java
├── service/
│   ├── UserService.java
│   ├── UserProfileService.java
│   ├── RecommendationService.java
│   └── ConnectionService.java
├── repository/
├── entity/
├── dto/
├── security/
│   └── JwtAuthenticationFilter.java
└── util/
    └── JwtUtil.java

API Endpoints
Authentication

    POST /api/auth/register - User registration

    POST /api/auth/login - User login

    POST /api/auth/logout - User logout

User Profiles

    GET /api/users/{id} - Basic user info

    GET /api/users/{id}/profile - Full profile

    GET /api/users/{id}/bio - Biographical data for matching

    GET /api/me, /me/profile, /me/bio - Current user shortcuts

    PUT /api/me/profile - Update profile

Recommendations & Connections

    GET /api/recommendations - Get match suggestions

    GET /api/connections - List user connections

    POST /api/connections/{userId}/connect - Send connection request

    POST /api/connections/{userId}/accept - Accept connection

Setup Instructions
Prerequisites

    Java 17

    PostgreSQL

    Maven

Database Setup

    Create PostgreSQL database: matchme_gaming

    Update application.properties with your credentials

Running the Application
bash

mvn clean compile
mvn spring-boot:run

The application will start on http://localhost:8080
Gamer Profile Data Points

The matching algorithm uses these 5 core criteria:

    Server Preferences - Which game servers the user plays on

    Game Library - What games the user plays

    Gaming Schedule - When the user is available to play

    Skill Rank - Competitive ranking/tier

    Age - For age-appropriate matching (±3 years)

Security Features

    Passwords never stored in plain text

    JWT tokens with 10-hour expiration

    Endpoint protection based on authentication

    CORS configured for frontend integration

    Email privacy (emails not exposed in user profiles)

Next Development Phase

    Real-time Chat - WebSocket implementation for messaging

    File Upload - Profile picture management

    Connection Logic - Complete connection workflow

    Frontend Application - React TypeScript implementation

    Advanced Features - Online indicators, typing notifications