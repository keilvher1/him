package com.handong.internationalmedia.controller;

import com.handong.internationalmedia.dto.ArticleDto;
import com.handong.internationalmedia.entity.Article;
import com.handong.internationalmedia.entity.Category;
import com.handong.internationalmedia.entity.User;
import com.handong.internationalmedia.service.ArticleService;
import com.handong.internationalmedia.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ArticleRestController {

    private final ArticleService articleService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Page<ArticleDto>> getAllArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Article> articles = articleService.getAllPublishedArticles(pageable);
        Page<ArticleDto> articleDtos = articles.map(this::convertToDto);
        return ResponseEntity.ok(articleDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleDto> getArticleById(@PathVariable Long id) {
        return articleService.getArticleById(id)
                .map(article -> {
                    articleService.incrementViewCount(id);
                    return ResponseEntity.ok(convertToDto(article));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{categoryName}")
    public ResponseEntity<Page<ArticleDto>> getArticlesByCategory(
            @PathVariable String categoryName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Article> articles = articleService.getArticlesByCategory(categoryName, pageable);
        Page<ArticleDto> articleDtos = articles.map(this::convertToDto);
        return ResponseEntity.ok(articleDtos);
    }

    @GetMapping("/featured")
    public ResponseEntity<List<ArticleDto>> getFeaturedArticles() {
        List<Article> articles = articleService.getFeaturedArticles();
        List<ArticleDto> articleDtos = articles.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(articleDtos);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ArticleDto>> searchArticles(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Article> articles = articleService.searchArticles(keyword, pageable);
        Page<ArticleDto> articleDtos = articles.map(this::convertToDto);
        return ResponseEntity.ok(articleDtos);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createArticle(
            @RequestPart("article") ArticleDto articleDto,
            @RequestPart(value = "image", required = false) MultipartFile image,
            Authentication authentication) {
        try {
            log.info("Creating article with title: {}", articleDto.getTitle());
            log.info("Authentication principal type: {}", authentication.getPrincipal().getClass());
            
            // Get user by username from UserDetails
            String username;
            if (authentication.getPrincipal() instanceof UserDetails) {
                username = ((UserDetails) authentication.getPrincipal()).getUsername();
            } else {
                username = authentication.getName();
            }
            
            User author = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));
            
            log.info("Found author: {}", author.getUsername());
            
            Article article = convertToEntity(articleDto);
            article.setPublishedAt(LocalDateTime.now());
            
            Article savedArticle = articleService.saveArticleWithImage(article, image, author);
            log.info("Article created successfully with ID: {}", savedArticle.getId());
            return ResponseEntity.ok(convertToDto(savedArticle));
        } catch (Exception e) {
            log.error("Error creating article: ", e);
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateArticle(
            @PathVariable Long id,
            @RequestPart("article") ArticleDto articleDto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            Article updatedArticle = convertToEntity(articleDto);
            Article savedArticle = articleService.updateArticleWithImage(id, updatedArticle, image);
            return ResponseEntity.ok(convertToDto(savedArticle));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteArticle(@PathVariable Long id) {
        try {
            articleService.deleteArticle(id);
            return ResponseEntity.ok(new MessageResponse("Article deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private ArticleDto convertToDto(Article article) {
        return ArticleDto.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(article.getContent())
                .summary(article.getSummary())
                .authorName(article.getAuthor() != null ? article.getAuthor().getFullName() : "Unknown")
                .categoryName(article.getCategory() != null ? article.getCategory().getName() : null)
                .featuredImage(article.getFeaturedImage())
                .viewCount(article.getViewCount())
                .isFeatured(article.getIsFeatured())
                .isPublished(article.getIsPublished())
                .publishedAt(article.getPublishedAt() != null ? article.getPublishedAt().toString() : null)
                .createdAt(article.getCreatedAt() != null ? article.getCreatedAt().toString() : null)
                .updatedAt(article.getUpdatedAt() != null ? article.getUpdatedAt().toString() : null)
                .build();
    }

    private Article convertToEntity(ArticleDto dto) {
        Category category = null;
        if (dto.getCategoryName() != null) {
            category = articleService.getCategoryByName(dto.getCategoryName())
                    .orElseThrow(() -> new RuntimeException("Category not found: " + dto.getCategoryName()));
        }

        return Article.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .summary(dto.getSummary())
                .category(category)
                .isFeatured(dto.getIsFeatured() != null ? dto.getIsFeatured() : false)
                .isPublished(dto.getIsPublished() != null ? dto.getIsPublished() : true)
                .build();
    }

    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }

    public static class MessageResponse {
        private String message;

        public MessageResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}