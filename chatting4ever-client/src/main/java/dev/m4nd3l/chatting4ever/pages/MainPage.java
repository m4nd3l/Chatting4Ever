package dev.m4nd3l.chatting4ever.pages;

import com.formdev.flatlaf.FlatClientProperties;
import dev.m4nd3l.chatting4ever.account.AccountData;
import dev.m4nd3l.chatting4ever.components.CELabel;

import javax.swing.*;
import java.awt.*;

public class MainPage extends JPanel implements Page {
    private CELabel token;

    public MainPage() { init(); }

    private void init() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        token = new CELabel("Token: " + AccountData.get().getToken());
        token.setFontSize(24);
        token.setFontStyle(Font.BOLD);

        JPanel contentCard = new JPanel(new GridBagLayout());
        contentCard.putClientProperty(FlatClientProperties.STYLE, "background: #1e1e24; arc: 20;");
        contentCard.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(5, 0, 5, 0);
        constraints.gridwidth = 2;
        constraints.gridx = 0;

        constraints.gridy = 0;
        constraints.insets = new Insets(0, 0, 20, 0);
        contentCard.add(token, constraints);

        add(contentCard, new GridBagConstraints());
    }

    @Override
    public JPanel getPanel() { return this; }
}
