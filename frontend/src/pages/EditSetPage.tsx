import { FormEvent, useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { api, ApiError } from '../services/api';
import { Flashcard, FlashcardSet } from '../models/types';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { FormField } from '../components/FormField';
import { PageHeader } from '../components/PageHeader';
import { useNotification } from '../services/notificationContext';

export function EditSetPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { notify } = useNotification();
  const [set, setSet] = useState<FlashcardSet | null>(null);
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [category, setCategory] = useState('');
  const [isPublic, setIsPublic] = useState(true);
  const [question, setQuestion] = useState('');
  const [answer, setAnswer] = useState('');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (id) loadSet(id);
  }, [id]);

  async function loadSet(setId: string) {
    setLoading(true);
    try {
      const data = (await api.getSet(setId)) as FlashcardSet;
      setSet(data);
      setTitle(data.title);
      setDescription(data.description);
      setCategory(data.category);
      setIsPublic(data.isPublic);
    } catch (err) {
      notify(err instanceof ApiError ? err.message : 'Failed to load set', 'error');
    } finally {
      setLoading(false);
    }
  }

  async function handleUpdateSet(e: FormEvent) {
    e.preventDefault();
    if (!id) return;
    setSaving(true);
    try {
      await api.updateSet(id, { title, description, category, isPublic });
      notify('Set updated', 'success');
    } catch (err) {
      notify(err instanceof ApiError ? err.message : 'Update failed', 'error');
    } finally {
      setSaving(false);
    }
  }

  async function handleAddCard(e: FormEvent) {
    e.preventDefault();
    if (!id) return;
    try {
      const card = (await api.addCard(id, { question, answer })) as Flashcard;
      setSet((prev) => (prev ? { ...prev, flashcards: [...prev.flashcards, card] } : prev));
      setQuestion('');
      setAnswer('');
      notify('Flashcard added', 'success');
    } catch (err) {
      notify(err instanceof ApiError ? err.message : 'Failed to add card', 'error');
    }
  }

  async function handleDeleteCard(cardId: string) {
    if (!confirm('Delete this flashcard?')) return;
    try {
      await api.deleteCard(cardId);
      setSet((prev) =>
        prev ? { ...prev, flashcards: prev.flashcards.filter((c) => c.id !== cardId) } : prev
      );
      notify('Flashcard deleted', 'success');
    } catch (err) {
      notify(err instanceof ApiError ? err.message : 'Delete failed', 'error');
    }
  }

  if (loading) return <LoadingSpinner />;
  if (!set) return <p>Set not found.</p>;

  return (
    <section>
      <PageHeader
        title="Edit Flashcard Set"
        subtitle={`Editing "${set.title}"`}
      />

      <div className="form-card form-card-wide">
        <h2 className="section-title">Set details</h2>
        <form onSubmit={handleUpdateSet}>
          <FormField label="Title" value={title} onChange={(e) => setTitle(e.target.value)} required />
          <FormField
            as="textarea"
            label="Description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
          />
          <FormField label="Category" value={category} onChange={(e) => setCategory(e.target.value)} required />
          <label className="checkbox-field">
            <input type="checkbox" checked={isPublic} onChange={(e) => setIsPublic(e.target.checked)} />
            <span>Public set</span>
          </label>
          <button type="submit" className="btn btn-primary" disabled={saving}>
            {saving ? 'Saving...' : 'Save Changes'}
          </button>
        </form>

        <hr className="form-divider" />

        <h2 className="section-title">Flashcards ({set.flashcards.length})</h2>
        <form onSubmit={handleAddCard}>
          <FormField
            label="Question"
            value={question}
            onChange={(e) => setQuestion(e.target.value)}
            placeholder="Enter the question"
            required
          />
          <FormField
            label="Answer"
            value={answer}
            onChange={(e) => setAnswer(e.target.value)}
            placeholder="Enter the answer"
            required
          />
          <button type="submit" className="btn btn-secondary">
            + Add Card
          </button>
        </form>

        {set.flashcards.length === 0 ? (
          <p className="muted" style={{ marginTop: '1.5rem' }}>
            No flashcards yet. Add your first card above.
          </p>
        ) : (
          <ul className="flashcard-list editable" style={{ marginTop: '1.5rem' }}>
            {set.flashcards.map((card) => (
              <li key={card.id}>
                <div>
                  <span className="flashcard-q">Question</span>
                  <p style={{ margin: '0.25rem 0 0.75rem' }}>{card.question}</p>
                  <span className="flashcard-q">Answer</span>
                  <p style={{ margin: '0.25rem 0 0' }}>{card.answer}</p>
                </div>
                <button type="button" className="btn btn-danger-outline btn-sm" onClick={() => handleDeleteCard(card.id)}>
                  Delete
                </button>
              </li>
            ))}
          </ul>
        )}

        <div className="actions" style={{ marginTop: '2rem' }}>
          <button type="button" className="btn btn-ghost" onClick={() => navigate('/my-sets')}>
            ← Back to My Sets
          </button>
        </div>
      </div>
    </section>
  );
}
