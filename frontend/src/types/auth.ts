export interface AuthResponse {
  token: string;
  type: string;
  id: number;
  email: string;
  displayName: string;
}
export interface ErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
}
