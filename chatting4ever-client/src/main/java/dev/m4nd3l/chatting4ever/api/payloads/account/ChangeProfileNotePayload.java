package dev.m4nd3l.chatting4ever.api.payloads.account;

import dev.m4nd3l.chatting4ever.api.payloads.Payload;

import java.util.Map;

public class ChangeProfileNotePayload extends Payload {
    private String newProfileNote;

    public ChangeProfileNotePayload(String newProfileNote) { this.newProfileNote = newProfileNote; }

    public String getNewProfileNote() { return newProfileNote; }

    public ChangeProfileNotePayload setNewProfileNote(String newProfileNote) { this.newProfileNote = newProfileNote; return this; }

    @Override
    public String getString() { return toJsonFormat(Map.of("new-note", getNewProfileNote())); }
}