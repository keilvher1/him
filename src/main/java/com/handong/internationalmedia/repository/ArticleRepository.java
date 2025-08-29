package com.handong.internationalmedia.repository;

import com.handong.internationalmedia.entity.Article;
import com.handong.internationalmedia.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    
    Page<Article> findByIsPublishedTrueOrderByPublishedAtDesc(Pageable pageable);
    
    Page<Article> findByCategoryAndIsPublishedTrueOrderByPublishedAtDesc(Category category, Pageable pageable);
    
    List<Article> findByIsFeaturedTrueAndIsPublishedTrueOrderByPublishedAtDesc();
    
    @Query("SELECT a FROM Article a WHERE a.isPublished = true AND " +
           "(LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.summary) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Article> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT a FROM Article a WHERE LOWER(a.category.name) = LOWER(:categoryName) AND a.isPublished = true ORDER BY a.publishedAt DESC")
    Page<Article> findByCategoryNameAndIsPublishedTrue(@Param("categoryName") String categoryName, Pageable pageable);
    
    List<Article> findTop5ByCategoryAndIsPublishedTrueOrderByPublishedAtDesc(Category category);
    
    List<Article> findTop10ByIsPublishedTrueOrderByViewCountDesc();
    
    @Query("SELECT a FROM Article a WHERE a.isPublished = true ORDER BY a.publishedAt DESC")
    List<Article> findLatestArticles(Pageable pageable);
}