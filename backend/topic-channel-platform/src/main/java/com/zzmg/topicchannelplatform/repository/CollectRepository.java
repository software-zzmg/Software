package com.zzmg.topicchannelplatform.repository;

import com.zzmg.topicchannelplatform.entity.Collect;
import com.zzmg.topicchannelplatform.entity.Collect.CollectId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectRepository extends JpaRepository<Collect, CollectId> {
}
