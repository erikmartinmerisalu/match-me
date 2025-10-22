  export interface UserBioData {
  displayName: string | null;
  aboutMe: string | null;
  lookingFor: string | null;
  birthDate: string | null;
}

export interface UserBioProps {
  onDataChange?: (data: UserBioData) => void;
}