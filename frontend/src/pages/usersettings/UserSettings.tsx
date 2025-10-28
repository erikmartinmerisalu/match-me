import React, { useState } from 'react'
import UserBioComponent from '../../components/userProfile/UserBioComponent'
import UserGameComponent from '../../components/userProfile/UserGamesComponent'
import { useAuth } from '../../context/AuthContext';
import UserGamerType from '../../components/userProfile/UserGamerType';
import type LocationAndPreferences from '../../components/userProfile/UserPreferencesComponent';

function UserSettings() {
    const buttons = ["Bio" , "Games", "Preferences", "Other"];
    const [clickedSetting, setClickedSetting] = useState("Bio");
    const {loggedInUserData} = useAuth();
    
  return (
    <div>
        <div>
            {buttons.map(button => <button key={button} onClick={() => setClickedSetting(button)}>{button}</button>)}
        </div>
        <br />
        <div className='profile-card'>
            <form className='profile-form'>
                {clickedSetting === "Bio" ? <UserBioComponent /> : ""}
                {clickedSetting === "Games" && loggedInUserData?.games && (
                    <>
                    {Object.keys(loggedInUserData.games).map((key) => {
                        const gameData = loggedInUserData.games[key];
                        return (
                        <div key={key}>
                            <UserGameComponent  />
                        </div>
                        );
                    })}
                    </>
                )}
                {/* {clickedSetting === "Preferences" ? <UserGamerType /> : ""}
                {clickedSetting === "Other" ? <LocationAndPreferences /> : ""} */}

            </form>
        </div>
    </div>
  )
}

export default UserSettings