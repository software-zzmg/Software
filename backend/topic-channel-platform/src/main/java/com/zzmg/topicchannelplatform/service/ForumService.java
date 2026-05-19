package com.zzmg.topicchannelplatform.service;

import com.zzmg.topicchannelplatform.entity.Forum;
import com.zzmg.topicchannelplatform.repository.ForumMemberRepository;
import com.zzmg.topicchannelplatform.repository.ForumRepository;
import com.zzmg.topicchannelplatform.repository.ThemePostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ForumService {

    private final ForumRepository forumRepository;
    private final ForumMemberRepository forumMemberRepository;
    private final ThemePostRepository postRepository;
    private final ThemePostService themePostService;

    public ForumService(ForumRepository forumRepository,
                        ForumMemberRepository forumMemberRepository,
                        ThemePostRepository postRepository,
                        ThemePostService themePostService) {
        this.forumRepository = forumRepository;
        this.forumMemberRepository = forumMemberRepository;
        this.postRepository = postRepository;
        this.themePostService = themePostService;
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
        return forumRepository.findAll().stream()
                .filter(f -> "审核通过".equals(f.getAuditState()))
                .toList();
    }

    public List<Forum> findByKeyword(String keyword) {
        return forumRepository.searchApproved(keyword);
    }

    public void update(Forum forum) {
        forumRepository.findById(forum.getForumId()).ifPresent(existing -> {
            existing.setForumName(forum.getForumName());
            existing.setContent(forum.getContent());
            forumRepository.save(existing);
        });
    }

    public void updateAuditState(Long forumId, String auditState) {
        forumRepository.findById(forumId).ifPresent(f -> {
            f.setAuditState(auditState);
            forumRepository.save(f);
        });
    }

    @Transactional
    public void deleteById(Long forumId) {
        forumMemberRepository.findAll().stream()
                .filter(m -> m.getForum().getForumId().equals(forumId))
                .forEach(forumMemberRepository::delete);
        postRepository.findAll().stream()
                .filter(p -> p.getForum().getForumId().equals(forumId))
                .forEach(p -> themePostService.deleteById(p.getThemePostId()));
        forumRepository.deleteById(forumId);
    }
}
