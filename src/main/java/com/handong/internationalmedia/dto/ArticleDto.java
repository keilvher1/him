package com.handong.internationalmedia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleDto {
    private Long id;
    
    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    private String title;
    
    @NotBlank(message = "내용은 필수 입력 항목입니다.")
    private String content;
    
    private String summary;
    
    private String author;
    
    private String authorName;
    
    private String featuredImage;
    
    private Integer readTime;
    
    private Boolean isFeatured = false;
    
    private Boolean isPublished = true;
    
    @NotNull(message = "카테고리를 선택해주세요.")
    private Long categoryId;
    
    private String categoryName;
    
    private Long viewCount;
    
    private String publishedAt;
    
    private String createdAt;
    
    private String updatedAt;
}