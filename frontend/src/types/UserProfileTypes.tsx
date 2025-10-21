export interface Game {
  expLvl: string;
  gamingHours: string;
  preferredServers: string[];
  // ADD THESE NEW FIELDS:
  competitiveness: string;
  voiceChatPreference: string;
  playSchedule: string;
  mainGoal: string;
  currentRank: string;
}  ;

export interface Games {
  [key: string]: Game | {};
}

export type UserFormData = {
  id : number | null,
  displayName: string;
  aboutMe: string;
  birthdate: string;
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
} | null;

export interface UserBioProps {
  userName: string;
  aboutMe: string;
  lookingfor: string;
  birthdate: string;
  handleChange: (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => void;
}

// export interface UserGameProps {
//   games: string[];
//   handleChange: (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => void;
// }