## Features

# User Registration & Authentication
Users can sign up with a unique email and password. Passwords are securely hashed using bcrypt with a salt. Authentication uses JWT sessions.

# User Profiles

- Complete profile before viewing recommendations
- Biographical info including hobbies, interests, preferences, and more
- Profile pictures (with default placeholder if none uploaded)
- Profile editing at any time

# Recommendations

- Matches are generated using a recommendation algorithm based on a minimum of 5 biographical data points
- Recommendations are prioritized to show the strongest matches first
- If there are less than 3 "good" recommendations, algorithm will lower threshold to provide more recommendations.
- Maximum of 10 recommendations displayed at a time
- Location-based filtering ensures recommendations are practical

# Connections

- Users can send connection requests
- Accept, dismiss, or disconnect from connections
- Only recommended or connected users are visible

# Real-Time Chat

- Chat with connected users
- Paginated chat history
- Online/offline indicators
- "Typing" indicator when a user is composing a message
- Real-time updates using WebSocket

# Location-Based Matching

- Optional geospatial proximity filtering using browser coordinates
- Maximum radius configurable by the user

## Run Match-me locally

To run the application on your local machine, follow these steps:

Navigate to the backend directory:
```bash
cd backend
```
Follow the instructions in README.md to install dependencies, configure environment variables, and start the server.

Navigate to the frontend directory:
```bash
cd frontend
```
Follow the instructions in README.md to install dependencies, configure environment variables, and start the React application.