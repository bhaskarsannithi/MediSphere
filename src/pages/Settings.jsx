import React, { useState } from 'react';
import MainLayout from '../layouts/MainLayout';
import { mockUserProfile } from '../data/mockData';
import '../styles/pages.css';

export default function Settings() {
  const [settings, setSettings] = useState({
    fullName: mockUserProfile.name,
    email: mockUserProfile.email,
    department: mockUserProfile.department,
    notifications: true,
    emailDigest: false,
    twoFactor: true,
    darkMode: false,
    language: 'en',
  });

  const handleChange = (field, value) => {
    setSettings({ ...settings, [field]: value });
  };

  const handleSave = () => {
    alert('Settings saved successfully!');
  };

  return (
    <MainLayout title="Settings">
      <div className="dashboard-container">
        {/* Profile Section */}
        <div className="card">
          <h3 className="section-title" style={{ marginBottom: 'var(--spacing-6)' }}>
            Profile Information
          </h3>

          <div style={{ display: 'flex', flexDirection: 'column', gap: 'var(--spacing-6)' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 'var(--spacing-4)' }}>
              <div
                style={{
                  fontSize: '2.5rem',
                  width: '80px',
                  height: '80px',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  backgroundColor: 'var(--background)',
                  borderRadius: 'var(--radius-lg)',
                }}
              >
                {mockUserProfile.avatar}
              </div>
              <div>
                <div style={{ fontSize: '1rem', fontWeight: '600' }}>
                  {mockUserProfile.name}
                </div>
                <div style={{ fontSize: '0.875rem', color: 'var(--text-secondary)', marginTop: '4px' }}>
                  {mockUserProfile.role} • {mockUserProfile.department}
                </div>
              </div>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 'var(--spacing-6)' }}>
              <div className="form-group">
                <label>Full Name</label>
                <input
                  type="text"
                  value={settings.fullName}
                  onChange={(e) => handleChange('fullName', e.target.value)}
                />
              </div>

              <div className="form-group">
                <label>Email</label>
                <input
                  type="email"
                  value={settings.email}
                  onChange={(e) => handleChange('email', e.target.value)}
                />
              </div>

              <div className="form-group">
                <label>Department</label>
                <input
                  type="text"
                  value={settings.department}
                  onChange={(e) => handleChange('department', e.target.value)}
                />
              </div>
            </div>
          </div>
        </div>

        {/* Notification Settings */}
        <div className="card">
          <h3 className="section-title" style={{ marginBottom: 'var(--spacing-6)' }}>
            Notifications
          </h3>

          <div style={{ display: 'flex', flexDirection: 'column', gap: 'var(--spacing-4)' }}>
            <div
              style={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                padding: 'var(--spacing-3)',
                backgroundColor: 'var(--background)',
                borderRadius: 'var(--radius-md)',
              }}
            >
              <div>
                <div style={{ fontWeight: '500' }}>Push Notifications</div>
                <div style={{ fontSize: '0.875rem', color: 'var(--text-secondary)', marginTop: '4px' }}>
                  Receive alerts for critical patient events
                </div>
              </div>
              <input
                type="checkbox"
                checked={settings.notifications}
                onChange={(e) => handleChange('notifications', e.target.checked)}
                style={{ width: '20px', height: '20px', cursor: 'pointer' }}
              />
            </div>

            <div
              style={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                padding: 'var(--spacing-3)',
                backgroundColor: 'var(--background)',
                borderRadius: 'var(--radius-md)',
              }}
            >
              <div>
                <div style={{ fontWeight: '500' }}>Email Digest</div>
                <div style={{ fontSize: '0.875rem', color: 'var(--text-secondary)', marginTop: '4px' }}>
                  Receive daily summary of activities
                </div>
              </div>
              <input
                type="checkbox"
                checked={settings.emailDigest}
                onChange={(e) => handleChange('emailDigest', e.target.checked)}
                style={{ width: '20px', height: '20px', cursor: 'pointer' }}
              />
            </div>
          </div>
        </div>

        {/* Display Preferences */}
        <div className="card">
          <h3 className="section-title" style={{ marginBottom: 'var(--spacing-6)' }}>
            Display Preferences
          </h3>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 'var(--spacing-6)' }}>
            <div className="form-group">
              <label>Language</label>
              <select
                value={settings.language}
                onChange={(e) => handleChange('language', e.target.value)}
              >
                <option value="en">English</option>
                <option value="es">Spanish</option>
                <option value="fr">French</option>
                <option value="de">German</option>
              </select>
            </div>

            <div
              style={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                padding: 'var(--spacing-3)',
                backgroundColor: 'var(--background)',
                borderRadius: 'var(--radius-md)',
              }}
            >
              <div>
                <div style={{ fontWeight: '500' }}>Dark Mode</div>
              </div>
              <input
                type="checkbox"
                checked={settings.darkMode}
                onChange={(e) => handleChange('darkMode', e.target.checked)}
                style={{ width: '20px', height: '20px', cursor: 'pointer' }}
              />
            </div>
          </div>
        </div>

        {/* Security Settings */}
        <div className="card">
          <h3 className="section-title" style={{ marginBottom: 'var(--spacing-6)' }}>
            Security
          </h3>

          <div
            style={{
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
              padding: 'var(--spacing-3)',
              backgroundColor: 'var(--background)',
              borderRadius: 'var(--radius-md)',
              marginBottom: 'var(--spacing-4)',
            }}
          >
            <div>
              <div style={{ fontWeight: '500' }}>Two-Factor Authentication</div>
              <div style={{ fontSize: '0.875rem', color: 'var(--text-secondary)', marginTop: '4px' }}>
                Enhanced security with 2FA enabled
              </div>
            </div>
            <input
              type="checkbox"
              checked={settings.twoFactor}
              onChange={(e) => handleChange('twoFactor', e.target.checked)}
              style={{ width: '20px', height: '20px', cursor: 'pointer' }}
            />
          </div>
        </div>

        {/* System Information */}
        <div className="card">
          <h3 className="section-title" style={{ marginBottom: 'var(--spacing-6)' }}>
            System Information
          </h3>

          <div
            style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
              gap: 'var(--spacing-6)',
            }}
          >
            <div>
              <div style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
                Application Version
              </div>
              <div style={{ fontSize: '1rem', fontWeight: '600', marginTop: '4px' }}>
                1.0.0
              </div>
            </div>

            <div>
              <div style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
                API Version
              </div>
              <div style={{ fontSize: '1rem', fontWeight: '600', marginTop: '4px' }}>
                v2.1.0
              </div>
            </div>

            <div>
              <div style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
                Last Update
              </div>
              <div style={{ fontSize: '1rem', fontWeight: '600', marginTop: '4px' }}>
                2025-09-01
              </div>
            </div>
          </div>
        </div>

        {/* Save Button */}
        <button className="btn btn-primary btn-lg" onClick={handleSave} style={{ width: '100%' }}>
          Save Settings
        </button>
      </div>
    </MainLayout>
  );
}
