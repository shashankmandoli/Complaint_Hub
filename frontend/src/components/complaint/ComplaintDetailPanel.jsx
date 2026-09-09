import { useEffect, useState } from "react";
import { FaUserTie, FaCheckCircle, FaExclamationTriangle, FaBan } from "react-icons/fa";
import {
  getComplaintById,
  addUpdate,
  resolveComplaint,
  confirmResolution,
  assignAgent,
  rejectComplaint,
  updatePriority,
  STATUS,
  STATUS_LABELS,
  PRIORITIES,
  AVAILABLE_AGENTS,
} from "../../utils/mockComplaints";
import ComplaintOriginal from "./ComplaintOriginal";
import ComplaintLifecycle from "./ComplaintLifecycle";
import ResolutionConfirmation from "./ResolutionConfirmation";
import ComplaintUpdates from "./ComplaintUpdates";
import "./ComplaintDetailPanel.css";

const STATUS_BADGE_CLASS = {
  [STATUS.OPEN]: "badge-open",
  [STATUS.ASSIGNED]: "badge-assigned",
  [STATUS.IN_PROGRESS]: "badge-progress",
  [STATUS.RESOLVED]: "badge-resolved",
  [STATUS.CLOSED]: "badge-closed",
  [STATUS.REJECTED]: "badge-rejected",
  [STATUS.NEEDS_REASSIGNMENT]: "badge-reassignment",
};

