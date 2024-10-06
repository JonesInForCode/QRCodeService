package qrcodeapi.service;

import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.awt.image.BufferedImage;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class BufferedImageCreator {
    private String content;
    private int size;
    private CorrectionLevel correctionLevel;

    public BufferedImageCreator(String content, int size, CorrectionLevel correctionLevel) {
        this.content = content;
        this.size = size;
        this.correctionLevel = correctionLevel;
    }

    public BufferedImage createImage() throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();
        Map<EncodeHintType, ?> hints = Map.of(EncodeHintType.ERROR_CORRECTION, getErrorCorrectionLevel());
        BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    private ErrorCorrectionLevel getErrorCorrectionLevel() {
        return switch (correctionLevel) {
            case L -> ErrorCorrectionLevel.L;
            case M -> ErrorCorrectionLevel.M;
            case Q -> ErrorCorrectionLevel.Q;
            case H -> ErrorCorrectionLevel.H;
            default -> throw new IllegalArgumentException("Invalid correction level");
        };
    }
}
