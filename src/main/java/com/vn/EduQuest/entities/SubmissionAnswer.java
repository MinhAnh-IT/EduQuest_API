package com.vn.EduQuest.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "submission_answers")
public class SubmissionAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "participation_id")
    Participation participation;

    @ManyToOne
    @JoinColumn(name = "exercise_question_id")
    ExerciseQuestion exerciseQuestion;

    @ManyToOne
    @JoinColumn(name = "answer_id")
    Answer answer;

    @CreationTimestamp
    LocalDateTime createdAt;
}