import React, { useState } from 'react';
import { useParams } from 'react-router-dom';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import MainLayout from '../layouts/MainLayout';
import {
  mockPatientDetails,
  mockVitals,
  mockVitalTrends,
  mockLabResults,
  mockFHIRResources,
  mockRecentActivity,
} from '../data/mockData';
import '../styles/pages.css';

export default function Patient360() {
  const { patientId = 'MS-10001' } = useParams();
  const [activeVitalTab, setActiveVitalTab] = useState('heartRate');
  const [showJsonModal, setShowJsonModal] = useState(false);
  const [selectedFHIRId, setSelectedFHIRId] = useState(null);

  const patient = mockPatientDetails[patientId];
  const vitals = mockVitals[patientId];
  const vitalTrends = mockVitalTrends[patientId];
  const labResults = mockLabResults[patientId] || [];
  const fhirResources = mockFHIRResources[patientId] || [];
  const recentActivity = mockRecentActivity;

  if (!patient) {
    return <MainLayout title="Patient Not Found">Patient data not found</MainLayout>;
  }

  const chartData = vitalTrends[activeVitalTab];
  const currentVitalValue = vitals
    ? activeVitalTab === 'heartRate'
      ? vitals.heartRate
      : activeVitalTab === 'spO2'
        ? vitals.spO2
        : vitals.temperature
    : 0;

  const getLabResultStatusClass = (status) => {
    switch (status) {
      case 'NORMAL':
        return 'badge-success';
      case 'SLIGHTLY_HIGH':
        return 'badge-warning';
      case 'ABNORMAL':
        return 'badge-danger';
      default:
        return 'badge-info';
    }
  };

  const getFHIRStatusClass = (status) => {
    return status === 'FINAL' ? 'badge-success' : 'badge-warning';
  };

  return (
    <MainLayout title="Patient 360">
      <div className="patient-360-container">
        {/* Patient Header */}
        <div className="patient-header">
          <div className="patient-avatar">{patient.avatar}</div>
          <div className="patient-info">
            <div className="patient-name">{patient.name}</div>
            <div className="patient-id">Patient ID: {patient.id}</div>

            <div className="patient-meta">
              <div className="meta-item">
                <div className="meta-label">Age</div>
                <div className="meta-value">{patient.age} years</div>
              </div>
              <div className="meta-item">
                <div className="meta-label">Gender</div>
                <div className="meta-value">{patient.gender}</div>
              </div>
              <div className="meta-item">
                <div className="meta-label">FHIR ID</div>
                <div className="meta-value">{patient.fhirId}</div>
              </div>
              <div className="meta-item">
                <div className="meta-label">Consent</div>
                <span className="badge badge-success">{patient.consent}</span>
              </div>
              <div className="meta-item">
                <div className="meta-label">Digital Twin</div>
                <span className="badge badge-success">{patient.digitalTwin}</span>
              </div>
              <div className="meta-item">
                <div className="meta-label">Last Updated</div>
                <div className="meta-value">{patient.lastUpdated}</div>
              </div>
            </div>
          </div>
        </div>

        {/* Current Vitals Section */}
        <div className="dashboard-section">
          <h3 className="section-title">Current Vitals</h3>

          <div className="vitals-grid">
            <div className="vital-card">
              <div className="vital-icon">❤️</div>
              <div className="vital-label">Heart Rate</div>
              <div className="vital-value">{vitals?.heartRate || 0}</div>
              <div className="vital-unit">BPM</div>
              <div className="vital-timestamp">Last: {vitals?.lastUpdated}</div>
            </div>

            <div className="vital-card">
              <div className="vital-icon">🩸</div>
              <div className="vital-label">Blood Pressure</div>
              <div className="vital-value">
                {vitals?.bloodPressureSystolic}/{vitals?.bloodPressureDiastolic}
              </div>
              <div className="vital-unit">mmHg</div>
              <div className="vital-timestamp">Last: {vitals?.lastUpdated}</div>
            </div>

            <div className="vital-card">
              <div className="vital-icon">💧</div>
              <div className="vital-label">SpO2</div>
              <div className="vital-value">{vitals?.spO2 || 0}</div>
              <div className="vital-unit">%</div>
              <div className="vital-timestamp">Last: {vitals?.lastUpdated}</div>
            </div>

            <div className="vital-card">
              <div className="vital-icon">🌡️</div>
              <div className="vital-label">Temperature</div>
              <div className="vital-value">{vitals?.temperature || 0}</div>
              <div className="vital-unit">°C</div>
              <div className="vital-timestamp">Last: {vitals?.lastUpdated}</div>
            </div>
          </div>
        </div>

        {/* Vital Trends Chart */}
        <div className="chart-card">
          <div className="chart-header">
            <div>
              <div className="chart-title">
                {activeVitalTab === 'heartRate'
                  ? 'Heart Rate'
                  : activeVitalTab === 'spO2'
                    ? 'SpO2'
                    : 'Temperature'}{' '}
                — Last 24 Hours
              </div>
              <div className="dashboard-subtitle" style={{ marginTop: '2px' }}>
                Illustrative Demo Data
              </div>
            </div>
            <div className="chart-tabs">
              <button
                className={`chart-tab ${activeVitalTab === 'heartRate' ? 'active' : ''}`}
                onClick={() => setActiveVitalTab('heartRate')}
              >
                Heart Rate
              </button>
              <button
                className={`chart-tab ${activeVitalTab === 'spO2' ? 'active' : ''}`}
                onClick={() => setActiveVitalTab('spO2')}
              >
                SpO2
              </button>
              <button
                className={`chart-tab ${activeVitalTab === 'temperature' ? 'active' : ''}`}
                onClick={() => setActiveVitalTab('temperature')}
              >
                Temperature
              </button>
            </div>
          </div>
          <div className="chart-container">
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={chartData}>
                <CartesianGrid strokeDasharray="3 3" stroke="var(--border-light)" />
                <XAxis dataKey="time" stroke="var(--text-secondary)" />
                <YAxis stroke="var(--text-secondary)" />
                <Tooltip />
                <Line
                  type="monotone"
                  dataKey="value"
                  stroke="var(--primary)"
                  strokeWidth={2}
                  dot={{ fill: 'var(--primary)', r: 4 }}
                />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Digital Health Twin Card */}
        <div className="twin-card">
          <div className="twin-header">
            <div className="twin-title">Digital Health Twin</div>
            <div className="twin-status-badge">ACTIVE</div>
          </div>

          <div className="twin-content">
            <div className="twin-stat">
              <div className="twin-stat-label">Data Completeness</div>
              <div className="twin-stat-value">{patient.dataCompleteness}%</div>
              <div className="progress-bar">
                <div
                  className="progress-fill"
                  style={{ width: `${patient.dataCompleteness}%` }}
                ></div>
              </div>
              <p
                style={{
                  fontSize: '0.75rem',
                  color: 'var(--text-tertiary)',
                  marginTop: 'var(--spacing-2)',
                }}
              >
                Data completeness is NOT a medical health score.
              </p>
            </div>

            <div className="twin-stat">
              <div className="twin-stat-label">Last Updated</div>
              <div className="twin-stat-value">{patient.lastUpdated}</div>
            </div>

            <div className="twin-stat">
              <div className="twin-stat-label">Connected Sources</div>
              <div className="sources-list">
                {patient.connectedSources.map((source) => (
                  <div key={source} className="source-badge">
                    {source}
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>

        {/* Recent Lab Results */}
        <div className="dashboard-section">
          <h3 className="section-title">Recent Lab Results</h3>
          <div className="table-wrapper">
            <div className="table-responsive">
              <table>
                <thead>
                  <tr>
                    <th>Test</th>
                    <th>Result</th>
                    <th>Unit</th>
                    <th>Reference Range</th>
                    <th>Status</th>
                    <th>Date</th>
                  </tr>
                </thead>
                <tbody>
                  {labResults.map((lab) => (
                    <tr key={lab.id}>
                      <td>{lab.test}</td>
                      <td>{lab.result}</td>
                      <td>{lab.unit}</td>
                      <td>{lab.referenceRange}</td>
                      <td>
                        <span className={`badge ${getLabResultStatusClass(lab.status)}`}>
                          {lab.status.replace('_', ' ')}
                        </span>
                      </td>
                      <td>{lab.date}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>

        {/* FHIR Resources */}
        <div className="dashboard-section">
          <h3 className="section-title">FHIR Resources</h3>
          <div className="table-wrapper">
            <div className="table-responsive">
              <table>
                <thead>
                  <tr>
                    <th>Resource Type</th>
                    <th>FHIR ID</th>
                    <th>Status</th>
                    <th>Last Sync</th>
                    <th>Action</th>
                  </tr>
                </thead>
                <tbody>
                  {fhirResources.map((resource) => (
                    <tr key={resource.id}>
                      <td>{resource.resourceType}</td>
                      <td>
                        <span style={{ fontFamily: 'monospace', fontSize: '0.85rem' }}>
                          {resource.fhirId}
                        </span>
                      </td>
                      <td>
                        <span className={`badge ${getFHIRStatusClass(resource.status)}`}>
                          {resource.status}
                        </span>
                      </td>
                      <td>{resource.lastSync}</td>
                      <td>
                        <button
                          className="action-btn"
                          onClick={() => {
                            setSelectedFHIRId(resource.fhirId);
                            setShowJsonModal(true);
                          }}
                        >
                          View JSON
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>

        {/* Consent */}
        <div className="consent-card">
          <div className="consent-header">
            <div className="consent-title">Consent</div>
            <span className="badge badge-success">GRANTED</span>
          </div>
          <div className="consent-content">
            <div className="consent-item">
              <div className="consent-label">Purpose</div>
              <div className="consent-value">{patient.consentPurpose}</div>
            </div>
            <div className="consent-item">
              <div className="consent-label">Granted Date</div>
              <div className="consent-value">{patient.consentGrantedDate}</div>
            </div>
            <div className="consent-item">
              <div className="consent-label">Status</div>
              <div className="consent-value">{patient.consent}</div>
            </div>
          </div>
        </div>

        {/* Recent Activity */}
        <div className="activity-card">
          <div style={{ marginBottom: 'var(--spacing-6)' }}>
            <h3 className="chart-title">Recent Activity</h3>
          </div>
          <div className="activity-timeline">
            {recentActivity.map((activity) => (
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
      </div>

      {/* JSON Modal */}
      {showJsonModal && (
        <div className="modal-overlay" onClick={() => setShowJsonModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <div className="modal-title">FHIR Resource</div>
              <button className="modal-close-btn" onClick={() => setShowJsonModal(false)}>
                ✕
              </button>
            </div>
            <div
              className="json-viewer"
              style={{
                whiteSpace: 'pre-wrap',
                wordBreak: 'break-word',
              }}
            >
              {selectedFHIRId &&
                JSON.stringify(
                  require('../data/mockData').mockFHIRResourceDetails[selectedFHIRId] || {},
                  null,
                  2
                )}
            </div>
          </div>
        </div>
      )}
    </MainLayout>
  );
}