// Single source of truth for "what a complaint detail screen looks like" —
// reused by the customer page, the agent page, and the admin modal.
// variant="embedded" drops the outer shadow card for use inside a modal.
function ComplaintDetailPanel({
  complaintId,
  role, // "customer" | "agent" | "admin"
  currentUserName = "You",
  variant = "page",
  onChanged,
  onNotFound,
}) {
  const [complaint, setComplaint] = useState(undefined);
  const [resolveNote, setResolveNote] = useState("");
  const [showResolveForm, setShowResolveForm] = useState(false);
  const [showAssignList, setShowAssignList] = useState(false);

  const refresh = () => {
    const found = getComplaintById(complaintId);
    setComplaint(found);
    if (!found) onNotFound?.();
    return found;
  };

  useEffect(() => {
    refresh();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [complaintId]);

  if (complaint === undefined || complaint === null) {
    return null;
  }

  const notify = () => {
    refresh();
    onChanged?.();
  };

  const handleAddUpdate = async ({ title, description }) => {
    const authorRole = role === "customer" ? "User" : role === "agent" ? "Agent" : "Admin";
    addUpdate(complaint.id, { title, description, author: currentUserName, role: authorRole });
    notify();
  };

  const handleConfirmResolution = (solved) => {
    confirmResolution(complaint.id, solved);
    notify();
  };

  const handleResolve = (e) => {
    e.preventDefault();
    resolveComplaint(complaint.id, { description: resolveNote.trim(), author: currentUserName });
    setResolveNote("");
    setShowResolveForm(false);
    notify();
  };

  const handleAssign = (agentName) => {
    assignAgent(complaint.id, agentName);
    setShowAssignList(false);
    notify();
  };

  const handleReject = () => {
    rejectComplaint(complaint.id, { author: currentUserName });
    notify();
  };

  const handlePriorityChange = (e) => {
    updatePriority(complaint.id, e.target.value);
    notify();
  };

  const canAddUpdate =
    role === "customer"
      ? ![STATUS.CLOSED, STATUS.REJECTED].includes(complaint.status)
      : role === "agent"
      ? [STATUS.ASSIGNED, STATUS.IN_PROGRESS].includes(complaint.status)
      : false; // Admin doesn't post generic updates — keeps the flow automatic.

  const agentCanResolve = role === "agent" && complaint.status === STATUS.IN_PROGRESS;
  const customerConfirms = role === "customer" && complaint.status === STATUS.RESOLVED;
  const adminCanAssign =
    role === "admin" && [STATUS.OPEN, STATUS.NEEDS_REASSIGNMENT].includes(complaint.status);
  const adminCanReject = role === "admin" && complaint.status === STATUS.OPEN;

  return (
    <div className={variant === "embedded" ? "panel embedded" : "panel details-card"}>
      <div className="panel-header">
        <div>
          <h1 className="panel-title">{complaint.title}</h1>
          <p className="panel-id">Complaint #{complaint.id}</p>
        </div>
        <span className={`status-badge ${STATUS_BADGE_CLASS[complaint.status]}`}>
          {STATUS_LABELS[complaint.status]}
        </span>
      </div>

      <ComplaintOriginal complaint={complaint} />

      {role === "admin" && (
        <div className="panel-section priority-editor">
          <h4>Priority</h4>
          <select value={complaint.priority} onChange={handlePriorityChange}>
            {PRIORITIES.map((p) => (
              <option key={p} value={p}>
                {p}
              </option>
            ))}
          </select>
        </div>
      )}

      <div className="panel-section">
        <h4>Complaint Lifecycle</h4>
        <ComplaintLifecycle status={complaint.status} />
      </div>

      <div className="panel-section">
        <h4>Assigned Agent</h4>
        <div className="agent-row">
          <FaUserTie />
          <span>{complaint.assignedAgent || "Not assigned yet"}</span>
        </div>
      </div>

      {agentCanResolve && (
        <div className="panel-section">
          {!showResolveForm ? (
            <button type="button" className="primary-action-btn" onClick={() => setShowResolveForm(true)}>
              <FaCheckCircle /> Mark as Resolved
            </button>
          ) : (
            <form className="resolve-form" onSubmit={handleResolve}>
              <label>Resolution Note (Optional)</label>
              <textarea
                rows={3}
                value={resolveNote}
                onChange={(e) => setResolveNote(e.target.value)}
                placeholder="What was done to resolve this?"
              />
              <div className="resolve-form-actions">
                <button type="button" className="cancel-btn" onClick={() => setShowResolveForm(false)}>
                  Cancel
                </button>
                <button type="submit" className="primary-action-btn">
                  <FaCheckCircle /> Confirm Resolved
                </button>
              </div>
            </form>
          )}
        </div>
      )}

      {customerConfirms && (
        <div className="panel-section">
          <ResolutionConfirmation onConfirm={handleConfirmResolution} />
        </div>
      )}

      {adminCanAssign && (
        <div className="panel-section">
          <div className="admin-assign-header">
            <FaExclamationTriangle />
            <span>
              {complaint.status === STATUS.NEEDS_REASSIGNMENT
                ? "This complaint needs reassignment to another agent."
                : "This complaint needs an agent assigned."}
            </span>
          </div>

          {!showAssignList ? (
            <div className="admin-assign-actions">
              <button type="button" className="primary-action-btn" onClick={() => setShowAssignList(true)}>
                {complaint.status === STATUS.NEEDS_REASSIGNMENT ? "Reassign Agent" : "Assign Agent"}
              </button>
              {adminCanReject && (
                <button type="button" className="reject-btn" onClick={handleReject}>
                  <FaBan /> Reject Complaint
                </button>
              )}
            </div>
          ) : (
            <div className="assign-list">
              {AVAILABLE_AGENTS.map((agent) => (
                <button key={agent.name} type="button" className="assign-option" onClick={() => handleAssign(agent.name)}>
                  {agent.name}
                  <span>{agent.department}</span>
                </button>
              ))}
              <button type="button" className="cancel-btn" onClick={() => setShowAssignList(false)}>
                Cancel
              </button>
            </div>
          )}
        </div>
      )}

      <div className="panel-section">
        <ComplaintUpdates
          complaint={complaint}
          canAddUpdate={canAddUpdate}
          currentRole={role}
          currentAuthor={currentUserName}
          onAddUpdate={handleAddUpdate}
        />
      </div>
    </div>
  );
}

export default ComplaintDetailPanel;