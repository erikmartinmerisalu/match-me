import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import "./viewprofile.css";

interface UserProfile {
  id: number;
  displayName: string;
  aboutMe?: string;
  expLvl?: string;
  gamingHours?: string;
  preferredServers?: string[];
  games?: string[];
  rank?: string;
  birthDate?: string;
  age?: number;
  timezone?: string;
  region?: string;
  lookingFor?: string;
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
      // TODO: Check if users are connected
      // const res = await fetch(`http://localhost:8080/api/connections/check/${userId}`);
      // const data = await res.json();
      // setIsConnected(data.connected);
      
      // Temporary - assume connected for testing
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

  if (loading) return <div>Loading...</div>;
  if (!profile) return <div>User not found</div>;

  return (
    <div className="view-profile-card">
      <div className="profile-header">
        <div className="profile-pic-view">
          <span className="placeholder">👤</span>
        </div>
        <h2>{profile.displayName}</h2>
        
        {isConnected && (
          <button className="message-btn" onClick={handleMessage}>
            💬 Message
          </button>
        )}
      </div>

      <div className="profile-details">
        {profile.preferredServers && profile.preferredServers.length > 0 && (
          <section>
            <h3>Preferred Servers</h3>
            <div className="tags">
              {profile.preferredServers.map(s => <span key={s} className="tag">{s}</span>)}
            </div>
          </section>
        )}

        {profile.games && profile.games.length > 0 && (
          <section>
            <h3>Games</h3>
            <div className="tags">
              {profile.games.map(g => <span key={g} className="tag">{g}</span>)}
            </div>
          </section>
        )}

        {profile.expLvl && (
          <section>
            <h3>Experience Level</h3>
            <div className="tags">
              <span className="tag">{profile.expLvl}</span>
            </div>
          </section>
        )}

        {profile.gamingHours && (
          <section>
            <h3>Gaming Hours</h3>
            <div className="tags">
              <span className="tag">{profile.gamingHours}</span>
            </div>
          </section>
        )}

        {profile.aboutMe && (
          <section>
            <h3>About</h3>
            <p>{profile.aboutMe}</p>
          </section>
        )}
      </div>
    </div>
  );
};

export default ViewProfile;