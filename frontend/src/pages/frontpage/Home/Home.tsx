import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../../context/AuthContext';
import './home.css';

const Home: React.FC = () => {
  const navigate = useNavigate();
  const { loggedIn } = useAuth();

  // Redirect to landing if not logged in
  React.useEffect(() => {
    if (!loggedIn) {
      navigate('/');
    }
  }, [loggedIn, navigate]);

  return (
    <div className="home-dashboard">
      <div className="dashboard-container">
        <h1 className="dashboard-title">Welcome to Gamely!</h1>
        
        <div className="quick-actions">
          <div className="action-card" onClick={() => navigate('/matches')}>
            <div className="action-icon">🎯</div>
            <h3>Find Matches</h3>
            <p>Discover gamers near you</p>
          </div>

          <div className="action-card" onClick={() => navigate('/recommendations')}>
            <div className="action-icon">✨</div>
            <h3>Recommendations</h3>
            <p>See who we think you'll vibe with</p>
          </div>

          <div className="action-card" onClick={() => navigate('/chat')}>
            <div className="action-icon">💬</div>
            <h3>Messages</h3>
            <p>Chat with your connections</p>
          </div>

          <div className="action-card" onClick={() => navigate('/userprofile')}>
            <div className="action-icon">👤</div>
            <h3>Your Profile</h3>
            <p>View and edit your profile</p>
          </div>
        </div>

        <div className="dashboard-info">
          <h2>What would you like to do today?</h2>
          <p>Start by finding new gaming friends or check your messages!</p>
        </div>
      </div>
    </div>
  );
};

export default Home;