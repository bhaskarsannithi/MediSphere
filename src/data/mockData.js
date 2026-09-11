//  data for MediSphere application

export const Patients = [
  {
    id: 'MS-10001',
    name: 'John Doe',
    age: 52,
    gender: 'Male',
    consent: 'GRANTED',
    digitalTwin: 'ACTIVE',
    lastUpdated: '2 minutes ago',
    dateOfBirth: '1972-03-15',
    email: 'john.doe@email.com',
    phone: '+1 (555) 234-5678',
    address: '123 Medical Plaza, New York, NY',
  },
  {
    id: 'MS-10002',
    name: 'Sarah Miller',
    age: 38,
    gender: 'Female',
    consent: 'GRANTED',
    digitalTwin: 'ACTIVE',
    lastUpdated: '5 minutes ago',
    dateOfBirth: '1986-07-22',
    email: 'sarah.miller@email.com',
    phone: '+1 (555) 345-6789',
    address: '456 Health Center Dr, Los Angeles, CA',
  },
  {
    id: 'MS-10003',
    name: 'Emily Carter',
    age: 67,
    gender: 'Female',
    consent: 'GRANTED',
    digitalTwin: 'INCOMPLETE',
    lastUpdated: '12 minutes ago',
    dateOfBirth: '1959-01-10',
    email: 'emily.carter@email.com',
    phone: '+1 (555) 456-7890',
    address: '789 Wellness Ave, Chicago, IL',
  },
  {
    id: 'MS-10004',
    name: 'Michael Brown',
    age: 45,
    gender: 'Male',
    consent: 'PENDING',
    digitalTwin: 'ACTIVE',
    lastUpdated: '1 hour ago',
    dateOfBirth: '1979-11-05',
    email: 'michael.brown@email.com',
    phone: '+1 (555) 567-8901',
    address: '321 Care Road, Houston, TX',
  },
  {
    id: 'MS-10005',
    name: 'Sophia Wilson',
    age: 34,
    gender: 'Female',
    consent: 'GRANTED',
    digitalTwin: 'ACTIVE',
    lastUpdated: '30 minutes ago',
    dateOfBirth: '1992-06-28',
    email: 'sophia.wilson@email.com',
    phone: '+1 (555) 678-9012',
    address: '654 Health Blvd, Phoenix, AZ',
  },
  {
    id: 'MS-10006',
    name: 'David Johnson',
    age: 56,
    gender: 'Male',
    consent: 'REVOKED',
    digitalTwin: 'INACTIVE',
    lastUpdated: '2 days ago',
    dateOfBirth: '1968-09-12',
    email: 'david.johnson@email.com',
    phone: '+1 (555) 789-0123',
    address: '987 Medical St, Philadelphia, PA',
  },
];

export const PatientDetails = {
  'MS-10001': {
    id: 'MS-10001',
    name: 'John Doe',
    avatar: '👨‍⚕️',
    age: 52,
    gender: 'Male',
    dateOfBirth: '1972-03-15',
    email: 'john.doe@email.com',
    phone: '+1 (555) 234-5678',
    address: '123 Medical Plaza, New York, NY',
    fhirId: 'fhir-pat-10001',
    consent: 'GRANTED',
    digitalTwin: 'ACTIVE',
    lastUpdated: '2 minutes ago',
    consentPurpose: 'Clinical Data Processing',
    consentGrantedDate: '2025-08-25',
    dataCompleteness: 96,
    connectedSources: ['EHR', 'Laboratory', 'Wearable'],
  },
};

export const Vitals = {
  'MS-10001': {
    heartRate: 78,
    heartRateUnit: 'BPM',
    bloodPressureSystolic: 120,
    bloodPressureDiastolic: 80,
    spO2: 98,
    spO2Unit: '%',
    temperature: 36.7,
    temperatureUnit: '°C',
    lastUpdated: '2 minutes ago',
  },
};

