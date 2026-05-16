package com.zzmg.topicchannelplatform.service;

import com.zzmg.topicchannelplatform.entity.Comment;
import com.zzmg.topicchannelplatform.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment publish(Comment comment) {
        comment.setPublishTime(LocalDateTime.now());
        comment.setAuditState("待审核");
        return commentRepository.save(comment);
    }

    public List<Comment> findByThemePostId(String postId) {
        return commentRepository.findAll().stream()
                .filter(c -> c.getThemePost().getThemePostId().equals(postId))
                .toList();
    }

    public void deleteById(String commentId) {
        commentRepository.deleteById(commentId);
    }

    public void updateAuditState(String commentId, String auditState) {
        commentRepository.findById(commentId).ifPresent(c -> {
            c.setAuditState(auditState);
            commentRepository.save(c);
        });
    }
}
