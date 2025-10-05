import { Route, Routes} from 'react-router-dom'
import LogIn from '../auth/LogIn'
import SignUp from '../auth/SignUp'
import {  useAuth } from '../context/AuthContext'
import UserProfile from '../pages/userprofile/UserProfile';
// import NotFound from '../pages/notfound/NotFound';
import Match from '../pages/matches/Match';
import Home from '../pages/frontpage/Home';

export default function AppRoutes() {
  const {loggedIn} = useAuth();

  return (
    <div>
        <Routes>
          <Route path="/home" element={<Home />} />
          <Route path="/login" element={<LogIn />} />
          <Route path="/signup" element={<SignUp />} />
          <Route path="/*" element={<Home />} />

          {loggedIn === true ? <>
            <Route path="/userprofile" element={<UserProfile />} />
            <Route path="/match" element={<Match />} />

          </> :
          <Route path="/login" element={<LogIn />} />
          }
        </Routes>
    </div>
  )
}
