// src/services/userService.ts
const API_BASE_URL = "http://localhost:8080";

// Import the type
import type { UserFormData } from "../types/UserProfileTypes";

export const userService = {
  async updateProfile(payload: any) {
    try {
      const res = await fetch(`${API_BASE_URL}/api/users/me/profile`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify(payload),
      });

      if (!res.ok) {
        let errorData;
        try {
          errorData = await res.json();
        } catch {
          errorData = { message: `HTTP ${res.status}: ${res.statusText}` };
        }
        
        throw new Error(errorData.error || errorData.message || 'Failed to update profile');
      }

      const data = await res.json();
      return data;
    } catch (err) {
      throw err;
    }
  },

  // KEEP ALL OTHER METHODS EXACTLY THE SAME
  async getUser(): Promise<UserFormData | null> {
    try {
      const res = await fetch(`${API_BASE_URL}/api/users/me`, {
        method: "GET",
        credentials: "include",
      });
      if (!res.ok) {
        return null;
      }
      const data: UserFormData = await res.json();
      return data;
    } catch {
      return null;
    }
  },

  async getUserProfile(): Promise<UserFormData | null> {
    try {
      const res = await fetch(`${API_BASE_URL}/api/users/me/profile`, {
        method: "GET",
        credentials: "include",
      });
      if (!res.ok) {
        return null;
      }
      const data: UserFormData = await res.json();
      return data;
    } catch {
      return null;
    }
  },

  async getUserBio(): Promise<UserFormData | null> {
    try {
      const res = await fetch(`${API_BASE_URL}/api/users/me/bio`, {
        method: "GET",
        credentials: "include",
      });
      if (!res.ok) {
        return null;
      }
      const data: UserFormData = await res.json();
      return data;
    } catch {
      return null;
    }
  }
};