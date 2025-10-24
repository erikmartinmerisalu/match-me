import { createContext, useContext, useEffect, useState } from "react";
import type { ReactNode} from "react";
import { userService } from "../services/userService";
import type { UserFormData } from "../types/UserProfileTypes";

type AuthContextType = {
    loggedIn : boolean | null,
    signOut : () => void;
    logIn : () => void;
    loggedInUserData : UserFormData | null;
    setLoggedInUserData : React.Dispatch<React.SetStateAction<UserFormData | null>>
  }



export const AuthContext = createContext<AuthContextType | undefined>(undefined);

type AuthProviderProps = {
  children: ReactNode;
};


// This is the Provider component that holds the state and provides it to children
export const AuthContextProvider = ({children } : AuthProviderProps) => {
    const [loggedIn, setLoggedIn] = useState<boolean | null >(false);
    const [loggedInUserData, setLoggedInUserData] = useState<UserFormData | null>({
    id: null,
    displayName: "",
    aboutMe: "",
    birthdate: "",
    lookingfor: "",
    games: {},
    maxPreferredDistance: 0,
    competitiveness: "",
    voiceChatPreference: "",
    playSchedule: "",
    mainGoal: "",
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


  useEffect(() => {
    const fetchUser = async ()=> {
      try{
        const res  = await userService.getUserProfile();
        
        if(res !== null){
          setLoggedIn(true);
          setLoggedInUserData(res);
        }else{
          setLoggedIn(false);
          setLoggedInUserData(null)
        }
      }catch (err){
          console.log(err)
      }

    }
    fetchUser();
      },[]);

    const signOut = async () => {
      await fetch("http://localhost:8080/api/auth/logout", {
        method: "POST",
        credentials: "include"
      });
      setLoggedIn(false);
      setLoggedInUserData(null)
      cookieStore.delete("jwt");
    }

    const logIn = async () => {
      try{
        const res  = await userService.getUserProfile();
        
        if(res !== null){
          setLoggedIn(true);
          setLoggedInUserData(res);

        }else{
          setLoggedIn(false);
          setLoggedInUserData(null)
        }
      }catch (err){
          console.log(err)
      }
      setLoggedIn(true);
    }

    return(
        <AuthContext.Provider value={{loggedIn, logIn, loggedInUserData, setLoggedInUserData, signOut}}>
            {children}
        </AuthContext.Provider>
    )
}

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error("useAuth must be used inside AuthContextProvider");
  }
  return ctx;
};