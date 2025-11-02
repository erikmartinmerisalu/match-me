import { useAuth } from '../../context/AuthContext';

function UserGameComponent() {
    const gameOptions = ["Game1", "Game2", "Game3", "Game4", "Game5"];
    const { loggedInUserData, setLoggedInUserData } = useAuth();

    const handleGameToggle = (game: string) => {
        setLoggedInUserData((prev: any) => {
            if (prev === null) return null;
            const updatedGames = { ...prev.games };

            if (game in updatedGames) {
                delete updatedGames[game];
            } else {
                updatedGames[game] = {
                    preferredServers: [],
                    expLvl: "",
                    gamingHours: "",
                    currentRank: ""
                };
            }

            return {
                ...prev,
                games: updatedGames
            };
        });
    };

    return (
        <div>
            <h2 className="sector">What games do you play?</h2>
            <div className="optionsmap">
                {gameOptions.map(game => (
                    <div
                        key={game}
                        className={`options ${loggedInUserData?.games?.[game] ? "selected" : ""}`}
                    >
                        <p onClick={() => handleGameToggle(game)}>{game}</p>
                    </div>
                ))}
            </div>
        </div>
    );
}

export default UserGameComponent;