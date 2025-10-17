import React, { useState, type ChangeEvent } from 'react'
import ProfilePic from '../../components/profilepic/ProfilePic'
import "./userprofile.css"
import UserBioComponent from '../../components/userProfile/userBioComponent';
import { Bounce, toast, ToastContainer } from 'react-toastify';

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
            location=''
            birthdate=''
            handleChange={handleSubmit}
            />
          }
          
        </form>
      </div>
      <button onClick={() => nextCardState(cardState)}>Save</button>

      <ToastContainer
        position="top-right"
        autoClose={5000}
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick={false}
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
        theme="light"
        transition={Bounce}
        />
    </div>
  )
}

export default UserProfile