import { Outlet } from "react-router-dom";
import Sidebar from "./Sidebar";
import "./PortalLayout.css";

function PortalLayout({ role }) {
  return (
    <div className="portal-dashboard">
      <Sidebar role={role} />
      <main className="portal-main">
        <Outlet />
      </main>
    </div>
  );
}

export default PortalLayout;