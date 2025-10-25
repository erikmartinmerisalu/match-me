export interface Game {
  expLvl: string;
  gamingHours: string;
  preferredServers: string[];
  currentRank: string;
}  ;

export interface Games {
  [key: string]: Game | {};
}

export type UserFormData = {
  id : number | null | undefined,
  displayName: string;
  aboutMe: string;
  birthDate: string;
  lookingfor: string;
  games: Games;
  maxPreferredDistance: number;
  timezone: string;
  lookingFor: string;
  preferredAgeMin: number;
  preferredAgeMax: number;
  profilePic: string | null;
  location: string;
  latitude: number | null;
  longitude: number | null;
  profileCompleted: boolean,
  age: number,
  competitiveness: string;
  voiceChatPreference: string;
  playSchedule: string;
  mainGoal: string;
} | null;



// export interface UserGameProps {
//   games: string[];
//   handleChange: (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => void;
// }