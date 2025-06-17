package com.vn.EduQuest.mapper;

import com.vn.EduQuest.entities.Exercise;
import com.vn.EduQuest.entities.Participation;
import com.vn.EduQuest.entities.User;
import com.vn.EduQuest.payload.response.exerciseQuestion.ExerciseQuestionResponse;
import com.vn.EduQuest.payload.response.participation.StartExamResponse;
import com.vn.EduQuest.payload.response.participation.SubmissionAnswerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ParticipationMapper {
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "exercise", source = "exercise"),
            @Mapping(target = "student", source = "user.studentDetail"),
            @Mapping(target = "score", ignore = true),
            @Mapping(target = "status", ignore = true),
            @Mapping(target = "submittedAt", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "startAt", ignore = true)
    })
    Participation toEntity(Exercise exercise, User user);

    @Mappings({
            @Mapping(target = "participationId", source = "participationSaved.id"),
            @Mapping(target = "exerciseId", source = "exercise.id"),
            @Mapping(target = "studentId", source = "participationSaved.student.id"),
            @Mapping(target = "startAt", source = "participationSaved.startAt"),
            @Mapping(target = "durationMinutes", source = "exercise.durationMinutes"),
            @Mapping(target = "questions", source = "questions"),
            @Mapping(target = "status", source = "participationSaved.status")
    })
    StartExamResponse toResponseAfterStartExam(Participation participationSaved,
                                               Exercise exercise, List<ExerciseQuestionResponse> questions);

    @Mappings({
            @Mapping(target = "participationId", source = "participation.id"),
            @Mapping(target = "status", source = "participation.status"),
            @Mapping(target = "submittedAt", source = "participation.submittedAt"),
    })
    SubmissionAnswerResponse toSubmissionAnswerResponse(Participation participation);
}
