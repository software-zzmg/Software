package com.zzmg.topicchannelplatform.repository;

import com.zzmg.topicchannelplatform.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByThemePost_ThemePostIdAndAuditStateOrderByPublishTimeAsc(Long postId, String auditState);

    long countByAuditState(String auditState);

    long countByThemePost_ThemePostIdAndAuditState(Long postId, String auditState);

    List<Comment> findByAuthor_UserIdOrderByPublishTimeDesc(String userId);
}
