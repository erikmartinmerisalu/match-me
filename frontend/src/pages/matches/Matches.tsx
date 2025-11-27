import { useEffect, useState } from 'react';
import './Matches.css';
import { matchWebSocketService } from '../../services/matchWebSocketService';

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
  const [blockedUsers, setBlockedUsers] = useState<Match[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<'accepted' | 'sent' | 'received' | 'blocked'>('accepted');

  useEffect(() => {
    fetchMatches();

    // Set up WebSocket for real-time updates
    matchWebSocketService.connect();
    
    const unsubscribe = matchWebSocketService.onUpdate(() => {
      fetchMatches();
    });

    // Cleanup on unmount
    return () => {
      unsubscribe();
    };
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

      // Fetch blocked users - users that CURRENT USER blocked
      const blockedRes = await fetch('http://localhost:8080/api/connections/blocked', {
        credentials: 'include',
      });
      const blockedData = await blockedRes.json();


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

      // Process blocked users - ONLY users that CURRENT USER blocked
      const blockedMatches = blockedData.map((userInfo: any) => {
        return {
          id: userInfo.id, // Connection ID for unblocking
          userId: userInfo.userId, // User ID of the blocked user
          displayName: userInfo.displayName,
          profilePic: userInfo.profilePic,
          status: 'blocked'
        };
      });


      // Filter out null values
      setAcceptedMatches(acceptedMatches.filter(m => m !== null) as Match[]);
      setSentRequests(sentMatches.filter(m => m !== null) as Match[]);
      setReceivedRequests(receivedMatches.filter(m => m !== null) as Match[]);
      setBlockedUsers(blockedMatches.filter((m: any) => m !== null) as Match[]);
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
      console.error('Failed to fetch user info for ID:', userId, err);
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
      } else {
        const errorData = await response.json();
        console.error('Failed to accept request:', errorData);
        alert(`Failed to accept request: ${errorData.error}`);
      }
    } catch (err) {
      console.error('Failed to accept request:', err);
      alert('Failed to accept request. Please try again.');
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
      } else {
        const errorData = await response.json();
        console.error('Failed to reject request:', errorData);
        alert(`Failed to reject request: ${errorData.error}`);
      }
    } catch (err) {
      console.error('Failed to reject request:', err);
      alert('Failed to reject request. Please try again.');
    }
  };

  const handleBlock = async (userId: number) => {
    try {
      const response = await fetch(`http://localhost:8080/api/connections/${userId}/block`, {
        method: 'POST',
        credentials: 'include',
      });

      if (response.ok) {
        fetchMatches();
      } else {
        const errorData = await response.json();
        console.error('Failed to block user:', errorData);
        alert(`Failed to block user: ${errorData.error}`);
      }
    } catch (err) {
      console.error('Failed to block user:', err);
      alert('Failed to block user. Please try again.');
    }
  };

  const handleUnblock = async (userId: number) => {
    try {
      const response = await fetch(`http://localhost:8080/api/connections/${userId}/unblock`, {
        method: 'POST',
        credentials: 'include',
      });

      if (response.ok) {
        fetchMatches();
      } else {
        const errorData = await response.json();
        console.error('Failed to unblock user:', errorData);
        alert(`Failed to unblock user: ${errorData.error}`);
      }
    } catch (err) {
      console.error('Failed to unblock user:', err);
      alert('Failed to unblock user. Please try again.');
    }
  };

  const handleViewProfile = (userId: number) => {
    window.location.href = `/viewprofile/${userId}`;
  };

  const handleChat = (userId: number) => {
    window.location.href = `/chat/${userId}`;
  };

  // Safe avatar rendering function
  const renderAvatar = (user: Match) => {
    if (user.profilePic) {
      return <img src={user.profilePic} alt={user.displayName} className="avatar-image" />;
    } else {
      const initial = user.displayName ? user.displayName.charAt(0).toUpperCase() : '?';
      return <div className="avatar-initial">{initial}</div>;
    }
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
        <button 
          className={`tab ${activeTab === 'blocked' ? 'active' : ''}`}
          onClick={() => setActiveTab('blocked')}
        >
          Blocked ({blockedUsers.length})
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
                  {renderAvatar(match)}
                </div>
                <h3>{match.displayName}</h3>
                <div className="match-actions">
                  <button 
                    className="view-profile-btn"
                    onClick={() => handleViewProfile(match.userId)}
                  >
                    View Profile
                  </button>
                  <button 
                    className="chat-btn" 
                    onClick={() => handleChat(match.userId)}
                  >
                    Chat
                  </button>
                  <button 
                    className="block-btn" 
                    onClick={() => handleBlock(match.userId)}
                  >
                    Block
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
                  {renderAvatar(match)}
                </div>
                <h3>{match.displayName}</h3>
                <p className="request-label">wants to connect</p>
                <div className="match-actions">
                  <button 
                    className="reject-btn" 
                    onClick={() => handleReject(match.id)}
                  >
                    Reject
                  </button>
                  <button 
                    className="accept-btn" 
                    onClick={() => handleAccept(match.id)}
                  >
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
                  {renderAvatar(match)}
                </div>
                <h3>{match.displayName}</h3>
                <p className="pending-label">Request pending...</p>
              </div>
            ))
          )}
        </div>
      )}

      {activeTab === 'blocked' && (
        <div className="matches-grid">
          {blockedUsers.length === 0 ? (
            <p className="empty-message">No blocked users</p>
          ) : (
            blockedUsers.map((user) => (
              <div key={user.id} className="match-card">
                <div className="match-avatar">
                  {renderAvatar(user)}
                </div>
                <h3>{user.displayName}</h3>
                <p className="blocked-label">You blocked this user</p>
                <div className="match-actions">
                  <button 
                    className="unblock-btn" 
                    onClick={() => handleUnblock(user.userId)}
                  >
                    Unblock
                  </button>
                </div>
              </div>
            ))
          )}
        </div>
      )}
    </div>
  );
};

export default Matches;