import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./AdminTopbar.css";

import {
  FaBell,
  FaSearch,
  FaCalendarAlt,
  FaChevronDown,
  FaSignOutAlt,
  FaUser,
} from "react-icons/fa";
import AdminProfileModal from "./AdminProfileModal";

function AdminTopbar() {
  const navigate = useNavigate();
  const [profileOpen, setProfileOpen] = useState(false);
  const [profileModalOpen, setProfileModalOpen] = useState(false);

  const today = new Date().toLocaleDateString("en-US", {
    weekday: "long",
    day: "numeric",
    month: "long",
    year: "numeric",
  });

  const handleLogout = () => {
    navigate("/login");
  };

  return (
    <header className="admin-topbar">
      {/* Left */}
      <div className="topbar-left">
        <h1>Dashboard</h1>
        <p>Welcome back! Here's what's happening today.</p>
      </div>

      {/* Right */}
      <div className="topbar-right">

        {/* Search */}
        <div className="search-box">
          <FaSearch className="search-icon" />
          <input type="text" placeholder="Search complaints, users..." />
        </div>

        {/* Date */}
        <div className="topbar-date">
          <FaCalendarAlt />
          <span>{today}</span>
        </div>

        {/* Notification */}
        <button
          className="notification-btn"
          onClick={() => navigate("/admin/notifications")}
        >
          <FaBell />
          <span className="notification-badge">4</span>
        </button>

        {/* Profile */}
        <div className="profile-wrapper">
          <button
            className="admin-profile"
            onClick={() => setProfileOpen((prev) => !prev)}
          >
            <img src="https://i.pravatar.cc/100?img=12" alt="Administrator" />

            <div className="profile-info">
              <h4>Administrator</h4>
              <p>Super Admin</p>
            </div>

            <FaChevronDown className="profile-arrow" />
          </button>

          {profileOpen && (
            <div className="profile-dropdown">
              <button
                className="dropdown-item"
                onClick={() => {
                  setProfileModalOpen(true);
                  setProfileOpen(false);
                }}
              >
                <FaUser /> Profile
              </button>
              <button className="dropdown-item logout" onClick={handleLogout}>
                <FaSignOutAlt /> Logout
              </button>
            </div>
          )}
        </div>

      </div>

      <AdminProfileModal
        open={profileModalOpen}
        onClose={() => setProfileModalOpen(false)}
      />
    </header>
  );
}

export default AdminTopbar;