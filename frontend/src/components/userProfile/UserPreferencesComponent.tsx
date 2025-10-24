import { useEffect, useState, type ChangeEvent } from "react";
import { useAuth } from "../../context/AuthContext";
import { useGeolocation } from "../../hooks/GeoLocation";

interface LocationAndPreferencesData {
  location: string | null;
  preferredAgeMin: number | null;
  preferredAgeMax: number | null;
  maxPreferredDistance: number | null;
  latitude : number | null,
  longitude : number | null
}

interface LocationAndPreferencesProps {
  onDataChange?: (data: LocationAndPreferencesData) => void;
}

const LocationAndPreferences: React.FC<LocationAndPreferencesProps> = ({ onDataChange }) => {
  const { loggedInUserData, setLoggedInUserData } = useAuth();
  const [locationOption, setLocationOption] = useState("")
  const { latitude, longitude } = useGeolocation();
  

  const [userPrefs, setUserPrefs] = useState<LocationAndPreferencesData>({
    location: loggedInUserData?.location ?? "",
    preferredAgeMin: loggedInUserData?.preferredAgeMin ?? 18,
    preferredAgeMax: loggedInUserData?.preferredAgeMax ?? 100,
    maxPreferredDistance: loggedInUserData?.maxPreferredDistance ?? 50,
    latitude : loggedInUserData?.latitude? loggedInUserData.latitude :  null,
    longitude : loggedInUserData?.longitude? loggedInUserData.longitude :  null
  });

  useEffect(() => {
    if (latitude && longitude) {
      setUserPrefs((prev) => ({
        ...prev,
        latitude,
        longitude,
      }));

      setLoggedInUserData((prev: any) => ({
        ...prev,
        latitude,
        longitude,
      }));
    }
  }, [latitude, longitude, setLoggedInUserData]);

  useEffect(() => {
    if (onDataChange) onDataChange(userPrefs);
  }, [userPrefs, onDataChange]);

  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    const newValue =
      e.target.type === "number" ? Number(value) : value;

    setUserPrefs((prev) => ({ ...prev, [name]: newValue }));

    setLoggedInUserData((prev: any) => ({
      ...prev,
      [name]: newValue,
    }));
  };

  return (
    <div className="preferences-section">
      <div>
        <div className="sector">Location</div>
        <br />
        <input
          name="location"
          type="text"
          value={userPrefs.location ?? ""}
          placeholder="Tallinn, Estonia"
          onChange={handleChange}
        />
        <br />
        <br />
        <div>
          <button>Get location from browser</button>
          <br />
          <br />
          <button>Choose my location</button>
        </div>
        <br />
      </div>

      <div className="preffered">
        <div className="sector">Preferred Age</div>
        <div className="ageInputs">
          <div>
            <div>Minimum</div>
            <input
              step={1}
              min={18}
              max={97}
              type="number"
              name="preferredAgeMin"
              value={userPrefs.preferredAgeMin ?? 18}
              onChange={handleChange}
            />
          </div>
          <div>
            <div>Maximum</div>
            <input
              step={1}
              min={20}
              max={100}
              type="number"
              name="preferredAgeMax"
              value={userPrefs.preferredAgeMax ?? 100}
              onChange={handleChange}
            />
          </div>
        </div>
      </div>

      <div className="preffered">
        <div className="sector">Preferred distance from you (km)</div>
        <input
          className="distance"
          type="number"
          name="maxPreferredDistance"
          min={5}
          max={200}
          value={userPrefs.maxPreferredDistance ?? 50}
          onChange={handleChange}
        />
      </div>
    </div>
  );
};

export default LocationAndPreferences;