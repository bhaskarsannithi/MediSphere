import React from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, BarChart, Bar } from 'recharts';
import MainLayout from '../layouts/MainLayout';
import {
  mockDashboardStats,
  mockPatientActivityChart,
  mockVitalMonitoringChart,
  mockSystemStatus,
  mockRecentActivity,
} from '../data/mockData';
import '../styles/pages.css';

export default function Dashboard() {
  return (
    <MainLayout title="Clinical Overview">
      <div className="dashboard-container">
        {/* Demo Badge */}
        <div className="demo-badge">
          <span>⚠️</span>
          <span>Demo Environment</span>
        </div>

        {/* Subtitle */}
        <p className="dashboard-subtitle">
          Real-time overview of the MediSphere healthcare ecosystem.
        </p>

        {/* Statistics Grid */}
        <div className="stats-grid">
          <div className="stat-card">
            <div className="stat-content">
              <div className="stat-label">Total Patients</div>
              <div className="stat-value">{mockDashboardStats.totalPatients}</div>
            </div>
            <div className="stat-icon">👥</div>
          </div>

          <div className="stat-card">
            <div className="stat-content">
              <div className="stat-label">Active Digital Twins</div>
              <div className="stat-value">{mockDashboardStats.activeDigitalTwins}</div>
            </div>
            <div className="stat-icon">🧠</div>
          </div>

          <div className="stat-card">
            <div className="stat-content">
              <div className="stat-label">FHIR Resources</div>
              <div className="stat-value">{mockDashboardStats.fhirResources}</div>
            </div>
            <div className="stat-icon">📊</div>
          </div>

          <div className="stat-card">
            <div className="stat-content">
              <div className="stat-label">Connected Devices</div>
              <div className="stat-value">{mockDashboardStats.connectedDevices}</div>
            </div>
            <div className="stat-icon">⚙️</div>
          </div>
        </div>

        {/* Charts Section */}
        <div className="charts-grid">
          {/* Patient Activity Chart */}
          <div className="chart-card">
            <div className="chart-header">
              <div>
                <div className="chart-title">Patient Activity</div>
                <div className="dashboard-subtitle" style={{ marginTop: '2px' }}>
                  Last 7 days
                </div>
              </div>
            </div>
            <div className="chart-container">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={mockPatientActivityChart}>
                  <CartesianGrid strokeDasharray="3 3" stroke="var(--border-light)" />
                  <XAxis dataKey="date" stroke="var(--text-secondary)" />
                  <YAxis stroke="var(--text-secondary)" />
                  <Tooltip />
                  <Bar dataKey="active" fill="var(--primary)" radius={[8, 8, 0, 0]} />
                  <Bar dataKey="inactive" fill="var(--border-light)" radius={[8, 8, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </div>

          {/* Vital Monitoring Chart */}
          <div className="chart-card">
            <div className="chart-header">
              <div>
                <div className="chart-title">Blood Pressure Trend</div>
                <div className="dashboard-subtitle" style={{ marginTop: '2px' }}>
                  Population average
                </div>
              </div>
            </div>
            <div className="chart-container">
              <ResponsiveContainer width="100%" height="100%">
                <LineChart data={mockVitalMonitoringChart}>
                  <CartesianGrid strokeDasharray="3 3" stroke="var(--border-light)" />
                  <XAxis dataKey="time" stroke="var(--text-secondary)" />
                  <YAxis stroke="var(--text-secondary)" />
                  <Tooltip />
                  <Line
                    type="monotone"
                    dataKey="systolic"
                    stroke="var(--primary)"
                    strokeWidth={2}
                    dot={false}
                  />
                  <Line
                    type="monotone"
                    dataKey="diastolic"
                    stroke="var(--text-tertiary)"
                    strokeWidth={2}
                    dot={false}
                  />
                </LineChart>
              </ResponsiveContainer>
            </div>
          </div>
        </div>

        {/* FHIR & Twin Status */}
        <div className="activity-grid">
          {/* FHIR Sync Card */}
          <div className="card">
            <div style={{ marginBottom: 'var(--spacing-4)' }}>
              <h3 className="chart-title">FHIR Synchronization</h3>
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 'var(--spacing-4)' }}>
              <div style={{ fontSize: '2.5rem' }}>📡</div>
              <div style={{ flex: 1 }}>
                <div style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>Status</div>
                <div
                  style={{
                    fontSize: '1.1rem',
                    fontWeight: '600',
                    color: 'var(--success)',
                    marginTop: '4px',
                  }}
                >
                  {mockSystemStatus.fhirSync.status}
                </div>
                <div
                  style={{
                    fontSize: '0.75rem',
                    color: 'var(--text-tertiary)',
                    marginTop: '4px',
                  }}
                >
                  Last sync: {mockSystemStatus.fhirSync.lastSync}
                </div>
              </div>
            </div>
          </div>

          {/* Digital Twin Status Card */}
          <div className="card">
            <div style={{ marginBottom: 'var(--spacing-4)' }}>
              <h3 className="chart-title">Digital Twin Status</h3>
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 'var(--spacing-4)' }}>
              <div style={{ fontSize: '2.5rem' }}>🧠</div>
              <div style={{ flex: 1 }}>
                <div style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>Active</div>
                <div
                  style={{
                    fontSize: '1.75rem',
                    fontWeight: '700',
                    color: 'var(--text-primary)',
                    marginTop: '4px',
                  }}
                >
                  {mockDashboardStats.activeDigitalTwins}
                </div>
                <div
                  style={{
                    fontSize: '0.75rem',
                    color: 'var(--text-tertiary)',
                    marginTop: '4px',
                  }}
                >
                  of {mockDashboardStats.totalPatients} patients
                </div>
              </div>
            </div>
          </div>

          {/* Consent Overview Card */}
          <div className="card">
            <div style={{ marginBottom: 'var(--spacing-4)' }}>
              <h3 className="chart-title">Consent Overview</h3>
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 'var(--spacing-4)' }}>
              <div style={{ fontSize: '2.5rem' }}>✓</div>
              <div style={{ flex: 1 }}>
                <div style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>Granted</div>
                <div
                  style={{
                    fontSize: '1.75rem',
                    fontWeight: '700',
                    color: 'var(--text-primary)',
                    marginTop: '4px',
                  }}
                >
                  972
                </div>
                <div
                  style={{
                    fontSize: '0.75rem',
                    color: 'var(--text-tertiary)',
                    marginTop: '4px',
                  }}
                >
                  out of {mockDashboardStats.totalPatients}
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Recent Activity */}
        <div className="card">
          <div style={{ marginBottom: 'var(--spacing-6)' }}>
            <h3 className="chart-title">Recent Activity</h3>
            <p className="dashboard-subtitle" style={{ marginTop: '4px' }}>
              Latest system events
            </p>
          </div>
          <div className="activity-timeline">
            {mockRecentActivity.map((activity) => (
              <div key={activity.id} className="activity-item">
                <div className="activity-dot"></div>
                <div className="activity-content">
                  <div className="activity-description">{activity.description}</div>
                  <div className="activity-timestamp">{activity.timestamp}</div>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* System Status */}
        <div className="card">
          <div style={{ marginBottom: 'var(--spacing-6)' }}>
            <h3 className="chart-title">System Status</h3>
          </div>
          <div
            style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
              gap: 'var(--spacing-4)',
            }}
          >
            <div>
              <div style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
                FHIR Connection
              </div>
              <div
                style={{
                  fontSize: '0.95rem',
                  fontWeight: '600',
                  color: 'var(--success)',
                  marginTop: '4px',
                }}
              >
                ✓ Active
              </div>
            </div>
            <div>
              <div style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
                Kafka Connection
              </div>
              <div
                style={{
                  fontSize: '0.95rem',
                  fontWeight: '600',
                  color: 'var(--success)',
                  marginTop: '4px',
                }}
              >
                ✓ Active
              </div>
            </div>
            <div>
              <div style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
                Database Latency
              </div>
              <div
                style={{
                  fontSize: '0.95rem',
                  fontWeight: '600',
                  color: 'var(--text-primary)',
                  marginTop: '4px',
                }}
              >
                2ms
              </div>
            </div>
          </div>
        </div>
      </div>
    </MainLayout>
  );
}
