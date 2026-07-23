package dev.m4nd3l.chatting4ever.pages.authentication;

import com.formdev.flatlaf.FlatClientProperties;
import dev.m4nd3l.chatting4ever.Chatting4EverClient;
import dev.m4nd3l.chatting4ever.account.AccountData;
import dev.m4nd3l.chatting4ever.api.APIEndpoints;
import dev.m4nd3l.chatting4ever.api.payloads.account.LoginPayload;
import dev.m4nd3l.chatting4ever.api.response.TokenAndInfoResponse;
import dev.m4nd3l.chatting4ever.components.CELabel;
import dev.m4nd3l.chatting4ever.components.CEPasswordField;
import dev.m4nd3l.chatting4ever.components.CETextField;
import dev.m4nd3l.chatting4ever.pages.MainPage;
import dev.m4nd3l.chatting4ever.pages.Page;
import dev.m4nd3l.chatting4ever.pages.authentication.signup.SignupPage;
import dev.m4nd3l.easysaves.EasySaves;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.regex.Pattern;

public class LoginPage extends JPanel implements Page {
    private static final Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private CETextField usernameOrEmailField;
    private CEPasswordField passwordField;
    private JCheckBox saveCredentialsCheckbox;
    private JButton signupInsteadButton;
    private JButton loginButton;

    public LoginPage() { init(); }

    private void init() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        CELabel titleLabel = new CELabel("Login");
        titleLabel.setFontSize(24);
        titleLabel.setFontStyle(Font.BOLD);

        usernameOrEmailField = new CETextField("john.smith_", "[a-zA-Z0-9_.-@]+");
        passwordField = new CEPasswordField(8);

        signupInsteadButton = new JButton("Don't have an account?");
        signupInsteadButton.putClientProperty(FlatClientProperties.STYLE, "borderWidth: 0; focusWidth: 0; background: null; foreground: #007aff");
        signupInsteadButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        saveCredentialsCheckbox = new JCheckBox("Save credentials", true);

        loginButton = new JButton("Login");
        loginButton.putClientProperty(FlatClientProperties.STYLE, "arc: 15; background: #007aff; foreground: #ffffff");

        usernameOrEmailField.focusOnEnterIfCondition(passwordField, text -> {
                    if (isNullOrEmpty(text)) return false;
                    if (text.contains("@")) return emailPattern.matcher(text).matches();
                    return true;
                },
                _ -> usernameOrEmailField.showErrorBubble("Cannot submit empty data or invalid emails"));
        passwordField.pressButtonOnEnterIfCondition(loginButton,
                Objects::nonNull, _ -> passwordField.showErrorBubble("Cannot submit an empty password"));
        signupInsteadButton.addActionListener(_ -> switchToSignupScreen());
        loginButton.addActionListener(_ -> pressedLoginButton());

        JPanel contentCard = new JPanel(new GridBagLayout());
        contentCard.putClientProperty(FlatClientProperties.STYLE, "background: #1e1e24; arc: 20;");
        contentCard.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        Dimension fixedSize = new Dimension(300, 35);
        usernameOrEmailField.setPreferredSize(fixedSize);
        passwordField.setPreferredSize(fixedSize);
        loginButton.setPreferredSize(new Dimension(300, 40));

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
        contentCard.add(new CELabel("Username or Email"), constraints);
        constraints.gridy = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        contentCard.add(usernameOrEmailField, constraints);

        constraints.gridy = 3;
        constraints.anchor = GridBagConstraints.WEST;
        contentCard.add(new CELabel("Password"), constraints);
        constraints.gridy = 4;
        constraints.anchor = GridBagConstraints.CENTER;
        contentCard.add(passwordField, constraints);

        constraints.gridy = 5;
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
        contentCard.add(signupInsteadButton, constraints);

        constraints.gridx = 0;
        constraints.gridwidth = 2;
        constraints.weightx = 0.0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridy = 6;
        constraints.insets = new Insets(10, 0, 0, 0);
        contentCard.add(loginButton, constraints);

        add(contentCard, new GridBagConstraints());
    }

    private void switchToSignupScreen() { changePage(new SignupPage()); }

    @Override
    public JPanel getPanel() { return this; }

    private void pressedLoginButton() {
        loginButton.setEnabled(false);
        String usernameOrEmail = usernameOrEmailField.getText();
        String password = new String(passwordField.getPassword());
        if (!isEverythingValid(usernameOrEmail, password, true)) return;

        try {
            TokenAndInfoResponse data = APIEndpoints.login.sendPostRequest(new LoginPayload(usernameOrEmail, password), TokenAndInfoResponse.class);
            if (data.getErrorData() == null) {
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
                    EasySaves.addSecureSetting(usernameOrEmail.contains("@") ? "email" : "username", usernameOrEmail);
                    EasySaves.removeSetting(usernameOrEmail.contains("@") ? "username" : "email");
                    EasySaves.addSecureSetting("password", password);
                } else {
                    EasySaves.removeSetting("username");
                    EasySaves.removeSetting("email");
                    EasySaves.removeSetting("password");
                }
            } else showError("An error occurred while logging in:\n" + data.getErrorData().getError());
        } catch (Exception e) { showError("An error occurred while logging in, retry later"); }

        TokenAndInfoResponse data = login(usernameOrEmail, password);
        if (data.getErrorData() != null) {
            showError("An error occurred while changing email visibility:\n" + data.getErrorData().getError());
            loginButton.setEnabled(true);
            return;
        }

        loginButton.setEnabled(true);
        changePage(new MainPage());
    }

    private boolean isEverythingValid(String usernameOrEmail, String password, boolean showBubbles) {
        boolean isEmail = usernameOrEmail != null && usernameOrEmail.contains("@");

        boolean usernameOrEmailValid = !isNullOrEmpty(usernameOrEmail);
        boolean usernameOrEmailExists = usernameOrEmailValid && (isEmail ? isEmailTaken(usernameOrEmail) : isUsernameTaken(usernameOrEmail));

        boolean passwordValid = !isNullOrEmpty(password);

        boolean everythingValid = usernameOrEmailValid && usernameOrEmailExists && passwordValid;

        if (!showBubbles) return everythingValid;

        String name = isEmail ? "email" : "username";

        if (!usernameOrEmailValid) usernameOrEmailField.showErrorBubble("Cannot submit an empty " + name);
        if (!usernameOrEmailExists)
            usernameOrEmailField.showErrorBubble("Couldn't find a user with " + name + " '" + usernameOrEmail + "'");

        if (!passwordValid) passwordField.showErrorBubble("Cannot submit null or empty password");

        return everythingValid;
    }
}