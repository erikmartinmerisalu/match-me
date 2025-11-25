import React from 'react'
import type { UserGameDetailsProps, Game } from '../../types/UserProfileTypes';

function UserGameDetails({ gameName, gameData, onChange }: UserGameDetailsProps) {
    const gameExpLvl = ["Beginner", "Intermediate", "Advanced"];
    const gaminghours = ["<100", "101-500", "501-1000", "1000+"];
    const serverOptions = ["N-America", "S-America", "EU East", "EU West", "Asia", "AU+SEA", "Africa+Middle east"];
    const rankOptions = ["Unranked", "Bronze", "Silver", "Gold", "Platinum", "Diamond", "Master", "Grandmaster", "N/A"];

    // Create a safe game data object with defaults
    const safeGameData: Game = {
        expLvl: (gameData as Game).expLvl || '',
        gamingHours: (gameData as Game).gamingHours || '',
        preferredServers: Array.isArray((gameData as Game).preferredServers) 
            ? (gameData as Game).preferredServers 
            : [],
        currentRank: (gameData as Game).currentRank || ''
    };

    const handleInputChange = (field: keyof Game, value: string | string[]) => {
        const updated: Game = { 
            ...safeGameData, 
            [field]: value 
        };
        onChange(updated);
    };

    const toggleServer = (server: string) => {
        const currentServers = safeGameData.preferredServers;
        const updatedServers = currentServers.includes(server)
            ? currentServers.filter((s: string) => s !== server)
            : [...currentServers, server];
        handleInputChange("preferredServers", updatedServers);
    };

    return (
        <div>
            <h3>Please tell us more about the game you play</h3>
            <p>Game: {gameName} </p>
            <br />

            <label htmlFor="expLvl">Game Experience</label>
            <select
                id="expLvl"
                value={safeGameData.expLvl}
                onChange={(e) => handleInputChange("expLvl", e.target.value)}
            >
                <option value="">Select...</option>
                {gameExpLvl.map(lvl => <option value={lvl} key={lvl}>{lvl}</option>)}
            </select>

            <label htmlFor="gamingHours">Played Hours</label>
            <select
                id="gamingHours"
                value={safeGameData.gamingHours}
                onChange={(e) => handleInputChange("gamingHours", e.target.value)}
            >
                <option value="">Select...</option>
                {gaminghours.map(hour => <option value={hour} key={hour}>{hour}</option>)}
            </select>

            <label>Servers I play in</label>
            <div className="optionsmap">
                {serverOptions.map((server) => (
                    <p
                        key={server}
                        className={`options ${safeGameData.preferredServers.includes(server) ? "selected" : ""}`}
                        onClick={() => toggleServer(server)}
                    >
                        {server}
                    </p>
                ))}
            </div>

            <label htmlFor="currentRank">Rank</label>
            <select
                id="currentRank"
                value={safeGameData.currentRank}
                onChange={(e) => handleInputChange("currentRank", e.target.value)}
            >
                <option value="">Select...</option>
                {rankOptions.map(rank => <option value={rank} key={rank}> {rank}</option>)}
            </select>
        </div>
    )
}

export default UserGameDetails