import React from 'react';
import './navbar.css';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

function Navbar() {
    const navigate = useNavigate();
    const {loggedIn} = useAuth();
    
  return (
    <div className='navbar'>
      <div className='navbar-logo'>Gamely</div>
      
      {loggedIn === true ?
      <div className='navbar-links'>
        <div>Matches</div>
        <div>Chat</div>
        <div>Something</div>
        <div>Idk</div>
      </div>
        : ""}
      <div className='navbar-buttons'>
        <button onClick={() => navigate("/signup")}>Signup</button>
        <button onClick={() => navigate("/login")}>Log In</button>
      </div>
    </div>
  );
}

export default Navbar;
