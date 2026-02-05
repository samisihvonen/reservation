// ============================================
// User Repository
// ============================================
// File: src/main/java/com/example/backend/repository/UserRepository.java

package com.example.backend.repository;


import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.example.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find a user by email address
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if user exists by email
     */
    boolean existsByEmail(String email);
    
    /**
     * Delete a user by email address
     * Returns the number of records deleted
     */
    @Transactional
    long deleteByEmail(String email);
}