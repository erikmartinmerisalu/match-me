import { Route, Routes } from 'react-router-dom'
import LogIn from '../pages/auth/LogIn'
import SignUp from '../pages/auth/SignUp'
import { useAuth } from '../context/AuthContext'
import Matches from '../pages/matches/Matches';
import ChatPage from '../pages/chat/ChatPage';
import ViewProfile from '../pages/viewprofile/ViewProfile';
import UserProfile from '../pages/userprofile/UserProfile';
import UserSettings from '../pages/usersettings/UserSettings';
import Recommendations from '../pages/matches/Recommendations';
import ProtectedRoute from './ProtectedRoute';
import Home from '../pages/frontpage/Home/Home';
import LandingPage from '../pages/frontpage/LandingPage';

export default function AppRoutes() {
  const {loggedIn} = useAuth();

  return (
    <div>
        <Routes>
          {/* Public landing page */}
          <Route path="/" element={<LandingPage />} />
          
          <Route path="/login" element={<LogIn />} />
          <Route path="/signup" element={<SignUp />} />

          {/* Protected home dashboard */}
          <Route path="/home" element={<ProtectedRoute><Home /></ProtectedRoute>} />
          
          {/* Other protected routes */}
          <Route path="/userprofile" element={<ProtectedRoute><UserProfile /></ProtectedRoute>} />
          <Route path="/recommendations" element={<ProtectedRoute><Recommendations /></ProtectedRoute>} />
          <Route path="/settings" element={<ProtectedRoute><UserSettings /></ProtectedRoute>} />
          <Route path="/matches" element={<ProtectedRoute><Matches /></ProtectedRoute>} />
          <Route path="/chat" element={<ProtectedRoute><ChatPage /></ProtectedRoute>} />
          <Route path="/chat/:userId" element={<ProtectedRoute><ChatPage /></ProtectedRoute>} />
          <Route path="/viewprofile/:userId" element={<ProtectedRoute><ViewProfile /></ProtectedRoute>} />
          
          {/* Catch all - redirect to landing */}
          <Route path="*" element={<LandingPage />} />
        </Routes>
    </div>
  )
}