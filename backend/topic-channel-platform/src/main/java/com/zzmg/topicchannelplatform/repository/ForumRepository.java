package com.zzmg.topicchannelplatform.repository;

import com.zzmg.topicchannelplatform.entity.Forum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForumRepository extends JpaRepository<Forum, String> {
}
