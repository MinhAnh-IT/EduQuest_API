package com.vn.EduQuest.repositories;

import com.vn.EduQuest.entities.Discussion;
import com.vn.EduQuest.entities.DiscussionComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiscussionCommentRepository extends JpaRepository<DiscussionComment, Long> {
    List<DiscussionComment> findByDiscussion(Discussion discussion);
}
