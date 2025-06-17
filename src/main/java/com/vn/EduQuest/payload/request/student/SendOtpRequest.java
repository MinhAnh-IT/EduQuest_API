package com.vn.EduQuest.payload.request.student;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Getter
@Setter
public class SendOtpRequest {
    String username;
}