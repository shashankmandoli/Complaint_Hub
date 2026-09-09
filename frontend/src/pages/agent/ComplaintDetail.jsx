import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { FaArrowLeft } from "react-icons/fa";
import { getComplaintById } from "../../utils/mockComplaints";
import ComplaintDetailPanel from "../../components/complaint/ComplaintDetailPanel";
import "../customer/ComplaintDetails.css";

const CURRENT_AGENT_NAME = "Neha Singh";

function AgentComplaintDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [exists, setExists] = useState(undefined); // undefined = loading

  useEffect(() => {
    setExists(!!getComplaintById(id));
  }, [id]);

  return (
    <div className="details-page">
      <button className="back-btn" onClick={() => navigate("/agent/dashboard")}>
        <FaArrowLeft /> Back to Assigned Complaints
      </button>

      {exists === false ? (
        <div className="not-found">Complaint not found.</div>
      ) : exists === true ? (
        <ComplaintDetailPanel complaintId={id} role="agent" currentUserName={CURRENT_AGENT_NAME} />
      ) : null}
    </div>
  );
}

export default AgentComplaintDetail;