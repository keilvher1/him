package com.handong.internationalmedia.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {
    
    @Value("${cloudinary.cloud_name:#{null}}")
    private String cloudName;
    
    @Value("${cloudinary.api_key:#{null}}")
    private String apiKey;
    
    @Value("${cloudinary.api_secret:#{null}}")
    private String apiSecret;
    
    @Bean
    public Cloudinary cloudinary() {
        // Cloudinary 설정이 없으면 null 반환 (이미지 업로드 비활성화)
        if (cloudName == null || apiKey == null || apiSecret == null) {
            return null;
        }
        
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }
}