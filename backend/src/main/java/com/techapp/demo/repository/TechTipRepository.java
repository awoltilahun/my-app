package com.techapp.demo.repository;

import com.techapp.demo.entity.TechTip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TechTipRepository extends JpaRepository<TechTip, Long> {
    
    // Find tech tips by title containing a keyword (case-insensitive)
    List<TechTip> findByTitleContainingIgnoreCase(String keyword);

    // Find tech tips by description containing a keyword (case-insensitive)
    List<TechTip> findByDescriptionContainingIgnoreCase(String keyword);

    // Find tech tip by exact tag (case-insensitive)
    TechTip findByTagIgnoreCase(String tag);
}
