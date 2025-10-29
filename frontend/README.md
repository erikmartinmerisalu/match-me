# Match-Me Web (Frontend)

## Tech Stack (Frontend)
- React -v 19.1.1
- React-dom -v 19.1.1 
- Rreact-router-dom -v 7.9.3
- Vite -v 7.1.7
- Typescript -v 5.8.3

## Features

# Authentication
- Users log in with email and password.
- JWT tokens are stored in cookies and used for authenticated requests.
- Logout clears session cookie and frontend state.

# User Profile
- Users must complete their profile to receive recommendations.
- Profile includes username, preferred servers, games, gaming experience, gaming hours, “About me” section.
- Users can upload/remove profile pictures.
- Profile info is editable at any time.




## Running the Frontend

Clone the repository
```bash
git clone https://github.com/erikmartinmerisalu/match-me
```

Navigate to front end
```bash
cd .\match-me\frontend\
```
Install the needed Tech Stack components
```bash
npm install
```

Start the development server
```bash
npm run dev
```

The server starts at localhost port 5173 and you should see:
```bash
  VITE v7.1.7  ready in 0 ms

  ➜  Local:   http://localhost:5173/
  ➜  Network: use --host to expose
  ➜  press h + enter to show help
```