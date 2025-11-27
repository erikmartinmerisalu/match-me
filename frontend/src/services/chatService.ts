export interface ChatMessage {
  id?: number;
  conversationId: number;
  senderId: number;
  receiverId: number;
  content: string;
  timestamp: string;
  isRead: boolean;
}

export interface Conversation {
  id: number;
  otherUserId: number;
  otherUserName: string;
  otherUserAvatar?: string;
  lastMessage: string;
  lastMessageAt: string;
  unreadCount: number;
}

class ChatService {
  private ws: WebSocket | null = null;
  private messageCallback: ((message: any) => void) | null = null;
  private reconnectTimeout: any = null;
  private isManualClose: boolean = false;
  private reconnectAttempts: number = 0;
  private maxReconnectAttempts: number = 50;

  connect(onMessageReceived: (message: any) => void) {
    if (this.ws) {
      if (this.ws.readyState === WebSocket.OPEN || this.ws.readyState === WebSocket.CONNECTING) {
        this.messageCallback = onMessageReceived;
        return;
      }
      this.ws.close();
    }

    this.messageCallback = onMessageReceived;
    this.isManualClose = false;
    
    this.ws = new WebSocket('ws://localhost:8080/ws/chat');
    
    this.ws.onopen = () => {
      console.log('✅ WebSocket connected successfully');
      this.reconnectAttempts = 0;
    };
    
    this.ws.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data);
        if (this.messageCallback) {
          this.messageCallback(data);
        }
      } catch (error) {
        console.error('Failed to parse WebSocket message:', error);
      }
    };
    
    this.ws.onerror = (error) => {
      console.error('❌ WebSocket error:', error);
    };
    
    this.ws.onclose = (event) => {
      this.ws = null;
      
      if (!this.isManualClose && this.reconnectAttempts < this.maxReconnectAttempts) {
        this.reconnectAttempts++;
        const delay = Math.min(1000 * this.reconnectAttempts, 5000);
        
        if (this.reconnectTimeout) {
          clearTimeout(this.reconnectTimeout);
        }
        
        console.log(`Reconnecting in ${delay}ms (attempt ${this.reconnectAttempts})...`);
        this.reconnectTimeout = setTimeout(() => {
          if (this.messageCallback && !this.isManualClose) {
            this.connect(this.messageCallback);
          }
        }, delay);
      } else if (this.reconnectAttempts >= this.maxReconnectAttempts) {
        console.error('Max reconnection attempts reached. Please refresh the page.');
      }
    };
  }

  disconnect() {
    this.isManualClose = true;
    this.reconnectAttempts = 0;
    
    if (this.reconnectTimeout) {
      clearTimeout(this.reconnectTimeout);
      this.reconnectTimeout = null;
    }
    
    if (this.ws) {
      this.ws.close();
      this.ws = null;
    }
  }

  async sendMessage(receiverId: number, content: string): Promise<boolean> {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      try {
        const messagePayload = JSON.stringify({
          type: 'message',
          receiverId,
          content
        });
        this.ws.send(messagePayload);
        return true;
      } catch (error) {
        throw new Error('Failed to send message via WebSocket');
      }
    } else {
      throw new Error('WebSocket is not connected');
    }
  }

  sendTypingIndicator(receiverId: number, isTyping: boolean) {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      this.ws.send(JSON.stringify({
        type: 'typing',
        recipientId: receiverId,
        isTyping
      }));
    }
  }

  markAsRead(conversationId: number) {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      this.ws.send(JSON.stringify({
        type: 'markAsRead',
        conversationId
      }));
    }
  }

  async getConversations(): Promise<Conversation[]> {
    try {
      const response = await fetch('http://localhost:8080/api/chat/conversations', {
        credentials: 'include'
      });
      
      if (!response.ok) {
        console.error(`Failed to fetch conversations: ${response.status}`);
        if (response.status === 403) {
          throw new Error('403: Access denied - not connected with users');
        }
        return [];
      }
      
      const data = await response.json();
      return Array.isArray(data) ? data : [];
    } catch (error) {
      console.error('Error fetching conversations:', error);
      throw error;
    }
  }

  async getMessages(conversationId: number, page: number = 0): Promise<ChatMessage[]> {
    try {
      const response = await fetch(
        `http://localhost:8080/api/chat/conversations/${conversationId}/messages?page=${page}&size=50`,
        {
          credentials: 'include'
        }
      );
      
      if (!response.ok) {
        console.error(`Failed to fetch messages: ${response.status}`);
        if (response.status === 403) {
          const errorText = await response.text();
          throw new Error(`403: ${errorText}`);
        }
        return [];
      }
      
      const data = await response.json();
      return Array.isArray(data) ? data : [];
    } catch (error) {
      console.error('Error fetching messages:', error);
      throw error;
    }
  }

  async getOrCreateConversation(otherUserId: number): Promise<Conversation | null> {
    try {
      const response = await fetch(
        `http://localhost:8080/api/chat/conversations/with/${otherUserId}`,
        {
          method: 'POST',
          credentials: 'include'
        }
      );
      
      if (!response.ok) {
        console.error(`Failed to get/create conversation: ${response.status}`);
        if (response.status === 403) {
          const errorText = await response.text();
          throw new Error(`403: ${errorText}`);
        }
        return null;
      }
      
      return response.json();
    } catch (error) {
      console.error('Error getting/creating conversation:', error);
      throw error;
    }
  }

  async getUnreadCount(): Promise<number> {
    try {
      const response = await fetch('http://localhost:8080/api/chat/unread-count', {
        credentials: 'include'
      });
      
      if (!response.ok) {
        return 0;
      }
      
      return response.json();
    } catch (error) {
      return 0;
    }
  }
}

export default new ChatService();