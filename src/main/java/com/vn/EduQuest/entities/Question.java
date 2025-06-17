package com.vn.EduQuest.entities;

import com.vn.EduQuest.enums.Difficulty;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String content;

    @Enumerated(EnumType.STRING)
    Difficulty difficulty = Difficulty.EASY;

    @ManyToOne
    @JoinColumn(name = "created_by")
    User createdBy;

    @OneToMany(mappedBy = "question")
    List<Answer> answers;

    @CreationTimestamp
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;
}