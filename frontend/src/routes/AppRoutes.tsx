import { Route, Routes, useNavigate} from 'react-router-dom'
import LogIn from '../pages/auth/LogIn'
import SignUp from '../pages/auth/SignUp'
import {  useAuth } from '../context/AuthContext'
// import NotFound from '../pages/notfound/NotFound';
import Match from '../pages/matches/Match';
import Home from '../pages/frontpage/Home';
import ChatPage from '../pages/chat/ChatPage';
import ViewProfile from '../pages/viewprofile/ViewProfile';  // Add this import
import UserProfile from '../pages/userprofile/UserProfile';
import UserSettings from '../pages/usersettings/UserSettings';
import { useEffect, useRef } from 'react';
import { useToast } from '../context/ToastContext';
import ProtectedRoute from './ProtectedRoute';

export default function AppRoutes() {
  const {loggedIn, loggedInUserData} = useAuth();
  const navigate = useNavigate();
  const toast = useToast();
  const hasRun = useRef(false);


  return (
    <div>
        <Routes>
          <Route path="/home" element={ <Home />} />
          <Route path="/login" element={<LogIn />} />
          <Route path="/signup" element={<SignUp />} />
          <Route path="/*" element={<Home />} />

          {loggedIn === true ? <>
            <Route path="/userprofile" element={<UserProfile />} />
            <Route path="/match" element={<ProtectedRoute> <Match /> </ProtectedRoute> } />
            <Route path="/chat" element={<ProtectedRoute> <ChatPage /> </ProtectedRoute>} />
            <Route path="/chat/:userId" element={<ProtectedRoute> <ChatPage /> </ProtectedRoute>} />
            <Route path="/viewprofile/:userId" element={<ProtectedRoute> <ViewProfile /> </ProtectedRoute>} />
            <Route path="/settings" element={<ProtectedRoute> <UserSettings /> </ProtectedRoute>} />
          </> :
          <Route path="/login" element={<LogIn />} />
          }
        </Routes>
    </div>
  )
}