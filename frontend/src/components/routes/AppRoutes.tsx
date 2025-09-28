import { Route, Routes} from 'react-router-dom'
import LogIn from '../../auth/LogIn'
import SignUp from '../../auth/SignUp'
import {  useAuth } from '../../context/AuthContext'

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
            <Route path="/dashboard" element={<SignUp />} />
          </> :
          <Route path="/login" element={<LogIn />} />}
        </Routes>
    </div>
  )
}
