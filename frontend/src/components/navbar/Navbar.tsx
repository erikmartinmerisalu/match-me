import React, { useState, useRef, useEffect } from 'react';
import './navbar.css';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import ProfilePicShow from '../profilepic/ProfilePicShow';

function Navbar() {
  const navigate = useNavigate();
  const { loggedIn, signOut, userName, profilePictureBase64 } = useAuth();
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  const toggleDropdown = () => setDropdownOpen((prev) => !prev);

  const handleLogOut = () => {
    signOut();
    navigate("/home");
  };

  // Sulgeb dropdowni, kui klikid väljaspool
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setDropdownOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  return (
    <div className='navbar'>
      <div className='navbar-logo' onClick={() => navigate("/home")}>Gamely</div>

      {loggedIn && (
        <div className='navbar-links'>
          
          <div onClick={() => navigate("/userprofile")}>Profile</div>
          <div onClick={() => navigate("/match")}>Match</div>
          <div onClick={() => navigate("/chat")}>Chat</div>
          <div>Idk</div>
        </div>
      )}

      <div className='navbar-buttons'>
        {!loggedIn ? (
          <>
            <button onClick={() => navigate("/signup")}>Signup</button>
            <button onClick={() => navigate("/login")}>Log In</button>
          </>
        ) : (
          <div className="user-dropdown" ref={dropdownRef}>
            <div className="user-info" >
              <ProfilePicShow src={profilePictureBase64} width={30} height={30} />
              <span style={{ marginLeft: '8px' }}>{userName}</span>
            </div>
            
            <img src="/down-arrow.png" className="icon" onClick={toggleDropdown} />
            {dropdownOpen && (
              
              <div className="dropdown-menu"> 
                <div className="dropdown-item" onClick={() => navigate("/settings")}>Settings</div>
                <div className="dropdown-item" onClick={handleLogOut}>Log Out</div>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}

export default Navbar;
