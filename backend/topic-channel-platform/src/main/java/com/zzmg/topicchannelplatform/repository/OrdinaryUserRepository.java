package com.zzmg.topicchannelplatform.repository;

import com.zzmg.topicchannelplatform.entity.OrdinaryUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdinaryUserRepository extends JpaRepository<OrdinaryUser, String> {
}
