package com.handong.internationalmedia.controller;

import com.handong.internationalmedia.entity.Article;
import com.handong.internationalmedia.entity.Category;
import com.handong.internationalmedia.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class HomeController {
    
    private final ArticleService articleService;
    
    @GetMapping("/")
    public String home(Model model) {
        // Get featured articles for hero section
        List<Article> featuredArticles = articleService.getFeaturedArticles();
        
        // Get latest articles for each category (5 per category)
        List<Category> categories = articleService.getAllActiveCategories();
        
        // Get latest 10 articles for "You will also like" section
        List<Article> latestArticles = articleService.getLatestArticles(10);
        
        model.addAttribute("featuredArticles", featuredArticles);
        model.addAttribute("categories", categories);
        model.addAttribute("latestArticles", latestArticles);
        
        return "index";
    }
    
    @GetMapping("/articles")
    public String articles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Article> articles = articleService.getAllPublishedArticles(pageable);
        List<Category> categories = articleService.getAllActiveCategories();
        
        model.addAttribute("articles", articles);
        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", articles.getTotalPages());
        
        return "articles/list";
    }
    
    @GetMapping("/articles/{id}")
    public String articleDetail(@PathVariable Long id, Model model) {
        Optional<Article> articleOpt = articleService.getArticleById(id);
        if (articleOpt.isEmpty()) {
            return "redirect:/";
        }
        
        Article article = articleOpt.get();
        
        // Increment view count
        articleService.incrementViewCount(id);
        
        // Get related articles from same category
        List<Article> relatedArticles = articleService.getLatestArticles(5);
        
        List<Category> categories = articleService.getAllActiveCategories();
        
        model.addAttribute("article", article);
        model.addAttribute("relatedArticles", relatedArticles);
        model.addAttribute("categories", categories);
        
        return "articles/detail";
    }
    
    @GetMapping("/category/{categoryName}")
    public String categoryArticles(
            @PathVariable String categoryName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {
        
        Optional<Category> categoryOpt = articleService.getCategoryByName(categoryName);
        if (categoryOpt.isEmpty()) {
            return "redirect:/";
        }
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Article> articles = articleService.getArticlesByCategory(categoryName, pageable);
        List<Category> categories = articleService.getAllActiveCategories();
        
        model.addAttribute("articles", articles);
        model.addAttribute("categories", categories);
        model.addAttribute("currentCategory", categoryOpt.get());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", articles.getTotalPages());
        
        return "articles/category";
    }
    
    @GetMapping("/search")
    public String search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Article> articles = articleService.searchArticles(q, pageable);
        List<Category> categories = articleService.getAllActiveCategories();
        
        model.addAttribute("articles", articles);
        model.addAttribute("categories", categories);
        model.addAttribute("searchQuery", q);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", articles.getTotalPages());
        
        return "articles/search";
    }
    
}