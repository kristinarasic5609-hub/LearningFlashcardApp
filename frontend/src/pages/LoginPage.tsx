import { FormEvent, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../services/authContext';
import { ApiError } from '../services/api';
import { FormField } from '../components/FormField';
import { useNotification } from '../services/notificationContext';

export function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const { notify } = useNotification();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await login(email, password);
      notify('Logged in successfully', 'success');
      navigate('/dashboard');
    } catch (err) {
      const msg = err instanceof ApiError ? err.message : 'Login failed';
      setError(msg);
      notify(msg, 'error');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="auth-page">
      <section className="auth-card">
        <div className="auth-card-header">
          <h1>Welcome back</h1>
          <p>Sign in to continue your learning journey.</p>
        </div>
        <form onSubmit={handleSubmit}>
          {error && <div className="form-error-banner" role="alert">{error}</div>}
          <FormField
            label="Email address"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="you@example.com"
            required
          />
          <FormField
            label="Password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Enter your password"
            required
            minLength={6}
            hint="Minimum 6 characters"
          />
          <button type="submit" className="btn btn-primary btn-block btn-lg" disabled={loading}>
            {loading ? 'Signing in...' : 'Sign in'}
          </button>
        </form>
        <p className="auth-footer">
          Don&apos;t have an account? <Link to="/register">Create one</Link>
        </p>
      </section>
    </div>
  );
}
