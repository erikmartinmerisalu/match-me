import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import "./viewprofile.css";

interface GameProfile {
  expLvl?: string;
  gamingHours?: string;
  preferredServers?: string[];
}

interface UserProfile {
  id: number;
  displayName: string;
  aboutMe?: string;
  games?: {
    [gameName: string]: GameProfile;
  };
  birthDate?: string;
  timezone?: string;
  lookingFor?: string;
  preferredAgeMin?: number;
  preferredAgeMax?: number;
  maxPreferredDistance?: number;
  profilePic?: string;
  latitude?: number;
  longitude?: number;
  location?: string;
  profileCompleted?: boolean;
}

const ViewProfile: React.FC = () => {
  const { userId } = useParams();
  const navigate = useNavigate();
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [isConnected, setIsConnected] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadProfile();
    checkConnection();
  }, [userId]);

  const loadProfile = async () => {
    try {
      const res = await fetch(`http://localhost:8080/api/users/${userId}/profile`, {
        credentials: "include",
      });
      if (!res.ok) {
        throw new Error(`Failed to load profile: ${res.status}`);
      }
      const data = await res.json();
      setProfile(data);
    } catch (err) {
      console.error("Failed to load profile", err);
    } finally {
      setLoading(false);
    }
  };

  const checkConnection = async () => {
    try {
      setIsConnected(true);
    } catch (err) {
      console.error("Failed to check connection", err);
    }
  };

  const handleMessage = async () => {
    if (!isConnected) {
      alert("You must be connected with this user to message them");
      return;
    }
    navigate(`/chat/${userId}`);
  };

  if (loading) return <div className="view-profile-loading">Loading...</div>;
  if (!profile) return <div className="view-profile-error">User not found</div>;

  return (
    <div className="view-profile-card">
      <div className="profile-header">
        <div className="profile-pic-view">
          {profile.profilePic ? (
            <img src={profile.profilePic}  />
          ) : (
            <span className="placeholder">👤</span>
          )}
        </div>
        <h2>{profile.displayName}</h2>
        {profile.location && <p className="location">📍 {profile.location}</p>}
        
        {isConnected && (
          <button className="message-btn" onClick={handleMessage}>
            💬 Message
          </button>
        )}
      </div>

      <div className="profile-details">
        {profile.aboutMe && (
          <section>
            <h3>About</h3>
            <p>{profile.aboutMe}</p>
          </section>
        )}

        {profile.lookingFor && (
          <section>
            <h3>Looking For</h3>
            <p>{profile.lookingFor}</p>
          </section>
        )}

        {profile.games && Object.keys(profile.games).length > 0 && (
          <section>
            <h3>Games</h3>
            {Object.entries(profile.games).map(([gameName, gameData]) => (
              <div key={gameName} className="game-entry">
                <h4>{gameName}</h4>
                <div className="game-details">
                  {gameData.expLvl && (
                    <span className="tag">Exp: {gameData.expLvl}</span>
                  )}
                  {gameData.gamingHours && (
                    <span className="tag">Hours: {gameData.gamingHours}</span>
                  )}
                  {gameData.preferredServers && gameData.preferredServers.length > 0 && (
                    <div className="servers">
                      <strong>Servers:</strong>{' '}
                      {gameData.preferredServers.map(server => (
                        <span key={server} className="tag">{server}</span>
                      ))}
                    </div>
                  )}
                </div>
              </div>
            ))}
          </section>
        )}

        {profile.timezone && (
          <section>
            <h3>Timezone</h3>
            <p>{profile.timezone}</p>
          </section>
        )}

        {(profile.preferredAgeMin || profile.preferredAgeMax) && (
          <section>
            <h3>Preferred Age Range</h3>
            <p>
              {profile.preferredAgeMin && profile.preferredAgeMax 
                ? `${profile.preferredAgeMin} - ${profile.preferredAgeMax} years`
                : profile.preferredAgeMin 
                  ? `${profile.preferredAgeMin}+ years`
                  : `Up to ${profile.preferredAgeMax} years`
              }
            </p>
          </section>
        )}

        {profile.maxPreferredDistance && (
          <section>
            <h3>Max Distance</h3>
            <p>{profile.maxPreferredDistance} km</p>
          </section>
        )}
      </div>
    </div>
  );
};

export default ViewProfile;