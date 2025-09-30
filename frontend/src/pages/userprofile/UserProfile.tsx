import  { useState } from "react";
import  {  ChangeEvent } from "react";

import "./userprofile.css";

const UserProfile: React.FC = () => {
  const platformOptions = ["PC", "PlayStation", "Xbox", "Nintendo Switch", "Mobile"]
  const playStyleOptions = ["Casual", "Competitive", "Hardcore", "Speedrun"]
  const genreOptions = ["Action", "Action-adventure", "Adventure", "Puzzle", "Simulation", "Strategy", "Sports", "Role-playing"]
  const [profilePic, setProfilePic] = useState<string | null>(null);
  const [selectedPlatforms, setSelectedPlatforms] = useState<string[]>([]);
  const [selectedPlayStyles, setSelectedPlayStyles] = useState<string[]>([]);
  const [selectedGenres, setSelectedGenres] = useState<string[]>([]);



  const [formData, setFormData] = useState({
    username: "",
    favoriteGame: "",
    platform: [],
    playStyle: [],
    genre: [],
    about: "",
    email: "myemail@example.com", // private, only user sees
  });

  const togglePlatform = (platform : string) => {
    setSelectedPlatforms((prev) => prev.includes(platform) ? 
  prev.filter((p) => p !== platform) : [...prev, platform])
  }

  const togglePlayStyle = (style : string) => {
    setSelectedPlayStyles((prev) => prev.includes(style) ? 
  prev.filter((p) => p !== style) : [...prev, style])
  }

  const toggleGenres = (genre : string) => {
    setSelectedGenres((prev) => prev.includes(genre) ? 
  prev.filter((p) => p !== genre) : [...prev, genre])
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

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    console.log("User Profile:", formData);
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
          <div className="sector">Gaming Platform</div>
          <div className="optionsmap">
          {platformOptions.map(platform => <div key={platform} 
              className={`options ${
              selectedPlatforms.includes(platform) ? "selected" : ""
            }`} > <div onClick={() => togglePlatform(platform)}>{platform} </div> </div>)}
          </div>
        </div>

        <div>
          <div className="sector">Play Style</div>
          <div className="optionsmap">
          {playStyleOptions.map(style => <div key={style} 
              className={`options ${
              selectedPlayStyles.includes(style) ? "selected" : ""
            }`} > <div onClick={() => togglePlayStyle(style)}>{style} </div> </div>)}
          </div>
        </div>

        <div>
          <div className="sector">Favorite Genre</div>
          <div className="optionsmap">
          {genreOptions.map(genre => <div key={genre} 
              className={`options ${
              selectedGenres.includes(genre) ? "selected" : ""
            }`} > <div onClick={() => toggleGenres(genre)}>{genre} </div> </div>)}
          </div>
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

    </div>
  );
};

export default UserProfile;
