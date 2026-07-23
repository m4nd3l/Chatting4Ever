package dev.m4nd3l.chatting4ever.api.payloads;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Payload {
    public abstract String getString();

    protected String toJsonFormat(Map<String, String> payloadData) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        List<Map.Entry<String, String>> entryList = new ArrayList<>(payloadData.entrySet());
        for (int i = 0; i < entryList.size(); i++) {
            Map.Entry<String, String> entry = entryList.get(i);
            if (i != 0)  json.append(",\n");
            json.append("\t\"").append(escape(entry.getKey())).append("\": \"").append(escape(entry.getValue())).append("\"");
        }
        json.append("\n}");
        return json.toString();
    }

    private String escape(String input) {
        return input == null || input.isEmpty() ? "" :
                input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}