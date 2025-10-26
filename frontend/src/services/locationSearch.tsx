import type { UserFormData } from "../types/UserProfileTypes";

// src/services/userService.ts
const API_BASE_URL = "http://localhost:8080";

export const locationSearchService = {
  async searchLOcation(payload: any) {
    console.log("it started", payload)
    try {
      const res = await fetch(`${API_BASE_URL}/api/search?query=${payload}`, {
        method: "GET",
        credentials: "include",
      });

      if (!res.ok) {
        console.log("failed")
        const errorData = await res.json();
        return errorData;
      }
      const data = await res.json();
      return data;
    } catch (err) {
      console.error("UpdateProfile failed:", err);
      throw err;
    }
  },


};

