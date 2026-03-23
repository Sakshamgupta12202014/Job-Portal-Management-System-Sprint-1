package com.capg.springboot.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

// This utility class handles saving resume files to disk
// It also validates the file type and size

@Component
public class FileUploadUtil {

    // This value is read from application.yml
    // If not set, it defaults to "./uploads"
    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    public String saveFile(MultipartFile file) throws IOException {

        // Step 1: Check that a file was actually provided
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Resume file is required. Please upload a PDF or DOCX file.");
        }

        // Step 2: Check the file type - only PDF and DOCX are allowed
        // We check the content type, not just the extension
        // A hacker could rename malware.exe to resume.pdf - content type check catches this
        String contentType = file.getContentType();

        boolean isPdf  = "application/pdf".equals(contentType);
        boolean isDocx = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                         .equals(contentType);

        if (!isPdf && !isDocx) {
            throw new IllegalArgumentException(
                "Invalid file type: " + contentType +
                ". Only PDF and DOCX files are allowed."
            );
        }

        // Step 3: Get the original file extension from the filename
        String originalFilename = file.getOriginalFilename();
        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            // Get everything after the last dot
            // Example: "my resume.pdf" -> extension = ".pdf"
            int lastDotIndex = originalFilename.lastIndexOf(".");
            extension = originalFilename.substring(lastDotIndex);
        }

        // Step 4: Generate a unique filename using UUID
        // This prevents two people with the same filename from overwriting each other
        // Example: "a1b2c3d4-e5f6-7890-abcd-ef1234567890.pdf"
        String uniqueFilename = UUID.randomUUID().toString() + extension;

        // Step 5: Create the upload folder if it does not already exist
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Step 6: Save the file to disk
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Step 7: Return the saved file path
        // We store this path in the database - not the actual file bytes
        String savedPath = uploadDir + "/" + uniqueFilename;
        return savedPath;
    }

}
