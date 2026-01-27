// ============================================
// 6. DASHBOARD COMPONENT WITH FIXED IMPORTS
// ============================================
// File: src/components/Dashboard.tsx

import { useState, useEffect } from 'react';
import type { Reservation } from '../types/reservation';
import type { Room } from '../types/room';
import { ROOMS } from '../services/reservationService';
import api from '../services/api';
import { RoomSelector } from './RoomSelector';
import { ReservationForm } from './ReservationForm';
import { ReservationsList } from './ReservationsList';
import '../styles/Dashboard.css';

interface DashboardProps {
  auth: {
    token: string;
    user: { email: string; displayName: string; id: number };
  };
  onLogout: () => void;
}

export const Dashboard = ({ auth, onLogout }:DashboardProps) => {
  const [selectedRoom, setSelectedRoom] = useState<Room>(ROOMS[0]);
  const [reservations, setReservations] = useState<Reservation[]>([]);
  const [allReservations, setAllReservations] = useState<Reservation[]>([]);
  const [loading, setLoading] = useState(false);
  const [activeTab, setActiveTab] = useState<'room' | 'all'>('room');

  useEffect(() => {
    if (auth.token) {
      if (activeTab === 'room') {
        fetchReservations(selectedRoom.id);
      } else {
        fetchAllReservations();
      }
    }
  }, [selectedRoom, activeTab, auth.token]);

  const fetchReservations = async (roomId: string): Promise<void> => {
    setLoading(true);
    try {
      const data = await api.getReservationsByRoom(roomId, auth.token);
      setReservations(data || []);
    } catch (err) {
      console.error('Error fetching reservations:', err);
    } finally {
      setLoading(false);
    }
  };

  const fetchAllReservations = async (): Promise<void> => {
    setLoading(true);
    try {
      const allReservationsData: Reservation[] = [];
      for (const room of ROOMS) {
        const data = await api.getReservationsByRoom(room.id, auth.token);
        allReservationsData.push(...(data || []));
      }
      setAllReservations(
        allReservationsData.sort(
          (a, b) =>
            new Date(b.startTime).getTime() - new Date(a.startTime).getTime()
        )
      );
    } catch (err) {
      console.error('Error fetching all reservations:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleReservationCreated = (): void => {
    fetchReservations(selectedRoom.id);
    fetchAllReservations();
  };

  const displayReservations = activeTab === 'room' ? reservations : allReservations;

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <h1>Varausjärjestelmä</h1>
        <div className="dashboard-user-info">
          <p>
            Tervetuloa, <strong>{auth.user?.displayName}</strong>
          </p>
          <button onClick={onLogout} className="dashboard-logout-button">
            Kirjaudu ulos
          </button>
        </div>
      </div>

      <div className="dashboard-layout">
        <div className="dashboard-sidebar">
          <RoomSelector selectedRoom={selectedRoom} onRoomSelect={setSelectedRoom} />
          <ReservationForm
            selectedRoom={selectedRoom}
            userName={auth.user?.displayName}
            token={auth.token}
            onReservationCreated={handleReservationCreated}
          />
        </div>

        <div>
          <ReservationsList
            reservations={displayReservations}
            loading={loading}
            activeTab={activeTab}
            onTabChange={setActiveTab}
          />
        </div>
      </div>
    </div>
  );
};