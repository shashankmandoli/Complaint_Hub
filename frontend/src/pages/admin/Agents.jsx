import { useState } from "react";
import "./Agents.css";
import AgentDetailsModal from "./AgentDetailsModal";

const agentsData = [
  { id: "AGT-101", name: "Neha Singh", email: "neha.singh@example.com", department: "Water Supply", status: "Active", assigned: 14 },
  { id: "AGT-102", name: "Raj Malhotra", email: "raj.malhotra@example.com", department: "Electricity", status: "Active", assigned: 9 },
  { id: "AGT-103", name: "Kavita Rao", email: "kavita.rao@example.com", department: "Road Maintenance", status: "On Leave", assigned: 0 },
  { id: "AGT-104", name: "Suresh Nair", email: "suresh.nair@example.com", department: "Garbage Collection", status: "Active", assigned: 6 },
];

const Agents = () => {
  const [search, setSearch] = useState("");
  const [agents, setAgents] = useState(agentsData);
  const [selectedAgentId, setSelectedAgentId] = useState(null);

  const filteredAgents = agents.filter(
    (agent) =>
      agent.name.toLowerCase().includes(search.toLowerCase()) ||
      agent.department.toLowerCase().includes(search.toLowerCase())
  );

  const selectedAgent = agents.find((a) => a.id === selectedAgentId) || null;

  const handleReassignWorkload = (fromId, toId) => {
    setAgents((prev) => {
      const moving = prev.find((a) => a.id === fromId);
      const movedCount = moving ? moving.assigned : 0;
      return prev.map((a) => {
        if (a.id === fromId) return { ...a, assigned: 0 };
        if (a.id === toId) return { ...a, assigned: a.assigned + movedCount };
        return a;
      });
    });
  };

  return (
    <div className="agents-page">
      <div className="page-header">
        <div>
          <h1>Agent Management</h1>
          <p>View and manage all support agents</p>
        </div>
      </div>

      <div className="agents-card">
        <div className="agents-toolbar">
          <input
            type="text"
            placeholder="Search agents..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />

          <select>
            <option>Status</option>
            <option>Active</option>
            <option>On Leave</option>
          </select>
        </div>

        <div className="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Email</th>
                <th>Department</th>
                <th>Status</th>
                <th>Assigned</th>
                <th>Action</th>
              </tr>
            </thead>

            <tbody>
              {filteredAgents.map((agent) => (
                <tr key={agent.id}>
                  <td>{agent.id}</td>
                  <td>{agent.name}</td>
                  <td>{agent.email}</td>
                  <td>{agent.department}</td>
                  <td>
                    <span
                      className={`status ${agent.status
                        .replace(" ", "-")
                        .toLowerCase()}`}
                    >
                      {agent.status}
                    </span>
                  </td>
                  <td>{agent.assigned}</td>
                  <td>
                    <button
                      className="view-btn"
                      onClick={() => setSelectedAgentId(agent.id)}
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

      <AgentDetailsModal
        agent={selectedAgent}
        otherAgents={agents}
        onClose={() => setSelectedAgentId(null)}
        onReassignWorkload={handleReassignWorkload}
      />
    </div>
  );
};

export default Agents;