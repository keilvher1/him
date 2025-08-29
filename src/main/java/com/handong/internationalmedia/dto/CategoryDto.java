package com.handong.internationalmedia.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CategoryDto {
    private Long id;
    private String name;
    private String description;
    private Integer displayOrder;
    private Boolean isActive;
}