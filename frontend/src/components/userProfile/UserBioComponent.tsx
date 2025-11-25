import { type ChangeEvent } from 'react';
import { useAuth } from '../../context/AuthContext';
import type { UserBioProps } from '../../types/UserProfileTypes';

function UserBioComponent({ onDataChange }: UserBioProps) {
    const today = new Date();
    const yyyy = today.getFullYear();
    const mm = String(today.getMonth() + 1).padStart(2, "0");
    const dd = String(today.getDate()).padStart(2, "0");
    const default18 = `${yyyy - 18}-${mm}-${dd}`;
    const { loggedInUserData, setLoggedInUserData } = useAuth();

    const handleChange = (e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value } = e.target;
        
        // Add character limit for displayName
        if (name === 'displayName' && value.length > 15) {
            return; // Don't update if exceeds 15 characters
        }
        
        setLoggedInUserData((prev: any) => ({
            ...prev,
            [name]: value,
        }));
    };

    return (
        <div>
            <div>
                <div>Username</div>
                <input
                    type="text"
                    name="displayName"
                    value={loggedInUserData?.displayName || ""}
                    onChange={handleChange}
                    required
                    maxLength={15} // Add maxLength attribute
                    pattern="^[a-zA-Z0-9_]{3,15}$" // Update pattern to match 3-15 chars
                    title="Username must be 3-15 characters and only letters, numbers, or underscores"
                />
                <div style={{ fontSize: '12px', color: '#666', marginTop: '4px' }}>
                    {loggedInUserData?.displayName?.length || 0}/15 characters
                </div>
            </div>

            <div>
                <div className="sector">About me</div>
                <textarea
                    name="aboutMe"
                    value={loggedInUserData?.aboutMe || ""}
                    onChange={handleChange}
                    maxLength={250}
                    placeholder="Tell other gamers about yourself..."
                />
            </div>

            <div>
                <div className="sector">Looking for</div>
                <textarea
                    name="lookingFor"
                    value={loggedInUserData?.lookingFor || ""}
                    onChange={handleChange}
                    maxLength={250}
                    placeholder="Tell others what are you looking for.."
                />
            </div>

            <div>
                <div className="sector">Age</div>
                <input 
                    type="date" 
                    name="birthDate" 
                    min="1900-01-01" 
                    max={default18} 
                    value={loggedInUserData?.birthDate}  
                    onChange={handleChange}
                />
            </div>
        </div>
    )
}

export default UserBioComponent;