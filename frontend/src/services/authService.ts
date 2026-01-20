// ============================================
// 2. AUTH SERVICE
// ============================================
// File: src/services/authService.ts

export interface AuthState {
  token: string | null;
  user: { email: string; displayName: string; id: number } | null;
}

class AuthService {
  private storageKey = "auth";

  getAuth(): AuthState {
    const saved = localStorage.getItem(this.storageKey);
    return saved ? JSON.parse(saved) : { token: null, user: null };
  }

  saveAuth(auth: AuthState): void {
    localStorage.setItem(this.storageKey, JSON.stringify(auth));
  }

  clearAuth(): void {
    localStorage.removeItem(this.storageKey);
  }
}

export default new AuthService();
