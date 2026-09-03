package com.techapp.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class FileUploadController {

    // Image upload is handled directly from browser to Cloudinary
    // This endpoint is kept for compatibility but not used
    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> status() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "Images upload directly to Cloudinary from browser");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
