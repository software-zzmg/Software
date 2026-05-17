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
        String forumId = comment.getThemePost().getForum().getForumId();
        if (!forumMemberService.isMember(userId, forumId)) {
            throw new IllegalStateException("只有频道成员才能评论");
        }
        if (comment.getCommentId() == null || comment.getCommentId().isEmpty()) {
            int nextId = commentRepository.findAll().stream()
                    .map(Comment::getCommentId)
                    .mapToInt(id -> {
                        try { return Integer.parseInt(id); } catch (NumberFormatException e) { return 0; }
                    })
                    .max()
                    .orElse(0) + 1;
            comment.setCommentId(String.valueOf(nextId));
        }
        comment.setPublishTime(LocalDateTime.now());
        comment.setAuditState("待审核");
        return commentRepository.save(comment);
    }

    public List<Comment> findByThemePostId(String postId) {
        return commentRepository.findAll().stream()
                .filter(c -> c.getThemePost().getThemePostId().equals(postId)
                        && "审核通过".equals(c.getAuditState()))
                .toList();
    }

    public boolean canDelete(String userId, String commentId) {
        return commentRepository.findById(commentId).map(comment -> {
            // 评论作者可以删除
            if (comment.getAuthor().getUserId().equals(userId)) {
                return true;
            }
            // 帖子作者可以删除该帖下评论
            String postId = comment.getThemePost().getThemePostId();
            return postRepository.findById(postId)
                    .map(p -> p.getAuthor().getUserId().equals(userId))
                    .orElse(false);
        }).orElse(false);
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
