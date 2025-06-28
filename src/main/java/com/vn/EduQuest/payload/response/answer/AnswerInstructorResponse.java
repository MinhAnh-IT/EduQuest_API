package com.vn.EduQuest.payload.response.answer;

import com.vn.EduQuest.entities.Answer;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnswerInstructorResponse {
    int id;
    String content;
    Boolean isCorrect;

    public static List<AnswerInstructorResponse> fromAnswers(List<Answer> answers) {
        return answers.stream()
                .map(answer -> AnswerInstructorResponse.builder()
                        .id(answer.getId().intValue())
                        .content(answer.getContent())
                        .isCorrect(answer.getIsCorrect())
                        .build())
                .collect(Collectors.toList());
    }
}