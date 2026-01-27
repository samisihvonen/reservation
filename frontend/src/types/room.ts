export interface Room {
  id: string;
  name: string;
  capacity: number;
  description?: string;
  location?: string;
  isActive: boolean;
  createdAt: string;
}