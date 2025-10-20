import type { ChangeEvent } from 'react';
import type { UserBioProps, UserFormData } from '../../types/UserProfileTypes';
import { useAuth } from '../../context/AuthContext';



function UserBioComponent () {
    const today = new Date();
    const yyyy = today.getFullYear();
    const mm = String(today.getMonth() + 1).padStart(2, "0");
    const dd = String(today.getDate()).padStart(2, "0");
    const default18 = `${yyyy - 18}-${mm}-${dd}`;
    const {loggedInUserData, setLoggedInUserData} = useAuth();
    

    const handleChange = (
      e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
    ) => {
      const { name, value } = e.target;
      setLoggedInUserData((prev : UserFormData) => ({ ...prev, [name]: value }));
    };

  return (
    <div>
        <div>
          <div>Username</div> 
          <input
            type="text"
            name="displayName"
            value={loggedInUserData?.displayName || ""}
            onChange={(e) => handleChange(e)}
            required
            pattern="^[a-zA-Z0-9_]+$"
            title="Username must be 3-20 characters and only letters, numbers, or underscores"
          />
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
          <input type="date" name="birthdate" min="1900-01-01" max={default18} value={loggedInUserData?.birthdate || default18} onChange={handleChange}/>
        </div>
    </div>
  )
}

export default UserBioComponent