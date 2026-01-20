// ============================================
// 2. LOGIN COMPONENT WITH FIXED IMPORTS
// ============================================
// File: src/components/Login.tsx
// cspell:disable

import { useState } from 'react';
import api from '../services/api';
import type { AuthResponse } from '../types/auth';
import authService from '../services/authService';
import type { AuthState } from '../services/authService';
import '../styles/Login.css';

interface LoginProps {
  onLoginSuccess: (auth: AuthState) => void;
}

export const Login = ({ onLoginSuccess }:LoginProps) => {
  const [showLogin, setShowLogin] = useState(true);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [displayName, setDisplayName] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleAuth = async (isRegister: boolean): Promise<void> => {
    setError(null);
    setLoading(true);

    try {
      const result: AuthResponse = isRegister
        ? await api.register(email, displayName, password)
        : await api.login(email, password);

      const newAuth: AuthState = {
        token: result.token,
        user: { email: result.email, displayName: result.displayName, id: result.id },
      };

      authService.saveAuth(newAuth);
      onLoginSuccess(newAuth);
      setEmail('');
      setPassword('');
      setDisplayName('');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Authentication failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-header">
        <h1>Varausjärjestelmä</h1>
      </div>

      <div className="login-tabs">
        <button
          onClick={() => setShowLogin(true)}
          className={`login-tab-button ${showLogin ? 'active' : ''}`}
        >
          Kirjaudu sisään
        </button>
        <button
          onClick={() => setShowLogin(false)}
          className={`login-tab-button register ${!showLogin ? 'active' : ''}`}
        >
          Rekisteröidy
        </button>
      </div>

      <div className="login-form">
        <h2>{showLogin ? 'Kirjaudu' : 'Rekisteröidy'}</h2>

        <div className="login-form-group">
          <label>Sähköposti</label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="example@example.com"
          />
        </div>

        {!showLogin && (
          <div className="login-form-group">
            <label>Nimi</label>
            <input
              type="text"
              value={displayName}
              onChange={(e) => setDisplayName(e.target.value)}
              placeholder="Sinun nimi"
            />
          </div>
        )}

        <div className="login-form-group">
          <label>Salasana</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="••••••••"
          />
        </div>

        {error && <div className="login-error">⚠️ {error}</div>}

        <button
          onClick={() => handleAuth(!showLogin)}
          disabled={loading}
          className="login-button"
        >
          {loading ? 'Odota...' : showLogin ? 'Kirjaudu' : 'Rekisteröidy'}
        </button>
      </div>
    </div>
  );
};