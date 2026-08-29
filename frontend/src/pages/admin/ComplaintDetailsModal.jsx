import { useEffect, useState } from "react";
import { FaTimes, FaCheckCircle } from "react-icons/fa";
import "./ComplaintDetailsModal.css";
import "../../components/common/Modal.css";

const availableAgents = [
  { name: "Neha Singh", department: "Water Supply" },
  { name: "Raj Malhotra", department: "Electricity" },
  { name: "Kavita Rao", department: "Road Maintenance" },
  { name: "Suresh Nair", department: "Garbage Collection" },
];

function ComplaintDetailsModal({ complaint, onClose, onReassign }) {
  const [mode, setMode] = useState("details"); // "details" | "reassign" | "done"
  const [assignedTo, setAssignedTo] = useState(null);

  useEffect(() => {
    // reset internal view whenever a new complaint is opened
    setMode("details");
    setAssignedTo(null);
  }, [complaint]);

  if (!complaint) return null;

  const renderAgentSection = () => {
    if (!complaint.agent) {
      return <span className="agent-status unassigned">Unassigned</span>;
    }

    if (complaint.agent.status === "accepted") {
      return (
        <div className="agent-cell">
          <span className="agent-name">{complaint.agent.name}</span>
          <span className="agent-status accepted">Accepted</span>
        </div>
      );
    }

    if (complaint.agent.status === "rejected") {
      return (
        <div className="agent-cell">
          <span className="agent-name">{complaint.agent.name}</span>
          <span className="agent-status rejected">Rejected</span>
          <span className="agent-reassigning">Reassigning agent...</span>
        </div>
      );
    }

    return (
      <div className="agent-cell">
        <span className="agent-name">{complaint.agent.name}</span>
        <span className="agent-status pending-response">Awaiting response</span>
      </div>
    );
  };

  const handlePickAgent = (agentName) => {
    setAssignedTo(agentName);
    onReassign?.(complaint.id, agentName);
    setMode("done");
  };

  const handleClose = () => {
    setMode("details");
    onClose();
  };

  return (
    <div className="modal-overlay" onClick={handleClose}>
      <div className="modal-box" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>
            {mode === "reassign"
              ? "Reassign Agent"
              : mode === "done"
              ? "Agent Reassigned"
              : "Complaint Details"}
          </h2>
          <button className="modal-close" onClick={handleClose}>
            <FaTimes />
          </button>
        </div>

        {mode === "details" && (
          <>
            <div className="modal-body">
              <div className="modal-row">
                <span className="modal-label">Complaint ID</span>
                <span className="modal-value complaint-id">{complaint.id}</span>
              </div>

              <div className="modal-row">
                <span className="modal-label">Citizen</span>
                <span className="modal-value">{complaint.user}</span>
              </div>

              <div className="modal-row">
                <span className="modal-label">Category</span>
                <span className="modal-value">{complaint.category}</span>
              </div>

              <div className="modal-row">
                <span className="modal-label">Priority</span>
                <span className={`priority ${complaint.priority.toLowerCase()}`}>
                  {complaint.priority}
                </span>
              </div>

              <div className="modal-row">
                <span className="modal-label">Status</span>
                <span
                  className={`status ${complaint.status
                    .replace(" ", "-")
                    .toLowerCase()}`}
                >
                  {complaint.status}
                </span>
              </div>

              <div className="modal-row">
                <span className="modal-label">Agent</span>
                {renderAgentSection()}
              </div>
            </div>

            <div className="modal-footer">
              <button className="modal-secondary-btn" onClick={handleClose}>
                Close
              </button>
              <button
                className="modal-primary-btn"
                onClick={() => setMode("reassign")}
              >
                Reassign Agent
              </button>
            </div>
          </>
        )}

        {mode === "reassign" && (
          <>
            <div className="modal-body">
              <p className="reassign-hint">
                Choose an agent to assign complaint {complaint.id} to:
              </p>
              <div className="reassign-list">
                {availableAgents.map((agent) => (
                  <button
                    key={agent.name}
                    className="reassign-option"
                    onClick={() => handlePickAgent(agent.name)}
                  >
                    {agent.name}
                    <span className="reassign-meta">{agent.department}</span>
                  </button>
                ))}
              </div>
            </div>

            <div className="modal-footer">
              <button
                className="modal-secondary-btn"
                onClick={() => setMode("details")}
              >
                Back
              </button>
            </div>
          </>
        )}

        {mode === "done" && (
          <>
            <div className="modal-body">
              <div className="action-success">
                <div className="success-icon">
                  <FaCheckCircle />
                </div>
                <h3>Complaint {complaint.id} reassigned</h3>
                <p>{assignedTo} has been notified and will respond shortly.</p>
              </div>
            </div>

            <div className="modal-footer">
              <button className="modal-primary-btn" onClick={handleClose}>
                Done
              </button>
            </div>
          </>
        )}
      </div>
    </div>
  );
}

export default ComplaintDetailsModal;