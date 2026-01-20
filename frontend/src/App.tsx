import React, { useState, useEffect } from 'react';
import './App.css';

interface Reservation {
  id: string;
  roomId: string;
  startTime: string;
  endTime: string;
  user: string;
  createdAt: string;
  updatedAt: string;
}

interface Room {
  id: string;
  name: string;
  capacity: number;
}

const ROOMS: Room[] = [
  { id: 'room-1', name: 'Neukkari 1', capacity: 6 },
  { id: 'room-2', name: 'Neukkari 2', capacity: 8 },
  { id: 'room-3', name: 'Neuvotteluhuone A', capacity: 4 },
  { id: 'room-4', name: 'Neuvotteluhuone B', capacity: 10 },
  { id: 'room-5', name: 'Kokoustila C', capacity: 12 },
];

function App() {
  const [selectedRoom, setSelectedRoom] = useState<Room>(ROOMS[0]);
  const [reservations, setReservations] = useState<Reservation[]>([]);
  const [allReservations, setAllReservations] = useState<Reservation[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState<'room' | 'all'>('room');

  const [startTime, setStartTime] = useState('');
  const [endTime, setEndTime] = useState('');
  const [userName, setUserName] = useState('');
  const [formLoading, setFormLoading] = useState(false);

  const fetchReservations = async (roomId: string) => {
    setLoading(true);
    setError(null);
    try {
      const response = await fetch(`http://localhost:8080/api/reservations/${roomId}`);
      if (!response.ok) throw new Error('Failed to fetch reservations');
      const data = await response.json();
      setReservations(data || []);
    } catch (err) {
      setError('Virhe varausten hakemisessa');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const fetchAllReservations = async () => {
    setLoading(true);
    setError(null);
    try {
      const allReservationsData: Reservation[] = [];
      for (const room of ROOMS) {
        const response = await fetch(`http://localhost:8080/api/reservations/${room.id}`);
        if (response.ok) {
          const data = await response.json();
          allReservationsData.push(...(data || []));
        }
      }
      // Sort by startTime descending (newest first)
      allReservationsData.sort(
        (a, b) => new Date(b.startTime).getTime() - new Date(a.startTime).getTime()
      );
      setAllReservations(allReservationsData);
    } catch (err) {
      setError('Virhe varausten hakemisessa');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (activeTab === 'room') {
      fetchReservations(selectedRoom.id);
    } else {
      fetchAllReservations();
    }
  }, [selectedRoom, activeTab]);

  const handleCreateReservation = async () => {
    setError(null);
    setSuccessMessage(null);

    if (!startTime || !endTime || !userName) {
      setError('Kaikki kentät vaaditaan');
      return;
    }

    if (new Date(startTime) >= new Date(endTime)) {
      setError('Päättymisaika täytyy olla alkamisaikaa myöhemmin');
      return;
    }

    setFormLoading(true);

    try {
      const response = await fetch('http://localhost:8080/api/reservations', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          roomId: selectedRoom.id,
          startTime,
          endTime,
          user: userName,
        }),
      });

      if (response.ok) {
        setSuccessMessage('Varaus luotu onnistuneesti!');
        setStartTime('');
        setEndTime('');
        setUserName('');
        await fetchReservations(selectedRoom.id);
        await fetchAllReservations();
      } else {
        const data = await response.json();
        setError(data.message || 'Varaus epäonnistui');
      }
    } catch (err) {
      setError('Palvelimeen ei saatu yhteyttä');
      console.error(err);
    } finally {
      setFormLoading(false);
    }
  };

  const getRoomName = (roomId: string) => {
    return ROOMS.find(r => r.id === roomId)?.name || roomId;
  };

  const formatDateTime = (dateString: string) => {
    return new Date(dateString).toLocaleString('fi-FI', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  const getDurationMinutes = (start: string, end: string) => {
    return Math.round(
      (new Date(end).getTime() - new Date(start).getTime()) / (1000 * 60)
    );
  };

  const isPastReservation = (startTime: string) => {
    return new Date(startTime) < new Date();
  };

  const displayReservations = activeTab === 'room' ? reservations : allReservations;

  return (
    <div className="app-container">
      {/* Header */}
      <div className="app-header">
        <h1>Kokoushuoneiden varausjärjestelmä</h1>
        <p className="app-subtitle">Valitse huone ja varaa aika helposti</p>
      </div>

      <div className="app-layout">
        {/* Left Column: Room Selection & Form */}
        <div className="app-sidebar">
          {/* Room Selection */}
          <div className="card room-selector">
            <h2>Valitse huone</h2>
            <div className="room-grid">
              {ROOMS.map((room) => (
                <button
                  key={room.id}
                  onClick={() => setSelectedRoom(room)}
                  className={`room-button ${selectedRoom.id === room.id ? 'active' : ''}`}
                >
                  <div className="room-name">{room.name}</div>
                  <div className="room-capacity">
                    Kapasiteetti: {room.capacity} henkilöä
                  </div>
                </button>
              ))}
            </div>
          </div>

          {/* Reservation Form */}
          <div className="card reservation-form">
            <h2>Luo varaus</h2>
            
            <div className="form-group">
              <label>Varaajan nimi</label>
              <input
                type="text"
                value={userName}
                onChange={(e) => setUserName(e.target.value)}
                placeholder="Nimesi"
                className="form-input"
              />
            </div>

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

            {error && (
              <div className="alert alert-error">
                <strong>⚠️ Virhe:</strong> {error}
              </div>
            )}

            {successMessage && (
              <div className="alert alert-success">
                <strong>✓ Onnistui:</strong> {successMessage}
              </div>
            )}

            <button
              onClick={handleCreateReservation}
              disabled={formLoading}
              className={`btn btn-primary ${formLoading ? 'btn-loading' : ''}`}
            >
              {formLoading ? 'Luodaan...' : 'Luo varaus'}
            </button>
          </div>
        </div>

        {/* Right Column: Reservations List */}
        <div className="app-main">
          <div className="card reservations-list">
            {/* Tabs */}
            <div className="tabs">
              <button
                className={`tab-button ${activeTab === 'room' ? 'active' : ''}`}
                onClick={() => setActiveTab('room')}
              >
                Varaukset: {selectedRoom.name}
              </button>
              <button
                className={`tab-button ${activeTab === 'all' ? 'active' : ''}`}
                onClick={() => setActiveTab('all')}
              >
                Kaikki varaukset
              </button>
            </div>

            {loading ? (
              <div className="loading-state">
                <p>⏳ Ladataan varauksia...</p>
              </div>
            ) : displayReservations.length === 0 ? (
              <div className="empty-state">
                <p>📅 Ei varauksia</p>
              </div>
            ) : (
              <ul className="reservations-ul">
                {displayReservations.map((res) => (
                  <li
                    key={res.id}
                    className={`reservation-item ${
                      isPastReservation(res.startTime) ? 'past' : 'upcoming'
                    }`}
                  >
                    <div className="res-time">
                      <strong>{formatDateTime(res.startTime)}</strong>
                      <br />
                      <span className="res-end-time">
                        - {formatDateTime(res.endTime)}
                      </span>
                    </div>
                    <div className="res-info">
                      <div className="res-user">
                        <strong>{res.user}</strong>
                      </div>
                      <div className="res-room">
                        {getRoomName(res.roomId)}
                      </div>
                      <div className="res-duration">
                        {getDurationMinutes(res.startTime, res.endTime)} min
                      </div>
                    </div>
                    <div className="res-created">
                      Luotu: {formatDateTime(res.createdAt)}
                    </div>
                  </li>
                ))}
              </ul>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default App;
