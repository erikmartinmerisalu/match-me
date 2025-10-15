import React, { useState, type ChangeEvent } from 'react'
import ProfilePic from '../../components/profilepic/ProfilePic'

function UserProfile() {
    const [profilePic, setProfilePic] = useState<string | null>(null);
    const [base64String, setBase64String] = useState<string | null >(null);
    const [toast, setToast] = useState<{message: string, type: 'success'|'failure'|'warning'} | null>(null);



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
  }
  function makeClick(){
    {toast && (
        <ToastComponent
          message={toast.message}
          type={toast.type}
          onClose={() => setToast(null)}
        />
  }
    
  return (
    <div className='profile-card'>
      <h2>🎮 Gamer Profile</h2>

        <ProfilePic
        src={profilePic}
        onUpload={handleProfilePicUpload}
        onRemove={handleRemovePic}
        width={150}
        height={150}
      />

      <form onSubmit={handleSubmit} className="profile-form">
            <div onClick={() => makeClick()}> 
                yee
            </div>
      </form>
    </div>
  )
}

export default UserProfile