import React, { useState, useEffect } from 'react';

interface Reservation {
  id: string;
  roomId: string;
  startTime: string;
  endTime: string;
  user: string;
}

function App() {
  const [res, setRes] = useState<Reservation[]>([]);
  const [room, setRoom] = useState('Neukkari 1');
  
  // Lomakkeen tila
  const [startTime, setStartTime] = useState('');
  const [endTime, setEndTime] = useState('');
  const [userName, setUserName] = useState('');
  const [error, setError] = useState<string | null>(null);

  const fetchReservations = () => {
    fetch(`http://localhost:8080/api/reservations/${room}`)
      .then(res => res.json())
      .then(data => setRes(data))
      .catch(err => console.error("Virhe haettaessa varauksia:", err));
  };

  useEffect(() => {
    fetchReservations();
  }, [room]);

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault(); // Estetään sivun latautuminen
    setError(null);

    const newRes = {
      roomId: room,
      startTime: startTime,
      endTime: endTime,
      user: userName
    };

    try {
      const response = await fetch('http://localhost:8080/api/reservations', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newRes)
      });

      if (response.ok) {
        alert("Varaus tehty onnistuneesti!");
        fetchReservations(); // Päivitetään lista heti
      } else {
        const errorText = await response.text();
        setError(errorText || "Varaus epäonnistui (päällekkäinen aika?)");
      }
    } catch (err) {
      setError("Palvelimeen ei saatu yhteyttä.");
    }
  };

  return (
    <div style={{ maxWidth: '600px', margin: 'auto', padding: '20px' }}>
      <h1>Varausjärjestelmä</h1>
      
      <form onSubmit={handleCreate} style={{ display: 'flex', flexDirection: 'column', gap: '10px', marginBottom: '30px' }}>
        <label>Huone:</label>
        <input value={room} onChange={e => setRoom(e.target.value)} required />
        
        <label>Varaaja:</label>
        <input value={userName} onChange={e => setUserName(e.target.value)} placeholder="Nimesi" required />
        
        <label>Alkaa:</label>
        <input type="datetime-local" value={startTime} onChange={e => setStartTime(e.target.value)} required />
        
        <label>Loppuu:</label>
        <input type="datetime-local" value={endTime} onChange={e => setEndTime(e.target.value)} required />
        
        <button type="submit" style={{ padding: '10px', background: '#007bff', color: 'white', border: 'none', cursor: 'pointer' }}>
          Tee varaus
        </button>
      </form>

      {error && <div style={{ color: 'red', marginBottom: '20px', fontWeight: 'bold' }}>{error}</div>}

      <hr />
      <h2>Varaukset: {room}</h2>
      <ul style={{ listStyle: 'none', padding: 0 }}>
        {res.map(r => (
          <li key={r.id} style={{ padding: '10px', borderBottom: '1px solid #ccc' }}>
            <strong>{new Date(r.startTime).toLocaleString('fi-FI')}</strong> - {r.user}
          </li>
        ))}
      </ul>
    </div>
  );
}

export default App;