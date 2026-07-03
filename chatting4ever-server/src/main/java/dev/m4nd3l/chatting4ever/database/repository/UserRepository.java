package dev.m4nd3l.chatting4ever.database.repository;

import dev.m4nd3l.chatting4ever.database.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = """
        SELECT * FROM users 
        WHERE username % :query OR displayed_name % :query 
        ORDER BY similarity(username, :query) DESC, similarity(displayed_name, :query) DESC 
        LIMIT :limit
        """, nativeQuery = true)
    List<User> searchFuzzyUsers(@Param("query") String query, @Param("limit") int limit);
    @Query(value = """
        SELECT * FROM users 
        WHERE username % :query OR displayed_name % :query 
        ORDER BY similarity(username, :query) DESC, similarity(displayed_name, :query) DESC
        """, nativeQuery = true)
    List<User> searchFuzzyUsers(@Param("query") String query);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}