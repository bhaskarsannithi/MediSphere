import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { useEffect, useState } from 'react';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Patients from './pages/Patients';
import Patient360 from './pages/Patient360';
import DigitalTwin from './pages/DigitalTwin';
import FHIRResources from './pages/FHIRResources';
import Vitals from './pages/Vitals';
import LabResults from './pages/LabResults';
import Consent from './pages/Consent';
import AuditLogs from './pages/AuditLogs';
import AIRiskPrediction from './pages/AIRiskPrediction';
import Settings from './pages/Settings';
import Monitoring from './pages/Monitoring';
import CarePlans from './pages/CarePlans';
import CarePlanDetails from './pages/CarePlanDetails';
import './styles/theme.css';
import './styles/layout.css';
import './styles/pages.css';
import './App.css';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(
    Boolean(localStorage.getItem('medisphereCredentials')),
  );

  useEffect(() => {
    const handleAuthChange = () => {
      setIsAuthenticated(Boolean(localStorage.getItem('medisphereCredentials')));
    };
    window.addEventListener('medisphere-auth-change', handleAuthChange);
    return () => window.removeEventListener('medisphere-auth-change', handleAuthChange);
  }, []);

  return (
    <Router>
      <Routes>
        {!isAuthenticated ? (
          <>
            <Route path="/login" element={<Login />} />
            <Route path="*" element={<Navigate to="/login" />} />
          </>
        ) : (
          <>
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/patients" element={<Patients />} />
            <Route path="/patient/:patientId" element={<Patient360 />} />
            <Route path="/digital-twin" element={<DigitalTwin />} />
            <Route path="/fhir-resources" element={<FHIRResources />} />
            <Route path="/vitals" element={<Vitals />} />
            <Route path="/monitoring" element={<Monitoring />} />
            <Route path="/careplans" element={<CarePlans />} />
            <Route path="/careplans/:carePlanId" element={<CarePlanDetails />} />
            <Route path="/lab-results" element={<LabResults />} />
            <Route path="/consent" element={<Consent />} />
            <Route path="/audit-logs" element={<AuditLogs />} />
            <Route path="/ai-risk-prediction" element={<AIRiskPrediction />} />
            <Route path="/settings" element={<Settings />} />
            <Route path="/" element={<Navigate to="/dashboard" />} />
            <Route path="*" element={<Navigate to="/dashboard" />} />
          </>
        )}
      </Routes>
    </Router>
  );
}

export default App;
