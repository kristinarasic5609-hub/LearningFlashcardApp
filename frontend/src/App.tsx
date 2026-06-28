import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './services/authContext';
import { NotificationProvider } from './services/notificationContext';
import { Layout } from './components/Layout';
import { ProtectedRoute } from './components/ProtectedRoute';
import { HomePage } from './pages/HomePage';
import { SetsListPage } from './pages/SetsListPage';
import { SetDetailsPage } from './pages/SetDetailsPage';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
import { DashboardPage } from './pages/DashboardPage';
import { MySetsPage } from './pages/MySetsPage';
import { CreateSetPage } from './pages/CreateSetPage';
import { EditSetPage } from './pages/EditSetPage';
import { LearningSessionPage } from './pages/LearningSessionPage';
import { StatisticsPage } from './pages/StatisticsPage';
import { AdminUsersPage } from './pages/AdminUsersPage';
import { AdminSetsPage } from './pages/AdminSetsPage';

export default function App() {
  return (
    <AuthProvider>
      <NotificationProvider>
        <BrowserRouter>
          <Routes>
            <Route element={<Layout />}>
              <Route path="/" element={<HomePage />} />
              <Route path="/sets" element={<SetsListPage />} />
              <Route path="/sets/:id" element={<SetDetailsPage />} />
              <Route path="/login" element={<LoginPage />} />
              <Route path="/register" element={<RegisterPage />} />
              <Route
                path="/dashboard"
                element={
                  <ProtectedRoute>
                    <DashboardPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/my-sets"
                element={
                  <ProtectedRoute>
                    <MySetsPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/my-sets/new"
                element={
                  <ProtectedRoute>
                    <CreateSetPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/my-sets/:id/edit"
                element={
                  <ProtectedRoute>
                    <EditSetPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/learn/:setId"
                element={
                  <ProtectedRoute>
                    <LearningSessionPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/statistics"
                element={
                  <ProtectedRoute>
                    <StatisticsPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/users"
                element={
                  <ProtectedRoute adminOnly>
                    <AdminUsersPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/sets"
                element={
                  <ProtectedRoute adminOnly>
                    <AdminSetsPage />
                  </ProtectedRoute>
                }
              />
            </Route>
          </Routes>
        </BrowserRouter>
      </NotificationProvider>
    </AuthProvider>
  );
}
