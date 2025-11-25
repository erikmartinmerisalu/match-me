// ProfilePicShow.tsx
import React from "react";

type ProfilePicShowProps = {
  src: string | null;
  width?: number;  // px
  height?: number; // px
};

const ProfilePicShow: React.FC<ProfilePicShowProps> = ({
  src,
  width = 150,
  height = 150,
}) => {
  return (
    <div className="profile-pic-show">
      {src ? (
        <img
          src={src}
          alt="Profile"
          style={{
            width: `${width}px`,
            height: `${height}px`,
            objectFit: "cover",
            borderRadius: "50%",
          }}
        />
      ) : (
        <span
          className="placeholder"
          style={{
            display: "inline-block",
            width: `${width}px`,
            height: `${height}px`,
            lineHeight: `${height}px`,
            textAlign: "center",
            borderRadius: "50%",
            backgroundColor: "#ccc",
          }}
        >
          👤
        </span>
      )}
    </div>
  );
};

export default ProfilePicShow;
