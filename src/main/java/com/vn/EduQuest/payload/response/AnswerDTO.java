package com.vn.EduQuest.payload.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AnswerDTO {
    private Long answerId;
    private String content;
    private Boolean isCorrect;
}