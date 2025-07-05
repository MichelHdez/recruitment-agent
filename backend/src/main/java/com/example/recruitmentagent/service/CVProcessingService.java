package com.example.recruitmentagent.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.*;
import java.util.regex.*;

@Service
public class CVProcessingService {
    @Value("${cohere.api.key}")
    private String cohereApiKey;

    public Map<String, Object> analyzeCV(String text, String requirements, String educationFromUser) {
        Map<String, Object> result = new HashMap<>();
        List<String> keywords = Arrays.asList("JavaScript", "SQL", "Java");
        int years = extractYearsOfExperience(text);
        StringBuilder skills = new StringBuilder();
        for (String kw : keywords) {
            if (text.contains(kw)) skills.append(kw).append(", ");
        }
        String education = (educationFromUser != null && !educationFromUser.isEmpty()) ? educationFromUser : (text.toLowerCase().contains("ingeniero") || text.toLowerCase().contains("licenciado")) ? "Universitaria" : "No especificada";
        result.put("education", education);
        result.put("skills", skills.toString());
        result.put("experienceYears", years);
        Map<String, Object> cohereResult = analyzeWithCohere(text, requirements);
        result.putAll(cohereResult);
        Double score = (Double) cohereResult.get("cohereScore");
        String status = "Revisión Manual";
        if (score != null) {
            if (score >= 0.7) {
                status = "Apto";
            } else {
                status = "No Apto";
            }
        }
        result.put("status", status);
        return result;
    }

    private int extractYearsOfExperience(String text) {
        Pattern p = Pattern.compile("(\\d+)\\s+(años|years)\\s+de\\s+experiencia", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        p = Pattern.compile("(\\d+)\\s+años", Pattern.CASE_INSENSITIVE);
        m = p.matcher(text);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return 0;
    }

    private Map<String, Object> analyzeWithCohere(String cvText, String vacancyDescription) {
        Map<String, Object> result = new HashMap<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://api.cohere.ai/v1/chat";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + cohereApiKey);
            String prompt = "Dado el siguiente CV y la descripción de la vacante, responde solo con un JSON con dos campos: 'score' (de 0 a 1, donde 1 es máxima compatibilidad) y 'resumen' (una breve conclusión en español sobre la compatibilidad del candidato para la vacante). El resumen debe estar redactado de forma natural, profesional y clara, como si fuera una recomendación real para un reclutador. Sé conciso, utiliza un lenguaje humano y evita frases robóticas. Ejemplo: {\"score\":0.85,\"resumen\":\"El candidato cumple con la mayoría de los requisitos y es apto para el puesto.\"}";
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "command");
            requestBody.put("message", prompt + "\nCV: " + cvText + "\nVacante: " + vacancyDescription);
            requestBody.put("temperature", 0.2);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> resp = response.getBody();
                String text = (String) resp.get("text");
                if (text != null && text.contains("score")) {
                    text = text.trim();
                    if (text.startsWith("{")) {
                        int scoreIdx = text.indexOf("\"score\"");
                        int resumenIdx = text.indexOf("\"resumen\"");
                        Double score = null;
                        String resumen = null;
                        if (scoreIdx != -1) {
                            String s = text.substring(scoreIdx + 8);
                            s = s.split(",")[0].replace(":", "").replace("}", "").trim();
                            score = Double.valueOf(s);
                        }
                        if (resumenIdx != -1) {
                            String s = text.substring(resumenIdx + 10);
                            s = s.replace(":", "").replace("}", "").replace("\"", "").trim();
                            resumen = s;
                        }
                        result.put("cohereScore", score);
                        result.put("cohereSummary", resumen);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("cohereScore", null);
            result.put("cohereSummary", "No se pudo obtener el análisis de Cohere.");
        }
        return result;
    }
}
