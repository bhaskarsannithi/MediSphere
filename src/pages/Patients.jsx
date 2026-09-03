import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import MainLayout from '../layouts/MainLayout';
import '../styles/pages.css';
import { apiFetch } from '../api';

export default function Patients() {
  const navigate = useNavigate();
  const [searchTerm, setSearchTerm] = useState('');
  const [filterStatus, setFilterStatus] = useState('');
  const [filterGender, setFilterGender] = useState('');
  const [filterConsent, setFilterConsent] = useState('');
  const [patients, setPatients] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    apiFetch('/api/patients')
      .then(setPatients)
      .catch((requestError) => setError(requestError.message));
  }, []);

  const displayPatients = patients.map((patient) => ({
    ...patient,
    id: patient.patientId,
    name: `${patient.firstName || ''} ${patient.lastName || ''}`.trim(),
    gender: patient.gender ? patient.gender[0].toUpperCase() + patient.gender.slice(1) : 'Unknown',
    consent: patient.consentStatus || 'PENDING',
    digitalTwin: patient.active === false ? 'INACTIVE' : 'ACTIVE',
    lastUpdated: patient.updatedAt || 'Unknown',
  }));

  const filteredPatients = displayPatients.filter((patient) => {
    const matchesSearch =
      patient.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      patient.id.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesStatus = filterStatus === '' || patient.digitalTwin === filterStatus;
    const matchesGender = filterGender === '' || patient.gender === filterGender;
    const matchesConsent = filterConsent === '' || patient.consent === filterConsent;

    return matchesSearch && matchesStatus && matchesGender && matchesConsent;
  });

  const handleRowClick = (patientId) => {
    navigate(`/patient/${patientId}`);
  };

  const getConsentBadgeClass = (consent) => {
    switch (consent) {
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

  const getTwinBadgeClass = (status) => {
    return status === 'ACTIVE' ? 'badge-success' : 'badge-warning';
  };

  return (
    <MainLayout title="Patients">
      <div className="patients-container">
        {error && <p className="error-message">{error}</p>}
        {/* Table Header with Filters */}
        <div className="table-header">
          <div style={{ flex: 1 }}>
            <input
              type="text"
              placeholder="Search patient by name or ID..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              style={{
                width: '100%',
                padding: 'var(--spacing-2) var(--spacing-3)',
                border: '1px solid var(--border-color)',
                borderRadius: 'var(--radius-md)',
                fontSize: '0.875rem',
              }}
            />
          </div>
        </div>

        {/* Filter Row */}
        <div className="filter-group">
          <select
            className="filter-select"
            value={filterStatus}
            onChange={(e) => setFilterStatus(e.target.value)}
          >
            <option value="">All Status</option>
            <option value="ACTIVE">Active</option>
            <option value="INCOMPLETE">Incomplete</option>
            <option value="INACTIVE">Inactive</option>
          </select>

          <select
            className="filter-select"
            value={filterGender}
            onChange={(e) => setFilterGender(e.target.value)}
          >
            <option value="">All Gender</option>
            <option value="Male">Male</option>
            <option value="Female">Female</option>
          </select>

          <select
            className="filter-select"
            value={filterConsent}
            onChange={(e) => setFilterConsent(e.target.value)}
          >
            <option value="">All Consent</option>
            <option value="GRANTED">Granted</option>
            <option value="PENDING">Pending</option>
            <option value="REVOKED">Revoked</option>
          </select>
        </div>

        {/* Table */}
        <div className="table-wrapper">
          <div className="table-responsive">
            <table>
              <thead>
                <tr>
                  <th>Patient ID</th>
                  <th>Patient Name</th>
                  <th>Age</th>
                  <th>Gender</th>
                  <th>Consent</th>
                  <th>Digital Twin</th>
                  <th>Last Updated</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {filteredPatients.map((patient) => (
                  <tr
                    key={patient.id}
                    onClick={() => handleRowClick(patient.id)}
                    style={{ cursor: 'pointer' }}
                  >
                    <td>
                      <span style={{ fontWeight: '600', color: 'var(--primary)' }}>
                        {patient.id}
                      </span>
                    </td>
                    <td>{patient.name}</td>
                    <td>{patient.age}</td>
                    <td>{patient.gender}</td>
                    <td>
                      <span className={`badge ${getConsentBadgeClass(patient.consent)}`}>
                        {patient.consent}
                      </span>
                    </td>
                    <td>
                      <span className={`badge ${getTwinBadgeClass(patient.digitalTwin)}`}>
                        {patient.digitalTwin}
                      </span>
                    </td>
                    <td>{patient.lastUpdated}</td>
                    <td>
                      <button
                        className="action-btn"
                        onClick={(e) => {
                          e.stopPropagation();
                          handleRowClick(patient.id);
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

        {/* Results Summary */}
        <div style={{ padding: 'var(--spacing-4)', color: 'var(--text-secondary)', fontSize: '0.875rem' }}>
          Showing {filteredPatients.length} of {displayPatients.length} patients
        </div>
      </div>
    </MainLayout>
  );
}
