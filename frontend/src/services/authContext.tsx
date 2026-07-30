import { createContext, useContext, useEffect, useState, ReactNode } from 'react';
import { api, getStoredUser, setStoredUser, setToken } from './api';
import { AuthResponse, User } from '../models/types';

interface AuthContextValue {
  user: User | null;
  loading: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (email: string, username: string, password: string) => Promise<void>;
  logout: () => void;
  updateProfile: (email: string, username: string, password?: string) => Promise<void>;
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

  async function logout() {
    try {
      await api.logout();
    } catch {
      // Logout is primarily client-side; ignore server errors.
    }
    setToken(null);
    setStoredUser(null);
    setUser(null);
  }

  async function updateProfile(email: string, username: string, password?: string) {
    const body: { email: string; username: string; password?: string } = { email, username };
    if (password) {
      body.password = password;
    }
    const res = (await api.updateProfile(body)) as AuthResponse;
    setToken(res.token);
    setStoredUser(res.user);
    setUser(res.user);
  }

  return (
    <AuthContext.Provider
      value={{
        user,
        loading,
        login,
        register,
        logout,
        updateProfile,
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
