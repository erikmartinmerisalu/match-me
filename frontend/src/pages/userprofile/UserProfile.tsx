import React, { useState, type ChangeEvent } from 'react'
import ProfilePic from '../../components/profilepic/ProfilePicChange'
import "./userprofile.css"
import { Bounce, toast, ToastContainer } from 'react-toastify';
import UserGamesComponent from '../../components/userProfile/UserGamesComponent';
import UserBioComponent  from '../../components/userProfile/UserBioComponent';
import ProfilePicChange from '../../components/profilepic/ProfilePicChange';
import { useAuth } from '../../context/AuthContext';
import { userService } from '../../service/userService';
import UserGameDetails from '../../components/userProfile/UserGameDetails';
import type { UserBioData } from '../../types/UserBioComponentTypes';
import UserPreferencesComponent from '../../components/userProfile/UserPreferencesComponent';

  function UserProfile() {
    const [cardState, setCardState] = useState<number>(0);
    const {loggedInUserData, setLoggedInUserData} = useAuth();
    const [bioData, setBioData] = useState<UserBioData | null>(null);
    const [gameIndex, setGameIndex] = useState(0);
    const gamesList = loggedInUserData?.games ? Object.entries(loggedInUserData.games): [];
    const currentGame = gamesList[gameIndex]

    

    const handleSubmit = async (e: React.FormEvent) => {
    }

    const handleBioChange = (data: UserBioData) => {
      setBioData(data);
  };

    async function nextCardState(){
      console.log(loggedInUserData)
      if(cardState === 0){
        const payload = {
          profilePic : loggedInUserData?.profilePic? loggedInUserData.profilePic : null,
          aboutMe : bioData?.aboutMe? bioData.aboutMe : null,
          displayName : bioData?.displayName? bioData.displayName : null,
          lookingFor : bioData?.lookingFor? bioData.lookingFor : null,
          birthDate : bioData?.birthDate? bioData.birthDate : null
        }
        const res = await userService.updateProfile(payload);

        if(!res){
          toast.error("Something went wrong")
          return;
        }
        toast.success("Profile saved");
        setLoggedInUserData((prev : any) => ({ ...prev, ...bioData }));
        setCardState(cardState +1);
        return;
      }
      if(cardState === 1){
        if(loggedInUserData?.games == null){
          toast.error("Please choose at least one game");
          return;
        }else{
          toast.success("Games saved");
          setCardState(cardState+1);
          return;
        }
        }
      if(cardState === 2){
        if(gameIndex < gamesList.length - 1){
          setGameIndex(gameIndex +1)
          return;
        }else{
          setCardState(cardState+1)
          return;
        }
      }

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
              onDataChange={handleBioChange}
              />
            }
            { cardState === 1 &&
            <UserGamesComponent  />
            }
            {cardState === 2 &&
            <UserGameDetails
              key={currentGame[0]}
              gameName={currentGame[0]}
              gameData={currentGame[1]}
              onChange={(updatedGame) => {
              setLoggedInUserData((prev : any) => ({
                ...prev,
                games: {
                  ...prev.games,
                  [currentGame[0]]: updatedGame
                }
              }));
              }}
            />}
            {cardState === 3 &&
            <UserPreferencesComponent />}
            
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