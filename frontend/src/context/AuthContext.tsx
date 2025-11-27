import { createContext, useContext, useEffect, useState } from "react";
import type { ReactNode } from "react";
import { userService } from "../services/userService";
import type { UserFormData } from "../types/UserProfileTypes";
import { useOnlineStatus } from "../hooks/useOnlineStatus";

type AuthContextType = {
  loggedIn: boolean | null;
  signOut: () => void;
  logIn: () => void;
  loggedInUserData: UserFormData | null;
  setLoggedInUserData: React.Dispatch<React.SetStateAction<UserFormData | null>>;
};

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

type AuthProviderProps = {
  children: ReactNode;
};

// Helper function to normalize games data
const normalizeGamesData = (games: any): { [key: string]: any } => {
  if (!games) return {};
  
  const normalized: { [key: string]: any } = {};
  Object.keys(games).forEach(gameKey => {
    const game = games[gameKey];
    normalized[gameKey] = {
      preferredServers: Array.isArray(game.preferredServers) ? game.preferredServers : [],
      expLvl: game.expLvl || '',
      gamingHours: game.gamingHours || '',
      currentRank: game.currentRank || ''
    };
  });
  
  return normalized;
};

export const AuthContextProvider = ({ children }: AuthProviderProps) => {
  const [loggedIn, setLoggedIn] = useState<boolean | null>(false);
  const [loggedInUserData, setLoggedInUserData] = useState<UserFormData | null>(null);

  //  Send heartbeats when logged in (runs on all pages)
  useOnlineStatus();

  // Enhanced setLoggedInUserData with debug logging
  const setLoggedInUserDataWithLogging = (update: React.SetStateAction<UserFormData | null>) => {
    if (typeof update === 'function') {
      setLoggedInUserData(prev => {
        const newData = update(prev);
        return newData;
      });
    } else {
      setLoggedInUserData(update);
    }
  };

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const res = await userService.getUserProfile();
        
        if (res !== null) {
          console.log("✅ AuthContext: User data received:", res);
          setLoggedIn(true);
          
          // Normalize games data
          const normalizedGames = normalizeGamesData(res.games);
          
          const completeUserData: UserFormData = {
            id: res.id || null,
            displayName: res.displayName || '',
            aboutMe: res.aboutMe || '',
            birthDate: res.birthDate || '',
            lookingFor: res.lookingFor || '', // Use lookingFor consistently
            games: normalizedGames,
            maxPreferredDistance: res.maxPreferredDistance || 50,
            timezone: res.timezone || '',
            preferredAgeMin: res.preferredAgeMin || 18,
            preferredAgeMax: res.preferredAgeMax || 100,
            profilePic: res.profilePic || null,
            location: res.location || '',
            latitude: res.latitude || null,
            longitude: res.longitude || null,
            profileCompleted: res.profileCompleted || false,
            age: res.age || 0,
            competitiveness: res.competitiveness || '',
            voiceChatPreference: res.voiceChatPreference || '',
            playSchedule: res.playSchedule || '',
            mainGoal: res.mainGoal || '',
          };
          
          setLoggedInUserData(completeUserData);
        } else {
          setLoggedIn(false);
          setLoggedInUserData(null);
        }
      } catch (err) {
        setLoggedIn(false);
        setLoggedInUserData(null);
      }
    };
    fetchUser();
  }, []);

  const signOut = async () => {
    try {
      await fetch("http://localhost:8080/api/auth/logout", {
        method: "POST",
        credentials: "include"
      });
    } catch (error) {
      console.error("Logout error:", error);
    } finally {
      setLoggedIn(false);
      setLoggedInUserData(null);
    }
  };

  const logIn = async () => {
    try {
      const res = await userService.getUserProfile();
      
      if (res !== null) {
        setLoggedIn(true);
        // Normalize games data
        const normalizedGames = normalizeGamesData(res.games);
        
        const completeUserData: UserFormData = {
          id: res.id || null,
          displayName: res.displayName || '',
          aboutMe: res.aboutMe || '',
          birthDate: res.birthDate || '',
          lookingFor: res.lookingFor || '',
          games: normalizedGames,
          maxPreferredDistance: res.maxPreferredDistance || 50,
          timezone: res.timezone || '',
          preferredAgeMin: res.preferredAgeMin || 18,
          preferredAgeMax: res.preferredAgeMax || 100,
          profilePic: res.profilePic || null,
          location: res.location || '',
          latitude: res.latitude || null,
          longitude: res.longitude || null,
          profileCompleted: res.profileCompleted || false,
          age: res.age || 0,
          competitiveness: res.competitiveness || '',
          voiceChatPreference: res.voiceChatPreference || '',
          playSchedule: res.playSchedule || '',
          mainGoal: res.mainGoal || '',
        };
        
        setLoggedInUserData(completeUserData);
      } else {
        setLoggedIn(false);
        setLoggedInUserData(null);
      }
    } catch (err) {
      console.error("Login error:", err);
      setLoggedIn(false);
      setLoggedInUserData(null);
    }
  };

  return (
    <AuthContext.Provider value={{
      loggedIn,
      logIn,
      loggedInUserData,
      setLoggedInUserData: setLoggedInUserDataWithLogging,
      signOut
    }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error("useAuth must be used inside AuthContextProvider");
  }
  return ctx;
};