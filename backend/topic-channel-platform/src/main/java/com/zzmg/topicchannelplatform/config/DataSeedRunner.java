package com.zzmg.topicchannelplatform.config;

import com.zzmg.topicchannelplatform.entity.Comment;
import com.zzmg.topicchannelplatform.entity.Forum;
import com.zzmg.topicchannelplatform.entity.ForumMember;
import com.zzmg.topicchannelplatform.entity.ForumMember.ForumMemberId;
import com.zzmg.topicchannelplatform.entity.OrdinaryUser;
import com.zzmg.topicchannelplatform.entity.ThemePost;
import com.zzmg.topicchannelplatform.repository.CommentRepository;
import com.zzmg.topicchannelplatform.repository.ForumMemberRepository;
import com.zzmg.topicchannelplatform.repository.ForumRepository;
import com.zzmg.topicchannelplatform.repository.OrdinaryUserRepository;
import com.zzmg.topicchannelplatform.repository.ThemePostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class DataSeedRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeedRunner.class);

    @Value("${seed.enabled:false}")
    private boolean seedEnabled;

    private final OrdinaryUserRepository userRepository;
    private final ForumRepository forumRepository;
    private final ThemePostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ForumMemberRepository forumMemberRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeedRunner(OrdinaryUserRepository userRepository,
                          ForumRepository forumRepository,
                          ThemePostRepository postRepository,
                          CommentRepository commentRepository,
                          ForumMemberRepository forumMemberRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.forumRepository = forumRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.forumMemberRepository = forumMemberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!seedEnabled) {
            log.info("Seed disabled (seed.enabled=false)");
            return;
        }
        log.info("=== DataSeedRunner start ===");

        OrdinaryUser user = seedUser();
        Forum forum = seedForum(user);
        seedMember(user, forum);
        ThemePost post = seedPost(forum, user);
        seedComments(post, user);

        log.info("=== DataSeedRunner done ===");
    }

    private OrdinaryUser seedUser() {
        String phone = "13800000001";
        if (userRepository.existsByPhoneNumber(phone)) {
            OrdinaryUser existing = userRepository.findByPhoneNumber(phone).orElseThrow();
            log.info("复用用户: phone={}", phone);
            return existing;
        }
        OrdinaryUser user = new OrdinaryUser();
        user.setUserId(String.format("%09d", new java.util.Random().nextInt(1_000_000_000)));
        user.setPhoneNumber(phone);
        user.setUserPassword(passwordEncoder.encode("123456"));
        user.setUserName("测试用户");
        user.setRegisterTime(LocalDateTime.of(2026, 2, 7, 0, 0, 0));
        user = userRepository.save(user);
        log.info("创建用户: phone={}, userId={}", phone, user.getUserId());
        return user;
    }

    private Forum seedForum(OrdinaryUser creator) {
        String name = "日期测试频道";
        return forumRepository.findAll().stream()
                .filter(f -> name.equals(f.getForumName()))
                .findFirst()
                .map(f -> {
                    log.info("复用频道: {}", name);
                    return f;
                })
                .orElseGet(() -> {
                    Forum forum = new Forum();
                    forum.setForumName(name);
                    forum.setContent("这是用于系统测试的主题频道");
                    forum.setCreator(creator);
                    forum.setAuditState("审核通过");
                    forum.setCreateTime(LocalDateTime.of(2026, 2, 12, 0, 0, 0));
                    forum = forumRepository.save(forum);
                    log.info("创建频道: {}", name);
                    return forum;
                });
    }

    private void seedMember(OrdinaryUser user, Forum forum) {
        ForumMemberId fmi = new ForumMemberId(user.getUserId(), forum.getForumId());
        if (forumMemberRepository.existsById(fmi)) {
            log.info("跳过成员: userId={}, forumId={}", user.getUserId(), forum.getForumId());
            return;
        }
        ForumMember member = new ForumMember();
        member.setId(new ForumMemberId());
        member.setUser(user);
        member.setForum(forum);
        member.setJoinTime(LocalDateTime.of(2026, 2, 12, 0, 0, 0));
        forumMemberRepository.save(member);
        log.info("创建成员: userId={}, forumId={}", user.getUserId(), forum.getForumId());
    }

    private ThemePost seedPost(Forum forum, OrdinaryUser author) {
        String title = "日期测试帖子";
        return postRepository.findAll().stream()
                .filter(p -> title.equals(p.getTitle())
                        && p.getForum().getForumId().equals(forum.getForumId()))
                .findFirst()
                .map(p -> {
                    log.info("复用帖子: {}", title);
                    return p;
                })
                .orElseGet(() -> {
                    ThemePost post = new ThemePost();
                    post.setForum(forum);
                    post.setTitle(title);
                    post.setContent("这是测试用户在测试频道中发布的测试帖子");
                    post.setAuthor(author);
                    post.setAuditState("审核通过");
                    post.setPublishTime(LocalDateTime.of(2026, 2, 12, 0, 0, 0));
                    post = postRepository.save(post);
                    log.info("创建帖子: {}", title);
                    return post;
                });
    }

    private void seedComments(ThemePost post, OrdinaryUser author) {
        long postId = post.getThemePostId();
        long existingCount = commentRepository.findAll().stream()
                .filter(c -> c.getThemePost().getThemePostId().equals(postId))
                .count();
        int skipped = (int) existingCount;
        int created = 0;

        LocalDateTime baseTime = LocalDateTime.of(2026, 2, 13, 0, 0, 0);

        for (int i = 1; i <= 100; i++) {
            String content = "第" + i + "条测试评论";
            final int idx = i;
            boolean exists = commentRepository.findAll().stream()
                    .anyMatch(c -> c.getThemePost().getThemePostId().equals(postId)
                            && content.equals(c.getContent()));
            if (exists) {
                continue;
            }
            Comment comment = new Comment();
            comment.setThemePost(post);
            comment.setContent(content);
            comment.setAuthor(author);
            comment.setAuditState("审核通过");
            comment.setPublishTime(baseTime.plusDays(idx - 1));
            commentRepository.save(comment);
            created++;
        }

        log.info("评论: 新增 {} 条, 跳过 {} 条", created, skipped);
    }
}
