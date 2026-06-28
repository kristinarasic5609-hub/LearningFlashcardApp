export function LoadingSpinner({ label = 'Loading...' }: { label?: string }) {
  return (
    <div className="loading" role="status" aria-live="polite">
      <div className="spinner" aria-hidden="true" />
      <p>{label}</p>
    </div>
  );
}
