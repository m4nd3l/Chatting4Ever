package dev.m4nd3l.chatting4ever.api;

import dev.m4nd3l.chatting4ever.database.repository.PendingEmailVerificationCodeRepository;
import java.time.LocalDateTime;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TokenCleanupService {
    private final PendingEmailVerificationCodeRepository repository;

    public TokenCleanupService(PendingEmailVerificationCodeRepository repository) { this.repository = repository; }

    @Scheduled(fixedRate = 60000)
    public void purgeExpiredCodes() { repository.deleteByExpirationDateBefore(LocalDateTime.now()); }
}