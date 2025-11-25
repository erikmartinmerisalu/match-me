export interface Game {
  expLvl: string;
  gamingHours: string;
  preferredServers: string[];
  currentRank: string;
}

export interface Games {
  [key: string]: Game | {};
}

export interface UserFormData {
  id: number | null | undefined;
  displayName: string;
  aboutMe: string;
  birthDate: string;
  lookingFor: string;
  games: Games;
  maxPreferredDistance: number;
  timezone: string;
  preferredAgeMin: number;
  preferredAgeMax: number;
  profilePic: string | null;
  location: string;
  latitude: number | null;
  longitude: number | null;
  profileCompleted: boolean;
  age: number;
  competitiveness: string;
  voiceChatPreference: string;
  playSchedule: string;
  mainGoal: string;
}

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
  gameData: Game | {};
  onChange: (updatedGame: Game) => void;
}

export interface UserGamerTypeDetailsProps {
  gameData: Partial<UserFormData> | null;
  onChange: (updatedData: Partial<UserFormData>) => void;
}

export interface LocationAndPreferencesData {
  location: string | null;
  preferredAgeMin: number | null;
  preferredAgeMax: number | null;
  maxPreferredDistance: number | null;
  latitude: number | null;
  longitude: number | null;
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