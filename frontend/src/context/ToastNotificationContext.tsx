import React, {
  createContext,
  useState,
  ReactNode,
  useContext,
  useEffect,
} from "react";

export enum NotificationTypes {
  success = "success",
  error = "error",
  info = "info",
}

// notification object
export interface Notification {
  id: string;
  message: string;
  type: NotificationTypes;
  duration?: number; // milliseconds to show
}

interface NotificationContextProps {
  notificationsList: Notification[];
  addNotification: (notification: Notification) => void;
  removeNotification: (id: string) => void;
  CustomComponent?: React.ElementType;
}

const NotificationContext = createContext<NotificationContextProps | undefined>(
  undefined
);

export const useNotification = (): NotificationContextProps => {
  const context = useContext(NotificationContext);
  if (!context) {
    throw new Error(
      "useNotification must be used within a NotificationProvider"
    );
  }
  return context;
};

// This file name: ToastNotificationContext

interface ToastNotificationProviderProps {
  children: ReactNode;
  customComponent?: React.ElementType;
}

export const ToastNotificationProvider = ({
  children,
  customComponent: CustomComponent,
}: ToastNotificationProviderProps) => {
  const [notificationsList, setNotificationsList] = useState<Notification[]>(
    []
  );

  const addNotification = (notification: Notification) => {
    setNotificationsList((prevNotifications) => [
      ...prevNotifications,
      notification,
    ]);
  };

  const removeNotification = (id: string) => {
    setNotificationsList((prevNotifications) =>
      prevNotifications.filter((notification) => notification.id !== id)
    );
  };

  // remove notifications after a timeout
  useEffect(() => {
    notificationsList.forEach((notification) => {
      if (notification.duration) {
        const timeoutId = setTimeout(() => {
          removeNotification(notification.id);
        }, notification.duration);

        return () => clearTimeout(timeoutId);
      }
    });
  }, [notificationsList]);

  return (
    <NotificationContext.Provider
      value={{
        notificationsList,
        addNotification,
        removeNotification,
        CustomComponent,
      }}
    >
      {children}
    </NotificationContext.Provider>
  );
};