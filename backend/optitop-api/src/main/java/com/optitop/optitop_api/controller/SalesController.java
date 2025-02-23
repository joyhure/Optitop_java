package com.optitop.optitop_api.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.optitop.optitop_api.service.SalesService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/sales")
@CrossOrigin(origins = "http://localhost")
public class SalesController {

    private static final Logger logger = LoggerFactory.getLogger(SalesController.class);

    @Autowired
    private SalesService salesService;

    @PostMapping("/import")
    public ResponseEntity<?> importSales(@RequestParam("file") MultipartFile file,
            @RequestParam("chunk") int chunk,
            @RequestParam("totalChunks") int totalChunks) {
        try {
            logger.info("Début de l'importation du fichier : " + file.getOriginalFilename());
            logger.info("Chunk : " + chunk + " / " + totalChunks);

            // Lecture du fichier avec BufferedReader pour gérer la mémoire et l'encodage
            // UTF-8
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(file.getInputStream(), "UTF-8"))) {

                List<String> batch = new ArrayList<>();
                String line;

                // Ignorer le BOM s'il existe
                line = br.readLine();
                if (line != null) {
                    line = line.replace("\uFEFF", "");
                    batch.add(line);
                }

                while ((line = br.readLine()) != null) {
                    batch.add(line);
                    logger.debug("Ligne lue : " + line); // Changé en debug pour réduire les logs
                }

                salesService.processBatch(batch);
            }

            logger.info("Importation réussie du fichier : " + file.getOriginalFilename());
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            logger.error("Erreur lors de l'importation des ventes", e);
            return ResponseEntity.status(500).body("Erreur lors de l'importation des ventes : " + e.getMessage());
        }
    }
}