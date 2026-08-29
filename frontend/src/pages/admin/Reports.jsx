import { useState } from "react";
import {
  FaFileAlt,
  FaDownload,
  FaCalendarAlt,
  FaChartBar,
  FaTimes,
  FaCheckCircle,
} from "react-icons/fa";
import "./Reports.css";
import "../../components/common/Modal.css";

const reportTypes = [
  {
    id: "RPT-01",
    title: "Complaint Summary Report",
    description: "Overview of all complaints by status and category",
    icon: <FaChartBar />,
  },
  {
    id: "RPT-02",
    title: "Agent Performance Report",
    description: "Resolution times and workload per agent",
    icon: <FaFileAlt />,
  },
  {
    id: "RPT-03",
    title: "Monthly Activity Report",
    description: "Complaints raised, resolved, and pending by month",
    icon: <FaCalendarAlt />,
  },
];

const initialRecentReports = [
  { name: "Complaint Summary - July 2026", generated: "01 Aug 2026", size: "212 KB" },
  { name: "Agent Performance - July 2026", generated: "01 Aug 2026", size: "148 KB" },
  { name: "Monthly Activity - June 2026", generated: "02 Jul 2026", size: "196 KB" },
];

function downloadTextFile(filename, content) {
  const blob = new Blob([content], { type: "text/plain" });
  const url = URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;
  link.download = filename;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  URL.revokeObjectURL(url);
}

const Reports = () => {
  const [range, setRange] = useState("This Month");
  const [recentReports, setRecentReports] = useState(initialRecentReports);
  const [generatedReport, setGeneratedReport] = useState(null);

  const handleGenerate = (report) => {
    const today = new Date().toLocaleDateString("en-GB", {
      day: "2-digit",
      month: "short",
      year: "numeric",
    });
    const sizeKb = Math.floor(120 + Math.random() * 150);
    const fileName = `${report.title.replace(" Report", "")} - ${range}`;

    const newEntry = { name: fileName, generated: today, size: `${sizeKb} KB` };
    setRecentReports((prev) => [newEntry, ...prev]);
    setGeneratedReport(newEntry);
  };

  const handleDownload = (report) => {
    const content =
      `${report.name}\n` +
      `Generated: ${report.generated}\n` +
      `Size: ${report.size}\n\n` +
      `This is a placeholder export from the Complaint Portal (frontend demo, no backend connected).`;
    downloadTextFile(`${report.name.replace(/\s+/g, "_")}.txt`, content);
  };

  return (
    <div className="reports-page">
      <div className="page-header">
        <div>
          <h1>Reports</h1>
          <p>Generate and download portal activity reports</p>
        </div>

        <select
          className="range-select"
          value={range}
          onChange={(e) => setRange(e.target.value)}
        >
          <option>This Week</option>
          <option>This Month</option>
          <option>This Quarter</option>
          <option>This Year</option>
        </select>
      </div>

      <div className="report-types-grid">
        {reportTypes.map((report) => (
          <div className="report-type-card" key={report.id}>
            <div className="report-icon">{report.icon}</div>
            <div className="report-info">
              <h3>{report.title}</h3>
              <p>{report.description}</p>
            </div>
            <button
              className="generate-btn"
              onClick={() => handleGenerate(report)}
            >
              Generate
            </button>
          </div>
        ))}
      </div>

      <div className="recent-reports-card">
        <h3>Recently Generated</h3>

        <div className="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>Report Name</th>
                <th>Generated On</th>
                <th>Size</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {recentReports.map((r, index) => (
                <tr key={index}>
                  <td>{r.name}</td>
                  <td>{r.generated}</td>
                  <td>{r.size}</td>
                  <td>
                    <button
                      className="download-btn"
                      onClick={() => handleDownload(r)}
                    >
                      <FaDownload /> Download
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {generatedReport && (
        <div className="modal-overlay" onClick={() => setGeneratedReport(null)}>
          <div className="modal-box" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Report Generated</h2>
              <button
                className="modal-close"
                onClick={() => setGeneratedReport(null)}
              >
                <FaTimes />
              </button>
            </div>

            <div className="modal-body">
              <div className="action-success">
                <div className="success-icon">
                  <FaCheckCircle />
                </div>
                <h3>{generatedReport.name}</h3>
                <p>{generatedReport.size} &middot; generated {generatedReport.generated}</p>
              </div>
            </div>

            <div className="modal-footer">
              <button
                className="modal-secondary-btn"
                onClick={() => setGeneratedReport(null)}
              >
                Close
              </button>
              <button
                className="modal-primary-btn"
                onClick={() => {
                  handleDownload(generatedReport);
                  setGeneratedReport(null);
                }}
              >
                Download
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Reports;