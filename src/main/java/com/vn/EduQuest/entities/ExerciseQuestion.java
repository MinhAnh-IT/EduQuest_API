package com.vn.EduQuest.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

}
