package com.zzmg.topicchannelplatform.service;

import com.zzmg.topicchannelplatform.entity.ThemePost;
import com.zzmg.topicchannelplatform.repository.ThemePostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ThemePostService {

    private final ThemePostRepository postRepository;

    public ThemePostService(ThemePostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public ThemePost publish(ThemePost post) {
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

    public void updateAuditState(String postId, String auditState) {
        postRepository.findById(postId).ifPresent(p -> {
            p.setAuditState(auditState);
            postRepository.save(p);
        });
    }

    public void deleteById(String postId) {
        postRepository.deleteById(postId);
    }
}
