package dev.m4nd3l.chatting4ever.components.icons;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;

public class EyeIcon implements Icon {
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        boolean isSelected = false;

        if (c instanceof AbstractButton) isSelected = ((AbstractButton) c).isSelected();

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(c.getForeground());
        g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        Path2D.Float path = new Path2D.Float();
        path.moveTo(x + 1, y + 8);
        path.quadTo(x + 8, y + 1, x + 15, y + 8);
        path.quadTo(x + 8, y + 15, x + 1, y + 8);
        path.closePath();
        g2.draw(path);

        if (!isSelected) g2.fillOval(x + 5, y + 5, 6, 6);
        else {
            g2.drawOval(x + 6, y + 6, 4, 4);
            g2.drawLine(x + 3, y + 3, x + 13, y + 13);
        }

        g2.dispose();
    }

    @Override
    public int getIconWidth() { return 16; }

    @Override
    public int getIconHeight() { return 16; }
}