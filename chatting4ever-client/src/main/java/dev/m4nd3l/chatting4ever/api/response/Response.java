package dev.m4nd3l.chatting4ever.api.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.m4nd3l.chatting4ever.Chatting4EverClient;
import dev.m4nd3l.chatting4ever.api.response.data.ErrorData;
import dev.m4nd3l.chatting4ever.api.response.data.ServerErrorData;

import java.time.LocalDateTime;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {
    @JsonIgnore public static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();
    @JsonIgnore private ErrorData errorData;
    @JsonIgnore private ServerErrorData serverErrorData;
    @JsonProperty("success") private boolean success = false;

    public ErrorData getErrorData() { return errorData; }
    public ServerErrorData getServerErrorData() { return serverErrorData; }
    public boolean wasSuccessful() { return success; }
    public boolean isServerError() { return serverErrorData != null; }

    public Response setErrorData(ErrorData errorData) { this.errorData = errorData; return this; }
    public Response setServerErrorData(ServerErrorData serverErrorData) { this.serverErrorData = serverErrorData; return this; }
    public Response setSuccess(boolean success) { this.success = success; return this; }

    public boolean isValidResponse() { return getErrorData() != null && !isServerError(); }

    public static <T extends Response> T fromJson(String json, Class<T> clazz) {
        try {
            T response =  MAPPER.readValue(json, clazz);
            if (response.wasSuccessful()) return response;
            try {
                Map<String, Object> map = MAPPER.readValue(json, new TypeReference<Map<String, Object>>() { });
                if (map.containsKey("timestamp") && map.containsKey("trace"))
                    response.setServerErrorData(new ServerErrorData(
                            (LocalDateTime) map.get("timestamp"),
                            (int) map.get("status"),
                            (String) map.get("error"),
                            (String) map.get("trace"),
                            (String) map.get("message"),
                            (String) map.get("path")));
                else if (map.containsKey("error"))
                    response.setErrorData(new ErrorData((String) map.get("error"), false));
            } catch (Exception _) { }
            return response;
        } catch (Exception _) {
            Chatting4EverClient.Window.error("Couldn't deserialize server response to " + clazz.getSimpleName());
            return null;
        }
    }
}