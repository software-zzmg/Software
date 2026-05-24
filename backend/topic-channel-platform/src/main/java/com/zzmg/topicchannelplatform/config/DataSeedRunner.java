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
import java.util.ArrayList;
import java.util.List;

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
        log.info("=== DataSeedRunner starting ===");

        List<OrdinaryUser> users = seedUsers();
        List<Forum> forums = seedForums(users);
        List<ThemePost> posts = seedPosts(forums, users);
        seedComments(posts, users);

        log.info("=== DataSeedRunner done: {} users, {} forums, {} posts, {} comments ===",
                users.size(), forums.size(), posts.size(),
                commentRepository.findAll().stream()
                        .filter(c -> "审核通过".equals(c.getAuditState())).count());
    }

    private List<OrdinaryUser> seedUsers() {
        List<OrdinaryUser> users = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            String phone = "1380000000" + i;
            if (userRepository.existsByPhoneNumber(phone)) {
                users.add(userRepository.findByPhoneNumber(phone).orElseThrow());
                continue;
            }
            OrdinaryUser user = new OrdinaryUser();
            user.setUserId(String.format("%09d", new java.util.Random().nextInt(1_000_000_000)));
            user.setPhoneNumber(phone);
            user.setUserPassword(passwordEncoder.encode("123456"));
            user.setUserName("测试用户" + i);
            user.setRegisterTime(LocalDateTime.now());
            users.add(userRepository.save(user));
        }
        log.info("Seed users: {} created, {} already existed",
                5 - (int) users.stream().filter(u -> u.getRegisterTime() == null).count(),
                (int) users.stream().filter(u -> u.getRegisterTime() == null).count());
        return users;
    }

    private List<Forum> seedForums(List<OrdinaryUser> users) {
        List<Forum> forums = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            String name = "测试频道" + i;
            if (forumRepository.findAll().stream().anyMatch(f -> name.equals(f.getForumName()))) {
                forums.add(forumRepository.findAll().stream()
                        .filter(f -> name.equals(f.getForumName())).findFirst().orElseThrow());
                continue;
            }
            Forum forum = new Forum();
            forum.setForumName(name);
            forum.setContent("这是" + name + "的简介");
            forum.setAuditState("审核通过");
            forum.setCreator(users.get(i % 5));
            forum.setCreateTime(LocalDateTime.now());
            forum = forumRepository.save(forum);
            forums.add(forum);

            // Add all 5 users as members
            for (OrdinaryUser user : users) {
                ForumMemberId fmi = new ForumMemberId(user.getUserId(), forum.getForumId());
                if (!forumMemberRepository.existsById(fmi)) {
                    ForumMember member = new ForumMember();
                    member.setId(new ForumMemberId());
                    member.setUser(user);
                    member.setForum(forum);
                    member.setJoinTime(LocalDateTime.now());
                    forumMemberRepository.save(member);
                }
            }
        }
        log.info("Seed forums: {}", forums.size());
        return forums;
    }

    private List<ThemePost> seedPosts(List<Forum> forums, List<OrdinaryUser> users) {
        List<ThemePost> posts = new ArrayList<>();
        for (Forum forum : forums) {
            for (int j = 1; j <= 10; j++) {
                String title = forum.getForumName() + "的帖子" + j;
                boolean exists = postRepository.findAll().stream()
                        .anyMatch(p -> title.equals(p.getTitle())
                                && p.getForum().getForumId().equals(forum.getForumId()));
                if (exists) {
                    continue;
                }
                ThemePost post = new ThemePost();
                post.setForum(forum);
                post.setTitle(title);
                post.setContent("这是" + forum.getForumName() + "下第" + j + "篇测试帖子的正文内容");
                post.setAuditState("审核通过");
                post.setAuthor(users.get((j - 1) % 5));
                post.setPublishTime(LocalDateTime.now());
                posts.add(postRepository.save(post));
            }
        }
        log.info("Seed posts: {}", posts.size());
        return posts;
    }

    private void seedComments(List<ThemePost> posts, List<OrdinaryUser> users) {
        int count = 0;
        for (ThemePost post : posts) {
            boolean exists = commentRepository.findAll().stream()
                    .anyMatch(c -> c.getThemePost().getThemePostId().equals(post.getThemePostId())
                            && c.getContent().contains(post.getTitle()));
            if (exists) {
                continue;
            }
            Comment comment = new Comment();
            comment.setThemePost(post);
            comment.setContent("这是帖子《" + post.getTitle() + "》的测试评论");
            comment.setAuditState("审核通过");
            comment.setAuthor(users.get(new java.util.Random().nextInt(5)));
            comment.setPublishTime(LocalDateTime.now());
            commentRepository.save(comment);
            count++;
        }
        log.info("Seed comments: {}", count);
    }
}
