import type { UserFormData } from "../types/UserProfileTypes";

// src/services/userService.ts
const API_BASE_URL = "http://localhost:8080/api/users";

export const userService = {
  async updateProfile(payload: any) {
    console.log("it started", payload)
    try {
      const res = await fetch(`${API_BASE_URL}/me/profile`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify(payload),
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

  async getUserProfile() : Promise <UserFormData | null> {
    try{
      const res = await fetch(`${API_BASE_URL}/me/profile`, {
        method : "GET",
        credentials : "include",
      })
        if(!res.ok){
        return null;
        }
        const data : UserFormData= await res.json();
        console.log("This is userService getUserProfile data:", data)
        return data;
      
      
    } catch {
      return null;
    }
    
  } 
};

