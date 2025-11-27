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
ALTER USER postgres PASSWORD 'admin';

# 8. Exit psql
\q

# 9. Test connection to the new database
psql -U postgres -d matchme_gaming -h localhost -W
# Enter password: admin
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


# Testing the admin endpoints available to the developer/tester
Clone the repository

Create a .env file in the project root with:

properties
```bash
ADMIN_EMAIL=admin@example.com
ADMIN_PASSWORD=admin123
CREATE_DEFAULT_ADMIN=true
```
That's it! The admin user will be automatically created when they run the application.

To create fake users use Postman and make a api call to log in as a admin user
```bash
http://localhost:8080/api/auth/login
```
with credintials:
```bash
{
  "email": "admin@example.com",
  "password": "admin123"
}
```
and make a API call to create fake users:
```bash
http://localhost:8080/api/admin/create-fake-users
```
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

    Game experience - Total hours played

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