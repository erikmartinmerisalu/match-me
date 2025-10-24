import React, { useState } from 'react'

interface UserGameDetailsProps {
  gameData: any;
  onChange: (updatedGame: any) => void;
}

function UserGamerType({gameData, onChange}: UserGameDetailsProps) {
  const competitivenessOptions = ["Just for fun", "Casual", "Semi-competitive", "Highly competitive"];
  const voiceChatOptions = ["Always", "Sometimes", "Rarely", "Never"];
  const playScheduleOptions = ["Weekday mornings", "Weekday evenings", "Weekend mornings", "Weekend evenings", "Late nights"];
  const mainGoalOptions = ["Rank climbing", "Learning", "Making friends", "Casual fun"];

  const [localData, setLocalData] = useState({
    ...gameData,
    playSchedule: gameData.playSchedule || [],
    mainGoal: gameData.mainGoal || [],
    competitiveness : gameData.compecompetitiveness || []
  });

  const handleInputChange = (field: string, value: any) => {
    const updated = { ...localData, [field]: value };
    setLocalData(updated);
    onChange(updated);
  };

  return (
    <div>
    <h3>Please tell us more about your playstyle</h3>

    <label>Voice Chat</label>
        <select value={localData.voiceChatPreference || ""}
          onChange={(e) => handleInputChange("voiceChatPreference", e.target.value)}>
            <option value="">Select...</option>
            {voiceChatOptions.map(voice => <option value={voice} key={voice}> {voice}</option>)}
        </select>

    <label>Competitiveness</label>
      <select
        value={localData.competitiveness || ""}
        onChange={(e) => handleInputChange("competitiveness", e.target.value)}
      >
        <option value="">Select...</option>
        {competitivenessOptions.map(option => (
          <option value={option} key={option}>{option}</option>
        ))}
      </select>

    <label>Play Schedule</label>
      <select
        value={localData.playSchedule || ""}
        onChange={(e) => handleInputChange("playSchedule", e.target.value)}
      >
        <option value="">Select...</option>
        {playScheduleOptions.map(option => (
          <option value={option} key={option}>{option}</option>
        ))}
      </select>

    <label>Main Goal</label>
      <select
        value={localData.mainGoal || ""}
        onChange={(e) => handleInputChange("mainGoal", e.target.value)}
      >
        <option value="">Select...</option>
        {mainGoalOptions.map(option => (
          <option value={option} key={option}>{option}</option>
        ))}
      </select>
    </div>
  )
}

export default UserGamerType