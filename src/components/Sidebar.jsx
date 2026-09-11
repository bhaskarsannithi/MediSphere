import { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import {
  Menu,
  Home,
  Users,
  Brain,
  FileText,
  Heart,
  Beaker,
  CheckCircle,
  BarChart3,
  Sparkles,
  Settings,
  X,
} from 'lucide-react';
import { mockUserProfile } from '../data/mockData';
import { logout } from '../api';
import '../styles/layout.css';

export default function Sidebar() {
  const [isOpen, setIsOpen] = useState(true);
  const location = useLocation();
  const navigate = useNavigate();

  const navItems = [
    { name: 'Dashboard', icon: Home, path: '/dashboard' },
    { name: 'Patients', icon: Users, path: '/patients' },
    { name: 'Digital Twin', icon: Brain, path: '/digital-twin' },
    { name: 'FHIR Resources', icon: FileText, path: '/fhir-resources' },
    { name: 'Vitals', icon: Heart, path: '/vitals' },
    { name: 'Lab Results', icon: Beaker, path: '/lab-results' },
    { name: 'Consent', icon: CheckCircle, path: '/consent' },
    { name: 'Audit Logs', icon: BarChart3, path: '/audit-logs' },
    { name: 'AI Risk Prediction', icon: Sparkles, path: '/ai-risk-prediction' },
    { name: 'Settings', icon: Settings, path: '/settings' },
  ];

  const isActive = (path) => location.pathname === path;

  return (
    <>
      {/* Mobile menu button */}
      <button className="sidebar-toggle" onClick={() => setIsOpen(!isOpen)}>
        {isOpen ? <X size={24} /> : <Menu size={24} />}
      </button>

      {/* Sidebar */}
      <aside className={`sidebar ${isOpen ? 'open' : 'closed'}`}>
        {/* Logo */}
        <div className="sidebar-header">
          <Link to="/dashboard" className="logo">
            <span className="logo-icon">🏥</span>
            <span className="logo-text">MediSphere</span>
          </Link>
        </div>

        {/* Navigation */}
        <nav className="sidebar-nav">
          {navItems.map((item) => {
            const Icon = item.icon;
            return (
              <Link
                key={item.path}
                to={item.path}
                className={`nav-item ${isActive(item.path) ? 'active' : ''}`}
              >
                <Icon size={20} />
                <span>{item.name}</span>
              </Link>
            );
          })}
        </nav>

        {/* Bottom Profile Section */}
        <div className="sidebar-footer">
          <div className="user-profile">
            <div className="user-avatar">{mockUserProfile.avatar}</div>
            <div className="user-info">
              <div className="user-name">{mockUserProfile.name}</div>
              <div className="user-role">{mockUserProfile.role}</div>
            </div>
          </div>
          <button
            className="logout-btn"
            onClick={() => {
              logout();
              navigate('/login');
            }}
          >
            Logout
          </button>
        </div>
      </aside>

      {/* Overlay for mobile */}
      {isOpen && (
        <div className="sidebar-overlay" onClick={() => setIsOpen(false)} />
      )}
    </>
  );
}
