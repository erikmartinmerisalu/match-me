import React, { useState } from 'react'

interface UserGameDetailsProps {
  gameName: string;
  gameData: any; // tüübi järgi saad defineerida
  onChange: (updatedGame: any) => void;
}

function UserGameDetails({gameName, gameData, onChange}: UserGameDetailsProps) {
  const gameExpLvl = ["Beginner", "Intermediate", "Advanced"]
  const gaminghours = ["<100", "101-500", "501-1000", "1000+"]
  const competitivenessOptions = ["Just for fun", "Casual", "Semi-competitive", "Highly competitive"]
  const voiceChatOptions = ["Always", "Sometimes", "Rarely", "Never"]
  const playScheduleOptions = ["Weekday mornings", "Weekday evenings", "Weekend mornings", "Weekend evenings", "Late nights"]
  const mainGoalOptions = ["Rank climbing", "Learning", "Making friends", "Casual fun"]
  const rankOptions = ["Unranked", "Bronze", "Silver", "Gold", "Platinum", "Diamond", "Master", "Grandmaster", "N/A"]
  const [localData, setLocalData] = useState(gameData);


  const handleInputChange = (field: string, value: any) => {
    const updated = { ...localData, [field]: value };
    setLocalData(updated);
    onChange(updated); // uuendame UserProfile state'i
  };
  return (
    <div>
      <h3>Please tell us more about the game you play</h3>
      <p>Game: {gameName} </p>
      <br />

      <label htmlFor=""> Game Experience</label>
        <select value={localData.expLvl || ""}
          onChange={(e) => handleInputChange("expLvl", e.target.value)}>
          <option value="">Select...</option>
            {gameExpLvl.map(lvl => <option value={lvl} key={lvl}>{lvl}</option>)}
        </select>

      <label >Played Hours</label>
          <select value={localData.played || ""}
          onChange={(e) => handleInputChange("playedHours", e.target.value)}>
            <option value="">Select...</option>
            {gaminghours.map(hour => <option value={hour} key={hour}>{hour}</option>)}
          </select>
                 
    </div>
  )
}

export default UserGameDetails