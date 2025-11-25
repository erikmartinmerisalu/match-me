import React, { createContext, useContext, type ReactNode } from "react";
import { toast, ToastContainer, Bounce } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

interface ToastContextProps {
  success: (message: string) => void;
  error: (message: string) => void;
  warn: (message: string) => void;
  info: (message: string) => void;
}

interface ToastProviderProps {
  children: ReactNode;
}

const ToastContext = createContext<ToastContextProps | undefined>(undefined);

export const ToastProvider: React.FC<ToastProviderProps> = ({ children }) => {
  // Custom toast functions that handle multi-line messages
  const success = (message: string) => {
    if (message.includes('\n')) {
      toast.success(
        <div>
          {message.split('\n').map((line, index) => (
            <div key={index}>{line}</div>
          ))}
        </div>
      );
    } else {
      toast.success(message);
    }
  };

  const error = (message: string) => {
    if (message.includes('\n')) {
      toast.error(
        <div>
          {message.split('\n').map((line, index) => (
            <div key={index}>{line}</div>
          ))}
        </div>
      );
    } else {
      toast.error(message);
    }
  };

  const warn = (message: string) => {
    if (message.includes('\n')) {
      toast.warn(
        <div>
          {message.split('\n').map((line, index) => (
            <div key={index}>{line}</div>
          ))}
        </div>
      );
    } else {
      toast.warn(message);
    }
  };

  const info = (message: string) => {
    if (message.includes('\n')) {
      toast.info(
        <div>
          {message.split('\n').map((line, index) => (
            <div key={index}>{line}</div>
          ))}
        </div>
      );
    } else {
      toast.info(message);
    }
  };

  return (
    <ToastContext.Provider value={{ success, error, warn, info }}>
      {children}
      <ToastContainer
        position="top-right"
        autoClose={4000}
        hideProgressBar={false}
        closeOnClick
        pauseOnHover
        draggable
        theme="light"
        transition={Bounce}
      />
    </ToastContext.Provider>
  );
};

export const useToast = (): ToastContextProps => {
  const context = useContext(ToastContext);
  if (!context) throw new Error("useToast must be used within a ToastProvider");
  return context;
};