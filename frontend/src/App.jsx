import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import AdminLayout from "./components/layout/AdminLayout.jsx";
import PortalLayout from "./components/layout/PortalLayout.jsx";

import Login from "./pages/auth/Login.jsx";

import Dashboard from "./pages/admin/Dashboard.jsx";
import Complaints from "./pages/admin/Complaints.jsx";
import UserManagement from "./pages/admin/UserManagement.jsx";
import Agents from "./pages/admin/Agents.jsx";
import Analytics from "./pages/admin/Analytics.jsx";
import Reports from "./pages/admin/Reports.jsx";
import Notifications from "./pages/admin/Notifications.jsx";

import RaiseComplaint from "./pages/customer/RaiseComplaint.jsx";
import MyComplaints from "./pages/customer/MyComplaints.jsx";
import ComplaintDetails from "./pages/customer/ComplaintDetails.jsx";

import AgentDashboard from "./pages/agent/Dashboard.jsx";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route path="/login" element={<Login />} />

        {/* Admin */}
        <Route path="/admin" element={<AdminLayout />}>
          <Route path="dashboard" element={<Dashboard />} />
          <Route path="complaints" element={<Complaints />} />
          <Route path="users" element={<UserManagement />} />
          <Route path="agents" element={<Agents />} />
          <Route path="analytics" element={<Analytics />} />
          <Route path="reports" element={<Reports />} />
          <Route path="notifications" element={<Notifications />} />
        </Route>

        {/* Customer */}
        <Route path="/customer" element={<PortalLayout role="customer" />}>
          <Route index element={<Navigate to="complaints" replace />} />
          <Route path="raise" element={<RaiseComplaint />} />
          <Route path="complaints" element={<MyComplaints />} />
          <Route path="complaints/:id" element={<ComplaintDetails />} />
        </Route>

        {/* Agent */}
        <Route path="/agent" element={<PortalLayout role="agent" />}>
          <Route path="dashboard" element={<AgentDashboard />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;