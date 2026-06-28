import { Outlet } from 'react-router-dom';
import { Navbar } from './Navbar';

export function Layout() {
  return (
    <div className="app-shell">
      <Navbar />
      <main className="main-content">
        <div className="container">
          <Outlet />
        </div>
      </main>
      <footer className="site-footer">
        <div className="container">
          <p>LearningFlashcardApp — study smarter, one card at a time.</p>
        </div>
      </footer>
    </div>
  );
}
