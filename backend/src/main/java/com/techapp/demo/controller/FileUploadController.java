package com.techapp.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class FileUploadController {

    @Value("${upload.dir:C:/uploads}")
    private String uploadDir;

    @Value("${server.url:http://localhost:8080}")
    private String serverUrl;

    @PostMapping("/image")
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file) {

        Map<String, String> response = new HashMap<>();

        if (file.isEmpty()) {
            response.put("error", "No file selected");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            response.put("error", "Only image files are allowed");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            response.put("error", "File too large. Max 5MB.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            // Use absolute path to avoid Tomcat temp dir issues
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String originalName = file.getOriginalFilename();
            String extension = (originalName != null && originalName.contains("."))
                    ? originalName.substring(originalName.lastIndexOf(".")).toLowerCase()
                    : ".jpg";
            String fileName = UUID.randomUUID().toString() + extension;

            // Save using InputStream — avoids Tomcat temp file issues
            Path filePath = uploadPath.resolve(fileName);
            try (InputStream is = file.getInputStream()) {
                Files.copy(is, filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            String imageUrl = serverUrl + "/uploads/" + fileName;
            response.put("url", imageUrl);
            response.put("fileName", fileName);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (IOException e) {
            response.put("error", "Upload failed: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
