import type { ChangeEvent } from "react";

type ProfilePicProps = {
  src: string | null;
  onUpload?: (e : ChangeEvent<HTMLInputElement> ) => void;
  onRemove?: () => void ;
  width?: number;  // px
  height?: number; // px
};

const ProfilePic: React.FC<ProfilePicProps> = ({
  src,
  onUpload,
  onRemove,
  width = 150,
  height = 150,
}) => {
  return (
    <div className="profile-pic">
      {src ? (
        <img
          src={src}
          alt="Profile"
          style={{ width: `${width}px`, height: `${height}px`, objectFit: "cover", borderRadius: "50%" }}
        />
      ) : (
        <span className="placeholder" style={{ display: "inline-block", width, height, lineHeight: `${height}px`, textAlign: "center", borderRadius: "50%" }}>👤</span>
      )}
      <div className="pic-actions" style={{ marginTop: "10px" }}>
        <input type="file"   accept="image/png, image/jpeg, image/jpg, image/webp" onChange={onUpload}   />
        {src && <button type="button" onClick={onRemove} style={{ marginLeft: "5px" }}>Remove</button>}
      </div>
    </div>
  );
};

export default ProfilePic;
