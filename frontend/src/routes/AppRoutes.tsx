import { Route, Routes} from 'react-router-dom'
import LogIn from '../auth/LogIn'
import SignUp from '../auth/SignUp'
import {  useAuth } from '../context/AuthContext'
import UserProfile from '../pages/userprofile/UserProfile';

export default function AppRoutes() {
  const {loggedIn} = useAuth();

  return (
    <div>
        <Routes>
          {/* <Route path="/" element={< />} /> */}
          {/* <Route path="/about" element={<About />} /> */}
          <Route path="/login" element={<LogIn />} />
          <Route path="/signup" element={<SignUp />} />

          {loggedIn === true ? <>
            <Route path="/userprofile" element={<UserProfile />} />
          </> :
          <Route path="/login" element={<LogIn />} />}
        </Routes>
    </div>
  )
}
