package qrcodeapi.web;

import com.google.zxing.WriterException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;

import qrcodeapi.service.BufferedImageCreator;
import qrcodeapi.service.CorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@RestController
public class ResponseController {
    @GetMapping("/api/health")
    public ResponseEntity<Void> healthCheck() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/qrcode")
    public ResponseEntity<?> getImage(@RequestParam(defaultValue = "250", required = false) int size,
                                      @RequestParam(defaultValue = "png", required = false) String type,
                                      @RequestParam String contents,
                                      @RequestParam(defaultValue = "L", required = false) String correction) {


                if (contents == null || contents.isBlank()) {
                    return ResponseEntity
                            .badRequest()
                            .body(Map.of("error", "Contents cannot be null or blank"));
                }

                if (size < 150 || size > 350) {
                    return ResponseEntity
                            .badRequest()
                            .body(Map.of("error", "Image size must be between 150 and 350 pixels"));
                }
                if (correction == null || correction.length() > 1 ||!correction.matches("[LMQH]")) {
                    return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Permitted error correction levels are L, M, Q, H"));
                }

                if (!type.equalsIgnoreCase("png") && !type.equalsIgnoreCase("jpeg") && !type.equalsIgnoreCase("gif")) {
                    return ResponseEntity
                            .badRequest()
                            .body(Map.of("error", "Only png, jpeg and gif image types are supported"));
                }

                CorrectionLevel correctionLevel;
                try {
                    correctionLevel = CorrectionLevel.valueOf(correction.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return ResponseEntity
                            .badRequest()
                            .body(Map.of("error", "Error msg"));
                }

                try {
                    BufferedImage bufferedImage = new BufferedImageCreator(contents, size, correctionLevel).createImage();

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    String formatName = type.equalsIgnoreCase("jpg") ? "jpeg" : type.toLowerCase();
                    ImageIO.write(bufferedImage, formatName, baos);
                    byte[] imageBytes = baos.toByteArray();

                    MediaType mediaType = switch (formatName) {
                        case "png" -> MediaType.IMAGE_PNG;
                        case "jpeg" -> MediaType.IMAGE_JPEG;
                        case "gif" -> MediaType.IMAGE_GIF;
                        default -> throw new IllegalArgumentException("Unsupported image type");
                    };

                    return ResponseEntity.ok()
                            .contentType(mediaType)
                            .body(imageBytes);
                } catch (WriterException | IOException e) {
                    return ResponseEntity
                            .internalServerError()
                            .body(Map.of("error", "Failed to generate QR code image"));
                }

            }
        }


