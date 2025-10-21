import  { useContext, useState } from "react";
import { useNavigate } from "react-router-dom";
import "./auth.css";
import { useAuth } from "../../context/AuthContext";

function LogIn() {
  const [email, setEMail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();
  const {logIn} = useAuth();

  async function loginUser() {
    setError("");

    if (!email || !password) {
      setError("Please fill all the fields!");
      return;
    }


    try {
      const response = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, password }),
        credentials: "include"
      });

      if (!response.ok) {
        const errorData = await response.text();
        throw new Error(errorData || "Login failed");
      }

      logIn();
      
      navigate("/userprofile");
    } catch (err: any) {
      setError(err.message || "Something went wrong!");
    }
  }

  return (
    <div className="login-container">
      <h2>Log In</h2>

      <label>E-mail</label>
      <input type="text" value={email} onChange={(e) => setEMail(e.target.value)} className="login-input" />

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
