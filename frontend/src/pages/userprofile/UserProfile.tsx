import  { useState } from "react";
import  {  ChangeEvent } from "react";

import "./userprofile.css";

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

  const [formData, setFormData] = useState({
    username: "",
    servers: [],
    games: [],
    experience: [],
    hours: [],
    about: "",
  });

  const toggleServer = (server : string) => {
    setServers((prev) => prev.includes(server) ? 
  prev.filter((p) => p !== server) : [...prev, server])
  }

  const toggleGameOption = (game : string) => {
    setGames((prev) => prev.includes(game) ? 
  prev.filter((p) => p !== game) : [...prev, game])
  }

  const toggleLvl = (lvl : string) => {
    SetExperience((prev) => prev.includes(lvl) ? 
  prev.filter((p) => p !== lvl) : [...prev, lvl])
  }

  const toggleHours = (hours: string) => {
    setHours((prev) => prev.includes(hours) ?
  prev.filter((p) => p !== hours) : [...prev, hours])
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
        }
      };
      reader.readAsDataURL(file);
    }
  };

  const handleRemovePic = () => {
    setProfilePic(null);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // Prepare the payload for api
      const payload = {
      username: formData.username,
      servers: servers,
      games: games,
      experience: experience,
      hours: hours,
      about:formData.about
    };

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

      if (!res.ok) {
        setError("Failed to save profile");
      }

      const data = await res.json();
      console.log("Saved profile:", data);
    } catch (err) {
      setError("Failed to save profile")
      console.error(err);
    }
  };


  return (
    <div className="profile-card">
      <h2>🎮 Gamer Profile</h2>

      <div className="profile-pic">
        {profilePic ? (
          <img src={profilePic} alt="Profile" />
        ) : (
          <span className="placeholder">👤</span>
        )}
        <div className="pic-actions">
          <input type="file" accept="image/*" onChange={handleProfilePicUpload} />
          {profilePic && (
            <button type="button" onClick={handleRemovePic}>
              Remove
            </button>
          )}
        </div>
      </div>

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
          <div className="sector">Age</div>
          <select></select>
        </div>

        <div>
          <div className="sector">Preferred Servers</div>
          <div className="optionsmap">
          {serverOptions.map(server => <div key={server} 
              className={`options ${
              servers.includes(server) ? "selected" : ""
            }`} > <div onClick={() => toggleServer(server)}>{server} </div> </div>)}
          </div>
        </div>

        <div>
          <div className="sector">Games</div>
          <div className="optionsmap">
          {gameOptions.map(game => <div key={game} 
              className={`options ${
              games.includes(game) ? "selected" : ""
            }`} > <div onClick={() => toggleGameOption(game)}>{game} </div> </div>)}
          </div>
        </div>

        <div>
          <div className="sector">Game Experience</div>
          <div className="optionsmap">
          {gameExpLvl.map(lvl => <div key={lvl} 
              className={`options ${
              experience.includes(lvl) ? "selected" : ""
            }`} > <div onClick={() => toggleLvl(lvl)}>{lvl} </div> </div>)}
          </div>
        </div>

        <div>
          <div className="sector">Gaming hours</div>
          <div className="optionsmap">
          {gaminghours.map(hour => <div key={hour} 
              className={`options ${
              hours.includes(hour) ? "selected" : ""
            }`} > <div onClick={() => toggleHours(hour)}>{hour} </div> </div>)}
          </div>
        </div>

        <div>
          <div className="sector">Preferred age</div>
            <select></select>
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

        <div className="private-email">
          <strong>Your Email (Private):</strong> {formData.email}
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
