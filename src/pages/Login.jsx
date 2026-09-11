import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/pages.css';
import { login } from '../api';

export default function Login() {
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  const handleLogin = (e) => {
    e.preventDefault();
    setIsLoading(true);

    setError('');
    login(username, password).then(() => {
      navigate('/dashboard');
      setIsLoading(false);
    }).catch((requestError) => {
      const isBackendUnavailable = requestError instanceof TypeError || requestError.message.includes('Failed to fetch');
      setError(isBackendUnavailable
        ? 'The MediSphere backend is unavailable. Start it on port 8080, then try again.'
        : 'Unable to authenticate. Check your username and password.');
      setIsLoading(false);
    });
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <div className="login-header">
          <div className="login-logo">🏥</div>
          <h1>MediSphere Cognitive Twin</h1>
          <p className="login-tagline">Predict • Monitor • Prevent</p>
        </div>

        <form onSubmit={handleLogin} className="login-form">
          {error && <p className="error-message">{error}</p>}
          <div className="form-group">
            <label htmlFor="username">Username</label>
            <input
              id="username"
              type="text"
              placeholder="Enter your username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              id="password"
              type="password"
              placeholder="Enter your password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          <button
            type="submit"
            className="btn btn-primary btn-lg login-button"
            disabled={isLoading}
          >
            {isLoading ? 'Logging in...' : 'Login'}
          </button>
        </form>

        <div className="login-footer">
          <p className="demo-note">Demo Environment</p>
        </div>
      </div>
    </div>
  );
}
