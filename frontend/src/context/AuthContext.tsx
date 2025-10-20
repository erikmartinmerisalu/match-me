import { createContext, useContext, useEffect, useState } from "react";
import type { ReactNode} from "react";
import { userService } from "../service/userService";
import type { UserFormData } from "../types/UserProfileTypes";

type AuthContextType = {
    loggedIn : boolean | null,
    signOut : () => void;
    loggedInUserData : UserFormData | null;
    setLoggedInUserData : React.Dispatch<React.SetStateAction<UserFormData>>
  }



export const AuthContext = createContext<AuthContextType | undefined>(undefined);

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

    const signOut = async () => {
      await fetch("http://localhost:8080/api/auth/logout", {
        method: "POST",
        credentials: "include"
      });
      setLoggedIn(false);
      
      // setUsername("");
      // setProfilePictureBase64("");
    }

    return(
        <AuthContext.Provider value={{loggedIn, loggedInUserData, setLoggedInUserData, signOut}}>
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