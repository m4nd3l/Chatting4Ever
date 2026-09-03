package dev.m4nd3l.chatting4ever.pages.authentication.login;

import com.formdev.flatlaf.FlatClientProperties;
import dev.m4nd3l.chatting4ever.account.AccountData;
import dev.m4nd3l.chatting4ever.api.response.auth.AccountAuthResponse;
import dev.m4nd3l.chatting4ever.api.response.data.ErrorData;
import dev.m4nd3l.chatting4ever.components.*;
import dev.m4nd3l.chatting4ever.pages.MainPage;
import dev.m4nd3l.chatting4ever.pages.Page;

import javax.swing.*;
import java.awt.*;
import java.util.regex.Pattern;

public class ForgotPasswordPage extends JPanel implements Page {
    private static final Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$");

    private CEButton backButton;
    private CETextField emailField;
    private CEButton sendButton;
    private CEDigitsField codeField;
    private CEPasswordField passwordField;
    private CEPasswordField confirmPasswordField;
    private CEButton resetPasswordButton;

    public ForgotPasswordPage() { init(); }

    private void init() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        CELabel titleLabel = new CELabel("Forgot Password");
        titleLabel.setFontSize(24);
        titleLabel.setFontStyle(Font.BOLD);

        backButton = new CEButton("<");

        emailField = new CETextField().setPlaceholder("john.smith99@example.org").setAcceptanceRegex("[a-zA-Z0-9_.-@]+");
        sendButton = new CEButton("Send");
        codeField = new CEDigitsField(6, true);
        codeField.disableField();
        passwordField = new CEPasswordField();
        confirmPasswordField = new CEPasswordField();
        resetPasswordButton = new CEButton("Reset password");

        emailField.onEnterPressed(email -> { if (emailPattern.matcher(email).matches()) sendButton.doClick(); });
        emailField.focusOnEnterIfCondition(codeField, email -> emailPattern.matcher(email).matches(), _ -> emailField.showErrorBubble("Cannot submit an empty or invalid email"));
        codeField.focusOnEnter(passwordField);
        passwordField.focusOnEnterIfCondition(confirmPasswordField, password -> password != null && passwordPattern.matcher(password).matches(),
                _ -> passwordField.showErrorBubble("Password must be at least 8 characters long and contain at least one uppercase letter, \n" +
                        "one lowercase letter, one number, and one special character (@#$%^&+=!)"));
        confirmPasswordField.pressButtonOnEnter(resetPasswordButton);

        backButton.addActionListener(_ -> back());
        sendButton.addActionListener(_ -> sendCode());
        resetPasswordButton.addActionListener(_ -> changePassword());

        JPanel contentCard = new JPanel(new GridBagLayout());
        contentCard.putClientProperty(FlatClientProperties.STYLE, "background: #1e1e24; arc: 20;");
        contentCard.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        Dimension fixedSize = new Dimension(300, 35);
        sendButton.setPreferredSize(new Dimension(75, 35));
        resetPasswordButton.setPreferredSize(fixedSize);
        codeField.setPreferredSize(fixedSize);
        passwordField.setPreferredSize(fixedSize);
        confirmPasswordField.setPreferredSize(fixedSize);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.gridwidth = 1;
        contentCard.add(backButton, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(0, 0, 15, 0);
        contentCard.add(titleLabel, constraints);

        constraints.insets = new Insets(5, 5, 5, 5);

        constraints.gridy = 2;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.8;
        contentCard.add(emailField, constraints);

        constraints.gridx = 1;
        constraints.weightx = 0.2;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        contentCard.add(sendButton, constraints);

        constraints.weightx = 0.0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridwidth = 2;
        constraints.gridx = 0;

        constraints.gridy = 3;
        constraints.anchor = GridBagConstraints.WEST;
        contentCard.add(new CELabel("Code"), constraints);

        constraints.gridy = 4;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        contentCard.add(codeField, constraints);

        constraints.gridy = 5;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.NONE;
        contentCard.add(new CELabel("Password"), constraints);

        constraints.gridy = 6;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        contentCard.add(passwordField, constraints);

        constraints.gridy = 7;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.NONE;
        contentCard.add(new CELabel("Confirm Password"), constraints);

        constraints.gridy = 8;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        contentCard.add(confirmPasswordField, constraints);

        constraints.gridy = 9;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.CENTER;
        contentCard.add(resetPasswordButton, constraints);

        add(contentCard, new GridBagConstraints());
    }

    @Override
    public JPanel getPanel() { return this; }

    private void back() { changePage(new LoginPage()); }

    private void sendCode() {
        sendButton.setEnabled(false);
        codeField.enableField();
        String email = emailField.getText();

        if (isNullOrEmpty(email) && !emailPattern.matcher(email).matches()) {
            sendButton.setEnabled(true);
            showError("Invalid email");
            return;
        }

        ErrorData errorData = sendForgotPasswordCode(email);

        if (errorData != null) {
            showError(errorData.getError());
            sendButton.setEnabled(true);
            return;
        }

        sendButton.setEnabled(true);
    }

    private void changePassword() {
        resetPasswordButton.setEnabled(false);
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String code = codeField.getNumber();
        if (!isEverythingValid(email, password, confirmPassword, code, true)) {
            resetPasswordButton.setEnabled(true);
            return;
        }

        ErrorData errorData = verifyForgotPassword(email, code, password);

        if (errorData != null) {
            showError(errorData.getError());
            resetPasswordButton.setEnabled(true);
            return;
        }

        resetPasswordButton.setEnabled(true);
        changePage(new LoginPage());
    }

    private boolean isEverythingValid(String email, String password, String confirmPassword, String code, boolean showBubbles) {
        boolean emailValid = !isNullOrEmpty(email) && emailPattern.matcher(email).matches();

        boolean codeValid = !isNullOrEmpty(code) && code.matches("^\\d{6}$");
        boolean passwordValid = !isNullOrEmpty(password) && passwordPattern.matcher(password).matches();
        boolean passwordsMatch = password.equals(confirmPassword);

        boolean everythingValid = codeValid && passwordValid && passwordsMatch;

        if (!showBubbles) return everythingValid;

        if (!emailValid) showError("Invalid email");

        if (isNullOrEmpty(code)) showError("Invalid code");
        if (isNullOrEmpty(password)) passwordField.showErrorBubble("Cannot use null or empty password");
        else if (!passwordValid) passwordField.showErrorBubble("Password must be at least 8 characters long and contain at least one uppercase letter, \n" +
                "one lowercase letter, one number, and one special character (@#$%^&+=!)");

        if (isNullOrEmpty(confirmPassword)) confirmPasswordField.showErrorBubble("Type your password again here");
        else if (!passwordsMatch) confirmPasswordField.showErrorBubble("The confirmation password doesn't match");

        return everythingValid;
    }
}