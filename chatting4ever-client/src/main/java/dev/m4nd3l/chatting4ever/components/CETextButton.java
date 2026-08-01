package dev.m4nd3l.chatting4ever.components;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import java.awt.*;

import static java.awt.Cursor.getPredefinedCursor;

public class CETextButton extends JButton implements FontGetter {
    private String fontName = defaultFontName;

    public CETextButton() { super(); init(false); }
    public CETextButton(String text) { super(text); init(false); }
    public CETextButton(String text, boolean hand) { super(text); init(hand); }

    private void init(boolean hand) {
        resetFontKeepingFontName();
        putClientProperty(FlatClientProperties.STYLE, "borderWidth: 0; focusWidth: 0; background: null; foreground: #007aff");
        if (hand) setCursor(getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public String getFontName() { return fontName; }
    public int getFontSize() { return getFont() != null ? getFont().getSize() : 12; }
    public int getFontStyle() { return getFont() != null ? getFont().getStyle() : 0; }

    public CETextButton resetFont() { setFontName(defaultFontName); setFont(getFont(fontName, -1, -1, getFont())); return this; }
    public CETextButton resetFontKeepingFontName() { setFont(getFont(fontName, -1, -1, getFont())); return this; }
    public CETextButton setFontName(String fontName) { this.fontName = fontName; setFont(getFont(fontName, -1, -1, getFont())); return this; }
    public CETextButton setFontSize(int size) { setFont(getFont(fontName, -1, size, getFont())); return this; }
    public CETextButton setFontStyle(int style) { setFont(getFont(fontName, style, -1, getFont())); return this; }
}
