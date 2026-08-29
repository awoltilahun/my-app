package com.techapp.demo.service;

import com.techapp.demo.entity.TechTip;
import com.techapp.demo.repository.TechTipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TechTipService {

    @Autowired
    private TechTipRepository techTipRepository;

    public List<TechTip> getAllTechTips() {
        return techTipRepository.findAll();
    }

    public Optional<TechTip> getTechTipById(Long id) {
        return techTipRepository.findById(id);
    }

    public TechTip createTechTip(TechTip techTip) {
        return techTipRepository.save(techTip);
    }

    public TechTip updateTechTip(Long id, TechTip techTipDetails) {
        Optional<TechTip> techTipOptional = techTipRepository.findById(id);

        if (techTipOptional.isPresent()) {
            TechTip techTip = techTipOptional.get();
            techTip.setTitle(techTipDetails.getTitle());
            techTip.setDescription(techTipDetails.getDescription());
            techTip.setVideoLink(techTipDetails.getVideoLink());
            techTip.setImageUrl(techTipDetails.getImageUrl());
            techTip.setWebsiteUrl(techTipDetails.getWebsiteUrl());
            techTip.setTag(techTipDetails.getTag());
            return techTipRepository.save(techTip);
        }

        return null;
    }

    public boolean deleteTechTip(Long id) {
        if (techTipRepository.existsById(id)) {
            techTipRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Search by title keyword OR exact tag
    public List<TechTip> searchTechTips(String keyword) {
        List<TechTip> results = new ArrayList<>();

        // First try exact tag match
        TechTip byTag = techTipRepository.findByTagIgnoreCase(keyword);
        if (byTag != null) {
            results.add(byTag);
            return results;
        }

        // Fall back to title search
        return techTipRepository.findByTitleContainingIgnoreCase(keyword);
    }
}
