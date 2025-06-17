package com.vn.EduQuest.enums;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ParticipationStatus {
    IN_PROGRESS("InProgress"),
    SUBMITTED("Submitted");

    String value;
}