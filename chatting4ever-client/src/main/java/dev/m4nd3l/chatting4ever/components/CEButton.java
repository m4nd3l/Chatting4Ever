package dev.m4nd3l.chatting4ever.components;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;

import java.awt.*;

import static java.awt.Cursor.getPredefinedCursor;

public class CEButton extends JButton implements FontGetter {
    private String fontName = defaultFontName;

    public CEButton() { super(); init(false); }
    public CEButton(String text) { super(text); init(false); }
    public CEButton(String text, boolean hand) { super(text); init(hand); }

    private void init(boolean hand) {
        resetFontKeepingFontName();
        putClientProperty(FlatClientProperties.STYLE, "arc: 15; background: #007aff; foreground: #ffffff");
        if (hand) setCursor(getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public String getFontName() { return fontName; }
    public int getFontSize() { return getFont() != null ? getFont().getSize() : 12; }
    public int getFontStyle() { return getFont() != null ? getFont().getStyle() : 0; }

    public CEButton resetFont() { setFontName(defaultFontName); setFont(getFont(fontName, -1, -1, getFont())); return this; }
    public CEButton resetFontKeepingFontName() { setFont(getFont(fontName, -1, -1, getFont())); return this; }
    public CEButton setFontName(String fontName) { this.fontName = fontName; setFont(getFont(fontName, -1, -1, getFont())); return this; }
    public CEButton setFontSize(int size) { setFont(getFont(fontName, -1, size, getFont())); return this; }
    public CEButton setFontStyle(int style) { setFont(getFont(fontName, style, -1, getFont())); return this; }
}
