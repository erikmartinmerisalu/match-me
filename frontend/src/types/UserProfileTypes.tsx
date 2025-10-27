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

  export interface UserBioData {
  displayName: string | null;
  aboutMe: string | null;
  lookingFor: string | null;
  birthDate: string | null;
}

export interface UserBioProps {
  onDataChange?: (data: UserBioData) => void;
}

export interface UserGameDetailsProps {
  gameName: string;
  gameData: any;
  onChange: (updatedGame: any) => void;
}

export interface UserGamerTypeDetailsProps {
  gameData: any;
  onChange: (updatedGame: any) => void;
}

export interface LocationAndPreferencesData {
  location: string | null;
  preferredAgeMin: number | null;
  preferredAgeMax: number | null;
  maxPreferredDistance: number | null;
  latitude : number | null,
  longitude : number | null
}

export interface LocationAndPreferencesProps {
  onDataChange?: (data: LocationAndPreferencesData) => void;
}

export interface LocationSuggestion {
  id: number;
  country: string;
  city: string;
  latitude: number;
  longitude: number;
  elevation: number;
}

export interface LocationAndPreferencesProps {
  onDataChange?: (data: LocationAndPreferencesData) => void;
}