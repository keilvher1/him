import React, { useState, useEffect } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import { articlesAPI } from '../services/api';
import { Article, Page } from '../types';
import { Search, Calendar, User, Eye, ChevronLeft, ChevronRight } from 'lucide-react';

const SearchResults: React.FC = () => {
  const [searchParams] = useSearchParams();
  const query = searchParams.get('q') || '';
  const [articles, setArticles] = useState<Page<Article> | null>(null);
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(0);

  useEffect(() => {
    if (query) {
      searchArticles(0);
    }
  }, [query]);

  const searchArticles = async (page: number) => {
    if (!query) return;
    
    try {
      setLoading(true);
      const response = await articlesAPI.search(query, page, 12);
      setArticles(response.data);
      setCurrentPage(page);
    } catch (error) {
      console.error('Failed to search articles:', error);
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString: string | null | undefined) => {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  const handlePageChange = (page: number) => {
    searchArticles(page);
    window.scrollTo(0, 0);
  };

  if (loading && !articles) {
    return (
      <div className="min-h-screen bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="animate-pulse">
            <div className="h-12 bg-gray-300 rounded mb-8"></div>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {[...Array(12)].map((_, index) => (
                <div key={index} className="bg-gray-300 h-64 rounded-lg"></div>
              ))}
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Header */}
        <div className="mb-8">
          <div className="flex items-center text-gray-600 mb-4">
            <Search size={20} className="mr-2" />
            <h1 className="text-2xl font-bold text-gray-900">
              Search Results
            </h1>
          </div>
          
          <div className="bg-white p-4 rounded-lg shadow-sm border">
            <p className="text-sm text-gray-600">
              Showing results for: <span className="font-semibold text-gray-900">"{query}"</span>
            </p>
            {articles && (
              <p className="text-sm text-gray-500 mt-1">
                {articles.totalElements} article{articles.totalElements !== 1 ? 's' : ''} found
              </p>
            )}
          </div>
        </div>

        {articles && articles.content.length > 0 ? (
          <>
            {/* Results Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8 mb-12">
              {articles.content.map((article) => (
                <article key={article.id} className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-xl transition-shadow duration-300">
                  <Link to={`/articles/${article.id}`} className="block">
                    <div className="aspect-[16/10] overflow-hidden">
                      {article.featuredImage ? (
                        <img
                          src={article.featuredImage}
                          alt={article.title}
                          className="w-full h-full object-cover hover:scale-105 transition-transform duration-300"
                        />
                      ) : (
                        <div className="w-full h-full bg-gradient-to-br from-gray-200 to-gray-300"></div>
                      )}
                    </div>
                    
                    <div className="p-6">
                      <div className="mb-3">
                        <span className="inline-block px-2 py-1 bg-gray-100 text-gray-600 text-xs font-medium rounded-full">
                          {article.categoryName}
                        </span>
                      </div>
                      
                      <h2 className="text-xl font-semibold text-gray-900 mb-3 line-clamp-2 hover:text-blue-600 transition-colors">
                        {article.title}
                      </h2>
                      
                      <p className="text-gray-600 mb-4 line-clamp-3">
                        {article.summary}
                      </p>
                      
                      <div className="flex items-center justify-between text-sm text-gray-500">
                        <div className="flex items-center space-x-4">
                          <div className="flex items-center">
                            <User size={14} className="mr-1" />
                            {article.authorName}
                          </div>
                          <div className="flex items-center">
                            <Eye size={14} className="mr-1" />
                            {article.viewCount}
                          </div>
                        </div>
                        <time className="flex items-center">
                          <Calendar size={14} className="mr-1" />
                          {formatDate(article.publishedAt)}
                        </time>
                      </div>
                    </div>
                  </Link>
                </article>
              ))}
            </div>

            {/* Pagination */}
            {articles.totalPages > 1 && (
              <div className="flex items-center justify-center space-x-2">
                <button
                  onClick={() => handlePageChange(currentPage - 1)}
                  disabled={articles.first || loading}
                  className="flex items-center px-3 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  <ChevronLeft size={16} className="mr-1" />
                  Previous
                </button>
                
                <div className="flex items-center space-x-1">
                  {[...Array(Math.min(5, articles.totalPages))].map((_, index) => {
                    const pageNum = Math.max(0, Math.min(
                      articles.totalPages - 5,
                      Math.max(0, currentPage - 2)
                    )) + index;
                    
                    return (
                      <button
                        key={pageNum}
                        onClick={() => handlePageChange(pageNum)}
                        disabled={loading}
                        className={`px-3 py-2 text-sm font-medium rounded-md ${
                          pageNum === currentPage
                            ? 'text-white bg-blue-600'
                            : 'text-gray-700 bg-white border border-gray-300 hover:bg-gray-50'
                        } disabled:opacity-50 disabled:cursor-not-allowed`}
                      >
                        {pageNum + 1}
                      </button>
                    );
                  })}
                </div>
                
                <button
                  onClick={() => handlePageChange(currentPage + 1)}
                  disabled={articles.last || loading}
                  className="flex items-center px-3 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  Next
                  <ChevronRight size={16} className="ml-1" />
                </button>
              </div>
            )}
          </>
        ) : (
          <div className="text-center py-16">
            <Search size={48} className="mx-auto text-gray-400 mb-4" />
            <h3 className="text-xl font-medium text-gray-900 mb-2">No results found</h3>
            <p className="text-gray-600 mb-8">
              Try adjusting your search terms or browse our categories.
            </p>
            <Link 
              to="/" 
              className="inline-flex items-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              Back to Home
            </Link>
          </div>
        )}
      </div>
    </div>
  );
};

export default SearchResults;