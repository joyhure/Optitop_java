package com.optitop.optitop_api.repository;

import com.optitop.optitop_api.model.PendingAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PendingAccountRepository extends JpaRepository<PendingAccount, Integer> {

    // Vérifie si une demande existe déjà pour ce login
    boolean existsByLogin(String login);

    // Trouve toutes les demandes d'un utilisateur
    @Query("SELECT pa FROM PendingAccount pa WHERE pa.createdBy.id = :userId ORDER BY pa.createdAt DESC")
    List<PendingAccount> findByCreatedById(Integer userId);

    @Query("SELECT pa FROM PendingAccount pa JOIN FETCH pa.createdBy ORDER BY pa.createdAt DESC")
    List<PendingAccount> findAllByOrderByCreatedAtDesc();
}