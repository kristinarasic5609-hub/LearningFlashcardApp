import { Link } from 'react-router-dom';
import { FlashcardSet } from '../models/types';

interface FlashcardSetCardProps {
  set: FlashcardSet;
  variant?: 'browse' | 'owned';
  onDelete?: (id: string) => void;
}

export function FlashcardSetCard({ set, variant = 'browse', onDelete }: FlashcardSetCardProps) {
  return (
    <article className="set-card">
      <div className="set-card-header">
        <span className="badge badge-category">{set.category}</span>
        {set.isPublic === false && <span className="badge badge-private">Private</span>}
      </div>
      <h3 className="set-card-title">{set.title}</h3>
      <p className="set-card-description">{set.description || 'No description provided.'}</p>
      <div className="set-card-meta">
        <span className="meta-item">
          <span className="meta-icon" aria-hidden="true">📚</span>
          {set.flashcards.length} {set.flashcards.length === 1 ? 'card' : 'cards'}
        </span>
        {set.owner && (
          <span className="meta-item">
            <span className="meta-icon" aria-hidden="true">👤</span>
            {set.owner.username}
          </span>
        )}
      </div>
      <div className="set-card-actions">
        {variant === 'owned' ? (
          <>
            <Link to={`/sets/${set.id}`} className="btn btn-ghost btn-sm">
              View
            </Link>
            <Link to={`/my-sets/${set.id}/edit`} className="btn btn-secondary btn-sm">
              Edit
            </Link>
            <Link to={`/learn/${set.id}`} className="btn btn-primary btn-sm">
              Learn
            </Link>
            {onDelete && (
              <button type="button" className="btn btn-danger btn-sm" onClick={() => onDelete(set.id)}>
                Delete
              </button>
            )}
          </>
        ) : (
          <Link to={`/sets/${set.id}`} className="btn btn-primary btn-sm">
            View Details
          </Link>
        )}
      </div>
    </article>
  );
}
