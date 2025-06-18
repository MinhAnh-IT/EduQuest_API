package com.vn.EduQuest.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class StorageServiceImpl implements StorageService {
    
    @Override
    public String saveFile(MultipartFile file) throws Exception {
        String uploadsDir = "uploads/";
        Path uploadPath = Paths.get(uploadsDir);
        
        // Tạo thư mục nếu chưa có
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return "/uploads/" + fileName;
    }
}