package dev.m4nd3l.chatting4ever.api.response.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;

public class ServerErrorData {
    @JsonProperty("timestamp") @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS") @JsonDeserialize(using = LocalDateTimeDeserializer.class) @JsonSerialize(using = LocalDateTimeSerializer.class) private LocalDateTime createdAt; private LocalDateTime timestamp;
    @JsonProperty("status") private int status;
    @JsonProperty("error") private String error;
    @JsonProperty("trace") private String trace;
    @JsonProperty("message") private String message;
    @JsonProperty("path") private String path;

    public ServerErrorData(LocalDateTime timestamp, int status, String error, String trace, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.trace = trace;
        this.message = message;
        this.path = path;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public String getMessage() { return message; }
    public int getStatus() { return status; }
    public String getTrace() { return trace; }
    public String getError() { return error; }
    public String getPath() { return path; }
}