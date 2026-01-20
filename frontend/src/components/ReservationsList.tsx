// ============================================
// 5. RESERVATIONS LIST COMPONENT WITH FIXED IMPORTS
// ============================================
// File: src/components/ReservationsList.tsx

import type { Reservation } from '../types/reservation';
import { ROOMS } from '../services/reservationService';
import reservationService from '../services/reservationService';
import '../styles/ReservationList.css';

interface ReservationsListProps {
  reservations: Reservation[];
  loading: boolean;
  activeTab: 'room' | 'all';
  onTabChange: (tab: 'room' | 'all') => void;
}

export const ReservationsList = ({
  reservations,
  loading,
  activeTab,
  onTabChange,
}:ReservationsListProps) => {
  return (
    <div className="card">
      <div className="tabs">
        <button
          onClick={() => onTabChange('room')}
          className={`tab-button ${activeTab === 'room' ? 'active' : ''}`}
        >
          Huoneen varaukset
        </button>
        <button
          onClick={() => onTabChange('all')}
          className={`tab-button ${activeTab === 'all' ? 'active' : ''}`}
        >
          Kaikki varaukset
        </button>
      </div>

      {loading ? (
        <div className="loading-state">
          <p>⏳ Ladataan...</p>
        </div>
      ) : reservations.length === 0 ? (
        <div className="empty-state">
          <p>📅 Ei varauksia</p>
        </div>
      ) : (
        <ul className="reservations-ul">
          {reservations.map((res) => (
            <li
              key={res.id}
              className={`reservation-item ${
                reservationService.isPastReservation(res.startTime)
                  ? 'past'
                  : 'upcoming'
              }`}
            >
              <div className="res-time">
                <strong>{reservationService.formatDateTime(res.startTime)}</strong>
                <span className="res-end-time">
                  - {reservationService.formatDateTime(res.endTime)}
                </span>
              </div>
              <div className="res-info">
                <div className="res-user">{res.user}</div>
                {activeTab === 'all' && (
                  <div className="res-room">
                    {ROOMS.find((r) => r.id === res.roomId)?.name}
                  </div>
                )}
                <div className="res-duration">
                  {(() => {
                    const duration =
                      (new Date(res.endTime).getTime() -
                        new Date(res.startTime).getTime()) /
                      (1000 * 60);
                    return `${Math.round(duration)} min`;
                  })()}
                </div>
              </div>
              <div className="res-created">
                Luotu: {reservationService.formatDateTime(res.createdAt)}
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};