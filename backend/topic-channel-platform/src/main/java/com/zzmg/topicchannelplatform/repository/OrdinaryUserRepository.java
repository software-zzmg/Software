package com.zzmg.topicchannelplatform.repository;

import com.zzmg.topicchannelplatform.entity.OrdinaryUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrdinaryUserRepository extends JpaRepository<OrdinaryUser, String> {

    Optional<OrdinaryUser> findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    List<OrdinaryUser> findByUserNameContainingOrUserIdContaining(String userName, String userId);
}
