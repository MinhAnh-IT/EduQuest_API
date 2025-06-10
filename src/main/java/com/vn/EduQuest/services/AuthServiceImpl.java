package com.vn.EduQuest.services;

import com.vn.EduQuest.entities.StudentDetail;
import com.vn.EduQuest.entities.User;
import com.vn.EduQuest.enums.Role;
import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.mapper.StudentsDetailMapper;
import com.vn.EduQuest.mapper.UserMapper;
import com.vn.EduQuest.payload.request.RegisterRequest;
import com.vn.EduQuest.payload.request.StudentDetailRequest;
import com.vn.EduQuest.payload.request.VerifyOtpRequest;
import com.vn.EduQuest.payload.response.LoginResponse;
import com.vn.EduQuest.payload.response.RegisterRespone;
import com.vn.EduQuest.payload.response.StudentDetailResponse;
import com.vn.EduQuest.repositories.StudentDetailRepository;
import com.vn.EduQuest.repositories.UserRepository;
import com.vn.EduQuest.utills.Bcrypt;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {
    UserRepository userRepository;
    StudentsDetailMapper studentsDetailMapper;
    StudentDetailRepository studentDetailRepository;
    UserMapper userMapper;
    OTPService otpService;
    EmailService emailService;

    @Override
    public LoginResponse login(String username, String password) {
        return null;
    }

    @Override
    @Transactional
    public RegisterRespone register(RegisterRequest request) throws CustomException {
        // Validate username uniqueness
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CustomException(StatusCode.EXIST_USERNAME, request.getUsername());
        }

        // Create new user
        User user = userMapper.toEntity(request);
        user.setIsActive(false); // User is inactive until OTP verification
        user.setPassword(Bcrypt.hashPassword(request.getPassword()));
        
        // Set role based on isTeacher flag
        if (request.isTeacher()) {
            user.setRole(Role.INSTRUCTOR);
            log.info("Setting role to INSTRUCTOR for user: {}", request.getUsername());
        } else {
            user.setRole(Role.STUDENT);
            log.info("Setting role to STUDENT for user: {}", request.getUsername());
        }

        // Save user to database
        user = userRepository.save(user);
        log.info("Saved user with role: {}", user.getRole());
        
        // Generate and send OTP
        String otp = otpService.generateOTP(request.getEmail());
        emailService.sendOTPEmail(request.getEmail(), otp);
        
        RegisterRespone response = userMapper.toUserDTO(user);
        log.info("Response role: {}", response.getRole());
        
        return response;
    }

    @Override
    public boolean verifyOTP (VerifyOtpRequest request) throws CustomException {
        // Validate OTP
        log.info("Verifying OTP for email: {}", request.getEmail());
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new CustomException(StatusCode.USER_NOT_FOUND));
        if (otpService.validateOTP(request.getEmail(),request.getOtp())) {
            user.setIsActive(true);
            userRepository.save(user);
            otpService.clearOTP(request.getEmail());
            return true;
        }
        return false;
    }

    @Override
    public void resendOTP(String email) throws CustomException {
        User user = userRepository.findByUsername(email)
            .orElseThrow(() -> new CustomException(StatusCode.USER_NOT_FOUND));
        
        if (user.getIsActive()) {
            throw new CustomException(StatusCode.USER_ALREADY_ACTIVE);
        }
        
        String otp = otpService.generateOTP(email);
        emailService.sendOTPEmail(email, otp);
    }

    @Override
    @Transactional
    public StudentDetailResponse updateStudentDetails(Long userId, StudentDetailRequest request) throws CustomException {
    // Kiểm tra user tồn tại và là sinh viên
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(StatusCode.USER_NOT_FOUND));
    
    if (user.getRole() != Role.STUDENT) {
        throw new CustomException(StatusCode.INVALID_ROLE);
    }

    // Kiểm tra user đã được kích hoạt chưa
    if (!user.getIsActive()) {
        throw new CustomException(StatusCode.USER_NOT_VERIFIED);
    }

    // Kiểm tra thông tin chi tiết sinh viên đã tồn tại chưa
    if (user.getStudentDetail() != null) {
        throw new CustomException(StatusCode.USER_ALREADY_ACTIVE);
    }

    // Kiểm tra mã số sinh viên đã tồn tại chưa
    if (studentDetailRepository.existsByStudentCode(request.getStudentCode())) {
        throw new CustomException(StatusCode.EXIST_STUDENT_CODE, request.getStudentCode());
    }

    // Tạo thông tin chi tiết sinh viên
    StudentDetail studentDetail = studentsDetailMapper.toEntity(request);
    studentDetail.setUser(user);
    user.setStudentDetail(studentDetail);

    // Lưu vào database
    user = userRepository.save(user);
    
    return userMapper.toStudentDetailResponse(user);
}
}