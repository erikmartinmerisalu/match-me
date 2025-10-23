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

  const toggleMultiChoice = (field: "playSchedule" | "mainGoal" | "competitiveness", value: string) => {
    const current = Array.isArray(localData[field]) ? localData[field] : [];
    const updated = current.includes(value)
      ? current.filter(v => v !== value)
      : [...current, value];
    handleInputChange(field, updated);
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

    <label>Competitiveness (multi-select)</label>
      <div className="optionsmap">
        {competitivenessOptions.map(option => (
          <p
            key={option}
            className={`options ${localData.competitiveness.includes(option) ? "selected" : ""}`}
            onClick={() => toggleMultiChoice("competitiveness", option)}
          >
            {option}
          </p>
        ))}
      </div>

      <label>Play Schedule (multi-select)</label>
      <div className="optionsmap">
        {playScheduleOptions.map(option => (
          <p
            key={option}
            className={`options ${localData.playSchedule.includes(option) ? "selected" : ""}`}
            onClick={() => toggleMultiChoice("playSchedule", option)}
          >
            {option}
          </p>
        ))}
      </div>

      <label>Main Goal (multi-select)</label>
      <div className="optionsmap">
        {mainGoalOptions.map(option => (
          <p
            key={option}
            className={`options ${localData.mainGoal.includes(option) ? "selected" : ""}`}
            onClick={() => toggleMultiChoice("mainGoal", option)}
          >
            {option}
          </p>
        ))}
      </div>
    </div>
  )
}

export default UserGamerType