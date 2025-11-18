import { useState, type ChangeEvent } from "react";
import { useAuth } from "../../context/AuthContext";
import { useToast } from "../../context/ToastContext";

type ProfilePicProps = {
  width: number;
  height: number;
};

const ProfilePicChange: React.FC<ProfilePicProps> = ({ width, height }) => {
  const { loggedInUserData, setLoggedInUserData } = useAuth();
  const [base64, setBase64] = useState<string>();
  const toast = useToast();

  const handleProfilePicUpload = async (e: ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      const file = e.target.files[0];
      const formData = new FormData();
        formData.append("file", file);
      const reader = new FileReader();
      reader.onloadend = () => {
        setBase64(reader.result as string);
      };
    reader.readAsDataURL(file);

      try {
        const res = await fetch("http://localhost:8080/api/images/upload", {
        method: "POST",
        body: formData,
        credentials: "include",
        });
        if (!res.ok) {  
          toast.error ("Failed to upload picture")      
          return;
      }

      const data = await res.json(); // { profilePic: "/uploads/1.png" }

      // Lisa cache-busting query string, et brauser ei kuvaks vana pilti
      const fullUrl = data.profilePic ? `http://localhost:8080${data.profilePic}?t=${Date.now()}` : null;

      setLoggedInUserData((prev: any) => ({
        ...prev,
        profilePic: fullUrl,
      }));


    } catch (err) {
      toast.error ("Failed to upload picture")
    }
      }
    }
  
    const handleRemovePic = async () => {
      setBase64("")
  try {
    const res = await fetch("http://localhost:8080/api/images/remove", {
      method: "POST",
      credentials: "include",
    });

    if (!res.ok) {
      toast.error ( "Failed to remove picture")
      return;
    }

    setLoggedInUserData((prev: any) => ({
      ...prev,
      profilePic: null,
    }));

  } catch (err) {
    console.error("Error removing profile picture:", err);
  }
};

  

  return (
    <div className="profile-pic">
      {loggedInUserData?.profilePic? (
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