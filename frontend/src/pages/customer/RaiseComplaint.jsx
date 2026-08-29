import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { FaPaperPlane, FaTag, FaHistory } from "react-icons/fa";
import { addComplaint, getComplaints } from "../../utils/mockComplaints.js";
import "./RaiseComplaint.css";

const statusClass = {
  Pending: "status pending",
  "In Progress": "status progress",
  Resolved: "status resolved",
};

function RaiseComplaint() {
  const navigate = useNavigate();
  const [title, setTitle] = useState("");
  const [category, setCategory] = useState("Plumbing");
  const [description, setDescription] = useState("");
  const [submitted, setSubmitted] = useState(false);
  const [complaints, setComplaints] = useState([]);

  useEffect(() => {
    // TODO: replace with a real GET call via services/api.js once the backend is ready.
    setComplaints(getComplaints());
  }, []);

  const categoryCounts = complaints.reduce((acc, c) => {
    acc[c.category] = (acc[c.category] || 0) + 1;
    return acc;
  }, {});

  const recent = complaints.slice(0, 3);

  const handleSubmit = (e) => {
    e.preventDefault();
    // TODO: replace with a real POST call via services/api.js once the backend is ready.
    addComplaint({ title, category, description });
    setSubmitted(true);
    setTimeout(() => navigate("/customer/complaints"), 800);
  };

  return (
    <div className="raise-page">
      <div className="page-header">
        <h1>Raise a Complaint</h1>
        <p>Tell us what went wrong and we'll get an agent on it.</p>
      </div>

      <div className="raise-layout">
        <form className="raise-card" onSubmit={handleSubmit}>
          <label>Title</label>
          <input
            type="text"
            placeholder="e.g. Water leakage in Block B"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            required
          />

          <label>Category</label>
          <select value={category} onChange={(e) => setCategory(e.target.value)}>
            <option>Plumbing</option>
            <option>Electrical</option>
            <option>Civil</option>
            <option>Billing</option>
            <option>Other</option>
          </select>

          <label>Description</label>
          <textarea
            placeholder="Describe the issue in detail..."
            rows={5}
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            required
          />

          <button type="submit" className="submit-btn">
            <FaPaperPlane />
            {submitted ? "Submitted!" : "Submit Complaint"}
          </button>
        </form>

        <div className="raise-summary">
          <div className="summary-card">
            <h4>
              <FaTag /> Your Complaint Categories
            </h4>
            {Object.keys(categoryCounts).length === 0 ? (
              <p className="summary-empty">No complaints raised yet.</p>
            ) : (
              <div className="category-pills">
                {Object.entries(categoryCounts).map(([cat, count]) => (
                  <span className="category-pill" key={cat}>
                    {cat} <b>{count}</b>
                  </span>
                ))}
              </div>
            )}
          </div>

          <div className="summary-card">
            <h4>
              <FaHistory /> Recently Raised
            </h4>
            {recent.length === 0 ? (
              <p className="summary-empty">
                Nothing here yet — your first complaint will show up.
              </p>
            ) : (
              <div className="recent-list">
                {recent.map((c) => (
                  <div className="recent-item" key={c.id}>
                    <div>
                      <p className="recent-title">{c.title}</p>
                      <p className="recent-date">{c.createdAt}</p>
                    </div>
                    <span className={statusClass[c.status] || "status pending"}>
                      {c.status}
                    </span>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default RaiseComplaint;