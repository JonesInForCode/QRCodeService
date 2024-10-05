package qrcodeapi.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.awt.Color;
import java.awt.Graphics2D;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class BufferedImageCreator {
    private String content;
    private int size;

    public BufferedImageCreator(String content, int size) {
        this.content = content;
        this.size = size;
    }

    public BufferedImage createImage() throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
}
