import "./Dashboard.css";
import {
  FaStar,
  FaCheckCircle,
} from "react-icons/fa";

const agents = [
  {
    name: "Amit Verma",
    department: "Water Department",
    complaints: 54,
    resolution: "98%",
    image: "https://i.pravatar.cc/100?img=15",
  },
  {
    name: "Priya Singh",
    department: "Electricity",
    complaints: 49,
    resolution: "96%",
    image: "https://i.pravatar.cc/100?img=25",
  },
  {
    name: "Rohan Mehta",
    department: "Road Maintenance",
    complaints: 42,
    resolution: "94%",
    image: "https://i.pravatar.cc/100?img=35",
  },
];

function TopAgents() {
  return (
    <div className="agents-card">

      <div className="card-header">

        <div>
          <h2>Top Performing Agents</h2>
          <p>Highest complaint resolution this month</p>
        </div>

      </div>

      <div className="agents-list">

        {agents.map((agent, index) => (

          <div
            className="agent-card"
            key={index}
          >

            <img
              src={agent.image}
              alt={agent.name}
            />

            <div className="agent-info">

              <h4>{agent.name}</h4>

              <p>{agent.department}</p>

              <div className="agent-stats">

                <span>
                  <FaCheckCircle />
                  {agent.complaints} Complaints
                </span>

                <span>
                  <FaStar />
                  {agent.resolution}
                </span>

              </div>

            </div>

          </div>

        ))}

      </div>

    </div>
  );
}

export default TopAgents;