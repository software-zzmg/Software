package com.zzmg.topicchannelplatform.service;

import com.zzmg.topicchannelplatform.entity.OrdinaryUser;
import com.zzmg.topicchannelplatform.repository.OrdinaryUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final OrdinaryUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(OrdinaryUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean register(OrdinaryUser user) {
        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            return false;
        }
        String userId = String.format("%09d", new java.util.Random().nextInt(1_000_000_000));
        user.setUserId(userId);
        user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
        user.setRegisterTime(java.time.LocalDateTime.now());
        userRepository.save(user);
        return true;
    }

    public Optional<OrdinaryUser> login(String phone, String password) {
        return userRepository.findByPhoneNumber(phone)
                .filter(u -> passwordEncoder.matches(password, u.getUserPassword()));
    }

    public Optional<OrdinaryUser> findById(String userId) {
        return userRepository.findById(userId);
    }

    public Optional<OrdinaryUser> findByPhone(String phone) {
        return userRepository.findByPhoneNumber(phone);
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
        userRepository.findByPhoneNumber(phone).ifPresent(u -> {
            u.setUserPassword(passwordEncoder.encode(newPassword));
            userRepository.save(u);
        });
    }

    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }
}
