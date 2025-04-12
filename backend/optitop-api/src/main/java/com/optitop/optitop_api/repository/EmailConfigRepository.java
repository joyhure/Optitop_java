package com.optitop.optitop_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.optitop.optitop_api.model.EmailConfig;

public interface EmailConfigRepository extends JpaRepository<EmailConfig, Integer> {
    EmailConfig findFirstByOrderByIdDesc();
}