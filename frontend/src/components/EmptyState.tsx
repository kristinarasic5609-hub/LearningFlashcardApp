import { ReactNode } from 'react';

interface EmptyStateProps {
  title: string;
  message: string;
  icon?: string;
  action?: ReactNode;
}

export function EmptyState({ title, message, icon = '📭', action }: EmptyStateProps) {
  return (
    <div className="empty-state">
      <span className="empty-state-icon" aria-hidden="true">{icon}</span>
      <h3>{title}</h3>
      <p>{message}</p>
      {action && <div className="empty-state-action">{action}</div>}
    </div>
  );
}
