package com.eco.volunteer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eco.volunteer.model.KnowledgeCategory;

@Repository
public interface KnowledgeCategoryRepository extends JpaRepository<KnowledgeCategory, Long> {
    boolean existsByName(String name);
} 