export const VitalTrends = {
  'MS-10001': {
    heartRate: [
      { time: '00:00', value: 72 },
      { time: '02:00', value: 70 },
      { time: '04:00', value: 68 },
      { time: '06:00', value: 69 },
      { time: '08:00', value: 71 },
      { time: '10:00', value: 75 },
      { time: '12:00', value: 76 },
      { time: '14:00', value: 77 },
      { time: '16:00', value: 78 },
      { time: '18:00', value: 77 },
      { time: '20:00', value: 76 },
      { time: '22:00', value: 74 },
      { time: '23:59', value: 78 },
    ],
    spO2: [
      { time: '00:00', value: 97 },
      { time: '02:00', value: 97 },
      { time: '04:00', value: 96 },
      { time: '06:00', value: 96 },
      { time: '08:00', value: 97 },
      { time: '10:00', value: 98 },
      { time: '12:00', value: 98 },
      { time: '14:00', value: 98 },
      { time: '16:00', value: 98 },
      { time: '18:00', value: 98 },
      { time: '20:00', value: 97 },
      { time: '22:00', value: 97 },
      { time: '23:59', value: 98 },
    ],
    temperature: [
      { time: '00:00', value: 36.5 },
      { time: '02:00', value: 36.4 },
      { time: '04:00', value: 36.3 },
      { time: '06:00', value: 36.4 },
      { time: '08:00', value: 36.6 },
      { time: '10:00', value: 36.7 },
      { time: '12:00', value: 36.8 },
      { time: '14:00', value: 36.8 },
      { time: '16:00', value: 36.7 },
      { time: '18:00', value: 36.7 },
      { time: '20:00', value: 36.6 },
      { time: '22:00', value: 36.5 },
      { time: '23:59', value: 36.7 },
    ],
  },
};

export const LabResults = {
  'MS-10001': [
    {
      id: 'lab-001',
      test: 'Complete Blood Count',
      result: '7.2',
      unit: '10^9/L',
      referenceRange: '4.5-11.0',
      status: 'NORMAL',
      date: '2025-09-01',
    },
    {
      id: 'lab-002',
      test: 'Hemoglobin',
      result: '14.8',
      unit: 'g/dL',
      referenceRange: '13.5-17.5',
      status: 'NORMAL',
      date: '2025-09-01',
    },
    {
      id: 'lab-003',
      test: 'Blood Glucose',
      result: '105',
      unit: 'mg/dL',
      referenceRange: '70-100',
      status: 'SLIGHTLY_HIGH',
      date: '2025-08-30',
    },
    {
      id: 'lab-004',
      test: 'Cholesterol Total',
      result: '195',
      unit: 'mg/dL',
      referenceRange: '<200',
      status: 'NORMAL',
      date: '2025-08-30',
    },
    {
      id: 'lab-005',
      test: 'Creatinine',
      result: '0.9',
      unit: 'mg/dL',
      referenceRange: '0.7-1.3',
      status: 'NORMAL',
      date: '2025-08-28',
    },
  ],
};

export const FHIRResources = {
  'MS-10001': [
    {
      id: 'fhir-obs-001',
      resourceType: 'Observation',
      fhirId: 'obs-10001-001',
      status: 'FINAL',
      lastSync: '2 minutes ago',
      syncTime: '2025-09-01 20:27:00',
    },
    {
      id: 'fhir-obs-002',
      resourceType: 'Observation',
      fhirId: 'obs-10001-002',
      status: 'FINAL',
      lastSync: '5 minutes ago',
      syncTime: '2025-09-01 20:22:00',
    },
    {
      id: 'fhir-diag-001',
      resourceType: 'DiagnosticReport',
      fhirId: 'diag-10001-001',
      status: 'FINAL',
      lastSync: '1 hour ago',
      syncTime: '2025-09-01 19:27:00',
    },
    {
      id: 'fhir-med-001',
      resourceType: 'MedicationRequest',
      fhirId: 'med-10001-001',
      status: 'ACTIVE',
      lastSync: '2 days ago',
      syncTime: '2025-08-30 14:15:00',
    },
  ],
};

export const FHIRResourceDetails = {
  'obs-10001-001': {
    resourceType: 'Observation',
    id: 'obs-10001-001',
    status: 'final',
    category: [
      {
        coding: [
          {
            system: 'http://terminology.hl7.org/CodeSystem/observation-category',
            code: 'vital-signs',
            display: 'Vital Signs',
          },
        ],
      },
    ],
    code: {
      coding: [
        {
          system: 'http://loinc.org',
          code: '8867-4',
          display: 'Heart rate',
        },
      ],
    },
    subject: {
      reference: 'Patient/MS-10001',
    },
    effectiveDateTime: '2025-09-01T20:27:00Z',
    valueQuantity: {
      value: 78,
      unit: 'beats/minute',
      system: 'http://unitsofmeasure.org',
      code: '/min',
    },
  },
  'diag-10001-001': {
    resourceType: 'DiagnosticReport',
    id: 'diag-10001-001',
    status: 'final',
    code: {
      coding: [
        {
          system: 'http://loinc.org',
          code: '85025-2',
          display: 'Complete blood count panel',
        },
      ],
    },
    subject: {
      reference: 'Patient/MS-10001',
    },
    issued: '2025-09-01T20:27:00Z',
    result: [
      {
        reference: 'Observation/obs-10001-001',
      },
    ],
  },
};

