package qrcodeapi.web;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import qrcodeapi.service.BufferedImageCreator;

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
    public ResponseEntity<?> getImage(@RequestParam(defaultValue = "250") int size,
                                      @RequestParam(defaultValue = "png") String type) {
        boolean isInvalidSize = size < 150 || size > 350;
        boolean isInvalidType = !type.equalsIgnoreCase("png") && !type.equalsIgnoreCase("jpeg") &&
                !type.equalsIgnoreCase("jpg") && !type.equalsIgnoreCase("gif");

        if (isInvalidSize) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Image size must be between 150 and 350 pixels"));
        }

        if (isInvalidType) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Only png, jpeg and gif image types are supported"));
        }

        try {
            BufferedImage bufferedImage = new BufferedImageCreator("content", size).createImage();

            try (var baos = new ByteArrayOutputStream()) {
                ImageIO.write(bufferedImage, type, baos);
                byte[] bytes = baos.toByteArray();

                MediaType mediaType;
                switch (type.toLowerCase()) {
                    case "png":
                        mediaType = MediaType.IMAGE_PNG;
                        break;
                    case "jpeg":
                        mediaType = MediaType.IMAGE_JPEG;
                        break;
                    case "gif":
                        mediaType = MediaType.IMAGE_GIF;
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported image type: " + type);
                }

                return ResponseEntity.ok().contentType(mediaType).body(bytes);
            }
        } catch (IOException e) {
            return ResponseEntity
                    .internalServerError()
                    .body(Map.of("error", "Failed to generate QR code image"));
        }
    }

}
