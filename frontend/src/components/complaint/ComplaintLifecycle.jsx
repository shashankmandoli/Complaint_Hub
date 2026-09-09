import { FaExclamationTriangle, FaBan } from "react-icons/fa";
import { STATUS, LIFECYCLE_STEPS, STATUS_LABELS } from "../../utils/mockComplaints";
import "./ComplaintLifecycle.css";

const STEP_LABELS = {
  [STATUS.OPEN]: "Open",
  [STATUS.ASSIGNED]: "Assigned",
  [STATUS.IN_PROGRESS]: "In Progress",
  [STATUS.RESOLVED]: "Resolved",
  [STATUS.CLOSED]: "Closed",
};

// Visual stepper for the primary lifecycle. REJECTED and NEEDS_REASSIGNMENT
// are branches off the happy path, so they render as a separate callout
// instead of being squeezed into the same straight line.
function ComplaintLifecycle({ status }) {
  if (status === STATUS.REJECTED) {
    return (
      <div className="lifecycle-branch rejected">
        <FaBan />
        <div>
          <p className="branch-title">Complaint Rejected</p>
          <p className="branch-subtitle">This complaint was rejected and will not proceed further.</p>
        </div>
      </div>
    );
  }

  const isNeedsReassignment = status === STATUS.NEEDS_REASSIGNMENT;
  const activeIndex = isNeedsReassignment
    ? LIFECYCLE_STEPS.indexOf(STATUS.RESOLVED)
    : LIFECYCLE_STEPS.indexOf(status);

  return (
    <div>
      <div className="lifecycle-steps">
        {LIFECYCLE_STEPS.map((step, i) => (
          <div
            key={step}
            className={`lifecycle-step ${i <= activeIndex ? "done" : ""} ${
              step === status ? "current" : ""
            }`}
          >
            <div className="lifecycle-dot" />
            <p>{STEP_LABELS[step]}</p>
          </div>
        ))}
      </div>

      {isNeedsReassignment && (
        <div className="lifecycle-branch reassignment">
          <FaExclamationTriangle />
          <div>
            <p className="branch-title">Issue Not Solved</p>
            <p className="branch-subtitle">
              The customer reported this isn't resolved. It requires reassignment.
            </p>
          </div>
        </div>
      )}
    </div>
  );
}

export default ComplaintLifecycle;
export { STATUS_LABELS };