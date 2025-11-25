import React from 'react'
import type { UserGamerTypeDetailsProps } from '../../types/UserProfileTypes';

function UserGamerType({ gameData, onChange }: UserGamerTypeDetailsProps) {
    const competitivenessOptions = ["Just for fun", "Casual", "Semi-competitive", "Highly competitive"];
    const voiceChatOptions = ["Always", "Sometimes", "Rarely", "Never"];
    const playScheduleOptions = ["Weekday mornings", "Weekday evenings", "Weekend mornings", "Weekend evenings", "Late nights"];
    const mainGoalOptions = ["Rank climbing", "Learning", "Making friends", "Casual fun"];

    // Handle null gameData
    const safeGameData = gameData || {};

    const handleInputChange = (field: string, value: any) => {
        const updated = { [field]: value };
        onChange(updated);
    };

    return (
        <div>
            <h3>Please tell us more about your playstyle</h3>

            <label>Voice Chat</label>
            <select
                value={safeGameData.voiceChatPreference || ""}
                onChange={(e) => handleInputChange("voiceChatPreference", e.target.value)}
            >
                <option value="">Select...</option>
                {voiceChatOptions.map(voice => <option value={voice} key={voice}> {voice}</option>)}
            </select>

            <label>Competitiveness</label>
            <select
                value={safeGameData.competitiveness || ""}
                onChange={(e) => handleInputChange("competitiveness", e.target.value)}
            >
                <option value="">Select...</option>
                {competitivenessOptions.map(option => (
                    <option value={option} key={option}>{option}</option>
                ))}
            </select>

            <label>Play Schedule</label>
            <select
                value={safeGameData.playSchedule || ""}
                onChange={(e) => handleInputChange("playSchedule", e.target.value)}
            >
                <option value="">Select...</option>
                {playScheduleOptions.map(option => (
                    <option value={option} key={option}>{option}</option>
                ))}
            </select>

            <label>Main Goal</label>
            <select
                value={safeGameData.mainGoal || ""}
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