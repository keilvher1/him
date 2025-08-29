import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { articlesAPI } from '../services/api';
import { Article } from '../types';
import { Calendar, Eye, User, ChevronRight } from 'lucide-react';

const HomePage: React.FC = () => {
  const [featuredArticles, setFeaturedArticles] = useState<Article[]>([]);
  const [recentArticles, setRecentArticles] = useState<Article[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [featuredResponse, recentResponse] = await Promise.all([
        articlesAPI.getFeatured(),
        articlesAPI.getAll(0, 12)
      ]);
      
      setFeaturedArticles(featuredResponse.data);
      setRecentArticles(recentResponse.data.content);
    } catch (error) {
      console.error('Failed to load articles:', error);
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


  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="animate-pulse">
            <div className="h-96 bg-gray-300 rounded-lg mb-8"></div>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {[...Array(9)].map((_, index) => (
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
      {/* Hero Section */}
      {featuredArticles.length > 0 && (
        <section className="bg-white">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
              {/* Main Featured Article */}
              <div className="lg:col-span-2">
                <Link to={`/articles/${featuredArticles[0].id}`} className="group">
                  <div className="relative overflow-hidden rounded-lg bg-gray-900 aspect-[16/9] lg:aspect-[21/9]">
                    {featuredArticles[0].featuredImage ? (
                      <img
                        src={featuredArticles[0].featuredImage}
                        alt={featuredArticles[0].title}
                        className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                      />
                    ) : (
                      <div className="w-full h-full bg-gradient-to-br from-gray-800 to-gray-900"></div>
                    )}
                    <div className="absolute inset-0 bg-gradient-to-t from-black/70 via-black/20 to-transparent"></div>
                    <div className="absolute bottom-0 left-0 right-0 p-6 lg:p-8">
                      <div className="mb-3">
                        <span className="inline-block px-3 py-1 bg-blue-600 text-white text-sm font-medium rounded-full">
                          {featuredArticles[0].categoryName}
                        </span>
                      </div>
                      <h1 className="text-2xl lg:text-4xl font-bold text-white mb-3 group-hover:text-blue-200 transition-colors">
                        {featuredArticles[0].title}
                      </h1>
                      <p className="text-gray-200 text-lg mb-4 line-clamp-2">
                        {featuredArticles[0].summary}
                      </p>
                      <div className="flex items-center text-gray-300 text-sm space-x-4">
                        <div className="flex items-center">
                          <User size={16} className="mr-1" />
                          {featuredArticles[0].authorName}
                        </div>
                        <div className="flex items-center">
                          <Calendar size={16} className="mr-1" />
                          {formatDate(featuredArticles[0].publishedAt)}
                        </div>
                        <div className="flex items-center">
                          <Eye size={16} className="mr-1" />
                          {featuredArticles[0].viewCount}
                        </div>
                      </div>
                    </div>
                  </div>
                </Link>
              </div>
            </div>
          </div>
        </section>
      )}

      {/* Recent Articles */}
      <section className="py-12">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between mb-8">
            <h2 className="text-3xl font-bold text-gray-900">Latest News</h2>
            <Link 
              to="/articles" 
              className="flex items-center text-blue-600 hover:text-blue-800 transition-colors font-medium"
            >
              View All
              <ChevronRight size={20} className="ml-1" />
            </Link>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            {recentArticles.map((article) => (
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
                    <h3 className="text-xl font-semibold text-gray-900 mb-3 line-clamp-2 hover:text-blue-600 transition-colors">
                      {article.title}
                    </h3>
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
        </div>
      </section>

      {/* Newsletter Section */}
      <section className="bg-gray-900 text-white py-16">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <h2 className="text-3xl font-bold mb-4">Stay Informed</h2>
          <p className="text-xl text-gray-300 mb-8">
            Get the latest news and updates from Handong International Media
          </p>
          <div className="max-w-md mx-auto">
            <div className="flex flex-col sm:flex-row gap-4">
              <input
                type="email"
                placeholder="Enter your email"
                className="flex-1 px-4 py-3 rounded-lg bg-white text-gray-900 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
              <button className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium">
                Subscribe
              </button>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
};

export default HomePage;