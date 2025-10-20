import { useState } from 'react';
import type { UserBioProps, UserFormData, UserGameProps } from '../../types/UserProfileTypes';
import { useAuth } from '../../context/AuthContext';



function UserGameComponent ( {} : UserGameProps) { 
    const gameOptions = ["Game1", "Game2", "Game3", "Game4", "Game5"];
    const {loggedInUserData, setLoggedInUserData} = useAuth();
    
    const handleGameToggle = (game : string) => {

    setLoggedInUserData((prev : UserFormData) => {
      console.log("this is prev" ,prev)
    const updatedGames = {...prev.games}

    if(game in updatedGames){
        delete updatedGames[game];
    }else {
        updatedGames[game] = {
        expLvl: "",
        gamingHours: "",
        preferredServers: [],
        competitiveness: "",
        voiceChatPreference: "",
        playSchedule: "",
        mainGoal: "",
        currentRank: ""
        }
    }
    return {
      ...prev,
      games: updatedGames
    }
    })
    }

  return (
    <div>
        <title className="sector">What games do you play?</title>
          <div className="optionsmap">
          {gameOptions.map(game => <div key={game} 
              className={`options ${
              loggedInUserData?.games[game] ? "selected" : ""
            }`} > <p onClick={() =>  handleGameToggle(game)}>{game} </p> </div>)}
        </div>
    </div>
  )
}

export default UserGameComponent