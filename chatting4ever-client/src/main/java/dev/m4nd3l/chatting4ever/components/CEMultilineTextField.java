package dev.m4nd3l.chatting4ever.components;

import com.formdev.flatlaf.FlatClientProperties;
import org.intellij.lang.annotations.RegExp;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.util.Arrays;
import java.util.function.Consumer;

public class CEMultilineTextField extends JPanel implements FontGetter {
    private final JTextArea textArea;
    private final JScrollPane scrollPane;
    private final JLabel counterLabel;

    private int maxChars = -1;
    private int maxLines = 10;
    private String fontName = defaultFontName;
    private String placeholder = "";
    private String acceptanceRegex = "[\\s\\S]*";
    private ErrorBubble errorPopup;
    private Timer clearErrorTimer;

    public CEMultilineTextField() {
        super(new BorderLayout());

        textArea = new JTextArea() {
            @Override
            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                if (getPlaceholder() == null || getPlaceholder().isEmpty() || !isNullOrEmpty(getText())) return;

                Graphics2D graphics2D = (Graphics2D) graphics;
                graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                FontMetrics fontMetrics = graphics2D.getFontMetrics();
                int x = getInsets().left;
                int y = fontMetrics.getAscent() + getInsets().top;

                graphics2D.drawString(getPlaceholder(), x, y);
            }
        };

        scrollPane = new JScrollPane(textArea);
        counterLabel = new JLabel();

        init();
    }

    private void init() {
        resetFontKeepingFontName();
        putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        scrollPane.putClientProperty(FlatClientProperties.STYLE, "arc: 15");

        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setOpaque(false);

        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        counterLabel.setFont(getFont().deriveFont(10f));
        counterLabel.setForeground(UIManager.getColor("textInactiveText"));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 2));
        bottomPanel.setOpaque(false);
        bottomPanel.add(counterLabel);

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setupFilters();
        updateCounter();

        clearErrorTimer = new Timer(2000, event -> {
            putClientProperty(FlatClientProperties.OUTLINE, null);
            setToolTipText(null);
            repaint();
        });
        clearErrorTimer.setRepeats(false);
    }

    public CEMultilineTextField setMaxChars(int maxChars) {
        this.maxChars = maxChars;
        updateCounter();
        return this;
    }

    public CEMultilineTextField setMaxLines(int maxLines) {
        this.maxLines = maxLines;
        updatePreferredSize();
        return this;
    }

    private void updatePreferredSize() {
        FontMetrics fontMetrics = textArea.getFontMetrics(textArea.getFont());
        int fontHeight = fontMetrics.getHeight();
        int targetLines = Math.min(maxLines, 10);
        int calculatedHeight = (fontHeight * targetLines)
                + counterLabel.getFontMetrics(counterLabel.getFont()).getHeight();

        textArea.setRows(targetLines);
        scrollPane.setPreferredSize(new Dimension(0, calculatedHeight));
        revalidate();
    }

    private void updateCounter() {
        int currentLength = textArea.getText().length();
        if (maxChars > 0) counterLabel.setText(currentLength + "/" + maxChars);
        else counterLabel.setText(String.valueOf(currentLength));
    }

    public void showErrorBubble(String error) { showErrorBubble(error, 1500, true); }
    public void showErrorBubble(String error, int durationMilliseconds, boolean up) {
        putClientProperty(FlatClientProperties.OUTLINE, FlatClientProperties.OUTLINE_ERROR);
        if (errorPopup == null) errorPopup = new ErrorBubble(this, error, up, durationMilliseconds);
        else errorPopup.add(error, up, durationMilliseconds);
    }

    public CEMultilineTextField onTextChanged(Consumer<String> action) {
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) { handleTextChange(action); }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) { handleTextChange(action); }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) { handleTextChange(action); }
        });
        return this;
    }

    private void handleTextChange(Consumer<String> action) {
        updateCounter();
        action.accept(textArea.getText());
    }

    public String getText() { return textArea.getText(); }
    public void setText(String text) {
        textArea.setText(text);
        updateCounter();
    }

    public String getAcceptanceRegex() { return acceptanceRegex; }
    public String getPlaceholder() { return placeholder; }
    public String getFontName() { return fontName; }
    public int getFontSize() { return textArea.getFont() != null ? textArea.getFont().getSize() : 12; }
    public int getFontStyle() { return textArea.getFont() != null ? textArea.getFont().getStyle() : 0; }

    public CEMultilineTextField setAcceptanceRegex(@RegExp String acceptanceRegex) { this.acceptanceRegex = acceptanceRegex; return this; }
    public CEMultilineTextField resetFont() { setFontName(defaultFontName); setFont(getFont(fontName, -1, -1, textArea.getFont())); return this; }
    public CEMultilineTextField resetFontKeepingFontName() { setFont(getFont(fontName, -1, -1, textArea.getFont())); return this; }
    public CEMultilineTextField setPlaceholder(String placeholder) { this.placeholder = placeholder; repaint(); return this; }
    public CEMultilineTextField setFontName(String fontName) { this.fontName = fontName; setFont(getFont(fontName, -1, -1, textArea.getFont())); return this; }
    public CEMultilineTextField setFontSize(int size) { setFont(getFont(fontName, -1, size, textArea.getFont())); updatePreferredSize(); return this; }
    public CEMultilineTextField setFontStyle(int style) { setFont(getFont(fontName, style, -1, textArea.getFont())); updatePreferredSize(); return this; }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        if (textArea != null) textArea.setFont(font);
    }

    private void setupFilters() {
        AbstractDocument document = (AbstractDocument) textArea.getDocument();
        document.setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;

                if (containsBannedChars(string)) {
                    showBannedCharErrorBubble(string);
                    return;
                }

                String filtered = filterBannedChars(string);

                if (maxLines > 0 && filtered.contains("\n")) {
                    int currentLines = textArea.getLineCount();
                    int addedLines = countNewlines(filtered);
                    if (currentLines + addedLines > maxLines) return;
                }

                if (maxChars > 0) {
                    int currentLength = fb.getDocument().getLength();
                    if (currentLength + filtered.length() > maxChars) {
                        int allowedLength = maxChars - currentLength;
                        if (allowedLength > 0) filtered = filtered.substring(0, allowedLength);
                        else return;
                    }
                }

                super.insertString(fb, offset, filtered, attr);
                updateCounter();
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) return;

                if (containsBannedChars(text)) {
                    showBannedCharErrorBubble(text);
                    return;
                }

                String filtered = filterBannedChars(text);

                if (maxLines > 0 && filtered.contains("\n")) {
                    int currentLines = textArea.getLineCount();
                    int removedTextLines = countNewlines(fb.getDocument().getText(offset, length));
                    int addedLines = countNewlines(filtered);
                    if (currentLines - removedTextLines + addedLines > maxLines) return;

                }

                if (maxChars > 0) {
                    int currentLength = fb.getDocument().getLength() - length;
                    if (currentLength + filtered.length() > maxChars) {
                        int allowedLength = maxChars - currentLength;
                        if (allowedLength > 0) filtered = filtered.substring(0, allowedLength);
                        else return;
                    }
                }

                super.replace(fb, offset, length, filtered, attrs);
                updateCounter();
            }

            @Override
            public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                super.remove(fb, offset, length);
                updateCounter();
            }
        });
    }

    private int countNewlines(String input) {
        int count = 0;
        for (char character : input.toCharArray()) {
            if (character == '\n') count++;
        }
        return count;
    }

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
}