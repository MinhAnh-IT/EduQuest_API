package com.vn.EduQuest.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;


@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Entity
@Table(name = "discussion_comments")
public class DiscussionComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "discussion_id")
    Discussion discussion;

    @ManyToOne
    @JoinColumn(name = "student_id")
    Student student;

    String content;

    @ManyToOne
    @JoinColumn(name = "created_by")
    User createdBy;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
