package com.vn.EduQuest.services;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String saveFile(MultipartFile file) throws Exception;
}
