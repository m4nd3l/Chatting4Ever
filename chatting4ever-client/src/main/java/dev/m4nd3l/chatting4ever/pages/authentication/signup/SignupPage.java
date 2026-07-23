package dev.m4nd3l.chatting4ever.pages.authentication.signup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.formdev.flatlaf.FlatClientProperties;
import dev.m4nd3l.chatting4ever.Chatting4EverClient;
import dev.m4nd3l.chatting4ever.account.AccountData;
import dev.m4nd3l.chatting4ever.api.APIEndpoints;
import dev.m4nd3l.chatting4ever.api.payloads.account.RegisterPayload;
import dev.m4nd3l.chatting4ever.api.response.TokenAndInfoResponse;
import dev.m4nd3l.chatting4ever.components.CELabel;
import dev.m4nd3l.chatting4ever.components.CEPasswordField;
import dev.m4nd3l.chatting4ever.components.CETextField;
import dev.m4nd3l.chatting4ever.pages.MainPage;
import dev.m4nd3l.chatting4ever.pages.Page;
import dev.m4nd3l.chatting4ever.pages.authentication.LoginPage;
import dev.m4nd3l.easysaves.EasySaves;
import dev.m4nd3l.easysaves.settings.EasySavesSettings;

import java.awt.*;
import java.util.regex.Pattern;
import javax.swing.*;

public class SignupPage extends JPanel implements Page {
    private static final Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$");
    private CETextField usernameField;
    private CETextField emailField;
    private CEPasswordField passwordField;
    private CEPasswordField confirmPasswordField;
    private JCheckBox saveCredentialsCheckbox;
    private JButton loginInsteadButton;
    private JButton registerButton;

    public SignupPage() { init(); }

    private void init() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        CELabel titleLabel = new CELabel("Create an Account");
        titleLabel.setFontSize(24);
        titleLabel.setFontStyle(Font.BOLD);

        usernameField = new CETextField("john.smith_", "[a-zA-Z0-9_.-]+");
        emailField = new CETextField("john.smith99@example.org", "[a-zA-Z0-9_.-@]+");
        passwordField = new CEPasswordField(8);
        confirmPasswordField = new CEPasswordField(8);

