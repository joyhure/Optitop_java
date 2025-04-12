package com.optitop.optitop_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.optitop.optitop_api.dto.SellerDTO;
import com.optitop.optitop_api.service.SellerService;
import java.util.List;

@RestController
@RequestMapping("/api/sellers")
@CrossOrigin(origins = "http://localhost")
public class SellerController {

    private final SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @GetMapping("/available-sellers")
    public ResponseEntity<List<SellerDTO>> getAvailableSellers() {
        List<SellerDTO> sellers = sellerService.findAvailableSellers();
        return sellers.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(sellers);
    }
}