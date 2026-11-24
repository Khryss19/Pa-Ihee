package ui.components;

import ui.Ui;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class LogoMark extends JComponent {

    private BufferedImage logoImage;

    public LogoMark(int targetHeight) {
        int targetWidth = targetHeight;
        try {
            BufferedImage raw = ImageIO.read(new File("resources/Pa-Ihee.png"));
            logoImage = trimTransparentPadding(raw);

            double ratio = (double) logoImage.getWidth() / logoImage.getHeight();
            targetWidth = (int) Math.round(targetHeight * ratio);
        } catch (IOException e) {
            System.err.println("Could not load logo image: " + e.getMessage());
        }

        Dimension d = new Dimension(targetWidth, targetHeight);
        setPreferredSize(d);
        setMinimumSize(d);
        setMaximumSize(d);
    }

    /** Automatically trims empty transparent borders around the PNG icon. */
    private BufferedImage trimTransparentPadding(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        int minX = w, minY = h, maxX = 0, maxY = 0;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int alpha = (img.getRGB(x, y) >> 24) & 0xff;
                if (alpha > 10) {
                    if (x < minX) minX = x;
                    if (x > maxX) maxX = x;
                    if (y < minY) minY = y;
                    if (y > maxY) maxY = y;
                }
            }
        }
        if (maxX < minX || maxY < minY) return img;
        return img.getSubimage(minX, minY, (maxX - minX) + 1, (maxY - minY) + 1);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Ui.smooth(g);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        if (logoImage != null) {
            g2.drawImage(logoImage, 0, 0, getWidth(), getHeight(), this);
        }
        g2.dispose();
    }
}