package dev.m4nd3l.chatting4ever.database.repository;

import dev.m4nd3l.chatting4ever.database.model.PendingEmailVerificationCode;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PendingEmailVerificationCodeRepository extends JpaRepository<PendingEmailVerificationCode, Long> {
    Optional<PendingEmailVerificationCode> findByCode(int code);
    Optional<PendingEmailVerificationCode> findByUserID(long userID);
    boolean existsByCode(int code);
    boolean existsByEmail(String email);
    boolean existsByUserID(long userID);
    void deleteAllByEmail(String email);
    @Transactional
    void deleteByExpirationDateBefore(LocalDateTime now);
}