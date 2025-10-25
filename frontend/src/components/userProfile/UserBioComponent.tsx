import { useEffect, useState, type ChangeEvent } from 'react';
import { useAuth } from '../../context/AuthContext';
import type { UserBioData, UserBioProps } from '../../types/UserBioComponentTypes';


function UserBioComponent ({onDataChange} : UserBioProps ) {
    const today = new Date();
    const yyyy = today.getFullYear();
    const mm = String(today.getMonth() + 1).padStart(2, "0");
    const dd = String(today.getDate()).padStart(2, "0");
    const default18 = `${yyyy - 18}-${mm}-${dd}`;
    const {loggedInUserData, setLoggedInUserData} = useAuth();
    const [userData, setUserData] = useState<UserBioData>({
      displayName : loggedInUserData?.displayName || null,
      aboutMe : loggedInUserData?.aboutMe || null,
      lookingFor : loggedInUserData?.lookingFor || null,
      birthDate : loggedInUserData?.birthDate || null
    });    

    useEffect(() => {
      if (loggedInUserData) {
        setUserData({
          displayName: loggedInUserData.displayName || "",
          aboutMe: loggedInUserData.aboutMe || "",
          lookingFor: loggedInUserData.lookingFor || "",
          birthDate: loggedInUserData.birthDate || default18,
        });
      }
    }, [loggedInUserData]);

    const handleChange = (e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
      const { name, value } = e.target;
      setUserData((prev) => ({ ...prev, [name]: value }));

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
            value={userData.displayName ?? loggedInUserData?.displayName ?? ""}
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
            value={userData.aboutMe ?? loggedInUserData?.aboutMe ?? ""}
            onChange={handleChange}
            maxLength={250}
            placeholder="Tell other gamers about yourself..."
          />
        </div>

        <div>
          <div className="sector">Looking for</div>
          <textarea
            name="lookingFor"
            value={userData.lookingFor ?? loggedInUserData?.lookingFor ?? ""}
            onChange={handleChange}
            maxLength={250}
            placeholder="Tell others what are you looking for.."
          />
        </div>

        <div>
          <div className="sector">Age</div>
          <input type="date" name="birthDate" min="1900-01-01" max={default18} value={loggedInUserData?.birthDate? loggedInUserData.birthDate : ""}  onChange={handleChange}/>
        </div>
    </div>
  )
}

export default UserBioComponent