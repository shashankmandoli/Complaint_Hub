import "./Dashboard.css";

import {
  FaUsers,
  FaUserTie,
  FaClipboardList,
  FaClock,
} from "react-icons/fa";


import StatsCard from "./StatsCard";
import ComplaintTable from "./ComplaintTable";
import AnalyticsCard from "./AnalyticsCard";
import RecentActivity from "./RecentActivity";
import QuickActions from "./QuickActions";


function Dashboard() {

  return (

    <div className="dashboard-content">


      {/* Statistics Cards */}

      <section className="stats-section">

        <StatsCard
          title="Total Users"
          value="1,248"
          subtitle="+18 Today"
          icon={<FaUsers />}
          color="#7C3AED"
        />


        <StatsCard
          title="Agents"
          value="42"
          subtitle="38 Active"
          icon={<FaUserTie />}
          color="#2563EB"
        />


        <StatsCard
          title="Complaints"
          value="386"
          subtitle="+12 Today"
          icon={<FaClipboardList />}
          color="#F59E0B"
        />


        <StatsCard
          title="Pending"
          value="57"
          subtitle="Needs Attention"
          icon={<FaClock />}
          color="#EF4444"
        />


      </section>



      {/* Dashboard Main Content */}


      <section className="dashboard-layout">


        {/* First Row */}

        <div className="dashboard-row dashboard-row-large">


          <div className="dashboard-card dashboard-card-wide">

            <ComplaintTable />

          </div>



          <div className="dashboard-card dashboard-card-small">

            <AnalyticsCard />

          </div>


        </div>





        {/* Second Row */}


        <div className="dashboard-row dashboard-row-small">


          <div className="dashboard-card dashboard-card-wide">

            <RecentActivity />

          </div>



          <div className="dashboard-card dashboard-card-small">

            <QuickActions />

          </div>


        </div>



      </section>



    </div>

  );

}


export default Dashboard;