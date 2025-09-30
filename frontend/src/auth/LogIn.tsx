import  { useContext, useState } from "react";
import { useNavigate } from "react-router-dom";
import "./auth.css"; // ← impordi CSS-fail
import { useAuth } from "../context/AuthContext";

function LogIn() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();
  const {setLoggedIn} = useAuth();

  async function loginUser() {
    setError("");

    if (!username || !password) {
      setError("Please fill all the fields!");
      return;
    }

    try {
      // const response = await fetch("http://localhost:3000/api/login", {
      //   method: "POST",
      //   headers: {
      //     "Content-Type": "application/json",
      //   },
      //   body: JSON.stringify({ username, password }),
      // });

      // if (!response.ok) {
      //   const errorData = await response.json();
      //   throw new Error(errorData.message || "Login failed");
      // }

      // const data = await response.json();
      // localStorage.setItem("token", data.token);

      
      navigate("/userprofile");
      setLoggedIn(true);
    } catch (err: any) {
      setError(err.message || "Something went wrong!");
    }
  }

  return (
    <div className="login-container">
      <h2>Log In</h2>

      <label>Username</label>
      <input type="text" value={username} onChange={(e) => setUsername(e.target.value)} className="login-input" />

      <label>Password</label>
      <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} className="login-input" />

      <button onClick={loginUser} className="login-button">
        Log In
      </button>

      {error && <div className="login-error">{error}</div>}
    </div>
  );
}

export default LogIn;
