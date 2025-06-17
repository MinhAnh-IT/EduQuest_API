package com.vn.EduQuest.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String content;
    String difficulty;

    @ManyToOne
    @JoinColumn(name = "created_by")
    User createdBy;

    @OneToMany(mappedBy = "question")
    List<Answer> answers;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
