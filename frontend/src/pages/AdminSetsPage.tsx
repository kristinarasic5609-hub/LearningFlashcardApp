import { useEffect, useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { api, ApiError } from '../services/api';
import { FlashcardSet } from '../models/types';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { EmptyState } from '../components/EmptyState';
import { PageHeader } from '../components/PageHeader';
import { useNotification } from '../services/notificationContext';

export function AdminSetsPage() {
  const [sets, setSets] = useState<FlashcardSet[]>([]);
  const [loading, setLoading] = useState(true);
  const { notify } = useNotification();
  const location = useLocation();

  useEffect(() => {
    loadSets();
  }, []);

  async function loadSets() {
    setLoading(true);
    try {
      const data = (await api.getAdminSets()) as FlashcardSet[];
      setSets(data);
    } catch (err) {
      notify(err instanceof ApiError ? err.message : 'Failed to load sets', 'error');
    } finally {
      setLoading(false);
    }
  }

  async function handleDelete(id: string) {
    if (!confirm('Delete this flashcard set?')) return;
    try {
      await api.deleteAdminSet(id);
      setSets((prev) => prev.filter((s) => s.id !== id));
      notify('Set deleted', 'success');
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

      <h2 className="section-title">All Flashcard Sets ({sets.length})</h2>

      {loading ? (
        <LoadingSpinner />
      ) : sets.length === 0 ? (
        <EmptyState title="No sets" message="No flashcard sets found." icon="📚" />
      ) : (
        <div className="table-wrapper">
          <table className="data-table">
            <thead>
              <tr>
                <th>Title</th>
                <th>Category</th>
                <th>Owner</th>
                <th>Cards</th>
                <th>Visibility</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {sets.map((set) => (
                <tr key={set.id}>
                  <td><strong>{set.title}</strong></td>
                  <td><span className="badge badge-category">{set.category}</span></td>
                  <td>{set.owner?.username || 'Unknown'}</td>
                  <td>{set.flashcards.length}</td>
                  <td>{set.isPublic ? 'Public' : 'Private'}</td>
                  <td>
                    <div className="table-actions">
                      <button type="button" className="btn btn-danger-outline btn-sm" onClick={() => handleDelete(set.id)}>
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
