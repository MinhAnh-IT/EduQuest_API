package com.vn.EduQuest.entities;

import com.vn.EduQuest.enums.ParticipationStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Entity
@Table(name = "participations")
public class Participation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    Student student;

    @ManyToOne
    @JoinColumn(name = "exercise_id")
    Exercise exercise;

    @Enumerated(EnumType.STRING)
    ParticipationStatus status = ParticipationStatus.IN_PROGRESS;

    float score;

    LocalDateTime submittedAt;

    @CreationTimestamp
    LocalDateTime startAt;

    @CreationTimestamp
    LocalDateTime createdAt;
}