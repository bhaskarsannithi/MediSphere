import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Check, ClipboardCheck, RefreshCw, ShieldAlert, Target } from 'lucide-react';
import MainLayout from '../layouts/MainLayout';
import { apiFetch } from '../api';
import '../styles/pages.css';

const badgeClass = (status) => ({ ACTIVE: 'badge-success', APPROVED: 'badge-success', PENDING_REVIEW: 'badge-warning', MODIFIED: 'badge-info', REJECTED: 'badge-danger' }[status] || 'badge-info');

export default function CarePlans() {
  const [patients, setPatients] = useState([]);
  const [patientId, setPatientId] = useState('');
  const [plans, setPlans] = useState([]);
  const [selected, setSelected] = useState(null);
  const [adherence, setAdherence] = useState({});
  const [outcomes, setOutcomes] = useState([]);
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);

  const loadDetails = (plan) => {
    setSelected(plan);
    Promise.all([apiFetch(`/api/careplans/${plan.carePlanId}/adherence/statistics`), apiFetch(`/api/careplans/${plan.carePlanId}/outcomes`)]).then(([stats, result]) => { setAdherence(stats); setOutcomes(result); }).catch((requestError) => setError(requestError.message));
  };
  const loadPlans = (id = patientId) => id && apiFetch(`/api/careplans/patient/${id}`).then((items) => { setPlans(items); if (items[0]) loadDetails(items[0]); });
  useEffect(() => { apiFetch('/api/patients').then((items) => { setPatients(items); if (items[0]) { setPatientId(items[0].patientId); apiFetch(`/api/careplans/patient/${items[0].patientId}`).then((plansForPatient) => { setPlans(plansForPatient); if (plansForPatient[0]) loadDetails(plansForPatient[0]); }); } }).catch((requestError) => setError(requestError.message)); }, []);

  const action = async (path, options = {}) => { setBusy(true); setError(''); try { const plan = await apiFetch(path, { method: 'POST', ...options }); await loadPlans(); loadDetails(plan); } catch (requestError) { setError(requestError.message); } finally { setBusy(false); } };
  const generate = () => action('/api/careplans/generate', { body: JSON.stringify({ patientId }) });
  const review = (verb) => selected && action(`/api/careplans/${selected.carePlanId}/${verb}`, { body: JSON.stringify({ comments: `Provider ${verb} review` }) });
  const recordAdherence = () => selected && action(`/api/careplans/${selected.carePlanId}/adherence`, { body: JSON.stringify({ patientId, taskId: selected.monitoringTasks?.[0]?.taskId, date: new Date().toISOString().slice(0, 10), status: 'COMPLETED', notes: 'Demo synthetic adherence record' }) });
  const addOutcome = () => selected && action(`/api/careplans/${selected.carePlanId}/outcomes`, { body: JSON.stringify({ patientId, metric: 'BLOOD_PRESSURE_SYSTOLIC', baselineValue: selected.expectedOutcomes?.[0]?.baselineValue, currentValue: 128, targetValue: 130, unit: 'mmHg' }) });

  return <MainLayout title="Care Plans">
    <div className="dashboard-container careplans-page">
      <div className="dashboard-header"><p className="eyebrow"><ClipboardCheck size={15} /> Precision care management</p><p className="dashboard-subtitle">AI-generated decision support using the existing Digital Twin, risk predictions, vitals, and alerts. Synthetic demo data; clinician review required.</p></div>
      {error && <p className="error-message">{error}</p>}
      <div className="filter-group"><select className="filter-select" value={patientId} onChange={(event) => { setPatientId(event.target.value); loadPlans(event.target.value); }}><option value="">Select patient</option>{patients.map((patient) => <option key={patient.patientId} value={patient.patientId}>{patient.firstName} {patient.lastName} ({patient.patientId})</option>)}</select><button className="btn btn-primary" onClick={generate} disabled={!patientId || busy}><RefreshCw size={15} /> Generate care plan</button></div>
      <div className="stats-grid"><div className="stat-card"><div className="stat-content"><div className="stat-label">Plans for patient</div><div className="stat-value">{plans.length}</div></div><ClipboardCheck className="stat-icon" /></div><div className="stat-card"><div className="stat-content"><div className="stat-label">Adherence rate</div><div className="stat-value">{Number(adherence.adherencePercentage || 0).toFixed(0)}%</div></div><Check className="stat-icon" /></div><div className="stat-card"><div className="stat-content"><div className="stat-label">Tracked outcomes</div><div className="stat-value">{outcomes.length}</div></div><Target className="stat-icon" /></div><div className="stat-card"><div className="stat-content"><div className="stat-label">Pending reviews</div><div className="stat-value">{plans.filter((plan) => plan.status === 'PENDING_REVIEW').length}</div></div><ShieldAlert className="stat-icon" /></div></div>
      <div className="activity-grid"><div className="table-wrapper"><div className="monitoring-table-header"><div><h3 className="section-title">Care plans</h3><p className="section-subtitle">Select a plan to review its complete workflow.</p></div></div><div className="table-responsive"><table><thead><tr><th>Title</th><th>Status</th><th>Created</th></tr></thead><tbody>{plans.map((plan) => <tr key={plan.carePlanId} onClick={() => loadDetails(plan)}><td>{plan.title}</td><td><span className={`badge ${badgeClass(plan.status)}`}>{plan.status}</span></td><td>{plan.createdAt}</td></tr>)}{!plans.length && <tr><td colSpan="3" className="empty-state">No care plan has been generated for this patient.</td></tr>}</tbody></table></div></div>
        <div className="card">{selected ? <><h3 className="chart-title">{selected.title}</h3><p>{selected.description}</p><div className="careplan-detail-row"><strong>Status</strong><span className={`badge ${badgeClass(selected.status)}`}>{selected.status}</span></div><div className="careplan-detail-row"><strong>Risk</strong><span>{selected.riskInformation?.riskPercentage == null ? 'No persisted prediction' : `${selected.riskInformation.riskPercentage}% ${selected.riskInformation.riskCategory || ''}`}</span></div><h4>Goals</h4>{selected.goals?.map((goal) => <p key={goal.targetMetric}>{goal.description}: {goal.targetMetric} to {goal.targetValue} {goal.targetUnit}</p>)}<h4>Monitoring</h4>{selected.monitoringTasks?.map((task) => <p key={task.taskId}>{task.metric} · {task.frequency} · {task.targetRange}</p>)}<div className="action-buttons"><Link className="action-btn" to={`/careplans/${selected.carePlanId}`}>Open full details</Link><button className="action-btn" disabled={busy} onClick={() => action(`/api/careplans/${selected.carePlanId}/validate`)}>Validate</button>{selected.status === 'PENDING_REVIEW' && <><button className="action-btn" disabled={busy} onClick={() => review('approve')}>Approve</button><button className="action-btn" disabled={busy} onClick={() => review('reject')}>Reject</button></>}{selected.status === 'ACTIVE' && <button className="action-btn" disabled={busy} onClick={recordAdherence}>Record adherence</button>}<button className="action-btn" disabled={busy} onClick={addOutcome}>Add outcome</button></div><p className="ai-disclaimer">Guideline result: {selected.guidelineValidation?.compliant ? 'Compliant demo rules' : 'Needs review'} · medication changes are never autonomous.</p></> : <p>Select or generate a care plan to begin provider review.</p>}</div></div>
      {selected && <div className="table-wrapper"><div className="monitoring-table-header"><div><h3 className="section-title">Outcome progress</h3><p className="section-subtitle">Persisted measurements only; no outcomes are fabricated.</p></div></div><div className="table-responsive"><table><thead><tr><th>Metric</th><th>Baseline</th><th>Current</th><th>Target</th><th>Progress</th></tr></thead><tbody>{outcomes.map((outcome) => <tr key={outcome.outcomeId}><td>{outcome.metric}</td><td>{outcome.baselineValue ?? 'N/A'}</td><td>{outcome.currentValue}</td><td>{outcome.targetValue}</td><td>{outcome.progressPercentage == null ? 'N/A' : `${Number(outcome.progressPercentage).toFixed(0)}%`}</td></tr>)}{!outcomes.length && <tr><td colSpan="5" className="empty-state">No measured outcomes yet.</td></tr>}</tbody></table></div></div>}
    </div>
  </MainLayout>;
}