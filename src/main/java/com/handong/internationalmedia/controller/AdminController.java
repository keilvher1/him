package com.handong.internationalmedia.controller;

import com.handong.internationalmedia.entity.Article;
import com.handong.internationalmedia.entity.Category;
import com.handong.internationalmedia.entity.User;
import com.handong.internationalmedia.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final ArticleService articleService;
    
    @GetMapping("/articles/new")
    public String showCreateForm(Model model) {
        model.addAttribute("article", new Article());
        model.addAttribute("categories", articleService.getAllActiveCategories());
        return "admin/article-form";
    }
    
    @PostMapping("/articles")
    public String createArticle(@ModelAttribute Article article,
                              @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        try {
            User author = (User) authentication.getPrincipal();
            article.setPublishedAt(LocalDateTime.now());
            article.setViewCount(0L);
            
            articleService.saveArticleWithImage(article, imageFile, author);
            
            redirectAttributes.addFlashAttribute("successMessage", "Article created successfully!");
            return "redirect:/articles/" + article.getId();
            
        } catch (Exception e) {
            log.error("Error creating article", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create article: " + e.getMessage());
            return "redirect:/admin/articles/new";
        }
    }
    
    @GetMapping("/articles/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Article article = articleService.getArticleById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));
        
        model.addAttribute("article", article);
        model.addAttribute("categories", articleService.getAllActiveCategories());
        return "admin/article-form";
    }
    
    @PostMapping("/articles/{id}")
    public String updateArticle(@PathVariable Long id,
                              @ModelAttribute Article article,
                              @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                              RedirectAttributes redirectAttributes) {
        try {
            articleService.updateArticleWithImage(id, article, imageFile);
            
            redirectAttributes.addFlashAttribute("successMessage", "Article updated successfully!");
            return "redirect:/articles/" + id;
            
        } catch (Exception e) {
            log.error("Error updating article", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update article: " + e.getMessage());
            return "redirect:/admin/articles/" + id + "/edit";
        }
    }
    
    @PostMapping("/articles/{id}/delete")
    public String deleteArticle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            articleService.deleteArticle(id);
            redirectAttributes.addFlashAttribute("successMessage", "Article deleted successfully!");
            return "redirect:/articles";
            
        } catch (Exception e) {
            log.error("Error deleting article", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete article: " + e.getMessage());
            return "redirect:/articles/" + id;
        }
    }
}