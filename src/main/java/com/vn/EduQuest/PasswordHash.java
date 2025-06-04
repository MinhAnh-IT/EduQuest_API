package com.vn.EduQuest;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "nguyenphucson";
        String hashed = encoder.encode(password);
        System.out.println(hashed);
    }
}