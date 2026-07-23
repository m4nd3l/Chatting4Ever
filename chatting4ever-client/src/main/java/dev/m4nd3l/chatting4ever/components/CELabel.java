package dev.m4nd3l.chatting4ever.components;

import javax.swing.*;

public class CELabel extends JLabel implements FontGetter {
    private String fontName = defaultFontName;

    public CELabel() { super(); resetFontKeepingFontName(); }
    public CELabel(Icon image) { super(image); resetFontKeepingFontName(); }
    public CELabel(String text) { super(text); resetFontKeepingFontName(); }
    public CELabel(Icon image, int horizontalAlignment) { super(image, horizontalAlignment); resetFontKeepingFontName(); }
    public CELabel(String text, int horizontalAlignment) { super(text, horizontalAlignment); resetFontKeepingFontName(); }
    public CELabel(String text, Icon icon, int horizontalAlignment) { super(text, icon, horizontalAlignment); resetFontKeepingFontName(); }

    public String getFontName() { return fontName; }
    public int getFontSize() { return getFont() != null ? getFont().getSize() : 12; }
    public int getFontStyle() { return getFont() != null ? getFont().getStyle() : 0; }

    public CELabel resetFont() { setFontName(defaultFontName); setFont(getFont(fontName, -1, -1, getFont())); return this; }
    public CELabel resetFontKeepingFontName() { setFont(getFont(fontName, -1, -1, getFont())); return this; }
    public CELabel setFontName(String fontName) { this.fontName = fontName; setFont(getFont(fontName, -1, -1, getFont())); return this; }
    public CELabel setFontSize(int size) { setFont(getFont(fontName, -1, size, getFont())); return this; }
    public CELabel setFontStyle(int style) { setFont(getFont(fontName, style, -1, getFont())); return this; }
}