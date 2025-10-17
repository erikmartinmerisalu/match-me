import type { UserBioProps } from '../../types/UserProfileTypes';



function UserBioComponent ( {userName , about , lookingfor, location, birthdate, handleChange } : UserBioProps) {
    const today = new Date();
    const yyyy = today.getFullYear();
    const mm = String(today.getMonth() + 1).padStart(2, "0");
    const dd = String(today.getDate()).padStart(2, "0");
    const default18 = `${yyyy - 18}-${mm}-${dd}`;

  return (
    <div>
        <div>
          <div>Username</div> 
          <input
            type="text"
            name="username"
            value={userName}
            onChange={handleChange}
            required
            pattern="^[a-zA-Z0-9_]+$"
            title="Username must be 3-20 characters and only letters, numbers, or underscores"
          />
        </div>

        <div>
          <div className="sector">About me</div>
          <textarea
            name="about"
            value={about}
            onChange={handleChange}
            maxLength={250}
            placeholder="Tell other gamers about yourself..."
          />
        </div>

        <div>
          <div className="sector">Looking for</div>
          <textarea
            name="lookingfor"
            value={lookingfor}
            onChange={handleChange}
            maxLength={250}
            placeholder="Tell others what are you looking for.."
          />
        </div>

        <div>
          <div className="sector">Location</div>
          <input name="location" type="text" value={location} placeholder="Tallinn, Estonia" onChange={handleChange}/>
        </div>

        <div>
          <div className="sector">Age</div>
          <input type="date" name="birthdate" min="1900-01-01" max={default18} value={birthdate || default18} onChange={handleChange}/>
        </div>
    </div>
  )
}

export default UserBioComponent