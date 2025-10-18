import React, { useState, type ChangeEvent } from 'react'
import ProfilePic from '../../components/profilepic/ProfilePic'
import "./userprofile.css"
import { Bounce, toast, ToastContainer } from 'react-toastify';
import UserGamesComponent from '../../components/userProfile/UserGamesComponent';
import UserBioComponent from '../../components/userProfile/UserBioComponent';

function UserProfile() {
    const [profilePic, setProfilePic] = useState<string | null>(null);
    const [base64String, setBase64String] = useState<string | null >(null);
    const [cardState, setCardState] = useState<number>(0);



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

  const nextCardState = (currentCardState : number) => {
    if(cardState == 0){

      toast.error("again")
    }
  }


    
  return (
    <div>
      <div className='profile-card'>
        <h2>🎮 Gamer Profile</h2>

        { cardState == 0 &&   <ProfilePic
          src={profilePic}
          onUpload={handleProfilePicUpload}
          onRemove={handleRemovePic}
          width={150}
          height={150}
        />}

        <form onSubmit={handleSubmit} className="profile-form">
          { cardState == 0 && 
            <UserBioComponent
            userName=''
            about=''
            lookingfor=''
            birthdate=''
            handleChange={handleSubmit}
            />
          }
          { cardState == 1 &&
          <UserGamesComponent />
          }
          
        </form>
      </div>
      <button onClick={() => nextCardState(cardState)}>Save</button>


    </div>
  )
}

export default UserProfile