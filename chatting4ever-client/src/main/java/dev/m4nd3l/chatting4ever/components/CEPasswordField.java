package dev.m4nd3l.chatting4ever.components;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionListener;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CEPasswordField extends JPasswordField implements FontGetter {
    private String fontName = defaultFontName;
    private String echoChar = String.valueOf(getEchoChar());
    private int placeholderLength = 8;
    private ErrorBubble errorPopup;
    private Timer clearErrorTimer;

    public CEPasswordField() { super(); init();  }
    public CEPasswordField(int placeholderLength) { super(); this.placeholderLength = placeholderLength; init(); }
    public CEPasswordField(int columns, int placeholderLength) { super(columns); this.placeholderLength = placeholderLength; init(); }
    public CEPasswordField(Document doc, String text, int columns, int placeholderLength) { super(doc, text, columns); this.placeholderLength = placeholderLength; init(); }
    public CEPasswordField(String text, int placeholderLength) { super(text); this.placeholderLength = placeholderLength; init(); }
    public CEPasswordField(String text, int columns, int placeholderLength) { super(text, columns); this.placeholderLength = placeholderLength; init(); }

    private void init() {
        resetFontKeepingFontName();
        putClientProperty(FlatClientProperties.STYLE,
                "iconTextGap:10;showRevealButton:true;arc: 15");
        clearErrorTimer = new Timer(2000, event -> {
            putClientProperty(FlatClientProperties.OUTLINE, null);
            setToolTipText(null);
            repaint();
        });
        clearErrorTimer.setRepeats(false);
    }

    public void showErrorBubble(String error) { showErrorBubble(error, 1500, true); }
    public void showErrorBubble(String error, int durationMilliseconds, boolean up) {
        putClientProperty(FlatClientProperties.OUTLINE, FlatClientProperties.OUTLINE_ERROR);

        if (errorPopup == null) errorPopup = new ErrorBubble(this, error, up, durationMilliseconds);
        else errorPopup.add(error, up, durationMilliseconds);
    }

    public CEPasswordField pressButtonOnEnter(JButton button) { return pressButtonOnEnterIfCondition(button, _ -> true, _ -> { }); }
    public CEPasswordField pressButtonOnEnterIfCondition(JButton button, Predicate<String> condition) { return pressButtonOnEnterIfCondition(button, condition, _ -> { }); }
    public CEPasswordField pressButtonOnEnterIfCondition(JButton button, Predicate<String> condition, Consumer<String> otherwise) {
        return onEnterPressed(password -> {
            if (condition.test(password)) {
                if (button != null) button.doClick();
            } else otherwise.accept(password);
        });
    }

    public CEPasswordField focusOnEnter(JComponent toFocusOnIf) { return focusOnEnterIfCondition(toFocusOnIf, _ -> true, _ -> { }); }
    public CEPasswordField focusOnEnterIfCondition(JComponent toFocusOnIf, Predicate<String> condition) { return focusOnEnterIfCondition(toFocusOnIf, condition, _ -> { }); }
    public CEPasswordField focusOnEnterIfCondition(JComponent toFocusOnIf, Predicate<String> condition, Consumer<String> otherwise) {
        return onEnterPressed(string -> {
            if (condition.test(string)) {
                if (toFocusOnIf != null) toFocusOnIf.requestFocusInWindow();
            } else otherwise.accept(string);
        });
    }

    public CEPasswordField onEnterPressed(Consumer<String> action) {
        for (ActionListener listener : getActionListeners()) removeActionListener(listener);

        addActionListener(_ -> SwingUtilities.invokeLater(() -> action.accept(new String(getPassword()))));
        return this;
    }

    public int getPlaceholderLength() { return placeholderLength; }
    public String getFontName() { return fontName; }
    public int getFontSize() { return getFont() != null ? getFont().getSize() : 12; }
    public int getFontStyle() { return getFont() != null ? getFont().getStyle() : 0; }

    public CEPasswordField setEchoChar(String echoChar) { this.echoChar = echoChar; setEchoChar(echoChar.toCharArray()[0]); return this; }
    public CEPasswordField resetFont() { setFontName(defaultFontName); setFont(getFont(fontName, -1, -1, getFont())); return this; }
    public CEPasswordField resetFontKeepingFontName() { setFont(getFont(fontName, -1, -1, getFont())); return this; }
    public CEPasswordField setPlaceholderLength(int placeholderLength) { this.placeholderLength = placeholderLength; repaint(); return this; }
    public CEPasswordField setFontName(String fontName) { this.fontName = fontName; setFont(getFont(fontName, -1, -1, getFont())); return this; }
    public CEPasswordField setFontSize(int size) { setFont(getFont(fontName, -1, size, getFont())); return this; }
    public CEPasswordField setFontStyle(int style) { setFont(getFont(fontName, style, -1, getFont())); return this; }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (placeholderLength <= 0 || !isNullOrEmpty(new String(getPassword()))) return;

        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        FontMetrics fontMetrics = graphics2D.getFontMetrics();
        int x = getInsets().left;
        int y = (getHeight() - fontMetrics.getHeight()) / 2 + fontMetrics.getAscent();

        graphics2D.drawString(echoChar.repeat(placeholderLength), x, y);
    }

    @Override
    public void paste() {
        try { showErrorBubble("Cannot paste in a password field"); }
        catch (Exception e) { super.paste(); }
    }

    private boolean isNullOrEmpty(String string) { return string == null || string.isEmpty(); }
}