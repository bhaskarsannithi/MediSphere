import React from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import MainLayout from '../layouts/MainLayout';
import { mockLiveVitalsData, mockVitalTrends } from '../data/mockData';
import '../styles/pages.css';

export default function Vitals() {
  const connectedDevices = 892;
  const vitalsToday = 2450;
  const validReadings = 2398;
  const validationErrors = 52;

  const getVitalStatus = (status) => {
    if (status === 'NORMAL') return 'badge-success';
    if (status === 'MONITORING') return 'badge-warning';
    return 'badge-danger';
  };

  return (
    <MainLayout title="Real-Time Vitals">
      <div className="dashboard-container">
        {/* Demo Badge */}
        <div className="demo-badge">
          <span>⚠️</span>
          <span>Simulated Live Data</span>
        </div>

        {/* Stats Cards */}
        <div className="stats-grid">
          <div className="stat-card">
            <div className="stat-content">
              <div className="stat-label">Connected Devices</div>
              <div className="stat-value">{connectedDevices}</div>
            </div>
            <div className="stat-icon">⚙️</div>
          </div>

          <div className="stat-card">
            <div className="stat-content">
              <div className="stat-label">Vitals Today</div>
              <div className="stat-value">{vitalsToday}</div>
            </div>
            <div className="stat-icon">📈</div>
          </div>

          <div className="stat-card">
            <div className="stat-content">
              <div className="stat-label">Valid Readings</div>
              <div className="stat-value">{validReadings}</div>
            </div>
            <div className="stat-icon">✓</div>
          </div>

          <div className="stat-card">
            <div className="stat-content">
              <div className="stat-label">Validation Errors</div>
              <div className="stat-value">{validationErrors}</div>
            </div>
            <div className="stat-icon">⚠️</div>
          </div>
        </div>

        {/* Heart Rate Chart */}
        <div className="chart-card">
          <div className="chart-header">
            <div>
              <div className="chart-title">Heart Rate Trend</div>
              <div className="dashboard-subtitle" style={{ marginTop: '2px' }}>
                Population average over 24 hours
              </div>
            </div>
          </div>
          <div className="chart-container">
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={mockVitalTrends['MS-10001']?.heartRate || []}>
                <CartesianGrid strokeDasharray="3 3" stroke="var(--border-light)" />
                <XAxis dataKey="time" stroke="var(--text-secondary)" />
                <YAxis stroke="var(--text-secondary)" />
                <Tooltip />
                <Line
                  type="monotone"
                  dataKey="value"
                  stroke="var(--primary)"
                  strokeWidth={2}
                  dot={false}
                />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Recent Vitals Table */}
        <div className="dashboard-section">
          <h3 className="section-title">Recent Vital Readings</h3>
          <div className="table-wrapper">
            <div className="table-responsive">
              <table>
                <thead>
                  <tr>
                    <th>Patient Name</th>
                    <th>Heart Rate</th>
                    <th>SpO2</th>
                    <th>Temperature</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {mockLiveVitalsData.map((vital, idx) => (
                    <tr key={idx}>
                      <td>{vital.patientName}</td>
                      <td>{vital.heartRate} BPM</td>
                      <td>{vital.spO2}%</td>
                      <td>{vital.temp}°C</td>
                      <td>
                        <span className={`badge ${getVitalStatus(vital.status)}`}>
                          {vital.status}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </MainLayout>
  );
}
