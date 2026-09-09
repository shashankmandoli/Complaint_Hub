import { Outlet } from "react-router-dom";

import Sidebar from "./Sidebar";
import AdminTopbar from "../../pages/admin/AdminTopbar";


function AdminLayout(){

    return (

        <div className="admin-dashboard">

            <Sidebar role="admin" />


            <main className="admin-main">

                <AdminTopbar />


                <Outlet />


            </main>


        </div>

    );

}


export default AdminLayout;