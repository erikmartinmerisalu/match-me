import { useEffect, useState } from 'react';
import { useToast } from '../../context/ToastContext';; // Adjust the import path
import './Recommendations.css';

interface Recommendation {
  userId: number;
  displayName: string;
  aboutMe: string;
  profilePic: string;
  compatibleGames: string[];
}

const Recommendations = () => {
  const [recommendations, setRecommendations] = useState<Recommendation[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const { success, error: toastError } = useToast(); // Add this line

  useEffect(() => {
    fetchRecommendations();
  }, []);

  const fetchRecommendations = async () => {
    try {
      setLoading(true);
      
      // Step 1: Get recommendation IDs
      const idsResponse = await fetch('http://localhost:8080/api/recommendations', {
        method: 'GET',
        credentials: 'include',
      });
      
      if (!idsResponse.ok) {
        throw new Error('Failed to fetch recommendations');
      }
      
      const ids: number[] = await idsResponse.json();
      
      if (ids.length === 0) {
        setRecommendations([]);
        setLoading(false);
        return;
      }
      
      // Step 2 & 3: Fetch user info and bio for each ID in parallel
      const userDataPromises = ids.map(async (id) => {
        try {
          // Fetch basic user info
          const userResponse = await fetch(`http://localhost:8080/api/users/${id}`, {
            credentials: 'include',
          });
          const userData = await userResponse.json();
          
          // Fetch bio
          const bioResponse = await fetch(`http://localhost:8080/api/users/${id}/bio`, {
            credentials: 'include',
          });
          const bioData = await bioResponse.json();
          
          // Step 4: Combine the data
          return {
            userId: id,
            displayName: userData.displayName || 'Unknown',
            aboutMe: bioData.aboutMe || '',
            profilePic: userData.profilePic || '',
            // Extract compatible games (games object keys)
            compatibleGames: bioData.games ? Object.keys(bioData.games) : []
          };
        } catch (err) {
          console.error(`Failed to fetch data for user ${id}:`, err);
          return null;
        }
      });
      
      const usersData = await Promise.all(userDataPromises);
      const validUsers = usersData.filter(user => user !== null) as Recommendation[];
      
      setRecommendations(validUsers);
      setLoading(false);
    } catch (err) {
      setError('Failed to load recommendations. Please try again.');
      setLoading(false);
      console.error(err);
    }
  };

  const handleMatchClick = async (userId: number, displayName: string) => {
    try {
      const response = await fetch(`http://localhost:8080/api/connections/${userId}`, {
        method: 'POST',
        credentials: 'include',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (response.ok) {
        setRecommendations(prev => prev.filter(rec => rec.userId !== userId));
        success(`Match request sent to ${displayName}!`); // Changed from alert
      } else {
        const errorData = await response.json();
        toastError(errorData.error || 'Failed to send match request'); // Changed from alert
      }
    } catch (err) {
      console.error(err);
      toastError('Error sending match request'); // Changed from alert
    }
  };

  const handleDismissClick = async (userId: number) => {
    try {
      const response = await fetch(`http://localhost:8080/api/connections/${userId}/dismiss`, {
        method: 'POST',
        credentials: 'include',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (response.ok) {
        setRecommendations(prev => prev.filter(rec => rec.userId !== userId));
      } else {
        const errorData = await response.json();
        toastError(errorData.error || 'Failed to dismiss user'); // Changed from alert
      }
    } catch (err) {
      console.error(err);
      toastError('Error dismissing user'); // Changed from alert
    }
  };

  if (loading) {
    return (
      <div className="recommendations-container">
        <div className="loading">Loading recommendations...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="recommendations-container">
        <div className="error">{error}</div>
      </div>
    );
  }

  return (
    <div className="recommendations-container">
      <h1 className="recommendations-title">🎮 Recommended Players</h1>
      
      {recommendations.length === 0 ? (
        <div className="no-recommendations">
          <p>No recommendations found at the moment.</p>
          <p>Try adjusting your profile preferences or check back later!</p>
        </div>
      ) : (
        <div className="recommendations-grid">
          {recommendations.map((rec) => (
            <div key={rec.userId} className="recommendation-card">
              <div className="card-header">
                <div className="profile-avatar">
                  {rec.profilePic ? (
                    <img src={rec.profilePic} alt={rec.displayName} />
                  ) : (
                    rec.displayName.charAt(0).toUpperCase()
                  )}
                </div>
                <h3 className="player-name">{rec.displayName}</h3>
              </div>

              {rec.aboutMe && (
                <div className="about-section">
                  <p className="about-text">{rec.aboutMe}</p>
                </div>
              )}

              <div className="compatible-games">
                <p className="games-label">Compatible for:</p>
                <div className="games-list">
                  {rec.compatibleGames.map((game, index) => (
                    <span key={index} className="game-badge">
                      {game}
                    </span>
                  ))}
                </div>
              </div>

              <div className="card-actions">
                <button 
                  className="dismiss-btn"
                  onClick={() => handleDismissClick(rec.userId)}
                >
                  Dismiss
                </button>
                <button 
                  className="match-btn"
                  onClick={() => handleMatchClick(rec.userId, rec.displayName)}
                >
                  Match
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Recommendations;