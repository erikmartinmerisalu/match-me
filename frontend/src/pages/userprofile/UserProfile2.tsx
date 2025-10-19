import  { useEffect, useState, type ChangeEvent } from "react";
import {  type FormData } from "../../types/UserProfileTypes";
import "./userprofile.css";
import ProfilePic from "../../components/profilepic/ProfilePic";
import { useGeolocation } from "../../hooks/GeoLocation";
import { userService } from "../../service/userService";


const UserProfile2: React.FC = () => {
  const serverOptions = ["N-America", "S-America", "EU East", "EU West", "Asia", "AU+SEA", "Africa+Middle east"]
  const gameOptions = ["Game1", "Game2", "Game3", "Game4", "Game5"]
  const gameExpLvl = ["Beginner", "Intermediate", "Advanced"]
  const gaminghours = ["<100", "101-500", "501-1000", "1000+"]
  const competitivenessOptions = ["Just for fun", "Casual", "Semi-competitive", "Highly competitive"]
  const voiceChatOptions = ["Always", "Sometimes", "Rarely", "Never"]
  const playScheduleOptions = ["Weekday mornings", "Weekday evenings", "Weekend mornings", "Weekend evenings", "Late nights"]
  const mainGoalOptions = ["Rank climbing", "Learning", "Making friends", "Casual fun"]
  const rankOptions = ["Unranked", "Bronze", "Silver", "Gold", "Platinum", "Diamond", "Master", "Grandmaster", "N/A"]
  const [profilePic, setProfilePic] = useState<string | null>(null);
  const [selectedGame, setSelectedGame] = useState<string []>([]);
  const [error, setError] = useState<string>("");
  const { latitude, longitude } = useGeolocation();
  // console.log(latitude)

  const today = new Date();
  const yyyy = today.getFullYear();
  const mm = String(today.getMonth() + 1).padStart(2, "0");
  const dd = String(today.getDate()).padStart(2, "0");
  const default18 = `${yyyy - 18}-${mm}-${dd}`;

  const [base64String, setBase64String] = useState<string | null >(null);

  // const [formData, setFormData] = useState< FormData>({
  //   displayName: "",
  //   aboutMe: "",
  //   birthdate: default18,
  //   lookingfor: "",
  //   games: {},
  //   maxPreferredDistance: 5,
  //   timezone: "",
  //   lookingFor: "",
  //   preferredAgeMin: 18,
  //   preferredAgeMax: 100,
  //   profilePic : "",
  //   location : "",
  //   latitude: latitude,
  //   longitude : longitude
  // });

  useEffect(() => {
  if (latitude && longitude) {
    setFormData((prev) => ({
      ...prev,
      latitude,
      longitude,
    }));
  }
}, [latitude, longitude]);


  const handleGameToggle = (game : string) => {
  // Prevent selecting more than 3 games
  if (!selectedGame.includes(game) && selectedGame.length >= 3) {
    setError("You can only select up to 3 games");
    return;
  }
  
  // Clear error when deselecting
  if (selectedGame.includes(game)) {
    setError("");
  }
  
  setSelectedGame(prev => prev.includes(game) ? prev.filter(g => g !== game) : [...prev, game]);
  setFormData((prev : any)=> {
    const updatedGames = {...prev.games}

    if(game in updatedGames){
      delete updatedGames[game];
    }else {
      updatedGames[game] = {
        expLvl: "",
        gamingHours: "",
        preferredServers: [],
        competitiveness: "",
        voiceChatPreference: "",
        playSchedule: "",
        mainGoal: "",
        currentRank: ""
      }
    }
    return {
      ...prev,
      games: updatedGames
    }
  })
}

  const toggleExpLvl = (game : any, lvl : string) => {
    console.log("its working")
    setFormData((prev : any) => {  
        if (!prev.games[game]) return prev;

        return {
          ...prev,
          games: {
            ...prev.games, [game] : {
              ...prev.games[game],
              expLvl: lvl
            }
          }
        }
    })
  }

  const toggleHours = (game : string, hours : string) => {
    console.log(formData)
    console.log("its working")
    setFormData((prev : any) => {
      if(!prev.games[game]) return prev;

      return {
        ...prev,
        games: {
          ...prev.games, [game] : {
            ...prev.games[game],
            gamingHours : hours
          }
        }
      }
    })
  }

  const togglePreferredServers = (game : string, server : string) => {
    setFormData((prev : any) => {
      if(!prev.games[game]) return prev;

      const currentServers = prev.games[game].preferredServers || [];
      const updatedServers = currentServers.includes(server) ? currentServers.filter((s : string) => s !== server ) : [...currentServers, server]

      return {
        ...prev,
        games: {
          ...prev.games, [game] : {
            ...prev.games[game],
            preferredServers : updatedServers
          }
        }
      }
    })
  }

  const toggleCompetitiveness = (game: string, value: string) => {
  setFormData((prev: any) => {
    if (!prev.games[game]) return prev;
    return {
      ...prev,
      games: {
        ...prev.games,
        [game]: { ...prev.games[game], competitiveness: value }
      }
    }
  })
}

const toggleVoiceChat = (game: string, value: string) => {
  setFormData((prev: any) => {
    if (!prev.games[game]) return prev;
    return {
      ...prev,
      games: {
        ...prev.games,
        [game]: { ...prev.games[game], voiceChatPreference: value }
      }
    }
  })
}

const togglePlaySchedule = (game: string, value: string) => {
  setFormData((prev: any) => {
    if (!prev.games[game]) return prev;
    return {
      ...prev,
      games: {
        ...prev.games,
        [game]: { ...prev.games[game], playSchedule: value }
      }
    }
  })
}

const toggleMainGoal = (game: string, value: string) => {
  setFormData((prev: any) => {
    if (!prev.games[game]) return prev;
    return {
      ...prev,
      games: {
        ...prev.games,
        [game]: { ...prev.games[game], mainGoal: value }
      }
    }
  })
}

const toggleCurrentRank = (game: string, value: string) => {
  setFormData((prev: any) => {
    if (!prev.games[game]) return prev;
    return {
      ...prev,
      games: {
        ...prev.games,
        [game]: { ...prev.games[game], currentRank: value }
      }
    }
  })
}

  const handleChange = (
    e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleProfilePicUpload = (e: ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      const file = e.target.files[0];
      const reader = new FileReader();
      reader.onload = (event) => {
        if (event.target) {
          setProfilePic(event.target.result as string);
          const base64 = event.target.result as string;
          setBase64String(base64);
          console.log(base64);
        }
      };
      reader.readAsDataURL(file);
    }
  };

  const handleRemovePic = () => {
    setProfilePic(null);
    setBase64String(null);
  };


  //INput validation and if all OK, make api call
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // Prepare the payload for api
    const payload = {
    displayName: formData.username.trim(),
    aboutMe: formData.about.trim(),
    birthDate: formData.birthdate,
    timezone: Intl.DateTimeFormat().resolvedOptions().timeZone,
    lookingFor: formData.lookingfor.trim(),
    preferredAgeMin: formData.preferredAgeMin,
    preferredAgeMax: formData.preferredAgeMax,
    games: formData.games,
    maxPreferredDistance: formData.maxPreferredDistance,
    profilePic : base64String,
    location : formData.location,
    latitude : latitude,
    longitude : longitude
  }
  console.log(payload)
  //
  if(!payload.displayName){
    return setError("Username cannot be empty!");
  }
  if(payload.displayName.length < 3 || payload.displayName.length > 20){
    return setError("Username length must be greater than 3 charaters and less than 20 charaters")
  }
  if (!/^[a-zA-Z0-9_]+$/.test(payload.displayName)){   
   return setError("Username may only contain letters, numbers and underscores");
  }
  if (payload.aboutMe.trim().length > 250){
    return setError("About section cannot exceed 250 characters");
  } 
  if (payload.lookingFor.trim().length > 250){
    return setError("Looking for section cannot exceed 250 characters");
  }
  if (payload.location.length > 100) {
    return setError("Location is too long");
  }
  if (payload.preferredAgeMin > payload.preferredAgeMax) {
    return setError("Minimum age cannot be greater than maximum age");
  }
  if (selectedGame.length === 0){
    return setError("Please select at least one game");
  }
  if (formData.maxPreferredDistance < 5 || formData.maxPreferredDistance > 200) {
  return setError("Preferred distance must be between 5 and 200 km");
  }
  if (!payload.timezone){
    payload.timezone = "UTC"
  }
  if (selectedGame.length > 0) {
    for (let game of selectedGame) {
      const g = payload.games?.[game];
      if (!g?.expLvl || !g?.gamingHours || g.preferredServers.length === 0 ||
          !g?.competitiveness || !g?.voiceChatPreference || !g?.playSchedule || 
          !g?.mainGoal || !g?.currentRank) {
        setError(`Please fill out all fields for ${game}`);
        return;
      }
    }
  }
  const birthDate = new Date(payload.birthDate);
  const age = today.getFullYear() - birthDate.getFullYear();
  if (age < 18) {
    setError("You must be at least 18 years old");
    return;
  }
  
    try {
      const res = await userService.updateProfile(payload);
    } catch (err) {
      console.error(err);
    }
  };


  return (
    <div className="profile-card">
      <h2>🎮 Gamer Profile</h2>

        <ProfilePic
        src={profilePic}
        onUpload={handleProfilePicUpload}
        onRemove={handleRemovePic}
        width={150}
        height={150}
        
      />

      <form onSubmit={handleSubmit} className="profile-form">
        <div>
          <div>Username</div> 
          <input
            type="text"
            name="username"
            value={formData.username}
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
            value={formData.about}
            onChange={handleChange}
            maxLength={250}
            placeholder="Tell other gamers about yourself..."
          />
        </div>

        <div>
          <div className="sector">Looking for</div>
          <textarea
            name="lookingfor"
            value={formData.lookingfor}
            onChange={handleChange}
            maxLength={250}
            placeholder="Tell others what are you looking for.."
          />
        </div>

        <div>
          <div className="sector">Location</div>
          <input name="location" type="text" value={formData.location} placeholder="Tallinn, Estonia" onChange={handleChange}/>
        </div>

        <div>
          <div className="sector">Age</div>
          <input type="date" name="birthdate" min="1900-01-01" max={default18} value={formData.birthdate || default18} onChange={handleChange}/>
        </div>

        <div>
          <div className="sector">Games you play</div>
          <div className="optionsmap">
          {gameOptions.map(game => <div key={game} 
              className={`options ${
              selectedGame.includes(game) ? "selected" : ""
            }`} > <div onClick={() =>  handleGameToggle(game)}>{game} </div> </div>)}
          </div>
        </div>
        
        {/* We map the games and the content inside with functions */}
        {selectedGame.length === 0 ? "" : <div className="games">
          <div className="sector">Give us more information about your game experience</div>
          <div>
            {selectedGame.map((game) => <div key={game} className="gamesector">
              <div className="gamename">{game}</div>
      
              <div className="gamedata">Game experience</div>
              <select onChange={(e) => toggleExpLvl(game, e.target.value)} value={formData.games?.[game]?.expLvl || ""}>
                <option value="">Select...</option>
                {gameExpLvl.map(lvl => <option value={lvl} key={lvl}>{lvl}</option>)}
              </select>
      
              <div className="gamedata">Played hours</div>
              <select onChange={(e) => toggleHours(game, e.target.value)} value={formData.games?.[game]?.gamingHours || ""}>
                <option value="">Select...</option>
                {gaminghours.map(hour => <option value={hour} key={hour}>{hour}</option>)}
              </select>
      
              <div className="gamedata">Servers I play in</div>
              <div className="optionsmap">{serverOptions.map((server) => 
                <div key={server} onClick={() => togglePreferredServers(game, server)} className={`options ${
                  formData.games?.[game]?.preferredServers.includes(server) ? "selected" : ""
                }`}>{server}</div>)}
              </div>

              <div className="gamedata">Competitiveness</div>
              <select onChange={(e) => toggleCompetitiveness(game, e.target.value)} value={formData.games?.[game]?.competitiveness || ""}>
                <option value="">Select...</option>
                {competitivenessOptions.map(opt => <option value={opt} key={opt}>{opt}</option>)}
              </select>
      
              <div className="gamedata">Voice chat preference</div>
              <select onChange={(e) => toggleVoiceChat(game, e.target.value)} value={formData.games?.[game]?.voiceChatPreference || ""}>
                <option value="">Select...</option>
                {voiceChatOptions.map(opt => <option value={opt} key={opt}>{opt}</option>)}
              </select>
      
              <div className="gamedata">Play schedule</div>
              <select onChange={(e) => togglePlaySchedule(game, e.target.value)} value={formData.games?.[game]?.playSchedule || ""}>
                <option value="">Select...</option>
                {playScheduleOptions.map(opt => <option value={opt} key={opt}>{opt}</option>)}
              </select>
      
              <div className="gamedata">Main goal</div>
              <select onChange={(e) => toggleMainGoal(game, e.target.value)} value={formData.games?.[game]?.mainGoal || ""}>
                <option value="">Select...</option>
                {mainGoalOptions.map(opt => <option value={opt} key={opt}>{opt}</option>)}
              </select>
      
              <div className="gamedata">Current rank</div>
              <select onChange={(e) => toggleCurrentRank(game, e.target.value)} value={formData.games?.[game]?.currentRank || ""}>
                <option value="">Select...</option>
                {rankOptions.map(opt => <option value={opt} key={opt}>{opt}</option>)}
              </select>

            </div>)}
          </div>
        </div>}



        <div className="preffered">
          <div className="sector">Preferred age</div>
          <div className="ageInputs">
            <div>
              <div>Minimum</div>
                <input step={1} min={18} max={97} type="number" defaultValue={18} value={formData.preferredAgeMin} onChange={(e) =>
                setFormData(prev => ({
                  ...prev,
                  preferredAgeMin: Number(e.target.value),
                }))}  />
            </div>
            <div>
              <div>Maximum</div>
                <input step={1} min={20} max={100} type="number" defaultValue={100} value={formData.preferredAgeMax} onChange={(e) =>
                setFormData(prev => ({
                  ...prev,
                  preferredAgeMax: Number(e.target.value),
                }))} />
            </div>
          </div>
        </div>

        <div className="preffered">
          <div className="sector">Preferred distance from you (km)</div>
            <input className="distance" type="number" min={5} max={200} defaultValue={50} />
        </div>


        <button type="submit" className="save-btn">
          Save Profile
        </button>
      </form>
      <div>{error}</div>
    </div>
  );
};

export default UserProfile2;
