import { useEffect, useState } from 'react';
import './Matches.css';

interface Match {
  id: number;
  userId: number;
  displayName: string;
  profilePic: string;
  status: string;
}

const Matches = () => {
  const [acceptedMatches, setAcceptedMatches] = useState<Match[]>([]);
  const [sentRequests, setSentRequests] = useState<Match[]>([]);
  const [receivedRequests, setReceivedRequests] = useState<Match[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<'accepted' | 'sent' | 'received'>('accepted');

  useEffect(() => {
    fetchMatches();
  }, []);

  const fetchMatches = async () => {
    try {
      setLoading(true);

      // Get current user ID
      const currentUserRes = await fetch('http://localhost:8080/api/users/me', {
        credentials: 'include',
      });
      const currentUser = await currentUserRes.json();
      const currentUserId = currentUser.id;

      // Fetch accepted matches
      const acceptedRes = await fetch('http://localhost:8080/api/connections', {
        credentials: 'include',
      });
      const acceptedData = await acceptedRes.json();

      // Fetch sent requests
      const sentRes = await fetch('http://localhost:8080/api/connections/pending/sent', {
        credentials: 'include',
      });
      const sentData = await sentRes.json();

      // Fetch received requests
      const receivedRes = await fetch('http://localhost:8080/api/connections/pending/received', {
        credentials: 'include',
      });
      const receivedData = await receivedRes.json();

      console.log('Accepted:', acceptedData);
      console.log('Sent:', sentData);
      console.log('Received:', receivedData);

      // Process accepted matches
      const acceptedMatches = await Promise.all(
        acceptedData.map(async (conn: any) => {
          const otherUserId = conn.fromUserId === currentUserId 
            ? conn.toUserId 
            : conn.fromUserId;
          
          if (!otherUserId) {
            console.error('No user ID found in connection:', conn);
            return null;
          }

          const userInfo = await fetchUserInfo(otherUserId);
          return {
            id: conn.id,
            userId: otherUserId,
            ...userInfo,
            status: 'accepted'
          };
        })
      );

      // Process sent requests
      const sentMatches = await Promise.all(
        sentData.map(async (conn: any) => {
          const userId = conn.toUserId;
          
          if (!userId) {
            console.error('No toUserId in sent connection:', conn);
            return null;
          }

          const userInfo = await fetchUserInfo(userId);
          return {
            id: conn.id,
            userId: userId,
            ...userInfo,
            status: 'pending'
          };
        })
      );

      // Process received requests
      const receivedMatches = await Promise.all(
        receivedData.map(async (conn: any) => {
          const userId = conn.fromUserId;
          
          if (!userId) {
            console.error('No fromUserId in received connection:', conn);
            return null;
          }

          const userInfo = await fetchUserInfo(userId);
          return {
            id: conn.id,
            userId: userId,
            ...userInfo,
            status: 'pending'
          };
        })
      );

      // Filter out null values
      setAcceptedMatches(acceptedMatches.filter(m => m !== null) as Match[]);
      setSentRequests(sentMatches.filter(m => m !== null) as Match[]);
      setReceivedRequests(receivedMatches.filter(m => m !== null) as Match[]);
      setLoading(false);
    } catch (err) {
      console.error('Failed to fetch matches:', err);
      setLoading(false);
    }
  };

  const fetchUserInfo = async (userId: number) => {
    try {
      const response = await fetch(`http://localhost:8080/api/users/${userId}`, {
        credentials: 'include',
      });
      const data = await response.json();
      return {
        displayName: data.displayName || 'Unknown',
        profilePic: data.profilePic || ''
      };
    } catch (err) {
      return { displayName: 'Unknown', profilePic: '' };
    }
  };

  const handleAccept = async (connectionId: number) => {
    try {
      const response = await fetch(`http://localhost:8080/api/connections/${connectionId}/accept`, {
        method: 'POST',
        credentials: 'include',
      });

      if (response.ok) {
        fetchMatches();
      }
    } catch (err) {
      console.error('Failed to accept request:', err);
    }
  };

  const handleReject = async (connectionId: number) => {
    try {
      const response = await fetch(`http://localhost:8080/api/connections/${connectionId}/reject`, {
        method: 'POST',
        credentials: 'include',
      });

      if (response.ok) {
        fetchMatches();
      }
    } catch (err) {
      console.error('Failed to reject request:', err);
    }
  };

  const handleViewProfile = (userId: number) => {
    window.location.href = `/viewprofile/${userId}`;
  };

  const handleChat = (userId: number) => {
    window.location.href = `/chat/${userId}`;
  };

  if (loading) {
    return (
      <div className="matches-container">
        <div className="loading">Loading matches...</div>
      </div>
    );
  }

  return (
    <div className="matches-container">
      <h1 className="matches-title">My Matches</h1>

      <div className="tabs">
        <button 
          className={`tab ${activeTab === 'accepted' ? 'active' : ''}`}
          onClick={() => setActiveTab('accepted')}
        >
          Matches ({acceptedMatches.length})
        </button>
        <button 
          className={`tab ${activeTab === 'received' ? 'active' : ''}`}
          onClick={() => setActiveTab('received')}
        >
          Requests ({receivedRequests.length})
        </button>
        <button 
          className={`tab ${activeTab === 'sent' ? 'active' : ''}`}
          onClick={() => setActiveTab('sent')}
        >
          Sent ({sentRequests.length})
        </button>
      </div>

      {activeTab === 'accepted' && (
        <div className="matches-grid">
          {acceptedMatches.length === 0 ? (
            <p className="empty-message">No matches yet. Keep discovering!</p>
          ) : (
            acceptedMatches.map((match) => (
              <div key={match.id} className="match-card">
                <div className="match-avatar">
                  {match.profilePic ? (
                    <img src={match.profilePic} alt={match.displayName} />
                  ) : (
                    match.displayName.charAt(0).toUpperCase()
                  )}
                </div>
                <h3>{match.displayName}</h3>
                <div className="match-actions">
                  <button onClick={() => handleViewProfile(match.userId)}>
                    View Profile
                  </button>
                  <button className="chat-btn" onClick={() => handleChat(match.userId)}>
                    Chat
                  </button>
                </div>
              </div>
            ))
          )}
        </div>
      )}

      {activeTab === 'received' && (
        <div className="matches-grid">
          {receivedRequests.length === 0 ? (
            <p className="empty-message">No pending requests</p>
          ) : (
            receivedRequests.map((match) => (
              <div key={match.id} className="match-card">
                <div className="match-avatar">
                  {match.profilePic ? (
                    <img src={match.profilePic} alt={match.displayName} />
                  ) : (
                    match.displayName.charAt(0).toUpperCase()
                  )}
                </div>
                <h3>{match.displayName}</h3>
                <p className="request-label">wants to connect</p>
                <div className="match-actions">
                  <button className="reject-btn" onClick={() => handleReject(match.id)}>
                    Reject
                  </button>
                  <button className="accept-btn" onClick={() => handleAccept(match.id)}>
                    Accept
                  </button>
                </div>
              </div>
            ))
          )}
        </div>
      )}

      {activeTab === 'sent' && (
        <div className="matches-grid">
          {sentRequests.length === 0 ? (
            <p className="empty-message">No pending sent requests</p>
          ) : (
            sentRequests.map((match) => (
              <div key={match.id} className="match-card">
                <div className="match-avatar">
                  {match.profilePic ? (
                    <img src={match.profilePic} alt={match.displayName} />
                  ) : (
                    match.displayName.charAt(0).toUpperCase()
                  )}
                </div>
                <h3>{match.displayName}</h3>
                <p className="pending-label">Request pending...</p>
              </div>
            ))
          )}
        </div>
      )}
    </div>
  );
};

export default Matches;