import { useEffect, useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { api, ApiError } from '../services/api';
import { User } from '../models/types';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { EmptyState } from '../components/EmptyState';
import { PageHeader } from '../components/PageHeader';
import { useNotification } from '../services/notificationContext';

export function AdminUsersPage() {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const { notify } = useNotification();
  const location = useLocation();

  useEffect(() => {
    loadUsers();
  }, []);

  async function loadUsers() {
    setLoading(true);
    try {
      const data = (await api.getAdminUsers()) as User[];
      setUsers(data);
    } catch (err) {
      notify(err instanceof ApiError ? err.message : 'Failed to load users', 'error');
    } finally {
      setLoading(false);
    }
  }

  async function handleDelete(id: string) {
    if (!confirm('Delete this user?')) return;
    try {
      await api.deleteAdminUser(id);
      setUsers((prev) => prev.filter((u) => u.id !== id));
      notify('User deleted', 'success');
    } catch (err) {
      notify(err instanceof ApiError ? err.message : 'Delete failed', 'error');
    }
  }

  return (
    <section>
      <PageHeader
        title="Admin Panel"
        subtitle="Manage users and flashcard sets across the platform."
      />

      <div className="admin-tabs">
        <Link to="/admin/users" className={`admin-tab${location.pathname.includes('/users') ? ' active' : ''}`}>
          Users
        </Link>
        <Link to="/admin/sets" className={`admin-tab${location.pathname.includes('/sets') ? ' active' : ''}`}>
          Flashcard Sets
        </Link>
      </div>

      <h2 className="section-title">Registered Users ({users.length})</h2>

      {loading ? (
        <LoadingSpinner />
      ) : users.length === 0 ? (
        <EmptyState title="No users" message="No registered users found." icon="👥" />
      ) : (
        <div className="table-wrapper">
          <table className="data-table">
            <thead>
              <tr>
                <th>Username</th>
                <th>Email</th>
                <th>Role</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {users.map((user) => (
                <tr key={user.id}>
                  <td><strong>{user.username}</strong></td>
                  <td>{user.email}</td>
                  <td>
                    <span className={`badge ${user.role === 'ADMIN' ? 'badge-role-admin' : 'badge-role-user'}`}>
                      {user.role}
                    </span>
                  </td>
                  <td>
                    <div className="table-actions">
                      <button type="button" className="btn btn-danger-outline btn-sm" onClick={() => handleDelete(user.id)}>
                        Delete
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}
