import { useState } from "react";
import { FaCheck, FaTimes } from "react-icons/fa";
import "./ResolutionConfirmation.css";

// Shown only to the customer when a complaint is RESOLVED. This is a
// confirmation step, not a generic edit — there is deliberately no
// "Reopen Complaint" option here (see mockComplaints.confirmResolution).
function ResolutionConfirmation({ onConfirm }) {
  const [submitting, setSubmitting] = useState(false);

  const handleClick = (solved) => {
    setSubmitting(true);
    onConfirm(solved);
  };

  return (
    <div className="resolution-card">
      <p className="resolution-question">Is your issue solved?</p>
      <div className="resolution-actions">
        <button
          type="button"
          className="resolution-btn yes"
          disabled={submitting}
          onClick={() => handleClick(true)}
        >
          <FaCheck /> Yes, Issue Is Solved
        </button>
        <button
          type="button"
          className="resolution-btn no"
          disabled={submitting}
          onClick={() => handleClick(false)}
        >
          <FaTimes /> No, Issue Is Not Solved
        </button>
      </div>
    </div>
  );
}

export default ResolutionConfirmation;