import React, { useState, type ChangeEvent } from 'react'
import ProfilePic from '../../components/profilepic/ProfilePicChange'
import "./userprofile.css"
import { Bounce, toast, ToastContainer } from 'react-toastify';
import UserGamesComponent from '../../components/userProfile/UserGamesComponent';
import UserBioComponent from '../../components/userProfile/UserBioComponent';
import ProfilePicChange from '../../components/profilepic/ProfilePicChange';
import { useAuth } from '../../context/AuthContext';
import { userService } from '../../service/userService';

function UserProfile() {
  const [cardState, setCardState] = useState<number>(0);
  const {loggedInUserData, setLoggedInUserData} = useAuth();
  

  const handleSubmit = async (e: React.FormEvent) => {
  }

   async function nextCardState(cardState : number){
    if(cardState == 0){
      const payload = {
        profilePic : loggedInUserData?.profilePic,
        userName : loggedInUserData?.aboutMe,
        lookingFor : loggedInUserData?.lookingFor,
        birthDate : loggedInUserData?.birthdate
      }
      const res = await userService.updateProfile(payload);

      if(!res.ok){
        return;
      }

      toast.error("again")
    }
    toast.success("Profile saved")
    setCardState(cardState +1)
  }


    
  return (
    <div>
      <div className='profile-card'>
        <h2>🎮 Gamer Profile</h2>

        { cardState == 0 &&   <ProfilePicChange
          width={150}
          height={150}
        />}

        <form className="profile-form">
          { cardState == 0 && 
            <UserBioComponent
            />
          }
          { cardState == 1 &&
          <UserGamesComponent />
          }
          
        </form>
      </div>
      <button onClick={() => nextCardState(cardState)}>Next</button>

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