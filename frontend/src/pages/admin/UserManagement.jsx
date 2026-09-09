import { useState } from "react";
import "./UserManagement.css";
import UserDetailsModal from "./UserDetailsModal";

const usersData = [
  { id: "USR-001", name: "Rahul Sharma", email: "rahul.sharma@example.com", role: "Citizen", status: "Active", joined: "12 Jan 2026" },
  { id: "USR-002", name: "Priya Verma", email: "priya.verma@example.com", role: "Citizen", status: "Active", joined: "03 Feb 2026" },
  { id: "USR-003", name: "Amit Kumar", email: "amit.kumar@example.com", role: "Citizen", status: "Suspended", joined: "21 Feb 2026" },
  { id: "USR-004", name: "Sneha Gupta", email: "sneha.gupta@example.com", role: "Citizen", status: "Active", joined: "09 Mar 2026" },
];

const UserManagement = () => {
  const [search, setSearch] = useState("");
  const [users, setUsers] = useState(usersData);
  const [selectedUserId, setSelectedUserId] = useState(null);

  const filteredUsers = users.filter(
    (user) =>
      user.name.toLowerCase().includes(search.toLowerCase()) ||
      user.email.toLowerCase().includes(search.toLowerCase())
  );

  const selectedUser = users.find((u) => u.id === selectedUserId) || null;

  const handleToggleStatus = (userId) => {
    setUsers((prev) =>
      prev.map((u) =>
        u.id === userId
          ? { ...u, status: u.status === "Active" ? "Suspended" : "Active" }
          : u
      )
    );
  };

  return (
    <div className="users-page">
      <div className="page-header">
        <div>
          <h1>User Management</h1>
          <p>View and manage all registered citizens</p>
        </div>
      </div>

      <div className="users-card">
        <div className="users-toolbar">
          <input
            type="text"
            placeholder="Search users..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />

          <select>
            <option>Status</option>
            <option>Active</option>
            <option>Suspended</option>
          </select>
        </div>

        <div className="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Email</th>
                <th>Role</th>
                <th>Status</th>
                <th>Joined</th>
                <th>Action</th>
              </tr>
            </thead>

            <tbody>
              {filteredUsers.map((user) => (
                <tr key={user.id}>
                  <td>{user.id}</td>
                  <td>{user.name}</td>
                  <td>{user.email}</td>
                  <td>{user.role}</td>
                  <td>
                    <span className={`status ${user.status.toLowerCase()}`}>
                      {user.status}
                    </span>
                  </td>
                  <td>{user.joined}</td>
                  <td>
                    <button
                      className="view-btn"
                      onClick={() => setSelectedUserId(user.id)}
                    >
                      View
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      <UserDetailsModal
        user={selectedUser}
        onClose={() => setSelectedUserId(null)}
        onToggleStatus={handleToggleStatus}
      />
    </div>
  );
};

export default UserManagement;