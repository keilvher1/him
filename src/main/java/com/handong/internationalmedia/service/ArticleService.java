package com.handong.internationalmedia.service;

import com.handong.internationalmedia.entity.Article;
import com.handong.internationalmedia.entity.Category;
import com.handong.internationalmedia.entity.User;
import com.handong.internationalmedia.repository.ArticleRepository;
import com.handong.internationalmedia.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleService {
    
    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final CloudinaryService cloudinaryService;
    
    public Page<Article> getAllPublishedArticles(Pageable pageable) {
        return articleRepository.findByIsPublishedTrueOrderByPublishedAtDesc(pageable);
    }
    
    public Page<Article> getArticlesByCategory(String categoryName, Pageable pageable) {
        return articleRepository.findByCategoryNameAndIsPublishedTrue(categoryName, pageable);
    }
    
    public Optional<Article> getArticleById(Long id) {
        return articleRepository.findById(id);
    }
    
    public List<Article> getFeaturedArticles() {
        return articleRepository.findByIsFeaturedTrueAndIsPublishedTrueOrderByPublishedAtDesc();
    }
    
    public Page<Article> searchArticles(String keyword, Pageable pageable) {
        return articleRepository.searchByKeyword(keyword, pageable);
    }
    
    public List<Article> getLatestArticles(int limit) {
        return articleRepository.findLatestArticles(Pageable.ofSize(limit));
    }
    
    public List<Article> getPopularArticles() {
        return articleRepository.findTop10ByIsPublishedTrueOrderByViewCountDesc();
    }
    
    @Transactional
    public Article saveArticle(Article article, User author) {
        article.setAuthor(author);
        return articleRepository.save(article);
    }
    
    @Transactional
    public Article saveArticleWithImage(Article article, MultipartFile image, User author) {
        article.setAuthor(author);
        if (image != null && !image.isEmpty()) {
            String imageUrl = cloudinaryService.uploadImage(image);
            article.setFeaturedImage(imageUrl);
        }
        return articleRepository.save(article);
    }
    
    @Transactional
    public Article updateArticleWithImage(Long id, Article updatedArticle, MultipartFile image) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));
        
        article.setTitle(updatedArticle.getTitle());
        article.setContent(updatedArticle.getContent());
        article.setSummary(updatedArticle.getSummary());
        article.setCategory(updatedArticle.getCategory());
        article.setIsPublished(updatedArticle.getIsPublished());
        article.setIsFeatured(updatedArticle.getIsFeatured());
        
        if (image != null && !image.isEmpty()) {
            if (article.getFeaturedImage() != null) {
                cloudinaryService.deleteImage(article.getFeaturedImage());
            }
            String imageUrl = cloudinaryService.uploadImage(image);
            article.setFeaturedImage(imageUrl);
        }
        
        return articleRepository.save(article);
    }
    
    @Transactional
    public void incrementViewCount(Long articleId) {
        Optional<Article> articleOpt = articleRepository.findById(articleId);
        if (articleOpt.isPresent()) {
            Article article = articleOpt.get();
            Long currentViewCount = article.getViewCount();
            article.setViewCount((currentViewCount != null ? currentViewCount : 0L) + 1);
            articleRepository.save(article);
        }
    }
    
    @Transactional
    public void deleteArticle(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));
        
        if (article.getFeaturedImage() != null) {
            cloudinaryService.deleteImage(article.getFeaturedImage());
        }
        
        articleRepository.deleteById(id);
    }
    
    public List<Category> getAllActiveCategories() {
        return categoryRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
    }
    
    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }
}