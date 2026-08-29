import { useEffect, useState } from "react";
import { FaTimes, FaCheckCircle } from "react-icons/fa";
import "./AgentDetailsModal.css";
import "../../components/common/Modal.css";

function AgentDetailsModal({ agent, otherAgents = [], onClose, onReassignWorkload }) {
  const [mode, setMode] = useState("details"); // "details" | "reassign" | "done"
  const [movedTo, setMovedTo] = useState(null);

  useEffect(() => {
    setMode("details");
    setMovedTo(null);
  }, [agent]);

  if (!agent) return null;

  const handlePickTarget = (targetId, targetName) => {
    setMovedTo(targetName);
    onReassignWorkload?.(agent.id, targetId);
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
              ? "Reassign Workload"
              : mode === "done"
              ? "Workload Reassigned"
              : "Agent Details"}
          </h2>
          <button className="modal-close" onClick={handleClose}>
            <FaTimes />
          </button>
        </div>

        {mode === "details" && (
          <>
            <div className="modal-body">
              <div className="modal-row">
                <span className="modal-label">Agent ID</span>
                <span className="modal-value agent-id">{agent.id}</span>
              </div>

              <div className="modal-row">
                <span className="modal-label">Name</span>
                <span className="modal-value">{agent.name}</span>
              </div>

              <div className="modal-row">
                <span className="modal-label">Email</span>
                <span className="modal-value">{agent.email}</span>
              </div>

              <div className="modal-row">
                <span className="modal-label">Department</span>
                <span className="modal-value">{agent.department}</span>
              </div>

              <div className="modal-row">
                <span className="modal-label">Status</span>
                <span
                  className={`status ${agent.status
                    .replace(" ", "-")
                    .toLowerCase()}`}
                >
                  {agent.status}
                </span>
              </div>

              <div className="modal-row">
                <span className="modal-label">Assigned Complaints</span>
                <span className="modal-value">{agent.assigned}</span>
              </div>
            </div>

            <div className="modal-footer">
              <button className="modal-secondary-btn" onClick={handleClose}>
                Close
              </button>
              <button
                className="modal-primary-btn"
                disabled={agent.assigned === 0}
                onClick={() => setMode("reassign")}
              >
                Reassign Workload
              </button>
            </div>
          </>
        )}

        {mode === "reassign" && (
          <>
            <div className="modal-body">
              <p className="reassign-hint">
                Move {agent.assigned} complaint{agent.assigned === 1 ? "" : "s"} from{" "}
                {agent.name} to:
              </p>
              <div className="reassign-list">
                {otherAgents
                  .filter((a) => a.id !== agent.id)
                  .map((a) => (
                    <button
                      key={a.id}
                      className="reassign-option"
                      onClick={() => handlePickTarget(a.id, a.name)}
                    >
                      {a.name}
                      <span className="reassign-meta">{a.department}</span>
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
                <h3>Workload moved</h3>
                <p>
                  {agent.name}'s complaints have been reassigned to {movedTo}.
                </p>
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

export default AgentDetailsModal;