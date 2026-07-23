package dev.m4nd3l.chatting4ever.database.service;

import dev.m4nd3l.chatting4ever.database.model.PendingForgotPasswordCode;
import dev.m4nd3l.chatting4ever.database.model.User;
import dev.m4nd3l.chatting4ever.database.repository.PendingForgotPasswordCodeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PendingForgotPasswordCodeService {
    private final PendingForgotPasswordCodeRepository forgotPasswordCodeRepository;

    @Autowired
    public PendingForgotPasswordCodeService(PendingForgotPasswordCodeRepository verificationCodeRepository) { this.forgotPasswordCodeRepository = verificationCodeRepository; }

    public PendingForgotPasswordCode getForgotPasswordCodeByID(long id) {
        Optional<PendingForgotPasswordCode> verificationCodeOptional = forgotPasswordCodeRepository.findById(id);
        return verificationCodeOptional.orElse(null);
    }

    public PendingForgotPasswordCode getForgotPasswordCodeByCode(int code) {
        Optional<PendingForgotPasswordCode> forgotPasswordCodeOptional = forgotPasswordCodeRepository.findByCode(code);
        return forgotPasswordCodeOptional.orElse(null);
    }

    public PendingForgotPasswordCode getForgotPasswordCodeByUserID(long userID) {
        Optional<PendingForgotPasswordCode> forgotPasswordCodeOptional = forgotPasswordCodeRepository.findByUserID(userID);
        return forgotPasswordCodeOptional.orElse(null);
    }

    public boolean contains(PendingForgotPasswordCode verificationCode) { return containsID(verificationCode.getID()); }
    public boolean containsID(long verificationCodeID) { return forgotPasswordCodeRepository.existsById(verificationCodeID); }
    public boolean containsUser(User user) { return containsUser(user.getID()); }
    public boolean containsUser(long userID) { return forgotPasswordCodeRepository.existsByUserID(userID); }
    public boolean containsEmail(String email) { return forgotPasswordCodeRepository.existsByEmail(email); }
    public boolean containsCode(int code) { return forgotPasswordCodeRepository.existsByCode(code); }

    public PendingForgotPasswordCode save(PendingForgotPasswordCode verificationCode) { return forgotPasswordCodeRepository.save(verificationCode); }
    public PendingForgotPasswordCode delete(PendingForgotPasswordCode verificationCode) { forgotPasswordCodeRepository.delete(verificationCode); return verificationCode; }
    public PendingForgotPasswordCode deleteByID(long verificationCodeID) {
        PendingForgotPasswordCode verificationCode = getForgotPasswordCodeByID(verificationCodeID);
        forgotPasswordCodeRepository.deleteById(verificationCodeID);
        return verificationCode;
    }
    public List<PendingForgotPasswordCode> deleteAll(List<PendingForgotPasswordCode> codes) { forgotPasswordCodeRepository.deleteAll(codes); return codes; }
    public List<PendingForgotPasswordCode> deleteAllByIDs(List<Long> verificationCodeIDs) {
        Iterable<PendingForgotPasswordCode> all = forgotPasswordCodeRepository.findAllById(verificationCodeIDs);
        forgotPasswordCodeRepository.deleteAllById(verificationCodeIDs);
        return new ArrayList<>((Collection<? extends PendingForgotPasswordCode>) all);
    }
    public List<PendingForgotPasswordCode> deleteAllByCodes(List<Integer> verificationCodes) {
        List<PendingForgotPasswordCode> toRemove = forgotPasswordCodeRepository.findAll();
        toRemove.removeIf(emailVerificationCode -> !verificationCodes.contains(emailVerificationCode.getCode()));
        forgotPasswordCodeRepository.deleteAll(toRemove);
        return toRemove;
    }
    public List<PendingForgotPasswordCode> deleteAll() {
        Iterable<PendingForgotPasswordCode> all = forgotPasswordCodeRepository.findAll();
        forgotPasswordCodeRepository.deleteAll();
        return new ArrayList<>((Collection<? extends PendingForgotPasswordCode>) all);
    }
    public void deleteAllByEmail(String email) { forgotPasswordCodeRepository.deleteAllByEmail(email); }
}