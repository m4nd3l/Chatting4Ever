package dev.m4nd3l.chatting4ever.api;

import dev.m4nd3l.chatting4ever.api.payloads.Payload;
import dev.m4nd3l.chatting4ever.api.response.Response;
import dev.m4nd3l.loggerutil.LoggerUtils;
import dev.m4nd3l.loggerutil.logger.Logger;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.List;

import static dev.m4nd3l.chatting4ever.Chatting4EverClient.DEBUG;

public class APIEndpoints {
    private static final HttpClient client = HttpClient.newHttpClient();

    private static final String main = "http://localhost:8080/api/";

    private static final String auth = main + "auth/";
    private static final String info = main + "info/";
    private static final String search = main + "search/";
    private static final String media = main + "media/";

    public static final APIEndpoint register = new APIEndpoint(auth + "register");
    public static final APIEndpoint login = new APIEndpoint(auth + "login");
    public static final APIEndpoint autologin = new APIEndpoint(auth + "autologin");
    public static final APIEndpoint getDataLogin = new APIEndpoint(auth + "get-data");
    public static final APIEndpoint changeUsername = new APIEndpoint(auth + "change-username");
    public static final APIEndpoint changeProfileImageURL = new APIEndpoint(auth + "change-profile-image");
    public static final APIEndpoint changeDisplayedName = new APIEndpoint(auth + "change-displayed-name");
    public static final APIEndpoint changeEmail = new APIEndpoint(auth + "change-email");
    public static final APIEndpoint changePassword = new APIEndpoint(auth + "change-password");
    public static final APIEndpoint changeProfileDescription = new APIEndpoint(auth + "change-profile-description");
    public static final APIEndpoint changeProfileNote = new APIEndpoint(auth + "change-profile-note");
    public static final APIEndpoint verifyEmail = new APIEndpoint(auth + "verify-email");
    public static final APIEndpoint resendVerificationEmail = new APIEndpoint(auth + "resend-verification-email");
    public static final APIEndpoint forgotPassword = new APIEndpoint(auth + "forgot-password");
    public static final APIEndpoint verifyForgotPassword = new APIEndpoint(auth + "verify-forgot-password");
    public static final APIEndpoint changeEmailVisibility = new APIEndpoint(auth + "change-email-visibility");
    public static final APIEndpoint change2FA = new APIEndpoint(auth + "change-2fa");
    public static final APIEndpoint delete = new APIEndpoint(auth + "delete");

    public static final APIEndpoint isUsernameTaken = new APIEndpoint(info + "is-username-taken");
    public static final APIEndpoint isEmailTaken = new APIEndpoint(info + "is-email-taken");

    public static final APIEndpoint userInfo = new APIEndpoint(search + "info");
    public static final APIEndpoint searchUser = new APIEndpoint(search + "search");

    public static final APIEndpoint uploadProfileImage = new APIEndpoint(media + "upload-profile-image");

    public static class APIEndpoint {
        private static final Logger LOGGER = LoggerUtils.getLogger();
        private String URL;

        public APIEndpoint(String url) { this.URL = url; }

        public String getURL() { return URL; }

        public APIEndpoint setURL(String URL) { this.URL = URL; return this; }

        @SuppressWarnings("DuplicatedCode")
        public <T extends Response> T sendAuthenticatedGetRequest(String token, Class<T> clazz) throws Exception {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", token)
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            return makeRequest(request, clazz, "GET REQUEST");
        }

        public <T extends Response> T sendAuthenticatedPostRequest(String token, Payload payload, Class<T> clazz) throws Exception {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", token)
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload.getString()))
                    .build();

            return makeRequest(request, clazz, payload.getString());
        }

        public <T extends Response> T sendPostRequest(Payload payload, Class<T> clazz) throws Exception {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload.getString()))
                    .build();

            return makeRequest(request, clazz, payload.getString());
        }

        public <T extends Response> T uploadFile(String token, File file, Class<T> clazz) throws Exception {
            String boundary = "Boundary" + System.currentTimeMillis();

            String requestBody = "--" + boundary + "\r\n" +
                    "Content-Disposition: form-data; name=\"file\"; filename=\"" + file.toPath().getFileName() + "\"\r\n" +
                    "Content-Type: application/octet-stream\r\n\r\n";

            byte[] fileBytes = Files.readAllBytes(file.toPath());
            byte[] footer = ("\r\n--" + boundary + "--\r\n").getBytes();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getURL()))
                    .header("Authorization", token)
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(HttpRequest.BodyPublishers.ofByteArrays(List.of(requestBody.getBytes(), fileBytes, footer)))
                    .build();

            return makeRequest(request, clazz, "FILE - " + file.getAbsolutePath());
        }

        private <T extends Response> T makeRequest(HttpRequest request, Class<T> clazz, String payload) throws Exception {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            T parsedResponse = Response.fromJson(response.body(), clazz);
            if (DEBUG) LOGGER.debug(getURL() + "\nHEADERS:\n" + Response.MAPPER.writeValueAsString(request.headers().map()) + "\nPAYLOAD:\n" + payload + "\nRESPONSE:\n" + response.body() + "\n\n");
            if (parsedResponse != null && parsedResponse.isValidResponse()) return parsedResponse;
            if (parsedResponse == null) throw new Exception("");
            if (parsedResponse.isServerError()) throw new APIErrorException(parsedResponse.getServerErrorData());
            else throw new APIErrorException(parsedResponse.getErrorData());
        }
    }
}