        loginInsteadButton = new JButton("Already have an account?");
        loginInsteadButton.putClientProperty(FlatClientProperties.STYLE, "borderWidth: 0; focusWidth: 0; background: null; foreground: #007aff");
        loginInsteadButton.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));

        saveCredentialsCheckbox = new JCheckBox("Save credentials", true);

        registerButton = new JButton("Register");
        registerButton.putClientProperty(FlatClientProperties.STYLE, "arc: 15; background: #007aff; foreground: #ffffff");

        usernameField.focusOnEnterIfCondition(emailField, username -> !isNullOrEmpty(username),
                _ -> usernameField.showErrorBubble("Cannot submit an empty username"));
        emailField.focusOnEnterIfCondition(passwordField, email -> !isNullOrEmpty(email) && emailPattern.matcher(email).matches(),
                _ -> emailField.showErrorBubble("Cannot submit an empty or invalid email"));
        passwordField.focusOnEnterIfCondition(confirmPasswordField, password -> password != null && passwordPattern.matcher(password).matches(),
                _ -> passwordField.showErrorBubble("Password must be at least 8 characters long and contain at least one uppercase letter, \n" +
                        "one lowercase letter, one number, and one special character (@#$%^&+=!)"));
        confirmPasswordField.pressButtonOnEnterIfCondition(registerButton, _ -> true, _ -> { });
        loginInsteadButton.addActionListener(_ -> switchToLoginScreen());
        registerButton.addActionListener(_ -> pressedRegisterButton());

        JPanel contentCard = new JPanel(new GridBagLayout());
        contentCard.putClientProperty(FlatClientProperties.STYLE, "background: #1e1e24; arc: 20;");
        contentCard.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        Dimension fixedSize = new Dimension(300, 35);
        usernameField.setPreferredSize(fixedSize);
        emailField.setPreferredSize(fixedSize);
        passwordField.setPreferredSize(fixedSize);
        confirmPasswordField.setPreferredSize(fixedSize);
        registerButton.setPreferredSize(new Dimension(300, 40));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(5, 0, 5, 0);
        constraints.gridwidth = 2;
        constraints.gridx = 0;

        constraints.gridy = 0;
        constraints.insets = new Insets(0, 0, 20, 0);
        contentCard.add(titleLabel, constraints);

        constraints.insets = new Insets(5, 0, 5, 0);

        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.WEST;
        contentCard.add(new CELabel("Username"), constraints);
        constraints.gridy = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        contentCard.add(usernameField, constraints);

        constraints.gridy = 3;
        constraints.anchor = GridBagConstraints.WEST;
        contentCard.add(new CELabel("Email"), constraints);
        constraints.gridy = 4;
        constraints.anchor = GridBagConstraints.CENTER;
        contentCard.add(emailField, constraints);

        constraints.gridy = 5;
        constraints.anchor = GridBagConstraints.WEST;
        contentCard.add(new CELabel("Password"), constraints);
        constraints.gridy = 6;
        constraints.anchor = GridBagConstraints.CENTER;
        contentCard.add(passwordField, constraints);

        constraints.gridy = 7;
        constraints.anchor = GridBagConstraints.WEST;
        contentCard.add(new CELabel("Confirm Password"), constraints);
        constraints.gridy = 8;
        constraints.anchor = GridBagConstraints.CENTER;
        contentCard.add(confirmPasswordField, constraints);

        constraints.gridy = 9;
        constraints.insets = new Insets(10, 0, 10, 0);
        constraints.gridwidth = 1;

        constraints.gridx = 0;
        constraints.weightx = 0.5;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        contentCard.add(saveCredentialsCheckbox, constraints);

        constraints.gridx = 1;
        constraints.weightx = 0.5;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.EAST;
        contentCard.add(loginInsteadButton, constraints);

        constraints.gridx = 0;
        constraints.gridwidth = 2;
        constraints.weightx = 0.0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridy = 10;
        constraints.insets = new Insets(10, 0, 0, 0);
        contentCard.add(registerButton, constraints);

        add(contentCard, new GridBagConstraints());
    }

    private void switchToLoginScreen() { changePage(new LoginPage()); }

    @Override
    public JPanel getPanel() { return this; }

    private void pressedRegisterButton() {
        registerButton.setEnabled(false);
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        if (!isEverythingValid(username, email, password, confirmPassword, true)) return;

        try {
            TokenAndInfoResponse data = APIEndpoints.register.sendPostRequest(new RegisterPayload(username, username, email, password), TokenAndInfoResponse.class);
            AccountData.setAccount(
                    new AccountData(
                            data.getToken(),
                            data.getUsername(),
                            data.getDisplayedName(),
                            data.getEmail(),
                            data.getProfileImageURL(),
                            data.getProfileDescription(),
                            data.getProfileNote(),
                            data.getCreatedAt(),
                            data.isPublicEmail()));

            EasySaves.addSetting("logged-in", String.valueOf(saveCredentialsCheckbox.isSelected()));
            if (saveCredentialsCheckbox.isSelected()) {
                EasySaves.addSecureSetting("username", username);
                EasySaves.addSecureSetting("email", email);
                EasySaves.addSecureSetting("password", password);
            } else {
                EasySaves.removeSetting("username");
                EasySaves.removeSetting("email");
                EasySaves.removeSetting("password");
            }
        } catch (Exception e) { showError("An error occurred while signing up, retry later"); System.err.println(e); }
        registerButton.setEnabled(true);
        changePage(new PersonalizeAccountPage());
    }

    private boolean isEverythingValid(String username, String email, String password, String confirmPassword, boolean showBubbles) {
        boolean usernameValid = !isNullOrEmpty(username);
        boolean usernameTaken = usernameValid && isUsernameTaken(username);

        boolean emailValid = !isNullOrEmpty(email) && emailPattern.matcher(email).matches();
        boolean emailTaken = emailValid && isEmailTaken(email);

        boolean passwordValid = !isNullOrEmpty(password) && passwordPattern.matcher(password).matches();
        boolean passwordsMatch = password.equals(confirmPassword);

        boolean everythingValid = usernameValid && !usernameTaken && emailValid && !emailTaken && passwordValid && passwordsMatch && !isNullOrEmpty(confirmPassword);

        if (!showBubbles) return everythingValid;

        if (!usernameValid) usernameField.showErrorBubble("Cannot submit an empty username");
        if (usernameTaken) usernameField.showErrorBubble("Username is already taken");

        if (isNullOrEmpty(email)) emailField.showErrorBubble("Cannot use null or empty email");
        else if (!emailValid) emailField.showErrorBubble("Cannot use invalid email");
        if (emailTaken) emailField.showErrorBubble("Email is already taken");

        if (isNullOrEmpty(password)) passwordField.showErrorBubble("Cannot use null or empty password");
        else if (!passwordValid) passwordField.showErrorBubble("Password must be at least 8 characters long and contain at least one uppercase letter, \n" +
                "one lowercase letter, one number, and one special character (@#$%^&+=!)");

        if (isNullOrEmpty(confirmPassword)) confirmPasswordField.showErrorBubble("Type your password again here");
        else if (!passwordsMatch) confirmPasswordField.showErrorBubble("The confirmation password doesn't match");

        return everythingValid;
    }
}