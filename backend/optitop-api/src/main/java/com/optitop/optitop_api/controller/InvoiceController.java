package com.optitop.optitop_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.optitop.optitop_api.dto.AverageBasketDTO;
import com.optitop.optitop_api.dto.FrameStatsDTO;
import com.optitop.optitop_api.service.InvoiceService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "http://localhost")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @GetMapping("/average-baskets")
    public ResponseEntity<List<AverageBasketDTO>> getAverageBaskets(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<AverageBasketDTO> stats = invoiceService.getAverageBaskets(startDate, endDate);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/total-stats")
    public ResponseEntity<AverageBasketDTO> getTotalStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            AverageBasketDTO stats = invoiceService.getTotalStats(startDate, endDate);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/frame-stats")
    public ResponseEntity<List<FrameStatsDTO>> getFrameStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(invoiceService.getFrameStats(startDate, endDate));
    }

    @GetMapping("/years")
    public ResponseEntity<List<Integer>> getDistinctYears() {
        return ResponseEntity.ok(invoiceService.getDistinctYears());
    }

    @GetMapping("/monthly-revenue/{year}")
    public ResponseEntity<Map<Integer, Double>> getMonthlyRevenue(@PathVariable int year) {
        return ResponseEntity.ok(invoiceService.getMonthlyRevenue(year));
    }

    @GetMapping("/period-revenue")
    public ResponseEntity<Map<String, Object>> getPeriodRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(invoiceService.getPeriodRevenue(startDate, endDate));
    }

    @GetMapping("/seller-stats")
    public ResponseEntity<List<Map<String, Object>>> getSellerRevenueStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(invoiceService.getSellerRevenueStats(startDate, endDate));
    }

}