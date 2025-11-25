import { useState, useEffect } from "react";
import { useAuth } from "../context/AuthContext";

interface Coordinates {
  latitude: number | null;
  longitude: number | null;
  LocationError: string | null;
}

export const useGeolocation = () => {
  
  const [coords, setCoords] = useState<Coordinates>({
    latitude: null,
    longitude: null,
    LocationError: null,
  });

  useEffect(() => {
    if (!navigator.geolocation) {
      setCoords(prev => ({ ...prev, LocationError: "Geolocation failed, please try again" }));
      return;
    }

    const success = (position: GeolocationPosition) => {
      
      setCoords({
        latitude: position.coords.latitude,
        longitude: position.coords.longitude,
        LocationError: null,
        
      });
       const { latitude, longitude } = position.coords;
      console.log(`📍 User allowed GPS access. Coordinates received: ${latitude}, ${longitude}`);
    };

    const error = (err: GeolocationPositionError) => {
      setCoords(prev => ({ ...prev, LocationError: err.message }));
    };

    // ADDED: Options to prevent continuous updates
    const options = {
      maximumAge: 300000, // Cache location for 5 minutes
      timeout: 10000, // Wait max 10 seconds
      enableHighAccuracy: false // Use less battery
    };

    navigator.geolocation.getCurrentPosition(success, error, options);
  }, []);

  return coords;
};