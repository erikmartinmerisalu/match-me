import { type ChangeEvent } from "react";
import { useAuth } from "../../context/AuthContext";

type ProfilePicProps = {
  width: number;
  height: number;
};

const ProfilePicChange: React.FC<ProfilePicProps> = ({ width, height }) => {
  const { loggedInUserData, setLoggedInUserData } = useAuth();

  const handleProfilePicUpload = (e: ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      const file = e.target.files[0];
      const reader = new FileReader();
      reader.onload = (event) => {
        if (event.target) {
          const base64 = event.target.result as string;
          console.log("📸 Uploaded profile pic base64:", base64);
          setLoggedInUserData((prev: any) => ({
            ...prev,
            profilePic: base64,
          }));
          console.log("✅ Profile pic updated in state");
        }
      };
      reader.readAsDataURL(file);
    }
    console.log("🔍 Current profilePic in state:", loggedInUserData?.profilePic);
  };

  const handleRemovePic = () => {
    setLoggedInUserData((prev: any) => ({
      ...prev,
      profilePic: "",
    }));
    console.log("🗑️ Profile pic removed");
  };

  return (
    <div className="profile-pic">
      {loggedInUserData?.profilePic ? (
        <img
          src={loggedInUserData.profilePic}
          alt="Profile"
          style={{ width: `${width}px`, height: `${height}px`, objectFit: "cover", borderRadius: "50%" }}
        />
      ) : (
        <span className="placeholder" 
          style={{ display: "inline-block", 
          width, height, 
          lineHeight: `${height}px`, 
          textAlign: "center", 
          borderRadius: "50%" }}>
            👤
          </span>
      )}
      <div className="pic-actions" style={{ marginTop: "10px" }}>
        <input type="file" accept="image/png, image/jpeg, image/jpg, image/webp" onChange={handleProfilePicUpload} />
        {loggedInUserData?.profilePic && <button type="button" onClick={handleRemovePic} style={{ marginLeft: "5px" }}>Remove</button>}
      </div>
    </div>
  );
};

export default ProfilePicChange;