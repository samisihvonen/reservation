// ============================================
// 1. FIXED API SERVICE WITH TYPES
// ============================================
// File: src/services/api.ts

import type { AuthResponse, ErrorResponse } from "../types/auth";
import type { Reservation } from "../types/reservation";

const API_BASE = "http://localhost:8080/api";

class ApiService {
  private getHeaders(token?: string): HeadersInit {
    const headers: HeadersInit = {
      "Content-Type": "application/json",
    };
    if (token) {
      headers["Authorization"] = `Bearer ${token}`;
    }
    return headers;
  }

  private async handleResponse<T>(response: Response): Promise<T> {
    if (!response.ok) {
      const error: ErrorResponse = await response.json();
      throw new Error(error.message || "API request failed");
    }
    return response.json() as Promise<T>;
  }

  // Auth endpoints
  async register(
    email: string,
    displayName: string,
    password: string
  ): Promise<AuthResponse> {
    const response = await fetch(`${API_BASE}/auth/register`, {
      method: "POST",
      headers: this.getHeaders(),
      body: JSON.stringify({ email, displayName, password }),
    });
    return this.handleResponse<AuthResponse>(response);
  }

  async login(email: string, password: string): Promise<AuthResponse> {
    const response = await fetch(`${API_BASE}/auth/login`, {
      method: "POST",
      headers: this.getHeaders(),
      body: JSON.stringify({ email, password }),
    });
    return this.handleResponse<AuthResponse>(response);
  }

  // Reservation endpoints
  async getReservationsByRoom(
    roomId: string,
    token: string
  ): Promise<Reservation[]> {
    const response = await fetch(`${API_BASE}/reservations/${roomId}`, {
      headers: this.getHeaders(token),
    });
    return this.handleResponse<Reservation[]>(response);
  }

  async createReservation(
    roomId: string,
    startTime: string,
    endTime: string,
    user: string,
    token: string
  ): Promise<Reservation> {
    const response = await fetch(`${API_BASE}/reservations`, {
      method: "POST",
      headers: this.getHeaders(token),
      body: JSON.stringify({ roomId, startTime, endTime, user }),
    });
    return this.handleResponse<Reservation>(response);
  }

  async deleteReservation(id: string, token: string): Promise<void> {
    const response = await fetch(`${API_BASE}/reservations/${id}`, {
      method: "DELETE",
      headers: this.getHeaders(token),
    });
    if (!response.ok) {
      const error: ErrorResponse = await response.json();
      throw new Error(error.message || "Failed to delete reservation");
    }
  }
}

export default new ApiService();
