package com.zzmg.topicchannelplatform.service;

import com.zzmg.topicchannelplatform.entity.OrdinaryUser;
import com.zzmg.topicchannelplatform.repository.CollectRepository;
import com.zzmg.topicchannelplatform.repository.ForumMemberRepository;
import com.zzmg.topicchannelplatform.repository.OrdinaryUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final OrdinaryUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CollectRepository collectRepository;
    private final ForumMemberRepository forumMemberRepository;

    public UserService(OrdinaryUserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       CollectRepository collectRepository,
                       ForumMemberRepository forumMemberRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.collectRepository = collectRepository;
        this.forumMemberRepository = forumMemberRepository;
    }

    // TODO: 注册手机号检测允许与已注销用户重复
    // TODO: 手机号格式验证
    public boolean register(OrdinaryUser user) {
        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            return false;
        }
        String userId = String.format("%09d", new java.util.Random().nextInt(1_000_000_000));
        user.setUserId(userId);
        user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
        user.setRegisterTime(java.time.LocalDateTime.now());
        user.setStatus("normal");
        userRepository.save(user);
        return true;
    }

    public Optional<OrdinaryUser> login(String phone, String password) {
        return userRepository.findByPhoneNumber(phone)
                .filter(u -> !"deleted".equals(u.getStatus()))
                .filter(u -> passwordEncoder.matches(password, u.getUserPassword()));
    }

    public Optional<OrdinaryUser> findById(String userId) {
        return userRepository.findById(userId);
    }

    public Optional<OrdinaryUser> findByPhone(String phone) {
        return userRepository.findByPhoneNumber(phone)
                .filter(u -> !"deleted".equals(u.getStatus()));
    }

    public void updateInfo(OrdinaryUser user) {
        userRepository.findById(user.getUserId()).ifPresent(existing -> {
            existing.setUserName(user.getUserName());
            existing.setRealName(user.getRealName());
            existing.setGender(user.getGender());
            existing.setBirthday(user.getBirthday());
            existing.setIdNumber(user.getIdNumber());
            userRepository.save(existing);
        });
    }

    public boolean changePassword(String userId, String oldPassword, String newPassword) {
        Optional<OrdinaryUser> opt = userRepository.findById(userId);
        if (opt.isPresent() && passwordEncoder.matches(oldPassword, opt.get().getUserPassword())) {
            opt.get().setUserPassword(passwordEncoder.encode(newPassword));
            userRepository.save(opt.get());
            return true;
        }
        return false;
    }

    public void resetPassword(String phone, String newPassword) {
        userRepository.findByPhoneNumber(phone)
                .filter(u -> !"deleted".equals(u.getStatus()))
                .ifPresent(u -> {
                    u.setUserPassword(passwordEncoder.encode(newPassword));
                    userRepository.save(u);
                });
    }

    public void deleteUser(String userId) {
        userRepository.findById(userId).ifPresent(user -> {
            collectRepository.findAll().stream()
                    .filter(c -> c.getUser().getUserId().equals(userId))
                    .forEach(collectRepository::delete);
            forumMemberRepository.findAll().stream()
                    .filter(m -> m.getUser().getUserId().equals(userId))
                    .forEach(forumMemberRepository::delete);
            user.setStatus("deleted");
            userRepository.save(user);
        });
    }
}
