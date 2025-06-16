package com.optitop.optitop_api.service;

// ===== IMPORTS DTO ET MODÈLES =====
import com.optitop.optitop_api.dto.AverageBasketDTO;
import com.optitop.optitop_api.dto.FrameStatsDTO;
import com.optitop.optitop_api.repository.InvoicesLinesRepository;
import com.optitop.optitop_api.repository.InvoicesRepository;

// ===== IMPORTS SPRING FRAMEWORK =====
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// ===== IMPORTS UTILITAIRES =====
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des statistiques de factures
 * 
 * Fournit les opérations métier pour calculer et analyser les données de vente
 * :
 * - Statistiques des paniers moyens par vendeur
 * - Analyses des montures primées
 * - Évolutions du chiffre d'affaires
 * - Comparaisons temporelles et performances vendeurs
 */
@Service
public class InvoiceService {

        // ===== INJECTION DES DÉPENDANCES =====

        /**
         * Repository pour l'accès aux données des lignes de factures
         * Gère les requêtes sur les détails produits (P1 MON, P1 VER, P2)
         */
        @Autowired
        private InvoicesLinesRepository invoicesLinesRepository;

        /**
         * Repository pour l'accès aux données des factures
         * Gère les requêtes sur les totaux et métadonnées de factures
         */
        @Autowired
        private InvoicesRepository invoicesRepository;

        /**
         * Logger pour tracer les opérations et déboguer les calculs
         */
        private static final Logger logger = LoggerFactory.getLogger(InvoiceService.class);

        // ===== MÉTHODES STATISTIQUES DÉTAILLÉES =====

        /**
         * Calcule les statistiques détaillées des paniers moyens par vendeur
         * 
         * @param startDate Date de début de la période d'analyse
         * @param endDate   Date de fin de la période d'analyse
         * @return Liste des statistiques par vendeur triée par référence vendeur
         */
        public List<AverageBasketDTO> getAverageBaskets(LocalDate startDate, LocalDate endDate) {
                // Récupération des montants totaux par vendeur
                Map<String, Double> totalAmounts = invoicesRepository.calculateTotalAmounts(startDate, endDate)
                                .stream()
                                .collect(Collectors.toMap(
                                                row -> (String) row[0],
                                                row -> (Double) row[1]));

                // Récupération des nombres de factures par vendeur
                Map<String, Long> invoiceCounts = invoicesRepository.calculateInvoiceCounts(startDate, endDate)
                                .stream()
                                .collect(Collectors.toMap(
                                                row -> (String) row[0],
                                                row -> (Long) row[1]));

                // Récupération des montants P1 montures
                Map<String, Double> p1MonAmounts = invoicesLinesRepository
                                .calculateP1FramesTotalAmounts(startDate, endDate)
                                .stream()
                                .collect(Collectors.toMap(
                                                row -> (String) row[0],
                                                row -> (Double) row[1],
                                                (v1, v2) -> v1,
                                                HashMap::new));

                // Récupération des compteurs P1 montures
                Map<String, Long> p1MonCounts = invoicesLinesRepository.calculateP1FramesCounts(startDate, endDate)
                                .stream()
                                .collect(Collectors.toMap(
                                                row -> (String) row[0],
                                                row -> (Long) row[1],
                                                (v1, v2) -> v1,
                                                HashMap::new));

                // Récupération des montants P1 verres
                Map<String, Double> p1VerAmounts = invoicesLinesRepository
                                .calculateP1LensesTotalAmounts(startDate, endDate)
                                .stream()
                                .collect(Collectors.toMap(
                                                row -> (String) row[0],
                                                row -> (Double) row[1],
                                                (v1, v2) -> v1,
                                                HashMap::new));

                // Récupération des compteurs P1 verres
                Map<String, Long> p1VerCounts = invoicesLinesRepository.calculateP1LensesCounts(startDate, endDate)
                                .stream()
                                .collect(Collectors.toMap(
                                                row -> (String) row[0],
                                                row -> (Long) row[1],
                                                (v1, v2) -> v1,
                                                HashMap::new));

                // Récupération des montants P2
                Map<String, Double> p2Amounts = invoicesLinesRepository.calculateP2TotalAmounts(startDate, endDate)
                                .stream()
                                .collect(Collectors.toMap(
                                                row -> (String) row[0],
                                                row -> (Double) row[1],
                                                (v1, v2) -> v1,
                                                HashMap::new));

                // Récupération des compteurs P2
                Map<String, Long> p2Counts = invoicesLinesRepository.calculateP2Counts(startDate, endDate)
                                .stream()
                                .collect(Collectors.toMap(
                                                row -> (String) row[0],
                                                row -> (Long) row[1],
                                                (v1, v2) -> v1,
                                                HashMap::new));

                // Création des DTOs avec toutes les données collectées
                return totalAmounts.keySet().stream()
                                .map(sellerRef -> new AverageBasketDTO(
                                                sellerRef,
                                                totalAmounts.get(sellerRef),
                                                invoiceCounts.getOrDefault(sellerRef, 0L),
                                                p1MonAmounts.getOrDefault(sellerRef, 0.0),
                                                p1MonCounts.getOrDefault(sellerRef, 0L),
                                                p1VerAmounts.getOrDefault(sellerRef, 0.0),
                                                p1VerCounts.getOrDefault(sellerRef, 0L),
                                                p2Amounts.getOrDefault(sellerRef, 0.0),
                                                p2Counts.getOrDefault(sellerRef, 0L)))
                                .sorted(Comparator.comparing(AverageBasketDTO::getSellerRef))
                                .collect(Collectors.toList());
        }

