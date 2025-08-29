import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';

// Components
import Header from './components/Header';
import HomePage from './components/HomePage';
import LoginPage from './components/LoginPage';
import ArticleEditor from './components/ArticleEditor';
import ArticleDetail from './components/ArticleDetail';
import CategoryPage from './components/CategoryPage';
import AdminArticles from './components/AdminArticles';
import SearchResults from './components/SearchResults';
import ProtectedRoute from './components/ProtectedRoute';
import Footer from './components/Footer';

// Global CSS
import './App.css';

const App: React.FC = () => {
  return (
    <AuthProvider>
      <Router>
        <div className="min-h-screen flex flex-col">
          <Header />
          <main className="flex-1">
            <Routes>
              <Route path="/" element={<HomePage />} />
              <Route path="/login" element={<LoginPage />} />
              <Route path="/articles/:id" element={<ArticleDetail />} />
              <Route path="/category/:categoryName" element={<CategoryPage />} />
              <Route path="/search" element={<SearchResults />} />
              
              {/* Admin Routes */}
              <Route path="/admin/articles" element={
                <ProtectedRoute requireAdmin>
                  <AdminArticles />
                </ProtectedRoute>
              } />
              <Route path="/admin/articles/new" element={
                <ProtectedRoute requireAdmin>
                  <ArticleEditor />
                </ProtectedRoute>
              } />
              <Route path="/admin/articles/:id/edit" element={
                <ProtectedRoute requireAdmin>
                  <ArticleEditor />
                </ProtectedRoute>
              } />
            </Routes>
          </main>
          <Footer />
        </div>
      </Router>
    </AuthProvider>
  );
};

export default App;
