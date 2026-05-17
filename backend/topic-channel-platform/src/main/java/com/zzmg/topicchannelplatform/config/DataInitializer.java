package com.zzmg.topicchannelplatform.config;

import com.zzmg.topicchannelplatform.entity.Administrator;
import com.zzmg.topicchannelplatform.entity.Forum;
import com.zzmg.topicchannelplatform.entity.OrdinaryUser;
import com.zzmg.topicchannelplatform.entity.ForumMember;
import com.zzmg.topicchannelplatform.entity.ForumMember.ForumMemberId;
import com.zzmg.topicchannelplatform.repository.AdministratorRepository;
import com.zzmg.topicchannelplatform.repository.ForumMemberRepository;
import com.zzmg.topicchannelplatform.repository.ForumRepository;
import com.zzmg.topicchannelplatform.repository.OrdinaryUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AdministratorRepository adminRepository;
    private final OrdinaryUserRepository userRepository;
    private final ForumRepository forumRepository;
    private final ForumMemberRepository forumMemberRepository;

    public DataInitializer(AdministratorRepository adminRepository,
                           OrdinaryUserRepository userRepository,
                           ForumRepository forumRepository,
                           ForumMemberRepository forumMemberRepository) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.forumRepository = forumRepository;
        this.forumMemberRepository = forumMemberRepository;
    }

    @Override
    public void run(String... args) {
        initAdmin();
        OrdinaryUser testUser = initTestUser();
        initDefaultForum(testUser);
    }

    private void initAdmin() {
        if (adminRepository.findById("admin").isPresent()) {
            return;
        }
        Administrator admin = new Administrator();
        admin.setUserId("admin");
        admin.setUserPassword("admin123");
        adminRepository.save(admin);
    }

    private OrdinaryUser initTestUser() {
        if (userRepository.existsByPhoneNumber("13800000000")) {
            return userRepository.findByPhoneNumber("13800000000").orElse(null);
        }
        OrdinaryUser user = new OrdinaryUser();
        user.setUserId(String.format("%09d", new java.util.Random().nextInt(1_000_000_000)));
        user.setPhoneNumber("13800000000");
        user.setUserPassword("123456");
        user.setUserName("test");
        user.setRegisterTime(LocalDateTime.now());
        return userRepository.save(user);
    }

    private void initDefaultForum(OrdinaryUser creator) {
        if (creator == null) {
            return;
        }
        boolean exists = forumRepository.findAll().stream()
                .anyMatch(f -> "默认频道".equals(f.getForumName()));
        if (exists) {
            return;
        }
        Forum forum = new Forum();
        forum.setForumName("默认频道");
        forum.setContent("系统默认频道，欢迎加入交流");
        forum.setAuditState("审核通过");
        forum.setCreator(creator);
        forum.setCreateTime(LocalDateTime.now());
        forum = forumRepository.save(forum);

        ForumMember member = new ForumMember();
        member.setId(new ForumMemberId());
        member.setUser(creator);
        member.setForum(forum);
        member.setJoinTime(LocalDateTime.now());
        forumMemberRepository.save(member);
    }
}
