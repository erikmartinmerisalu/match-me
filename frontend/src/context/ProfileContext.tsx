import { createContext, useContext, useEffect, useState } from "react";
import type { ReactNode} from "react";
import { userService } from "../service/userService";


type ProfileContextType = {
    loggedIn : boolean | null,

  }



export const ProfileContext = createContext<ProfileContextType | undefined>(undefined);

type ProfileProviderProps = {
  children: ReactNode;
};


export const AuthContextProvider = ({children } : ProfileProviderProps) => {
    const [loggedIn, setLoggedIn] = useState<boolean | null >(false);
    const [userData, setUserData] = useState<FormData | null >(null);

  useEffect(() => {
    try{
        const res = await userService.getUserProfile();
    }catch (err){
        console.log(err)
    }

    },[]);

    

    return(
        <ProfileContext.Provider value={{loggedIn}}>
            {children}
        </ProfileContext.Provider>
    )
}

export const useProfile = () => {
  const ctx = useContext(ProfileContext);
  if (!ctx) {
    throw new Error("useAuth must be used inside AuthContextProvider");
  }
  return ctx;
};