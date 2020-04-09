package com.bolsaparatodos.bolsaparatodos.repository;

import com.bolsaparatodos.bolsaparatodos.entity.financial.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByApiKey(String apiKey);
}
