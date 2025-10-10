export interface Game {
  expLvl: string;
  gamingHours: string;
  preferredServers: string[];
}

export interface Games {
  [key: string]: Game;
}

export type FormData = {
  username: string;
  about: string;
  birthdate: string;
  lookingfor: string;
  games: Games | null;
  maxPreferredDistance: number;
  timezone: string;
  lookingFor: string;
  preferredAgeMin: number;
  preferredAgeMax: number;
  profilePic: string;
  location: string;
  latitude: number | null;
  longitude: number | null;
};
