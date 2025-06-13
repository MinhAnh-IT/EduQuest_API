package com.vn.EduQuest.repositories;

import com.vn.EduQuest.entities.DiscussionComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscussionCommentRepository extends JpaRepository<DiscussionComment, Long> {
}
