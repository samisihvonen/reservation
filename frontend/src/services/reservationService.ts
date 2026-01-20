// ============================================
// 3. RESERVATION SERVICE
// ============================================
// File: src/services/reservationService.ts

import type { Room } from "../types/room";

export const ROOMS: Room[] = [
  { id: "room-1", name: "Neukkari 1", capacity: 6 },
  { id: "room-2", name: "Neukkari 2", capacity: 8 },
  { id: "room-3", name: "Neuvotteluhuone A", capacity: 4 },
  { id: "room-4", name: "Neuvotteluhuone B", capacity: 10 },
  { id: "room-5", name: "Kokoustila C", capacity: 12 },
];

class ReservationService {
  getRoomById(roomId: string): Room | undefined {
    return ROOMS.find((r) => r.id === roomId);
  }

  formatDateTime(dateString: string): string {
    return new Date(dateString).toLocaleString("fi-FI", {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
    });
  }

  isPastReservation(startTime: string): boolean {
    return new Date(startTime) < new Date();
  }
}

export default new ReservationService();
