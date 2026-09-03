import React, { useState } from 'react';
import MainLayout from '../layouts/MainLayout';
import { mockFHIRResources, mockFHIRResourceDetails } from '../data/mockData';
import '../styles/pages.css';

export default function FHIRResources() {
  const [showJsonModal, setShowJsonModal] = useState(false);
  const [selectedFHIRId, setSelectedFHIRId] = useState(null);

  const allResources = [];
  Object.values(mockFHIRResources).forEach((resources) => {
    allResources.push(...resources);
  });

  const totalResources = allResources.length;
  const successfulSync = allResources.filter((r) => r.status === 'FINAL').length;
  const warnings = 3;
  const errors = 1;

  const getFHIRStatusClass = (status) => {
    return status === 'FINAL' ? 'badge-success' : 'badge-warning';
  };

  return (
    <MainLayout title="FHIR Resource Center">
      <div className="dashboard-container">
        {/* Stats Cards */}
        <div className="stats-grid">
          <div className="stat-card">
            <div className="stat-content">
              <div className="stat-label">Total Resources</div>
              <div className="stat-value">{totalResources}</div>
            </div>
            <div className="stat-icon">📊</div>
          </div>

          <div className="stat-card">
            <div className="stat-content">
              <div className="stat-label">Successful Sync</div>
              <div className="stat-value">{successfulSync}</div>
            </div>
            <div className="stat-icon">✓</div>
          </div>

          <div className="stat-card">
            <div className="stat-content">
              <div className="stat-label">Warnings</div>
              <div className="stat-value">{warnings}</div>
            </div>
            <div className="stat-icon">⚠️</div>
          </div>

          <div className="stat-card">
            <div className="stat-content">
              <div className="stat-label">Validation Errors</div>
              <div className="stat-value">{errors}</div>
            </div>
            <div className="stat-icon">❌</div>
          </div>
        </div>

        {/* Resources Table */}
        <div className="table-wrapper">
          <div className="table-responsive">
            <table>
              <thead>
                <tr>
                  <th>FHIR ID</th>
                  <th>Resource Type</th>
                  <th>Patient</th>
                  <th>Source</th>
                  <th>Status</th>
                  <th>Last Updated</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {allResources.slice(0, 10).map((resource, idx) => (
                  <tr key={idx}>
                    <td>
                      <span style={{ fontFamily: 'monospace', fontSize: '0.85rem' }}>
                        {resource.fhirId}
                      </span>
                    </td>
                    <td>{resource.resourceType}</td>
                    <td>MS-1000{idx + 1}</td>
                    <td>EHR System</td>
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
                        View
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* JSON Modal */}
        {showJsonModal && (
          <div className="modal-overlay" onClick={() => setShowJsonModal(false)}>
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
              <div className="modal-header">
                <div className="modal-title">FHIR Resource Details</div>
                <button className="modal-close-btn" onClick={() => setShowJsonModal(false)}>
                  ✕
                </button>
              </div>
              <div className="json-viewer">
                {selectedFHIRId &&
                  JSON.stringify(mockFHIRResourceDetails[selectedFHIRId] || {}, null, 2)}
              </div>
            </div>
          </div>
        )}
      </div>
    </MainLayout>
  );
}
