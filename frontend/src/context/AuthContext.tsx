import { createContext, useContext, useEffect, useState } from "react";
import type { ReactNode} from "react";

type AuthContextType = {
    loggedIn : boolean | null,
    setLoggedIn: (value : boolean) =>void ;
    signOut : () => void;
  }

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

type AuthProviderProps = {
  children: ReactNode;
};

export const AuthContextProvider = ({children } : AuthProviderProps) => {
    const [loggedIn, setLoggedIn] = useState<boolean | null >(null);


  useEffect(() => {
    const token = sessionStorage.getItem("token");

    if (token) {
        fetch("/api/me", {
        headers: {
            Authorization: `Bearer ${token}`,
        },
        })
        .then((res) => {
            if (!res.ok) throw new Error("unauthorized");
            setLoggedIn(true);
        })
        .catch(() => {
            setLoggedIn(false);
            sessionStorage.removeItem("token");
        });
    }
    }, []);

    const signOut = () => {
      sessionStorage.removeItem("token");
      setLoggedIn(false);
    }


    return(
        <AuthContext.Provider value={{loggedIn, setLoggedIn, signOut}}>
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