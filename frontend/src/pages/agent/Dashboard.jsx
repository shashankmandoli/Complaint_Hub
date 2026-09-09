import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getComplaintsByAgent, STATUS, STATUS_LABELS } from "../../utils/mockComplaints";
import "../customer/MyComplaints.css";

// No auth yet, so the logged-in agent is mocked here — swap this for the
// real signed-in agent's name once authentication exists.
const CURRENT_AGENT_NAME = "Neha Singh";

const statusClass = {
  [STATUS.OPEN]: "status pending",
  [STATUS.ASSIGNED]: "status progress",
  [STATUS.IN_PROGRESS]: "status progress",
  [STATUS.RESOLVED]: "status resolved",
  [STATUS.CLOSED]: "status resolved",
  [STATUS.REJECTED]: "status pending",
  [STATUS.NEEDS_REASSIGNMENT]: "status progress",
};

function Dashboard() {
  const [complaints, setComplaints] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    // TODO: replace with a real GET call via services/api.js once the backend is ready.
    setComplaints(getComplaintsByAgent(CURRENT_AGENT_NAME));
  }, []);

  return (
    <div className="my-complaints-page">
      <div className="page-header">
        <h1>Assigned Complaints</h1>
        <p>Complaints currently assigned to you.</p>
      </div>

      {complaints.length === 0 ? (
        <div className="empty-state">No complaints assigned yet.</div>
      ) : (
        <div className="complaints-list">
          {complaints.map((c) => (
            <div
              className="complaint-card"
              key={c.id}
              onClick={() => navigate(`/agent/complaints/${c.id}`)}
              role="button"
              tabIndex={0}
            >
              <div className="complaint-main">
                <h3>{c.title}</h3>
                <p className="meta">
                  {c.category} • {new Date(c.createdAt).toLocaleDateString()}
                </p>
              </div>
              <span className={statusClass[c.status] || "status pending"}>
                {STATUS_LABELS[c.status]}
              </span>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default Dashboard;