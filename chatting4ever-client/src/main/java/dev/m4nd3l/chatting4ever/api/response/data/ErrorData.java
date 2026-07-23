package dev.m4nd3l.chatting4ever.api.response.data;

public class ErrorData {
    private String error;
    private boolean missingInternet;

    public ErrorData(String error, boolean missingInternet) {
        this.error = error;
        this.missingInternet = missingInternet;
    }

    public String getError() { return error; }
    public boolean isMissingInternet() { return missingInternet; }

    public ErrorData setError(String error) { this.error = error; return this; }
    public ErrorData setMissingInternet(boolean missingInternet) { this.missingInternet = missingInternet; return this; }
}