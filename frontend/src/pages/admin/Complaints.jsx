import React, { useEffect, useState } from "react";
import "./Complaints.css";
import ComplaintDetailsModal from "./ComplaintDetailsModal";
import { getComplaints, STATUS, STATUS_LABELS } from "../../utils/mockComplaints";

const STATUS_CLASS = {
  [STATUS.OPEN]: "pending",
  [STATUS.ASSIGNED]: "assigned",
  [STATUS.IN_PROGRESS]: "in-progress",
  [STATUS.RESOLVED]: "resolved",
  [STATUS.CLOSED]: "closed",
  [STATUS.REJECTED]: "rejected",
  [STATUS.NEEDS_REASSIGNMENT]: "reassignment",
};

const Complaints = () => {
  const [search, setSearch] = useState("");
  const [statusFilter, setStatusFilter] = useState("All");
  const [priorityFilter, setPriorityFilter] = useState("All");
  const [complaints, setComplaints] = useState([]);
  const [selectedId, setSelectedId] = useState(null);

  const refresh = () => setComplaints(getComplaints());

  useEffect(() => {
    // TODO: replace with a real GET call via services/api.js once the backend is ready.
    refresh();
  }, []);

  const filteredComplaints = complaints.filter((item) => {
    const matchesSearch =
      item.title.toLowerCase().includes(search.toLowerCase()) ||
      (item.createdBy || "").toLowerCase().includes(search.toLowerCase());
    const matchesStatus = statusFilter === "All" || item.status === statusFilter;
    const matchesPriority = priorityFilter === "All" || item.priority === priorityFilter;
    return matchesSearch && matchesStatus && matchesPriority;
  });

  const selectedComplaint = complaints.find((c) => String(c.id) === String(selectedId)) || null;

  return (
    <div className="complaints-page">
      <div className="page-header">
        <div>
          <h1>Complaint Management</h1>
          <p>Manage, track and resolve customer complaints</p>
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

          <select value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
            <option value="All">Status</option>
            {Object.entries(STATUS_LABELS).map(([value, label]) => (
              <option key={value} value={value}>
                {label}
              </option>
            ))}
          </select>

          <select value={priorityFilter} onChange={(e) => setPriorityFilter(e.target.value)}>
            <option value="All">Priority</option>
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
              {filteredComplaints.length === 0 ? (
                <tr>
                  <td colSpan={7} className="empty-row">
                    No complaints match your filters.
                  </td>
                </tr>
              ) : (
                filteredComplaints.map((item) => (
                  <tr key={item.id}>
                    <td>#{item.id}</td>
                    <td>{item.title}</td>
                    <td>{item.createdBy}</td>
                    <td>{item.category}</td>

                    <td>
                      <span className={`priority ${item.priority.toLowerCase()}`}>
                        {item.priority}
                      </span>
                    </td>

                    <td>
                      <span className={`status ${STATUS_CLASS[item.status]}`}>
                        {STATUS_LABELS[item.status]}
                      </span>
                    </td>

                    <td>
                      <button className="view-btn" onClick={() => setSelectedId(item.id)}>
                        View
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      <ComplaintDetailsModal
        complaintId={selectedComplaint ? selectedComplaint.id : null}
        onClose={() => setSelectedId(null)}
        onChanged={refresh}
      />
    </div>
  );
};

export default Complaints;