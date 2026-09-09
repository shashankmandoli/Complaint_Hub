import { FaTimes } from "react-icons/fa";
import ComplaintDetailPanel from "../../components/complaint/ComplaintDetailPanel";
import "./ComplaintDetailsModal.css";
import "../../components/common/Modal.css";

// Thin modal shell now — all the actual complaint content and admin actions
// (original complaint, lifecycle, priority editor, reassignment, updates)
// live in ComplaintDetailPanel so this stays in sync with the customer and
// agent views instead of duplicating logic.
function ComplaintDetailsModal({ complaintId, onClose, onChanged }) {
  if (!complaintId) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-box wide" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>Complaint Details</h2>
          <button className="modal-close" onClick={onClose}>
            <FaTimes />
          </button>
        </div>

        <div className="modal-body">
          <ComplaintDetailPanel
            complaintId={complaintId}
            role="admin"
            currentUserName="Admin"
            variant="embedded"
            onChanged={onChanged}
          />
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

export default ComplaintDetailsModal;