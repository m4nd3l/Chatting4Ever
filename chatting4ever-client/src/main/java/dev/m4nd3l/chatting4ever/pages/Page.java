package dev.m4nd3l.chatting4ever.pages;

import dev.m4nd3l.chatting4ever.Chatting4EverClient;
import dev.m4nd3l.chatting4ever.api.APIEndpoints;
import dev.m4nd3l.chatting4ever.api.APIErrorException;
import dev.m4nd3l.chatting4ever.api.payloads.account.ChangeEmailVisibility;
import dev.m4nd3l.chatting4ever.api.payloads.Payload;
import dev.m4nd3l.chatting4ever.api.payloads.account.*;
import dev.m4nd3l.chatting4ever.api.payloads.info.IsEmailTakenPayload;
import dev.m4nd3l.chatting4ever.api.payloads.info.IsUsernameTakenPayload;
import dev.m4nd3l.chatting4ever.api.response.auth.SuccessResponse;
import dev.m4nd3l.chatting4ever.api.response.auth.AccountAuthResponse;
import dev.m4nd3l.chatting4ever.api.response.auth.TokenResponse;
import dev.m4nd3l.chatting4ever.api.response.upload.UploadProfileImageResponse;
import dev.m4nd3l.chatting4ever.api.response.data.ErrorData;
import dev.m4nd3l.chatting4ever.api.response.info.EmailTakenResponse;
import dev.m4nd3l.chatting4ever.api.response.info.UsernameTakenResponse;
import dev.m4nd3l.easysaves.EasySaves;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public interface Page {
    default void beforeActivating() { }
    default void afterActivating() { }

    default void beforeHiding() { }
    default void afterHiding() { }
    default void beforeShowing() { }
    default void afterShowing() { }

    default void beforeChanging() { }
    default void afterChanging() { }

    JPanel getPanel();

    default void changePage(Page page) { Chatting4EverClient.Window.setContent(page); }

    default void showError(String error) { showError(getPanel(), error); }
    default void showError(JPanel mother, String error) {
        if (!mother.isShowing()) {
            mother.addAncestorListener(new AncestorListener() {
                public void ancestorAdded(AncestorEvent event) {
                    showError(mother, error);
                    mother.removeAncestorListener(this);
                }
                public void ancestorRemoved(AncestorEvent event) {}
                public void ancestorMoved(AncestorEvent event) {}
            });
            return;
        }

        JDialog dialog = new JDialog((Frame) null, "Error", false);
        dialog.setUndecorated(false);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(250, 100);
        dialog.setResizable(false);

        JLabel label = new JLabel("<html><div style='padding:10px;'>" + error + "</div></html>");
        dialog.add(label);

        if (mother.isShowing()) dialog.setLocationRelativeTo(mother);
        else dialog.setLocationRelativeTo(null);

        dialog.setVisible(true);

        Timer timer = new Timer(15000, _ -> dialog.dispose());
        timer.setRepeats(false);
        timer.start();
    }

    default AccountAuthResponse register(String username, String displayedName, String email, String password, boolean handOutUUID) {
        try { return APIEndpoints.register.sendPostRequest(new RegisterPayload(username, displayedName, email, password, handOutUUID), AccountAuthResponse.class); }
        catch (IOException exception) { return (AccountAuthResponse) new AccountAuthResponse().setErrorData(new ErrorData("No internet connection", true)).setSuccess(false); }
        catch (APIErrorException exception) { return (AccountAuthResponse) new AccountAuthResponse().setErrorData(new ErrorData(exception.getErrorCause(), false)).setSuccess(false); }
        catch (Exception exception) { return (AccountAuthResponse) new AccountAuthResponse().setErrorData(new ErrorData("Unknown error", false)).setSuccess(false); }
    }
    default AccountAuthResponse login(String usernameOrEmail, String password, boolean handOutUUID) {
        try { return APIEndpoints.login.sendPostRequest(new LoginPayload(usernameOrEmail, password, handOutUUID), AccountAuthResponse.class); }
        catch (IOException exception) { return (AccountAuthResponse) new AccountAuthResponse().setErrorData(new ErrorData("No internet connection", true)).setSuccess(false); }
        catch (APIErrorException exception) { return (AccountAuthResponse) new AccountAuthResponse().setErrorData(new ErrorData(exception.getErrorCause(), false)).setSuccess(false); }
        catch (Exception exception) { return (AccountAuthResponse) new AccountAuthResponse().setErrorData(new ErrorData("Unknown error", false)).setSuccess(false); }
    }
    default AccountAuthResponse getDataLogin(LoginPayload loginData, String code, boolean handOutUUID) {
        try { return APIEndpoints.getDataLogin.sendPostRequest(new GetDataLoginPayload(loginData.getUsernameOrEmail(), loginData.getPassword(), code, handOutUUID), AccountAuthResponse.class); }
        catch (IOException exception) { return (AccountAuthResponse) new AccountAuthResponse().setErrorData(new ErrorData("No internet connection", true)).setSuccess(false); }
        catch (APIErrorException exception) { return (AccountAuthResponse) new AccountAuthResponse().setErrorData(new ErrorData(exception.getErrorCause(), false)).setSuccess(false); }
        catch (Exception exception) { return (AccountAuthResponse) new AccountAuthResponse().setErrorData(new ErrorData("Unknown error", false)).setSuccess(false); }
    }
    default ErrorData changeDisplayedName(String token, String newDisplayedName) { return postRequest(APIEndpoints.changeDisplayedName, token, new ChangeDisplayedNamePayload(newDisplayedName)); }
    default TokenResponse changeUsername(String token, String newUsername) {
        try { return APIEndpoints.changeUsername.sendAuthenticatedPostRequest(token, new ChangeUsernameNamePayload(newUsername), TokenResponse.class); }
        catch (IOException exception) { return (TokenResponse) new TokenResponse().setErrorData(new ErrorData("No internet connection", true)).setSuccess(false); }
        catch (APIErrorException exception) { return (TokenResponse) new TokenResponse().setErrorData(new ErrorData(exception.getErrorCause(), false)).setSuccess(false); }
        catch (Exception exception) { return (TokenResponse) new TokenResponse().setErrorData(new ErrorData("Unknown error", false)).setSuccess(false); }
    }
    default ErrorData changeEmail(String token, String newEmail) { return postRequest(APIEndpoints.changeEmail, token, new ChangeEmailPayload(newEmail)); }
    default ErrorData changePassword(String token, String oldPassword, String newPassword) { return postRequest(APIEndpoints.changePassword, token, new ChangePasswordPayload(oldPassword, newPassword)); }
    default ErrorData changeProfileDescription(String token, String newDescription) { return postRequest(APIEndpoints.changeProfileDescription, token, new ChangeProfileDescriptionPayload(newDescription)); }
    default ErrorData changeProfileNote(String token, String newNote) { return postRequest(APIEndpoints.changeProfileNote, token, new ChangeProfileNotePayload(newNote)); }
    default ErrorData verifyEmail(String token, String code) { return postRequest(APIEndpoints.verifyEmail, token, new VerifyEmailPayload(code)); }
    default ErrorData resendEmailVerificationCode(String token) {return getRequest(APIEndpoints.resendVerificationEmail, token); }
    default ErrorData sendForgotPasswordCode(String email) { return  postRequest(APIEndpoints.forgotPassword, new ForgotPasswordPayload(email)); }
    default ErrorData verifyForgotPassword(String email, String code, String newPassword) { return postRequest(APIEndpoints.verifyForgotPassword, new VerifyForgotPassword(email, code, newPassword)); }
    default ErrorData changeEmailVisibility(String token, boolean newVisibility) { return postRequest(APIEndpoints.changeEmailVisibility, token, new ChangeEmailVisibility(newVisibility)); }
    default ErrorData change2FA(String token, boolean new2FA) { return postRequest(APIEndpoints.change2FA, token, new Change2FA(new2FA)); }
    default ErrorData changeProfileImage(String token, String newURL) { return postRequest(APIEndpoints.changeProfileImageURL, token, new ChangeProfileImageURL(newURL)); }
    default ErrorData delete(String token, String password) { return postRequest(APIEndpoints.delete, token, new DeletePayload(password)); }
    default UploadProfileImageResponse uploadProfileImage(String token, File image) {
        try { return APIEndpoints.uploadProfileImage.uploadFile(token, image, UploadProfileImageResponse.class); }
        catch (IOException exception) { return (UploadProfileImageResponse) new UploadProfileImageResponse().setErrorData(new ErrorData("No internet connection", true)).setSuccess(false); }
        catch (APIErrorException exception) { return (UploadProfileImageResponse) new UploadProfileImageResponse().setErrorData(new ErrorData(exception.getErrorCause(), false)).setSuccess(false); }
        catch (Exception exception) { return (UploadProfileImageResponse) new UploadProfileImageResponse().setErrorData(new ErrorData("Unknown error", false)).setSuccess(false); }
    }

    default boolean isUsernameTaken(String username) {
        try { return APIEndpoints.isUsernameTaken.sendPostRequest(new IsUsernameTakenPayload(username), UsernameTakenResponse.class).isUsernameTaken(); }
        catch (Exception _) { return false; }
    }

    default boolean isEmailTaken(String email) {
        try { return APIEndpoints.isEmailTaken.sendPostRequest(new IsEmailTakenPayload(email), EmailTakenResponse.class).isEmailTaken(); }
        catch (Exception _) { return false; }
    }

    private ErrorData postRequest(APIEndpoints.APIEndpoint endpoint, Payload payload) {
        try { return endpoint.sendPostRequest(payload, SuccessResponse.class).getErrorData(); }
        catch (IOException exception) { return new ErrorData("No internet connection", true); }
        catch (APIErrorException exception) { return new ErrorData(exception.getErrorCause(), false); }
        catch (Exception exception) { return new ErrorData("Unknown error", false); }
    }

    private ErrorData postRequest(APIEndpoints.APIEndpoint endpoint, String token, Payload payload) {
        try { return endpoint.sendAuthenticatedPostRequest(token, payload, SuccessResponse.class).getErrorData(); }
        catch (IOException exception) { return new ErrorData("No internet connection", true); }
        catch (APIErrorException exception) { return new ErrorData(exception.getErrorCause(), false); }
        catch (Exception exception) { return new ErrorData("Unknown error", false); }
    }

    private ErrorData getRequest(APIEndpoints.APIEndpoint endpoint, String token) {
        try { return endpoint.sendAuthenticatedGetRequest(token, SuccessResponse.class).getErrorData(); }
        catch (IOException exception) { return new ErrorData("No internet connection", true); }
        catch (APIErrorException exception) { return new ErrorData(exception.getErrorCause(), false); }
        catch (Exception exception) { return new ErrorData("Unknown error", false); }
    }

    default boolean isNullOrEmpty(String string) { return string == null || string.isEmpty(); }
    default boolean isNullOrEmpty(String... params) {
        if (params == null) return false;
        for (String param : params) if (isNullOrEmpty(param)) return true;
        return false;
    }

    default void changeUUID(UUID oldUUID, UUID newUUID) {
        if (!UUID.fromString(EasySaves.getSecureSetting("uuid")).equals(oldUUID)) return;
        EasySaves.addSecureSetting("uuid", newUUID.toString());
    }

    default void saveCredentials(AccountAuthResponse data) {
        if (data.has2FA()) showError("An error occurred while saving credentials");
        EasySaves.addSetting("logged-in", String.valueOf(true));
        EasySaves.addSecureSetting("username", data.getUsername());
        EasySaves.addSecureSetting("email", data.getEmail());
        EasySaves.addSecureSetting("uuid", data.getUUID().toString());
    }

    default void deleteCredentials() {
        EasySaves.addSetting("logged-in", String.valueOf(false));
        EasySaves.removeSetting("username");
        EasySaves.removeSetting("email");
        EasySaves.removeSetting("uuid");
    }

    default InputStream getResource(String pathNoSlash) { return getClass().getResourceAsStream("/" + pathNoSlash); }
}