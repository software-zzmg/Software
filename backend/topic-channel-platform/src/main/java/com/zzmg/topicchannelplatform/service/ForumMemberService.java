package com.zzmg.topicchannelplatform.service;

import com.zzmg.topicchannelplatform.entity.Forum;
import com.zzmg.topicchannelplatform.entity.ForumMember;
import com.zzmg.topicchannelplatform.entity.OrdinaryUser;
import com.zzmg.topicchannelplatform.repository.ForumMemberRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ForumMemberService {

    private final ForumMemberRepository forumMemberRepository;

    public ForumMemberService(ForumMemberRepository forumMemberRepository) {
        this.forumMemberRepository = forumMemberRepository;
    }

    public ForumMember join(String userId, String forumId) {
        ForumMember member = new ForumMember();
        OrdinaryUser user = new OrdinaryUser();
        user.setUserId(userId);
        member.setUser(user);
        Forum forum = new Forum();
        forum.setForumId(forumId);
        member.setForum(forum);
        member.setJoinTime(LocalDateTime.now());
        return forumMemberRepository.save(member);
    }

    public void leave(String userId, String forumId) {
        forumMemberRepository.findAll().stream()
                .filter(m -> m.getUser().getUserId().equals(userId)
                        && m.getForum().getForumId().equals(forumId))
                .findFirst()
                .ifPresent(forumMemberRepository::delete);
    }

    public List<ForumMember> findByUserId(String userId) {
        return forumMemberRepository.findAll().stream()
                .filter(m -> m.getUser().getUserId().equals(userId))
                .toList();
    }

    public boolean isMember(String userId, String forumId) {
        return forumMemberRepository.findAll().stream()
                .anyMatch(m -> m.getUser().getUserId().equals(userId)
                        && m.getForum().getForumId().equals(forumId));
    }
}
