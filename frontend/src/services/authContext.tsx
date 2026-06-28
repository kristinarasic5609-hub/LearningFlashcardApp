import { createContext, useContext, useEffect, useState, ReactNode } from 'react';
import { api, getStoredUser, setStoredUser, setToken } from './api';
import { AuthResponse, User } from '../models/types';

interface AuthContextValue {
  user: User | null;
  loading: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (email: string, username: string, password: string) => Promise<void>;
  logout: () => void;
  isAdmin: boolean;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setUser(getStoredUser());
    setLoading(false);
  }, []);

  async function login(email: string, password: string) {
    const res = (await api.login({ email, password })) as AuthResponse;
    setToken(res.token);
    setStoredUser(res.user);
    setUser(res.user);
  }

  async function register(email: string, username: string, password: string) {
    const res = (await api.register({ email, username, password })) as AuthResponse;
    setToken(res.token);
    setStoredUser(res.user);
    setUser(res.user);
  }

  function logout() {
    setToken(null);
    setStoredUser(null);
    setUser(null);
  }

  return (
    <AuthContext.Provider
      value={{
        user,
        loading,
        login,
        register,
        logout,
        isAdmin: user?.role === 'ADMIN',
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
