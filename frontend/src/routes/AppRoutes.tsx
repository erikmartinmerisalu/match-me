import { Route, Routes, useNavigate} from 'react-router-dom'
import LogIn from '../pages/auth/LogIn'
import SignUp from '../pages/auth/SignUp'
import {  useAuth } from '../context/AuthContext'
import Matches from '../pages/matches/Matches';
import Home from '../pages/frontpage/Home';
import ChatPage from '../pages/chat/ChatPage';
import ViewProfile from '../pages/viewprofile/ViewProfile';
import UserProfile from '../pages/userprofile/UserProfile';
import UserSettings from '../pages/usersettings/UserSettings';
import Recommendations from '../pages/matches/Recommendations';
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
            <Route path="/recommendations" element={<ProtectedRoute> <Recommendations /> </ProtectedRoute>} />
            <Route path="/settings" element={<ProtectedRoute> <UserSettings /> </ProtectedRoute>} />
            <Route path="/matches" element={<ProtectedRoute> <Matches /> </ProtectedRoute> } />
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