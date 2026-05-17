package com.zzmg.topicchannelplatform.service;

import com.zzmg.topicchannelplatform.entity.ForumMember;
import com.zzmg.topicchannelplatform.entity.ForumMember.ForumMemberId;
import com.zzmg.topicchannelplatform.repository.ForumMemberRepository;
import com.zzmg.topicchannelplatform.repository.ForumRepository;
import com.zzmg.topicchannelplatform.repository.OrdinaryUserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ForumMemberService {

    private final ForumMemberRepository forumMemberRepository;
    private final OrdinaryUserRepository userRepository;
    private final ForumRepository forumRepository;

    public ForumMemberService(ForumMemberRepository forumMemberRepository,
                              OrdinaryUserRepository userRepository,
                              ForumRepository forumRepository) {
        this.forumMemberRepository = forumMemberRepository;
        this.userRepository = userRepository;
        this.forumRepository = forumRepository;
    }

    public ForumMember join(String userId, Long forumId) {
        boolean canJoin = forumRepository.findById(forumId).map(f -> {
            if ("审核通过".equals(f.getAuditState())) return true;
            return f.getCreator().getUserId().equals(userId);
        }).orElse(false);
        if (!canJoin) {
            throw new IllegalStateException("该频道尚未审核通过，暂不能加入");
        }
        ForumMember member = new ForumMember();
        member.setId(new ForumMemberId());
        member.setUser(userRepository.getReferenceById(userId));
        member.setForum(forumRepository.getReferenceById(forumId));
        member.setJoinTime(LocalDateTime.now());
        return forumMemberRepository.save(member);
    }

    public void leave(String userId, Long forumId) {
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

    public boolean isMember(String userId, Long forumId) {
        return forumMemberRepository.findAll().stream()
                .anyMatch(m -> m.getUser().getUserId().equals(userId)
                        && m.getForum().getForumId().equals(forumId));
    }
}
