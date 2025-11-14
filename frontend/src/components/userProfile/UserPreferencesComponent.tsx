import { useEffect, useState, type ChangeEvent } from "react";
import { useAuth } from "../../context/AuthContext";
import { useGeolocation } from "../../hooks/GeoLocation";
import { locationSearchService } from "../../services/locationSearch";
import { toast } from "react-toastify";
import type { LocationSuggestion } from "../../types/UserProfileTypes";

const UserPreferencesComponent = () => {
  const { loggedInUserData, setLoggedInUserData } = useAuth();
  const { latitude, longitude } = useGeolocation();
  const [suggestions, setSuggestions] = useState<LocationSuggestion[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const [hasSetInitialLocation, setHasSetInitialLocation] = useState(false);
  const [gpsUsed, setGpsUsed] = useState(false);
  const [isReverseGeocoding, setIsReverseGeocoding] = useState(false);

  const reverseGeocode = async (lat: number, lng: number): Promise<string> => {
    try {
      console.log(`🔄 Reverse geocoding coordinates: ${lat}, ${lng}`);
      const response = await fetch(
        `https://api.bigdatacloud.net/data/reverse-geocode-client?latitude=${lat}&longitude=${lng}&localityLanguage=en`
      );
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      const data = await response.json();
      console.log("📍 Reverse geocoding result:", data);
      
      if (data.city && data.countryName) {
        return `${data.city}, ${data.countryName}`;
      } else if (data.locality) {
        return `${data.locality}, ${data.countryName}`;
      } else {
        return "GPS location detected";
      }
    } catch (error) {
      console.error("❌ Reverse geocoding failed:", error);
      return "GPS location detected";
    }
  };

  useEffect(() => {
    const setLocationFromGPS = async () => {
      if (latitude && longitude && !hasSetInitialLocation) {
        const currentLat = loggedInUserData?.latitude;
        const currentLng = loggedInUserData?.longitude;
        
        if (currentLat !== latitude || currentLng !== longitude) {
          setIsReverseGeocoding(true);
          setGpsUsed(true);
          
          try {
            const locationName = await reverseGeocode(latitude, longitude);
            
            setLoggedInUserData((prev: any) => ({
              ...prev,
              latitude,
              longitude,
              location: locationName,
            }));
            
            setHasSetInitialLocation(true);
            console.log("✅ GPS location set:", locationName);
            toast.info(`Location set to: ${locationName}`);
          } catch (error) {
            console.error("Failed to set GPS location:", error);
            setLoggedInUserData((prev: any) => ({
              ...prev,
              latitude,
              longitude,
              location: "GPS location detected",
            }));
            toast.info("GPS location detected and set");
          } finally {
            setIsReverseGeocoding(false);
          }
        }
      }
    };

    setLocationFromGPS();
  }, [latitude, longitude, setLoggedInUserData, hasSetInitialLocation, loggedInUserData?.latitude, loggedInUserData?.longitude]);

 const handleChange = async (e: ChangeEvent<HTMLInputElement>) => {
  const { name, value } = e.target;

  // FIX: Handle empty values for number inputs
  if (name.includes("Age") || name.includes("Distance")) {
    // If value is empty, set to undefined instead of converting to 0
    const numericValue = value === '' ? undefined : Number(value);
    setLoggedInUserData((prev: any) => ({ 
      ...prev, 
      [name]: numericValue
    }));
  } else {
    setLoggedInUserData((prev: any) => ({ 
      ...prev, 
      [name]: value 
    }));
  }

    if (name === "location" && gpsUsed) {
      setGpsUsed(false);
    }

    if (name === "location") {
      if (value.length >= 3) {
        setIsLoading(true);
        setShowSuggestions(true);
        try {
          const res = await locationSearchService.searchLOcation(encodeURIComponent(value));
          if (res.err) {
            toast.error("Location not found!");
            setSuggestions([]);
          } else {
            setSuggestions(Array.isArray(res) ? res.slice(0, 5) : []);
          }
        } catch (err) {
          console.error("Error fetching locations:", err);
          setSuggestions([]);
        } finally {
          setIsLoading(false);
        }
      } else {
        setSuggestions([]);
        setShowSuggestions(false);
      }
    }
  };

  const handleSelectSuggestion = (suggestion: LocationSuggestion) => {
    const locationString = `${suggestion.city}, ${suggestion.country}`;
    setLoggedInUserData((prev: any) => ({
      ...prev,
      location: locationString,
      latitude: suggestion.latitude,
      longitude: suggestion.longitude,
    }));
    setSuggestions([]);
    setShowSuggestions(false);
    setGpsUsed(false);
  };

  const handleBlur = () => {
    setTimeout(() => {
      setShowSuggestions(false);
    }, 200);
  };

  const handleFocus = () => {
    if (suggestions.length > 0) {
      setShowSuggestions(true);
    }
  };

  return (
    <div className="preferences-section">
      <div>
        <div className="sector">Location</div>
        <br />
        
        {gpsUsed && (
          <div style={{
            padding: '8px',
            marginBottom: '10px',
            backgroundColor: '#e8f5e8',
            border: '1px solid #4caf50',
            borderRadius: '4px',
            color: '#2e7d32',
            fontSize: '14px',
            display: 'flex',
            alignItems: 'center',
            gap: '8px'
          }}>
            <span>📍</span>
            <span>Using GPS coordinates</span>
          </div>
        )}

        {isReverseGeocoding && (
          <div style={{
            padding: '8px',
            marginBottom: '10px',
            backgroundColor: '#fff3cd',
            border: '1px solid #ffc107',
            borderRadius: '4px',
            color: '#856404',
            fontSize: '14px',
            display: 'flex',
            alignItems: 'center',
            gap: '8px'
          }}>
            <span>🔄</span>
            <span>Detecting your location...</span>
          </div>
        )}
        
        <div className="autocomplete-wrapper" style={{ position: 'relative' }}>
          <input
            name="location"
            type="text"
            value={loggedInUserData?.location ?? ""}
            placeholder={isReverseGeocoding ? "Detecting your location..." : "Type to search..."}
            onChange={handleChange}
            onFocus={handleFocus}
            onBlur={handleBlur}
            autoComplete="off"
            disabled={isReverseGeocoding}
          />

          {isLoading && (
            <div style={{
              position: 'absolute',
              top: '100%',
              left: 0,
              right: 0,
              backgroundColor: 'white',
              border: '1px solid #ccc',
              borderRadius: '4px',
              padding: '8px',
              zIndex: 1000,
              marginTop: '2px',
              color: '#333',
              fontSize: '14px'
            }}>
              Searching locations...
            </div>
          )}

          {showSuggestions && suggestions.length > 0 && (
            <div style={{
              position: 'absolute',
              top: '100%',
              left: 0,
              right: 0,
              backgroundColor: 'white',
              border: '1px solid #666',
              borderRadius: '4px',
              maxHeight: '200px',
              overflowY: 'auto',
              zIndex: 1000,
              marginTop: '2px',
              boxShadow: '0 2px 8px rgba(0,0,0,0.2)'
            }}>
              {suggestions.map((suggestion, index) => (
                <div
                  key={`${suggestion.city}-${index}`}
                  onClick={() => handleSelectSuggestion(suggestion)}
                  style={{
                    padding: '10px 12px',
                    cursor: 'pointer',
                    borderBottom: '1px solid #ddd',
                    fontSize: '14px',
                    color: '#222',
                    fontWeight: '500'
                  }}
                  onMouseEnter={(e) => {
                    e.currentTarget.style.backgroundColor = '#f0f0f0';
                    e.currentTarget.style.color = '#000';
                  }}
                  onMouseLeave={(e) => {
                    e.currentTarget.style.backgroundColor = 'white';
                    e.currentTarget.style.color = '#222';
                  }}
                >
                  {suggestion.city}, {suggestion.country}
                </div>
              ))}
            </div>
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
              value={loggedInUserData?.preferredAgeMin ??  ''}
              onChange={handleChange}
               placeholder="18"
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
              value={loggedInUserData?.preferredAgeMax ??  ''}
              onChange={handleChange}
              placeholder="100" // ADD
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
          max={20000}
          value={loggedInUserData?.maxPreferredDistance ??''} //use the placeholder instead of forcing a value
          onChange={handleChange}
          placeholder="50" // Placeholder
        />
        <div style={{ fontSize: '12px', color: '#666', marginTop: '4px' }}>
          Maximum: 20,000 km (global)
        </div>
      </div>
    </div> 
  );
};

export default UserPreferencesComponent;