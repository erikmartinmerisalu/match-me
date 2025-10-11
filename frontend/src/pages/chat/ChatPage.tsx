import { useEffect, useState, useRef, useCallback } from "react";
import { useParams, useNavigate } from "react-router-dom";
import chatService, { type Conversation, type ChatMessage } from "../../services/chatService";
import { useAuth } from "../../context/AuthContext";
import "./chatpage.css";

const ChatPage = () => {
  const { userId } = useParams();
  const navigate = useNavigate();
  const { loggedIn } = useAuth();
  const [conversations, setConversations] = useState<Conversation[]>([]);
  const [selectedConv, setSelectedConv] = useState<Conversation | null>(null);
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [messageInput, setMessageInput] = useState("");
  const [currentUserId, setCurrentUserId] = useState<number | null>(null);
  const [isOtherUserTyping, setIsOtherUserTyping] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const typingTimeoutRef = useRef<any>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const loadConversations = useCallback(async () => {
    try {
      const convos = await chatService.getConversations();
      setConversations(convos);
      
      // If current conversation is no longer in list (disconnected), clear it
      if (selectedConv && !convos.find(c => c.id === selectedConv.id)) {
        console.log('Current conversation no longer available (users disconnected)');
        setSelectedConv(null);
        setMessages([]);
      }
    } catch (err) {
      console.error("Failed to load conversations", err);
    }
  }, [selectedConv]);

  const loadMessages = useCallback(async (conversationId: number) => {
    try {
      const msgs = await chatService.getMessages(conversationId);
      setMessages(msgs);
    } catch (err: any) {
      console.error("Failed to load messages", err);
      // If we get a 403, users are no longer connected
      if (err.message?.includes('403') || err.message?.includes('no longer connected')) {
        alert('You are no longer connected with this user. The conversation will be removed.');
        await loadConversations();
        setSelectedConv(null);
        setMessages([]);
      }
    }
  }, [loadConversations]);

  useEffect(() => {
    if (!loggedIn) return;

    getCurrentUser();
    loadConversations();
    
    // Refresh conversations every 30 seconds to catch connection changes
    const refreshInterval = setInterval(() => {
      loadConversations();
    }, 30000);
    
    const handleWebSocketMessage = (data: any) => {
      console.log('WebSocket notification:', data);
      
      if (data.type === 'newMessage') {
        console.log('New message received from user', data.senderId);
        loadConversations();
        
        setSelectedConv(current => {
          if (current) {
            console.log('Reloading messages for conversation:', current.id);
            loadMessages(current.id);
          }
          return current;
        });
      } else if (data.type === 'messageConfirmed') {
        console.log('Message confirmed by server, messageId:', data.messageId);
        setSelectedConv(current => {
          if (current) {
            loadMessages(current.id);
          }
          return current;
        });
        loadConversations();
      } else if (data.type === 'userTyping') {
        setIsOtherUserTyping(data.isTyping);
        
        if (data.isTyping) {
          if (typingTimeoutRef.current) clearTimeout(typingTimeoutRef.current);
          typingTimeoutRef.current = setTimeout(() => {
            setIsOtherUserTyping(false);
          }, 3000);
        }
      } else if (data.type === 'messagesRead') {
        console.log('Messages marked as read in conversation', data.conversationId);
        
        // Reload messages to show updated read status
        setSelectedConv(current => {
          if (current && current.id === data.conversationId) {
            loadMessages(current.id);
          }
          return current;
        });
        
        loadConversations();
      } else if (data.type === 'error') {
        console.error('WebSocket error:', data.message);
        if (data.message?.includes('not connected')) {
          alert('You are no longer connected with this user.');
          loadConversations();
          setSelectedConv(null);
          setMessages([]);
        }
      }
    };
    
    chatService.connect(handleWebSocketMessage);

    return () => {
      console.log('ChatPage unmounting, disconnecting WebSocket');
      clearInterval(refreshInterval);
      chatService.disconnect();
    };
  }, [loggedIn, loadConversations, loadMessages]);

  useEffect(() => {
    if (userId && conversations.length > 0 && currentUserId) {
      const userIdNum = Number(userId);
      
      if (userIdNum === currentUserId) {
        console.error("Cannot message yourself");
        navigate('/chat', { replace: true });
        return;
      }
      
      openConversationWithUser(userIdNum);
    }
  }, [userId, conversations, currentUserId]);

  const getCurrentUser = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/users/me', {
        credentials: 'include'
      });
      const data = await response.json();
      setCurrentUserId(data.id);
    } catch (err) {
      console.error("Failed to get current user", err);
    }
  };

  const openConversationWithUser = async (otherUserId: number) => {
    const existing = conversations.find(c => c.otherUserId === otherUserId);
    
    if (existing) {
      selectConversation(existing);
      navigate('/chat', { replace: true });
    } else {
      try {
        const newConv = await chatService.getOrCreateConversation(otherUserId);
        if (newConv) {
          await loadConversations();
          const created = conversations.find(c => c.otherUserId === otherUserId);
          if (created) {
            selectConversation(created);
          }
          navigate('/chat', { replace: true });
        }
      } catch (err: any) {
        console.error('Failed to create conversation:', err);
        if (err.message?.includes('403') || err.message?.includes('not connected')) {
          alert('Cannot start conversation. You must be connected with this user first.');
        } else {
          alert('Failed to start conversation. Please try again.');
        }
        navigate('/chat', { replace: true });
      }
    }
  };

  const selectConversation = async (conv: Conversation) => {
    setSelectedConv(conv);
    await loadMessages(conv.id);
    chatService.markAsRead(conv.id);
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setMessageInput(value);
    
    if (selectedConv) {
      if (value.length > 0) {
        chatService.sendTypingIndicator(selectedConv.otherUserId, true);
        
        if (typingTimeoutRef.current) clearTimeout(typingTimeoutRef.current);
        
        typingTimeoutRef.current = setTimeout(() => {
          if (selectedConv) {
            chatService.sendTypingIndicator(selectedConv.otherUserId, false);
          }
        }, 1000);
      } else {
        chatService.sendTypingIndicator(selectedConv.otherUserId, false);
      }
    }
  };

  const sendMessage = async () => {
    if (selectedConv && messageInput.trim()) {
      try {
        const success = await chatService.sendMessage(selectedConv.otherUserId, messageInput);
        if (success) {
          setMessageInput("");
          // Stop typing indicator
          chatService.sendTypingIndicator(selectedConv.otherUserId, false);
        } else {
          alert('Failed to send message - WebSocket not connected. Try refreshing the page.');
        }
      } catch (err: any) {
        console.error('Failed to send message:', err);
        if (err.message?.includes('403') || err.message?.includes('not connected')) {
          alert('Cannot send message. You are no longer connected with this user.');
          await loadConversations();
          setSelectedConv(null);
          setMessages([]);
        } else {
          alert('Failed to send message. Please try again.');
        }
      }
    }
  };

  return (
    <div className="chat-container">
      <div className="conversations-panel">
        <div className="conversations-header">
          <h2>Messages</h2>
        </div>
        
        <div className="conversations-list">
          {conversations.length === 0 ? (
            <div className="no-conversations">No conversations yet</div>
          ) : (
            conversations.map(conv => (
              <div
                key={conv.id}
                onClick={() => selectConversation(conv)}
                className={`conversation-item ${conv.id === selectedConv?.id ? "selected" : ""}`}
              >
                <div className="conversation-avatar">👤</div>
                <div className="conversation-content">
                  <div className="conversation-name">
                    {conv.otherUserName}
                    {conv.unreadCount > 0 && (
                      <span className="unread-badge">{conv.unreadCount}</span>
                    )}
                  </div>
                  <div className="conversation-last-message">{conv.lastMessage || "No messages"}</div>
                </div>
              </div>
            ))
          )}
        </div>
      </div>

      <div className="chat-panel">
        {selectedConv ? (
          <>
            <div className="chat-header">
              <h3>{selectedConv.otherUserName}</h3>
            </div>
            
            <div className="messages-container">
              {messages.map(msg => (
                <div
                  key={msg.id}
                  className={`message ${msg.senderId === currentUserId ? "sent" : "received"}`}
                >
                  <div className="message-bubble">
                    <div className="message-content">{msg.content}</div>
                    <div className="message-time">
                      {new Date(msg.timestamp).toLocaleTimeString([], { 
                        hour: '2-digit', 
                        minute: '2-digit' 
                      })}
                      {msg.senderId === currentUserId && (
                        <span className={`message-status ${msg.isRead ? 'read' : 'sent'}`}>
                          {msg.isRead ? ' ✓✓' : ' ✓'}
                        </span>
                      )}
                    </div>
                  </div>
                </div>
              ))}
              <div ref={messagesEndRef} />
            </div>
            
            {isOtherUserTyping && (
              <div style={{ padding: '8px 16px', fontSize: '14px', color: '#999', fontStyle: 'italic' }}>
                {selectedConv.otherUserName} is typing...
              </div>
            )}
            
            <div className="message-input-container">
              <input
                className="message-input"
                value={messageInput}
                onChange={handleInputChange}
                onKeyPress={(e) => e.key === "Enter" && sendMessage()}
                placeholder="Type a message..."
              />
              <button className="send-button" onClick={sendMessage}>
                Send
              </button>
            </div>
          </>
        ) : (
          <div className="no-chat-selected">
            <div className="no-chat-icon">💬</div>
            <h3>Select a conversation</h3>
            <p>Choose a conversation from the list to start chatting</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default ChatPage;