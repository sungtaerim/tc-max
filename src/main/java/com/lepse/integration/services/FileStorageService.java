package com.lepse.integration.services;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * a service that saves files and stores them
 */
public class FileStorageService {
    private final Path fileStorageLocation;

    /**
     * creates a new storage instance at the specified path
     */
    public FileStorageService(String pathToStorage){
        fileStorageLocation = Paths.get(pathToStorage)
                .toAbsolutePath()
                .normalize();
    }

    /**
     * stores the transferred file
     * @return returns the name of the saved file
     */
    public String storeFile(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        Path targetLocation = fileStorageLocation.resolve(fileName);

        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

}
