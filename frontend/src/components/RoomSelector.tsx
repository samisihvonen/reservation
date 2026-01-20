// ============================================
// 3. ROOM SELECTOR COMPONENT WITH FIXED IMPORTS
// ============================================
// File: src/components/RoomSelector.tsx

import type { Room } from '../types/room';
import { ROOMS } from '../services/reservationService';
import '../styles/RoomSelector.css';

interface RoomSelectorProps {
  selectedRoom: Room;
  onRoomSelect: (room: Room) => void;
}

export const RoomSelector = ({
  selectedRoom,
  onRoomSelect,
}:RoomSelectorProps) => {
  return (
    <div className="card">
      <h2>Valitse huone</h2>
      <div className="room-grid">
        {ROOMS.map((room) => (
          <button
            key={room.id}
            onClick={() => onRoomSelect(room)}
            className={`room-button ${selectedRoom.id === room.id ? 'active' : ''}`}
          >
            <div className="room-name">{room.name}</div>
            <div className="room-capacity">Kapasiteetti: {room.capacity}</div>
          </button>
        ))}
      </div>
    </div>
  );
};
