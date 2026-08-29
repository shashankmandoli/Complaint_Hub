import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { FaArrowLeft, FaUserTie, FaCalendarAlt, FaTag } from "react-icons/fa";
import { getComplaintById } from "../../utils/mockComplaints.js";
import "./ComplaintDetails.css";

const STAGES = ["Pending", "In Progress", "Resolved"];

const statusClass = {
  Pending: "status pending",
  "In Progress": "status progress",
  Resolved: "status resolved",
};

function ComplaintDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [complaint, setComplaint] = useState(undefined); // undefined = loading, null = not found

  useEffect(() => {
    // TODO: replace with a real GET call via services/api.js once the backend is ready.
    setComplaint(getComplaintById(id));
  }, [id]);

  if (complaint === undefined) {
    return null;
  }

  if (complaint === null) {
    return (
      <div className="details-page">
        <button className="back-btn" onClick={() => navigate("/customer/complaints")}>
          <FaArrowLeft /> Back to My Complaints
        </button>
        <div className="not-found">Complaint not found.</div>
      </div>
    );
  }

  const currentStageIndex = STAGES.indexOf(complaint.status);

  return (
    <div className="details-page">
      <button className="back-btn" onClick={() => navigate("/customer/complaints")}>
        <FaArrowLeft /> Back to My Complaints
      </button>

      <div className="details-card">
        <div className="details-header">
          <div>
            <h1>{complaint.title}</h1>
            <div className="details-meta">
              <span>
                <FaTag /> {complaint.category}
              </span>
              <span>
                <FaCalendarAlt /> {complaint.createdAt}
              </span>
            </div>
          </div>
          <span className={statusClass[complaint.status] || "status pending"}>
            {complaint.status}
          </span>
        </div>

        <div className="timeline">
          {STAGES.map((stage, i) => (
            <div
              key={stage}
              className={`timeline-step ${i <= currentStageIndex ? "done" : ""}`}
            >
              <div className="timeline-dot" />
              <p>{stage}</p>
            </div>
          ))}
        </div>

        <div className="details-section">
          <h4>Description</h4>
          <p>{complaint.description || "No description provided."}</p>
        </div>

        <div className="details-section">
          <h4>Assigned Agent</h4>
          <div className="agent-row">
            <FaUserTie />
            <span>{complaint.agent ? complaint.agent : "Not assigned yet"}</span>
          </div>
        </div>
      </div>
    </div>
  );
}

export default ComplaintDetails;