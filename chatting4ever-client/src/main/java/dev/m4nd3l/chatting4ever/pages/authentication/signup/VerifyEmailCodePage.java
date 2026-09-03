package dev.m4nd3l.chatting4ever.pages.authentication.signup;

import com.formdev.flatlaf.FlatClientProperties;
import dev.m4nd3l.chatting4ever.account.AccountData;
import dev.m4nd3l.chatting4ever.api.response.data.ErrorData;
import dev.m4nd3l.chatting4ever.components.*;
import dev.m4nd3l.chatting4ever.pages.MainPage;
import dev.m4nd3l.chatting4ever.pages.Page;

import javax.swing.*;
import java.awt.*;

public class VerifyEmailCodePage extends JPanel implements Page {
    private CEDigitsField verificationCodeField;
    private CEButton resendButton;
    private CEButton skipButton;
    private CEButton nextButton;

    public VerifyEmailCodePage() { init(); }

    private void init() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        CELabel titleLabel = new CELabel("Verify your email");
        titleLabel.setFontSize(24);
        titleLabel.setFontStyle(Font.BOLD);

        verificationCodeField = new CEDigitsField(6, true);
        resendButton = new CEButton("Resend code");
        skipButton = new CEButton("Skip for now");
        nextButton = new CEButton("Next");

        resendButton.addActionListener(_ -> resendEmailVerificationCode(AccountData.get().getToken()));
        skipButton.addActionListener(_ -> changePage(new MainPage()));
        nextButton.addActionListener(_ -> pressedNextButton());

        JPanel contentCard = new JPanel(new GridBagLayout());
        contentCard.putClientProperty(FlatClientProperties.STYLE, "background: #1e1e24; arc: 20;");
        contentCard.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        Dimension fixedSize = new Dimension(300, 35);
        resendButton.setPreferredSize(new Dimension(100, 40));
        skipButton.setPreferredSize(new Dimension(100, 40));
        nextButton.setPreferredSize(fixedSize);

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
        contentCard.add(new CELabel("Verification Code"), constraints);
        constraints.gridy = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        contentCard.add(verificationCodeField, constraints);

        constraints.gridy = 3;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.weightx = 0.5;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        contentCard.add(resendButton, constraints);

        constraints.gridx = 1;
        constraints.weightx = 0.5;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.EAST;
        contentCard.add(skipButton, constraints);

        constraints.gridy = 4;
        constraints.gridx = 0;
        constraints.weightx = 0;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        contentCard.add(nextButton, constraints);

        add(contentCard, new GridBagConstraints());
    }

    @Override
    public JPanel getPanel() { return this; }

    private void pressedNextButton() {
        nextButton.setEnabled(false);

        String code = verificationCodeField.getNumber();
        if (!isEverythingValid(code, true)) {
            nextButton.setEnabled(true);
            return;
        }

        String token = AccountData.get().getToken();

        ErrorData verifyCodeErrorData = verifyEmail(token, code);

        if (verifyCodeErrorData == null) {
            nextButton.setEnabled(true);
            AccountData.get().setVerifiedEmail(true);
            changePage(new PersonalizeAccountPage());
            return;
        }

        nextButton.setEnabled(true);
        showError(verifyCodeErrorData.getError());
    }

    private boolean isEverythingValid(String code, boolean showBubbles) {
        boolean codeValid = !isNullOrEmpty(code) && code.matches("^\\d{6}$");
        if (showBubbles && isNullOrEmpty(code)) showError("Invalid code");
        return codeValid;
    }
}