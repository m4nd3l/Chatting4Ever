package dev.m4nd3l.chatting4ever.pages;

import dev.m4nd3l.chatting4ever.Chatting4EverClient;
import dev.m4nd3l.chatting4ever.api.APIEndpoints;
import dev.m4nd3l.chatting4ever.api.payloads.ChangeEmailVisibility;
import dev.m4nd3l.chatting4ever.api.payloads.Payload;
import dev.m4nd3l.chatting4ever.api.payloads.account.*;
import dev.m4nd3l.chatting4ever.api.payloads.info.IsEmailTakenPayload;
import dev.m4nd3l.chatting4ever.api.payloads.info.IsUsernameTakenPayload;
import dev.m4nd3l.chatting4ever.api.response.*;
import dev.m4nd3l.chatting4ever.api.response.data.ErrorData;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.io.File;
import java.io.IOException;

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

    default TokenAndInfoResponse register(String username, String displayedName, String email, String password) {
        try { return APIEndpoints.register.sendPostRequest(new RegisterPayload(username, displayedName, email, password), TokenAndInfoResponse.class); }
        catch (IOException exception) { return (TokenAndInfoResponse) new TokenAndInfoResponse().setErrorData(new ErrorData("No internet connection", true)).setSuccess(false); }
        catch (Exception exception) { return (TokenAndInfoResponse) new TokenAndInfoResponse().setErrorData(new ErrorData("Unknown error", false)).setSuccess(false); }
    }
    default TokenAndInfoResponse login(String usernameOrEmail, String password) {
        try { return APIEndpoints.login.sendPostRequest(new LoginPayload(usernameOrEmail, password), TokenAndInfoResponse.class); }
        catch (IOException exception) { return (TokenAndInfoResponse) new TokenAndInfoResponse().setErrorData(new ErrorData("No internet connection", true)).setSuccess(false); }
        catch (Exception exception) { return (TokenAndInfoResponse) new TokenAndInfoResponse().setErrorData(new ErrorData("Unknown error", false)).setSuccess(false); }
    }
    default ErrorData changeDisplayedName(String token, String newDisplayedName) { return postRequest(APIEndpoints.changeDisplayedName, token, new ChangeDisplayedNamePayload(newDisplayedName)); }
    default TokenResponse changeUsername(String token, String newUsername) {
        try { return APIEndpoints.changeUsername.sendAuthenticatedPostRequest(token, new ChangeUsernameNamePayload(newUsername), TokenResponse.class); }
        catch (IOException exception) { return (TokenResponse) new TokenResponse().setErrorData(new ErrorData("No internet connection", true)).setSuccess(false); }
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
    default ErrorData changeProfileImage(String token, String newURL) { return postRequest(APIEndpoints.changeProfileImageURL, token, new ChangeProfileImageURL(newURL)); }
    default ErrorData delete(String token, String password) { return postRequest(APIEndpoints.delete, token, new DeletePayload(password)); }
    default UploadProfileImageResponse uploadProfileImage(String token, File image) {
        try { return APIEndpoints.uploadProfileImage.uploadFile(token, image, UploadProfileImageResponse.class); }
        catch (IOException exception) { return (UploadProfileImageResponse) new UploadProfileImageResponse().setErrorData(new ErrorData("No internet connection", true)).setSuccess(false); }
        catch (Exception exception) { return (UploadProfileImageResponse) new UploadProfileImageResponse().setErrorData(new ErrorData("Unknown error", false)).setSuccess(false); }
    }

    default boolean isUsernameTaken(String username) {
        try { return APIEndpoints.isUsernameTaken.sendPostRequest(new IsUsernameTakenPayload(username), UsernameTakenResponse.class).isUsernameTaken(); }
        catch (Exception exception) { return false; }
    }

    default boolean isEmailTaken(String email) {
        try { return APIEndpoints.isEmailTaken.sendPostRequest(new IsEmailTakenPayload(email), EmailTakenResponse.class).isEmailTaken(); }
        catch (Exception exception) { return false; }
    }

    private ErrorData postRequest(APIEndpoints.APIEndpoint endpoint, Payload payload) {
        try { return endpoint.sendPostRequest(payload, SuccessResponse.class).getErrorData(); }
        catch (IOException exception) { return new ErrorData("No internet connection", true); }
        catch (Exception exception) { return new ErrorData("Unknown error", false); }
    }

    private ErrorData postRequest(APIEndpoints.APIEndpoint endpoint, String token, Payload payload) {
        try { return endpoint.sendAuthenticatedPostRequest(token, payload, SuccessResponse.class).getErrorData(); }
        catch (IOException exception) { return new ErrorData("No internet connection", true); }
        catch (Exception exception) { return new ErrorData("Unknown error", false); }
    }

    private ErrorData getRequest(APIEndpoints.APIEndpoint endpoint, String token) {
        try { return endpoint.sendAuthenticatedGetRequest(token, SuccessResponse.class).getErrorData(); }
        catch (IOException exception) { return new ErrorData("No internet connection", true); }
        catch (Exception exception) { return new ErrorData("Unknown error", false); }
    }

    default boolean isNullOrEmpty(String string) { return string == null || string.isEmpty(); }
    default boolean isNullOrEmpty(String... params) {
        if (params == null) return false;
        for (String param : params) if (isNullOrEmpty(param)) return true;
        return false;
    }
}