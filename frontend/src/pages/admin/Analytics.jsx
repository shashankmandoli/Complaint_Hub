import "./Analytics.css";

import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
  LineChart,
  Line,
  Legend,
} from "recharts";

const categoryData = [
  { name: "Water Supply", complaints: 92 },
  { name: "Electricity", complaints: 78 },
  { name: "Road Damage", complaints: 54 },
  { name: "Garbage", complaints: 61 },
  { name: "Street Light", complaints: 39 },
];

const statusData = [
  { name: "Resolved", value: 329, color: "#22c55e" },
  { name: "In Progress", value: 57, color: "#3b82f6" },
  { name: "Pending", value: 28, color: "#f59e0b" },
];

const trendData = [
  { month: "Mar", complaints: 210 },
  { month: "Apr", complaints: 248 },
  { month: "May", complaints: 265 },
  { month: "Jun", complaints: 301 },
  { month: "Jul", complaints: 340 },
  { month: "Aug", complaints: 386 },
];

const Analytics = () => {
  return (
    <div className="analytics-page">
      <div className="page-header">
        <div>
          <h1>Analytics</h1>
          <p>Insights into complaint trends and performance</p>
        </div>
      </div>

      <div className="analytics-grid">
        <div className="analytics-card wide">
          <h3>Complaints by Category</h3>
          <ResponsiveContainer width="100%" height={280}>
            <BarChart data={categoryData}>
              <CartesianGrid strokeDasharray="3 3" vertical={false} />
              <XAxis dataKey="name" tick={{ fontSize: 12 }} />
              <YAxis tick={{ fontSize: 12 }} />
              <Tooltip />
              <Bar dataKey="complaints" fill="#7c3aed" radius={[6, 6, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>

        <div className="analytics-card">
          <h3>Status Distribution</h3>
          <ResponsiveContainer width="100%" height={280}>
            <PieChart>
              <Pie
                data={statusData}
                dataKey="value"
                nameKey="name"
                innerRadius={60}
                outerRadius={90}
                paddingAngle={3}
              >
                {statusData.map((entry, index) => (
                  <Cell key={index} fill={entry.color} />
                ))}
              </Pie>
              <Tooltip />
              <Legend />
            </PieChart>
          </ResponsiveContainer>
        </div>

        <div className="analytics-card wide">
          <h3>Monthly Complaint Trend</h3>
          <ResponsiveContainer width="100%" height={280}>
            <LineChart data={trendData}>
              <CartesianGrid strokeDasharray="3 3" vertical={false} />
              <XAxis dataKey="month" tick={{ fontSize: 12 }} />
              <YAxis tick={{ fontSize: 12 }} />
              <Tooltip />
              <Line
                type="monotone"
                dataKey="complaints"
                stroke="#7c3aed"
                strokeWidth={3}
                dot={{ r: 4 }}
              />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  );
};

export default Analytics;