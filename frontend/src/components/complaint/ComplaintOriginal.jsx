import { FaLock, FaTag, FaCalendarAlt, FaFlag } from "react-icons/fa";
import AttachmentGallery from "./AttachmentGallery";
import "./ComplaintOriginal.css";

function formatDate(iso) {
  try {
    return new Date(iso).toLocaleString("en-IN", {
      day: "numeric",
      month: "short",
      year: "numeric",
      hour: "numeric",
      minute: "2-digit",
    });
  } catch {
    return iso;
  }
}

// The permanent, immutable record — deliberately has no edit affordances of
// any kind. Additional information only ever enters through ComplaintUpdates.
function ComplaintOriginal({ complaint }) {
  return (
    <div className="original-section">
      <div className="original-header">
        <h4>Original Complaint</h4>
        <span className="immutable-badge">
          <FaLock /> Read-only
        </span>
      </div>

      <h3 className="original-title">{complaint.title}</h3>

      <div className="original-meta">
        <span>
          <FaTag /> {complaint.category}
        </span>
        <span className={`priority-pill priority-${complaint.priority.toLowerCase()}`}>
          <FaFlag /> {complaint.priority} Priority
        </span>
        <span>
          <FaCalendarAlt /> {formatDate(complaint.createdAt)}
        </span>
      </div>

      <p className="original-description">{complaint.description || "No description provided."}</p>

      <div className="original-attachments">
        <h5>Attachments</h5>
        <AttachmentGallery attachments={complaint.attachments} />
      </div>
    </div>
  );
}

export default ComplaintOriginal;