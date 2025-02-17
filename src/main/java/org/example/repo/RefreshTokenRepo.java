package org.example.repo;

import org.example.entity.RefreshToken;
import org.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {

    @Modifying
    int deleteByUser(User user);

    Optional<RefreshToken> findByToken(String token);
}
