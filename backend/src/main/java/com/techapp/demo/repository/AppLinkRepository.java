package com.techapp.demo.repository;

import com.techapp.demo.entity.AppLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppLinkRepository extends JpaRepository<AppLink, Long> {
    
    // Find app links by name containing a keyword (case-insensitive)
    List<AppLink> findByNameContainingIgnoreCase(String keyword);
    
    // Find app link by exact name
    AppLink findByName(String name);
}
