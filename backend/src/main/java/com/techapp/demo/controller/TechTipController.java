package com.techapp.demo.controller;

import com.techapp.demo.entity.TechTip;
import com.techapp.demo.service.TechTipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/techtips")
@CrossOrigin(origins = "*")
public class TechTipController {
    
    @Autowired
    private TechTipService techTipService;
    
    // Get all tech tips
    @GetMapping
    public ResponseEntity<List<TechTip>> getAllTechTips() {
        List<TechTip> techTips = techTipService.getAllTechTips();
        return new ResponseEntity<>(techTips, HttpStatus.OK);
    }
    
    // Get tech tip by ID
    @GetMapping("/{id}")
    public ResponseEntity<TechTip> getTechTipById(@PathVariable Long id) {
        Optional<TechTip> techTip = techTipService.getTechTipById(id);
        return techTip.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    // Create new tech tip
    @PostMapping
    public ResponseEntity<TechTip> createTechTip(@RequestBody TechTip techTip) {
        TechTip createdTechTip = techTipService.createTechTip(techTip);
        return new ResponseEntity<>(createdTechTip, HttpStatus.CREATED);
    }
    
    // Update existing tech tip
    @PutMapping("/{id}")
    public ResponseEntity<TechTip> updateTechTip(@PathVariable Long id, @RequestBody TechTip techTip) {
        TechTip updatedTechTip = techTipService.updateTechTip(id, techTip);
        if (updatedTechTip != null) {
            return new ResponseEntity<>(updatedTechTip, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    // Delete tech tip
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTechTip(@PathVariable Long id) {
        boolean deleted = techTipService.deleteTechTip(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    // Search tech tips by keyword
    @GetMapping("/search")
    public ResponseEntity<List<TechTip>> searchTechTips(@RequestParam String keyword) {
        List<TechTip> techTips = techTipService.searchTechTips(keyword);
        return new ResponseEntity<>(techTips, HttpStatus.OK);
    }
}
