interface StatCardProps {
  label: string;
  value: string | number;
  hint?: string;
  variant?: 'default' | 'primary' | 'success' | 'warning';
}

export function StatCard({ label, value, hint, variant = 'default' }: StatCardProps) {
  return (
    <article className={`stat-card stat-card-${variant}`}>
      <p className="stat-label">{label}</p>
      <p className="stat-value">{value}</p>
      {hint && <p className="stat-hint">{hint}</p>}
    </article>
  );
}
