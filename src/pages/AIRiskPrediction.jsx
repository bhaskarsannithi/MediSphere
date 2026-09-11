import { useEffect, useState } from 'react';
import MainLayout from '../layouts/MainLayout';
import { apiFetch } from '../api';
import '../styles/pages.css';

const badgeClass = (category) => category === 'HIGH' ? 'badge-danger' : category === 'MODERATE' ? 'badge-warning' : 'badge-success';
const percent = (value) => value == null ? 'Not calculated' : `${Number(value).toFixed(1)}%`;
const directionLabel = (direction) => direction === 'INCREASES_RISK' ? 'Higher model risk' : 'Lower model risk';

export default function AIRiskPrediction() {
  const [patients, setPatients] = useState([]);
  const [patientId, setPatientId] = useState('');
  const [predictions, setPredictions] = useState([]);
  const [models, setModels] = useState([]);
  const [federated, setFederated] = useState(null);
  const [federatedError, setFederatedError] = useState('');
  const [loading, setLoading] = useState('');
  const [error, setError] = useState('');

  const refresh = (id) => {
    if (!id) return;
    apiFetch(`/api/ai/predictions/${id}`).then(setPredictions).catch(() => setPredictions([]));
  };

  useEffect(() => {
    apiFetch('/api/patients').then((items) => { setPatients(items); setPatientId(items[0]?.patientId || ''); }).catch((e) => setError(e.message));
    apiFetch('/api/ai/models').then(setModels).catch(() => {});
    let active = true;
    const refreshFederatedStatus = () => apiFetch('/api/ai/federated/status')
      .then((status) => {
        if (active) { setFederated(status); setFederatedError(''); }
      })
      .catch(() => { if (active) setFederatedError('Unable to load federated training status.'); });
    refreshFederatedStatus();
    const statusTimer = window.setInterval(refreshFederatedStatus, 5000);
    return () => { active = false; window.clearInterval(statusTimer); };
  }, []);
  useEffect(() => refresh(patientId), [patientId]);

  const runPrediction = async (type) => {
    setLoading(type); setError('');
    try {
      await apiFetch(`/api/ai/predict/${type}/${patientId}`, { method: 'POST' });
      refresh(patientId);
      setModels(await apiFetch('/api/ai/models'));
    } catch (e) { setError(e.message); }
    finally { setLoading(''); }
  };

  const cvd = predictions.find((item) => item.modelType === 'CVD');
  const diabetes = predictions.find((item) => item.modelType === 'DIABETES_COMPLICATION');
  const selectedPatient = patients.find((item) => item.patientId === patientId);
  const activeExplanation = cvd?.explanation || diabetes?.explanation || [];

  return <MainLayout title="AI Risk Prediction">
    <div className="dashboard-container">
      <p className="dashboard-subtitle">Clinical decision support only. Risk estimates do not diagnose or prescribe; an authorized healthcare professional makes the final decision.</p>
      {error && <p className="error-message">{error}</p>}
      <div className="dashboard-section">
        <label htmlFor="ai-patient">Patient</label>
        <select id="ai-patient" className="filter-select" value={patientId} onChange={(e) => setPatientId(e.target.value)} style={{ display: 'block', marginTop: '8px', maxWidth: '360px' }}>
          {patients.map((patient) => <option key={patient.patientId} value={patient.patientId}>{patient.firstName} {patient.lastName} ({patient.patientId})</option>)}
        </select>
      </div>
      <div className="stats-grid">
        {[['CVD Risk', cvd, 'cvd'], ['Diabetes Complication Risk', diabetes, 'diabetes']].map(([title, prediction, type]) => <div className="stat-card" key={type}>
          <div className="stat-content"><div className="stat-label">{title}</div><div className="stat-value">{percent(prediction?.riskPercentage)}</div>
            {prediction && <span className={`badge ${badgeClass(prediction.riskCategory)}`}>{prediction.riskCategory}</span>}
            <div className="dashboard-subtitle" style={{ marginTop: '8px' }}>{prediction ? `${prediction.modelVersion} · confidence ${Number(prediction.confidence * 100).toFixed(1)}%` : 'No prediction yet'}</div>
          </div>
          <button className="btn btn-primary" disabled={!patientId || loading === type} onClick={() => runPrediction(type)}>{loading === type ? 'Calculating...' : 'Calculate'}</button>
        </div>)}
      </div>
      <div className="chart-card">
        <div className="chart-title">Why This Prediction</div>
        <div className="dashboard-subtitle" style={{ margin: '4px 0 16px' }}>Patient-specific SHAP contributions from the persisted model.</div>
        {activeExplanation.length ? activeExplanation.slice(0, 8).map((item) => <div className="shap-row" key={item.feature}>
          <div><strong>{item.label || item.feature.replaceAll('_', ' ')}</strong><small>{item.summary || `${item.label || item.feature} contributed to the prediction.`}</small></div>
          <div className="progress-bar"><div className="progress-fill" style={{ width: `${Math.min(Math.abs(item.shapValue ?? item.contribution) * 100, 100)}%`, background: item.direction === 'INCREASES_RISK' || (item.shapValue ?? item.contribution) >= 0 ? 'var(--danger)' : 'var(--success)' }} /></div>
          <span className={`badge ${item.direction === 'INCREASES_RISK' ? 'badge-danger' : 'badge-success'}`}>{directionLabel(item.direction)}</span>
        </div>) : <p>No explanation is available until a risk prediction is calculated for {selectedPatient?.firstName || 'this patient'}.</p>}
        <p className="ai-disclaimer">AI explanation shows model feature contributions, not causation or a clinical diagnosis. Clinical decisions require qualified healthcare professional review.</p>
      </div>
      <div className="activity-grid">
        <div className="card"><h3 className="chart-title">Federated Learning</h3>{federated ? <div style={{ marginTop: '12px' }}><p>Status: {federated.status}</p><p>Current round: {federated.currentRound}</p><p>Clients: {federated.clients}</p><p>Global accuracy: {federated.globalAccuracy == null ? 'Not trained' : percent(federated.globalAccuracy * 100)}</p><p>Loss: {federated.loss == null ? 'Not trained' : Number(federated.loss).toFixed(4)}</p></div> : <p>{federatedError || 'Loading federated training status...'}</p>}</div>
        <div className="card"><h3 className="chart-title">Model Performance</h3>{models.map((model) => <div key={model.modelId} style={{ marginTop: '12px' }}><strong>{model.modelId}</strong><p>Accuracy {percent(model.metrics?.accuracy * 100)} · ROC-AUC {model.metrics?.rocAuc == null ? 'N/A' : Number(model.metrics.rocAuc).toFixed(3)}</p></div>)}</div>
      </div>
    </div>
  </MainLayout>;
}
