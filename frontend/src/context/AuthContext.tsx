import { createContext, useContext, useEffect, useState } from "react";
import type { ReactNode} from "react";

//React Context --> Auth context is for providing login boolean state over the application. If user or app refreshes,
// then we need to fetch and check if the cookie is still valid, because context loses all of the values after refresh

//decleare the types
type AuthContextType = {
    loggedIn : boolean | null,
    setLoggedIn: (value : boolean) =>void ;
    signOut : () => void;
    userName : string | null;
    profilePictureBase64 : string | null,
  }



export const AuthContext = createContext<AuthContextType | undefined>(undefined);

//types for what we expect as output
type AuthProviderProps = {
  children: ReactNode;
};


// This is the Provider component that holds the state and provides it to children
export const AuthContextProvider = ({children } : AuthProviderProps) => {
    const [loggedIn, setLoggedIn] = useState<boolean | null >(false);
    const [userName, setUsername] = useState<string |null>(" ");
    const [profilePictureBase64, setProfilePictureBase64] = useState<string | null>(null)

// useEffect runs on component mount to check if the user session is valid
// Fetches /api/users/me which returns user info if logged in (cookie/session is valid)
  useEffect(() => {
        fetch("http://localhost:8080/api/users/me/profile", {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        },
        //cookies are included
        credentials: "include"
        })
        .then((res) => {
            if (!res.ok) throw new Error("unauthorized");
            setLoggedIn(true);
            console.log(res);
            return res.json()
            //if res is ok, then set login true

        })
        .then((data) =>{   
        setUsername(data.displayName);
        console.log(userName);
        setProfilePictureBase64(data.profilePic);
        })
        .catch(() => {
            setLoggedIn(false);
        });
    },[]);

    // Function to log out the user
    // Sends POST to backend to clear session/cookie
    // Then updates local state
    const signOut = async () => {
      await fetch("http://localhost:8080/api/auth/logout", {
        method: "POST",
        credentials: "include"
      });
      setLoggedIn(false);
      setUsername("");
      setProfilePictureBase64("");
    }

    // Provide the state and functions to all children components
    return(
        <AuthContext.Provider value={{loggedIn, setLoggedIn, signOut, userName, profilePictureBase64}}>
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