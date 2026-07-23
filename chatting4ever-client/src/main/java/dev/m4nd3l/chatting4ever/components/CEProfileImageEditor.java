package dev.m4nd3l.chatting4ever.components;

import dev.m4nd3l.chatting4ever.Chatting4EverClient;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import javax.imageio.ImageIO;

public class CEProfileImageEditor extends JComponent {
    private BufferedImage currentImage;
    private boolean isHovered = false;
    private final Image editPencilIcon;

    public CEProfileImageEditor(BufferedImage initialImage) {
        this.currentImage = initialImage;
        this.editPencilIcon = createDefaultPencilIcon();

        setPreferredSize(new Dimension(120, 120));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) { isHovered = true; repaint(); }

            @Override
            public void mouseExited(MouseEvent event) { isHovered = false; repaint(); }

            @Override
            public void mouseClicked(MouseEvent event) { openImageChooser(); }
        });
    }

    public File saveToTemp() {
        try {
            Path tempFile = Files.createTempFile(UUID.randomUUID() + "_chatting4ever_profile_image", ".png");
            File output = tempFile.toFile();
            ImageIO.write(currentImage, "png", output);
            output.deleteOnExit();
            return output;
        } catch (Exception _) {
            Chatting4EverClient.Window.error("Couldn't upload the image to the server.");
            return null;
        }
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

        if (isHovered) {
            graphics2D.setColor(new Color(0, 0, 0, 120));
            graphics2D.fill(circle);

            if (editPencilIcon != null) {
                int iconWidth = editPencilIcon.getWidth(this);
                int iconHeight = editPencilIcon.getHeight(this);
                int iconX = x + (diameter - iconWidth) / 2;
                int iconY = y + (diameter - iconHeight) / 2;
                graphics2D.drawImage(editPencilIcon, iconX, iconY, this);
            }
        }

        graphics2D.dispose();
    }

    private void openImageChooser() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "png", "jpeg", "gif");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                BufferedImage newImage = ImageIO.read(selectedFile);
                if (newImage != null) {
                    currentImage = newImage;
                    repaint();
                }
            } catch (IOException exception) {
                currentImage = new BufferedImage(980, 980, BufferedImage.TYPE_INT_RGB);
                repaint();
            }
        }
    }

    private Image createDefaultPencilIcon() {
        BufferedImage image = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setColor(Color.WHITE);
        graphics2D.setStroke(new BasicStroke(2));
        graphics2D.drawLine(6, 18, 18, 6);
        graphics2D.drawLine(14, 6, 18, 10);
        graphics2D.dispose();
        return image;
    }
}
