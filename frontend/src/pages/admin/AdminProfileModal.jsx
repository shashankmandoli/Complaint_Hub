import { FaTimes } from "react-icons/fa";
import "../../components/common/Modal.css";

function AdminProfileModal({ open, onClose }) {
  if (!open) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-box" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>Your Profile</h2>
          <button className="modal-close" onClick={onClose}>
            <FaTimes />
          </button>
        </div>

        <div className="modal-body">
          <div className="modal-row">
            <span className="modal-label">Name</span>
            <span className="modal-value">Administrator</span>
          </div>

          <div className="modal-row">
            <span className="modal-label">Email</span>
            <span className="modal-value">admin@complainthub.com</span>
          </div>

          <div className="modal-row">
            <span className="modal-label">Role</span>
            <span className="modal-value">Super Admin</span>
          </div>

          <div className="modal-row">
            <span className="modal-label">Department</span>
            <span className="modal-value">Platform Administration</span>
          </div>
        </div>

        <div className="modal-footer">
          <button className="modal-secondary-btn" onClick={onClose}>
            Close
          </button>
        </div>
      </div>
    </div>
  );
}

export default AdminProfileModal;