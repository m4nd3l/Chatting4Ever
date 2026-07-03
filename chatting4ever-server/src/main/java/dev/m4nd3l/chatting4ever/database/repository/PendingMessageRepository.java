package dev.m4nd3l.chatting4ever.database.repository;

import dev.m4nd3l.chatting4ever.database.model.PendingMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PendingMessageRepository extends JpaRepository<PendingMessage, Long> {
    List<PendingMessage> findByReceiverID(long receiverID);
    List<PendingMessage> findBySenderID(long receiverID);
}