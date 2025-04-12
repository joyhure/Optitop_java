package com.optitop.optitop_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.optitop.optitop_api.dto.PendingAccountDTO;
import com.optitop.optitop_api.dto.PendingAccountDisplayDTO;
import com.optitop.optitop_api.model.PendingAccount;
import com.optitop.optitop_api.service.PendingAccountService;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Collections;

@RestController
@RequestMapping("/api/pending-accounts")
@CrossOrigin(origins = "http://localhost")
public class PendingAccountController {

    private final PendingAccountService pendingAccountService;

    public PendingAccountController(PendingAccountService pendingAccountService) {
        this.pendingAccountService = pendingAccountService;
    }

    @PostMapping
    public ResponseEntity<?> createPendingAccount(
            @Valid @RequestBody PendingAccountDTO dto,
            @RequestHeader("Authorization") String authHeader) {

        Integer userId = Integer.valueOf(authHeader.replace("Bearer ", ""));

        try {
            pendingAccountService.createPendingAccount(dto, userId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Demande créée avec succès");
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Erreur lors de la création de la demande");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    @PostMapping("/validate/{id}")
    public ResponseEntity<?> validatePendingAccount(
            @PathVariable Integer id,
            @RequestHeader("Authorization") String authHeader) {
        try {
            Integer userId = Integer.valueOf(authHeader.replace("Bearer ", ""));
            pendingAccountService.validatePendingAccount(id, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<PendingAccountDisplayDTO>> getAllPendingAccounts() {
        try {
            List<PendingAccount> pendingAccounts = pendingAccountService.getAllPendingAccounts();

            List<PendingAccountDisplayDTO> displayDtos = pendingAccounts.stream()
                    .map(account -> new PendingAccountDisplayDTO(
                            account.getId(),
                            account.getLastname(),
                            account.getFirstname(),
                            account.getEmail(),
                            account.getLogin(),
                            account.getRole(),
                            account.getRequestType().name(),
                            account.getCreatedAt(),
                            account.getCreatedBy().getLogin()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(displayDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }
}