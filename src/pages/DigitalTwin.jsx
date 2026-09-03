import { useEffect, useState } from 'react';
import MainLayout from '../layouts/MainLayout';
import '../styles/pages.css';
import { apiFetch } from '../api';

export default function DigitalTwin() {
  const [twins, setTwins] = useState([]);
  useEffect(() => { apiFetch('/api/twins').then(setTwins).catch(() => setTwins([])); }, []);
  const totalTwins = twins.length;
  const activeTwins = twins.filter((t) => t.completeness >= 70).length;
  const incompleteTwins = totalTwins - activeTwins;
  const recentlyUpdated = twins[0];

  const getTwinStatusClass = (status) => {
    return status === 'ACTIVE' ? 'badge-success' : 'badge-warning';
  };

  return (
    <MainLayout title="Digital Health Twins">
      <div className="dashboard-container">
        {/* Stats Cards */}
        <div className="stats-grid">
          <div className="stat-card">
            <div className="stat-content">
              <div className="stat-label">Total Twins</div>
              <div className="stat-value">{totalTwins}</div>
            </div>
            <div className="stat-icon">🧠</div>
          </div>

          <div className="stat-card">
            <div className="stat-content">
              <div className="stat-label">Active</div>
              <div className="stat-value">{activeTwins}</div>
            </div>
            <div className="stat-icon">✓</div>
          </div>

          <div className="stat-card">
            <div className="stat-content">
              <div className="stat-label">Incomplete</div>
              <div className="stat-value">{incompleteTwins}</div>
            </div>
            <div className="stat-icon">⏳</div>
          </div>

          <div className="stat-card">
            <div className="stat-content">
              <div className="stat-label">Recently Updated</div>
              <div className="stat-value" style={{ fontSize: '1rem' }}>
                {recentlyUpdated?.lastUpdated}
              </div>
            </div>
            <div className="stat-icon">⚡</div>
          </div>
        </div>

        {/* Twins Table */}
        <div className="table-wrapper">
          <div className="table-responsive">
            <table>
              <thead>
                <tr>
                  <th>Twin ID</th>
                  <th>Patient</th>
                  <th>Completeness</th>
                  <th>Sources</th>
                  <th>Status</th>
                  <th>Last Updated</th>
                </tr>
              </thead>
              <tbody>
                {twins.map((twin) => (
                  <tr key={twin.twinId}>
                    <td>
                      <span style={{ fontWeight: '600', color: 'var(--primary)' }}>
                        {twin.twinId}
                      </span>
                    </td>
                    <td>{twin.patientId}</td>
                    <td>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                        <span>{twin.completeness || 0}%</span>
                        <div className="progress-bar" style={{ width: '100px' }}>
                          <div
                            className="progress-fill"
                            style={{ width: `${twin.completeness || 0}%` }}
                          ></div>
                        </div>
                      </div>
                    </td>
                    <td>
                      <div className="sources-list">
                        {(twin.connectedSources || []).map((source) => (
                          <div key={source} className="source-badge">
                            {source}
                          </div>
                        ))}
                      </div>
                    </td>
                    <td>
                      <span className={`badge ${getTwinStatusClass(twin.status)}`}>
                        {twin.completeness >= 70 ? 'ACTIVE' : 'INCOMPLETE'}
                      </span>
                    </td>
                    <td>{twin.lastUpdated}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </MainLayout>
  );
}
