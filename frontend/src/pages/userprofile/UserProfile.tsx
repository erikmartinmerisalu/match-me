import React, { useState, type ChangeEvent } from 'react'
import ProfilePic from '../../components/profilepic/ProfilePicChange'
import "./userprofile.css"
import { Bounce, toast, ToastContainer } from 'react-toastify';
import UserGamesComponent from '../../components/userProfile/UserGamesComponent';
import UserBioComponent from '../../components/userProfile/UserBioComponent';
import ProfilePicChange from '../../components/profilepic/ProfilePicChange';
import { useAuth } from '../../context/AuthContext';
import { userService } from '../../service/userService';
import UserGameDetails from '../../components/userProfile/UserGameDetails';

  function UserProfile() {
    const [cardState, setCardState] = useState<number>(0);
    const {loggedInUserData, setLoggedInUserData} = useAuth();
    

    const handleSubmit = async (e: React.FormEvent) => {
    }

    async function nextCardState(){
      console.log(loggedInUserData)
      if(cardState === 0){
        const payload = {
          profilePic : loggedInUserData?.profilePic,
          aboutMe : loggedInUserData?.aboutMe,
          displayName : loggedInUserData?.displayName,
          lookingFor : loggedInUserData?.lookingFor,
          birthDate : loggedInUserData?.birthdate
        }
        const res = await userService.updateProfile(payload);

        if(!res){
          toast.error("Something went wrong")
          return;
        }
        toast.success("Profile saved")
      }
      if(cardState === 1){
      toast.success("Games saved")
      }
      if(cardState ===3){
        
      }
      toast.success("Profile saved")
      setCardState(prev => prev +1)
      console.log(cardState)
    }


      
    return (
      <div>
        <div className='profile-card'>
          <h2>🎮 Gamer Profile</h2>

          { cardState === 0 &&   <ProfilePicChange
            width={150}
            height={150}
          />}

          <form className="profile-form">
            { cardState === 0 && 
              <UserBioComponent
              />
            }
            { cardState === 1 &&
            <UserGamesComponent />
            }
            {cardState === 3 &&
            <UserGameDetails />}
            
          </form>
        </div>
        <button onClick={() => nextCardState()}>Next</button>

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