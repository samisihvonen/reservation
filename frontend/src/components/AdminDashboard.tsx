// ============================================
// IMPROVED ADMIN DASHBOARD COMPONENT
// ============================================
// File: src/components/AdminDashboard.tsx

import { useState, useEffect, useCallback, useMemo } from "react";
import api from "../services/api";
import "../styles/AdminDashboard.css";
import type { Room } from "../types/room";

interface AdminDashboardProps {
  token: string;
}

// Custom hook for data fetching with loading and error states
const useDataFetcher = <T,>(
  fetchFn: () => Promise<T>,
  dependencies: any[] = []
) => {
  const [data, setData] = useState<T | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchData = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const result = await fetchFn();
      setData(result);
    } catch (err) {
      setError(err instanceof Error ? err.message : "An error occurred");
    } finally {
      setLoading(false);
    }
  }, dependencies);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  return { data, loading, error, refetch: fetchData };
};

// Custom hook for async actions with loading
const useAsyncAction = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const execute = useCallback(async (action: () => Promise<void>, successMessage: string) => {
    setLoading(true);
    setError(null);
    setSuccess(null);
    try {
      await action();
      setSuccess(successMessage);
      setTimeout(() => setSuccess(null), 5000); // Clear after 5 seconds
    } catch (err) {
      setError(err instanceof Error ? err.message : "An error occurred");
      setTimeout(() => setError(null), 5000);
    } finally {
      setLoading(false);
    }
  }, []);

  return { loading, error, success, execute };
};