export const RecentActivity = [
  {
    id: 1,
    description: 'Vital signs updated',
    timestamp: '2 minutes ago',
    type: 'vital',
  },
  {
    id: 2,
    description: 'FHIR Observation synchronized',
    timestamp: '5 minutes ago',
    type: 'fhir',
  },
  {
    id: 3,
    description: 'Digital Twin updated',
    timestamp: '12 minutes ago',
    type: 'twin',
  },
  {
    id: 4,
    description: 'Lab result received',
    timestamp: '1 hour ago',
    type: 'lab',
  },
  {
    id: 5,
    description: 'Patient record accessed',
    timestamp: '2 hours ago',
    type: 'access',
  },
];

export const DashboardStats = {
  totalPatients: 1247,
  activeDigitalTwins: 1198,
  fhirResources: '2.4M',
  connectedDevices: 892,
};

export const SystemStatus = {
  fhirSync: { status: 'ACTIVE', lastSync: '2 minutes ago' },
  kafkaConnection: { status: 'ACTIVE', messagesProcessed: 25432 },
  databaseConnection: { status: 'ACTIVE', latency: '2ms' },
};

export const DigitalTwins = [
  {
    id: 'twin-001',
    patientId: 'MS-10001',
    patientName: 'John Doe',
    completeness: 96,
    sources: ['EHR', 'Laboratory', 'Wearable'],
    status: 'ACTIVE',
    lastUpdated: '2 minutes ago',
  },
  {
    id: 'twin-002',
    patientId: 'MS-10002',
    patientName: 'Sarah Miller',
    completeness: 92,
    sources: ['EHR', 'Laboratory'],
    status: 'ACTIVE',
    lastUpdated: '5 minutes ago',
  },
  {
    id: 'twin-003',
    patientId: 'MS-10003',
    patientName: 'Emily Carter',
    completeness: 75,
    sources: ['EHR'],
    status: 'INCOMPLETE',
    lastUpdated: '12 minutes ago',
  },
  {
    id: 'twin-004',
    patientId: 'MS-10004',
    patientName: 'Michael Brown',
    completeness: 88,
    sources: ['EHR', 'Laboratory', 'Wearable'],
    status: 'ACTIVE',
    lastUpdated: '1 hour ago',
  },
  {
    id: 'twin-005',
    patientId: 'MS-10005',
    patientName: 'Sophia Wilson',
    completeness: 94,
    sources: ['EHR', 'Laboratory', 'Wearable'],
    status: 'ACTIVE',
    lastUpdated: '30 minutes ago',
  },
];

export const Consents = [
  {
    id: 'consent-001',
    patientId: 'MS-10001',
    patientName: 'John Doe',
    consentId: 'CON-10001',
    purpose: 'Clinical Data Processing',
    status: 'GRANTED',
    grantedDate: '2025-08-25',
    expirationDate: '2026-08-25',
    daysUntilExpire: 359,
  },
  {
    id: 'consent-002',
    patientId: 'MS-10002',
    patientName: 'Sarah Miller',
    consentId: 'CON-10002',
    purpose: 'Research Study Participation',
    status: 'PENDING',
    grantedDate: null,
    expirationDate: null,
    daysUntilExpire: null,
  },
  {
    id: 'consent-003',
    patientId: 'MS-10003',
    patientName: 'Emily Carter',
    consentId: 'CON-10003',
    purpose: 'Clinical Data Processing',
    status: 'GRANTED',
    grantedDate: '2025-01-15',
    expirationDate: '2026-01-15',
    daysUntilExpire: 137,
  },
  {
    id: 'consent-004',
    patientId: 'MS-10004',
    patientName: 'Michael Brown',
    consentId: 'CON-10004',
    purpose: 'Clinical Data Processing',
    status: 'REVOKED',
    grantedDate: '2024-06-10',
    expirationDate: '2025-06-10',
    daysUntilExpire: 0,
  },
  {
    id: 'consent-005',
    patientId: 'MS-10005',
    patientName: 'Sophia Wilson',
    consentId: 'CON-10005',
    purpose: 'Clinical Data Processing',
    status: 'GRANTED',
    grantedDate: '2025-09-01',
    expirationDate: '2026-09-01',
    daysUntilExpire: 365,
  },
];

