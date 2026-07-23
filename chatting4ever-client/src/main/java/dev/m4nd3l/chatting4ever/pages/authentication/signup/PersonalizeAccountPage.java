package dev.m4nd3l.chatting4ever.pages.authentication.signup;

import com.formdev.flatlaf.FlatClientProperties;
import dev.m4nd3l.chatting4ever.Chatting4EverClient;
import dev.m4nd3l.chatting4ever.account.AccountData;
import dev.m4nd3l.chatting4ever.api.APIEndpoints;
import dev.m4nd3l.chatting4ever.api.response.UploadProfileImageResponse;
import dev.m4nd3l.chatting4ever.api.response.data.ErrorData;
import dev.m4nd3l.chatting4ever.components.CELabel;
import dev.m4nd3l.chatting4ever.components.CEProfileImageEditor;
import dev.m4nd3l.chatting4ever.components.CETextField;
import dev.m4nd3l.chatting4ever.pages.MainPage;
import dev.m4nd3l.chatting4ever.pages.Page;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.regex.Pattern;

public class PersonalizeAccountPage extends JPanel implements Page {
    private static final Pattern displayedNamePattern = Pattern.compile("^[^\\x21\\x23-\\x25\\x2E\\x2F\\x3A-\\x3F\\x5B-\\x5E\\x60\\x7B-\\x7E\\n\\r\\t]+$");
    private static final Pattern descriptionPattern = Pattern.compile("^[^\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]*$");

    private CEProfileImageEditor imageEditor;
    private CETextField displayedNameTextField;
    private CETextField descriptionTextField;
    private JCheckBox publicEmailCheckbox;
    private JButton nextButton;

    public PersonalizeAccountPage() { init(); }

    private void init() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        CELabel titleLabel = new CELabel("Customize your account");
        titleLabel.setFontSize(24);
        titleLabel.setFontStyle(Font.BOLD);

        try { imageEditor = new CEProfileImageEditor(ImageIO.read(new URI(AccountData.get().getProfileImageURL()).toURL())); }
        catch (Exception _) { imageEditor = new CEProfileImageEditor(new BufferedImage(980, 980, BufferedImage.TYPE_INT_RGB)); }
        displayedNameTextField = new CETextField(AccountData.get().getUsername(), "^[^\\x21\\x23-\\x25\\x2E\\x2F\\x3A-\\x3F\\x5B-\\x5E\\x60\\x7B-\\x7E\\n\\r\\t]+$");
        displayedNameTextField.setText(displayedNameTextField.getPlaceholder());
        descriptionTextField = new CETextField("Hey there! My name is " + displayedNameTextField.getPlaceholder(), "^[^\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]*$");
        publicEmailCheckbox = new JCheckBox("Public email", false);
        nextButton = new JButton("Next");
        nextButton.putClientProperty(FlatClientProperties.STYLE, "arc: 15; background: #007aff; foreground: #ffffff");

        displayedNameTextField.focusOnEnterIfCondition(descriptionTextField, displayedName -> !isNullOrEmpty(displayedName),
                _ -> displayedNameTextField.showErrorBubble("Cannot use an empty displayed name"));
        displayedNameTextField.onTextChanged(newDisplayedName -> descriptionTextField.setPlaceholder("Hey there! My name is " + newDisplayedName));
        nextButton.addActionListener(_ -> pressedNextButton());

        JPanel contentCard = new JPanel(new GridBagLayout());
        contentCard.putClientProperty(FlatClientProperties.STYLE, "background: #1e1e24; arc: 20;");
        contentCard.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        Dimension fixedSize = new Dimension(300, 35);
        displayedNameTextField.setPreferredSize(fixedSize);
        descriptionTextField.setPreferredSize(fixedSize);
        nextButton.setPreferredSize(new Dimension(300, 40));

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
        constraints.anchor = GridBagConstraints.CENTER;
        contentCard.add(imageEditor, constraints);

        constraints.gridy = 2;
        constraints.anchor = GridBagConstraints.WEST;
        contentCard.add(new CELabel("Displayed Name"), constraints);
        constraints.gridy = 3;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;
        contentCard.add(displayedNameTextField, constraints);

        constraints.gridy = 4;
        constraints.anchor = GridBagConstraints.WEST;
        contentCard.add(new CELabel("Description"), constraints);
        constraints.gridy = 5;
        constraints.anchor = GridBagConstraints.CENTER;
        contentCard.add(descriptionTextField, constraints);

        constraints.gridy = 6;
        constraints.insets = new Insets(10, 0, 10, 0);
        constraints.gridwidth = 1;

        constraints.gridx = 0;
        constraints.weightx = 0.5;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        contentCard.add(publicEmailCheckbox, constraints);

        constraints.gridx = 1;
        constraints.weightx = 0.1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.EAST;
        contentCard.add(nextButton, constraints);

        add(contentCard, new GridBagConstraints());
    }

    @Override
    public JPanel getPanel() { return this; }

    private void pressedNextButton() {
        String displayedName = displayedNameTextField.getText();
        String description = descriptionTextField.getText();
        if (!isEverythingValid(displayedName, description, true)) return;

        String token = AccountData.get().getToken();

        ErrorData changeDisplayedName = null;
        ErrorData changeProfileDescription = null;
        ErrorData changeEmailVisibility = null;

        if (!displayedName.equals(AccountData.get().getDisplayedName()))
            changeDisplayedName = changeDisplayedName(token, displayedName);
        if (!isNullOrEmpty(description) || !description.equals(AccountData.get().getProfileDescription()))
            changeProfileDescription = changeProfileDescription(token, description);
        if (publicEmailCheckbox.isSelected())
            changeEmailVisibility = changeEmailVisibility(token, true);

        if (changeDisplayedName != null) showError( "An error occurred while changing displayed name:\n" + changeDisplayedName.getError());
        if (changeProfileDescription != null) showError("An error occurred while changing profile description:\n" + changeProfileDescription.getError());
        if (changeEmailVisibility != null) showError("An error occurred while changing email visibility:\n" + changeEmailVisibility.getError());

        UploadProfileImageResponse uploadProfileImageResponse = uploadProfileImage(token, imageEditor.saveToTemp());
        if (uploadProfileImageResponse.getErrorData() != null) {
            showError("An error occurred while uploading profile image:\n" + uploadProfileImageResponse.getErrorData().getError());
            changePage(new MainPage());
            return;
        }

        String url = uploadProfileImageResponse.getUrl();

        ErrorData changeProfileImage = changeProfileImage(token, url);
        if (changeProfileImage != null) showError("An error occurred while changing profile image:\n" + changeProfileImage.getError());

        changePage(new MainPage());
    }

    private boolean isEverythingValid(String displayedName, String description, boolean showBubbles) {
        boolean displayedNameValid = !isNullOrEmpty(displayedName) && displayedNamePattern.matcher(displayedName).matches();

        boolean descriptionValid = isNullOrEmpty(description) || descriptionPattern.matcher(description).matches();

        boolean everythingValid = displayedNameValid && descriptionValid;

        if (!showBubbles) return everythingValid;

        if (!displayedNameValid) displayedNameTextField.showErrorBubble("Cannot use empty or invalid displayed name");
        if (descriptionValid) descriptionTextField.showErrorBubble("Description is not valid");

        return everythingValid;
    }
}