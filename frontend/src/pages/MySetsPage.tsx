import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api, ApiError } from '../services/api';
import { FlashcardSet } from '../models/types';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { EmptyState } from '../components/EmptyState';
import { FlashcardSetCard } from '../components/FlashcardSetCard';
import { PageHeader } from '../components/PageHeader';
import { useNotification } from '../services/notificationContext';

export function MySetsPage() {
  const [sets, setSets] = useState<FlashcardSet[]>([]);
  const [loading, setLoading] = useState(true);
  const { notify } = useNotification();

  useEffect(() => {
    loadSets();
  }, []);

  async function loadSets() {
    setLoading(true);
    try {
      const data = (await api.getMySets()) as FlashcardSet[];
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
      await api.deleteSet(id);
      notify('Set deleted', 'success');
      setSets((prev) => prev.filter((s) => s.id !== id));
    } catch (err) {
      notify(err instanceof ApiError ? err.message : 'Delete failed', 'error');
    }
  }

  return (
    <section>
      <PageHeader
        title="My Flashcard Sets"
        subtitle="Manage your personal flashcard collections."
        actions={
          <Link to="/my-sets/new" className="btn btn-primary">
            + Create New Set
          </Link>
        }
      />

      {loading ? (
        <LoadingSpinner />
      ) : sets.length === 0 ? (
        <EmptyState
          icon="📝"
          title="No sets yet"
          message="Create your first flashcard set and start building your learning library."
          action={
            <Link to="/my-sets/new" className="btn btn-primary">
              Create Your First Set
            </Link>
          }
        />
      ) : (
        <div className="set-grid">
          {sets.map((set) => (
            <FlashcardSetCard key={set.id} set={set} variant="owned" onDelete={handleDelete} />
          ))}
        </div>
      )}
    </section>
  );
}
