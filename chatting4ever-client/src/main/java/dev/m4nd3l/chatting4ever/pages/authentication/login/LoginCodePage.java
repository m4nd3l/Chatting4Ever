package dev.m4nd3l.chatting4ever.pages.authentication.login;

import com.formdev.flatlaf.FlatClientProperties;
import dev.m4nd3l.chatting4ever.account.AccountData;
import dev.m4nd3l.chatting4ever.api.payloads.account.LoginPayload;
import dev.m4nd3l.chatting4ever.api.response.auth.AccountAuthResponse;
import dev.m4nd3l.chatting4ever.components.CEButton;
import dev.m4nd3l.chatting4ever.components.CEDigitsField;
import dev.m4nd3l.chatting4ever.components.CELabel;
import dev.m4nd3l.chatting4ever.pages.MainPage;
import dev.m4nd3l.chatting4ever.pages.Page;

import javax.swing.*;
import java.awt.*;

public class LoginCodePage extends JPanel implements Page {
    private LoginPayload loginData;
    private boolean handOutUUID;

    private CEDigitsField loginCodeField;
    private CEButton resendButton;
    private CEButton loginButton;

    public LoginCodePage(LoginPayload payloadForResend) { this.loginData = payloadForResend; this.handOutUUID = loginData.handOutToken(); init(); }

    private void init() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        CELabel titleLabel = new CELabel("2FA Authentication");
        titleLabel.setFontSize(24);
        titleLabel.setFontStyle(Font.BOLD);

        loginCodeField = new CEDigitsField(6, true);
        resendButton = new CEButton("Resend code");
        loginButton = new CEButton("Login");

        resendButton.addActionListener(_ -> login(loginData.getUsernameOrEmail(), loginData.getPassword(), handOutUUID));
        loginButton.addActionListener(_ -> pressedLoginButton());

        JPanel contentCard = new JPanel(new GridBagLayout());
        contentCard.putClientProperty(FlatClientProperties.STYLE, "background: #1e1e24; arc: 20;");
        contentCard.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        Dimension fixedSize = new Dimension(300, 35);
        resendButton.setPreferredSize(new Dimension(100, 40));
        loginButton.setPreferredSize(fixedSize);

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
        contentCard.add(new CELabel("Login Code"), constraints);
        constraints.gridy = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        contentCard.add(loginCodeField, constraints);

        constraints.gridy = 3;
        contentCard.add(resendButton, constraints);

        constraints.gridy = 4;
        contentCard.add(loginButton, constraints);

        add(contentCard, new GridBagConstraints());
    }

    @Override
    public JPanel getPanel() { return this; }

    private void pressedLoginButton() {
        loginButton.setEnabled(false);
        String code = loginCodeField.getNumber();
        if (!isEverythingValid(code, true)) {
            loginButton.setEnabled(true);
            return;
        }

        AccountAuthResponse response = getDataLogin(loginData, code, handOutUUID);

        if (!response.wasSuccessful()) {
            showError(response.getErrorCause());
            loginButton.setEnabled(true);
            return;
        }

        if (response.has2FA()) {
            if (response.getErrorCause().equals("Unknown")) showError("An error occurred, try later");
            else showError("An error occurred: " + response.getErrorCause());
            loginButton.setEnabled(true);
            return;
        }

        AccountData.setAccount(response);
        loginButton.setEnabled(true);
        changePage(new MainPage());
    }

    private boolean isEverythingValid(String code, boolean showBubbles) {
        boolean codeValid = !isNullOrEmpty(code) && code.matches("^\\d{6}$");
        if (showBubbles && isNullOrEmpty(code)) showError("Invalid code");
        return codeValid;
    }
}