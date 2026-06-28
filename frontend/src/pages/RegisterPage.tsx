import { FormEvent, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../services/authContext';
import { ApiError } from '../services/api';
import { FormField } from '../components/FormField';
import { useNotification } from '../services/notificationContext';

export function RegisterPage() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const { notify } = useNotification();
  const [email, setEmail] = useState('');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await register(email, username, password);
      notify('Account created successfully', 'success');
      navigate('/dashboard');
    } catch (err) {
      const msg = err instanceof ApiError ? err.message : 'Registration failed';
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
          <h1>Create your account</h1>
          <p>Join the platform and start learning with flashcards.</p>
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
            label="Username"
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            placeholder="Choose a username"
            required
            minLength={2}
            hint="2–50 characters"
          />
          <FormField
            label="Password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Create a password"
            required
            minLength={6}
            hint="Minimum 6 characters"
          />
          <button type="submit" className="btn btn-primary btn-block btn-lg" disabled={loading}>
            {loading ? 'Creating account...' : 'Create account'}
          </button>
        </form>
        <p className="auth-footer">
          Already have an account? <Link to="/login">Sign in</Link>
        </p>
      </section>
    </div>
  );
}
