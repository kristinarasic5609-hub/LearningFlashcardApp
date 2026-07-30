import { FormEvent, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../services/authContext';
import { useNotification } from '../services/notificationContext';
import { ApiError } from '../services/api';
import { PageHeader } from '../components/PageHeader';
import { FormField } from '../components/FormField';

export function ProfilePage() {
  const { user, updateProfile } = useAuth();
  const { notify } = useNotification();
  const navigate = useNavigate();

  const [email, setEmail] = useState(user?.email ?? '');
  const [username, setUsername] = useState(user?.username ?? '');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setError('');
    setSubmitting(true);

    try {
      await updateProfile(email, username, password || undefined);
      notify('Profile updated successfully', 'success');
      navigate('/dashboard');
    } catch (err) {
      const message = err instanceof ApiError ? err.message : 'Failed to update profile';
      setError(message);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="page-narrow">
      <PageHeader
        title="Update Profile"
        subtitle="Change your account email, username, or password."
      />

      <form className="card form-card" onSubmit={handleSubmit}>
        {error && <div className="form-error-banner" role="alert">{error}</div>}

        <FormField
          label="Email"
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
          autoComplete="email"
        />

        <FormField
          label="Username"
          type="text"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
          minLength={2}
          maxLength={50}
          autoComplete="username"
        />

        <FormField
          label="New password"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          minLength={6}
          hint="Leave blank to keep your current password"
          autoComplete="new-password"
        />

        <div className="form-actions">
          <button type="submit" className="btn btn-primary" disabled={submitting}>
            {submitting ? 'Saving…' : 'Save changes'}
          </button>
        </div>
      </form>
    </div>
  );
}
