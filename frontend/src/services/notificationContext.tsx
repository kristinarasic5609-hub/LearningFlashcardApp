import { createContext, useContext, useState, ReactNode, useCallback } from 'react';

type NotificationType = 'success' | 'error' | 'info';

interface Notification {
  id: number;
  type: NotificationType;
  message: string;
}

interface NotificationContextValue {
  notify: (message: string, type?: NotificationType) => void;
}

const NotificationContext = createContext<NotificationContextValue | undefined>(undefined);

export function NotificationProvider({ children }: { children: ReactNode }) {
  const [notifications, setNotifications] = useState<Notification[]>([]);

  const notify = useCallback((message: string, type: NotificationType = 'info') => {
    const id = Date.now();
    setNotifications((prev) => [...prev, { id, type, message }]);
    setTimeout(() => {
      setNotifications((prev) => prev.filter((n) => n.id !== id));
    }, 4000);
  }, []);

  return (
    <NotificationContext.Provider value={{ notify }}>
      {children}
      <div className="notifications">
        {notifications.map((n) => (
          <div key={n.id} className={`notification notification-${n.type}`}>
            {n.message}
          </div>
        ))}
      </div>
    </NotificationContext.Provider>
  );
}

export function useNotification() {
  const ctx = useContext(NotificationContext);
  if (!ctx) throw new Error('useNotification must be used within NotificationProvider');
  return ctx;
}
