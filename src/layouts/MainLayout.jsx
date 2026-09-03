import React from 'react';
import Sidebar from '../components/Sidebar';
import TopBar from '../components/TopBar';
import '../styles/layout.css';

export default function MainLayout({ title, children }) {
  return (
    <div className="main-layout">
      <Sidebar />
      <div className="main-content">
        <TopBar title={title} />
        <div className="content-area">
          {children}
        </div>
      </div>
    </div>
  );
}
