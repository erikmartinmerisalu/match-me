// src/services/userService.ts
const API_BASE_URL = "http://localhost:8080/api/users";

export const userService = {
  async updateProfile(payload: any) {
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
        return "Failed to save profile";
      }

      const data = await res.json();
      return data;
    } catch (err) {
      console.error("UpdateProfile failed:", err);
      throw err;
    }
  },

  async getUserProfile(){
    try{
      await fetch(`${API_BASE_URL}/me/profile`, {
        method : "GET",
        credentials : "include",
      })
      .then((res) => {  
        if(!res.ok){
        return;
        }
        const data = await res.json();
        console.log("this is response" ,res)
        console.log("this is data", data)
        console.log(data.displayName)
        console.log("yess")
        return res;
      })
      
    } catch {
      return;
    }
  } 
};