        /**
         * Calcule les statistiques globales du magasin pour la période donnée
         * 
         * @param startDate Date de début de la période d'analyse
         * @param endDate   Date de fin de la période d'analyse
         * @return DTO avec les totaux et moyennes globales (ligne TOTAL)
         */
        public AverageBasketDTO getTotalStats(LocalDate startDate, LocalDate endDate) {
                // Totaux depuis la table Invoices
                Double totalAmount = invoicesRepository.calculateTotalAmount(startDate, endDate);
                Long invoiceCount = invoicesRepository.calculateTotalInvoiceCount(startDate, endDate);

                // Totaux depuis la table InvoicesLines - P1 Montures
                List<Object[]> p1FramesTotalAmounts = invoicesLinesRepository.calculateP1FramesTotalAmounts(startDate,
                                endDate);

                logger.info("P1 Frames Total Amounts: {}", p1FramesTotalAmounts);
                p1FramesTotalAmounts.forEach(row -> {
                        logger.info("Seller: {}, Amount: {}", row[0], row[1]);
                });

                Double totalAmountP1MON = p1FramesTotalAmounts.stream()
                                .map(row -> (Double) row[1])
                                .reduce(0.0, Double::sum);

                logger.info("Total Amount P1MON: {}", totalAmountP1MON);

                Long countP1MON = invoicesLinesRepository
                                .calculateP1FramesCounts(startDate, endDate)
                                .stream()
                                .map(row -> (Long) row[1])
                                .reduce(0L, Long::sum);

                // Totaux P1 Verres
                Double totalAmountP1VER = invoicesLinesRepository
                                .calculateP1LensesTotalAmounts(startDate, endDate)
                                .stream()
                                .map(row -> (Double) row[1])
                                .reduce(0.0, Double::sum);

                Long countP1VER = invoicesLinesRepository
                                .calculateP1LensesCounts(startDate, endDate)
                                .stream()
                                .map(row -> (Long) row[1])
                                .reduce(0L, Long::sum);

                // Totaux P2
                Double totalAmountP2 = invoicesLinesRepository
                                .calculateP2TotalAmounts(startDate, endDate)
                                .stream()
                                .map(row -> (Double) row[1])
                                .reduce(0.0, Double::sum);

                Long countP2 = invoicesLinesRepository
                                .calculateP2Counts(startDate, endDate)
                                .stream()
                                .map(row -> (Long) row[1])
                                .reduce(0L, Long::sum);

                // Retour du DTO global avec reference "TOTAL"
                return new AverageBasketDTO(
                                "TOTAL",
                                totalAmount,
                                invoiceCount,
                                totalAmountP1MON,
                                countP1MON,
                                totalAmountP1VER,
                                countP1VER,
                                totalAmountP2,
                                countP2);
        }

        // ===== MÉTHODES ANALYSES DES MONTURES =====

