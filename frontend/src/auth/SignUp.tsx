import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "./auth.css"; // ← kasutame sama CSS-i!

function SignUp() {
  const [email, setEMail] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [repeatPassword, setRepeatPassword] = useState("");
  const [passwordStrength, setPasswordStrength] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    evaluatePassword(password);
  }, [password]);

  function evaluatePassword(pw: string) {
    if (!pw) {
      setPasswordStrength("");
      return;
    }

    const hasUpper = /[A-Z]/.test(pw);
    const hasLower = /[a-z]/.test(pw);
    const hasNumber = /[0-9]/.test(pw);
    const hasSpecial = /[!@#$%^&*(),.?":{}|<>]/.test(pw);

    if (pw.length < 8) {
      setPasswordStrength("Weak");
    } else if (hasUpper && hasLower && hasNumber && hasSpecial) {
      setPasswordStrength("Strong");
    } else {
      setPasswordStrength("Medium");
    }
  }

  async function checkInputs() {
    setError("");
    setSuccess("");

    if (!email || !username || !password || !repeatPassword) {
      setError("All fields must be filled!");
      return;
    }

    if (password !== repeatPassword) {
      setError("Passwords doesn't match!");
      return;
    }

    if (password.length < 8) {
      setError("Password must be 8 character long");
      return;
    }

    // Post to backend
    try {
      const response = await fetch("http://localhost:8080/api/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        // Authorization : "Bearer " + sessionStorage.getItem("LokiAuthToken")
        credentials: "include",
        body: JSON.stringify({ email, 
          password,
          displayName: username, 
          birthDate: "2000-01-01"  }),
      });

      if (!response.ok) {
        const errorData = await response.text();
        throw new Error(errorData || "Signup failed");
      }

      setSuccess("Account made!!");
      setTimeout(() => navigate("/login"), 1500);
    } catch (err: any) {
      setError(err.message || "Error :/ ");
    }
  }

  return (
    <div className="login-container"> 
      <h2>Sign Up</h2>

      <label>E-mail</label>
      <input
        type="text"
        value={email}
        onChange={(e) => setEMail(e.target.value)}
        className="login-input"
      />

      <label>Username</label>
      <input
        type="text"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
        className="login-input"
      />

      <label>Password</label>
      <input
        type="password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        className="login-input"
      />

      <br />

      {password && (
        <div
          className="password-strength"
          style={{
            color:
              passwordStrength === "Strong"
                ? "green"
                : passwordStrength === "Medium"
                ? "orange"
                : "red",
            
          }} >
          Strenght: {passwordStrength}
        </div>
      )}
      <br />

      <label>Repeat Password</label>
      <input type="password" value={repeatPassword} 
      onChange={(e) => setRepeatPassword(e.target.value)} className="login-input"
      />

      <button onClick={checkInputs} className="login-button">
        Sign Up
      </button>

      {error && <div className="login-error">{error}</div>}
      {success && <div className="login-success">{success}</div>}
    </div>
  );
}

export default SignUp;
