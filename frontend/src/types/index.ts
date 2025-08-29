export interface User {
  username: string;
  fullName: string;
  role: string;
  isAdmin: boolean;
}

export interface Article {
  id: number;
  title: string;
  content: string;
  summary?: string;
  authorName?: string;
  categoryName?: string;
  featuredImage?: string;
  viewCount?: number;
  isFeatured?: boolean;
  isPublished?: boolean;
  publishedAt?: string | null;
  createdAt?: string | null;
  updatedAt?: string | null;
}

export interface Category {
  id: number;
  name: string;
  description: string;
  displayOrder: number;
  isActive: boolean;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  email: string;
  fullName: string;
}

export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  message?: string;
  error?: string;
}

export interface Page<T> {
  content: T[];
  pageable: {
    sort: {
      sorted: boolean;
      empty: boolean;
      unsorted: boolean;
    };
    offset: number;
    pageSize: number;
    pageNumber: number;
    paged: boolean;
    unpaged: boolean;
  };
  last: boolean;
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  sort: {
    sorted: boolean;
    empty: boolean;
    unsorted: boolean;
  };
  first: boolean;
  numberOfElements: number;
  empty: boolean;
}