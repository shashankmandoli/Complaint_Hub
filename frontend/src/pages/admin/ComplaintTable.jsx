import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./ComplaintTable.css";
import ComplaintDetailsModal from "./ComplaintDetailsModal.jsx";

const initialComplaints = [
  {
    id: "CMP-1001",
    user: "Rahul Sharma",
    category: "Water Supply",
    priority: "High",
    status: "Pending",
    agent: null,
  },
  {
    id: "CMP-1002",
    user: "Priya Verma",
    category: "Electricity",
    priority: "Medium",
    status: "In Progress",
    agent: { name: "Neha Singh", status: "accepted" },
  },
  {
    id: "CMP-1003",
    user: "Amit Kumar",
    category: "Road Damage",
    priority: "Low",
    status: "Resolved",
    agent: { name: "Raj Malhotra", status: "accepted" },
  },
  {
    id: "CMP-1004",
    user: "Sneha Gupta",
    category: "Garbage",
    priority: "High",
    status: "Pending",
    agent: { name: "Kavita Rao", status: "rejected" },
  },
  {
    id: "CMP-1005",
    user: "Rohit Singh",
    category: "Street Light",
    priority: "Medium",
    status: "Resolved",
    agent: { name: "Suresh Nair", status: "accepted" },
  },
];

function ComplaintTable() {
  const navigate = useNavigate();
  const [complaints, setComplaints] = useState(initialComplaints);
  const [selectedComplaint, setSelectedComplaint] = useState(null);

  const handleReassign = (complaintId, agentName) => {
    setComplaints((prev) =>
      prev.map((item) =>
        item.id === complaintId
          ? { ...item, agent: { name: agentName, status: "pending" }, status: "In Progress" }
          : item
      )
    );
  };

  const getStatusClass = (status) => {
    switch (status) {
      case "Pending":
        return "pending";
      case "In Progress":
        return "progress";
      case "Resolved":
        return "resolved";
      default:
        return "";
    }
  };

  const getPriorityClass = (priority) => {
    switch (priority) {
      case "High":
        return "high";
      case "Medium":
        return "medium";
      case "Low":
        return "low";
      default:
        return "";
    }
  };

  const renderAgentCell = (agent) => {
    if (!agent) {
      return <span className="agent-status unassigned">Unassigned</span>;
    }

    if (agent.status === "accepted") {
      return (
        <div className="agent-cell">
          <span className="agent-name">{agent.name}</span>
          <span className="agent-status accepted">Accepted</span>
        </div>
      );
    }

    if (agent.status === "rejected") {
      return (
        <div className="agent-cell">
          <span className="agent-name">{agent.name}</span>
          <span className="agent-status rejected">Rejected</span>
          <span className="agent-reassigning">Reassigning agent...</span>
        </div>
      );
    }

    return (
      <div className="agent-cell">
        <span className="agent-name">{agent.name}</span>
        <span className="agent-status pending-response">Awaiting response</span>
      </div>
    );
  };

  return (
    <div className="table-card">
      <div className="table-header">
        <div>
          <h2>Recent Complaints</h2>
          <p>Latest complaints received</p>
        </div>

        <button
          className="view-all-btn"
          onClick={() => navigate("/admin/complaints")}
        >
          View All
        </button>
      </div>

      <div className="table-wrapper">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Citizen</th>
              <th>Category</th>
              <th>Priority</th>
              <th>Status</th>
              <th>Agent</th>
              <th></th>
            </tr>
          </thead>

          <tbody>
            {complaints.map((item) => (
              <tr key={item.id}>
                <td className="complaint-id">{item.id}</td>
                <td>{item.user}</td>
                <td>{item.category}</td>

                <td>
                  <span className={`priority ${getPriorityClass(item.priority)}`}>
                    {item.priority}
                  </span>
                </td>

                <td>
                  <span className={`status ${getStatusClass(item.status)}`}>
                    {item.status}
                  </span>
                </td>

                <td>{renderAgentCell(item.agent)}</td>

                <td>
                  <button
                    className="table-action"
                    onClick={() => setSelectedComplaint(item)}
                  >
                    View
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <ComplaintDetailsModal
        complaint={selectedComplaint}
        onClose={() => setSelectedComplaint(null)}
        onReassign={handleReassign}
      />
    </div>
  );
}

export default ComplaintTable;