package com.vn.EduQuest.entities;

import com.vn.EduQuest.enums.ParticipationStatus; // Import enum ParticipationStatus
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
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
    @Column(columnDefinition = "TEXT CHECK (status IN ('IN_PROGRESS', 'SUBMITTED', 'GRADED')) DEFAULT 'IN_PROGRESS'")
    ParticipationStatus status;

    @Column(precision = 4, scale = 2)
    @DecimalMin("0.00")
    @DecimalMax("10.00")
    BigDecimal score;

    @Column(name = "submitted_at")
    LocalDateTime submittedAt;

    @Column(name = "start_at")
    LocalDateTime startAt;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    LocalDateTime createdAt;
}