package dev.m4nd3l.chatting4ever.database.service;

import dev.m4nd3l.chatting4ever.database.model.User;
import dev.m4nd3l.chatting4ever.database.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) { this.userRepository = userRepository; }

    public User getUserByID(long id) {
        Optional<User> userOptional = userRepository.findById(id);
        return userOptional.orElse(null);
    }

    public User getUserByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        return userOptional.orElse(null);
    }

    public User getUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        return userOptional.orElse(null);
    }

    public boolean contains(User user) { return containsID(user.getID()); }
    public boolean containsID(long userID) { return userRepository.existsById(userID); }
    public boolean containsUsername(String username) { return userRepository.existsByUsername(username); }
    public boolean containsEmail(String email) { return userRepository.existsByEmail(email); }

    public User save(User user) { return userRepository.save(user); }
    public User delete(User user) { userRepository.delete(user); return user; }
    public User deleteByID(long userID) { User user = getUserByID(userID); userRepository.deleteById(userID); return user; }
    public List<User> deleteAll(List<User> users) { userRepository.deleteAll(users); return users; }
    public List<User> deleteAllByIDs(List<Long> userIDs) {
        Iterable<User> all = userRepository.findAllById(userIDs);
        userRepository.deleteAllById(userIDs);
        return new ArrayList<>((Collection<? extends User>) all);
    }
    public List<User> deleteAll() {
        Iterable<User> all = userRepository.findAll();
        userRepository.deleteAll();
        return new ArrayList<>((Collection<? extends User>) all);
    }

    public void updateOnlineStatus(long userID, boolean isOnline) {
        User user = getUserByID(userID);
        if (user == null) return;
        user.setOnline(isOnline);
        save(user);
    }

    public List<User> searchUsers(String query) { return userRepository.searchFuzzyUsers(query); }
    public List<User> searchUsers(String query, int limit) { return userRepository.searchFuzzyUsers(query, limit); }
    public List<User> searchUsers(String query, int startIncluded, int count) {
        List<User> users =  userRepository.searchFuzzyUsers(query, startIncluded + count);
        if (users.isEmpty() || startIncluded >= users.size()) return new ArrayList<>();

        int toIndex = Math.min(startIncluded + count, users.size());
        return new ArrayList<>(users.subList(startIncluded, toIndex));
    }
}