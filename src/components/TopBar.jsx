import React, { useState } from 'react';
import { Search, Bell, Settings } from 'lucide-react';
import '../styles/layout.css';

export default function TopBar({ title }) {
  const [searchQuery, setSearchQuery] = useState('');

  return (
    <header className="topbar">
      <div className="topbar-left">
        <h1 className="page-title">{title}</h1>
      </div>

      <div className="topbar-center">
        <div className="search-box">
          <Search size={18} />
          <input
            type="text"
            placeholder="Search patients..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>
      </div>

      <div className="topbar-right">
        <button className="icon-button notification-button">
          <Bell size={20} />
          <span className="notification-badge">3</span>
        </button>
        <button className="icon-button">
          <Settings size={20} />
        </button>
      </div>
    </header>
  );
}
