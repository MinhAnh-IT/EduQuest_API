package com.vn.EduQuest.entities;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;


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