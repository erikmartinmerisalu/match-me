import React from 'react';
import './navbar.css';
import { useNavigate } from 'react-router-dom';

function Navbar() {
    const navigate = useNavigate();
  return (
    <div className='navbar'>
      <div className='navbar-logo'>Gamely</div>
      <div className='navbar-links'>
        <div>Home</div>
        <div>Idk</div>
        <div>Something</div>
        <div>Idk</div>
      </div>
      <div className='navbar-buttons'>
        <button onClick={() => navigate("/signup")}>Signup</button>
        <button onClick={() => navigate("/login")}>Log In</button>
      </div>
    </div>
  );
}

export default Navbar;
