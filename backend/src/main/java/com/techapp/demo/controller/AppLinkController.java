package com.techapp.demo.controller;

import com.techapp.demo.entity.AppLink;
import com.techapp.demo.service.AppLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/applinks")
@CrossOrigin(origins = "*")
public class AppLinkController {
    
    @Autowired
    private AppLinkService appLinkService;
    
    // Get all app links
    @GetMapping
    public ResponseEntity<List<AppLink>> getAllAppLinks() {
        List<AppLink> appLinks = appLinkService.getAllAppLinks();
        return new ResponseEntity<>(appLinks, HttpStatus.OK);
    }
    
    // Get app link by ID
    @GetMapping("/{id}")
    public ResponseEntity<AppLink> getAppLinkById(@PathVariable Long id) {
        Optional<AppLink> appLink = appLinkService.getAppLinkById(id);
        return appLink.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    // Create new app link
    @PostMapping
    public ResponseEntity<AppLink> createAppLink(@RequestBody AppLink appLink) {
        AppLink createdAppLink = appLinkService.createAppLink(appLink);
        return new ResponseEntity<>(createdAppLink, HttpStatus.CREATED);
    }
    
    // Update existing app link
    @PutMapping("/{id}")
    public ResponseEntity<AppLink> updateAppLink(@PathVariable Long id, @RequestBody AppLink appLink) {
        AppLink updatedAppLink = appLinkService.updateAppLink(id, appLink);
        if (updatedAppLink != null) {
            return new ResponseEntity<>(updatedAppLink, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    // Delete app link
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppLink(@PathVariable Long id) {
        boolean deleted = appLinkService.deleteAppLink(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    // Search app links by keyword
    @GetMapping("/search")
    public ResponseEntity<List<AppLink>> searchAppLinks(@RequestParam String keyword) {
        List<AppLink> appLinks = appLinkService.searchAppLinks(keyword);
        return new ResponseEntity<>(appLinks, HttpStatus.OK);
    }
}