export const AuditLogs = [
  {
    id: 1,
    timestamp: '2025-09-01 20:27:00',
    user: 'Dr. Smith',
    role: 'Physician',
    action: 'VIEWED_PATIENT',
    resource: 'Patient Record',
    patient: 'John Doe (MS-10001)',
    result: 'SUCCESS',
  },
  {
    id: 2,
    timestamp: '2025-09-01 20:15:00',
    user: 'Nurse Jane',
    role: 'Nurse',
    action: 'UPDATED_VITALS',
    resource: 'Vital Signs',
    patient: 'John Doe (MS-10001)',
    result: 'SUCCESS',
  },
  {
    id: 3,
    timestamp: '2025-09-01 19:45:00',
    user: 'Dr. Johnson',
    role: 'Physician',
    action: 'VIEWED_TWIN',
    resource: 'Digital Twin',
    patient: 'Sarah Miller (MS-10002)',
    result: 'SUCCESS',
  },
  {
    id: 4,
    timestamp: '2025-09-01 18:30:00',
    user: 'Admin User',
    role: 'Administrator',
    action: 'SYSTEM_CONFIG_CHANGE',
    resource: 'System Settings',
    patient: 'N/A',
    result: 'SUCCESS',
  },
  {
    id: 5,
    timestamp: '2025-09-01 17:00:00',
    user: 'Dr. Smith',
    role: 'Physician',
    action: 'EXPORTED_DATA',
    resource: 'Patient Data',
    patient: 'Michael Brown (MS-10004)',
    result: 'FAILED',
  },
];

export const UserProfile = {
  name: 'Dr. Sarah Chen',
  role: 'Physician',
  email: 'sarah.chen@medisphere.com',
  department: 'Cardiology',
  avatar: '👩‍⚕️',
};

export const PatientActivityChart = [
  { date: 'Sep 1', active: 245, inactive: 98 },
  { date: 'Sep 2', active: 267, inactive: 89 },
  { date: 'Sep 3', active: 289, inactive: 76 },
  { date: 'Sep 4', active: 312, inactive: 71 },
  { date: 'Sep 5', active: 298, inactive: 82 },
  { date: 'Sep 6', active: 325, inactive: 65 },
  { date: 'Sep 7', active: 342, inactive: 58 },
];

export const VitalMonitoringChart = [
  { time: '00:00', systolic: 118, diastolic: 78 },
  { time: '04:00', systolic: 115, diastolic: 76 },
  { time: '08:00', systolic: 120, diastolic: 79 },
  { time: '12:00', systolic: 125, diastolic: 82 },
  { time: '16:00', systolic: 122, diastolic: 80 },
  { time: '20:00', systolic: 120, diastolic: 79 },
  { time: '23:59', systolic: 118, diastolic: 78 },
];

export const LiveVitalsData = [
  { id: 1, patientName: 'John Doe', heartRate: 78, spO2: 98, temp: 36.7, status: 'NORMAL' },
  { id: 2, patientName: 'Sarah Miller', heartRate: 72, spO2: 97, temp: 36.5, status: 'NORMAL' },
  { id: 3, patientName: 'Emily Carter', heartRate: 85, spO2: 96, temp: 37.1, status: 'MONITORING' },
  { id: 4, patientName: 'Michael Brown', heartRate: 68, spO2: 98, temp: 36.4, status: 'NORMAL' },
  { id: 5, patientName: 'Sophia Wilson', heartRate: 76, spO2: 98, temp: 36.6, status: 'NORMAL' },
];

export const mockUserProfile = UserProfile;
export const mockPatientActivityChart = PatientActivityChart;
export const mockVitalMonitoringChart = VitalMonitoringChart;
export const mockSystemStatus = SystemStatus;
export const mockRecentActivity = RecentActivity;
export const mockVitals = Vitals;
export const mockVitalTrends = VitalTrends;
export const mockLiveVitalsData = LiveVitalsData;
