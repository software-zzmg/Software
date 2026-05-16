package com.zzmg.topicchannelplatform.service;

import com.zzmg.topicchannelplatform.entity.Collect;
import com.zzmg.topicchannelplatform.entity.OrdinaryUser;
import com.zzmg.topicchannelplatform.entity.ThemePost;
import com.zzmg.topicchannelplatform.repository.CollectRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CollectService {

    private final CollectRepository collectRepository;

    public CollectService(CollectRepository collectRepository) {
        this.collectRepository = collectRepository;
    }

    public Collect collect(String userId, String postId) {
        Collect collect = new Collect();
        OrdinaryUser user = new OrdinaryUser();
        user.setUserId(userId);
        collect.setUser(user);
        ThemePost post = new ThemePost();
        post.setThemePostId(postId);
        collect.setThemePost(post);
        collect.setCollectTime(LocalDateTime.now());
        return collectRepository.save(collect);
    }

    public void cancelCollect(String userId, String postId) {
        collectRepository.findAll().stream()
                .filter(c -> c.getUser().getUserId().equals(userId)
                        && c.getThemePost().getThemePostId().equals(postId))
                .findFirst()
                .ifPresent(collectRepository::delete);
    }

    public List<Collect> findByUserId(String userId) {
        return collectRepository.findAll().stream()
                .filter(c -> c.getUser().getUserId().equals(userId))
                .toList();
    }

    public boolean isCollected(String userId, String postId) {
        return collectRepository.findAll().stream()
                .anyMatch(c -> c.getUser().getUserId().equals(userId)
                        && c.getThemePost().getThemePostId().equals(postId));
    }
}
