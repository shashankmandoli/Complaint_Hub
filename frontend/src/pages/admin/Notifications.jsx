import { useState } from "react";
import {
  FaExclamationCircle,
  FaCheckCircle,
  FaUserPlus,
  FaClipboardList,
} from "react-icons/fa";
import "./Notifications.css";

const notificationsData = [
  {
    id: 1,
    icon: <FaExclamationCircle />,
    type: "alert",
    title: "High priority complaint raised",
    message: "Rahul Sharma reported a Water Supply issue marked High priority.",
    time: "5 min ago",
    read: false,
  },
  {
    id: 2,
    icon: <FaCheckCircle />,
    type: "success",
    title: "Complaint resolved",
    message: "Agent Neha resolved complaint #CMP-1042.",
    time: "18 min ago",
    read: false,
  },
  {
    id: 3,
    icon: <FaUserPlus />,
    type: "info",
    title: "New agent onboarded",
    message: "Raj Malhotra joined the Electricity department.",
    time: "1 hour ago",
    read: true,
  },
  {
    id: 4,
    icon: <FaClipboardList />,
    type: "info",
    title: "Complaint reassigned",
    message: "Complaint #CMP-1038 was reassigned to Agent Kavita.",
    time: "3 hours ago",
    read: true,
  },
];

const Notifications = () => {
  const [notifications, setNotifications] = useState(notificationsData);

  const markAllRead = () => {
    setNotifications((prev) => prev.map((n) => ({ ...n, read: true })));
  };

  const unreadCount = notifications.filter((n) => !n.read).length;

  return (
    <div className="notifications-page">
      <div className="page-header">
        <div>
          <h1>Notifications</h1>
          <p>
            {unreadCount > 0
              ? `You have ${unreadCount} unread notification${unreadCount > 1 ? "s" : ""}`
              : "You're all caught up"}
          </p>
        </div>

        <button className="mark-read-btn" onClick={markAllRead}>
          Mark all as read
        </button>
      </div>

      <div className="notifications-card">
        {notifications.map((n) => (
          <div
            className={`notification-item ${n.type} ${n.read ? "" : "unread"}`}
            key={n.id}
          >
            <div className="notification-icon">{n.icon}</div>

            <div className="notification-content">
              <h4>{n.title}</h4>
              <p>{n.message}</p>
            </div>

            <span className="notification-time">{n.time}</span>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Notifications;