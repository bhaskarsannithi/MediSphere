import React, { useState } from 'react';
import MainLayout from '../layouts/MainLayout';
import { mockConsents } from '../data/mockData';
import '../styles/pages.css';

export default function Consent() {
  const [filterStatus, setFilterStatus] = useState('');

  const grantedCount = mockConsents.filter((c) => c.status === 'GRANTED').length;
  const pendingCount = mockConsents.filter((c) => c.status === 'PENDING').length;
  const revokedCount = mockConsents.filter((c) => c.status === 'REVOKED').length;
  const expiringCount = mockConsents.filter(
    (c) => c.daysUntilExpire && c.daysUntilExpire <= 30 && c.daysUntilExpire > 0
  ).length;

  const filteredConsents =
    filterStatus === '' ? mockConsents : mockConsents.filter((c) => c.status === filterStatus);

  const getStatusClass = (status) => {
    switch (status) {
      case 'GRANTED':
        return 'badge-success';
      case 'PENDING':
        return 'badge-warning';
      case 'REVOKED':
        return 'badge-danger';
      default:
        return 'badge-info';
    }
  };

  return (
    <MainLayout title="Patient Consent Management">
      <div className="dashboard-container">
        {/* Stats Cards */}
        <div className="stats-grid">
          <div className="stat-card">
            <div className="stat-content">
              <div className="stat-label">Granted</div>
              <div className="stat-value">{grantedCount}</div>
            </div>
            <div className="stat-icon">✓</div>
          </div>

          <div className="stat-card">
            <div className="stat-content">
              <div className="stat-label">Pending</div>
              <div className="stat-value">{pendingCount}</div>
            </div>
            <div className="stat-icon">⏳</div>
          </div>

          <div className="stat-card">
            <div className="stat-content">
              <div className="stat-label">Revoked</div>
              <div className="stat-value">{revokedCount}</div>
            </div>
            <div className="stat-icon">✕</div>
          </div>

          <div className="stat-card">
            <div className="stat-content">
              <div className="stat-label">Expiring Soon</div>
              <div className="stat-value">{expiringCount}</div>
            </div>
            <div className="stat-icon">⚠️</div>
          </div>
        </div>

        {/* Filter */}
        <div style={{ marginBottom: 'var(--spacing-6)' }}>
          <select
            className="filter-select"
            value={filterStatus}
            onChange={(e) => setFilterStatus(e.target.value)}
          >
            <option value="">All Status</option>
            <option value="GRANTED">Granted</option>
            <option value="PENDING">Pending</option>
            <option value="REVOKED">Revoked</option>
          </select>
        </div>

        {/* Consent Table */}
        <div className="table-wrapper">
          <div className="table-responsive">
            <table>
              <thead>
                <tr>
                  <th>Patient</th>
                  <th>Consent ID</th>
                  <th>Purpose</th>
                  <th>Status</th>
                  <th>Granted Date</th>
                  <th>Expiration</th>
                </tr>
              </thead>
              <tbody>
                {filteredConsents.map((consent) => (
                  <tr key={consent.id}>
                    <td>{consent.patientName}</td>
                    <td>
                      <span style={{ fontWeight: '600', color: 'var(--primary)' }}>
                        {consent.consentId}
                      </span>
                    </td>
                    <td>{consent.purpose}</td>
                    <td>
                      <span className={`badge ${getStatusClass(consent.status)}`}>
                        {consent.status}
                      </span>
                    </td>
                    <td>{consent.grantedDate || 'N/A'}</td>
                    <td>
                      {consent.expirationDate ? (
                        <span>
                          {consent.expirationDate}
                          {consent.daysUntilExpire !== null && (
                            <span style={{ display: 'block', fontSize: '0.75rem', color: 'var(--text-tertiary)' }}>
                              ({consent.daysUntilExpire} days)
                            </span>
                          )}
                        </span>
                      ) : (
                        'N/A'
                      )}
                    </td>
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
