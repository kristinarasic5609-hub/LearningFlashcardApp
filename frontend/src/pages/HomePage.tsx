import { Link } from 'react-router-dom';
import { useAuth } from '../services/authContext';

export function HomePage() {
  const { user } = useAuth();

  return (
    <section className="hero">
      <span className="hero-badge">Modern flashcard learning</span>
      <h1>Learn smarter with flashcards</h1>
      <p>
        Browse public flashcard sets, create your own collections, and track your learning progress — all in one place.
      </p>
      <div className="hero-actions">
        <Link to="/sets" className="btn btn-primary btn-lg">
          Browse Public Sets
        </Link>
        {user ? (
          <Link to="/dashboard" className="btn btn-secondary btn-lg">
            Go to Dashboard
          </Link>
        ) : (
          <Link to="/register" className="btn btn-secondary btn-lg">
            Get Started Free
          </Link>
        )}
      </div>
      <div className="hero-features">
        <div className="hero-feature">
          <strong>📖 Browse & search</strong>
          <span>Explore public sets from the community.</span>
        </div>
        <div className="hero-feature">
          <strong>✏️ Create & manage</strong>
          <span>Build your own flashcard collections.</span>
        </div>
        <div className="hero-feature">
          <strong>📊 Track progress</strong>
          <span>See your learning stats and history.</span>
        </div>
      </div>
    </section>
  );
}
