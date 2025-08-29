import React from 'react';
import { Link } from 'react-router-dom';

const Footer: React.FC = () => {
  return (
    <footer className="bg-gray-900 text-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
          <div className="col-span-1 md:col-span-2">
            <Link to="/" className="flex items-center space-x-2 mb-4">
              <div className="text-xl font-bold text-white">HANDONG</div>
              <div className="text-sm font-light text-gray-300 leading-tight">
                INTERNATIONAL<br/>MEDIA
              </div>
            </Link>
            <p className="text-gray-400 text-sm max-w-md">
              Handong International Media is a premier source for news and insights, 
              providing comprehensive coverage of global events, politics, business, and culture.
            </p>
          </div>
          
          <div>
            <h3 className="text-lg font-semibold mb-4">Quick Links</h3>
            <ul className="space-y-2">
              <li><Link to="/" className="text-gray-400 hover:text-white transition-colors">Home</Link></li>
              <li><Link to="/category/politics" className="text-gray-400 hover:text-white transition-colors">Politics</Link></li>
              <li><Link to="/category/business" className="text-gray-400 hover:text-white transition-colors">Business</Link></li>
              <li><Link to="/category/world" className="text-gray-400 hover:text-white transition-colors">World</Link></li>
            </ul>
          </div>
          
          <div>
            <h3 className="text-lg font-semibold mb-4">Contact</h3>
            <ul className="space-y-2 text-gray-400 text-sm">
              <li>Handong Global University</li>
              <li>558 Handong-ro, Pohang</li>
              <li>Gyeongbuk, South Korea</li>
              <li>contact@handong-media.com</li>
            </ul>
          </div>
        </div>
        
        <div className="border-t border-gray-800 mt-8 pt-8 text-center">
          <p className="text-gray-400 text-sm">
            © {new Date().getFullYear()} Handong International Media. All rights reserved.
          </p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;