package dev.m4nd3l.chatting4ever.components;

import javax.swing.*;
import java.awt.*;

public class ErrorBubble {
    private final JComponent target;
    private final JWindow popup;
    private final JLabel label;

    public ErrorBubble(JComponent target, String message, boolean pointUp, int duration) {
        this.target = target;
        this.popup = new JWindow();
        this.label = new JLabel("<html>" + message.replace("\n", "<br>") + "</html>");

        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        label.setBackground(new Color(255, 82, 82));
        label.setOpaque(true);
        label.setForeground(Color.WHITE);
        popup.add(label);
        popup.pack();

        show(pointUp, duration);
    }

    public void add(String message, boolean pointUp, int duration) {
        if (popup.isVisible()) return;

        label.setText("<html>" + message.replace("\n", "<br>") + "</html>");
        popup.pack();
        show(pointUp, duration);
    }

    private void show(boolean pointUp, int duration) {
        Point location = target.getLocationOnScreen();

        int y = pointUp ? location.y + target.getHeight() : location.y - popup.getHeight();
        popup.setLocation(location.x, y);
        popup.setVisible(true);

        Timer timer = new Timer(duration, e -> popup.dispose());
        timer.setRepeats(false);
        timer.start();
    }
}