import { createContext, useContext, useEffect, useState } from "react";
import type { ReactNode} from "react";
import { userService } from "../service/userService";
import type { UserFormData } from "../types/UserProfileTypes";

//React Context --> Auth context is for providing login boolean state over the application. If user or app refreshes,
// then we need to fetch and check if the cookie is still valid, because context loses all of the values after refresh

//decleare the types
type AuthContextType = {
    loggedIn : boolean | null,
    signOut : () => void;
    loggedInUserData : UserFormData | null;
    setLoggedInUserData : React.Dispatch<React.SetStateAction<UserFormData>>
  }



export const AuthContext = createContext<AuthContextType | undefined>(undefined);

//types for what we expect as output
type AuthProviderProps = {
  children: ReactNode;
};


// This is the Provider component that holds the state and provides it to children
export const AuthContextProvider = ({children } : AuthProviderProps) => {
    const [loggedIn, setLoggedIn] = useState<boolean | null >(false);
    const [loggedInUserData, setLoggedInUserData] = useState<UserFormData>({
    id: null,
    displayName: "",
    aboutMe: "",
    birthdate: "",
    lookingfor: "",
    games: {},
    maxPreferredDistance: 0,
    timezone: "",
    lookingFor: "",
    preferredAgeMin: 0,
    preferredAgeMax: 0,
    profilePic: "",
    location: "",
    latitude: null,
    longitude: null,
    profileCompleted: false,
    age: 0,
    }
    )

// useEffect runs on component mount to check if the user session is valid
// Fetches /api/users/me which returns user info if logged in (cookie/session is valid)
  useEffect(() => {
    const fetchUser = async ()=> { 
      try{
        const res  = await userService.getUserProfile();
        
        if(!res){
          setLoggedIn(false);
        }
        
        setLoggedIn(true);
        setLoggedInUserData(res)
        console.log(res)
      }catch (err){
          console.log(err)
      }
    }
    fetchUser();
      },[]);
    console.log(loggedInUserData)

    // Function to log out the user
    // Sends POST to backend to clear session/cookie
    // Then updates local state
    const signOut = async () => {
      await fetch("http://localhost:8080/api/auth/logout", {
        method: "POST",
        credentials: "include"
      });
      setLoggedIn(false);
      // setUsername("");
      // setProfilePictureBase64("");
    }

    // Provide the state and functions to all children components
    return(
        <AuthContext.Provider value={{loggedIn, loggedInUserData, setLoggedInUserData, signOut}}>
            {children}
        </AuthContext.Provider>
    )
}

// Custom hook to use AuthContext easily in other components/pages
export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error("useAuth must be used inside AuthContextProvider");
  }
  return ctx;
};