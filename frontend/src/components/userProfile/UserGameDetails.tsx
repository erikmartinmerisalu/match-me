import React, { useState } from 'react'
import "./gamecomponents.css"
import type { UserGameDetailsProps } from '../../types/UserProfileTypes';

function UserGameDetails({gameName, gameData, onChange}: UserGameDetailsProps) {
  const gameExpLvl = ["Beginner", "Intermediate", "Advanced"];
  const gaminghours = ["<100", "101-500", "501-1000", "1000+"];
  const serverOptions = ["N-America", "S-America", "EU East", "EU West", "Asia", "AU+SEA", "Africa+Middle east"];
  const rankOptions = ["Unranked", "Bronze", "Silver", "Gold", "Platinum", "Diamond", "Master", "Grandmaster", "N/A"];
  const [localData, setLocalData] = useState({
    ...gameData,
    preferredServers: gameData.preferredServers || []
  });


  const handleInputChange = (field: string, value: any) => {
    const updated = { ...localData, [field]: value };
    setLocalData(updated);
    onChange(updated);
  };

  const toggleServer = (server: string) => {
    const currentServers = Array.isArray(localData.preferredServers)
      ? localData.preferredServers
      : [];
    const updatedServers = currentServers.includes(server)
      ? currentServers.filter((s : string)  => s !== server)
      : [...currentServers, server];
    handleInputChange("preferredServers", updatedServers);
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
        <select value={localData.gamingHours || ""}
          onChange={(e) => handleInputChange("gamingHours", e.target.value)}>
            <option value="">Select...</option>
            {gaminghours.map(hour => <option value={hour} key={hour}>{hour}</option>)}
        </select>

      <label>Servers I play in</label>
      <div className="optionsmap">
        {serverOptions.map((server) => (
          <p
            key={server}
            className={`options ${
            localData.preferredServers?.includes(server) ? "selected" : ""
            }`}
            onClick={() => toggleServer(server)}
          >
            {server}
          </p>
        ))}
      </div>

      <label>Rank</label>
        <select value={localData.currentRank || ""}
          onChange={(e) => handleInputChange("currentRank", e.target.value)}>
            <option value="">Select...</option>
            {rankOptions.map(rank => <option value={rank} key={rank}> {rank}</option>)}
        </select>
                 
    </div>
  )
}

export default UserGameDetails