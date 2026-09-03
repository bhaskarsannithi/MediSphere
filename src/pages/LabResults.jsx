import React, { useState } from 'react';
import MainLayout from '../layouts/MainLayout';
import { mockPatients, mockLabResults } from '../data/mockData';
import '../styles/pages.css';

export default function LabResults() {
  const [selectedPatient, setSelectedPatient] = useState('MS-10001');
  const [searchTerm, setSearchTerm] = useState('');

  const currentLabResults = mockLabResults[selectedPatient] || [];
  const filteredResults = currentLabResults.filter((lab) =>
    lab.test.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const getStatusClass = (status) => {
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

  return (
    <MainLayout title="Lab Results">
      <div className="dashboard-container">
        {/* Patient Selector */}
        <div style={{ marginBottom: 'var(--spacing-6)' }}>
          <label
            style={{
              display: 'block',
              marginBottom: 'var(--spacing-2)',
              fontSize: '0.875rem',
              fontWeight: '500',
            }}
          >
            Select Patient
          </label>
          <select
            className="filter-select"
            value={selectedPatient}
            onChange={(e) => setSelectedPatient(e.target.value)}
            style={{ width: '100%', maxWidth: '300px' }}
          >
            {mockPatients.map((patient) => (
              <option key={patient.id} value={patient.id}>
                {patient.name} ({patient.id})
              </option>
            ))}
          </select>
        </div>

        {/* Search */}
        <div style={{ marginBottom: 'var(--spacing-6)' }}>
          <input
            type="text"
            placeholder="Search lab results..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            style={{
              width: '100%',
              maxWidth: '400px',
              padding: 'var(--spacing-2) var(--spacing-3)',
              border: '1px solid var(--border-color)',
              borderRadius: 'var(--radius-md)',
              fontSize: '0.875rem',
            }}
          />
        </div>

        {/* Results Table */}
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
                {filteredResults.map((lab) => (
                  <tr key={lab.id}>
                    <td>{lab.test}</td>
                    <td>{lab.result}</td>
                    <td>{lab.unit}</td>
                    <td>{lab.referenceRange}</td>
                    <td>
                      <span className={`badge ${getStatusClass(lab.status)}`}>
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

        {/* Summary */}
        <div style={{ marginTop: 'var(--spacing-6)', color: 'var(--text-secondary)', fontSize: '0.875rem' }}>
          Showing {filteredResults.length} of {currentLabResults.length} results
        </div>
      </div>
    </MainLayout>
  );
}
