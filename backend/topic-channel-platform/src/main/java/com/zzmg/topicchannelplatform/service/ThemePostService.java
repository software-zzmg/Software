package com.zzmg.topicchannelplatform.service;

import com.zzmg.topicchannelplatform.entity.ThemePost;
import com.zzmg.topicchannelplatform.repository.ForumRepository;
import com.zzmg.topicchannelplatform.repository.ThemePostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ThemePostService {

    private final ThemePostRepository postRepository;
    private final ForumRepository forumRepository;
    private final ForumMemberService forumMemberService;

    public ThemePostService(ThemePostRepository postRepository,
                            ForumRepository forumRepository,
                            ForumMemberService forumMemberService) {
        this.postRepository = postRepository;
        this.forumRepository = forumRepository;
        this.forumMemberService = forumMemberService;
    }

    public ThemePost publish(ThemePost post, String userId) {
        if (!forumMemberService.isMember(userId, post.getForum().getForumId())) {
            throw new IllegalStateException("只有频道成员才能发帖");
        }
        if (post.getThemePostId() == null || post.getThemePostId().isEmpty()) {
            int nextId = postRepository.findAll().stream()
                    .map(ThemePost::getThemePostId)
                    .mapToInt(id -> {
                        try { return Integer.parseInt(id); } catch (NumberFormatException e) { return 0; }
                    })
                    .max()
                    .orElse(0) + 1;
            post.setThemePostId(String.valueOf(nextId));
        }
        post.setPublishTime(LocalDateTime.now());
        post.setAuditState("待审核");
        return postRepository.save(post);
    }

    public Optional<ThemePost> findById(String postId) {
        return postRepository.findById(postId);
    }

    public List<ThemePost> findByForumId(String forumId) {
        return postRepository.findAll().stream()
                .filter(p -> p.getForum().getForumId().equals(forumId))
                .toList();
    }

    public void update(ThemePost post) {
        postRepository.findById(post.getThemePostId()).ifPresent(existing -> {
            existing.setTitle(post.getTitle());
            existing.setContent(post.getContent());
            postRepository.save(existing);
        });
    }

    public boolean isAuthor(String userId, String postId) {
        return postRepository.findById(postId)
                .map(p -> p.getAuthor().getUserId().equals(userId))
                .orElse(false);
    }

    public void updateAuditState(String postId, String auditState) {
        postRepository.findById(postId).ifPresent(p -> {
            p.setAuditState(auditState);
            postRepository.save(p);
        });
    }

    public List<ThemePost> findApproved() {
        return postRepository.findAll().stream()
                .filter(p -> "审核通过".equals(p.getAuditState()))
                .toList();
    }

    public void deleteById(String postId) {
        postRepository.deleteById(postId);
    }
}
