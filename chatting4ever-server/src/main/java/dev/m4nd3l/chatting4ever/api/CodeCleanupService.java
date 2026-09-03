package dev.m4nd3l.chatting4ever.api;

import dev.m4nd3l.chatting4ever.database.model.Pending2FALoginCode;
import dev.m4nd3l.chatting4ever.database.model.PendingForgotPasswordCode;
import dev.m4nd3l.chatting4ever.database.repository.Pending2FALoginCodeRepository;
import dev.m4nd3l.chatting4ever.database.repository.PendingEmailVerificationCodeRepository;
import java.time.LocalDateTime;

import dev.m4nd3l.chatting4ever.database.repository.PendingForgotPasswordCodeRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CodeCleanupService {
    private final PendingEmailVerificationCodeRepository pendingEmailCodeRepository;
    private final Pending2FALoginCodeRepository pending2FALoginCodeRepository;
    private final PendingForgotPasswordCodeRepository pendingForgotPasswordCodeRepository;

    public CodeCleanupService(PendingEmailVerificationCodeRepository pendingEmailCodeRepository, Pending2FALoginCodeRepository pending2FALoginCodeRepository, PendingForgotPasswordCodeRepository pendingForgotPasswordCodeRepository) {
        this.pendingEmailCodeRepository = pendingEmailCodeRepository;
        this.pending2FALoginCodeRepository = pending2FALoginCodeRepository;
        this.pendingForgotPasswordCodeRepository = pendingForgotPasswordCodeRepository;
    }

    @Scheduled(fixedRate = 60000)
    public void purgeExpiredCodes() {
        pendingEmailCodeRepository.deleteByExpirationDateBefore(LocalDateTime.now());
        pending2FALoginCodeRepository.deleteByExpirationDateBefore(LocalDateTime.now());
        pendingForgotPasswordCodeRepository.deleteByExpirationDateBefore(LocalDateTime.now());
    }
}