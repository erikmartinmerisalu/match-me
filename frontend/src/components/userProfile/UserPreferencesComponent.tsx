import { useEffect, useState, type ChangeEvent } from "react";
import { useAuth } from "../../context/AuthContext";
import { useGeolocation } from "../../hooks/GeoLocation";
import { locationSearchService } from "../../services/locationSearch";
import { toast } from "react-toastify";
import type { LocationAndPreferencesData, LocationAndPreferencesProps, LocationSuggestion } from "../../types/UserProfileTypes";



const LocationAndPreferences: React.FC<LocationAndPreferencesProps> = ({ onDataChange }) => {
  const { loggedInUserData, setLoggedInUserData } = useAuth();
  const { latitude, longitude } = useGeolocation();
  const [suggestions, setSuggestions] = useState<LocationSuggestion[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  

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

  
  const handleChange = async (e: ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;

    setUserPrefs((prev) => ({ ...prev, [name]: value }));
    setLoggedInUserData((prev: any) => ({ ...prev, [name]: value }));

    if (name === "location") {
      if (value.length >= 3) {
        setIsLoading(true);
        try {
           const res = await locationSearchService.searchLOcation(encodeURIComponent(value))
           if(res.err){
            toast.error("Location not found!")
           }
          setSuggestions(res.slice(0, 5));
        } catch (err) {
          console.error("Error fetching locations:", err);
        } finally {
          setIsLoading(false);
        }
      } else {
        setSuggestions([]);
      }
    }
  };

  const handleSelectSuggestion = (suggestion: LocationSuggestion) => {
    const locationString = `${suggestion.city}, ${suggestion.country}`;
    setUserPrefs((prev) => ({
      ...prev,
      location: locationString,
      latitude: suggestion.latitude,
      longitude: suggestion.longitude,
    }));
    setLoggedInUserData((prev: any) => ({
      ...prev,
      location: locationString,
      latitude: suggestion.latitude,
      longitude: suggestion.longitude,
    }));
    setSuggestions([]);
  };

  return (
    <div className="preferences-section">
      <div>
        <div className="sector">Location</div>
        <br />
        <div className="autocomplete-wrapper">
    <input
      name="location"
      type="text"
      value={userPrefs.location ?? ""}
      placeholder="Type to search..."
      onChange={handleChange}
      autoComplete="off"
    />

    {isLoading && <div className="absolute mt-1 bg-white p-1 rounded shadow">Loading...</div>}

    {suggestions.length > 0 && (
      <>
      <div className="results-list">
        {suggestions.map(suggestion => <div key={suggestion.city} onClick={() => handleSelectSuggestion(suggestion)}>{suggestion.country + "," + suggestion.city}</div>)}
      </div>

      </>

    )}
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
              defaultValue={userPrefs.preferredAgeMax ?? 100}
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
          defaultValue={userPrefs.maxPreferredDistance ?? 100}
          value={userPrefs.maxPreferredDistance ?? 100}
          onChange={handleChange}
        />
      </div>
    </div>
  );
};

export default LocationAndPreferences;