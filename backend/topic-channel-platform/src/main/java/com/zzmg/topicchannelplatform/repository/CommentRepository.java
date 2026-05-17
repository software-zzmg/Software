package com.zzmg.topicchannelplatform.repository;

import com.zzmg.topicchannelplatform.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
