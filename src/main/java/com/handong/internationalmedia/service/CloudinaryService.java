package com.handong.internationalmedia.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class CloudinaryService {
    
    private final Cloudinary cloudinary;
    
    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }
    
    public String uploadImage(MultipartFile file) {
        if (cloudinary == null) {
            log.warn("Cloudinary not configured, skipping image upload");
            return null; // 이미지 업로드 없이 진행
        }
        
        try {
            String originalFilename = file.getOriginalFilename();
            String fileName = UUID.randomUUID().toString() + "-" + originalFilename;
            
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", fileName,
                            "folder", "him-articles",
                            "resource_type", "image"
                    ));
            
            String imageUrl = (String) uploadResult.get("secure_url");
            log.info("Image uploaded successfully: {}", imageUrl);
            
            return imageUrl;
            
        } catch (IOException e) {
            log.error("Failed to upload image", e);
            throw new RuntimeException("Failed to upload image", e);
        }
    }
    
    public void deleteImage(String imageUrl) {
        try {
            String publicId = extractPublicId(imageUrl);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("Image deleted successfully: {}", publicId);
        } catch (IOException e) {
            log.error("Failed to delete image", e);
        }
    }
    
    private String extractPublicId(String imageUrl) {
        int startIndex = imageUrl.indexOf("him-articles/");
        int endIndex = imageUrl.lastIndexOf(".");
        if (startIndex != -1 && endIndex != -1) {
            return imageUrl.substring(startIndex, endIndex);
        }
        return "";
    }
}