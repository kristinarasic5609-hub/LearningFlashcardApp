import { FormEvent, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api, ApiError } from '../services/api';
import { FormField } from '../components/FormField';
import { PageHeader } from '../components/PageHeader';
import { useNotification } from '../services/notificationContext';

export function CreateSetPage() {
  const navigate = useNavigate();
  const { notify } = useNotification();
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [category, setCategory] = useState('');
  const [isPublic, setIsPublic] = useState(true);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const set = (await api.createSet({ title, description, category, isPublic })) as { id: string };
      notify('Flashcard set created', 'success');
      navigate(`/my-sets/${set.id}/edit`);
    } catch (err) {
      const msg = err instanceof ApiError ? err.message : 'Failed to create set';
      setError(msg);
      notify(msg, 'error');
    } finally {
      setLoading(false);
    }
  }

  return (
    <section>
      <PageHeader
        title="Create Flashcard Set"
        subtitle="Set up a new collection — you can add flashcards after creating the set."
      />
      <div className="form-card">
        <form onSubmit={handleSubmit}>
          {error && <div className="form-error-banner" role="alert">{error}</div>}
          <FormField
            label="Title"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="e.g. JavaScript Basics"
            required
            maxLength={100}
          />
          <FormField
            as="textarea"
            label="Description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            placeholder="What will learners study in this set?"
            maxLength={500}
          />
          <FormField
            label="Category"
            value={category}
            onChange={(e) => setCategory(e.target.value)}
            placeholder="e.g. Programming, Language, Science"
            required
            maxLength={50}
          />
          <label className="checkbox-field">
            <input type="checkbox" checked={isPublic} onChange={(e) => setIsPublic(e.target.checked)} />
            <span>Make this set public (visible to guests)</span>
          </label>
          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? 'Creating...' : 'Create Set'}
          </button>
        </form>
      </div>
    </section>
  );
}
