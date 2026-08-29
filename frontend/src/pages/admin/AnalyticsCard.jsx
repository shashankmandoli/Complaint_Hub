import "./AnalyticsCard.css";

import {
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
} from "recharts";

const data = [
  { name: "Resolved", value: 329, color: "#10B981" },
  { name: "In Progress", value: 57, color: "#3B82F6" },
  { name: "Pending", value: 28, color: "#F59E0B" },
];

const total = data.reduce((sum, item) => sum + item.value, 0);

function AnalyticsCard() {
  return (
    <div className="analytics-card">

      <div className="analytics-header">
        <h2>Complaint Status</h2>
        <p>Overall complaint distribution</p>
      </div>

      <div className="analytics-chart">

        <ResponsiveContainer width="100%" height="100%">
          <PieChart>

            <Pie
              data={data}
              cx="50%"
              cy="50%"
              innerRadius={48}
              outerRadius={68}
              paddingAngle={3}
              dataKey="value"
            >
              {data.map((item) => (
                <Cell
                  key={item.name}
                  fill={item.color}
                />
              ))}
            </Pie>

          </PieChart>
        </ResponsiveContainer>

      </div>

      <div className="analytics-total">
        <h3>{total}</h3>
        <p>Total Complaints</p>
      </div>

      <div className="analytics-legend">

        {data.map((item) => (

          <div
            key={item.name}
            className="legend-item"
          >

            <div className="legend-left">

              <span
                className="legend-dot"
                style={{
                  background: item.color,
                }}
              />

              <span>{item.name}</span>

            </div>

            <strong>{item.value}</strong>

          </div>

        ))}

      </div>

    </div>
  );
}

export default AnalyticsCard;