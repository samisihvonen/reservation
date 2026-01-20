// ============================================
// 7. MAIN APP COMPONENT WITH FIXED IMPORTS
// ============================================
// File: src/App.tsx

import { useState, useEffect } from 'react';
import { Login } from './components/Login';
import { Dashboard } from './components/Dashboard';
import authService, { type AuthState } from './services/authService';

function App() {
  const [auth, setAuth] = useState<AuthState>({ token: null, user: null });

  useEffect(() => {
    const savedAuth = authService.getAuth();
    setAuth(savedAuth);
  }, []);

  const handleLoginSuccess = (newAuth: AuthState): void => {
    setAuth(newAuth);
  };

  const handleLogout = (): void => {
    authService.clearAuth();
    setAuth({ token: null, user: null });
  };

  if (!auth.token) {
    return <Login onLoginSuccess={handleLoginSuccess} />;
  }

  const authenticatedAuth = auth as { token: string; user: { email: string; displayName: string; id: number; } };
  return <Dashboard auth={authenticatedAuth} onLogout={handleLogout} />;
}

export default App;