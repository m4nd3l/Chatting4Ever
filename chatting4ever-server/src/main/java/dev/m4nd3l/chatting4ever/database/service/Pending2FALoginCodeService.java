package dev.m4nd3l.chatting4ever.database.service;

import dev.m4nd3l.chatting4ever.database.model.Pending2FALoginCode;
import dev.m4nd3l.chatting4ever.database.model.User;
import dev.m4nd3l.chatting4ever.database.repository.Pending2FALoginCodeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class Pending2FALoginCodeService {
    private final Pending2FALoginCodeRepository pending2FALoginCodeRepository;

    @Autowired
    public Pending2FALoginCodeService(Pending2FALoginCodeRepository verificationCodeRepository) { this.pending2FALoginCodeRepository = verificationCodeRepository; }

    public Pending2FALoginCode get2FALoginCodeByID(long id) {
        Optional<Pending2FALoginCode> verificationCodeOptional = pending2FALoginCodeRepository.findById(id);
        return verificationCodeOptional.orElse(null);
    }

    public Pending2FALoginCode get2FALoginCodeByCode(int code) {
        Optional<Pending2FALoginCode> twoFALoginCodeOptional = pending2FALoginCodeRepository.findByCode(code);
        return twoFALoginCodeOptional.orElse(null);
    }

    public Pending2FALoginCode get2FALoginCodeByUserID(long userID) {
        Optional<Pending2FALoginCode> twoFALoginCodeOptional = pending2FALoginCodeRepository.findByUserID(userID);
        return twoFALoginCodeOptional.orElse(null);
    }

    public boolean contains(Pending2FALoginCode verificationCode) { return containsID(verificationCode.getID()); }
    public boolean containsID(long verificationCodeID) { return pending2FALoginCodeRepository.existsById(verificationCodeID); }
    public boolean containsUser(User user) { return containsUser(user.getID()); }
    public boolean containsUser(long userID) { return pending2FALoginCodeRepository.existsByUserID(userID); }
    public boolean containsEmail(String email) { return pending2FALoginCodeRepository.existsByEmail(email); }
    public boolean containsCode(int code) { return pending2FALoginCodeRepository.existsByCode(code); }

    public Pending2FALoginCode save(Pending2FALoginCode verificationCode) { return pending2FALoginCodeRepository.save(verificationCode); }
    public Pending2FALoginCode delete(Pending2FALoginCode verificationCode) { pending2FALoginCodeRepository.delete(verificationCode); return verificationCode; }
    public Pending2FALoginCode deleteByID(long verificationCodeID) {
        Pending2FALoginCode verificationCode = get2FALoginCodeByID(verificationCodeID);
        pending2FALoginCodeRepository.deleteById(verificationCodeID);
        return verificationCode;
    }
    public List<Pending2FALoginCode> deleteAll(List<Pending2FALoginCode> codes) { pending2FALoginCodeRepository.deleteAll(codes); return codes; }
    public List<Pending2FALoginCode> deleteAllByIDs(List<Long> verificationCodeIDs) {
        Iterable<Pending2FALoginCode> all = pending2FALoginCodeRepository.findAllById(verificationCodeIDs);
        pending2FALoginCodeRepository.deleteAllById(verificationCodeIDs);
        return new ArrayList<>((Collection<? extends Pending2FALoginCode>) all);
    }
    public List<Pending2FALoginCode> deleteAllByCodes(List<Integer> verificationCodes) {
        List<Pending2FALoginCode> toRemove = pending2FALoginCodeRepository.findAll();
        toRemove.removeIf(emailVerificationCode -> !verificationCodes.contains(emailVerificationCode.getCode()));
        pending2FALoginCodeRepository.deleteAll(toRemove);
        return toRemove;
    }
    public List<Pending2FALoginCode> deleteAll() {
        Iterable<Pending2FALoginCode> all = pending2FALoginCodeRepository.findAll();
        pending2FALoginCodeRepository.deleteAll();
        return new ArrayList<>((Collection<? extends Pending2FALoginCode>) all);
    }
    public void deleteAllByEmail(String email) { pending2FALoginCodeRepository.deleteAllByEmail(email); }
}