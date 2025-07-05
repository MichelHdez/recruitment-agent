package com.example.recruitmentagent.controller;

import com.example.recruitmentagent.model.Vacancy;
import com.example.recruitmentagent.repository.VacancyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vacancies")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class VacancyController {
    @Autowired
    private VacancyRepository vacancyRepo;

    @PostMapping
    public Vacancy createVacancy(@RequestBody Vacancy vacancy) {
        return vacancyRepo.save(vacancy);
    }

    @PostMapping("/{id}/publish")
    public String publishVacancy(@PathVariable Long id) {
        return "Vacante publicada internamente (simulado)";
    }

    @GetMapping
    public Iterable<Vacancy> getAll() {
        return vacancyRepo.findAll();
    }

    @GetMapping("/search")
    public Page<Vacancy> searchVacancies(@RequestParam(required = false) String location,
                                         @RequestParam(required = false) Integer experienceYears,
                                         @RequestParam(required = false) String requirements,
                                         Pageable pageable) {
        if (location == null) location = "";
        if (experienceYears == null) experienceYears = 0;
        if (requirements == null) requirements = "";
        return vacancyRepo.findByLocationContainingIgnoreCaseAndExperienceYearsGreaterThanEqualAndRequirementsContainingIgnoreCase(
            location, experienceYears, requirements, pageable);
    }

    @PutMapping("/{id}")
    public Vacancy updateVacancy(@PathVariable Long id, @RequestBody Vacancy vacancyDetails) {
        Vacancy vacancy = vacancyRepo.findById(id).orElseThrow(() -> new RuntimeException("Vacancy not found"));
        vacancy.setTitle(vacancyDetails.getTitle());
        vacancy.setDescription(vacancyDetails.getDescription());
        vacancy.setRequirements(vacancyDetails.getRequirements());
        vacancy.setLocation(vacancyDetails.getLocation());
        vacancy.setExperienceYears(vacancyDetails.getExperienceYears());
        return vacancyRepo.save(vacancy);
    }

    @DeleteMapping("/{id}")
    public void deleteVacancy(@PathVariable Long id) {
        vacancyRepo.deleteById(id);
    }
}
