package com.example.recruitmentagent.controller;

import com.example.recruitmentagent.model.Metrics;
import com.example.recruitmentagent.repository.MetricsRepository;
import com.example.recruitmentagent.repository.VacancyRepository;
import com.example.recruitmentagent.repository.CVRepository;
import com.example.recruitmentagent.model.CV;
import com.example.recruitmentagent.model.Vacancy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xddf.usermodel.chart.*;

@RestController
@RequestMapping("/metrics")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class MetricsController {
    @Autowired
    private MetricsRepository metricsRepo;
    @Autowired
    private VacancyRepository vacancyRepo;
    @Autowired
    private CVRepository cvRepo;

    @GetMapping
    public Map<String, Object> getMetrics() {
        Metrics metrics = metricsRepo.findById(1).orElse(new Metrics());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("cvsProcessed", metrics.getCvsProcessed());
        result.put("candidatesAptos", metrics.getCandidatesAptos());
        result.put("candidatesNoAptos", metrics.getCandidatesNoAptos());
        result.put("candidatesRevisionManual", metrics.getCandidatesRevisionManual());
        // Total de vacantes
        long totalVacancies = vacancyRepo.count();
        result.put("totalVacancies", totalVacancies);
        // Promedio de años de experiencia
        List<CV> cvs = cvRepo.findAll();
        double avgExperience = cvs.stream().filter(cv -> cv.getExperienceYears() != null).mapToInt(CV::getExperienceYears).average().orElse(0);
        result.put("avgExperience", avgExperience);
        // Top 3 skills más frecuentes
        Map<String, Long> skillCount = new HashMap<>();
        for (CV cv : cvs) {
            if (cv.getSkills() != null) {
                for (String skill : cv.getSkills().split(",")) {
                    String s = skill.trim();
                    if (!s.isEmpty()) skillCount.put(s, skillCount.getOrDefault(s, 0L) + 1);
                }
            }
        }
        List<String> topSkills = skillCount.entrySet().stream()
            .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
            .limit(3)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        result.put("topSkills", topSkills);
        // Distribución de niveles educativos
        List<String> allEducationLevels = Arrays.asList(
            "Licenciatura", "Ingeniería", "Maestría", "Doctorado", "Técnico", "Otro", "Universitaria", "No especificada"
        );
        Map<String, Long> educationDist = cvs.stream()
            .filter(cv -> cv.getEducation() != null)
            .collect(Collectors.groupingBy(CV::getEducation, Collectors.counting()));
        // Asegura que todos los niveles estén presentes
        Map<String, Long> fullEducationDist = new LinkedHashMap<>();
        for (String level : allEducationLevels) {
            fullEducationDist.put(level, educationDist.getOrDefault(level, 0L));
        }
        result.put("educationDistribution", fullEducationDist);
        // Vacante con más candidatos
        Map<String, Long> vacancyCount = cvs.stream()
            .filter(cv -> cv.getVacancy() != null)
            .collect(Collectors.groupingBy(cv -> cv.getVacancy().getTitle(), Collectors.counting()));
        String topVacancy = vacancyCount.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("N/A");
        result.put("topVacancy", topVacancy);
        // Fecha del último CV procesado
        Date lastCVDate = cvs.stream()
            .map(CV::getProcessedAt)
            .filter(Objects::nonNull)
            .max(Date::compareTo)
            .orElse(null);
        result.put("lastCVDate", lastCVDate);
        return result;
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportMetrics() throws IOException {
        Metrics metrics = metricsRepo.findById(1).orElse(new Metrics());
        List<CV> cvs = cvRepo.findAll();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Metrics");
        int rowIdx = 0;
        // Estilos
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        CellStyle bordered = workbook.createCellStyle();
        bordered.setBorderBottom(BorderStyle.THIN);
        bordered.setBorderTop(BorderStyle.THIN);
        bordered.setBorderLeft(BorderStyle.THIN);
        bordered.setBorderRight(BorderStyle.THIN);
        // Encabezado
        Row header = sheet.createRow(rowIdx++);
        Cell h0 = header.createCell(0);
        h0.setCellValue("Campo");
        h0.setCellStyle(headerStyle);
        Cell h1 = header.createCell(1);
        h1.setCellValue("Valor");
        h1.setCellStyle(headerStyle);
        // Datos simples
        String[][] datos = {
            {"CVs procesados", String.valueOf(metrics.getCvsProcessed())},
            {"Candidatos aptos", String.valueOf(metrics.getCandidatesAptos())},
            {"Candidatos no aptos", String.valueOf(metrics.getCandidatesNoAptos())},
            {"Candidatos revisión manual", String.valueOf(metrics.getCandidatesRevisionManual())}
        };
        for (String[] fila : datos) {
            Row r = sheet.createRow(rowIdx++);
            for (int i = 0; i < fila.length; i++) {
                Cell c = r.createCell(i);
                c.setCellValue(fila[i]);
                c.setCellStyle(bordered);
            }
        }
        // Promedio de experiencia
        double avgExperience = cvs.stream().filter(cv -> cv.getExperienceYears() != null).mapToInt(CV::getExperienceYears).average().orElse(0);
        Row rExp = sheet.createRow(rowIdx++);
        rExp.createCell(0).setCellValue("Promedio de años de experiencia");
        rExp.createCell(1).setCellValue(avgExperience);
        rExp.getCell(0).setCellStyle(bordered);
        rExp.getCell(1).setCellStyle(bordered);
        // Distribución de educación
        Map<String, Long> educationDist = cvs.stream()
            .filter(cv -> cv.getEducation() != null)
            .collect(Collectors.groupingBy(CV::getEducation, Collectors.counting()));
        int eduStart = rowIdx;
        for (Map.Entry<String, Long> entry : educationDist.entrySet()) {
            if (!"Universitaria".equals(entry.getKey())) {
                Row r = sheet.createRow(rowIdx++);
                r.createCell(0).setCellValue("Educación: " + entry.getKey());
                r.createCell(1).setCellValue(entry.getValue());
                r.getCell(0).setCellStyle(bordered);
                r.getCell(1).setCellStyle(bordered);
            }
        }
        int eduEnd = rowIdx - 1;
        // Vacante con más candidatos
        Map<String, Long> vacancyCount = cvs.stream()
            .filter(cv -> cv.getVacancy() != null)
            .collect(Collectors.groupingBy(cv -> cv.getVacancy().getTitle(), Collectors.counting()));
        String topVacancy = vacancyCount.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("N/A");
        Row rVac = sheet.createRow(rowIdx++);
        rVac.createCell(0).setCellValue("Vacante con más candidatos");
        rVac.createCell(1).setCellValue(topVacancy);
        rVac.getCell(0).setCellStyle(bordered);
        rVac.getCell(1).setCellStyle(bordered);
        // Fecha del último CV procesado
        Date lastCVDate = cvs.stream()
            .map(CV::getProcessedAt)
            .filter(Objects::nonNull)
            .max(Date::compareTo)
            .orElse(null);
        Row rDate = sheet.createRow(rowIdx++);
        rDate.createCell(0).setCellValue("Fecha del último CV procesado");
        rDate.createCell(1).setCellValue(lastCVDate != null ? lastCVDate.toString() : "N/A");
        rDate.getCell(0).setCellStyle(bordered);
        rDate.getCell(1).setCellStyle(bordered);
        // Gráfico de pastel para CVs procesados, aptos y no aptos
        int chartRowStart = rowIdx + 2;
        Row chartHeader = sheet.createRow(chartRowStart);
        chartHeader.createCell(0).setCellValue("Categoría");
        chartHeader.createCell(1).setCellValue("Cantidad");
        chartHeader.getCell(0).setCellStyle(headerStyle);
        chartHeader.getCell(1).setCellStyle(headerStyle);
        int chartDataStart = chartRowStart + 1;
        String[][] chartData = {
            {"CVs procesados", String.valueOf(metrics.getCvsProcessed())},
            {"Candidatos aptos", String.valueOf(metrics.getCandidatesAptos())},
            {"Candidatos no aptos", String.valueOf(metrics.getCandidatesNoAptos())}
        };
        for (int i = 0; i < chartData.length; i++) {
            Row r = sheet.createRow(chartDataStart + i);
            r.createCell(0).setCellValue(chartData[i][0]);
            r.createCell(1).setCellValue(Integer.parseInt(chartData[i][1]));
            r.getCell(0).setCellStyle(bordered);
            r.getCell(1).setCellStyle(bordered);
        }
        int chartDataEnd = chartDataStart + chartData.length - 1;
        // Crear gráfico de pastel para estos datos
        XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 3, chartRowStart, 10, chartRowStart + 15);
        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("CVs procesados, aptos y no aptos");
        chart.setTitleOverlay(false);
        XDDFChartData data = chart.createData(ChartTypes.PIE, null, null);
        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        for (int i = chartDataStart; i <= chartDataEnd; i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                labels.add(row.getCell(0).getStringCellValue());
                values.add(row.getCell(1).getNumericCellValue());
            }
        }
        XDDFDataSource<String> dsLabels = XDDFDataSourcesFactory.fromArray(labels.toArray(new String[0]));
        XDDFNumericalDataSource<Double> dsValues = XDDFDataSourcesFactory.fromArray(values.toArray(new Double[0]));
        data.addSeries(dsLabels, dsValues);
        chart.plot(data);
        // Ajustar ancho
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "metrics.xlsx");
        return ResponseEntity.ok()
                .headers(headers)
                .body(out.toByteArray());
    }

    @PutMapping
    public Metrics updateMetrics(@RequestBody Metrics metrics) {
        return metricsRepo.save(metrics);
    }
}
