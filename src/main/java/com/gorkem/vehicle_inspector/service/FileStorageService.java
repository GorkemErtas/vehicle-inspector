package com.gorkem.vehicle_inspector.service;

import com.gorkem.vehicle_inspector.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES =
            Set.of(
                    "image/jpeg",
                    "image/png",
                    "image/webp"
            );

    private final Path uploadDirectory;

    public FileStorageService(
            @Value("${application.storage.upload-dir}")
            String uploadDirectory
    ) {
        this.uploadDirectory = Path.of(uploadDirectory)
                .toAbsolutePath()
                .normalize();

        createUploadDirectory();
    }

    public String storeImage(MultipartFile file) {
        validateFile(file);

        String originalFilename =
                StringUtils.cleanPath(
                        file.getOriginalFilename() == null
                                ? "image"
                                : file.getOriginalFilename()
                );

        String extension = getExtension(originalFilename);

        String storedFilename =
                UUID.randomUUID() + extension;

        Path destination =
                uploadDirectory.resolve(storedFilename)
                        .normalize();

        if (!destination.startsWith(uploadDirectory)) {
            throw new FileStorageException(
                    "Geçersiz dosya yolu."
            );
        }

        try {
            Files.copy(
                    file.getInputStream(),
                    destination,
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException exception) {
            throw new FileStorageException(
                    "Dosya kaydedilemedi.",
                    exception
            );
        }

        return storedFilename;
    }

    private void createUploadDirectory() {
        try {
            Files.createDirectories(uploadDirectory);
        } catch (IOException exception) {
            throw new FileStorageException(
                    "Upload klasörü oluşturulamadı.",
                    exception
            );
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileStorageException(
                    "Yüklenecek fotoğraf boş olamaz."
            );
        }

        String contentType = file.getContentType();

        if (contentType == null
                || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new FileStorageException(
                    "Yalnızca JPG, PNG veya WEBP yüklenebilir."
            );
        }
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf(".");

        if (dotIndex < 0) {
            return ".jpg";
        }

        return filename.substring(dotIndex).toLowerCase();
    }

    public Path resolveStoredFile(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            throw new FileStorageException(
                    "İncelemeye ait fotoğraf bulunamadı."
            );
        }

        String filename = Path.of(imagePath)
                .getFileName()
                .toString();

        Path resolvedPath = uploadDirectory
                .resolve(filename)
                .normalize();

        if (!resolvedPath.startsWith(uploadDirectory)) {
            throw new FileStorageException(
                    "Geçersiz fotoğraf yolu."
            );
        }

        if (!Files.exists(resolvedPath)) {
            throw new FileStorageException(
                    "Fotoğraf dosyası bulunamadı."
            );
        }

        return resolvedPath;
    }
}