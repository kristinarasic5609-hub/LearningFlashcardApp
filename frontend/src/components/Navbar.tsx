import { useState } from 'react';
import { Link, NavLink } from 'react-router-dom';
import { useAuth } from '../services/authContext';

function NavItem({
  to,
  children,
  onNavigate,
  end,
}: {
  to: string;
  children: React.ReactNode;
  onNavigate: () => void;
  end?: boolean;
}) {
  return (
    <NavLink
      to={to}
      end={end}
      className={({ isActive }) => `nav-link${isActive ? ' active' : ''}`}
      onClick={onNavigate}
    >
      {children}
    </NavLink>
  );
}

export function Navbar() {
  const { user, logout, isAdmin } = useAuth();
  const [mobileOpen, setMobileOpen] = useState(false);
  const closeMobile = () => setMobileOpen(false);

  const guestLinks = (
    <>
      <NavItem to="/" onNavigate={closeMobile} end>Home</NavItem>
      <NavItem to="/sets" onNavigate={closeMobile}>Browse Sets</NavItem>
    </>
  );

  const userLinks = (
    <>
      <NavItem to="/dashboard" onNavigate={closeMobile}>Dashboard</NavItem>
      <NavItem to="/my-sets" onNavigate={closeMobile}>My Sets</NavItem>
      <NavItem to="/statistics" onNavigate={closeMobile}>Statistics</NavItem>
    </>
  );

  const adminLinks = (
    <div className="nav-group nav-group-admin">
      <span className="nav-group-label">Admin</span>
      <NavItem to="/admin/users" onNavigate={closeMobile}>Users</NavItem>
      <NavItem to="/admin/sets" onNavigate={closeMobile}>Sets</NavItem>
    </div>
  );

  return (
    <header className="navbar">
      <div className="navbar-inner">
        <Link to="/" className="brand" onClick={closeMobile}>
          <span className="brand-icon" aria-hidden="true">⚡</span>
          <span className="brand-text">
            <strong>FlashLearn</strong>
            <small>Learning Platform</small>
          </span>
        </Link>

        <button
          type="button"
          className="nav-toggle"
          aria-label="Toggle navigation"
          aria-expanded={mobileOpen}
          onClick={() => setMobileOpen(!mobileOpen)}
        >
          <span />
          <span />
          <span />
        </button>

        <nav className={`navbar-nav ${mobileOpen ? 'open' : ''}`}>
          <div className="nav-section">
            {!user && guestLinks}
            {user && userLinks}
            {user && isAdmin && adminLinks}
          </div>

          <div className="nav-section nav-section-account">
            {user ? (
              <>
                <div className="nav-user-chip">
                  <span className="nav-avatar" aria-hidden="true">
                    {user.username.charAt(0).toUpperCase()}
                  </span>
                  <div>
                    <span className="nav-user-name">{user.username}</span>
                    <span className="nav-user-role">{user.role}</span>
                  </div>
                </div>
                <button type="button" className="btn btn-ghost btn-sm" onClick={logout}>
                  Logout
                </button>
              </>
            ) : (
              <>
                <NavItem to="/login" onNavigate={closeMobile}>Login</NavItem>
                <Link to="/register" className="btn btn-primary btn-sm" onClick={closeMobile}>
                  Sign up
                </Link>
              </>
            )}
          </div>
        </nav>
      </div>
    </header>
  );
}
