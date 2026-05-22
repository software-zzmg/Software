package com.zzmg.topicchannelplatform.service;

import com.zzmg.topicchannelplatform.entity.Administrator;
import com.zzmg.topicchannelplatform.entity.Comment;
import com.zzmg.topicchannelplatform.entity.Forum;
import com.zzmg.topicchannelplatform.entity.OrdinaryUser;
import com.zzmg.topicchannelplatform.entity.ThemePost;
import com.zzmg.topicchannelplatform.repository.AdministratorRepository;
import com.zzmg.topicchannelplatform.repository.CommentRepository;
import com.zzmg.topicchannelplatform.repository.ForumRepository;
import com.zzmg.topicchannelplatform.repository.OrdinaryUserRepository;
import com.zzmg.topicchannelplatform.repository.ThemePostRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdministratorService {

    private final AdministratorRepository adminRepository;
    private final OrdinaryUserRepository userRepository;
    private final ForumRepository forumRepository;
    private final ThemePostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PasswordEncoder passwordEncoder;

    public AdministratorService(AdministratorRepository adminRepository,
                                OrdinaryUserRepository userRepository,
                                ForumRepository forumRepository,
                                ThemePostRepository postRepository,
                                CommentRepository commentRepository,
                                PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.forumRepository = forumRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<Administrator> login(String userId, String password) {
        return adminRepository.findById(userId)
                .filter(a -> passwordEncoder.matches(password, a.getUserPassword()));
    }

    public List<OrdinaryUser> findAllUsers() {
        return userRepository.findAll();
    }

    public List<OrdinaryUser> searchUsers(String keyword) {
        return userRepository.findAll().stream()
                .filter(u -> (u.getUserName() != null && u.getUserName().contains(keyword))
                        || (u.getPhoneNumber() != null && u.getPhoneNumber().contains(keyword)))
                .toList();
    }

    public List<Forum> findPendingForums() {
        return forumRepository.findAll().stream()
                .filter(f -> "待审核".equals(f.getAuditState()))
                .toList();
    }

    public List<ThemePost> findPendingPosts() {
        return postRepository.findAll().stream()
                .filter(p -> "待审核".equals(p.getAuditState()))
                .toList();
    }

    public List<Comment> findPendingComments() {
        return commentRepository.findAll().stream()
                .filter(c -> "待审核".equals(c.getAuditState()))
                .toList();
    }

    public void auditForum(Long forumId, boolean approved) {
        forumRepository.findById(forumId).ifPresent(f -> {
            f.setAuditState(approved ? "审核通过" : "审核未通过");
            forumRepository.save(f);
        });
    }

    public void auditThemePost(Long postId, boolean approved) {
        postRepository.findById(postId).ifPresent(p -> {
            p.setAuditState(approved ? "审核通过" : "审核未通过");
            postRepository.save(p);
        });
    }

    public long countPendingForums() {
        return forumRepository.countByAuditState("待审核");
    }

    public long countPendingPosts() {
        return postRepository.countByAuditState("待审核");
    }

    public long countPendingComments() {
        return commentRepository.countByAuditState("待审核");
    }

    public void auditComment(Long commentId, boolean approved) {
        commentRepository.findById(commentId).ifPresent(c -> {
            c.setAuditState(approved ? "审核通过" : "审核未通过");
            commentRepository.save(c);
        });
    }
}
