package com.sentinelscm.api;

import com.sentinelscm.builder.Report;
import com.sentinelscm.security.Access;
import com.sentinelscm.service.ReportCsvWriter;
import com.sentinelscm.service.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/report")
public class ReportApiController {

    private final ReportService reports;

    public ReportApiController(ReportService reports) {
        this.reports = reports;
    }

    @GetMapping("/performance")
    @PreAuthorize(Access.ADMIN_PM_RA)
    public Report performance() {
        return reports.performance();
    }

    @GetMapping("/strategic")
    @PreAuthorize(Access.ADMIN)
    public Report strategic() {
        return reports.strategic();
    }

    @GetMapping(value = "/performance.csv", produces = "text/csv")
    @PreAuthorize(Access.ADMIN_PM_RA)
    public ResponseEntity<String> performanceCsv() {
        return csv("performance-report.csv", reports.performance());
    }

    @GetMapping(value = "/strategic.csv", produces = "text/csv")
    @PreAuthorize(Access.ADMIN)
    public ResponseEntity<String> strategicCsv() {
        return csv("strategic-report.csv", reports.strategic());
    }

    private static ResponseEntity<String> csv(String filename, Report report) {
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .contentType(new MediaType("text", "csv"))
            .body(ReportCsvWriter.toCsv(report));
    }
}
