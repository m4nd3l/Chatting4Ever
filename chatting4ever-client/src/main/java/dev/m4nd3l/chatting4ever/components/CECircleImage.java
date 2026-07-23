package dev.m4nd3l.chatting4ever.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class CECircleImage extends JComponent {
    private final BufferedImage currentImage;

    public CECircleImage(BufferedImage initialImage) {
        this.currentImage = initialImage;
        setPreferredSize(new Dimension(120, 120));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics.create();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int diameter = Math.min(width, height);
        int x = (width - diameter) / 2;
        int y = (height - diameter) / 2;

        Ellipse2D.Double circle = new Ellipse2D.Double(x, y, diameter, diameter);

        graphics2D.setClip(circle);
        if (currentImage != null) graphics2D.drawImage(currentImage, x, y, diameter, diameter, this);
        else {
            graphics2D.setColor(Color.LIGHT_GRAY);
            graphics2D.fill(circle);
        }

        graphics2D.dispose();
    }
}
