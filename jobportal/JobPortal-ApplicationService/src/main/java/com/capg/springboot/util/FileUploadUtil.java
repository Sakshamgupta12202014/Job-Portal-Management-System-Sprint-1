package com.capg.springboot.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Component
public class FileUploadUtil {

    // Accepted MIME types — validate server-side, never trust the file extension alone
    private static final List<String> ACCEPTED_TYPES = List.of(
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    /**
     * Save file to disk and return the stored path.
     * Uses UUID + original extension to prevent overwrites and path traversal.
     * Max file size enforced via application.yml (spring.servlet.multipart.max-file-size=5MB)
     */
    public String saveFile(MultipartFile file) throws IOException {
        // Validate MIME type server-side
        String contentType = file.getContentType();
        if (contentType == null || !ACCEPTED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException(
                    "Unsupported file type. Only PDF and DOCX are allowed.");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // UUID filename prevents overwrites and path traversal attacks
        String uniqueFilename = UUID.randomUUID() + extension;

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Store only the file path in DB — never the raw bytes
        return uploadDir + "/" + uniqueFilename;
    }
}
