import { useEffect, useRef,type  ReactNode } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { useToast } from "../context/ToastContext";

interface ProfileProtectedRouteProps {
  children: ReactNode;
}

export default function ProtectedRoute({ children }: ProfileProtectedRouteProps) {
  const { loggedInUserData } = useAuth();
  const navigate = useNavigate();
  const toast = useToast();
  const hasRun = useRef(false);

  useEffect(() => {
    if (!hasRun.current && loggedInUserData?.profileCompleted === false) {
      hasRun.current = true;
      toast.info("Please complete your profile first!");
      navigate("/userprofile");
    } else if (loggedInUserData?.profileCompleted === true && window.location.pathname === "/userprofile") {
        hasRun.current = true;
        navigate("/settings");
    }
  }, [loggedInUserData, navigate, toast]);

  if (loggedInUserData?.profileCompleted === false) return null;

  return <>{children}</>;
}
