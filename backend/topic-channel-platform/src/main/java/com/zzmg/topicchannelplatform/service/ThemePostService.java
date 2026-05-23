package com.zzmg.topicchannelplatform.service;

import com.zzmg.topicchannelplatform.entity.ThemePost;
import com.zzmg.topicchannelplatform.repository.CollectRepository;
import com.zzmg.topicchannelplatform.repository.CommentRepository;
import com.zzmg.topicchannelplatform.repository.ForumRepository;
import com.zzmg.topicchannelplatform.repository.OrdinaryUserRepository;
import com.zzmg.topicchannelplatform.repository.ThemePostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ThemePostService {

    private final ThemePostRepository postRepository;
    private final ForumRepository forumRepository;
    private final OrdinaryUserRepository userRepository;
    private final ForumMemberService forumMemberService;
    private final CollectRepository collectRepository;
    private final CommentRepository commentRepository;

    public ThemePostService(ThemePostRepository postRepository,
                            ForumRepository forumRepository,
                            OrdinaryUserRepository userRepository,
                            ForumMemberService forumMemberService,
                            CollectRepository collectRepository,
                            CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.forumRepository = forumRepository;
        this.userRepository = userRepository;
        this.forumMemberService = forumMemberService;
        this.collectRepository = collectRepository;
        this.commentRepository = commentRepository;
    }

    public ThemePost publish(ThemePost post, String userId) {
        if (!forumMemberService.isMember(userId, post.getForum().getForumId())) {
            throw new IllegalStateException("只有频道成员才能发帖");
        }
        post.setPublishTime(LocalDateTime.now());
        post.setAuditState("待审核");
        return postRepository.save(post);
    }

    public Optional<ThemePost> findById(Long postId) {
        return postRepository.findById(postId);
    }

    public List<ThemePost> findByForumId(Long forumId) {
        return postRepository.findAll().stream()
                .filter(p -> p.getForum().getForumId().equals(forumId)
                        && "审核通过".equals(p.getAuditState()))
                .toList();
    }

    public void update(ThemePost post) {
        postRepository.findById(post.getThemePostId()).ifPresent(existing -> {
            existing.setTitle(post.getTitle());
            existing.setContent(post.getContent());
            postRepository.save(existing);
        });
    }

    public boolean isAuthor(String userId, Long postId) {
        return postRepository.findById(postId)
                .map(p -> p.getAuthor().getUserId().equals(userId))
                .orElse(false);
    }

    public boolean canDelete(String userId, Long postId) {
        return postRepository.findById(postId)
                .map(p -> p.getAuthor().getUserId().equals(userId)
                        || p.getForum().getCreator().getUserId().equals(userId))
                .orElse(false);
    }

    public List<ThemePost> findApproved() {
        return postRepository.findAll().stream()
                .filter(p -> "审核通过".equals(p.getAuditState()))
                .sorted((a, b) -> b.getPublishTime().compareTo(a.getPublishTime()))
                .toList();
    }

    public List<ThemePost> findApprovedPosts() {
        return postRepository.findByAuditStateOrderByPublishTimeDesc("审核通过");
    }

    public List<ThemePost> findApprovedByUserId(String userId) {
        return postRepository.findByAuthor_UserIdOrderByPublishTimeDesc(userId).stream()
                .filter(p -> !"审核未通过".equals(p.getAuditState()))
                .toList();
    }

    public Optional<ThemePost> findApprovedPostById(Long postId) {
        return postRepository.findById(postId)
                .filter(p -> "审核通过".equals(p.getAuditState()));
    }

    public ThemePost publishPost(ThemePost post, String userId, Long forumId) {
        post.setForum(forumRepository.getReferenceById(forumId));
        post.setAuthor(userRepository.getReferenceById(userId));
        return publish(post, userId);
    }

    public void updateAuditState(Long postId, String auditState) {
        postRepository.findById(postId).ifPresent(p -> {
            p.setAuditState(auditState);
            postRepository.save(p);
        });
    }

    @Transactional
    public void deleteById(Long postId) {
        collectRepository.findAll().stream()
                .filter(c -> c.getThemePost().getThemePostId().equals(postId))
                .forEach(collectRepository::delete);
        commentRepository.findAll().stream()
                .filter(c -> c.getThemePost().getThemePostId().equals(postId))
                .forEach(commentRepository::delete);
        postRepository.deleteById(postId);
    }
}
