package dev.m4nd3l.chatting4ever.database.service;

import dev.m4nd3l.chatting4ever.database.model.PendingMessage;
import dev.m4nd3l.chatting4ever.database.model.User;
import dev.m4nd3l.chatting4ever.database.repository.PendingMessageRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PendingMessageService {
    private final PendingMessageRepository pendingMessageRepository;

    @Autowired
    public PendingMessageService(PendingMessageRepository pendingMessageRepository) { this.pendingMessageRepository = pendingMessageRepository; }

    public void save(PendingMessage message) { pendingMessageRepository.save(message); }

    public List<PendingMessage> getMessagesByReceiverID(long receiverID) { return pendingMessageRepository.findByReceiverID(receiverID); }
    public List<PendingMessage> getMessagesByReceiver(User receiver) { return getMessagesByReceiverID(receiver.getID()); }
    public List<PendingMessage> getMessagesBySenderID(long senderID) { return pendingMessageRepository.findBySenderID(senderID); }
    public List<PendingMessage> getMessagesBySender(User sender) { return getMessagesBySenderID(sender.getID()); }

    public void deleteAllWithReceiverID(long receiverID) { pendingMessageRepository.deleteAll(getMessagesByReceiverID(receiverID)); }
    public void deleteAllWithReceiver(User receiver) { pendingMessageRepository.deleteAll(getMessagesByReceiver(receiver)); }
    public void deleteAllWithSenderID(long senderID) { pendingMessageRepository.deleteAll(getMessagesBySenderID(senderID)); }
    public void deleteAllWithSender(User sender) { pendingMessageRepository.deleteAll(getMessagesBySender(sender)); }

    public void delete(PendingMessage message) { pendingMessageRepository.delete(message); }
}