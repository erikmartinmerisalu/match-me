import  { useState } from "react";
import  {  ChangeEvent } from "react";

import "./userprofile.css";
import ProfilePic from "../../components/profilepic/ProfilePic";

const UserProfile: React.FC = () => {
  const serverOptions = ["N-America", "S-America", "EU East", "EU West", "Asia", "AU+SEA", "Africa+Middle east"]
  const gameOptions = ["Game1", "Game2", "Game3", "Game4", "Game5"]
  const gameExpLvl = ["Beginner", "Intermediate", "Advanced"]
  const gaminghours = ["<100", "101-500", "501-1000", "1000+"]
  const [profilePic, setProfilePic] = useState<string | null>(null);
  const [servers, setServers] = useState<string[]>([]);
  const [games, setGames] = useState<string[]>([]);
  const [experience, SetExperience] = useState<string[]>([]);
  const [hours, setHours] = useState<string[]>([]);
  const [error, setError] = useState<string>("");

  const [base64String, setBase64String] = useState<string | null>(null);


  const [formData, setFormData] = useState({
    username: "",
    about: "",
    birthdate: "",
    lookingfor: "",
    games: {},
    maxPreferredDistance: "",
    timezone: "Europe/Tallinn",
    lookingFor: "",
    preferredAgeMin: "",
    preferredAgeMax: "",
    profilePic : base64String
  });

  const toggleServer = (server : string, index : number) => {
    setServers((prev) => prev.includes(server) ? 
  prev.filter((p) => p !== server) : [...prev, server])
  }

  const toggleGameOption = (game : string) => {
    setGames((prev) => prev.includes(game) ? 
  prev.filter((p) => p !== game) : [...prev, game])
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

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // Prepare the payload for api
    const payload = {
    displayName: formData.username,
    aboutMe: formData.about,
    birthDate: "1995-06-15",
    timezone: "Europe/Tallinn",
    lookingFor: formData.lookingfor,
    preferredAgeMin: 20,
    preferredAgeMax: 35,
    games: {
      Game1: {
        expLvl: "Intermediate",
        gamingHours: "101-500",
        preferredServers: ["EU East", "Asia"]
      },
      Game2: {
        expLvl: "Intermediate",
        gamingHours: "101-500",
        preferredServers: ["EU East"]
      }
    },
    maxPreferredDistance: 50
  }

    try {
      const res = await fetch("http://localhost:8080/api/users/me/profile", {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        //send cookies, JWT
        credentials: "include", 
        body: JSON.stringify(payload),
      });

      // if (!res.ok) {

      //   setError("Failed to save profile");
      // }

      const data = await res.json();
      console.log("Saved profile:", data);
    } catch (err) {
      // setError(err)
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
          />
        </div>

        <div>
          <div className="sector">About me</div>
          <textarea
            name="about"
            value={formData.about}
            onChange={handleChange}
            maxLength={500}
            placeholder="Tell other gamers about yourself..."
          />
        </div>

        <div>
          <div className="sector">Looking for</div>
          <textarea
            name="about"
            value={formData.lookingfor}
            onChange={handleChange}
            maxLength={100}
            placeholder="Tell others what are you looking for.."
          />
        </div>

        <div>
          <div className="sector">Age</div>
          <input type="date" value={formData.birthdate} onChange={handleChange}/>
        </div>

        <div>
          <div className="sector">Games you play</div>
          <div className="optionsmap">
          {gameOptions.map(game => <div key={game} 
              className={`options ${
              games.includes(game) ? "selected" : ""
            }`} > <div onClick={() => toggleGameOption(game)}>{game} </div> </div>)}
          </div>
        </div>
        
        {/* We map the games and the content inside with functions */}
        {games.length === 0 ? "" : <div className="games">
          <div className="sector">Give us more information about your game experience</div>
          <div>
            {games.map(game => <div key={game} className="gamesector">
              <div className="gamename">{game}</div>
              <div className="gamedata"> Game experience  </div>
              <select>{gameExpLvl.map(lvl => <option key={lvl}>{lvl}</option>)}</select>
              <div className="gamedata"> Played hours</div>
              <select>{gaminghours.map(hour => <option key={hour}>{hour}</option>)}</select>
              <div className="gamedata">Servers I play in</div>
              <div className="optionsmap">{serverOptions.map((server, index) => <div key={server} onClick={(index) => toggleServer} className={`options ${
              servers.includes(game) ? "selected" : ""
            }`}>{server}</div>)}</div>
              </div>)}
          </div>
        </div>}



        <div className="preffered">
          <div className="sector">Preferred age</div>
            <select></select>
        </div>


        <div className="preffered">
          <div className="sector">Preferred distance from you</div>
        </div>


        <button type="submit" className="save-btn">
          Save Profile
        </button>
      </form>
      <div>{error}</div>
    </div>
  );
};

export default UserProfile;
