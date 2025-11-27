// src/services/matchWebSocketService.ts

class MatchWebSocketService {
  private socket: WebSocket | null = null;
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;
  private onUpdateCallbacks: (() => void)[] = [];

  connect() {
    if (this.socket && this.socket.readyState === WebSocket.OPEN) {
      return;
    }

    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const wsUrl = `ws://localhost:8080/ws/matches`;
    
    this.socket = new WebSocket(wsUrl);

    this.socket.onopen = () => {
      console.log('Match WebSocket connected');
      this.reconnectAttempts = 0;
    };

    this.socket.onmessage = (event) => {
      try {
        const message = JSON.parse(event.data);
        
        if (message.type === 'MATCH_UPDATE') {
          this.notifyUpdate();
        }
      } catch (error) {
        console.error('Error parsing WebSocket message:', error);
      }
    };

    this.socket.onclose = (event) => {
      this.attemptReconnect();
    };

    this.socket.onerror = (error) => {
      console.error('Match WebSocket error:', error);
    };
  }

  private attemptReconnect() {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++;
      const delay = Math.min(1000 * this.reconnectAttempts, 10000);
      
      console.log(`Attempting to reconnect in ${delay}ms (attempt ${this.reconnectAttempts})`);
      
      setTimeout(() => {
        this.connect();
      }, delay);
    } else {
      console.error('Max reconnection attempts reached');
    }
  }

  onUpdate(callback: () => void) {
    this.onUpdateCallbacks.push(callback);
    return () => {
      this.onUpdateCallbacks = this.onUpdateCallbacks.filter(cb => cb !== callback);
    };
  }

  private notifyUpdate() {
    this.onUpdateCallbacks.forEach(callback => {
      try {
        callback();
      } catch (error) {
        console.error('Error in update callback:', error);
      }
    });
  }

  disconnect() {
    if (this.socket) {
      this.socket.close();
      this.socket = null;
    }
    this.onUpdateCallbacks = [];
  }
}

export const matchWebSocketService = new MatchWebSocketService();