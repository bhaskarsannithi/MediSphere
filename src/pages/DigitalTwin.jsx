import React from 'react';
import MainLayout from '../layouts/MainLayout';
import { mockDigitalTwins } from '../data/mockData';
import '../styles/pages.css';

export default function DigitalTwin() {
  const totalTwins = mockDigitalTwins.length;
  const activeTwins = mockDigitalTwins.filter((t) => t.status === 'ACTIVE').length;
  const incompleteTwins = mockDigitalTwins.filter((t) => t.status === 'INCOMPLETE').length;
  const recentlyUpdated = mockDigitalTwins.slice(0, 1)[0];

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
                {mockDigitalTwins.map((twin) => (
                  <tr key={twin.id}>
                    <td>
                      <span style={{ fontWeight: '600', color: 'var(--primary)' }}>
                        {twin.id}
                      </span>
                    </td>
                    <td>{twin.patientName}</td>
                    <td>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                        <span>{twin.completeness}%</span>
                        <div className="progress-bar" style={{ width: '100px' }}>
                          <div
                            className="progress-fill"
                            style={{ width: `${twin.completeness}%` }}
                          ></div>
                        </div>
                      </div>
                    </td>
                    <td>
                      <div className="sources-list">
                        {twin.sources.map((source) => (
                          <div key={source} className="source-badge">
                            {source}
                          </div>
                        ))}
                      </div>
                    </td>
                    <td>
                      <span className={`badge ${getTwinStatusClass(twin.status)}`}>
                        {twin.status}
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
