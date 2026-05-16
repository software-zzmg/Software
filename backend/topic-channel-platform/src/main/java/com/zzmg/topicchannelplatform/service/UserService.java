package com.zzmg.topicchannelplatform.service;

import com.zzmg.topicchannelplatform.entity.OrdinaryUser;
import com.zzmg.topicchannelplatform.repository.OrdinaryUserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final OrdinaryUserRepository userRepository;

    public UserService(OrdinaryUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean register(OrdinaryUser user) {
        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            return false;
        }
        user.setUserId(java.util.UUID.randomUUID().toString());
        user.setRegisterTime(java.time.LocalDateTime.now());
        userRepository.save(user);
        return true;
    }

    public Optional<OrdinaryUser> login(String phone, String password) {
        return userRepository.findByPhoneNumber(phone)
                .filter(u -> u.getUserPassword().equals(password));
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
        if (opt.isPresent() && opt.get().getUserPassword().equals(oldPassword)) {
            opt.get().setUserPassword(newPassword);
            userRepository.save(opt.get());
            return true;
        }
        return false;
    }

    public void resetPassword(String phone, String newPassword) {
        userRepository.findByPhoneNumber(phone).ifPresent(u -> {
            u.setUserPassword(newPassword);
            userRepository.save(u);
        });
    }

    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }
}
