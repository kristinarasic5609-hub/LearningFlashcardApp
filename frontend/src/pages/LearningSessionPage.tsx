import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { api, ApiError } from '../services/api';
import { LearningSessionStart } from '../models/types';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { useNotification } from '../services/notificationContext';

export function LearningSessionPage() {
  const { setId } = useParams<{ setId: string }>();
  const { notify } = useNotification();
  const [session, setSession] = useState<LearningSessionStart | null>(null);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [showAnswer, setShowAnswer] = useState(false);
  const [completed, setCompleted] = useState(false);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (setId) startSession(setId);
  }, [setId]);

  async function startSession(id: string) {
    setLoading(true);
    try {
      const data = (await api.startLearning(id)) as LearningSessionStart;
      setSession(data);
    } catch (err) {
      notify(err instanceof ApiError ? err.message : 'Failed to start session', 'error');
    } finally {
      setLoading(false);
    }
  }

  async function handleAnswer(known: boolean) {
    if (!session || submitting) return;
    const card = session.flashcards[currentIndex];
    setSubmitting(true);
    try {
      await api.recordResult({
        sessionId: session.sessionId,
        flashcardId: card.id,
        known,
      });

      if (currentIndex + 1 >= session.flashcards.length) {
        setCompleted(true);
        notify('Learning session completed!', 'success');
      } else {
        setCurrentIndex((i) => i + 1);
        setShowAnswer(false);
      }
    } catch (err) {
      notify(err instanceof ApiError ? err.message : 'Failed to save result', 'error');
    } finally {
      setSubmitting(false);
    }
  }

  if (loading) return <LoadingSpinner label="Starting session..." />;
  if (!session) return <p>Could not start learning session.</p>;

  if (completed) {
    return (
      <section className="learning-complete">
        <div className="complete-icon" aria-hidden="true">🎉</div>
        <h1>Session Complete!</h1>
        <p>You studied {session.flashcards.length} cards. Great work — keep the momentum going!</p>
        <div className="actions" style={{ justifyContent: 'center' }}>
          <Link to="/statistics" className="btn btn-primary">
            View Statistics
          </Link>
          <Link to={`/sets/${session.flashcardSetId}`} className="btn btn-secondary">
            Back to Set
          </Link>
        </div>
      </section>
    );
  }

  const card = session.flashcards[currentIndex];
  const progress = ((currentIndex) / session.flashcards.length) * 100;

  return (
    <section className="learning-page">
      <div className="learning-progress">
        <div className="learning-progress-header">
          <span>Card {currentIndex + 1} of {session.flashcards.length}</span>
          <span>{Math.round(progress)}% complete</span>
        </div>
        <div className="progress-bar" role="progressbar" aria-valuenow={currentIndex + 1} aria-valuemin={1} aria-valuemax={session.flashcards.length}>
          <div className="progress-bar-fill" style={{ width: `${progress}%` }} />
        </div>
      </div>

      <div className="study-card">
        <span className={`study-card-label ${showAnswer ? 'study-card-label-answer' : 'study-card-label-question'}`}>
          {showAnswer ? 'Answer' : 'Question'}
        </span>
        <p className="study-card-text">{showAnswer ? card.answer : card.question}</p>
        {!showAnswer ? (
          <button type="button" className="btn btn-primary btn-lg" onClick={() => setShowAnswer(true)}>
            Reveal Answer
          </button>
        ) : (
          <div className="study-actions">
            <button
              type="button"
              className="btn btn-know"
              disabled={submitting}
              onClick={() => handleAnswer(true)}
            >
              ✓ I know
            </button>
            <button
              type="button"
              className="btn btn-dont-know"
              disabled={submitting}
              onClick={() => handleAnswer(false)}
            >
              ✗ I don&apos;t know
            </button>
          </div>
        )}
      </div>
    </section>
  );
}
