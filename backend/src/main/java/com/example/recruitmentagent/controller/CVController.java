package com.example.recruitmentagent.controller;

import com.example.recruitmentagent.model.*;
import com.example.recruitmentagent.repository.*;
import com.example.recruitmentagent.service.CVProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/cvs")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class CVController {
    @Autowired
    private CVRepository cvRepo;
    @Autowired
    private VacancyRepository vacancyRepo;
    @Autowired
    private CVProcessingService cvService;
    @Autowired
    private MetricsRepository metricsRepo;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public CV registerCV(@RequestBody Map<String, Object> payload) throws Exception {
        String candidateName = (String) payload.get("candidateName");
        String cvText = (String) payload.get("cvText");
        Long vacancyId = Long.valueOf(payload.get("vacancyId").toString());
        Integer experienceYears = payload.get("experienceYears") != null ? Integer.valueOf(payload.get("experienceYears").toString()) : null;
        String education = (String) payload.get("education");
        if (candidateName == null || candidateName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del candidato es obligatorio.");
        }
        if (cvText == null || cvText.trim().isEmpty()) {
            throw new IllegalArgumentException("El texto del CV es obligatorio.");
        }
        if (vacancyId == null) {
            throw new IllegalArgumentException("La vacante es obligatoria.");
        }
        Vacancy vacancy = vacancyRepo.findById(vacancyId).orElseThrow();
        Map<String, Object> analysis = cvService.analyzeCV(cvText, vacancy.getRequirements(), education);
        CV cv = new CV();
        cv.setCandidateName(candidateName);
        cv.setFileName(candidateName + ".txt");
        cv.setExtractedText(cvText);
        cv.setSkills((String) analysis.get("skills"));
        cv.setExperienceYears(experienceYears != null ? experienceYears : (Integer) analysis.get("experienceYears"));
        cv.setEducation((String) analysis.get("education"));
        cv.setStatus((String) analysis.get("status"));
        cv.setVacancy(vacancy);
        cv.setFileContent(null);
        cv.setCohereScore(analysis.get("cohereScore") != null ? (Double) analysis.get("cohereScore") : null);
        cv.setCohereSummary((String) analysis.get("cohereSummary"));
        cvRepo.save(cv);
        Metrics metrics = metricsRepo.findById(1).orElse(new Metrics());
        metrics.setCvsProcessed(metrics.getCvsProcessed() + 1);
        switch (cv.getStatus()) {
            case "Apto": metrics.setCandidatesAptos(metrics.getCandidatesAptos() + 1); break;
            case "No Apto": metrics.setCandidatesNoAptos(metrics.getCandidatesNoAptos() + 1); break;
            default: metrics.setCandidatesRevisionManual(metrics.getCandidatesRevisionManual() + 1);
        }
        metricsRepo.save(metrics);
        return cv;
    }

    @PostMapping(path = "/analizar", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> analizarCV(@RequestBody Map<String, Object> payload) throws Exception {
        String cvText = (String) payload.get("cvText");
        Long vacancyId = Long.valueOf(payload.get("vacancyId").toString());
        if (cvText == null || cvText.trim().isEmpty()) {
            throw new IllegalArgumentException("El texto del CV es obligatorio.");
        }
        if (vacancyId == null) {
            throw new IllegalArgumentException("La vacante es obligatoria.");
        }
        Vacancy vacancy = vacancyRepo.findById(vacancyId).orElseThrow();
        Map<String, Object> analysis = cvService.analyzeCV(cvText, vacancy.getRequirements(), null);
        return analysis;
    }

    @GetMapping
    public Iterable<CV> getAll() {
        return cvRepo.findAll();
    }

    @GetMapping("/search")
    public Iterable<CV> searchCVs(@RequestParam(required = false) String skills,
                                  @RequestParam(required = false) Integer experienceYears,
                                  @RequestParam(required = false) String location) {
        if (skills == null) skills = "";
        if (experienceYears == null) experienceYears = 0;
        if (location == null) location = "";
        return cvRepo.findBySkillsContainingIgnoreCaseAndExperienceYearsGreaterThanEqualAndVacancy_LocationContainingIgnoreCase(
            skills, experienceYears, location);
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<byte[]> downloadCVFile(@PathVariable Long id) {
        CV cv = cvRepo.findById(id).orElseThrow();
        byte[] fileContent = cv.getFileContent();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + cv.getFileName());
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM).body(fileContent);
    }

    @PutMapping("/{id}")
    public CV updateCV(@PathVariable Long id, @RequestBody CV cvDetails) {
        CV cv = cvRepo.findById(id).orElseThrow(() -> new RuntimeException("CV not found"));
        cv.setCandidateName(cvDetails.getCandidateName());
        cv.setStatus(cvDetails.getStatus());
        return cvRepo.save(cv);
    }

    @DeleteMapping("/{id}")
    public void deleteCV(@PathVariable Long id) {
        cvRepo.deleteById(id);
    }

    @GetMapping("/apt-for-vacancy/{vacancyId}")
    public Iterable<CV> getAptCVsForVacancy(@PathVariable Long vacancyId) {
        Vacancy vacancy = vacancyRepo.findById(vacancyId).orElseThrow(() -> new RuntimeException("Vacancy not found"));
        Iterable<CV> allCVs = cvRepo.findAll();
        return StreamSupport.stream(allCVs.spliterator(), false)
                .filter(cv -> {
                    try {
                        Map<String, Object> analysis = cvService.analyzeCV(cv.getExtractedText(), vacancy.getRequirements(), null);
                        return "Apto".equals(analysis.get("status"));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/paged")
    public Page<CV> getPagedCVs(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return cvRepo.findAll(PageRequest.of(page, size));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        return ResponseEntity.status(400).body("Error: " + ex.getMessage());
    }
}
