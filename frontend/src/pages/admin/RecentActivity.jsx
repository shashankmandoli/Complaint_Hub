import "./RecentActivity.css";

const activities = [
  {
    id: 1,
    user: "Rahul Sharma",
    action: "raised a new complaint",
    time: "5 min ago",
    color: "#7C3AED",
  },
  {
    id: 2,
    user: "Agent Neha",
    action: "resolved Complaint #CMP-1042",
    time: "18 min ago",
    color: "#10B981",
  },
  {
    id: 3,
    user: "Priya Verma",
    action: "updated complaint details",
    time: "42 min ago",
    color: "#3B82F6",
  },
  {
    id: 4,
    user: "System",
    action: "assigned complaint to Agent Raj",
    time: "1 hour ago",
    color: "#F59E0B",
  },
];

function RecentActivity() {
  return (
    <div className="activity-card">

      <div className="activity-header">
        <div>
          <h2>Recent Activity</h2>
          <p>Latest updates across the portal</p>
        </div>
      </div>

      <div className="activity-list">

        {activities.map((item) => (
          <div
            className="activity-item"
            key={item.id}
          >
            <div
              className="activity-dot"
              style={{ background: item.color }}
            />

            <div className="activity-content">
              <h4>{item.user}</h4>
              <p>{item.action}</p>
            </div>

            <span>{item.time}</span>
          </div>
        ))}

      </div>

    </div>
  );
}

export default RecentActivity;