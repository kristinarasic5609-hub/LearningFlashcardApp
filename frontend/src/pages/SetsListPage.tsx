import { useEffect, useState } from 'react';
import { api, ApiError } from '../services/api';
import { FlashcardSet } from '../models/types';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { EmptyState } from '../components/EmptyState';
import { FlashcardSetCard } from '../components/FlashcardSetCard';
import { PageHeader } from '../components/PageHeader';
import { useNotification } from '../services/notificationContext';

export function SetsListPage() {
  const [sets, setSets] = useState<FlashcardSet[]>([]);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);
  const { notify } = useNotification();

  useEffect(() => {
    loadSets();
  }, []);

  async function loadSets(query = '') {
    setLoading(true);
    try {
      const data = (await api.getSets(query)) as FlashcardSet[];
      setSets(data);
    } catch (err) {
      notify(err instanceof ApiError ? err.message : 'Failed to load sets', 'error');
    } finally {
      setLoading(false);
    }
  }

  function handleSearch(e: React.FormEvent) {
    e.preventDefault();
    loadSets(search);
  }

  return (
    <section>
      <PageHeader
        title="Public Flashcard Sets"
        subtitle="Discover and study flashcard collections shared by the community."
      />

      <form className="search-bar" onSubmit={handleSearch}>
        <div className="search-input-wrap">
          <span className="search-icon" aria-hidden="true">🔍</span>
          <input
            type="text"
            placeholder="Search by title, description, or category..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>
        <button type="submit" className="btn btn-primary">
          Search
        </button>
      </form>

      {loading ? (
        <LoadingSpinner />
      ) : sets.length === 0 ? (
        <EmptyState
          icon="🔎"
          title="No sets found"
          message="Try a different search term or check back later for new collections."
        />
      ) : (
        <div className="set-grid">
          {sets.map((set) => (
            <FlashcardSetCard key={set.id} set={set} variant="browse" />
          ))}
        </div>
      )}
    </section>
  );
}
