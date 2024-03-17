import React, {useState} from "react";
import DashboardTab from "./DashboardTab";
import ManagementTab from "./ManagementTab";
import SearchTab from "./SearchTab";

const TabNavItems = () => {
    const [activeTab, setActiveTab] = useState('dashboard');

    const dashboardContent = activeTab === 'dashboard' ? <DashboardTab/> : null;
    const managementContent = activeTab === 'management' ? <ManagementTab/> : null;
    const searchContent = activeTab === 'search' ? <SearchTab/> : null;

    const switchToDashboard = () => {
        setActiveTab("dashboard");
    }

    const switchToManagement = () => {
        setActiveTab("management");
    }

    const switchToSearch = () => {
        setActiveTab("search");
    }

    return (
        <div className="Tabs">
            <ul className="nav">
                <li
                    className={activeTab === 'dashboard' ? 'active' : ''}
                    onClick={switchToDashboard}>
                    Dashboard
                </li>
                <li
                    className={activeTab === 'management' ? 'active' : ''}
                    onClick={switchToManagement}>
                    Management
                </li>

                <li
                    className={activeTab === 'search' ? 'active' : ''}
                    onClick={switchToSearch}>
                    Search
                </li>
            </ul>
            <div className="outlet">
                {dashboardContent}
                {managementContent}
                {searchContent}
            </div>
        </div>
    );
}

export default TabNavItems;