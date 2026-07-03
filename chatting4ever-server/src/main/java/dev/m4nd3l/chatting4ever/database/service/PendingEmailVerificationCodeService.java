package dev.m4nd3l.chatting4ever.database.service;

import dev.m4nd3l.chatting4ever.database.model.PendingEmailVerificationCode;
import dev.m4nd3l.chatting4ever.database.model.User;
import dev.m4nd3l.chatting4ever.database.repository.PendingEmailVerificationCodeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PendingEmailVerificationCodeService {
    private final PendingEmailVerificationCodeRepository verificationCodeRepository;

    @Autowired
    public PendingEmailVerificationCodeService(PendingEmailVerificationCodeRepository verificationCodeRepository) { this.verificationCodeRepository = verificationCodeRepository; }

    public PendingEmailVerificationCode getVerificationCodeByID(long id) {
        Optional<PendingEmailVerificationCode> verificationCodeOptional = verificationCodeRepository.findById(id);
        return verificationCodeOptional.orElse(null);
    }

    public PendingEmailVerificationCode getVerificationCodeByCode(int code) {
        Optional<PendingEmailVerificationCode> verificationCodeOptional = verificationCodeRepository.findByCode(code);
        return verificationCodeOptional.orElse(null);
    }

    public PendingEmailVerificationCode getVerificationCodesByUserID(long userID) {
        Optional<PendingEmailVerificationCode> verificationCodeOptional = verificationCodeRepository.findByUserID(userID);
        return verificationCodeOptional.orElse(null);
    }

    public boolean contains(PendingEmailVerificationCode verificationCode) { return containsID(verificationCode.getID()); }
    public boolean containsID(long verificationCodeID) { return verificationCodeRepository.existsById(verificationCodeID); }
    public boolean containsUser(User user) { return containsUser(user.getID()); }
    public boolean containsUser(long userID) { return verificationCodeRepository.existsByUserID(userID); }
    public boolean containsEmail(String email) { return verificationCodeRepository.existsByEmail(email); }
    public boolean containsCode(int code) { return verificationCodeRepository.existsByCode(code); }

    public PendingEmailVerificationCode save(PendingEmailVerificationCode verificationCode) { return verificationCodeRepository.save(verificationCode); }
    public PendingEmailVerificationCode delete(PendingEmailVerificationCode verificationCode) { verificationCodeRepository.delete(verificationCode); return verificationCode; }
    public PendingEmailVerificationCode deleteByID(long verificationCodeID) {
        PendingEmailVerificationCode verificationCode = getVerificationCodeByID(verificationCodeID);
        verificationCodeRepository.deleteById(verificationCodeID);
        return verificationCode;
    }
    public List<PendingEmailVerificationCode> deleteAll(List<PendingEmailVerificationCode> codes) { verificationCodeRepository.deleteAll(codes); return codes; }
    public List<PendingEmailVerificationCode> deleteAllByIDs(List<Long> verificationCodeIDs) {
        Iterable<PendingEmailVerificationCode> all = verificationCodeRepository.findAllById(verificationCodeIDs);
        verificationCodeRepository.deleteAllById(verificationCodeIDs);
        return new ArrayList<>((Collection<? extends PendingEmailVerificationCode>) all);
    }
    public List<PendingEmailVerificationCode> deleteAllByCodes(List<Integer> verificationCodes) {
        List<PendingEmailVerificationCode> toRemove = verificationCodeRepository.findAll();
        toRemove.removeIf(emailVerificationCode -> !verificationCodes.contains(emailVerificationCode.getCode()));
        verificationCodeRepository.deleteAll(toRemove);
        return toRemove;
    }
    public List<PendingEmailVerificationCode> deleteAll() {
        Iterable<PendingEmailVerificationCode> all = verificationCodeRepository.findAll();
        verificationCodeRepository.deleteAll();
        return new ArrayList<>((Collection<? extends PendingEmailVerificationCode>) all);
    }
    public void deleteAllByEmail(String email) {
        verificationCodeRepository.deleteAllByEmail(email);
    }
}
