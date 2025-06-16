package com.vn.EduQuest.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "exercise_questions")
public class ExerciseQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "exercise_id")
    Exercise exercise;

    @ManyToOne
    @JoinColumn(name = "question_id")
    Question question;

    @Column(name = "\"order\"")
    Integer order;

    @Column(precision = 5, scale = 2)
    BigDecimal point;
}