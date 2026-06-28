import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api, ApiError } from '../services/api';
import { UserStatistics } from '../models/types';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { PageHeader } from '../components/PageHeader';
import { StatCard } from '../components/StatCard';
import { useAuth } from '../services/authContext';
import { useNotification } from '../services/notificationContext';

export function DashboardPage() {
  const { user } = useAuth();
  const { notify } = useNotification();
  const [totalSets, setTotalSets] = useState(0);
  const [stats, setStats] = useState<UserStatistics | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (user) loadDashboard(user.id);
  }, [user]);

  async function loadDashboard(userId: string) {
    setLoading(true);
    try {
      const [sets, statistics] = await Promise.all([
        api.getMySets(),
        api.getStatistics(userId),
      ]);
      setTotalSets((sets as unknown[]).length);
      setStats(statistics as UserStatistics);
    } catch (err) {
      notify(err instanceof ApiError ? err.message : 'Failed to load dashboard', 'error');
    } finally {
      setLoading(false);
    }
  }

  if (loading) return <LoadingSpinner label="Loading dashboard..." />;

  const cardsLearned = stats?.totalCardsStudied ?? 0;
  const progress = stats?.successPercentage ?? 0;

  return (
    <section>
      <PageHeader
        title={`Welcome back, ${user?.username}`}
        subtitle="Here's an overview of your learning activity. Pick up where you left off or start something new."
      />

      <div className="dashboard-stats">
        <StatCard
          label="Total Sets"
          value={totalSets}
          hint={totalSets === 0 ? 'Create your first set to get started' : 'Flashcard sets you own'}
          variant="primary"
        />
        <StatCard
          label="Cards Learned"
          value={cardsLearned}
          hint={cardsLearned === 0 ? 'Complete a session to track progress' : 'Total cards studied'}
          variant="success"
        />
        <StatCard
          label="Progress"
          value={`${progress}%`}
          hint={cardsLearned === 0 ? 'No sessions completed yet' : 'Overall success rate'}
          variant="warning"
        />
      </div>

      <h2 className="section-title">Quick actions</h2>
      <div className="dashboard-actions">
        <article className="action-card">
          <div className="action-card-icon">📚</div>
          <h3>My Flashcard Sets</h3>
          <p>Create, edit, and organize your personal collections.</p>
          <Link to="/my-sets" className="btn btn-primary">
            {totalSets === 0 ? 'Create Your First Set' : 'View My Sets'}
          </Link>
        </article>
        <article className="action-card">
          <div className="action-card-icon">🎯</div>
          <h3>Start Learning</h3>
          <p>Browse public sets or study your own collections.</p>
          <Link to="/sets" className="btn btn-secondary">
            Browse Sets
          </Link>
        </article>
        <article className="action-card">
          <div className="action-card-icon">📈</div>
          <h3>Statistics</h3>
          <p>Review your study history and success rate.</p>
          <Link to="/statistics" className="btn btn-secondary">
            View Statistics
          </Link>
        </article>
      </div>
    </section>
  );
}
