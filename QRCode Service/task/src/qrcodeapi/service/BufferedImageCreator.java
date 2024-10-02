package qrcodeapi.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.awt.Color;
import java.awt.Graphics2D;

public class BufferedImageCreator {
    private String content;
    private int size;

    public BufferedImageCreator(String content, int size) {
        this.content = content;
        this.size = size;
    }

    public BufferedImage createImage() {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, size, size);

        g.dispose();

        return image;
    }
}
