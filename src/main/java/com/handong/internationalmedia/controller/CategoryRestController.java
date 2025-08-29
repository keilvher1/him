package com.handong.internationalmedia.controller;

import com.handong.internationalmedia.dto.CategoryDto;
import com.handong.internationalmedia.entity.Category;
import com.handong.internationalmedia.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class CategoryRestController {

    private final ArticleService articleService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<Category> categories = articleService.getAllActiveCategories();
        List<CategoryDto> categoryDtos = categories.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categoryDtos);
    }

    @GetMapping("/{name}")
    public ResponseEntity<CategoryDto> getCategoryByName(@PathVariable String name) {
        return articleService.getCategoryByName(name)
                .map(category -> ResponseEntity.ok(convertToDto(category)))
                .orElse(ResponseEntity.notFound().build());
    }

    private CategoryDto convertToDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .displayOrder(category.getDisplayOrder())
                .isActive(category.getIsActive())
                .build();
    }
}