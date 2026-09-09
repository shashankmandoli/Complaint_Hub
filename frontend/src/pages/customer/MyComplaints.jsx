import { useEffect, useState } from "react";
import {
  FaClipboardList,
  FaHourglassHalf,
  FaSpinner,
  FaCheckCircle,
  FaPlusCircle,
} from "react-icons/fa";
import { useNavigate } from "react-router-dom";
import { getComplaints, STATUS, STATUS_LABELS } from "../../utils/mockComplaints";
import "./MyComplaints.css";

const statusClass = {
  [STATUS.OPEN]: "status pending",
  [STATUS.ASSIGNED]: "status progress",
  [STATUS.IN_PROGRESS]: "status progress",
  [STATUS.RESOLVED]: "status resolved",
  [STATUS.CLOSED]: "status resolved",
  [STATUS.REJECTED]: "status pending",
  [STATUS.NEEDS_REASSIGNMENT]: "status progress",
};

function MyComplaints() {
  const [complaints, setComplaints] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    // TODO: replace with a real GET call via services/api.js once the backend is ready.
    setComplaints(getComplaints());
  }, []);

  const total = complaints.length;
  const pending = complaints.filter((c) => c.status === STATUS.OPEN).length;
  const inProgress = complaints.filter((c) =>
    [STATUS.ASSIGNED, STATUS.IN_PROGRESS, STATUS.NEEDS_REASSIGNMENT].includes(c.status)
  ).length;
  const resolved = complaints.filter((c) =>
    [STATUS.RESOLVED, STATUS.CLOSED].includes(c.status)
  ).length;

  const stats = [
    { label: "Total", value: total, icon: <FaClipboardList />, tone: "total" },
    { label: "Pending", value: pending, icon: <FaHourglassHalf />, tone: "pending" },
    { label: "In Progress", value: inProgress, icon: <FaSpinner />, tone: "progress" },
    { label: "Resolved", value: resolved, icon: <FaCheckCircle />, tone: "resolved" },
  ];

  return (
    <div className="my-complaints-page">
      <div className="page-header">
        <div>
          <h1>My Complaints</h1>
          <p>Track the status of every complaint you've raised.</p>
        </div>
        <button className="raise-btn" onClick={() => navigate("/customer/raise")}>
          <FaPlusCircle />
          Raise Complaint
        </button>
      </div>

      <div className="stats-grid">
        {stats.map((s) => (
          <div className={`stat-card ${s.tone}`} key={s.label}>
            <div className="stat-icon">{s.icon}</div>
            <div>
              <h2>{s.value}</h2>
              <p>{s.label}</p>
            </div>
          </div>
        ))}
      </div>

      {complaints.length === 0 ? (
        <div className="empty-state">
          <FaClipboardList className="empty-icon" />
          <h3>No complaints yet</h3>
          <p>Anything you raise will show up here with its status and assigned agent.</p>
          <button className="raise-btn" onClick={() => navigate("/customer/raise")}>
            <FaPlusCircle />
            Raise your first complaint
          </button>
        </div>
      ) : (
        <div className="complaints-list">
          {complaints.map((c) => (
            <div
              className="complaint-card"
              key={c.id}
              onClick={() => navigate(`/customer/complaints/${c.id}`)}
              role="button"
              tabIndex={0}
            >
              <div className="complaint-main">
                <h3>{c.title}</h3>
                <p className="meta">
                  {c.category} • Raised on {new Date(c.createdAt).toLocaleDateString()}
                </p>
              </div>

              <div className="complaint-side">
                <span className={statusClass[c.status] || "status pending"}>
                  {STATUS_LABELS[c.status] || c.status}
                </span>
                <p className="agent-info">
                  {c.assignedAgent ? `Assigned to ${c.assignedAgent}` : "Agent not assigned yet"}
                </p>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default MyComplaints;