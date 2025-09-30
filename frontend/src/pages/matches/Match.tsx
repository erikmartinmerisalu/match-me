import React, { useState } from 'react'
import Reccomendations from './Reccomendations'
import Matches from './Matches'
import "./match.css";

function Match() {
  const [clicked, setClicked] = useState("reccomendations")

  return (
    <div className='main'>
        <div>
            <button onClick={() => setClicked("reccomendations")}>Reccomendations</button>
            <button onClick={() => setClicked("matches")}> Matches </button>
        </div>
        <br />
        {clicked === "reccomendations" ? 
        <Reccomendations /> :
        <Matches />}

    </div>
  )
}

export default Match