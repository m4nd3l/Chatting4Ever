package dev.m4nd3l.chatting4ever.database.repository;

import dev.m4nd3l.chatting4ever.database.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}