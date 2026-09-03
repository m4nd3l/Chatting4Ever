package dev.m4nd3l.chatting4ever.database.repository;

import dev.m4nd3l.chatting4ever.database.model.Pending2FALoginCode;
import dev.m4nd3l.chatting4ever.database.model.PendingForgotPasswordCode;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface Pending2FALoginCodeRepository extends JpaRepository<Pending2FALoginCode, Long> {
    Optional<Pending2FALoginCode> findByCode(int code);
    Optional<Pending2FALoginCode> findByUserID(long userID);
    boolean existsByCode(int code);
    boolean existsByEmail(String email);
    boolean existsByUserID(long userID);
    void deleteAllByEmail(String email);
    @Transactional
    void deleteByExpirationDateBefore(LocalDateTime now);
}