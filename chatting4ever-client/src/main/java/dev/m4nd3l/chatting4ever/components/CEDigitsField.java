package dev.m4nd3l.chatting4ever.components;

import dev.m4nd3l.chatting4ever.Chatting4EverClient;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CEDigitsField extends JComponent implements FontGetter {
    private String fontName = defaultFontName;
    private final int numberOfDigits;
    private final boolean allowPaste;
    private final CETextField[] digitFields;

    public CEDigitsField(int numberOfDigits, boolean allowPaste) {
        this.numberOfDigits = numberOfDigits;
        this.allowPaste = allowPaste;
        this.digitFields = new CETextField[numberOfDigits];

        setLayout(new GridLayout(1, numberOfDigits, 10, 0));
        initComponent();
    }

    public void showErrorBubble(String error) { Chatting4EverClient.Window.error(error); }

    private void initComponent() {
        resetFontKeepingFontName();
        for (int i = 0; i < numberOfDigits; i++) {
            final int index = i;
            CETextField field = new CETextField();
            field.setColumns(1);
            field.setPlaceholder(String.valueOf(i));
            field.setHorizontalAlignment(JTextField.CENTER);
            field.setDocument(new PlainDocument() {
                @Override
                public void insertString(int offset, String string, AttributeSet attributeSet) throws BadLocationException {
                    if (string == null) return;

                    if (allowPaste && string.length() > 1) {
                        handlePaste(string);
                        return;
                    }

                    if (getLength() + string.length() <= 1 && string.matches("\\d")) super.insertString(offset, string, attributeSet);
                }
            });

            field.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent keyEvent) {
                    if (keyEvent.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                        if (field.getText().isEmpty() && index > 0) {
                            digitFields[index - 1].requestFocus();
                            digitFields[index - 1].setText("");
                        } else field.setText("");

                        keyEvent.consume();
                    } else if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT && index > 0) digitFields[index - 1].requestFocus();
                    else if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT && index < numberOfDigits - 1) digitFields[index + 1].requestFocus();
                }

                @Override
                public void keyTyped(KeyEvent keyEvent) {
                    char character = keyEvent.getKeyChar();
                    if (Character.isDigit(character)) {
                        field.setText(String.valueOf(character));
                        if (index < numberOfDigits - 1) digitFields[index + 1].requestFocus();
                        keyEvent.consume();
                    } else keyEvent.consume();
                }
            });

            field.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent focusEvent) { field.selectAll(); }
            });

            digitFields[i] = field;
            add(field);
        }
    }

    private void handlePaste(String text) {
        String digitsOnly = text.replaceAll("\\D", "");
        int length = Math.min(digitsOnly.length(), numberOfDigits);
        for (int i = 0; i < length; i++) digitFields[i].setText(String.valueOf(digitsOnly.charAt(i)));

        if (length < numberOfDigits) digitFields[length].requestFocus();
        else digitFields[numberOfDigits - 1].requestFocus();
    }

    public String getNumber() {
        StringBuilder stringBuilder = new StringBuilder();
        for (JTextField field : digitFields) stringBuilder.append(field.getText());
        return stringBuilder.toString();
    }

    public CEDigitsField pressButtonOnEnter(JButton button) { return pressButtonOnEnterIfCondition(button, _ -> true, _ -> { }); }
    public CEDigitsField pressButtonOnEnterIfCondition(JButton button, Predicate<String> condition) { return pressButtonOnEnterIfCondition(button, condition, _ -> { }); }
    public CEDigitsField pressButtonOnEnterIfCondition(JButton button, Predicate<String> condition, Consumer<String> otherwise) {
        return onEnterPressed(password -> {
            if (condition.test(password)) {
                if (button != null) button.doClick();
            } else otherwise.accept(password);
        });
    }

    public CEDigitsField focusOnEnter(JComponent toFocusOnIf) { return focusOnEnterIfCondition(toFocusOnIf, _ -> true, _ -> { }); }
    public CEDigitsField focusOnEnterIfCondition(JComponent toFocusOnIf, Predicate<String> condition) { return focusOnEnterIfCondition(toFocusOnIf, condition, _ -> { }); }
    public CEDigitsField focusOnEnterIfCondition(JComponent toFocusOnIf, Predicate<String> condition, Consumer<String> otherwise) {
        return onEnterPressed(string -> {
            if (condition.test(string)) {
                if (toFocusOnIf != null) toFocusOnIf.requestFocusInWindow();
            } else otherwise.accept(string);
        });
    }

    public CEDigitsField onEnterPressed(Consumer<String> action) { digitFields[numberOfDigits - 1].onEnterPressed(action); return this; }

    public String getFontName() { return fontName; }
    public int getFontSize() { return getFont() != null ? getFont().getSize() : 12; }
    public int getFontStyle() { return getFont() != null ? getFont().getStyle() : 0; }
    public int getNumberOfDigits() { return numberOfDigits; }
    public boolean isAllowPaste() { return allowPaste; }

    public CEDigitsField resetFont() { setFontName(defaultFontName); setFont(getFont(fontName, -1, -1, getFont())); return this; }
    public CEDigitsField resetFontKeepingFontName() { setFont(getFont(fontName, -1, -1, getFont())); return this; }
    public CEDigitsField setFontName(String fontName) { this.fontName = fontName; setFont(getFont(fontName, -1, -1, getFont())); return this; }
    public CEDigitsField setFontSize(int size) { setFont(getFont(fontName, -1, size, getFont())); return this; }
    public CEDigitsField setFontStyle(int style) { setFont(getFont(fontName, style, -1, getFont())); return this; }

    public void enableField() { for (CETextField digitField : digitFields) digitField.setEnabled(true); }
    public void disableField() { for (CETextField digitField : digitFields) digitField.setEnabled(false); }

    @Override
    public boolean requestFocusInWindow() { return digitFields[0].requestFocusInWindow(); }
}