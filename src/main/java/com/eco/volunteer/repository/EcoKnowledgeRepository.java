package com.eco.volunteer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eco.volunteer.model.EcoKnowledge;

@Repository
public interface EcoKnowledgeRepository extends JpaRepository<EcoKnowledge, Long> {
} 