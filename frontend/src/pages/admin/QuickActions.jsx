import "./QuickActions.css";
import { useNavigate } from "react-router-dom";

import {
  FaUserPlus,
  FaClipboardList,
  FaChartBar,
  FaFileDownload,
} from "react-icons/fa";


const actions = [
  {
    icon: <FaUserPlus />,
    title: "Add Agent",
    path: "/admin/agents",
  },
  {
    icon: <FaClipboardList />,
    title: "Complaints",
    path: "/admin/complaints",
  },
  {
    icon: <FaChartBar />,
    title: "Analytics",
    path: "/admin/analytics",
  },
  {
    icon: <FaFileDownload />,
    title: "Reports",
    path: "/admin/reports",
  },
];


function QuickActions(){
  const navigate = useNavigate();

  return(

    <div className="quick-actions-card">


      <div className="quick-header">

        <h2>
          Quick Actions
        </h2>

      </div>



      <div className="quick-list">


        {
          actions.map((item)=>(

            <button
              className="quick-item"
              key={item.title}
              onClick={() => navigate(item.path)}
            >

              <span className="quick-icon">
                {item.icon}
              </span>


              <span>
                {item.title}
              </span>


            </button>

          ))
        }


      </div>


    </div>

  );

}


export default QuickActions;