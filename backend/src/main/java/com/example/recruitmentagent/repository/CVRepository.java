package com.example.recruitmentagent.repository;

import com.example.recruitmentagent.model.CV;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CVRepository extends JpaRepository<CV, Long> {
    // Búsqueda avanzada por skills, experiencia y ubicación
    List<CV> findBySkillsContainingIgnoreCaseAndExperienceYearsGreaterThanEqualAndVacancy_LocationContainingIgnoreCase(
        String skills, Integer experienceYears, String location);

    Page<CV> findAll(Pageable pageable);
}