        /**
         * Récupère les statistiques de vente des montures par vendeur
         * 
         * @param startDate Date de début de la période d'analyse
         * @param endDate   Date de fin de la période d'analyse
         * @return Liste des statistiques de montures (normales et primées) par vendeur
         */
        public List<FrameStatsDTO> getFrameStats(LocalDate startDate, LocalDate endDate) {
                // Récupération du nombre total de montures par vendeur
                Map<String, Long> totalFrames = invoicesLinesRepository.calculateP1FramesCounts(startDate, endDate)
                                .stream()
                                .collect(Collectors.toMap(
                                                row -> (String) row[0],
                                                row -> (Long) row[1]));

                // Récupération du nombre de montures primées par vendeur
                Map<String, Long> premiumFrames = invoicesLinesRepository
                                .calculatePremiumFramesCount(startDate, endDate)
                                .stream()
                                .collect(Collectors.toMap(
                                                row -> (String) row[0],
                                                row -> (Long) row[1]));

                // Création des DTOs avec les données de montures
                return totalFrames.keySet().stream()
                                .map(sellerRef -> new FrameStatsDTO(
                                                sellerRef,
                                                totalFrames.getOrDefault(sellerRef, 0L),
                                                premiumFrames.getOrDefault(sellerRef, 0L)))
                                .sorted(Comparator.comparing(FrameStatsDTO::getSellerRef))
                                .collect(Collectors.toList());
        }

        // ===== MÉTHODES DONNÉES RÉFÉRENTIELLES =====

        /**
         * Liste toutes les années pour lesquelles des factures existent en base
         * 
         * @return Liste des années d'activité disponibles
         */
        public List<Integer> getDistinctYears() {
                return invoicesLinesRepository.findDistinctYears();
        }

        // ===== MÉTHODES ANALYSES TEMPORELLES =====

        /**
         * Calcule le chiffre d'affaires mensuel pour une année donnée
         * 
         * @param year Année pour laquelle calculer le CA mensuel
         * @return Map avec les numéros de mois (1-12) et leurs montants de CA
         */
        public Map<Integer, Double> getMonthlyRevenue(int year) {
                return invoicesRepository.calculateMonthlyRevenue(year)
                                .stream()
                                .collect(Collectors.toMap(
                                                row -> (Integer) row[0],
                                                row -> (Double) row[1],
                                                (v1, v2) -> v1,
                                                () -> new HashMap<>()));
        }

        /**
         * Compare le chiffre d'affaires d'une période avec la même période l'année
         * précédente
         * 
         * @param startDate Date de début de la période d'analyse
         * @param endDate   Date de fin de la période d'analyse
         * @return Map avec currentAmount, previousAmount pour comparaison N vs N-1
         */
        public Map<String, Object> getPeriodRevenue(LocalDate startDate, LocalDate endDate) {
                Map<String, Object> result = new HashMap<>();

                // Calcul des dates pour la période précédente (année N-1)
                LocalDate previousStartDate = startDate.minusYears(1);
                LocalDate previousEndDate = endDate.minusYears(1);

                // Récupération des montants pour les deux périodes
                Double currentAmount = invoicesRepository.getCurrentPeriodTotal(startDate, endDate);
                Double previousAmount = invoicesRepository.getCurrentPeriodTotal(previousStartDate, previousEndDate);

                // Construction du résultat de comparaison
                result.put("currentAmount", currentAmount);
                result.put("previousAmount", previousAmount);

                return result;
        }

        // ===== MÉTHODES ANALYSES PAR VENDEUR =====

        /**
         * Calcule le chiffre d'affaires et le pourcentage de contribution de chaque
         * vendeur
         * 
         * @param startDate Date de début de la période d'analyse
         * @param endDate   Date de fin de la période d'analyse
         * @return Liste des vendeurs avec leur CA et pourcentage de contribution
         */
        public List<Map<String, Object>> getSellerRevenueStats(LocalDate startDate, LocalDate endDate) {
                List<Map<String, Object>> result = new ArrayList<>();

                // Récupération des données brutes depuis le repository
                List<Object[]> data = invoicesRepository.getSellerRevenueStats(startDate, endDate);

                // Transformation en format Map pour chaque vendeur
                for (Object[] row : data) {
                        Map<String, Object> sellerStats = new HashMap<>();
                        sellerStats.put("sellerRef", row[0]);
                        sellerStats.put("amount", row[1]);
                        sellerStats.put("percentage", row[2]);
                        result.add(sellerStats);
                }

                return result;
        }
}