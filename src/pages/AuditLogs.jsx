import { useEffect, useState } from 'react';
import MainLayout from '../layouts/MainLayout';
import '../styles/pages.css';
import { apiFetch } from '../api';

export default function AuditLogs() {
  const [filterAction, setFilterAction] = useState('');
  const [filterResult, setFilterResult] = useState('');
  const [auditLogs, setAuditLogs] = useState([]);

  useEffect(() => { apiFetch('/api/audit').then(setAuditLogs).catch(() => setAuditLogs([])); }, []);

  const filteredLogs = auditLogs.filter((log) => {
    const actionMatch = filterAction === '' || log.action === filterAction;
    const resultMatch = filterResult === '' || log.result === filterResult;
    return actionMatch && resultMatch;
  });

  const getResultBadgeClass = (result) => {
    return result === 'SUCCESS' ? 'badge-success' : 'badge-danger';
  };

  const actions = ['VIEWED_PATIENT', 'UPDATED_VITALS', 'VIEWED_TWIN', 'SYSTEM_CONFIG_CHANGE', 'EXPORTED_DATA'];
  const results = ['SUCCESS', 'FAILED'];

  return (
    <MainLayout title="Audit Trail">
      <div className="dashboard-container">
        {/* Filters */}
        <div className="filter-group" style={{ marginBottom: 'var(--spacing-6)' }}>
          <select
            className="filter-select"
            value={filterAction}
            onChange={(e) => setFilterAction(e.target.value)}
          >
            <option value="">All Actions</option>
            {actions.map((action) => (
              <option key={action} value={action}>
                {action.replace(/_/g, ' ')}
              </option>
            ))}
          </select>

          <select
            className="filter-select"
            value={filterResult}
            onChange={(e) => setFilterResult(e.target.value)}
          >
            <option value="">All Results</option>
            {results.map((result) => (
              <option key={result} value={result}>
                {result}
              </option>
            ))}
          </select>
        </div>

        {/* Audit Log Table */}
        <div className="table-wrapper">
          <div className="table-responsive">
            <table>
              <thead>
                <tr>
                  <th>Timestamp</th>
                  <th>User</th>
                  <th>Role</th>
                  <th>Action</th>
                  <th>Resource</th>
                  <th>Patient</th>
                  <th>Result</th>
                </tr>
              </thead>
              <tbody>
                {filteredLogs.map((log) => (
                  <tr key={log.id}>
                    <td>{log.timestamp}</td>
                    <td>{log.userId}</td>
                    <td>{log.role}</td>
                    <td>{log.action.replace(/_/g, ' ')}</td>
                    <td>{log.resourceType} {log.resourceId}</td>
                    <td>{log.patientId || 'System'}</td>
                    <td>
                      <span className={`badge ${getResultBadgeClass(log.result)}`}>
                        {log.result}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* Summary */}
        <div style={{ marginTop: 'var(--spacing-6)', color: 'var(--text-secondary)', fontSize: '0.875rem' }}>
          Showing {filteredLogs.length} audit logs
        </div>
      </div>
    </MainLayout>
  );
}