// User Management Component
const UserManagement = ({ token }: { token: string }) => {
  const { data: users, loading, error, refetch } = useDataFetcher(
    () => api.getUsers(token),
    [token]
  );
  const { loading: actionLoading, error: actionError, success, execute } = useAsyncAction();

  const handleDeleteUser = useCallback((id: number) => {
    if (!window.confirm("Are you sure you want to delete this user?")) return;
    execute(async () => {
      await api.deleteUser(id, token);
      refetch();
    }, "User deleted successfully");
  }, [token, execute, refetch]);

  if (loading) return <p>Loading users...</p>;
  if (error) return <div className="admin-error">{error}</div>;

  return (
    <div className="admin-section">
      <h2>User Management</h2>
      {actionError && <div className="admin-error">{actionError}</div>}
      {success && <div className="admin-success">{success}</div>}
      <table className="admin-table">
        <thead>
          <tr>
            <th>Email</th>
            <th>Name</th>
            <th>Created</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {users?.map((user) => (
            <tr key={user.id}>
              <td>{user.email}</td>
              <td>{user.displayName}</td>
              <td>{new Date(user.createdAt).toLocaleDateString()}</td>
              <td>
                <button
                  onClick={() => handleDeleteUser(user.id)}
                  className="delete-btn"
                  disabled={actionLoading}
                >
                  {actionLoading ? "Deleting..." : "Delete"}
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

// Room Management Component
const RoomManagement = ({ token }: { token: string }) => {
  const { data: rooms, loading, error, refetch } = useDataFetcher(
    () => api.getRooms(token),
    [token]
  );
  const { loading: actionLoading, error: actionError, success, execute } = useAsyncAction();

  const [formData, setFormData] = useState({
    name: "",
    capacity: 1,
    description: "",
    location: "",
  });
  const [editingRoom, setEditingRoom] = useState<Room | null>(null);
  const [editName, setEditName] = useState("");

  const resetForm = useCallback(() => {
    setFormData({ name: "", capacity: 1, description: "", location: "" });
  }, []);

  const validateForm = useCallback(() => {
    if (!formData.name.trim()) return "Room name is required";
    if (formData.capacity < 1) return "Capacity must be at least 1";
    return null;
  }, [formData]);

  const handleCreateRoom = useCallback(async (e: React.FormEvent) => {
    e.preventDefault();
    const validationError = validateForm();
    if (validationError) {
      alert(validationError);
      return;
    }
    await execute(async () => {
      await api.createRoom({
        name: formData.name.trim(),
        capacity: formData.capacity,
        description: formData.description.trim() || undefined,
        location: formData.location.trim() || undefined,
        isActive: true,
      }, token);
      resetForm();
      refetch();
    }, "Room created successfully");
  }, [formData, token, execute, resetForm, refetch, validateForm]);

  const handleDeleteRoom = useCallback((id: string) => {
    if (!window.confirm("Are you sure you want to delete this room?")) return;
    execute(async () => {
      await api.deleteRoom(id, token);
      refetch();
    }, "Room deleted successfully");
  }, [token, execute, refetch]);

  const handleEditRoom = useCallback((room: Room) => {
    setEditingRoom(room);
    setEditName(room.name);
  }, []);

  const handleSaveEdit = useCallback(async () => {
    if (!editingRoom || !editName.trim()) return;
    await execute(async () => {
      await api.updateRoomName(editingRoom.id, editName.trim(), token);
      setEditingRoom(null);
      refetch();
    }, "Room name updated successfully");
  }, [editingRoom, editName, token, execute, refetch]);

  const handleCancelEdit = useCallback(() => {
    setEditingRoom(null);
    setEditName("");
  }, []);

  if (loading) return <p>Loading rooms...</p>;
  if (error) return <div className="admin-error">{error}</div>;

  return (
    <div className="admin-section">
      <h2>Room Management</h2>
      {actionError && <div className="admin-error">{actionError}</div>}
      {success && <div className="admin-success">{success}</div>}

      <form onSubmit={handleCreateRoom} className="admin-form">
        <h3>Create New Room</h3>
        <div className="form-group">
          <label htmlFor="room-name">Room Name *</label>
          <input
            id="room-name"
            type="text"
            value={formData.name}
            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="room-capacity">Capacity *</label>
          <input
            id="room-capacity"
            type="number"
            min="1"
            value={formData.capacity}
            onChange={(e) => setFormData({ ...formData, capacity: parseInt(e.target.value) || 1 })}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="room-description">Description</label>
          <input
            id="room-description"
            type="text"
            value={formData.description}
            onChange={(e) => setFormData({ ...formData, description: e.target.value })}
          />
        </div>
        <div className="form-group">
          <label htmlFor="room-location">Location</label>
          <input
            id="room-location"
            type="text"
            value={formData.location}
            onChange={(e) => setFormData({ ...formData, location: e.target.value })}
          />
        </div>
        <button type="submit" disabled={actionLoading}>
          {actionLoading ? "Creating..." : "Create Room"}
        </button>
      </form>

      <table className="admin-table">
        <thead>
          <tr>
            <th>Name</th>
            <th>Capacity</th>
            <th>Location</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {rooms?.map((room) => (
            <tr key={room.id}>
              <td>
                {editingRoom?.id === room.id ? (
                  <input
                    type="text"
                    value={editName}
                    onChange={(e) => setEditName(e.target.value)}
                    onKeyDown={(e) => {
                      if (e.key === "Enter") handleSaveEdit();
                      if (e.key === "Escape") handleCancelEdit();
                    }}
                    autoFocus
                  />
                ) : (
                  room.name
                )}
              </td>
              <td>{room.capacity}</td>
              <td>{room.location || "-"}</td>
              <td>{room.isActive ? "Active" : "Inactive"}</td>
              <td>
                {editingRoom?.id === room.id ? (
                  <>
                    <button onClick={handleSaveEdit} className="save-btn" disabled={actionLoading}>
                      Save
                    </button>
                    <button onClick={handleCancelEdit} className="cancel-btn">
                      Cancel
                    </button>
                  </>
                ) : (
                  <>
                    <button
                      onClick={() => handleEditRoom(room)}
                      className="edit-btn"
                      disabled={actionLoading}
                    >
                      Edit
                    </button>
                    <button
                      onClick={() => handleDeleteRoom(room.id)}
                      className="delete-btn"
                      disabled={actionLoading}
                    >
                      Delete
                    </button>
                  </>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export const AdminDashboard = ({ token }: AdminDashboardProps) => {
  const [activeTab, setActiveTab] = useState<"users" | "rooms">("users");

  const tabs = useMemo(() => [
    { key: "users" as const, label: "Users" },
    { key: "rooms" as const, label: "Rooms" },
  ], []);

  return (
    <div className="admin-container">
      <h1>Admin Dashboard</h1>

      <div className="admin-tabs">
        {tabs.map((tab) => (
          <button
            key={tab.key}
            className={`admin-tab ${activeTab === tab.key ? "active" : ""}`}
            onClick={() => setActiveTab(tab.key)}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {activeTab === "users" ? (
        <UserManagement token={token} />
      ) : (
        <RoomManagement token={token} />
      )}
    </div>
  );
};
