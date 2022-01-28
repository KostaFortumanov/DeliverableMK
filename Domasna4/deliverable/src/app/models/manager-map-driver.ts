import { Job } from './job' 

export interface ManagerMapDriver {
  id: number;
  name: string;
  currentLon: number;
  currentLat: number;
  destinationLon: number;
  destinationLat: number;
  job: Job;
  lastUpdate: number;
  seconds: number;
}