package dev.m4nd3l.chatting4ever.components;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.util.Arrays;
import java.util.function.Consumer;

public class CEMultilineTextField extends JScrollPane implements FontGetter {
    private final InternalTextArea textArea;
    private String fontName = defaultFontName;
    private String placeholder = "";
    private String acceptanceRegex = "[\\s\\S]*";
    private ErrorBubble errorPopup;
    private Timer clearErrorTimer;
    private int maxChars = 0;
    private int maxLines = 10;

    public CEMultilineTextField() {
        textArea = new InternalTextArea();
        setViewportView(textArea);
        putClientProperty(FlatClientProperties.STYLE, "arc: 15");

        init();
    }

    private void init() {
        textArea.resetFontKeepingFontName();
        setupDocumentFilters();
        updateScrollbarPolicy();

        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) { updateScrollbarPolicy(); }
            @Override
            public void removeUpdate(DocumentEvent documentEvent) { updateScrollbarPolicy(); }
            @Override
            public void changedUpdate(DocumentEvent documentEvent) { updateScrollbarPolicy(); }
        });

        clearErrorTimer = new Timer(2000, _ -> {
            putClientProperty(FlatClientProperties.OUTLINE, null);
            setToolTipText(null);
            repaint();
        });
        clearErrorTimer.setRepeats(false);
    }

    private void updateScrollbarPolicy() {
        if (maxLines <= 10) {
            setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        } else {
            if (textArea.getLineCount() > 10) setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            else setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        }
    }

    public String getText() { return textArea.getText(); }
    public void setText(String text) { textArea.setText(text); }
    public void replaceSelection(String content) { textArea.replaceSelection(content); }

    public void showErrorBubble(String error) { showErrorBubble(error, 1500, true); }
    public void showErrorBubble(String error, int durationMilliseconds, boolean up) {
        putClientProperty(FlatClientProperties.OUTLINE, FlatClientProperties.OUTLINE_ERROR);

        if (errorPopup == null) errorPopup = new ErrorBubble(this, error, up, durationMilliseconds);
        else errorPopup.add(error, up, durationMilliseconds);
    }

    public CEMultilineTextField onTextChanged(Consumer<String> action) {
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) { action.accept(getText()); }
            @Override
            public void removeUpdate(DocumentEvent documentEvent) { action.accept(getText()); }
            @Override
            public void changedUpdate(DocumentEvent documentEvent) { action.accept(getText()); }
        });
        return this;
    }

    public String getAcceptanceRegex() { return acceptanceRegex; }
    public String getPlaceholder() { return placeholder; }
    public String getFontName() { return fontName; }
    public int getMaxChars() { return maxChars; }
    public int getMaxLines() { return maxLines; }
    public int getFontSize() { return textArea.getFont() != null ? textArea.getFont().getSize() : 12; }
    public int getFontStyle() { return textArea.getFont() != null ? textArea.getFont().getStyle() : 0; }

    public CEMultilineTextField setAcceptanceRegex(String acceptanceRegex) { this.acceptanceRegex = acceptanceRegex; return this; }
    public CEMultilineTextField setMaxChars(int maxChars) { this.maxChars = maxChars; repaint(); return this; }
    public CEMultilineTextField setMaxLines(int maxLines) {
        this.maxLines = maxLines - 1;
        updateScrollbarPolicy();
        revalidate();
        return this;
    }
    public CEMultilineTextField resetFont() { setFontName(defaultFontName); return this; }
    public CEMultilineTextField resetFontKeepingFontName() { textArea.resetFontKeepingFontName(); return this; }
    public CEMultilineTextField setPlaceholder(String placeholder) { this.placeholder = placeholder; textArea.repaint(); return this; }
    public CEMultilineTextField setFontName(String fontName) { this.fontName = fontName; textArea.setFontName(fontName); return this; }
    public CEMultilineTextField setFontSize(int size) { textArea.setFont(getFont(getFontName(), size, getFontStyle(), getFont())); return this; }
    public CEMultilineTextField setFontStyle(int style) { textArea.setFont(getFont(getFontName(), getFontSize(), style, getFont())); return this; }

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

    private void setupDocumentFilters() {
        if (textArea.getDocument() instanceof AbstractDocument) {
            ((AbstractDocument) textArea.getDocument()).setDocumentFilter(new DocumentFilter() {
                @Override
                public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                    if (string == null) return;
                    if (containsBannedChars(string)) showBannedCharErrorBubble(string);

                    String filtered = filterBannedChars(string);

                    if (maxLines > 0) {
                        int currentLines = textArea.getLineCount();
                        int addedLines = countNewlines(filtered);
                        if (currentLines + addedLines > maxLines) {
                            int allowedNewlines = maxLines - currentLines;
                            filtered = truncateToMaxLines(filtered, Math.max(0, allowedNewlines));
                        }
                    }
                    if (maxChars > 0) {
                        int currentLength = fb.getDocument().getLength();
                        int allowedLength = maxChars - currentLength;
                        if (allowedLength <= 0) return;
                        if (filtered.length() > allowedLength) filtered = filtered.substring(0, allowedLength);
                    }

                    if (!filtered.isEmpty()) {
                        super.insertString(fb, offset, filtered, attr);
                        textArea.repaint();
                        revalidate();
                    }
                }

                @Override
                public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                    if (text == null) text = "";
                    if (containsBannedChars(text)) showBannedCharErrorBubble(text);

                    String filtered = filterBannedChars(text);

                    if (maxLines > 0) {
                        String fullText = fb.getDocument().getText(0, fb.getDocument().getLength());
                        String textWithoutSelection = fullText.substring(0, offset) + fullText.substring(offset + length);
                        int linesWithoutSelection = countNewlines(textWithoutSelection) + 1;
                        int addedLines = countNewlines(filtered);

                        if (linesWithoutSelection + addedLines - 1 > maxLines) {
                            int allowedLines = maxLines - linesWithoutSelection + 1;
                            filtered = truncateToMaxLines(filtered, Math.max(0, allowedLines));
                        }
                    }

                    if (maxChars > 0) {
                        int currentLength = fb.getDocument().getLength();
                        int allowedLength = maxChars - (currentLength - length);
                        if (allowedLength <= 0 && length == 0) return;
                        if (filtered.length() > allowedLength) {
                            filtered = filtered.substring(0, Math.max(0, allowedLength));
                        }
                    }

                    super.replace(fb, offset, length, filtered, attrs);
                    textArea.repaint();
                    revalidate();
                }

                @Override
                public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                    super.remove(fb, offset, length);
                    textArea.repaint();
                    revalidate();
                }
            });
        }
    }

    private int countNewlines(String input) {
        int count = 0;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '\n') count++;
        }
        return count;
    }

    private String truncateToMaxLines(String input, int allowedNewlines) {
        if (allowedNewlines <= 0) return "";
        int newlinesFound = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '\n') {
                if (newlinesFound >= allowedNewlines) break;
                newlinesFound++;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public void paste() {
        textArea.paste();
    }

    private String filterBannedChars(String input) {
        if (input == null) return "";
        StringBuilder buffer = new StringBuilder();
        for (char character : input.toCharArray()) {
            if (!containsBannedChars(String.valueOf(character))) buffer.append(character);
        }
        return buffer.toString();
    }

    private boolean containsBannedChars(String input) {
        if (input == null) return false;
        return !input.matches(acceptanceRegex);
    }

    private boolean isNullOrEmpty(String string) { return string == null || string.isEmpty(); }

    private class InternalTextArea extends JTextArea implements FontGetter {
        public void resetFontKeepingFontName() { setFont(getFont(fontName, -1, -1, getFont())); }
        public void setFontName(String name) { fontName = name; setFont(getFont(fontName, -1, -1, getFont())); }
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension dimensions = super.getPreferredSize();
        FontMetrics fontMetrics = textArea.getFontMetrics(textArea.getFont());
        int lineHeight = fontMetrics.getHeight();
        int targetLines = maxLines <= 10 ? maxLines : Math.min(textArea.getLineCount(), 10);
        dimensions.height = (lineHeight * targetLines) + getViewport().getInsets().top + getViewport().getInsets().bottom + 6;
        return dimensions;
    }
}