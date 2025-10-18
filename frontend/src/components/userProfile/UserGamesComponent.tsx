import { useState } from 'react';
import type { UserBioProps, UserGameProps } from '../../types/UserProfileTypes';



function UserGameComponent ( {} : UserGameProps) { 
    const gameOptions = ["Game1", "Game2", "Game3", "Game4", "Game5"];
    const [selectedGame, setSelectedGame] = useState<string []>([]);
    
    const handleGameToggle = (game : string) => {
    if (!selectedGame.includes(game) && selectedGame.length >= 3) {
        return;
    }
      // Clear error when deselecting
    if (selectedGame.includes(game)) {
        setError("");
    }
  
    setSelectedGame(prev => prev.includes(game) ? prev.filter(g => g !== game) : [...prev, game]);
    setFormData((prev : any)=> {
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
        <div className="sector">Games you play</div>
          <div className="optionsmap">
          {gameOptions.map(game => <div key={game} 
              className={`options ${
              selectedGame.includes(game) ? "selected" : ""
            }`} > <div onClick={() =>  handleGameToggle(game)}>{game} </div> </div>)}
        </div>
    </div>
  )
}

export default UserGameComponent