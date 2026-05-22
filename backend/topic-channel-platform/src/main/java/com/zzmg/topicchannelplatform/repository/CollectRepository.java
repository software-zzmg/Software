package com.zzmg.topicchannelplatform.repository;

import com.zzmg.topicchannelplatform.entity.Collect;
import com.zzmg.topicchannelplatform.entity.Collect.CollectId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollectRepository extends JpaRepository<Collect, CollectId> {

    List<Collect> findByUser_UserId(String userId);
}
