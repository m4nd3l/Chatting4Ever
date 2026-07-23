package dev.m4nd3l.chatting4ever;

import dev.m4nd3l.chatting4ever.database.model.PendingEmailVerificationCode;
import dev.m4nd3l.chatting4ever.database.model.User;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@SpringBootApplication
@EnableScheduling
@EntityScan(basePackageClasses = { User.class, PendingEmailVerificationCode.class })
@ComponentScan("dev.m4nd3l.chatting4ever")
@EnableJpaRepositories(basePackages = "dev.m4nd3l.chatting4ever.database.repository")
public class Chatting4EverServer {
    public static void main(String[] args) { SpringApplication.run(Chatting4EverServer.class, args); }

    @PostConstruct
    public void init() {
        Path targetPath = Paths.get("server-data", "uploads", "profile-images", "default.png");
        try {
            Files.createDirectories(Paths.get("server-data/uploads/profile-images/"));
            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("images/default.png")) {
                if (inputStream == null) throw new IllegalArgumentException("File not found in resources");
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception _) { }
    }
}