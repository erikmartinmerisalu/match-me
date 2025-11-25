import { useEffect } from 'react';

export const useOnlineStatus = () => {
  useEffect(() => {
    // Send heartbeat every 2 minutes
    const sendHeartbeat = () => {
      fetch('http://localhost:8080/api/users/heartbeat', {
        method: 'POST',
        credentials: 'include',
      }).catch(err => console.error('Heartbeat failed:', err));
    };

    sendHeartbeat();

    const interval = setInterval(sendHeartbeat, 60000); // 1 minutes

    return () => clearInterval(interval);
  }, []);
};