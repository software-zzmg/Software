package com.zzmg.topicchannelplatform.service;

import com.zzmg.topicchannelplatform.entity.Collect;
import com.zzmg.topicchannelplatform.entity.Collect.CollectId;
import com.zzmg.topicchannelplatform.repository.CollectRepository;
import com.zzmg.topicchannelplatform.repository.OrdinaryUserRepository;
import com.zzmg.topicchannelplatform.repository.ThemePostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CollectService {

    private final CollectRepository collectRepository;
    private final OrdinaryUserRepository userRepository;
    private final ThemePostRepository postRepository;

    public CollectService(CollectRepository collectRepository,
                          OrdinaryUserRepository userRepository,
                          ThemePostRepository postRepository) {
        this.collectRepository = collectRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public Collect collect(String userId, String postId) {
        if (isCollected(userId, postId)) {
            return collectRepository.findAll().stream()
                    .filter(c -> c.getUser().getUserId().equals(userId)
                            && c.getThemePost().getThemePostId().equals(postId))
                    .findFirst().orElse(null);
        }
        Collect collect = new Collect();
        collect.setId(new CollectId());
        collect.setUser(userRepository.getReferenceById(userId));
        collect.setThemePost(postRepository.getReferenceById(postId));
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
