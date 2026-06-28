import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { api, ApiError } from '../services/api';
import { FlashcardSet } from '../models/types';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { PageHeader } from '../components/PageHeader';
import { useAuth } from '../services/authContext';
import { useNotification } from '../services/notificationContext';

export function SetDetailsPage() {
  const { id } = useParams<{ id: string }>();
  const [set, setSet] = useState<FlashcardSet | null>(null);
  const [loading, setLoading] = useState(true);
  const { user } = useAuth();
  const { notify } = useNotification();

  useEffect(() => {
    if (id) loadSet(id);
  }, [id]);

  async function loadSet(setId: string) {
    setLoading(true);
    try {
      const data = (await api.getSet(setId)) as FlashcardSet;
      setSet(data);
    } catch (err) {
      notify(err instanceof ApiError ? err.message : 'Failed to load set', 'error');
    } finally {
      setLoading(false);
    }
  }

  if (loading) return <LoadingSpinner />;
  if (!set) return <p>Set not found.</p>;

  const isOwner = user?.id === set.ownerId;

  return (
    <section>
      <PageHeader
        title={set.title}
        subtitle={set.description}
        actions={
          <div className="actions">
            {user && (
              <Link to={`/learn/${set.id}`} className="btn btn-primary">
                Start Learning
              </Link>
            )}
            {isOwner && (
              <Link to={`/my-sets/${set.id}/edit`} className="btn btn-secondary">
                Edit Set
              </Link>
            )}
          </div>
        }
      />

      <div className="detail-panel">
        <div className="detail-meta">
          <span className="badge badge-category">{set.category}</span>
          {!set.isPublic && <span className="badge badge-private">Private</span>}
          <span className="meta-item">
            <span className="meta-icon" aria-hidden="true">📚</span>
            {set.flashcards.length} cards
          </span>
          <span className="meta-item">
            <span className="meta-icon" aria-hidden="true">👤</span>
            {set.owner?.username || 'Unknown'}
          </span>
        </div>
      </div>

      <h2 className="section-title">Flashcards</h2>
      {set.flashcards.length === 0 ? (
        <p className="muted">No flashcards in this set yet.</p>
      ) : (
        <ul className="flashcard-list">
          {set.flashcards.map((card, index) => (
            <li key={card.id}>
              <span className="flashcard-q">Card {index + 1}</span>
              <p style={{ margin: '0.35rem 0 0.75rem', fontWeight: 500 }}>{card.question}</p>
              <span className="flashcard-q">Answer</span>
              <p style={{ margin: '0.35rem 0 0' }}>{card.answer}</p>
            </li>
          ))}
        </ul>
      )}
    </section>
  );
}
