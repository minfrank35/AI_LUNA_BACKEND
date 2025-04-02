package com.minfrank.luna.controller;

import com.minfrank.luna.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/tarot/images")
@RequiredArgsConstructor
public class TarotCardImageController {

    private final S3Service s3Service;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type) throws IOException {
        
        String imageUrl = s3Service.uploadFile(file, "tarot/" + type.toLowerCase());
        
        Map<String, String> response = new HashMap<>();
        response.put("imageUrl", imageUrl);
        
        return ResponseEntity.ok(response);
    }
} 