import { FaTimes } from "react-icons/fa";
import "./UserDetailsModal.css";

function UserDetailsModal({ user, onClose, onToggleStatus }) {
  if (!user) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-box" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>User Details</h2>
          <button className="modal-close" onClick={onClose}>
            <FaTimes />
          </button>
        </div>

        <div className="modal-body">
          <div className="modal-row">
            <span className="modal-label">User ID</span>
            <span className="modal-value user-id">{user.id}</span>
          </div>

          <div className="modal-row">
            <span className="modal-label">Name</span>
            <span className="modal-value">{user.name}</span>
          </div>

          <div className="modal-row">
            <span className="modal-label">Email</span>
            <span className="modal-value">{user.email}</span>
          </div>

          <div className="modal-row">
            <span className="modal-label">Role</span>
            <span className="modal-value">{user.role}</span>
          </div>

          <div className="modal-row">
            <span className="modal-label">Status</span>
            <span className={`status ${user.status.toLowerCase()}`}>
              {user.status}
            </span>
          </div>

          <div className="modal-row">
            <span className="modal-label">Joined</span>
            <span className="modal-value">{user.joined}</span>
          </div>
        </div>

        <div className="modal-footer">
          <button className="modal-secondary-btn" onClick={onClose}>
            Close
          </button>
          <button
            className="modal-primary-btn"
            onClick={() => onToggleStatus?.(user.id)}
          >
            {user.status === "Active" ? "Suspend User" : "Reactivate User"}
          </button>
        </div>
      </div>
    </div>
  );
}

export default UserDetailsModal;