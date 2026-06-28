import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api, ApiError } from '../services/api';
import { UserStatistics } from '../models/types';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { EmptyState } from '../components/EmptyState';
import { PageHeader } from '../components/PageHeader';
import { StatCard } from '../components/StatCard';
import { useAuth } from '../services/authContext';
import { useNotification } from '../services/notificationContext';

export function StatisticsPage() {
  const { user } = useAuth();
  const { notify } = useNotification();
  const [stats, setStats] = useState<UserStatistics | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (user) loadStats(user.id);
  }, [user]);

  async function loadStats(userId: string) {
    setLoading(true);
    try {
      const data = (await api.getStatistics(userId)) as UserStatistics;
      setStats(data);
    } catch (err) {
      notify(err instanceof ApiError ? err.message : 'Failed to load statistics', 'error');
    } finally {
      setLoading(false);
    }
  }

  if (loading) return <LoadingSpinner />;
  if (!stats) return <p>Statistics unavailable.</p>;

  return (
    <section>
      <PageHeader
        title="Learning Statistics"
        subtitle="Track your study progress and review session history."
      />

      <div className="dashboard-stats">
        <StatCard label="Cards Studied" value={stats.totalCardsStudied} variant="primary" />
        <StatCard label="Correct" value={stats.correctAnswers} variant="success" />
        <StatCard label="Incorrect" value={stats.incorrectAnswers} variant="default" />
        <StatCard label="Success Rate" value={`${stats.successPercentage}%`} variant="warning" />
      </div>

      <h2 className="section-title">Progress History</h2>
      {stats.progressHistory.length === 0 ? (
        <EmptyState
          icon="📊"
          title="No sessions yet"
          message="Complete a learning session to see your progress history here."
          action={
            <Link to="/sets" className="btn btn-primary">
              Browse Sets to Study
            </Link>
          }
        />
      ) : (
        <div className="table-wrapper">
          <table className="data-table">
            <thead>
              <tr>
                <th>Set</th>
                <th>Date</th>
                <th>Studied</th>
                <th>Correct</th>
                <th>Incorrect</th>
                <th>Success</th>
              </tr>
            </thead>
            <tbody>
              {stats.progressHistory.map((entry) => (
                <tr key={entry.sessionId}>
                  <td><strong>{entry.flashcardSetTitle}</strong></td>
                  <td>{new Date(entry.studiedAt).toLocaleString()}</td>
                  <td>{entry.cardsStudied}</td>
                  <td>{entry.correctAnswers}</td>
                  <td>{entry.incorrectAnswers}</td>
                  <td>{entry.successPercentage}%</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}
