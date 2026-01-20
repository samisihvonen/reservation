// ============================================
// 4. RESERVATION FORM COMPONENT WITH FIXED IMPORTS
// ============================================
// File: src/components/ReservationForm.tsx

import React, { useState } from 'react';
import type { Room } from '../types/room';
import api from '../services/api';
import '../styles/ReservationForm.css';

interface ReservationFormProps {
  selectedRoom: Room;
  userName: string;
  token: string;
  onReservationCreated: () => void;
}

export const ReservationForm= ({
  selectedRoom,
  userName,
  token,
  onReservationCreated,
}:ReservationFormProps) => {
  const [startTime, setStartTime] = useState('');
  const [endTime, setEndTime] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const handleCreateReservation = async (): Promise<void> => {
    setError(null);
    setSuccessMessage(null);

    if (!startTime || !endTime) {
      setError('Kaikki kentät vaaditaan');
      return;
    }

    if (new Date(startTime) >= new Date(endTime)) {
      setError('Päättymisaika täytyy olla alkamisaikaa myöhemmin');
      return;
    }

    setLoading(true);

    try {
      await api.createReservation(
        selectedRoom.id,
        startTime,
        endTime,
        userName,
        token
      );
      setSuccessMessage('Varaus luotu onnistuneesti!');
      setStartTime('');
      setEndTime('');
      onReservationCreated();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Varaus epäonnistui');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="card">
      <h2>Luo varaus</h2>

      <div className="form-group">
        <label>Alkamisaika</label>
        <input
          type="datetime-local"
          value={startTime}
          onChange={(e) => setStartTime(e.target.value)}
          className="form-input"
        />
      </div>

      <div className="form-group">
        <label>Päättymisaika</label>
        <input
          type="datetime-local"
          value={endTime}
          onChange={(e) => setEndTime(e.target.value)}
          className="form-input"
        />
      </div>

      {error && <div className="form-error">⚠️ {error}</div>}
      {successMessage && <div className="form-success">✓ {successMessage}</div>}

      <button
        onClick={handleCreateReservation}
        disabled={loading}
        className="form-button"
      >
        {loading ? 'Luodaan...' : 'Luo varaus'}
      </button>
    </div>
  );
};