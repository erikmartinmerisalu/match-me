import { Route, Routes} from 'react-router-dom'
import LogIn from '../auth/LogIn'
import SignUp from '../auth/SignUp'
import {  useAuth } from '../context/AuthContext'
import UserProfile from '../pages/userprofile/UserProfile';
import NotFound from '../pages/notfound/NotFound';
import Match from '../pages/matches/Match';
import Home from '../pages/frontpage/Home';
import ChatPage from '../pages/chat/ChatPage';
import ViewProfile from '../pages/viewprofile/ViewProfile';  // Add this import

export default function AppRoutes() {
  const {loggedIn} = useAuth();

  return (
    <div>
        <Routes>
          <Route path="/home" element={<Home />} />
          <Route path="/login" element={<LogIn />} />
          <Route path="/signup" element={<SignUp />} />
          <Route path="/*" element={<NotFound />} />

          {loggedIn === true ? <>
            <Route path="/userprofile" element={<UserProfile />} />
            <Route path="/match" element={<Match />} />
            <Route path="/chat" element={<ChatPage />} />
            <Route path="/chat/:userId" element={<ChatPage />} />
            <Route path="/viewprofile/:userId" element={<ViewProfile />} />
          </> :
          <Route path="/login" element={<LogIn />} />
          }
        </Routes>
    </div>
  )
}