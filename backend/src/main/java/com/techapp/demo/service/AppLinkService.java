package com.techapp.demo.service;

import com.techapp.demo.entity.AppLink;
import com.techapp.demo.repository.AppLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppLinkService {
    
    @Autowired
    private AppLinkRepository appLinkRepository;
    
    // Get all app links
    public List<AppLink> getAllAppLinks() {
        return appLinkRepository.findAll();
    }
    
    // Get app link by ID
    public Optional<AppLink> getAppLinkById(Long id) {
        return appLinkRepository.findById(id);
    }
    
    // Create new app link
    public AppLink createAppLink(AppLink appLink) {
        return appLinkRepository.save(appLink);
    }
    
    // Update existing app link
    public AppLink updateAppLink(Long id, AppLink appLinkDetails) {
        Optional<AppLink> appLinkOptional = appLinkRepository.findById(id);
        
        if (appLinkOptional.isPresent()) {
            AppLink appLink = appLinkOptional.get();
            appLink.setName(appLinkDetails.getName());
            appLink.setPlaystoreUrl(appLinkDetails.getPlaystoreUrl());
            return appLinkRepository.save(appLink);
        }
        
        return null;
    }
    
    // Delete app link
    public boolean deleteAppLink(Long id) {
        if (appLinkRepository.existsById(id)) {
            appLinkRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    // Search app links by keyword
    public List<AppLink> searchAppLinks(String keyword) {
        return appLinkRepository.findByNameContainingIgnoreCase(keyword);
    }
}
