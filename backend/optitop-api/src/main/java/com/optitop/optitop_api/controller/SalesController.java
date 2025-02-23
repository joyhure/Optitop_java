package com.optitop.optitop_api.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.optitop.optitop_api.service.SalesService;

@RestController
@RequestMapping("/api/sales")
@CrossOrigin(origins = "http://localhost")
public class SalesController {

    private static final Logger logger = LoggerFactory.getLogger(SalesController.class);

    @Autowired
    private SalesService salesService;

    /**
     * Handles CSV file import for sales data
     * 
     * @param file CSV file to import
     * @return ResponseEntity with success or error message
     */
    @PostMapping("/import")
    public ResponseEntity<?> importSales(@RequestParam("file") MultipartFile file) {
        try {
            logger.info("Starting import of file: {}", file.getOriginalFilename());

            List<String> batch = new ArrayList<>();

            // Read CSV file with UTF-8 encoding for proper accent handling
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(file.getInputStream(), "UTF-8"))) {

                String line = br.readLine();
                if (line != null) {
                    // Remove BOM if present
                    line = line.replace("\uFEFF", "");
                    batch.add(line);
                }

                while ((line = br.readLine()) != null) {
                    batch.add(line);
                }
            }

            salesService.processBatch(batch);
            logger.info("Successfully imported file: {}", file.getOriginalFilename());
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            logger.error("Error while importing sales file: {}", file.getOriginalFilename(), e);
            return ResponseEntity.status(500)
                    .body("Error while importing sales file: " + e.getMessage());
        }
    }
}