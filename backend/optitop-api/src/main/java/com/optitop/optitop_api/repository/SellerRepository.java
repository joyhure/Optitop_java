package com.optitop.optitop_api.repository;

import com.optitop.optitop_api.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Integer> {

    // Vérifie si un vendeur existe avec cette référence
    boolean existsBySellerRef(String sellerRef);

    // Trouve un vendeur par sa référence
    Optional<Seller> findBySellerRef(String sellerRef);

    // Trouve tous les vendeurs triés par référence
    @Query("SELECT s FROM Seller s ORDER BY s.sellerRef")
    List<Seller> findAllOrderBySellerRef();

    // Trouve un vendeur par son user_id
    Optional<Seller> findByUserId(Integer userId);

    // Trouve tous les vendeurs sans utilisateur associé
    List<Seller> findAllByUserIsNull();
}
