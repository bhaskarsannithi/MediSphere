import { useEffect, useState } from 'react';
import { Activity, Bell, Check, RefreshCw } from 'lucide-react';
import MainLayout from '../layouts/MainLayout';
import { apiFetch } from '../api';
import '../styles/pages.css';

const severityClass = (severity) => {
  if (severity === 'CRITICAL' || severity === 'HIGH') return 'badge-danger';
  if (severity === 'MEDIUM') return 'badge-warning';
  return 'badge-info';
};

const formatAcknowledgement = (milliseconds) => {
  if (!milliseconds) return 'N/A';
  const totalSeconds = Math.round(milliseconds / 1000);
  const minutes = Math.floor(totalSeconds / 60);
  const seconds = totalSeconds % 60;
  return minutes ? `${minutes}m ${seconds}s` : `${seconds}s`;
};

export default function Monitoring() {
  const [alerts, setAlerts] = useState([]);
  const [events, setEvents] = useState([]);
  const [stats, setStats] = useState({});
  const [error, setError] = useState('');
  const [lastUpdated, setLastUpdated] = useState(null);

  const loadMonitoring = () => Promise.all([apiFetch('/api/alerts'), apiFetch('/api/alerts/statistics'), apiFetch('/api/alerts/events')])
    .then(([activeAlerts, statistics, recentEvents]) => {
      setAlerts(activeAlerts);
      setStats(statistics);
      setEvents(recentEvents);
      setLastUpdated(new Date());
      setError('');
    })
    .catch((requestError) => setError(requestError.message));

  useEffect(() => {
    loadMonitoring();
    const interval = window.setInterval(loadMonitoring, 5000);
    return () => window.clearInterval(interval);
  }, []);

  const acknowledge = (alertId) => apiFetch(`/api/alerts/${alertId}/acknowledge`, { method: 'POST' })
    .then(loadMonitoring)
    .catch((requestError) => setError(requestError.message));

  const updateStatus = (alertId, status) => apiFetch(`/api/alerts/${alertId}/${status}`, { method: 'POST' })
    .then(loadMonitoring)
    .catch((requestError) => setError(requestError.message));

  return (
    <MainLayout title="Real-Time Health Monitoring">
      <div className="dashboard-container monitoring-page">
        <div className="monitoring-heading">
          <div>
            <p className="eyebrow"><Activity size={15} /> Kafka vital stream</p>
            <p className="dashboard-subtitle">Validated wearable events, explainable anomaly detection, and clinical alert workflow.</p>
          </div>
          <button className="action-btn monitoring-refresh" onClick={loadMonitoring} title="Refresh monitoring data">
            <RefreshCw size={15} /> Refresh
          </button>
        </div>

        {error && <p className="error-message">{error}</p>}
        <div className="stats-grid">
          <div className="stat-card"><div className="stat-content"><div className="stat-label">Active alerts</div><div className="stat-value">{stats.activeAlerts ?? 0}</div></div><Bell className="stat-icon" /></div>
          <div className="stat-card"><div className="stat-content"><div className="stat-label">Total critical alerts</div><div className="stat-value">{stats.criticalAlerts ?? 0}</div></div><Bell className="stat-icon" /></div>
          <div className="stat-card"><div className="stat-content"><div className="stat-label">Suppressed duplicates</div><div className="stat-value">{stats.suppressedAlerts ?? 0}</div></div><Check className="stat-icon" /></div>
          <div className="stat-card"><div className="stat-content"><div className="stat-label">Avg acknowledgement</div><div className="stat-value">{formatAcknowledgement(stats.averageAcknowledgementMs)}</div></div><Activity className="stat-icon" /></div>
        </div>

        <div className="table-wrapper">
          <div className="monitoring-table-header"><div><h3 className="section-title">Active Alerts</h3><p className="section-subtitle">Updates every five seconds. Values are sourced from persisted monitoring events.</p></div><span className="live-indicator">LIVE {lastUpdated ? lastUpdated.toLocaleTimeString() : ''}</span></div>
          <div className="table-responsive">
            <table>
              <thead><tr><th>Patient</th><th>Vital</th><th>Observed</th><th>Severity</th><th>Detection</th><th>Assigned</th><th>Notice</th><th>Status</th><th /></tr></thead>
              <tbody>
                {alerts.map((alert) => (
                  <tr key={alert.alertId || alert.id}>
                    <td>{alert.patientId}</td><td>{alert.vitalType}</td><td>{alert.observedValue}</td>
                    <td><span className={`badge ${severityClass(alert.severity)}`}>{alert.severity}</span></td>
                    <td><small>{alert.detectionMethod}<br />score {Number(alert.anomalyScore || 0).toFixed(2)}</small></td>
                    <td>{alert.assignedTo}</td><td>{alert.notificationStatus || 'N/A'}</td><td><span className="badge badge-warning">{alert.status}</span></td>
                    <td className="action-buttons">{alert.status === 'NEW' && <button className="action-btn" onClick={() => acknowledge(alert.alertId || alert.id)}>Acknowledge</button>}{alert.status === 'ACKNOWLEDGED' && <><button className="action-btn" onClick={() => updateStatus(alert.alertId || alert.id, 'RESOLVED')}>Resolve</button><button className="action-btn" onClick={() => updateStatus(alert.alertId || alert.id, 'DISMISSED')}>Dismiss</button></>}</td>
                  </tr>
                ))}
                {!alerts.length && <tr><td colSpan="9" className="empty-state">No active alerts have been persisted.</td></tr>}
              </tbody>
            </table>
          </div>
        </div>

        <div className="table-wrapper">
          <div className="monitoring-table-header"><div><h3 className="section-title">Recent Monitoring Events</h3><p className="section-subtitle">Latest validated vital readings received by the backend.</p></div></div>
          <div className="table-responsive">
            <table>
              <thead><tr><th>Patient</th><th>Vital</th><th>Value</th><th>Unit</th><th>Source</th><th>Validation</th><th>Timestamp</th></tr></thead>
              <tbody>
                {events.map((event) => <tr key={event.vitalId || event.id}><td>{event.patientId}</td><td>{event.type}</td><td>{event.value}</td><td>{event.unit}</td><td>{event.source}</td><td><span className="badge badge-success">{event.validationStatus}</span></td><td>{event.timestamp}</td></tr>)}
                {!events.length && <tr><td colSpan="7" className="empty-state">No validated monitoring events have been persisted.</td></tr>}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </MainLayout>
  );
}