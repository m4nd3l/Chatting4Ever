package dev.m4nd3l.chatting4ever.components;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CETextField extends JTextField implements FontGetter {
    private String fontName = defaultFontName;
    private String placeholder = "";
    private String acceptanceRegex = "[\\s\\S]*";
    private ErrorBubble errorPopup;
    private Timer clearErrorTimer;

    public CETextField() { super(); init(); }
    public CETextField(String placeholder) { super(); this.placeholder = placeholder; init(); }
    public CETextField(String placeholder, String acceptanceRegex) { super(); this.placeholder = placeholder; setAcceptanceRegex(acceptanceRegex); init(); }
    public CETextField(int columns, String placeholder) { super(columns); this.placeholder = placeholder; init(); }
    public CETextField(int columns, String placeholder, String acceptanceRegex) { super(columns); this.placeholder = placeholder; setAcceptanceRegex(acceptanceRegex); init(); }
    public CETextField(Document doc, String text, int columns, String placeholder) { super(doc, text, columns); this.placeholder = placeholder; init(); }
    public CETextField(Document doc, String text, int columns, String placeholder, String acceptanceRegex) { super(doc, text, columns); this.placeholder = placeholder; setAcceptanceRegex(acceptanceRegex); init(); }
    public CETextField(String text, String placeholder, String acceptanceRegex) { super(text); this.placeholder = placeholder; setAcceptanceRegex(acceptanceRegex); init(); }
    public CETextField(String text, int columns, String placeholder) { super(text, columns); this.placeholder = placeholder; init(); }
    public CETextField(String text, int columns, String placeholder, String acceptanceRegex) { super(text, columns); this.placeholder = placeholder; setAcceptanceRegex(acceptanceRegex); init(); }

    private void init() {
        resetFontKeepingFontName();
        putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        setupBannedCharsFilter();

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

    public CETextField pressButtonOnEnter(JButton button) { return pressButtonOnEnterIfCondition(button, _ -> true, _ -> { }); }
    public CETextField pressButtonOnEnterIfCondition(JButton button, Predicate<String> condition) { return pressButtonOnEnterIfCondition(button, condition, _ -> { }); }
    public CETextField pressButtonOnEnterIfCondition(JButton button, Predicate<String> condition, Consumer<String> otherwise) {
        return onEnterPressed(password -> {
            if (condition.test(password)) {
                if (button != null) button.doClick();
            } else otherwise.accept(password);
        });
    }

    public CETextField focusOnEnter(JComponent toFocusOnIf) { return focusOnEnterIfCondition(toFocusOnIf, _ -> true, _ -> { }); }
    public CETextField focusOnEnterIfCondition(JComponent toFocusOnIf, Predicate<String> condition) { return focusOnEnterIfCondition(toFocusOnIf, condition, _ -> { }); }
    public CETextField focusOnEnterIfCondition(JComponent toFocusOnIf, Predicate<String> condition, Consumer<String> otherwise) {
        return onEnterPressed(string -> {
            if (condition.test(string)) {
                if (toFocusOnIf != null) toFocusOnIf.requestFocusInWindow();
            } else otherwise.accept(string);
        });
    }

    public CETextField onEnterPressed(Consumer<String> action) {
        for (ActionListener listener : getActionListeners()) removeActionListener(listener);

        addActionListener(_ -> SwingUtilities.invokeLater(() -> action.accept(getText())));
        return this;
    }

    public CETextField onTextChanged(Consumer<String> action) {
        getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent documentEvent) { action.accept(getText()); }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent documentEvent) { action.accept(getText()); }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent documentEvent) { action.accept(getText()); }
        });
        return this;
    }

    public String getAcceptanceRegex() { return acceptanceRegex; }
    public String getPlaceholder() { return placeholder; }
    public String getFontName() { return fontName; }
    public int getFontSize() { return getFont() != null ? getFont().getSize() : 12; }
    public int getFontStyle() { return getFont() != null ? getFont().getStyle() : 0; }

    public CETextField setAcceptanceRegex(String acceptanceRegex) { this.acceptanceRegex = acceptanceRegex; return this; }
    public CETextField resetFont() { setFontName(defaultFontName); setFont(getFont(fontName, -1, -1, getFont())); return this; }
    public CETextField resetFontKeepingFontName() { setFont(getFont(fontName, -1, -1, getFont())); return this; }
    public CETextField setPlaceholder(String placeholder) { this.placeholder = placeholder; repaint(); return this; }
    public CETextField setFontName(String fontName) { this.fontName = fontName; setFont(getFont(fontName, -1, -1, getFont())); return this; }
    public CETextField setFontSize(int size) { setFont(getFont(fontName, -1, size, getFont())); return this; }
    public CETextField setFontStyle(int style) { setFont(getFont(fontName, style, -1, getFont())); return this; }

    private void showBannedCharErrorBubble(String input) {
        String[] invalidChars = new String[input.length()];
        char[] string = input.toCharArray();
        int currentIndex = 0;
        for (char character : string) {
            if (String.valueOf(character).matches(acceptanceRegex)) continue;
            invalidChars[currentIndex] = character == '\n' ? "Enter/Newline" : String.valueOf(character);
            currentIndex++;
        }
        StringBuilder error = new StringBuilder();
        invalidChars = Arrays.copyOf(invalidChars, currentIndex);
        for (int i = 0; i < invalidChars.length; i++) {
            String invalidChar = invalidChars[i];
            if (invalidChar == null || invalidChar.isEmpty()) continue;
            if (i != 0) error.append(", ");
            error.append('\'').append(invalidChar).append('\'');
        }
        error.append(invalidChars.length <= 1 ? " is not allowed!" : " are not allowed!");
        showErrorBubble(error.toString());
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (getPlaceholder() == null || getPlaceholder().isEmpty() || !isNullOrEmpty(getText())) return;

        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        FontMetrics fontMetrics = graphics2D.getFontMetrics();
        int x = getInsets().left;
        int y = (getHeight() - fontMetrics.getHeight()) / 2 + fontMetrics.getAscent();

        graphics2D.drawString(getPlaceholder(), x, y);
    }

    private void setupBannedCharsFilter() {
        if (getDocument() instanceof AbstractDocument)
            ((AbstractDocument) getDocument()).setDocumentFilter(new DocumentFilter() {
                @Override
                public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                    if (containsBannedChars(string)) showBannedCharErrorBubble(string);
                    super.insertString(fb, offset, filterBannedChars(string), attr);
                }

                @Override
                public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                    if (containsBannedChars(text)) showBannedCharErrorBubble(text);
                    super.replace(fb, offset, length, filterBannedChars(text), attrs);
                }
            });
    }

    @Override
    public void paste() {
        try {
            String text = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            if (text != null) replaceSelection(text);
        } catch (Exception e) { super.paste(); }
    }

    private String filterBannedChars(String input) {
        if (input == null) return "";
        StringBuilder buffer = new StringBuilder();
        for (char character : input.toCharArray())
            if (!containsBannedChars(String.valueOf(character))) buffer.append(character);

        return buffer.toString();
    }

    private boolean containsBannedChars(String input) {
        if (input == null) return false;
        return !input.matches(acceptanceRegex);
    }

    private boolean isNullOrEmpty(String string) { return string == null || string.isEmpty(); }
}