import React, { useEffect, useState } from 'react'
import "./userprofile.css"
import UserGamesComponent from '../../components/userProfile/UserGamesComponent';
import UserBioComponent from '../../components/userProfile/UserBioComponent';
import ProfilePicChange from '../../components/profilepic/ProfilePicChange';
import { useAuth } from '../../context/AuthContext';
import { userService } from '../../services/userService';
import UserGameDetails from '../../components/userProfile/UserGameDetails';
import UserGamerType from '../../components/userProfile/UserGamerType';
import UserPreferencesComponent from '../../components/userProfile/UserPreferencesComponent';
import { useNavigate } from 'react-router-dom';
import { useToast } from '../../context/ToastContext';

// Helper function to normalize games data
const normalizeGamesData = (games: any) => {
  if (!games) return {};
  
  const normalized: any = {};
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


const validateGameData = (games: any): { isValid: boolean; errorMessage?: string } => {
  if (!games || Object.keys(games).length === 0) {
    return { 
      isValid: false, 
      errorMessage: "Please select at least one game" 
    };
  }
  
  const incompleteGames = [];

  for (const gameKey in games) {
    const game = games[gameKey];
    const missingFields = [];
    
    if (!game.expLvl || game.expLvl === "") missingFields.push("Experience");
    if (!game.gamingHours || game.gamingHours === "") missingFields.push("Hours");
    if (!Array.isArray(game.preferredServers) || game.preferredServers.length === 0) missingFields.push("Servers");
    if (!game.currentRank || game.currentRank === "") missingFields.push("Rank");
    
    if (missingFields.length > 0) {
      incompleteGames.push(`• ${gameKey} - ${missingFields.join(", ")}`);
    }
  }
  
  if (incompleteGames.length > 0) {
    return { 
      isValid: false, 
      errorMessage: `Fill out missing details:\n${incompleteGames.join('\n')}` 
    };
  }
  
  return { isValid: true };
};
function UserProfile() {
  const [cardState, setCardState] = useState<number>(0);
  const { loggedInUserData, setLoggedInUserData } = useAuth();
  const [gameIndex, setGameIndex] = useState(0);
  const gamesList = loggedInUserData?.games ? Object.entries(loggedInUserData.games) : [];
  const currentGame = gamesList[gameIndex] || null;
  const navigate = useNavigate();
  const toast = useToast();
  const [isLoading, setIsLoading] = useState(true);
  const [isCompleting, setIsCompleting] = useState(false);

  // Enhanced profile loading - REMOVED the automatic redirect
  useEffect(() => {
    let isMounted = true;

    const fetchProfile = async () => {
      try {
        console.log("🔄 UserProfile: Fetching profile data...");
        const profile = await userService.getUserProfile();
        
        if (!isMounted) return;
        
        console.log("✅ UserProfile: Raw profile response:", profile);
        
        if (profile) {
          // REMOVED: Automatic redirect for completed profiles
          // Users should be able to edit their completed profiles
          
          // Normalize games data
          const normalizedGames = normalizeGamesData(profile.games);
          
          // Create a complete user data object
          const completeUserData = {
            id: profile.id || null,
            displayName: profile.displayName || '',
            aboutMe: profile.aboutMe || '',
            birthDate: profile.birthDate || '',
            lookingFor: profile.lookingFor || '',
            games: normalizedGames,
            maxPreferredDistance: profile.maxPreferredDistance || 50,
            timezone: profile.timezone || '',
            preferredAgeMin: profile.preferredAgeMin || 18,
            preferredAgeMax: profile.preferredAgeMax || 100,
            profilePic: profile.profilePic || null,
            location: profile.location || '',
            latitude: profile.latitude || null,
            longitude: profile.longitude || null,
            profileCompleted: profile.profileCompleted || false,
            age: profile.age || 0,
            competitiveness: profile.competitiveness || '',
            voiceChatPreference: profile.voiceChatPreference || '',
            playSchedule: profile.playSchedule || '',
            mainGoal: profile.mainGoal || '',
          };

          console.log("✅ UserProfile: Setting complete user data:", completeUserData);
          setLoggedInUserData(completeUserData);
        } else {
          console.log("❌ UserProfile: No profile data received");
        }
      } catch (error) {
        console.error("❌ UserProfile: Failed to fetch profile:", error);
        toast.error("Failed to load profile data");
      } finally {
        if (isMounted) {
          setIsLoading(false);
        }
      }
    };
    
    fetchProfile();

    return () => {
      isMounted = false;
    };
  }, []);

  // Debug logging - only log when data actually changes
  useEffect(() => {
    if (loggedInUserData && Object.keys(loggedInUserData).length > 0) {
      console.log("📊 UserProfile: Current loggedInUserData:", loggedInUserData);
      console.log("🎮 UserProfile: Games data:", loggedInUserData.games);
    }
  }, [loggedInUserData]);

  async function nextCardState() {
    try {
      if (cardState === 0) {
        const payload = {
          profilePic: loggedInUserData?.profilePic || '',
          aboutMe: loggedInUserData?.aboutMe || '',
          displayName: loggedInUserData?.displayName || '',
          lookingFor: loggedInUserData?.lookingFor || '',
          birthDate: loggedInUserData?.birthDate || ''
        };
        
        // ENHANCED: Validation with specific field names
        const missingFields = [];
        
        if (!payload.displayName?.trim()) missingFields.push("Display Name");
        if (!payload.birthDate) missingFields.push("Birth Date");
        
        if (missingFields.length > 0) {
          toast.error(`Please fill in these required fields: ${missingFields.join(", ")}`);
          return;
        }
        
        console.log("📤 UserProfile: Saving bio data:", payload);
        const res = await userService.updateProfile(payload);
        if (res.error) {
          toast.error(res.error);
          return;
        }
        toast.success("Profile saved");
        setCardState(cardState + 1);
        return;
      }

      if (cardState === 1) {
        const gamesCount = Object.keys(loggedInUserData?.games || {}).length;
        if (gamesCount === 0) {
            toast.error("Please select at least one game");
            return;
        }
        
        // Get the ORIGINAL games from backend
        const originalProfile = await userService.getUserProfile();
        const originalGames = originalProfile?.games || {};
        const currentGames = loggedInUserData?.games || {};
        
        // Find games that were deselected (in original but not in current)
        const gamesToRemove = Object.keys(originalGames).filter(
            game => !currentGames[game]
        );
        
        console.log(" Games to remove:", gamesToRemove);
        
        if (gamesToRemove.length > 0) {
            // Only remove deselected games, keep everything else as-is
            const cleanedGames = { ...originalGames };
            gamesToRemove.forEach(game => {
                delete cleanedGames[game];
            });
            
            console.log("🔍 After removal:", Object.keys(cleanedGames));
            
            // Save the cleaned games (preserves data for selected games)
            const payload = { games: cleanedGames };
            console.log("📤 Removing deselected games:", gamesToRemove);
            const res = await userService.updateProfile(payload);
            if (res.error) {
                toast.error(res.error);
                return;
            }
            
            // VERIFY: Fetch again to see if backend actually updated
            const verifyProfile = await userService.getUserProfile();
            console.log("🔍 After save, backend has:", Object.keys(verifyProfile?.games || {}));
            
            // Update local state to match the cleaned backend
            setLoggedInUserData((prev: any) => ({
                ...prev,
                games: cleanedGames
            }));
        }
        
        toast.success("Games updated - now fill in the details");
        setCardState(cardState + 1);
        return;
    }

      if (cardState === 2) {
        if (gameIndex < gamesList.length - 1) {
          setGameIndex(gameIndex + 1);
          return;
        } else {
          // FIX: Validate that all game details are filled before saving
          const validation = validateGameData(loggedInUserData?.games);
          if (!validation.isValid) {
            toast.error(validation.errorMessage || "Please fill in all game details");
            return;
          }
          
          // Save all game details
          const payload = {
            games: loggedInUserData?.games || {}
          };
          console.log("📤 UserProfile: Saving game details:", payload);
          const res = await userService.updateProfile(payload);
          if (res.error) {
            toast.error(res.error);
            return;
          }
          toast.success("Game Details saved!");
          setCardState(cardState + 1);
          return;
        }
      }

      if (cardState === 3) {
        // ENHANCED: Validation with specific field names
        const missingFields = [];
        
        if (!loggedInUserData?.competitiveness) missingFields.push("Competitiveness");
        if (!loggedInUserData?.voiceChatPreference) missingFields.push("Voice Chat Preference");
        if (!loggedInUserData?.playSchedule) missingFields.push("Play Schedule");
        if (!loggedInUserData?.mainGoal) missingFields.push("Main Goal");
        
        if (missingFields.length > 0) {
          toast.error(`Please fill in these gamer type fields: ${missingFields.join(", ")}`);
          return;
        }

        const payload = {
          competitiveness: loggedInUserData?.competitiveness || '',
          voiceChatPreference: loggedInUserData?.voiceChatPreference || '',
          playSchedule: loggedInUserData?.playSchedule || '',
          mainGoal: loggedInUserData?.mainGoal || '',
        };
        console.log("📤 UserProfile: Saving gamer type:", payload);
        const res = await userService.updateProfile(payload);
        if (res.error) {
          toast.error(res.error);
          return;
        }
        toast.success("Gamer type saved!");
        setCardState(cardState + 1);
        return;
      }

      if (cardState === 4) {
        // If profile is already completed, just save the updates without redirecting
        if (loggedInUserData?.profileCompleted) {
          const payload = {
            // Bio data
            displayName: loggedInUserData?.displayName || '',
            aboutMe: loggedInUserData?.aboutMe || '',
            birthDate: loggedInUserData?.birthDate || '',
            lookingFor: loggedInUserData?.lookingFor || '',
            profilePic: loggedInUserData?.profilePic || '',
            
            // Games data
            games: loggedInUserData?.games || {},
            
            // Gamer type data
            competitiveness: loggedInUserData?.competitiveness || '',
            voiceChatPreference: loggedInUserData?.voiceChatPreference || '',
            playSchedule: loggedInUserData?.playSchedule || '',
            mainGoal: loggedInUserData?.mainGoal || '',
            
            // Preferences data
            preferredAgeMin: loggedInUserData.preferredAgeMin,
            preferredAgeMax: loggedInUserData.preferredAgeMax,
            location: loggedInUserData.location || '',
            latitude: loggedInUserData.latitude || null,
            longitude: loggedInUserData.longitude || null,
            maxPreferredDistance: loggedInUserData.maxPreferredDistance,
            timezone: loggedInUserData?.timezone || Intl.DateTimeFormat().resolvedOptions().timeZone,
            
            // Keep profile completed
            profileCompleted: true
          };
          
          console.log("📤 UserProfile: Saving updated profile:", payload);
          const res = await userService.updateProfile(payload);
          if (res.error) {
            toast.error(res.error);
            return;
          }
          
          toast.success("Profile updated!");
          return;
        }

        // For new profile completion (first time setup)
        // Prevent multiple completion attempts
        if (isCompleting) {
          return;
        }

        setIsCompleting(true);

        // ENHANCED: Validation with specific field names
        const missingFields = [];
        
        if (!loggedInUserData?.preferredAgeMin) missingFields.push("Minimum Age");
        if (!loggedInUserData?.preferredAgeMax) missingFields.push("Maximum Age");
        if (!loggedInUserData?.maxPreferredDistance) missingFields.push("Maximum Distance");
        if (!loggedInUserData?.location) missingFields.push("Location");
        
        if (missingFields.length > 0) {
          toast.error(`Please fill in these preference fields: ${missingFields.join(", ")}`);
          setIsCompleting(false);
          return;
        }

        // Final validation - ensure all steps are completed
        const finalValidation = validateGameData(loggedInUserData?.games).isValid &&
          loggedInUserData?.competitiveness &&
          loggedInUserData?.voiceChatPreference &&
          loggedInUserData?.playSchedule &&
          loggedInUserData?.mainGoal;

        if (!finalValidation) {
          toast.error("Please complete all previous steps before finishing");
          setIsCompleting(false);
          return;
        }

        // Send ALL data in the final update to ensure everything is saved
        const payload = {
          // Bio data
          displayName: loggedInUserData?.displayName || '',
          aboutMe: loggedInUserData?.aboutMe || '',
          birthDate: loggedInUserData?.birthDate || '',
          lookingFor: loggedInUserData?.lookingFor || '',
          profilePic: loggedInUserData?.profilePic || '',
          
          // Games data
          games: loggedInUserData?.games || {},
          
          // Gamer type data
          competitiveness: loggedInUserData?.competitiveness || '',
          voiceChatPreference: loggedInUserData?.voiceChatPreference || '',
          playSchedule: loggedInUserData?.playSchedule || '',
          mainGoal: loggedInUserData?.mainGoal || '',
          
          // Preferences data
          preferredAgeMin: loggedInUserData.preferredAgeMin,
          preferredAgeMax: loggedInUserData.preferredAgeMax,
          location: loggedInUserData.location || '',
          latitude: loggedInUserData.latitude || null,
          longitude: loggedInUserData.longitude || null,
          maxPreferredDistance: loggedInUserData.maxPreferredDistance,
          timezone: loggedInUserData?.timezone || Intl.DateTimeFormat().resolvedOptions().timeZone,
          
          // Completion flag
          profileCompleted: true
        };
        
        console.log("📤 UserProfile: Saving COMPLETE profile:", payload);
        const res = await userService.updateProfile(payload);
        if (res.error) {
          toast.error(res.error);
          setIsCompleting(false);
          return;
        }
        
        // Update context with completion status
        setLoggedInUserData((prev: any) => ({
          ...prev,
          ...payload,
          profileCompleted: true,
        }));
        
        toast.success("Profile completed!");

        // Single navigation with delay
        setTimeout(() => {
          navigate("/home");
        }, 1500);   
        return;
      }
    } catch (error) {
      console.error("❌ UserProfile: Error in nextCardState:", error);
      const errorMessage = error instanceof Error ? error.message : "An error occurred while saving";
      toast.error(errorMessage);
      setIsCompleting(false);
    }
  }

  // Add a back button function
  const prevCardState = () => {
    if (cardState > 0) {
      if (cardState === 2 && gameIndex > 0) {
        setGameIndex(gameIndex - 1);
      } else {
        setCardState(cardState - 1);
      }
    }
  };
  
  if (isLoading) {
    return <div>Loading profile...</div>;
  }
    
  return (
    <div>
      <div className='profile-card'>
        <h2> Gamer Profile {loggedInUserData?.profileCompleted ? '(Edit)' : '(Setup)'}</h2>
        
        {/* Progress indicator */}
        <div className="progress-indicator">
          Step {cardState + 1} of 5: 
          {cardState === 0 && ' Basic Info'}
          {cardState === 1 && ' Games'}
          {cardState === 2 && ' Game Details'}
          {cardState === 3 && ' Gamer Type'}
          {cardState === 4 && ' Preferences'}
        </div>

        <form className="profile-form">
          { cardState === 0 &&
          <>
            <ProfilePicChange
              width={150}
              height={150}
            />  
            <UserBioComponent/>
          </>
          }
          { cardState === 1 &&
          <UserGamesComponent  />
          }
          {cardState === 2 && currentGame &&
          <UserGameDetails
            key={currentGame[0]}
            gameName={currentGame[0]}
            gameData={currentGame[1]}
            onChange={(updatedGame) => {
              setLoggedInUserData((prev: any) => ({
                ...prev,
                games: {
                  ...prev.games,
                  [currentGame[0]]: updatedGame
                }
              }));
            }}
          />}
          {cardState === 3 &&
          <UserGamerType
            gameData={loggedInUserData}
            onChange={(updatedData) => {
              setLoggedInUserData((prev: any) => ({
                ...prev,
                ...updatedData 
              }));
            }} 
          />}
          {cardState === 4 &&
          <UserPreferencesComponent />}
          
        </form>
      </div>
      
      {/* Navigation buttons */}
      <div className="profile-navigation">
        {cardState > 0 && (
          <button onClick={prevCardState}>Back</button>
        )}
        <button onClick={nextCardState} disabled={isCompleting}>
          {cardState === 4 ? 
            (loggedInUserData?.profileCompleted ? 'Update Profile' : (isCompleting ? 'Completing...' : 'Complete Profile')) 
            : 'Next'}
        </button>
      </div>
    </div>
  );
}

export default UserProfile;