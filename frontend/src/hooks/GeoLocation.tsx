import { useState, useEffect } from "react";

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
      setCoords(prev => ({ ...prev, error: "Geolocation failed, please try again" }));
      return;
    }

    const success = (position: GeolocationPosition) => {
      setCoords({
        latitude: position.coords.latitude,
        longitude: position.coords.longitude,
        LocationError: null,
      });
    };

    const error = (err: GeolocationPositionError) => {
      setCoords(prev => ({ ...prev, LocationError: err.message }));
    };

    navigator.geolocation.getCurrentPosition(success, error);
  }, []);

  return coords;
};
