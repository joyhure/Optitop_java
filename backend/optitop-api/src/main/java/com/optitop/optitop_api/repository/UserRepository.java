package com.optitop.optitop_api.repository;

import com.optitop.optitop_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByLogin(String login);

    @Query("SELECT u.login FROM User u ORDER BY u.login")
    List<String> findAllLogins();
}
