import { useContext, type ChangeEvent } from "react";
import { AuthContext } from "../../context/AuthContext";
import type { UserFormData } from "../../types/UserProfileTypes";

type ProfilePicProps = {
  width: number;  // px
  height: number; // px
};

const ProfilePicChange: React.FC<ProfilePicProps> = ({
  width,
  height,
}) => {
  
  const {loggedInUserData, setLoggedInUserData} = useContext(AuthContext);

  const handleProfilePicUpload = (e: ChangeEvent<HTMLInputElement>) => {
    
    if (e.target.files && e.target.files[0]) {
      const file = e.target.files[0];
      const reader = new FileReader();
      reader.onload = (event) => {
        if (event.target) {
          const base64 = event.target.result as string;
          console.log(base64)
          setLoggedInUserData((prev: UserFormData) => ({
            ...prev,
            profilePic : base64,
          }))
          console.log(base64);
        }
      };
      reader.readAsDataURL(file);
    }
    console.log(loggedInUserData?.profilePic)
  };

  const handleRemovePic = () => {
    // setProfilePic(null);
    setLoggedInUserData((prev: UserFormData) => ({
      ...prev,
      profilePic : null,
    }))
  };

  return (
    <div className="profile-pic">
      {loggedInUserData?.profilePic ? (
        <img
          src={loggedInUserData?.profilePic}
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
        <input type="file"   accept="image/png, image/jpeg, image/jpg, image/webp" onChange={handleProfilePicUpload}   />
        {loggedInUserData?.profilePic && <button type="button" onClick={() => handleRemovePic()} style={{ marginLeft: "5px" }}>Remove</button>}
      </div>
    </div>
  );
};

export default ProfilePicChange;
