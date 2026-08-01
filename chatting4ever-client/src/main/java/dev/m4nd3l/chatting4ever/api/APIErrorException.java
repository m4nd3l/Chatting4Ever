package dev.m4nd3l.chatting4ever.api;

import dev.m4nd3l.chatting4ever.api.response.data.ErrorData;
import dev.m4nd3l.chatting4ever.api.response.data.ServerErrorData;

public class APIErrorException extends RuntimeException {
    private ServerErrorData serverErrorData;
    private ErrorData errorData;

    public APIErrorException(String message) { super(message); }
    public APIErrorException(ServerErrorData serverErrorData, ErrorData errorData) { super(); this.serverErrorData = serverErrorData; this.errorData = errorData; }
    public APIErrorException(ServerErrorData serverErrorData) { this.serverErrorData = serverErrorData; }
    public APIErrorException(ErrorData errorData) { this.errorData = errorData; }

    public ServerErrorData getServerErrorData() { return serverErrorData; }
    public ErrorData getErrorData() { return errorData; }
    public String getErrorCause() { return getErrorData() != null ? getErrorData().getError() : (getServerErrorData() != null ? getServerErrorData().getMessage() : "Unknown"); }

    public APIErrorException setServerErrorData(ServerErrorData serverErrorData) { this.serverErrorData = serverErrorData; return this; }
    public APIErrorException setErrorData(ErrorData errorData) { this.errorData = errorData; return this; }
}
