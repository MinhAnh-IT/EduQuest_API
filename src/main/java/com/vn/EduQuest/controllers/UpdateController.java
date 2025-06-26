
package com.vn.EduQuest.controllers;
import java.nio.file.Paths;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.ApiResponse;
import com.vn.EduQuest.payload.request.UpdateRequest;
import com.vn.EduQuest.payload.response.UpdateResponse;
import com.vn.EduQuest.security.UserDetailsImpl;
import com.vn.EduQuest.services.StorageService;
import com.vn.EduQuest.services.UpdateService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UpdateController {
        UpdateService updateService;
        StorageService StorageService;
        @GetMapping("/Profile/me")
        public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetailsImpl userDetails) throws CustomException {
        UpdateResponse response = updateService.getProfile(userDetails.getId());
        ApiResponse<?> apiResponse = ApiResponse.<UpdateResponse>builder()
                .code(StatusCode.OK.getCode())
                .message(StatusCode.OK.getMessage())
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }
       @PutMapping("/update/profile")
    public ResponseEntity<?> updateProfiles(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam("email") String email,
        @RequestParam(value = "avatar", required = false) MultipartFile avatarFile
    ) throws CustomException {
        String avatarUrl = null;
        
        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
            String fileName = StorageService.saveFile(avatarFile);
            fileName = Paths.get(fileName).getFileName().toString();
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            avatarUrl = baseUrl + "/uploads/" + fileName;
            } catch (Exception e) {
                throw new CustomException(StatusCode.INTERNAL_SERVER_ERROR, "Lỗi khi lưu file: " + e.getMessage());
            }
        }
        
        UpdateRequest request = new UpdateRequest();
        request.setEmail(email);
        request.setAvatarUrl(avatarUrl);
        
        UpdateResponse response = updateService.updateProfiles(userDetails.getId(), request);
        ApiResponse<?> apiResponse = ApiResponse.<UpdateResponse>builder()
                .code(StatusCode.OK.getCode())
                .message(StatusCode.OK.getMessage())
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
