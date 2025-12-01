Match-Me - Gamer Matching Platform

# Project Overview:
Match-Me is a full-stack gamer-themed matching platform that connects users based on their gaming preferences, availability, skill levels, and age compatibility. Built with Spring Boot and React, it helps gamers find compatible teammates and friends.

# Technology Stack:
    Backend: Java 17, Spring Boot 3.2.4
    Frontend: React with TypeScript
    Database: PostgreSQL
    Security: JWT, BCrypt, Spring Security
    Build Tool: Maven

# Security Implementation
    JWT (JSON Web Tokens) for stateless session management
    BCrypt with salt for secure password hashing
    Spring Security for endpoint protection
    CORS configured for cross-origin requests


# Security Flow:
    User registers/login → JWT generated → Token sent to client
    Client includes token in Authorization: Bearer <token> header
    JwtAuthenticationFilter validates token on each request
    If valid, access granted to protected endpoints

# Security Features
    Passwords never stored in plain text
    JWT tokens with 10-hour expiration
    Endpoint protection based on authentication
    CORS configured for frontend integration
    Email privacy (emails not exposed in user profiles)


# API Endpoints
Are all included in the POSTMAN Collection.

## PostGreSQL server setup(Linux/Ubuntu):
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

## PostGreSQL server setup (Windows):
# 1. Download PostgreSQL
Go to the official installer page:
https://www.postgresql.org/download/windows/
# 2. Run the installer
Choose components:
PostgreSQL Server, pgAdmin 4, Command Line Tools (psql)
2.1 Set the PostgreSQL superuser password to: "admin"
Choose default port: 5432
# 3. Verify PostgreSQL service is running
Open Windows Services (services.msc) and find: postgresql-x64-XX
Status should be: Running
If not running: Right-click → Start
# 4. Open psql (PostgreSQL Shell)
You will be asked:
1.Server [localhost]: 2.Database [postgres]: 3.Port [5432]: 4.Username [postgres]: 5.Password: admin
Press ENTER for defaults, then enter password admin.
# 5. Create your database
Inside psql enter prompt:
CREATE DATABASE matchme_gaming;
# 6. Test connection manually
Open Termnial / Command Prompt or PowerShell:
psql -U postgres -d matchme_gaming -h localhost -W
Enter the password:
admin
It should say "Active"/ "Active connection", this means your setup was a success!


## Run the back-end of the project: 

# 1. Navigate to your projects backend directory(Ubuntu/Linux):
cd ~/match-me
cd backend
# 2. Build the project with Maven
mvn clean install
# 3. Run the Spring Boot application
mvn spring-boot:run


# Testing the admin endpoints available to the developer/tester
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
and make a API call to create (count=)150 fake users:
```bash
http://localhost:8080/api/admin/create-fake-users?count=150
```
