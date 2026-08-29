import "./StatsCard.css";
function StatsCard({
  title,
  value,
  icon,
  color,
  subtitle,
}) {
  return (
    <div className="stats-card">

      <div className="stats-left">

        <p className="stats-title">
          {title}
        </p>

        <h2 className="stats-value">
          {value}
        </h2>

        <span className="stats-subtitle">
          {subtitle}
        </span>

      </div>

      <div
        className="stats-icon"
        style={{ background: color }}
      >
        {icon}
      </div>

    </div>
  );
}

export default StatsCard;