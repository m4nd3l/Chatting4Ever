package dev.m4nd3l.chatting4ever;

import dev.m4nd3l.chatting4ever.database.model.PendingEmailVerificationCode;
import dev.m4nd3l.chatting4ever.database.model.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EntityScan(basePackageClasses = { User.class, PendingEmailVerificationCode.class })
@EnableJpaRepositories(basePackages = "dev.m4nd3l.chatting4ever.database.repository")
public class Chatting4EverApplication {
    public static void main(String[] args) { SpringApplication.run(Chatting4EverApplication.class, args); }
}
