package dev.m4nd3l.chatting4ever.database;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseExtensionInitializer {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseExtensionInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeExtensions() {
        jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS pg_trgm;");

        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_user_username_trgm ON users USING gin (username gin_trgm_ops);");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_user_displayed_name_trgm ON users USING gin (displayed_name gin_trgm_ops);");
    }
}