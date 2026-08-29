import React, { useState } from "react";
import "./Complaints.css";
import ComplaintDetailsModal from "./ComplaintDetailsModal.jsx";

const complaintsData = [
  {
    id: "#1024",
    title: "Internet Connectivity Issue",
    user: "Rahul Sharma",
    category: "Network",
    priority: "High",
    status: "Pending",
    agent: null,
  },
  {
    id: "#1025",
    title: "Payment Failure",
    user: "Priya Singh",
    category: "Billing",
    priority: "Medium",
    status: "In Progress",
    agent: { name: "Neha Singh", status: "accepted" },
  },
  {
    id: "#1026",
    title: "Account Login Problem",
    user: "Amit Verma",
    category: "Account",
    priority: "Low",
    status: "Resolved",
    agent: { name: "Raj Malhotra", status: "accepted" },
  },
];

const Complaints = () => {
  const [search, setSearch] = useState("");
  const [complaints, setComplaints] = useState(complaintsData);
  const [selectedComplaint, setSelectedComplaint] = useState(null);

  const filteredComplaints = complaints.filter((item) => (
    item.title.toLowerCase().includes(search.toLowerCase()) ||
    item.user.toLowerCase().includes(search.toLowerCase())
  ));

  const handleReassign = (complaintId, agentName) => {
    setComplaints((prev) =>
      prev.map((item) =>
        item.id === complaintId
          ? { ...item, agent: { name: agentName, status: "pending" }, status: "In Progress" }
          : item
      )
    );
  };

  return (
    <div className="complaints-page">

      <div className="page-header">
        <div>
          <h1>Complaint Management</h1>
          <p>
            Manage, track and resolve customer complaints
          </p>
        </div>
      </div>

      <div className="complaint-card">

        <div className="complaint-toolbar">
          <input
            type="text"
            placeholder="Search complaints..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />

          <select>
            <option>Status</option>
            <option>Pending</option>
            <option>In Progress</option>
            <option>Resolved</option>
          </select>

          <select>
            <option>Priority</option>
            <option>High</option>
            <option>Medium</option>
            <option>Low</option>
          </select>
        </div>

        <div className="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Complaint</th>
                <th>User</th>
                <th>Category</th>
                <th>Priority</th>
                <th>Status</th>
                <th>Action</th>
              </tr>
            </thead>

            <tbody>
              {filteredComplaints.map((item, index) => (
                <tr key={index}>
                  <td>{item.id}</td>
                  <td>{item.title}</td>
                  <td>{item.user}</td>
                  <td>{item.category}</td>

                  <td>
                    <span className={`priority ${item.priority.toLowerCase()}`}>
                      {item.priority}
                    </span>
                  </td>

                  <td>
                    <span className={`status ${item.status.replace(" ", "-").toLowerCase()}`}>
                      {item.status}
                    </span>
                  </td>

                  <td>
                    <button
                      className="view-btn"
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

      </div>

      <ComplaintDetailsModal
        complaint={selectedComplaint}
        onClose={() => setSelectedComplaint(null)}
        onReassign={handleReassign}
      />

    </div>
  );
};

export default Complaints;