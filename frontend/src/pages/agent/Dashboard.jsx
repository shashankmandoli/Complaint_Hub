import { useEffect, useState } from "react";
import { getComplaints } from "../../utils/mockComplaints.js";
import "../customer/MyComplaints.css";

function Dashboard() {
  const [complaints, setComplaints] = useState([]);

  useEffect(() => {
    setComplaints(getComplaints());
  }, []);

  const assigned = complaints.filter((c) => c.agent);

  return (
    <div className="my-complaints-page">
      <div className="page-header">
        <h1>Assigned Complaints</h1>
        <p>Complaints currently assigned to you.</p>
      </div>

      {assigned.length === 0 ? (
        <div className="empty-state">No complaints assigned yet.</div>
      ) : (
        <div className="complaints-list">
          {assigned.map((c) => (
            <div className="complaint-card" key={c.id}>
              <div className="complaint-main">
                <h3>{c.title}</h3>
                <p className="meta">
                  {c.category} • {c.createdAt}
                </p>
              </div>
              <span className="status pending">{c.status}</span>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default Dashboard;