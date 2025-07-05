package com.example.recruitmentagent.repository;

import com.example.recruitmentagent.model.Vacancy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VacancyRepository extends JpaRepository<Vacancy, Long> {
    // Búsqueda avanzada por ubicación, experiencia y requisitos
    Page<Vacancy> findByLocationContainingIgnoreCaseAndExperienceYearsGreaterThanEqualAndRequirementsContainingIgnoreCase(
        String location, Integer experienceYears, String requirements, Pageable pageable);
}
