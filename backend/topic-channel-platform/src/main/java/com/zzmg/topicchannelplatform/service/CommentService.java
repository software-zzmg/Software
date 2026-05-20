package com.zzmg.topicchannelplatform.service;

import com.zzmg.topicchannelplatform.entity.Comment;
import com.zzmg.topicchannelplatform.repository.CommentRepository;
import com.zzmg.topicchannelplatform.repository.ThemePostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final ThemePostRepository postRepository;
    private final ForumMemberService forumMemberService;

    public CommentService(CommentRepository commentRepository,
                          ThemePostRepository postRepository,
                          ForumMemberService forumMemberService) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.forumMemberService = forumMemberService;
    }

    public Comment publish(Comment comment, String userId) {
        Long forumId = comment.getThemePost().getForum().getForumId();
        if (!forumMemberService.isMember(userId, forumId)) {
            throw new IllegalStateException("只有频道成员才能评论");
        }
        comment.setPublishTime(LocalDateTime.now());
        comment.setAuditState("待审核");
        return commentRepository.save(comment);
    }

    public List<Comment> findByThemePostId(Long postId) {
        return commentRepository.findAll().stream()
                .filter(c -> c.getThemePost().getThemePostId().equals(postId)
                        && "审核通过".equals(c.getAuditState()))
                .toList();
    }

    public List<Comment> findApprovedCommentsByPostId(Long postId) {
        return commentRepository.findByThemePost_ThemePostIdAndAuditStateOrderByPublishTimeAsc(
                postId, "审核通过");
    }

    public boolean canDelete(String userId, Long commentId) {
        return commentRepository.findById(commentId).map(comment -> {
            if (comment.getAuthor().getUserId().equals(userId)) {
                return true;
            }
            Long postId = comment.getThemePost().getThemePostId();
            return postRepository.findById(postId)
                    .map(p -> p.getAuthor().getUserId().equals(userId))
                    .orElse(false);
        }).orElse(false);
    }

    public void deleteById(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    public void updateAuditState(Long commentId, String auditState) {
        commentRepository.findById(commentId).ifPresent(c -> {
            c.setAuditState(auditState);
            commentRepository.save(c);
        });
    }
}
