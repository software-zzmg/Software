package com.zzmg.topicchannelplatform.service;

import com.zzmg.topicchannelplatform.entity.Forum;
import com.zzmg.topicchannelplatform.repository.ForumRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ForumService {

    private final ForumRepository forumRepository;

    public ForumService(ForumRepository forumRepository) {
        this.forumRepository = forumRepository;
    }

    public Forum create(Forum forum) {
        forum.setCreateTime(LocalDateTime.now());
        forum.setAuditState("待审核");
        return forumRepository.save(forum);
    }

    public Optional<Forum> findById(Long forumId) {
        return forumRepository.findById(forumId);
    }

    public List<Forum> findAll() {
        return forumRepository.findAll();
    }

    public List<Forum> findByKeyword(String keyword) {
        return forumRepository.findAll().stream()
                .filter(f -> f.getForumName() != null && f.getForumName().contains(keyword))
                .toList();
    }

    public void update(Forum forum) {
        forumRepository.findById(forum.getForumId()).ifPresent(existing -> {
            existing.setForumName(forum.getForumName());
            existing.setContent(forum.getContent());
            existing.setAuditState("待审核");
            forumRepository.save(existing);
        });
    }

    public void updateAuditState(Long forumId, String auditState) {
        forumRepository.findById(forumId).ifPresent(f -> {
            f.setAuditState(auditState);
            forumRepository.save(f);
        });
    }

    public void deleteById(Long forumId) {
        forumRepository.deleteById(forumId);
    }
}
